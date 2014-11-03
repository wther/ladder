function CreateRoom() {
	var formDiv = document.getElementById("createRoomDetails");
	formDiv.style.display= "inline-block";
}

function CreateRoomSubmit() {
	var form = document.getElementById("createRoomForm");
	var roomName = form.children[0].value;
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
		roomSelected.classList.remove("selectedRoom");
	}
	if(room == roomSelected) {
		roomSelected = null;
		return;
	}
	roomSelected = room;
	roomSelected.classList.add("selectedRoom");
	//alert(room.id);
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