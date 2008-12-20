/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wattricalfx;

import java.util.Date;

/**
 *
 * @author daniel
 */
public class DateRange {
   public static double normalize(Date stamp,Date minStamp,Date maxStamp){
       try {
       long mn = minStamp.getTime();
       long mx = maxStamp.getTime();
       long range = mx-mn;
       double normalized = (stamp.getTime()-mn)*1.0/range;
       return normalized;
       } catch (Exception e) {
           return 0;
       }
   }
}
