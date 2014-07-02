var isStarted = false;
function runServer() {
	if (!isStarted) {
		document.getElementById("ip").disabled = true;
		document.getElementById("port").disabled = true;
		document.getElementById("runServerButton").innerHTML = "Running";
		document.getElementById("runServerButton").disabled = true;
		document.getElementById("stopServerButton").disabled = false;
		document.getElementById("select").disabled = true;
		isStarted = true;
	} else {
		document.getElementById("ip").disabled = false;
		document.getElementById("port").disabled = false;
		document.getElementById("runServerButton").disabled = false;
		document.getElementById("runServerButton").innerHTML = "<span id = 'startSpan' class='glyphicon glyphicon-play'></span>Run";
		document.getElementById("stopServerButton").disabled = true;
		document.getElementById("select").disabled = false;
		clearFrame("serverIframe");
		window.oldLogs = [];
		isStarted = false;
	}
}

function clearFrame(frame) {

	document.getElementById(frame).contentWindow.document.body.innerHTML = "";
}

function write(frame, message) {

	document.getElementById(frame).contentWindow.document.write(message);
	document.getElementById(frame).contentWindow.scrollTo(0, document
			.getElementById(frame).contentWindow.document.body.scrollHeight);

}
