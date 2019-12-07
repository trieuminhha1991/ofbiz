if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};

function checkUiLabelMapDefault(){
	if (!uiLabelMap.BSClear) uiLabelMap.BSClear = "Xoa trang";
	if (!uiLabelMap.BSAddNew) uiLabelMap.BSAddNew = "Them moi";
};
checkUiLabelMapDefault();

var setUiLabelMap = function(key, value){
	uiLabelMap[key] = value;
};

var OlbCore = (function(){
	var theme = "olbius";
	var formatDate = 'dd/MM/yyyy';
	var formatDateTime = 'dd/MM/yyyy HH:mm:ss';
	
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
	var alert = {
		error: function(msg, callback){
			var _labelwgok = "Ok";
			if (typeof(labelwgok) != "undefined") _labelwgok = labelwgok;
			var messageError = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i><span class='message-content-alert-danger'>";
			messageError += msg;
			messageError += "</span>";
			bootbox.dialog(messageError, [{
				"label" : _labelwgok,
				"class" : "btn-small btn-primary width60px",
				"callback": callback,
			}]);
			return false;
		},
		info: function(msg, callback){
			var _labelwgok = "Ok";
			if (typeof(labelwgok) != "undefined") _labelwgok = labelwgok;
			var message = "<i class='fa-info-circle open-sans icon-modal-alert-info'></i><span class='message-content-alert-info'>";
			message += msg;
			message += "</span>";
			bootbox.dialog(message, [{
				"label" : _labelwgok,
				"class" : "btn-small btn-primary width60px",
				"callback": callback,
				}]
			);
		}
	};
	var formatValueStrNumber = function(number, locale){
		var numberStr = "" + number;
		if (locale == "vi") {
			if (numberStr.indexOf(",") > -1) {
				numberStr.replace(",", "");
			}
			if (numberStr.indexOf(".") > -1) {
				numberStr = numberStr.replace(".", ",");
			}
		}
		return numberStr;
	};
	var formatCurrencyRoundByUom = function(num, uom){
	    if(num == null || typeof(num) == "undefined"){
	        return "";
	    }
	    
	    decimalseparator = ",";
	    thousandsseparator = ".";
	    if (typeof(uom) == "undefined" || uom == null) {
	        uom = ""; /* defaultOrganizationPartyCurrencyUomId */
	    }
	    if (uom == "VND") {
	    	num = Math.round(num / 100) * 100;
	    }
	    
	    if (uom == "USD") {
	        decimalseparator = ".";
	        thousandsseparator = ",";
	    } else if(uom == "EUR") {
	        decimalseparator = ".";
	        thousandsseparator = ",";
	    }
	    
	    var str = num.toString(), parts = false, output = [], i = 1, formatted = null;
	    if(str.indexOf(".") > 0) {
	        parts = str.split(".");
	        str = parts[0];
	    }
	    str = str.split("").reverse();
	    var c;
	    for(var j = 0, len = str.length; j < len; j++) {
	        if(str[j] != ",") {
	        	if(str[j] == '-'){
	        		if(output && output.length > 1){
	        			if(output[output.length - 1] == '.'){
	        				output.splice(output.length - 1,1);
	        			}
	            		c = true;
	            		break;
	        		}
	        	} 
	            output.push(str[j]);
	            if(i%3 == 0 && j < (len - 1)) {
	            	output.push(thousandsseparator);
	            }
	            i++;
	        }
	    }
	    if(c) output.push("-");
	    formatted = output.reverse().join("");
	    
	    if (uom == "VND") {
	    	return (formatted ? formatted : "0") + ((parts) ? parts[1].substr(0, 2) : "");
	    } else {
	    	var decimalfraction = "";
	    	
			//decimalfraction = decimalseparator + parts[1].substr(0, 2);
			var dectmp = (parts) ? parts[1].substr(0, 2) : "";
			if (dectmp.length == 1) {
				dectmp += "0";
			} else if (dectmp.length == 0) {
				dectmp += "00";
			}
			decimalfraction = decimalseparator + dectmp;
			
		    //var returnValue = (formatted ? formatted : "0") + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "");
			var returnValue = (formatted ? formatted : "0") + decimalfraction;
		    return returnValue;
	    }
	};
	return {
		theme: theme,
		formatDate: formatDate,
		formatDateTime: formatDateTime,
		isNotEmpty: isNotEmpty,
		isEmpty: isEmpty,
		isArray: isArray,
		isString: isString,
		alert: alert,
		formatValueStrNumber: formatValueStrNumber,
		formatCurrencyRoundByUom: formatCurrencyRoundByUom
	}
}());

var DateFormatUtil = (function(){
	var init = function(){
		initEvent();
	};
	var initEvent = function(){
		Date.prototype.formatDate = function(format) {
			/* copyright from author @RickStrahl */
		    var date = this;
		    if (!format) format="MM/dd/yyyy";

		    var month = date.getMonth() + 1;
		    var year = date.getFullYear();    

		    format = format.replace("MM", padL(month.toString(), 2, "0"));        

		    if (format.indexOf("yyyy") > -1) 
		        format = format.replace("yyyy", year.toString());
		    else if (format.indexOf("yy") > -1) 
		        format = format.replace("yy", year.toString().substr(2, 2));

		    format = format.replace("dd", padL(date.getDate().toString(), 2, "0"));

		    var hours = date.getHours();
		    if (format.indexOf("t") > -1) {
		       if (hours > 11) format = format.replace("t", "pm")
		       else format = format.replace("t", "am")
		    }
		    if (format.indexOf("HH") > -1)
		        format = format.replace("HH", padL(hours.toString(), 2, "0"));
		    if (format.indexOf("hh") > -1) {
		        if (hours > 12) hours - 12;
		        if (hours == 0) hours = 12;
		        format = format.replace("hh", padL(hours.toString(), 2, "0"));
		    }
		    if (format.indexOf("mm") > -1)
		       format = format.replace("mm", padL(date.getMinutes().toString(), 2, "0"));
		    if (format.indexOf("ss") > -1)
		       format = format.replace("ss", padL(date.getSeconds().toString(), 2, "0"));
		    return format;
		}
	};
	var padL = function (value, width, pad) {
	    if (!width || width < 1)
	        return value;

	    if (!pad) pad = " ";
	    var length = width - value.length;
	    if (length < 1) return value.substr(0, width);

	    return (repeat(pad, length) + value).substr(0, width);
	};
	var padR = function (value, width, pad) {
	    if (!width || width < 1)
	        return value;        
	 
	    if (!pad) pad=" ";
	    var length = width - value.length
	    if (length < 1) value.substr(0, width);
	 
	    return (value + repeat(pad, length)).substr(0, width);
	};
	var repeat = function(chr, count) {    
	    var str = ""; 
	    for (var x=0; x < count; x++) {str += chr}; 
	    return str;
	};
	return {
		init: init
	};
}());
DateFormatUtil.init();
