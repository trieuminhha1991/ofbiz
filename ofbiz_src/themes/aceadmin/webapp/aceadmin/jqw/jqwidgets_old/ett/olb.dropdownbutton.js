/**
 * Require OlbGrid Object
 */
var OlbDropDownButton = function(p_buttonObj, p_gridObj, p_localData, p_config, p_selectArr){
	var _button_obj;
	var _grid_obj;
	var _grid;
	var _config;
	var _selectedIndex_def; // value default
	var _selectArr_def;
	var _seft = this;

	function initButton() {
		$(_button_obj).jqxDropDownButton({
			theme: _config.theme,
			width: typeof(_config.widthButton) != "undefined" ? _config.widthButton : 218,
			height: typeof(_config.heightButton) != "undefined" ? _config.heightButton : 25,
			dropDownHorizontalAlignment: typeof(_config.dropDownHorizontalAlignment) != "undefined" ? _config.dropDownHorizontalAlignment : null,
		});
	}

	/**
     * Initialize DropDown Button
     * @param buttonObj
     * @param gridObj
     * @param config
     * @param selectArr
     * @return DROP_DOWN_BUTTON
     */
	function init(buttonObj, gridObj, localData, config, selectArr) {
		_button_obj = buttonObj;
		_grid_obj = gridObj;
		_config = config;

		if (!OlbCore.isNotEmpty(buttonObj)) {
			_grid = new OlbGrid(gridObj, null, null, null);
			return;
		}

		_selectedIndex_def = config.selectedIndex;
		_selectArr_def = selectArr;
		if (typeof(_config.displayDetail) == "undefined") _config.displayDetail = true;
		if (typeof(_config.showdefaultloadelement) == "undefined") _config.showdefaultloadelement = false;
		if (typeof(_config.autoshowloadelement) == "undefined") _config.autoshowloadelement = false;
		if (typeof(_config.autoCloseDropDown) == "undefined") _config.autoCloseDropDown = true;
		if (typeof(_config.theme) == "undefined") _config.theme = OlbCore.theme;
		if (typeof(_config.pagesize) == "undefined") _config.pagesize = 10;

		// initialize button in dropDown button
		initButton();

		// initialize jqxGrid in dropDown button
		config.selectedIndex = null;
		_grid = new OlbGrid(gridObj, localData, config, null);

		// call listener event on row select item
		_grid.rowSelectListener(function(rowData){
			setContentByRowData(rowData);
		});

		// select default item
		if ((OlbCore.isNotEmpty(_selectArr_def) && _selectArr_def.length > 0) || (OlbCore.isNotEmpty(_config.defaultValue) && _config.defaultLabel) || OlbCore.isNotEmpty(_selectedIndex_def)) {
			_grid.bindingCompleteListener(function(){
				_seft.selectItem();
			}, true);
		}
	}

	this.selectItem = function(selectArr, selectedIndex) {
		var __selectedIndex = _selectedIndex_def; // __ = local
		var __selectArr = _selectArr_def;
		if (OlbCore.isNotEmpty(selectedIndex)) __selectedIndex = selectedIndex;
		if (OlbCore.isNotEmpty(selectArr)) __selectArr = selectArr;
		var isFinded = false;
		if (OlbCore.isNotEmpty(__selectArr) && __selectArr.length > 0) {
			if (OlbCore.isArray(__selectArr)) {
				// process is array item selected
				var key = _config.key;
				var rowsData = _grid_obj.jqxGrid('getrows');
				if (key != null && rowsData != null) {
					$.each(__selectArr, function(itemKey, itemValue){
						for (var i = 0; i < rowsData.length; i++) {
							var rowData = rowsData[i];
							if (rowData != window && itemValue == rowsData[i][key]) {
								isFinded = true;
								// _grid_obj.jqxGrid({selectedrowindex: i});
								setContentByRowData(rowData); // initialize had _grid.rowSelectListener listener
								return false;
							}
						}
						if (!isFinded) setContent(itemValue, "[" + itemValue + "]");
						return false;
					});
				}
			}
		}
		if (!isFinded) {
			var defaultValue = _config.defaultValue; // value of key
			var defaultLabel = _config.defaultLabel; // value of description
			if (OlbCore.isNotEmpty(defaultValue)) {
				isFinded = true;
				if (_config.displayDetail) defaultLabel += " [" + defaultValue + "]";
				setContent(defaultValue, defaultLabel);
				if (_config.autoCloseDropDown) _button_obj.jqxDropDownButton('close');
			}
		}
		if (!isFinded) {
			if (OlbCore.isNotEmpty(__selectedIndex)){
				var rowData = _grid_obj.jqxGrid('getrowdata', __selectedIndex);
				if (rowData) {
					// _grid_obj.jqxGrid({selectedrowindex: __selectedIndex});
					setContentByRowData(rowData); // initialize had _grid.rowSelectListener listener
				}
			}
		}
	}

	// processRowSelectedDropDownButton
	function setContentByRowData(rowData){
		if (rowData) {
			var key = _config.key;
			var description = _config.description;
			var valKey = rowData[key];
			var valDescription = valKey;
			if (typeof(description) == "function") {
				valDescription = description(rowData);
			} else if (Object.prototype.toString.call(description) === '[object Array]') {
				valDescription = processDescriptionByArray(rowData, description);
			} else if (typeof(description) == 'string') {
				valDescription = rowData[description];
			}

			if (_config.displayDetail) valDescription += " [" + valKey + "]";
			setContent(valKey, valDescription);

			if (_config.autoCloseDropDown) _button_obj.jqxDropDownButton('close');
		}
	}

	function processDescriptionByArray(rowData, idArr){
		var result = [];
		$.each(idArr, function(key, value){
			if (OlbCore.isNotEmpty(value) && OlbCore.isNotEmpty(rowData[value])) {
				result.push(rowData[value]);
			}
		});
		return result.join(", ");
	};

	// setValueDropDownButtonOnly
	function setContent(valKey, valDescription) {
		var content = '<div class="innerDropdownContent">' + valDescription + '</div>';
        _button_obj.jqxDropDownButton('setContent', content);
        if (OlbCore.isNotEmpty(_button_obj)) _seft.setAttrDataValue(valKey);
	}

	this.getGrid = function(){
		return _grid;
	}
	this.getGridObj = function(){
		return _grid_obj;
	}
	this.getButtonObj = function(){
		return _button_obj;
	}
	this.getSeft = function(){
		return _seft;
	}
	this.putConfig = function(key, value){
		_config[key] = value;
	}

	return init(p_buttonObj, p_gridObj, p_localData, p_config, p_selectArr);
};

OlbDropDownButton.prototype.getAttrDataValue = function() {
	var id = this.getButtonObj().attr("id");
	var obj = document.getElementById(id);
	if (OlbCore.isNotEmpty(obj)) {
		return obj.getAttribute("data-value");
	}
};
OlbDropDownButton.prototype.setAttrDataValue = function(value) {
	var id = this.getButtonObj().attr("id");
	var obj = document.getElementById(id);
	if (OlbCore.isNotEmpty(obj)) {
		obj.setAttribute("data-value", value);
		this.getButtonObj().trigger('valueChange');
	}
};
OlbDropDownButton.prototype.getValue = function() {
	return this.getAttrDataValue();
};
OlbDropDownButton.prototype.setValue = function(value) {
	return this.setAttrDataValue(value);
};
OlbDropDownButton.prototype.on = function(action, callback){
	if (typeof(callback) == "function") {
		this.getButtonObj().on(action, callback);
	}
};
OlbDropDownButton.prototype.clearAttrDataValue = function(){
	this.setAttrDataValue("");
};
OlbDropDownButton.prototype.clearAll = function(isClearSource){
	var __button_obj = this.getButtonObj();
	var __grid_obj = this.getGridObj();

	__button_obj.jqxDropDownButton('setContent', '');
	__grid_obj.jqxGrid('clearselection');
	__grid_obj.jqxGrid('clearfilters');
	if (isClearSource) __grid_obj.jqxGrid('clear');
	var buttonId = __button_obj.attr('id');
	if (OlbCore.isNotEmpty(buttonId)) this.clearAttrDataValue(buttonId);
};
OlbDropDownButton.prototype.updateSource = function(newUrl, localData, callback) {
	/*var __grid_obj = this.getGridObj();
	var __button_obj = this.getButtonObj();

	var tmpSource = __grid_obj.jqxGrid('source');
	if (tmpSource) {
		if (newUrl) {
			tmpSource._source.url = newUrl;
			__button_obj.jqxDropDownButton('setContent', '');
			__grid_obj.jqxGrid('clearselection');
			__grid_obj.jqxGrid('source', tmpSource);
			var buttonId = __button_obj.attr('id');
			if (OlbCore.isNotEmpty(buttonId)) this.clearAttrDataValue(buttonId);
		} else if (localData) {
			// TODO
		}
	}*/
	if (!OlbCore.isNotEmpty(this.getGridObj())) return;

	var __grid = this.getGrid();
	var __button_obj = this.getButtonObj();
	var __button = this.getSeft();

	__grid.updateSource(newUrl, localData, function(){
		__button_obj.jqxDropDownButton('setContent', '');
		var buttonId = __button_obj.attr('id');
		if (OlbCore.isNotEmpty(buttonId)) __button.clearAttrDataValue(buttonId);
	});

	if (typeof(callback) == "function") {
		this.getGrid().bindingCompleteListener(function(){
			callback();
		}, true);
	}
};