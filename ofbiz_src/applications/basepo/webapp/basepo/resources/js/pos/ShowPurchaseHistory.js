var showPurchaseHistoryObject = (function() {
	var selectRow = function() {
		if (showPurchaseHistoryGlobalObject.orderId == "") {
			$("#jqxgridPurchaseHistory").jqxGrid("selectrow", 0);
		}
	};
	return {
		selectRow : selectRow
	}
}());
$(document).ready(function() {
	showPurchaseHistoryObject.selectRow();
});