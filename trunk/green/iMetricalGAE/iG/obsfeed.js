/* Obsfeed parser, depends on jQuery
 * Daniel Lauzon, 2008
 */

var globalurl = "http://imetrical.appspot.com/feeds?owner=daniel";
// while true; do curl -o feeds.xml "http://imetrical.appspot.com/feeds?owner=daniel"; sleep 3; done
//globalurl = "feeds.xml";

var globalFeedArray = null;
var globalSelectedFeed = 0;
function updateFeeds() {
    var feeds = globalFeedArray;
    if (!feeds) return;
    // show the main panel.
    //$("#main").hide("puff",{percent:110},null,function () {
    $("#main").fadeOut("slow",function () {
        var feeds = globalFeedArray;
        if (!feeds) return;
        // change contents

        //var coco=""+feeds[0].value+" Watts";
        //$("#main").html(coco).corner();
        var feed = feeds[globalSelectedFeed];
        $("#scopename").html(""+feed.name);
        $("#stamp").html(""+feed.stamp.getYMDHMS());
        if (globalSelectedFeed==0) {
            $("#value").html(""+feed.value);
            $("#units").html("Watt");

        } else {
            $("#value").html(""+(feed.value*24/1000));
            $("#units").html("kWh/d");

        }
        var target=1250.0;
        var percentChange = Math.round( (feed.value/target -1) * 100 );
        var percentChangeStr = ((percentChange>0)?"+":"")+percentChange+"%";
        $("#change").html(percentChangeStr);

        // fade back in
        $("#main").fadeIn("fast");
        
    });

    // set the 1..len kWh elements
    for (var i = 1; i < feeds.length; i++) {
        var f = feeds[i];
        $('#'+f.name+' div.kWh').html(""+(f.value*24.0/1000));
    }


    // details panel
    var html = "<table>";
    //html += "<tr><th>Scope</th><th>Stamp</th><th>W</th><th>kWh/d</th></tr>"
    for (var i = 0; i < feeds.length; i++) {
        var f = feeds[i];
        html += "<tr><td>"+f.name+"</td><td>"+f.stamp.getHMS()+"</td><td>"+f.value+"</td><td>"+(f.value*24.0/1000)+"</td></tr>"
    }
    html += "</table>";
    //$("div.details").hide("puff").html(html).fadeIn("fast");
    $("div.details").html(html);

}
function makeJQueryRequest() {
    $.ajax({
        type: "GET",
        url: globalurl,
        dataType: "xml",
        success: function(xmlData) {


            var feeds=[]; // result array

            var feedList = xmlData.getElementsByTagName("feed");
            for (var i = 0; i < feedList.length ; i++) {
                var stamp = new Date();
                stamp.setISO8601(feedList.item(i).getAttribute("stamp"));
                var feed = {
                    name: feedList.item(i).getAttribute("name"),
                    //stamp: feedList.item(i).getAttribute("stamp").substring(11,19),
                    stamp: stamp,
                    value: feedList.item(i).getAttribute("value"),
                }
                feeds.push(feed);
            }
            lastUpdate = feeds[0].stamp;
            latency = new Date().getTime() - lastUpdate.getTime();
            latency = Math.round(latency/100)/10;
            globalFeedArray=feeds;
            $('#status').html("update: "+lastUpdate.getYMDHMS()+" (delay: "+latency+"s.)");
            updateFeeds();

        }
    });


}



function makeNativeDOMRequest() {
    var url = "http://imetrical.appspot.com/feeds?owner=daniel";
    //var url = "http://dl.sologlobe.com:9999/iMetrical/feeds.php";
    //var url = "http://192.168.5.2/iMetrical/feeds.php";
    xmlhttp=new XMLHttpRequest();

    if (xmlhttp!=null)  {
        xmlhttp.onreadystatechange=onResponseNative;
        xmlhttp.open("GET",url,true);
        xmlhttp.send(null);
    } else  {
        alert("Your browser does not support XMLHTTP.");
    }
};

function onResponseNative() {
    if(xmlhttp.readyState!=4) return;
    if(xmlhttp.status!=200) {
        alert("Problem retrieving XML data");
        return;
    }
    var domdata = xmlhttp.responseXML.documentElement;
    responseCommon(domdata);
    var refreshRate = 5;
    setTimeout("makeNativeDOMRequest()", refreshRate*1000)
};

function makeDOMRequest() {
    var params = {};
    params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.DOM;
    var url = "http://imetrical.appspot.com/feeds?owner=daniel";
    gadgets.io.makeRequest(url, response, params);
};

function responseIG(obj) {
    // obj.data contains a Document DOM element corresponding to the
    // page that was requested
    var domdata = obj.data;
    responseCommon(domdata)
};

function responseCommon(domdata) {
    var html = "<table>";
    html += "<tr><th>Scope</th><th>Stamp</th><th>W</th><th>kWh/d</th></tr>"
    var feedList = domdata.getElementsByTagName("feed");
    for (var i = 0; i < feedList.length ; i++) {
        // For each <food> node, get child nodes.
        var name = feedList.item(i).getAttribute("name");
        var stamp = feedList.item(i).getAttribute("stamp").substring(11,19);
        var value = feedList.item(i).getAttribute("value");

        html += "<tr><td>"+name+"</td><td>"+stamp+"</td><td>"+value+"</td><td>"+(value*24.0/1000)+"</td></tr>"
    }
    html += "</table>";

    var latestStampStr = feedList.item(0).getAttribute("stamp")
    var dstamp = new Date();
    //dstamp.setISO8601("2005-03-26T19:51:34Z");
    //dstamp.setISO8601("2008-12-30T04:23:13Z");
    dstamp.setISO8601(latestStampStr);

    var refreshRate = 5;
    html += "<pre>Refresh Rate: "+refreshRate+" latency: "+(new Date().getTime()-dstamp.getTime())/1000+"<pre>"

    document.getElementById('content_div').innerHTML = html;
    document.getElementById('status_div').innerHTML = dstamp+"<br>"+new Date();

};

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

