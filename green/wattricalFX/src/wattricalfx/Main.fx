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
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import wattricalfx.Env;
import wattricalfx.Graph;
import wattricalfx.model.Feed;
import wattricalfx.model.Observation;
import wattricalfx.parser.ObsFeedParser;
import wattricalfx.view.PlayPause;
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
    name:"Splash"
    scopeId:-1
    stamp: new Date()
    value:1000;
}

def now =
new Date().getTime();
for (t in [0..300 step 2]) {
    var rnd:Random = new Random();
    //var v = ( Math.sin(t  /  300.0 * 4  *  Math.PI) + 1) / 2 * 2000 + rnd.nextInt(200);
    var v = ( Math.sin(t  /  300.0 * 8  *  Math.PI) + 1) / 2 * 4000;
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

var wattStr = "----";
var kWhStr = "----";
var kWhMoStr = "----";

var powerGroup = Group {
    content: [
        RoundPanel { 
            value: bind wattStr
            units:"W"
            scope:"Live"
            onClickAction: function() {
                playPause.play=false;
                flipper.selectFeed(0);
            }
        }
        RoundPanel {
            value: bind kWhStr
            units: "kWh/d"
            scope: "Day"
            translateY: 60
            onClickAction: function() {
                playPause.play=false;
                flipper.selectFeed(2);
            }
        }
        RoundPanel {
            value: bind kWhMoStr
            units: "kWh/d"
            scope:"Month"
            translateY: 120
            onClickAction: function() {
                playPause.play=false;
                flipper.selectFeed(4);
            }
        }
    ]
    translateX:60
    translateY:70
}



var graph:Graph;
var statusText:Text;
var playPause:PlayPause;

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
    scene:
    scene = Scene {
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
                y: bind trackingEnv.screenHeight - 5
                textOrigin:TextOrigin.BOTTOM
                content: "Status"},

            playPause = PlayPause {
                play:true
                transforms: bind [
                    Transform.translate(trackingEnv.screenWidth - 35,trackingEnv.screenHeight - 35),
                    Transform.scale(0.7,0.7),
                ]
            }
        ]
        fill:
        if (false) Color.BLACK else
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

var flipper:Flipper = Flipper {
    graph:graph
}
flipper.timer.play();

var watcher:Watcher = Watcher{
    flipper:flipper
};
watcher.timer.play();

/* This class controls the displayed feed graph
 * selection
 * animation
 */
class Flipper {
    var graph:Graph;
    var whichFeed=0;
    var feeds:Feed[] on replace {
        updateFeeds();
    };
    init {
        whichFeed=0;
        feeds=[];  // array of feeds
    }

    function updateDynamicRefresh(name) {
        var desired=2.0;
        if ("Live".equals(name)) {
            desired=2.0;
        } else
        if ("Hour".equals(name)) {
            desired=10.0;
        } else { // Day/Week/Month,...
            desired=60.0;
        }
        if (watcher.dynamicExpirySeconds != desired) {
            println("  Flipper update: {name} refresh {watcher.dynamicExpirySeconds}->{desired}");
            watcher.dynamicExpirySeconds = desired;
        } else {
            println("  Flipper update: {name} refresh {watcher.dynamicExpirySeconds}");
        }
    }
    function updateFeeds() {
        if ((sizeof feeds) == 0) {
            return;
        }
        if (whichFeed < 0)
        whichFeed=0;
        whichFeed = ( whichFeed ) mod (sizeof feeds);
        var newFeed = feeds[whichFeed];
        updateDynamicRefresh(newFeed.name);
        graph.feed = newFeed;
    }
    function selectFeed(selected:Integer) {
        whichFeed = selected;
        updateFeeds();
    }
    function nextFeed() {
        println("Flipper nextFeed: {new Date()}");
        selectFeed(whichFeed + 1);
    }
    
    public var timer : Timeline = Timeline {
        repeatCount: Timeline.INDEFINITE
        keyFrames: KeyFrame {
            time: 5s
            canSkip:true
            action: function() {
                if (playPause.play) {
                    nextFeed();
                }
            }
        },
    };

}

/* This class polls the feeds
 * it is run on a tight loop (1s) but only refreshes the feed
 * if the expiry for that feed has elapsed...
 *  it set the power strings,status,..
 */

class Watcher {
    var flipper:Flipper; // where to push the feeds
    def minRefreshSeconds:Number = 2.0;
    // flipper tells us the refresh rate for it feed
    var dynamicExpirySeconds:Number=2.0;
    def parser:ObsFeedParser = ObsFeedParser {
        feedLocation: env.feedLocation
    }
    var lastRefreshed:Date;

    function isExpired():Boolean {
        if (lastRefreshed == null) {
            println("  Watcher:No current feed.");
            return true;
        }
        var now = new Date();
        var secondsOld = (now.getTime() - lastRefreshed.getTime()) / 1000.0;
        if (secondsOld < minRefreshSeconds or secondsOld < dynamicExpirySeconds) {
            //println("  Watcher:Current feeds still valid: {secondsOld} seconds old");
            return false;
        }
        return true;
    }
    function parseAndPush() {
        if (not isExpired()) {
            return;
        }
        parser.parseURL();
        if (parser.parsedFeeds != null) {
            lastRefreshed=new Date();
            println("  Feed updated: {lastRefreshed}");

            //println("  LOCAL : {parser.parsedFeeds[0].isoStamp}");
            //println("  GMT   : {parser.parsedFeeds[0].isoGMT()}");
            var stamp = parser.parsedFeeds[0].isoStamp; //isoGMT()
            var now = new Date();
            var latency = (now.getTime() - parser.parsedFeeds[0].stamp.getTime()) / 1000.0;
            //statusText.content = "{stamp} ({-latency}s.)  {parser.parsedFeeds[0].value} W  {parser.parsedFeeds[2].value*24.0/1000} kWh/d";
            statusText.content = "{stamp} ({latency}s.)";
            wattStr = "{parser.parsedFeeds[0].value}";
            kWhStr = "{parser.parsedFeeds[2].value*24.0/1000}";
            kWhMoStr = "{parser.parsedFeeds[4].value*24.0/1000}";
            flipper.feeds = parser.parsedFeeds;
        }
    }
    public var timer : Timeline = Timeline {
        repeatCount: Timeline.INDEFINITE
        keyFrames: KeyFrame {
            time: 1s
            canSkip:true
            action: function() {
                //println("Watcher timeline: {new Date()}");
                parseAndPush();
            }
        },
    };

}
