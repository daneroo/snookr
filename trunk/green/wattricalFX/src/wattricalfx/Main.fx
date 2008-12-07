/*
 * Main.fx
 *
 * Created on Dec 7, 2008, 1:10:20 PM
 */

package wattricalfx;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.Color;

/**
 * @author daniel
 */

Stage {
    title: "Wattrical FX"
    width: 480
    height: 320
    scene: Scene {
        content: Graph {
        }
        fill: LinearGradient {
            startX: 0.0,
            startY: 0.0,
            endX: 0.0,
            endY: 1.0,
            proportional: true
            stops: [
                Stop {
                    offset: 0.0
                    color: Color.BLACK},
                Stop {
                    offset: 1.0
                    color: Color.GREEN}
            ]
        }
    }
}