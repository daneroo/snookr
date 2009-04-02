/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model.broker;

import imetrical.util.Timer;
import java.util.Date;
import java.util.Vector;

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
            throw new RuntimeException("DONT DO THIS");
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

    public static void main(String[] args) {
        Test t = new Test();
        t.testGetObjects();
        t.testScalarInts();
        t.testScalarStrings();
        // long time : 
        t.testStampAndDoublesHandler();
    //t.testCreateAndInsert();
    }
}
