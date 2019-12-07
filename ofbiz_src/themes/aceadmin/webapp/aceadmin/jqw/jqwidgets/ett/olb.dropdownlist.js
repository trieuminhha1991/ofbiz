
var OlbDropDownList = function(p_listObj, p_localData, p_config, p_selectArr){
	var _list_obj;
	var _config;
	var _selectedIndex_def; // value default
	var _selectArr_def;
	var _seft = this;
	var _datafields;
	var _columns;
	var _type = "dropDownList";
	
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
		
		//$.jqx.theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
		var theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
	    var dataAdapter = new $.jqx.dataAdapter(dataSource);
	    _list_obj.jqxDropDownList({
	    	theme: theme,
	    	source: dataAdapter, 
	    	width: 				typeof(_config.width) != "undefined" ? _config.width : 218, 
	    	displayMember: 		typeof(_config.value) != "undefined" ? _config.value : null, 
	    	valueMember: 		typeof(_config.key) != "undefined" ? _config.key : null, 
	    	disabled: 			typeof(_config.disabled) != "undefined" ? _config.disabled : null,
	    	dropDownWidth: 		typeof(_config.dropDownWidth) != "undefined" ? _config.dropDownWidth : "auto", 
	    	dropDownHeight: 	typeof(_config.dropDownHeight) != "undefined" ? _config.dropDownHeight : null, 
	    	autoDropDownHeight: typeof(_config.autoDropDownHeight) != "undefined" ? _config.autoDropDownHeight : null, 
	    	placeHolder: 		typeof(_config.placeHolder) != "undefined" ? "--" + _config.placeHolder : "--Click to choose",
			renderer: 			typeof(_config.renderer) != "undefined" ? _config.renderer : __renderer,
	    	filterable: 		typeof(_config.filterable) != "undefined" ? _config.filterable : null,
			dropDownHorizontalAlignment: typeof(_config.dropDownHorizontalAlignment) != "undefined" ? _config.dropDownHorizontalAlignment : null,
			checkboxes:			typeof(_config.checkboxes) != "undefined" ? _config.checkboxes : false,
	    });
	    
	    if (_config.addNullItem) {
	    	$(_list_obj).jqxDropDownList('insertAt', {label: '---', value: ''}, 0);
	    }
	    
	    // select default item
		_seft.selectItem();
		
		var sourceAfter = $(_list_obj).jqxDropDownList("source");
		if (sourceAfter) {
			var numberRows = sourceAfter.totalrecords;
	    	if (numberRows && numberRows > 10) {
	    		$(_list_obj).jqxDropDownList({"autoDropDownHeight": false, "dropDownHeight": 200});
	    	}
		}
		
		// show button clear data
		if (_config.showClearButton) {
			createClearButton();
		}
		
		// show button add data
		if (_config.showAddButton) {
			createAddButton();
		}
	}
	
	var createClearButton = function(){
		var parent = _list_obj.parent();
		var idClearBtn = _list_obj.attr("id") + "_clearBtn";
		var clearBtnStr = '<a id="' + idClearBtn + '" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="' + uiLabelMap.BSClear + '"><i class="fa fa-eraser"></i></a>';
		var clearBtn = $(clearBtnStr);
		parent.css("position", "relative");
		_list_obj.after(clearBtn);
		//_list_obj.css("display", "inline-block");
		resizeClearButton(clearBtn);
		
		clearBtn.click(function(){_list_obj.jqxDropDownList('clearSelection')});
		$(window).resize(function(){
			resizeClearButton(clearBtn);
		});
	}
	
	var createAddButton = function(){
		var parent = _list_obj.parent();
		var idAddBtn = _list_obj.attr("id") + "_addBtn";
		var addBtnStr = '<a id="' + idAddBtn + '" tabindex="-1" href="javascript:void(0);" class="btn-add-quick" title="' + uiLabelMap.BSAddNew + '"><i class="icon-plus open-sans"></i></a>';
		var addBtn = $(addBtnStr);
		parent.css("position", "relative");
		_list_obj.after(addBtn);
		//_list_obj.css("display", "inline-block");
		resizeExtendButton(addBtn);
		
		//clearBtn.click(function(){_list_obj.jqxDropDownList('clearSelection')});
		$(window).resize(function(){
			resizeExtendButton(addBtn);
		});
	}
	
	var resizeClearButton = function(clearBtn){
		var alignLeft = _list_obj.width() + 5;
		clearBtn.css("left", alignLeft);
	}
	
	var resizeExtendButton = function(newBtn){
		var alignLeft = _list_obj.width() + 5;
		newBtn.css("left", alignLeft);
	}
	
	this.selectItem = function(selectArr, selectedIndex) {
		var __selectedIndex = _selectedIndex_def; // __ = local
		var __selectArr = _selectArr_def;
		if (OlbCore.isNotEmpty(selectedIndex)) __selectedIndex = selectedIndex;
		if (OlbCore.isNotEmpty(selectArr)) __selectArr = selectArr;
		
		var isFinded = false;
		var actionName = 'selectItem';
		if (_list_obj.jqxDropDownList('checkboxes')) {
			actionName = 'checkItem';
		}
		if (OlbCore.isNotEmpty(__selectArr) && __selectArr.length > 0) {
			if (OlbCore.isArray(__selectArr)) {
				// process is array item selected
				$.each(__selectArr, function(itemKey, itemValue){
					_list_obj.jqxDropDownList(actionName, itemValue);
				});
			}
		} else if (OlbCore.isNotEmpty(__selectedIndex)){
			_list_obj.jqxDropDownList({selectedIndex : __selectedIndex});
		}
	}
	
	this.getListObj = function(){
		return _list_obj;
	}
	
	this.getType = function(){
		return _type;
	}
	
	this.updateBoundData = function(){
		if (_config.useUrl && _config.url != null) {
			var newUrl = _config.url;
			_seft.updateSource(newUrl);
		} else {
			// TODO update localData
		}
	};
	
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
		if ($(obj).jqxDropDownList('checkboxes')) {
			var items = $(obj).jqxDropDownList('getCheckedItems');
			if (items) {
				var resultValue = new Array();
				$.each(items, function (index) {
					if (this.value) resultValue.push(this.value);
                });
				return resultValue;
			}
		} else {
			var item = obj.jqxDropDownList("getSelectedItem");
			if (item) return item.value;
			else return ;
			//return obj.val();
		}
	}
};
OlbDropDownList.prototype.getLabel = function() {
	var obj = this.getListObj();
	var label = obj.val();
	if (OlbCore.isNotEmpty(obj)) {
		var item = obj.jqxDropDownList("getSelectedItem");
		if (item) label = item.label;
	}
	return label;
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
OlbDropDownList.prototype.on = function(action, callback){
	if (typeof(callback) == "function") {
		this.getListObj().on(action, callback);
	}
};