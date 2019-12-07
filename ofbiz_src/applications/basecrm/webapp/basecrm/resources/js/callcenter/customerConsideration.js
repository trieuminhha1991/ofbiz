if (typeof (Consideration) == "undefined") {
	var Consideration = (function() {
		initHotkey = function() {
			$(document).keydown(function(e) {
				switch (e.keyCode) {
				case 114:
					$("#crm-consideration-btn").click();
					return false;
					break;
				default:
					break;
				}
				return;
			});
		};
		var initJqxElements = function() {
			$("body").append("<div id='crm-consideration-modal'></div>");
//			$("#cons-panel").jqxSplitter({ width: "100%", height: "100%", orientation: "vertical", panels: [{ size: "25%" }] });
//			$("#crm-cons-main").jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "25%" }] });
			$("#customerTabs").jqxTabs({ theme: "energyblue", height: "100%", width: "100%" });
		};
		var handleEvents = function() {
			$("#crm-consideration-btn").on(ace.click_event, function() {
				$(this).toggleClass("open");
				$("#ace-settings-box").toggleClass("open");
				$("#crm-consideration-modal").toggleClass("open");
			});
		};
		return {
			init: function() {
				initHotkey();
				handleEvents();
				initJqxElements();
//				CustomerConsideration.init();
				PurchaseHistory.init();
			}
		}
	})();
}