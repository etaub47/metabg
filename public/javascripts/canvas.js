
$(function() {

    var canvas = document.getElementById('viewport');
    var ctx = canvas.getContext('2d');
    var images = new Array();
    
    ctx.fillStyle = "blue";
    ctx.font = "24pt Helvetica";
    ctx.fillText("Loading Resources...", 10, 250);
    
    var resources = context.resources;
    var imageCounter = 0;
    for (var r in resources) {
    	var resource = resources[r];
    	if (resource.imageFront) {
    		images[imageCounter] = new Image();
    		images[imageCounter].src = "/resources/images/" + resource.imageFront;
    	
    		/*
    		images[imageCounter].addEventListener("load", function() {
    	  		// execute drawImage statements here
    		}, false);
    		 */
    		
    		imageCounter++;
    	}    	
    }
    
    
	
})
