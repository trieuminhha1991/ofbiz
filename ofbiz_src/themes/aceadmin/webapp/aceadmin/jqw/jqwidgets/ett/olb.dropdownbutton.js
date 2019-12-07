/**
 * Require OlbGrid object or OlbTreeGrid object
 * OlbCore object
 */
var OlbDropDownButton = function(p_buttonObj, p_gridObj, p_localData, p_config, p_selectArr){
	var _button_obj; // object is jQuery Object
	var _grid_obj; // object is jQuery Object
	var _grid; // object is instance of OlbGrid
	var _config;
	var _selectedIndex_def; // value default
	var _selectArr_def;
	var _seft = this;
	var _type = "dropDownButton";

	function initButton() {
		$(_button_obj).jqxDropDownButton({
			theme: _config.theme,
			width: typeof(_config.widthButton) != "undefined" ? _config.widthButton : 218, 
			height: typeof(_config.heightButton) != "undefined" ? _config.heightButton : 25,
			dropDownHorizontalAlignment: typeof(_config.dropDownHorizontalAlignment) != "undefined" ? _config.dropDownHorizontalAlignment : null,
			disabled: typeof(_config.disabled) != "undefined" ? _config.disabled : false,
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
		if (typeof(_config.autoCloseDropDown) == "undefined") _config.autoCloseDropDown = false;
		if (typeof(_config.theme) == "undefined") _config.theme = OlbCore.theme;
		if (typeof(_config.filterable) == "undefined") _config.filterable = true;
		if (typeof(_config.sortable) == "undefined") _config.sortable = true;
		
		// initialize button in dropDown button
		initButton();
		
		// initialize jqxGrid in dropDown button
		_config.selectedIndex = null;
		
		if (_config.gridType == "jqxTreeGrid") {
			// tree grid
				/* need configuration
				 * parentKeyId: 'parentPeriodId',
				 * gridType: 'jqxTreeGrid',
				 */
			if (typeof(_config.pagesize) == "undefined") _config.pagesize = 10;
			_grid = new OlbTreeGrid(gridObj, localData, _config, null);
		} else {
			// grid
			if (typeof(_config.pagesize) == "undefined") _config.pagesize = 5;
			_grid = new OlbGrid(gridObj, localData, _config, null);
		}
		
		// call listener event on row select item
		var selectionMode = _grid_obj.jqxGrid('selectionmode');
		if ("checkbox" == selectionMode || "multiplerows" == selectionMode) {
			_grid.rowUnSelectListener(function(rowData){
				setContentByRowData(rowData);
			});
		}
		_grid.rowSelectListener(function(rowData){
			setContentByRowData(rowData);
		});
		
		// select default item
		if ((OlbCore.isNotEmpty(_selectArr_def) && _selectArr_def.length > 0) || (OlbCore.isNotEmpty(_config.defaultValue) && _config.defaultLabel) || OlbCore.isNotEmpty(_selectedIndex_def)) {
			_grid.bindingCompleteListener(function(){
				_seft.selectItem();
			}, true);
		}
		
		// show button clear data
		if (_config.showClearButton) {
			createClearButton();
		}
	}
	
	var createClearButton = function(){
		var parent = _button_obj.parent();
		var idClearBtn = _button_obj.attr("id") + "_clearbtn";
		var clearBtnStr = '<a id="' + idClearBtn + '" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx"><i class="fa fa-eraser"></i></a>';
		var clearBtn = $(clearBtnStr);
		parent.css("position", "relative");
		_button_obj.after(clearBtn);
		_button_obj.css("display", "inline-block");
		resizeClearButton(clearBtn);
		
		clearBtn.click(function(){
			_seft.clearAll();
		});
		$(window).resize(function(){
			resizeClearButton(clearBtn);
		});
	}
	
	var resizeClearButton = function(clearBtn){
		var alignLeft = _button_obj.width() + 5;
		clearBtn.css("left", alignLeft);
	}
	
	/*this.selectItem = function(selectArr, selectedIndex, configSelect) {
		console.log("told go", _grid_obj);
		var __selectedIndex = _selectedIndex_def; // __ = local
		var __selectArr = _selectArr_def;
		if (OlbCore.isNotEmpty(selectedIndex)) __selectedIndex = selectedIndex;
		if (OlbCore.isNotEmpty(selectArr)) __selectArr = selectArr;
		var isFinded = false;
		if (OlbCore.isNotEmpty(__selectArr) && __selectArr.length > 0) {
			if (OlbCore.isArray(__selectArr)) {
				// process is array item selected
				var key = _config.key;
				if (_grid.getType() == "grid") {
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
				} else if (_grid.getType() == "treeGrid") {
					var defaultValue = configSelect.defaultValue; // value of key
					var defaultLabel = configSelect.defaultLabel; // value of description
					if (OlbCore.isNotEmpty(defaultValue)) {
						isFinded = true;
						if (_config.displayDetail) defaultLabel += " [" + defaultValue + "]";
						setContent(defaultValue, defaultLabel);
						if (_config.autoCloseDropDown) _button_obj.jqxDropDownButton('close');
					}
				}
			}
		}
		if (!isFinded) {
			var defaultValue = ""; // value of key
			var defaultCode = null; // code of key
			var defaultLabel = ""; // value of description
			if (configSelect) {
				defaultValue = configSelect.defaultValue;
				defaultCode = configSelect.defaultCode;
				defaultLabel = configSelect.defaultLabel;
			} else {
				defaultValue = _config.defaultValue;
				defaultCode = _config.defaultCode;
				defaultLabel = _config.defaultLabel;
			}
			if (OlbCore.isEmpty(defaultCode)) defaultCode = defaultValue;
			if (OlbCore.isNotEmpty(defaultValue)) {
				isFinded = true;
				if (_config.displayDetail) defaultLabel += " [" + defaultCode + "]";
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
	}*/
	
	this.selectItem = function(selectArr, selectedIndex, configSelect) {
		if (OlbCore.isEmpty(selectedIndex) && OlbCore.isEmpty(selectArr)) {
			selectedIndex = _selectedIndex_def;
			selectArr = _selectArr_def;
		}
		
		var key = _config.key;
		if (OlbCore.isEmpty(key)) return false;
		
		var isFinded = false;
		// select by index
		if (OlbCore.isNotEmpty(selectedIndex)){
			var rowData = _grid_obj.jqxGrid('getrowdata', selectedIndex);
			if (rowData) {
				isFinded = true;
				_grid_obj.jqxGrid({selectedrowindex: selectedIndex});
				//setContentByRowData(rowData); // initialize had _grid.rowSelectListener listener
			}
		}
		if (isFinded) {
			return true;
		}
		
		// select by array parameter
		if (OlbCore.isNotEmpty(selectArr) && selectArr.length > 0) {
			if (OlbCore.isArray(selectArr)) {
				// is array of items selected
				if (_grid.getType() == "grid") {
					var rowsData = _grid_obj.jqxGrid('getrows');
					if (rowsData) {
						var selectionMode = _grid_obj.jqxGrid('selectionmode');
						if (selectionMode == "singlerow") {
							var selectItem = selectArr[0];
							for (var i = 0; i < rowsData.length; i++) {
								var rowData = rowsData[i];
								if (rowData != window && selectItem == rowData[key]) {
									isFinded = true;
									//_grid_obj.jqxGrid({selectedrowindex: i});
									setContentByRowData(rowData); // initialize had _grid.rowSelectListener listener
									return true;
								}
							}
							
							if (!isFinded && _config.useUrl) {
								// search ajax
								var gridUrl = _grid_obj.jqxGrid('source')._source.url;
								var gridRoot = _grid_obj.jqxGrid('source')._source.root;
								if (!gridRoot) gridRoot = "results";
								
								var dataMap = buildDataMapFilter(key, selectItem);
								$.ajax({
									type: 'POST',
									url: gridUrl,
									data: dataMap,
									beforeSend: function(){
										//$("#loader_page_common").show();
									},
									success: function(data){
										var results = data[gridRoot];
										if (OlbCore.isNotEmpty(results) && results.length > 0) {
											setContentByRowData(results[0]);
										}
									},
									error: function(data){
										console.log("Send request is error");
									},
									complete: function(data){
										//$("#loader_page_common").hide();
									},
								});
							}
						} else if (selectionMode == "multiplerows" || selectionMode == "checkbox") {
							// TODO
							$.each(selectArr, function(itemKey, itemValue){
								for (var i = 0; i < rowsData.length; i++) {
									var rowData = rowsData[i];
									if (rowData != window && itemValue == rowData[key]) {
										isFinded = true;
										_grid_obj.jqxGrid({selectedrowindex: i});
										//setContentByRowData(rowData); // initialize had _grid.rowSelectListener listener
										//return false;
									}
								}
								//if (!isFinded) setContent(itemValue, "[" + itemValue + "]");
								//return false;
							});
							setContentByRowData();
						}
					}
				} else if (_grid.getType() == "treeGrid") {
					var defaultValue = configSelect.defaultValue; // value of key
					var defaultLabel = configSelect.defaultLabel; // value of description
					if (OlbCore.isNotEmpty(defaultValue)) {
						isFinded = true;
						if (_config.displayDetail) defaultLabel += " [" + defaultValue + "]";
						setContent(defaultValue, defaultLabel);
						if (_config.autoCloseDropDown) _button_obj.jqxDropDownButton('close');
					}
				}
			}
		}
		/*if (!isFinded) {
			var defaultValue = ""; // value of key
			var defaultCode = null; // code of key
			var defaultLabel = ""; // value of description
			if (configSelect) {
				defaultValue = configSelect.defaultValue;
				defaultCode = configSelect.defaultCode;
				defaultLabel = configSelect.defaultLabel;
			} else {
				defaultValue = _config.defaultValue;
				defaultCode = _config.defaultCode;
				defaultLabel = _config.defaultLabel;
			}
			if (OlbCore.isEmpty(defaultCode)) defaultCode = defaultValue;
			if (OlbCore.isNotEmpty(defaultValue)) {
				isFinded = true;
				if (_config.displayDetail) defaultLabel += " [" + defaultCode + "]";
				setContent(defaultValue, defaultLabel);
				if (_config.autoCloseDropDown) _button_obj.jqxDropDownButton('close');
			}
		}*/
	}
	
	function buildDataMapFilter(key, value) {
		var totalSize = 1;
		return {
			partyCodeoperator: "and",
			filtervalue0: value,
			filtercondition0: "EQUAL",
			filteroperator0: 1,
			filterdatafield0: key,
			filterscount: 1,
			groupscount: 0,
			sortdatafield: "",
			sortorder: "asc",
			pagenum: 0,
			pagesize: totalSize,
			recordstartindex: 0,
			recordendindex: totalSize,
			noConditionFind: "Y",
			conditionsFind: "N",
			filterListFields: "|OLBIUS|" + key + "|SUIBLO|" + value + "|SUIBLO|EQUAL|SUIBLO|1",
			$skip: 0,
			$top: totalSize,
			$inlinecount: "allpages",
			otherParams: "",
		}
	}
	
	// processRowSelectedDropDownButton
	function setContentByRowData(rowData){
		var selectionMode = _grid_obj.jqxGrid('selectionmode');
		if ("checkbox" == selectionMode || "multiplerows" == selectionMode) {
			var selectedValue = _grid.getValueOrigin();
			var selectedSize = selectedValue ? Object.keys(selectedValue).length : 0;
			var valDescription = "" + selectedSize + " item selected";
			var content = '<div class="innerDropdownContent">' + valDescription + '</div>';
	        _button_obj.jqxDropDownButton('setContent', content);
		} else {
			if (rowData) {
				var key = _config.key;
				var keyMain = typeof(_config.keyCode) != "undefined" ? _config.keyCode : key;
				var description = _config.description;
				var valKey = rowData[key];
				var valKeyMain = rowData[keyMain];
				var valDescription = valKeyMain;
				
				if (typeof(valKeyMain) == "undefined") valKeyMain = valKey;
				if (typeof(description) == "function") {
					valDescription = description(rowData);
				} else if (Object.prototype.toString.call(description) === '[object Array]') {
					valDescription = processDescriptionByArray(rowData, description);
				} else if (typeof(description) == 'string') {
					valDescription = rowData[description];
				}
				
				if (_config.displayDetail) valDescription += " [" + valKeyMain + "]";
				setContent(valKey, valDescription);
				
				if (_config.autoCloseDropDown) _button_obj.jqxDropDownButton('close');
			}
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
	
	this.getType = function(){
		return _type;
	}
	
	this.disable = function(isDisable){
		if (typeof(isDisable) != "undefined") _button_obj.jqxDropDownButton('disabled', isDisable);
	}
	
	this.getValue2 = function(isFullRow){
		isFullRow = !!isFullRow;
		if (isFullRow) {
			return _grid.getValue();
		} else {
			return _grid.getValue(_config.key);
		}
	}
	
	return init(p_buttonObj, p_gridObj, p_localData, p_config, p_selectArr);
};

OlbDropDownButton.prototype.getAttrDataValue = function() {
	return OlbDropDownButtonUtil.getAttrDataValue(this.getButtonObj());
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
	if (__grid_obj.jqxGrid("getfilterinformation") && __grid_obj.jqxGrid("getfilterinformation").length > 0) {
		__grid_obj.jqxGrid('clearfilters');
	}
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

var OlbDropDownButtonUtil = (function(){
	var getAttrDataValue = function(buttonObj){
		var id = $(buttonObj).attr("id");
		var obj = document.getElementById(id);
		if (OlbCore.isNotEmpty(obj)) {
			return obj.getAttribute("data-value");
		}
	};
	return {
		getAttrDataValue: getAttrDataValue
	};
}());