var CONNECTION_ESTABLISHED = 10, KEY_PRESSED = 11, PIN_TOGGLE = 20, RASPBERRY_LOGGING = 90;

var wsUri = getRootUri() + "/robot-server/control";
function getRootUri()
{
    return "ws://" + (document.location.hostname == "" ? "localhost" : document.location.hostname) + ":" + (document.location.port == "" ? "8080" : document.location.port);
}

console.log("Connecting to " + wsUri);

var websocket = new WebSocket(wsUri);
websocket.onopen = function (evt)
{
    onOpen(evt);
};
websocket.onmessage = function (evt)
{
    onMessage(evt);
};
websocket.onerror = function (evt)
{
    onError(evt);
};

var output = document.getElementById("output");
function onOpen()
{
    console.log("CONNECTED to : " + wsUri);
    writeToScreen("CONNECTED to : " + wsUri);
}

function onMessage(evt)
{
    var data = JSON.parse(evt.data);
    console.log("RECEIVED (text): " + evt.data);
    switch (data.messageType)
    {
        case RASPBERRY_LOGGING :
            showLog(data.requestData.message, data.requestData.level.localizedName);
            break;
    }
}

function showLog(msg, level)
{
    switch (level)
    {
        case "INFO" :
            writeToScreen(msg);
            break;
        case "SEVERE":
            writeToScreen('<span style="color: red;">' + msg + '</span>');
    }
}

function onError(evt)
{
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

function writeToScreen(message)
{
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    output.appendChild(pre);
}


function sendKeyPressed(code)
{
    sendMessage({messageType: KEY_PRESSED, requestData: {keyCode: code}});
}

function sendPinToggle()
{
    var number = $('#pinSelection').val();
    sendMessage({messageType: PIN_TOGGLE, requestData: {pinNumber: number}});
}

function sendMessage(message)
{
    var json = JSON.stringify(message);
    console.log("Send : " + json);
    websocket.send(json);
}
