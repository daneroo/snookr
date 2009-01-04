/* ObservationFeed parser, depends on jQuery
 * Daniel Lauzon, 2009
 * Detect environment to select ajax get Method
 *   should work locally, on iG, and on appspot.com
 *     with IE, Firefox, and Safari
 * First Implentation call iMetricalDetect, which will
 *    alert with detection results, and polling data
 *
 * An example should import jquery.js, imetrical.js
 *    --from google code, and later from appspot
 *    http://snookr.googlecode.com/svn/trunk/green/iMetricalGAE/iG/jquery.js
 *    http://imetrical.appspot.com/iG/jquery.js
 *
 *    then call AjaxDetect, like on a button callback ?
 */

var defaultiMetricalURL = "http://imetrical.appspot.com/feeds?owner=daniel";

/*
 * Default mapping from feeds array to DOM-elements
 *    #FEEDNAME div.wattVal <--  f.value
 *    #FEEDNAME div.kWhVal  <--  f.value*24/1000
 *    also
 *    #status <- update time and latency
 *    #error  <- any error messages
 *     *    e.g.
 *    #Live div.wattVal <--  feeds[0].value
 *    #Hour div.kWhVal  <--  f.value*24/1000
 */
function fetchAndMapFeeds(feedurl,feedsCallback,errorCallback) {
    feedsCallback = feedsCallback || function(feeds) {
        for (var i = 0; i < feeds.length; i++) {
            var f = feeds[i];
            $('#'+f.name+' div.wattVal').html(""+f.value);
            $('#'+f.name+' div.kWhVal').html(""+(f.value*24.0/1000));
        }
        var latency = 0;
        try {
            latency = (new Date().getTime()) - (feeds[0].stamp.getTime());
        } catch (err) {}
        latency = Math.round(latency/100)/10;

        $('#status').html(""+(new Date().getYMDHMS())+"  (delay: "+latency+"s.)");
        // latency test reveals &dum=stamp necessary
        //$('#status').html(""+(feeds[0].stamp.getYMDHMS())+"<br>"+(new Date().getYMDHMS())+"<br>  (delay: "+latency+"s.)");
    };
    errorCallback = errorCallback || function(message) {
        if ($('#error').length) {
            $('#error').html(message);
        } else {
            alert(message);
        }
    };
    fetchFeeds(feedurl,feedsCallback,errorCallback);
}
function fetchFeeds(feedurl,feedsCallback,errorCallback) {
    // default value
    feedsCallback = feedsCallback || function(feeds) {
        var message = "success.f: "+latestStringFromFeeds(feeds);
        alert(message);
    }

    var xmlCallbackAdapter = function(xmlDoc){
        var feeds = feedArrayFromXmlDoc(xmlDoc);
        feedsCallback(feeds);
    };
    fetchDOM(feedurl,xmlCallbackAdapter,errorCallback);
}


// works but does not yet report errors in all cases
// default implementation: alert on success, and error
function fetchDOM(feedurl,successCallback,errorCallback) {
    // default values
    feedurl = feedurl || defaultiMetricalURL;
    successCallback = successCallback || function(xmlDoc) {
        var message = "success.x: "+latestStringFromDoc(xmlDoc);
        alert(message);
    };
    errorCallback = errorCallback || function(message) {
        if ($('#error').length) { // if the element exists
            $('#error').html(message);
        } else {
            alert(message);
        }
    };

    try {

        var fetchMethod = "Undtermined";
        if (typeof(_IG_FetchXmlContent) != "undefined") {
            fetchMethod = "iG.Fetch";
            // Disable caching completely and fetch fresh content every time --  !! Try to avoid using this !!
            var nocacheoption = {
                refreshInterval: 0
            }
            // Using the nocache approach above yields sync delays of ~5 seconds more
            // NOTE This is just to expire the cache !!!!
            // THIS should be fixed elsewhere....
            feedurl = feedurl+"&dum="+new Date().getTime();
            _IG_FetchXmlContent(feedurl, function (xmlDoc) {
                if (xmlDoc == null || typeof(xmlDoc) != "object" || xmlDoc.firstChild == null) {
                    var message = "error fetching data with: "+fetchMethod;
                    errorCallback(message);
                    return;
                } else { // everything is ok
                    successCallback(xmlDoc);
                }
            },nocacheoption);
        } else {
            fetchMethod = "jQ.ajax";
            $.ajax({
                type: "GET",
                url: feedurl,
                dataType: "xml",
                success: successCallback,
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    //var message = "error: "+textStatus+" (exception: "+errorThrown+")";
                    var message = "error fetching data with: "+fetchMethod;
                    errorCallback(message);
                },
                complete:function (XMLHttpRequest, textStatus) {
                // NOT USED
                //this; // the options for this ajax request
                //var message = "complete: "+textStatus;
                //alert(message);
                }
            });
        }
    } catch (error) {
        var message = "error fetching data with: "+fetchMethod;
        errorCallback(message);
    }

}

function feedArrayFromXmlDoc(xmlDoc){
    var feeds=[]; // result array
    var feedList = xmlDoc.getElementsByTagName("feed");
    for (var i = 0; i < feedList.length ; i++) {
        var stamp = new Date();
        stamp.setISO8601(feedList.item(i).getAttribute("stamp"));
        var feed = {
            name: feedList.item(i).getAttribute("name"),
            stamp: stamp,
            value: feedList.item(i).getAttribute("value")
        }
        feeds.push(feed);
    }
    return feeds;
}

function latestStringFromFeeds(feeds){
    try {
        var feed = feeds[0];
        return(""+feed.name+": "+feed.stamp.getYMDHMS()+" - "+feed.value);
    } catch (err){}
    return "LatestString DefaultValue (exeption?)";
}

function latestStringFromDoc(xmlDoc){
    try {
        var feeds = feedArrayFromXmlDoc(xmlDoc);
        return latestStringFromFeeds(feeds);
    } catch (err){}
    return "LatestString DefaultValue (exeption?)";
}

// Date Handling - http://delete.me.uk/2005/03/iso8601.html
// see also http://blog.stevenlevithan.com/archives/date-time-format
Date.prototype.setISO8601 = function (string) {
    var regexp = "([0-9]{4})(-([0-9]{2})(-([0-9]{2})" +
    "(T([0-9]{2}):([0-9]{2})(:([0-9]{2})(\.([0-9]+))?)?" +
    "(Z|(([-+])([0-9]{2}):([0-9]{2})))?)?)?)?";
    var d = string.match(new RegExp(regexp));

    var offset = 0;
    var date = new Date(d[1], 0, 1);

    if (d[3]) {
        date.setMonth(d[3] - 1);
    }
    if (d[5]) {
        date.setDate(d[5]);
    }
    if (d[7]) {
        date.setHours(d[7]);
    }
    if (d[8]) {
        date.setMinutes(d[8]);
    }
    if (d[10]) {
        date.setSeconds(d[10]);
    }
    if (d[12]) {
        date.setMilliseconds(Number("0." + d[12]) * 1000);
    }
    if (d[14]) {
        offset = (Number(d[16]) * 60) + Number(d[17]);
        offset *= ((d[15] == '-') ? 1 : -1);
    }

    offset -= date.getTimezoneOffset();
    time = (Number(date) + (offset * 60 * 1000));
    this.setTime(Number(time));
};

pad = function (val, len) {
    val = String(val);
    len = len || 2;
    while (val.length < len) val = "0" + val;
    return val;
};

Date.prototype.getHMS = function () {
    return ""+pad(this.getHours())+":"+pad(this.getMinutes())+":"+pad(this.getSeconds());
};
Date.prototype.getYMD = function () {
    return ""+pad(this.getFullYear())+"-"+pad(this.getMonth()+1)+"-"+pad(this.getDate());
};
Date.prototype.getYMDHMS = function () {
    return this.getYMD()+" "+this.getHMS();
};

