if (typeof (PurchaseHistory) == "undefined") {
	var PurchaseHistory = (function() {
		var load = function(partyId) {
			var grid = $('#jqxPurchaseHistory');
			var url = "jqxGeneralServicer?sname=JQGetListPurchaseHistory&partyId=" + partyId;
			ExtendScreen.changeGridUrl(grid, url);
		};
		return {
			init: function() {
				initGridjqxPurchaseHistory();
			},
			load: load
		};
	})();
}