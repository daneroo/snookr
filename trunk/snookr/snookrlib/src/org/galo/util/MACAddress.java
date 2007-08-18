/*
  ref http://www.dclunie.com/pixelmed/software/javadoc/com/pixelmed/utils/MACAddress.html
*/
package org.galo.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

public class MACAddress {

    public static String getMACAddress() { 
        // TODO MACAddress is broken under Vista
        if (new java.util.Date().getTime()>0) return "00:00:00:00:00:00";
	String commandsToTry[] = {
	    "ipconfig /all",            // Windows NT/2000/XP ipconfig
	    "cmd.exe /c ipconfig /all", // Windows NT/2000/XP ipconfig via cmd.exe
	    "ifconfig",                 // a plain ifconfig should work for linux and darwin
	    "/sbin/ifconfig",           // redhat if /sbin not in path..
	    "arp "+getUnqualifiedHostname(), // for solaris I think...
	};
	MACAddress mac = new MACAddress();
	String macAddressString = null;
	for (int i=0;i<commandsToTry.length;i++) {
	    String cmd = commandsToTry[i];
	    macAddressString = mac.executeCommandLookingForFirstLineContainingAndGetMatchingString(cmd,regexForMAC);
	    if (macAddressString != null) {
		// normalize [-:] to : and use uppercase
		return macAddressString.replaceAll(regexForMACSeparator,":").toUpperCase();
	    }
	}
	// or could return 00:00:00:00:00:00
	return null;

    }

    public static long getMACAddressAsLong() {
	String macAddressString = getMACAddress();
	long macAddressValue = 0;
	if (macAddressString != null) {
	    StringTokenizer st = new StringTokenizer(macAddressString,regexForMACSeparator);
	    macAddressValue = 0;
	    while (st.hasMoreTokens()) {
		String hexValue = st.nextToken();
		macAddressValue = (macAddressValue<<8) + (Long.parseLong(hexValue,16)&0x000000ff);
	    }
	}
	return macAddressValue;
    }

    public static void main(String arg[]) {
	System.out.println("MAC address = "+getMACAddress());
	//System.out.println("MAC address = 0x"+Long.toHexString(getMACAddressAsLong()));
    }    


    
    private static final String regexForMACSeparator = "[:-]";	// : on unix, - on windoze
    // note : "\\p{XDigit}}" is equivalent to "[0-9A-Fa-f]"
    private static final String regexForMAC = "([0-9A-Fa-f]{1,2}[:-]){5}[0-9A-Fa-f]{1,2}";
    
    // the following pattern of using threads to consume exec output is from
    // "http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html"

   // See also ...
    // "http://www.artsci.wustl.edu/ASCC/documentation/macaddrss.html"
    // "http://forum.java.sun.com/thread.jspa?threadID=245711&start=0&tstart=0"
    
    private class ConsumeStreamLookingForRexEx extends Thread {
	BufferedReader r;
	String regex;
	String value;
	ConsumeStreamLookingForRexEx(InputStream i,String regex) {
	    r = new BufferedReader(new InputStreamReader(i),10000);
	    this.regex=regex;
	    value=null;
	}
	
	public void run() {
	    try {
		String line;
		while ((line=r.readLine()) != null && (value == null || value.length() == 0)) {
		    //System.err.println("MACAddress.ConsumeStreamLookingForRexEx.run(): read line=\""+line+"\"");
		    if (line.length() > 0 && line.matches(".*"+regex+".*")) {
			//System.err.println("MACAddress.ConsumeStreamLookingForRexEx.run(): line matches=\""+line+"\"");
			StringTokenizer st = new StringTokenizer(line," ");
			while (st.hasMoreTokens()) {
			    String test = st.nextToken();
			    if (test.matches(regex)) {
				value = test;
				if (value != null && value.length() > 0) {
				    //System.err.println("MACAddress.ConsumeStreamLookingForRexEx.run(): got=\""+value+"\"");
				    break;		// do not look beyond the first found
				}
			    }
			}
		    }
		}
	    } catch (Exception e) {
		//e.printStackTrace(System.err);
	    }
	}
	
	String getValue() { return value; }
    }
    
    
    private final String executeCommandLookingForFirstLineContainingAndGetMatchingString(String command,String regex) {
	//System.err.println("MACAddress.executeCommandLookingForFirstLineContaining(): command=\""+command+"\"");
	String value = null;
	try {
	    Process p = Runtime.getRuntime().exec(command);
	    ConsumeStreamLookingForRexEx outConsumer = new ConsumeStreamLookingForRexEx(p.getInputStream(),regex);
	    ConsumeStreamLookingForRexEx errConsumer = new ConsumeStreamLookingForRexEx(p.getErrorStream(),regex);
	    outConsumer.start();
	    errConsumer.start();
	    //System.err.println("MACAddress.executeCommandLookingForFirstLineContaining(): waitFor");
	    int exitValue = p.waitFor();
	    //System.err.println("MACAddress.executeCommandLookingForFirstLineContaining(): exitVal=\""+exitValue+"\"");
	    // don't try to get a value until the output has been completely processed (may be after process finished)
	    outConsumer.join();
	    errConsumer.join();
	    value = outConsumer.getValue();
	    if (value == null) {
		value = errConsumer.getValue();
	    }
	} catch (Exception e) {
	    // ignore exception (e.g. if cannot find command on another platform, such as "java.io.IOException: cmd: not found")
	    // e.printStackTrace(System.err);
	}
	return value;
    }

    
    // This is just to support arp call (for solaris I think...
    private static final String getUnqualifiedHostname() {
	String hostname = null;
	try {
	    hostname = java.net.InetAddress.getLocalHost().getHostName();
	    if (hostname != null && hostname.length() > 0) {
		int period = hostname.indexOf(".");
		if (period != -1) {
		    hostname = hostname.substring(0,period);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace(System.err);
	}
	return hostname;
    }
    
 }

