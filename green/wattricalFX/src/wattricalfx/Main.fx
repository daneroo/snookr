/*
 * Main.fx
 *
 * Created on Dec 7, 2008, 1:10:20 PM
 */

package wattricalfx;

import java.lang.Long;
import java.util.Date;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wattricalfx.Graph;

/**
 * @author daniel
 */

var graph:Graph =  Graph {}

Stage {
    title: "Wattrical FX"
    width: 480
    height: 320
    scene: Scene {
        content: [
            graph,
            Text {
                font: Font {
                    size: 24
                }
                x: 10,
                y: 50
                content: "iMetrical - Wattrical"
            },
            Text {
                font: Font {
                    size: 24
                }
                x: 10,
                y: 80
                content: "100"
            }

        ]
        fill: LinearGradient {
            startX: 0.0,
            startY: 0.0,
            endX: 0.0,
            endY: 1.0,
            proportional: true
            stops: [
                Stop {
                    offset: 0.0
                color: Color.WHITE},
                Stop {
                    offset: 1.0
                color: Color.GREEN}
            ]
        }
    }
}


var watcher:Watcher = Watcher{
    graph:graph};
watcher.timer.play();

class Watcher {
    var secs:Long;
    var graph:Graph;
    public var timer : Timeline = Timeline {
        repeatCount: Timeline.INDEFINITE
        keyFrames: KeyFrame {
            time: 3s
            canSkip:true
            action: function() {
                var now:Date = new Date();
                secs=now.getTime();
                graph.invokeParser();
                println("Disconected timer: {now}");
            }
        },
    };

}
