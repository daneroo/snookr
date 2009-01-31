/*
 * GraphResizeBase.fx
 *
 * Created on Jan 31, 2009, 2:39:09 PM
 */

package wattricalfx.view;
import java.util.Date;
import javafx.scene.CustomNode;
import wattricalfx.DateRange;
import wattricalfx.model.Feed;

/**
 * @author daniel
 */

abstract public class GraphResizeBase extends CustomNode {
    public-init var feed:Feed;
    public var xLeft:Number;
    public var xWidth:Number;
    public var yBottom:Number;
    public var yHeight:Number;

    protected bound function xFromStamp(stamp:Date){
        var normalized = DateRange.normalize(stamp, feed.minStamp, feed.maxStamp);
        var x = xLeft + xWidth * normalized;
        //println("s:{stamp} x:{x} min:{feed.minStamp} max:{feed.maxStamp}  width: {layoutBounds.width}");
        return x;
    }
    protected bound function yFromValue(value:Integer){
        var normalized = (value - feed.minValue) * 1.0 / feed.rangeValue;
        var y = yBottom -   yHeight * normalized;
        //println("v:{value} y:{y} min:{feed.minValue} max:{feed.maxValue} r:{feed.rangeValue} height: {layoutBounds.height}");
        return y;
    }

}
