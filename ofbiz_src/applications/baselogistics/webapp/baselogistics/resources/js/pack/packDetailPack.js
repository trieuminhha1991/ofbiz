$(function(){
	PackDetailObj.init();
});
var PackDetailObj = (function() {
	var init = function() {
		if (noteValidate === undefined) var noteValidate;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		
		if (shipBeforeDateDt){
			$("#shipBeforeDateDT").text(DatetimeUtilObj.formatToMinutes(new Date(shipBeforeDateDt)));
		}
		if (shipAfterDateDt){
			$("#shipAfterDateDT").text(DatetimeUtilObj.formatToMinutes(new Date(shipAfterDateDt)));
		}
		
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {

	};

	
	var initValidateForm = function(){
		};
	return {
		init: init,
	}
}());