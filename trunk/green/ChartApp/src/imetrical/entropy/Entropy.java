/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.entropy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imetrical.model.DataFetcher;
import imetrical.model.ExpandedSignal;
import imetrical.model.SignalRange;
import imetrical.time.TimeManip;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author daniel
 */
public class Entropy {

    ExpandedSignal delta(ExpandedSignal es) {
        ExpandedSignal delta = new ExpandedSignal(es);
        delta.values[0] = es.values[0];
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
        es = toJSONandBack(es, name);
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
        //encoded = toJSON(es);
        int uncompressedLen = encoded.length();
        int compressedLen = compress(encoded);
        int gzipedLen = gzip(encoded);
        int gziped9Len = gzip9(encoded);
        double ratio = 1.0 * uncompressedLen / compressedLen;
        double gz9ratio = 1.0 * uncompressedLen / gziped9Len;
        double Zbps = compressedLen * 8.0 / samples;
        double gz9bps = gziped9Len * 8.0 / samples;
        //System.out.println(String.format("  %7s Str raw:%6d compressed:%6d  gzipped:%6d gzip9:%6d ratio:%.2f %9.2f bps", name, uncompressedLen, compressedLen, gzipedLen, gziped9Len, ratio, Zbps));

        System.out.println(String.format("  %10s samples:%6d entropy:%5.2f bps gz9:%5.2f bps raw:%7d gz9:%7d ratio:%5.2f", name, samples, H / samples, gz9bps, uncompressedLen, gziped9Len, gz9ratio));

    }

    void entropy(ExpandedSignal es,String scopePrefix) {
        System.out.println(String.format("Signal:%20s %6d", TimeManip.isoFmt.format(new Date(es.offsetMS)), es.values.length));
        codingCost(es, scopePrefix+"-Signal");
        // Verified that H(s/10)==H(s)

        ExpandedSignal delta = delta(es);
        codingCost(delta, scopePrefix+"-Delta");
    }

    public void doADay(int daysAgo) {
        SignalRange referenceSR = new SignalRange(daysAgo);
        ExpandedSignal referenceES = DataFetcher.fetchForRange(referenceSR);
        entropy(referenceES,"D");
    }

    public void doDays(int numDays) {
        for (int i = 1; i <= numDays; i++) {
            doADay(i);
        }
    }

    public void doAnHour(Date start) {
        long hourMillis = 60 * 60 * 1000l;
        Date stop = new Date(start.getTime() + hourMillis);
        SignalRange referenceSR = new SignalRange(start, stop);
        ExpandedSignal referenceES = DataFetcher.fetchForRange(referenceSR);
        entropy(referenceES,"H");
    }
    // walk back

    public void doHours(int numHours) {
        Date start = TimeManip.startOfDay(new Date(), 0);
        for (int i = 0; i < numHours; i++) {
            long hourMillis = 60 * 60 * 1000l;
            start = new Date(start.getTime() - hourMillis);
            doAnHour(start);
        }
    }

    public void doAllTimeHours() {
        SignalRange referenceSR = new SignalRange("2008-07-29 20:00:00", "2009-06-09 08:00:00", SignalRange.Grain.HOUR);
        ExpandedSignal referenceES = DataFetcher.fetchForRange(referenceSR);
        entropy(referenceES,"A");
    }
    /*
     *  Let us consider the representation of a Day (24H GMT or 23-25Local)
     *  zip(signal.json)
     *  zip(signal.gson.gz)
     *  zip(delta.json)
     *  zip(delta.json.gzip)
     */
    public static void main(String[] args) {
        new Entropy().doAllTimeHours();
        System.exit(0);
        int nDays=2;
        new Entropy().doDays(nDays);
        new Entropy().doHours(nDays*24);
    }

    ExpandedSignal toJSONandBack(ExpandedSignal es, String name) {
        final SimpleDateFormat fileDateFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = fileDateFmt.format(new Date(es.offsetMS)) + "-" + name + ".json";
        ExpandedSignal readES = null;
        boolean pretty = false;
        GsonBuilder gsb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
        if (pretty) {
            gsb.setPrettyPrinting();
        }
        Gson gson = gsb.create();
        String jsonStr = gson.toJson(new ExpandedSignalInt(es));
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.append(jsonStr);
            fw.close();
            readES = gson.fromJson(new FileReader(fileName), ExpandedSignal.class);
        } catch (IOException ex) {
            Logger.getLogger(Entropy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return readES;
    }

    double log2(double d) {
        if (d == 0) {
            return 0;
        }
        return Math.log(d) / Math.log(2.0);
    }
}

class ExpandedSignalInt {

    int intervalLengthSecs = 1;    // 10
    long offsetMS;
    int values[];

    ExpandedSignalInt(ExpandedSignal es) {
        intervalLengthSecs = es.intervalLengthSecs;
        offsetMS = es.offsetMS;
        values = new int[es.values.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) es.values[i];
        }
    }
}
