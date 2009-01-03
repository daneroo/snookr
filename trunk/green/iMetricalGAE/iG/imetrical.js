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

function iMetricalDetect(feedurl) {
    feedurl = feedurl || defaultiMetricalURL;
    $.ajax({
        type: "GET",
        url: feedurl,
        dataType: "xml",
        success: function(xmlDoc) {
            var message = "success with jQ.ajax: "+latestStringFromDoc(xmlDoc);
            pushMessageString(message);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            //this; // the options for this ajax request
            $('#status').html("error: "+textStatus+" (exception: "+errorThrown+")");
        },
        complete:function (XMLHttpRequest, textStatus) {
            //this; // the options for this ajax request
            $('#status').html("complete: "+textStatus);
        }

    });

}

function pushMessageString(message) {
    $('#message').html(message);
}

function latestStringFromDoc(xmlDoc){
    try {
        var feedList = xmlDoc.getElementsByTagName("feed");
        var latestValueStr = feedList.item(0).getAttribute("value");
        var latestStampStr = feedList.item(0).getAttribute("stamp");
        var stamp = new Date();
        stamp.setISO8601(latestStampStr);
        latestStampStr = stamp.getYMDHMS();
        return("latest: "+latestStampStr+" - "+latestValueStr);
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

