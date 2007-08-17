/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2006

Invoke test
ant compile.java; /usr/java/jdk1.5.0_07/bin/java -cp lib/metadata-extractor-2.2.2.jar:lib/ostermillerutils-1.04.03.jar:lib/jsonrpc-1.0.jar:target/classes/java/ org.galo.JnlpPersist

 */

package org.galo;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class JnlpPersist {
    
    /* This is a small class to test the persisten storage for a jnlp app
       There are two approaches:
         Jnlp PersistenceService (Muffins aka JWS cookies
           key is a url
           only small amounts of data
           Meant to be persisted on server 
            manage state TEMPORARY/CACHED/DIRTY
           
         Local directory : <user.home>/.galo
             store a properties file : galo.properties
             eventually store a db.

         Combine both _?_?
     */
    public static void main(String[] args) {
        new JnlpPersist().test();
    }

    static void log(String msg) { System.err.println(msg);  }

    void test() {
        log("JnlpPersist test -- start");
        testHomeDir();
        log("JnlpPersist test -- done");
        log("");
    } 

    /* see if we can read/create an app folder in user.home
       see if we can read/create/update a properties file in that dir
     */
    void testHomeDir() {
        // settings
        String appDirName = ".galo";
        String appPropsFileName = "galo.properties";

        // home dir
        File homeDir = new File(homeDirPath());
        log("user.home: "+(homeDir.exists()?"exists":"does not exits"));
        log("user.home: "+(homeDir.isDirectory()?"is":"is not")+" a directory");
        log("user.home: "+(homeDir.canRead()?"is":"is not")+" readble");
        log("user.home: "+(homeDir.canWrite()?"is":"is not")+" writeable");
        
        // app dir
        File appDir = new File(homeDir,appDirName);
        // check if appDir already exists - and is a directory
        //   could also check that it is writeable
        if ( !appDir.exists() ) {
            boolean success = appDir.mkdir();
            log("appDir: "+(success?"created":"not created"));
        }
        log("appDir: "+(appDir.exists()?"exists":"does not exist"));
        log("appDir: "+(appDir.isDirectory()?"is":"is not")+" a directory");
        log("appDir: "+(appDir.canRead()?"is":"is not")+" readable");
        log("appDir: "+(appDir.canWrite()?"is":"is not")+" writeable");

        // app properties file
       File appPropsFile = new File(appDir,appPropsFileName);
       // get default properties
       // galo.siteID
       // galo.machineID
       // galo.baseDir

       Properties appProps = new Properties();
       try {
           if (appPropsFile.exists()) {
               appProps.load(new FileInputStream(appPropsFile));
               log("appProps: read");
           }
       } catch (IOException ioe) {
           ioe.printStackTrace();
           log("appProps: could not be read");
       }

       String baseDirPath = baseDir(appProps.getProperty("galo.baseDir"));
       if (baseDirPath!=null) {
           appProps.setProperty("galo.baseDir",baseDirPath);
       }

       try {
           String header = "Galo Properties File v1.0";
           appProps.store(new FileOutputStream(appPropsFile),header);
           // close output stream
       } catch (IOException ioe) {
           ioe.printStackTrace();
       }

       log("appProps: written");
      
         
    }

    
    public static String homeDirPath() {
        // home dir
        String userHome = System.getProperty("user.home");
        return userHome;
    }
    public static String appDirPath() {
        String appDirName = ".galo";
        File appDir = new File(new File(homeDirPath()),appDirName);
        //return appDir.toURI().toString();
        return appDir.toString();
    }
    public static String baseDir() {
        return baseDir(null);
    }
    public static String baseDir(String firstPath) {
       // can be absolute or relative to homeDir

        // put this in a global place
        String userHome = System.getProperty("user.home");
        File homeDir = new File(userHome);

       String baseDirPath[] = { 
           firstPath,
           "My Documents/My Pictures",
           "Mes documents/Mes images",
           "photo",
           "media/photo",
           "media",
           "/archive/media/photo" 
       };
       // try paths relative to homeDir : if absolute it will work anyways.?
       for (int i=0; i<baseDirPath.length; i++) {
           String path = baseDirPath[i];
           if (path==null) continue;
           //log("trying path :"+path);
           File baseDir  = new File(path);
           if (!baseDir.isAbsolute()) {
               //log("resolving relative path");
               baseDir  = new File(homeDir,path);
           }
           if (baseDir.exists()) { // and .isDirectory ?
               try {
                   String cpath = baseDir.getCanonicalPath();
                   log("using path :"+cpath);
                   return cpath;
               } catch (IOException ioecp) { }// igonore 
           }
       }
       return null;
    }
    
}
