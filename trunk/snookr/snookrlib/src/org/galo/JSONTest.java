/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2006

Invoke test
ant compile.java; /usr/java/jdk1.5.0_07/bin/java -cp lib/metadata-extractor-2.2.2.jar:lib/ostermillerutils-1.04.03.jar:lib/jsonrpc-1.0.jar:target/classes/java/ org.galo.HttpPostTest

 */

package org.galo;

import java.util.Map;
import java.io.*;
//import org.json.JSONObject;
//import org.json.JSONArray;
import com.metaparadigm.jsonrpc.JSONSerializer;

public class JSONTest {
    
    
    public static void main(String[] args) {
        new JSONTest().test();
    }

    void log(String msg) { System.err.println(msg);  }

    void test() {
        log("test what ?");
    }

    public static String toJSON(Object o) {
        return toJSON(o,true);
    }
    public static String toJSON(Object o,boolean classHints) {
        JSONSerializer ser = new JSONSerializer();
        try {
            ser.registerDefaultSerializers(); // Throws Esception
            ser.setMarshallClassHints(classHints);
            
            return ser.toJSON(o); // Throws MarshalException
        } catch (Exception e) { }
        return null;
    }
    public String writeTest(Object o) {
        String jsonStr=null;
        try {
            JSONSerializer ser = new JSONSerializer();
            ser.registerDefaultSerializers();
            //ser.setMarshallClassHints(false);
            
            File appDir = new File(JnlpPersist.appDirPath());
            File jsonRepo = new File(appDir,"galorepo.json");
            
            FileWriter fw = new FileWriter(jsonRepo);
            jsonStr=ser.toJSON(o);
            fw.write(jsonStr);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
    
    public Map readTest() {
        try {
            JSONSerializer ser = new JSONSerializer();
            ser.registerDefaultSerializers();
            //ser.setMarshallClassHints(false);
            
            File appDir = new File(JnlpPersist.appDirPath());
            File jsonRepo = new File(appDir,"galorepo.json");
            
            FileReader reader = new FileReader(jsonRepo);
            String jsonText = org.galo.util.Stream.readerToString(reader);
            reader.close();
            
            Object o = ser.fromJSON(jsonText);
            
            //System.err.println(o.getClass().getName());
            if (o instanceof Map) return (Map)o;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
