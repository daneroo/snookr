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

/**
 * @author daniel
 */

Stage {
    title: "Wattrical FX"
    width: 250
    height: 80
    scene: Scene {
        content: Text {
            font : Font {
                size : 24
            }
            x: 10, y: 30
            content: "iMetrical - Wattrical"
        }
    }
}