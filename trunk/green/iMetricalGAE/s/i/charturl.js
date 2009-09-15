function chartURL(feed){
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
                return ""+pad(stamp.getHours())+":"+pad(stamp.getMinutes())
            };
            isBarChart=false;
            units = "kW";
            multiplier=1.0/1000.0;
            break;
        case "Day":
            timeFormat = function(stamp){
                return ""+pad(stamp.getHours())+":"+pad(stamp.getMinutes())
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

    // not used yet
    /*
                var hasPredictor = (feed.compareobs!=null);
                var currentColumn=1;
                if (hasPredictor) currentColumn=2;
                 */

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
        //backgroundColor: "#444444",
        backgroundColor: "#000000",
        //backgroundColor: rgba(85,255,0,.8),
        axisColor:"#444",
        axisBackgroundColor:"#222",
        //colors:["#7f93bc","#3b5998"],
        colors:["#55ff00","#cccccc"],
        width: 320,
        height: 180,
        min: 0,
        pointSize: 1,
        is3D: false,
        title: getI18n(feed.name)+' - '+getI18n('Power Consumption')+' ('+getI18n(units)+')',
        titleY: getI18n(units),
        titleFontSize: 12,
        titleColor: "#fff",
        legend:legend,
        legendBackgroundColor:"#000",
        legendTextColor: "#fff",
        legendFontSize:8

    };
    chart.draw(data, options);
}
