var CONNECTION_ESTABLISHED = 10, KEY_PRESSED = 11, PIN_TOGGLE = 20, SET_SERVO_ANGLE = 21, STOP_MOVEMENT = 25, VOICE_SYNTHESIZE = 30, LOAD_PHRASES = 31, RASPBERRY_LOGGING = 90, EXECUTE_ACTION = 99;

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
        case CONNECTION_ESTABLISHED:
            sendMessage({messageType: LOAD_PHRASES});
            break;
        case LOAD_PHRASES:
            onLoadPhrases(data.requestData);
            break;
    }
}

function onLoadPhrases(phrases){
    var select = document.getElementById('selectedPhrase');
    for(var i in phrases){
         var phrase = replaceSubstring(decodeURIComponent(phrases[i]), "+", " ");
         var opt = document.createElement('option');
         opt.value = i;
         opt.innerHTML = phrase;
         select.appendChild(opt);
    }
}

function replaceSubstring(inSource, inToReplace, inReplaceWith) {
    var outString = inSource;
    while (true) {
      var idx = outString.indexOf(inToReplace);
      if (idx == -1) {
        break;
      }
      outString = outString.substring(0, idx) + inReplaceWith +
        outString.substring(idx + inToReplace.length);
    }
    return outString;
}

function showLog(msg, level)
{
    switch (level)
    {
        case "INFO" :
            writeToScreen(msg);
            break;
        case "DEBUG" :
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
    var pre = document.createElement("li");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    output.appendChild(pre);
    output.scrollTop = output.scrollHeight;
}

function onStopMovement()
{
    sendMessage({messageType: STOP_MOVEMENT});
}

function onExecuteButton()
{
    sendMessage({messageType: EXECUTE_ACTION});
}

function onForwardDirection(){
    sendMessage({messageType: SET_SERVO_ANGLE, requestData: {angle: 40}})
}

function onSetAngleButton()
{
    var angle = $('#servoAngle').val();
    sendMessage({messageType: SET_SERVO_ANGLE, requestData: {angle: angle}})
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

function onSynthesizeById() {
    var id = $('#selectedPhrase').val();
    sendMessage({messageType: VOICE_SYNTHESIZE, requestData: {phraseId: id}});
}

function onSynthesize(){
    var phrase = encodeURIComponent($('#speakPhrase').val());
    sendMessage({messageType: VOICE_SYNTHESIZE, requestData: {phraseId: 0, text:phrase}});
}

function sendMessage(message)
{
    var json = JSON.stringify(message);
    console.log("Send : " + json);
    websocket.send(json);
}
