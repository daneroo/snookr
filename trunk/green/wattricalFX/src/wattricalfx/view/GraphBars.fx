/*
 * GraphBars.fx
 *
 * Created on Jan 31, 2009, 3:33:34 PM
 */

package wattricalfx.view;

import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;
import wattricalfx.model.Feed;
import wattricalfx.view.GraphResizeBase;

/**
 * @author daniel
 */

public class GraphBars extends GraphResizeBase {
    def barWidth = bind xWidth * 0.8 / sizeof(feed.observations);
    def minX = xFromStamp(feed.minStamp);
    def lastIndex=sizeof(feed.observations) - 1;
    def fillColor = Color.LIGHTGRAY;
    def strokeColor = Color.WHITE;
    var rectOpacity=0.7;

    // Don't forget these a re time reversed.
    def rectSeq =  bind {
        [
            Rectangle{ // LAST on RIGHT: maxStamp
                x: bind xFromStamp(feed.observations[0].stamp) - barWidth / 2.0
                y: bind yFromValue(feed.observations[0].value)
                width:bind barWidth / 2.0
                height: bind yBottom - yFromValue(feed.observations[0].value)
                fill: fillColor
                stroke:strokeColor
                opacity:rectOpacity
                    onMouseEntered: function(e) {
                        e.node.opacity=1.0;
                        axisText="{feed.observations[0].stamp} - {feed.observations[0].value}W";
                    }
                    onMouseExited: function(e) {
                        e.node.opacity=rectOpacity;
                        axisText="";
                    }
            },
            for (index in [1..<lastIndex]) {
                Rectangle{
                    x: bind xFromStamp(feed.observations[index].stamp) - barWidth / 2.0
                    y: bind yFromValue(feed.observations[index].value)
                    width:bind barWidth
                    height: bind yBottom - yFromValue(feed.observations[index].value)
                    fill: fillColor
                    stroke:strokeColor
                    opacity:rectOpacity
                    onMouseEntered: function(e) {
                        e.node.opacity=1.0;
                        axisText="{feed.observations[index].stamp} - {feed.observations[index].value}W";
                    }
                    onMouseExited: function(e) {
                        e.node.opacity=rectOpacity;
                        axisText="";
                    }

                }
            },
            Rectangle{ // First on Left minStamp
                x: bind xLeft
                y: bind yFromValue(feed.observations[lastIndex].value)
                width:bind barWidth / 2.0
                height: bind yBottom - yFromValue(feed.observations[lastIndex].value)
                fill: fillColor
                stroke:strokeColor
                opacity:rectOpacity
                    onMouseEntered: function(e) {
                        e.node.opacity=1.0;
                        axisText="{feed.observations[lastIndex].stamp} - {feed.observations[lastIndex].value}W";
                    }
                    onMouseExited: function(e) {
                        e.node.opacity=rectOpacity;
                        axisText="";
                    }
            }
        ];
    }


    var axisText = "";//"STAMP - VALUE";
    def xAxisLabel = Text {  // units
        x:100
        y: bind yBottom + 4
        content: bind axisText
        fill: Color.WHITE
        font: Font{
            size:14
        }
        textOrigin: TextOrigin.TOP;
    };

    public override function create(): Node {
        return Group {
            content: [
                rectSeq,
                xAxisLabel,
            ];
        };
    }
}