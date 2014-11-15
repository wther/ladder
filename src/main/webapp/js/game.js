/**
 * To board is 600x600
 */
var SIZE = 600;

MAX_PLAYERS = 4;

var IMGSRC_RED = '../images/token_red.png';
var IMGSRC_GREEN = '../images/token_green.png';
var IMGSRC_BLUE = '../images/token_blue.png';
var IMGSRC_YELLOW = '../images/token_yellow.png';

var IMGSRC_LADDER = '../images/ladder.png';

//storing board data here
var boardData;

//layer that contains the players
var playerLayer;

//simple js image objects
var playerImages;

//the kinetic images, stored as a dictionary with colors as keys
// example: the red player is reached by playerTokens["RED"]
var playerTokens;

//js image object for ladder
var ladderImage;


/**
 * returns the path of the image corresponding to the colors
 */
function imageForColor(colorString) {
	switch(colorString) {
	case "RED":
		return IMGSRC_RED;
	case "GREEN":
		return IMGSRC_GREEN;
	case "BLUE":
		return IMGSRC_BLUE;
	case "YELLOW":
		return IMGSRC_YELLOW;
	default:
		throw "imageForColor bad argument: " + colorString;
	}
}


/**
 * returns the pixel dimensions for a player token that has i index
 * (the tokens are rotated around the center of the field)
 */ 
function getTokenDim(position, i, boardSize) {
	var radius = 0.2;
	var rotMax = MAX_PLAYERS;
	var scale = 0.7;
	var pixels = fieldToPixels(position, boardSize, SIZE);
	return {
		X: pixels.centerX + pixels.width * radius * Math.cos(Math.PI * (1/rotMax + 2/rotMax * i)) - pixels.width * scale/2,
		Y: pixels.centerY + pixels.width * radius * Math.sin(Math.PI * (1/rotMax + 2/rotMax * i)) - pixels.width * 0.35,
		W: pixels.width * scale,
		H: pixels.height * scale
	};
}

/**
 * Returns to pixel coordinates for field x;y given the size and ratio of the
 * canvas
 */
function fieldToPixels(field, boardSize, pixelSize, padding) {
    
        var width = parseInt(Math.sqrt(boardSize));
        if(width*width !== boardSize){
            throw "Unexpected board size: " + boardSize;
        }
        
        var y = width - 1 - Math.floor(field / width);
        
        // Follow a spiral root
        var x = (y % 2 === 1) ? field  % width : (width - 1 - field % width);

	// Default argument values
	if (pixelSize === undefined) {
		pixelSize = SIZE;
	}
	if (padding === undefined) {
		padding = 0;
	}

	var cellWidth = pixelSize / width;
	var cellHeight = pixelSize / width;

	return {
		topX : (x + padding) * cellWidth,
		topY : (y + padding) * cellHeight,
		bottomX : (x + 1 - padding) * cellWidth,
		bottomY : (y + 1 - padding) * cellHeight,
		centerX : (x + 0.5) * cellWidth,
		centerY : (y + 0.5) * cellHeight,
		width : cellWidth * (1 - 2 * padding),
		height : cellHeight * (1 - 2 * padding)
	};
}

/**
 * Draw board from scratch, it works based on the assumption that images are already loaded : player token, ladder (and snake) images
 */
function drawBoard(board) {

	boardData = board;
	
	var stage = new Kinetic.Stage({
		container : 'container',
		width : 600,
		height : 600
	});

	var boardLayer = new Kinetic.Layer();

	// Draw board
	for(var i = 0; i < board.size; i++){
            var pixels = fieldToPixels(i, board.size, SIZE, 0.06);
            boardLayer.add(new Kinetic.Rect({
                    x : pixels.topX,
                    y : pixels.topY,
                    width : pixels.width,
                    height : pixels.height,
                    fill : '#aaffaa',
                    stroke : 'black',
                    strokeWidth : 1
            }));
            boardLayer.add(new Kinetic.Text({
                    x : pixels.topX,
                    y : pixels.centerY,
                    width : pixels.width,
                    height : pixels.height,
                    align : "center",
                    text : "" + (i+1),
                    fill : "black"
            }));
        }
        

	// add the layer to the stage
	stage.add(boardLayer);
	
	// Draw players
	
	playerTokens = {};
	playerLayer = new Kinetic.Layer();
	
	
	for(var i = 0; i < board.players.length; i++){
		var player = board.players[i];
		var Dim = getTokenDim(player.position, i, board.size);
		playerTokens[player.color] = new kinetic.Image({
			x: Dim.X,
			y: Dim.Y,
			image: playerImages[player.color],
			width: Dim.W,
			height: Dim.H,
			visible: true
		});
			
		playerLayer.add(playerTokens[player.color]);
	}
	stage.add(playerLayer);

        
    // Draw snakes and ladders
    var snakeLayer = new Kinetic.Layer();
    for(var i in board.snakes){
        var snake = board.snakes[i];
        
        var from = fieldToPixels(snake.from, board.size, SIZE);
        var to = fieldToPixels(snake.to, board.size, SIZE);
        
        snakeLayer.add(new Kinetic.Line({
            points: [from.centerX, from.centerY, to.centerX, to.centerY],
            stroke: "green",
            tension: 1,
            opacity: 0.5
        }));
    }
    stage.add(snakeLayer);
    
    var ladderLayer = new Kinetic.Layer();
    for(var i in board.ladders){
        var ladder = board.ladders[i];
        
        var from = fieldToPixels(ladder.from, board.size, SIZE);
        var to = fieldToPixels(ladder.to, board.size, SIZE);
        
        ladderLayer.add(new Kinetic.Line({
            points: [from.centerX, from.centerY, to.centerX, to.centerY],
            stroke: "brown",
            tension: 1,
            opacity: 0.5
        }));
    }
    stage.add(ladderLayer);
    
    var testLayer = new Kinetic.Layer();
    var imageObj = new Image();
	imageObj.playerColor = player.color;
	imageObj.Dim = getTokenDim(player.position, i, board.size);
	imageObj.i = i;
	imageObj.onload = function(ev) {
		var Dim = ev.target.Dim;
		var playerImage = new Kinetic.Image({
			x: 50,
			y: 50,
			image: ev.target,
			width: 70,
			height: 70,
			visible: false
		});
		
		testImage = playerImage;
		
		testLayer.add(testImage);
		if(loadCount == board.players.length) {
			addToStage(stage, board);
		}
		stage.add(testLayer);
		
		testImage.attrs.x = 400;
		testImage.show();
//		testLayer.clear();
//		stage.clear();
//		stage.draw();
		

		
		var tween = new Kinetic.Tween({
			  node: testImage,
			  x: 100,
			  y: 50,
			  duration: 2,
			  easing: Kinetic.Easings.BounceEaseOut
			  //onFinish: processAnimations(board)
		});
		tween.play();
		
		
	};
	imageObj.src = imageForColor("RED");
    
    
}

//loading image resources
function loadResources() {
	playerImages = {};
	var board = boardData;
	var maxCount = board.players.length + 1;
	var loadCount = 0;
	for(var i = 0; i < board.players.length; i++){
		var imageObj = new Image();
		imageObj.playerColor = player.color;
		imageObj.onload = function(ev) {
			loadCount++;
			
			playerImages[ev.target.playerColor] = ev.target;
			if(loadCount == maxCount) {
				resourcesLoaded();
			}
		};
		imageObj.src = imageForColor(player.color);
	}
	
	var ladderImageObj = new Image();
	ladderImageObj.onload = function(ev) {
		loadCount++;
		
		ladderImage = ev.target;
		if(loadCount == maxCount) {
			resourcesLoaded();
		}
	};
	ladderImageObj.src = imageForColor(player.color);
	
}

//this func is called when images are loaded
function resourcesLoaded() {
	drawBoard(boardData);
	processAnimations();
}

//localstorage variable
//storing: Storage.set("key", value);
//loading: var val = Storage.get("key"); //returns null if key is not found
var Storage = {
	    set: function(key, value) {
	        localStorage[key] = JSON.stringify(value);
	    },
	    get: function(key) {
	        return localStorage[key] ? JSON.parse(localStorage[key]) : null;
	    }
	};


//storing the last animated stateChange's sequenceNumber 
var processedUntilSequenceNumber = 0;

//will be used for optimisation - so that the for loop doesn't need to loop through all stateChanges
var currentArrayIndex = 0;

function processAnimations() {
	//storage is used so refreshing does not make the game animate it from the beginning
	//processedUntilSequenceNumber = Storage.get("processedUntilSequenceNumber") || 0;
	
	//we animate one animation, and it's onfinish will call this function back
	for(var j = currentArrayIndex; j < boardData.stateChanges.length; j++) {
		var stateChange = boardData.stateChanges[j];
		if(stateChange.sequenceNumber > processedUntilSequenceNumber) {
			//console.log("SN: " + stateChange.sequenceNumber);
			animateStateChange(stateChange, boardData);
			processedUntilSequenceNumber = stateChange.sequenceNumber;
			//currentArrayIndex = j+1;
			//animateStateChange(stateChange, boardData);
			break;
		}
	}
	//Storage.set("processedUntilSequenceNumber", processedUntilSequenceNumber);
}



function testFunc() {
	debugger;
}

function animateStateChange(stateChange, board) {
	var playerToAnimate;
	
	playerToAnimate = playerTokens[stateChange.playerColor];
	if( playerToAnimate === undefined || playerToAnimate.tween != null) {
		console.log("dilation");
		setTimeout(function() {
			animateStateChange(stateChange, board);
		}, 200);
		return;
	}
	
	if( playerToAnimate === undefined) {
		throw "bad argument in animateStateChange";
	}
	
	var fromDim = getTokenDim(stateChange.from, playerToAnimate.i, board.size);
	var toDim = getTokenDim(stateChange.to, playerToAnimate.i, board.size);
	
	playerToAnimate.attrs.x = fromDim.X;
	playerToAnimate.attrs.y = fromDim.Y;
	playerToAnimate.show();
	
	var dist = (toDim.X - fromDim.X) * (toDim.X - fromDim.X) + (toDim.Y - fromDim.Y) * (toDim.Y - fromDim.Y);
	dist = Math.sqrt(dist);
	dist /= SIZE/10;
	console.log("SN " + stateChange.sequenceNumber + " ,dist " + dist);
	
	var tween = new Kinetic.Tween({
		  node: playerToAnimate,
		  x: toDim.X,
		  y: toDim.Y,
		  duration: dist * 1.0,
		  //easing: Kinetic.Easings.BounceEaseOut,
		  onFinish: function() {
			  playerToAnimate.tween = null;
			  processAnimations();
		  }
	});
	playerToAnimate.tween = tween;
	tween.play();
	
	
}


	

/**
 * Fetch board from backend and draw it
 */
function refreshBoard() {
    $.get('board', function(data) {
    	boardData = data;
        loadResources();
    });
}

$('#roll_button').click(function(){
    $.ajax({
        url: 'board/action',
        data: {action: 'ROLL'},
        method: 'POST',
        success: function(data){
            drawBoard(data);
            processAnimations();
        }
    });
});

refreshBoard();