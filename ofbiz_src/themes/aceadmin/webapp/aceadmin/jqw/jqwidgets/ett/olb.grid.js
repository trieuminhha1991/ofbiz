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
	var _configcontextmenu;
	var _type = "grid";
	var _dynamicParams = {};
	var _isInit = true;
	var _totalRows = 0;
	var _selectedValue;
	
	function init(gridObj, localData, config, selectArr){
		_selectedValue = {};
		_grid_obj = $(gridObj);
		_config = config != undefined ? config : {};
		_localdata = localData;
		_configcontextmenu = _config.configcontextmenu != undefined ? config.configcontextmenu : {};
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
		if (typeof(_config.root) == "undefined") _config.root = "results";

		if (_config.useUtilFunc) {
			initUtil();
		} else {
			initNormal();
		}

		_seft.bindingCompleteListener(function(){
			_seft.clearSelection();
		}, false);
		
		// call listener event on row select item
		var keyUid = _config.key;
		if (keyUid) {
			_seft.rowSelectListener(function(rowData){
				var keyMap = rowData[keyUid];
				if (keyMap) {
					_selectedValue[keyMap] = rowData;
				}
			});
			_seft.rowUnSelectListener(function(rowData){
				var keyMap = rowData[keyUid];
				if (keyMap && _selectedValue[keyMap]) {
					delete _selectedValue[keyMap];
				}
			});
		}
	}

	function initUtil() {
		// process configData
		_config.source = typeof(_config.source) != 'undefined' ? _config.source : {};
		_config.source.dataUrl = typeof(_config.dataUrl) != 'undefined' ? _config.dataUrl : null;
		_config.source.pagesize = typeof(_config.pagesize) != "undefined" ? _config.pagesize : null;
		
		_config.virtualmode = typeof(_config.virtualmode) != "undefined" ? _config.virtualmode : true, 
		_config.source.createUrl = typeof(_config.createUrl) != 'undefined' ? _config.createUrl : null;
		_config.source.addColumns = typeof(_config.addColumns) != 'undefined' ? _config.addColumns : null;
		_config.source.removeUrl = typeof(_config.removeUrl) != 'undefined' ? _config.removeUrl : null;
		_config.source.deleteColumns = typeof(_config.deleteColumns) != 'undefined' ? _config.deleteColumns : null;
		
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
		var filterDefault = null;
		var sortDefault = null;
		if (_config.virtualmode || typeof(_config.virtualmode) == "undefined") {
			rendergridrows = function(obj) {	
				return obj.data;
			}; 
			filterDefault = function() {
				if(_config.filterable){
					_grid_obj.jqxGrid('updatebounddata');
				}
			};
			sortDefault = function() {
				if(_config.sortable){
					_grid_obj.jqxGrid('updatebounddata');
				}
			};
		}
		
		var dataMap = typeof(_config.dataMap) != "undefined" ? _config.dataMap : {};
		dataMap.noConditionFind = 'Y';
		dataMap.conditionsFind = 'N';
		dataMap.isInit = _isInit;
		
		if (_config.useUrl && _config.url != null) {
			sourceGrid = {
				datafields: _datafields,
				cache: false,
				beforeprocessing: function (data) {
					//sourceGrid.totalrecords = data.TotalRows;
					// new process
					var mTotalRows = data.TotalRows;
                    if (_isInit && (typeof mTotalRows != 'undefined') && (mTotalRows != null)) {
                    	sourceGrid.totalrecords = mTotalRows;
                    	_totalRows = mTotalRows;
                    } else {
                    	sourceGrid.totalrecords = _totalRows;
                    }
				},
				pager: function (pagenum, pagesize, oldpagenum) {
					// callback called when a page or page size is changed.
					_isInit = false;
				},
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				root: 			typeof(_config.root) != "undefined" ? _config.root : "results",
				datatype: 		typeof(_config.datatype) != "undefined" ? _config.datatype : "json",
				filter: 		OlbCore.isNotEmpty(_config.filter) ? _config.filter : filterDefault,
				sort: 			OlbCore.isNotEmpty(_config.sort) ? _config.sort : sortDefault,
				sortcolumn: 	typeof(_config.sortcolumn) != "undefined" ? _config.sortcolumn : "",
	           	sortdirection: 	typeof(_config.sortdirection) != "undefined" ? _config.sortdirection : "asc",
				type: 'POST',
				data: dataMap,
				contentType: 'application/x-www-form-urlencoded',
				url: _config.url,
			};
			if (typeof(_config.addrow) == "function") sourceGrid.addrow = _config.addrow;
			if (typeof(_config.deleterow) == "function") sourceGrid.deleterow = _config.deleterow;
			if (typeof(_config.updaterow) == "function") sourceGrid.updaterow = _config.updaterow;
			
			/*dataAdapterGrid = new $.jqx.dataAdapter(sourceGrid, {
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
		    });*/
			dataAdapterGrid = initDataAdapter(_grid_obj, sourceGrid, _config);
		} else {
			sourceGrid = {
				datafields: _config.datafields,
				datatype: _config.datatype,
				localdata: _localdata,
			};
			dataAdapterGrid = new $.jqx.dataAdapter(sourceGrid);
		}
		
		var __rendertoolbar = null;
		if (typeof(_config.rendertoolbar) != "undefined") {
			__rendertoolbar = _config.rendertoolbar;
		} else if (typeof(_config.rendertoolbarconfig) != "undefined") {
			var rendertoolbarconfig = _config.rendertoolbarconfig;
			var id = _grid_obj.attr("id");
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
					OlbGridUtil.createCustomControlButton(_grid_obj, container, rendertoolbarconfig.customcontrol1);
				}
				// button customcontrol2
				if (OlbCore.isNotEmpty(rendertoolbarconfig.customcontrol2)) {
					OlbGridUtil.createCustomControlButton(_grid_obj, container, rendertoolbarconfig.customcontrol2);
				}
				// button customcontrol3
				if (OlbCore.isNotEmpty(rendertoolbarconfig.customcontrol3)) {
					OlbGridUtil.createCustomControlButton(_grid_obj, container, rendertoolbarconfig.customcontrol3);
				}
				// button customcontrol4
				if (OlbCore.isNotEmpty(rendertoolbarconfig.customcontrol4)) {
					OlbGridUtil.createCustomControlButton(_grid_obj, container, rendertoolbarconfig.customcontrol4);
				}
			}
		}
		
		//$.jqx.theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
		var theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
		var configGrid = 
		_grid_obj.jqxGrid({
			theme: 					theme,
			source: 				dataAdapterGrid,
			width: 					typeof(_config.width) != "undefined" ? _config.width : "100%", 
			height: 				typeof(_config.height) != "undefined" ? _config.height : "auto",
			filterable: 			typeof(_config.filterable) != "undefined" ? _config.filterable : false,
			showfilterrow: 			typeof(_config.showfilterrow) != "undefined" ? _config.showfilterrow : false,
			sortable: 				typeof(_config.sortable) != "undefined" ? _config.sortable : false,
			editable: 				typeof(_config.editable) != "undefined" ? _config.editable : false,
			disabled: 				typeof(_config.disabled) != "undefined" ? _config.disabled : null,
			autoheight: 			typeof(_config.autoheight) != "undefined" ? _config.autoheight : true,
			columnsresize: 			typeof(_config.columnsresize) != "undefined" ? _config.columnsresize : true,
			columnsreorder: 		typeof(_config.columnsreorder) != "undefined" ? _config.columnsreorder : true,
			columnsheight: 			typeof(_config.columnsheight) != "undefined" ? _config.columnsheight : 30,
			pageable: 				typeof(_config.pageable) != "undefined" ? _config.pageable : false,
			pagesize: 				typeof(_config.pagesize) != "undefined" ? _config.pagesize : 15,
			pagesizeoptions: 		typeof(_config.pagesizeoptions) != "undefined" ? _config.pagesizeoptions : [5, 10, 15, 20, 25, 50, 100],
			showdefaultloadelement: typeof(_config.showdefaultloadelement) != "undefined" ? _config.showdefaultloadelement : true,
			autoshowloadelement: 	typeof(_config.autoshowloadelement) != "undefined" ? _config.autoshowloadelement : true,
			virtualmode: 			typeof(_config.virtualmode) != "undefined" ? _config.virtualmode : true, 
			rendergridrows: 		typeof(_config.rendergridrows) != "undefined" ? _config.rendergridrows : rendergridrows,
			columns: 				typeof(_config.columns) != "undefined" ? _config.columns : [],
			columngroups: 			typeof(_config.columngroups) != "undefined" ? _config.columngroups : null,
			rendertoolbar: 			__rendertoolbar,
			showtoolbar: 			typeof(_config.showtoolbar) != "undefined" ? _config.showtoolbar : null,
			toolbarheight: 			typeof(_config.toolbarheight) != "undefined" ? _config.toolbarheight : 34,
			showaggregates: 		typeof(_config.showaggregates) != "undefined" ? _config.showaggregates : null,
			showstatusbar: 			typeof(_config.showstatusbar) != "undefined" ? _config.showstatusbar : null,
			statusbarheight: 		typeof(_config.statusbarheight) != "undefined" ? _config.statusbarheight : null,
			localization: 			typeof(_config.localization) != "undefined" ? _config.localization : getLocalization(),
			selectionmode: 			typeof(_config.selectionmode) != "undefined" ? _config.selectionmode : "singlerow",
			groupable: 				typeof(_config.groupable) != "undefined" ? _config.groupable : false,
			showgroupsheader: 		typeof(_config.showgroupsheader) != "undefined" ? _config.showgroupsheader : null,
			enabletooltips: 		typeof(_config.enabletooltips) != "undefined" ? _config.enabletooltips : false,
			editmode: 				typeof(_config.editmode) != "undefined" ? _config.editmode : 'selectedcell',
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
		if (_config.bindresize) {
			_seft.bindingCompleteListener(function(){
				$(window).resize();
			});
			setTimeout(function(){
				$(window).resize();
			}, 500);
			/*setTimeout(function(){
				bindResize();
			}, 500);*/
		}
	}
	
	function bindResize() {
		/*var grid = _grid_obj;
		var par = grid.parent();
		var resizeAction = function(){
			var tmpWidth = par.width();
			var id = grid.attr('id');
			var toolbar = grid.find('.widget-header');
			var header = toolbar.find('h4');
			var tool = toolbar.find('.pull-right');
            grid.jqxGrid({ width: tmpWidth });
            var total = header.width() + tool.width() + 50;
            if(total > tmpWidth){
        		toolbar.find('span').hide();
            }
		};
		resizeAction();*/
        /*par.bind('resize', function() {
			resizeAction();
        });.trigger('resize');
        $("#sidebar-collapse").on('click', function(){
        	resizeAction();  
        });*/
	};
	
	/*
	 * sourceG.grid
	 * sourceG.datafields
	 * 
	 */
	var initDataAdapter = function(grid, sourceGrid, config) {
		var datafields = sourceGrid.datafields;
		var formatData = config && config.formatData ? config.formatData : null;
		var defaultFormat = function(data) {
			data.isInit = _isInit;
			if((typeof outFilterCondition === 'undefined' || outFilterCondition =="") && (typeof alterData === 'undefined' || alterData =="" || $.isEmptyObject(alterData))){
				var filterListFields = "";
                if (data.filterscount) {
                    var tmpFieldName = "";
                    for (var i = 0; i < data.filterscount; i++) {
                        var filterValue = data["filtervalue" + i];
                		if(filterValue == ""){
                    		continue;
                    	}
                        var filterCondition = data["filtercondition" + i];
                        var filterDataField = data["filterdatafield" + i];
                        var filterOperator = data["filteroperator" + i];
                        if(getFieldType(datafields,filterDataField)=='number'){
                            filterListFields += "|OLBIUS|" + filterDataField + "(BigDecimal)";
                        }else if(getFieldType(datafields,filterDataField)=='date'){
                            filterListFields += "|OLBIUS|" + filterDataField + "(Date)";
                        }else if(getFieldType(datafields,filterDataField)=='Timestamp'){
                        	if(getFieldPattern(datafields,filterDataField) != ''){
                        		filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[" + getFieldPattern(datafields,filterDataField) + "]";
                        	}else{
                        		filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[dd/MM/yyyy HH:mm:ss]";
                        	}
                        }else if(getFieldType(datafields,filterDataField)=='Long'){//add type long
                        	filterListFields += "|OLBIUS|" + filterDataField + "(Long)";
                        }else if(getFieldType(datafields,filterDataField)=='Double'){//add type Double
                        	filterListFields += "|OLBIUS|" + filterDataField + "(Double)";
                        }else if(getFieldType(datafields,filterDataField) == 'olap'){
                        	if(typeof config.replaceFields !== undefined){
                        		for(field in config.replaceFields){
                        			if(filterDataField == field){
                        				filterListFields += "|OLBIUS|" + config.replaceFields[field + ''];
                                	}else continue;
                        		}
                        	}else  filterListFields += "|OLBIUS|" + filterDataField;
                        }
                        else{
                            filterListFields += "|OLBIUS|" + filterDataField;
                        }
                        //if has hh:mm:ss fix
                        if(getFieldType(datafields,filterDataField)=='Timestamp' && getFieldPattern(datafields,filterDataField) === ''){
                            if(tmpFieldName != filterDataField){
								if(filterValue.length > 11){
									filterListFields += "|SUIBLO|" + filterValue;
								}else{
									filterListFields += "|SUIBLO|" + filterValue + " 00:00:00";
								}
                            }else{
								if(filterValue.length > 11){
									filterListFields += "|SUIBLO|" + filterValue;
								}else{
									filterListFields += "|SUIBLO|" + filterValue + " 23:59:59";
								}
                            }
                        }else{
                            filterListFields += "|SUIBLO|" + filterValue;
                        }
                        filterListFields += "|SUIBLO|" + filterCondition;
                        filterListFields += "|SUIBLO|" + filterOperator;
                        tmpFieldName = filterDataField;
                    }
                }
                filterListFields += getAllCondition(grid);
                data.filterListFields = filterListFields;
            }else if(!(typeof alterData === 'undefined' || alterData =="")){
                var tmppn = data.pagenum;
                data = alterData;
                data.pagenum = tmppn;
            }else{
                data.filterListFields = outFilterCondition;
                outFilterCondition = "";
            }
            data.$skip = data.pagenum * data.pagesize;
            data.$top = data.pagesize;
            data.$inlinecount = "allpages";
			data.otherParams = config && config.otherParams ? config.otherParams : null;
			if(config.isSaveFormData && config.formData) config.formData.data = _.extend({}, data);
			if(!_.isEmpty(_dynamicParams)) _.extend(data, _dynamicParams);
			
            return data;
		};
		var format = typeof (formatData) == "function" ? formatData : defaultFormat;
		var dataAdapterG = new $.jqx.dataAdapter(sourceGrid, {
			formatData : format,
			loadError : function(xhr, status, error) {
			},
			downloadComplete : function(data, status, xhr) {
				_isInit = true;
				if(data !== undefined && data.records !== undefined) localStorage.total_records = data.records.length;
			},
			beforeLoadComplete : function(records) {
			}
		});
		return dataAdapterG;
	};
	var getAllCondition = function(grid){
		var data = grid.data();
		var all = "";
		if(data){
			for(var x in data){
				if(x && x.indexOf('value999999') != -1){
					var key = x.replace("value999999", "");
					if(key){
						var str = getFilterCondition(grid, key);
						all += str;
					}
				}
			}
		}
		return all;
	};
	var getFilterCondition = function(grid, key){
		var c = grid.data('condition999999'+ key);
		var data = grid.data('value999999' + key);
		var str = "|OLBIUS|"+key+"|SUIBLO|" + data +"|SUIBLO|EQUAL|SUIBLO|"+c;
		return str;
	};
	var getFieldType = function(datafields, fName){
		for (i=0;i < datafields.length;i++) {
			if(datafields[i]['name'] == fName){
				if(!(typeof datafields[i]['other'] === 'undefined' || datafields[i]['other'] =="")){
					return  datafields[i]['other'];
				}else{
					return  datafields[i]['type'];
				}
				
			}
		}
	};
	var getFieldPattern = function(datafields, fName){
        for (i=0;i < datafields.length;i++) {
           if(datafields[i]['name'] == fName){
                if(!(typeof datafields[i]['pattern'] === 'undefined' || datafields[i]['pattern'] =="")){
                    return  datafields[i]['pattern'];
                }else{
                    return  '';
                }
                
           }
        }
    };
	
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
                	var __selectrow = typeof(_configcontextmenu.selectrow) != "undefined" ? _configcontextmenu.selectrow : true;
                	if (__selectrow) _grid_obj.jqxGrid('selectrow', event.args.rowindex);
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    _contextmenu_obj.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;   
                }
            });
		}
		OlbGridUtil.addContextMenuHoverStyle(_grid_obj, _contextmenu_obj);
	}
	
	this.getType = function(){
		return _type;
	}
	this.getGridObj = function(){
		return _grid_obj;
	}
	this.putConfig = function(key, value){
		_config.put(key, value);
	}
	
	this.getDynamicParams = function(){
		return _dynamicParams;
	}
	this.setDynamicParams = function(dynamicParams){
		_dynamicParams = _.extend({}, dynamicParams);
	}
	this.addDynamicParams = function(key, value){
		_dynamicParams[key] = value;
	}
	
	function callbackFocusFilter() {
		if (sessionStorage) {
			var previousId = sessionStorage.getItem('previousInputFilter');
			var inpt = $($("#" + previousId).find('input')).get(0);
			if(inpt) inpt.focus();
		} else if (localStorage) {
			var previousId = localStorage.getItem('previousInputFilter');
			$("#" + previousId).find('input').focus();
		}
	}
	/*function selectItem(selectedIndex, selectArr) {
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
	}*/
	function selectItem(selectedIndex, selectArr) {
		if (OlbCore.isEmpty(selectedIndex) && OlbCore.isEmpty(selectArr)) {
			selectedIndex = _selectedIndex_def;
			selectArr = _selectArr_def;
		}
		
		if (OlbCore.isNotEmpty(selectArr) && selectArr.length > 0) {
			if (OlbCore.isArray(selectArr)) {
				// process is array item selected
				var key = _config.key;
				var rowsData = _grid_obj.jqxGrid('getrows');
				if (key != null && rowsData != null) {
					var finded = false;
					$.each(selectArr, function(itemKey, itemValue){
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
		} else if (OlbCore.isNotEmpty(selectedIndex)){
			_grid_obj.jqxGrid({selectedrowindex: selectedIndex});
		}
	}
	
	this.disable = function(isDisable){
		if (typeof(isDisable) != "undefined") _grid_obj.jqxGrid('disabled', isDisable);
	}
	
	this.getValueOrigin = function(){
		return _selectedValue;
	}
	this.getValue = function(key){
		if (_selectedValue) {
			var returnValue;
			var selectionMode = _grid_obj.jqxGrid('selectionmode');
			if ("checkbox" == selectionMode || "multiplerows" == selectionMode) {
				returnValue = [];
				if (key) {
					$.each(_selectedValue, function(id, value){
						if (value) returnValue.push(value[key]);
					});
				} else {
					$.each(_selectedValue, function(id, value){
						if (value) returnValue.push(value);
					});
				}
				return returnValue;
			} else {
				// single row
				if (key) {
					$.each(_selectedValue, function(id, value){
						return value[key];
					});
				} else {
					$.each(_selectedValue, function(id, value){
						return value;
					});
				}
			}
		}
		return null;
	}
	
	return init(p_gridObj, p_localData, p_config, p_selectArr);
};
OlbGrid.prototype.clearSelection = function() {
	var rowSelected = this.getGridObj().jqxGrid('selectedrowindex');
	var rowsSelected = this.getGridObj().jqxGrid('selectedrowindexes');
	if (rowSelected > -1 || (typeof(rowsSelected) != "undefined" && rowsSelected.length > 0)) {
		this.getGridObj().jqxGrid('clearselection');
	}
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
OlbGrid.prototype.rowUnSelectListener = function(callback) {
	var __grid_obj = this.getGridObj();
	__grid_obj.on('rowunselect', function (event) {
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
OlbGrid.prototype.on = function(action, callback){
	if (typeof(callback) == "function") {
		this.getGridObj().on(action, callback);
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
			tmpSource._source.localdata = localData;
			__grid_obj.jqxGrid('clearselection');
			__grid_obj.jqxGrid('source', tmpSource);
		}
	}
	
	if (typeof(callback) == "function") {
		this.bindingCompleteListener(function(){
			callback();
		}, true);
	}
};
OlbGrid.prototype.isExistData = function() {
	var isExist = false;
	var dataRow = this.getGridObj().jqxGrid("getrows");
	if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
		isExist = true;
	}
	return isExist;
};
OlbGrid.prototype.getAllRowData = function() {
	var dataRowResult = [];
	var dataRow = this.getGridObj().jqxGrid("getboundrows");
	if (typeof(dataRow) != 'undefined') {
		for (var i = 0; i < dataRow.length; i++) {
			var dataItem = dataRow[i];
			if (dataItem != window) {
				dataRowResult.push(dataItem);
			}
		}
	}
	return dataRowResult;
};

var OlbGridUtil = (function(){
	var updateSource = function(gridObj, newUrl, localData, callback) {
		var tmpSource = gridObj.jqxGrid('source');
		if (tmpSource) {
			if (newUrl) {
				tmpSource._source.url = newUrl;
				clearSelection(gridObj);
				gridObj.jqxGrid('source', tmpSource);
			} else if (localData) {
				tmpSource._source.localdata = localData;
				clearSelection(gridObj);
				gridObj.jqxGrid('source', tmpSource);
			}
		}
		
		if (typeof(callback) == "function") {
			gridObj.one('bindingcomplete', function (event) {
				callback();
		    });
		}
	};
	var clearSelection = function(gridObj) {
		var rowSelected = gridObj.jqxGrid('selectedrowindex');
		var rowsSelected = gridObj.jqxGrid('selectedrowindexes');
		if (rowSelected > -1 || (typeof(rowsSelected) != "undefined" && rowsSelected.length > 0)) {
			gridObj.jqxGrid('clearselection');
		}
	};
	var closeTooltipOnRow = function(gridId, rowObj, rowIndex, columnIndex, isCloseAll, isDestroy){
		var action = isDestroy ? 'destroy' : 'close';
		if (isCloseAll) {
			// gridId, null, null, columnIndex, isCloseAll
			var dataRow = $("#" + gridId).jqxGrid("getboundrows");
			if (typeof(dataRow) != 'undefined') {
				var icount = 0;
				for (var i = 0; i < dataRow.length; i++) {
					var dataItem = dataRow[i];
					if (dataItem != window) {
						var rowObj = $("#row" + icount + gridId + " div[role='gridcell']");
						if (typeof(rowObj) != 'undefined' && typeof(rowObj[columnIndex]) != 'undefined') {
							if (isDestroy) {
								$(rowObj[columnIndex]).jqxTooltip(action);
							} else {
								$(rowObj[columnIndex]).jqxTooltip(action);
							}
						}
						icount++;
					}
				}
			}
		}
	};
	var displayTooltipOnRow = function(typeTooltip, gridId, rowObj, rowIndex, columnIndex, contentMsg){
		// typeTooltip || rowObj || columnIndex || contentMsg
		// typeTooltip || gridId || rowIndex || columnIndex || contentMsg
		if (OlbCore.isNotEmpty(rowObj)) {
			if ("ERROR" == typeTooltip){
				var rrr = $(rowObj[columnIndex]).jqxTooltip({theme: 'tooltip-validation', content: contentMsg, position: 'bottom', autoHide: false});
			} else if ("INFO" == typeTooltip){
				var rrr = $(rowObject[columnIndex]).jqxTooltip({content: contentMsg, position: 'mouse', name: 'movieTooltip'});
			}
		} else {
			var rowId = "#row" + rowIndex + gridId + " div[role='gridcell']";
			var rowObject = $('#' + gridId).find(rowId);
			if (OlbCore.isNotEmpty(rowObject)) {
				if ("ERROR" == typeTooltip){
					var rrr = $(rowObject[columnIndex]).jqxTooltip({theme: 'tooltip-validation', content: contentMsg, position: 'bottom', autoHide: false});
					// setTimeout(function(){$(rowObj[columnIndex]).jqxTooltip("open");}, 100);
				} else if ("INFO" == typeTooltip){
					var rrr = $(rowObject[columnIndex]).jqxTooltip({content: contentMsg, position: 'mouse', name: 'movieTooltip'});
				}
			}
		}
	};
	var addContextMenuHoverStyle = function(grid_obj, contextmenu_obj){
		var dm = $(contextmenu_obj);
		if (contextmenu_obj && dm.length) {
			grid_obj.addClass('jqx-grid-context-menu');
		}
	};
	
	// copy from jqx.util.js
	var createCustomControlButton = function(grid, container, value){
		var tmpStr = value.split("@");
		var id = grid.attr('id');
		var group = $('.custom-control-toolbar').length + 1;
		
		var divCustomControlNew = new StringBuilder();
		if (tmpStr.length == 4) {
        	if (tmpStr[1] == '_last_update_') {
        		divCustomControlNew.append('<div class="custom-control-toolbar">');
        		divCustomControlNew.append('&nbsp;<span id=' + tmpStr[1] +'></span>&nbsp;');
        		divCustomControlNew.append('<a id="customcontrol' + id + group + '" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '"><i class="' + tmpStr[0] +'"></i></a>');
        		divCustomControlNew.append('<span id="_status_update_" style="float:right; margin-right:4px; color:#4383b4; display:none;"><image src="/images/ajax-loader.gif"></span>');
        		divCustomControlNew.append('</div>');
			} else {
				divCustomControlNew.append('<div class="custom-control-toolbar">');
				divCustomControlNew.append('<a id="customcontrol' + id + group + '" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '">');
				divCustomControlNew.append('<i class="' + tmpStr[0] +'"></i>&nbsp;<span>' + tmpStr[1] +'</span></a>');
				divCustomControlNew.append('</div>');
			}
            container.append(divCustomControlNew.toString());
        } else {
			var tmp = tmpStr[2];
			var link = tmpStr[2];
			var target = "";
			if (tmp.indexOf("$") != "-1") {
				var arr = tmp.split("$");
				link = arr.shift();
				target = arr.pop();
			}
			divCustomControlNew.append('<div class="custom-control-toolbar">');
			divCustomControlNew.append('<a id="customcontrol' + id + group +'" style="color:#438eb9;" href="' + link + '" ' + target + '>');
			divCustomControlNew.append('<i class="' + tmpStr[0] +'"></i>&nbsp;<span>' + tmpStr[1] +'</span></a>');
			divCustomControlNew.append('</div>');
            container.append(divCustomControlNew.toString());
        }
	};
	
	this.getValue = function(grid){
		return getValueByKey(grid);
	};
	
	this.getValueByKey = function(grid, key){
		var returnValue = null;
		var grid = this.getGridObj();
		var selectionMode = grid.jqxGrid('selectionmode');
		if (selectionMode) {
			if ("checkbox" == selectionMode || "multiplerows" == selectionMode) {
				// multi rows
				returnValue = [];
				var rowindexes = grid.jqxGrid("getselectedrowindexes");
				
				if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
					return returnValue;
				}
				
				if (key) {
					// get only value of key
					for (var i = 0; i < rowindexes.length; i++) {
						var dataItem = grid.jqxGrid("getrowdata", rowindexes[i]);
						if (dataItem) {
							returnValue.push(dataItem[key]);
						}
					}
				} else {
					// get all row
					for (var i = 0; i < rowindexes.length; i++) {
						var dataItem = grid.jqxGrid("getrowdata", rowindexes[i]);
						if (dataItem) {
							returnValue.push(dataItem);
						}
					}
				}
				
				return returnValue;
			} else {
				// single row
				var rowindex = grid.jqxGrid('getselectedrowindex');
				if (rowindex > -1) {
					var dataItem = grid.jqxGrid("getrowdata", rowindex);
					if (key) {
						returnValue = dataItem[key];
					} else {
						returnValue = dataItem;
					}
				}
			}
		}
		return returnValue;
	}
	return {
		updateSource: updateSource,
		closeTooltipOnRow: closeTooltipOnRow,
		displayTooltipOnRow: displayTooltipOnRow,
		addContextMenuHoverStyle: addContextMenuHoverStyle,
		clearSelection: clearSelection,
		createCustomControlButton: createCustomControlButton,
		getValue: getValue,
		getValueByKey: getValueByKey
	};
}());