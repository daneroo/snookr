/*
 * Observation.fx
 *
 * Created on Dec 7, 2008, 3:23:45 PM
 */

package wattricalfx.model;
import java.util.Date;

/**
 * @author daniel
 */

public class Observation {
    public var stamp: Date;
    public var value: Integer;

    public override function toString() : String {
        return "obs [ stamp:{stamp} value={value}]";
    }
}
