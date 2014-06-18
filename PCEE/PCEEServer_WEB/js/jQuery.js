$(document).ready(function() {
	$("#option1").ready(function() {
		document.getElementById("network").src = "images/atlanta.png";
	});
	$("#option1").click(function() {
		document.getElementById("network").src = "images/atlanta.png";
	});
	$("#option2").click(function() {
		document.getElementById("network").src = "images/newyork.png";
	});
	$("#option3").click(function() {
		document.getElementById("network").src = "images/24nodes.png";
	});
});

$(document).on('click', '.dropdown-menu li a', function() {
	var selText = $(this).text();
	$('#select').html(selText + ' <span class="caret"></span>');
});