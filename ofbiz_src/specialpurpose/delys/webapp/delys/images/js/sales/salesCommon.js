function getDataStrUrlComboBox(index, obj) {
	var data = "";
	var dataListSelected = $(obj).jqxComboBox('getSelectedItems');
    var id = $(obj).attr("id");
	if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
		for (var i = 0; i < dataListSelected.length; i++) {
			var item = dataListSelected[i];
		 	if (item != null) data += "&" + id + "=" + item.value;
		}
	}
	return data;
}
function getDataStrUrlComboBoxOne(index, obj) {
	var data = "";
	var dataListSelected = $(obj).jqxComboBox('getSelectedItem');
    var id = $(obj).attr("id");
	if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
	 	if (dataListSelected != null) data += "&" + id + "=" + dataListSelected.value;
	}
	return data;
}
function getDataStrUrlComboBoxById(id) {
	var data = "";
	var dataListSelected = $("#" + id).jqxComboBox('getSelectedItems');
	if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
		for (var i = 0; i < dataListSelected.length; i++) {
			var item = dataListSelected[i];
		 	if (item != null) data += "&" + id + "=" + item.value;
		}
	}
	return data;
}
function isNotEmptyComboBoxById(id) {
	var count = 0;
	var dataListSelected = $("#" + id).jqxComboBox('getSelectedItems');
	if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
		for (var i = 0; i < dataListSelected.length; i++) {
			var item = dataListSelected[i];
		 	if (item != null) count++;
		}
	}
	if (count > 0) return true;
	return false;
}
function isNotEmptyComboBoxOneById(id) {
	var count = 0;
	var dataListSelected = $("#" + id).jqxComboBox('getSelectedItem');
	if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
		if (dataListSelected != null) count++;
	}
	if (count > 0) return true;
	return false;
}
function isNotEmptyComboBox(comboBox) {
	var count = 0;
	var dataListSelected = $(comboBox).jqxComboBox('getSelectedItems');
	if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
		for (var i = 0; i < dataListSelected.length; i++) {
			var item = dataListSelected[i];
		 	if (item != null) count++;
		}
	}
	if (count > 0) return true;
	return false;
}
function isNotEmptyComboBoxOne(comboBox) {
	var count = 0;
	var dataListSelected = $(comboBox).jqxComboBox('getSelectedItem');
	if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
		if (dataListSelected != null) count++;
	}
	if (count > 0) return true;
	return false;
}
var jSalesCommon = (function(){
	/*@param config = {
					width
					key
					value
					dropDownWidth
					autoDropDownHeight
					placeHolder
					-----------
					useUrl
					url
					root
					-----------
					selectedIndex
				} 
	*/
	var initDropDownList = function initDropDownList(downDownListObject, localData, config, selectArr){
		var widthCf = config.width ? config.width : "100%";
		var keyCf = config.key ? config.key : "id";
		var valueCf = config.value ? config.value : "description";
		var dropDownWidthCf = config.dropDownWidth ? config.dropDownWidth : "auto";
		var autoDropDownHeightCf = config.autoDropDownHeight ? config.autoDropDownHeight : false;
		var displayDetailCf = config.displayDetail ? config.displayDetail : false;
		var disabledCf = config.disabled ? config.disabled : false;
		var dataFieldsCf = config.datafields ? config.datafields : [{name: keyCf}, {name: valueCf}];
		var placeHolderCf = config.placeHolder ? config.placeHolder : "";
		var selectedIndexCf = config.selectedIndex != null ? config.selectedIndex : null;
		var useUrlCf = config.useUrl ? config.useUrl : false;
		var urlCf = config.url ? config.url : null;
		var rootCf = config.root ? config.root : "results";
		var sourceReason;
		if (useUrlCf && (urlCf != null)) {
			sourceReason = {
                datatype: "json",
                datafields: dataFieldsCf, 
                url: urlCf,
                async: false,
                root: rootCf,
            };
		} else {
			sourceReason = {
	        	localdata: localData,
		        datatype: "array"
		    };
		}
	    var dataPromoTypeAdapter = new $.jqx.dataAdapter(sourceReason);
	    $(downDownListObject).jqxDropDownList({
	    	source: dataPromoTypeAdapter, 
	    	width: widthCf, 
	    	displayMember: valueCf, 
	    	valueMember: keyCf, 
	    	disabled: disabledCf,
	    	autoDropDownHeight: autoDropDownHeightCf, 
	    	placeHolder: placeHolderCf,
	    	renderer: function (index, label, value) {
	            var valueStr;
	            if (displayDetailCf) {
	            	valueStr = label + " [" + value + "]";
	            } else {
	            	valueStr = label;
	            }
	            return valueStr;
	        }
	    });
	    if (selectedIndexCf != null) {
	    	$(downDownListObject).jqxDropDownList({selectedIndex : selectedIndexCf});
	    }
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(downDownListObject).jqxDropDownList('selectItem', item);
	    	}
		}
	}
	var initComboBox = function initComboBox(comboBoxObject, localData, config, selectArr){
		var widthCf = config.width ? config.width : "100%";
		var heightCf = config.height ? config.height : 25;
		var keyCf = config.key ? config.key : "id";
		var valueCf = config.value ? config.value : "description";
		var dropDownWidthCf = config.dropDownWidth ? config.dropDownWidth : "auto";
		var autoDropDownHeightCf = config.autoDropDownHeight ? config.autoDropDownHeight : false;
		var remoteAutoCompleteCf = config.remoteAutoComplete ? config.remoteAutoComplete : false;
		var displayDetailCf = config.displayDetail ? config.displayDetail : false;
		var disabledCf = config.disabled ? config.disabled : false;
		var dataFieldsCf = config.datafields ? config.datafields : [{name: keyCf}, {name: valueCf}];
		var placeHolderCf = config.placeHolder ? config.placeHolder : "";
		var multiSelectCf = config.multiSelect ? config.multiSelect : false;
		var selectedIndexCf = config.selectedIndex != null ? config.selectedIndex : null;
		var useUrlCf = config.useUrl ? config.useUrl : false;
		var urlCf = config.url ? config.url : null;
		var rootCf = config.root ? config.root : "results";
		
		var sourceInputEnum;
		if (useUrlCf && (urlCf != null)) {
			sourceInputEnum = {
                datatype: "json",
                datafields: dataFieldsCf, 
                url: urlCf,
                async: false,
                root: rootCf,
            };
		} else {
			sourceInputEnum = {
	        	localdata: localData,
		        datatype: "array",
		        datafields: dataFieldsCf, 
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
	    // selectedIndex: 0, 
	    $(comboBoxObject).jqxComboBox({
	    	source: dataAdapterInputEnum, 
	    	multiSelect: multiSelectCf, 
	    	width:widthCf, 
	    	height: heightCf,
	    	placeHolder: placeHolderCf, 
	    	displayMember: valueCf, 
	    	valueMember: keyCf, 
	    	dropDownWidth: dropDownWidthCf, 
	    	autoDropDownHeight: autoDropDownHeightCf,
	    	remoteAutoComplete: remoteAutoCompleteCf,
	    	disabled: disabledCf,
	    	renderer: function (index, label, value) {
	            var valueStr;
	            if (displayDetailCf) {
	            	valueStr = label + " [" + value + "]";
	            } else {
	            	valueStr = label;
	            }
	            return valueStr;
	        },
	        renderSelectedItem: function(index, item) {
	            var item = dataAdapterInputEnum.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
	        search: function (searchString) {
	            dataAdapterInputEnum.dataBind();
	        }
	    });
	    if (selectedIndexCf != null) {
	    	$(comboBoxObject).jqxComboBox({selectedIndex : selectedIndexCf});
	    }
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBoxObject).jqxComboBox('selectItem', item);
	    	}
		}
	}
	var initDropDownButton = function initDropDownButton(dropDownButtonObject, dropDownGridObject, localData, config, selectArr){
		var widthCf = config.width ? config.width : 600;
		var heightCf = config.height ? config.height : 'auto';
		var widthButtonCf = config.widthButton ? config.widthButton : 218;
		var heightButtonCf = config.heightButton ? config.heightButton : 25;
		var datatypeCf = config.datatype ? config.datatype : 'json';
		var sortcolumnCf = config.sortcolumn ? config.sortcolumn : '';
		var sortdirectionCf = config.sortdirection ? config.sortdirection : 'asc';
		var useUrlCf = config.useUrl ? config.useUrl : false;
		var urlCf = config.url ? config.url : null;
		var rootCf = config.root ? config.root : "results";
		var datafieldsCf = config.datafields ? config.datafields : [];
		var columnsCf = config.columns ? config.columns : [];
		var showdefaultloadelementCf = config.showdefaultloadelement ? config.showdefaultloadelement : false;
		var autoshowloadelementCf = config.autoshowloadelement ? config.autoshowloadelement : false;
		$(dropDownButtonObject).jqxDropDownButton({width: widthButtonCf, height: heightButtonCf});
		
		var sourceGrid;
		if (useUrlCf && (urlCf != null)) {
			sourceGrid = {
				datafields: datafieldsCf,
				cache: false,
				root: rootCf,
				datatype: datatypeCf,
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
					sourceGrid.totalrecords = data.TotalRows;
				},
				filter: function () {
					// update the grid and send a request to the server.
					$(dropDownGridObject).jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
					// callback called when a page or page size is changed.
				},
				sort: function () {
					$(dropDownGridObject).jqxGrid('updatebounddata');
				},
				sortcolumn: sortcolumnCf,
	           	sortdirection: sortdirectionCf,
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize: 5,
				contentType: 'application/x-www-form-urlencoded',
				url: urlCf,
			};
		}
		
		var dataAdapterGrid = new $.jqx.dataAdapter(sourceGrid, {
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
		
		$(dropDownGridObject).jqxGrid({
			width: widthCf, 
			height: heightCf,
			source: dataAdapterGrid,
			filterable: true,
			virtualmode: true, 
			showfilterrow: true,
			sortable:true,
			editable: false,
			autoheight:true,
			columnsresize: true,
			pageable: true,
			showdefaultloadelement: showdefaultloadelementCf,
			autoshowloadelement: autoshowloadelementCf,
			rendergridrows: function(obj) {	
				return obj.data;
			},
			columns: columnsCf
		});
	}
	var isNotEmpty = function isNotEmpty(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			return true;
		} else {
			return false;
		}
	}
	var addZero = function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
	var formatFullDate = function formatFullDate(value) {
		if (jSalesCommon.isNotEmpty(value)) {
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
	}
	return {
		initDropDownList: initDropDownList,
		initComboBox: initComboBox,
		initDropDownButton: initDropDownButton,
		isNotEmpty: isNotEmpty,
		formatFullDate: formatFullDate
	};
}());
function initComboBoxEnum(comboBox, selectArr, localData, placeHolder, config){
	var widthCf = config.width ? config.width : "100%";
	var keyCf = config.key ? config.key : "enumId"; // key config
	var valueCf = config.value ? config.value : "description";
	var dropDownWidthCf = config.dropDownWidth ? config.dropDownWidth : "auto";
	var autoDropDownHeightCf = config.autoDropDownHeight ? config.autoDropDownHeight : false;
	var remoteAutoCompleteCf = config.remoteAutoComplete ? config.remoteAutoComplete : false;
	var displayDetailCf = config.displayDetail ? config.displayDetail : false;
	var disabledCf = config.disabled ? config.disabled : false;
	var sourceInputEnum = {
		localdata: localData,
        datatype: "array",
        datafields: [
            {name: keyCf},
            {name: 'abbreviation'},
            {name: valueCf}
        ]
    };
    var dataAdapterInputEnum = new $.jqx.dataAdapter(sourceInputEnum, {
        	formatData: function (data) {
                if ($(comboBox).jqxComboBox('searchString') != undefined) {
                    data.searchKey = $(comboBox).jqxComboBox('searchString');
                    return data;
                }
            }
        }
    );
    // selectedIndex: 0, 
    $(comboBox).jqxComboBox({
    	source: dataAdapterInputEnum, 
    	multiSelect: false, 
    	width:widthCf, 
    	height: 25,
    	placeHolder: placeHolder, 
    	displayMember: valueCf, 
    	valueMember: keyCf, 
    	dropDownWidth: dropDownWidthCf, 
    	autoDropDownHeight: autoDropDownHeightCf,
    	remoteAutoComplete: remoteAutoCompleteCf,
    	disabled: disabledCf,
    	renderer: function (index, label, value) {
            var valueStr;
            if (displayDetailCf) {
            	valueStr = label + " [" + value + "]";
            } else {
            	valueStr = label;
            }
            return valueStr;
        },
        renderSelectedItem: function(index, item) {
            var item = dataAdapterInputEnum.records[index];
            if (item != null) {
                var label = item.description;
                return label;
            }
            return "";
        },
        search: function (searchString) {
            dataAdapterInputEnum.dataBind();
        }
    });
    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
    	for (var i = 0; i < selectArr.length; i++) {
    		var item = selectArr[i];
    		$(comboBox).jqxComboBox('selectItem', item);
    	}
	}
}