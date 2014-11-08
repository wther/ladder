/**
 * To board is 600x600
 */
var SIZE = 600;


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
	var playerLayer = new Kinetic.Layer();
	
	for(var i in board.players){
		var pixels = fieldToPixels(board.players[i].position, board.size, SIZE);
		playerLayer.add(new Kinetic.Star({
			innerRadius: 25,
			outerRadius: 15,
			numPoints: 5,
			x: pixels.centerX,
			y: pixels.centerY,
			fill: board.players[i].color.toLowerCase()
		}));
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