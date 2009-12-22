
var now=new Date();var ago=new Date(); ago.setTime(Number(now)-3600);
var noobs = [
{
    stamp:now,
    value:1000
}/*,
{
    stamp:ago,
    value:1000
}*/];
var hiddenFeeds = [
{
    name:'Live',
    stamp:new Date(),
    value:1000,
    observations:noobs
},
{
    name:'Hour',
    stamp:new Date(),
    value:1000,
    observations:noobs
},
{
    name:'Day',
    stamp:new Date(),
    value:1000,
    observations:noobs
},
{
    name:'Week',
    stamp:new Date(),
    value:1000,
    observations:noobs
},
{
    name:'Month',
    stamp:new Date(),
    value:1000,
    observations:noobs
},
{
    name:'Year',
    stamp:new Date(),
    value:1000,
    observations:noobs
}
];
function ted5kfetchAndMapFeeds(proxyurlmap,feedsCallback,elapsed){
    fetchTed(proxyurlmap,elapsed);
    //$('#error').html('after fetchTed: '+hiddenFeeds.length);
    if (hiddenFeeds.length>0 && feedsCallback){
        feedsCallback(hiddenFeeds);
    }
}

// historical urls
/* param  u: seems to be cache control, to make url unique
 *  COUNT (+1) : if you want 3, COUNT=4, if you want 60, COUNT=61
 * http://192.168.5.238/history/monthlyhistory.xml?INDEX=0&MTU=0&COUNT=120&u=1
 * http://192.168.5.238/history/dailyhistory.xml?INDEX=0&MTU=0&COUNT=20&u=2
 * http://192.168.5.238/history/hourlyhistory.xml?INDEX=0&MTU=0&COUNT=60&u=3
 * http://192.168.5.238/history/minutehistory.xml?MTU=0
 * http://192.168.5.238/history/secondhistory.xml?INDEX=0&MTU=0&COUNT=6&u=17
 */
function makeURLs(baseURL){
    var urlmap={
        'live':  baseURL+'/api/LiveData.xml',
        'second':baseURL+'/history/secondhistory.xml?INDEX=0&MTU=0&COUNT=61',
        'minute':baseURL+'/history/minutehistory.xml?INDEX=0&MTU=0&COUNT=61',
        'hour':  baseURL+'/history/hourlyhistory.xml?INDEX=0&MTU=0&COUNT=25',
        'day':   baseURL+'/history/dailyhistory.xml?INDEX=0&MTU=0&COUNT=32',
        'month': baseURL+'/history/monthlyhistory.xml?INDEX=0&MTU=0&COUNT=13'
    }
    return urlmap;
}
// relative to this document!
function makeProxyURLs(urlmap){
    var proxyprefix='proxy.php?mimeType=text/xml&url=';
    var proxyurlmap={}
    for (key in urlmap){
        proxyurlmap[key] =proxyprefix+encodeURIComponent(urlmap[key]);
    }
    return proxyurlmap;
}

var lastLongFetched = null;
function fetchTed(proxyurl){
    // each scop should have it's length/expiry...'
    //fetchDOM(proxyurl['live'],tedLiveCallBack,errorHandler)
    var scopes = ['second','minute','hour','day','month'];
    var elapsed = 100000;
    if (lastLongFetched!=null){
        elapsed = new Date().getTime()-lastLongFetched.getTime();
    }
    //$('#error').html('elapsed: '+elapsed);
    for (i in scopes){
        var scope = scopes[i];
        if (elapsed<60000 && scope!='second') {
            //$('#error').html('skipped elapsed: '+elapsed);
            continue;
        }
        var scopeTag = scope.toUpperCase();
        fetchDOM(proxyurl[scope],tedHistoryCallBack(scopeTag),errorHandler)
        if (scope!='second'){
            lastLongFetched = new Date();
            //$('#error').html('long elapsed: '+elapsed);
        }
    }
}

function errorHandler(message){
    $('#error').html(message);
}

/*
 *<Power><Total>..<PowerNow>901</PowerNow><PowerHour>1202</PowerHour><PowerTDY>21951</PowerTDY><PowerMTD>158109</PowerMTD>
 */
tedLiveCallBack = function(xmlDoc){
    var powerNode = xmlDoc.getElementsByTagName("Power");
    var totalPowerNode = powerNode.item(0).getElementsByTagName("Total");
    var now = totalPowerNode.item(0).getElementsByTagName("PowerNow").item(0).childNodes[0].nodeValue;
    var hour = totalPowerNode.item(0).getElementsByTagName("PowerHour").item(0).childNodes[0].nodeValue;
    var today = totalPowerNode.item(0).getElementsByTagName("PowerTDY").item(0).childNodes[0].nodeValue;
    var mtd = totalPowerNode.item(0).getElementsByTagName("PowerMTD").item(0).childNodes[0].nodeValue;
    $('#ted-Live').html("Power Now | Hour | TDY | MTD : "+now+" | "+hour+" | "+today+" | "+mtd);
}

// These DTD's are similar, at lest with respect to DATE,POWER nodes.
//<SECOND><MTU>0</MTU><DATE>11/07/2009 16:23:02</DATE><POWER>1608</POWER><COST>12</COST><VOLTAGE>1215</VOLTAGE></SECOND>
//<MINUTE><MTU>0</MTU><DATE>11/09/2009 14:32:00</DATE><POWER>818</POWER><COST>6</COST><VOLTAGE>1211</VOLTAGE></MINUTE>
//<HOUR><MTU>0</MTU><DATE>11/09/2009 13:00:00</DATE><POWER>925</POWER><COST>6</COST><VMIN>1196</VMIN><VMAX>1196</VMAX></HOUR>
//<DAY><MTU>0</MTU><DATE>11/08/2009 00:00:00</DATE><POWER>29413</POWER><COST>206</COST><PMIN><VAL>500</VAL><DATE>20:3</DATE></PMIN><PMAX><VAL>8638</VAL><DATE>11:13</DATE></PMAX><CMIN><VAL>4</VAL><DATE>9:26</DATE></CMIN><CMAX><VAL>61</VAL><DATE>11:13</DATE></CMAX><VMIN><VAL>2396</VAL><DATE>10:56</DATE></VMIN><VMAX><VAL>2492</VAL><DATE>2:4</DATE></VMAX></DAY>
// callback generator
tedHistoryCallBack = function(scopeTag){ // SECOND,MINUTE,HOUR,DAY,MONTH
    callback = function(xmlDoc){
        var scopedNodes = xmlDoc.getElementsByTagName(scopeTag);
        var feed = {
            name:scopeTag,
            stamp:new Date(),
            value:1000
        }
        feed.observations = [] // observation array
        var average=0;
        for (var i = 0; i < scopedNodes.length ; i++) {
            var aNode = scopedNodes.item(i);
            var stampStr = aNode.getElementsByTagName("DATE").item(0).childNodes[0].nodeValue;
            var power = aNode.getElementsByTagName("POWER").item(0).childNodes[0].nodeValue;
            var value = Number(power);
            if (scopeTag=='DAY'){
                if (value<5000) continue; // or even break!
                value=value/24.0;
            }
            var ostamp = new Date();
            ostamp.setTED5kDate(stampStr);
            var observation = {
                stamp: ostamp,
                value: value
            }
            average+=value
            feed.observations.push(observation);
        }
        if (feed.observations.length>0){
            feed.value = average/feed.observations.length;
            feed.stamp = feed.observations[0].stamp;
        }

        var fIndex=0;
        switch (feed.name){
            case "SECOND":
                feed.name='Live';
                fIndex=0;
                break;
            case "MINUTE":
                feed.name='Hour';
                fIndex=1;
                break;
            case "HOUR":
                feed.name='Day';
                fIndex=2;
                break;
            case "DAY":
                feed.name='Month';
                fIndex=4;
                break;
            case "MONTH":
                feed.name='Year';
                fIndex=5;
                break;
        }
        if (fIndex==4){ //Month -> copy slice to week
            var weekCopy = {
                name:'Week',
                stamp:feed.stamp,
                value:feed.value, // recalc average below
                observations:feed.observations.slice(0,7)
            };
            if (weekCopy.observations.length>0){
                var avg=0;
                for (i=0;i<weekCopy.observations.length;i++){
                    avg+=weekCopy.observations[i].value;
                }
                weekCopy.value=avg/weekCopy.observations.length;

            }
            hiddenFeeds[3]=weekCopy;
            tedInjectorForOneFeed(weekCopy);
        }
        if (fIndex==5){ //Year - compareobs==0
            feed.compareobs = [];
            for (i in feed.observations) feed.compareobs.push(500);

            // actually why don;t we replace with static feed...
            feed=staticYearFeed;
        // or even better append...

        }
        hiddenFeeds[fIndex]=feed;
        tedInjectorForOneFeed(feed);


        var htmllist = '<h4>'+feed.name+' History ('+feed.stamp.getYMDHMS()+' | '+feed.value+')</h4><ul>';
        for (var i=0;i<feed.observations.length;i++){
            var o = feed.observations[i];
            htmllist+='<li>'+o.stamp.getYMDHMS()+' | '+o.value+'</li>'
        }
        htmllist+='</ul>';
        scopeId = scopeTag.toLowerCase(scopeTag);
        $('#ted-'+scopeId).html(htmllist)
    }
    return callback;
}

function tedInjectorForOneFeed(feed){
    //$('#error').html('got: '+f.name);
    standardInjectorForOneFeed(feed);
    var darkOptions={};// all defaults
    $('#chart  .im-feed-'+feed.name+'  .im-chart-img').css('background-image',"url('"+chartURL(feed,darkOptions)+"')");

}
// 11/09/2009 14:55:00
Date.prototype.setTED5kDate = function (string) {
    var regexp = "([0-9]{2})/([0-9]{2})/([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})";
    var d = string.match(new RegExp(regexp));

    var offset = 0;
    var date = new Date(d[3], 0, 1);
    if (d[1]) {
        date.setMonth(d[1] - 1);
    }
    if (d[2]) {
        date.setDate(d[2]);
    }
    if (d[4]) {
        date.setHours(d[4]);
    }
    if (d[5]) {
        date.setMinutes(d[5]);
    }
    if (d[6]) {
        date.setSeconds(d[6]);
    }
    this.setTime(Number(date));
};
