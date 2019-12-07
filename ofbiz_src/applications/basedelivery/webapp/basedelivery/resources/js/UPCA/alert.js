$(document).ready(function() {
	Alert.init();
});
if (typeof (Alert) == "undefined") {
	var Alert = (function() {
		var successElement, errorElement;
		var init = function() {
			successElement = document.createElement("audio");
			successElement.setAttribute("src", "/deliresources/sound/Success.mp3");
			errorElement = document.createElement("audio");
			errorElement.setAttribute("src", "/deliresources/sound/Error.mp3");
		};
		var success = function() {
			successElement.play();
		};
		var error = function() {
			errorElement.play();
		};
		return {
			init : init,
			success: success,
			error: error
		};
	})();
}