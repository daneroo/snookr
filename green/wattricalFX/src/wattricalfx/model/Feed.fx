/*
 * Feed.fx
 *
 * Created on Dec 7, 2008, 3:25:14 PM
 */

package wattricalfx.model;

import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import wattricalfx.model.Observation;

/**
 * @author daniel
 */

public class Feed {
    public var name: String;
    public var scopeId: Integer;
    public var stamp: Date; // should be date
    public var value: Integer;
    public var observations: Observation[]=[] on replace oldValue[firstIdx .. lastIdx] = newElements {
        //println("replaced {oldValue}[{firstIdx}..{lastIdx}] by {newElements} yielding {observations}")
        if (sizeof(newElements) > 0) { // only adding
            // re-initialize if first observation
            checkRange(newElements,(sizeof(observations) == 1));
        } else {
            checkRange(observations,true);
        }
    };

    def sdf:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'Z");
    var sdfGMT:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'Z");
    init {
        /*var ids:String[] = TimeZone.getAvailableIDs(0);
        for (i in ids){
         println("id is {i}");
         }*/
        def gmt:SimpleTimeZone = new SimpleTimeZone(0,"GMT");
        def cal:GregorianCalendar = new GregorianCalendar();
        cal.setTimeZone(gmt);
        sdfGMT.setCalendar(cal);
        //println("sdfGMT intialized:");
        //println("  sdf    : {sdf.format(new Date())}");
        //println("  sdfGMT : {sdfGMT.format(new Date())}");
    }

    public function isoGMT(): String{
        //println("  sdf    : {sdf.format(new Date())}");
        //println("  sdfGMT : {sdfGMT.format(new Date())}");
        return
        sdfGMT.format(stamp);
    }
    public-read var isoStamp = bind sdf.format(stamp);
    // this does'nt work for some reason ??? use the function above instead
    //public-read var isoStampGMT = bind sdfGMT.format(stamp);
    public-read var minValue:Integer=0;
    public-read var maxValue:Integer=0;
    public-read var rangeValue:Integer=0;
    public-read var minStamp:Date = null;
    public-read var maxStamp:Date = null;

    public override function toString() : String {
        return "feed ({name}) [ stamp:{stamp} value:{value}] |obs|=[{sizeof observations} min:{minValue} max:{maxValue}";
    }

    function checkRange(tocheck:Observation[], reInit: Boolean){
        if (reInit) {
            minValue = 0;
            maxValue = 0;
            minStamp = null;
            maxStamp = null;
            if (sizeof(tocheck) > 0) {
                minValue =
                maxValue = tocheck[0].value;
                minStamp =
                maxStamp = tocheck[0].stamp;
            }
        }
        for (obs in tocheck) {
            minValue = Math.min(minValue, obs.value);
            maxValue = Math.max(maxValue, obs.value);
            var stamp:Date = obs.stamp;
            if (stamp.getTime()<minStamp.getTime()) {
                minStamp=stamp;
            }
            if (stamp.getTime()>maxStamp.getTime()) {
                maxStamp=stamp;
            }
        }
        // Override  min to 0
        minValue=0;
        rangeValue= maxValue - minValue;
    }
}
