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

/*
 * Constants, Settings
 */
var defaultiMetricalURL = "http://imetrical.appspot.com/feeds?owner=daniel";

/*
 * i18N : Internationalisation
 */
var iM18nLang= 'en'; //'fr';
var iMi18n = {
    'Live':  {
        'fr':'Courant'
    },
    'Hour':  {
        'fr':'Heure'
    },
    'Day':   {
        'fr':'Jour'
    },
    'Week':  {
        'fr':'Sem'
    },
    'Month': {
        'fr':'Mois'
    },
    'Year':  {
        'fr':'Annee'
    },
    'kWh/d': {
        'fr':'kWh/j'
    },
    'under target': {
        'fr':'sous objectif'
    },
    'over target': {
        'fr':'sur objectif'
    },
    'Sun': {
        'fr':'Dim'
    },
    'Mon': {
        'fr':'Lun'
    },
    'Tue': {
        'fr':'Mar'
    },
    'Wed': {
        'fr':'Mer'
    },
    'Thu': {
        'fr':'Jeu'
    },
    'Fri': {
        'fr':'Ven'
    },
    'Sat': {
        'fr':'Sam'
    },
    'Jan': {
        'fr':'Jan'
    },
    'Feb': {
        'fr':'Fev'
    },
    'Mar': {
        'fr':'Mar'
    },
    'Apr': {
        'fr':'Avr'
    },
    'May': {
        'fr':'Mai'
    },
    'Jun': {
        'fr':'Juin'
    },
    'Jul': {
        'fr':'Juil'
    },
    'Aug': {
        'fr':'Aout'
    },
    'Sep': {
        'fr':'Sep'
    },
    'Oct': {
        'fr':'Oct'
    },
    'Nov': {
        'fr':'Nov'
    },
    'Dec': {
        'fr':'Dec'
    },
    'Time': {
        'fr':'Temps'
    },
    'Previous Year': {
        'fr':'Annee preced.'
    },
    'Current Year': {
        'fr':'Annee courante'
    },
    'Power Consumption': {
        'fr':'Consommation en Puissance'
    }
//'e': {'fr':'f'},  /* no trailing comma on last*/
};

function getI18n(lookup) {
    var foundEntry = iMi18n[lookup];
    if (foundEntry) {
        var foundForLang = foundEntry[iM18nLang];
        if (foundForLang) return foundForLang;
    }
    return lookup;
}

// map unit name and value-class-suffix
var iMUnits = {
    w:   {
        name:"W",
        suffix:"w",
        format:"0000"
    },
    kw:  {
        name:"kW",
        suffix:"kw",
        format:"0.00"
    },
    kwhd:{
        name:"kWh/d",
        suffix:"kwhd",
        format:"00.0"
    }
};

/*
 * Animation callbacks
 */
function startAnimNow(interval,callback) {
    //calls the first iteration immediately
    //setTimeout(1000,callback);
    callback();
    $.timer(interval,callback);

}
function storyboardNextAndRollTheme( ) {
    storyboardNext();
    if (currentframe==0) rolltheme();
}
var currentframe=-1;
function storyboardNext( ) {
    var animOut='slide'; // slide,drop,null
    var animIn='slide'; // slide,drop,null
    if (currentframe==-1) { // also force reset? when
        $('.toggler > div.im-badge:nth-child(n+4)').hide();
        $('.toggler > div.im-badge:nth-child(-n+3)').show();
        currentframe=0;
        return;
    }
    //moves
    var m = {
        L:1,
        H:2,
        D:3,
        W:4,
        M:5,
        Y:6,
        Lx:101,
        Hx:102,
        Dx:103,
        Wx:104,
        Mx:105,
        Yx:106
    };
    var storyboard = [ // 1-based index
    //[-H,W],
    [-m.H,m.Dx],
    [-m.Dx,m.W],
    [-m.W,m.M],
    [-m.M,m.Y],
    [-m.Y,m.H]
    ];

    /*
     * Codes use 1..6 for indexing (as in css, and avoid signed '0' problem
     * +n show n, -n: hide n
     * +100+n expand n, -100+n
     */
    var eat=storyboard[currentframe];
    function boundEater() {
        if (eat.length==0) return;
        var head = eat.shift(); // from the front
        if (head<0) {
            head=-head;
            if (head>100) {
                head-=100;
                $('.toggler > div.im-badge:nth-child('+(head)+')').find('.im-badge-right').hide(animOut,null,1000,boundEater);
            } else {
                $('.toggler > div.im-badge:nth-child('+(head)+')').hide(animOut,null,1000,boundEater);
            }
        } else {
            if (head>100) {
                head-=100;
                $('.toggler > div.im-badge:nth-child('+(head)+')').find('.im-badge-right').show(animIn,null,1000,boundEater);
            } else {
                $('.toggler > div.im-badge:nth-child('+(head)+')').show(animIn,null,1000,boundEater);
            }
        }
    }
    boundEater();
    currentframe = (currentframe+1) % storyboard.length;
}
var rollingThemes=['im-lf-black-gloss','im-lf-blue-hsoft','im-lf-green-glass'];
function rolltheme() {
    var newtheme = rollingThemes.shift();
    $('.toggler').addClass(newtheme);
    $.each(rollingThemes,function(){
        $('.toggler').removeClass(this);
    });
    rollingThemes.push(newtheme);
}


function drawChart(feed) {
    var weekDayNames=["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
    var monthNames=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
    var isBarChart=true; // switch areachart(line) or columnchart(bar)
    var units = "?";
    var multiplier=1.0;
    switch (feed.name){
        case "Live":
            timeFormat = function(stamp){
                return ""+stamp.getHMS()
            };
            isBarChart=false;
            units = "Watts";
            multiplier=1.0;
            break;
        case "Hour":
            timeFormat = function(stamp){
                return ""+pad(stamp.getHours())+":"+pad(stamp.getMinutes());
            };
            isBarChart=false;
            units = "kW";
            multiplier=1.0/1000.0;
            break;
        case "Day":
            timeFormat = function(stamp){
                return ""+pad(stamp.getHours())+":"+pad(stamp.getMinutes());
            };
            units = "kWh";
            multiplier=1.0/1000.0;
            break;
        case "Week":
            timeFormat = function(stamp){
                return getI18n(weekDayNames[stamp.getDay()]);
            };
            units = "kWh/d";
            multiplier=24.0/1000.0;
            break;
        case "Month":
            timeFormat = function(stamp){
                return getI18n(monthNames[stamp.getMonth()])+" "+stamp.getDate();
            };
            units = "kWh/d";
            multiplier=24.0/1000.0;
            break;
        case "Year":
            timeFormat = function(stamp){
                return getI18n(monthNames[stamp.getMonth()])+" "+stamp.getFullYear();
            };
            units = "kWh/d";
            multiplier=24.0/1000.0;
            break;
        default : // nothing
    }

    var data = new google.visualization.DataTable();

    var hasPredictor = (feed.compareobs!=null);
    var currentColumn=1;
    if (hasPredictor) currentColumn=2;


    data.addColumn('string', getI18n('Time'));
    if (hasPredictor) {
        data.addColumn('number', getI18n('Previous Year'));
        data.addColumn('number', getI18n('Current Year'));
    } else {
        data.addColumn('number', getI18n(units));
    }

    data.addRows(feed.observations.length);
    // reverso order ..
    for (var i = 0; i < feed.observations.length ; i++) {
        var o = feed.observations[i];
        var c = null;
        if (hasPredictor) c=feed.compareobs[i];
        var timeLabel = ""+timeFormat(o.stamp);
        data.setValue(feed.observations.length-i-1, 0, timeLabel);
        if (hasPredictor) {
            data.setValue(feed.observations.length-i-1, 1, c.value*multiplier); // div by 1 to get Number ?
        }
        data.setValue(feed.observations.length-i-1, currentColumn, o.value*multiplier); // div by 1 to get Number ?
    }

    // necessay otherwise appends.
    $('#chart').html("");
    var chart;
    if (isBarChart){
        chart = new google.visualization.ColumnChart(document.getElementById('chart'));
    } else {
        chart = new google.visualization.AreaChart(document.getElementById('chart'));
    }

    var legend = 'none';
    if (hasPredictor) legend='bottom';
    /* documentation for options at:
     * http://code.google.com/apis/visualization/documentation/gallery/areachart.html
     * */
    options =  {
        backgroundColor: "#444444",
        //backgroundColor: "#000000",
        //backgroundColor: rgba(85,255,0,.8),
        axisColor:"#555555",
        axisBackgroundColor:"#444444",
        //colors:["#7f93bc","#3b5998"],
        colors:["#55ff00","#33aa00"],
        width: 320,
        height: 180,
        min: 0,
        pointSize: 1,
        is3D: false,
        title: getI18n(feed.name)+' - '+getI18n('Power Consumption')+' ('+getI18n(units)+')',
        titleY: getI18n(units),
        titleFontSize: 12,
        titleColor: "#ffffff",
        legend:legend,
        legendBackgroundColor:"#444444",
        legendTextColor: "#ffffff",
        legendFontSize:8

    };
    chart.draw(data, options);
}

/*
 * Layout generation
 */
function add6Badges(parentID){
    //addBadge(parentID,'Live', iMUnits.w);
    addBadge(parentID,'Live',  iMUnits.w );
    addBadge(parentID,'Hour',  iMUnits.kw );
    addBadge(parentID,'Day',   iMUnits.kwhd );
    addBadge(parentID,'Week',  iMUnits.kwhd );
    addBadge(parentID,'Month', iMUnits.kwhd );
    addBadge(parentID,'Year',  iMUnits.kwhd );
    // hide all 6 'right parts of the badges''
    $(parentID).find('.im-badge-right').hide();
    $(parentID).find('.im-badge').addClass('ui-state-default ui-corner-all');
    $(parentID).find('.im-badge').hover(
        function() {
            $(this).addClass('ui-state-hover');
        },
        function() {
            $(this).removeClass('ui-state-hover');
        }  );
}
function addBadge(parentID,feedName,feedUnits){
    var html = '<div class="im-badge im-feed-'+feedName+'">'+
    '   <div class="im-badge-left">'+
    '       <div><span class="im-feed-name">'+getI18n(feedName)+'</span><span class="im-feed-units">'+getI18n(feedUnits.name)+'</span></div>'+
    '       <div class="im-v-'+feedUnits.suffix+'">'+feedUnits.format+'</div>'+
    '   </div>'+
    '   <div class="im-badge-right">'+
    '       <div class="im-v-percent">+0%</div>'+
    '       <div class="im-v-overunder">under target</div>'+
    '   </div>'+
    '</div>';
    $(parentID).append(html);
// hiding and decorationg is performed in add6Badges.
}


// inject red color flash into "Live" feed Name
function flashInjector() { // visual simulation of fetch
    var el = $('.im-feed-Live').find(".im-feed-name");
    el.addClass("im-flash");
    setTimeout ( function(){
        el.removeClass("im-flash");
    }, 500 );
}

/*
 * Injector model based on
  <div class="im-badge im-feed-FFFF">
    <div class="im-badge-left">
      <div><span class="im-feed-name">FFFF</span><span class="im-feed-units">TWh/c</span></div>
      <div class="im-v-UNIT">00.00</div>
    </div>
    <div class="im-badge-right">
      <div class="im-v-percent">+0%</div>
      <div class="im-v-overunder">over target</div>
    </div>
  </div>

 */
/*
 * Default mapping from feeds array to DOM-elements
 *    .im-feed-FFFF im-v-w    <--  f.value
 *    .im-feed-FFFF im-v-kw   <--  f.value/1000
 *    .im-feed-FFFF im-v-kwhd <--  f.value*24/1000
 *    also
 *    #status <- update time and latency
 *    #error  <- any error messages
 *     *    e.g.
 *    .im-feed-Live .im-v-w <--  feeds[0].value
 *    .im-feed-Hour .im-v-kwhd  <--  f.value*24/1000
 */
function standardInjector(feeds) {
    for (var i = 0; i < feeds.length; i++) {
        var f = feeds[i];
        standardInjectorForOneFeed(f);
    }
    var latency = 0;
    try {
        latency = (new Date().getTime()) - (feeds[0].stamp.getTime());
    } catch (err) {}
    latency = Math.round(latency/100)/10;

    $('#status').html(""+(new Date().getYMDHMS())+"  ("+latency+"s.)");
// latency test reveals &dum=stamp necessary
//$('#status').html(""+(feeds[0].stamp.getYMDHMS())+"<br>"+(new Date().getYMDHMS())+"<br>  (delay: "+latency+"s.)");
}
function standardInjectorForOneFeed(f) {
    var W = Math.round(f.value);
    var kW = Math.round((f.value/1000)*100)/100.0;
    var kWhPerDay = Math.round((f.value*24.0/1000)*10)/10.0;

    $('.im-feed-'+f.name+' .im-v-w').html(""+W);
    $('.im-feed-'+f.name+' .im-v-kw').html(""+kW);
    $('.im-feed-'+f.name+' .im-v-kwhd').html(""+kWhPerDay);
    var targetW = 1666.0; //40/24*1000Ê
    // percent to .1
    var percent = Math.round(((f.value / targetW) - 1) * 1000)/10.0;
    $('.im-feed-'+f.name+' .im-v-percent').html(""+percent+"%");
    if (percent<0) {
        $('.im-feed-'+f.name+' .im-v-overunder').html(getI18n("under target"));
        $('.im-feed-'+f.name+' .im-v-overunder').css({
            'color' : 'green'
        });
    } else {
        $('.im-feed-'+f.name+' .im-v-overunder').html(getI18n("over target"));
        $('.im-feed-'+f.name+' .im-v-overunder').css({
            'color' : 'red'
        });
    }
}
function fetchAndMapFeeds(feedurl,feedsCallback,errorCallback) {
    feedsCallback = feedsCallback || standardInjector;
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
                    var message = "error fetching data with: "+fetchMethod+"("+feedurl+")";
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

        // Observations section
        // add an array member: feed.observations
        feed.observations = [] // observation array
        var obsList = feedList.item(i).getElementsByTagName("observation");
        for (var j = 0; j < obsList.length ; j++) {
            var ostamp = new Date();
            ostamp.setISO8601(obsList.item(j).getAttribute("stamp"));
            var observation = {
                stamp: ostamp,
                value: obsList.item(j).getAttribute("value")
            }
            feed.observations.push(observation);
        }
    
    }
    if (feeds.length>0) {
        feeds.push(staticYearFeed);
    }
    return feeds;
}

// used to get feed from feeds, and observations from feed..
function observationArrayFromFeed(node){
    var observations=[]; // result array
    var obsList = xmlDoc.getElementsByTagName('observation');
    for (var i = 0; i < obsList.length ; i++) {
        var stamp = new Date();
        stamp.setISO8601(obsList.item(i).getAttribute("stamp"));
        var feed = {
            name: obsList.item(i).getAttribute("name"),
            stamp: stamp,
            value: obsList.item(i).getAttribute("value")
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

// Yearly Data - till included
// sql to generate from hydro.watt_billing
//mysql -N -B -e "select concat(' { stampStr: ''',left(stamp,7),'-01T05:00:00Z'''),', value: ',avg(watt),'},' from watt_billing group by left(stamp,7)" hydro
var hydroData = [
{
    stampStr:'2006-06-01T05:00:00Z',
    value:2272.0000
},
{
    stampStr:'2006-07-01T05:00:00Z',
    value:2272.0000
},
{
    stampStr: '2006-08-01T05:00:00Z',
    value:1863.5161
},
{
    stampStr: '2006-09-01T05:00:00Z',
    value:1819.9333
},
{
    stampStr: '2006-10-01T05:00:00Z',
    value:2057.0000
},
{
    stampStr: '2006-11-01T05:00:00Z',
    value:2057.0000
},
{
    stampStr: '2006-12-01T05:00:00Z',
    value:2335.0000
},
{
    stampStr: '2007-01-01T05:00:00Z',
    value:2335.0000
},
{
    stampStr: '2007-02-01T05:00:00Z',
    value:2146.0000
},
{
    stampStr: '2007-03-01T05:00:00Z',
    value:2139.0000
},
{
    stampStr: '2007-04-01T05:00:00Z',
    value:2068.0667
},
{
    stampStr: '2007-05-01T05:00:00Z',
    value:2063.0000
},
{
    stampStr: '2007-06-01T05:00:00Z',
    value:2166.4333
},
{
    stampStr: '2007-07-01T05:00:00Z',
    value:2170.0000
},
{
    stampStr: '2007-08-01T05:00:00Z',
    value:2061.6129
},
{
    stampStr: '2007-09-01T05:00:00Z',
    value:2030.0000
},
{
    stampStr: '2007-10-01T05:00:00Z',
    value:2170.3226
},
{
    stampStr: '2007-11-01T05:00:00Z',
    value:2180.2333
},
{
    stampStr: '2007-12-01T05:00:00Z',
    value:2332.0000
},
{
    stampStr: '2008-01-01T05:00:00Z',
    value:2332.0000
},
{
    stampStr: '2008-02-01T05:00:00Z',
    value:2332.8621
},
{
    stampStr: '2008-03-01T05:00:00Z',
    value:2333.0000
},
{
    stampStr: '2008-04-01T05:00:00Z',
    value:1996.3000
},
{
    stampStr: '2008-05-01T05:00:00Z',
    value:1852.0000
},
{
    stampStr: '2008-06-01T05:00:00Z',
    value:2113.3333
},
{
    stampStr: '2008-07-01T05:00:00Z',
    value:2132.0000
},
{
    stampStr: '2008-08-01T05:00:00Z',
    value:1591.0323
},
{
    stampStr: '2008-09-01T05:00:00Z',
    value:1573.0000
},
{
    stampStr: '2008-10-01T05:00:00Z',
    value:1666.5484
},
{
    stampStr: '2008-11-01T05:00:00Z',
    value:1673.0000
},
// override by ted
//{ stampStr: '2008-12-01T05:00:00Z'	, value: 	1673.0000	},
// theese are from ted
{
    stampStr: '2008-12-01T05:00:00Z'	,
    value: 	1572.6774
},
{
    stampStr: '2009-01-01T05:00:00Z'	,
    value: 	1681.0968
},
{
    stampStr: '2009-02-01T05:00:00Z'	,
    value: 	1717.1071
},
{
    stampStr: '2009-03-01T05:00:00Z'	,
    value: 	1472.3548
},
{
    stampStr: '2009-04-01T05:00:00Z'	,
    value: 	1524.4667
},
{
    stampStr: '2009-05-01T05:00:00Z'	,
    value: 	1238.0000
},
{
    stampStr: '2009-06-01T05:00:00Z'	,
    value: 	1219.3667
},
{
    stampStr: '2009-07-01T05:00:00Z'	,
    value: 	1180.9677
},
{
    stampStr: '2009-08-01T05:00:00Z'	,
    value: 	1320.0323
},
{
    stampStr: '2009-09-01T05:00:00Z'	,
    value: 	1123.2000
},
{
    stampStr: '2009-10-01T05:00:00Z'	,
    value: 	1427.2581
}

];
hydroData.reverse();
// sql to gen from ted.watt_day
//mysql -N -B -e "select concat(' { stampStr: ''',left(stamp,7),'-01T05:00:00Z'''),', value: ',avg(watt),'},' from watt_day group by left(stamp,7)" ted
var tedData = [
{
    stampStr: '2008-07-01T05:00:00Z'	,
    value: 	1359.3333
},
{
    stampStr: '2008-08-01T05:00:00Z'	,
    value: 	1452.8065
},
{
    stampStr: '2008-09-01T05:00:00Z'	,
    value: 	1621.6000
},
{
    stampStr: '2008-10-01T05:00:00Z'	,
    value: 	1767.3667
},
{
    stampStr: '2008-11-01T05:00:00Z'	,
    value: 	1644.6667
},
{
    stampStr: '2008-12-01T05:00:00Z'	,
    value: 	1572.6774
},
{
    stampStr: '2009-01-01T05:00:00Z'	,
    value: 	1681.0968
},
{
    stampStr: '2009-02-01T05:00:00Z'	,
    value: 	1717.1071
},
{
    stampStr: '2009-03-01T05:00:00Z'	,
    value: 	1472.3548
},
{
    stampStr: '2009-04-01T05:00:00Z'	,
    value: 	1524.4667
},
{
    stampStr: '2009-05-01T05:00:00Z'	,
    value: 	1238.0000
},
{
    stampStr: '2009-06-01T05:00:00Z'	,
    value: 	1219.3667
},
{
    stampStr: '2009-07-01T05:00:00Z'	,
    value: 	1180.9677
},
{
    stampStr: '2009-08-01T05:00:00Z'	,
    value: 	1320.0323
},
{
    stampStr: '2009-09-01T05:00:00Z'	,
    value: 	1123.2000
},
{
    stampStr: '2009-10-01T05:00:00Z'	,
    value: 	1427.2581
}


];
tedData.reverse();

function makeDatesFromStrForFeed(data) {
    for (var i = 0; i < data.length ; i++) {
        var ostamp = new Date();
        ostamp.setISO8601(data[i].stampStr);
        data[i].stamp = ostamp;
    /*var observation = {
                        stamp: ostamp,
                        value: obsList.item(j).getAttribute("value")
                    }*/
    }
}
makeDatesFromStrForFeed(hydroData);
makeDatesFromStrForFeed(tedData);
var monthsToShow=15;
var staticYearFeed = {
    name: "Year",
    stamp: hydroData[0].stamp,
    value: hydroData[0].value,
    observations:  hydroData.slice(0,monthsToShow) ,
    compareobs: hydroData.slice(12,monthsToShow+12)
}
var sum12Month=0;
$.each(hydroData.slice(0,12),function(){
    sum12Month+=this.value
});
staticYearFeed.value = sum12Month/12.0;
            //alert("lastMo:"+hydroData[0].value+" -12 mo avg:"+staticYearFeed.value);
            //alert(staticYearFeed.compareobs.length);
            //alert("hydroData: "+hydroData.length);
