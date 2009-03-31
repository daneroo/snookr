/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model;

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
public class SignalRange {

    private static final SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd");
    public static final String GRAIN_SECOND = "watt";

    public Date start, stop;
    public String grain = GRAIN_SECOND; //GRAIN_TENSEC
    public int intervalLengthSecs = 1;    // 10

    public SignalRange(String startStr, String stopStr) {
        try {
            start = isoFmt.parse(startStr);
            stop = isoFmt.parse(stopStr);
        } catch (ParseException ex) {
            Logger.getLogger(EnergyEventCorrelator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SignalRange(int daysAgo) {
        start = startOfDay(new Date(), -daysAgo);
        stop = startOfDay(new Date(), -daysAgo + 1);

    }

    public Date startOfDay(Date ref, int offsetInDays) {
        Date offset = new Date(ref.getTime() + offsetInDays * 24 * 60 * 60 * 1000l);
        Date startOfDay = offset;
        try {
            startOfDay = dayFmt.parse(dayFmt.format(offset));
        } catch (ParseException ex) {
            Logger.getLogger(EnergyEventCorrelator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return startOfDay;
    }
}
