$(document).ready(function() {
	$("#coolerTabs").tabs();
	var loadEl = $("#loading-mk");
	var loading = setTimeout(function() {
		loadEl.hide();
		clearTimeout(loading);
	}, 1100);
});
