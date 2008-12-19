/*
 * Feed.fx
 *
 * Created on Dec 7, 2008, 3:25:14 PM
 */

package wattricalfx.model;

/**
 * @author daniel
 */

public class Feed {
    public var name: String;
    public var scopeId: Integer;
    public var stamp: String; // should be date
    public var value: Integer;
    public var observations: Observation[]=[];

    public override function toString() : java.lang.String{
        return "feed ({name}) [ stamp:{stamp} value={value}] |obs|=[{sizeof observations}";
    }

}
