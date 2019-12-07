$(document).ready(function() {
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
