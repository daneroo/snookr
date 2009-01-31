/*
 * Main.fx
 *
 * Created on Dec 7, 2008, 1:10:20 PM
 */

package wattricalfx;

import java.lang.Long;
import java.lang.Math;
import java.util.Date;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;
import javafx.stage.Stage;
import wattricalfx.Graph;
import wattricalfx.model.Feed;
import wattricalfx.model.Observation;
import wattricalfx.parser.ObsFeedParser;
import wattricalfx.view.RoundPanel;

/**
 * @author daniel
 */

 //def env = Env{screenWidth: 320, screenHeight: 240};
def feedURL = "http://imetrical.appspot.com/feeds?owner=daniel";
//def feedURL = "http://192.168.5.2/iMetrical/feeds.php";;
//def feedURL = "http://imetrical.morphexchange.com/feeds.xml";

def env = Env{
    screenWidth: 480,
    screenHeight: 320
    feedLocation: feedURL
};

var fakeFeed:Feed = Feed {
    name:"Fake"
    scopeId:-1
    stamp: new Date()
    value:1000;
}

def now =
new Date().getTime();
for (t in [0..300 step 2]) {
    var rnd:Random = new Random();
    var v = ( Math.sin(t  /  300.0 * 4  *  Math.PI) + 1) / 2 * 2000 + rnd.nextInt(200);
    //v = t; // ramp instead
    def observation = Observation {
        stamp: new Date(
            now - t * 1000);
        value: v.intValue()
    }
    //println("  - {observation}");
    insert observation into fakeFeed.observations;
}


var titleText = Text {
    font: Font {
        size: 24
    }
    x: 150,
    y: 20
    content: "iMetrical"
    fill: Color.WHITE
};

var wattStr = "1234";
var kWhStr = "30.0";
var kWhMoStr = "30.0";

var powerGroup = Group {
    content: [
        RoundPanel { 
            value: bind wattStr
            units:"W"
            scope:"live"}
        RoundPanel {
            value: bind kWhStr
            units: "kWh/d"
            scope: "day"
            translateY: 60
        }
        RoundPanel {
            value: bind kWhMoStr
            units: "kWh/d"
            scope:"month"
            translateY: 120
        }
    ]
    translateX:30
    translateY:70
}

var graph:Graph;
var statusText:Text;
Stage {
    var scene:Scene;
    var trackingEnv = Env{
        screenWidth: bind {
            if (scene.width > 10) scene.width else 480;
        }
        screenHeight: bind {
            if (scene.height > 10) scene.height else 320;
        }
        feedLocation: feedURL
    };

    title: "Wattrical FX"
    width: env.screenWidth
    height: env.screenHeight
    scene: scene = Scene {
            content: [
                graph =  Graph {
                    env:trackingEnv
                    feed: fakeFeed
                    /*effect: Reflection {
                    fraction: 0.9
                     topOpacity: 0.5
                     topOffset: 0.3
                     }*/
                },
            titleText,
            powerGroup,
                statusText = Text {
                    font: Font {
                        size: 14
                    }
                    fill: Color.LIGHTGRAY
                    x: 10,
                    y: bind trackingEnv.screenHeight-5
                    textOrigin:TextOrigin.BOTTOM
                    content: "Status"},
            ]
            fill: if (false) Color.BLACK else
                LinearGradient {
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
                };
        }
}


var watcher:Watcher = Watcher{
    graph:graph};
watcher.timer.play();

class Watcher {
    var secs:Long;
    var graph:Graph;

    def parser:ObsFeedParser = ObsFeedParser {
        feedLocation: env.feedLocation
    }


    public var timer : Timeline = Timeline {
        repeatCount: Timeline.INDEFINITE
        keyFrames: KeyFrame {
            time: 2s
            canSkip:true
            action: function() {
                var now:Date = new Date();
                secs=now.getTime();
                parser.parseURL();
                if (parser.parsedFeeds != null) {
                    //println("  LOCAL : {parser.parsedFeeds[0].isoStamp}");
                    //println("  GMT   : {parser.parsedFeeds[0].isoGMT()}");
                    var stamp = parser.parsedFeeds[0].isoStamp; //isoGMT()
                    var latency = (
                    new Date().getTime() - parser.parsedFeeds[0].stamp.getTime()) / 1000.0;
                    //statusText.content = "{stamp} ({-latency}s.)  {parser.parsedFeeds[0].value} W  {parser.parsedFeeds[2].value*24.0/1000} kWh/d";
                    statusText.content = "{stamp} ({-latency}s.)";
                    wattStr = "{parser.parsedFeeds[0].value}";
                    kWhStr = "{parser.parsedFeeds[2].value*24.0/1000}";
                    kWhMoStr = "{parser.parsedFeeds[4].value*24.0/1000}";
                    var whichFeed = ((now.getSeconds() / 10) mod 3);
                    graph.feed = parser.parsedFeeds[whichFeed];
                    println(" selected feed:{whichFeed}");
                }
                println("Watcher timeline: {now}");
            }
        },
    };

}
