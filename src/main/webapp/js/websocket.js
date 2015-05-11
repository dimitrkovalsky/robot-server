var wsUri = getRootUri() + "/robot-server/control";
function getRootUri() {
    return "ws://" + (document.location.hostname == "" ? "localhost" : document.location.hostname) + ":" + (document.location.port == "" ? "8080" : document.location.port);
}


//var wsUri = "ws://" + document.location.host + "/server/rotation";
console.log("Connecting to " + wsUri);
var websocket = new WebSocket(wsUri);

var data = [];
var totalPoints = 50;
var updateInterval = 1000;
var now = new Date().getTime();
var initialized = false;
var iteration = 0;
function updateData(value) {
    if (data.length >= totalPoints)
        data.shift();
    //while (data.length < totalPoints) {
    var temp = [now += updateInterval, value];
    data.push(temp);
    //}
    update()
}

console.log("Connected");
var chart = [];
websocket.onmessage = function (event) {
    var value = parseInt(event);
    if (!initialized)
        graph(value);
    if (!isNaN(value)) {
        updateData(value)
    }
};

websocket.onerror = onalert;
websocket.onclose = onalert;

var onalert = function (event) {
    alert("Some problem ");
};

var options = {
    series: {
        lines: {
            show: true,
            lineWidth: 1.2,
            fill: true
        },
        points: {show: true, symbol: "circle"}
    },

    xaxis: {
        mode: "time",
        tickSize: [2, "second"],
        tickFormatter: function (v, axis) {
            var date = new Date(v);

            if (date.getSeconds() % 20 == 0) {
                var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
                var minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
                var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

                return hours + ":" + minutes + ":" + seconds;
            } else {
                return "";
            }
        },
        axisLabel: "Time",
        axisLabelUseCanvas: true,
        axisLabelFontSizePixels: 12,
        axisLabelFontFamily: 'Verdana, Arial',
        axisLabelPadding: 10
    },
    yaxis: {
        axisLabelUseCanvas: true,
        axisLabelFontSizePixels: 12,
        axisLabelFontFamily: 'Verdana, Arial',
        axisLabelPadding: 6
    },
    legend: {
        labelBoxBorderColor: "#fff"
    }
};
dataset = [
    {data: data}
];
function graph(value) {
    //for(i = 1; i < 50; i++)
    //  updateData(value);
    $.plot($("#graph"), dataset, options);
    initialized = true;
    // update();
}
function update() {
    $.plot($("#graph"), dataset, options)
}