function CreateRoom() {
	$('#createRoomForm')
	
	var formDiv = $("#createRoomDetails");
	formDiv.css("display", "inline-block");
}

function CreateRoomSubmit() {
	var form = $("#createRoomForm");
	var roomName = form.children(":first").val();
	alert("Ok clicked, room name is: " + roomName);
}

var clear=true;
function RoomNameFocus(obj) {
	if(clear) {
		obj.value = "";
		clear = false;
	}
}

var roomSelected = null;

function RoomClicked(room) {
	if(room.id == "") return;
	
	if(roomSelected != null) {
		$(roomSelected).removeClass("selectedRoom");
	}
	if(room == roomSelected) {
		roomSelected = null;
		return;
	}
	roomSelected = room;
	$(roomSelected).addClass("selectedRoom");
}

function JoinRoomClicked() {
	if(roomSelected != null) {
		alert("joining room "+roomSelected.id);
		document.location.href = "/room.html";
	}
}

function RefreshRooms() {
	location.reload();
}