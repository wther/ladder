/**
 * To board is 600x600
 */
var SIZE = 600;

MAX_PLAYERS = 4;

var IMGSRC_RED = '../images/token_red.png';
var IMGSRC_GREEN = '../images/token_green.png';
var IMGSRC_BLUE = '../images/token_blue.png';
var IMGSRC_YELLOW = '../images/token_yellow.png';

var IMGSRC_LADDER = '../images/ladder_';

var LADDER_IMAGECOUNT = 6;

var LADDER_SIZE_STEP = 80;
//var ladder_bounds = [100, 150, 200, 400,600,800]

var IMGSRC_SNAKE = '../images/snake_';

var SNAKE_IMAGECOUNT = 2;
var SNAKE_SIZE_STEP = 400;

//storing board data here
var boardData;

//layer that contains the players
var playerLayer;

//simple js image objects
var playerImages;

//the kinetic images, stored as a dictionary with colors as keys
// example: the red player is reached by playerTokens["RED"]
var playerTokens;

//js image objects for ladder
var ladderImages;

//js image objects for snakes
var snakeImages;

//kinetic field rectangles
var fieldRects;

//js image objects
var diceImages;


//the player obj that is me of the boardData (not the kinetic image)
function playerMe() {
	for(var i = 0; i < boardData.players.length; i++){
		var player = boardData.players[i];
		if(player.isMe) {
			return player;
		}
	}
}

/**
 * returns the path of the token image corresponding to the colors
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

//index is starting from 0
function ladderImageSourceForIndex(index) {
	return IMGSRC_LADDER + index + ".png";
}

function snakeImageSourceForIndex(index) {
	return IMGSRC_SNAKE + index + ".png";
}

//distance between two points
function getDistance(x1, y1, x2, y2) {
	return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
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
		Y: pixels.centerY + pixels.width * radius * Math.sin(Math.PI * (1/rotMax + 2/rotMax * i)) - pixels.width * scale/2,
		W: pixels.width * scale,
		H: pixels.height * scale
	};
}

function fieldToVectorPos(field, boardSize) {
	var width = parseInt(Math.sqrt(boardSize));
    if(width*width !== boardSize){
        throw "Unexpected board size: " + boardSize;
    }
    
    var y = width - 1 - Math.floor(field / width);
    
    // Follow a spiral root
    var x = (y % 2 === 1) ? field  % width : (width - 1 - field % width);
    return {
    	X: x,
    	Y: y,
    	W: width
    };
}

/**
 * Returns to pixel coordinates for field x;y given the size and ratio of the
 * canvas
 */
function fieldToPixels(field, boardSize, pixelSize, padding) {
    var Pos = fieldToVectorPos(field, boardSize);
    var x = Pos.X;
    var y = Pos.Y;
    var width = Pos.W;

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

// the inverse of fieldToPixels for event handling
function pixelToField(x, y) {
	console.log("x, y: " + x + ", " + y);
	var boardSize = boardData.size;
	var width = parseInt(Math.sqrt(boardSize));
	var pixelSize = SIZE;
	//var padding = 0.06;
	
	var cellWidth = pixelSize / width;
	var cellHeight = pixelSize / width;
	var i = Math.floor(x / cellWidth);
	var j = width - 1 - Math.floor(y / cellHeight);
	var position = j * width;
	position += (j % 2 === 1) ? width - i - 1 : i;
	
	return position;
}

// returns a kinetic image
function createLadderImage(fromX, fromY, toX, toY) {
	
	var length = getDistance(fromX, fromY, toX, toY);
	
	var imageIndex = Math.floor(length / LADDER_SIZE_STEP);
	if(imageIndex < 0) ImageIndex = 0;
	if(imageIndex >= LADDER_IMAGECOUNT) imageIndex = LADDER_IMAGECOUNT - 1;
	
	var rot = Math.atan2(toY - fromY, toX - fromX) * 180.0 / Math.PI;
	
	var dirX = (toX - fromX) / length;
	var dirY = (toY - fromY) / length;
	
	var corrDirX = -dirY;
	var corrDirY = dirX;
	
	var W = SIZE/15;
	var image = new Kinetic.Image({
		x: fromX + corrDirX * W/2,
		y: fromY + corrDirY * W/2,
		image: ladderImages[imageIndex],
		width: W,
		height: length,
		rotation: rot - 90,
		opacity: 0.8
	});
	
	return image;	
}

// same as createLadderImage but with snakes
function createSnakeImage(fromX, fromY, toX, toY) {
	
	var length = getDistance(fromX, fromY, toX, toY);
	
	var imageIndex = Math.floor(length / SNAKE_SIZE_STEP);
	if(imageIndex < 0) ImageIndex = 0;
	if(imageIndex >= SNAKE_IMAGECOUNT) imageIndex = SNAKE_IMAGECOUNT - 1;
	
	var rot = Math.atan2(toY - fromY, toX - fromX) * 180.0 / Math.PI;
	
	var dirX = (toX - fromX) / length;
	var dirY = (toY - fromY) / length;
	
	var corrDirX = -dirY;
	var corrDirY = dirX;
	
	var W = SIZE/20  + length / 50;
	var image = new Kinetic.Image({
		x: fromX + corrDirX * W/2,
		y: fromY + corrDirY * W/2,
		image: snakeImages[imageIndex],
		width: W,
		height: length,
		rotation: rot - 90,
		opacity: 0.8
	});
	
	return image;	
}


var stage;
var boardLayer;

/**
 * Draw board from scratch, it works based on the assumption that images are already loaded : player token, ladder (and snake) images
 */
function drawBoard(board) {

	boardData = board;
	
	stage = new Kinetic.Stage({
		container : 'container',
		width : 600,
		height : 600
	});

	boardLayer = new Kinetic.Layer();
	fieldRects = [];
	
	// Draw board
	for(var i = 0; i < board.size; i++){
            var pixels = fieldToPixels(i, board.size, SIZE, 0.06);
            var rect = new Kinetic.Rect({
                x : pixels.topX,
                y : pixels.topY,
                width : pixels.width,
                height : pixels.height,
                fill : '#aaffaa',
                stroke : 'black',
                strokeWidth : 1
            });
            rect.position = i;
            rect.on('click', function(evt) {
                console.log("rect clicked: " + evt.target.position);
              });
            fieldRects[i] = rect;
            boardLayer.add(rect);
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
		playerTokens[player.color] = new Kinetic.Image({
			x: Dim.X,
			y: Dim.Y,
			image: playerImages[player.color],
			width: Dim.W,
			height: Dim.H,
			visible: true,
		});
		playerTokens[player.color].i = i;
		playerLayer.add(playerTokens[player.color]);
	}
	stage.add(playerLayer);

        
    // Draw snakes and ladders
    var snakeLayer = new Kinetic.Layer();
    for(var i in board.snakes){
        var snake = board.snakes[i];
        
        var from = fieldToPixels(snake.from, board.size, SIZE);
        var to = fieldToPixels(snake.to, board.size, SIZE);
        
        snakeLayer.add(createSnakeImage(from.centerX, from.centerY, to.centerX, to.centerY));
        
        snakeLayer.add(new Kinetic.Line({
            points: [from.centerX, from.centerY, to.centerX, to.centerY],
            stroke: "green",
            tension: 1,
            opacity: 0.5
        }));
    }
    stage.add(snakeLayer);
    
    var ladderLayer = new Kinetic.Layer();
    for(var i in board.ladders) {
        var ladder = board.ladders[i];
        
        var from = fieldToPixels(ladder.from, board.size, SIZE);
        var to = fieldToPixels(ladder.to, board.size, SIZE);
        
        ladderLayer.add(createLadderImage(from.centerX, from.centerY, to.centerX, to.centerY));
        
        ladderLayer.add(new Kinetic.Line({
            points: [from.centerX, from.centerY, to.centerX, to.centerY],
            stroke: "brown",
            tension: 1,
            opacity: 0.5
        }));
    }
    stage.add(ladderLayer);
    
    stage.getContent().addEventListener('click', stageClicked);
}

//loading image resources
function loadResources() {
	playerImages = {};
	var board = boardData;
	var maxCount = board.players.length + LADDER_IMAGECOUNT + SNAKE_IMAGECOUNT;
	var loadCount = 0;
	for(var i = 0; i < board.players.length; i++){
		var player = board.players[i];
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
	
	ladderImages = [];
	for(var i = 0; i < LADDER_IMAGECOUNT; i++) {
		var ladderImageObj = new Image();
		ladderImageObj.i = i;
		ladderImageObj.onload = function(ev) {
			loadCount++;
			
			ladderImages[ev.target.i] = ev.target;
			if(loadCount == maxCount) {
				resourcesLoaded();
			}
		};
		ladderImageObj.src = ladderImageSourceForIndex(i);
	}
	snakeImages = [];
	for(var i = 0; i < SNAKE_IMAGECOUNT; i++) {
		var snakeImageObj = new Image();
		snakeImageObj.i = i ;
		snakeImageObj.onload = function(ev) {
			loadCount++;
			
			snakeImages[ev.target.i] = ev.target;
			if(loadCount == maxCount) {
				resourcesLoaded();
			}
		};
		snakeImageObj.src = snakeImageSourceForIndex(i);
	}
	diceImages = [];
	for(var i = 0; i < 6; i++) {
		diceImages[i] = $('#dice_' + (i+1));
	}
	
	
	
}

//this func is called when images are loaded
function resourcesLoaded() {
	drawBoard(boardData);
	processAnimations();
}

//shows the die with rollValue
function showDice(rollValue) {
	for(var i = 0; i < 6; i++) {
		diceImages[i].css("display", "none");
	}
	diceImages[rollValue - 1].css("display", "inline-block");
}

var rollAnim;

var justRolled = false;
//dice is rolled, player has to click on correspondent field to advance
function rolled() {

	$("#roll_button").attr("disabled", "disabled");
	justRolled = true;
	var stateChange = getNextStateChanges()[0];
	if(playerMe().color === stateChange.playerColor) {
		safeProcessAnimations();
	}
	
}

//clickBlocking means that no stateChanges must be animated until the player clicks on the correct field
var clickBlocking = false;
//fieldClicked is true when the player has clicked on the correct field
var fieldClicked = false;

//this function makes the player click on the next field where the player needs to move
function clickFieldBlocking(stateChange) {
	clickBlocking = true;
	var rollValue = stateChange.to - stateChange.from;
	//showDice(rollValue);
	
	playerMustClickHere = stateChange.to;
	animateFieldToBeClicked(playerMustClickHere);
}

// animates the area to be clicked within animate
function animateFieldToBeClicked(pos) {
	stopRollAnim();
	var period = 2000;
	var field = fieldRects[pos];
	var defFill = field.fill();
	rollAnim = new Kinetic.Animation(function(frame) {
        var scale = Math.sin(frame.time * 2 * Math.PI / period) / 2 + 0.5;
        var color = 'rgb(120,' + (200 + Math.floor(scale*55) )+ ',' + (150 + Math.floor(scale*105)) + ')';
        field.fill(color);
      }, boardLayer);
	rollAnim.targetObject = field;
	rollAnim.objectDefStateName = "fill";
	rollAnim.objectDefStateVal = defFill;
	rollAnim.start();
}

function stopRollAnim() {
	if(rollAnim == undefined) {
		return;
	}
	rollAnim.stop();
	
	var stateName = rollAnim.objectDefStateName;
	var stateVal = rollAnim.objectDefStateVal;
	rollAnim.targetObject.fill(stateVal);
	boardLayer.draw();
	
}

//stores the position where the player must click
var playerMustClickHere;

//detects if the player clicked on the correct field after a roll
function stageClicked(evt) {
	console.log("stage Clicked");
	var XY = stage.getPointerPosition();
	var x = XY.x;
	var y = XY.y;
	var pixels = fieldToPixels(17, boardData.size, SIZE, 0.06);
	var pos = pixelToField(x, y);
	console.log("position: " + pos);
	if(playerMustClickHere === pos) {
		stopRollAnim();
		
		fieldClicked = true;
		processAnimations();
	}
	
}

//only calls processAnimations if it's not currently processing
function safeProcessAnimations() {
	if(!processing) {
		processAnimations();
	}
}

var processing = false;
//storing the last animated stateChange's sequenceNumber 
var processedUntilSequenceNumber = 0;

//will be used for optimisation - so that the for loop doesn't need to loop through all stateChanges
var currentArrayIndex = 0;


function getNextStateChanges() {
	var stateChanges = [];
	for(var j = currentArrayIndex; j < boardData.stateChanges.length; j++) {
		var stateChange = boardData.stateChanges[j];
		if(stateChange.sequenceNumber > processedUntilSequenceNumber) {
			stateChanges.push(stateChange);
			for(var i = j+1; i < boardData.stateChanges.length; i++) {
				var otherStateChange = boardData.stateChanges[i];
				if(otherStateChange.sequenceNumber == stateChange.sequenceNumber) {
					stateChanges.push(otherStateChange);
				}
				else if(otherStateChange.sequenceNumber > stateChange.sequenceNumber) {
					break;
				}
			}
			
			return stateChanges;
		}
	}
	return null;
}
// if last player animated is the same as current to be animated player, 
//it is climbing/sliding on a ladder/snake, so no need for a click (clickFieldBlocking)
var lastPlayerAnimated;
function processAnimations() {
	//animateFieldClicked means the player clicked on the right field, and this time we can animate that stateChange
	var animateFieldClicked = false;
	if(fieldClicked) {
		clickBlocking = false;
		animateFieldClicked = true;
		fieldClicked = false;
	}
	if(clickBlocking) {
		return;
	}
	
	processing = true;
	
	//we animate one animation, and it's onfinish will call this function back
	var stateChanges = getNextStateChanges();
	
	if(stateChanges != null) {
		if(stateChanges.length == 1) {
			var stateChange = stateChanges[0];
			//if this was a dice roll, then show the dice
			var rollMove = lastPlayerAnimated == undefined || stateChange.playerColor != lastPlayerAnimated;
			
			if(rollMove) {
				showDice(stateChange.to - stateChange.from);
			}
			//if it's us, we may need to make the user click on the corresponding field only then will the animation be played
			if(playerMe().color === stateChange.playerColor && justRolled) {
				justRolled = false;
				showDice(stateChange.to - stateChange.from);
				clickFieldBlocking(stateChange);
			}
//			if(playerMe().color === stateChange.playerColor && !animateFieldClicked && stateChange.playerColor != lastPlayerAnimated) {
//				clickFieldBlocking(stateChange);
//			}
			else {
				lastPlayerAnimated = stateChange.playerColor;
				animateStateChange(stateChange, boardData, rollMove, processAnimations);
				processedUntilSequenceNumber = stateChange.sequenceNumber;
			}
		}
		//earthquake - if there are more than one stateChanges with the same sequenceNumber
		else {
			animateStateChanges(stateChanges, boardData);
			processedUntilSequenceNumber = stateChanges[0].sequenceNumber;
		}
		
	}
	else {
		processing = false;
		if(boardData.nextPlayer == null || boardData.nextPlayer.color === playerMe().color) {
			$("#roll_button").removeAttr("disabled");
		}
		
	}
	
}

//this part is for getting around corners nicely
var tStateChanges;
var tboard;
var trollMove;
var tfinishFunc;
function animateStateChangeVoid() {
	var stateChange = tStateChanges.shift();
	var finishFunc = (tStateChanges.length == 0) ? tfinishFunc : animateStateChangeVoid;
	
	animateStateChange(stateChange, tboard, trollMove, finishFunc);
}

//for corner animations
function animateStateChangesSequentially(stateChanges, board, finishFunc) {
	tStateChanges = stateChanges;
	tboard = board;
	tfinishFunc = finishFunc;
	trollMove = false;
	animateStateChangeVoid();
}

//rollMove indicates if the move is not a ladder/snake movement and so
// if passes on a corner it needs to be sequenced
function animateStateChange(stateChange, board, rollMove, finishFunc) {	
	var playerToAnimate;
	
	playerToAnimate = playerTokens[stateChange.playerColor];
	
	if( playerToAnimate === undefined) {
		throw "bad argument in animateStateChange";
	}
	
	if(rollMove) {
		var Pos1 = fieldToVectorPos(stateChange.from, board.size);
		var Pos2 = fieldToVectorPos(stateChange.to, board.size);
		//passing a corner
		if(Pos1.Y != Pos2.Y) {
			var stateChanges = [];
			var cornerField1 = stateChange.from + (9 - (stateChange.from % 10));
			var cornerField2 = cornerField1 + 1;
			if(stateChange.from != cornerField1) {
				stateChanges.push({
					from: stateChange.from,
					to: cornerField1,
					sequenceNumber: stateChange.sequenceNumber,
					playerColor: stateChange.playerColor
				});
			}
			stateChanges.push({
				from: cornerField1,
				to: cornerField2,
				sequenceNumber: stateChange.sequenceNumber,
				playerColor: stateChange.playerColor
			});
			if(cornerField2 != stateChange.to) {
				stateChanges.push({
					from: cornerField2,
					to: stateChange.to,
					sequenceNumber: stateChange.sequenceNumber,
					playerColor: stateChange.playerColor
				});
			}
			animateStateChangesSequentially(stateChanges, board, finishFunc);
			return;
		}
		//otherwise just animate it regularly
	}
	
	
	var fromDim = getTokenDim(stateChange.from, playerToAnimate.i, board.size);
	var toDim = getTokenDim(stateChange.to, playerToAnimate.i, board.size);
	
	playerToAnimate.attrs.x = fromDim.X;
	playerToAnimate.attrs.y = fromDim.Y;
	playerToAnimate.show();
	
	var dist = getDistance(fromDim.X, fromDim.Y, toDim.X, toDim.Y);
	dist /= SIZE/10;
	console.log("SN " + stateChange.sequenceNumber + " ,dist " + dist);
	
	var tween = new Kinetic.Tween({
		  node: playerToAnimate,
		  x: toDim.X,
		  y: toDim.Y,
		  duration: 1 + dist / 10.0,
		  easing: Kinetic.Easings.BounceEaseOut,
		  onFinish: finishFunc
	});
	
	tween.play();
	
	
}

//for earthquake
function animateStateChangesSimultaneously(stateChanges, board) {
	var maxCount = stateChanges.length;
	var count = 0;
	for(var i = 0; i < stateChanges.length; i++) {
		var stateChange = stateChanges[i];
		var playerToAnimate;
		
		playerToAnimate = playerTokens[stateChange.playerColor];
		
		if( playerToAnimate === undefined) {
			throw "bad argument in animateStateChanges";
		}
		
		var fromDim = getTokenDim(stateChange.from, playerToAnimate.i, board.size);
		var toDim = getTokenDim(stateChange.to, playerToAnimate.i, board.size);
		
		playerToAnimate.attrs.x = fromDim.X;
		playerToAnimate.attrs.y = fromDim.Y;
		playerToAnimate.show();
		
		var dist = getDistance(fromDim.X, fromDim.Y, toDim.X, toDim.Y);
		dist /= SIZE/10;
		console.log("SN " + stateChange.sequenceNumber + " ,dist " + dist);
		
		var tween = new Kinetic.Tween({
			  node: playerToAnimate,
			  x: toDim.X,
			  y: toDim.Y,
			  duration: 1 + dist / 10.0,
			  easing: Kinetic.Easings.BounceEaseOut,
			  onFinish: function() {
				  count++;
				  if(count == maxCount) {
					  processAnimations();
				  }
			  }
		});
		
		tween.play();
	}
}

//refreshes the board data but does not redraw it
function refreshBoardData() {
	$.get('board', function(data) {
    	boardData = data;
    	
    	safeProcessAnimations();
    });
}
	

/**
 * Fetch board from backend and draw it
 */
function refreshBoard() {
    $.get('board', function(data) {
    	boardData = data;
    	var stateChanges = boardData.stateChanges;
    	if(stateChanges.length > 0) {
    		processedUntilSequenceNumber = stateChanges[stateChanges.length - 1].sequenceNumber;
    	}
    	
        loadResources();
    });
}

$('#roll_button').click(function(){
    $.ajax({
        url: 'board/action',
        data: {action: 'ROLL'},
        method: 'POST',
        success: function(data){
//            drawBoard(data);
//            processAnimations();
        	boardData = data;
        	rolled();
        	
        }
    });
});

refreshBoard();

setInterval(refreshBoardData, 1500);
