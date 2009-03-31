/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 *  First problem we are trying to solve, is:
 * dates were parsed by jdbcdataset as local times,
 * but the database is actually populated with GMT
 *  so we simply reprint date as isoFMT with local TimeZon, append +0000 and reparse.
 */
public class TimeConvert {

    static String isoStr = "yyyy-MM-dd HH:mm:ss";
    static String isoRFC822Str = "yyyy-MM-dd HH:mm:ssZ";
    static SimpleDateFormat localFormat = new SimpleDateFormat(isoStr);
    static SimpleDateFormat gmtFormat = new SimpleDateFormat(isoStr);
    static SimpleDateFormat rfc822Format = new SimpleDateFormat(isoRFC822Str);


    static { // static init block
        System.out.println("Static init");
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        gmtFormat.setCalendar(cal);
    }

    // input date is local
    public static Date localToGMT(Date localDate) {
        String localString = localFormat.format(localDate);
        Date gmtDate = null;
        try {
            //gmtDate = rfc822Format.parse(localString + "+0000");
            gmtDate = gmtFormat.parse(localString);
        } catch (ParseException ex) {
            Logger.getLogger(TimeConvert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gmtDate;
    }

    public static Date gmtToLocal(Date gmtDate) {
        String gmtString = gmtFormat.format(gmtDate);
        Date localDate = null;
        try {
            localDate = localFormat.parse(gmtString);
        } catch (ParseException ex) {
            Logger.getLogger(TimeConvert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return localDate;
    }

    public static void main(String[] args) {

        String[] dateString = {"2008-03-20 00:00:00", "2008-06-21 00:00:00", "2008-09-22 00:00:00", "2008-12-22 00:00:00",};
        for (int i = 0; i < dateString.length; i++) {
            Date date = null;
            Date dateGMT = null;
            try {
                date = localFormat.parse(dateString[i]);
                roundTripLocal(date);
                roundTripGMT(date);
            } catch (ParseException ex) {
                Logger.getLogger(TimeConvert.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Date now = new Date();
        Date twoMago = new Date(now.getTime() - (60l * 24 * 60 * 60 * 1000));
        System.out.println("--Now:        " + now + " " + rfc822Format.format(now));
        System.out.println("--2mo ago:    " + twoMago + " " + rfc822Format.format(twoMago));
        roundTripLocal(new Date());
        roundTripGMT(new Date());
    }

    public static void roundTripLocal(Date d) {
        Date gmt = localToGMT(d);
        Date local = gmtToLocal(gmt);
        System.out.println(String.format("Local Roundtrip L:%19s   G:%19s   L:%19s", localFormat.format(d), localFormat.format(gmt), localFormat.format(local)));
    }

    public static void roundTripGMT(Date g) {
        Date local = gmtToLocal(g);
        Date gmt = localToGMT(local);
        System.out.println(String.format("GMT   Roundtrip G:%19s   L:%19s   G:%19s", gmtFormat.format(g), gmtFormat.format(local), gmtFormat.format(gmt)));
    }
}
