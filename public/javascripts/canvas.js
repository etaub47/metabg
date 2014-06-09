
$(function() {

	var wsUri = new String(window.location.href).replace("http", "ws") + "/connect";
	var range = 4;
	var zoom = 3;
	var pan_x = (2560 - window.innerWidth) / 180;
	var pan_y = (1600 - window.innerHeight) / 110;
	var gameState;
	
	// TODO; limit the panning to the edge of the table

	var table = document.getElementById('table');
	var tableCtx = table.getContext('2d');
	var canvas = document.getElementById('viewport');
	var ctx = canvas.getContext('2d');

	writeToScreen("Loading Resources...");
	window.images = new Array();    
	window.numLoaded = 0;
	window.images["table"] = new Image();
	window.images["table"].src = "/resources/images/table.png";
    
    // TODO: deal with closures without using globals?
    
    function checkLoaded () {
    	window.numLoaded++;
    	if (window.numLoaded == Object.keys(context.resources).length)
    		openWebSocket();    	
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
    
    function openWebSocket () {
    	writeToScreen("Connecting to Server...");
        websocket = new WebSocket(wsUri);
        websocket.onopen = function(evt) { onOpen(evt) };
        websocket.onclose = function(evt) { onClose(evt) };
        websocket.onmessage = function(evt) { onMessage(evt) };
        websocket.onerror = function(evt) { onError(evt) };
    }

    function onOpen (evt) {
    	writeToScreen("Waiting for Other Players...");
    }

    function onClose (evt) {
        writeToScreen("Disconnected");
    }

    function onMessage (evt) {
    	gameState = jQuery.parseJSON(evt.data);
        loadSprites();
    }

    function onError (evt) {
        writeToScreen('Error: ' + evt.data);
    }

    function doSend (message) {
        websocket.send(message);
    }

	// TODO: remove
    function writeToScreen (message) {
      	ctx.setTransform(1, 0, 0, 1, 0, 0);
      	ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = "blue";
        ctx.font = "24pt Helvetica";
        ctx.fillText(message, 10, 250);
    }
      
    function loadSprites () {
    	spritesLoaded = true;
    	tableCtx.drawImage(window.images["table"], 0, 0);
    	var sprites = gameState.sprites;
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
