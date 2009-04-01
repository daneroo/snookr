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
public class EnergyEventCorrelator {

    public TimeSeriesCollection correlateEvents() {
        //int daysAgo = 0;
        //SignalRange referenceSR = new SignalRange(daysAgo);

        SignalRange.Grain grain = SignalRange.Grain.SECOND;
        // All in GMT now
        // this one has holes
        //SignalRange referenceSR = new SignalRange("2009-03-26 09:00:00","2009-03-26 10:00:00");
        //SignalRange referenceSR = new SignalRange("2009-03-26 14:00:00", "2009-03-26 15:00:00");
        SignalRange referenceSR = new SignalRange("2009-03-26 05:00:00", "2009-03-26 10:00:00", grain);
        //SignalRange referenceSR = new SignalRange("2009-03-26 00:00:00", "2009-03-27 00:00:00");

        // days of interest for DST: 2008-11-02 2009-03-08
        // DO NOT USE JFREECHART FOR DST - IS THE CONCLUSION
        /*
        SignalRange referenceSR = new SignalRange("2009-03-08 00:00:00", "2009-03-08 05:00:00");
        SignalRange referenceSR = new SignalRange("2008-11-02 00:00:00", "2008-11-02 05:00:00");
         */

        //SignalRange eventSR = new SignalRange("2009-03-26 06:07:44", "2009-03-26 06:13:33");
        //SignalRange eventSR = new SignalRange("2009-03-26 06:07:00", "2009-03-26 06:14:00");
        //SignalRange eventSR = new SignalRange("2009-03-26 06:04:00", "2009-03-26 06:18:00");
        SignalRange eventSR = new SignalRange("2009-03-26 06:05:00", "2009-03-26 06:17:00", grain);


        TimeSeriesCollection dataset = new TimeSeriesCollection();

        ExpandedSignal referenceES = DataFetcher.getDBExpandedSignal(referenceSR);
        ExpandedSignal eventES = DataFetcher.getDBExpandedSignal(eventSR);
        // zerobase
        eventES.add(-eventES.min());

        correlateEnergy(dataset, referenceES, eventES);

        return dataset;
    }

    private ExpandedSignal convolutionDelta(ExpandedSignal referenceES, ExpandedSignal eventES) {
        int rn = referenceES.values.length;
        int en = eventES.values.length;
        ExpandedSignal evDeltaES = delta(eventES);
        evDeltaES.values[0] = 0;
        ExpandedSignal refDeltaES = delta(referenceES);
        refDeltaES.values[0] = 0;
        ExpandedSignal correlationES = new ExpandedSignal(refDeltaES);
        for (int o = 0; o < rn - en; o++) {
            double sumsq = 0;
            /*            double avgRef = 0;
            double avgEvt = 0;
            for (int i = 0; i < en; i++) {
            avgRef += refDeltaES.values[o + i];
            }
            avgRef /= en;
            for (int i = 0; i < en; i++) {
            avgEvt += evDeltaES.values[i];
            }
            avgEvt /= en;
            avgEvt = 0;
            avgRef = 0;
             */
            for (int i = 0; i < en; i++) {
                if (Math.abs(evDeltaES.values[i]) > 20) {
                    double mult = Math.abs(evDeltaES.values[i]) * Math.abs(evDeltaES.values[i] - refDeltaES.values[o + i]);
                    sumsq += mult;
                }
            }
            double corr = Math.sqrt(sumsq) / en;
            //corr = Math.min(400, corr);
            correlationES.values[o] = corr;
        //System.out.println(String.format("%d: DC: %f - %f = %f  R:%f",(referenceES.offsetMS-eventES.offsetMS)/1000+o,avgEvt,avgRef,avgEvt-avgRef,corr));
        }
        return correlationES;
    }

    private ExpandedSignal convolution(ExpandedSignal referenceES, ExpandedSignal eventES) {
        int rn = referenceES.values.length;
        int en = eventES.values.length;
        ExpandedSignal correlationES = new ExpandedSignal(referenceES);
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
            correlationES.values[o] = corr;
        //System.out.println(String.format("%d: DC: %f - %f = %f  R:%f",(referenceES.offsetMS-eventES.offsetMS)/1000+o,avgEvt,avgRef,avgEvt-avgRef,corr));
        }
        return correlationES;
    }

    // building the Returning TimeSeriesCollection: dataset
    private void correlateEnergy(TimeSeriesCollection dataset, ExpandedSignal referenceES, ExpandedSignal eventES) {
        int rn = referenceES.values.length;
        int en = eventES.values.length;
        ExpandedSignal correlationES = convolution(referenceES, eventES);
        double threshold = 15;
        ExpandedSignal accumulatedES = accumulateEvents(eventES, correlationES, threshold);

        //ExpandedSignal corrDeltaES = convolutionDelta(referenceES, eventES);
        //double thresholdDelta = 1.6;
        //ExpandedSignal accumulatedES = accumulateEvents(eventES, corrDeltaES, thresholdDelta);

        ExpandedSignal remainingES = referenceES.copy();
        remainingES.minus(accumulatedES);
        /*
        System.out.println(String.format("Reference kWh:%.2f", referenceES.kWh()));
        System.out.println(String.format("Event     kWh:%.2f", eventES.kWh()));
        System.out.println(String.format("Extracted kWh:%.2f", accumulatedES.kWh()));
        System.out.println(String.format("Remaining kWh:%.2f", remainingES.kWh()));
         */
        System.out.println(String.format("%20s %8.2f %8.2f %8.2f %8.2f", TimeManip.isoFmt.format(new Date(referenceES.offsetMS)), referenceES.kWh(), eventES.kWh(), accumulatedES.kWh(), remainingES.kWh()));

        eventES.offsetMS = referenceES.offsetMS;
        ExpandedSignal evDeltaES = delta(eventES);
        evDeltaES.offsetMS = referenceES.offsetMS;
        evDeltaES.values[0] = 0;
        // shift down for visual
        for (int i = 0; i < en; i++) {
            eventES.values[i] -= 1500;
            evDeltaES.values[i] -= 2000;
        }
        // shift up for visual
        for (int i = 0; i < rn; i++) {
            accumulatedES.values[i] += 2000;
        }



        dataset.addSeries(DataShower.timeSeries("Reference Watts", referenceES));
        dataset.addSeries(DataShower.timeSeries("Event", eventES));
        //dataset.addSeries(DataShower.timeSeries("Event D", evDeltaES));
        dataset.addSeries(normalizeCorrelation(correlationES, -1000));
        //dataset.addSeries(normalizeCorrelation(corrDeltaES, -2000));
        dataset.addSeries(DataShower.timeSeries("Accumulated Events", accumulatedES));
        dataset.addSeries(DataShower.timeSeries("Remaining Noise", remainingES));
    }

    // find local correlation minima : no overlap
    private ExpandedSignal accumulateEvents(ExpandedSignal eventES, ExpandedSignal correlationES, double threshold) {
        double big = 10 * threshold;
        int rn = correlationES.values.length;
        int en = eventES.values.length;

        // MAKE a COPY
        correlationES = correlationES.copy();

        ExpandedSignal accumES = new ExpandedSignal(correlationES);
        while (true) {
            // find minimum
            int minCorrIndex = correlationES.minIndex();
            double minCorr = correlationES.values[minCorrIndex];
            if (minCorr <= threshold) {
                // accumuate event
                for (int i = 0; i < en; i++) {
                    accumES.values[i + minCorrIndex] += eventES.values[i];
                }
                // blank correlation copy
                int start = Math.max(0, minCorrIndex - en + 1);
                int stop = Math.min(rn, minCorrIndex + en);
                for (int b = start; b < stop; b++) {
                    correlationES.values[b] = big;
                }
            } else {
                break;
            }
        }
        return accumES;
    }

    private TimeSeries normalizeCorrelation(ExpandedSignal correlationES, double maxValue) {
        double corrMax = correlationES.max();
        ExpandedSignal copyES = correlationES.copy();
        copyES.normalize(maxValue);
        String correlationLegend = String.format("Correlation: %.1f", corrMax);
        return DataShower.timeSeries(correlationLegend, copyES);
    }

    ExpandedSignal delta(ExpandedSignal es) {
        ExpandedSignal delta = new ExpandedSignal(es);
        delta.values[0] = es.values[0];
        for (int i = 1; i < es.values.length; i++) {
            delta.values[i] = es.values[i] - es.values[i - 1];
        }
        return delta;
    }

    public void doADay(int daysAgo) {
        //SignalRange eventSR = new SignalRange("2009-03-26 14:04:00", "2009-03-26 14:18:00");
        SignalRange eventSR = new SignalRange("2009-03-26 06:05:00", "2009-03-26 06:17:00");
        SignalRange referenceSR = new SignalRange(daysAgo);

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        ExpandedSignal referenceES = DataFetcher.getDBExpandedSignal(referenceSR);
        ExpandedSignal eventES = DataFetcher.getDBExpandedSignal(eventSR);
        // zerobase
        eventES.add(-eventES.min());

        correlateEnergy(dataset, referenceES, eventES);
    }

    public static void main(String[] args) {
        System.out.println(String.format("%20s %8s %8s %8s %8s", "Date", "Total", "Event", "Accumuated", "Remaining"));

        for (int i = 3; i < 31; i++) {
            new EnergyEventCorrelator().doADay(i);
        }
    }
}
