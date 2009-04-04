/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model.broker;

import chartapp.EnergyEventCorrelator;
import imetrical.model.DataFetcher;
import imetrical.model.ExpandedSignal;
import imetrical.model.SignalRange;
import imetrical.time.TimeManip;
import imetrical.util.Timer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author daniel
 */
public class Test {

    public static void log(String msg) {
        System.err.println(msg);
    //Logger.getLogger(Test.class.getName()).info(msg);
    }

    private static String asStringArray(Object[] row) {
        StringBuffer sb = new StringBuffer("[");
        for (Object o : row) {
            sb.append(o.toString() + ", ");
        }
        sb.append("]");

        return sb.toString();
    }

    private static String asClassNameArray(Object[] row) {
        StringBuffer sb = new StringBuffer("[");
        for (Object o : row) {
            sb.append(o.getClass().getName() + ", ");
        }
        sb.append("]");

        return sb.toString();
    }

    private static void showHeadAndTail(Vector<Object[]> v, int entries, boolean showClass) {
        int r = -1;
        for (Object[] row : v) {
            r++;
            if (entries > 0 && r > entries && r < v.size() - entries) {
                continue;
            }
            StringBuffer sb = new StringBuffer("row[" + r + "]: ");
            sb.append(asStringArray(row));
            if (showClass) {
                sb.append(" ");
                sb.append(asClassNameArray(row));
            }
            log(sb.toString());
        }
    }

    public void testGetObjects() {
        Broker b = Broker.instance();

        String sql = "select stamp,watt from watt_tensec where stamp>='2008-09-15 00:00:00' and stamp<'2008-09-16 00:00:00'";
        Vector<Object[]> v = b.getObjects(sql, 0);
        showHeadAndTail(v, 3, false);
        log("count vec: " + v.size());

    }

    public void testScalarInts() {
        Broker b = Broker.instance();

        int countAll = b.getOneInt("select count(*) from watt", -1);
        log("count all: " + countAll);
        int countDay = b.getOneInt("select count(*) from watt where stamp>='2008-09-15 00:00:00' and stamp<'2008-09-16 00:00:00'", -1);
        log("count day: " + countDay);

        log("getMax: " + b.getOneInt("select max(watt) from watt", -1));
        log("get Scalar Float: " + b.getOneInt("select 123.456 from watt", -1));
        log("get Scalar String: " + b.getOneInt("select concat('12','3') from watt", -1));

    }

    private void testScalarStrings() {
        Broker b = Broker.instance();

        log("get Scalar String: " + b.getOneString("select 'This is a string'", "Bad Value"));
        log("get Scalar Int: " + b.getOneString("select 123", "Bad Value"));
        log("get Scalar Float: " + b.getOneString("select 1/3", "Bad Value"));
        log("get Scalar Date: " + b.getOneString("select max(stamp) from watt", "Bad Value"));
    }

    private void testStampAndDoublesHandler() {
        Broker b = Broker.instance();
        String sql = "select left(stamp,10) as day,avg(watt) from watt where stamp>='2008-09-01' and stamp<'2008-09-07' group by day limit 4";
        Vector<Object[]> v = b.getObjects(sql, 0, new StampAndDoublesHandler());
        showHeadAndTail(v, 2, true);
    }

    private void testCreateAndInsert() {
        if (true) {
            throw new RuntimeException("DONT DO THIS, was for testing");
        }

        Broker b = Broker.instance();

        String unddl = "DROP TABLE IF EXISTS testwatt";
        log("executing: " + unddl);
        log("returned: " + b.execute(unddl));


        String ddl = "CREATE TABLE testwatt ( stamp datetime NOT NULL default '1970-01-01 00:00:00',`watt` int(11) NOT NULL default '0', PRIMARY KEY  (`stamp`))";
        log("executing: " + ddl);
        log("returned: " + b.execute(ddl));

        Date now = new Date();
        String sql = "INSERT INTO testwatt(stamp,watt) VALUES(?,?)";
        log("executing: " + sql);
        log("returned: " + b.execute(sql, new Object[]{now, 1000}));

        log("failing primary key: " + sql);
        log("returned: " + b.execute(sql, new Object[]{now, 2000}));

        sql = "REPLACE INTO testwatt(stamp,watt) VALUES(?,?)";
        log("replacing: " + sql);
        log("returned: " + b.execute(sql, new Object[]{now, 2000}));
        log("replacing +1sec: " + sql);
        log("returned: " + b.execute(sql, new Object[]{new Date(now.getTime() + 1000), 2000}));

        log("getCount: " + b.getOneInt("select count(*) from testwatt", -1));
        String showsql = "select * from testwatt";
        Vector<Object[]> v = b.getObjects(showsql, 0);
        showHeadAndTail(v, -1, false);

        int iterations = 1000;
        Timer tt = new Timer();
        for (int i = 0; i < iterations; i++) {
            b.execute(sql, new Object[]{new Date(now.getTime() + i * 1000), 2000 + (i % 100) * 10});
        }
        String msg = "speed test: " + iterations + " processed at rate " + tt.rate(iterations) + " ins/s or " + tt.diff() / iterations + " s/it";
        log(msg);
        v = b.getObjects(showsql, 0);
        showHeadAndTail(v, 2, false);

        log("executing: " + unddl);
        log("returned: " + b.execute(unddl));
    }

    private void testStampTZAndDoublesHandler() {
        log("Test sql for fetching iso+0000 Dates");
        Broker b = Broker.instance();
        String queries[] = new String[]{
            "select concat(left(stamp,10),' 00:00:00+0000') as day,avg(watt) from watt where stamp>='2008-09-01' and stamp<'2008-09-07' group by day limit 4",
            "select concat(stamp,'+0000'),watt from watt where stamp>='2009-01-26' and stamp<'2009-01-26 00:01:00'",};
        for (String sql : queries) {
            log("Executing: " + sql);
            Vector<Object[]> v = b.getObjects(sql, 0, new StampTZAndDoublesHandler());
            showHeadAndTail(v, 2, true);
        }
    }

    private void testStampGMTAndDoublesHandler() {
        log("Test sql for fetcing Dates as GMT");
        Broker b = Broker.instance();
        String queries[] = new String[]{
            "select concat(left(stamp,10),' 00:00:00') as day,avg(watt) from watt where stamp>='2008-09-01' and stamp<'2008-09-07' group by day limit 4",
            "select concat(stamp),watt from watt where stamp>='2009-01-26' and stamp<'2009-01-26 00:01:00'",};
        for (String sql : queries) {
            log("Executing: " + sql);
            Vector<Object[]> v = b.getObjects(sql, 0, new StampGMTAndDoublesHandler());
            showHeadAndTail(v, 2, true);
        }
    }

    private void testTimeZone() {
        // http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-configuration-properties.html
        // useJDBCCompliantTimezoneShift : default false
        // useLegacyDatetimeCode : default true
        // useTimezone : default false
        // I thonk that useJDBCCompliantTimezoneShift=true should be sufficient

        log("Test timezone handling");
        //String sql = "select cast('1966-05-16 06:07:08' as Datetime),1234";
        String sqltz = "select concat(cast('1966-05-16 06:07:08' as Datetime),'+0000'),1234";
        //String sql = "select stamp,watt from watt where stamp>='2009-01-01' and stamp<'2009-01-01 00:00:10'";
        String sql = "select stamp,watt from watt_day where stamp>='2009-01-01' and stamp<'2010-01-01 00:00:10'";
        Broker b = Broker.instance();
        Vector<Object[]> v;
        log("Sql: " + sql);

        log(" LocalTime: ");
        v = b.getObjects(sql, 0, new StampAndDoublesHandler());
        showHeadAndTail(v, 2, true);

        log(" GMT: ");
        v = b.getObjects(sql, 0, new StampGMTAndDoublesHandler());
        showHeadAndTail(v, 2, true);

    //log(" GMT-tz: ");
    //v = b.getObjects(sqltz, 0, new StampTZAndDoublesHandler());
    //showHeadAndTail(v, 2, true);
    }

    private void testTimeZoneForDSTBoundary() {
        /*
         * Pull the days from database and look
         *  The real problem is at
         *     gmtstamp: 2009-03-08 02:00:00+0000 -db-> 2009-03-08 03:00:00.000 -> 2009-03-07 22:00:00-0500
         *                should be -> 2009-03-07 21:00:00-0500
         *     gmtstamp: 2009-03-08 02:30:00+0000 -db-> 2009-03-08 03:30:00.000 -> 2009-03-07 22:30:00-0500
         *                should be -> 2009-03-07 21:30:00-0500
         *
         * Non-existant local hour
         *   stamp: 2009-03-08 02:00:00  reformat: 2009-03-08 03:00:00-0400
         *   stamp: 2009-03-08 02:30:00  reformat: 2009-03-08 03:30:00-0400
         *
         *   gmtstamp: 2008-11-02 05:00:00+0000  local: 2008-11-02 01:00:00-0400
         *   gmtstamp: 2008-11-02 05:30:00+0000  local: 2008-11-02 01:30:00-0400
         *   gmtstamp: 2008-11-02 06:00:00+0000  local: 2008-11-02 01:00:00-0500
         *   gmtstamp: 2008-11-02 06:30:00+0000  local: 2008-11-02 01:30:00-0500
         *
         *   gmtstamp: 2009-03-08 06:00:00+0000  local: 2009-03-08 01:00:00-0500
         *   gmtstamp: 2009-03-08 06:30:00+0000  local: 2009-03-08 01:30:00-0500
         *   gmtstamp: 2009-03-08 07:00:00+0000  local: 2009-03-08 03:00:00-0400
         *   gmtstamp: 2009-03-08 07:30:00+0000  local: 2009-03-08 03:30:00-0400
         */
        log("Test timezone handling at DST Boundary");
        String[] daysOfInterest = {"2008-11-02", "2009-03-08"};
        for (String day : daysOfInterest) {
            log(String.format("day: %s", day));
            for (int h = 0; h < 4; h++) {
                for (String min : new String[]{"00", "30"}) {
                    String stamp = String.format("%s %02d:%s:00", day, h, min);
                    Date localDate = TimeManip.parseISO(stamp);
                    String reformat = TimeManip.isoTZFmt.format(localDate);
                    log(String.format("  stamp: %s  reformat: %s", stamp, reformat));
                }
            }
        }
        for (String day : daysOfInterest) {
            log(String.format("day: %s", day));
            for (int h = 0; h < 8; h++) {
                for (String min : new String[]{"00", "30"}) {
                    String gmtstamp = String.format("%s %02d:%s:00+0000", day, h, min);
                    Date localDate = TimeManip.parseISOTZ(gmtstamp);
                    String localstamp = TimeManip.isoTZFmt.format(localDate);
                    // log(String.format("  gmtstamp: %s  local: %s", gmtstamp, localstamp));

                    String sql = "select cast('" + gmtstamp + "' as char),1234";
                    Broker b = Broker.instance();
                    Vector<Object[]> v;
                    //log("Sql: " + sql);

                    //log(" Interpreted as LocalTime: ");
                    //v = b.getObjects(sql, 0, new StampAndDoublesHandler());
                    //showHeadAndTail(v, 2, true);

                    //log(" interpreted as GMT: ");
                    v = b.getObjects(sql, 0, new StampGMTAndDoublesHandler());
                    //showHeadAndTail(v, 2, true);
                    Date localbackfromdb = (Date) (v.get(0)[0]);
                    String localbackfromdbstamp = TimeManip.isoTZFmt.format(localbackfromdb);

                    log(String.format("  gmtstamp: %s  local: %s  fromdb: %s", gmtstamp, localstamp, localbackfromdbstamp));

                }
            }
        }
    }

    private void compareDBSpeed() {
        System.out.println("---Compare speeds");
        Broker b = Broker.instance();
        String sql = "select stamp,watt from watt where stamp>='2008-11-02 04:00:00' and stamp<'2008-11-03 05:00:00'";
        String sqltz = "select concat(stamp,'+0000'),watt from watt where stamp>='2008-11-02 04:00:00' and stamp<'2008-11-03 05:00:00'";
        SignalRange sr = new SignalRange("2008-11-02 00:00:00", "2008-11-03 00:00:00");

        Timer tt = new Timer();
        ExpandedSignal dummy = new DataFetcher().expandGMTXYDataset(sr);
        System.out.println(String.format(" DF.expand:      %fs  (%d)", tt.diff(), dummy.values.length));
        tt.restart();

        Vector<Object[]> v = b.getObjects(sql, 0);
        System.out.println(String.format(" Obj      Hndlr: %fs  (%d)", tt.diff(), v.size()));
        tt.restart();

        v = b.getObjects(sql, 0, new StringHandler());
        System.out.println(String.format(" String   Hndlr: %fs  (%d)", tt.diff(), v.size()));
        tt.restart();

        v = b.getObjects(sql, 0, new StampAndDoublesHandler());
        System.out.println(String.format(" Stamp    Hndlr: %fs  (%d)", tt.diff(), v.size()));
        tt.restart();

        //v = b.getObjects(sqltz, 0, new StampTZAndDoublesHandler());
        //System.out.println(String.format(" StampTZ  Hndlr: %fs  (%d)", tt.diff(), v.size()));
        tt.restart();

        v = b.getObjects(sql, 0, new StampGMTAndDoublesHandler());
        System.out.println(String.format(" StampGMT Hndlr: %fs  (%d)", tt.diff(), v.size()));
        tt.restart();

    }

    private Date parseWithFormat(String dateStr,SimpleDateFormat fmt) {
        try {
            return fmt.parse(dateStr);
        } catch (ParseException ex) {
            Logger.getLogger(EnergyEventCorrelator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private void compareParseSpeed() {
        /* Compare parsing speeds for 86400 date strings
         *
         */
        final SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat isoTZFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        final SimpleDateFormat gmtFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        gmtFmt.setCalendar(cal);
        final DateTimeFormatter jodaTZ = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ");
        final DateTimeFormatter jodaISO = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeZone gmtZone = DateTimeZone.forID("UTC");
        final DateTimeFormatter jodaGMT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(gmtZone);

        int secsPerDay = 24 * 60 * 60;
        Date start = TimeManip.parseISOAsGMT("2009-03-08 00:00:00");
        String[] wholeday=new String[secsPerDay];
        String[] wholedaytz=new String[secsPerDay];
        for (int i = 0; i < secsPerDay; i++) {
            Date d = new Date(start.getTime() + (i * 1000l));
            wholeday[i] = gmtFmt.format(d);
            wholedaytz[i] = gmtFmt.format(d)+"+0000";
            if (false && i % 100 == 0) {
                log("wholdeay[" + i + "] = " + wholeday[i]);
            }
        }
        Timer tt = new Timer();
        for (int i = 0; i < secsPerDay; i++) {
            Date d = parseWithFormat(wholeday[i],isoFmt);
        }
        System.out.println(String.format(" Parse ISO      : %fs %f it/s (%d)", tt.diff(), tt.rate(secsPerDay), secsPerDay));
        tt.restart();
        for (int i = 0; i < secsPerDay; i++) {
            Date d = parseWithFormat(wholeday[i],gmtFmt);
        }
        System.out.println(String.format(" Parse ISOasGMT : %fs %f it/s (%d)", tt.diff(), tt.rate(secsPerDay), secsPerDay));
        tt.restart();
        for (int i = 0; i < secsPerDay; i++) {
            //Date d = parseWithFormat(wholedaytz[i],isoTZFmt);
        }
        System.out.println(String.format(" Parse ISOTZ    : %fs %f it/s (%d)", tt.diff(), tt.rate(secsPerDay), secsPerDay));
        tt.restart();
        for (int i = 0; i < secsPerDay; i++) {
            DateTime d = jodaTZ.parseDateTime(wholedaytz[i]);
        }
        System.out.println(String.format(" Parse JodaTZ    : %fs %f it/s (%d)", tt.diff(), tt.rate(secsPerDay), secsPerDay));
        tt.restart();
        for (int i = 0; i < secsPerDay; i++) {
            DateTime d = jodaGMT.parseDateTime(wholeday[i]);
        }
        System.out.println(String.format(" Parse JodaGMT   : %fs %f it/s (%d)", tt.diff(), tt.rate(secsPerDay), secsPerDay));
        tt.restart();

    }

    public static void main(String[] args) {
        Test t = new Test();
        //t.testTimeZone();
        //t.testTimeZoneForDSTBoundary();
        for (int i = 0; i < 4; i++) t.compareParseSpeed();
        System.exit(0);
        for (int i = 0; i < 4; i++) {
            t.compareDBSpeed();
        }
        System.exit(0);

        t.testGetObjects();
        t.testScalarInts();
        t.testScalarStrings();
        t.testStampAndDoublesHandler();
        //t.testCreateAndInsert();
        t.testStampTZAndDoublesHandler();
        t.testStampGMTAndDoublesHandler();
    }
}
