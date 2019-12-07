$(function(){
	ShipmentObj.init();
});
var ShipmentObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
	
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
	};
	var initValidateForm = function(){
		var extendRules = [
          ];
   		var mapRules = [
          ];
	};
	
	var addZero = function(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	var formatFullDate = function(value) {
		if (value) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	};
	
	return {
		init: init,
		formatFullDate: formatFullDate,
	}
}());