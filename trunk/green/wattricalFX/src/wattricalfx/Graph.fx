/*
 * Graph.fx
 *
 * Created on Dec 6, 2008, 2:44:27 PM
 */

package wattricalfx;

import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Path;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.paint.Color;
import java.lang.Math;
import java.lang.System;


/**
 * @author daniel
 */
public class Graph extends CustomNode {

    public override function create(): Node {
        return Group {
            content: [
                Text {
                    font: Font {
                        size: 24
                    }
                    x: 10,
                    y: 30
                    content: "iMetrical - Wattrical"
                },
                Text {
                    font: Font {
                        size: 24
                    }
                    x: 10,
                    y: 50
                    content: "100"
                }
                Path {
                    elements: [
                        MoveTo {
                            x: 0,
                        y: 0},
                        for (t in [10..480-10 step 10])
                        LineTo{
                            x: t,
                            y: Math.random() * 320,
                        },
                    ] // elements
                    stroke: Color.WHITE
                }

            ]
            onMouseClicked : showBounds;
        };
    }

    function showBounds(e:javafx.scene.input.MouseEvent){
        //def b:Number = 123.45;
        def b = boundsInParent.height;
        System.out.println("Bounds: {b.toString()}");
    }

    init {
        showBounds(null);
        System.out.println("Hello FX Graph");
    }

}

