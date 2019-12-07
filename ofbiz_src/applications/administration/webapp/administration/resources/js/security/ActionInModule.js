if (typeof (ActionInModule) == "undefined") {
	var ActionInModule = (function() {
		return {
			init: function() {
				AddUserToAction.init();
				AddGroupToAction.init();
			}
		}
	})();
}