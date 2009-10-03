var timeplot;
function onLoad() {
    var eventSource = new Timeplot.DefaultEventSource();

    var timeGeometry = new Timeplot.DefaultTimeGeometry({
        gridColor: new Timeplot.Color("#000000"),
        axisLabelsPlacement: "bottom",
        //min:'2009-08-01'
        //min:'2009-08-01'
    });
    /*var timeGeometry = new Timeplot.MagnifyingTimeGeometry({
        gridColor: new Timeplot.Color("#000000"),
        axisLabelsPlacement: "bottom"
    });*/

    var valueGeometry = new Timeplot.DefaultValueGeometry({
        gridColor: "#000000"
        //min: 0,
        //max: 100
    });

    var plotInfo = [
    Timeplot.createPlotInfo({
        id: "plot1",
        dataSource: new Timeplot.ColumnSource(eventSource,1),
        timeGeometry: timeGeometry,
        valueGeometry: valueGeometry,
        lineColor: "#ff00",
        //fillColor: "#cc8080",
        fillColor: "#00cc00",
        showValues: true,
        roundValues:false
    })
    ];

    timeplot =  Timeplot.create(document.getElementById("timeplotdiv"), plotInfo);

    timeplot.loadText("weightrical-data.txt", ",", eventSource);
    //timeplot.loadText("bush_ratings.txt", ",", eventSource);
}

var resizeTimerID = null;
function onResize() {
    if (resizeTimerID == null) {
        resizeTimerID = window.setTimeout(function() {
            resizeTimerID = null;
            timeplot.repaint();
        //for (var i = 0; i < timeplots.length; i++) {
        //    timeplots[i].repaint();
        //}
        }, 500);
    }
}
