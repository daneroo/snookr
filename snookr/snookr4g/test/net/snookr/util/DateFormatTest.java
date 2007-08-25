/*
 * DateFormatTest.java
 * JUnit based test
 *
 * Created on August 25, 2007, 12:53 AM
 */

package net.snookr.util;

import java.util.Calendar;
import junit.framework.*;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author daniel
 */
public class DateFormatTest extends TestCase {
    
    public DateFormatTest(String testName) {
        super(testName);
    }

    Date date1 = new GregorianCalendar(1997,Calendar.APRIL,3).getTime();
    String str1 = "1997-04-03 00:00:00";
    
    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of format method, of class net.snookr.util.DateFormat.
     */
    public void testFormat() {
        String expResult = str1;
        String result = DateFormat.format(date1);
        assertEquals(expResult, result);
    }
    public void testFormatNull() {
        String expResult = "1970-01-01 00:00:00";
        String result = DateFormat.format(null);
        assertEquals(expResult, result);
    }
    public void testFormatWithDefaultValue() {
        String expResult = str1;
        String result = DateFormat.format(null,str1);
        assertEquals(expResult, result);
    }

    /**
     * Test of parse method, of class net.snookr.util.DateFormat.
     */
    public void testParse() {
        Date expResult = date1;
        Date result = DateFormat.parse(str1);
        assertEquals(expResult, result);
    }
    public void testParseNull() {
        Date expResult = new GregorianCalendar(1970,Calendar.JANUARY,1).getTime();
        Date result = DateFormat.parse(null);
        assertEquals(expResult, result);
    }
    public void testParseBadValue() {
        Date expResult = new GregorianCalendar(1970,Calendar.JANUARY,1).getTime();
        Date result = DateFormat.parse("Badly-Formated-Date-String");
        assertEquals(expResult, result);
    }
    public void testParseWithDefaultValue() {
        Date expResult = date1;
        Date result = DateFormat.parse(null,date1);
        assertEquals(expResult, result);
    }
    
}
