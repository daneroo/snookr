/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartapp;

import imetrical.model.DataFetcher;
import imetrical.model.DataShower;
import imetrical.model.ExpandedSignal;
import imetrical.model.SignalRange;
import imetrical.time.TimeManip;
import java.util.Date;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author daniel
 */
public class EnergyAnimator {


    TimeSeriesCollection timeSeriesCollection;
    Date graphStart;
    int  graphDisplayedHours;
    public TimeSeriesCollection setup() {
        timeSeriesCollection = new TimeSeriesCollection();
        graphStart = TimeManip.parseISO("2009-01-26 05:00:00");
        graphDisplayedHours=6;

        update();
        return timeSeriesCollection;
    }

    public void advance() {
        int advanceHours=2;
        graphStart = new Date(graphStart.getTime()+advanceHours*60*60*1000l);
        update();
    }
    // building the Returning TimeSeriesCollection: dataset
    private void update() {
        SignalRange.Grain grain = SignalRange.Grain.MINUTE;
        Date graphStop = new Date(graphStart.getTime()+graphDisplayedHours*60*60*1000l);

        SignalRange referenceSR = new SignalRange(graphStart,graphStop, grain);
        ExpandedSignal referenceES = DataFetcher.getDBExpandedSignal(referenceSR);

        System.out.println(String.format("Fetching for %s - %s : %d samples (x%ds)", TimeManip.isoFmt.format(graphStart),TimeManip.isoFmt.format(graphStop),referenceES.values.length,referenceES.intervalLengthSecs));
        timeSeriesCollection.removeAllSeries(); // or seriex or index
        String title = String.format("Reference Watts @ %s",TimeManip.dayFmt.format(graphStart));
        timeSeriesCollection.addSeries(DataShower.timeSeries(title, referenceES));
    }
}
