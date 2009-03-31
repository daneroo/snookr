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
    public static final SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd");

    public static Date startOfDay(Date ref, int offsetInDays) {
        Date offset = new Date(ref.getTime() + offsetInDays * 24 * 60 * 60 * 1000l);
        Date startOfDay = parseDay(dayFmt.format(offset));
        return startOfDay;
    }

    public static Date parseISO(String dateStr) {
        try {
            return isoFmt.parse(dateStr);
        } catch (ParseException ex) {
            Logger.getLogger(EnergyEventCorrelator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static Date parseDay(String dateStr) {
        try {
            return dayFmt.parse(dateStr);
        } catch (ParseException ex) {
            Logger.getLogger(EnergyEventCorrelator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
