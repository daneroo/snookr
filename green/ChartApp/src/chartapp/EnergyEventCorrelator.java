/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartapp;

import green.util.TimeConvert;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
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

        // All in GMT now
        // this one has holes
        //SignalRange referenceSR = new SignalRange("2009-03-26 09:00:00","2009-03-26 10:00:00");
        //SignalRange referenceSR = new SignalRange("2009-03-26 14:00:00", "2009-03-26 15:00:00");
        SignalRange referenceSR = new SignalRange("2009-03-26 13:00:00", "2009-03-26 18:00:00");
        //SignalRange referenceSR = new SignalRange("2009-03-26 00:00:00", "2009-03-27 00:00:00");

        //SignalRange eventSR = new SignalRange("2009-03-26 14:07:44", "2009-03-26 14:13:33");
        //SignalRange eventSR = new SignalRange("2009-03-26 14:07:00", "2009-03-26 14:14:00");
        //SignalRange eventSR = new SignalRange("2009-03-26 14:04:00", "2009-03-26 14:18:00");
        SignalRange eventSR = new SignalRange("2009-03-26 14:05:00", "2009-03-26 14:17:00");


        TimeSeriesCollection dataset = new TimeSeriesCollection();

        ExpandedSignal referenceES = getDBExpandedSignal(referenceSR);
        ExpandedSignal eventES = getDBExpandedSignal(eventSR);
        eventES.zeroBase();

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
                    double mult = Math.abs(evDeltaES.values[i]) * Math.abs(evDeltaES.values[i]-refDeltaES.values[o + i]);
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
        ExpandedSignal remainingES = new ExpandedSignal(referenceES);
        for (int i = 0; i < rn; i++) {
            remainingES.values[i] = referenceES.values[i] - accumulatedES.values[i];
        }

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


        /*
        System.out.println(String.format("Reference kWh:%.2f", referenceES.kWh()));
        System.out.println(String.format("Event     kWh:%.2f", eventES.kWh()));
        System.out.println(String.format("Extracted kWh:%.2f", accumulatedES.kWh()));
        System.out.println(String.format("Remaining kWh:%.2f", remainingES.kWh()));
         */
        System.out.println(String.format("%20s %8.2f %8.2f %8.2f %8.2f", isoFmt.format(new Date(referenceES.offsetMS)), referenceES.kWh(), eventES.kWh(), accumulatedES.kWh(), remainingES.kWh()));

        dataset.addSeries(timeSeriesFromExpandedSignal("Reference Watts", referenceES));
        dataset.addSeries(timeSeriesFromExpandedSignal("Event", eventES));
        //dataset.addSeries(timeSeriesFromExpandedSignal("Event D", evDeltaES));
        dataset.addSeries(normalizeCorrelation(correlationES, -1000));
        //dataset.addSeries(normalizeCorrelation(corrDeltaES, -2000));
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
        ExpandedSignal es = expandGMTXYDataset(dbdataset);
        es.intervalLengthSecs = sr.intervalLengthSecs;
        return es;
    }

    private XYDataset getDBDataset(SignalRange sr) {
        XYDataset dbdataset = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date gmtstart = TimeConvert.localToGMT(sr.start);
            Date gmtstop = TimeConvert.localToGMT(sr.stop);
            String sql = "select stamp,watt from " + sr.grain + " where stamp>='" + sdf.format(gmtstart) + "' and stamp<'" + sdf.format(gmtstop) + "'";
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
    private ExpandedSignal expandGMTXYDataset(XYDataset dbdataset) {
        int series = 0;
        int n = dbdataset.getItemCount(series);
        long minX = dbdataset.getX(series, 0).longValue();
        long maxX = dbdataset.getX(series, n - 1).longValue();
        int diff = (int) ((maxX - minX) / 1000);
        //System.out.println(String.format("minX:%d maxX:%d diff:%d", minX, maxX, diff));
        ExpandedSignal es = new ExpandedSignal(diff + 1);
        es.offsetMS = minX;
        es.offsetMS = TimeConvert.gmtToLocal(new Date(minX)).getTime();
        for (int i = 0; i < n; i++) {
            Number xi = dbdataset.getX(series, i);
            double yi = dbdataset.getYValue(series, i);
            int xoffset = (int) ((xi.longValue() - minX) / 1000);
            es.values[xoffset] = yi;
        }
        return es;
    }

    // omit zero values!
    // convert time from gmt to local!
    private TimeSeries timeSeriesFromExpandedSignal(String name, ExpandedSignal es) {

        TimeSeries fromdb = new TimeSeries(name, Millisecond.class);
        for (int i = 0; i < es.values.length; i++) {
            long iAsLong = es.offsetMS + i * 1000l;
            //Date gmtDate = new Date(iAsLong);
            // moved conversion into expandXYDataset()
            //Date localDate = TimeConvert.gmtToLocal(new Date(iAsLong));
            Date localDate = new Date(iAsLong);
            Millisecond mi = new Millisecond(localDate);
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

    double log2(double d) {
        if (d == 0) {
            return 0;
        }
        return Math.log(d) / Math.log(2.0);
    }

    ExpandedSignal delta(ExpandedSignal es) {
        ExpandedSignal delta = new ExpandedSignal(es);
        for (int i = 1; i < es.values.length; i++) {
            delta.values[i] = es.values[i] - es.values[i - 1];
        }
        return delta;
    }

    int gzip9(String encoded) {
        /*        Object o = new GZIPOutputStream(os) {

        {
        def.setLevel(Deflater.BEST_COMPRESSION);
        }
        ;*/
        byte[] input = encoded.getBytes();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Create the GZIP output stream
            GZIPOutputStream out = new GZIPOutputStream(bos) {

                {
                    //System.out.println("Compression Level Default: "+def.DEFAULT_COMPRESSION);
                    //System.out.println("Compression Level speed:   "+def.BEST_SPEED);
                    //System.out.println("Compression Level max:     "+def.BEST_COMPRESSION);
                    def.setLevel(Deflater.BEST_COMPRESSION);
                }
            };
            // Transfer bytes from the input file to the GZIP output stream
            out.write(input, 0, input.length);
            out.finish();
            out.close();
            bos.close();
        } catch (IOException e) {
        }
        byte[] compressedData = bos.toByteArray();
        boolean actualFileTest = false;
        if (actualFileTest) {
            try {
                String fname = "data.best.gz";
                File f = new File(fname);
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(compressedData);
                fos.close();
                System.out.println(String.format("%s : %d =?= %d", f.getCanonicalPath(), f.length(), compressedData.length));
            } catch (Exception e) {
            }
        }
        return compressedData.length;
    }

    int gzip(String encoded) {
        byte[] input = encoded.getBytes();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Create the GZIP output stream
            GZIPOutputStream out = new GZIPOutputStream(bos);
            // Transfer bytes from the input file to the GZIP output stream
            out.write(input, 0, input.length);
            out.finish();
            out.close();
            bos.close();
        } catch (IOException e) {
        }
        byte[] compressedData = bos.toByteArray();
        return compressedData.length;
    }

    int compress(
            String encoded) {
        byte[] input = encoded.getBytes();

        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        compressor.setInput(input);
        compressor.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
        }
        byte[] compressedData = bos.toByteArray();
        return compressedData.length;
    }

    void codingCost(ExpandedSignal es, String name) {
        int max = (int) (Math.ceil(es.max()));
        int min = (int) (Math.floor(es.min()));
        int range = max - min + 1;
        //System.out.println(String.format("  %7s min:%6d max:%6d range:%6d", name, min, max, range));
        int[] histo = new int[range];
        for (int i = 0; i < es.values.length; i++) {
            int v = (int) es.values[i];
            histo[v - min] += 1;
        }
        int samples = 0;
        for (int i = 0; i < histo.length; i++) {
            samples += histo[i];
        }
        double fixedCodingCost = log2(max - min + 1) * samples;
        double H = 0;
        for (int i = 0; i < histo.length; i++) {
            double prob = histo[i] * 1.0 / samples;
            H += -log2(prob) * histo[i];
        }
        //System.out.println(String.format("  samples:%6d fixed:%9.2f bits H:%9.2f bits",samples,fixedCodingCost,H));
        //System.out.println(String.format("  %7s samples:%6d fixed:%9.2f bps entropy:%9.2f bps", name, samples, fixedCodingCost / samples, H / samples));
        // Measure zipped text representation:
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < es.values.length; i++) {
            sb.append(String.format("%.0f,", es.values[i]));
        }
        String encoded = sb.toString();
        int uncompressedLen = encoded.length();
        int compressedLen = compress(encoded);
        int gzipedLen = gzip(encoded);
        int gziped9Len = gzip9(encoded);
        double ratio = 1.0 * uncompressedLen / compressedLen;
        double gz9ratio = 1.0 * uncompressedLen / gziped9Len;
        double Zbps = compressedLen * 8.0 / samples;
        double gz9bps = gziped9Len * 8.0 / samples;
        //System.out.println(String.format("  %7s Str raw:%6d compressed:%6d  gzipped:%6d gzip9:%6d ratio:%.2f %9.2f bps", name, uncompressedLen, compressedLen, gzipedLen, gziped9Len, ratio, Zbps));

        System.out.println(String.format("  %7s samples:%6d entropy:%5.2f bps gz9:%5.2f bps raw:%7d gz9:%7d ratio:%5.2f", name, samples, H / samples, gz9bps, uncompressedLen, gziped9Len, gz9ratio));

    }

    void entropy(ExpandedSignal es) {
        System.out.println(String.format("Signal:%20s %6d", isoFmt.format(new Date(es.offsetMS)), es.values.length));
        codingCost(es, "Signal");
        // Verified that H(s/10)==H(s)

        ExpandedSignal delta = delta(es);
        codingCost(delta, "Delta");
    }

    public void doADay(int daysAgo) {
        //SignalRange eventSR = new SignalRange("2009-03-26 14:04:00", "2009-03-26 14:18:00");
        SignalRange eventSR = new SignalRange("2009-03-26 14:05:00", "2009-03-26 14:17:00");
        SignalRange referenceSR = new SignalRange(daysAgo);

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        ExpandedSignal referenceES = getDBExpandedSignal(referenceSR);
        ExpandedSignal eventES = getDBExpandedSignal(eventSR);
        eventES.zeroBase();

        correlateEnergy(dataset, referenceES, eventES);
    //entropy(referenceES);
    }

    public static void main(String[] args) {
        System.out.println(String.format("%20s %8s %8s %8s %8s", "Date", "Total", "Event", "Accumuated", "Remaining"));

        for (int i = 1; i < 31; i++) {
            try {
                new EnergyEventCorrelator().doADay(i);
            } catch (Exception e) {
                Logger.getLogger(EnergyEventCorrelator.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}
