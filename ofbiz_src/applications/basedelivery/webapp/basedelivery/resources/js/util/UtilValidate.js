$(function(){
	
});
var ValidateObj = (function() {
	
	var isNotEmpty = function(value) {
		if (typeof(value) != 'undefined' && value != null && !(/^\s*$/.test(value))) {
			return true;
		} else {
			return false;
		}
	};
	
	return {
		isNotEmpty: isNotEmpty,
	}
}());