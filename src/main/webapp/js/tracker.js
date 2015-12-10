init();

var canvas;
var context;
var CANVAS_WIDTH;
var CANVAS_HEIGHT;
var timerId;

//set rightDown or leftDown if the right or left keys are down
function onKeyDown(evt) {
    console.log("ON onKeyDown");
    processKey(evt.keyCode);
}

//and unset them when the right or left key is released
function onKeyUp(evt) {
    console.log("ON onKeyUp");
    clear();
}

$(document).keydown(onKeyDown);
$(document).keyup(onKeyUp);

function clear() {
    context.fillStyle = "#eee";
    context.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    var width = CANVAS_WIDTH / 3;
    var height = CANVAS_HEIGHT / 2;
    emptyRect(CANVAS_WIDTH / 3, 0, width, height);
    for (var x = 0; x <= CANVAS_WIDTH / 3; x++) {
        emptyRect(x * CANVAS_WIDTH / 3, CANVAS_HEIGHT / 2, width, height);
    }
}

function processKey(code) {
    clear();
    var x, y;
    var notProcessed = false;
    if (code == 37) { // left
        x = 0;
        y = CANVAS_HEIGHT / 2;
    } else if (code == 38) { // up
        x = CANVAS_WIDTH / 3;
        y = 0;
    } else if (code == 39) { // right
        x = 2 * CANVAS_WIDTH / 3;
        y = CANVAS_HEIGHT / 2;
    } else if (code == 40) { // down
        x = CANVAS_WIDTH / 3;
        y = CANVAS_HEIGHT / 2;
    } else if (code == 32) {
        onStopMovement();
        notProcessed = true;
    } else if (code == 17){
        onForwardDirection();
        notProcessed = true;
    } else {
        notProcessed = true;
    }
    if (!notProcessed) {
        var width = CANVAS_WIDTH / 3;
        var height = CANVAS_HEIGHT / 2;
        rect(x, y, width, height);
        sendKeyPressed(code);
    }
}

function rect(x, y, w, h) {
    context.beginPath();
    context.lineWidth = "3";
    context.strokeStyle = "red";
    context.fillStyle = "#ccc";
    context.rect(x, y, w, h);
    context.stroke();
    context.closePath();
    context.fill();
}

function emptyRect(x, y, w, h) {
    context.fillStyle = "#eee";
    context.beginPath();
    context.lineWidth = "2";
    context.strokeStyle = "black";
    context.rect(x, y, w, h);
    context.stroke();
    context.fill();
    context.closePath();
}

function inRectangle(x, y, rectX, rectY) {
    var width = CANVAS_WIDTH / 3;
    var height = CANVAS_HEIGHT / 2;
    return x >= rectX && x <= rectX + width && y >= rectY && y <= rectY + height
}

function defineRectangle(event) {
    var x = event.x;
    var y = event.y;
    x -= canvas.offsetLeft;
    y -= canvas.offsetTop;

    processClick(x, y);
}

function processClick(x, y) {
    var notProcessed = false;
    var code;
    if (inRectangle(x, y, 0, CANVAS_HEIGHT / 2)) { // left
        code = 37;
    } else if (inRectangle(x, y, CANVAS_WIDTH / 3, 0)) { // up
        code = 38;
    } else if (inRectangle(x, y, 2 * CANVAS_WIDTH / 3, CANVAS_HEIGHT / 2)) { // right
        code = 39;
    } else if (inRectangle(x, y, CANVAS_WIDTH / 3, CANVAS_HEIGHT / 2)) { // down
        code = 40;
    } else {
        notProcessed = true;
    }
    if (!notProcessed) {
        processKey(code);
    }
}

function handleTouchStart(event) {
    var x = event.targetTouches[0].pageX;
    var y = event.targetTouches[0].pageY;
    timerId = setTimeout(function () {
        event.stopPropagation();
        event.preventDefault();
        processClick(x, y)
    }, 30);
}

function handleTouchEnd(event) {
    clearTimeout(timerId);
}

function init() {
    canvas = $('#canvas')[0];
    context = canvas.getContext("2d");
    CANVAS_WIDTH = $("#canvas").width();
    CANVAS_HEIGHT = $("#canvas").height();
    canvas.addEventListener("mousedown", defineRectangle, false);
    canvas.addEventListener("touchstart", handleTouchStart, false);
    canvas.addEventListener("touchend", handleTouchEnd, false);
    clear();
}