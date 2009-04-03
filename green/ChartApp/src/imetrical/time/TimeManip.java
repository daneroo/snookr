/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.time;

import chartapp.EnergyEventCorrelator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
public class TimeManip {

    public static final SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat isoTZFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    public static final SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd");

    public static Date startOfDay(Date ref, int offsetInDays) {
        Date offset = new Date(ref.getTime() + offsetInDays * 24 * 60 * 60 * 1000l);
        Date startOfDay = parseDay(dayFmt.format(offset));
        return startOfDay;
    }

    public static Date parseISOTZ(String dateStrWithTZ) {
        //System.err.println("-dateStrWithTZ=|"+dateStrWithTZ+"|");
        return parseWithFormat(dateStrWithTZ, isoTZFmt);
    }

    public static Date parseISOAsGMT(String dateStrWithoutTZ) {
        // just to handle Timestamp.toString() YYYY-MM-DD HH:MM:SS.0 format
        //System.err.println("-dateStrWithoutTZ=|"+dateStrWithoutTZ+"|");
        if (dateStrWithoutTZ.endsWith(".0")) {
            dateStrWithoutTZ = dateStrWithoutTZ.substring(0, dateStrWithoutTZ.length()-2);
        }
        //System.err.println("+dateStrWithoutTZ=|"+dateStrWithoutTZ+"|");
        return parseWithFormat(dateStrWithoutTZ+"+0000", isoTZFmt);
    }
    public static Date parseISO(String dateStr) {
        return parseWithFormat(dateStr, isoFmt);
    }

    public static Date parseDay(String dateStr) {
        return parseWithFormat(dateStr, dayFmt);
    }

    private static Date parseWithFormat(String dateStr,SimpleDateFormat fmt) {
        try {
            return fmt.parse(dateStr);
        } catch (ParseException ex) {
            Logger.getLogger(EnergyEventCorrelator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
