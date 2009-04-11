package net.snookr.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.File;


import com.drew.metadata.Metadata;
//import com.drew.metadata.Tag;
import com.drew.metadata.Directory;
import com.drew.imaging.jpeg.JpegMetadataReader;
//import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.exif.ExifDirectory;

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
}
