function addChartImg(parentID,feedName,feedUnits){
    // im-chart-img is now just a div: using background-image
    var html = '<div class="im-chart im-feed-'+feedName+'">'+
    '      <div class="im-chart-img"  />'+
    '      <div class="im-chart-text">'+
    '           <span class="im-feed-name">'+getI18n(feedName)+'</span>'+
    '           <span class="im-v-'+feedUnits.suffix+'">'+feedUnits.format+'</span>'+
    '           <span class="im-feed-units">'+getI18n(feedUnits.name)+'</span>'+
    '      <div>'+
    '</div>';
    $(parentID).append(html);
// hiding and decorationg is performed in add6Badges.
}

function chartURL(feed,options){
    /* The actual vaues in tha graph data are decaWatts (avg power), we only scale the axis Label!
     *
     * options:
     *   width: default 320
     *   height: default 180
     *   bgColor: no hash mark
     *      e.g. cfc, ccffcc, may include alpha: ccffcc77
     *   penColors: array of colors e.g. ['55ff00','33aa00']
     *   textColor: as bg.
     */
    calcOptions={ // copy over theese defaults
      width: 320,
      height:180,
      bgColor: '00000000', // alpha==0;
      penColors: ['55ff00','33aa00'],
      textColor: 'cccccc'
    };
    for (var key in options) {
        calcOptions[key] = options[key];
    }

    var weekDayNames=["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
    var monthNames=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
    var isBarChart=true; // switch areachart(line) or columnchart(bar)
    var units = "?";
    var multiplier=1.0;
    var yAxisQuant=1.0;
    var LineChartType='lc';
    var BarChartGroupedType='bvg';
    var BarChartStackedType='bvs';
    var chartType=null;
    switch (feed.name){
        case "Live":
            chartType=LineChartType;
            timeFormat = function(stamp){
                //return ""+stamp.getHMS()
                if (stamp.getSeconds()%30!=0) return "";
                return ""+pad(stamp.getHours())+":"+pad(stamp.getMinutes())+":"+pad(stamp.getSeconds())
            };
            isBarChart=false;
            //units = "Watts";
            //multiplier=1.0;
            //yAxisQuant=1000;
            units = "kW";
            multiplier=1.0/1000.0;
            yAxisQuant=1.0;
            break;
        case "Hour":
            chartType=LineChartType;
            timeFormat = function(stamp){
                if (stamp.getMinutes()%15!=0) return "";
                return ""+pad(stamp.getHours())+":"+pad(stamp.getMinutes())
            };
            isBarChart=false;
            units = "kW";
            multiplier=1.0/1000.0;
            yAxisQuant=1.0;
            break;
        case "Day":
            chartType=BarChartGroupedType;
            timeFormat = function(stamp){
                if (stamp.getHours()%6!=0) return "";
                return ""+pad(stamp.getHours())+":"+pad(stamp.getMinutes())
            };
            units = "kWh";
            multiplier=1.0/1000.0;
            yAxisQuant=2.0;
            break;
        case "Week":
            chartType=BarChartGroupedType;
            timeFormat = function(stamp){
                return getI18n(weekDayNames[stamp.getDay()]);
            };
            units = "kWh/d";
            multiplier=24.0/1000.0;
            yAxisQuant=10.0;
            break;
        case "Month":
            chartType=BarChartGroupedType;
            timeFormat = function(stamp){
                //return getI18n(monthNames[stamp.getMonth()])+" "+stamp.getDate();
                var dd = stamp.getDate();
                if (dd==1) return getI18n(monthNames[stamp.getMonth()]).substr(0, 3)+" 1";
                if (dd==10 || dd==20) return dd;
                return "";
            };
            units = "kWh/d";
            multiplier=24.0/1000.0;
            yAxisQuant=10.0;
            break;
        case "Year":
            chartType=BarChartStackedType;
            timeFormat = function(stamp){
                var month =  getI18n(monthNames[stamp.getMonth()]).substr(0,2);
                return month;
                return getI18n(monthNames[stamp.getMonth()])+" "+stamp.getFullYear();
            };
            units = "kWh/d";
            multiplier=24.0/1000.0;
            yAxisQuant=10.0;
            break;
        default : // nothing
    }

    var hasPredictor = (feed.compareobs!=null);

    // build value array decaWattAA=[[1,2,3,4],[5,4,3,2]]
    // and time array:   timeA=[]
    var decaWattAA=[[],[]]; // for compare values
    timeA=[];
    // reverse order
    var decaWattMax=0;
    for (var i = feed.observations.length-1; i>=0 ; i--) {
        var o = feed.observations[i];
        var timeLabel = ""+timeFormat(o.stamp);
        timeA.push(timeLabel);
        var decaWatt=Math.round(o.value/10.0);
        decaWattAA[0].push(decaWatt);
        if (decaWattMax<decaWatt) {
            decaWattMax=decaWatt;
        }
        if (hasPredictor) {
            var c = feed.compareobs[i];
            decaWatt=Math.round(c.value/10.0);
            decaWattAA[1].push(decaWatt);
            if (decaWattMax<decaWatt) {
                decaWattMax=decaWatt;
            }
        }
    }
    
    // Quantize yAxis values
    var yAxisMax = Math.ceil((decaWattMax*10.0*multiplier)/yAxisQuant)*yAxisQuant;
    var yAxis=[0,yAxisMax/2,yAxisMax];
    // text encoded data (with scaling) see also Simple and Extended Coding..
    var dataEncoding='t:'+decaWattAA[0].join(',');
    if (hasPredictor) {
        if (chartType==BarChartStackedType){
            for(i=0;i<decaWattAA[1].length;i++){
                decaWattAA[1][i]-=decaWattAA[0][i];
                if (decaWattAA[1][i]<0) decaWattAA[1][i]=0;
            }
        }
        dataEncoding += '|'+decaWattAA[1].join(',');
    }
    var txtClr = calcOptions['textColor'];
    var optsinuse = {
        cht: chartType, /* chart type: lc, bvg, bvs*/
        chts: txtClr,
        //chxs: '0,ffffff,12,0,lt,ffffff|1,ffffff,12,1,lt,ffffff',
        chxs: '0,'+txtClr+',12,0,lt,'+txtClr+'|1,'+txtClr+',12,1,lt,'+txtClr+'',
        chf: 'bg,s,'+calcOptions['bgColor'], // background fill may include alpha
        chs: ''+calcOptions['width']+'x'+calcOptions['height'],
        chbh: 'a', // bar auto-sizing
        chd: dataEncoding,
        chds: [0,yAxisMax/10.0/multiplier].join(','),
        chco: calcOptions['penColors'].join(','),
        chxt: 'x,y', // list of x,y,r,t for bott,left,right,top
        chxl: '0:|'+timeA.join('|')+'|1:|'+yAxis.join('|'),
        chtt: getI18n(feed.name)+' - '+getI18n('Power Consumption')+' ('+getI18n(units)+')'
    };
    if (chartType==LineChartType) {
        optsinuse['chm'] = 'B,'+calcOptions['penColors'][1]+',0,0,0'; // fill area under line chart
    }
    if (feed.name=='Year'){
        // legend and position
        optsinuse['chdl']= getI18n('Current Year')+'|'+getI18n('Previous Year');
        optsinuse['chdlp']= 'b';
    }
    var params=[];
    for (var key in optsinuse) {
        params.push([key, optsinuse[key]].join("="));
    }
    var chartURL = "http://chart.apis.google.com/chart?"+params.join("&");
    return chartURL;
}
