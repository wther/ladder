
function Start() {
	document.location.href = "/..";
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
                if(player === data.host){
                    row.addClass('host');
                }
                
                if(player === data.user){
                    row.addClass('self');
                }
                
                row.append($('<td>').text(player).addClass('playerNameColumn'));
                row.append($('<td>').addClass('playerColorColumn'));
                $('table#playerList tbody').append(row);
                
                ++count;
            }
            
            for(var i = count; i < 4; i++){
                var row = $('<tr>').addClass('playerRow').addClass('empty');
                
                row.append($('<td>').text('Empty').addClass('playerNameColumn'));
                row.append($('<td>').addClass('playerColorColumn'));
                $('table#playerList tbody').append(row);
            }
        }
    });
}

$(RefreshPlayers);