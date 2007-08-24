/*
 * FSImage.java
 */

package net.snookr.model;

import java.util.Date;
import java.io.File;
import java.text.SimpleDateFormat;

/**
 *
 * @author daniel
 */
public class FSImage {
    String fileName; // canonical path
    Long size;
    String md5;
    Date lastModified;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** Creates a new instance of FSImage */
    public FSImage() {
    }
    
    public String toString() {
        File f = new File(fileName);
        String lm="????-??-?? ??:??:??";
        try { lm = sdf.format(lastModified);} catch (Exception e) {}
        return "f:"+f.getPath()+" sz:"+size+" md5:"+md5+" mod:"+lm;
    }
}

