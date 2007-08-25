/*
 * FSImage.java
 */

package net.snookr.model;

import java.util.Date;
import java.io.File;
import net.snookr.util.DateFormat;

/**
 *
 * @author daniel
 */
public class FSImage {
    String fileName; // canonical path - Natural (unique) key
    Long size;
    String md5;
    Date lastModified;
    Date taken;  // this is extracted from exif data when available - null if none available

    /** Creates a new instance of FSImage */
    public FSImage() {
    }
    
    public String toString() {
        File f = new File(fileName);
        return "file:"+f.getPath()+" sz:"+size+" md5:"+md5+" mod:"+safeDate(lastModified)+" exif:"+safeDate(taken);
    }
    String safeDate(Date d) {
        return DateFormat.format(d,"????-??-?? ??:??:??");
    }
    
}

