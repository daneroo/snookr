/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

import org.galo.digest.MD5;
import org.galo.util.Timer;
import org.galo.filesystem.DirectoryWalker;
import org.galo.filesystem.FileWalker;
import org.galo.filesystem.BaseWalker;
import org.galo.filesystem.IFileHandler;
import org.galo.filesystem.ListingFileHandler;
import org.galo.model.Image;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.Directory;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifDirectory;
/**
 * First Client of file traversal
 * -Creates a list of files and directories.
 */
public final class GaloClient implements Runnable {
    
    public static void main(String[] aArguments) throws FileNotFoundException {
        //String basedirPath = getProperties().getProperty("galo.basedir");
        String basedirPath = getBundle().getString("galo.basedir");
        File baseDir = new File(basedirPath);
        GaloClient galoClient = new GaloClient(baseDir);
        galoClient.run();
    }
    
    private File baseDir=null;
    public GaloClient(File baseDir) {
        this.baseDir = baseDir;
    }
    
    
    public void run() {
        Timer outer = new Timer();
        
        Timer tt = new Timer();
        getFilesAndDirs();
        System.out.println( "Found  "+files.size()+" files, "+dirs.size()+" dirs  in "+tt.diff()+"s ("+tt.rate(files.size()+dirs.size())+" [file|dir]/s) for basedir: "+baseDir );
        
        tt.restart();
        mapImages();
        System.out.println( "Mapped "+files.size()+" files in "+tt.diff()+"s ("+tt.rate(files.size())+" files/s)");
        
        tt.restart();
        fillInExif();
        System.out.println( "Exifed "+files.size()+" files in "+tt.diff()+"s ("+tt.rate(files.size())+" files/s)");
        
        
        tt.restart();
        fillInDigest();
        System.out.println( "Digested "+files.size()+" files in "+tt.diff()+"s ("+tt.rate(files.size())+" files/s)");
        
        tt.restart();
        //printImages();
        System.out.println( "Printed "+files.size()+" files in "+tt.diff()+"s ("+tt.rate(files.size())+" files/s)");
        
        System.out.println( "Total time:"+outer.diff()+"s for basedir: "+baseDir );
    }
    
    List dirs = null;
    List files = null;
    public void getFilesAndDirs() {
        dirs = new java.util.Vector();
        files = new java.util.Vector();
        new BaseWalker(new ListingFileHandler(dirs),new ListingFileHandler(files)).execute(baseDir);
    }
    
    Map imageForFile = null;
    public void mapImages() {
        imageForFile = new TreeMap();
        Iterator iter = files.iterator();
        while ( iter.hasNext() ) {
            File f = (File)iter.next();
            imageForFile.put(f, makeImage(f));
        }
    }
    
    
    // factor into fillInExif - fillInDigest
    public Image makeImage(File f) {
        Image image = new Image();
        
        // file name idea needs to be refined, (encoding, directory, host,...)
        image.setFileName(f.toURI().toString());
        // setStamp after we have exif data (or not)
        image.setFileSize(f.length());
        
        image.setLastModified(new Date(f.lastModified()));
        // set stamp from lastModified : will be overwritten by exif if available
        image.setStamp(image.getLastModified());
        
        //String stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(f.lastModified()));
        
        return image;
    }
    
    // could iterate over map instead. of files.
    public void fillInDigest() {
        Iterator iter = files.iterator();
        while ( iter.hasNext() ) {
            File f = (File)iter.next();
            Image image = (Image)imageForFile.get(f);
            try {
                image.setMd5(MD5.digest(f));
            } catch (IOException ioe) {}
        }
    }
    
    public void fillInExif() {
        // cause drew does some bad out.println : should recompile.
        PrintStream realout = System.out;
        System.setOut(new PrintStream(nullOutputStream()));
        
        try {
            
            Iterator iter = files.iterator();
            while ( iter.hasNext() ) {
                File f = (File)iter.next();
                Image image = (Image)imageForFile.get(f);
                fillInExif(image,f);
            }
        } finally {
            System.setOut(realout);
        }
    }
    
    public static OutputStream nullOutputStream() {
        return new OutputStream() {
            public void close() throws IOException {}
            public void flush() throws IOException {}
            public void write(byte b[]) throws IOException {}
            public void write(byte b[], int off, int len) throws IOException {}
            public void write(int b) throws IOException {}
        };
    }
    
    public void fillInExif(Image image,File f) {
        try {
            // 1-
            Metadata metadata = JpegMetadataReader.readMetadata(f);
            
            // for debugging, and finding other tags.
            //System.out.println(" exif for :"+f);
            //printAllExifTags(metadata);
            
            Directory directory = metadata.getDirectory(ExifDirectory.class);
            //public static final int 	TAG_DATETIME 	306
            //public static final int 	TAG_EXIF_IMAGE_HEIGHT 	40963
            //public static final int 	TAG_EXIF_IMAGE_WIDTH 	40962
            
            image.setExifDate( directory.getDate(ExifDirectory.TAG_DATETIME) );
            image.setStamp(image.getExifDate());
            
            image.setWidth( directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH) );
            image.setHeight( directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT) );
        } catch (com.drew.metadata.MetadataException me) {
            //throw new RuntimeException(me.getMessage());
        } catch (JpegProcessingException jpe) {
            //throw new RuntimeException(jpe.getMessage());
        }
    }
    
    private static void printAllExifTags(Metadata metadata)  {
        // iterate over the exif data and print to System.out
        Iterator directories = metadata.getDirectoryIterator();
        while (directories.hasNext()) {
            Directory directory = (Directory)directories.next();
            System.out.println(" -="+directory.getClass().getName()+"-=-"+directory.getName()+"=-");
            
            Iterator tags = directory.getTagIterator();
            while (tags.hasNext()) {
                Tag tag = (Tag)tags.next();
                System.out.println("  ---"+tag.getTagType()+"---"+tag);
            }
            if (directory.hasErrors()) {
                Iterator errors = directory.getErrors();
                while (errors.hasNext()) {
                    System.out.println("ERROR: " + errors.next());
                }
            }
        }
    }
    
    
    public void printImages() {
        Iterator iter = files.iterator();
        while ( iter.hasNext() ) {
            File f = (File)iter.next();
            Image image = (Image)imageForFile.get(f);
            System.out.println(""+image);
        }
    }
    
    
    /*
      ClassLoader.getResourceAsStream ("some/pkg/resource.properties");
     */
    static Properties getProperties() {
        try {
            
            ResourceBundle.getBundle("galo");
            
            Properties p = new Properties();
            p.load(GaloClient.class.getClassLoader().getResourceAsStream("galo.properties"));
            return p;
            
        } catch (IOException ioe) {
            // what ?
        }
        return null;
    }
    
    /*
      ResourceBundle.getBundle ("some.pkg.resource");
     */
    static ResourceBundle getBundle() {
        try {
            // Throws unchecked java.util.MissingResourceException
            return ResourceBundle.getBundle("galo");
        } catch (MissingResourceException mre) {
            // what ?
        }
        return null;
    }
}
