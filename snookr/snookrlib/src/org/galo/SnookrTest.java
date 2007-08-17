/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2006
 */

package org.galo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map;
import java.util.Date;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.BufferedInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.util.IOUtilities;
import com.aetrion.flickr.test.TestInterface;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.Parameter;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.tags.Tag;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.aetrion.flickr.uploader.Uploader;

import org.galo.filesystem.*;
import org.galo.util.Timer;
import org.galo.snookr.Snookr;
import org.galo.model.FlickrPhotoEntry;

// Db4o
import com.db4o.*;
import com.db4o.ext.ExtDb4o;
import com.db4o.query.*;

public class SnookrTest {
    //Flickr f;
    // Db4o
    ObjectContainer db;
    // Snookr
    Snookr snookr;

    static void log(String msg) { System.err.println(msg);  }

    public SnookrTest() throws ParserConfigurationException, IOException, SAXException {
        snookr = Snookr.getInstance();
        snookr.setPropertiesFromFile(new File(new File(JnlpPersist.appDirPath()),"snookr.properties"));
        snookr.setup();
        snookr.authenticate();

        try {
            openDB();
            //printPhotosFromDB();

            /* 
               WORK IN PROGRESS

               Assumptions
               the dbList should always have an md5sum.

               Algorithm for getting from Flickr
               -Check Flickr For sanity.
                   are all photos snookrd ?
                   are all snookrd photos identified with unique md5
                Alternatively 
                   are all snookrd photos uniquely identified by md5.

               Then create the list of Flickr images from getPhotoList
               compare with database version of list by photoid.

               if any photo in flickrlist, has no equivalent in dblist
               create it in db (adding md5)

               any photos in db without md5 should be resolved.


               get all photos - 
               get all photos tagged with snookr:md5:*
               getAll Tags with counts
                if num photos==num tags with md5, and count==1 for all. ok
             */
	    //readAllFromDB();
            //if (snookr==null) 
		getAndSavePhotos();


        } catch(FlickrException e) {
            
            System.out.println("errorCode: "+e.getErrorCode());
            System.out.println("errorMessage: "+e.getErrorMessage());
            System.out.println("threw FlickrException");
            e.printStackTrace();
        } finally {
            closeDB();
        }
        

    }

    /*
      Getting a valid set from Flickr without fetching every photo:
      Get all photos.
      Get All snookrd photos
      Get All tags,
      assert one-to-one
      Then associate.

     */

    // maybe we could thread this....
    private void getAndSavePhotos() throws IOException, SAXException, FlickrException {
        // The list we are trying to produce
        List photoList = snookr.getPhotoList();

        //if (photoList!=null) return;

        List photoFullList = snookr.getPhotoFullList(photoList);

        readAllFromDB();

        //savePhotoList(photoList);
        savePhotoList(photoFullList);

        readAllFromDB();
        //readPhotoList();


    }

    private void savePhotoList(List photoList) {
        for (Iterator it=photoList.iterator(); it.hasNext(); ) {
            Photo photo = (Photo) it.next();
            // resolve to DB.
            FlickrPhotoEntry qbe = new FlickrPhotoEntry();
            qbe.photoId = photo.getId();
            // get The Photo by Id...
            ObjectSet result = db.get(qbe);
            if (result!=null && result.size()==0) {
                System.out.println("Saving : "+qbe.photoId);
                FlickrPhotoEntry toSave = new FlickrPhotoEntry();
                toSave.photoId = photo.getId();
                toSave.title = photo.getTitle();
                toSave.dateTaken = photo.getDateTaken();
                toSave.datePosted = photo.getDatePosted();

                // and now md5...
		Collection tags = photo.getTags();
		
		if (tags!=null) {
		    for (Iterator i2=tags.iterator(); i2.hasNext(); ) {
			Tag tag = (Tag)i2.next();
			System.out.println("  tag - id:"+tag.getId()+" v:"+tag.getValue()+" r:"+tag.getRaw());
			String raw = tag.getRaw();
			if (raw!=null && raw.startsWith("snookr:md5=")) {
			    toSave.md5=raw.substring(11);
			    System.out.println("  tag - id:"+tag.getId()+" v:"+tag.getValue()+" r:"+tag.getRaw());
			}
		    }
		}

                db.set(toSave);
            } else {
                System.out.println("Already had : "+qbe.photoId);
            }
        }
    }
    public void readAllFromDB() {
        // just read everything in the db:
        ObjectSet result=db.get(null);
        log("db has "+result.size()+" objects");
	Map  counter = new TreeMap();
        while(result.hasNext()) {
	    Object o  = result.next();
            log(" : "+o);
	    increment(counter,o.getClass().getName());

	}
        result.reset();

	Iterator it = counter.keySet().iterator();
	while (it.hasNext()){
	    Object key = it.next();
            log(" : "+key+" : "+counter.get(key));
	}
    }
    // use a map as a counter...
    public void increment(Map m,Object key) {
	int c = 0;
	try { c = ((Integer)m.get(key)).intValue(); } catch (Exception e){}
	m.put(key,new Integer(c+1));
    }

    /*
    private void readPhotoList() {
        for (Iterator it=photoList.iterator(); it.hasNext(); ) {
            Photo photo = (Photo) it.next();
            db.set(photo);
        }
    }
    */

    private File yapFile() {
        File appDir = new File(JnlpPersist.appDirPath());
        return new File(appDir,"snookrrepo.yap");
    }
    private void openDB() {
        if(db==null) {
            ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
            ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
            db = Db4o.openFile(yapFile().toString());
        }
    }
    private void clearDB() {
        closeDB();
        yapFile().delete();
        openDB();
    }
    private void closeDB() {
        if (db!=null) {
            db.close();
        }
        db=null;
    }



    
    public static void main(String[] args) {
        try {
            SnookrTest t = new SnookrTest();
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


    static final String algorithm = "MD5";
    
    public static String digest(File f) throws IOException {
        InputStream in = new FileInputStream(f);
        String digest =  digest(in);
        in.close();
        return digest;
    }

    static MessageDigest getImplementation() {
        try {
            return MessageDigest.getInstance(algorithm);  
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae.getMessage());
        }
    }
    public static String digest(byte b[]) {
        MessageDigest md = getImplementation();
        return toHex(md.digest(b));
    }


    public static String digest(InputStream in) throws IOException {
        MessageDigest md = getImplementation();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1){
            md.update(buffer,0, read);
        }
        return toHex(md.digest());
    }
    private static String toHex(byte hash[]){
        StringBuffer buf = new StringBuffer(hash.length * 2);
        for (int i=0; i<hash.length; i++){
            int intVal = hash[i] & 0xff;
            if (intVal < 0x10){
                // append a zero before a one digit hex
                // number to make it two digits.
                buf.append("0");
            }
            buf.append(Integer.toHexString(intVal));
        }
        return buf.toString();
    }

}

