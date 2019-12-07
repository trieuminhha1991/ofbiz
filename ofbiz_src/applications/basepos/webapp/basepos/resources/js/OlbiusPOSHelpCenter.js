$(document).ready(function() {
	HelpCenter.init();
});
if (typeof (HelpCenter) == "undefined") {
	var HelpCenter = (function() {
		var jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: "olbius", width: "80%", maxWidth: 2000, height: "96%", maxHeight: 1000, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCancelHelpCenter"), modalOpacity: 0.7
			});
		};
		var handleEvents = function() {
			$("body").keydown(function(e) {
				var keycode = window.event ? window.event.keyCode : e.which;
				if (e.altKey && keycode==83) {
					HelpCenter.open();
				}
			});
			$("#divListOfShortcuts").click(function() {
				HelpCenter.open();
			});
		};
		var open = function() {
			var wtmp = window;
			var tmpwidth = jqxwindow.jqxWindow("width");
			jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 10 }});
			jqxwindow.jqxWindow("open");
			jqxwindow.focus();
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowHelpCenter");
				initJqxElements();
				handleEvents();
			},
			open: open
		}
	})();
}