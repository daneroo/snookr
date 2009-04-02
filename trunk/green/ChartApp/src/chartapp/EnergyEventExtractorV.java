/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartapp;

import imetrical.model.broker.Broker;
import green.util.Timer;
import imetrical.time.TimeManip;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
public class EnergyEventExtractorV {

    public static final String GRAIN_TENSEC = "watttensec";
    public static final String GRAIN_SECOND = "watt";

    public void extractEnergyEvents() {
        doADay(1);
    }

    public void extractEnergyEvents(String grain, int intervalLengthSecs, Date start, Date stop) {

        Vector<Object[]> dbvec = getDBDataset(grain, start, stop);

        List listOfEvents = new Vector();

        extractEnergy(listOfEvents, dbvec, intervalLengthSecs);

    //return CollectionOfEvents;
    }

    private Vector<Object[]> getDBDataset(String grain, Date start, Date stop) {
        Vector<Object[]> dbvec = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "select stamp,watt from " + grain + " where stamp>='" + sdf.format(start) + "' and stamp<'" + sdf.format(stop) + "'";
            System.err.println("sql: " + sql);

            Broker b = Broker.instance();
            dbvec = b.getObjects(sql, 0);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        return dbvec;
    }

    private void appendEvent(Date maxStartStamp, int durationSec, double maxW) {
        String sql = "INSERT INTO event(stamp,duration,watt) VALUES(?,?,?)";
        Broker b = Broker.instance();
    //b.execute(sql,new Object[]{maxStartStamp,durationSec,maxW});
    }

    private double castValue(Object[] di) {
        // what is the guaranteed type of di[1];
        return ((Number) di[1]).doubleValue();
    }

    private Date castDate(Object[] di) {
        return (Date) di[0];
    }

    private Object[] makePair(Date d, double v) {
        return new Object[]{d, v};
    }

    private void extractEnergy(List listOfEvents, Vector<Object[]> dbvec, int intervalLengthSecs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Here is where we optimize the working datastructure
        Vector<Object[]> remaining = (Vector<Object[]>) new Vector<Object[]>(dbvec.size());

        int n = dbvec.size();

        remaining.addAll(dbvec); // otherwise set does not work...

        for (int i = 0; i < n; i++) {
            Object[] di = (Object[]) dbvec.get(i);
            Date ti = castDate(di);
            double yi = castValue(di);
            // what is the guaranteed type of di[1];
            remaining.set(i, makePair(ti, yi));
        }
        int extractionIteration = 1;
        System.out.println("Start @ " + new Date());

        long endOfDataMS = castDate(remaining.get(n - 1)).getTime() + intervalLengthSecs * 1000;
        System.err.println("End of Data: " + sdf.format(new Date(endOfDataMS)));

        while (extractionIteration < 3) {
            /*
             * Each extraction round finds maximal energy step function
             * characterized by start,stop,maxW
             *  where remaining(t)>w for all t in (start,stop)
             */
            double maxE = 0;
            double maxW = 0;
            int maxStart = 0;
            int maxStop = 0;
            long maxDurationMS = 0;
            Date maxStartStamp = null;
            Timer tt = new Timer();
            for (int start = 0; start < n; start++) {

                double maxWForStart = castValue(remaining.get(start));
                long startTimeMS = castDate(remaining.get(start)).getTime();

                for (int stop = start; stop < n; stop++) {
                    maxWForStart = Math.min(maxWForStart, castValue(remaining.get(stop)));
                    long stopTimeMS = castDate(remaining.get(stop)).getTime();
                    // correct the duration!
                    stopTimeMS += intervalLengthSecs * 1000;

                    double maxEForStartStop = (stopTimeMS - startTimeMS) * maxWForStart;
                    if (maxEForStartStop > maxE) {
                        maxStart = start;
                        maxStop = stop;
                        maxDurationMS = stopTimeMS - startTimeMS;
                        maxStartStamp = new Date(startTimeMS);
                        maxW = maxWForStart;
                        maxE = maxEForStartStop;
                    //System.out.println("    New MaxE = " + (maxE / 1000 / 60 / 60 / 1000) + " kwh");
                    }
                    /* skip test #1
                     *  if [start,endOfData]@maxWForStart <maxE skip to next start: break.
                     */
                    double maxPossibleEForStart = (endOfDataMS - startTimeMS) * maxWForStart;
                    if (maxPossibleEForStart < maxE) {
                        //System.err.println("Broke at stop="+stop+" of n="+n);
                        break;
                    }
                }
            }
            System.out.println("t: " + tt.diff() + "s #" + extractionIteration + " MaxE = " + (maxE / 1000 / 60 / 60 / 1000) + " kwh = " + maxW + "w x " + (maxDurationMS / 1000.0) + "s @ " + sdf.format(maxStartStamp));

            appendEvent(maxStartStamp, (int) (maxDurationMS / 1000.0), maxW);

            // This part adds to extracted, and subtracts from remaining.
            // we will skip extracted in favor of accumulating Energy Events...
            for (int i = maxStart; i <= maxStop; i++) {
                Object[] di = (Object[]) remaining.get(i);
                Date ti = castDate(di);
                double yi = castValue(di);
                // what is the guaranteed type of di[1];
                remaining.set(i, makePair(ti, yi - maxW));
            }

            //listOfEvents.add(SOMETHING)
            extractionIteration++;
        }


        System.out.println("End @ " + new Date());

    }

    private void doADay(int daysAgo) {
        Date start = TimeManip.startOfDay(new Date(), -daysAgo);
        Date stop = TimeManip.startOfDay(new Date(), -daysAgo + 1);
        extractEnergyEvents(GRAIN_TENSEC, 10, start, stop);
    // extractEnergyEvents(GRAIN_SECOND, 1, start, stop);
    }

    public static void main(String[] args) {
        System.out.println("Hello Extractor!");
        EnergyEventExtractorV eee = new EnergyEventExtractorV();
        for (int i = 3; i < 5; i++) {
            eee.doADay(i);
        }
    }
}
