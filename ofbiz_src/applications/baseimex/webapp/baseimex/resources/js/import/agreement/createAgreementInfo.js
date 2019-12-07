$(function() {  
	ObjAggrement.init();
});

var ObjAggrement = (function() {
	var gridPartyTo = $("#jqxGridPartyIdTo");  
	var gridProduct = $("#listProduct");  
	var gridOrder = $("#gridOrder");  
	var gridFacilityPort = $("#gridFacilityPort");  
	var gridFacility = $("#gridFacility");  
	var product = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
	};
	
	var initInput = function() { 
		$("#portOfDischarge").jqxDropDownButton({width: 300, theme: theme});
		$('#portOfDischarge').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		$("#facility").jqxDropDownButton({width: 300, theme: theme});
		$('#facility').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#order").jqxDropDownButton({width: 300, theme: theme});
		$('#order').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$('#listBank').jqxDropDownList({ source: bankData, width: 300, theme: theme, placeHolder : uiLabelMap.PleaseSelectTitle,
			displayMember: 'groupName',
			valueMember: 'partyId'
		});
		
		$('#bankAccount').jqxDropDownList({ source: [], width: 300, theme: theme, placeHolder : uiLabelMap.PleaseSelectTitle,
			displayMember: 'finAccountCode',
			valueMember: 'finAccountId'
		});
	}
	
	var initFacilityGridPort = function(grid){
		var url = "jqGetFacilities&facilityTypeId=PORT";
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
      		{text: uiLabelMap.BIEPortCode, datafield: 'facilityCode', width: '150',
				cellsrenderer: function (row, column, value) {
			        return '<div style="cursor:pointer;">' + (value) + '</div>';
			    }
			},
			{text: uiLabelMap.BIEPortName, datafield: 'facilityName', minwidth: '200',
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
	
	function updateSupplierInfo(partyId){
		updateCurrencyUomId(partyId);
		update({
			partyId: partyId,
			contactMechPurposeTypeId: "PRIMARY_LOCATION",
			}, 'getPartyContactMechs' , 'listPartyContactMechs', 'contactMechId', 'address1', 'EditPurchaseAgreement2_addressIdTo');
        
		update({
			partyId: partyId,
			contactMechTypeId: "EMAIL_ADDRESS",
			}, 'getPartyPrimaryEmails' , 'listPartyPrimaryEmails', 'contactMechId', 'infoString', 'EditPurchaseAgreement2_emailAddressIdTo');
	}
	
	var initEvents = function() { 
		gridPartyTo.on('rowselect', function (event) {
			
	        var args = event.args;
	        var boundIndex = args.rowindex;
			var data = gridPartyTo.jqxGrid('getrowdata', boundIndex);
			gridProduct.jqxGrid('clear');
	        var desc = null;
	        if (data){
	        	partyIdToSelected = $.extend({}, data);
	        	var partyId = partyIdToSelected.partyId;
        		
        		if (partyIdToSelected.partyCode != null){
        			desc = "[" + partyIdToSelected.partyCode + "] " + partyIdToSelected.groupName;
        		} else {
        			desc = "[" + partyIdToSelected.partyId + "] " + partyIdToSelected.groupName;
        		}
        		updateSupplierInfo(partyId);
	        } else {
				desc = uiLabelMap.PleaseSelectTitle;
			}
	        $('#partyIdTo').jqxDropDownButton('close');
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#partyIdTo').jqxDropDownButton('setContent', dropDownContent);
	        
	    });
		
		gridProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantity'){
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
					if (value > 0 && value >= rowData.minimumOrderQuantity){
						var item = $.extend({}, rowData);
						item.quantity = value;
						item.valueTotal = item.quantity * item.lastPrice;
						listProductSelected.push(item);
					}
				} 
				if (dataField == 'lastPrice' && rowData.quantity > 0){
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
				
					var item = $.extend({}, rowData);
					item.lastPrice = value;
					item.valueTotal = item.quantity * item.lastPrice;
					listProductSelected.push(item);
				} 
			}
		});
		
		
		gridFacilityPort.on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        portSelected = $.extend({}, rowData);
	        var description = uiLabelMap.PleaseSelectTitle; 
	        if (portSelected) {
        		description = portSelected.facilityName;
	        }
			
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#portOfDischarge').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		gridFacilityPort.on('rowdoubleclick', function (event) { 
			$('#portOfDischarge').jqxDropDownButton('close');
		});
		
		gridFacilityPort.on('bindingcomplete', function (event) {
			if (portSelected != null){
				var rows = gridFacilityPort.jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						var index = gridFacilityPort.jqxGrid('getrowboundindexbyid', data1.uid);
						if (data1.facilityId == portSelected.facilityId){
							gridFacilityPort.jqxGrid('selectrow', index);
						} else {
							gridFacilityPort.jqxGrid('unselectrow', index);
						}
					}
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
		
		$('#order').on('close', function (event) {
			if (orderSelected) {
				getProductFromOrder(orderSelected.orderId);
				updateProductGrid();
			}
		});
		
		gridOrder.on('rowselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			orderSelected = $.extend({}, rowData);
			var description = uiLabelMap.PleaseSelectTitle; 
			if (orderSelected) {
				description = orderSelected.orderId;
			}
			
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#order').jqxDropDownButton('setContent', dropDownContent);
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
		
		$("#listBank").on('change', function (event) {
			var x = currencySelected;
			var y = $("#listBank").jqxDropDownList('val');
			if (x && y && bankAccountData){
				var source = [];
				for (var u in bankAccountData){
					var o = bankAccountData[u];
					if (o.bankId == y && o.currencyUomId == x.currencyUomId){
						source.push(o);
					}
				}
				$("#bankAccount").jqxDropDownList({source: source});
			}
		});
		
		$("#currencyUomId").on('change', function (event) {
			currencySelected = event.args.item.originalItem;
			var x = currencySelected;
			var y = $("#listBank").jqxDropDownList('val');
			if (x && y && bankAccountData){
				var source = [];
				for (var u in bankAccountData){
					var o = bankAccountData[u];
					if (o.bankId == y && o.currencyUomId == x.currencyUomId){
						source.push(o);
					}
				}
				$("#bankAccount").jqxDropDownList({source: source});
			}
		});
		
	}
	
	var initElementComplex = function() {
		initSupplierGrid(gridPartyTo);
		initProductGrid(gridProduct);
		initOrderGrid(gridOrder);
		initFacilityGridPort(gridFacilityPort);
		initFacilityGrid(gridFacility);
		$("#partyIdTo").jqxDropDownButton({width: 300, theme: theme, dropDownHorizontalAlignment: "right",});
		if (partyIdToSelected) {
			updateSupplierInfo(partyIdToSelected.partyId);
			
			var desc = null;
        	var partyId = partyIdToSelected.partyId;
    		
    		if (partyIdToSelected.partyCode != null){
    			desc = "[" + partyIdToSelected.partyCode + "] " + partyIdToSelected.groupName;
    		} else {
    			desc = "[" + partyIdToSelected.partyId + "] " + partyIdToSelected.groupName;
    		}
	        $('#partyIdTo').jqxDropDownButton('close');
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#partyIdTo').jqxDropDownButton('setContent', dropDownContent);
	        
		} else {
			var descTmp = uiLabelMap.PleaseSelectTitle;
			$('#partyIdTo').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+descTmp+'</div>');
		}
	}
	
	var initSupplierGrid = function(grid){
		var url = "jqGetListPartySupplier";
		var datafield =  [
			{name: 'partyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'groupName', type: 'string'},
      	];
      	var columnlist = [
              { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				    }
				},
				{datafield: 'partyId', hidden: true}, 
				{text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: '25%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = grid.jqxGrid('getrowdata', row);
							value = data.partyId;
						}
				        return '<div style=margin:4px;cursor:pointer;>' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.POSupplierName, datafield: 'groupName', width: '75%',
					cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;cursor:pointer;>' + (value) + '</div>';
				    }
				},
      	];
      	
      	var config = {
  			width: 500, 
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
	
	var initOrderGrid = function(grid){
		var url = "";
		var datafield =  [
			{name: 'orderId', type: 'string'},
			{ name: 'orderDate', type: 'date', other: 'Timestamp' },
			{ name: 'shipAfterDate', type: 'date', other: 'Timestamp' },
			{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp' },
			];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				}
			},
			{text: uiLabelMap.DAOrderId, datafield: 'orderId', minwidth: 150, pinned: true, classes: 'pointer',
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;cursor:pointer;>' + value + '</div>';
				}
			},
			{ text: uiLabelMap.DACreateDate, dataField: 'orderDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype:'range', 
				cellsrenderer: function(row, colum, value) {
				}
			},
			{ text: uiLabelMap.DAShipAfterDate, dataField: 'shipAfterDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype:'range', 
				cellsrenderer: function(row, colum, value) {
				}
			},
			{ text: uiLabelMap.DAShipBeforeDate, dataField: 'shipBeforeDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype:'range', 
				cellsrenderer: function(row, colum, value) {
				}
			},
			];
		
		var config = {
				width: 650, 
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
	
	var initProductGrid = function(grid){
		var url = "";
		var datafield =  [
		                  {name: 'productId', type: 'string'},
		                  {name: 'productCode', type: 'string'},
		                  {name: 'productName', type: 'string'},
		                  {name: 'quantityUomId', type: 'string'},
		                  {name: 'weightUomId', type: 'string'},
		                  {name: 'requireAmount', type: 'string'},
		                  {name: 'amountUomTypeId', type: 'string'},
		                  {name: 'unit', type: 'string'},
		                  {name: 'quantity', type: 'number'},
		                  {name: 'orderedQuantity', type: 'number'},
		                  {name: 'quantityQuota', type: 'number'},
		                  {name: 'minimumOrderQuantity', type: 'number'},
		                  {name: 'planQuantity', type: 'number'},
		                  {name: 'lastPrice', type: 'number'},
		                  {name: 'valueTotal', type: 'string'}
		                  ];
		var columnlist = [
              { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},	
          	{datafield: 'productId', width: 100, editable: false, hidden: true, cellclassname: cellclassname},
          	{text: uiLabelMap.ProductId, pinned: true, datafield: 'productCode', width: '10%', editable: false, cellclassname: cellclassname,
          		cellsrenderer: function (row, column, value) {
			        return "<div style='margin:4px;'>" + value + "</div>";
			    },
          	},
          	{text: uiLabelMap.ProductName, pinned: true, datafield: 'productName', editable: false,  cellclassname: cellclassname, minwidth: 100,
          		cellsrenderer: function (row, column, value) {
			        return "<div style='margin:4px;'>" + value + "</div>";
			    },
          	},
          	{text: uiLabelMap.Unit, datafield: 'quantityUomId', editable: false,  cellclassname: cellclassname, width: '8%',
          		cellsrenderer: function (row, column, value) {
          			var rowsdata = gridProduct.jqxGrid('getrowdata', row);
          			if (rowsdata) {
          				if (rowsdata.requirementAmount && rowsdata.requirementAmount == 'Y' && rowsdata.amountUomTypeId && rowsdata.amountUomTypeId == 'WEIGHT_MEASURE'){
          					value = rowsdata.weightUomId;
          				}
          			}
          			return "<div style='margin:4px;'>" + getUomDesc(value) + "</div>";
          		},
          	},
      		{text: uiLabelMap.PlanQuantity, hidden: checkPlan(), datafield: 'planQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right',width: '12%',
          		cellsrenderer: function(row, column, value){
  					if (value){
  						return '<span class="align-right">' + formatnumber(value) +'</span>';
  					}
  				},  cellclassname: cellclassname
          	},
          	{text: uiLabelMap.orderedQuantity, hidden: checkPlan(), datafield: 'orderedQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '12%',
          		cellsrenderer: function(row, column, value){
  					if (value){
  						return '<span class="align-right">' + formatnumber(value) +'</span>';
  					}
  				},  cellclassname: cellclassname
          	},
          	{text: uiLabelMap.BIEQuota, datafield: 'quantityQuota', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '10%',
          		cellsrenderer: function(row, column, value){
          			if (value){
          				return '<span class="align-right">' + formatnumber(value) +'</span>';
          			}
          		},  cellclassname: cellclassname
          	},
          	{text: uiLabelMap.MOQ, datafield: 'minimumOrderQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '10%',
          		cellsrenderer: function(row, column, value){
          			if (value){
          				return '<span class="align-right">' + formatnumber(value) +'</span>';
          			}
          		},  cellclassname: cellclassname
          	},
          	{text: uiLabelMap.OrderQuantityEdit, datafield: 'quantity', editable: true, filterable: false, align: 'left', cellsalign: 'right',columntype: 'numberinput',width: '10%', cellclassname: cellclassname,
          		cellsrenderer: function(row, column, value){
          			var rowData = grid.jqxGrid('getrowdata', row);
          			if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.quantity;
			   					return false;
			   				}
			   			});
				    }
  					if (value > 0){
  						return '<span class="align-right">' + formatnumber(value) +'</span>';
  					} else {
  						return '<span class="align-right"></span>';
  					} 
  				},
  				initeditor: function (row, cellvalue, editor) {
					var rowData = grid.jqxGrid('getrowdata', row);
					
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
					}
					var rowData = grid.jqxGrid('getrowdata', row);
					if (!cellvalue) {
						if (listProductSelected.length > 0){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					cellvalue = olb.quantity;
				   					return false;
				   				}
				   			});
					    }
					}
					if (cellvalue) {
						var u = cellvalue.toString().replace('.', ',');
						editor.jqxNumberInput('val', u);
					}
				},
				validation: function (cell, value) {
					var rowData = grid.jqxGrid('getrowdata', cell.row);
					if (value < 0) {
						return { result: false, message: uiLabelMap.DAQuantityMustBeGreaterThanZero };
					}
					if (value > 0 && value < rowData.minimumOrderQuantity) {
						return { result: false, message: uiLabelMap.DmsRestrictQuantityPO };
					}
					return true;
				},
          	},
          	{text: uiLabelMap.unitPrice, datafield: 'lastPrice', editable: true, filterable: false, columntype: 'numberinput', cellsalign: 'right',width: '10%', cellclassname: cellclassname,
          		cellsrenderer: function(row, column, value){
          			var rowData = grid.jqxGrid('getrowdata', row);
          			if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.lastPrice;
			   					return false;
			   				}
			   			});
				    }
          			if (value){
  						return '<span class="align-right">' + formatnumber(value) +'</span>';
  					}
  				},
  				initeditor: function (row, cellvalue, editor) {
					var rowData = grid.jqxGrid('getrowdata', row);
					editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					var rowData = grid.jqxGrid('getrowdata', row);
					if (!cellvalue) {
						if (listProductSelected.length > 0){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					cellvalue = olb.lastPrice;
				   					return false;
				   				}
				   			});
					    }
					}
					if (cellvalue) {
						var u = cellvalue.toString().replace('.', ',');
						editor.jqxNumberInput('val', u);
					}
				},
          	},
          	{text: uiLabelMap.DAItemTotal, datafield: 'valueTotal', editable: false, filterable: false, width: '12%',  cellclassname: cellclassname, cellsalign: 'right',
          		cellsrenderer: function(row, column, value){
          			var rowData = grid.jqxGrid('getrowdata', row);
          			if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.valueTotal;
			   					return false;
			   				}
			   			});
				    }
  					if (value){
  						return '<span class="align-right">' + formatnumber(value) +'</span>';
  					}
  				},
          	}
          ];
		var virtualMode = true;
		if (customTimePeriodId && productPlanId){
			virtualMode = false;
		}
		var config = {
				datafields: datafield,
				columns: columnlist,
				width: '100%',
				height: 'auto',
				sortable: true,
				editable: true,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: false,
				url: '',
				groupable: false,
				showgroupsheader: false,
				showaggregates: false,
				showstatusbar: false,
				virtualmode:virtualMode,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				showtoolbar:false,
				columnsresize: true,
				isSaveFormData: true,
				formData: "filterObjData",
				selectionmode: "singlerow",
				bindresize: true,
				pagesize: 10,
				editmode: 'click',
			};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var updateCurrencyUomId = function(partyId) {
		$.ajax({
			url : "getSupplierCurrencyUom",
			type : "POST",
			data : {
				partyId : partyId,
			},
			dataType : "json",
			success : function(data) {
				
			}
		}).done(function(data) {
			var listCurrencyUoms = data.listCurrencyUoms;
			var currencyCombo = [];
			if (listCurrencyUoms != undefined && listCurrencyUoms.length > 0) {
				for (var i = 0; i < listCurrencyUoms.length; i ++) {
					var x = {};
					x.currencyUomId = listCurrencyUoms[i].uomId;
					x.description = listCurrencyUoms[i].abbreviation;
					currencyCombo.push(x);
				}
			}
			if (currencyCombo.length > 0){
				$("#currencyUomId").jqxDropDownList({ source : currencyCombo, disabled : false });
				$("#currencyUomId").jqxDropDownList('selectIndex', 0);
				currencySelected = currencyCombo[0]; 
				if (partyIdToSelected && currencySelected) {
					loadAjaxProduct(partyIdToSelected.partyId, currencySelected.currencyUomId);
				}
			}
		});
	};
	
	function loadAjaxProduct(partyIdToFirst, uomCurency){
		if (partyIdToFirst && uomCurency) {
			listProductSelected = [];
			if (customTimePeriodId && productPlanId){
				$.ajax({
					url: 'loadProductPlanItemByCustomTimePeriodId',
			    	type: "POST",
			    	data: {supplierPartyId: partyIdToFirst, uomCurency: uomCurency, customTimePeriodId: customTimePeriodId, productPlanId: productPlanId},
			    	async: false,
			    	success: function(data) {
			    		
			    		var dataSourceGrid = data.listProductPlanItem;
			    		for (var x in dataSourceGrid){
			    			var obj = dataSourceGrid[x];
			    			var item = $.extend({}, obj);
			    			item.quantity = obj.planQuantity;
			    			item.valueTotal = obj.planQuantity * obj.lastPrice;
			    			listProductSelected.push(item);
			    		}
			    		var tmpS = gridProduct.jqxGrid("source");
		    			tmpS._source.localdata = listProductSelected;
		    			gridProduct.jqxGrid("source", tmpS);
		    			gridProduct.jqxGrid("updatebounddata");
			    	}
				});
			} else {
				var tmpS = gridProduct.jqxGrid("source");
				tmpS._source.url = "jqxGeneralServicer?sname=JQListProductBySupplier&getQuota=Y&supplierId="
					+ partyIdToFirst + "&currencyUomId=" + uomCurency;
				gridProduct.jqxGrid("updatebounddata");
			}
		}
	}
	
	var cellclassname = function (row, column, value) {
 	  	if (column == 'quantity' || column == 'lastPrice') {
    		return 'background-prepare';
    	}
	};
	
	var checkPlan = function (){
		if (customTimePeriodId && productPlanId) return false;
		return true;
	}
	
	var loadOrderData = function (){
		if (partyIdToSelected && currencySelected) {
			var tmpS = gridOrder.jqxGrid("source");
			tmpS._source.url = "jqxGeneralServicer?sname=JQListPOOrder&partySupplierId="+ partyIdToSelected.partyId+ "&filterStatusId=ORDER_APPROVED&currencyUomId="+currencySelected.currencyUomId;
			gridOrder.jqxGrid("updatebounddata");
		}
	}
	
	var getProductFromOrder = function (orderId){
		$.ajax({
			url: 'getOrderItemByOrderToCreateAgreement',
	    	type: "POST",
	    	data: {orderId: orderId},
	    	async: false,
	    	success: function(data) {
	    		var listProducts = data.listProducts;
	    		if (listProducts.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.BIEOrderCreateAgreementFull);
					return false;
				}
	    		listProductSelected = [];
	    		for (var x in listProducts){
	    			var obj = listProducts[x];
	    			var item = $.extend({}, obj);
	    			item.quantity = obj.quantity;
	    			item.lastPrice = obj.unitPrice;
	    			item.valueTotal = obj.quantity * obj.unitPrice;
	    			listProductSelected.push(item);
	    		}
	    	}
		});
	}
	
	var updateProductGrid = function (){
		gridProduct.jqxGrid("updatebounddata");
	}
	
	return {
		init : init,
		loadOrderData:loadOrderData,
	}
}());