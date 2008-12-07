/*
 * Observation.fx
 *
 * Created on Dec 7, 2008, 3:23:45 PM
 */

package wattricalfx.model;

/**
 * @author daniel
 */

public class Observation {
    public var stamp: String;
    public var value: Integer;

    public override function toString() : java.lang.String{
        return "obs [ stamp:{stamp} value={value}]";
    }
}
