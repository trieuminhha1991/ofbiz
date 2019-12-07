if (typeof (AddVariance) == "undefined") {
	var AddVariance = (function() {
		var reasons = function(data) {
			return _.filter(varianceReasons, function(item){ return item.negativeNumber == ((data.quantityDifference < 0)?"Y":"N"); });
		};
		var add = function(grid, eventId, productId) {
			grid.jqxGrid("addRow", null,
			{
				eventId: eventId,
				productId: productId,
				statusId: "STOCKING_VARIANCE_CREATED"
			} , "first");
		};
		return {
			reasons: reasons,
			add: add
		}
	})();
}