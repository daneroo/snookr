/*
 * PlayPause.fx
 *
 * Created on Feb 1, 2009, 11:42:06 AM
 */

package wattricalfx.view;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Glow;
import javafx.scene.effect.light.DistantLight;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.CustomNode;
import javafx.scene.Node;

/**
 * @author daniel
 *  make a button a 0,0 size 40,40
 */
public class PlayPause extends CustomNode {
    public-init var play: Boolean = false;

    public override function create(): Node {
        return playPauseGrp;
    }


    var glowEffect = Glow{};
    var dropEffect = DropShadow {
        offsetX:5
        offsetY:5
        color: Color.TRANSPARENT
    };
                     

    var playPauseGrp:Node = Group{
        cursor: Cursor.HAND
        effect: glowEffect

        content: [
            Rectangle {
                arcHeight:6
                arcWidth:6
                x: 0
                y: 0
                width: 40
                height: 40
                opacity: 0.5
                //stroke: Color.LIGHTGRAY
                fill: LinearGradient {
                    startX:0
                    startY:0
                    endX:1
                    endY:1
                    stops: [
                        Stop {
                            offset:0
                            color: Color.GRAY },
                        Stop {
                            offset:1
                            color: Color.DARKGRAY },
                    ]
                }
                effect: dropEffect
            },

            Path{
                visible: bind not play;
                fill: Color.BLACK
                elements: [
                    MoveTo {
                        x: 12
                        y: 30 },
                    LineTo {
                        x: 28
                        y: 20 },
                    LineTo {
                        x: 12
                        y: 10 }
                ]
                effect: Lighting{
                    light: DistantLight{
                        azimuth: 90
                    }
                }
            },

            Rectangle {
                visible: bind play;
                x: 12
                y: 10
                width: 6
                height: 20
                stroke: Color.BLACK
                fill: Color.BLACK
                effect: Lighting{
                    light: DistantLight{
                        azimuth: 90
                    }
                }
            },

            Rectangle {
                visible: bind play;
                x: 22
                y: 10
                width: 6
                height: 20
                stroke: Color.BLACK
                fill: Color.BLACK
                effect: Lighting{
                    light: DistantLight{
                        azimuth: 90
                    }
                }
            },
        ]

        onMouseEntered: function(evt: MouseEvent):Void {
            glowEffect.level = 0.65;
            dropEffect.color = Color.LIGHTGRAY
        }

        onMouseClicked: function(evt: MouseEvent):Void {
            if(evt.button == MouseButton.PRIMARY) {
                play = not play;
            }
        }

        onMousePressed: function(evt: MouseEvent):Void {
            if(evt.button == MouseButton.PRIMARY) {
                glowEffect.level = 0.0;
                dropEffect.color = Color.TRANSPARENT
            }
        }

        onMouseReleased: function(evt: MouseEvent):Void {
            glowEffect.level = 0.65;
            dropEffect.color = Color.LIGHTGRAY
        }

        onMouseExited: function(evt: MouseEvent):Void {
            glowEffect.level = 0.3;
            dropEffect.color = Color.TRANSPARENT
        }
    };
}
