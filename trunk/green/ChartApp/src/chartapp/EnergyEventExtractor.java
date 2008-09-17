/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
public class EnergyEventExtractor {

    public static final String GRAIN_TENSEC = "watttensec";
    public static final String GRAIN_SECOND = "watt";
    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://127.0.0.1/ted";
    private static final String DBUSER = "aviso";
    private static final String DBPASSWORD = null;

    public Date startOfDay(Date ref, int offsetInDays) {
        SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd");
        Date offset = new Date(ref.getTime() + offsetInDays * 24 * 60 * 60 * 1000l);
        Date startOfDay = offset;
        try {
            startOfDay = dayFmt.parse(dayFmt.format(offset));
        } catch (ParseException ex) {
            Logger.getLogger(EnergyEventExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Ref Date:   " + sdf.format(ref) + " - " + offsetInDays + " days");
        System.out.println("Offset:     " + sdf.format(offset));
        System.out.println("StartOfDay: " + sdf.format(startOfDay));
         */
        return startOfDay;
    }

    public TimeSeriesCollection extractEnergyEvents() {
        Date start = startOfDay(new Date(), -1);
        Date stop = startOfDay(new Date(), 0);
        return extractEnergyEvents(GRAIN_TENSEC, 10, start, stop);
    //return extractEnergyEvents(GRAIN_SECOND, 1, start, stop);
    }

    public TimeSeriesCollection extractEnergyEvents(String grain, int intervalLengthSecs, Date start, Date stop) {

        XYDataset dbdataset = getDBDataset(grain, start, stop);

        TimeSeries fromdb = copyFirstTimeSeries(dbdataset);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(fromdb);

        //makeMinMaxDiff(dataset, fromdb);
        extractEnergy(dataset, fromdb, intervalLengthSecs);

        return dataset;
    }

    private XYDataset getDBDataset(String grain, Date start, Date stop) {
        XYDataset dbdataset = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "select stamp,watt from " + grain + " where stamp>='" + sdf.format(start) + "' and stamp<'" + sdf.format(stop) + "'";
            System.err.println("sql: " + sql);
            dbdataset = new JDBCXYDataset(DBURL, DBDRIVER, DBUSER, DBPASSWORD);
            ((JDBCXYDataset) dbdataset).executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(AnalyzeChart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnalyzeChart.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dbdataset;
    }

    private TimeSeries copyFirstTimeSeries(XYDataset dbdataset) {

        TimeSeries fromdb = new TimeSeries("DB Watts", Millisecond.class);
        for (int series = 0; series < 1; series++) {
            int n = dbdataset.getItemCount(series);
            for (int i = 0; i < n; i++) {
                Number xi = dbdataset.getX(series, i);
                Millisecond mi = new Millisecond(new Date(xi.longValue()));
                double yi = dbdataset.getYValue(series, i);
                //System.out.println("m=" + mi + " X Y = (" + xi + "," + yi);
                fromdb.add(mi, yi);
            }
        }
        return fromdb;
    }

    private void appendEvent(Date maxStartStamp, int durationSec, double maxW) {
        //log.debug("migratesql "+sql);
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(DBDRIVER);
            String sql = "INSERT INTO event(stamp,duration,watt) VALUES(?,?,?)";
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);
            pstmt = con.prepareStatement(sql);
            pstmt.setObject(1, maxStartStamp);
            pstmt.setObject(2, durationSec);
            pstmt.setObject(3, maxW);
            int rowcount = pstmt.executeUpdate();
        } catch (ClassNotFoundException cnfe) {
            Logger.getLogger(EnergyEventExtractor.class.getName()).log(Level.SEVERE, null, cnfe);
        } catch (SQLException sqle) {
            Logger.getLogger(EnergyEventExtractor.class.getName()).log(Level.SEVERE, null, sqle);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle2) {
                    Logger.getLogger(EnergyEventExtractor.class.getName()).log(Level.SEVERE, null, sqle2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqle3) {
                    Logger.getLogger(EnergyEventExtractor.class.getName()).log(Level.SEVERE, null, sqle3);
                }
            }
        }

    }

    private void extractEnergy(TimeSeriesCollection dataset, TimeSeries fromdb, int intervalLengthSecs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeSeries remaining = new TimeSeries("Remaining Noise", Millisecond.class);
        TimeSeries extracted = new TimeSeries("Extracted", Millisecond.class);

        int n = fromdb.getItemCount();
        for (int i = 0; i < n; i++) {
            TimeSeriesDataItem di = fromdb.getDataItem(i);
            RegularTimePeriod ti = di.getPeriod(); //should be a MilliSecond ?

            double yi = di.getValue().doubleValue();
            remaining.add(ti, yi);
        }
        int extractionIteration = 1;
        System.out.println("Start @ " + new Date());
        while (extractionIteration < 200) {
            /*
             * Each extraction round finds maximal energy step function
             * characterized by start,stop,maxW
             *  where remaining(t)>w for all t in (start,stop)
             */
            double maxE = 0;
            double maxW = 0;
            int maxStart = 0;
            int maxStop = 0;
            long maxDurationMS = 0;
            Date maxStartStamp = null;
            long startItTime = new Date().getTime();
            for (int start = 0; start < n; start++) {

                double maxWForStart = remaining.getDataItem(start).getValue().doubleValue();
                long startTimeMS = remaining.getDataItem(start).getPeriod().getFirstMillisecond();

                for (int stop = start; stop < n; stop++) {
                    maxWForStart = Math.min(maxWForStart, remaining.getDataItem(stop).getValue().doubleValue());
                    long stopTimeMS = remaining.getDataItem(stop).getPeriod().getFirstMillisecond();
                    // correct the duration!
                    stopTimeMS += intervalLengthSecs * 1000;
                    double maxEForStartStop = (stopTimeMS - startTimeMS) * maxWForStart;
                    if (maxEForStartStop > maxE) {
                        maxStart = start;
                        maxStop = stop;
                        maxDurationMS = stopTimeMS - startTimeMS;
                        maxStartStamp = new Date(startTimeMS);
                        maxW = maxWForStart;
                        maxE = maxEForStartStop;
                    //System.out.println("    New MaxE = " + (maxE / 1000 / 60 / 60 / 1000) + " kwh");
                    }
                }
            }
            long elapsed = new Date().getTime() - startItTime;
            System.out.println("el: " + elapsed + " #" + extractionIteration + " MaxE = " + (maxE / 1000 / 60 / 60 / 1000) + " kwh = " + maxW + "w x " + (maxDurationMS / 1000.0) + "s @ " + sdf.format(maxStartStamp));
            TimeSeries eventSeries = new TimeSeries("Iteration " + extractionIteration, Millisecond.class);
            appendEvent(maxStartStamp, (int) (maxDurationMS / 1000.0), maxW);
            for (int i = maxStart; i <= maxStop; i++) {
                TimeSeriesDataItem di = remaining.getDataItem(i);
                RegularTimePeriod ti = di.getPeriod(); //should be a MilliSecond ?

                double yi = di.getValue().doubleValue();
                remaining.addOrUpdate(ti, yi - maxW);

                double exi = 0;
                try {
                    exi = extracted.getDataItem(ti).getValue().doubleValue();
                } catch (NullPointerException npe) {
                }
                extracted.addOrUpdate(ti, exi + maxW);

                eventSeries.add(ti, maxW);

            }

            if (extractionIteration < 2) {
                dataset.addSeries(eventSeries);
            }
            extractionIteration++;
        }

        // Reverse sign on remaining.
        for (int i = 0; i < n; i++) {
            TimeSeriesDataItem di = remaining.getDataItem(i);
            RegularTimePeriod ti = di.getPeriod(); //should be a MilliSecond ?

            double yi = di.getValue().doubleValue();
            remaining.addOrUpdate(ti, -yi);
        }

        System.out.println("End @ " + new Date());

        dataset.addSeries(remaining);
        dataset.addSeries(extracted);
    }

    private void makeMinMaxDiff(TimeSeriesCollection dataset, TimeSeries fromdb) {
        TimeSeries maxWatt = new TimeSeries("Max Watts", Millisecond.class);
        TimeSeries minWatt = new TimeSeries("Min Watts", Millisecond.class);

        int n = fromdb.getItemCount();
        for (int i = 0; i < n; i++) {
            TimeSeriesDataItem di = fromdb.getDataItem(i);
            RegularTimePeriod ti = di.getPeriod(); //should be a MilliSecond ?

            double yi = di.getValue().doubleValue();
            //System.out.println("(" + ti.getClass().getName() + ") t=" + ti + " y = " + yi);
            double mx = 0;
            double mn = 0;
            for (int j = i; j < i + 5 && j < n; j++) {
                double yj = fromdb.getDataItem(j).getValue().doubleValue();
                mx = Math.max(Math.max(mx, yj - yi), 0);
                mn = Math.min(Math.min(mn, yj - yi), 0);
            }
            if (mx < 300) {
                mx = 0;
            }
            maxWatt.add(ti, mx);
            if (mn > -300) {
                mn = 0;
            }
            minWatt.add(ti, mn);
        }

        dataset.addSeries(maxWatt);
        dataset.addSeries(minWatt);
    }

    private void doADay(int daysAgo) {
        Date start = startOfDay(new Date(), -daysAgo);
        Date stop = startOfDay(new Date(), -daysAgo+1);
        extractEnergyEvents(GRAIN_TENSEC, 10, start, stop);
        
    }
    public static void main(String[] args) {
        System.out.println("Hello Extractor!");
        EnergyEventExtractor eee = new EnergyEventExtractor();
        for (int i=1;i<8;i++) {
            eee.doADay(i);
        }
    }
}
