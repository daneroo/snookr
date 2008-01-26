package net.snookr.model;

import java.util.Date;
import net.snookr.util.DateFormat;

public class FlickrImage {
    String photoid; // Foreign Natural (unique) key
    String md5;
    Date taken;   // data seeded from exif data / dan be modified...
    Date posted;  // data of original post to flickr/can be modified...
    Date lastUpdate; // last modification to any metadata/ includes tags,comments,etc...
    
    /** Creates a new instance of FlickrImage */
    public FlickrImage() {
    }
    
    public String toString() {
        return "id:"+photoid+" md5:"+md5+" taken:"+safeDate(taken)+" posted:"+safeDate(posted)+" lastUpdate:"+safeDate(lastUpdate);
    }
    String safeDate(Date d) {
        return DateFormat.format(d,"????-??-?? ??:??:??");
    }
}

