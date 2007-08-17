/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2006

Invoke test
ant compile.java; /usr/java/jdk1.5.0_07/bin/java -cp lib/metadata-extractor-2.2.2.jar:lib/ostermillerutils-1.04.03.jar:lib/jsonrpc-1.0.jar:target/classes/java/ org.galo.HttpPostTest

 */

package org.galo;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class HttpPostTest {
    
    
    public static void main(String[] args) {
        //String json = " {\"age\":26,\"hairColor\":\"brown\",\"name\":\"Mike\",\"siblingNames\":[\"Matt\",\"Tammy\"]}";
        //String json = "[\"a\",\"b\",\"c\"]";
        String json = "\"abc\"";

        new HttpPostTest().test(json);
    }

    void log(String msg) { System.err.println(msg);  }

    String cookie=null;
    void test(String json) {
        //log("HttpPostTest test -- start");
        post(json);
        //log("HttpPostTest test -- done");
        //log("");
    }
    void login() {
        //post("","http://192.168.3.201:8080/jsonrpc-1.0/register.jsp");
    }
    void post(String json) {
        post(json,"http://192.168.3.201:8080/jsonrpc-1.0/JSON-RPC");
    }
    void post(String json,String useURL) {
        try {
            // connect back to where you came from ? if webstart...
            // 201|boole is dead to me !
            // euler:
            URL serverURL = new URL("http://192.168.3.204/galo/json/testService.php");
            // abel thru openvpn
            //URL serverURL = new URL("http://192.168.5.3/galo/json/testService.php");

            //URL serverURL = new URL("http://192.168.3.201:8080/jsonrpc-1.0/JSON-RPC");
            //URL serverURL = new URL(useURL);


            URLConnection urlcon = serverURL.openConnection();
            urlcon.setDoInput(true); 
            urlcon.setDoOutput(true);
            urlcon.setUseCaches(false);
            urlcon.setDefaultUseCaches(false);
            urlcon.setRequestProperty ("Content-Type", "application/octet-stream");

            // if we ever need to set cookies explicitly: but seems to happen already
            // This is probably handled by the browser because appletviewer
            // doesn't handle this automatically. However if we send the cookie
            // ouselves, it seems to break the sesion in Netscape 4
            // also tried using set/getDefaultRequestProprty but that never works
            // FINAL RESULT: do no handle cookies: the browsers will do it.
            
            if (cookie!=null) {
                //log("   ***   Sending cookie: "+cookie+"   ***");
                urlcon.setRequestProperty("Cookie",cookie); 
                System.out.println("sending cookie: "+cookie);
            }
            
            
            Writer writer = new OutputStreamWriter(urlcon.getOutputStream());


            writer.write(json);
            writer.flush();                
            writer.close();
            
            // this is how we get the cookie that is re-used in submitting.
            
            String newcookie = urlcon.getHeaderField("Set-Cookie");
            if (newcookie!=null) {
                //log("   ***   Saw cookie: "+newcookie+"   ***");
                cookie = newcookie;
                System.out.println("got cookie: "+cookie);
                
            }
            
            Reader reader = new InputStreamReader(urlcon.getInputStream());
            int c;
            while ((c = reader.read()) != -1)  {
                System.out.write(c);
            }
            reader.close();
            System.out.println("");

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    } 
    
    
}
