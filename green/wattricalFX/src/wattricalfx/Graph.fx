/*
 * Graph.fx
 *
 * Created on Dec 6, 2008, 2:44:27 PM
 */

package wattricalfx;

import java.lang.Math;
import java.lang.System;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import wattricalfx.parser.ObsFeedParser;

/**
 * @author daniel
 */
public class Graph extends CustomNode {

    public override function create(): Node {
        return Group {
            content: [
                Path {
                    elements: [
                        MoveTo {
                            x: 0,
                            y: 100},
                        for (t in [10..
                            480 - 10 step 2])
                        LineTo{
                            x: t,
                            y: 50 + (Math.sin(t  /  400.0  *  4  *  Math.PI) + 1) * 100 + Math.random() * 50,
                        },
                    ] // elements
                    stroke: Color.WHITE
                }

            ]
            onMouseClicked: showBounds;
        };
    }

    function showBounds(e:javafx.scene.input.MouseEvent){
        //def b:Number = 123.45;
        def b = boundsInParent.height;
        System.out.println("Bounds: {b.toString()}");
    }

    public function invokeParser():Void {
        System.out.println("Gonna Parse");
        def parser = ObsFeedParser {
        }

        parser.parseURL();
    }
    init {
        //showBounds(null);
        System.out.println("Hello FX Graph");
    }

}

