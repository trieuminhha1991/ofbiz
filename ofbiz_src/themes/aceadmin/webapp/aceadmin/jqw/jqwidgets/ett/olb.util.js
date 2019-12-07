/**
 * Require OlbCore Object
 */
var jOlbUtil = (function(){
	var uiLabelMap = {};
	var setUiLabelMap = function(key, value){
		uiLabelMap[key] = value;
	};
	
	var dateTime = {
		formatFullDate: function(value) { // dd/MM/yyyy HH:mm:ss
			if (OlbCore.isNotEmpty(value) && (Object.prototype.toString.call(value) === "[object Date]" || !isNaN(value))) {
				var _value = new Date(value);
				var dateStr = "";
				dateStr += addZero(_value.getDate()) + '/';
				dateStr += addZero(_value.getMonth()+1) + '/';
				dateStr += addZero(_value.getFullYear()) + ' ';
				dateStr += addZero(_value.getHours()) + ':';
				dateStr += addZero(_value.getMinutes()) + ':';
				dateStr += addZero(_value.getSeconds());
				return dateStr;
			} else if (OlbCore.isNotEmpty(value) && typeof value == "object") {
				var dateStr = "";
				if (value.time) {
					var _value = new Date(value.time);
					dateStr += addZero(_value.getDate()) + '/';
					dateStr += addZero(_value.getMonth()+1) + '/';
					dateStr += addZero(_value.getFullYear()) + ' ';
					dateStr += addZero(_value.getHours()) + ':';
					dateStr += addZero(_value.getMinutes()) + ':';
					dateStr += addZero(_value.getSeconds());
				}
				return dateStr;
			} else {
				return "";
			}
		},
		formatDate: function(value, charJoin) { // dd/MM/yyyy
			if (OlbCore.isNotEmpty(value) && (Object.prototype.toString.call(value) === "[object Date]" || !isNaN(value))) {
				if (!OlbCore.isNotEmpty(charJoin) || charJoin.length > 1) {
					charJoin = '/';
				}
				
				var _value = new Date(value);
				var dateStr = "";
				dateStr += addZero(_value.getDate()) + charJoin;
				dateStr += addZero(_value.getMonth()+1) + charJoin;
				dateStr += addZero(_value.getFullYear());
				return dateStr;
			} else if (typeof value == "object") {
				var dateStr = "";
				if (value.time) {
					var _value = new Date(value.time);
					dateStr += addZero(_value.getDate()) + '/';
					dateStr += addZero(_value.getMonth()+1) + '/';
					dateStr += addZero(_value.getFullYear());
				}
				return dateStr;
			} else {
				return "";
			}
		},
		formatTime: function(value, charJoin) { // HH:mm:ss
			if (!OlbCore.isNotEmpty(charJoin) || charJoin.length > 1) {
				charJoin = ':';
			}
			
			if (OlbCore.isNotEmpty(value) && (Object.prototype.toString.call(value) === "[object Date]" || !isNaN(value))) {
				var _value = new Date(value);
				var dateStr = "";
				dateStr += addZero(_value.getHours()) + charJoin;
				dateStr += addZero(_value.getMinutes()) + charJoin;
				dateStr += addZero(_value.getSeconds());
				return dateStr;
			} else if (OlbCore.isNotEmpty(value) && typeof value == "object") {
				var dateStr = "";
				if (value.time) {
					var _value = new Date(value.time);
					dateStr += addZero(_value.getHours()) + charJoin;
					dateStr += addZero(_value.getMinutes()) + charJoin;
					dateStr += addZero(_value.getSeconds());
				}
				return dateStr;
			} else {
				return "";
			}
		}
	};
	var addZero = function(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	var processResultDataAjax = function(data, callbackJsonError, callbackJsonSuccess, callbackData) {
		if (data.thisRequestUri == "json") {
    		var errorMessage = "";
	        if (data._ERROR_MESSAGE_LIST_ != null) {
	        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
	        		errorMessage += "<p>" + data._ERROR_MESSAGE_LIST_[i] + "</p>";
	        	}
	        }
	        if (data._ERROR_MESSAGE_ != null) {
	        	errorMessage += "<p>" + data._ERROR_MESSAGE_ + "</p>";
	        }
	        if (errorMessage != "") {
	        	if (typeof(callbackJsonError) == 'string') {
	        		if (callbackJsonError == "default") {
	        			$("#container").empty();
	    	        	$("#jqxNotification").jqxNotification({ template: 'error'});
	    	        	$("#jqxNotification").html(errorMessage);
	    	        	$("#jqxNotification").jqxNotification("open");
	        		} else {
	        			$("#container" + callbackJsonError).empty();
	    	        	$("#jqxNotification" + callbackJsonError).jqxNotification({ template: 'error'});
	    	        	$("#jqxNotification" + callbackJsonError).html(errorMessage);
	    	        	$("#jqxNotification" + callbackJsonError).jqxNotification("open");
	        		}
	        	} else if (typeof(callbackJsonError) == 'function') {
	        		callbackJsonError(data, errorMessage);
	        	}
	        	return false;
	        } else {
	        	if (typeof(callbackJsonSuccess) == 'string') {
	        		if (callbackJsonSuccess == "default") {
	        			$('#container').empty();
	    	        	$('#jqxNotification').jqxNotification({ template: 'info'});
	    	        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
	    	        	$("#jqxNotification").jqxNotification("open");
	        		} else {
	        			$("#container" + callbackJsonSuccess).empty();
	    	        	$("#jqxNotification" + callbackJsonSuccess).jqxNotification({ template: 'info'});
	    	        	$("#jqxNotification" + callbackJsonSuccess).html(uiLabelMap.wgupdatesuccess);
	    	        	$("#jqxNotification" + callbackJsonSuccess).jqxNotification("open");
	        		}
	        	} else if (typeof(callbackJsonSuccess) == 'function') {
	        		callbackJsonSuccess(data);
	        	}
				return true;
	        }
    	} else {
    		if (typeof(callbackData) == 'function') {
    			callbackData(data);
    		}
    		return true;
    	}
	};
	
	var confirm = (function(){
		var confirm = function(msg, callback){
			bootbox.confirm(msg, labelwgcancel, labelwgok, function(result) {
				if(result) {
					callback();
				}
			});
		};
		var dialog = function(msg, callback, p_labelwgcancel, p_labelwgok){
			var _labelwgcancel = "Cancel";
			var _labelwgok = "Ok";
			if (typeof(labelwgcancel) != "undefined") _labelwgcancel = labelwgcancel;
			if (typeof(labelwgok) != "undefined") _labelwgok = labelwgok;
			if (p_labelwgcancel) _labelwgcancel = p_labelwgcancel;
			if (p_labelwgok) _labelwgok = p_labelwgok;
			
			bootbox.dialog(msg, [
                {"label": _labelwgcancel, "icon": 'fa fa-remove', "class": 'btn btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": _labelwgok, "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": callback
				}
		    ]);
		};
		return {
			confirm : confirm,
			dialog: dialog,
		};
	}());
	var alert = {
		error: function(msg){
			var _labelwgok = "Ok";
			if (typeof(labelwgok) != "undefined") _labelwgok = labelwgok;
			var messageError = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i><span class='message-content-alert-danger'>";
			messageError += msg;
			messageError += "</span>";
			bootbox.dialog(messageError, [{
				"label" : _labelwgok,
				"class" : "btn-small btn-primary width60px",
			}]);
			return false;
		},
		info: function(msg){
			var _labelwgok = "Ok";
			if (typeof(labelwgok) != "undefined") _labelwgok = labelwgok;
			var message = "<i class='fa-info-circle open-sans icon-modal-alert-info'></i><span class='message-content-alert-info'>";
			message += msg;
			message += "</span>";
			bootbox.dialog(message, [{
				"label" : _labelwgok,
				"class" : "btn-small btn-primary width60px",
				}]
			);
		}
	};
	var notification = {
		create: function(idContainer, idNotification, template, config){
			if (config == undefined || config == null) config = {};
			if (idContainer == null) idContainer = "#container";
			if (idNotification == null) idNotification = "#jqxNotification";
			if (template == null || template == undefined) template = "success";
			var tmpWidth = typeof(config.width) != "undefined" ? config.width : "auto";
			var autoClose = typeof(config.autoClose) != "undefined" ? config.autoClose : true;
			
			$(idContainer).width(tmpWidth);
	        $(idNotification).jqxNotification({ 
	        	icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}, 
	        	width: tmpWidth, 
	        	appendContainer: "#" + $(idContainer).attr("id"), 
	        	opacity: 1, autoClose: autoClose, template: template 
	        });
		},
	};
	var contextMenu = {
		create: function(contextMenuObj, numItem, config){
			if (numItem == undefined || numItem == null) {
				//var _numItem = $(contextMenuObj).find("ul > li");
				//numItem = _numItem.length;
				var _numItemUls = $(contextMenuObj).children("ul");
				if (_numItemUls) {
					var _numItemUl = $(_numItemUls).first();
					if (_numItemUl) {
						_numItem = $(_numItemUl).children("li");
						numItem = _numItem.length;
					}
				}
			};
			var height = 28 * numItem;
			var configMap = {width: 220, height: height, autoOpenPopup: false, mode: 'popup', theme: OlbCore.theme};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
				    if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			$(contextMenuObj).jqxMenu(configMap);
		},
	};
	var input = {
		create: function(id, config){
			var configMap = {height: 25, theme: OlbCore.theme}; // width: 213
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
				    if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			$(id).jqxInput(configMap);
		}
	};
	var dateTimeInput = {
		create: function(id, config){
			var configMap = {width: 220, height: 25, theme: OlbCore.theme, formatString: OlbCore.formatDateTime};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
					if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			$(id).jqxDateTimeInput(configMap);
		}
	};
	var createClearButton = function(obj, type){
		if (typeof(type) == "undefined") return false;
		
		var parent = obj.parent();
		var idClearBtn = obj.attr("id") + "_clearbtn";
		var clearBtnStr = '<a id="' + idClearBtn + '" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="' + uiLabelMap.BSClear + '"><i class="fa fa-eraser"></i></a>';
		var clearBtn = $(clearBtnStr);
		parent.css("position", "relative");
		obj.after(clearBtn);
		//obj.css("display", "inline-block");
		resizeClearButton(obj, clearBtn);
		
		clearBtn.click(function(){
			if (type == "dropDownList") {
				obj.jqxDropDownList('clearSelection');
			} else if (type == "comboBox") {
				obj.jqxComboBox('clearSelection');
			} else if (type == "numberInput") {
				jOlbUtil.numberInput.clear(obj);
			}
		});
		$(window).resize(function(){
			resizeClearButton(obj, clearBtn);
		});
	}
	var resizeClearButton = function(obj, clearBtn){
		var alignLeft = obj.width() + 5;
		clearBtn.css("left", alignLeft);
	}
	var numberInput = (function(){
		var create = function(id, config){
			var configMap = {height: 25, theme: OlbCore.theme};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
				    if (config.hasOwnProperty(key)) {
				    	if (key == "showClearButton") {
				    		return true;
				    	}
				    	configMap[key] = value;
				    }
				});
			}
			$(id).jqxNumberInput(configMap);
			
			// show button clear data
			if (config.showClearButton) {
				createClearButton($(id), "numberInput");
			}
		};
		var val = function(id, val){
			$(id).jqxNumberInput('val', val);
		};
		var clear = function(id){
			$(id).jqxNumberInput('val', null);
		};
		return {
			create: create,
			val: val,
			clear: clear
		}
	}());
	var checkBox = (function(){
		var create = function(id, config){
			var configMap = {height: 25, theme: OlbCore.theme};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
				    if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			$(id).jqxCheckBox(configMap);
		};
		return {
			create: create,
		}
	}());
	var windowPopup = {
		create: function(id, config){
			var wi = $(id);
			var configMap = {width: 540, height: 460, resizable: true, isModal: true, autoOpen: false, modalOpacity: 0.7, theme: OlbCore.theme};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
				    if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			wi.jqxWindow(configMap);
			
			wi.on("open", function(){
				var tmpwidth = wi.jqxWindow('width');
	            wi.jqxWindow({position: {x: (window.outerWidth - tmpwidth)/2, y: pageYOffset + 30}});
			});
			
            wi.on('close', function (event) { 
            	if(wi.jqxValidator){
            		wi.jqxValidator('hide');
            	}
            }); 
		}
	};
	var setAttrDataValue = function(id, value){
		var obj = document.getElementById(id);
		if (OlbCore.isNotEmpty(obj)) {
			obj.setAttribute("data-value", value);
		}
	};
	var getAttrDataValue = function(id){
		var obj = document.getElementById(id);
		if (OlbCore.isNotEmpty(obj)) {
			return obj.getAttribute("data-value");
		}
	};
	var clearAttrDataValue = function(id){
		setAttrDataValue(id, "");
	};
	return {
		setUiLabelMap: setUiLabelMap,
		dateTime: dateTime,
		processResultDataAjax: processResultDataAjax,
		confirm: confirm,
		alert: alert,
		notification: notification,
		contextMenu: contextMenu,
		input: input,
		dateTimeInput: dateTimeInput,
		numberInput: numberInput,
		checkBox: checkBox,
		windowPopup: windowPopup,
		setAttrDataValue: setAttrDataValue,
		getAttrDataValue: getAttrDataValue,
		clearAttrDataValue: clearAttrDataValue,
	};
}());