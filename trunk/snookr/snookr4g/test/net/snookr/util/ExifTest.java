/*
 * ExifTest.java
 * JUnit based test
 *
 * Created on August 25, 2007, 1:16 AM
 */

package net.snookr.util;

import junit.framework.*;
import java.util.Date;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author daniel
 */
public class ExifTest extends TestCase {
    
    public ExifTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getExifDate method, of class net.snookr.util.Exif.
     */
    public void testGetExifDate() {
        File f = null;
       
        Date expResult = new GregorianCalendar(1970,Calendar.JANUARY,1).getTime();
        Date result = Exif.getExifDate(f);
        assertEquals(expResult, result);
    }
    
}
