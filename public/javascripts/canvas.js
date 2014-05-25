
$(function() {

	var table = document.getElementById('table');
	var tableCtx = table.getContext('2d');
    var canvas = document.getElementById('viewport');
    var ctx = canvas.getContext('2d');
    
    window.images = new Array();
    window.numLoaded = 0;
    
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
    	ctx.clearRect(0, 0, canvas.width, canvas.height);
    	var sprites = context.sprites;
    	for (var l in sprites) {
    		var level = sprites[l];
    		for (var s in level) {
    			var sprite = level[s];
    			var image = window.images[sprite.resource];
    			//tableCtx.drawImage(image, sprite.x, sprite.y);
    			ctx.drawImage(image, sprite.x, sprite.y);
    		}
    	}
    };
    
})
