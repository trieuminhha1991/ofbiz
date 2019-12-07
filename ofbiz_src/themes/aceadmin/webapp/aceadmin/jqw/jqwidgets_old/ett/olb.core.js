var OlbCore = (function(){
	var theme = "olbius";
	var isNotEmpty = function(value) {
		if (typeof(value) != 'undefined' && value != null && !(/^\s*$/.test(value))) {
			return true;
		} else {
			return false;
		}
	};
	var isEmpty = function(value) {
		return !isNotEmpty(value);
	};
	var isArray = function(object_param) {
		if (Object.prototype.toString.call(object_param) === '[object Array]') {
			return true;
		} else {
			return false;
		}
	};
	var isString = function(object_param) {
		if (typeof(object_param) == 'string') {
			return true;
		} else {
			return false;
		}
	};
	return {
		theme: theme,
		isNotEmpty: isNotEmpty,
		isEmpty: isEmpty,
		isArray: isArray,
		isString: isString
	}
}());