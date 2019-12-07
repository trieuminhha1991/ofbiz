$(function(){
	if (typeof(dataSelected) == "undefined") var dataSelected = [];
	OlbPhysicalInvConfirm.init();
});
var OlbPhysicalInvConfirm = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initGridProductInventory();
		initGridInventoryVariance();
		initGridProductToUpdate();
	};
	
	var initInputs = function() {
	};
	
	var initElementComplex = function() {
	};
	
	var initEvents = function() {
	};
	
	var initGridInventoryVariance = function(){
		var grid = $("#jqxgridInventorySelected");
		var datafield =  [{ name: 'productId', type: 'string'},
		                  { name: 'productCode', type: 'string' },
		                  { name: 'productName', type: 'string' },
		                  { name: 'facilityId', type: 'string'},
		                  { name: 'weightUomId', type: 'string'},
		                  { name: 'requireAmount', type: 'string'},
		                  { name: 'facilityName', type: 'string'},
		                  { name: 'comments', type: 'string'},
		                  { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
		                  { name: 'expireDate', type: 'date', other: 'Timestamp'},
		                  { name: 'quantityOnHandTotal', type: 'number' },
		                  { name: 'amountOnHandTotal', type: 'number' },
		                  { name: 'availableToPromiseTotal', type: 'number' },
		                  { name: 'quantityOnHandVar', type: 'number' },
		                  { name: 'quantityUomId', type: 'string' },
		                  { name: 'statusId', type: 'string' },
		                  { name: 'varianceReasonId', type: 'string' },
		                  { name: 'lotId', type: 'string' }
		                  ];
		var columnlist = [{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
		{ text: uiLabelMap.ProductId, datafield: 'productCode', align: 'left', width: 130, pinned: true, editable: false,},
		{ text: uiLabelMap.ProductName, datafield: 'productName', align: 'left', minwidth: 250,editable: false,},
		{ text: uiLabelMap.ProductManufactureDate, dataField: 'datetimeManufactured',editable: false, align: 'left', width: 110, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			cellsrenderer: function(row, colum, value){
				if(value === null || value === undefined || value === ''){
					return '<span class="align-right"></span>';
				}
			}
		},
		{ text: uiLabelMap.ProductExpireDate, dataField: 'expireDate',editable: false, align: 'left', width: 110, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			cellsrenderer: function(row, colum, value){
				if(value === null || value === undefined || value === ''){
					return '<span class="align-right"></span>';
				}
			}
		},
		{text: uiLabelMap.Reason, dataField: 'varianceReasonId', width: 250, columntype: 'dropdownlist',  filterable:false,
			cellsrenderer: function(row, column, value){
				var data = $('#jqxgridInvGroupByProduct').jqxGrid('getrowdata', row);
					for (var i = 0 ; i < reasonData.length; i++){
					if (value == reasonData[i].varianceReasonId){
						return '<span title=' + reasonData[i].description + '>' + reasonData[i].description + '</span>';
					}
				}
				return value;
			},
		},
		{text: uiLabelMap.Quantity, dataField: 'quantityOnHandVar', width: 120, cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd',
			cellsrenderer: function(row, column, value){
				if (value != undefined && value != null && value != ''){
					return '<span class="align-right" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
				} else {
					if (listInventorySelected.length > 0){
					var data = grid.jqxGrid('getrowdata', row);
					for (var t = 0; t < listInventorySelected.length; t ++){
						var olb = listInventorySelected[t];
						if (olb.inventoryItemId === data.inventoryItemId){
							return '<span class="align-right">' +  formatnumber(olb.quantityOnHandVar) + '</span>';
							break;
						}
					}
					return '<span></span>';
				}
				}
		 	},
		},
		{ text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: 100,editable: false,
			cellsrenderer: function(row, colum, value){
				var data = grid.jqxGrid('getrowdata', row);
				if (data.requireAmount && data.requireAmount == 'Y') {
					value = data.weightUomId;
				}
				if(value){
					return '<span>' +  getUomDescription(value) + '</span>';
				}
		    }, 
		},
		{ text: uiLabelMap.Description, datafield: 'comments', align: 'left', width: 150, editable: false,},
		{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', align: 'left',editable: false, width: 90, cellsalign: 'right', filtertype: 'number',
			cellsrenderer: function(row, colum, value){
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
		{ text: uiLabelMap.Status, datafield: 'statusId', align: 'left', width: 120, filtertype: 'checkedlist',editable: false,
			cellsrenderer: function(row, colum, value){
				for(i=0; i < statusData.length; i++){
		            if(statusData[i].statusId == value){
		            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
		            }
		        }
				if (!value){
		        	return '<span style=\"text-align: left;\" title='+value+'>${uiLabelMap.InventoryGood}</span>';
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
			var id = "jqxgridInventorySelected";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4 style='max-width: 60%; overflow: hidden'>"+uiLabelMap.InventoryVariance+"</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
//	        Grid.createCustomAction($("#jqxgridInventorySelected"), maincontainer, viewMethod02);
		};
		var config = {
	  			width: '100%', 
		   		virtualmode: false,
		   		showtoolbar: false,
		   		selectionmode: 'multiplecellsadvanced',
		   		pageable: true,
		   		rendertoolbar: rendertoolbar,
		   		sortable: true,
		        filterable: false,	        
		        editable: false,
		        rowsheight: 26,
		        rowdetails: false,
		        url: '',                
		        source: {pagesize: 10}
      	};
		
      	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var viewMethod01 = function(container){
		var str = "<div id='viewMethodDropdown01' class='pull-right margin-top5' style='margin-top: 4px;'></div>";
		container.append(str);
		$('#viewMethodDropdown01').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 200, dropDownHeight: 150, selectedIndex: 0, source: listViewMethod01, theme: theme, displayMember: 'description', valueMember: 'methodId', autoDropDownHeight: true});
	     
		$('#viewMethodDropdown01').on('change', function (event){     
			var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
			    var label = item.label;
			    var mtId = item.value;
				if ("viewByInventoryItem" == mtId){
					var tmpSource = $("#jqxgridInventorySelected").jqxGrid('source');
					if(typeof(tmpSource) != 'undefined'){
						tmpSource._source.localdata = listInventorySelected;
						$("#jqxgridInventorySelected").jqxGrid('source', tmpSource);
					}
					$("#groupByProduct").hide();
					$("#detailItems").show();
				}
				if ("viewByProduct" == mtId){
					var tmpSource = $("#jqxgridInvGroupByProduct").jqxGrid('source');
					if(typeof(tmpSource) != 'undefined'){
						
						tmpSource._source.localdata = listProductSelected;
						$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpSource);
					}
				 	$("#detailItems").hide();
				 	$("#groupByProduct").show();
				}
			}
		    if ($("#viewMethodDropdown02").length > 0){
	     		$('#viewMethodDropdown02').jqxDropDownList('val', $("#viewMethodDropdown01").val());
		    }
		});
	};
	
	var initGridProductInventory = function(){
		var grid = $("#jqxgridInvGroupByProduct");
		var datafield =  [
      		{ name: 'productId', type: 'string'},
			{ name: 'productCode', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'facilityId', type: 'string'},
			{ name: 'requireAmount', type: 'string'},
			{ name: 'weightUomId', type: 'string'},
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
					if (data.requireAmount && data.requireAmount == 'Y') {
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
					if (data.requireAmount && data.requireAmount == 'Y') {
						value = data.weightUomId;
					}
					if(value){
						return '<span>' +  getUomDescription(value) + '</span>';
					}
			    }, 
			},
			{ text: uiLabelMap.Status, datafield: 'statusId', align: 'left', width: 120, filtertype: 'checkedlist',editable: false,
				cellsrenderer: function(row, colum, value){
					for(i=0; i < statusData.length; i++){
			            if(statusData[i].statusId == value){
			            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
			            }
			        }
					if (!value){
		            	return '<span style=\"text-align: left;\" title='+value+'>${uiLabelMap.InventoryGood}</span>';
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
      	
    	var rendertoolbar2 = function(toolbar){
    		toolbar.html("");
    		var id = "jqxgridInvGroupByProduct";
    		var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4 style='max-width: 60%; overflow: hidden'>"+uiLabelMap.InventoryCounted+"</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
    		toolbar.append(jqxheader);
         	var container = $('#toolbarButtonContainer' + id);
            var maincontainer = $("#toolbarcontainer" + id);
//            Grid.createCustomAction($("#jqxgridInvGroupByProduct"), maincontainer, viewMethod01);
    	};
    	
      	var config = {
  			width: '100%', 
	   		virtualmode: false,
	   		showtoolbar: false,
	   		pageable: true,
	   		sortable: true,
	        filterable: false,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: true,
	        rowdetailstemplate: { rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 210, rowdetailshidden: true },
	        initrowdetails: initrowdetails,
	        rendertoolbar: rendertoolbar2,
	        url: '',                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var viewMethod02 = function(container){
		var str = "<div id='viewMethodDropdown02' class='pull-right margin-top5' style='margin-top: 4px;'></div>";
		container.append(str);
		$('#viewMethodDropdown02').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 200, dropDownHeight: 150, selectedIndex: 0, source: listViewMethod02, theme: theme, displayMember: 'description', valueMember: 'methodId', autoDropDownHeight: true});
		if ($("#viewMethodDropdown01").length > 0){
			$('#viewMethodDropdown02').jqxDropDownList('val', $("#viewMethodDropdown01").val());
		}
	    
		$('#viewMethodDropdown02').on('change', function (event){     
			var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
			    var label = item.label;
			    var mtId = item.value;
				if ("viewByInventoryItem" == mtId){
					var tmpSource = $("#jqxgridInventorySelected").jqxGrid('source');
					if(typeof(tmpSource) != 'undefined'){
						
						tmpSource._source.localdata = listInventorySelected;
						$("#jqxgridInventorySelected").jqxGrid('source', tmpSource);
					}
					$("#groupByProduct").hide();
					$("#detailItems").show();
				}
				if ("viewByProduct" == mtId){
					var tmpSource = $("#jqxgridInvGroupByProduct").jqxGrid('source');
					if(typeof(tmpSource) != 'undefined'){
						
						tmpSource._source.localdata = listProductSelected;
						$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpSource);
					}
				 	$("#detailItems").hide();
				 	$("#groupByProduct").show();
				}
			}
		    if ($("#viewMethodDropdown01").length > 0){
	    		$('#viewMethodDropdown01').jqxDropDownList('val', $("#viewMethodDropdown02").val());
	    	}
		});
	};
	
	var initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridInvGroupByProduct'+index);
		reponsiveRowDetails(grid);
		var sourceGridDetail =
	    {
	        localdata: 	JSON.parse(datarecord.rowDetail),
	        datatype: 'local',
	        datafields:
		        [{ name: 'varianceReasonId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityOnHandTotal', type: 'number' },
				{ name: 'quantityOnHandVar', type: 'number' },
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
						{ text: uiLabelMap.Reason, dataField: 'description', minwidth: 300, filtertype:'input', editable: false,},
						{text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 200, columntype: 'dropdownlist',  filterable:false, editable: false,
							cellsrenderer: function(row, column, value){
								var data = grid.jqxGrid('getrowdata', row);
								if (data.requireAmount && data.requireAmount == 'Y') {
									value = data.weightUomId;
								}
								if (value) {
									return '<span title=' + getUomDescription(value) + '>' + getUomDescription(value) + '</span>';
								}
								return value;
							},
						},
						{ text: uiLabelMap.Quantity, dataField: 'quantityOnHandVar', columntype: 'numberinput', width: 200, editable: false,
							cellsrenderer: function(row, column, value){
								if (value != null && value != undefined && value != ''){
									return '<span style=\"text-align: right\">' + value + '</span>';
								} else {
									return '<span style=\"text-align: right\">' + 0	 + '</span>';
								}
							},
						},
						{text: uiLabelMap.Description, dataField: 'comments', columntype: 'textbox', width: 200,
						},
					]
	        });
	}
	
	var initGridProductToUpdate = function(){
		var grid = $("#jqxgridProductToUpdate");
		var datafieldUpdate =  [
      		{ name: 'productId', type: 'string'},
			{ name: 'productCode', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'weightUomId', type: 'string' },
			{ name: 'requireAmount', type: 'string' },
			{ name: 'facilityId', type: 'string'},
			{ name: 'facilityName', type: 'string'},
            { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
            { name: 'expireDate', type: 'date', other: 'Timestamp'},
			{ name: 'quantityOnHandTotal', type: 'number' },
			{ name: 'amountOnHandTotal', type: 'number' },
			{ name: 'availableToPromiseTotal', type: 'number' },
			{ name: 'quantity', type: 'number' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'statusId', type: 'string' },
			{ name: 'statusDesc', type: 'string' },
			{ name: 'varianceReasonId', type: 'string' },
			{ name: 'rowDetail', type: 'string'},
			{ name: 'lotId', type: 'string' }
      	];
      	var columnlistUpdate = [
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
			{ text: uiLabelMap.Batch, datafield: 'lotId', align: 'left', width: 80,editable: false,
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						return '<span class="align-right"></span>';
					} else {
						return '<span class="align-right">'+value+'</span>';
					}
				}	
			},
			{ text: uiLabelMap.Quantity, dataField: 'quantity', columntype: 'numberinput', width: 100, editable: false,
				cellsrenderer: function(row, column, value){
					if (value != null && value != undefined && value != ''){
						return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
					} else {
						return '<span style=\"text-align: right\">' + 0	 + '</span>';
					}
				},
			},
			{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', align: 'left',editable: false, width: 100, cellsalign: 'right', filtertype: 'number',
				cellsrenderer: function(row, colum, value){
					var data = grid.jqxGrid('getrowdata', row);
					if (data.requireAmount && data.requireAmount == 'Y') value = data.amountOnHandTotal;
					if(value){
						return '<span class="align-right">' + formatnumber(value) + '</span>';
					} 
					return value;
			    }, 
			    rendered: function(element){
			    	$(element).jqxTooltip({content: uiLabelMap.QuantityOnHandTotal, theme: 'orange' });
			    }, 
			},
			{ text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: 80,editable: false, filtertype: 'checkedlist', filterable: false,
				cellsrenderer: function(row, colum, value){
					var data = grid.jqxGrid('getrowdata', row);
					if (data.requireAmount && data.requireAmount == 'Y') value = data.weightUomId;
					if(value){
						return '<span>' +  getUomDescription(value) + '</span>';
					}
					return value;
			    }, 
			},
			{ text: uiLabelMap.Status, datafield: 'statusId', align: 'left', width: 120, filtertype: 'checkedlist',editable: false,
				cellsrenderer: function(row, colum, value){
					for(i=0; i < statusData.length; i++){
			            if(statusData[i].statusId == value){
			            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
			            }
			        }
					if (!value){
		            	return '<span style=\"text-align: left;\" title='+value+'>${uiLabelMap.InventoryGood}</span>';
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
	   		virtualmode: false,
	   		showtoolbar: false,
	   		pageable: true,
	   		sortable: true,
	        filterable: false,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: false,
	        url: '',                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafieldUpdate, columnlistUpdate, null, grid);
	};
	
	return {
		init: init
	};
}());