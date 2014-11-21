
function Start() {
    $.ajax({
        method: 'POST',
        url: "game/start",
        success: function(){
            document.location = 'game.html';
        }
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
        }
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
                
                // Dont show ready the host
                if(player.name == data.host){
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
            
            // Set the robots number selector
            $('#robotRow div.button').removeClass('button-selected');
            $('#robotButton_' + data.numberOfRobots).addClass('button-selected');
            
            // Am I not the host?
            if(data.host !== data.user){
                // Can't start game, but can only indicate that one is ready
                RefreshReadyIndicator();
            } else {
                $('#start').click(Start);
                for(var i = 0; i < 4; i++){
                    var j = 0 + i;
                    $('#robotButton_' + i).click(function(){
                        SetNumberOfRobots($(this).attr('id').substr(-1));
                    });
                }
            }
        }
    });
}

/**
 * Send Ajax request to /game/robots
 */
function SetNumberOfRobots(number) {
    $.ajax({
            method: 'POST',
            url: 'game/robots',
            data: {number: number},
            success: function (data) {
                RefreshPlayers();
            }
    });
}

$(RefreshPlayers);