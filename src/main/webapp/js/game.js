/**
 * To board is 600x600
 */
var SIZE = 600;

MAX_PLAYERS = 4;

var IMGSRC_RED = '../images/token_red.png';
var IMGSRC_GREEN = '../images/token_green.png';
var IMGSRC_BLUE = '../images/token_blue.png';
var IMGSRC_YELLOW = '../images/token_yellow.png';

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
 * Draw board from scratch
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
	
	var loadCount = 0;
	for(var i = 0; i < board.players.length; i++){
		var player = board.players[i];
		
		var imageObj = new Image();
		imageObj.playerColor = player.color;
		imageObj.Dim = getTokenDim(player.position, i, board.size);
		imageObj.i = i;
		imageObj.onload = function(ev) {
			loadCount++;
			var Dim = ev.target.Dim;
			var playerImage = new Kinetic.Image({
				x: Dim.X,
				y: Dim.Y,
				image: ev.target,
				width: Dim.W,
				height: Dim.H
			});
			
			playerImage.i = ev.target.i;
			
			playerTokens[ev.target.playerColor] = playerImage;
			
			playerLayer.add(playerImage);
			if(loadCount == board.players.length) {
				addToStage(stage, board);
			}
		};
		imageObj.src = imageForColor(player.color);
		
		
	  }
	

        
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
    
    
    if(loaded1) {
		processAnimations();
	}else {
		loaded2 = true;
	}
    processAnimations();
    
    loaded2 = true;
}

var loaded1 = false;
var loaded2 = false;
function addToStage(stage) {
	stage.add(playerLayer);
	
	
	if(loaded2) {
		processAnimations();
	}else {
		loaded1 = true;
	}
}

	  
var Storage = {
	    set: function(key, value) {
	        localStorage[key] = JSON.stringify(value);
	    },
	    get: function(key) {
	        return localStorage[key] ? JSON.parse(localStorage[key]) : null;
	    }
	};

var processedUntilSequenceNumber = 0;
var currentArrayIndex = 0;

var boardData;

function processAnimations() {
	//storage is used so refreshing does not make the game animate it from the beginning
	//processedUntilSequenceNumber = Storage.get("processedUntilSequenceNumber") || 0;
	
	//we animate one animation, and it's onfinish will call this function back
	for(var j = currentArrayIndex; j < boardData.stateChanges.length; j++) {
		var stateChange = boardData.stateChanges[j];
		if(stateChange.sequenceNumber > processedUntilSequenceNumber) {
			console.log(stateChange.sequenceNumber);
			processedUntilSequenceNumber = stateChange.sequenceNumber;
			currentArrayIndex = j+1;
			animateStateChange(stateChange, boardData);
			break;
		}
	}
	//Storage.set("processedUntilSequenceNumber", processedUntilSequenceNumber);
}

var playerLayer;

var playerTokens;

function testFunc() {
	debugger;
}

function animateStateChange(stateChange, board) {
	var playerToAnimate;
	
	playerToAnimate = playerTokens[stateChange.playerColor];
	if( playerToAnimate === undefined) {
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
	
	var tween2 = new Kinetic.Tween({
		  node: playerToAnimate,
		  x: fromDim.X,
		  y: fromDim.Y,
		  duration: 0.5,
		  easing: Kinetic.Easings.BounceEaseOut,
		  onFinish: function() {
			  playerToAnimate.setPosition(fromDim.X, fromDim.Y);
				playerToAnimate.show();
				playerLayer.draw();
				var tween = new Kinetic.Tween({
					  node: playerToAnimate,
					  x: toDim.X,
					  y: toDim.Y,
					  duration: 2,
					  easing: Kinetic.Easings.BounceEaseOut
					  //onFinish: processAnimations(board)
				});
				tween.onFinish = processAnimations;
				tween.play();
		  }
	});
	tween2.play();
//	playerToAnimate.setPosition(fromDim.X, fromDim.Y);
//	playerToAnimate.show();
//	playerLayer.draw();
//	var tween = new Kinetic.Tween({
//		  node: playerToAnimate,
//		  x: toDim.X,
//		  y: toDim.Y,
//		  duration: 2,
//		  easing: Kinetic.Easings.BounceEaseOut
//		  //onFinish: processAnimations(board)
//	});
//	tween.onFinish = processAnimations;
//	tween.play();
	
	//
//	var myfunc = function () {
//	  	if(count == 0) return;
//	  	count--;
//	    currentX += 60;
//	    var tween = new Kinetic.Tween({
//    		  node: sprite,
//    		  x: currentX,
//    		  y: 550,
//    		  duration: 0.5,
//    		  easing: Kinetic.Easings.BounceEaseOut
//	    });
//	    tween.onFinish =  myfunc;
//	    tween.play();
//  }
//  
//  myfunc();
	
//	playerToAnimate.setPosition(100, 100);
//	var tween = new Kinetic.Tween({
//		  node: playerToAnimate,
//		  x: 200,
//		  y: 200,
//		  duration: 2,
//		  easing: Kinetic.Easings.BounceEaseOut
//		  //onFinish: processAnimations(board)
//	});
//	tween.play();
	
	
	
}


	

/**
 * Fetch board from backend and draw it
 */
function refreshBoard() {
    $.get('board', function(data) {
        drawBoard(data);
    });
}

$('#roll_button').click(function(){
    $.ajax({
        url: 'board/action',
        data: {action: 'ROLL'},
        method: 'POST',
        success: function(data){
            drawBoard(data);
        }
    });
});

refreshBoard();