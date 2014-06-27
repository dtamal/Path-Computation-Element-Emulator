function clearFrame(frame) {

	document.getElementById(frame).contentWindow.document.body.innerHTML = "";

}

var isConnected = false;
function connectClient() {
	if (!isConnected) {
		document.getElementById("connectionIP").disabled = true;
		document.getElementById("connectionPort").disabled = true;
		document.getElementById("connectToServerButton").innerHTML = "Disconnect";
		document.getElementById("srcAddr").disabled = false;
		document.getElementById("dstAddr").disabled = false;
		document.getElementById("sendButton").disabled = false;
		isConnected = true;
	} else {
		document.getElementById("connectionIP").disabled = false;
		document.getElementById("connectionPort").disabled = false;
		document.getElementById("connectToServerButton").innerHTML = "Connect";
		document.getElementById("srcAddr").disabled = true;
		document.getElementById("dstAddr").disabled = true;
		document.getElementById("sendButton").disabled = true;
		clearFrame("clientIframe");
		deleteNetworkgraph();
		isConnected = false;
	}
}

function write(frame, message) {
    
    document.getElementById(frame).contentWindow.document.write(message);
	    document.getElementById(frame).contentWindow.scrollTo(0, document
			.getElementById(frame).contentWindow.document.body.scrollHeight);

   }