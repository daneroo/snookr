/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model;

import imetrical.time.TimeConvert;
import imetrical.time.TimeManip;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    //private static final String DBURL = "jdbc:mysql://192.168.5.2/ted";
    private static final String DBURL = "jdbc:mysql://192.168.3.200/ted";
    private static final String DBUSER = "aviso";
    private static final String DBPASSWORD = null;

    public static ExpandedSignal getDBExpandedSignal(SignalRange sr) {
        return new DataFetcher().expandGMTXYDataset(sr);
    }

    // insert 0 values into 0'th series
    private ExpandedSignal expandGMTXYDataset(SignalRange sr) {
        Date gmtstart = TimeConvert.localToGMT(sr.start);
        Date gmtstop = TimeConvert.localToGMT(sr.stop);
        String tableName = sr.grain.tableName();

        XYDataset dbdataset = getDBDataset(gmtstart, gmtstop, tableName);

        int series = 0;
        int n = dbdataset.getItemCount(series);
        int samples = (int) (sr.stop.getTime() - sr.start.getTime()) / sr.grain.intervalLengthMS();

        //System.out.println(String.format("l:start: %d  stop: %d samples: %d itemCount: %d", sr.start.getTime(), sr.stop.getTime(), samples, n));
        ExpandedSignal es = new ExpandedSignal(samples);
        es.intervalLengthSecs = sr.intervalLengthSecs;
        es.offsetMS = sr.start.getTime();
        if (samples > 0) {
            // bad range, e.g. null result set return one sample (epoch,0) which we skip
            for (int i = 0; i < n; i++) {
                Number xi = dbdataset.getX(series, i);
                double yi = dbdataset.getYValue(series, i);
                int xoffset = (int) ((xi.longValue() - gmtstart.getTime()) / sr.grain.intervalLengthMS());
                //System.out.println(String.format("xoffset: %d y: %f --> %s", xoffset,yi,TimeManip.isoFmt.format(new Date(xi.longValue()))));
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

    public static void main(String[] args) {
        // little test 
        // Local Day of 2009-03-08 00:00:00
        // '2009-03-07 19:00:00' and stamp<'2009-03-08 20:00:00' has 9000 values not 86400
        for (int i = 7; i < 10; i++) {
            String start = String.format("2009-03-%02d 00:00:00", i);
            String stop = String.format("2009-03-%02d 00:00:00", i+1);
            SignalRange sr = new SignalRange(start, stop, SignalRange.Grain.MINUTE);
            ExpandedSignal es = DataFetcher.getDBExpandedSignal(sr);
            System.out.println(String.format("Day of %s grain: %s size: %d", start, sr.grain, es.values.length));
        }
    }
}
