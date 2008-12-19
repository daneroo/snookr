/*
 * Feed.fx
 *
 * Created on Dec 7, 2008, 3:25:14 PM
 */

package wattricalfx.model;

import java.lang.Math;
import java.util.Date;
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
        if (sizeof(newElements)>0) { // only adding
            // re-initialize if first observation
            checkRange(newElements,(sizeof(observations)==1));
        } else {
            checkRange(observations,true);
        }
    };

    public-read var minValue:Integer=0;
    public-read var maxValue:Integer=0;
    public-read var rangeValue:Integer=0;

    public override function toString() : String {
        return "feed ({name}) [ stamp:{stamp} value:{value}] |obs|=[{sizeof observations} min:{minValue} max:{maxValue}";
    }

    function checkRange(tocheck:Observation[], reInit: Boolean){
        if (reInit) {
            minValue = if (sizeof(tocheck) > 0) tocheck[0].value else 0;
            maxValue = if (sizeof(tocheck) > 0) tocheck[0].value else 0;
        }
        for (obs in tocheck) {
            minValue = Math.min(minValue, obs.value);
            maxValue = Math.max(maxValue, obs.value);
        }
        rangeValue= maxValue - minValue;
    }
}
