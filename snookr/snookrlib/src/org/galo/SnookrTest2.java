package org.galo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Date;
import java.io.*;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.BufferedInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

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
import org.galo.snookr.Snookr;
import org.galo.util.Timer;


public class SnookrTest2 {
    // Snookr
    Snookr snookr;
    Flickr f;
    REST rest;
    RequestContext requestContext;
    
    public SnookrTest2() throws ParserConfigurationException, IOException, SAXException {
        
        snookr = Snookr.getInstance();
        snookr.setPropertiesFromFile(new File(new File(JnlpPersist.appDirPath()),"snookr.properties"));
        snookr.setup();
        snookr.authenticate();
        f = snookr.getFlickr();
        rest = snookr.getREST();
        requestContext = snookr.getRequestContext();
        
        try {
            //testTestInterface();
            testGetPhotos();
            //testGetTags();
            //testGetImage();
            //testChecksumImage();
            //testSearchMachineTag();
            
            //String fileName = "/home/daniel/media/2005_07_31/IMG_1242.JPG";
            //testUploadImage(new File(fileName));
            
            //testUploadAllImages();
            
            //testReadAllTags();
        } catch(FlickrException e) {
            
            System.out.println("errorCode: "+e.getErrorCode());
            System.out.println("errorMessage: "+e.getErrorMessage());
            System.out.println("threw FlickrException");
            e.printStackTrace();
        }
    }
    
    // maybe we could thread this....
    private void testReadAllTags() throws IOException, SAXException, FlickrException {
        // The list we are trying to produce
        List photoList = new ArrayList();
        int perPage = 500; // bigger is faster, 500 max
        // get first page of "my photos"
        PhotosInterface iface = f.getPhotosInterface();
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
        
        tt.restart();
        System.out.println("Now fill-in full");
        List photoFullList = new ArrayList();
        for (Iterator it=photoList.iterator(); it.hasNext(); ) {
            Photo photo = (Photo) it.next();
            //replace photo by full
            Photo photoFull = iface.getPhoto(photo.getId()); // maps to getInfo
            photoFullList.add(photoFull);
            
            if (photoFullList.size()%100==1) {
                System.out.println(" full  "+photoFull.getId()+"="+photo.getId()+" sofar:"+photoFullList.size()+" @"+tt.rate(photoFullList.size())+" ph/s");
            }
            
            //System.out.println("photo("+photo.getId()+") "+photo.getTitle());
        }
        
    }
    
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static void log(String s) {
        System.out.println( ""+sdf.format(new Date())+" "+s);
    }
    private void testUploadAllImages() throws IOException, SAXException, FlickrException {
        File baseDir = new File(JnlpPersist.baseDir());
        Timer tt = new Timer();
        List dirs = new java.util.Vector();
        List files = new java.util.Vector();
        new BaseWalker(new ListingFileHandler(dirs),new ListingFileHandler(files)).execute(baseDir);
        log( "Found  "+files.size()+" files, "+dirs.size()+" dirs  in "+tt.diff()+"s ("+tt.rate(files.size()+dirs.size())+" [file|dir]/s) for basedir: "+baseDir );
        
        Timer ttt = new Timer();
        int uploadCount=0;
        for (int i=0;i<files.size();i++) {
            File imageFile = (File)files.get(i);
            String fileName = imageFile.getName();
            if ( fileName.endsWith(".JPG") || fileName.endsWith(".jpg") ) {
                //log("Considering - "+imageFile.getName());
                try {
                    //TODO used to setupFlickr every Time ? why.
                    uploadCount+=testUploadImage(imageFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log("Skipping (!.JPG) - "+imageFile);
            }
            log( "Uploaded  "+uploadCount+"/"+i+" of "+files.size()+" images in "+tt.diff()+"s ("+tt.rate(uploadCount)+" images/s) from "+baseDir );
        }
    }
    private int testUploadImage(File imageFile) throws IOException, SAXException, FlickrException {
        String digest = digest(imageFile);
        String snookrmd5tag = "snookr:md5="+digest;
        //log("Considering - "+imageFile.getName());
        
        // find an image with md5 tag
        PhotosInterface iface = f.getPhotosInterface();
        SearchParameters searchParams = new SearchParameters();
        searchParams.setUserId(requestContext.getAuth().getUser().getId());
        
        // Searching for a particular COMPLETE machine tag
        //searchParams.setMachineTags(new String[]{snookrmd5tag});
        searchParams.setTags(new String[]{snookrmd5tag});
        
        // find at least one photo with correct signature...
        PhotoList photos = iface.search(searchParams,1, 1);
        if (photos!=null && photos.size()>0) {
            Photo photo = (Photo)photos.get(0);
            log("Skipping - found photo - id:"+photo.getId()+" t:"+photo.getTitle()+" md5:"+digest);
            return 0;
        }
        
        // else Upload
        
        
        Uploader uploader = new Uploader(f.getApiKey(), rest);
        InputStream in = null;
        try {
            log("Uploading - "+imageFile);
            in = new FileInputStream(imageFile);
            UploadMetaData metaData = new UploadMetaData();
            metaData.setTitle(imageFile.getName());
            metaData.setPublicFlag(true);
            metaData.setTags(Arrays.asList(new Object[]{snookrmd5tag,"snookrd"}));
            String photoId = uploader.upload(in, metaData);
            return 1;
        } finally {
            IOUtilities.close(in);
            //  return 0;
        }
        
    }
    
    /* this depends on added functionality in
       the SearchParameters Class...
     */
    private void testSearchMachineTag() throws IOException, SAXException, FlickrException {
        int perPage = 10;
        // get first page of "my photos"
        
        PhotosInterface iface = f.getPhotosInterface();
        SearchParameters searchParams = new SearchParameters();
        searchParams.setUserId(requestContext.getAuth().getUser().getId());
        // TODO fix this logic... for finding partial machine tags
        //searchParams.setMachineTags(new String[]{"snookr:"});
        searchParams.setTags(new String[]{"snookr:"});
        
        PhotoList photos = iface.search(searchParams, perPage, 0);
        
        System.out.println("returned machinetag photo count - "+photos.size());
        //System.out.println("photos instanceof - "+photos.getClass().getName());
        for (Iterator it=photos.iterator(); it.hasNext(); ) {
            Photo photo = (Photo) it.next();
            System.out.println("photo - id:"+photo.getId()+" t:"+photo.getTitle());
        }
        
    }
    private void testChecksumImage() throws IOException, SAXException, FlickrException {
        // String photoid = "388235234" on danerooim
        String photoid = "396109516";
        
        
        PhotosInterface iface = f.getPhotosInterface();
        Photo photo = iface.getPhoto(photoid);
        
        System.out.println("photo - id:"+photo.getId()+" t:"+photo.getTitle());
        
        String filename = "original."+photo.getId()+".jpg";
        
        /*
        System.out.println("Now writing " + filename);
        BufferedInputStream inStream = new BufferedInputStream(photo.getOriginalAsStream());
        File newFile = new File(filename);
         
        FileOutputStream fos = new FileOutputStream(newFile);
        int read;
        while ((read = inStream.read()) != -1) {
            fos.write(read);
        }
        fos.flush();
        fos.close();
        inStream.close();
         */
        
        String digest = digest(new File(filename));
        System.out.println("photo - id:"+photo.getId()+" md5sum:"+digest);
        
        String snookrmd5tag = "snookr:md5="+digest;
        
        //now that I have the digest
        // - check if the tag exists
        Collection tags = photo.getTags();
        Iterator tagsIter = tags.iterator();
        while (tagsIter.hasNext()) {
            Tag tag = (Tag) tagsIter.next();
            System.out.println("  tag - id:"+tag.getId()+" v:"+tag.getValue()+" r:"+tag.getRaw());
            if (tag.getRaw().equals(snookrmd5tag)) {
                System.out.println("tag alread exists");;
                break;
            }
        }
        
        // - add a tag
        String[] tagsToAdd = {
            snookrmd5tag,
            /*
            "snookr:owner=daniel",
            "snookr:machine=cantor",
            "snookr:machine=dirac",
             */
        };
        iface.addTags(photoid, tagsToAdd);
        
    }
    
    private void testGetImage() throws IOException, SAXException, FlickrException {
        // String photoid = "388235234" on danerooim
        String photoid = "396109516";
        
        PhotosInterface iface = f.getPhotosInterface();
        Photo photo = iface.getPhoto(photoid);
        
        System.out.println("photo - id:"+photo.getId()+" t:"+photo.getTitle());
        
        String filename = "original."+photo.getId()+".jpg";
        System.out.println("Now writing " + filename);
        BufferedInputStream inStream = new BufferedInputStream(photo.getOriginalAsStream());
        File newFile = new File(filename);
        
        FileOutputStream fos = new FileOutputStream(newFile);
        int read;
        while ((read = inStream.read()) != -1) {
            fos.write(read);
        }
        fos.flush();
        fos.close();
        inStream.close();
        
    }
    private void testGetTags() throws IOException, SAXException, FlickrException {
        int perPage = 10;
        // get first page of "my photos"
        
        PhotosInterface iface = f.getPhotosInterface();
        SearchParameters searchParams = new SearchParameters();
        searchParams.setUserId(requestContext.getAuth().getUser().getId());
        
        PhotoList photos = iface.search(searchParams, perPage, 0);
        
        System.out.println("returned photo count - "+photos.size());
        //System.out.println("photos instanceof - "+photos.getClass().getName());
        for (Iterator it=photos.iterator(); it.hasNext(); ) {
            Photo photo = (Photo) it.next();
            System.out.println("photo - id:"+photo.getId()+" t:"+photo.getTitle());
            
            //replace photo by full...
            photo = iface.getPhoto(photo.getId()); // maps to getInfo
            Collection tags = photo.getTags();
            if (tags!=null) {
                for (Iterator i2=tags.iterator(); i2.hasNext(); ) {
                    Tag tag = (Tag)i2.next();
                    System.out.println("  tag - id:"+tag.getId()+" v:"+tag.getValue()+" r:"+tag.getRaw());
                }
            }
            
        }
        
    }
    private void testGetPhotos() throws IOException, SAXException, FlickrException {
        int perPage = 10;
        // get first page of "my photos"
        
        PhotosInterface iface = f.getPhotosInterface();
        SearchParameters searchParams = new SearchParameters();
        searchParams.setUserId(requestContext.getAuth().getUser().getId());
        searchParams.setExtrasDateTaken(true);
        //searchParams.setExtrasTags(true);
        //searchParams.setExtrasMachineTags(true);
        PhotoList photos = iface.search(searchParams, perPage, 0);
        
        System.out.println("returned photo count - "+photos.size());
        //System.out.println("photos instanceof - "+photos.getClass().getName());
        for (Iterator it=photos.iterator(); it.hasNext(); ) {
            Photo photo = (Photo) it.next();
            System.out.println("photo - id:"+photo.getId()+" t:"+photo.getTitle()+
                    " dt:"+photo.getDateTaken()
                    //" tags:"+photo.getTags().size()
                    );
        }
        
    }
    
    // check testInterface.login|echo
    private void testTestInterface() throws IOException, SAXException, FlickrException {
        TestInterface testInterface = f.getTestInterface();
        
        User u = testInterface.login();
        System.out.println("got loginUser id: "+u.getId());
        System.out.println("");
        
        //List params = Collections.EMPTY_LIST;
        ArrayList params = new ArrayList();
        try {
            params.add(new Parameter("name","value"));
            params.add(new Parameter("universe","42"));
        } catch (Exception e) {}
        
        
        Collection results =  testInterface.echo(params);
        //System.out.println("results instanceof - "+results.getClass().getName());
        for (Iterator it=results.iterator(); it.hasNext(); ) {
            Object element = it.next();
            //System.out.println("result - "+element+ " is a "+element.getClass().getName());
            System.out.println("result - "+element);
        }
        
    }
    
    
    public static void main(String[] args) {
        try {
            SnookrTest2 t = new SnookrTest2();
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
