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

    public enum Grain {

        SECOND("watt", 1),
        TENSEC("watt_tensec", 10),
        MINUTE("watt_minute", 60),
        HOUR("watt_hour", 3600);
        //DAY("watt_day",86400)
        private final String tableName;
        private final int intervalLengthSecs; // in seconds

        Grain(String tableName, int intervalLengthSecs) {
            this.tableName = tableName;
            this.intervalLengthSecs = intervalLengthSecs;
        }

        public String tableName() {
            return tableName;
        }

        public int intervalLengthSecs() {
            return intervalLengthSecs;
        }
        public int intervalLengthMS() {
            return intervalLengthSecs*1000;
        }
    }
    public Date start,  stop;
    public Grain grain = Grain.SECOND;
    public int intervalLengthSecs = 1;    // 10

    public SignalRange(String startStr, String stopStr) {
        this(startStr, stopStr, Grain.SECOND);
    }

    public SignalRange(String startStr, String stopStr, Grain grain) {
        this.grain=grain;
        start = TimeManip.parseISO(startStr);
        stop = TimeManip.parseISO(stopStr);
    }

    public SignalRange(int daysAgo) {
        start = TimeManip.startOfDay(new Date(), -daysAgo);
        stop = TimeManip.startOfDay(new Date(), -daysAgo + 1);

    }
}
