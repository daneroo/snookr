/*
 * HostInfo.java
 *
 * Created on February 14, 2008, 9:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package observefiles;

import java.net.InetAddress;

/**
 *
 * @author daniel
 */
public class HostInfo {
    
     public static String getHostInfo() {
        return "[O/S: "+ getOSName()+" IP: "+getIPAddress()+
               " Host: "+getHostName()+" MAC: "+getMACAddress()+"]";

    }
    public static String getOSName() { return System.getProperty("os.name"); }
    public static String getHostName() {
        try {return InetAddress.getLocalHost().getCanonicalHostName(); } catch (Exception e){return null;}
    }
    public static String getIPAddress() {
        try {return InetAddress.getLocalHost().getHostAddress(); } catch (Exception e){return null;}
    }
    public static String getMACAddress() {
        return MACAddress.getMACAddress(); 
    }

    public final static void main(String[] args) {
        System.out.println("Host: "+getHostInfo());
        System.out.println("  O/S:  "+ getOSName());
        System.out.println("  IP:   "+ getIPAddress());
        System.out.println("  Host: "+ getHostName());
        System.out.println("  MAC:  "+ getMACAddress());
    }   
}
