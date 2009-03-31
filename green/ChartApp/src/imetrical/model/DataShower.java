/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imetrical.model;

import java.util.Date;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

/**
 *
 * @author daniel
 */
public class DataShower {

    // omit zero values!
    // MOVED GMT conversion to DataFetcher!
    public static TimeSeries timeSeries(String name, ExpandedSignal es) {
        TimeSeries fromdb = new TimeSeries(name, Millisecond.class);
        for (int i = 0; i < es.values.length; i++) {
            long iAsLong = es.offsetMS + i * 1000l;
            Date localDate = new Date(iAsLong);
            Millisecond mi = new Millisecond(localDate);
            double yi = es.values[i];
            // OMIT ZERO VALUES
            if (yi == 0) {
                continue;
            }
            fromdb.add(mi, yi);
        }
        return fromdb;
    }

}
