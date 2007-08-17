package org.galo.util;
/**
   refs:
   http://www.dclunie.com/pixelmed/software/javadoc/com/pixelmed/utils/MACAddress.html
   http://forum.java.sun.com/thread.jspa?forumID=4&threadID=245711
   referenced from sun bug:
   http://bugs.sun.com/bugdatabase/view_bug.do;:WuuT?bug_id=4143901


*/

import java.net.InetAddress;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;

public final class HostInfo {

    
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
    public static String getMACAddress() { return MACAddress.getMACAddress(); }
    
    public final static void main(String[] args) {
	System.out.println("Host: "+getHostInfo());
	System.out.println("  O/S:  "+ getOSName());
	System.out.println("  IP:   "+ getIPAddress());
	System.out.println("  Host: "+ getHostName());
	System.out.println("  MAC:  "+ getMACAddress());
    }


}
