/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model;

import imetrical.time.TimeConvert;
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

    public static  ExpandedSignal getDBExpandedSignal(SignalRange sr) {
        DataFetcher df = new DataFetcher();
        XYDataset dbdataset = df.getDBDataset(sr);
        ExpandedSignal es = df.expandGMTXYDataset(dbdataset);
        es.intervalLengthSecs = sr.intervalLengthSecs;
        return es;
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
}
