/*
 * Graph.fx
 *
 * Created on Dec 6, 2008, 2:44:27 PM
 */

package wattricalfx;

import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import wattricalfx.model.Feed;

/**
 * @author daniel
 */
public class Graph extends CustomNode {
    public-init var env: Env;
    public var feed:Feed;
    def movetoseq = bind
    if (sizeof(feed.observations) > 0) MoveTo {
        x: xFromStamp(feed.observations[0].stamp)
        y: yFromValue(feed.observations[0].value)
    } else null;

    def yBottom=250.0;
    def yHeight=200.0;
    def xLeft = 10;
    def xWidth= env.screenWidth-2*xLeft;

    function xFromStamp(stamp:Date){
        var normalized = DateRange.normalize(stamp, feed.minStamp, feed.maxStamp);
        var x = xLeft + xWidth * normalized;
        //println("s:{stamp} x:{x} min:{feed.minStamp} max:{feed.maxStamp}  width: {layoutBounds.width}");
        return x;
    }
    function yFromValue(value:Integer){
        var normalized = (value - feed.minValue) * 1.0 / feed.rangeValue;
        var y = yBottom -   yHeight * normalized;
        //println("v:{value} y:{y} min:{feed.minValue} max:{feed.maxValue} r:{feed.rangeValue} height: {layoutBounds.height}");
        return y;
    }

    def linetoseq = bind {
        for (index in [0..<sizeof(feed.observations)]) {
            LineTo{
                //x:450 - index * 400.0 / sizeof(feed.observations)
                x: xFromStamp(feed.observations[index].stamp)
                // y = 50+ 250 (1- val in 0..1)
                //y: 200.0 + 150.0 * (1.0 - ((feed.observations[index].value) - feed.minValue) * 1.0) / feed.rangeValue;
                y: yFromValue(feed.observations[index].value)
            };
        }
    }
    def borderoo = [
        MoveTo{
            x:xLeft
            y:yBottom},
        LineTo{
            x:xLeft
            y:yBottom - yHeight},
        LineTo{
            x:xLeft + xWidth
            y:yBottom - yHeight},
        LineTo{
            x:xLeft + xWidth
            y:yBottom},
        LineTo{
            x:xLeft
            y:yBottom},
    ];

    public override function create(): Node {
        return Group {
            content: [
                Path {
                    elements: borderoo
                    stroke: Color.GRAY
                    strokeWidth: 1;
                },
                Path {
                    elements: bind [
                        movetoseq,
                        linetoseq
                    ] // elements
                    stroke: Color.WHITE
                    strokeWidth: 2;
                },
                Text {
                    font: Font {
                        size: 14
                    }
                    fill: Color.LIGHTGRAY
                    x: env.screenWidth - 70;
                    y: 20;

                    content: bind "Note {sizeof(feed.observations)}"
                }

            ]
            onMouseClicked: showBounds;
        };
    }

    function showBounds(e:javafx.scene.input.MouseEvent){
        println("Bounds: {boundsInParent.height.toString()}");
    }

    init {
        println("Hello FX Graph");
    }

}

