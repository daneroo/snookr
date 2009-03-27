/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartapp;

import green.model.Broker;
import green.util.Timer;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author daniel
 */
public class EnergyEventCorrelator {

    public static final String GRAIN_TENSEC = "watttensec";
    public static final String GRAIN_SECOND = "watt";
    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    //private static final String DBURL = "jdbc:mysql://127.0.0.1/ted";
    //private static final String DBURL = "jdbc:mysql://192.168.5.2/ted";
    private static final String DBURL = "jdbc:mysql://192.168.3.200/ted";
    private static final String DBUSER = "aviso";
    private static final String DBPASSWORD = null;
    private static final SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    class ExpandedSignal {

        int intervalLengthSecs = 1;    // 10
        long offsetMS;
        double values[];

        public ExpandedSignal(int size) {
            values = new double[size];
        }

        // same size: do NOT copy data.
        public ExpandedSignal(ExpandedSignal other) {
            intervalLengthSecs = other.intervalLengthSecs;
            offsetMS = other.offsetMS;
            values = new double[other.values.length];
        }

        public ExpandedSignal copy() {
            ExpandedSignal newES = new ExpandedSignal(this);
            for (int i = 0; i < values.length; i++) {
                newES.values[i] = values[i];
            }
            return newES;
        }

        public double min() {
            return values[minIndex()];
        }

        public int minIndex() {
            int minIndex = 0;
            double minV = values[0];
            for (int i = 1; i < values.length; i++) {
                if (values[i] < minV) {
                    minV = values[i];
                    minIndex = i;
                }
            }
            return minIndex;
        }

        public double max() {
            return values[maxIndex()];
        }

        public int maxIndex() {
            int maxIndex = 0;
            double maxV = values[0];
            for (int i = 1; i < values.length; i++) {
                if (values[i] > maxV) {
                    maxV = values[i];
                    maxIndex = i;
                }
            }
            return maxIndex;
        }

        public void zeroBase() {
            double minV = min();
            for (int i = 0; i < values.length; i++) {
                values[i] -= minV;
            }
        }

        public double avg() { // onZeroes ?
            double sum = 0;
            for (int i = 0; i < values.length; i++) {
                sum += values[i];
            }
            return sum / values.length;
        }
        // return average power*time

        public double kWh() {
            double avgPower = avg();
            int nSecs = values.length * intervalLengthSecs;
            return avgPower * nSecs / 3600000;
        }
    }

    class SignalRange {

        Date start, stop;
        String grain = GRAIN_SECOND; //GRAIN_TENSEC
        int intervalLengthSecs = 1;    // 10

        SignalRange(String startStr, String stopStr) {
            try {
                start = isoFmt.parse(startStr);
                stop = isoFmt.parse(stopStr);
            } catch (ParseException ex) {
                Logger.getLogger(EnergyEventCorrelator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        SignalRange(int daysAgo) {
            start = startOfDay(new Date(), -daysAgo);
            stop = startOfDay(new Date(), -daysAgo + 1);

        }
    }

    public TimeSeriesCollection correlateEvents() {
        //int daysAgo = 0;
        //SignalRange referenceSR = new SignalRange(daysAgo);

        // this one has holes
        //SignalRange referenceSR = new SignalRange("2009-03-26 05:00:00","2009-03-26 06:00:00");
        //SignalRange referenceSR = new SignalRange("2009-03-26 10:00:00", "2009-03-26 11:00:00");
        //SignalRange referenceSR = new SignalRange("2009-03-26 09:00:00", "2009-03-26 12:00:00");
        SignalRange referenceSR = new SignalRange("2009-03-26 00:00:00", "2009-03-27 00:00:00");

        //SignalRange eventSR = new SignalRange("2009-03-26 10:07:44", "2009-03-26 10:13:33");
        SignalRange eventSR = new SignalRange("2009-03-26 10:04:00", "2009-03-26 10:18:00");


        TimeSeriesCollection dataset = new TimeSeriesCollection();

        ExpandedSignal referenceES = getDBExpandedSignal(referenceSR);
        ExpandedSignal eventES = getDBExpandedSignal(eventSR);
        eventES.zeroBase();

        correlateEnergy(dataset, referenceES, eventES);

        return dataset;
    }

    // building the Returning TimeSeriesCollection: dataset
    private void correlateEnergy(TimeSeriesCollection dataset, ExpandedSignal referenceES, ExpandedSignal eventES) {

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
        double threshold = 100;
        ExpandedSignal accumulatedES = accumulateEvents(eventES, correlationES, threshold);
        ExpandedSignal remainingES = new ExpandedSignal(referenceES);
        for (int i = 0; i < rn; i++) {
            remainingES.values[i] = referenceES.values[i] - accumulatedES.values[i];
        }

        eventES.offsetMS = referenceES.offsetMS;

        /*
        System.out.println(String.format("Reference kWh:%.2f", referenceES.kWh()));
        System.out.println(String.format("Event     kWh:%.2f", eventES.kWh()));
        System.out.println(String.format("Extracted kWh:%.2f", accumulatedES.kWh()));
        System.out.println(String.format("Remaining kWh:%.2f", remainingES.kWh()));
        */
        System.out.println(String.format("%20s %8.2f %8.2f %8.2f %8.2f",isoFmt.format(new Date(referenceES.offsetMS)), referenceES.kWh(),eventES.kWh(),accumulatedES.kWh(),remainingES.kWh()));

        dataset.addSeries(timeSeriesFromExpandedSignal("Reference Watts", referenceES));
        dataset.addSeries(timeSeriesFromExpandedSignal("Event", eventES));
        dataset.addSeries(normalizeCorrelation(correlationES, -2000));
        dataset.addSeries(timeSeriesFromExpandedSignal("Accumulated Events", accumulatedES));
        dataset.addSeries(timeSeriesFromExpandedSignal("Remaining Noise", remainingES));
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
        int rn = correlationES.values.length;
        double normCorr = 0;
        for (int i = 0; i < rn; i++) {
            normCorr = Math.max(normCorr, Math.abs(correlationES.values[i]));
        //System.out.println(String.format("i:%d normCorr:%f",i,normCorr));
        }
        ExpandedSignal copyES = new ExpandedSignal(correlationES);
        for (int i = 0; i < rn; i++) {
            copyES.values[i] = correlationES.values[i] / normCorr * maxValue;
        }
        String correlationLegend = String.format("Correlation: %.1f", normCorr);
        return timeSeriesFromExpandedSignal(correlationLegend, copyES);
    }

    private ExpandedSignal getDBExpandedSignal(SignalRange sr) {
        XYDataset dbdataset = getDBDataset(sr);
        ExpandedSignal es = expandXYDataset(dbdataset);
        es.intervalLengthSecs = sr.intervalLengthSecs;
        return es;
    }

    private XYDataset getDBDataset(SignalRange sr) {
        XYDataset dbdataset = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "select stamp,watt from " + sr.grain + " where stamp>='" + sdf.format(sr.start) + "' and stamp<'" + sdf.format(sr.stop) + "'";
            //System.err.println("sql: " + sql);
            dbdataset = new JDBCXYDataset(DBURL, DBDRIVER, DBUSER, DBPASSWORD);
            ((JDBCXYDataset) dbdataset).executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        return dbdataset;
    }

    // insert 0 values into 0'th series
    private ExpandedSignal expandXYDataset(XYDataset dbdataset) {
        int series = 0;
        int n = dbdataset.getItemCount(series);
        long minX = dbdataset.getX(series, 0).longValue();
        long maxX = dbdataset.getX(series, n - 1).longValue();
        int diff = (int) ((maxX - minX) / 1000);
        //System.out.println(String.format("minX:%d maxX:%d diff:%d", minX, maxX, diff));
        ExpandedSignal es = new ExpandedSignal(diff + 1);
        es.offsetMS = minX;
        for (int i = 0; i < n; i++) {
            Number xi = dbdataset.getX(series, i);
            double yi = dbdataset.getYValue(series, i);
            int xoffset = (int) ((xi.longValue() - minX) / 1000);
            es.values[xoffset] = yi;
        }
        return es;
    }

    // omit zero values!
    private TimeSeries timeSeriesFromExpandedSignal(String name, ExpandedSignal es) {

        TimeSeries fromdb = new TimeSeries(name, Millisecond.class);
        for (int i = 0; i < es.values.length; i++) {
            long iAsLong = es.offsetMS + i * 1000l;
            Millisecond mi = new Millisecond(new Date(iAsLong));
            double yi = es.values[i];
            if (yi == 0) {
                //continue;
            }
            fromdb.add(mi, yi);
        }
        return fromdb;
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

    public void doADay(int daysAgo) {
        SignalRange eventSR = new SignalRange("2009-03-26 10:04:00", "2009-03-26 10:18:00");
        SignalRange referenceSR = new SignalRange(daysAgo);

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        ExpandedSignal referenceES = getDBExpandedSignal(referenceSR);
        ExpandedSignal eventES = getDBExpandedSignal(eventSR);
        eventES.zeroBase();

        correlateEnergy(dataset, referenceES, eventES);

    }

    public static void main(String[] args) {
        System.out.println(String.format("%20s %8s %8s %8s %8s","Date", "Total","Event","Accumuated","Remaining"));

        for (int i = 1; i < 365; i++) {
            try {
                new EnergyEventCorrelator().doADay(i);
            } catch (Exception e) {
            }
        }
    }
}
