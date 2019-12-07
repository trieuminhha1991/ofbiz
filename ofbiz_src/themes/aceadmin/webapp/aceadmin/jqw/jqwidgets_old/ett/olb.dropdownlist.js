
var OlbDropDownList = function(p_listObj, p_localData, p_config, p_selectArr){
	var _list_obj;
	var _config;
	var _selectedIndex_def; // value default
	var _selectArr_def;
	var _seft = this;
	var _datafields;
	var _columns;

	/**
     * Initialize DropDown List
     * @param listObj
     * @param config
     * @param selectArr
     * @return DROP_DOWN_LIST
     */
	function init(listObj, localData, config, selectArr){
		_list_obj = listObj;
		_config = config;
		_selectedIndex_def = config.selectedIndex;
		_selectArr_def = selectArr;
		_datafields = config.dataFields;
		_columns = config.columns;

		var __renderer = function (index, label, value) {
            var valueStr;
            if (_config.displayDetail) {
		valueStr = label + " [" + value + "]";
            } else {
		valueStr = label;
            }
            return valueStr;
        };

		var dataSource;
		if (_config.useUrl && _config.url != null) {
			dataSource = {
				datatype: 	typeof(_config.datatype) != "undefined" ? _config.datatype : "json",
				root:		typeof(_config.root) != "undefined" ? _config.root : "results",
				async: 		typeof(_config.async) != "undefined" ? _config.async : false,
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
		        datatype: typeof(_config.datatype) != "undefined" ? _config.datatype : "json"
		    };
		}

		$.jqx.theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
	    var dataAdapter = new $.jqx.dataAdapter(dataSource);
	    _list_obj.jqxDropDownList({
		theme: $.jqx.theme,
		source: dataAdapter,
		width: 				typeof(_config.width) != "undefined" ? _config.width : 218,
		displayMember: 		typeof(_config.value) != "undefined" ? _config.value : null,
		valueMember: 		typeof(_config.key) != "undefined" ? _config.key : null,
		disabled: 			typeof(_config.disabled) != "undefined" ? _config.disabled : null,
		dropDownWidth: 		typeof(_config.dropDownWidth) != "undefined" ? _config.dropDownWidth : "auto",
		dropDownHeight: 	typeof(_config.dropDownHeight) != "undefined" ? _config.dropDownHeight : null,
		autoDropDownHeight: typeof(_config.autoDropDownHeight) != "undefined" ? _config.autoDropDownHeight : null,
		placeHolder: 		typeof(_config.placeHolder) != "undefined" ? _config.placeHolder : "Click to choose",
			renderer: 			typeof(_config.renderer) != "undefined" ? _config.renderer : __renderer,
		filterable: 		typeof(_config.filterable) != "undefined" ? _config.filterable : null,
			dropDownHorizontalAlignment: typeof(_config.dropDownHorizontalAlignment) != "undefined" ? _config.dropDownHorizontalAlignment : null,
	    });

	    // select default item
		_seft.selectItem();
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
				$.each(__selectArr, function(itemKey, itemValue){
					_list_obj.jqxDropDownList('selectItem', itemValue);
				});
			}
		} else if (OlbCore.isNotEmpty(__selectedIndex)){
			_list_obj.jqxDropDownList({selectedIndex : __selectedIndex});
		}
	}

	this.getListObj = function(){
		return _list_obj;
	}

	return init(p_listObj, p_localData, p_config, p_selectArr);
}
OlbDropDownList.prototype.bindingCompleteListener = function(callback, onlyOnce) {
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
OlbDropDownList.prototype.selectListener = function(callback) {
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
OlbDropDownList.prototype.getValue = function() {
	var obj = this.getListObj();
	if (OlbCore.isNotEmpty(obj)) {
		return obj.val();
	}
};
OlbDropDownList.prototype.clearAll = function(isClearSource){
	var __list_obj = this.getListObj();

	__list_obj.jqxDropDownList('clearSelection');
	if (isClearSource) __list_obj.jqxDropDownList('clear');
};
OlbDropDownList.prototype.updateSource = function(newUrl, localData, callback) {
	var __list_obj = this.getListObj();

	var tmpSource = __list_obj.jqxDropDownList('source');
	if (tmpSource) {
		if (newUrl) {
			tmpSource._source.url = newUrl;
			__list_obj.jqxDropDownList('clearSelection');
			__list_obj.jqxDropDownList('source', tmpSource);
		} else if (localData) {
			// TODO
			tmpSource._source.localdata = localData;
			__list_obj.jqxDropDownList('clearSelection');
			__list_obj.jqxDropDownList('source', tmpSource);
		}
	}

	if (typeof(callback) == "function") {
		callback();
		/*this.bindingCompleteListener(function(){
			callback();
		}, true);*/
	}
};