
$(function() {

    var uri = new String(window.location.href).replace("http", "ws");
    var queryStringIndex = uri.indexOf("?");
    if (queryStringIndex > 0) uri = uri.substring(0, queryStringIndex);    
    var wsUri = uri + "/connect";
    
    var me = uri.substring(uri.lastIndexOf("/") + 1);
    var range = 4, zoom = context.zoom;
    var pan_x = context.pan_x; //(2560 - window.innerWidth) / 180;
    var pan_y = context.pan_y; //(1600 - window.innerHeight) / 110;
    var gameState, myOptions, prompt, phase = "";
    var messages = ["", "", "", ""];
    var history = new Array();
    
    var table = document.getElementById('table');
    var tableCtx = table.getContext('2d');
    var canvas = document.getElementById('viewport');
    var ctx = canvas.getContext('2d');

    displayPrompt("Loading Resources...");
    window.images = new Array();    
    window.numLoaded = 0;
    window.images["table"] = new Image();
    window.images["table"].src = "/resources/images/table.jpg";
    
    function checkLoaded () {
        window.numLoaded++;
        if (window.numLoaded == Object.keys(context.resources).length)
            openWebSocket();        
    };
    
    var resources = context.resources;
    var imageCounter = 0;
    jQuery.each(resources, function(resourceName, resourceImage) {
        window.loaded = false;
        window.images[resourceName] = new Image();
        window.images[resourceName].src = "/resources/images/" + resourceImage;            
        window.images[resourceName].onload = checkLoaded;
    });
    
    function openWebSocket () {
        redraw();
        displayPrompt("Connecting to Server...");
        websocket = new WebSocket(wsUri);
        websocket.onopen = function(evt) { onOpen(evt) };
        websocket.onclose = function(evt) { onClose(evt) };
        websocket.onmessage = function(evt) { onMessage(evt) };
        websocket.onerror = function(evt) { onError(evt) };
    }

    function onOpen (evt) {
        displayPrompt("Waiting for all players to connect...");
    }

    function onClose (evt) {
        redraw();
        displayPrompt("Disconnected");
    }

    function onMessage (evt) {
        if (evt.data.indexOf("ERROR: ") == 0)
            displayPrompt(evt.data.substring("ERROR: ".length));
        else if (evt.data.indexOf("MESSAGE: ") == 0)
            displayMessage(evt.data.substring("MESSAGE: ".length));
        else {
            gameState = jQuery.parseJSON(evt.data);
            loadSprites();
            displayImportantMessage();
        }
    }

    function onError (evt) {
        displayPrompt('Error: ' + evt.data);
    }

    function doSend (message) {
        websocket.send(message);
    }

    function loadSprites () {
        spritesLoaded = true;
        tableCtx.drawImage(window.images["table"], 0, 0);
        for (var l in gameState.userInterface) {
            var layer = gameState.userInterface[l];
            for (var s in layer.sprites) {
                var sprite = layer.sprites[s];
                var image = window.images[sprite.resource];
                tableCtx.drawImage(image, sprite.x, sprite.y);
            }
            for (var r in layer.regions) {
                var region = layer.regions[r];
                if (region.highlightColor) {
                    tableCtx.beginPath();
                    tableCtx.rect(region.x, region.y, region.width, region.height);
                    tableCtx.lineWidth = 5;
                    tableCtx.strokeStyle = region.highlightColor;
                    tableCtx.stroke();
                }
            }
        }        
        document.getElementById('viewport').width = window.innerWidth - 20;
        document.getElementById('viewport').height = window.innerHeight - 30;
        redraw();      
    };
    
    function displayImportantMessage () {
        myOptions = [];
        if (gameState.status == "WaitingForConnections")
        	displayPrompt("Waiting for all players to connect...");
        else if (gameState.status == "WaitingForReconnections") {
            if (gameState.disconnected.length == 1)
            	displayPrompt("Waiting for " + gameState.playerNames[gameState.disconnected[0]] + " to reconnect...");
            else            
            	displayPrompt("Waiting for all players to reconnect...");
        }
        else if (gameState.status == "InProgress") {
            var foundMe = false;
            prompt = "";
            phase = gameState.phase;
            for (var a in gameState.actions) {
                var action = gameState.actions[a];
                if (action.player == me) {
                    foundMe = true;
                    myOptions = action.options;
                    displayPrompt(action.prompt);
                }
            }
            if (!foundMe) {
                if (gameState.actions.length == 1) {
                    var action = gameState.actions[0];
                    displayPrompt("Waiting for " + gameState.playerNames[action.player]);
                }
                else
                	displayPrompt("Waiting for other players");
            }            
        }
    }

    // zoom = 0-10, pan_x = 0-50, y = 0-50
    function redraw () {        
        ctx.setTransform(1, 0, 0, 1, 0, 0);
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = "black";
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.setTransform(0.2857 + (0.07143 * zoom), 0, 0, 0.2857 + (0.07143 * zoom), 0 - (50.5 * pan_x), 0 - (35 * pan_y));
        ctx.drawImage(table, 0, 0);
        ctx.setTransform(1, 0, 0, 1, 0, 0);
        ctx.font = "12pt Helvetica";
        ctx.fillStyle = "white";
        ctx.fillText(prompt, 10, 25);
        ctx.fillStyle = "yellow";
        ctx.fillText(phase, 10, 45);
        ctx.fillStyle = "black";
        ctx.fillText(messages[0], 10, 85);
        ctx.fillText(messages[1], 10, 105);
        ctx.fillText(messages[2], 10, 125);
        ctx.fillStyle = "white";
        ctx.fillText(messages[3], 10, 145);
    };

    function displayMessage (message) {
        if (messages.length < 4)
            messages[messages.length] = message;
        else {
            messages[0] = messages[1];
            messages[1] = messages[2];
            messages[2] = messages[3];
            messages[3] = message;
        }
        history[history.length - 1] = message;
        redraw();
    }
    
    function displayPrompt (promptStr) {
    	prompt = promptStr;
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
            for (var o in myOptions)
                if (myOptions[o].category == "Confirm")
                    doSend("Confirm|OK");
        }
        else if (e.which == 27 || e.which == 8 || e.which == 46) {
            for (var o in myOptions)
                if (myOptions[o].category == "Undo")
                    doSend("Undo|OK");
        }
    });
    
    $("#viewport").click(function(e) {
        for (var o in myOptions) {
            var option = myOptions[o];
            if (option.category == "TableClick") {
                tableX = Math.round((e.pageX + (50.5 * pan_x)) / (0.2857 + (0.07143 * zoom)));
                tableY = Math.round((e.pageY + (35 * pan_y)) / (0.2857 + (0.07143 * zoom)));
                for (regionIndex in gameState.userInterface[option.value].regions) {
                    region = gameState.userInterface[option.value].regions[regionIndex];
                    if (tableX > region.x && tableX < region.x + region.width && tableY > region.y && tableY < region.y + region.height)
                        doSend("TableClick|" + region.id);
                }
            }
        }        
    });

    $(window).resize(function() {
        document.getElementById('viewport').width = window.innerWidth - 20;
        document.getElementById('viewport').height = window.innerHeight - 30;
        //range = 
        redraw();
    });

})
