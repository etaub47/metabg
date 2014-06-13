
$(function() {

	var wsUri = new String(window.location.href).replace("http", "ws") + "/connect";
	var me = window.location.href.substring(window.location.href.lastIndexOf("/") + 1);
	var range = 4;
	var zoom = 3;
	var pan_x = (2560 - window.innerWidth) / 180;
	var pan_y = (1600 - window.innerHeight) / 110;
	var gameState;
	var messages = new Array();
	
	// TODO; limit the panning to the edge of the table

	var table = document.getElementById('table');
	var tableCtx = table.getContext('2d');
	var canvas = document.getElementById('viewport');
	var ctx = canvas.getContext('2d');

	displayMessage("Loading Resources...");
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
    	redraw();
    	displayMessage("Connecting to Server...");
        websocket = new WebSocket(wsUri);
        websocket.onopen = function(evt) { onOpen(evt) };
        websocket.onclose = function(evt) { onClose(evt) };
        websocket.onmessage = function(evt) { onMessage(evt) };
        websocket.onerror = function(evt) { onError(evt) };
    }

    function onOpen (evt) {
    	displayMessage("Waiting for all players to connect...");
    }

    function onClose (evt) {
    	redraw();
    	displayMessage("Disconnected");
    }

    function onMessage (evt) {
    	gameState = jQuery.parseJSON(evt.data);
        loadSprites();
        displayImportantMessage();
    }

    function onError (evt) {
    	displayMessage('Error: ' + evt.data);
    }

    function doSend (message) {
        websocket.send(message);
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
    
    function displayImportantMessage () {
    	if (gameState.status == "WaitingForConnections")
    		displayMessage("Waiting for all players to connect...");
    	else if (gameState.status == "WaitingForReconnections") {
    		if (gameState.disconnected.length == 1)
    			displayMessage("Waiting for " + gameState.playerNames[gameState.disconnected[0]] + " to reconnect...");
    		else    		
    			displayMessage("Waiting for all players to reconnect...");
    	}
    	else if (gameState.status == "InProgress") { // TODO: handle GameOver state
    		var foundMe = false;
    		var prompt = "";
    		for (var actionIndex in gameState.expectedActions) {
    			var action = gameState.expectedActions[actionIndex];
    			if (action.player == me) {
    				foundMe = true; 
    				displayMessage(action.prompt);
    			}
    		}
    		if (!foundMe) {
    			if (gameState.expectedActions.length == 1) {
    				var action = gameState.expectedActions[0];
    				displayMessage("Waiting for " + gameState.playerNames[action.player]);
    			}
    			else
    				displayMessage("Waiting for other players");
    		}    		
    	}    		
    }

    // zoom = 0-10, pan_x = 0-50, y = 0-50
    function redraw () {    	
    	ctx.setTransform(1, 0, 0, 1, 0, 0);
    	ctx.clearRect(0, 0, canvas.width, canvas.height);
    	ctx.setTransform(0.2857 + (0.07143 * zoom), 0, 0, 0.2857 + (0.07143 * zoom), 0 - (50.5 * pan_x), 0 - (35 * pan_y));
    	ctx.drawImage(table, 0, 0);
        ctx.setTransform(1, 0, 0, 1, 0, 0);
        ctx.fillStyle = "black";
        ctx.font = "12pt Helvetica";
        // ctx.fillText(window.innerWidth + "," + window.innerHeight, 50, 50);
        ctx.fillText(messages[0], 10, 25);
        ctx.fillText(messages[1], 10, 45);
        ctx.fillText(messages[2], 10, 65);
    };

    function displayMessage (message) {
    	if (messages.length < 3)
    		messages[messages.length] = message;
    	else {
    		messages[0] = messages[1];
    		messages[1] = messages[2];
    		messages[2] = message;
    	}
    	redraw();
    }
    
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
    	else if (e.which == 13 || e.which == 32) {
    		alert("SUBMIT");
    	}
    	else if (e.which == 27 || e.which == 8 || e.which == 46) {
    		alert("UNDO");
    	}
    });

    $(window).resize(function() {
    	document.getElementById('viewport').width = window.innerWidth - 20;
    	document.getElementById('viewport').height = window.innerHeight - 30;
    	//range = 
    	redraw();
    });

})
