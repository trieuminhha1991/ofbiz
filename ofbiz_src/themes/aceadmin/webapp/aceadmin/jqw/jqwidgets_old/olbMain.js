var OlbPage = function(){
	$.jqx.theme = 'olbius';
	var theme = 'olbius'; //OlbUtil.theme;
	var formatDate = 'dd/MM/yyyy HH:mm:ss';//OlbUtil.formatDate;
	var _jqxDateTimeInputs = [];
	var _jqxInputs = [];
	var _jqxNumberInputs = [];
	var _jqxCheckBoxes = [];
	var _formValidators = [];

	this.theme = theme;
	this.formatDate = formatDate;

	this.init = function(){
		// initialization elements, complex elements, validate rules
	};
	this.validate = function(operator){
		var _checkValidate = true;
		if (_formValidators.length > 0) {
			if (operator != undefined && operator == "OR") {
				$.each(_formValidators, function(key, value){
					_checkValidate = _checkValidate || $("#" + value).jqxValidator('validate');
				})
			} else {
				$.each(_formValidators, function(key, value){
					_checkValidate = _checkValidate && $("#" + value).jqxValidator('validate');
				})
			}
		}
		return _checkValidate;
	};
	this.getValue = function(){
		// get values of elements
		var _resultValue = {};
		var _dataItem;
		var _valueItem;
		$.each(_jqxDateTimeInputs, function(key, value){
			_dataItem = $('#' + value).jqxDateTimeInput('getDate');
			if (_dataItem != undefined && _dataItem != null) {
				_resultValue[value] = _dataItem.getTime();
			}
		});
		$.each(_jqxInputs, function(key, value){
			_valueItem = $('#' + value).val();
			if (_valueItem != undefined && _valueItem != null) {
				_resultValue[value] = _valueItem;
			}
		});
		return _resultValue;
	};
	this.setValue = function(dataMap){
		// set values of elements from dataMap
	};
	this.addDateTimeInput = function(id){
		if (id == undefined || id == null || $(id) == undefined) return;
		_jqxDateTimeInputs.push($(id).attr('id'));
	};
	this.addInput = function(id){
		if (id == undefined || id == null || $(id) == undefined) return;
		_jqxInputs.push($(id).attr('id'));
	};
	this.addCheckBox = function(id){
		if (id == undefined || id == null || $(id) == undefined) return;
		_jqxCheckBoxes.push($(id).attr('id'));
	};
	this.getDateTimeInput = function(id){
		return _jqxDateTimeInputs;
	};
	this.getInput = function(id){
		return _jqxInputs;
	};
	this.getNumberInput = function(id){
		return _jqxNumberInputs;
	};
	this.getCheckBox = function(id){
		return _jqxCheckBoxes;
	};
	this.getformValidators = function(id){
		return _formValidators;
	};
}
OlbPage.prototype.createDateTimeInput = function(id, config){
	OlbUtil.dateTimeInput.create(id, config);
	this.getDateTimeInput().push($(id).attr('id'));
};
OlbPage.prototype.createInput = function(id, config){
	OlbUtil.input.create(id, config);
	this.getInput().push($(id).attr('id'));
};
OlbPage.prototype.createNumberInput = function(id, config){
	OlbUtil.numberInput.create(id, config);
	this.getNumberInput().push($(id).attr('id'));
};
OlbPage.prototype.createCheckBox = function(id, config){
	OlbUtil.checkBox.create(id, config);
	this.getCheckBox().push($(id).attr('id'));
};
OlbPage.prototype.createValidator = function(formObj, dataList, extendRules, config){
	OlbUtil.validator.create(formObj, dataList, extendRules, config);
	this.getformValidators().push($(formObj).attr('id'));
};
OlbPage.prototype.createContextMenu = function(contextMenuObj, numItem, config){
	if (numItem == undefined || numItem == null) {
		var _numItem = $(contextMenuObj).find("ul > li");
		numItem = _numItem.length;
	};
	var height = 28 * numItem;
	var configMap = {width: 200, height: height, autoOpenPopup: false, mode: 'popup', theme: this.theme};
	if (config != undefined && config != null) {
		$.each(config, function(key, value){
		    if (config.hasOwnProperty(key)) {
		         configMap[key] = value;
		    }
		});
	}
	$(contextMenuObj).jqxMenu(configMap);
};

var OlbUtil = (function(){
	//$.jqx.theme = 'olbius';
	var theme = 'olbius';
	var formatDate = 'dd/MM/yyyy HH:mm:ss';
	var confirm = (function(){
		var confirm = function(msg, callback){
			bootbox.confirm(msg, labelwgcancel, labelwgok, function(result) {
				if(result) {
					callback();
				}
			});
		};
		var dialog = function(msg, callback){
			bootbox.dialog(msg, [
                {"label": labelwgcancel, "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        },
		        {"label": labelwgok, "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": callback
				}
		    ]);
		};
		return {
			confirm : confirm,
			dialog: dialog,
		};
	}());
	var alert = (function(){
		var error = function(msg){
			var messageError = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i><span class='message-content-alert-danger'>";
			messageError += msg;
			messageError += "</span>";
			bootbox.dialog(messageError, [{
				"label" : labelwgok,
				"class" : "btn-mini btn-primary width60px",
			}]);
			return false;
		};
		var info = function(msg){
			var message = "<i class='fa-info-circle open-sans icon-modal-alert-info'></i><span class='message-content-alert-info'>";
			message += msg;
			message += "</span>";
			bootbox.dialog(message, [{
				"label" : labelwgok,
				"class" : "btn-mini btn-primary width60px",
				}]
			);
		};
		return {
			error: error,
			info: info,
		};
	}());
	var dateTimeInput = (function(){
		var create = function(id, config){
			var configMap = {height: 25, theme: theme, formatString: formatDate};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
					if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			$(id).jqxDateTimeInput(configMap);
		};
		return {
			create: create,
		};
	}());

	var input = (function(){
		var create = function(id, config){
			var configMap = {height: 25, theme: theme};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
				    if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			$(id).jqxInput(configMap);
		};
		return {
			create: create,
		}
	}());

	var numberInput = (function(){
		var create = function(id, config){
			var configMap = {height: 25, theme: theme};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
				    if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			$(id).jqxNumberInput(configMap);
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
			var configMap = {height: 25, theme: theme};
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
	var windowPopup = (function(){
		var create = function(id, config){
			var configMap = {width: 540, height: 460, resizable: true, isModal: true, autoOpen: false, modalOpacity: 0.7, theme: theme};
			if (config != undefined && config != null) {
				$.each(config, function(key, value){
				    if (config.hasOwnProperty(key)) {
				         configMap[key] = value;
				    }
				});
			}
			$(id).jqxWindow(configMap);
		};
		var createValidator = function(formObj, mapRules, extendRules, config){
			validator.create(formObj, mapRules, extendRules, config);
			$(formObj).on('close', function (event) {
				validator.hide(formObj);
			});
		};
		return {
			create: create,
			createValidator: createValidator,
		}
	}());
	var validator = (function(){
		var validate = function(formObj){
			return $(formObj).jqxValidator('validate');
		};
		var create = function(formObj, dataList, extendRules, config){
			var _position = config != undefined && config.position != undefined ? config.position : 'bottom';
			$(formObj).jqxValidator({
				position: _position,
			rules: createValidatorRules(formObj, dataList, extendRules, config),
		    });

		};
		var hide = function(formObj){
			$(formObj).jqxValidator('hide');
		};
		var createValidatorRules = function(formObj, dataList, extendRules, config){
			var _rules = [];
			var _position = config != undefined && config.position != undefined ? config.position : 'bottom';
			if (Object.prototype.toString.call(dataList) === '[object Array]') {
				var _isAdd = false;
				$.each(dataList, function(key, mapItem){
					var _input = mapItem.input;
					var _message = mapItem.message;
					var _action = mapItem.action;
					var _rule = mapItem.rule;
					var _type = mapItem.type;

					_isAdd = false;
					var _mapItemRule = {};
					if (_type == 'validCannotSpecialCharactor') {
						if (_message == undefined || _message == null) _message = validContainSpecialCharacter;
						if (_action == undefined || _action == null) _action = 'blur';
						if (_rule == undefined || _rule == null || typeof(_rule) != 'function') {
							_rule = function (input, commit) {
							return OlbElementUtil.validElement(input, commit, 'validCannotSpecialCharactor');
							}
						}
						_isAdd = true;
					} else if (_type == 'validObjectNotNull') {
						if (_message == undefined || _message == null) _message = validFieldRequire;
						if (_action == undefined || _action == null) _action = 'blur';
						if (_rule == undefined || _rule == null || typeof(_rule) != 'function') {
							_rule = function (input, commit) {
							return OlbElementUtil.validElement(input, commit, 'validObjectNotNull', {objType: mapItem.objType});
							}
						}
						_isAdd = true;
					} else if (_type == 'validInputNotNull') {
						if (_message == undefined || _message == null) _message = validFieldRequire;
						if (_action == undefined || _action == null) _action = 'blur';
						if (_rule == undefined || _rule == null || typeof(_rule) != 'function') {
							_rule = function (input, commit) {
								return OlbElementUtil.validElement(input, commit, 'validInputNotNull');
							}
						}
						_isAdd = true;
					} else if (_type == 'validDateTimeInputNotNull') {
						if (_message == undefined || _message == null) _message = validFieldRequire;
						if (_action == undefined || _action == null) _action = 'valueChanged';
						if (_rule == undefined || _rule == null || typeof(_rule) != 'function') {
							_rule = function (input, commit) {
							return OlbElementUtil.validElement(input, commit, 'validDateTimeInputNotNull');
							}
						}
						_isAdd = true;
					} else if (_type == 'validDateTimeCompareToday') {
						if (_message == undefined || _message == null) _message = validRequiredValueGreatherOrEqualDateTimeToDay;
						if (_action == undefined || _action == null) _action = 'valueChanged';
						if (_rule == undefined || _rule == null || typeof(_rule) != 'function') {
							_rule = function (input, commit) {
							return OlbElementUtil.validElement(input, commit, 'validDateTimeCompareToday');
							}
						}
						_isAdd = true;
					} else if (_type == 'validDateCompareToday') {
						if (_message == undefined || _message == null) _message = validRequiredValueGreatherOrEqualToDay;
						if (_action == undefined || _action == null) _action = 'valueChanged';
						if (_rule == undefined || _rule == null || typeof(_rule) != 'function') {
							_rule = function (input, commit) {
								return OlbElementUtil.validElement(input, commit, 'validDateCompareToday');
							}
						}
						_isAdd = true;
					} else if (_type == 'validCompareTwoDate') {
						if (_message == undefined || _message == null) _message = validStartDateMustLessThanOrEqualFinishDate;
						_paramId1 = mapItem.paramId1 != undefined ? mapItem.paramId1 : "fromDate";
						_paramId2 = mapItem.paramId2 != undefined ? mapItem.paramId2 : "thruDate";
						if (_action == undefined || _action == null) _action = 'valueChanged';
						if (_rule == undefined || _rule == null || typeof(_rule) != 'function') {
							_rule = function (input, commit) {
								return OlbElementUtil.validElement(input, commit, 'validCompareTwoDate', {paramId1 : _paramId1, paramId2 : _paramId2});
							}
						}
						_isAdd = true;
					} else {
						if (_input != undefined && _message != undefined && _action != undefined && typeof(_rule) == 'function') {
							_isAdd = true;
						}
					}
					if (_isAdd){
						_mapItemRule = {
							input: _input, message: _message, action: _action,
							rule: function (input, commit) {
								return _rule(input, commit);
							}
						}
						_rules.push(_mapItemRule);
					}
				});
			}
			if (extendRules != undefined && extendRules != null) {
				$.each(extendRules, function(key, value){
					_rules.push(value);
				});
			}
			$(formObj).jqxValidator({position: _position, 'rules': _rules});
			return _rules;
		};
		return {
			create: create,
			validate: validate,
			hide: hide,
		}
	}());
	var notification = (function(){
		var create = function(idContainer, idNotification, template, config){
			if (config == undefined || config == null) config = {};
			if (idContainer == null) idContainer = "#container";
			if (idNotification == null) idNotification = "#jqxNotification";
			if (template == null || template == undefined) template = "success";
			var tmpWidth = typeof(config.width) != "undefined" ? config.width : "100%";
			$(idContainer).width(tmpWidth);
	        $(idNotification).jqxNotification({
			icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},
			width: tmpWidth,
			appendContainer: "#" + $(idContainer).attr("id"),
			opacity: 1, autoClose: true, template: template
	        });
		};
		return {
			create: create,
		};
	}());
	return {
		theme: theme,
		formatDate: formatDate,
		dateTimeInput: dateTimeInput,
		input: input,
		checkBox: checkBox,
		validator: validator,
		confirm: confirm,
		alert: alert,
		windowPopup: windowPopup,
		notification: notification,
		numberInput: numberInput,
	}
}()); // OlbUtil

var OlbElementUtil = (function(){
	var isNotEmpty = function(value) {
		if (typeof(value) != 'undefined' && value != null && !(/^\s*$/.test(value))) {
			return true;
		} else {
			return false;
		}
	};
	var validElement = function(input, commit, nameValidor, config){
		var validInputNotNull = function(input, commit){
			var value = $(input).val();
			if(isNotEmpty(value)){
				return true;
			}
			return false;
		};
		var validObjectNotNull = function(input, commit, config){
			var objType = config.objType;
			if (objType != undefined) {
				if (objType == "comboBox") {
					var item = $(input).jqxComboBox("getSelectedItem");
					if (item != undefined) {
						if (isNotEmpty(item.value)) {
							return true;
						}
					}
				} else if (objType == "dropDownButton") {
					var idObj = $(input).attr("id");
					if (idObj != undefined) {
						var value = getAttrDataValue(idObj);
						if(!isNotEmpty(value)){
							return false;
						}
					}
				} else if (objType == "comboBoxMulti") {
					var count = 0;
					var dataListSelected = $(input).jqxComboBox('getSelectedItems');
					if (isNotEmpty(dataListSelected)) {
						for (var i = 0; i < dataListSelected.length; i++) {
							var item = dataListSelected[i];
							if (item != null) count++;
						}
					}
					if (count > 0) return true;
					return false;
				}
			}
			return false;
		};
		var validDateTimeInputNotNull = function(input, commit){
			var value = $(input).jqxDateTimeInput('getDate');
			if(isNotEmpty(value)){
				return true;
			}
			return false;
		};
		var validDateCompareToday = function (input, commit) {
			var value = $(input).jqxDateTimeInput('getDate');
			if (isNotEmpty(value)) {
				var now = new Date();
				now.setHours(0,0,0,0);
			if(value < now){return false;}
			}
		return true;
		};
		var validDateTimeCompareToday = function (input, commit) {
			var value = $(input).jqxDateTimeInput('getDate');
			if (isNotEmpty(value)) {
				var now = new Date();
			if(value < now){return false;}
			}
		return true;
		};
		var validCannotSpecialCharactor = function (input, commit) {
			var value = $(input).val();
			if(isNotEmpty(value) && !(/^[a-zA-Z0-9_]+$/.test(value))){
				return false;
			}
			return true;
		};
		var validPostalCharactor = function (input, commit) {
			var value = $(input).val();
			if(isNotEmpty(value) && !(/^[a-zA-Z0-9-]+$/.test(value))){
				return false;
			}
			return true;
		};
		var validCompareTwoDate = function (input, commit, config) {
			var paramId1 = "fromDate";
			if (isNotEmpty(config) && isNotEmpty(config.paramId1)) paramId1 = config.paramId1;
			var paramId2 = "thruDate";
			if (isNotEmpty(config) && isNotEmpty(config.paramId2)) paramId2 = config.paramId2;
			if (typeof($('#' + paramId1)) == 'undefined' || typeof($('#' + paramId2)) == 'undefined') return false;
			var fromDate = $('#' + paramId1).jqxDateTimeInput('getDate');
			var thruDate = $('#' + paramId2).jqxDateTimeInput('getDate');
			if (isNotEmpty(fromDate) && isNotEmpty(thruDate)) {
				if (fromDate > thruDate) {
					return false;
				}
			}
			return true;
		};
		if ('validInputNotNull' == nameValidor) {
			return validInputNotNull(input, commit);
		} else if ('validObjectNotNull' == nameValidor) {
			return validObjectNotNull(input, commit, config);
		} else if ('validDateTimeInputNotNull' == nameValidor) {
			return validDateTimeInputNotNull(input, commit);
		} else if ('validDateCompareToday' == nameValidor) {
			return validDateCompareToday(input, commit);
		} else if ('validDateTimeCompareToday' == nameValidor) {
			return validDateTimeCompareToday(input, commit);
		} else if ('validCannotSpecialCharactor' == nameValidor) {
			return validCannotSpecialCharactor(input, commit);
		} else if ('validPostalCharactor' == nameValidor) {
			return validPostalCharactor(input, commit);
		} else if ('validCompareTwoDate' == nameValidor) {
			return validCompareTwoDate(input, commit, config);
		}
	};
	return {
		isNotEmpty: isNotEmpty,
		validElement: validElement,
	}
}());

// StringBuilder =====================================================
//Initializes a new instance of the StringBuilder class
//and appends the given value if supplied
function StringBuilder(value){
	this.strings = new Array("");
	this.append(value);
}
//Appends the given value to the end of this instance.
StringBuilder.prototype.append = function (value){
	if(value) this.strings.push(value);
}
//Clears the string buffer
StringBuilder.prototype.clear = function (){
	this.strings.length = 1;
}
//Converts this instance to a String.
StringBuilder.prototype.toString = function (){
	return this.strings.join("");
}
// end StringBuilder =====================================================
