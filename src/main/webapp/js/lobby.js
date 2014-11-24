

function HandleError(xhr){
    console.log(xhr);
    alert(xhr.responseText);
}

function CreateRoom() {
    $.ajax({
        method: "POST",
        url: "game/new",
        success: function (data) {
            document.location = "room.html";
        },
        error: HandleError
    });
}

var clear = true;
function RoomNameFocus(obj) {
    if (clear) {
        obj.value = "";
        clear = false;
    }
}

var roomSelected = null;

function RoomClicked(room) {
    if (roomSelected !== null) {
        $(roomSelected).removeClass("selectedRoom");
    }
    if (room === roomSelected) {
        roomSelected = null;
        return;
    }
    roomSelected = room;
    $(roomSelected).addClass("selectedRoom");
}

function JoinRoomClicked() {
    if (roomSelected != null) {
        var roomId = $(roomSelected).attr('roomId');
        $.ajax({
            method: 'PUT',
            url: 'game/join/' + roomId,
            success: function(){
                document.location = "room.html";
            },
            error: HandleError
        });
    }
}

function RefreshRooms() {
    $.ajax({
        url: "games",
        success: function (data) {
            var table = $('table#gameList');
            // Remove all rows
            $('table#gameList tbody').empty();

            // Add rooms
            for (var i in data) {
                var tr = $('<tr>')
                        .addClass('roomRow')
                        .attr('roomId', data[i].gameId)
                        .unbind()
                        .click(function () {
                            RoomClicked(this);
                        });

                tr.append($('<td>')
                        .text(data[i].host)
                        .addClass('roomNameColumn'));

                tr.append($('<td>')
                        .text(data[i].allPlayers.length + '/4')
                        .addClass('playersColumn'));

                table.append(tr);
            }
        },
        error: HandleError
    });
}

function RefreshUserName(){
    $.ajax({
        url: "user/details",
        success: function (data) {
            $('#userName').val(data.name);
        },
        error: HandleError
    });
}

function SaveUserName(){
    $.ajax({
        method: 'POST',
        url: "user/name",
        data: {'name': $('#userName').val()},
        success: function (data) {
            RefreshRooms();
        },
        error: HandleError
    });
}

$(function () {
    RefreshUserName();
    RefreshRooms();
});