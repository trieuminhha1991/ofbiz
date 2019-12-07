$(function(){
	PhysicalInvObj.init();
});
var PhysicalInvObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		var facilityId = $("#facilityId").jqxDropDownList('val');
		initGridInventory(facilityId);
	};
	var initInputs = function() {
	};
	var initElementComplex = function() {
		
	};
	var initEvents = function() {
	};
	var initValidateForm = function(){
		var extendRules = [];
   		var mapRules = [];
	};
	var getData = function getData(){
		var facilityId = $("#facilityId").jqxDropDownList('val');
		var tmpS = $("#jqxgridItemPhysical").jqxGrid('source');
	    tmpS._source.url = "jqxGeneralServicer?sname=jqGetInventoryItemPhysicalDetail&facilityId=" + facilityId;
	    $("#jqxgridItemPhysical").jqxGrid('source', tmpS);
	}
	
	var initGridInventory = function(facilityId){
		
		var grid = $("#jqxgridItemPhysical");
		var datafield =  [
      		{ name: 'productId', type: 'string'},
			{ name: 'productCode', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'requireAmount', type: 'string' },
			{ name: 'weightUomId', type: 'string' },
			{ name: 'facilityId', type: 'string'},
			{ name: 'ownerPartyId', type: 'string'},
			{ name: 'facilityName', type: 'string'},
            { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
            { name: 'expireDate', type: 'date', other: 'Timestamp'},
			{ name: 'quantityOnHandTotal', type: 'number' },
			{ name: 'amountOnHandTotal', type: 'number' },
			{ name: 'availableToPromiseTotal', type: 'number' },
			{ name: 'quantityOnHandVar', type: 'number' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'statusId', type: 'string' },
			{ name: 'statusDesc', type: 'string' },
			{ name: 'varianceReasonId', type: 'string' },
			{ name: 'rowDetail', type: 'string'},
			{ name: 'lotId', type: 'string' }
      	];
      	var columnlist = [
          { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},
			{ text: uiLabelMap.ProductId, datafield: 'productCode', align: 'left', width: 130, pinned: true, editable: false,
				cellsrenderer: function(row, colum, value){
					var data = grid.jqxGrid('getrowdata', row);
				}
			},
			{ text: uiLabelMap.ProductName, datafield: 'productName', align: 'left', minwidth: 250,editable: false,},
			{ text: uiLabelMap.ProductManufactureDate, dataField: 'datetimeManufactured', editable: false, align: 'left', width: 110, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						return '<span class="align-right"></span>';
					}
				}
			},
			{ text: uiLabelMap.ProductExpireDate, dataField: 'expireDate',editable: false, align: 'left', width: 110, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						return '<span class="align-right"></span>';
					}
				}
			},
			{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', align: 'left',editable: false, width: 100, cellsalign: 'right', filtertype: 'number',
				cellsrenderer: function(row, colum, value){
					var data = grid.jqxGrid('getrowdata', row);
					if (data.requireAmount && data.requireAmount == 'Y') value = data.amountOnHandTotal;
					if(value){
						return '<span class="align-right">' + formatnumber(value) + '</span>';
					} 
			    }, 
			    rendered: function(element){
			    	$(element).jqxTooltip({content: uiLabelMap.QuantityOnHandTotal, theme: 'orange' });
			    }, 
			},
			{ text: uiLabelMap.Batch, datafield: 'lotId', align: 'left', width: 80,editable: false,
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						return '<span class="align-right"></span>';
					} else {
						return '<span class="align-right">'+value+'</span>';
					}
				}	
			},
			{ text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: 80,editable: false, filtertype: 'checkedlist', filterable: false,
				cellsrenderer: function(row, colum, value){
					var data = grid.jqxGrid('getrowdata', row);
					if (data.requireAmount && data.requireAmount == 'Y') value = data.weightUomId;
					if(value){
						return '<span>' +  getUomDescription(value) + '</span>';
					}
			    }, 
			    createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
						renderer: function(index, label, value){
				        	if (quantityUomData.length > 0) {
								for(var i = 0; i < quantityUomData.length; i++){
									if(quantityUomData[i].uomId == value){
										return '<span>' + quantityUomData[i].description + '</span>';
									}
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			{ text: uiLabelMap.Status, datafield: 'statusId', align: 'left', width: 120, filtertype: 'checkedlist',editable: false,
				cellsrenderer: function(row, colum, value){
					for(i=0; i < statusData.length; i++){
			            if(statusData[i].statusId == value){
			            	return '<span class="align-right" title='+value+'>' + statusData[i].description + '</span>';
			            }
			        }
					if (!value){
		            	return '<span class="align-right" title='+value+'>' + uiLabelMap.InventoryGood +'</span>';
					}
				},
				createfilterwidget: function (column, columnElement, widget) {
					var tmp = statusData;
					var tmpRow = {};
					tmpRow['statusId'] = '';
					tmpRow['description'] = uiLabelMap.InventoryGood;
					tmp.push(tmpRow);
					var filterDataAdapter = new $.jqx.dataAdapter(tmp, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
						renderer: function(index, label, value){
				        	if (tmp.length > 0) {
								for(var i = 0; i < tmp.length; i++){
									if(tmp[i].statusId == value){
										return '<span>' + tmp[i].description + '</span>';
									}
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			},
			}
      	];
      	
      	var config = {
  			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'checkbox',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: true,
	        rowdetailstemplate: { rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 222, rowdetailshidden: true },
	        initrowdetails: initrowdetails,
	        useUrl: true,
	        url: 'jqGetInventoryItemPhysicalDetail&facilityId='+facilityId,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
      	
	};
	
	var getPackingUomIds = function (productId){
		var tmp ;
		$.ajax({
			url: 'getProductPackingUoms',
			type: "POST",
			async: false,
			data: {
				productId: productId,
			},
			dataType: 'json',
			success : function(res) {
				tmp = res.listUomIds;
			},
		});
		return tmp;
	};
	
	var initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridItemPhysical'+index);
		reponsiveRowDetails(grid);
		var listChilds = [];
		var packingUomIds = getPackingUomIds(datarecord.productId);
		for (var m = 0; m < reasonData.length; m ++){
			var reason = reasonData[m];
			var rowDetail = {};
			rowDetail['quantity'] = 0;
			rowDetail['quantityUomId'] = datarecord.quantityUomId;
			rowDetail['productId'] = datarecord.productId;
			rowDetail['productCode'] = datarecord.productCode;
			rowDetail['requireAmount'] = datarecord.requireAmount;
			rowDetail['weightUomId'] = datarecord.weightUomId;
			rowDetail['productName'] = datarecord.productName;
			rowDetail['quantityOnHandTotal'] = datarecord.quantityOnHandTotal;
			rowDetail['amountOnHandTotal'] = datarecord.amountOnHandTotal;
			rowDetail['availabelToPromiseTotal'] = datarecord.availabelToPromiseTotal;
			rowDetail['lotId'] = datarecord.lotId;
			rowDetail['datetimeManufactured'] = datarecord.datetimeManufactured;
			rowDetail['expireDate'] = datarecord.expireDate;
			rowDetail['packingUomIds'] = packingUomIds;
			rowDetail['comments'] = datarecord.comments;
			rowDetail['statusId'] = datarecord.statusId;
			rowDetail['ownerPartyId'] = datarecord.ownerPartyId;
			rowDetail['facilityId'] = datarecord.facilityId;
			rowDetail["varianceReasonId"] = reason.varianceReasonId;
			rowDetail["negativeNumber"] = reason.negativeNumber;
			rowDetail["description"] = reason.description;
			listChilds.push(rowDetail);
		}
		var sourceGridDetail =
	    {
	        localdata: listChilds,
	        datatype: 'local',
	        datafields:
		        [{ name: 'varianceReasonId', type: 'string' },
		        { name: 'negativeNumber', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'productId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityOnHandTotal', type: 'number' },
				{ name: 'amountOnHandTotal', type: 'number' },
				{ name: 'availabelToPromiseTotal', type: 'number' },
				{ name: 'lotId', type: 'string' },
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'packingUomIds', type: 'string' },
				{ name: 'comments', type: 'string' },
				{ name: 'description', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'ownerPartyId', type: 'string' },
				{ name: 'facilityId', type: 'string' },
				]
	    };
	    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	    grid.jqxGrid({
	        width: '98%',
	        height: 212,
	        theme: 'olbius',
	        localization: getLocalization(),
	        source: dataAdapterGridDetail,
	        sortable: true,
	        pagesize: 5,
	 		pageable: true,
	 		editable: true,
	 		columnsresize: true,
	        selectionmode: 'singlerow',
	        columns: [{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},
						{ text: uiLabelMap.Reason, dataField: 'description', minwidth: 300, filtertype:'input', editable: false,},
						{text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 200, columntype: 'dropdownlist',  filterable: false, editable: true,
							cellsrenderer: function(row, column, value){
								var data = grid.jqxGrid('getrowdata', row);
								var requireAmount = data.requireAmount;
								if (requireAmount && requireAmount == 'Y') {
									value = data.weightUomId;
								}
								return '<span title=' + getUomDescription(value) + '>' + getUomDescription(value) + '</span>';
							},
						 	initeditor: function (row, cellvalue, editor) {
						 		var packingUomData = new Array();
								var data = grid.jqxGrid('getrowdata', row);
								var packingUomIdArray = [];
								if (data.requireAmount && data.requireAmount == 'Y') {
									packingUomData = weightUomData;
									packingUomIdArray = weightUomData;
								} else {
									var packingUoms = data['packingUomIds'];
									packingUomIdArray = packingUoms.split(',');
								}
								var itemSelected = data['quantityUomId'];
								
								for (var i = 0; i < packingUomIdArray.length; i++) {
									var uomId = packingUomIdArray[i];
									var row = {};
									if (uomId === undefined || uomId === '' || uomId === null) {
										row['description'] = '' + uomId;
									} else {
										row['description'] = '';
										for (var j = 0; j < quantityUomData.length; j ++){
											if (quantityUomData[j].uomId == uomId){
												row['description'] = '' + quantityUomData[j].description;
											}
										}
									}
									row['uomId'] = '' + uomId;
									packingUomData[i] = row;
								}
						 		var sourceDataPacking = {
					                localdata: packingUomData,
					                datatype: 'array'
					            };
					            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
					            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId', autoDropDownHeight: true});
					            editor.jqxDropDownList('selectItem', itemSelected);
					      	}
						},
						{ text: uiLabelMap.Quantity, dataField: 'quantity', columntype: 'numberinput', width: 200, editable: true,
							cellsrenderer: function(row, column, value){
								if (value != null && value != undefined && value != ''){
									return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
								} else {
									return '<span style=\"text-align: right\">' + 0	 + '</span>';
								}
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
								var data = grid.jqxGrid('getrowdata', row);
								var requireAmount = data.requireAmount;
								editor.jqxNumberInput({decimalDigits: 0, disabled: false, negativeSymbol: '-'});
                            	if (requireAmount && requireAmount == 'Y') {
                            		editor.jqxNumberInput({decimalDigits: 2, disabled: false, negativeSymbol: '-'});
                            	}
						    },
						    validation: function (cell, value) {
						    	var data = grid.jqxGrid('getrowdata', cell.row);
						    	if (value < 0 && data.negativeNumber != undefined && data.negativeNumber != null && data.negativeNumber == 'N'){
						    		return { result: false, message: uiLabelMap.ThatReasonHasBeenConfigToIncreaseInventory};
						    	}
						    	var requireAmount = data.requireAmount;
						    	if (data.negativeNumber == "Y"){
						    		var rows = grid.jqxGrid('getrows');
							    	var totalDiffMenus = 0;
							    	for (var i = 0; i < rows.length; i ++){
							    		totalDiffMenus = totalDiffMenus + rows[i].quantity;
							    	}
							    	totalDiffMenus = totalDiffMenus - data.quantity + value;
							    	var qoh = data.quantityOnHandTotal;
							    	if (requireAmount && requireAmount == 'Y') {
							    		qoh = data.amountOnHandTotal;
							    	}
							    	if (Math.abs(totalDiffMenus) > qoh) return { result: false, message: uiLabelMap.CannotGreaterThanActualQuantityOnHand + ': ' + Math.abs(totalDiffMenus) + ' > ' + data.quantityOnHandTotal};
						    	}
						    	return true;
						    }
						},
						{text: uiLabelMap.Description, dataField: 'comments', columntype: 'textbox', width: 200,
						},
					]
	        });
	    	
	    	$(grid).on('cellendedit', function (event) {
	    		    // event arguments.
	    		    var args = event.args;
	    		    // column data field.
	    		    var dataField = event.args.datafield;
	    		    // row's bound index.
	    		    var rowBoundIndex = event.args.rowindex;
	    		    // cell value
	    		    var value = args.value;
	    		    var index = datarecord.uid;
	    		    if (value == 0){
	    				var par = datarecord;
	    				var listChilds = [];
	    				var check = false;
	    				if ($('#jqxgridItemPhysical'+ index).length > 0){
	    					var childIndexs = $('#jqxgridItemPhysical'+ index).jqxGrid('getrows');
	    	    			for(var j = 0; j < childIndexs.length; j ++){
	    	    				var data = childIndexs[j];
	    	    				if ((data.quantity < 0 || data.quantity > 0) && rowBoundIndex != childIndexs[j].uid){
	    	    					check = true; break;
	    	    				}
	    	    			}	
	    				}
	    		    } else {
	    		    	if (dataField == 'quantity') {
	    		    		var par = datarecord;
		    				var listChilds = [];
		    				var newData = jQuery.extend(true, {}, par);
		    		    	var requireAmount = par.requireAmount;
		    				var listChilds = [];
		    				var totalQty = newData.quantityOnHandTotal;
		    				if (requireAmount && requireAmount == 'Y') {
		    					totalQty = newData.amountOnHandTotal;
		    				}
		    				var data = $(grid).jqxGrid('getrowdata', rowBoundIndex);
		    				var listChildRows = $(grid).jqxGrid('getrows', rowBoundIndex);
		    				for (k in listChildRows) {
		    					var h = listChildRows[k];
		    					if (h.varianceReasonId != data.varianceReasonId) {
		    						if (h.negativeNumber == "Y") {
			    						totalQty = totalQty - h.quantity;
			    					} else {
			    						totalQty = totalQty + h.quantity;
			    					}
		    						if (h.quantity > 0){
		    							var olb = jQuery.extend(true, {}, h);;
					   					olb['quantityOnHandVar'] = h.quantity;
					   					listChilds.push(olb);
		    						}
		    					} else {
		    						var olb = jQuery.extend(true, {}, h);;
				   					olb['quantityOnHandVar'] = value;
				   					listChilds.push(olb);
		    					}
		    				}
		    				
		    		    	if (data.negativeNumber == "Y") {
	    						totalQty = totalQty - value;
	    					} else {
	    						totalQty = totalQty + value;
	    					}
		   					
		   					for (z in listInventorySelected) {
		   						var a1 = listInventorySelected[z];
		   						if (a1.productId == data.productId && a1.varianceReasonId == data.varianceReasonId && compareDateTime(a1.expireDate, data.expireDate) && compareDateTime(a1.datetimeManufactured, data.datetimeManufactured) && a1.lotId == data.lotId) {
		   							listInventorySelected.splice(z, 1);
		   						}
		   					}
		   					listInventorySelected.push(olb);
		   					newData['rowDetail'] = JSON.stringify(listChilds);
		   					newData['quantityOnHandTotal'] = totalQty;
		   					if (requireAmount && requireAmount == 'Y') {
		   						newData['amountOnHandTotal'] = totalQty;
		   					}
		   					for (z in listProductSelected) {
		   						var a1 = listProductSelected[z];
		   						if (a1.productId == data.productId && compareDateTime(a1.expireDate, data.expireDate) && compareDateTime(a1.datetimeManufactured, data.datetimeManufactured) && a1.lotId == data.lotId) {
		   							listProductSelected.splice(z, 1);
		   						}
		   					}
		   					listProductSelected.push(newData);
		   					var newObject = jQuery.extend(true, {}, par);
		   					newObject['expireDateOld'] = newObject.expireDate;
		   					newObject['datetimeManufacturedOld'] = newObject.datetimeManufactured;
		   					newObject['lotIdOld'] = newObject.lotId;
		   					if(requireAmount && requireAmount == 'Y') {
		   						newObject['quantityOnHandTotalOld'] = newObject.amountOnHandTotal;
		   					} else {
		   						newObject['quantityOnHandTotalOld'] = newObject.quantityOnHandTotal;
		   					}
		    				newObject['quantityOnHandTotal'] = totalQty;
		    				newObject['amountOnHandTotal'] = totalQty;
			    			newObject['quantityOnHandTotalOld'] = newObject.quantityOnHandTotal;
			    			for (z in listInvToUpdates) {
		   						var a1 = listInvToUpdates[z];
		   						
		   						if (a1.productId == data.productId && compareDateTime(a1.expireDate, data.expireDate) && compareDateTime(a1.datetimeManufactured, data.datetimeManufactured) && a1.lotId == data.lotId) {
		   							listInvToUpdates.splice(z, 1);
		   						}
		   					}
			    			
			    			listInvToUpdates.push(newObject);
	    		    	}
	    		    	var q = $('#jqxgridItemPhysical').jqxGrid('getrowboundindexbyid', index);
	    		    	$('#jqxgridItemPhysical').jqxGrid('selectrow', q);
	    		    }
			});
	}
	
	function compareDateTime(date1, date2) {
		if (date1 == null && date2 == null) return true;
		if (date1 == '' && date2 == '') return true;
		if (date1 == undefined && date2 == undefined) return true;
		var x1 = new Date(date1);
		var x2 = new Date(date2);
		if (x1.getTime() == x2.getTime()) return true;
		return false;
	}
	
	return {
		init: init,
		getData: getData,
	}
}());