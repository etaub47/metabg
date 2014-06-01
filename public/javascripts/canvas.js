
var range = 4;
var zoom = 3;
var pan_x = (2560 - window.innerWidth) / 180;//7.5;
var pan_y = (1600 - window.innerHeight) / 110;//7.5;

$(function() {

	var table = document.getElementById('table');
	var tableCtx = table.getContext('2d');
    var canvas = document.getElementById('viewport');
    var ctx = canvas.getContext('2d');
    
    window.images = new Array();    
    window.numLoaded = 0;
    window.images["table"] = new Image();
    window.images["table"].src = "/resources/images/table.png";
    
    ctx.fillStyle = "blue";
    ctx.font = "24pt Helvetica";
    ctx.fillText("Loading Resources...", 10, 250);
    
    // TODO: deal with closures without using globals
    
    checkLoaded = function() {
    	window.numLoaded++;
    	if (window.numLoaded == Object.keys(context.resources).length)
    		loadSprites();    	
    };
    
    var resources = context.resources;
    var imageCounter = 0;
    jQuery.each(resources, function(resourceName, resource) {
    	if (resource.imageFront) {
    		window.loaded = false;
    		window.images[resourceName] = new Image();
    		window.images[resourceName].src = "/resources/images/" + resource.imageFront;    		
    		window.images[resourceName].onload = checkLoaded;    		
    	} 	
    });
    
    loadSprites = function() {
    	tableCtx.drawImage(window.images["table"], 0, 0);
    	var sprites = context.sprites;
    	for (var l in sprites) {
    		var level = sprites[l];
    		for (var s in level) {
    			var sprite = level[s];
    			var image = window.images[sprite.resource];
    			tableCtx.drawImage(image, sprite.x, sprite.y);
    		}
    	}
    	
    	document.getElementById('viewport').width = window.innerWidth - 20;
    	document.getElementById('viewport').height = window.innerHeight - 30;
    	redraw();
    	
        websocket = new WebSocket(wsUri);
        websocket.onopen = function(evt) { onOpen(evt) };
        websocket.onclose = function(evt) { onClose(evt) };
        websocket.onmessage = function(evt) { onMessage(evt) };
        websocket.onerror = function(evt) { onError(evt) };
    };
    
    // zoom = 0-10, pan_x = 0-50, y = 0-50
    redraw = function() {    	
    	ctx.setTransform(1, 0, 0, 1, 0, 0);
    	ctx.clearRect(0, 0, canvas.width, canvas.height);
    	ctx.setTransform(0.2857 + (0.07143 * zoom), 0, 0, 0.2857 + (0.07143 * zoom), 0 - (50.5 * pan_x), 0 - (35 * pan_y));
		ctx.drawImage(table, 0, 0);
ctx.setTransform(1, 0, 0, 1, 0, 0);
ctx.fillText(window.innerWidth + "," + window.innerHeight, 50, 50);    	
    };

    $(document).keydown(function(e) {
    	if (e.which == 65) {
    		if (zoom < 10) {
    			zoom++; pan_x += 2.5; pan_y += 2.5;
    		}    		
    		redraw();
    	}
    	else if (e.which == 90) {
    		if (zoom > 0) {
    			zoom--; pan_x -= 2.5; pan_y -= 2.5;
    		}
    		if (pan_x > (zoom * range)) pan_x = zoom * range;
    		if (pan_y > (zoom * range)) pan_y = zoom * range;
    		if (pan_x < 0) pan_x = 0;
    		if (pan_y < 0) pan_y = 0;
    		redraw();
    	}    	
    	else if (e.which == 37) {
    		if (pan_x > 0) pan_x--;
    		if (pan_x < 0) pan_x = 0;
    		redraw();
    	}
    	else if (e.which == 39) {
    		if (pan_x < range * zoom) pan_x++;
    		if (pan_x > range * zoom) pan_x = range * zoom;
    		redraw();
    	}
    	else if (e.which == 38) {
    		if (pan_y > 0) pan_y--;
    		if (pan_y < 0) pan_y = 0;
    		redraw();
    	}
    	else if (e.which == 40) {
    		if (pan_y < range * zoom) pan_y++;
    		if (pan_y > range * zoom) pan_y = range * zoom;
    		redraw();
    	}
    });
    
    $(window).resize(function() {
    	document.getElementById('viewport').width = window.innerWidth - 20;
    	document.getElementById('viewport').height = window.innerHeight - 30;
    	//range = 
    	redraw();
    });
    
})

  var wsUri = "ws://localhost:9000/metabg/Checkers/Testing/0/connect"; // TODO: compute from current server
  var output;

  function onOpen(evt)
  {
    writeToScreen("CONNECTED");
    doSend("WebSocket rocks");
  }

  function onClose(evt)
  {
    writeToScreen("DISCONNECTED");
  }

  function onMessage(evt)
  {
    writeToScreen('<span style="color: blue;">RESPONSE: ' + evt.data+'</span>');
    websocket.close();
  }

  function onError(evt)
  {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
  }

  function doSend(message)
  {
    writeToScreen("SENT: " + message); 
    websocket.send(message);
  }

  function writeToScreen(message)
  {
	  alert(message);
  }
