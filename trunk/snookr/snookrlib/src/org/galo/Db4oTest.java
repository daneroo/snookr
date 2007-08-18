/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2006
 */

package org.galo;

import java.io.*;

import java.util.*;
import com.db4o.*;
import com.db4o.ext.ExtDb4o;
import com.db4o.query.*;

import org.galo.model.Image;

public class Db4oTest {
    
    public static void main(String[] args) {
        List l = new Vector();
        Map m = new TreeMap();
        for (int i=0;i<3;i++) {
            Image ima = fakeImage(1000+i);
            l.add(ima);
            m.put(ima.getFileName(),ima);
        }
        new Db4oTest().test(m);
    }

    ObjectContainer db;
    
    File yapFile() {
        File appDir = new File(JnlpPersist.appDirPath());
        return new File(appDir,"galorepo.yap");
    }
    void clearDB() {
        closeDB();
        yapFile().delete();
        openDB();
    }
    void openDB() {
        if(db==null) {
            ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
            ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
            db = Db4o.openFile(yapFile().toString());
        }
    }
    void closeDB(){
        if (db!=null) {
            db.close();
        }
        db=null;
    }

    static void log(String msg) { System.err.println(msg);  }

    static Image fakeImage(int i) {
        Image ima = new Image();
        ima.setFileName("file:/home/fake/media/subdir/IMG_"+i+".JPG");
        ima.setFileSize(1000+i);
        Date d = new Date(1120000000000l); // a date in the past 2005-06..
        ima.setLastModified(new Date(d.getTime()+i*6*3600*1000));
        log("ima :"+ima);
        return ima;
    }
    void test(Object o) {
        log("Db4oTest -- start");
        writeTest(o);
        readTest();
    }
    
    public void writeTest(Object o) {
        clearDB();
        openDB();
        
        db.set(o);

        closeDB();
    }
    
    public void readTest() {
        openDB();

        // just read everything in the db:
        ObjectSet result=db.get(null);
        log("db has "+result.size()+" objects");
	Map  counter = new TreeMap();
        while(result.hasNext()) {
            //log(" : "+result.next());
	    increment(counter,result.next().getClass().getName());
	}
        result.reset();

	Iterator it = counter.keySet().iterator();
	while (it.hasNext()){
	    Object key = it.next();
            log(" : "+key+" : "+counter.get(key));
	}

        closeDB();
    }
    
    // use a map as a counter...
    public void increment(Map m,Object key) {
	int c = 0;
	try { c = ((Integer)m.get(key)).intValue(); } catch (Exception e){}
	m.put(key,new Integer(c+1));
    }
}
