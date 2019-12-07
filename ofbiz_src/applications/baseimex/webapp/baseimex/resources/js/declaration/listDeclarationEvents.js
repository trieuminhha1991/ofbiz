$(document).ready(function() {
	ObjPrEve.init();
});
var ObjPrEve = (function() {
	var grid = $("#jqxGridProductEvent");
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidateForm();

	};
	
	var initInput = function() { 
		$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	}
	
	var initElementComplex = function() {
		initGridProductEvent(grid);
	}
	
	var initGridProductEvent = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,cellclassname: cellClassDeclaration, 
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.CommonCode, dataField: 'eventCode', width: '10%', editable: false, pinned: true, cellclassname: cellClassDeclaration,
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					if (!value) value = data.eventId;
					return '<span><a href="javascript:ObjPrEve.showDetailEvent('+data.eventId+')\"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.Name, dataField: 'eventName', minwidth: 200, editable:false, cellclassname: cellClassDeclaration,},
			{ text: uiLabelMap.Status, dataField: 'statusId', width: '10%',  filtertype: 'checkedlist', cellclassname: cellClassDeclaration,
				cellsrenderer: function (row, column, value) {
					return '<span>' + getStatusDesc(value) +'</span>';
			    },
			    createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
						renderer: function(index, label, value){
				        	if (statusData.length > 0) {
				        		return getStatusDesc(value);
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			{ text: uiLabelMap.BIETestEventType, dataField: 'eventTypeId', width: '10%', editable:false, filtertype: 'checkedlist', cellclassname: cellClassDeclaration,
				cellsrenderer: function (row, column, value) {
					return '<span>' + getTypeDesc(value) +'</span>';
			    },
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(productEventTypeData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'eventTypeId', valueMember: 'eventTypeId',
						renderer: function(index, label, value){
				        	if (productEventTypeData.length > 0) {
				        		return getTypeDesc(value);
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			{ text: uiLabelMap.CreatedDate, dataField: 'createdDate', editable: false, align: 'left', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', cellclassname: cellClassDeclaration,
			},
			{ text: uiLabelMap.BIEExecutedDate, dataField: 'executedDate', editable: false, align: 'left', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', cellclassname: cellClassDeclaration,
			},
			{ text: uiLabelMap.BIECompletedDate, dataField: 'completedDate', editable: false, align: 'left', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', cellclassname: cellClassDeclaration,
			},
			{ text: uiLabelMap.Description, dataField: 'description', width: '10%', editable:false, cellclassname: cellClassDeclaration,},
			{ text: uiLabelMap.CreatedBy, dataField: 'createdByUserLogin', width: '10%', editable:false, cellclassname: cellClassDeclaration,},
        ];
		
		var datafield = [
         	{ name: 'eventId', type: 'string'},
         	{ name: 'eventCode', type: 'string'},
         	{ name: 'statusId', type: 'string'},
         	{ name: 'eventName', type: 'string'},
			{ name: 'eventTypeId', type: 'string'},
			{ name: 'organizationPartyId', type: 'string'},
			{ name: 'packingListNumber', type: 'string'},
			{ name: 'fullName', type: 'string'},
			{ name: 'agreementId', type: 'string'},
			{ name: 'packingListId', type: 'string'},
			{ name: 'description', type: 'string'},
			{ name: 'createdByUserLogin', type: 'string'},
			{ name: 'createdDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'executedDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'completedDate', type: 'date', other: 'Timestamp'},
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "ProductEvent";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.BIEListDeclarations + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjPrEve.openScreenAdd()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
		}; 
		
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: 'jqGetProductEvents&parentEventTypeId=' + parentEventTypeId,                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#contextMenu"), false);
	}
	var initEvents = function() {
		
		$("#contextMenu").on('itemclick', function (event) {
			var data = grid.jqxGrid('getRowData', grid.jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			facilitySelectedId = data.facilityId;
			if(tmpStr == uiLabelMap.AddNew){
				openScreenAdd(data.partyId, data.currencyUomId);
			}
			if(tmpStr == uiLabelMap.Edit){
				if (data.statusId == 'PRODUCT_EVENT_CREATED') {
					ObjEditEve.openPopupEdit(data);
				} else {
					jOlbUtil.alert.error(uiLabelMap.BIECommonCannotEdit);
				}
			}
			if(tmpStr == uiLabelMap.BSRefresh){
				grid.jqxGrid('updatebounddata');
			}
		});
		
	}
	var cellClassDeclaration = function (row, columnfield, value) {
 		var data = $('#jqxGridProductEvent').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("PRODUCT_EVENT_CANCELLED" == data.statusId) {
 				return "background-cancel-nd";
 			} else if ("PRODUCT_EVENT_CREATED" == data.statusId) {
 				return "background-prepare";
 			} else if ("PRODUCT_EVENT_APPROVED" == data.statusId) {
 				return "background-running";
 			}
 		}
    }
	
	var initValidateForm = function(){
		var extendRules = [];
   		var mapRules = [];
	};
	
	
	var showDetailEvent = function(eventId) {
		location.href = "getDetailDeclarationEvent?eventId=" + eventId;
	}
	
    var openScreenAdd = function (){
    	location.href = "newDeclarationQualityEvent?parentEventTypeId=QUALITY_EVENT";
    }
    
	return {
		init : init,
		openScreenAdd: openScreenAdd,
		showDetailEvent: showDetailEvent,
	}
}());