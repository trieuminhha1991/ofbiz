$(document).ready(function() {
	$("a[href='#product-tab']").click(function() {
		initGridjqxgridSupplierProduct();
	});
	$("a[href='#promotion-tab']").click(function() {
		initGridjqxPromotion();
	});
});
var cellclassname = function(row, column, value, data) {
	if (data.availableThruDate) {
		var availableThruDate = data.availableThruDate.getTime();
		var nowDate = new Date().getTime();
		if (availableThruDate < nowDate) {
			return "jqx-grid-cell-expired";
		}
	}
	return "";
};
