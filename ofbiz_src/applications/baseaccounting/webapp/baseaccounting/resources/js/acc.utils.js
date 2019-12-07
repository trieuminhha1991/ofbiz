if(typeof(accutils) == "undefined"){
var accutils = (function(){
	var uiLabelMap = {};
	var setUiLabelMap = function(key, value){
		uiLabelMap[key] = value;
	};
	
	var isNotEmpty = function(value) {
		if (typeof(value) != 'undefined' && value != null && !(/^\s*$/.test(value))) {
			return true;
		} else {
			return false;
		}
	};
	var addZero = function(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	var formatFullDate = function(value) {
		if (accutils.isNotEmpty(value)) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	};
	var getTimestamp = function(value) {
		if (accutils.isNotEmpty(value)) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + '-';
			dateStr += addZero((value.getMonth()+1)) + '-';
			dateStr += addZero(value.getDate()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	};
	var validElement = function(input, commit, nameValidor, config){
		var validInputNotNull = function(input, commit){
			var value = $(input).val();
			// value == null || /^\s*$/.test(value)
			if(!isNotEmpty(value)){
				return false;
			}
			if(!isNaN(value) && value == 0){
				return false;
			}
			return true;
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
				}
			}
			return false;
		};
		var validDateTimeInputNotNull = function(input, commit){
			var value = $(input).jqxDateTimeInput('getDate');
			if(!isNotEmpty(value)){
				return false;
			}
			return true;
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
		var validDeliveryDateNotNull = function (input, commit) {
			var desiredDeliveryDate = $('#desiredDeliveryDate').jqxDateTimeInput('getDate');
			var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
			var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
			if((!isNotEmpty(desiredDeliveryDate))
				&& (!isNotEmpty(shipAfterDate)  
					|| !isNotEmpty(shipBeforeDate))){
				return false;
			}
			return true;
		};
		var validCompareStartDateAndFinishDate = function (input, commit) {
			var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
			var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
			if (isNotEmpty(shipAfterDate) && isNotEmpty(shipBeforeDate)) {
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
			if (isNotEmpty(desiredDeliveryDate)) {
				if (isNotEmpty(shipAfterDate)) {
					if (desiredDeliveryDate < shipAfterDate) {
						return false;
					}
				}
				if (isNotEmpty(shipBeforeDate)) {
					if (desiredDeliveryDate > shipBeforeDate) {
						return false;
					}
				}
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
		var validComboBoxMultiNotNull = function(input, commit, config) { //isNotEmptyComboBox
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
		} else if ('validDeliveryDateNotNull' == nameValidor) {
			return validDeliveryDateNotNull(input, commit);
		} else if ('validCannotSpecialCharactor' == nameValidor) {
			return validCannotSpecialCharactor(input, commit);
		} else if ('validPostalCharactor' == nameValidor) {
			return validPostalCharactor(input, commit);
		} else if ('validCompareStartDateAndFinishDate' == nameValidor) {
			return validCompareStartDateAndFinishDate(input, commit);
		} else if ('validCompareDeliveryDateBetweenStartDateAndFinishDate' == nameValidor) {
			return validCompareDeliveryDateBetweenStartDateAndFinishDate(input, commit);
		} else if ('validCompareTwoDate' == nameValidor) {
			return validCompareTwoDate(input, commit, config);
		} else if ('validComboBoxMultiNotNull' == nameValidor) {
			return validComboBoxMultiNotNull(input, commit, config);
		}
	};
	var ObjectTypeEnum = {
		GRID: 1,
		DROP_DOWN_BUTTON: 2,
		DROP_DOWN_LIST: 3,
		COMBO_BOX: 4,
		TREE_GRID: 5,
	};
	var processConfig = function(config){
		var configCf = {};
		if (typeof(config) == 'undefined' || config == null) {
			return configCf;
		}
		var objType = typeof(config.objType) != 'undefined' ? config.objType : -1;
		
		// common
		configCf.theme = typeof(config.theme) != 'undefined' ? config.theme : "olbius";
		configCf.width = typeof(config.width) != 'undefined' ? config.width : "100%";
		configCf.height = typeof(config.height) != 'undefined' ? config.height : 25;
		configCf.selectedIndex = config.selectedIndex != null ? config.selectedIndex : null;
		configCf.useUrl = config.useUrl ? config.useUrl : false;
		configCf.url = config.url ? config.url : "";
		configCf.root = config.root ? config.root : "results";
		
		configCf.key = config.key ? config.key : "id";
		configCf.value = config.value ? config.value : "description";
		configCf.description = config.description ? config.description : "description";
		
		if (objType == ObjectTypeEnum.DROP_DOWN_LIST || objType == ObjectTypeEnum.COMBO_BOX) {
			// dropDownList or comboBox
			configCf.dropDownWidth = typeof(config.dropDownWidth) != 'undefined' ? config.dropDownWidth : "auto";
			configCf.dropDownHeight = typeof(config.dropDownHeight) != 'undefined' ? config.dropDownHeight : null;
			configCf.autoDropDownHeight = typeof(config.autoDropDownHeight) != 'undefined' ? config.autoDropDownHeight : false;
			
			configCf.displayDetail = config.displayDetail ? config.displayDetail : false;
			var renderer = function (index, label, value) {
	            var valueStr;
	            if (config.displayDetail) {
	            	valueStr = label + " [" + value + "]";
	            } else {
	            	valueStr = label;
	            }
	            return valueStr;
	        };
			configCf.renderer = typeof(config.renderer) != "undefined" ? config.renderer : renderer;
			
			configCf.disabled = config.disabled ? config.disabled : false;
			configCf.placeHolder = config.placeHolder ? config.placeHolder : "";
			
			
			if (objType == ObjectTypeEnum.COMBO_BOX) {
				// comboBox
				configCf.remoteAutoComplete = config.remoteAutoComplete ? config.remoteAutoComplete : false;
				configCf.autoComplete = typeof(config.autoComplete) != 'undefined' ? config.autoComplete : null, 
				configCf.searchMode = config.searchMode ? config.searchMode : 'containsignorecase', 
				configCf.multiSelect = config.multiSelect ? config.multiSelect : false;
				
				var renderSelectedItem = function(index, item) {
					if (isNotEmpty(item)) {
						return item.description;
					}
		            return "";
		        };
		        configCf.renderSelectedItem = typeof(config.renderSelectedItem) != "undefined" ? config.renderSelectedItem : renderSelectedItem;
		        
			}
		} else if (objType == ObjectTypeEnum.DROP_DOWN_BUTTON || objType == ObjectTypeEnum.GRID || objType == ObjectTypeEnum.TREE_GRID) {
			// dropDownButton, grid, treeGrid
			configCf.selectionmode = typeof(config.selectionmode) != 'undefined' ? config.selectionmode : 'singlerow';
			
			if (objType == ObjectTypeEnum.DROP_DOWN_BUTTON) {
				// dropDownButton
				configCf.width = config.width ? config.width : 600;
				configCf.showDetail = (config.showDetail != undefined || config.showDetail == null) ? config.showDetail : true;
				configCf.height = config.height ? config.height : 'auto';
				configCf.widthButton = config.widthButton ? config.widthButton : 218;
				configCf.heightButton = config.heightButton ? config.heightButton : 25;
				configCf.gridType = config.gridType ? config.gridType : null;
				if (configCf.gridType == "jqxTreeGrid") {
					configCf.parentKeyId = typeof(config.parentKeyId) != 'undefined' ? config.parentKeyId : null;
				}
			} else if (objType == ObjectTypeEnum.GRID) {
				// grid
				configCf.showaggregates = typeof(config.showaggregates) != 'undefined' ? config.showaggregates : null;
				configCf.showstatusbar = typeof(config.showstatusbar) != 'undefined' ? config.showstatusbar : null;
				configCf.statusbarheight = typeof(config.statusbarheight) != 'undefined' ? config.statusbarheight : null;
				configCf.contextMenu = typeof(config.contextMenu) != 'undefined' ? config.contextMenu : null;
			} else if (objType == ObjectTypeEnum.TREE_GRID) {
				// treeGrid
				configCf.selectionMode = typeof(config.selectionMode) != 'undefined' ? config.selectionMode : null;
				configCf.expandAll = typeof(config.expandAll) != 'undefined' ? config.expandAll : false;
				configCf.columnGroups = typeof(config.columnGroups) != 'undefined' ? config.columnGroups : null;
				configCf.parentKeyId = typeof(config.parentKeyId) != 'undefined' ? config.parentKeyId : null;
				
			}
			
			configCf.datatype = config.datatype ? config.datatype : 'json';
			configCf.sortcolumn = config.sortcolumn ? config.sortcolumn : '';
			configCf.sortdirection = config.sortdirection ? config.sortdirection : 'asc';
			configCf.columns = config.columns ? config.columns : [];
			
			configCf.filterable = typeof(config.filterable) != 'undefined' ? config.filterable : true;
			configCf.virtualmode = typeof(config.virtualmode) != 'undefined' ? config.virtualmode : true; 
			var rendergridrows = null;
			if (configCf.virtualmode) {
				rendergridrows = function(obj) {	
					return obj.data;
				}; 
			}
			configCf.rendergridrows = typeof(config.rendergridrows) != 'undefined' ? config.rendergridrows : rendergridrows;
			configCf.showfilterrow = typeof(config.showfilterrow) != 'undefined' ? config.showfilterrow : true;
			configCf.sortable = typeof(config.sortable) != 'undefined' ? config.sortable : true;
			configCf.editable = typeof(config.editable) != 'undefined' ? config.editable : false;
			configCf.autoheight = typeof(config.autoheight) != 'undefined' ? config.autoheight : true;
			configCf.columnsresize = typeof(config.columnsresize) != 'undefined' ? config.columnsresize : true;
			configCf.pageable = typeof(config.pageable) != 'undefined' ? config.pageable : true;
			/*configCf.groupable = typeof(config.groupable) != 'undefined' ? config.groupable : false;
			configCf.pagesize = typeof(config.pagesize) != 'undefined' ? config.pagesize : 10;
			configCf.source = typeof(config.source) != 'undefined' ? config.source : {pagesize: configCf.pagesize};
			configCf.editmode = typeof(config.editmode) != 'undefined' ? config.editmode : 'none';
			if (configCf.groupable) {
				configCf.showgroupsheader = true;
				if (typeof(config.groupsrenderer) != 'undefined') {
					configCf.groupsrenderer = config.groupsrenderer;
				}*/
				/*
				else {
					configCf.groupsrenderer = function (text, group, expanded, data) {
					    return "" + group + "";
					};
				}
				*/
			/*}*/
			configCf.rendertoolbar = typeof(config.rendertoolbar) != 'undefined' ? config.rendertoolbar : null;
			configCf.showtoolbar = typeof(config.showtoolbar) != 'undefined' ? config.showtoolbar : null;
		}
		
		// common
		var coupleKeyValue = [];
		if (config.key != null) coupleKeyValue.push({name: config.key});
		if (config.value != null) coupleKeyValue.push({name: config.value});
		configCf.datafields = config.datafields ? config.datafields : coupleKeyValue;
		
		configCf.showdefaultloadelement = config.showdefaultloadelement ? config.showdefaultloadelement : false;
		configCf.autoshowloadelement = config.autoshowloadelement ? config.autoshowloadelement : false;
		configCf.useUtilFunc = config.useUtilFunc ? config.useUtilFunc : false;
		
		configCf.dropDownHorizontalAlignment = config.dropDownHorizontalAlignment ? config.dropDownHorizontalAlignment : 'left';
		configCf.searchId = config.searchId ? config.searchId : null;
		configCf.displayId = config.displayId ? config.displayId : null;
		configCf.displayValue = config.displayValue ? config.displayValue : null;
		configCf.displayLabel = config.displayLabel ? config.displayLabel : null;
		
		configCf.source = typeof(config.source) != 'undefined' ? config.source : {};
		configCf.source.dataUrl = typeof(config.dataUrl) != 'undefined' ? config.dataUrl : null;
		configCf.source.pagesize = typeof(config.pagesize) != 'undefined' ? config.pagesize : null;
		configCf.localization = typeof(config.localization) != 'undefined' ? config.localization : getLocalization();
		
		return configCf;
	};
	var initDropDownList = function(downDownListObject, localData, config, selectArr){
		config.objType = ObjectTypeEnum.DROP_DOWN_LIST;
		config = processConfig(config);
		var sourceReason;
		if (config.useUrl && (config.url != null)) {
			sourceReason = {
                datatype: "json",
                datafields: config.dataFields, 
                data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
                url: config.url,
                async: false,
                root: config.root,
            };
		} else {
			sourceReason = {
	        	localdata: localData,
		        datatype: "array"
		    };
		}
		$.jqx.theme = config.theme;
	    var dataPromoTypeAdapter = new $.jqx.dataAdapter(sourceReason);
	    $(downDownListObject).jqxDropDownList({
	    	theme: $.jqx.theme,
	    	source: dataPromoTypeAdapter, 
	    	width: config.width, 
	    	displayMember: config.value, 
	    	valueMember: config.key, 
	    	disabled: config.disabled,
	    	dropDownWidth: config.dropDownWidth, 
	    	dropDownHeight: config.dropDownHeight, 
	    	autoDropDownHeight: config.autoDropDownHeight, 
	    	placeHolder: config.placeHolder,
	    	dropDownHorizontalAlignment: config.dropDownHorizontalAlignment,
	    	renderer: config.renderer
	    });
	    if (config.selectedIndex != null) {
	    	$(downDownListObject).jqxDropDownList({selectedIndex : config.selectedIndex});
	    }
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(downDownListObject).jqxDropDownList('selectItem', item);
	    	}
		}
	}
	var initComboBox = function(comboBoxObject, localData, config, selectArr){
		config.objType = ObjectTypeEnum.COMBO_BOX;
		config = processConfig(config);
		var sourceInputEnum;
		if (config.useUrl && (config.url != null)) {
			sourceInputEnum = {
                datatype: "json",
                datafields: config.dataFields, 
                data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
                url: config.url,
                async: false,
                root: config.root,
            };
		} else {
			sourceInputEnum = {
	        	localdata: localData,
		        datatype: "array",
		        datafields: config.dataFields, 
		    };
		}
	    var dataAdapterInputEnum = new $.jqx.dataAdapter(sourceInputEnum, {
	        	formatData: function (data) {
	                if ($(comboBoxObject).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $(comboBoxObject).jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $.jqx.theme = config.theme;
	    // selectedIndex: 0, 
	    $(comboBoxObject).jqxComboBox({
	    	theme: $.jqx.theme,
	    	source: dataAdapterInputEnum, 
	    	multiSelect: config.multiSelect, 
	    	width: config.width, 
	    	height: config.height,
	    	placeHolder: config.placeHolder, 
	    	displayMember: config.value, 
	    	valueMember: config.key, 
	    	dropDownWidth: config.dropDownWidth, 
	    	dropDownHeight: config.dropDownHeight, 
	    	autoDropDownHeight: config.autoDropDownHeight,
	    	remoteAutoComplete: config.remoteAutoComplete,
	    	disabled: config.disabled,
	    	dropDownHorizontalAlignment: config.dropDownHorizontalAlignment,
	    	autoComplete: config.autoComplete, 
	    	searchMode: config.searchMode,
	    	renderer: config.renderer,
	        renderSelectedItem: config.renderSelectedItem,
	        search: function (searchString) {
	            dataAdapterInputEnum.dataBind();
	        }
	    });
	    if (config.selectedIndex != null) {
	    	$(comboBoxObject).jqxComboBox({selectedIndex : config.selectedIndex});
	    }
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBoxObject).jqxComboBox('selectItem', item);
	    	}
		}
	};
	
	
	
	var initTreeGrid = function(treeGridObject, localData, config, selectArr){
		config.objType = ObjectTypeEnum.TREE_GRID;
		config = processConfig(config);
		var source;
		if (config.useUrl && (config.url != null)) {
			source = {
                datatype: "json",
                datafields: config.dataFields, 
                data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
                url: config.url,
                async: false,
                root: config.root,
                id: config.key,
                hierarchy: {
                    keyDataField: { name: config.key },
                    parentDataField: { name: config.parentKeyId }
                },
            };
		} else {
			source = {
	        	localdata: localData,
		        datatype: "array",
		        datafields: config.dataFields, 
		    };
		}
	    var dataAdapter = new $.jqx.dataAdapter(source);
	    $.jqx.theme = config.theme;
	    var theme = $.jqx.theme;
	    $(treeGridObject).jqxTreeGrid({
	    	source: dataAdapter,
          	theme: theme,
          	width: config.width,
          	height: config.height,
          	altRows: true,
          	autoRowHeight: false,
          	localization: config.localization,
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
          	editable: config.editable,
          	selectionMode: config.selectionMode,
          	ready: function () {
          		if (config.expandAll) {
          			//$("#${id}").jqxTreeGrid('expandAll');
          			//expandAllTreeGrid("${id}");
          		} else {
          			$(treeGridObject).jqxTreeGrid('expandRow', 1);
          		}
            },
          	columns: config.columns,
          	columnGroups: config.columnGroups,
          	columnsResize: config.columnsresize,
          	showToolbar: config.showToolbar,
          	rendertoolbar: config.rendertoolbar,
	    });
	};
	var initGridUtil = function(gridObject, localData, config, selectArr){
		config.objType = ObjectTypeEnum.GRID;
		config = processConfig(config);
		if (config.useUtilFunc) {
			initGridUtilPrivate(gridObject, config, selectArr);
		} else {
			initGridNormal(gridObject, localData, config, selectArr);
		}
	};
	var initGridUtilPrivate = function(gridObject, config, selectArr){
		$.getScript('/aceadmin/jqw/jqwidgets/jqx.utils.js', function(){
			Grid.initGrid(config, config.datafields, config.columns, null, gridObject);
		});
	};
	var initGridNormal = function(dropDownGridObject, localData, config, selectArr){
		var sourceGrid;
		var dataAdapterGrid;
		config.filter = !config.vitualmode ? null : function () {
			// update the grid and send a request to the server.
			$(dropDownGridObject).jqxGrid('updatebounddata');
		};
		config.sort = !config.vitualmode ? null : function () {
			$(dropDownGridObject).jqxGrid('updatebounddata');
		};
		if (config.useUrl && (config.url != null)) {
			sourceGrid = {
				datafields: config.datafields,
				cache: false,
				root: config.root,
				datatype: config.datatype,
				beforeprocessing: function (data) {
					sourceGrid.totalrecords = data.TotalRows;
				},
				pager: function (pagenum, pagesize, oldpagenum) {
					// callback called when a page or page size is changed.
				},
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				filter: config.filter,
				sort: config.sort,
				sortcolumn: config.sortcolumn,
	           	sortdirection: config.sortdirection,
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize: 5,
				contentType: 'application/x-www-form-urlencoded',
				url: config.url,
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
				datafields: config.datafields,
				datatype: config.datatype,
				localdata: localData,
			};
			dataAdapterGrid = new $.jqx.dataAdapter(sourceGrid);
		}
		
		$.jqx.theme = config.theme;
		$(dropDownGridObject).jqxGrid({
			theme: $.jqx.theme,
			width: config.width, 
			height: config.height,
			source: dataAdapterGrid,
			filterable: config.filterable,
			showfilterrow: config.showfilterrow,
			sortable:true,
			editable: config.editable,
			autoheight:config.autoheight,
			columnsresize: true,
			pageable: config.pageable,
			pagesize: config.source.pagesize,
			showdefaultloadelement: config.showdefaultloadelement,
			autoshowloadelement: config.autoshowloadelement,
			virtualmode: config.virtualmode, 
			rendergridrows: config.rendergridrows,
			columns: config.columns,
			localization: config.localization,
			rendertoolbar: config.rendertoolbar,
			showtoolbar: config.showtoolbar,
			showaggregates: config.showaggregates,
			showstatusbar: config.showstatusbar,
			statusbarheight: config.statusbarheight,
			localization: config.localization,
			selectionmode: config.selectionmode,
		});
		if (isNotEmpty(config.selectedIndex) || (isNotEmpty(selectArr) && selectArr.length > 0)) {
			$(dropDownGridObject).on("bindingcomplete", function (event) {
				callEventSelectedGrid(dropDownGridObject, config, selectArr);
			});
		};
		if (isNotEmpty(config.contextMenu)) {
			dropDownGridObject.on('contextmenu', function () {
	            return false;
	        });
			dropDownGridObject.on('rowclick', function (event) {
				var adapter = dropDownGridObject.jqxGrid('source');
	            var record;
	            if (adapter) {
	            	var source = adapter._source;
	            	record = adapter.records;
		            if(!record || !record.length){
						return;
					}
	            }
                if (event.args.rightclick) {
                	dropDownGridObject.jqxGrid('selectrow', event.args.rowindex);
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    $("#" + config.contextMenu).jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;   
                }
            });
		}
	};
	var initDropDownButton = function(dropDownButtonObject, dropDownGridObject, localData, config, selectArr){
		config.objType = ObjectTypeEnum.DROP_DOWN_BUTTON;
		config = processConfig(config);
		$(dropDownButtonObject).jqxDropDownButton({
			width: config.widthButton, 
			height: config.heightButton,
			theme: typeof(config.theme) != 'undefined' ? config.theme : 'olbius',
			dropDownHorizontalAlignment: config.dropDownHorizontalAlignment
		});
		if (config.gridType == "jqxTreeGrid") {
			initTreeGrid(dropDownGridObject, localData, config, selectArr);
			$(dropDownGridObject).on('rowSelect', function(event){
				var args = event.args;
		        var row = args.row;
		        if (row) {
		        	processRowSelectedDropDownButton(dropDownButtonObject, dropDownGridObject, row, config);
		        }
			});
		} else {
			if (config.useUtilFunc) {
				initGridUtilPrivate(dropDownGridObject, config, selectArr);
			} else {
				initGridNormal(dropDownGridObject, localData, config, selectArr);
			}
			$(dropDownGridObject).on('rowselect', function(event){
				var args = event.args;
		        var row = $(dropDownGridObject).jqxGrid('getrowdata', args.rowindex);
		        processRowSelectedDropDownButton(dropDownButtonObject, dropDownGridObject, row, config);
			});
			//callEventSelectedDropDownButton(dropDownButtonObject, dropDownGridObject, config, selectArr);
			if (isNotEmpty(config.selectedIndex) || (isNotEmpty(selectArr) && selectArr.length > 0)) {
				$(dropDownGridObject).on("bindingcomplete", function (event) {
					dropDownButton.selectItem(dropDownButtonObject, dropDownGridObject, config, selectArr);
				});
			}
		}
	};
	var processRowSelectedDropDownButton = function(dropDownButtonObject, dropDownGridObject, row, config){
		if(row){
        	var key = config.key;
        	var	showDetail = config.showDetail;
        	var description = config.description;
			var keyValue = row[config.key];
			var descValue = "";
			if (typeof(description) == 'function') {
				descValue = description(row);
			} else if (Object.prototype.toString.call(description) === '[object Array]') {
				descValue = processRowDataDisplay(row, description);
			} else if (typeof(description) == 'string') {
				descValue = row[description];
			} else {
				descValue = row[key];
			}
			if(showDetail){
				accutils.setValueDropDownButtonOnly($(dropDownButtonObject), row[key], descValue + ' [' + row[key] + ']');
			}else{
				//descValue = row[key];
				accutils.setValueDropDownButtonOnly($(dropDownButtonObject), row[key], descValue);
			}
			$(dropDownButtonObject).jqxDropDownButton('close');
        }
	};
	/* =========================================== display ================================================ */
	var processRowDataDisplay = function(rowData, displayIdArr){
		var displayLabelAll = "";
		var isFirstLabelAll = true;
		for (var i = 0; i < displayIdArr.length; i++) {
			var displayIdItem = displayIdArr[i];
			if (isNotEmpty(rowData[displayIdItem])) {
				if (isFirstLabelAll) {
					isFirstLabelAll = false;
				} else {
					displayLabelAll += ", ";
				}
				displayLabelAll += rowData[displayIdItem];
			}
		}
		return displayLabelAll;
	}
	var callEventSelectedGrid = function(dropDownGridObject, config, selectArr){
		if (isNotEmpty(selectArr) && selectArr.length > 0) {
			// select item by value of row[key]
			for (var i = 0; i < selectArr.length; i++) {
				var item = selectArr[i];
				var key = config.key;
				var dataValue = $(dropDownGridObject).jqxGrid('getrows');
				if (dataValue){
					for (var j = 0; j < dataValue.length; j++) {
						var row = dataValue[j];
						if (row[key] == item) {
							$(dropDownGridObject).jqxGrid({selectedrowindex: j});
							return;
						}
					}
				}
			}
		} else {
			if (!isNotEmpty(config.displayValue)) {
				// select item by index
				if (isNotEmpty(config.selectedIndex)) {
					$(dropDownGridObject).jqxGrid({selectedrowindex: config.selectedIndex});
				}
			}
		}
	};
	var callEventSelectedDropDownButtonAndGrid = function(dropDownButtonObject, dropDownGridObject, config, selectArr){
		callEventSelectedGrid(dropDownGridObject, config, selectArr);
		dropDownButton.selectItem(dropDownButtonObject, dropDownGridObject, config, selectArr);
	};
	var setAttrDataValue = function(id, value){
		var obj = document.getElementById(id);
		if (isNotEmpty(obj)) {
			obj.setAttribute("data-value", value);
		}
	};
	var getAttrDataValue = function(id){
		var obj = document.getElementById(id);
		if (isNotEmpty(obj)) {
			return obj.getAttribute("data-value");
		}
	};
	var clearAttrDataValue = function(id){
		setAttrDataValue(id, "");
	};
	var setValueDropDownButtonOnly = function(dropDownButtonObj, value, label){
		if (isNotEmpty(dropDownButtonObj)) {
			var dropDownContent = '<div class="innerDropdownContent">' + label + '</div>';
	        $(dropDownButtonObj).jqxDropDownButton('setContent', dropDownContent);
	        var objId = $(dropDownButtonObj).attr('id');
	        setAttrDataValue(objId, value);
		}
	};
	var setValueDropDownListOnly = function(dropDownListObj, value, key, data){
		var index = 0;
		for(var i = 0; i < data.length; i++){
			if(data[i][key] == value){
				index = i;
				break;
			}
		}
		dropDownListObj.jqxDropDownList({selectedIndex: index});
	};
	var updateGridSource = function(gridObj, newUri){
		var tmpSource = $(gridObj).jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.url = newUri;
			$(gridObj).jqxGrid('clearselection');
			$(gridObj).jqxGrid('source', tmpSource);
		}
	};
	var dropDownList = (function(){
		var updateSource = function(dropDownObj, newUri, selectItem){
			var tmpSource = $(dropDownObj).jqxDropDownList('source');
			if (typeof(tmpSource) != 'undefined') {
				tmpSource._source.url = newUri;
				$(dropDownObj).jqxDropDownList('clearSelection');
				$(dropDownObj).jqxDropDownList('source', tmpSource);
				if (selectItem) {
					$(dropDownObj).jqxDropDownList('selectItem', selectItem);
				}
			}
		};
		var clearAll = function(dropDownListObj, isClearSource){
			$(dropDownListObj).jqxDropDownList('clearSelection');
			if (isClearSource) $(dropDownListObj).jqxDropDownList('clear');
		};
		return {
			updateSource: updateSource,
			clearAll: clearAll,
		};
	}());
	var dropDownButton = (function(){
		var updateSource = function(dropDownButtonObject, dropDownGridObject, newUri){
			var tmpSource = $(dropDownGridObject).jqxGrid('source');
			if(typeof(tmpSource) != 'undefined'){
				tmpSource._source.url = newUri;
				$(dropDownButtonObject).jqxDropDownButton('setContent', '');
				$(dropDownGridObject).jqxGrid('clearselection');
				$(dropDownGridObject).jqxGrid('source', tmpSource);
				var objId = $(dropDownButtonObject).attr('id');
				clearAttrDataValue(objId);
			}
		};
		var clearAll = function(dropDownButtonObj, dropDownGridObj, isClearSource){
			$(dropDownButtonObj).jqxDropDownButton('setContent', '');
			$(dropDownGridObj).jqxGrid('clearselection');
			if (isClearSource) $(dropDownGridObj).jqxGrid('clear');
			var objId = $(dropDownButtonObj).attr('id');
			clearAttrDataValue(objId);
		};
		var selectItem = function(dropDownButtonObject, dropDownGridObject, config, selectArr){
			/* config.selectedIndex
			 * config.searchId
			 * config.displayId: mang chua danh sach ten cua cot se duoc hien thi tren button
			 * selectArr: mang chua cac gia tri selected
			 * */
			var isExists = false;
			if (isNotEmpty(selectArr) && selectArr.length > 0) {
				// select item by value of row[key]
				for (var i = 0; i < selectArr.length; i++) {
					var item = selectArr[i];
					var key = config.key;
					var dataValue = $(dropDownGridObject).jqxGrid('getrows');
					if (dataValue){
						for (var j = 0; j < dataValue.length; j++) {
							var row = dataValue[j];
							if (row[key] == item) {
								isExists = true;
								processRowSelectedDropDownButton(dropDownButtonObject, dropDownGridObject, row, config);
								break;
							}
						}
					}
					if (isExists) break;
				}
				if (!isExists) {
					accutils.setValueDropDownButtonOnly($(dropDownButtonObject), selectArr[0], "[" + selectArr[0] + "]");
				}
			} else {
				if (!isExists) {
					if (isNotEmpty(config.displayValue)) {
						var displayValue = config.displayValue;
						var displayLabel = config.displayValue;
						if (isNotEmpty(config.displayLabel)) 
							displayLabel = config.displayLabel;
						
						isExists = true;
						accutils.setValueDropDownButtonOnly($(dropDownButtonObject), displayValue, displayLabel);
					}
				}
				if (!isExists) {
					// select item by index
					if (isNotEmpty(config.selectedIndex)) {
						isExists = true;
						var row = $(dropDownGridObject).jqxGrid('getrowdata', config.selectedIndex);
						if (row != undefined) {
							processRowSelectedDropDownButton(dropDownButtonObject, dropDownGridObject, row, config);
						}
					}
				}
			}
			$(dropDownButtonObject).jqxDropDownButton('close');
		};
		return {
			selectItem: selectItem,
			updateSource: updateSource,
			clearAll:clearAll,
		}
	}());
	var displayTooltipOnRow = function(typeTooltip, gridId, rowObj, rowIndex, columnIndex, contentMsg){
		// typeTooltip || rowObj || columnIndex || contentMsg
		// typeTooltip || gridId || rowIndex || columnIndex || contentMsg
		if (isNotEmpty(rowObj)) {
			if ("ERROR" == typeTooltip){
				$(rowObj[columnIndex]).jqxTooltip({theme: 'tooltip-validation', content: contentMsg, position: 'bottom', autoHide: false});
			} else if ("INFO" == typeTooltip){
				$(rowObject[columnIndex]).jqxTooltip({content: contentMsg, position: 'mouse', name: 'movieTooltip'});
			}
		} else {
			var rowId = "#row" + rowIndex + gridId + " div[role='gridcell']";
			var rowObject = $('#' + gridId).find(rowId);
			if (isNotEmpty(rowObject)) {
				if ("ERROR" == typeTooltip){
					$(rowObject[columnIndex]).jqxTooltip({theme: 'tooltip-validation', content: contentMsg, position: 'bottom', autoHide: false});
					// setTimeout(function(){$(rowObj[columnIndex]).jqxTooltip("open");}, 100);
				} else if ("INFO" == typeTooltip){
					$(rowObject[columnIndex]).jqxTooltip({content: contentMsg, position: 'mouse', name: 'movieTooltip'});
				}
			}
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
						if (typeof(rowObj) != 'undefined') {
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
	var confirm = (function(){
		var confirm = function(msg, callback, lblCancel, lblOk){
			if (!isNotEmpty(lblCancel)) lblCancel = "Cancel";
			if (!isNotEmpty(lblOk)) lblOk = "Ok";
			bootbox.confirm(msg, lblCancel, lblOk, function(result) {
				if(result) {
					callback();
				}
			});
		};
		var dialog = function(msg, callback, lblCancel, lblOk){
			bootbox.dialog(msg, [
                {"label": lblCancel, "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": lblOk, "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
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
				"label" : "OK",
				"class" : "btn-mini btn-primary width60px",
			}]);
			return false;
		};
		var info = function(msg){
			var message = "<i class='fa-info-circle open-sans icon-modal-alert-info'></i><span class='message-content-alert-info'>";
			message += msg;
			message += "</span>";
			bootbox.dialog(message, [{
				"label" : "OK",
				"class" : "btn-mini btn-primary width60px",
				}]
			);
		};
		return {
			error: error,
			info: info,
		};
	}());
	var createNotificationContainer = function(idContainer, idNotification, template){
		if (idContainer == null) idContainer = "container";
		if (idNotification == null) idNotification = "jqxNotification";
		if (template == null || template == undefined) template = "success";
		var tmpWidth = "100%";
		$("#" + idContainer).width(tmpWidth);
        $("#" + idNotification).jqxNotification({ 
        	icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}, 
        	width: tmpWidth, 
        	appendContainer: "#container", 
        	opacity: 1, autoClose: true, template: template 
        });
	};
	var processResultDataAjax = function(data, callbackJsonError, callbackJsonSuccess, callbackData) {
		if (data.thisRequestUri == "json") {
    		var errorMessage = "";
	        if (data._ERROR_MESSAGE_LIST_ != null) {
	        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
	        		errorMessage += "<p>" + data._ERROR_MESSAGE_LIST_[i] + "</p>";
	        	}
	        }
	        if (data._ERROR_MESSAGE_ != null) {
	        	errorMessage += "<p>" + data._ERROR_MESSAGE_ + "</p>";
	        }
	        if (errorMessage != "") {
	        	if (typeof(callbackJsonError) == 'string') {
	        		if (callbackJsonError == "default") {
	        			$("#container").empty();
	    	        	$("#jqxNotification").jqxNotification({ template: 'error'});
	    	        	$("#jqxNotification").html(errorMessage);
	    	        	$("#jqxNotification").jqxNotification("open");
	        		} else {
	        			$("#container" + callbackJsonError).empty();
	    	        	$("#jqxNotification" + callbackJsonError).jqxNotification({ template: 'error'});
	    	        	$("#jqxNotification" + callbackJsonError).html(errorMessage);
	    	        	$("#jqxNotification" + callbackJsonError).jqxNotification("open");
	        		}
	        	} else if (typeof(callbackJsonError) == 'function') {
	        		callbackJsonError();
	        	}
	        	return false;
	        } else {
	        	if (typeof(callbackJsonSuccess) == 'string') {
	        		if (callbackJsonSuccess == "default") {
	        			$('#container').empty();
	    	        	$('#jqxNotification').jqxNotification({ template: 'info'});
	    	        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
	    	        	$("#jqxNotification").jqxNotification("open");
	        		} else {
	        			$("#container" + callbackJsonSuccess).empty();
	    	        	$("#jqxNotification" + callbackJsonSuccess).jqxNotification({ template: 'info'});
	    	        	$("#jqxNotification" + callbackJsonSuccess).html(uiLabelMap.wgupdatesuccess);
	    	        	$("#jqxNotification" + callbackJsonSuccess).jqxNotification("open");
	        		}
	        	} else if (typeof(callbackJsonSuccess) == 'function') {
	        		callbackJsonSuccess();
	        	}
				return true;
	        }
    	} else {
    		if (typeof(callbackData) == 'function') {
    			callbackData();
    		}
    		return true;
    	}
	};
	
	var getLabel = function(data, key, label, keyValue) {
		for(var i = 0; i < data.length; i++){
			if(data[i][key] == keyValue){
				return data[i][label];
			}
		}
		return keyValue;
	}
	
	var cellsrenderer = function(data, key, label, keyValue) {
		for(var i = 0; i < data.length; i++){
			if(data[i][key] == keyValue){
				return data[i][label];
			}
		}
		return keyValue;
	}
	
	var multi = function(number1, number2) {
		return Number(number1)*Number(number2);
	}
	
	var getThruDate = function(yearPeriod, monthPeriod, quaterPeriod) {
		var thruDate;
		if(monthPeriod && yearPeriod){
			var lastDateofTheMonth = new Date(yearPeriod, monthPeriod, 0);
			thruDate = lastDateofTheMonth.getFullYear() + '-' + (lastDateofTheMonth.getMonth() +1) + '-' +  (lastDateofTheMonth.getDate());
		}else if(quaterPeriod && yearPeriod){
			var q1 = new Date(yearPeriod,2,31);
		    var q2 = new Date(yearPeriod,5,30);
		    var q3 = new Date(yearPeriod,8,30);
		    var q4 = new Date(yearPeriod,11,31);
		    if(quaterPeriod == 1){
    		     thruDate = q1.getFullYear() + '-' + (q1.getMonth() +1) + '-' +  (q1.getDate());
		    }
		    
		    if(quaterPeriod == 2){
		    	thruDate = q2.getFullYear() + '-' + (q2.getMonth() +1) + '-' +  (q2.getDate());
		    }
		    
		    if(quaterPeriod == 3){
		    	thruDate = q3.getFullYear() + '-' + (q3.getMonth() +1) + '-' +  (q3.getDate());
		    }
		   
		    if(quaterPeriod == 4){
		    	thruDate = q4.getFullYear() + '-' + (q4.getMonth() +1) + '-' +  (q4.getDate());
		    }
		}else if(yearPeriod){
			var dateLastYear = new Date(yearPeriod, 11, 31);
			thruDate = dateLastYear.getFullYear() + '-' + (dateLastYear.getMonth() + 1) + '-' +  (dateLastYear.getDate());
		}
		return thruDate;
	}
	
	var getFromDate = function(yearPeriod, monthPeriod, quaterPeriod) {
		var fromDate;
		if(monthPeriod && yearPeriod){
			var firstDay = new Date(yearPeriod, monthPeriod - 1, 1);
			fromDate = firstDay.getFullYear() + '-' + (firstDay.getMonth() + 1) + '-' +  (firstDay.getDate());
		}else if(quaterPeriod && yearPeriod){
			var q1 = new Date(yearPeriod,2,31);
		    var q2 = new Date(yearPeriod,5,30);
		    var q3 = new Date(yearPeriod,8,30);
		    var q4 = new Date(yearPeriod,11,31);
		    if(quaterPeriod == 1){
		    	var firstDayQuater1 = new Date(yearPeriod, 1, 1);
    		     fromDate = firstDayQuater1.getFullYear() + '-' + (firstDayQuater1.getMonth()) + '-' +  (firstDayQuater1.getDate());
		    }
		    
		    if(quaterPeriod == 2){
		    	var firstDayQuater2 = new Date(yearPeriod, 4, 1);
		    	fromDate = firstDayQuater2.getFullYear() + '-' + (firstDayQuater2.getMonth()) + '-' +  (firstDayQuater2.getDate());
		    }
		    
		    if(quaterPeriod == 3){
		    	var firstDayQuater3 = new Date(yearPeriod, 7, 1);
		    	fromDate = firstDayQuater3.getFullYear() + '-' + (firstDayQuater3.getMonth()) + '-' +  (firstDayQuater3.getDate());
		    }
		   
		    if(quaterPeriod == 4){
		    	var firstDayQuater4 = new Date(yearPeriod, 10, 1);
		    	fromDate = firstDayQuater4.getFullYear() + '-' + (firstDayQuater4.getMonth()) + '-' +  (firstDayQuater4.getDate());
		    }
		}else if(yearPeriod){
			var dateFirstYear = new Date(yearPeriod,1,1);
			fromDate = dateFirstYear.getFullYear() + '-' + (dateFirstYear.getMonth()) + '-' +  (dateFirstYear.getDate());
		}
		return fromDate;
	}
	
	var processAjax = (function(){
		
		var success = function(data, gridObj, windowObj, cancelLabel, okLabel, isUpdate, isClose) {
			if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_ && !data.errorMessage){
				if(isClose == undefined || isClose == null || isClose){
					windowObj.jqxWindow('close');
				}
				if(isUpdate == undefined || isUpdate == null || isUpdate){
					gridObj.jqxGrid('updatebounddata');
				}
				var gridId = gridObj.attr('id');
				$('#container' + gridId).empty();
                $('#jqxNotification' + gridId).jqxNotification({ template: 'success'});
                $("#notificationContent" + gridId).text(wgaddsuccess);
                $("#jqxNotification" + gridId).jqxNotification("open");
			}else if(data._ERROR_MESSAGE_){
				accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, cancelLabel, okLabel);
			}else if(data._ERROR_MESSAGE_LIST_){
				accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_, function(){}, cancelLabel, okLabel);
			}else if(data.errorMessage){
				accutils.confirm.confirm(data.errorMessage, function(){}, cancelLabel, okLabel);
			}
		}
		
		var beforeSend = function(btnSave, btnCancel) {
			if(btnSave == undefined || btnSave == null){
				btnSave = $('#alterSave');
			}
			if(btnCancel == undefined || btnCancel == null){
				btnCancel = $('#alterCancel');
			}
			btnSave.prop('disabled', true);
			btnCancel.prop('disabled', true);
		}
		
		var complete = function(btnSave, btnCancel) {
			if(btnSave == undefined || btnSave == null){
				btnSave = $('#alterSave');
			}
			if(btnCancel == undefined || btnCancel == null){
				btnCancel = $('#alterCancel');
			}
			
			btnSave.prop('disabled', false);
			btnCancel.prop('disabled', false);
		}
		
		return {
			success: success,
			beforeSend: beforeSend,
			complete: complete,
		};
	}());
	
	var callAjax = function(setting,windowObj, gridObj, cancelLabel, okLabel, btnSave, btnCancel, isUpdate, isClose) {
		var url = setting['url'];
		var data = setting['data'];
		
		//Send Ajax Request
		$.ajax({
			url: url,
			type: "POST",
			data: data,
			dataType: 'json',
			async: false,
			success : function(data) {
				processAjax.success(data, gridObj, windowObj, cancelLabel, okLabel, isUpdate, isClose);
			},
			beforeSend: function(){
				processAjax.beforeSend(btnSave, btnCancel);
			},
			complete: function() {
				processAjax.complete(btnSave, btnCancel);
		    }
		});
	}
	
	var converNumber = function(number, locale) {
		var number  = number.toFixed(2);
		number = new Number(number);
		return number.toLocaleString(locale);
	}
	
	var formatcurrency = function(num, uom){
        if(num == null){
            return "";
        }
        decimalseparator = ",";
        thousandsseparator = ".";
        currencysymbol = "đ";
        if(typeof(uom) == "undefined" || uom == null){
            uom = "${defaultOrganizationPartyCurrencyUomId?if_exists}";
        }
        if(uom == "USD"){
            currencysymbol = "$";
            decimalseparator = ".";
            thousandsseparator = ",";
        }else if(uom == "EUR"){
            currencysymbol = "€";
            decimalseparator = ".";
            thousandsseparator = ",";
        }
        var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
        if(str.indexOf(".") > 0) {
            parts = str.split(".");
            str = parts[0];
        }
        str = str.split("").reverse();
        for(var j = 0, len = str.length; j < len; j++) {
            if(str[j] != ",") {
                output.push(str[j]);
                if(i%3 == 0 && j < (len - 1)) {
                    output.push(thousandsseparator);
                }
                i++;
            }
        }
        formatted = output.reverse().join("");
        return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
    };
	
    var createJqxWindow = function(divEle, width, height, initContent){
    	if(typeof(initContent) == null){
    		initContent = function () {};
    	}
    	var config = {
    	        showCollapseButton: false, width: width, theme:'olbius',
    	        autoOpen: false, isModal: true, maxWidth: 1000, 
    	        initContent: initContent
    	};
    	if(typeof(height) != 'undefined'){
    		config.height = height;
    	}
    	divEle.jqxWindow(config);
    };
    var createJqxDropDownList = function(elemenDiv, sourceArr, config){
    	var source = {
    			localdata: sourceArr,
    	        datatype: "array"	
    	};
    	var dataAdapter = new $.jqx.dataAdapter(source);
    	
    	config.source = dataAdapter;
    	elemenDiv.jqxDropDownList(config);
    	if(typeof renderer != "undefined"){
    		elemenDiv.jqxDropDownList({renderer: renderer});
    	};
    	
    	if(sourceArr.length < 8){
    		elemenDiv.jqxDropDownList({autoDropDownHeight: true});
    	}else{
    		elemenDiv.jqxDropDownList({autoDropDownHeight: false});
    	}
    };
    var updateSourceDropdownlist = function(dropdownEle, sourceArr){	
    	var source = {
    			localdata: sourceArr,
                datatype: "array"
    	}
    	var dataAdapter = new $.jqx.dataAdapter(source);
    	dropdownEle.jqxDropDownList('clearSelection');
    	dropdownEle.jqxDropDownList({source: dataAdapter});
    	if(sourceArr.length < 8){
    		dropdownEle.jqxDropDownList({autoDropDownHeight: true});
    	}else{
    		dropdownEle.jqxDropDownList({autoDropDownHeight: false});
    	}
    };
    var openJqxWindow = function(jqxWindowDiv){
    	var wtmp = window;
    	var tmpwidth = jqxWindowDiv.jqxWindow('width');
    	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
    	jqxWindowDiv.jqxWindow('open');
    };
    var createJqxMenu = function(id, itemHeight, width, config){
    	var liElement = $("#" + id + ">ul>li").length;
    	var contextMenuHeight = itemHeight* liElement;
    	if(typeof(config) == 'undefined'){
    		config = {};
    	}
    	config.width = width;
    	config.height = contextMenuHeight;
    	config.autoOpenPopup = false;
    	config.mode = "popup";
    	$("#" + id).jqxMenu(config);
    };
	return {
		openJqxWindow: openJqxWindow,
		createJqxMenu: createJqxMenu,
		updateSourceDropdownlist: updateSourceDropdownlist,
		createJqxDropDownList: createJqxDropDownList,
		createJqxWindow: createJqxWindow,
		formatcurrency: formatcurrency,
		converNumber: converNumber,
		initGridUtil: initGridUtil,
		callAjax: callAjax,
		initDropDownList: initDropDownList,
		setValueDropDownListOnly: setValueDropDownListOnly,
		initComboBox: initComboBox,
		initTreeGrid: initTreeGrid,
		initDropDownButton: initDropDownButton,
		isNotEmpty: isNotEmpty,
		formatFullDate: formatFullDate,
		callEventSelectedDropDownButtonAndGrid: callEventSelectedDropDownButtonAndGrid,
		callEventSelectedGrid: callEventSelectedGrid,
		validElement: validElement,
		clearAttrDataValue: clearAttrDataValue,
		setAttrDataValue: setAttrDataValue,
		getAttrDataValue: getAttrDataValue,
		setValueDropDownButtonOnly: setValueDropDownButtonOnly,
		updateGridSource: updateGridSource,
		processRowDataDisplay: processRowDataDisplay,
		dropDownList: dropDownList,
		dropDownButton: dropDownButton,
		displayTooltipOnRow: displayTooltipOnRow,
		closeTooltipOnRow: closeTooltipOnRow,
		confirm: confirm,
		alert: alert,
		processResultDataAjax: processResultDataAjax,
		setUiLabelMap: setUiLabelMap,
		getLabel: getLabel,
		multi: multi,
		getTimestamp: getTimestamp,
		getThruDate: getThruDate,
		getFromDate: getFromDate,
		createNotificationContainer: createNotificationContainer,
	};
}());
}