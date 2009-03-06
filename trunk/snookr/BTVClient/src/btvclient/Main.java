/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package btvclient;

import com.snapstream.types.ArrayOfPVSProperty;
import com.snapstream.types.PVSProperty;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello BTV");
        new Main().run();

    }

    private void flatViewByDate(String retreivedAuthTicket) {
        try {
            // Call Web Service Operation
            com.snapstream.webservice.BTVLibrary service = new com.snapstream.webservice.BTVLibrary();
            com.snapstream.webservice.BTVLibrarySoap port = service.getBTVLibrarySoap();
            // TODO initialize WS operation arguments here
            java.lang.String authTicket = retreivedAuthTicket;
            // TODO process result here
            com.snapstream.webservice.ArrayOfPVSPropertyBag result = port.flatViewByDate(authTicket);
            System.out.println("Result = " + result);
            show(result);
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
    }

    private void getMedia(String retreivedAuthTicket) {
        try {
            // Call Web Service Operation
            com.snapstream.webservice.BTVLibrary service = new com.snapstream.webservice.BTVLibrary();
            com.snapstream.webservice.BTVLibrarySoap port = service.getBTVLibrarySoap12();
            // TODO initialize WS operation arguments here
            java.lang.String authTicket = retreivedAuthTicket;
            com.snapstream.webservice.LibraryHideOption hide = com.snapstream.webservice.LibraryHideOption.NONE;
            com.snapstream.webservice.LibrarySort sort = com.snapstream.webservice.LibrarySort.DATE_RECORDED;
            com.snapstream.webservice.LibrarySortOrder order = com.snapstream.webservice.LibrarySortOrder.ASCENDING;
            int start = 0;
            int count = 100;
            javax.xml.ws.Holder<com.snapstream.webservice.ArrayOfPVSPropertyBag> getMediaResult = new javax.xml.ws.Holder<com.snapstream.webservice.ArrayOfPVSPropertyBag>();
            javax.xml.ws.Holder<Integer> totalCount = new javax.xml.ws.Holder<Integer>();
            port.getMedia(authTicket, hide, sort, order, start, count, getMediaResult, totalCount);
            System.out.println("totalCount = " + totalCount.value);
            show(getMediaResult.value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getStatsInformation(String retreivedAuthTicket) {
        //Library
        // Statis Information
        try {
            // Call Web Service Operation
            com.snapstream.webservice.BTVLibrary service = new com.snapstream.webservice.BTVLibrary();
            com.snapstream.webservice.BTVLibrarySoap port = service.getBTVLibrarySoap12();
            // TODO initialize WS operation arguments here
            java.lang.String authTicket = retreivedAuthTicket;
            // TODO process result here
            com.snapstream.webservice.ArrayOfPVSPropertyBag result = port.getStatsInformation(authTicket);
            System.out.println("Result = " + result);
            show(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getUpcomingRecording(String retreivedAuthTicket) {
        // upcoming recordings
        try {
            // Call Web Service Operation
            com.snapstream.webservice.BTVScheduler service = new com.snapstream.webservice.BTVScheduler();
            com.snapstream.webservice.BTVSchedulerSoap port = service.getBTVSchedulerSoap12();
            // TODO initialize WS operation arguments here
            java.lang.String authTicket = retreivedAuthTicket;
            // TODO process result here
            com.snapstream.webservice.ArrayOfPVSPropertyBag result = port.getUpcomingRecordings(authTicket);
            System.out.println("Result = " + result);
            show(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    //Library
    }

    private void run() {
        //Logon ???
        String retreivedAuthTicket = "";
        try { // Call Web Service Operation
            com.snapstream.webservice.BTVLicenseManager service = new com.snapstream.webservice.BTVLicenseManager();
            com.snapstream.webservice.BTVLicenseManagerSoap port = service.getBTVLicenseManagerSoap12();
            // TODO initialize WS operation arguments here
            java.lang.String networkLicense = "";
            java.lang.String username = "";
            java.lang.String password = "";
            // TODO process result here
            com.snapstream.types.PVSPropertyBag result = port.logonRemote(networkLicense, username, password);
            //System.out.println("Result = " + result);
            Map<String, String> map = toMap(result);
            retreivedAuthTicket = map.get("AuthTicket");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Get Version Number
        try { // Call Web Service Operation
            com.snapstream.webservice.BTVLicenseManager service = new com.snapstream.webservice.BTVLicenseManager();
            com.snapstream.webservice.BTVLicenseManagerSoap port = service.getBTVLicenseManagerSoap12();
            // TODO process result here
            java.lang.String result = port.getVersionNumber();
            System.out.println("Result = " + result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //getUpcomingRecording(retreivedAuthTicket);
        //getStatsInformation(retreivedAuthTicket);
        //getMedia(retreivedAuthTicket);
        flatViewByDate(retreivedAuthTicket);

        //java.lang.String authTicket = retreivedAuthTicket;
        System.out.println(""); //DONT WRAP PAST THIS


        try { // Call Web Service Operation
            com.snapstream.webservice.BTVGuideUpdater service = new com.snapstream.webservice.BTVGuideUpdater();
            com.snapstream.webservice.BTVGuideUpdaterSoap port = service.getBTVGuideUpdaterSoap12();
            // TODO initialize WS operation arguments here
            java.lang.String authTicket = retreivedAuthTicket;
            // TODO process result here
            java.math.BigInteger lau = port.getLastAttemptedUpdate(authTicket);
            System.out.println("Last Attempted Update = " + df.format(convertBTVToDate(lau.longValue())));
            java.math.BigInteger nau = port.getNextAttemptedUpdate(authTicket);
            System.out.println("NextAtempted Update = " + df.format(convertBTVToDate(nau.longValue())));
            java.math.BigInteger lsu = port.getLastSuccessfulUpdate(authTicket);
            System.out.println("Last Succesful Update = " + df.format(convertBTVToDate(lsu.longValue())));
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }

        //final long wintime = 633533725320003750l;
        // 2008-08-04 01:39
        final long wintime = 633534107180007500l;
        final long tedEpochOnOSX = -62135582400000l;
        String tedEpochStr = "01-01-02 23:00:00";
        Date tedEpoch = new Date();
        try {
            tedEpoch = df.parse(tedEpochStr);
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("parsed ted epoch: " + tedEpoch);
        System.out.println("ted epoch.getTime: " + tedEpoch.getTime());

        /* Alexandre le grand....
         * ActualStart : 128491522812423350
         * Added : 128491611026642100
         * AddedBias : 300
         * Duration : 38371037500
         * LastExistsTime : 128804300615468750
         * LastWriteTime : 128491610160548350
         * TargetStart : 128491524000000000
         */

        // start with duration:
        long duration = 38371037500l;
        long tenMillion = 10000000;
        long oneMillion = 1000000;
        long hunThou = 100000;

        System.out.println(String.format("duration: %d -> %d", duration, duration / tenMillion));
        // == 1h+237 seconds ~ 1h+2*2minutes

        String alexExpectedStr = "2008-03-04 20:00:00";
        Date alexExpectedDate = new Date();
        try {
            alexExpectedDate = df.parse(alexExpectedStr);
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        // expectDate.getTime() = 1204678800000
        System.out.println(String.format("Expected: %s -> %s -> %d", alexExpectedStr, df.format(alexExpectedDate), alexExpectedDate.getTime()));

        long alexTargetStart = 128491524000000000l;  // ?? 3/4/2008 8:00 PM
        long alexTargetStartSecs = alexTargetStart / hunThou;
        System.out.println(String.format("TargetStart: %d -> %d", alexTargetStart, alexTargetStartSecs));
        Date alexTargetStartDate = new Date(alexTargetStartSecs);
        System.out.println(String.format("TargetStart: %d -> %s", alexTargetStart, df.format(alexTargetStartDate)));

        /*
         * 
         * Just for Laughs	                    3/5/2009 7:30 PM	1 Hr 3 Min
         *   targetStart: 128807730000000000  duration: 38368034643
         * Dans une galaxie près de chez-vous	3/5/2009 6:30 PM	33 Min 56 Sec 	
         *   targetStart: 128807694000000000  duration: 20367367143
         * Les Simpson                       	3/5/2009 6:00 PM	33 Min 55 Sec 	
         *   targetStart: 128807676000000000  duration: 20355355000
         *
         * Lets do some math:
         *   duration(laughs)   = 38368034643 / 10,000,000 = 3836.8 01:03:56
         *   duration(galaxie)  = 20367367143 / 10,000,000 =?= 2036 == 33:56
         *   duration(simpsons) = 20355355000 / 10,000,000 = 2035.53 == 33:55
         *
         * start(laughs) - start(galaxie)  = 36000000000 = 3600*10,000,000
         * start(galaxie)- start(simpsons) = 18000000000 = 1800*10,000,000
         *
         * laugh    df.parse("2009-03-05 19:30:00").getTime(): 1236299400000
         * galaxie  df.parse("2009-03-05 18:30:00").getTime(): 1236295800000
         * simpsons df.parse("2009-03-05 18:00:00").getTime(): 1236294000000
         *
         * so how do we 1236299400000  -> 12880773000 *10000000 = 128807730000000000
         * 1236299400 secs vs 12880773000 secs = -11644473600 secs
         * epoch: 1600-12-31 19:00:00 = 1601-01-01 00:00:00 - GMT-5:00
         *  so if we parsse in GMT :
         *     long offsetMS = df.parse("1601-01-01 00:00:00");
         *   but if we parse in EST:
         *     long offsetMS = df.parse("1600-12-31 19:00:00").getTime();

         */
        try {
            long laughStartTime = df.parse("2009-03-05 19:30:00").getTime();
            long galaxieStartTime = df.parse("2009-03-05 18:30:00").getTime();
            long simsonsStartTime = df.parse("2009-03-05 18:00:00").getTime();
            System.out.println("laugh    df.parse(\"2009-03-05 19:30:00\").getTime(): " + laughStartTime);
            System.out.println("galaxie  df.parse(\"2009-03-05 18:30:00\").getTime(): " + galaxieStartTime);
            System.out.println("simpsons df.parse(\"2009-03-05 18:00:00\").getTime(): " + simsonsStartTime);
            System.out.println("Epoch ? : " + df.format(new Date(-11644473600000l)));

            // if we were in GMT : long offsetMS = df.parse("1601-01-01 00:00:00").getTime();
            long offsetMS = df.parse("1600-12-31 19:00:00").getTime();
            System.out.println("Offset : " + offsetMS);

            compare("laugh",   "2009-03-05 19:30:00",128807730000000000l);
            compare("galaxie", "2009-03-05 18:30:00",128807694000000000l);
            compare("simpsons","2009-03-05 18:00:00",128807676000000000l);

        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


    /*
    long aBTVStamp = 128808332532650000l;
    long converted = aBTVStamp / 10000l + tedEpoch.getTime();
    Date aBTVDate = new Date(converted);
    System.out.println(String.format("BTV stamps convert: %d -> %s", aBTVStamp, df.format(aBTVDate)));
     */
    }

    Date convertBTVToDate(long btvStamp) {
        /*
         *  so if we parsse in GMT :
         *     long offsetMS = df.parse("1601-01-01 00:00:00").getTime();
         *   but if we parse in EST:
         *     long offsetMS = df.parse("1600-12-31 19:00:00").getTime();
         */
        final long epochBTVMS = -11644473600000l;
        return new Date(btvStamp / 10000 + epochBTVMS);
    }
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    void compare(String name, String dateStr, long btvStamp) {
        Date cnvDate = convertBTVToDate(btvStamp);
        System.out.println(String.format("%s  : %s == %s <- %d ", name, dateStr, df.format(cnvDate), btvStamp));
    }

    Map<String, String> toMap(com.snapstream.types.PVSPropertyBag result) {
        Map<String, String> map = new HashMap<String, String>();
        ArrayOfPVSProperty ap = result.getProperties();
        List<PVSProperty> pl = ap.getPVSProperty();
        for (PVSProperty p : pl) {
            map.put(p.getName(), p.getValue());
        }

        return map;
    }

    private void show(com.snapstream.webservice.ArrayOfPVSPropertyBag result) {
        System.out.println("++ List of PropertyBags ++ ");
        List<com.snapstream.types.PVSPropertyBag> lpb = result.getPVSPropertyBag();
        for (com.snapstream.types.PVSPropertyBag pb : lpb) {
            show(pb);
        }

        System.out.println("-- List of PropertyBags -- (" + lpb.size() + ")");
    }

    private void show(com.snapstream.types.PVSPropertyBag result) {
        System.out.println("  ++ PropertyBag ++ ");
        ArrayOfPVSProperty ap = result.getProperties();
        List<PVSProperty> pl = ap.getPVSProperty();
        for (PVSProperty p : pl) {
            System.out.println(String.format("    %s : %s", p.getName(), p.getValue()));
        }

        System.out.println("  -- PropertyBag -- ");
    }
}
