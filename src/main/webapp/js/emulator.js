/**
 * Created by Dmytro_Kovalskyi on 10.07.2014.
 */

var ROTATION_CHANGED = 11;
var wsUri = getRootUri() + "/server/rotation";
function getRootUri() {
    return "ws://" + (document.location.hostname == "" ? "localhost" : document.location.hostname) + ":" + (document.location.port == "" ? "8080" : document.location.port);
}

console.log("Connecting to " + wsUri);
var websocket = new WebSocket(wsUri);
websocket.onopen = function (evt) {
    onOpen(evt);
};
websocket.onmessage = function (evt) {
    onMessage(evt);
};
websocket.onerror = function (evt) {
    onError(evt);
};

var output = document.getElementById("output");


function onOpen() {
    console.log("onOpen");
    writeToScreen("CONNECTED TO : " + wsUri);
}

function onMessage(evt) {
    var id = JSON.parse(evt.data).response.deviceId;
    writeToScreen("DEVICE ID : " + id);
    console.log("RECEIVED : " + evt.data);
}

function onError(evt) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

function sendMessage(message) {
    var json = JSON.stringify(message);
    console.log("Send : " + json);
    websocket.send(json);
}

function sendData() {
    var rot = {
        x: Math.random().toFixed(3),
        y: Math.random().toFixed(3),
        z: Math.random().toFixed(3),
        t: Date.now()
    };

    var message = {
        messageType: ROTATION_CHANGED,
        requestData: rot
    };

    sendMessage(message);

    writeToScreen("Sent : " + JSON.stringify(message))
}

function writeToScreen(message) {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    output.appendChild(pre);
}