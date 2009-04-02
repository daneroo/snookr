/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.util;

import java.util.Date;
import java.util.Set;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author daniel
 */
public class JodaTimeTest {

    static String isoStr = "yyyy-MM-dd HH:mm:ss";
    static String isoRFC822Str = "yyyy-MM-dd HH:mm:ssZ";


    static { // static init block
        System.out.println("Static init");
    }

    public static void getAvailableIDs() {
        Set<String> setID = DateTimeZone.getAvailableIDs();
        for (String str : setID) {
            System.out.println("--TZ ID:    " + str);
        }
        System.out.println("--Default TZ ID:    " + DateTimeZone.getDefault());

    }

    public static void main(String[] args) {
        //getAvailableIDs();
        DateTime dt = new DateTime();
        DateTimeFormatter fmtT = ISODateTimeFormat.dateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern(isoStr);
        fmt.withZone(DateTimeZone.getDefault());

        String str = fmt.print(dt);
        System.out.println("--Joda Now:    " + str);
        DateTime firstMarch = new DateTime(2009, 03, 01, 0, 0, 0, 0);
        System.out.println("--Joda March1:    " + firstMarch);


        String[] dateString = {
            "2008-01-01 00:00:00",
            "2008-02-01 00:00:00",
            "2008-03-01 00:00:00",
            "2008-04-01 00:00:00",
            "2008-05-01 00:00:00",
            "2008-06-01 00:00:00",
            "2008-07-01 00:00:00",
            "2008-08-01 00:00:00",
            "2008-09-01 00:00:00",
            "2008-10-01 00:00:00",
            "2008-11-01 00:00:00",
            "2008-12-01 00:00:00",
            "2009-01-01 00:00:00",
            "2009-02-01 00:00:00",
            "2009-03-01 00:00:00",
            "2009-04-01 00:00:00",
            "2009-05-01 00:00:00",
            "2009-06-01 00:00:00",
            "2009-07-01 00:00:00",
            "2009-08-01 00:00:00",
            "2009-09-01 00:00:00",
            "2009-10-01 00:00:00",
            "2009-11-01 00:00:00",
            "2009-12-01 00:00:00",
            "2009-03-20 00:00:00",
            "2009-06-21 00:00:00",
            "2009-09-22 00:00:00",
            "2009-12-22 00:00:00",
        };
        //String[] dateString = {"2008-03-20T00:00:00", "2008-06-21T00:00:00", "2008-06-01T00:00:00", "2008-09-22T00:00:00"};
        for (int i = 0; i < dateString.length; i++) {
            DateTime date = fmt.parseDateTime(dateString[i]);
            System.out.println("--Parsed:    " + date + " zone:" + date.getZone()+" isOffsetParsed:"+fmt.isOffsetParsed());
            //DateTime redone = new DateTime(date.year(), date.monthOfYear(), date.dayOfMonth(), 0, 0, 0, 0);
            //DateMidnight redone = new DateMidnight(date.year().get(), date.monthOfYear().get(), date.dayOfMonth().get());
            //System.out.println("--Redone:    " + redone + " zone:" + redone.getZone());
        }

        // Walk back:
        DateTime walk = new DateTime();
        for (int i = 0; i < 0; i++) {
            System.out.println("walk:    " + walk);
            walk = walk.minusDays(1);
        }

    }

    // input date is local
    Date locatToGMT(Date localDate) {
        return null;
    }
}
