/*
 * RoundPanel.fx
 *
 * Created on Dec 27, 2008, 4:07:46 PM
 */

package wattricalfx.view;

import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author daniel
 */

public class RoundPanel extends CustomNode {
    public var value:String;
    public-init var units:String;
    public-init var scope:String;

    def width = 170;
    def height = 40;
    def arc = 15;
    var strokeAlpha = 0.3;
    var scale=1.0;

    def fade = Timeline {
        keyFrames: [
            at(0s) { strokeAlpha => 0.3 tween Interpolator.LINEAR }
            at(1s) { strokeAlpha => 1.0 tween Interpolator.LINEAR }
        ]
    };


    def anontateColor = Color.BLACK;
    var unitText = Text{
        opacity: .5
        font: Font{
            size: 16
        }
        y:16
        content: bind units
        fill: anontateColor
    };
    var scopeText = Text{
        opacity: .5
        font: Font{
            size: 14
        }
        y:16
        content: bind scope
        fill: anontateColor
    };

    override function create():Node {
        println("unitText = {unitText.layoutBounds}");
        unitText.translateX = width - unitText.layoutBounds.width - 5;
        unitText.translateY = 0*(height - unitText.layoutBounds.height) +2;
        scopeText.translateX = width - scopeText.layoutBounds.width - 5;
        scopeText.translateY = (height - scopeText.layoutBounds.height) -3;
        return Group {
            content: [
                Rectangle {
                    fill: Color.DARKGRAY
                    opacity: bind .2 +strokeAlpha/2.0
                    width: width
                    height: height
                    arcWidth: arc
                    arcHeight: arc
                },
                Rectangle {
                    //stroke:Color.WHITE
                    stroke: bind Color{
                        red: 1,
                        green: 1,
                        blue: 1,
                        opacity: strokeAlpha
                    }
                    strokeWidth: 2
                    fill: Color.rgb(1,1,1,0)
                    width: width
                    height: height
                    arcWidth: arc
                    arcHeight: arc
                    onMouseEntered: function(e) {
                        fade.rate = 10.0;
                        fade.play();
                    }
                    onMouseExited: function(e) {
                        fade.rate = -10.0;
                        fade.play();
                    }

                },
                Text{
                    //opacity: .5
                    opacity: bind .5 +strokeAlpha/2.0
                    font: Font{
                        size: 24
                    }
                    x: 20
                    y: 30
                    content: bind value
                    fill: Color.WHITE
                },
                unitText,
                scopeText,
            ]
        }
    }
}
