/*
 * SnookrWSClient.java
 *
 * Created on February 15, 2008, 10:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package observefiles;

import java.sql.Time;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import net.snookr.util.Timer;

/**
 *
 * @author daniel
 */
public class SnookrWSClient {
    
    public static void main(String[] args){
        System.out.println("Hello SnookrWS");
        new SnookrWSClient().test();
    }
    /** Creates a new instance of SnookrWSClient */
    public SnookrWSClient() {
    }
    
    protected void finalize() throws Throwable {
    }
    
    private void test() {
        System.out.println("testing SnookrWS");
        //Commented for compiling. without ws
        /*
        testEcho();
        testCreateOrUpdate(true);
        testCreateOrUpdateSpeed(10);
        //testCreateOrUpdateSpeed(100);
        testCountForHost();
         */
    }
    /*
    private void testEcho() {
        System.out.println("testing SnookrWS.echo");
        try { // Call Web Service Operation
            observefiles.SnookrWSService service = new observefiles.SnookrWSService();
            observefiles.SnookrWS port = service.getSnookrWSPort();
            // TODO initialize WS operation arguments here
            java.lang.String str = "stamp:"+new java.util.Date();
            // TODO process result here
            java.lang.String result = port.echo(str);
            System.out.println("Result = "+result);
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        
    }
    private XMLGregorianCalendar toxmlgc(Date d) {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(d);
            XMLGregorianCalendar xmlgc =
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            return xmlgc;
        } catch (DatatypeConfigurationException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private void testCreateOrUpdate(boolean verbose) {
        if (verbose) System.out.println("testing SnookrWS.createOrUpdate");
        try { // Call Web Service Operation
            observefiles.SnookrWSService service = new observefiles.SnookrWSService();
            observefiles.SnookrWS port = service.getSnookrWSPort();
            // TODO initialize WS operation arguments here
            java.lang.String host = "myhost.domain.net";
            java.lang.String fileName = "myimage.jpg";
            java.lang.Long fileSize = new Long(1234);
            XMLGregorianCalendar lastModified = toxmlgc(new Date());
            java.lang.String md5 = "e20367119f35b304f65904eef0b298e1";
            XMLGregorianCalendar taken = toxmlgc(new Date(new Date().getTime()-10000)); // minus 10 seconds
            // TODO process result here
            java.lang.String result = port.createOrUpdate(host, fileName, fileSize, lastModified, md5, taken);
            if (verbose) System.out.println("Result = "+result);
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        
    }
    private void testCreateOrUpdateSpeed(int iterations) {
        System.out.println("testing SnookrWS.createOrUpdateSpeed("+iterations+")");
        Timer tt = new Timer();
        for(int i=0;i<iterations;i++) testCreateOrUpdate(false);
        System.out.println("CreateOrUpdate "+iterations+" it in "+tt.diff()+"s ("+tt.rate(iterations)+" it/s)");
    }
    
    private void testCountForHost() {
        String host = HostInfo.getHostName();
        System.out.println("testing countForHost "+host);
        try { // Call Web Service Operation
            observefiles.SnookrWSService service = new observefiles.SnookrWSService();
            observefiles.SnookrWS port = service.getSnookrWSPort();
            // TODO initialize WS operation arguments here
            // TODO process result here
            int result = port.countForHost(host);
            System.out.println("Result = "+result);
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
    }
    */
}
