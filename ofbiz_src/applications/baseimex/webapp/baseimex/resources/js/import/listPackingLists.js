$(document).ready(function() {
	ObjPack.init();
});
var ObjPack = (function() {
	var agreementSelected = null;
	var containerSelected = null;
	var listProductSelected = null;
	var gridFacility = $("#gridFacility");
	var grid = $("#jqxGridPackingLists");
	var gridBOL = $("#jqxGridBOL");
	var gridContainer = $("#jqxGridContainer");
	var popupWindowAddNewPackingList = $("#popupWindowAddNewPackingList");
	var validatorVAL = null;
	var gridProduct = $('#jqxgridPackingListDetail');
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidator();
	};
	
	var initGridProduct = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
	        	  groupable: false, draggable: false, resizable: false,
	        	  datafield: '', columntype: 'number', width: 50,
	        	  cellsrenderer: function (row, column, value) {
	        		  return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
	        	  }
	      	},
			{ text: uiLabelMap.ProductId, datafield: 'productCode', editable: false, width: 120,
				cellsrenderer: function(row, colum, value){
				},
			},
			{ text: uiLabelMap.ProductName, datafield: 'productName', editable: false, minwidth: 150,
				cellsrenderer: function(row, colum, value){
				},
			},
			{ text: uiLabelMap.globalTradeItemNumber, datafield: 'globalTradeItemNumber', width: 120, editable: true, cellclassname: productGridCellclass,},
			{ text: uiLabelMap.batchNumber, datafield: 'batchNumber', width: 120, editable: true , cellclassname: productGridCellclass,},
			{ text: uiLabelMap.packingUnits, datafield: 'packingUnit', width: 120, editable: true, columntype:'numberinput', cellsalign: 'right', cellclassname: productGridCellclass,
				cellsrenderer: function(row, colum, value){
	     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.packingUomId, hidden: true, dataField: 'packingUomId', width: 80, editable: true},
			{ text: uiLabelMap.orderUnits, datafield: 'orderUnit', width: 120, editable: true, columntype:'numberinput', cellsalign: 'right', cellclassname: productGridCellclass,
				cellsrenderer: function(row, colum, value){
		     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.orderUomId, hidden: true, dataField: 'orderUomId', width: 80, editable: true},
			{ text: uiLabelMap.originOrderUnit, datafield: 'originOrderUnit', width: 120, editable: false, cellsalign: 'right', 
				cellsrenderer: function(row, colum, value){
		     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.dateOfManufacture, datafield: 'datetimeManufactured', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',  cellclassname: productGridCellclass,
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
	        		var uid = gridProduct.jqxGrid('getrowid', row);
	        		editor.on('change', function(event){
	        			var jsDate = event.args.date;
	        		});
				}
			},
			{ text: uiLabelMap.ProductExpireDate, datafield: 'expireDate', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',  cellclassname: productGridCellclass,
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
	        		var uid = gridProduct.jqxGrid('getrowid', row);
	        		var datemanu = gridProduct.jqxGrid('getcellvaluebyid', uid, 'datetimeManufactured');
	        		if(datemanu && datemanu != null && datemanu != ''){
	        			editor.jqxDateTimeInput('setMinDate', datemanu);
	        		}else{
	        			
	        		}
				},
				validation: function (cell, value) {
					var uid = gridProduct.jqxGrid('getrowid', cell.row);
	        		var datemanu = gridProduct.jqxGrid('getcellvaluebyid', uid, 'datetimeManufactured');
			        if ((!value || value == null || value == '') && (datemanu && datemanu != null && datemanu != '')) {
			        	return { result: false};
			        }
			        return true;
			    }
			}
        ];
		
		var datafield = [
		 	{ name: 'productId', type: 'string'},
		 	{ name: 'productCode', type: 'string'},
		 	{ name: 'productName', type: 'string'},
			{ name: 'packingListId', type: 'string'},
			{ name: 'packingListSeqId', type: 'string'},
			{ name: 'orderItemSeqId', type: 'string'},
			{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
			{ name: 'expireDate', type: 'date', other: 'Timestamp'},
			{ name: 'batchNumber', type: 'string'},
			{ name: 'globalTradeItemNumber', type: 'string'},
			{ name: 'packingUnit', type: 'number'},
			{ name: 'packingUomId', type: 'string'},
			{ name: 'orderUnit', type: 'number'},
			{ name: 'originOrderUnit', type: 'number'},
			{ name: 'orderUomId', type: 'string'},
			{ name: 'quantity', type: 'number'},
			{ name: 'itemDescription', type: 'string'}
			]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "Container";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.Product + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-trash open-sans@" + uiLabelMap.CommonDelete + "@javascript:ContainerManager.btnRemoveRowDetail()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
		}; 
		
		var config = {
			width: '100%', 
	   		virtualmode: false,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		editmode: 'click',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: true,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: false,
	        url: "",                
	        source: {pagesize: 10}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	var initInput = function() { 
		$("#billOfLadingPacking").jqxDropDownButton({width: 200, theme: theme});
		$('#billOfLadingPacking').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		if (billSelected != null){
			$('#billOfLadingPacking').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+billSelected.billNumber+'</div>');
			$("#billOfLadingPacking").jqxDropDownButton({disabled: true});
		}

		$("#facility").jqxDropDownButton({width: 200, theme: theme, dropDownHorizontalAlignment: 'right'});
		$('#facility').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#container").jqxDropDownButton({width: 200, theme: theme});
		$('#container').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#orderNumberSupp").jqxInput({
			height : '22px',
			width : '195px',
			minLength : 1,
			theme : theme
		});
		$("#invoiceNumber").jqxInput({
			height : '22px',
			width : '195px',
			minLength : 1,
			theme : theme
		});
		$("#packingListNumber").jqxInput({
			height : '22px',
			width : '195px',
			minLength : 1,
			theme : theme
		});

		$("#totalNetWeight").jqxNumberInput({
			width : '200px',
			height : '25px',
			spinButtons : true,
			theme : theme
		});
		$("#totalGrossWeight").jqxNumberInput({
			width : '200px',
			height : '25px',
			spinButtons : true,
			theme : theme
		});

		$("#packingListDate").jqxDateTimeInput({
			width : '200px',
			theme : theme
		});
		$("#invoiceDate").jqxDateTimeInput({
			width : '200px',
			theme : theme
		});
		$('#packingListDate').jqxDateTimeInput('clear');
		$('#invoiceDate').jqxDateTimeInput('clear');

		$("#orderTypeSupp").jqxComboBox({
			displayMember : 'externalOrderTypeName',
			valueMember : 'externalOrderTypeId',
			autoDropDownHeight : true,
			width : '200px',
			theme : theme,
			searchMode : 'containsignorecase',
			autoOpen : false,
			autoComplete : false
		});

		$("#agreementId").jqxComboBox({
			displayMember : 'agreementCode',
			valueMember : 'agreementId',
			width : '200px',
			searchMode : 'containsignorecase',
			theme : theme,
			autoOpen : false,
			autoComplete : false
		});
		
		popupWindowAddNewPackingList.jqxWindow({
			maxWidth : 1200,
			maxHeight : 900,
			minWidth : 800,
			width : 1200,
			minHeight : 500,
			height : 640,
			resizable : false,
			cancelButton : $("#alterCancel"),
			keyboardNavigation : true,
			keyboardCloseKey : 15,
			isModal : true,
			autoOpen : false,
			modalOpacity : 0.7,
			theme : theme
		});
		
		$('#jqxMenu').jqxMenu({ width: '300px', autoOpenPopup: false, mode: 'popup', theme: theme});
	}
	
	var productGridCellclass = function (row, column, value, data) {
		return 'background-prepare';
	}
	
	var initElementComplex = function() {
		initGridPackingList(grid);
		initGridProduct(gridProduct);
		initFacilityGrid(gridFacility);
		if (!billSelected){
			initGridBillOfLadingPacking(gridBOL);
		}
		initGridContainers(gridContainer);
	}
	
	var initFacilityGrid = function(grid){
		var url = "jqGetFacilities&primaryFacilityGroupId=FACILITY_INTERNAL";
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
			];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				}
			},
			{text: uiLabelMap.FacilityId, datafield: 'facilityCode', width: '150',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
			{text: uiLabelMap.FacilityName, datafield: 'facilityName', minwidth: '200',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
			];
		
		var config = {
				width: 450, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				useUrl: true,
				url: url,                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	
	var initGridPackingList = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.BIEPackingListId, dataField: 'packingListNumber', width: 120,
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span><a href="javascript:ObjPack.showDetailPackingList('+data.packingListId+')\"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.BIEAgreementId, dataField: 'agreementId', width: 120,
				cellsrenderer: function(row, column, value) {
					return '<span><a href="javascript:ObjPack.showDetailAgreement('+value+')\"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.BIEBillId, dataField: 'billNumber', width: 120,
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span><a href="javascript:ObjPack.showDetailBill('+data.billId+')\"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.BIEContainerId, dataField: 'containerNumber', width: 120,
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span><a href="javascript:ObjPack.showDetailContainer('+data.containerId+')\"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.OrderPO, dataField: 'purchaseOrderId', width: 120,
				cellsrenderer: function(row, column, value) {
					return '<span><a href="javascript:ObjPack.showDetailOrder('+value+')\"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.BIEVendorInvoiceNum, dataField: 'externalInvoiceNumber', width: 130,},
			{ text: uiLabelMap.BIEVendorOrderNum, dataField: 'externalOrderNumber', width: 130,},
			{ text: uiLabelMap.BIESealNumber, dataField: 'sealNumber', width: 120,},
			{ text: uiLabelMap.BIENetWeight, dataField: 'netWeightTotal', width: 120,
				cellsrenderer: function(row, column, value) {
					return '<span class="align-right">' + formatnumber(value)  + '</span>';
				}
			},
			{ text: uiLabelMap.BIEGrossWeight, dataField: 'grossWeightTotal', width: 120,
				cellsrenderer: function(row, column, value) {
					return '<span class="align-right">' + formatnumber(value) + '</span>';
				}
			},
			{ text: uiLabelMap.BIEPackingListDate, dataField: 'packingListDate', editable: false, align: 'left', width: 120, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.BIEInvoiceDate, dataField: 'externalInvoiceDate', editable: false, align: 'left', width: 120, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.BIEDepartureDate, dataField: 'departureDate', editable: false, align: 'left', width: 120, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.BIEArrivalDate, dataField: 'arrivalDate', editable: false, align: 'left', width: 120, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.Description, dataField: 'description', minwidth: 100,},
        ];
		
		var datafield = [
         	{ name: 'packingListId', type: 'string'},
         	{ name: 'packingListNumber', type: 'string'},
         	{ name: 'externalInvoiceNumber', type: 'string'},
         	{ name: 'externalOrderNumber', type: 'string'},
			{ name: 'externalOrderTypeId', type: 'string'},
			{ name: 'description', type: 'string'},
			{ name: 'containerId', type: 'string'},
			{ name: 'sealNumber', type: 'string'},
			{ name: 'billId', type: 'string'},
			{ name: 'billNumber', type: 'string'},
			{ name: 'containerNumber', type: 'string'},
			{ name: 'purchaseOrderId', type: 'string'},
			{ name: 'agreementId', type: 'string'},
			{ name: 'netWeightTotal', type: 'number'},
			{ name: 'grossWeightTotal', type: 'number'},
			{ name: 'packingListDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'externalInvoiceDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'departureDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "PackingList";
			var me = this;
			var jqxheader = $("<div id='toolbarpackinglist" + id +"' class='widget-header'><h4>" + uiLabelMap.BIEPackingList + "</h4><div id='toolbarButtonPackingList" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonPackingList' + id);
	        var maincontainer = $("#toolbarpackinglist" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjPack.openPopupAdd()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
		}; 
		
		var url = "jqGetPackingLists";
		if (containerParamId != null){
			url = "jqGetPackingLists&containerId=" + containerParamId;
		}
		if (billId != null){
			url = "jqGetPackingLists&billId=" + billId;
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
		Grid.createContextMenu(grid, $("#jqxMenu"), false);
	}
	
	var initGridContainers = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.BIEContainerNumber, dataField: 'containerNumber', width: 120, 
				cellsrenderer: function (row, column, value) {
				}
			},
			{ text: uiLabelMap.BIEContainerType, dataField: 'containerTypeId', width: 150,  filtertype: 'checkedlist',
				cellsrenderer: function (row, column, value) {
					return '<span>' + getContainerTypeDesc(value) +'</span>';
			    },
			    createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(containerTypeData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'containerTypeId', valueMember: 'containerTypeId',
						renderer: function(index, label, value){
				        	if (containerTypeData.length > 0) {
				        		return getContainerTypeDesc(value);
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			{ text: uiLabelMap.BIESealNumber, dataField: 'sealNumber', width: 120, 
			},
			{ text: uiLabelMap.Description, dataField: 'description', minwidth: 100, },
        ];
		
		var datafield = [
         	{ name: 'containerId', type: 'string'},
         	{ name: 'containerNumber', type: 'string'},
         	{ name: 'containerTypeId', type: 'string'},
         	{ name: 'description', type: 'string'},
			{ name: 'billNumber', type: 'string'},
			{ name: 'sealNumber', type: 'string'},
			{ name: 'billId', type: 'string'},
			{ name: 'departureDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
		 	]
		
		var url = '';
		if (billSelected != null){
			url = 'jqGetContainers&billId=' + billSelected.billId;
		}
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: false,
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
	}
	
	var initGridBillOfLadingPacking = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},		
			{ text: uiLabelMap.BIEBillId, dataField: 'billNumber', minwidth: 100, 
				cellsrenderer: function(row, column, value) {
				}
			},
			{ text: uiLabelMap.BIEDepartureDate, dataField: 'departureDate', editable: false, align: 'left', width: 140, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.BIEArrivalDate, dataField: 'arrivalDate', editable: false, align: 'left', width: 140, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			];
		
		var datafield = [
			{ name: 'billNumber', type: 'string'},
			{ name: 'billId', type: 'string'},
			{ name: 'departureDate', type: 'date', other: 'Timestamp'},
			{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
			]
		
		var config = {
				width: '100%', 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'jqGetBillOfLading',                
				source: {pagesize: 15}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	var initEvents = function() {
		
		$('#agreementId').on('select', function(event) {
			var args = event.args;
			if (args) {
				var index = args.index;
				var item = args.item;
				if (item && item.originalItem){
					agreementSelected = $.extend({}, item.originalItem);
				}
			}
		});
		
		$('#orderTypeSupp').on('select', function(event) {
			var args = event.args;
			if (args) {
				var index = args.index;
				var item = args.item;
				if (item.value == "ORIGINAL" && agreementSelected != null && agreementSelected.agreementId) {
					getDataProductFromAgreement(agreementSelected.agreementId);
				}
			}
		});
		
		gridBOL.on('rowclick', function (event) {
	        var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        var rowData = gridBOL.jqxGrid('getrowdata', rowBoundIndex);
	        billSelected = {};
	        billSelected = $.extend({}, rowData);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.billNumber +'</div>';
	        $('#billOfLadingPacking').jqxDropDownButton('setContent', dropDownContent);
	        $("#billOfLadingPacking").jqxDropDownButton('close');
	        
	        var tmpS = gridContainer.jqxGrid('source');
			tmpS._source.url = "jqxGeneralServicer?sname=jqGetContainers&billId=" + billSelected.billId;
			gridContainer.jqxGrid('source', tmpS);
			gridContainer.jqxGrid('updatebounddata');
			
			jQuery.ajax({
		        url: "getExternalOrderType",
		        type: "POST",
		        async: false,
		        data: {},
		        dataType: 'json',
		        success: function(res){
		        	$("#agreementId").jqxComboBox({
		        		source: res.listAgreementNotBill
		        	});
		        	$("#orderTypeSupp").jqxComboBox({
		    			source: res.listOrderType
		        	});
		        }
		    });
	    });
		
		gridBOL.on('bindingcomplete', function (event) {
			if (billSelected != null){
				var rows = gridBOL.jqxGrid('getrows');
				if (rows && rows.length > 0){
					for (var i in rows){
						if (rows[i].billId == billSelected.billId){
							var index = gridBOL.jqxGrid('getrowboundindexbyid', rows[i].uid);
							gridBOL.jqxGrid('selectrow', index);
							break;
						}
					}
				}
			}
		});
		
		gridContainer.on('rowclick', function (event) {
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			var rowData = gridContainer.jqxGrid('getrowdata', rowBoundIndex);
			containerSelected = {};
			containerSelected = $.extend({}, rowData);
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.containerNumber +'</div>';
			$('#container').jqxDropDownButton('setContent', dropDownContent);
			$("#container").jqxDropDownButton('close');
		});
		
		gridContainer.on('bindingcomplete', function (event) {
			if (containerSelected != null){
				var rows = gridContainer.jqxGrid('getrows');
				if (rows && rows.length > 0){
					for (var i in rows){
						if (rows[i].containerId == containerSelected.containerId){
							var index = gridContainer.jqxGrid('getrowboundindexbyid', rows[i].uid);
							gridContainer.jqxGrid('selectrow', index);
							break;
						}
					}
				}
			}
		});
		
		popupWindowAddNewPackingList.on('close', function (event) {
			$('#packingListNumber').jqxInput('val', null);
			$('#orderTypeSupp').jqxComboBox('clearSelection');
			$('#orderNumberSupp').jqxInput('val', null);
			$('#invoiceNumber').jqxInput('val', null);
			$('#totalNetWeight').jqxNumberInput('val', 0);
			$('#totalGrossWeight').jqxNumberInput('val', 0);
			$('#packingListDate').jqxDateTimeInput('clear');
			$('#invoiceDate').jqxDateTimeInput('clear');
		});
		
		$("#alterSave").on('click', function(){
			if (!validatorVAL.validate()){
				return false;
			}
			bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
	        {"label": uiLabelMap.OK,
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	Loading.show('loadingMacro');
			    	setTimeout(function(){
			    		var data = getData();
			    		$.ajax({
			    			url : "createContainerAndPackingList",
			    			type : "POST",
			    			data : data,
			    			async : false,
			    			success : function(res) {
			    				if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    					if (res._ERROR_MESSAGE_){
			    						jOlbUtil.alert.error(res._ERROR_MESSAGE_);
			    					}
			    					if (res._ERROR_MESSAGE_LIST_){
			    						jOlbUtil.alert.error(res._ERROR_MESSAGE_LIST_[0]);
			    					}
			    					return false;
			    				} else {
			    					grid.jqxGrid('updatebounddata');
			    					popupWindowAddNewPackingList.jqxWindow('close');
			    				}
			    			}
			    		});
			    		
			    		Loading.hide('loadingMacro');
			    	}, 500);
	            }
	        }]);
		});
		
		gridProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				$.each(listProductSelected, function(i){
	   				var olb = listProductSelected[i];
	   				if (olb.productId == rowData.productId){
	   					listProductSelected.splice(i,1);
	   					return false;
	   				}
	   			});
				if ((dataField == 'orderUnit' && value > 0) || (dataField != 'orderUnit' && rowData.orderUnit > 0) ){
					var item = $.extend({}, rowData);
					item[dataField] = value;
					listProductSelected.push(item);
				}
			}
		});
		
		gridFacility.on('rowselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			facilitySelected = $.extend({}, rowData);
			var description = uiLabelMap.PleaseSelectTitle; 
			if (facilitySelected) {
				description = facilitySelected.facilityName;
			}
			
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#facility').jqxDropDownButton('setContent', dropDownContent);
		});
		
		gridFacility.on('rowdoubleclick', function (event) { 
			$('#facility').jqxDropDownButton('close');
		});
		
		gridFacility.on('bindingcomplete', function (event) {
			if (facilitySelected != null){
				var rows = gridFacility.jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						var index = gridFacility.jqxGrid('getrowboundindexbyid', data1.uid);
						if (data1.facilityId == facilitySelected.facilityId){
							gridFacility.jqxGrid('selectrow', index);
						} else {
							gridFacility.jqxGrid('unselectrow', index);
						}
					}
				}
			}
		});
		
		$("#jqxMenu").on('itemclick', function (event) {
			var liId = event.args.id;
			if (liId == "refreshGridPKL"){
				grid.jqxGrid('updatebounddata');
			}
		});
	}
	
	function getData() {
		var billId = billSelected.billId;
		var containerId = containerSelected.containerId;
		var packingListNumber = $('#packingListNumber').jqxInput('val');
		var orderNumberSupp = $('#orderNumberSupp').jqxInput('val');
		var invoiceNumber = $('#invoiceNumber').jqxInput('val');
		var totalNetWeight = $('#totalNetWeight').jqxNumberInput('val');
		totalNetWeight = totalNetWeight.toString();
		var totalGrossWeight = $('#totalGrossWeight').jqxNumberInput('val');
		totalGrossWeight = totalGrossWeight.toString();
		
		var packingListDate = $('#packingListDate').jqxDateTimeInput('getDate')
				.getTime();
		var invoiceDate = $('#invoiceDate').jqxDateTimeInput('getDate')
				.getTime();
		var orderTypeSuppId = "";
		var valueCombo = $('#orderTypeSupp').jqxComboBox('getSelectedItem');
		if (valueCombo) {
			orderTypeSuppId = valueCombo.value;
		}
		var destFacilityId = null;
		if (facilitySelected) {
			destFacilityId = facilitySelected.facilityId;
		}
		var gridDetailId = $('#gridDetailId').val();
		var dataJson = {
			agreementId : agreementSelected.agreementId,
			containerId : containerId,
			packingListNumber : packingListNumber,
			orderNumberSupp : orderNumberSupp,
			invoiceNumber : invoiceNumber,
			totalNetWeight : totalNetWeight,
			totalGrossWeight : totalGrossWeight,
			packingListDate : packingListDate,
			invoiceDate : invoiceDate,
			destFacilityId : destFacilityId,
			orderTypeSuppId : orderTypeSuppId,
			billId : billId,
		};
		
		var listProducts = [];
		for (var i in listProductSelected){
			var map = listProductSelected[i];
			var obj = {};
			obj.orderId = map.orderId;
			obj.orderItemSeqId = map.orderItemSeqId;
			obj.productId = map.productId;
			obj.batchNumber = map.batchNumber;
			obj.globalTradeItemNumber = map.globalTradeItemNumber;
			obj.orderUnit = map.orderUnit;
			obj.packingUnit = map.packingUnit;
			if (map.datetimeManufactured){
				var x = new Date(map.datetimeManufactured);
				obj.datetimeManufactured = x.getTime();
			}
			if (map.expireDate){
				var x = new Date(map.expireDate);
				obj.expireDate = x.getTime();
			}
			obj.originOrderUnit = map.originOrderUnit;
			listProducts.push(obj);
		}
		
		var data = {
			packingList : JSON.stringify(dataJson),
			packingListDetail : JSON.stringify(listProducts)
		};
		
		return data;
	}
	
	var showDetailPackingList = function(packingListId) {
		location.href = "viewDetailPackingList?packingListId=" + packingListId;
	}
	
	var showDetailAgreement = function(agreementId) {
		location.href = "detailPurchaseAgreement?agreementId=" + agreementId;
	}
	
	var showDetailOrder = function(orderId) {
		location.href = "viewDetailPO?orderId=" + orderId;
	}
	
	var showDetailContainer = function(containerId) {
		location.href = "viewDetailContainer?containerId=" + containerId;
	}
	
	var showDetailBill = function(billId) {
		location.href = "viewDetailBillOfLading?billId=" + billId;
	}
	
	var openPopupAdd = function() {
		jQuery.ajax({
	        url: "getExternalOrderType",
	        type: "POST",
	        async: false,
	        data: {},
	        dataType: 'json',
	        success: function(res){
	        	$("#agreementId").jqxComboBox({
	        		source: res.listAgreementNotBill
	        	});
	        	$("#orderTypeSupp").jqxComboBox({
	    			source: res.listOrderType
	        	});
	        }
	    });
		gridContainer.jqxGrid('updatebounddata');
		popupWindowAddNewPackingList.jqxWindow('open');
	}
	
	var initValidator = function() {
		var extendRules = [
			{
				input: '#billOfLadingPacking', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	if (billSelected == null){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#container', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (containerSelected == null){
						return false;
					}
					return true;
				}
			},
			{
				input: '#facility', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (facilitySelected == null){
						return false;
					}
					return true;
				}
			},
		];
   		var mapRules = [
			{input: '#agreementId', type: 'validInputNotNull'},
			{input: '#packingListNumber', type: 'validInputNotNull'},
			{input: '#invoiceNumber', type: 'validInputNotNull'},
			{input: '#orderNumberSupp', type: 'validInputNotNull'},
			{input: '#packingListDate', type: 'validInputNotNull'},
			{input: '#invoiceDate', type: 'validInputNotNull'},
			{input: '#orderTypeSupp', type: 'validInputNotNull'},
			{input: '#totalNetWeight', type: 'validInputNotNull'},
			{input: '#totalGrossWeight', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator($("#popupWindowAddNewPackingList"), mapRules, extendRules, {position: 'right'});
	};
	
	var getDataProductFromAgreement = function (agreementId){
		$.ajax({
	        url: "getOrderItemByAgreements",
	        type: "POST",
	        async: false,
	        data: {agreementId: agreementId},
	        dataType: 'json',
	        success: function(res){
	        	if (res.listProducts){
	        		listProductSelected = res.listProducts;
	        		if (listProductSelected && listProductSelected.length > 0){
	        			updateProductGridData(listProductSelected);
	        		}
	        	}
	        }
	    });
	}
	
	var updateProductGridData = function(data){
		var tmpS = gridProduct.jqxGrid("source");
		tmpS._source.localdata = data;
		gridProduct.jqxGrid("source", tmpS);
		gridProduct.jqxGrid("updatebounddata");
	}
	
	return {
		init : init,
		showDetailPackingList: showDetailPackingList,
		showDetailAgreement: showDetailAgreement,
		showDetailOrder: showDetailOrder,
		showDetailContainer: showDetailContainer,
		showDetailBill: showDetailBill,
		openPopupAdd: openPopupAdd,
	}
}());