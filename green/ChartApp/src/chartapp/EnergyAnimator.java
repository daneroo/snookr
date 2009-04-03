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
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author daniel
 */
public class EnergyAnimator {

    TimeSeriesCollection timeSeriesCollection;
    Date graphStart;
    int graphHighresHours;
    int graphPadLowresHours;
    ExpandedSignal referenceES;
    ExpandedSignal eventES;
    ExpandedSignal remainingES;
    ExpandedSignal accumulatedES;
    ExpandedSignal correlationES;

    public TimeSeriesCollection setup() {
        timeSeriesCollection = new TimeSeriesCollection();
        graphStart = TimeManip.parseISO("2009-01-26 05:00:00");
        //graphStart = TimeManip.parseISO("2009-03-26 05:00:00");
        //graphStart = TimeManip.parseISO("2009-04-01 15:00:00");
        graphStart = TimeManip.parseISO("2009-03-29 06:00:00");
        graphHighresHours = 3;
        graphPadLowresHours = 1;

        updateAfterAdvance();
        return timeSeriesCollection;
    }

    public void advance(int steps) {
        int advanceHours = 2;
        graphStart = new Date(graphStart.getTime() + steps * advanceHours * 60 * 60 * 1000l);
        updateAfterAdvance();
    }

    public void step(int steps) {
        if (steps == 0) {
            advance(0);
        } else {
            System.out.println("Perform a step, update");
            performStep();
            updateAfterStep();
        }
    }

    private void performStep() {
        double threshold = 10000000;
        int minCorrIndex = correlationES.minIndex();
        double minCorr = correlationES.values[minCorrIndex];
        System.out.println(String.format("Found minCorr=%f at index: %d",minCorr,minCorrIndex));
        if (minCorr <= threshold) {
            // accumuate event
            for (int i = 0; i < eventES.values.length; i++) {
                accumulatedES.values[i + minCorrIndex] += eventES.values[i];
                remainingES.values[i + minCorrIndex] -= eventES.values[i];
            }
        }

        //accumulatedES.add(10);
        //remainingES.add(-10);
        correlationES = convolution(remainingES, eventES);
    }
    private TimeSeries remainingTS,  accumulatedTS,  correlationTS;

    private void updateAfterStep() {
        // replace remaining, accumulated, correlation
        if (remainingTS != null) {
            timeSeriesCollection.removeSeries(remainingTS);
        }
        timeSeriesCollection.addSeries(remainingTS = DataShower.timeSeries("Remaining", remainingES));
        if (accumulatedTS != null) {
            timeSeriesCollection.removeSeries(accumulatedTS);
        }
        timeSeriesCollection.addSeries(accumulatedTS = DataShower.timeSeries("Accumulated", accumulatedES));
        if (correlationTS != null) {
            timeSeriesCollection.removeSeries(correlationTS);
        }
        timeSeriesCollection.addSeries(correlationTS = adjustCorrelationForDisplay(correlationES, -2000));
    }

    private void updateAfterAdvance() {

        SignalRange.Grain lowresGrain = SignalRange.Grain.MINUTE;
        SignalRange.Grain highresGrain = SignalRange.Grain.SECOND;
        Date graphStop = new Date(graphStart.getTime() + graphHighresHours * 60 * 60 * 1000l);
        // Context padding at Lowres
        Date padStart = new Date(graphStart.getTime() - graphPadLowresHours * 60 * 60 * 1000l);
        Date padStop = new Date(graphStop.getTime() + graphPadLowresHours * 60 * 60 * 1000l);

        SignalRange referenceSR = new SignalRange(graphStart, graphStop, highresGrain);
        referenceES = DataFetcher.fetchForRange(referenceSR);
        System.out.println(String.format("Fetching for %s - %s : %d samples (x%ds)", TimeManip.isoFmt.format(graphStart), TimeManip.isoFmt.format(graphStop), referenceES.values.length, referenceES.intervalLengthSecs));

        SignalRange eventSR = new SignalRange("2009-03-26 06:05:00", "2009-03-26 06:17:00", highresGrain);
        eventES = DataFetcher.fetchForRange(eventSR);
        // zero base event
        eventES.add(-eventES.min());

        timeSeriesCollection.removeAllSeries();
        remainingTS = accumulatedTS = correlationTS = null; // for removal..
        String title = String.format("Reference Watts @ %s", TimeManip.dayFmt.format(graphStart));
        timeSeriesCollection.addSeries(DataShower.timeSeries(title, referenceES));
        timeSeriesCollection.addSeries(adjustEventForDisplay(eventES, padStart));

        // Context padding at Lowres
        SignalRange padLeftSR = new SignalRange(padStart, graphStart, lowresGrain);
        ExpandedSignal padLeftES = DataFetcher.fetchForRange(padLeftSR);
        SignalRange padRightSR = new SignalRange(graphStop, padStop, lowresGrain);
        ExpandedSignal padRightES = DataFetcher.fetchForRange(padRightSR);
        timeSeriesCollection.addSeries(DataShower.timeSeries("Context", padLeftES));
        timeSeriesCollection.addSeries(DataShower.timeSeries("Context", padRightES));

        // intialize data for updateAfterStep, performStep.
        remainingES = referenceES.copy();
        accumulatedES = new ExpandedSignal(referenceES);
        correlationES = convolution(remainingES, eventES);
        updateAfterStep();
    }

    private TimeSeries adjustEventForDisplay(ExpandedSignal evES, Date showStart) {
        ExpandedSignal copyES = evES.copy();
        copyES.offsetMS = showStart.getTime();
        copyES.add(-1500);
        return DataShower.timeSeries("Event", copyES);
    }
    // Just to add caption..

    private TimeSeries adjustCorrelationForDisplay(ExpandedSignal correlationES, double maxValue) {
        //double corrMax = correlationES.max();
        //String correlationLegend = String.format("Correlation: %.1f", corrMax);
        ExpandedSignal copyES = correlationES.copy();
        double clip = 20;
        String correlationLegend = String.format("Correlation x %.1f", -maxValue / clip);
        for (int i = 0; i < copyES.values.length; i++) {
            if (copyES.values[i] > clip) {
                copyES.values[i] = clip;
            }
        }
        copyES.normalize(maxValue);
        return DataShower.timeSeries(correlationLegend, copyES);
    }

    private ExpandedSignal convolution(ExpandedSignal referenceES, ExpandedSignal eventES) {
        int rn = referenceES.values.length;
        int en = eventES.values.length;
        ExpandedSignal newCorrelationES = new ExpandedSignal(referenceES);
        for (int o = 0; o < rn - en; o++) {
            double sumsq = 0;
            double avgRef = 0;
            double avgEvt = 0;
            for (int i = 0; i < en; i++) {
                avgRef += referenceES.values[o + i];
            }
            avgRef /= en;
            for (int i = 0; i < en; i++) {
                avgEvt += eventES.values[i];
            }
            avgEvt /= en;
            for (int i = 0; i < en; i++) {
                double diff = (eventES.values[i] - avgEvt) - (referenceES.values[o + i] - avgRef);
                sumsq += diff * diff;
            }
            double corr = Math.sqrt(sumsq) / en;
            //corr = Math.min(400, corr);
            newCorrelationES.values[o] = corr;
        //System.out.println(String.format("%d: DC: %f - %f = %f  R:%f",(referenceES.offsetMS-eventES.offsetMS)/1000+o,avgEvt,avgRef,avgEvt-avgRef,corr));
        }
        for (int o = rn-en; o < rn; o++) {
            newCorrelationES.values[o] = 1000;
        }
        return newCorrelationES;
    }
}
