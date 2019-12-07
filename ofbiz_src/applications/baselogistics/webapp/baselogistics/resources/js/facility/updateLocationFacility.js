$(function(){
	updateLocationFacilityObj.init();
});

var updateLocationFacilityObj = (function(){
	var init = function(){
		initInput();
		initEvents();
		initValidateForm();
	};
	
	
	
    return {
		init: init,
		
	};
}());
