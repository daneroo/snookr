
package net.snookr.db;

// Db4o
import com.db4o.*;
import com.db4o.ext.ExtDb4o;
import com.db4o.query.*;
import net.snookr.util.Environment;
import net.snookr.model.FSImage;
import net.snookr.model.FlickrImage;

class Database {
    ObjectContainer oc;
    Database() {
        println "-=-=-= Open Database: ${Environment.yapFile} =-=-=-"
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().objectClass(FSImage.class).objectField("fileName").indexed(true);
        Db4o.configure().objectClass(FlickrImage.class).objectField("photoid").indexed(true);
        oc = Db4o.openFile(Environment.yapFile);

    }

    public void close() {
        oc.close();
    }

    public void printSummary(boolean verbose) {
        // just read everything in the db
        ObjectSet result=oc.get(null);
        println ("database has "+result.size()+" objects");
        def wholeDb = [];
        while(result.hasNext()) { wholeDb << result.next() }
        Map mapByType = [:];
        for ( o in wholeDb) {
            String className = o.getClass().getName();
            int count = mapByType[className];
            mapByType[className] = (count==null)?1:(count+1);
        }
        mapByType.each() { k,v ->
                println "db has ${v} objects of type ${k}"
        }
        if (verbose) {
            for ( o in wholeDb) {
                println (""+o.getClass().getName()+" : "+o);
            }
        }
        
    }

    Map getMapForClassByPrimaryKey(Class claz,String fieldName) {
        ObjectSet result = oc.get(claz)
        println "found ${result.size()} ${claz.getName()} objects";
        Map mapForClass = [:];
        while(result.hasNext()) { 
            def clazInstance = result.next(); // FSImage, or FlickrImage
            mapForClass[clazInstance[fieldName]]=clazInstance;
        }
        return mapForClass;
        
    }

    Object getForPrimaryKey(Class claz,String fieldName,Object value) {
        return getForPrimaryKey(claz,fieldName,value,false);
    }

    Object getForPrimaryKey(Class claz,String fieldName,Object value,boolean verbose) {
        def qbe = claz.newInstance();
        qbe[fieldName] = value;
        ObjectSet result = oc.get(qbe);
        if (verbose || (result.size()!=1) ) {
            println "found ${result.size()} matching objects for ${claz.getName()}.${fieldName}=${value}";
        }
        if (result.size()>0) {
            return result.next();
        }
        return null;
    }
    
    String save(Object persist) {
        oc.set(persist);
    }

}
