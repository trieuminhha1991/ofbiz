$(function(){
	ListProductObj.init();
});
var ListProductObj = (function() {
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
	var initEvents = function() {};
	var initValidateForm = function(){
		var extendRules = [];
   		var mapRules = [];
	};
	
	var preparePrintBarcodeProducts = function(){
		href = "preparePrintBarcodeProducts";
		window.location.href = href;
	};
	
	return {
		preparePrintBarcodeProducts: preparePrintBarcodeProducts,
		init: init,
	}
}());