/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model;

import imetrical.model.broker.Broker;
import imetrical.model.broker.StampGMTAndDoublesHandler;
import imetrical.model.broker.StampTZAndDoublesHandler;
import imetrical.time.TimeConvert;
import imetrical.time.TimeManip;
import imetrical.util.Timer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author daniel
 */
public class DataFetcher {

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    //private static final String DBURL = "jdbc:mysql://127.0.0.1/ted";
    private static final String DBURL = "jdbc:mysql://192.168.5.2/ted";
    //private static final String DBURL = "jdbc:mysql://192.168.3.200/ted";
    private static final String DBUSER = "aviso";
    private static final String DBPASSWORD = null;

    public static ExpandedSignal fetchForRange(SignalRange sr) {
        return new DataFetcher().fetchForRangeInternal(sr);
    }

    // *** MAKE    expandGMTXYDataset   PRIVATE AGAIN ***
    private ExpandedSignal fetchForRangeInternal(SignalRange sr) {
        Timer tt = new Timer();
        ExpandedSignal bes = brokerFetchForRange(sr);
        // NOT SURE THIS IS WORKING!!!!
        /*
        float bdiff = tt.diff();
        tt.restart();
        ExpandedSignal ces = expandGMTXYDataset(sr);
        float cdiff = tt.diff();
        tt.restart();
        System.out.println(String.format(" b: %fs  c:%fs", bdiff, cdiff));
         */
        ExpandedSignal es = bes;
        //es.fillin();
        /*
        for (int i = 0; i < es.values.length; i += 2) {
        // for marking input on graph!!!
        //es.values[i] += 100;
        }
         */
        return es;
    }

    private Vector<Object[]> getWithTZ(String tableName, Date gmtstart, Date gmtstop) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select concat(stamp,'+0000'),watt from " + tableName + " where stamp>='" + sdf.format(gmtstart) + "' and stamp<'" + sdf.format(gmtstop) + "'";
        //System.err.println("broker sql: " + sql);
        Broker b = Broker.instance();
        Vector<Object[]> v = b.getObjects(sql, 0, new StampTZAndDoublesHandler());
        return v;
    }

    private Vector<Object[]> getAsGMT(String tableName, Date gmtstart, Date gmtstop) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // could also use select cast(stamp as char), or concat(stamp) instead of XXX.o triming in StampGMTAndDoublesHandler.getString
        String sql = "select cast(stamp as char),watt from " + tableName + " where stamp>='" + sdf.format(gmtstart) + "' and stamp<'" + sdf.format(gmtstop) + "'";
        System.err.println("broker sql: " + sql);
        Broker b = Broker.instance();
        Vector<Object[]> v = b.getObjects(sql, 0, new StampGMTAndDoublesHandler());
        System.err.println("broker return size: " + v.size());
        return v;
    }

    private ExpandedSignal brokerFetchForRange(SignalRange sr) {
        Date gmtstart = TimeConvert.localToGMT(sr.start);
        Date gmtstop = TimeConvert.localToGMT(sr.stop);
        String tableName = sr.grain.tableName();

        Vector<Object[]> v = getAsGMT(tableName, gmtstart, gmtstop);
        //Vector<Object[]> v = getWithTZ(tableName, gmtstart, gmtstop);

        int n = v.size();
        int samples = (int) ((sr.stop.getTime() - sr.start.getTime()) / sr.grain.intervalLengthMS());
        System.out.format("start %d, stop:%d grainMS:%d\n", sr.start.getTime(),sr.stop.getTime(),sr.grain.intervalLengthMS() );
        System.out.format("stop-start:%d grainMS:%d\n", sr.stop.getTime()-sr.start.getTime(),sr.grain.intervalLengthMS() );
        System.out.format("(stop-start) / grainMS:%d\n", (sr.stop.getTime()-sr.start.getTime())/sr.grain.intervalLengthMS() );
        System.out.println(String.format("b:start: %s  stop: %s samples: %d itemCount: %d", TimeManip.isoFmt.format(sr.start), TimeManip.isoFmt.format(sr.stop), samples, n));

        ExpandedSignal es = new ExpandedSignal(samples);
        es.intervalLengthSecs = sr.intervalLengthSecs;
        es.offsetMS = sr.start.getTime();
        long localstart = sr.start.getTime();
        int intervalLengthMS = sr.grain.intervalLengthMS();
        for (Object[] oa : v) {
            Date stamp = (Date) oa[0];
            double yi = (Double) oa[1];
            long xi = stamp.getTime();
            int xoffset = (int) ((xi - localstart) / intervalLengthMS);
            //System.out.println(String.format("xoffset: %d xi: %d y: %f --> %s",xoffset, xi, yi,TimeManip.isoFmt.format(stamp)));
            es.values[xoffset] = yi;
        }
        return es;

    }

    public ExpandedSignal expandGMTXYDataset(SignalRange sr) {
        Date gmtstart = TimeConvert.localToGMT(sr.start);
        Date gmtstop = TimeConvert.localToGMT(sr.stop);
        String tableName = sr.grain.tableName();

        XYDataset dbdataset = getDBDataset(gmtstart, gmtstop, tableName);

        int series = 0;
        int n = dbdataset.getItemCount(series);
        int samples = (int) (sr.stop.getTime() - sr.start.getTime()) / sr.grain.intervalLengthMS();

        //System.out.println(String.format("l:start: %s  stop: %s samples: %d itemCount: %d", TimeManip.isoFmt.format(sr.start), TimeManip.isoFmt.format(sr.stop), samples, n));

        ExpandedSignal es = new ExpandedSignal(samples);
        es.intervalLengthSecs = sr.intervalLengthSecs;
        es.offsetMS = sr.start.getTime();
        long localstart = sr.start.getTime();
        int intervalLengthMS = sr.grain.intervalLengthMS();
        if (n > 1) {
            // bad range, e.g. null result set return one sample (epoch,0) which we skip
            for (int i = 0; i < n; i++) {
                Number xi = dbdataset.getX(series, i);
                double yi = dbdataset.getYValue(series, i);

                // Critical - must use gmtToLocal on EVERY sample.
                long xilocal = TimeConvert.gmtToLocal(new Date(xi.longValue())).getTime();

                int xoffset = (int) ((xilocal - localstart) / intervalLengthMS);
                //System.out.println(String.format("xoffset: %d xi: %d y: %f --> %s", xoffset, xi.longValue(), yi, TimeManip.isoFmt.format(new Date(xilocal))));
                es.values[xoffset] = yi;
            }
        }
        return es;
    }

    private XYDataset getDBDataset(Date gmtstart, Date gmtstop, String tableName) {
        XYDataset dbdataset = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "select stamp,watt from " + tableName + " where stamp>='" + sdf.format(gmtstart) + "' and stamp<'" + sdf.format(gmtstop) + "'";
            //sql = "select stamp,mod(stamp,1500) from " + tableName + " where stamp>='" + sdf.format(gmtstart) + "' and stamp<'" + sdf.format(gmtstop) + "'";

            //System.err.println("dataset sql: " + sql);
            dbdataset = new JDBCXYDataset(DBURL, DBDRIVER, DBUSER, DBPASSWORD);
            ((JDBCXYDataset) dbdataset).executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        return dbdataset;
    }

    public static void main(String[] args) {
        // little test 
        // Local Day of 2008-11-02 00:00:00 grain: HOUR size: 25
        // sql: select stamp,watt from watt_hour where stamp>='2008-11-02 04:00:00' and stamp<'2008-11-03 05:00:00'
        // Local Day of 2009-03-08 00:00:00 grain: HOUR size: 23
        // sql: select stamp,watt from watt_hour where stamp>='2009-03-08 05:00:00' and stamp<'2009-03-09 04:00:00'
        for (SignalRange.Grain grain : new SignalRange.Grain[]{SignalRange.Grain.SECOND, SignalRange.Grain.TENSEC, SignalRange.Grain.MINUTE, SignalRange.Grain.HOUR}) {
            //for (SignalRange.Grain grain : new SignalRange.Grain[]{ SignalRange.Grain.HOUR}) {
            System.out.println("-- Testing Grain: " + grain);
            for (DayOfInterest doi : new DayOfInterest[]{DayOfInterest.NOV_2_2008, DayOfInterest.MAR_8_2009}) {
                //for (DayOfInterest doi : new DayOfInterest[]{DayOfInterest.NOV_2_2008}) {
                for (int i = -1; i <= 1; i++) {
                    String start = String.format(doi.fmt, doi.day + i);
                    String stop = String.format(doi.fmt, doi.day + i + 1);
                    SignalRange sr = new SignalRange(start, stop, grain);
                    ExpandedSignal es = DataFetcher.fetchForRange(sr);
                    System.out.println(String.format("  Day of %s grain: %s size: %d", start, sr.grain, es.values.length));
                }
            }
        }

        Date start = TimeManip.startOfDay(new Date(), -7);
        for (int i = 0; i < 0; i++) {
            Date startHour = new Date(start.getTime() - i * 60 * 60 * 1000l);
            Date stopHour = new Date(startHour.getTime() + 60 * 60 * 1000l);
            ExpandedSignal es = DataFetcher.fetchForRange(new SignalRange(startHour, stopHour));
        }
    }
}

enum DayOfInterest {

    NOV_2_2008("2008-11-%02d 00:00:00", 2),
    MAR_8_2009("2009-03-%02d 00:00:00", 8);
    final String fmt;
    final int day; // in seconds

    DayOfInterest(String fmt, int day) {
        this.fmt = fmt;
        this.day = day;
    }
}
