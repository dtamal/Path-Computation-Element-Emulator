function runServer(isStarted){
	if (!isStarted) {
		this.changeImage();
		var ip = document.getElementById("ip").value;
		var port = document.getElementById("port").value;
		this.write("serverIframe", "Server running "+ip+":"+port+"<br/>");
		document.getElementById("ip").disabled = true;
		document.getElementById("port").disabled = true;
		document.getElementById("runButton").innerHTML = "Running";
    	document.getElementById("runButton").disabled = true;
    	document.getElementById("stopButton").disabled = false;
		document.getElementById("radios1").disabled = true;
		document.getElementById("radios2").disabled = true;
		document.getElementById("radios3").disabled = true;
	} else {
		this.write("serverIframe", "Server stopped <br/>");
		document.getElementById("ip").disabled = false;
		document.getElementById("port").disabled = false;
    	document.getElementById("runButton").disabled = false;
    	document.getElementById("runButton").innerHTML = "<span id = 'startSpan' class='glyphicon glyphicon-play'></span>Run";
    	document.getElementById("stopButton").disabled = true;
		document.getElementById("radios1").disabled = false;
		document.getElementById("radios2").disabled = false;
		document.getElementById("radios3").disabled = false;
	}
}

function clearFrame(frame) {
        
     document.getElementById(frame).contentWindow.document.body.innerHTML = "";
	
    }
    
function write(frame, message) {
        
     document.getElementById(frame).contentWindow.document.write(message);

    }

var isConnected= false;
function connectClient(){
	if (!isConnected) {
		var ip = document.getElementById("connectionIP").value;
		var port = document.getElementById("connectionPort").value;
		this.write("clientIframe", "Connected to the server: "+ip+":"+port+"<br/>");
		document.getElementById("connectionIP").disabled = true;
		document.getElementById("connectionPort").disabled = true;
		document.getElementById("connectButton").innerHTML = "Disconnect";
		document.getElementById("srcAddr").disabled = false;
    		document.getElementById("dstAddr").disabled = false;
    		document.getElementById("sendButton").disabled = false;
		isConnected = true;
	} else {
		document.getElementById("connectionIP").disabled = false;
		document.getElementById("connectionPort").disabled = false;
		document.getElementById("connectButton").innerHTML = "Connect";
		this.write("clientIframe", "Disconnected from the server<br/>");
		document.getElementById("srcAddr").disabled = true;
    		document.getElementById("dstAddr").disabled = true;
    		document.getElementById("sendButton").disabled = true;
		isConnected= false;
	}
}

function sendRequest(){

    		
    		var src = document.getElementById("srcAddr").value;
			var dst = document.getElementById("dstAddr").value;
    		this.write("clientIframe", "Request message ["+src+"-->"+dst+"] <br/>");
    	

}

function changeImage(){
	if(document.getElementById("radios1").checked) {
  		document.getElementById("network").src = "images/atlanta.png";
}else if(document.getElementById("radios2").checked) {
  		document.getElementById("network").src = "images/newyork.png";
}else if(document.getElementById("radios3").checked) {
		document.getElementById("network").src = "images/24nodes.png";
}
}