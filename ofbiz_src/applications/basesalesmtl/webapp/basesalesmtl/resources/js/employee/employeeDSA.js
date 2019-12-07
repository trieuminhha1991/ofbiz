$(document).ready(function() {
	
});

if (typeof (EmployeeTree) == "undefined") {
	var EmployeeTree = (function() {
		var prepareData = function() {
			var data = DataAccess.getData({
						url: "getEmployeeDSA",
						data: {},
						source: "employeeDSA"});
		};
		var handlerEvent = function() {
			
		};
		return {
			init: function() {
				prepareData();
				handlerEvent();
			},
		};
	})();
}