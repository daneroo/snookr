/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model.broker;

import imetrical.time.TimeManip;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author daniel
 */
public class StampGMTAndDoublesHandler implements Handler {

    Calendar gmtcal;
    DateTimeFormatter gmtFmt;
    DateTimeZone gmtZone;

    public StampGMTAndDoublesHandler() {
        //gmtcal = Calendar.getInstance();
        //gmtcal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmtcal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));

        gmtZone = DateTimeZone.forID("UTC");
        String isoRFC822Str = "yyyy-MM-dd HH:mm:ss";
        gmtFmt = DateTimeFormat.forPattern(isoRFC822Str).withZone(gmtZone);

    }

    private Date jodaParseISOAsGMT(String dateStrWithoutTZ) {
        /*if (dateStrWithoutTZ.endsWith(".0")) {
            dateStrWithoutTZ = dateStrWithoutTZ.substring(0, dateStrWithoutTZ.length() - 2);
        }*/
        //DateTime date = gmtFmt.parseDateTime(dateStrWithoutTZ + "+0000");
        DateTime date = gmtFmt.parseDateTime(dateStrWithoutTZ);
        return date.toDate();
    }

    private Date reinterpretAsGMT(Date badGMT) {
        //return badGMT;
        DateTime b = new DateTime(badGMT.getTime());
        //return b.toDate();
        System.err.println(String.format(" joda-re: %4d-%02d-%02d %02d:%02d:%02d.%03d",b.getYear(), b.getMonthOfYear(), b.getDayOfMonth(),b.getHourOfDay(), b.getMinuteOfHour(), b.getSecondOfMinute(), b.getMillisOfSecond()));
        DateTime good = new DateTime(b.getYear(), b.getMonthOfYear(), b.getDayOfMonth(),
                b.getHourOfDay(), b.getMinuteOfHour(), b.getSecondOfMinute(), b.getMillisOfSecond(), gmtZone);
        return good.toDate();
    //DateTime date = gmtFmt.parseDateTime(dateStrWithoutTZ+"+0000");
    //return date.toDate();
    }

    public Object[] get(ResultSet rs, int cols) throws SQLException {
        Object array[] = new Object[cols];
        for (int i = 0; i < cols; i++) {
            if (i == 0) {
                //array[i] = TimeManip.parseISOAsGMT(rs.getString(i + 1));
                array[i] = jodaParseISOAsGMT(rs.getString(i + 1));
                //array[i] = rs.getTimestamp(i + 1, gmtcal);
                //array[i] = reinterpretAsGMT(rs.getTimestamp(i + 1));
                //array[i] = TimeManip.parseISOAsGMT(rs.getString(i + 1));
            } else {
                array[i] = rs.getDouble(i + 1);
            }
        }
        return array;
    }
}
