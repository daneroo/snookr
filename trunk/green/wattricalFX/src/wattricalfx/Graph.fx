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
import javafx.scene.shape.Line;

/**
 * @author daniel
 */
public class Graph extends CustomNode {
    public-init var env: Env;
    public var feed:Feed;

    def smallFont:Font = Font {
        size: 14
    }
    def leftSpace = 40;   // room for yTicks
    def rightSpace = 20;
    def topSpace = 40;    // room for title
    def bottomSpace = 50; // room for status+xTicks

    def xLeft = leftSpace;
    def xWidth = bind {
        env.screenWidth - leftSpace - rightSpace };
    def yBottom= bind {
        env.screenHeight - bottomSpace };
    def yHeight= bind {
        env.screenHeight - bottomSpace - topSpace };

    bound function xFromStamp(stamp:Date){
        var normalized = DateRange.normalize(stamp, feed.minStamp, feed.maxStamp);
        var x = xLeft + xWidth * normalized;
        //println("s:{stamp} x:{x} min:{feed.minStamp} max:{feed.maxStamp}  width: {layoutBounds.width}");
        return x;
    }
    bound function yFromValue(value:Integer){
        var normalized = (value - feed.minValue) * 1.0 / feed.rangeValue;
        var y = yBottom -   yHeight * normalized;
        //println("v:{value} y:{y} min:{feed.minValue} max:{feed.maxValue} r:{feed.rangeValue} height: {layoutBounds.height}");
        return y;
    }

    def movetoseq = bind
    if (sizeof(feed.observations) > 0) MoveTo {
        x: xFromStamp(feed.observations[0].stamp)
        y: yFromValue(feed.observations[0].value)
    } else null;

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

    /* Borderoo
    Path {
     elements: borderoo
     stroke: Color.GRAY
     strokeWidth: 1;
     },

     def borderoo = [
     MoveTo{
     x:xLeft
     y:bind yBottom},
     LineTo{
     x:xLeft
     y:bind{
     yBottom - yHeight
     }},
     LineTo{
     x:bind{
     xLeft + xWidth
     }
     y:bind{
     yBottom - yHeight
     }},
     LineTo{
     x:bind {
     xLeft + xWidth
     }
     y:bind { yBottom
     }},
     LineTo{
     x:xLeft
     y:bind yBottom},
     ];
     */

     // 0 .. 99 kW - 4 Labels no 0 < 2.5
    def yTickRange:Integer = bind (feed.maxValue + 1000 - 1) / 1000 - 1;
    def yTickRange2:Integer = bind (feed.maxValue + 500 - 1) / 500 - 1;
    def halfLine = 7;
    def axisColor = Color.LIGHTGRAY;
    def yLabels = [
            Text {  // units
            x:10
            y: topSpace - 15
            content: "kW"
            fill: axisColor
            font: smallFont
        },
        Text {
            x:20
            y: bind yFromValue(yTickRange * 1000)+halfLine
            content: bind "{yTickRange}"
            //y: bind yFromValue(yTickRange2 * 500)
            //content: bind "{yTickRange2/2.0} kW"
            fill: axisColor
            font: smallFont
        }
        Text {
            x:20
            y: bind yFromValue(1000)+halfLine
            content: "1"
            fill: axisColor
            font: smallFont
        }
    ];
    def yAxis =  Line{
        startX: xLeft
        startY: topSpace
        endX: xLeft
        endY: bind yBottom
        strokeWidth: 1
        stroke: axisColor
    };
    def yTicks = bind
    for (tck in [1..yTickRange2]) {
        Line {
            startX: xLeft,
            startY: bind yFromValue(tck * 500)
            endX: xLeft - 3,
            endY: bind yFromValue(tck * 500)
            strokeWidth: 1
            stroke: axisColor
        }
    };
    def xAxis =  Line{
        startX: xLeft
        startY: bind yBottom
        endX: bind xLeft + xWidth
        endY: bind yBottom
        strokeWidth: 1
        stroke: axisColor
    };


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
                Text {
                    font: smallFont
                    fill: Color.LIGHTGRAY
                    x: env.screenWidth - 200;
                    y: 20;
                    //content: bind "|{feed.name}|={sizeof(feed.observations)}"
                    content: bind "|{feed.name}|<{yTickRange}<{yTickRange2/2.0}<{feed.maxValue}"
                },
                Group{
                    content:yLabels},
                yAxis,
                yTicks,
                xAxis,

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

