/*
 * ObsFeedParser.fx
 *
 * Created on Dec 7, 2008, 3:56:47 PM
 */

package wattricalfx.parser;


import java.io.InputStream;
import java.lang.Exception;
import java.lang.System;
import javafx.data.pull.Event;
import javafx.data.pull.PullParser;
import javafx.data.xml.QName;
import javafx.io.http.HttpRequest;
import wattricalfx.model.Observation;
import wattricalfx.model.Feed;

/**
 * @author daniel
 */

public class ObsFeedParser {


    var errorMessage = "";
    def nameQname = QName{
        name:"name"}
    def scopeIdQname = QName{
        name:"scopeId"}
    def stampQname = QName{
        name:"stamp"}
    def valueQname = QName{
        name:"value"}

    function parseInputStream(input: InputStream) {
        System.out.println("Gonna Parse");

        var feeds: Feed[];
        var currentFeed:Feed=null;
        var accumFeed:Feed= Feed {
            stamp: "2008-11-02 14:35:56"
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
                            name: pullEvt.getAttributeValue(nameQname) as String
                            scopeId:java.lang.Integer.parseInt( pullEvt.getAttributeValue(scopeIdQname))
                            stamp: pullEvt.getAttributeValue(stampQname) as String
                            value: java.lang.Integer.parseInt(pullEvt.getAttributeValue(valueQname))
                        }
                        println("- {feed}");
                        currentFeed=feed;
                        insert feed into feeds;

                    }
                    if(pullEvt.qname.name == "observation" and pullEvt.level == 2) {
                        def observation = Observation {
                            stamp: pullEvt.getAttributeValue(stampQname) as String
                            value: java.lang.Integer.parseInt(pullEvt.getAttributeValue(valueQname))
                        }
                        println("  - {observation}");
                        insert observation into currentFeed.observations;
                        insert observation into accumFeed.observations;

                    }
                }
            }
        }

        parser.parse();
        println("Accumulated feed: {accumFeed}");

    }

    public function parseURL() {


        var httpRequestError: Boolean = false;

        // Submit HttpRequest
        var request: HttpRequest =
        HttpRequest {

            location: "http://192.168.5.2/iMetrical/feeds.php"
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
                    parseInputStream(input);
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
                    println("I am Done with no Errors");
                } else {
                    println("I am Done with Error: {errorMessage}");
                }
            }
        }

        request.enqueue();
    }

}
