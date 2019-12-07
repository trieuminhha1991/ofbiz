$(function(){
	OlbPhysicalInvTotal.init();
});
var OlbPhysicalInvTotal = (function(){
	var init = function(){
		initGridProductInventory();
		initInputs();
		initElementComplex();
		initEvents();
	};
	
	var initInputs = function() {
		var listInventoryItems = [];
		$.ajax({
			type: 'POST',
			url: 'getPhysicalInventoryItemCountAndVariances',
			async: false,
			data: {
				physicalInventoryId: physicalInventoryId,
			},
			success: function(res){
				listInventoryItems = res.listInventoryItems;
				for (var i = 0; i < listInventoryItems.length; i ++){
					var row = listInventoryItems[i];
					if (row.expireDate != null && row.expireDate != undefined && row.expireDate != ''){
						row['expireDate'] = row.expireDate.time;
					}
					if (row.datetimeManufactured != null && row.datetimeManufactured != undefined && row.datetimeManufactured != ''){
						row['datetimeManufactured'] = row.datetimeManufactured.time;
					}
					row['rowDetail'] = JSON.stringify(row['rowDetail']);
				}
				var tmpSource = $("#physicalInventoryProduct").jqxGrid('source');
				if(typeof(tmpSource) != 'undefined'){
					tmpSource._source.localdata = listInventoryItems;
					$("#physicalInventoryProduct").jqxGrid('source', tmpSource);
				}
			},
		});
	};
	
	var initElementComplex = function() {
	};
	
	var initEvents = function() {
	};

	var initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','physicalInventoryProduct'+index);
		var sourceGridDetail =
	    {
	        localdata: JSON.parse(datarecord.rowDetail),
	        datatype: 'local',
	        datafields:
		        [{ name: 'varianceReasonId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityOnHandVar', type: 'number' },
				{ name: 'amountOnHandVar', type: 'number' },
				{ name: 'availabelToPromiseVar', type: 'number' },
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
	        height: 195,
	        theme: 'olbius',
	        localization: getLocalization(),
	        source: dataAdapterGridDetail,
	        sortable: true,
	        pagesize: 5,
	 		pageable: true,
	 		editable: false,
	 		columnsresize: true,
	        selectionmode: 'singlerow',
	        columns: [{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},
						{ text: uiLabelMap.Reason, dataField: 'varianceReasonId', minwidth: 300, filtertype:'input', editable: false,
							cellsrenderer: function(row, column, value){
								for (var i=0; i < reasonData.length; i++){
									if (reasonData[i].varianceReasonId == value){
										return '<span>' + reasonData[i].description + '</span>';
									}
								}
							},
						},
						{ text: uiLabelMap.Quantity, dataField: 'quantityOnHandVar', columntype: 'numberinput', width: 200, editable: false,
							cellsrenderer: function(row, column, value){
								var data = grid.jqxGrid('getrowdata', row);
								if (data.requireAmount && data.requireAmount == 'Y') value = data.amountOnHandVar;
								if (value != null && value != undefined && value != ''){
									return '<span class="align-right">' + formatnumber(value) + '</span>';
								} else {
									return '<span class="align-right">' + 0	 + '</span>';
								}
							},
						},
						{text: uiLabelMap.Description, dataField: 'comments', columntype: 'textbox', width: 300,
						},
					]
	        });
	}

	var initGridProductInventory = function(){
		var grid = $("#physicalInventoryProduct");
		var datafield =  [
	  		{ name: 'productId', type: 'string'},
			{ name: 'productCode', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'facilityId', type: 'string'},
			{ name: 'weightUomId', type: 'string'},
			{ name: 'requireAmount', type: 'string'},
			{ name: 'facilityName', type: 'string'},
	        { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
	        { name: 'expireDate', type: 'date', other: 'Timestamp'},
			{ name: 'quantityOnHandTotal', type: 'number' },
			{ name: 'amountOnHandTotal', type: 'number' },
			{ name: 'availableToPromiseTotal', type: 'number' },
			{ name: 'quantityOnHandVar', type: 'number' },
			{ name: 'amountOnHandVar', type: 'number' },
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
			{ text: uiLabelMap.ProductId, datafield: 'productCode', align: 'left', width: 130, pinned: true, editable: false,},
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
					if (data.requireAmount && data.requireAmount == 'Y'){
						value = data.amountOnHandTotal;
					}
					if(value){
						return '<span class="align-right">' + formatnumber(value) + '</span>';
					} 
			    }, 
			    rendered: function(element){
			    	$(element).jqxTooltip({content: uiLabelMap.QuantityOnHandTotal, theme: 'orange' });
			    }, 
			},
			{ text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: 80,editable: false, filtertype: 'checkedlist', filterable: false,
				cellsrenderer: function(row, colum, value){
					var data = grid.jqxGrid('getrowdata', row);
					if (data.requireAmount && data.requireAmount == 'Y'){
						value = data.weightUomId;
					}
					if(value){
						return '<span>' +  getUomDescription(value) + '</span>';
					}
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
			{ text: uiLabelMap.Status, datafield: 'statusId', align: 'left', width: 120, filtertype: 'checkedlist',editable: false,
				cellsrenderer: function(row, colum, value){
					for(i=0; i < statusData.length; i++){
			            if(statusData[i].statusId == value){
			            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
			            }
			        }
					if (!value){
		            	return '<span style=\"text-align: left;\" title='+value+'>' + uiLabelMap.InventoryGood+ '</span>';
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
	  	
	  	var rendertoolbar = function(toolbar){
    		toolbar.html("");
    		var id = "physicalInventoryProduct";
    		var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4 style='max-width: 60%; overflow: hidden'>"+uiLabelMap.ListProduct+"</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
    		toolbar.append(jqxheader);
    	};
    	
	  	var config = {
				width: '100%', 
	   		virtualmode: false,
	   		showtoolbar: true,
	   		pageable: true,
	   		sortable: true,
	        filterable: false,	      
	        rendertoolbar: rendertoolbar,
	        editable: false,
	        rowsheight: 26,
	        rowdetails: true,
	        rowdetailstemplate: { rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 210, rowdetailshidden: true },
	        initrowdetails: initrowdetails,
	        url: '',                
	        source: {pagesize: 10}
	  	};
	  	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	return {
		init: init
	};
}());