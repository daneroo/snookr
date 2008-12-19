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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import wattricalfx.model.Feed;

/**
 * @author daniel
 */
public class Graph extends CustomNode {
    public var feed:Feed;
    def movetoseq = bind
    if (sizeof(feed.observations) > 0) MoveTo {
        x: 450,
        y: 160
    } else null;

    def linetoseq = bind {
        for (index in [0..<sizeof(feed.observations)]) {
            LineTo{
                x:450 - index * 400.0 / sizeof(feed.observations)
                // y = 50+ 250 (1- val in 0..1)
                y: 200.0 + 150.0 * (1.0 - ((feed.observations[index].value) - feed.minValue) * 1.0) / feed.rangeValue;
            };
        }
    }
    public override function create(): Node {
        return Group {
            content: bind [
                Path {
                    elements: [
                        movetoseq,
                        linetoseq
                    ] // elements
                    stroke: Color.RED
                },
                Text {
                    font: Font {
                        size: 12
                    }
                    x: 300,
                    y: 200
                    content: "Note {sizeof(feed.observations)}"
                }

            ]
            onMouseClicked: showBounds;
        };
    }

    function showBounds(e:javafx.scene.input.MouseEvent){
        System.out.println("Bounds: {boundsInParent.height.toString()}");
    }

    init {
        System.out.println("Hello FX Graph");
    }

}

