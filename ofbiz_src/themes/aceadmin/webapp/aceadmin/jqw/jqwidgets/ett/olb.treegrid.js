/**
 * Require OlbCore
 */
var OlbTreeGrid = function(p_treeGridObj, p_localData, p_config, p_selectArr){
	var _tree_grid_obj;
	var _config;
	var _localdata;
	var _source;
	var _datafields;
	var _columns;
	var _selectedIndex_def; // value default
	var _selectArr_def;
	var _contextmenu_obj;
	var _seft = this;
	var _configcontextmenu;
	var _type = "treeGrid";

	function init(treeGridObj, localData, config, selectArr){
		_tree_grid_obj = $(treeGridObj);
		_config = config != undefined ? config : {};
		_localdata = localData;
		_configcontextmenu = _config.configcontextmenu != undefined ? config.configcontextmenu : {};
		if (!OlbCore.isNotEmpty(treeGridObj)) return;
		
		_selectedIndex_def = config.selectedIndex;
		_selectArr_def = selectArr;
		if (typeof(config.contextMenu) == "Object") {
			_contextmenu_obj = $(config.contextMenu);
		} else if (OlbCore.isString(config.contextMenu)) {
			_contextmenu_obj = $("#" + config.contextMenu);
		}
		//if (typeof(config.useUtilFunc) == "undefined") _config.useUtilFunc = false;
		if (typeof(_config.theme) == "undefined") _config.theme = OlbCore.theme;
		if (typeof(_config.root) == "undefined") _config.root = "results";

		/*if (_config.useUtilFunc) {
			initUtil();
		} else {*/
		initNormal();
		//}

		/*_seft.bindingCompleteListener(function(){
			_seft.clearSelection();
		}, true);*/
	}
	
	function initNormal() {
		var sourceGrid;
		var dataAdapterGrid;
		
		_datafields = _config.datafields;
		_columns = _config.columns;
		
		var dataMap = typeof(_config.dataMap) != "undefined" ? _config.dataMap : {};
		dataMap.noConditionFind = 'Y';
		dataMap.conditionsFind = 'N';
		
		var keyId = typeof(_config.key) != "undefined" ? _config.key : "id";
		var parentKeyId = typeof(_config.parentKeyId) != "undefined" ? _config.parentKeyId : null;
		
		if (_config.useUrl && (_config.url != null)) {
			sourceGrid = {
				type: 'POST',
				datafields: _datafields, 
				root: 			typeof(_config.root) != "undefined" ? _config.root : "results",
				datatype:		typeof(_config.datatype) != "undefined" ? _config.datatype : "json",
                data: dataMap,
                id: keyId,
                hierarchy: {
                    keyDataField: {name: keyId},
                    parentDataField: {name: parentKeyId}
                },
                async: false,
                url: _config.url,
            };
			if (typeof(_config.addRow) == "function") sourceGrid.addRow = _config.addRow;
			if (typeof(_config.deleteRow) == "function") sourceGrid.deleteRow = _config.deleteRow;
			if (typeof(_config.updateRow) == "function") sourceGrid.updateRow = _config.updateRow;
		} else {
			sourceGrid = {
	        	datafields: _datafields, 
		        datatype: _config.datatype,
		        localdata: _localdata,
		    };
		}
		
		dataAdapterGrid = new $.jqx.dataAdapter(sourceGrid);
		
		var __rendertoolbar = null;
		if (typeof(_config.rendertoolbar) != "undefined") {
			__rendertoolbar = _config.rendertoolbar;
		} else if (typeof(_config.rendertoolbarconfig) != "undefined") {
			var rendertoolbarconfig = _config.rendertoolbarconfig;
			var id = _tree_grid_obj.attr("id");
			__rendertoolbar = function (toolbar) {
				toolbar.html("");
				
				// title properties
				var jqxheaderStr = "<div id='toolbarcontainer" + id + "' class='widget-header'><h4>";
				if (typeof(rendertoolbarconfig.titleProperty) != "undefined") {
					jqxheaderStr += rendertoolbarconfig.titleProperty;
				}
				jqxheaderStr += "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>";
				
				var jqxheader = $(jqxheaderStr);
				toolbar.append(jqxheader);
				
				var container = $("#toolbarButtonContainer" + id);
                //var maincontainer = $("#toolbarcontainer" + id);
				
				// button customcontrol1
				if (OlbCore.isNotEmpty(rendertoolbarconfig.customcontrol1)) {
					Grid.createCustomControlButton(_tree_grid_obj, container, rendertoolbarconfig.customcontrol1);
				}
				// button customcontrol2
				if (OlbCore.isNotEmpty(rendertoolbarconfig.customcontrol2)) {
					Grid.createCustomControlButton(_tree_grid_obj, container, rendertoolbarconfig.customcontrol2);
				}
				// button customcontrol3
				if (OlbCore.isNotEmpty(rendertoolbarconfig.customcontrol3)) {
					Grid.createCustomControlButton(_tree_grid_obj, container, rendertoolbarconfig.customcontrol3);
				}
				// button customcontrol4
				if (OlbCore.isNotEmpty(rendertoolbarconfig.customcontrol4)) {
					Grid.createCustomControlButton(_tree_grid_obj, container, rendertoolbarconfig.customcontrol4);
				}
				
				// button expend, collapse
				if (rendertoolbarconfig.expendButton) {
					initButtonExpendAll(_tree_grid_obj, container);
				}
			}
		}
	    
	    //$.jqx.theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
		var theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
	    _tree_grid_obj.jqxTreeGrid({
	    	theme:					theme,
	    	source: 				dataAdapterGrid,
          	width: 					typeof (_config.width) != "undefined" ? _config.width : "100%",
          	height:					typeof (_config.height) != "undefined" ? _config.height : "auto",
          	editable:				typeof (_config.editable) != "undefined" ? _config.editable : false,
  			autoRowHeight:			typeof (_config.autoRowHeight) != "undefined" ? _config.autoRowHeight : false,
			columnsResize:			typeof (_config.columnsresize) != "undefined" ? _config.columnsresize : true,
			pageable:				typeof (_config.pageable) != "undefined" ? _config.pageable : false,
			pageSize: 				typeof (_config.pagesize) != "undefined" ? _config.pagesize : 15,
			pagerMode:				typeof (_config.pagerMode) != "undefined" ? _config.pagerMode : "advanced",
			pageSizeOptions:		typeof (_config.pageSizeOptions) != "undefined" ? _config.pageSizeOptions : [5, 10, 15, 20, 25, 50, 100],
			columns:				typeof (_config.columns) != "undefined" ? _config.columns : [],
          	columnGroups:			typeof (_config.columnGroups) != "undefined" ? _config.columnGroups : null,
			showToolbar:			typeof (_config.showtoolbar) != "undefined" ? _config.showtoolbar : null,
			rendertoolbar:			__rendertoolbar,
			localization:			typeof (_config.localization) != "undefined" ? _config.localization : getLocalization(),
			selectionMode:			typeof (_config.selectionMode) != "undefined" ? _config.selectionMode : "singleRow",
			altRows: 				typeof (_config.altRows) != "undefined" ? _config.altRows : false,
			showStatusbar: 			typeof (_config.showStatusbar) != "undefined" ? _config.showStatusbar : false,
			showHeader: 			typeof (_config.showHeader) != "undefined" ? _config.showHeader : true,
          	editSettings: { 
          		saveOnPageChange: true, 
          		saveOnBlur: false, 
          		saveOnSelectionChange: true, 
          		cancelOnEsc: true, 
          		saveOnEnter: true, 
          		editSingleCell: true, 
          		editOnDoubleClick: true, 
          		editOnF2: true 
          	},
          	ready: function () {
          		if (_config.expandAll) {
          			_tree_grid_obj.jqxTreeGrid('expandAll');
          		} else {
          			_tree_grid_obj.jqxTreeGrid('expandRow', 1);
          		}
            },
	    });
	    
	    // check and initialization context menu
		initContextMenu();
		
	}
	
	function initButtonExpendAll(treeGridObj, container) {
		if ($(container).length < 1) return;
		
		var id = $(treeGridObj).attr("id");
		var buttonContainer = $('<div class="custom-control-toolbar"></div>');
		var buttonExpend = $('<a id="btnExpend' + id + '" style="color:#438eb9;" href="javascript:void(0);"><i class="fa fa-expand"></i></a>');
		var buttonCollapse = $('<a id="btnCollapse' + id + '" style="color:#438eb9;" href="javascript:void(0);"><i class="fa fa-compress"></i></a>');
		buttonContainer.append(buttonCollapse);
		buttonContainer.append(buttonExpend);
		$(container).append(buttonContainer);
		
		$("#btnExpend" + id).click(function(){
			$(treeGridObj).jqxTreeGrid('expandAll', true);
		});
		$("#btnCollapse" + id).click(function(){
			$(treeGridObj).jqxTreeGrid('collapseAll', true);
		});
	}
	
	function initContextMenu() {
		if (OlbCore.isNotEmpty(_contextmenu_obj)) {
			_tree_grid_obj.on('contextmenu', function () {
	            return false;
	        });
			_tree_grid_obj.on('rowClick', function (event) {
				var args = event.args;
	            if (args.originalEvent.button == 2) {
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    _contextmenu_obj.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
                }
            });
		}
		OlbTreeGridUtil.addContextMenuHoverStyle(_tree_grid_obj, _contextmenu_obj);
	}
	
	this.getType = function(){
		return _type;
	}
	this.getGridObj = function(){
		return _tree_grid_obj;
	}
	this.putConfig = function(key, value){
		_config.put(key, value);
	}
	
	return init(p_treeGridObj, p_localData, p_config, p_selectArr);
};
OlbTreeGrid.prototype.rowSelectListener = function(callback) {
	var __grid_obj = this.getGridObj();
	__grid_obj.on('rowSelect', function (event) {
		var args = event.args;
		if (args) {
			var rowData = args.row;
			if (rowData) callback(rowData);
		}
    });
};
OlbTreeGrid.prototype.rowUnSelectListener = function(callback) {
	var __grid_obj = this.getGridObj();
	__grid_obj.on('rowUnselect', function (event) {
		var args = event.args;
		if (args) {
			var rowData = args.row;
			if (rowData) callback(rowData);
		}
    });
};
OlbTreeGrid.prototype.bindingCompleteListener = function(callback, onlyOnce) {
	if (onlyOnce) {
		this.getGridObj().one('bindingComplete', function (event) {
			callback();
	    });
	} else {
		this.getGridObj().on('bindingComplete', function (event) {
			callback();
	    });
	}
};
OlbTreeGrid.prototype.updateSource = function(newUrl, localData, callback) {
	var __grid_obj = this.getGridObj();
	var tmpSource = __grid_obj.jqxTreeGrid('source');
	if (tmpSource) {
		if (newUrl) {
			tmpSource._source.url = newUrl;
			__grid_obj.jqxTreeGrid('clearSelection');
			__grid_obj.jqxTreeGrid('source', tmpSource);
			__grid_obj.jqxTreeGrid('updateBoundData');  
			__grid_obj.jqxTreeGrid('refresh');
		} else if (localData) {
			// TODO
			tmpSource._source.localdata = localData;
			__grid_obj.jqxTreeGrid('clearselection');
			__grid_obj.jqxTreeGrid('source', tmpSource);
		}
	}
	
	if (typeof(callback) == "function") {
		this.bindingCompleteListener(function(){
			callback();
		}, true);
	}
};

var OlbTreeGridUtil = (function(){
	var addContextMenuHoverStyle = function(tree_grid_obj, contextmenu_obj){
		var dm = $(contextmenu_obj);
		if (contextmenu_obj && dm.length) {
			tree_grid_obj.addClass('jqx-grid-context-menu');
		}
	};
	return {
		addContextMenuHoverStyle: addContextMenuHoverStyle,
	}
}());