var Utils = (function() {

	var getWindowGUID = function() {
		var windowGUID = function() {
			//----------
			var S4 = function() {
				return (
					Math.floor(Math.random() * 0x10000).toString(16)
				);
			};

			return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4()
			);
		};

		var topMostWindow = window;

		while (topMostWindow != topMostWindow.parent) {
			topMostWindow = topMostWindow.parent;
		}

		if (!topMostWindow.name.match(/^GUID-/)) {
			topMostWindow.name = "GUID-" + windowGUID();
		}

		return topMostWindow.name;
	};
	return {
		getWindowGUID : getWindowGUID
	};
})();