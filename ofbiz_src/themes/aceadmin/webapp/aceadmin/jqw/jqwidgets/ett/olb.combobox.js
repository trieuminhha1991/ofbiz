/**
 * Require OlbCore Object
 */
var OlbComboBox = function(p_listObj, p_localData, p_config, p_selectArr){
	var _list_obj;
	var _config;
	var _selectedIndex_def; // value default
	var _selectArr_def;
	var _seft = this;
	var _datafields;
	var _columns;
	var _multiSelect = false;
	var _type = "comboBox";
	
	/**
     * Initialize Combo box
     * @param listObj
     * @param config
     * @param selectArr
     * @return COMBO_BOX
     */
	function init(listObj, localData, config, selectArr){
		if (typeof(config) == undefined) config = [];
		_list_obj = listObj;
		_config = config;
		_selectedIndex_def = config.selectedIndex;
		_selectArr_def = selectArr;
		_datafields = config.dataFields;
		_columns = config.columns;
		_multiSelect = typeof(_config.multiSelect) != "undefined" ? _config.multiSelect : false;
		_config.remoteAutoComplete = typeof(_config.remoteAutoComplete) != "undefined" ? _config.remoteAutoComplete : false;
		
		var renderer = _config.renderer;
		var renderSelectedItem = _config.renderSelectedItem;
		if (typeof renderer == "undefined") {
			renderer = function (index, label, value) {
	            var valueStr;
	            if (_config.displayDetail) {
	            	valueStr = label + " [" + value + "]";
	            } else {
	            	valueStr = label;
	            }
	            return valueStr;
	        };
		}
        if (typeof renderSelectedItem == "undefined") {
        	renderSelectedItem = function(index, item) {
    			if (OlbCore.isNotEmpty(item)) {
    				return item.description;
    			}
                return "";
            };
        }
        var search = _config.search;
        if (typeof search == "undefined") {
        	search = function (searchString) {
				dataAdapter.dataBind();
	        }
        }
		
		var dataSource;
		if (_config.useUrl && _config.url != null) {
			dataSource = {
				datatype: 	typeof(_config.datatype) != "undefined" ? _config.datatype : "json",
				root:		typeof(_config.root) != "undefined" ? _config.root : "results",
				async: 		typeof(_config.async) != "undefined" ? _config.async : false,
				contentType: typeof(_config.contentType) != "undefined" ? _config.contentType : null,
				datafields: _datafields,
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				url: _config.url,
			};
		} else {
			dataSource = {
	        	localdata: localData,
		        datatype: typeof(_config.datatype) != "undefined" ? _config.datatype : "array",
		        datafields: _datafields,
		    };
		}
		
		//$.jqx.theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
		var theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
	    var dataAdapter;
	    if (_config.remoteAutoComplete) {
	    	var formatData = _config.formatData;
	    	var formatDataFuncItem = _config.formatDataFuncItem;
	    	if (typeof _config.formatData == "undefined") {
				formatData = function (data) {
					var searchStringValue = $(_list_obj).jqxComboBox('searchString');
		        	if (searchStringValue != undefined) {
		        		searchStringValue = searchStringValue.trim();
			            if (typeof formatDataFuncItem == "function") {
			            	return formatDataFuncItem(data, searchStringValue);
			            }
			            var keySearchString = typeof _config.keySearchString != "undefined" ? _config.keySearchString : "searchKey";
			            data[keySearchString] = searchStringValue;
			            return data;
		            }
		        	/*if ($(_list_obj).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $(_list_obj).jqxComboBox('searchString');
	                    return data;
	                }*/
		        };
			}
	    	// make data adapter
	    	dataAdapter = new $.jqx.dataAdapter(dataSource, {
    			formatData: formatData
	    	});
	    } else {
	    	dataAdapter = new $.jqx.dataAdapter(dataSource);
	    }
	    _list_obj.jqxComboBox({
	    	theme: theme,
	    	source: dataAdapter, 
	    	multiSelect: _multiSelect, 
	    	width: 				typeof(_config.width) != "undefined" ? _config.width : 218, 
	    	height: 			typeof(_config.height) != "undefined" ? _config.height : 25, 
	    	displayMember: 		typeof(_config.value) != "undefined" ? _config.value : null, 
	    	valueMember: 		typeof(_config.key) != "undefined" ? _config.key : null, 
	    	disabled: 			typeof(_config.disabled) != "undefined" ? _config.disabled : null,
	    	dropDownWidth: 		typeof(_config.dropDownWidth) != "undefined" ? _config.dropDownWidth : "auto", 
	    	dropDownHeight: 	typeof(_config.dropDownHeight) != "undefined" ? _config.dropDownHeight : null, 
	    	autoDropDownHeight: typeof(_config.autoDropDownHeight) != "undefined" ? _config.autoDropDownHeight : null, 
	    	placeHolder: 		typeof(_config.placeHolder) != "undefined" ? _config.placeHolder : "Click to choose",
			renderer: renderer,
			renderSelectedItem: renderSelectedItem,
			dropDownHorizontalAlignment: typeof(_config.dropDownHorizontalAlignment) != "undefined" ? _config.dropDownHorizontalAlignment : null,
			autoComplete: 		typeof(_config.autoComplete) != "undefined" ? _config.autoComplete : null,
			searchMode: 		typeof(_config.searchMode) != "undefined" ? _config.searchMode : "containsignorecase",
			search: search,
	        autoOpen: typeof(_config.autoOpen) != "undefined" ? _config.autoOpen : false,
    		remoteAutoComplete: typeof(_config.remoteAutoComplete) != "undefined" ? _config.remoteAutoComplete : false,
			remoteAutoCompleteDelay: typeof(_config.remoteAutoCompleteDelay) != "undefined" ? _config.remoteAutoCompleteDelay : 500,
			minLength: typeof(_config.minLength) != "undefined" ? _config.minLength : 2,
	    });
	    
	    // select default item
		_seft.selectItem();
		
		// show button clear data
		if (_config.showClearButton) {
			createClearButton();
		}
		
		var searchKeyCode = _config.searchKeyCode;
		if (typeof searchKeyCode != "undefined") {
			$(_list_obj).keypress(function(e) {
			    if (e.which == searchKeyCode) {
			    	dataAdapter.dataBind();
			    }
			});
		}
	}
	
	var createClearButton = function(){
		var parent = _list_obj.parent();
		var idClearBtn = _list_obj.attr("id") + "_clearbtn";
		var clearBtnStr = '<a id="' + idClearBtn + '" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="' + uiLabelMap.BSClear + '"><i class="fa fa-eraser"></i></a>';
		var clearBtn = $(clearBtnStr);
		parent.css("position", "relative");
		_list_obj.after(clearBtn);
		_list_obj.css("display", "inline-block");
		resizeClearButton(clearBtn);
		
		clearBtn.click(function(){_list_obj.jqxComboBox('clearSelection')});
		$(window).resize(function(){
			resizeClearButton(clearBtn);
		});
	}
	
	var resizeClearButton = function(clearBtn){
		var alignLeft = _list_obj.width() + 5;
		clearBtn.css("left", alignLeft);
	}
	
	this.selectItem = function(selectArr, selectedIndex) {
		var __selectedIndex = _selectedIndex_def; // __ = local
		var __selectArr = _selectArr_def;
		if (OlbCore.isNotEmpty(selectedIndex)) __selectedIndex = selectedIndex;
		if (OlbCore.isNotEmpty(selectArr)) __selectArr = selectArr;
		
		OlbComboBoxUtil.selectItem(_list_obj, __selectArr, __selectedIndex);
	}
	
	this.getListObj = function(){
		return _list_obj;
	}
	
	this.isMultiSelect = function(){
		return _multiSelect;
	}
	
	this.getType = function(){
		return _type;
	}
	
	this.disable = function(isDisable){
		if (typeof(isDisable) != "undefined") _list_obj.jqxComboBox('disabled', isDisable);
	}
	
	return init(p_listObj, p_localData, p_config, p_selectArr);
}
OlbComboBox.prototype.bindingCompleteListener = function(callback, onlyOnce) {
	if (onlyOnce) {
		this.getListObj().one('bindingComplete', function (event) {
			callback();
	    });
	} else {
		this.getListObj().on('bindingComplete', function (event) {
			callback();
	    });
	}
};
OlbComboBox.prototype.selectListener = function(callback) {
	var __list_obj = this.getListObj();
	__list_obj.on('select', function (event) {
		var args = event.args;
		if (args) {
			var itemData = args.item;
			var index = args.index;
			if (itemData) callback(itemData, index);
		}
    });
};
OlbComboBox.prototype.getValue = function() {
	var obj = this.getListObj();
	if (OlbCore.isNotEmpty(obj)) {
		//return obj.val();
		if (!this.isMultiSelect()) {
			// return a value
			var itemSelected = obj.jqxComboBox("getSelectedItem");
			if (OlbCore.isNotEmpty(itemSelected)) return itemSelected.value;
		} else {
			// get values, return array value
			var returnValue = [];
			var itemsSelected = obj.jqxComboBox("getSelectedItems");
			if (OlbCore.isNotEmpty(itemsSelected)) {
				for (var i = 0; i < itemsSelected.length; i++) {
					var item = itemsSelected[i];
					if (item != null) returnValue.push(item.value);
				}
				return returnValue;
			}
		}
	}
	return null;
};
OlbComboBox.prototype.on = function(action, callback){
	if (typeof(callback) == "function") {
		this.getListObj().on(action, callback);
	}
};
OlbComboBox.prototype.clearAll = function(isClearSource){
	var __list_obj = this.getListObj();
	
	__list_obj.jqxComboBox('clearSelection');
	if (isClearSource) __list_obj.jqxComboBox('clear');
};
OlbComboBox.prototype.updateSource = function(newUrl, localData, callback) {
	var __list_obj = this.getListObj();
	
	OlbComboBoxUtil.updateSource(__list_obj, newUrl, localData, callback);
};

var OlbComboBoxUtil = (function(){
	var selectItem = function(comboBoxObj, selectArr, selectedIndex) {
		var isFinded = false;
		if (OlbCore.isNotEmpty(selectArr) && selectArr.length > 0) {
			if (OlbCore.isArray(selectArr)) {
				// process is array item selected
				$.each(selectArr, function(itemKey, itemValue){
					comboBoxObj.jqxComboBox('selectItem', itemValue);
				});
			}
		} else if (OlbCore.isNotEmpty(selectedIndex)){
			comboBoxObj.jqxComboBox({selectedIndex : selectedIndex});
		}
	};
	var updateSource = function(comboBoxObj, newUrl, localData, callback) {
		var tmpSource = comboBoxObj.jqxComboBox('source');
		if (tmpSource) {
			if (newUrl) {
				tmpSource._source.url = newUrl;
				comboBoxObj.jqxComboBox('clearSelection');
				comboBoxObj.jqxComboBox('source', tmpSource);
			} else if (localData) {
				// TODO
				tmpSource._source.localdata = localData;
				comboBoxObj.jqxComboBox('clearSelection');
				comboBoxObj.jqxComboBox('source', tmpSource);
			}
		}
		
		if (typeof(callback) == "function") {
			callback();
			/*this.bindingCompleteListener(function(){
				callback();
			}, true);*/
		}
	};
	return {
		selectItem: selectItem,
		updateSource: updateSource,
	};
}());