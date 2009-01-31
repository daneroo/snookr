/*
 * GraphYAxis.fx
 *
 * Created on Jan 31, 2009, 2:46:39 PM
 */

package wattricalfx.view;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author daniel
 */

public class GraphYAxis extends GraphResizeBase {
    public-init var axisColor = Color.LIGHTGRAY;
    public-init var smallFont:Font;

    def topSpace = yFromValue(feed.maxValue);
    // 0 .. 99 kW - 4 Labels no 0 < 2.5
    def yTickRange:Integer = bind (feed.maxValue + 1000 - 1) / 1000 - 1;
    def yTickRange2:Integer = bind (feed.maxValue + 500 - 1) / 500 - 1;
    def halfLine = 5;
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
            y: bind yFromValue(yTickRange * 1000) + halfLine
            content: bind "{yTickRange}"
            //y: bind yFromValue(yTickRange2 * 500)
            //content: bind "{yTickRange2/2.0} kW"
            fill: axisColor
            font: smallFont
        }
        Text {
            x:20
            y: bind yFromValue(1000) + halfLine
            content: "1"
            fill: axisColor
            font: smallFont
        }
    ];
    def xAxis =  Line{
        startX: xLeft
        startY: bind yBottom
        endX: bind xLeft + xWidth
        endY: bind yBottom
        strokeWidth: 1
        stroke: axisColor;
    };

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

    public override function create(): Node {
        return Group {
            content: [
                Group{
                    content:yLabels},
                yAxis,
                yTicks,
                xAxis,

            ]
        };
    }

}
