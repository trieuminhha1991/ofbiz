/**
 * Require OlbCore Object
 */
var OlbValidator = function(p_containerObj, p_mapRules, p_extendRules, p_config){
	var _container_obj;
	var _map_rules;
	var _extend_rules;
	var _config;
	var _seft = this;
	
	/**
     * Initialize Validator
     * @param containerObj
     * @param mapRules
     * @param extendRules
     * @param config
     * @return VALIDATOR
     */
	var init = function(containerObj, mapRules, extendRules, config){
		_container_obj = containerObj;
		_map_rules = mapRules;
		_extend_rules = extendRules;
		_config = typeof(config) != "undefined" ? config : {};
		_config.position = typeof(_config.position) != "undefined" ? _config.position : "bottom";
		
		createValidator();
		
		/*if ($(_container_obj).length > 0) {
			$(_container_obj).on("close", function(){
				$(_container_obj).jqxValidator("hide");
			});
		}*/
	}
	
	this.validate = function(){
		return _container_obj.jqxValidator('validate');
	};
	this.hide = function(){
		_container_obj.jqxValidator('hide');
	}
	
	var createValidator = function(){
		var rules = [];
		
		var default_message = {
			validFieldRequire: "Field require",
			validContainSpecialCharacter: "This data field must not by contain special character",
			validOnlyContainCharacterAZaz09: "Only contains characters 'a-z' or 'A-Z' or digits (0-9)",
			validOnlyContainCharacterNumber: "Only contains characters number",
			validEmailAddress: "Email address is not valid",
			validPhoneNumber: "Phone number is not valid",
			validRequiredValueGreatherOrEqualDateTimeToDay: "Required value greather than or equal date time today",
			validStartDateMustLessThanOrEqualFinishDate: "Start date must less than or equal finish date",
			validRequiredValueGreatherOrEqualToDay: "Required value greather than or equal today",
		};
		
		if (OlbCore.isArray(_map_rules)) {
			var isAdd = false;
			
			$.each(_map_rules, function(key, mapItem){
				var input = mapItem.input;
				var message = mapItem.message;
				var action = mapItem.action;
				var rule = mapItem.rule;
				var type = mapItem.type;
				
				isAdd = false;
				var mapItemRule = {};
				if ("validCannotSpecialCharactor" == type) {
					action = processAction(action, 'keyup');
					message = processMessage(message, uiLabelMap.validContainSpecialCharacter, default_message.validContainSpecialCharacter);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validCannotSpecialCharactor');
					});
					isAdd = true;
				} else if ("validOnlyContainCharacterAZaz09" == type) {
						action = processAction(action, 'keyup');
						message = processMessage(message, uiLabelMap.validOnlyContainCharacterAZaz09, default_message.validOnlyContainCharacterAZaz09);
						rule = processRule(rule, function (input, commit) {
							return OlbValidatorUtil.validElement(input, commit, 'validOnlyContainCharacterAZaz09');
						});
						isAdd = true;
				} else if ("validOnlyContainCharacterNumber" == type) {
					action = processAction(action, 'keyup');
					message = processMessage(message, uiLabelMap.validOnlyContainCharacterNumber, default_message.validOnlyContainCharacterNumber);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validOnlyContainCharacterNumber');
					});
					isAdd = true;
				} else if ("validEmailAddress" == type) {
					action = processAction(action, 'keyup');
					message = processMessage(message, uiLabelMap.validEmailAddress, default_message.validEmailAddress);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validEmailAddress');
					});
					isAdd = true;
				} else if ("validPhoneNumber" == type) {
					action = processAction(action, 'keyup');
					message = processMessage(message, uiLabelMap.validPhoneNumber, default_message.validPhoneNumber);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validPhoneNumber');
					});
					isAdd = true;
				} else if ("validObjectNotNull" == type) {
					// check type of object
					var objType = mapItem.objType;
					if (objType) {
						if (objType == "dropDownButton") {
							action = processAction(action, 'close');
						} else {
							action = processAction(action, 'change');
						}
					} else {
						action = processAction(action, 'change');
					}
					
					message = processMessage(message, uiLabelMap.validFieldRequire, default_message.validFieldRequire);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validObjectNotNull', {objType: mapItem.objType});
					});
					isAdd = true;
				} else if ("validInputNotNull" == type) {
					action = processAction(action, 'keyup');
					message = processMessage(message, uiLabelMap.validFieldRequire, default_message.validFieldRequire);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
					});
					isAdd = true;
				} else if ("validDateTimeInputNotNull" == type) {
					action = processAction(action, 'valueChanged');
					message = processMessage(message, uiLabelMap.validFieldRequire, default_message.validFieldRequire);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validDateTimeInputNotNull');
					});
					isAdd = true;
				} else if ("validDateTimeCompareToday" == type) {
					action = processAction(action, 'valueChanged');
					message = processMessage(message, uiLabelMap.validRequiredValueGreatherOrEqualDateTimeToDay, default_message.validRequiredValueGreatherOrEqualDateTimeToDay);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validDateTimeCompareToday');
					});
					isAdd = true;
				} else if ("validDateCompareToday" == type) {
					action = processAction(action, 'valueChanged');
					message = processMessage(message, uiLabelMap.validRequiredValueGreatherOrEqualToDay, default_message.validRequiredValueGreatherOrEqualToDay);
					rule = processRule(rule, function (input, commit) {
						return OlbValidatorUtil.validElement(input, commit, 'validDateCompareToday');
					});
					isAdd = true;
				} else if ("validCompareTwoDate" == type) {
					action = processAction(action, 'valueChanged');
					message = processMessage(message, uiLabelMap.validStartDateMustLessThanOrEqualFinishDate, default_message.validStartDateMustLessThanOrEqualFinishDate);
					rule = processRule(rule, function (input, commit) {
						var __paramId1 = mapItem.paramId1 != undefined ? mapItem.paramId1 : "fromDate";
						var __paramId2 = mapItem.paramId2 != undefined ? mapItem.paramId2 : "thruDate";
						return OlbValidatorUtil.validElement(input, commit, 'validCompareTwoDate', {paramId1 : __paramId1, paramId2 : __paramId2});
					});
					isAdd = true;
				} else {
					if (input != undefined && message != undefined && action != undefined && typeof(rule) == 'function') {
						isAdd = true;
					}
				}
				if (isAdd){
					mapItemRule = {
						input: input, message: message, action: action, 
						rule: function (input, commit) {
							return rule(input, commit);
						}
					}
					rules.push(mapItemRule);
				}
			});
		}
		if (_extend_rules != undefined && _extend_rules != null) {
			$.each(_extend_rules, function(key, value){
				rules.push(value);
			});
		}
		_container_obj.jqxValidator({
			position: _config.position, 
			scroll: typeof(_config.scroll) != "undefined" ? _config.scroll : false,
			rules: rules
		});
		
		return rules;
	};
	
	var processMessage = function(message, defaultMessageGlobal, defaultMessageLocal){
		if (message == undefined || message == null) {
			if (defaultMessageGlobal == undefined || defaultMessageGlobal == null) message = defaultMessageLocal;
			else message = defaultMessageGlobal;
		}
		return message;
	};
	var processAction = function(action, defaultAction){
		if (action == undefined || action == null) action = defaultAction;
		return action;
	};
	var processRule = function(rule, callback){
		if (rule == undefined || rule == null || typeof(rule) != 'function') {
			rule = function (input, commit) {
				return callback(input, commit);
			}
		}
		return rule;
	};
	
	return init(p_containerObj, p_mapRules, p_extendRules, p_config);
};

var OlbValidatorUtil = (function(){
	var validElement = function(input, commit, nameValidator, config){
		var validInputNotNull = function(input, commit){
			var value = $(input).val();
			if(OlbCore.isNotEmpty(value)){
				return true;
			}
			return false;
		};
		var validObjectNotNull = function(input, commit, config){
			var objType = config.objType;
			if (objType != undefined) {
				if (objType == "dropDownList") {
					var item = $(input).jqxDropDownList("getSelectedItem");
					if (item != undefined) {
						if (OlbCore.isNotEmpty(item.value)) {
							return true;
						}
					}
				} else if (objType == "comboBox") {
					var item = $(input).jqxComboBox("getSelectedItem");
					if (item != undefined) {
						if (OlbCore.isNotEmpty(item.value)) {
							return true;
						}
					}
				} else if (objType == "dropDownButton") {
					var idObj = $(input).attr("id");
					if (idObj != undefined) {
						var value = jOlbUtil.getAttrDataValue(idObj);
						if(OlbCore.isNotEmpty(value)){
							return true;
						}
					}
				} else if (objType == "comboBoxMulti") {
					var count = 0;
					var dataListSelected = $(input).jqxComboBox('getSelectedItems');
					if (OlbCore.isNotEmpty(dataListSelected)) {
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
			if(OlbCore.isNotEmpty(value)){
				return true;
			}
			return false;
		};
		var validDateCompareToday = function (input, commit) {
			var value = $(input).jqxDateTimeInput('getDate');
			if (OlbCore.isNotEmpty(value)) {
				var now = new Date();
				now.setHours(0,0,0,0);
	    		if(value < now){return false;}
			}
    		return true;
		};
		var validDateTimeCompareToday = function (input, commit) {
			var value = $(input).jqxDateTimeInput('getDate');
			if (OlbCore.isNotEmpty(value)) {
				var now = new Date();
	    		if(value < now){return false;}
			}
    		return true;
		};
		var validCannotSpecialCharactor = function (input, commit) {
			var value = $(input).val();
			if(OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9_]+$/.test(value))){
				return false;
			}
			return true;
		};
		var validOnlyContainCharacterAZaz09 = function (input, commit) {
			var value = $(input).val();
			if(OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9]+$/.test(value))){
				return false;
			}
			return true;
		};
		var validOnlyContainCharacterNumber = function (input, commit) {
			var value = $(input).val();
			if(OlbCore.isNotEmpty(value) && !(/^[0-9]+$/.test(value))){
				return false;
			}
			return true;
		};
		var validEmailAddress = function (input, commit) {
			var value = $(input).val();
			if(OlbCore.isNotEmpty(value) && !(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(value))){
				return false;
			}
			return true;
		};
		var validPhoneNumber = function (input, commit) {
			/* validPhoneNumber matches:
		     - (+351) 282 43 50 50
			 - 90191919908
			 - 555-8909
			 - 001 6867684
			 - 001 6867684x1
			 - 1 (234) 567-8901
			 - 1-234-567-8901 x1234
			 - 1-234-567-8901 ext1234
			 - 1-234 567.89/01 ext.1234
			 - 1(234)5678901x1234
			 - (123)8575973
			 - (0055)(123)8575973
			 */
			var value = $(input).val();
			if(OlbCore.isNotEmpty(value) && !(/^(?:(?:\(?(?:00|\+)([1-4]\d\d|[1-9]\d?)\)?)?[\-\.\ \\\/]?)?((?:\(?\d{1,}\)?[\-\.\ \\\/]?){0,})(?:[\-\.\ \\\/]?(?:#|ext\.?|extension|x)[\-\.\ \\\/]?(\d+))?$/i.test(value))){
				return false;
			}
			return true;
		};
		var validPostalCharactor = function (input, commit) {
			var value = $(input).val();
			if(OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9-]+$/.test(value))){
				return false;
			}
			return true;
		};
		var validCompareTwoDate = function (input, commit, config) {
			var paramId1 = "fromDate";
			if (OlbCore.isNotEmpty(config) && OlbCore.isNotEmpty(config.paramId1)) paramId1 = config.paramId1;
			var paramId2 = "thruDate";
			if (OlbCore.isNotEmpty(config) && OlbCore.isNotEmpty(config.paramId2)) paramId2 = config.paramId2;
			if (typeof($('#' + paramId1)) == 'undefined' || typeof($('#' + paramId2)) == 'undefined') return false;
			var fromDate = $('#' + paramId1).jqxDateTimeInput('getDate');
			var thruDate = $('#' + paramId2).jqxDateTimeInput('getDate');
			if (OlbCore.isNotEmpty(fromDate) && OlbCore.isNotEmpty(thruDate)) {
				if (fromDate > thruDate) {
					return false;
				}
			}
			return true;
		};
		var validDeliveryDateNotNull = function (input, commit) {
			var desiredDeliveryDate = $('#desiredDeliveryDate').jqxDateTimeInput('getDate');
			var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
			var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
			if(OlbCore.isNotEmpty(desiredDeliveryDate) || (OlbCore.isNotEmpty(shipAfterDate) && OlbCore.isNotEmpty(shipBeforeDate))){
				return true;
			}
			return false;
		};
		var validCompareStartDateAndFinishDate = function (input, commit) {
			var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
			var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
			if (OlbCore.isNotEmpty(shipAfterDate) && OlbCore.isNotEmpty(shipBeforeDate)) {
				if (shipAfterDate > shipBeforeDate) {
					return false;
				}
			}
			return true;
		};
		var validCompareDeliveryDateBetweenStartDateAndFinishDate = function (input, commit) {
			var desiredDeliveryDate = $('#desiredDeliveryDate').jqxDateTimeInput('getDate');
			var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
			var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
			if (OlbCore.isNotEmpty(desiredDeliveryDate)) {
				if (OlbCore.isNotEmpty(shipAfterDate)) {
					if (desiredDeliveryDate < shipAfterDate) {
						return false;
					}
				}
				if (OlbCore.isNotEmpty(shipBeforeDate)) {
					if (desiredDeliveryDate > shipBeforeDate) {
						return false;
					}
				}
			}
			return true;
		};
		if ('validInputNotNull' == nameValidator) {
			return validInputNotNull(input, commit);
		} else if ('validObjectNotNull' == nameValidator) {
			return validObjectNotNull(input, commit, config);
		} else if ('validDateTimeInputNotNull' == nameValidator) {
			return validDateTimeInputNotNull(input, commit);
		} else if ('validDateCompareToday' == nameValidator) {
			return validDateCompareToday(input, commit);
		} else if ('validDateTimeCompareToday' == nameValidator) {
			return validDateTimeCompareToday(input, commit);
		} else if ('validCannotSpecialCharactor' == nameValidator) {
			return validCannotSpecialCharactor(input, commit);
		} else if ('validOnlyContainCharacterAZaz09' == nameValidator) {
			return validOnlyContainCharacterAZaz09(input, commit);
		} else if ('validOnlyContainCharacterNumber' == nameValidator) {
			return validOnlyContainCharacterNumber(input, commit);
		} else if ('validEmailAddress' == nameValidator) {
			return validEmailAddress(input, commit);
		} else if ('validPhoneNumber' == nameValidator) {
			return validPhoneNumber(input, commit);
		} else if ('validPostalCharactor' == nameValidator) {
			return validPostalCharactor(input, commit);
		} else if ('validCompareTwoDate' == nameValidator) {
			return validCompareTwoDate(input, commit, config);
		} else if ('validDeliveryDateNotNull' == nameValidator) { // only sales
			return validDeliveryDateNotNull(input, commit);
		} else if ('validCompareStartDateAndFinishDate' == nameValidator) { // only sales
			return validCompareStartDateAndFinishDate(input, commit);
		} else if ('validCompareDeliveryDateBetweenStartDateAndFinishDate' == nameValidator) { // only sales
			return validCompareDeliveryDateBetweenStartDateAndFinishDate(input, commit);
		}
	};
	return {
		validElement: validElement
	}
}());
