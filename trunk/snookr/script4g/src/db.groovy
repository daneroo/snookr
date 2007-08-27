// Db4o
import com.db4o.*;
import com.db4o.ext.ExtDb4o;
import com.db4o.query.*;

import java.util.regex.Pattern
import java.text.SimpleDateFormat
import java.util.GregorianCalendar;

import net.snookr.util.MD5;
import net.snookr.util.Environment;
import net.snookr.db.Database;
import net.snookr.model.FSImage;

println "-=-=-= Hello db =-=-=-"

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
        this.db.printSummary(true);
    }
    FSImage fetch(String fileName) {
    }
    public void printSummary(boolean verbose){
        this.db.printSummary(verbose);
    }
    void close() { this.db.close(); }
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
broker.printSummary(true);

/*
  Scenario: create many 10,100K FSimages
   measure time to insert.
   measure time to fecth (by QBE, by constrainedQuery
   measure time to update
*/
(100..103).each() {
    FSImage coco = gen.get(it);
    println "${it} ${coco}";
//    Object coco = gen.get(it);
    broker.createOrUpdate(coco);
}

println "-=-=-= Close Database:  =-=-=-"
broker.close();


