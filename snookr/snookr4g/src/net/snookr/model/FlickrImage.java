package net.snookr.model;
import java.util.Date;
import java.text.SimpleDateFormat;

class FlickrImage {
    String photoid; // canonical path
    String md5;
    Date taken;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String toString() {
        String lm="????-??-?? ??:??:??";
        try { lm = sdf.format(taken);} catch (Exception e) {}
        return "id:"+photoid+" md5:"+md5+" taken:"+lm;
    }
}

