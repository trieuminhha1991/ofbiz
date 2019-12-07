/**
 * Require jqx.utils.js if has parameter useUtilFunc = true
 */
var OlbGrid = function(p_gridObj, p_localData, p_config, p_selectArr){
	var _grid_obj;
	var _config;
	var _localdata;
	var _source;
	var _datafields;
	var _columns;
	var _selectedIndex_def; // value default
	var _selectArr_def;
	var _contextmenu_obj;
	var _seft = this;

	function init(gridObj, localData, config, selectArr){
		_grid_obj = $(gridObj);
		_config = config;
		_localdata = localData;
		if (!OlbCore.isNotEmpty(gridObj)) return;

		_selectedIndex_def = config.selectedIndex;
		_selectArr_def = selectArr;
		if (typeof(config.contextMenu) == "Object") {
			_contextmenu_obj = $(config.contextMenu);
		} else if (OlbCore.isString(config.contextMenu)) {
			_contextmenu_obj = $("#" + config.contextMenu);
		}
		if (typeof(config.useUtilFunc) == "undefined") _config.useUtilFunc = false;
		if (typeof(_config.theme) == "undefined") _config.theme = OlbCore.theme;

		if (_config.useUtilFunc) {
			initUtil();
		} else {
			initNormal();
		}

		_seft.bindingCompleteListener(function(){
			_seft.clearSelection();
		});

	}

	function initUtil() {
		// process configData
		_config.source = typeof(_config.source) != 'undefined' ? _config.source : {};
		_config.source.dataUrl = typeof(_config.dataUrl) != 'undefined' ? _config.dataUrl : null;
		_config.source.pagesize = typeof(_config.pagesize) != "undefined" ? _config.pagesize : null;

		//$.getScript('/aceadmin/jqw/jqwidgets/jqx.utils.js', function(){
			Grid.initGrid(_config, _config.datafields, _config.columns, null, _grid_obj);
		//});

		// check and select by variables _def
		if ((OlbCore.isNotEmpty(_selectArr_def) && _selectArr_def.length > 0) || OlbCore.isNotEmpty(_selectedIndex_def)) {
			_seft.bindingCompleteListener(function(){
				selectItem();
			}, true);
		}

		// check and initialization context menu
		initContextMenu();

		if (_config.filterable) {
			_seft.bindingCompleteListener(function(){
				callbackFocusFilter();
			});
		}
	}

	function initNormal() {
		var sourceGrid;
		var dataAdapterGrid;

		_config.filter = !_config.vitualmode ? null : function () {
			_seft.updateBoundData(); // update the grid and send a request to the server.
		};
		_config.sort = !_config.vitualmode ? null : function () {
			_seft.updateBoundData();
		};

		_datafields = _config.datafields;
		_columns = _config.columns;

		var rendergridrows = null;
		if (_config.virtualmode) {
			rendergridrows = function(obj) {
				return obj.data;
			};
		}

		if (_config.useUrl && _config.url != null) {
			sourceGrid = {
				datafields: _datafields,
				cache: false,
				beforeprocessing: function (data) {
					sourceGrid.totalrecords = data.TotalRows;
				},
				pager: function (pagenum, pagesize, oldpagenum) {
					// callback called when a page or page size is changed.
				},
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command
				},
				root: 			typeof(_config.root) != "undefined" ? _config.root : "results",
				datatype: 		typeof(_config.datatype) != "undefined" ? _config.datatype : "json",
				filter: 		typeof(_config.filter) != "undefined" ? _config.filter : null,
				sort: 			typeof(_config.sort) != "undefined" ? _config.sort : null,
				sortcolumn: 	typeof(_config.sortcolumn) != "undefined" ? _config.sortcolumn : "",
			sortdirection: 	typeof(_config.sortdirection) != "undefined" ? _config.sortdirection : "asc",
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				contentType: 'application/x-www-form-urlencoded',
				url: _config.url,
			};
			dataAdapterGrid = new $.jqx.dataAdapter(sourceGrid, {
				autoBind: true,
				formatData: function (data) {
				if (data.filterscount) {
		                var filterListFields = "";
		                for (var i = 0; i < data.filterscount; i++) {
		                    var filterValue = data["filtervalue" + i];
		                    var filterCondition = data["filtercondition" + i];
		                    var filterDataField = data["filterdatafield" + i];
		                    var filterOperator = data["filteroperator" + i];
		                    filterListFields += "|OLBIUS|" + filterDataField;
		                    filterListFields += "|SUIBLO|" + filterValue;
		                    filterListFields += "|SUIBLO|" + filterCondition;
		                    filterListFields += "|SUIBLO|" + filterOperator;
		                }
		                data.filterListFields = filterListFields;
		            }
		            return data;
			},
		        loadError: function (xhr, status, error) {
		            alert(error);
		        },
		        downloadComplete: function (data, status, xhr) {
	                if (!sourceGrid.totalRecords) {
	                    sourceGrid.totalRecords = parseInt(data['odata.count']);
	                }
		        }
		    });
		} else {
			sourceGrid = {
				datafields: _config.datafields,
				datatype: _config.datatype,
				localdata: _localdata,
			};
			dataAdapterGrid = new $.jqx.dataAdapter(sourceGrid);
		}

		$.jqx.theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
		_grid_obj.jqxGrid({
			theme: 					$.jqx.theme,
			source: 				dataAdapterGrid,
			width: 					typeof(_config.width) != "undefined" ? _config.width : "100%",
			height: 				typeof(_config.height) != "undefined" ? _config.height : "auto",
			filterable: 			typeof(_config.filterable) != "undefined" ? _config.filterable : false,
			showfilterrow: 			typeof(_config.showfilterrow) != "undefined" ? _config.showfilterrow : false,
			sortable: 				typeof(_config.sortable) != "undefined" ? _config.sortable : false,
			editable: 				typeof(_config.editable) != "undefined" ? _config.editable : false,
			autoheight: 			typeof(_config.autoheight) != "undefined" ? _config.autoheight : true,
			columnsresize: 			typeof(_config.columnsresize) != "undefined" ? _config.columnsresize : null,
			pageable: 				typeof(_config.pageable) != "undefined" ? _config.pageable : false,
			pagesize: 				typeof(_config.pagesize) != "undefined" ? _config.pagesize : 15,
			showdefaultloadelement: typeof(_config.showdefaultloadelement) != "undefined" ? _config.showdefaultloadelement : null,
			autoshowloadelement: 	typeof(_config.autoshowloadelement) != "undefined" ? _config.autoshowloadelement : null,
			virtualmode: 			typeof(_config.virtualmode) != "undefined" ? _config.virtualmode : true,
			rendergridrows: 		typeof(_config.rendergridrows) != "undefined" ? _config.rendergridrows : rendergridrows,
			columns: 				typeof(_config.columns) != "undefined" ? _config.columns : [],
			columngroups: 			typeof(_config.columngroups) != "undefined" ? _config.columngroups : null,
			localization: 			typeof(_config.localization) != "undefined" ? _config.localization : null,
			rendertoolbar: 			typeof(_config.rendertoolbar) != "undefined" ? _config.rendertoolbar : null,
			showtoolbar: 			typeof(_config.showtoolbar) != "undefined" ? _config.showtoolbar : null,
			showaggregates: 		typeof(_config.showaggregates) != "undefined" ? _config.showaggregates : null,
			showstatusbar: 			typeof(_config.showstatusbar) != "undefined" ? _config.showstatusbar : null,
			statusbarheight: 		typeof(_config.statusbarheight) != "undefined" ? _config.statusbarheight : null,
			localization: 			typeof(_config.localization) != "undefined" ? _config.localization : getLocalization(),
			selectionmode: 			typeof(_config.selectionmode) != "undefined" ? _config.selectionmode : "singlerow",
			groupable: 				typeof(_config.groupable) != "undefined" ? _config.groupable : false,
			showgroupsheader: 		typeof(_config.showgroupsheader) != "undefined" ? _config.showgroupsheader : null,
		});

		// check and select by variables _def
		if ((OlbCore.isNotEmpty(_selectArr_def) && _selectArr_def.length > 0) || OlbCore.isNotEmpty(_selectedIndex_def)) {
			_seft.bindingCompleteListener(function(){
				selectItem();
			}, true);
		}

		// check and initialization context menu
		initContextMenu();

		if (_config.filterable) {
			_seft.bindingCompleteListener(function(){
				callbackFocusFilter();
			});
		}
	}

	function initContextMenu() {
		if (OlbCore.isNotEmpty(_contextmenu_obj)) {
			_grid_obj.on('contextmenu', function () {
	            return false;
	        });
			_grid_obj.on('rowclick', function (event) {
				var adapter = _grid_obj.jqxGrid('source');
	            if (adapter) {
			var source = adapter._source;
			var record = adapter.records;
		            if (!record || !record.length) {
						return;
					}
	            }
                if (event.args.rightclick) {
			_grid_obj.jqxGrid('selectrow', event.args.rowindex);
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    _contextmenu_obj.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
                }
            });
		}
	}

	this.getGridObj = function(){
		return _grid_obj;
	}
	this.putConfig = function(key, value){
		_config.put(key, value);
	}

	function callbackFocusFilter() {
		if (localStorage) {
			var previousId = localStorage.getItem('previousInputFilter');
			$("#" + previousId).find('input').focus();
		}
	}
	function selectItem(selectedIndex, selectArr) {
		var __selectedIndex = _selectedIndex_def; // __ = local
		var __selectArr = _selectArr_def;
		if (OlbCore.isNotEmpty(selectedIndex)) __selectedIndex = selectedIndex;
		if (OlbCore.isNotEmpty(selectArr)) __selectArr = selectArr;

		if (OlbCore.isNotEmpty(__selectArr) && __selectArr.length > 0) {
			if (OlbCore.isArray(__selectArr)) {
				// process is array item selected
				var key = _config.key;
				var rowsData = _grid_obj.jqxGrid('getrows');
				if (key != null && rowsData != null) {
					var finded = false;
					$.each(__selectArr, function(itemKey, itemValue){
						for (var i = 0; i < rowsData.length; i++) {
							var row = rowsData[i];
							if (row != window && itemValue == rowsData[i][key]) {
								_grid_obj.jqxGrid({selectedrowindex: i});
								return false;
							}
						}
					});
				}
			}
		} else if (OlbCore.isNotEmpty(__selectedIndex)){
			_grid_obj.jqxGrid({selectedrowindex: __selectedIndex});
		}
	}

	return init(p_gridObj, p_localData, p_config, p_selectArr);
};
OlbGrid.prototype.clearSelection = function() {
	this.getGridObj().jqxGrid('clearselection');
};
OlbGrid.prototype.updateBoundData = function() {
	this.getGridObj().jqxGrid('updatebounddata');
};
OlbGrid.prototype.rowSelectListener = function(callback) {
	var __grid_obj = this.getGridObj();
	__grid_obj.on('rowselect', function (event) {
		var args = event.args;
		if (args) {
			var rowData = __grid_obj.jqxGrid('getrowdata', args.rowindex);
			if (rowData) callback(rowData);
		}
    });
};
OlbGrid.prototype.bindingCompleteListener = function(callback, onlyOnce) {
	if (onlyOnce) {
		this.getGridObj().one('bindingcomplete', function (event) {
			callback();
	    });
	} else {
		this.getGridObj().on('bindingcomplete', function (event) {
			callback();
	    });
	}
};
OlbGrid.prototype.updateSource = function(newUrl, localData, callback) {
	var __grid_obj = this.getGridObj();

	var tmpSource = __grid_obj.jqxGrid('source');
	if (tmpSource) {
		if (newUrl) {
			tmpSource._source.url = newUrl;
			__grid_obj.jqxGrid('clearselection');
			__grid_obj.jqxGrid('source', tmpSource);
		} else if (localData) {
			// TODO
		}
	}

	if (typeof(callback) == "function") {
		this.bindingCompleteListener(function(){
			callback();
		}, true);
	}
};