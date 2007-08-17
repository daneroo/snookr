/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2007
 */

package org.galo.snookr;

import java.io.*;
import java.util.*;
import java.net.URL;

import com.aetrion.flickr.*;
import com.aetrion.flickr.auth.*;
import com.aetrion.flickr.people.*;
import com.aetrion.flickr.photos.*;
import com.aetrion.flickr.tags.*;
import com.aetrion.flickr.uploader.*;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.galo.util.Timer;


/* This class is a Singleton, and is not thread safe (for now)
   
 Example use:
 Snookr snookr = Snookr.getInstance();
 snookr.setPropertiesFromFile(new File("/path/to/myfile.properties"));
 snookr.setup();
 snookr.authenticate();

 */
public class Snookr {
    // instance variables for this session.
    Properties properties;
    Flickr flickr; 
    REST rest;
    RequestContext requestContext;


    public static Flickr getFlickr() {
        return getInstance().flickr;
    }
    
    // Singleton pattern with private constructor.
    static Snookr instance=null;
    public static Snookr getInstance() {
        if (instance == null) {
            instance = new Snookr();
        }
        return instance;
    }
    Snookr() {} // private constructor

    // should be moved..
    public List getPhotoList() throws IOException, SAXException, FlickrException {
        // The list we are trying to produce
        List photoList = new ArrayList();

        int perPage = 500; // bigger is faster, 500 max
        // get first page of "my photos"

        PhotosInterface iface = flickr.getPhotosInterface();
        SearchParameters searchParams = new SearchParameters();
        searchParams.setUserId(requestContext.getAuth().getUser().getId());

        Timer tt = new Timer();
        int currentPage=1;
        while (true) {
            PhotoList photos = iface.search(searchParams, perPage, currentPage);
            photoList.addAll(photos);
            // assert 
            //   photos.getPage()==currentPage
            //   photos.size()== perpage unless last Page
            //   photos.getTotal() / photos.perPage() = photos.getPages() (max +1)
            System.out.println("photos page "+photos.getPage()+"/"+photos.getPages()+" total:"+photos.getTotal()+" returned:"+photos.size()+" sofar:"+photoList.size()+" @"+tt.rate(photoList.size())+" ph/s");

            if (currentPage==photos.getPages()) break;

            currentPage++;

        }
        return photoList;
    }

    public List getPhotoFullList(List photoList) throws IOException, SAXException, FlickrException {
        System.out.println("Now fill-in full");

        PhotosInterface iface = flickr.getPhotosInterface();

        Timer tt = new Timer();
        List photoFullList = new ArrayList();
        for (Iterator it=photoList.iterator(); it.hasNext(); ) {
            Photo photo = (Photo) it.next();

            Photo photoFull = iface.getPhoto(photo.getId()); // maps to getInfo
            photoFullList.add(photoFull);

            if (photoFullList.size()%100==1) { // %100==1
                System.out.println(" full  "+photoFull.getId()+"="+photo.getId()+" sofar:"+photoFullList.size()+" @"+tt.rate(photoFullList.size())+" ph/s");
            }
        }
        return photoFullList;
    }
    

    // requires that properties be set.
    public void setup() {
        boolean debugFlag = false;

        try {
            rest = new REST();
        } catch (ParserConfigurationException pce) {  }

        rest.setHost(getProperty("host"));

        flickr = new Flickr(getProperty("apiKey"),rest);
        Flickr.debugStream = debugFlag;

       // Set the shared secret which is used for any calls which require signing.
        requestContext = RequestContext.getRequestContext();
        requestContext.setSharedSecret(properties.getProperty("secret"));
        
    }
    
    public void authenticate() throws IOException, SAXException {
        boolean verbose=false;
        // if token is defined in properties, check it.
        // if not, get a frob, authorize and produce a new token

        String token = properties.getProperty("token");
        if (token!=null) {
            System.out.println("Checking token: "+token);

            AuthInterface authInterface = flickr.getAuthInterface();
            try {
                Auth auth = authInterface.checkToken(token);
		if (verbose) {
		    System.out.println("-Authentication success");
		    System.out.println("-Token: "+auth.getToken());
		    System.out.println("-nsid: "+auth.getUser().getId());
		    System.out.println("-Realname: "+auth.getUser().getRealName());
		    System.out.println("-Username: "+auth.getUser().getUsername());
		    System.out.println("-Permission: "+auth.getPermission().getType());
                }
                //now setAuth on request Context....
                requestContext.setAuth(auth);
                
            } catch(FlickrException e) {
                e.printStackTrace();
            }
            
            
        } else {  // NOT alreadHaveToken
            
            AuthInterface authInterface = flickr.getAuthInterface();
            String frob = "";
            try {
                frob = authInterface.getFrob();
            } catch(FlickrException e) {
                e.printStackTrace();
            }
            System.out.println("frob: "+frob);
            URL url = authInterface.buildAuthenticationUrl(Permission.WRITE, frob);
            
            // Wait for external authentication: 30 seconds.
            int timeoutSeconds=10;
            System.out.println("You have "+timeoutSeconds+" seconds to grant access at this URL: ");
            System.out.println(url.toExternalForm());
            try { Thread.sleep(timeoutSeconds*1000l); } catch (InterruptedException ie) { }
            /*
            // Used to wait for input but running from ant this is not feasible.
            System.out.println("Press return after you granted access at this URL:"+url.toExternalForm());
            BufferedReader infile =
                new BufferedReader ( new InputStreamReader (System.in) );
            String line = infile.readLine();
            */
            

            try {
                Auth auth = authInterface.getToken(frob);
                System.out.println("Authentication success");
                System.out.println("Token: "+auth.getToken());
                System.out.println("nsid: "+auth.getUser().getId());
                System.out.println("Realname: "+auth.getUser().getRealName());
                System.out.println("Username: "+auth.getUser().getUsername());
                System.out.println("Permission: "+auth.getPermission().getType());
                
                //now setAuth on request Context....
                requestContext.setAuth(auth);
                
                System.out.println("");
                System.out.println("PASTE THIS BACK INTO CODE and set alreadyHaveToker=true");
                System.out.println("Token: "+auth.getToken());
                System.out.println("");
                
                        
            } catch(FlickrException e) {
                System.out.println("Authentication failed");
                e.printStackTrace();
            }
            
        }
    }

    /* START Properties section 
     This is the properties file where Communication is set up is set up
     host : typically www.flickr.com
     apiKey 
     secret
     token


     */

    public String getProperty(String key) {
        try {
            return getProperties().getProperty(key);
        } catch (Exception e) {}
        return null;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    public Properties getProperties() {
        return properties;
    }
    public void setPropertiesFromFile(File f) {
        InputStream in = null;
        try {
            in = new FileInputStream(f);
            Properties newProps = new Properties();
            newProps.load(in);
            setProperties(newProps);
        } catch (IOException ioe) {
            System.err.println("Unable to read properties File "+f);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) { }
            }
        }
    }

    /* END Properties section */
    
    
        
}
