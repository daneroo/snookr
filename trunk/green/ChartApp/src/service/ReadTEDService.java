/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author daniel
 */
public class ReadTEDService {

    public static final String KW_ERROR_VALUE = "0.0";
    public static final String TED_DASHBOARDDATA_URL = "http://aria.dl.sologlobe.com:9090/DashboardData";
    public static final String ISO_DATE_FORMAT = "%Y-%m-%d %H:%M:%S";
    private static final String XPATH_KWNOW = "/DashboardData/KWNow";

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            long startTime = new Date().getTime();
            String kwNow = readKW();
            long stopTime = new Date().getTime();
            long diffTime = stopTime - startTime;

            long sleepTime = (30000 - diffTime) % 1000l;

            if (i == 0) {
                sleepTime = 1000l - (stopTime % 1000l);
            }
            //log("diff: " + diffTime + " stop: " + stopTime + " sleep: " + sleepTime);
            log("" + (new Date().getTime()) + " kwNow " + kwNow);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                log(ex);
            }
        }
    }

    public static String readKW() {
        String kwNow = new ReadTEDService().xpathKW();
        return kwNow;
    }

    private String xpathKW() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try {
            InputSource inputSource = new InputSource(TED_DASHBOARDDATA_URL);


            String kwNow = xPath.evaluate(XPATH_KWNOW, inputSource);
            //log("" + (new Date().getTime()) + " kwNow " + kwNow);
            return kwNow;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(ReadTEDService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return KW_ERROR_VALUE;
    }

    private static void log(String message) {
        System.out.println(message);
    //Logger.getLogger(ReadTEDService.class.getName()).log(Level.INFO, message);
    }

    private static void log(InterruptedException ex) {
        Logger.getLogger(ReadTEDService.class.getName()).log(Level.SEVERE, null, ex);
    }
}
