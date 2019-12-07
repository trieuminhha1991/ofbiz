$(document).ready(function() {
	ObjQuotas.init();
});
var ObjQuotas = (function() {
	var grid = $("#jqxGridQuotaHeaders");
	var validatorVAL = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidate();
	};
	
	var initInput = function() { 
		
		$("#jqxContextMenu").jqxMenu({ width: 320, autoOpenPopup: false, mode: 'popup', theme: theme});
		
	}
	
	var initElementComplex = function() {
		initGridQuotas(grid);
	}
	
	var initGridQuotas = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.BIEQuotaId, dataField: 'quotaCode', width: 130, 
				cellsrenderer: function (row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span><a href="javascript:ObjQuotas.showDetailQuota('+data.quotaId+')"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.BIEQuotaName, dataField: 'quotaName', width: 150, },
			
			{ text: uiLabelMap.Status, dataField: 'statusId', width: 250,  filtertype: 'checkedlist',
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
			
//			{ text: uiLabelMap.POSupplierId, dataField: 'partyCode', width: 150, },
//			{ text: uiLabelMap.POSupplierName, dataField: 'fullName', width: 150, },
			{ text: uiLabelMap.CreatedDate, dataField: 'createdDate', editable: false, align: 'left', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', cellsalign: 'right',
			},
			{ text: uiLabelMap.CreatedBy, dataField: 'createdByUserLogin', width: 150, },
//			{ text: uiLabelMap.AccountingCurrency, dataField: 'currencyUomId', width: 90, },
			{ text: uiLabelMap.Description, dataField: 'description', minwidth: 100, },
        ];
		
		var datafield = [
         	{ name: 'quotaId', type: 'string'},
         	{ name: 'quotaCode', type: 'string'},
         	{ name: 'quotaName', type: 'string'},
//         	{ name: 'supplierPartyId', type: 'string'},
//         	{ name: 'partyCode', type: 'string'},
//         	{ name: 'fullName', type: 'string'},
         	{ name: 'statusId', type: 'string'},
         	{ name: 'quotaTypeId', type: 'string'},
         	{ name: 'currencyUomId', type: 'string'},
         	{ name: 'description', type: 'string'},
         	{ name: 'createdByUserLogin', type: 'string'},
			{ name: 'createdDate', type: 'date', other: 'Timestamp'},
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "Container";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.BIEListQuotaHeaders + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        if (ObjAddQuota){
	        	var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjAddQuota.openPopupAdd()";
		        Grid.createCustomControlButton(grid, container, customcontrol1);
	        }
		}; 
		
		var url = "jqGetQuotaHeaders";
		if (quotaTypeId){
			url = "jqGetQuotaHeaders&quotaTypeId=" + quotaTypeId;
		}
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
	        url: url,                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#jqxContextMenu"), false);
	}
	

	var initEvents = function() {
		
		$("#jqxContextMenu").on('itemclick', function (event) {
			var liId = event.args.id;
			var data = grid.jqxGrid('getRowData', grid.jqxGrid('selectedrowindexes'));
			
			if (ObjAddQuota != undefined){
				if (liId == "addQuotaHeader"){
					ObjAddQuota.openPopupAdd();
				}
			}
			
			if (ObjEditQuota != undefined){
				if (liId == "editQuotaHeader"){
					ObjEditQuota.openPopupEdit(data);
				}
			}
			
			if (liId == "refreshGrid"){
				grid.jqxGrid('updatebounddata');
			}
		});
		
	}
	
	var initValidate = function() {
	}
	
	var showDetailQuota = function(quotaId) {
		location.href = "getDetailQuotaHeaders?quotaId=" + quotaId;
	}
	var updateGridData = function() {
		grid.jqxGrid('updatebounddata');
	}
	
	return {
		init : init,
		updateGridData: updateGridData,
		showDetailQuota: showDetailQuota,
	}
}());