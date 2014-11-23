

function HandleError(xhr){
    if(xhr.status != 200){
        console.log(xhr);
        alert(xhr.responseText);
    }
}

function Start() {
    $.ajax({
        method: 'POST',
        url: "game/start",
        success: function(){
            document.location = 'game.html';
        },
        error: HandleError
    });
}

function Leave() {
    $.ajax({
        method: "DELETE",
        url: "game/leave",
        success: function () {
            document.location = "lobby.html";
        }
    });
}

function IndicateReady(ready){
     $.ajax({
        method: "POST",
        url: "user/ready",
        data: {'ready': ready },
        success: function () {
            RefreshReadyIndicator();
        },
        error: HandleError
    });
}

function RefreshReadyIndicator(){
    $.ajax({
        method: 'GET',
        url: 'user/details',
        success: function(data){
            if(data.ready){
                $('#start').html('WAIT PLS');
                $('#start').click(function(){
                    IndicateReady(false);
                });
            } else {
                $('#start').html('READY');
                $('#start').click(function(){
                    IndicateReady(true);
                });
            }
        }      
    });
}

function RefreshPlayers(){
    $.ajax({
        method: 'GET',
        url: 'game/details',
        success: function(data){
            // If game has already started redirect the player
            if(data.gameStarted){
                document.location = 'game.html';
                return;
            }
            
            $('#roomName').text(data.host);
            $('table#playerList tbody').empty();
            
            var count = 0;
            for(var i in data.allPlayers){
                var player = data.allPlayers[i];
                
                var row = $('<tr>').addClass('playerRow');
                if(player.name === data.host){
                    row.addClass('host');
                }
                
                if(player.name === data.user){
                    row.addClass('self');
                }
                
                row.append($('<td>').text(player.name).addClass('playerNameColumn'));
                
                // Put checkmark if ready, x if not
                var ready = player.ready ? "\u2713" : "\u2717";
                
                // Dont show ready for the host and for myself
                if(player.name == data.host || player.name == data.user){
                    ready = '';
                }
                
                row.append($('<td>').text(ready).addClass('playerColorColumn'));
                $('table#playerList tbody').append(row);
                
                ++count;
            }
            
            for(var i = count; i < 4; i++){
                var row = $('<tr>').addClass('playerRow').addClass('empty');
                
                row.append($('<td>').text('Empty').addClass('playerNameColumn'));
                row.append($('<td>').addClass('playerColorColumn'));
                $('table#playerList tbody').append(row);
            }
            
            // Set the params
            var paramMapping = {
                'numberOfRobots': 'robotRow',
                'numberOfSnakes': 'snakeRow',
                'numberOfLadders': 'ladderRow',
                'boardSize': 'sizeRow'
            };
            for(var field in paramMapping){
                $('#' + paramMapping[field] + ' div.button').removeClass('button-selected');
                $('#' + paramMapping[field] + ' div.button[button-value=' + data[field] + ']').addClass('button-selected');    
            }
            
            // Am I not the host?
            if(data.host !== data.user){
                // Can't start game, but can only indicate that one is ready
                RefreshReadyIndicator();
            } else {
                $('#start').unbind('click');
                $('#start').click(Start);
                
                for(var field in paramMapping){
                    $('#' + paramMapping[field] + ' div.button').click(function(){
                        $(this).parent().children().removeClass('button-selected');
                        $(this).addClass('button-selected');
                        SaveGameParams();
                    });
                }
            }
        }
    });
}

/**
 * Send Ajax request to /game/robots
 */
function SaveGameParams() {
    var robots = $('div#robotRow div.button-selected').attr('button-value');
    var boardSize = $('div#sizeRow div.button-selected').attr('button-value');
    var snakes = $('div#snakeRow div.button-selected').attr('button-value');
    var ladders = $('div#ladderRow div.button-selected').attr('button-value');
    
    $.ajax({
            method: 'POST',
            url: 'game/params',
            data: JSON.stringify({
                robots: robots,
                size: boardSize,
                snakes: snakes,
                ladders: ladders
            }),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (data) {
                RefreshPlayers();
            },
            error: HandleError
    });
}

$(RefreshPlayers);

// Periodically refresh
setInterval(RefreshPlayers, 1000);