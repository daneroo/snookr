var timeplot;
function onLoad() {
    var eventSource = new Timeplot.DefaultEventSource();

    var timeGeometry = new Timeplot.DefaultTimeGeometry({
        gridColor: new Timeplot.Color("#000000"),
        axisLabelsPlacement: "bottom"
    });

    var valueGeometry = new Timeplot.DefaultValueGeometry({
        gridColor: "#000000",
        min: 0,
        max: 100
    });

    var plotInfo7 = [
    Timeplot.createPlotInfo({
        id: "plot1",
        dataSource: new Timeplot.ColumnSource(eventSource,1),
        timeGeometry: timeGeometry,
        valueGeometry: valueGeometry,
        lineColor: "#ff00",
        //fillColor: "#cc8080",
        fillColor: "#00cc00",
        showValues: true
    }),
    Timeplot.createPlotInfo({
        id: "plot2",
        dataSource: new Timeplot.ColumnSource(eventSource,3),
        timeGeometry: timeGeometry,
        valueGeometry: valueGeometry,
        lineColor: "#D0A825",
        showValues: true
    })
    ];

    timeplot =  Timeplot.create(document.getElementById("timeplotdiv"), plotInfo7);
    timeplot.loadText("bush_ratings.txt", ",", eventSource);
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
