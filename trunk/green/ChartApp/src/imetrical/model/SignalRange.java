/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model;

import imetrical.time.TimeManip;
import java.util.Date;

/**
 *
 * @author daniel
 */
public class SignalRange {

    public static final String GRAIN_SECOND = "watt";
    public Date start,  stop;
    public String grain = GRAIN_SECOND; //GRAIN_TENSEC
    public int intervalLengthSecs = 1;    // 10

    public SignalRange(String startStr, String stopStr) {
        start = TimeManip.parseISO(startStr);
        stop = TimeManip.parseISO(stopStr);
    }

    public SignalRange(int daysAgo) {
        start = TimeManip.startOfDay(new Date(), -daysAgo);
        stop = TimeManip.startOfDay(new Date(), -daysAgo + 1);

    }
}
