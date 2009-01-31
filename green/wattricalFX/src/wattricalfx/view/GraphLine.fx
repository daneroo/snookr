/*
 * GraphLine.fx
 *
 * Created on Jan 31, 2009, 1:52:05 PM
 */

package wattricalfx.view;

import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import wattricalfx.DateRange;
import wattricalfx.model.Feed;

/**
 * @author daniel
 */

public class GraphLine extends GraphResizeBase {

    def movetoseq = bind
    if (sizeof(feed.observations) > 0) MoveTo {
        x: xFromStamp(feed.observations[0].stamp)
        y: yFromValue(feed.observations[0].value)
    } else null;

    def linetoseq =  bind {
        for (index in [0..<sizeof(feed.observations)]) {
            LineTo{
                x: xFromStamp(feed.observations[index].stamp)
                y: yFromValue(feed.observations[index].value)
            };
        }
    }

    public override function create(): Node {
        return Group {
            content: [
                Path {
                    elements: bind [
                        movetoseq,
                        linetoseq
                    ] // elements
                    stroke: Color.WHITE
                    strokeWidth: 2;
                },

            ]
        };
    }
}