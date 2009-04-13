package net.snookr.util;

import java.util.Date;
import java.io.File;


import com.drew.metadata.Metadata;
//import com.drew.metadata.Tag;
import com.drew.metadata.Directory;
import com.drew.imaging.jpeg.JpegMetadataReader;
//import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.CanonMakernoteDirectory;
import java.util.Iterator;

public class Exif {

    public static final String UNKNOWN_CAMERA = "UNKNOWN";

    public static Date getExifDate(File f) {
        try {
            // equivalent function for InputStream
            Metadata metadata = JpegMetadataReader.readMetadata(f);
            Directory directory = metadata.getDirectory(ExifDirectory.class);
            Date d = directory.getDate(ExifDirectory.TAG_DATETIME);
            if (d == null) {
                d = DateFormat.EPOCH;
            //System.err.println("exif for: "+f.getPath()+" d:"+d);
            }
            return d;

        } catch (com.drew.metadata.MetadataException me) {
            //System.err.println(me.getClass().getName()+" "+me.getMessage()+" f: "+f);
        } catch (JpegProcessingException jpe) {
            //System.err.println(jpe.getClass().getName()+" "+jpe.getMessage()+" f: "+f);
        } catch (Exception e) {
            //System.err.println(e.getClass().getName()+" "+e.getMessage()+" f: "+f);
        }
        //System.err.println("exif for: "+f.getPath()+" d:"+EPOCH);
        return DateFormat.EPOCH;
    }

    public static String getCamera(File f) {
        try {
            // equivalent function for InputStream
            Metadata metadata = JpegMetadataReader.readMetadata(f);
            Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
            String cameraMake = exifDirectory.getString(ExifDirectory.TAG_MAKE);
            String cameraModel = exifDirectory.getString(ExifDirectory.TAG_MODEL);

            if ("".equals(cameraMake) && "".equals(cameraModel)) {
                return UNKNOWN_CAMERA;
            }
            if (cameraMake == null && cameraModel == null) {
                return UNKNOWN_CAMERA;
            }
            return "" + cameraMake + "|" + cameraModel;
        } catch (JpegProcessingException jpe) {
            //System.err.println(jpe.getClass().getName()+" "+jpe.getMessage()+" f: "+f);
        } catch (Exception e) {
            //System.err.println(e.getClass().getName()+" "+e.getMessage()+" f: "+f);
        }
        //System.err.println("exif for: "+f.getPath()+" d:"+EPOCH);
        return UNKNOWN_CAMERA;
    }

    public static void identifyCamera(File f) {
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(f);
            Directory makerDirectory = metadata.getDirectory(CanonMakernoteDirectory.class);

            String imageNumber = makerDirectory.getString(CanonMakernoteDirectory.TAG_CANON_IMAGE_NUMBER);
            String serialNumber = makerDirectory.getString(CanonMakernoteDirectory.TAG_CANON_SERIAL_NUMBER);
            String ownerName = makerDirectory.getString(CanonMakernoteDirectory.TAG_CANON_OWNER_NAME);
            System.out.println("Camer ID: imgNum:"+imageNumber+" serialNum:"+serialNumber+" ownerName:"+ownerName);
        } catch (JpegProcessingException jpe) {
            //System.err.println(jpe.getClass().getName()+" "+jpe.getMessage()+" f: "+f);
        } catch (Exception e) {
            //System.err.println(e.getClass().getName()+" "+e.getMessage()+" f: "+f);
        }
    }

    public static void showAllTags(File f) {
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(f);
            // iterate through metadata directories
            Iterator directories = metadata.getDirectoryIterator();
            while (directories.hasNext()) {
                Directory directory = (Directory) directories.next();
                System.out.println("-=-=-= Directory : "+directory.getClass().getName());
                // iterate through tags and print to System.out
                Iterator tags = directory.getTagIterator();
                while (tags.hasNext()) {
                    Tag tag = (Tag) tags.next();
                    // use Tag.toString()
                    System.out.println(tag);
                }
            }
        } catch (JpegProcessingException jpe) {
            //System.err.println(jpe.getClass().getName()+" "+jpe.getMessage()+" f: "+f);
        } catch (Exception e) {
            //System.err.println(e.getClass().getName()+" "+e.getMessage()+" f: "+f);
        }
    }
}
