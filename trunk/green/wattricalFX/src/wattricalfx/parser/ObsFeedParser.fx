/*
 * ObsFeedParser.fx
 *
 * Created on Dec 7, 2008, 3:56:47 PM
 */

package wattricalfx.parser;

import java.io.InputStream;
import java.lang.Exception;
import java.lang.System;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javafx.data.pull.Event;
import javafx.data.pull.PullParser;
import javafx.data.xml.QName;
import javafx.io.http.HttpRequest;
import wattricalfx.model.Feed;
import wattricalfx.model.Observation;

/**
 * @author daniel
 */

public class ObsFeedParser {

    public-init var feedLocation:String;
    public var parsedFeeds:Feed[]=null;
    var errorMessage = "";
    def nameQname = QName{
        name:"name"}
    def scopeIdQname = QName{
        name:"scopeId"}
    def stampQname = QName{
        name:"stamp"}
    def valueQname = QName{
        name:"value"}

    def sdf:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'Z");
    def sdfGMT:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'Z");
    init {
        /*var ids:String[] = TimeZone.getAvailableIDs(0);
        for (i in ids){
         println("id is {i}");
         }*/
        def gmt:SimpleTimeZone = new SimpleTimeZone(0,"GMT");
        def cal:GregorianCalendar = new GregorianCalendar();
        cal.setTimeZone(gmt);
        sdfGMT.setCalendar(cal);
    }
    function getStrAttr(pullEvt: javafx.data.pull.Event,qname:QName) : String {
        return
        pullEvt.getAttributeValue(qname) as String;
    }
    function getIntAttr(pullEvt: javafx.data.pull.Event,qname:QName) : Integer {
        return
        java.lang.Integer.parseInt(pullEvt.getAttributeValue(qname));
    }
    function getDateAttr(pullEvt: javafx.data.pull.Event,qname:QName) : Date {
        try {
            var strVal =
        pullEvt.getAttributeValue(qname) as String;
            var d:Date = sdf.parse("{strVal}+0000");
            //println("{strVal} ->{d} ->{sdf.format(d)}");
            return d;
        } catch (e:Exception){
        }
        return new Date();
    }
    function parseInputStream(input: InputStream):Feed[] {
        System.out.println("Gonna Parse");

        var feeds: Feed[];
        var currentFeed:Feed=null;
        var accumFeed:Feed= Feed {
            stamp: new Date()
            value: 654
            name: "Accum"
            scopeId: -1;
        }

        def parser = PullParser {

            input: input

            onEvent: function(pullEvt: javafx.data.pull.Event) {
                if (pullEvt.type == PullParser.START_ELEMENT) {
                    //println("lvl:{pullEvt.level} elt: {pullEvt.qname.name}");

                    if(pullEvt.qname.name == "feed" and pullEvt.level == 1) {
                        def feed = Feed {
                            name: getStrAttr(pullEvt,  nameQname)
                            scopeId:getIntAttr(pullEvt,  scopeIdQname)
                            stamp: getDateAttr(pullEvt, stampQname)
                            value: getIntAttr(pullEvt,  valueQname)
                        }
                        //println("- {feed}");
                        currentFeed=feed;
                        insert feed into feeds;

                    }
                    if(pullEvt.qname.name == "observation" and pullEvt.level == 2) {
                        def observation = Observation {
                            stamp: getDateAttr(pullEvt, stampQname)
                            value: getIntAttr(pullEvt,  valueQname)
                        }
                        //println("  - {observation}");
                        insert observation into currentFeed.observations;
                        insert observation into accumFeed.observations;

                    }
                }
            }
        }

        parser.parse();
        /* verbose output
        println("Accumulated feed: {accumFeed}");
        for (feed in feeds) {
            println("+ {feed}");
        }*/
        return feeds;


    }

    public function parseURL() {


        var httpRequestError: Boolean = false;

        // Submit HttpRequest
        var request: HttpRequest =
        HttpRequest {

            location: feedLocation
            method: HttpRequest.GET

            onException: function(exception: Exception) {
                exception.printStackTrace();
                //alert("Error", "{exception}");
                httpRequestError = true;
            }

            onResponseCode: function(responseCode:Integer) {
                if (responseCode != 200) {
                    println("failed, response: {responseCode} {request.responseMessage}");
                }
            }

            onInput: function(input: java.io.InputStream) {
                try {
                    parsedFeeds = parseInputStream(input);
                    if(errorMessage.length() > 0) {
                        //alert("Error", parser.errorMessage);
                        httpRequestError = true;
                    }
                } finally {
                    input.close();
                }
            }

            onDone: function() {
                if(not httpRequestError) {
                    println("-I am Done with no Errors");
                } else {
                    println("-I am Done with Error: {errorMessage}");
                }
            }
        }

        request.enqueue();
    }

}
