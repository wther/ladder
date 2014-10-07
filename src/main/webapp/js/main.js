/**
 * To board is 600x600
 */
var SIZE = 600;

/**
 * Returns to pixel coordinates for field x;y given the size and ratio of the
 * canvas
 */
function fieldToPixels(x, y, board, size, padding) {

	// Default argument values
	if (size === undefined) {
		size = SIZE;
	}
	if (padding === undefined) {
		padding = 0;
	}

	var cellWidth = size / board.width;
	var cellHeight = size / board.height;

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

	// Board is either 600x600 or smaller but a square
	var size = 600;

	var stage = new Kinetic.Stage({
		container : 'container',
		width : 600,
		height : 600
	});

	var boardLayer = new Kinetic.Layer();

	// Draw board
	for (var j = 0; j < board.height; j++) {
		for (var i = 0; i < board.width; i++) {
			var pixels = fieldToPixels(i, j, board, SIZE, 0.06);
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
				text : "" + (j * board.height + i),
				fill : "black"
			}));
		}
	}

	// add the layer to the stage
	stage.add(boardLayer);
	
	// Draw player
	var playerLayer = new Kinetic.Layer();
	
	for(i in board.players){
		var pixels = fieldToPixels(board.players[i].position.x, board.players[i].position.y, board, SIZE);
		playerLayer.add(new Kinetic.Star({
			innerRadius: 25,
			outerRadius: 15,
			numPoints: 5,
			x: pixels.centerX,
			y: pixels.centerY,
			fill: "yellow"
		}));
	}
	
	stage.add(playerLayer);
}

/**
 * Fetch board from backend and draw it
 */
function refreshBoard() {
	$.get('board', function(data) {
		drawBoard(data);
	});
}
