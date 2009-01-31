/*
 * Graph.fx
 *
 * Created on Dec 6, 2008, 2:44:27 PM
 */

package wattricalfx;

import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import wattricalfx.Env;
import wattricalfx.model.Feed;
import wattricalfx.view.GraphResizeBase;
import wattricalfx.view.GraphLine;
import wattricalfx.view.GraphBars;
import wattricalfx.view.GraphYAxis;

/**
 * @author daniel
 */
public class Graph extends CustomNode {
    def smallFont:Font = Font {
        size: 14
    }
    def axisColor = Color.LIGHTGRAY;
    def leftSpace = 40;   // room for yTicks
    def rightSpace = 20;
    def topSpace = 40;    // room for title
    def bottomSpace = 50; // room for status+xTicks
    def xLeft = leftSpace;
    def xWidth = bind {
        env.screenWidth - leftSpace - rightSpace };
    def yBottom= bind {
        env.screenHeight - bottomSpace };
    def yHeight= bind {
        env.screenHeight - bottomSpace - topSpace };

    
    public-init var env: Env;
    public var feed:Feed on replace {
        println("replacing feed with {feed.name}");
        graphLineOrBar =  makeNewGraphLineOrBar();
        graphYAxis = makeNewGraphYAxis();
    };


    var graphLineOrBar:Node;
    var graphYAxis:GraphYAxis;

    function useBarForFeedName(name:String) {
        if ("Month".equals(name)) return true;
        if ("Week".equals(name)) return true;
        if ("Day".equals(name)) return true;
        return false;
    }
    
    function makeNewGraphLineOrBar():Node {
        if  (useBarForFeedName(feed.name)) {
            return GraphBars {
                feed: feed
                xLeft: bind xLeft
                xWidth: bind xWidth
                yBottom: bind yBottom
                yHeight:bind yHeight
            };
        } else {
            return GraphLine {
                feed: feed
                xLeft: bind xLeft
                xWidth: bind xWidth
                yBottom: bind yBottom
                yHeight:bind yHeight
            };
        }
    }
    function makeNewGraphYAxis() {
        return GraphYAxis {
            smallFont:smallFont
            axisColor:axisColor
            feed: feed
            xLeft: bind xLeft
            xWidth: bind xWidth
            yBottom: bind yBottom
            yHeight:bind yHeight
        };
    }

    
    public override function create():
    Node {
        return Group {
            content: [
                Text {
                    font: smallFont
                    fill: Color.LIGHTGRAY
                    x: bind env.screenWidth - 100;
                    y: 20;
                    content: bind "{feed.name}"
                    //content: bind "|{feed.name}|={sizeof(feed.observations)}"
                    //content: bind "|{feed.name}|<{yTickRange}<{yTickRange2/2.0}<{feed.maxValue}"
                },
                Group{
                    content:bind graphLineOrBar},
                Group{
                    content:bind graphYAxis},
            ]
        };
    }

    init {
        //println("Hello FX Graph");
    }

}

