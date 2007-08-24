// Db4o
import com.db4o.*;
import com.db4o.ext.ExtDb4o;
import com.db4o.query.*;

import java.util.regex.Pattern
import java.text.SimpleDateFormat

import net.snookr.util.MD5;
import net.snookr.util.Environment;
import net.snookr.db.Database;
import net.snookr.model.FSImage;

println "-=-=-= Hello db =-=-=-"

// Open db
Environment.yapFile="test.yap";
Database db = new Database();
db.printSummary(true);

(100..103).each() {
    FSImage coco = new FSImage();
    coco.fileName="/coco/file00${it}.jpg";
    coco.size=1000+it;
    coco.md5 = MD5.digest(coco.fileName);
    coco.lastModified=new Date(new Date().getTime()+it*1000);
    println "${it} ${coco}";
    createOrUpdate(db,coco);
}

db.printSummary(true);

void createOrUpdate(Database db,FSImage caca) {
    println "db class: ${db.getClass().getName()}"
    println "Accepted file ${caca.fileName}";
    def persist;
    persist =  null;
    if (persist==null) { 
        qbe = new FSImage();
        qbe.fileName=caca.fileName;
        println "Retreiving ${qbe}";

        //result = db.get(qbe);
        Query query=db.oc.query();
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
    db.save(persist);
}


println "-=-=-= Close Database:  =-=-=-"
db.close();


