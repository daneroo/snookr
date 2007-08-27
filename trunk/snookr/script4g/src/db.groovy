// Db4o
import com.db4o.*;
import com.db4o.ext.ExtDb4o;
import com.db4o.query.*;

import java.util.regex.Pattern
import java.text.SimpleDateFormat
import java.util.GregorianCalendar;

import net.snookr.util.MD5;
import net.snookr.util.Environment;
import net.snookr.util.Timer;
import net.snookr.db.Database;
import net.snookr.model.FSImage;

println "-=-=-= Hello db =-=-=-"

class Histo {
    Map values = [:];
    void add(String code,int n) {
        def count = this.values[code];
        this.values[code] = (count==null)?n:(count+n);
    }
    void show() {
        this.values.each() { k,v -> println "  values   ${k} : ${v}" }
    }
}

class Generator {
    Date startDate = new GregorianCalendar(2007,Calendar.AUGUST,27).getTime();
    public Object get(int i) {
        FSImage coco = new FSImage();
        coco.fileName="/coco/file00${i}.jpg";
        coco.size=1000+i;
        coco.md5 = MD5.digest(coco.fileName);
        coco.lastModified=new Date(startDate.getTime()+i*15000);
        coco.taken=coco.lastModified;
        return coco;
  }
};

// inject db name
Environment.yapFile="test.yap";

class Broker {
    Database db = null;
    public Broker() {
        this.db = new Database();
    }
    public void printSummary(boolean verbose){
        this.db.printSummary(verbose);
    }
    void close() { this.db.close(); }

    FSImage fetchByConstrain(String fileName) {
            Query query=this.db.oc.query();
            query.constrain(FSImage.class);
            query.descend("fileName").constrain(fileName);
            ObjectSet result=query.execute();
            assert result.size()<=1;
            if (result.size()==1) return result.next();
            return null;
    }
    FSImage fetchByQBE(String fileName) {
            FSImage qbe = new FSImage()
            qbe.fileName=fileName;
            ObjectSet result = this.db.oc.get(qbe);
            assert result.size()<=1;
            if (result.size()==1) return result.next();
            return null;
    }
    FSImage fetchByMD5(String md5) {
            FSImage qbe = new FSImage()
            qbe.md5=md5;
            ObjectSet result = this.db.oc.get(qbe);
            assert result.size()<=1;
            if (result.size()>0) return result.next();
            return null;
    }

    void insert(FSImage fsima) {
        this.db.save(fsima);
    }
    void createOrUpdate(FSImage caca) {
        println "db class: ${this.db.getClass().getName()}"
        println "Accepted file ${caca.fileName}";
        def persist;
        persist =  null;
        if (persist==null) { 
            FSImage qbe = new FSImage();
            qbe.fileName=caca.fileName;
            println "Retreiving ${qbe}";

            //result = db.get(qbe);
            Query query=this.db.oc.query();
            query.constrain(FSImage.class);
            query.descend("fileName").constrain(caca.fileName);
            ObjectSet result=query.execute();

            println "found ${result.size()} matching objects for ${qbe.fileName}";
            if (result.size()>0) {
                // update the first
                persist = result.next();
                println "  --old  ${persist}";
            }
        }

        if (persist==null) {
            persist = caca;
            println "  --new  ${persist}";
        }
        println "++saving  ${persist}";
        this.db.save(persist);
    }

}

Generator gen = new Generator();
Broker broker = new Broker();
//broker.printSummary(true);

/*
  Scenario: create many 10,100K FSimages
   test primary key violation is caught: ok
   measure time to insert.
     no commit:
        inserted 1000 at rate: 1805.0541 o/s
        inserted 10000 at rate: 4728.1323 o/s
        inserted 100000 at rate: 6277.464 o/s - fails to close
     commit every NN.
        NN=1     inserted 1000 at rate: 21.816435 o/s
        NN=10    inserted 1000 at rate: 139.08206 o/s
        NN=100   inserted 1000 at rate:  820.34454 o/s
        NN=100   inserted 10000 at rate: 1058.0891 o/s
        NN=100   inserted 100000 at rate: 848.4211 o/s
        NN=1000  inserted 100000 at rate: 2058.3755 o/s
        NN=10000 inserted 100000 at rate: 3267.5466 o/s

These rates changes slightly when added an other key on md5!?
However find by md5 is even faster !

NN=10000 fails at 191K, NN=1000 hangs at 194K.
 
   measure time to fecth (by QBE, by constrainedQuery
        fetch by QBE and constrin on primary unique key is identical in speed.
        choose QBE.

   measure time to update
*/

println "Size at start: ${broker.db.oc.get(null).size()}";

Histo histo = new Histo();
int startoid=1000000;
int countoids=20000;
Range oidRange=startoid..(startoid+countoids-1);
Timer tt = new Timer();
(oidRange).each() {
    FSImage fsima = gen.get(it);
    if (!broker.fetchByConstrain(fsima.fileName)) {
        broker.insert(fsima);
        histo.add("insert",1);
    } else {
        histo.add("none",1);
    }
    if ((it%10000)==0) {
        println "  sofar (${it-startoid}) at rate: ${tt.rate(it-startoid)} o/s";
    }
    if ((it%10000)==0) {
        broker.db.oc.commit();
    }
}
histo.show();
println "createOrUpdate ${countoids} at rate: ${tt.rate(countoids)} o/s";
println "createOrUpdate ${countoids} in: ${tt.diff()} s.";

(1..4).each() {

    tt.restart(); histo = new Histo();
    (oidRange).each() {
        if (broker.fetchByConstrain(gen.get(it).fileName)) {  histo.add("foundByConstrain",1);;   }
    }
    //histo.show();
    println "foundByConstrain ${countoids} at rate: ${tt.rate(countoids)} o/s (${tt.diff()} s.)";

    tt.restart(); histo = new Histo();
    (oidRange).each() {
        if (broker.fetchByQBE(gen.get(it).fileName)) {  histo.add("foundByQBE",1);;   }
    }
    //histo.show();
    println "foundByQBE       ${countoids} at rate: ${tt.rate(countoids)} o/s (${tt.diff()} s.)";

    tt.restart(); histo = new Histo();
    (oidRange).each() {
        if (broker.fetchByMD5(gen.get(it).fileName)) {  histo.add("foundByQBE",1);;   }
    }
    //histo.show();
    println "foundByMD5       ${countoids} at rate: ${tt.rate(countoids)} o/s (${tt.diff()} s.)";

}

println "-=-=-= Closing Database:  =-=-=-"
//broker.printSummary(false);
//println "-=-=-= Summary  ${tt.diff()} s.=-=-=-"
broker.close();
println "-=-=-= Done  ${tt.diff()} s.=-=-=-"


