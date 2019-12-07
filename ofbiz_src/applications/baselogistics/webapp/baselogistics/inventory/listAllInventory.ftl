<#include "script/listInventoryScript.ftl"/>
<div id="detailItems" class="hide">
<#assign initrowdetailsDetail2 = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridItemDetail'+index);
	reponsiveRowDetails(grid);
	var sourceGridDetail =
    {
        localdata: datarecord.rowDetail,
        datatype: 'local',
        datafields:
	        [{ name: 'inventoryItemId', type: 'string' },
	        { name: 'productId', type: 'string' },
	        { name: 'productCode', type: 'string' },
	        { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
	        { name: 'expireDate', type: 'date', other: 'Timestamp'},
	        { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
	        { name: 'facilityId', type: 'string' },
	        { name: 'facilityCode', type: 'string' },
	        { name: 'productName', type: 'string' },
			{ name: 'quantityOnHandTotal', type: 'number' },
			{ name: 'amountOnHandTotal', type: 'number' },
			{ name: 'availableToPromiseTotal', type: 'number' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'weightUomId', type: 'string' },
			{ name: 'requireAmount', type: 'string' },
			{ name: 'amountUomTypeId', type: 'string' },
			{ name: 'statusId', type: 'string' },
			{ name: 'description', type: 'string' },
			{ name: 'lotId', type: 'string' },
			]
    };
    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
    grid.jqxGrid({
        width: '98%',
        height: 210,
        theme: 'olbius',
        localization: getLocalization(),
        source: dataAdapterGridDetail,
        sortable: true,
        columnsresize: true,
        pagesize: 5,
 		pageable: true,
        selectionmode: 'singlecell',
        columns: [{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductLabel)}', dataField: 'description', align: 'left', width: 200, resizable: true,
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span></span>';
							}
					    },
					},
					{ hidden: true, text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span class=\"align-right\"></span>';
							}
					    },
					},
					{ hidden: true, text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span class=\"align-right\"></span>';
							}
					    },
					},
					{ text: '${uiLabelMap.ReceivedDate}', datafield: 'datetimeReceived', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
					{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 150, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							var data = grid.jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							var amountUomTypeId = data.amountUomTypeId;
							if (requireAmount && 'Y' == requireAmount && amountUomTypeId && amountUomTypeId == 'WEIGHT_MEASURE') {
								if(data.amountOnHandTotal){
									return '<span class=\"align-right\">' + formatnumber(data.amountOnHandTotal) + '</span>';
								}
							} else {
								if(value){
									return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
								}
							}
							return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
					    },
					},
					{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, align: 'left', minwidth: 150, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span class=\"align-right\">' + formatNumber(value) + '</span>';
							}
					    },
					},
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'left', width: 130, filtertype: 'checkedlist',
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
							tmpRow['description'] = '${uiLabelMap.InventoryGood}';
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
			   			}
					},
					{ text: '${uiLabelMap.Batch}', datafield: 'lotId', align: 'left', width: 160,
						cellsrenderer: function(row, colum, value){
							if(value === null || value === undefined || value === ''){
								return '<span></span>';
							}
						}
					},
				]
        });
 }"/>
	<#assign dataFieldDetail="[
					{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'primaryProductCategoryId', type: 'string' },
					{ name: 'idSKU', type: 'string' },
					{ name: 'facilityId', type: 'string'},
					{ name: 'facilityCode', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'inventoryItemId', type: 'string' },
	                { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
	                { name: 'expireDate', type: 'date', other: 'Timestamp'},
	                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'amountOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'weightUomId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'statusDesc', type: 'string' },
					{ name: 'rowDetail', type: 'string'},
					{ name: 'requireAmount', type: 'string'},
					{ name: 'amountUomTypeId', type: 'string'},
					{ name: 'lotId', type: 'string' }]"/>
	<#assign columnlistDetail="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.BLFacilityId}', datafield: 'facilityCode', align: 'left', width: 120, pinned: true},
					{ text: '${uiLabelMap.BLFacilityName}', datafield: 'facilityName', align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 130, pinned: true},
					{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 220},
					{ text: '${uiLabelMap.BLCategoryProduct}', datafield: 'primaryProductCategoryId', align: 'left', width: 150},
					{ hidden: true, text: '${uiLabelMap.BSUPC}', datafield: 'idSKU', align: 'left', width: 160},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(value === null || value === undefined || value === ''){
								return '<span class=\"align-right\"></span>';
							}
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(value === null || value === undefined || value === ''){
								return '<span class=\"align-right\"></span>';
							}
						}
					},
					{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 150, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							var data = grid.jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							var amountUomTypeId = data.amountUomTypeId;
							if (requireAmount && 'Y' == requireAmount && amountUomTypeId && amountUomTypeId == 'WEIGHT_MEASURE') {
								if(data.amountOnHandTotal){
									return '<span class=\"align-right\">' + formatnumber(data.amountOnHandTotal) + '</span>';
								}
							} else {
								if(value){
									return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
								}
							}
					    },
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}', theme: 'orange' });
					    },
					},
					{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, align: 'left', width: 150, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
							}
					    },
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.AvailableToPromiseTotal)}', theme: 'orange' });
					    },
					},
					{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', filerable: false,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridItemDetail').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') {
								return '<span class=\"align-right\">' + getDescriptionByUomId(data.weightUomId) +'</span>';
							} else {
								return '<span class=\"align-right\">' + getDescriptionByUomId(value) +'</span>';
							}
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
								renderer: function(index, label, value){
						        	if (uomData.length > 0) {
										for(var i = 0; i < uomData.length; i++){
											if(uomData[i].uomId == value){
												return '<span>' + uomData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					},
					{ text: '${uiLabelMap.Batch}', datafield: 'lotId', align: 'left', width: 160,
						cellsrenderer: function(row, colum, value){
							if(value === null || value === undefined || value === ''){
								return '<span></span>';
							}
						}
					},
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'left', width: 150, filtertype: 'checkedlist',
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
							tmpRow['description'] = '${uiLabelMap.InventoryGood}';
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
					},
					{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' }"/>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldDetail columnlist=columnlistDetail editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=getInventoryItemDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listFacilities="+"${json}" customTitleProperties="ListInventory" id="jqxgridItemDetail"
		initrowdetails="false" initrowdetailsDetail=initrowdetailsDetail2 rowdetailsheight="245" customtoolbaraction="viewMethod01" mouseRightMenu="true" contextMenuId="menuInvDetail"/>
</div>
<div id='menuInvDetail' style="display:none;">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <li><i class="fa fa-location-arrow"></i>${StringUtil.wrapString(uiLabelMap.Location)}</li>
	</ul>
</div>

<div id="groupByProduct">

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	reponsiveRowDetails(grid);
	var sourceGridDetail =
    {
        localdata: datarecord.rowDetail,
        datatype: 'local',
        datafields:
	        [{ name: 'inventoryItemId', type: 'string' },
	        { name: 'productId', type: 'string' },
	        { name: 'productCode', type: 'string' },
	        { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
	        { name: 'expireDate', type: 'date', other: 'Timestamp'},
	        { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
	        { name: 'facilityId', type: 'string' },
	        { name: 'facilityCode', type: 'string' },
	        { name: 'productName', type: 'string' },
			{ name: 'quantityOnHandTotal', type: 'number' },
			{ name: 'amountOnHandTotal', type: 'number' },
			{ name: 'availableToPromiseTotal', type: 'number' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'weightUomId', type: 'string' },
			{ name: 'requireAmount', type: 'string' },
			{ name: 'amountUomTypeId', type: 'string' },
			{ name: 'statusId', type: 'string' },
			{ name: 'lotId', type: 'string' },
			]
    };
    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
    grid.jqxGrid({
        width: '98%',
        height: 210,
        theme: 'olbius',
        localization: getLocalization(),
        source: dataAdapterGridDetail,
        sortable: true,
        pagesize: 5,
 		pageable: true,
 		columnsresize: true,
        selectionmode: 'singlecell',
        columns: [{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span class=\"align-right\"></span>';
							}
					    },
					},
					{text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span class=\"align-right\"></span>';
							}
					    },
					},
					{ text: '${uiLabelMap.ReceivedDate}', datafield: 'datetimeReceived', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
					{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 150, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							var data = grid.jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							var amountUomTypeId = data.amountUomTypeId;
							if (requireAmount && 'Y' == requireAmount && amountUomTypeId && amountUomTypeId == 'WEIGHT_MEASURE') {
								if(data.amountOnHandTotal){
									return '<span class=\"align-right\">' + formatnumber(data.amountOnHandTotal) + '</span>';
								}
							} else {
								if(value){
									return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
								}
							}
							return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
					    },
					},
					{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, align: 'left', minwidth: 150, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
							}
					    },
					},
					{ text: '${uiLabelMap.Batch}', datafield: 'lotId', align: 'left', width: 160,
						cellsrenderer: function(row, colum, value){
							if(value === null || value === undefined || value === ''){
								return '<span></span>';
							}
						}
					},
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'left', minwidth: 130, filtertype: 'checkedlist',
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
							tmpRow['description'] = '${uiLabelMap.InventoryGood}';
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
			   			}
					},
				]
        });
 }"/>
	<#assign dataField="[
					{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string'},
					{ name: 'productName', type: 'string' },
					{ name: 'primaryProductCategoryId', type: 'string' },
					{ name: 'idSKU', type: 'string' },
					{ name: 'primaryUPC', type: 'string' },
					{ name: 'listUPCs', type: 'string' },
					{ name: 'supplierId', type: 'string' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'amountOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
					{ name: 'accountingQuantityTotal', type: 'number' },
					{ name: 'rowDetail', type: 'string'},
					{ name: 'requireAmount', type: 'string'},
					{ name: 'amountUomTypeId', type: 'string'},
					{ name: 'weightUomId', type: 'string'},
					{ name: 'facilityId', type: 'string'},
					{ name: 'facilityCode', type: 'string'},
					{ name: 'facilityName', type: 'string'}]"/>
	<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.BLFacilityId}', datafield: 'facilityCode', align: 'left', width: 120, pinned: true},
					{ text: '${uiLabelMap.BLFacilityName}', datafield: 'facilityName', align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 100, pinned: true},
					{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 220},
					{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 120, cellsalign: 'right', filtertype: 'number', filterable: false,
						cellsrenderer: function(row, colum, value){
							var data = $('#jqxgridInvGroupByProduct').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							var amountUomTypeId = data.amountUomTypeId;
							if (requireAmount && 'Y' == requireAmount && amountUomTypeId && amountUomTypeId == 'WEIGHT_MEASURE') {
								if(data.amountOnHandTotal){
									return '<span class=\"align-right\">' + formatnumber(data.amountOnHandTotal) + '</span>';
								}
							} else {
								if(value){
									return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
								}
							}
					    },
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}', theme: 'orange' });
					    }
					},
					{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', align: 'left', width: 120, cellsalign: 'right', filtertype: 'number', filterable: false,
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span class=\"align-right\">' + formatnumber(value) + '</span>';
							}
					    },
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.AvailableToPromiseTotal)}', theme: 'orange' });
					    }
					},
					{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 80, filtertype: 'checkedlist', filterable: false,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridInvGroupByProduct').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') {
								return '<span class=\"align-right\">' + getDescriptionByUomId(data.weightUomId) +'</span>';
							} else {
								return '<span class=\"align-right\">' + getDescriptionByUomId(value) +'</span>';
							}
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(allUomData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
								renderer: function(index, label, value){
						        	if (allUomData.length > 0) {
										for(var i = 0; i < allUomData.length; i++){
											if(allUomData[i].uomId == value){
												return '<span>' + allUomData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					},
					{ text: '${uiLabelMap.BLCategoryProduct}', datafield: 'primaryProductCategoryId', align: 'left', width: 150},
					{ hidden: true, text: '${uiLabelMap.BSUPC}', datafield: 'idSKU', align: 'left', width: 160,
						cellsrenderer: function(row, colum, value){
							var data = $('#jqxgridInvGroupByProduct').jqxGrid('getrowdata', row);
							if(data.primaryUPC){
								return '<span title=\"' + '${uiLabelMap.BLDoubleClickToViewDetail}' +'\">' + data.primaryUPC + '</span>';
							}
							return '<span>' + value + '</span>';
					    },
					},"/>
					<#if !isDistributor || (isDistributor?has_content && isDistributor == false)>
						<#assign columnlist= columnlist + "
							{ text: '${uiLabelMap.SupplierId}', datafield: 'supplierId', align: 'left', width: 130},
						"/>
					</#if>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true" id="jqxgridInvGroupByProduct"
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail customTitleProperties="ListInventory" mouseRightMenu="true" contextMenuId="menuProductDetail"
		isSaveFormData="true" formData="filterObjData" customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()" selectionmode="singlecell"
		url="jqxGeneralServicer?sname=getInventory&facilityId=${parameters.facilityId?if_exists}&deposit=${parameters.deposit?if_exists}&listFacilities=${json}" rowdetailsheight="245" customtoolbaraction="viewMethod02"/>
</div>
<div id='menuProductDetail' style="display:none;">
	<ul>
	    <li><i id="refreshProduct" class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<div id="locationPopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLListLocationsContainProduct}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
	        <div>
	        	<div id="productIdLocation" class="green-label margin-bottom10 pull-left"></div><div id="productNameLocation" class="green-label margin-bottom10 pull-left"></div>
	        	<div id="jqxgridLocation"></div>
	        </div>
			<div class="form-action popup-footer">
		        <button id="locationCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
			</div>
		</div>
	</div>
</div>

<div id="upcPopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLUPCCode}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="listUPC" style="overflow:auto; height: 100%"></div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<script>
	var filterObjData = new Object();
	var exportExcel = function(){
		var dataGrid = $("#jqxgridInvGroupByProduct").jqxGrid('getrows');
		
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
		
		var listFacilities = FilterFacility.getFacilities();
		var facilityId = "${parameters.facilityId?if_exists}";
		var deposit = "${parameters.deposit?if_exists}";

		var winURL = "exportInventoryExcel";
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");

		if (!_.isEmpty(listFacilities)) {
			var hiddenField0 = document.createElement("input");
			hiddenField0.setAttribute("type", "hidden");
			hiddenField0.setAttribute("name", "listFacilities");
			hiddenField0.setAttribute("value", JSON.stringify(listFacilities));
			form.appendChild(hiddenField0);
		}
		
		if (facilityId) {
			var hiddenField1 = document.createElement("input");
			hiddenField1.setAttribute("type", "hidden");
			hiddenField1.setAttribute("name", "facilityId");
			hiddenField1.setAttribute("value", facilityId);
			form.appendChild(hiddenField1);
		}
		
		if (deposit) {
			var hiddenField2 = document.createElement("input");
			hiddenField2.setAttribute("type", "hidden");
			hiddenField2.setAttribute("name", "deposit");
			hiddenField2.setAttribute("value", deposit);
			form.appendChild(hiddenField2);
		}

		if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
			$.each(filterObjData.data, function(key, value) {
				var hiddenField3 = document.createElement("input");
				hiddenField3.setAttribute("type", "hidden");
				hiddenField3.setAttribute("name", key);
				hiddenField3.setAttribute("value", value);
				form.appendChild(hiddenField3);
			});
		}

		document.body.appendChild(form);
		form.submit();
	}

	var RequestWarehouse = (function() {
		var ownerParty = "${(facility.ownerPartyName)?if_exists}";
		var request = function() {
			bootbox.confirm("${StringUtil.wrapString(uiLabelMap.BSRequestWarehouseInventoryTo)} " + ownerParty, "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}", function(result) {
				if (result) {
					DataAccess.execute({
						url: "createNotification",
						data: {partyId: "${(facility.ownerPartyId)?if_exists}", header: "${StringUtil.wrapString(uiLabelMap.BSDemandedWarehouseInventory)}" + ": " + "${(facility.facilityName)?if_exists}", action: "editFacilityInventoryItemsDis",
								targetLink: "facilityId=" + facilityId, ntfType: "ONE", sendToSender: "Y", sendToGroup: "Y"}
						});
				}
			});
		};
		return {
			request: request
		};
	})();
	var viewMethod01 = function(container){
		var str = "<div id='viewMethodDropdown01' class='pull-right margin-top5' style='margin-top: 4px;'></div><div id='channelId' class='hide pull-right margin-top5' style='margin-top: 4px;margin-right: 5px;'></div><div id='labelId' class='hide pull-right margin-top5' style='margin-top: 4px;margin-right: 5px;'></div><div id='txtReceived' class='pull-right margin-top5' style='margin-top: 4px;margin-right: 5px;'></div>";
		<#if facility?exists><#if facility.ownerPartyId != company>
		str += "<div class='pull-right' style='margin-top: 8px;padding-right: 10px;'><a style='cursor: pointer;' onclick='RequestWarehouse.request()'><i class='fa-paper-plane-o'></i>${StringUtil.wrapString(uiLabelMap.BSRequestWarehouseInventory)}</a></div>";
		</#if></#if>
		container.append(str);

		$('#viewMethodDropdown01').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', width: 200, dropDownHeight: 150, selectedIndex: 0, source: listViewMethod01, theme: theme, displayMember: 'description', valueMember: 'methodId', autoDropDownHeight: true});
		if ($("#viewMethodDropdown02").length > 0){
			$('#viewMethodDropdown01').jqxDropDownList('val', $("#viewMethodDropdown02").val());
		}
		// Create a jqxComboBox
		$("#channelId").jqxComboBox({placeHolder: '${StringUtil.wrapString(uiLabelMap.SalesChannel)}', checkboxes: true, source: salesChannel, displayMember: "description", valueMember: "enumId", width: 200, height: 25});
		$("#labelId").jqxComboBox({placeHolder: '${StringUtil.wrapString(uiLabelMap.ProductInventoryType)}', checkboxes: true, source: invLabel, displayMember: "description", valueMember: "inventoryItemLabelId", width: 200, height: 25});
		$("#txtReceived").jqxComboBox({ placeHolder: '${StringUtil.wrapString(uiLabelMap.SGCDatetimeReceived)}', source: daysAgo, displayMember: "text", valueMember: "value", width: 160, height: 25, selectedIndex: 0 });

	     if ($("#channelId2").length > 0){
	    	var listTmp = $("#channelId2").jqxComboBox('getCheckedItems');
	        for (var i = 0; i < listTmp.length; i ++){
	        	$("#channelId").jqxComboBox('clearSelection');
	        	$("#channelId").jqxComboBox('checkIndex', listTmp[i].index);
	        }
	    }
	    if ($("#labelId2").length > 0){
	    	var listTmp = $("#labelId2").jqxComboBox('getCheckedItems');
	        for (var i = 0; i < listTmp.length; i ++){
	        	$("#labelId").jqxComboBox('clearSelection');
	        	$("#labelId").jqxComboBox('checkIndex', listTmp[i].index);
	        }
	    }

	     $("#channelId").on('checkChange', function (event) {
	         if (event.args) {
	        	 var listLabelTmp = $("#labelId").jqxComboBox('getCheckedItems');
	        	 var listLabels = new Array();
	        	 for (var i = 0; i < listLabelTmp.length; i ++){
	        		 var row = {};
	        		 row["inventoryItemLabelId"] = listLabelTmp[i].value;
	        		 listLabels.push(row);
	        	 }
	        	 listLabels = JSON.stringify(listLabels);

	        	 var listTmp = $("#channelId").jqxComboBox('getCheckedItems');
	        	 var listChannels = new Array();
	        	 for (var i = 0; i < listTmp.length; i ++){
	        		 var row = {};
	        		 row["salesMethodChannelEnumId"] = listTmp[i].value;
	        		 listChannels.push(row);
	        	 }
	        	 listChannels = JSON.stringify(listChannels);
	        	 var view = $('#viewMethodDropdown01').val();
	        	 if ("viewByInventoryItem" == view){
	 				var tmpS = $("#jqxgridItemDetail").jqxGrid('source');
	 			 	tmpS._source.url = "jqxGeneralServicer?sname=getInventoryItemDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels + timeAgo();
	 			 	$("#jqxgridItemDetail").jqxGrid('source', tmpS);
	 				$("#groupByProduct").hide();
	 				$("#detailItems").show();
	 			}
	 			if ("viewByProduct" == view){
	 				var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
	 			 	tmpS._source.url = "jqxGeneralServicer?sname=getInventory&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels + timeAgo();
	 			 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
	 			 	$("#detailItems").hide();
	 			 	$("#groupByProduct").show();
	 			}
	         }
	     });

	     $("#labelId").on('checkChange', function (event) {
	         if (event.args) {
	        	 var listLabelTmp = $("#labelId").jqxComboBox('getCheckedItems');
	        	 var listLabels = new Array();
	        	 for (var i = 0; i < listLabelTmp.length; i ++){
	        		 var row = {};
	        		 row["inventoryItemLabelId"] = listLabelTmp[i].value;
	        		 listLabels.push(row);
	        	 }
	        	 listLabels = JSON.stringify(listLabels);
	        	 var listTmp = $("#channelId").jqxComboBox('getCheckedItems');
	        	 var listChannels = new Array();
	        	 for (var i = 0; i < listTmp.length; i ++){
	        		 var row = {};
	        		 row["salesMethodChannelEnumId"] = listTmp[i].value;
	        		 listChannels.push(row);
	        	 }
	        	 listChannels = JSON.stringify(listChannels);
	        	 var view = $('#viewMethodDropdown01').val();
	        	 if ("viewByInventoryItem" == view){
	 				var tmpS = $("#jqxgridItemDetail").jqxGrid('source');
	 			 	tmpS._source.url = "jqxGeneralServicer?sname=getInventoryItemDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels + timeAgo();
	 			 	$("#jqxgridItemDetail").jqxGrid('source', tmpS);
	 				$("#groupByProduct").hide();
	 				$("#detailItems").show();
	 			}
	 			if ("viewByProduct" == view){
	 				var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
	 			 	tmpS._source.url = "jqxGeneralServicer?sname=getInventory&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels + timeAgo();
	 			 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
	 			 	$("#detailItems").hide();
	 			 	$("#groupByProduct").show();
	 			}
	         }
	     });

		$('#viewMethodDropdown01').on('change', function (event){
			 var listLabelTmp = $("#labelId").jqxComboBox('getCheckedItems');
        	 var listLabels = new Array();
        	 for (var i = 0; i < listLabelTmp.length; i ++){
        		 var row = {};
        		 row["inventoryItemLabelId"] = listLabelTmp[i].value;
        		 listLabels.push(row);
        	 }
        	 listLabels = JSON.stringify(listLabels);

			var listTmp = $("#channelId").jqxComboBox('getCheckedItems');
	   	 	var listChannels = new Array();
	   	 	for (var i = 0; i < listTmp.length; i ++){
	   	 		var row = {};
	   	 		row["salesMethodChannelEnumId"] = listTmp[i].value;
	   	 		listChannels.push(row);
	   	 	}
	   	 	listChannels = JSON.stringify(listChannels);
		    var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
			    var label = item.label;
			    var mtId = item.value;
				if ("viewByInventoryItem" == mtId){
					var tmpS = $("#jqxgridItemDetail").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventoryItemDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels + timeAgo();
				 	$("#jqxgridItemDetail").jqxGrid('source', tmpS);
					$("#groupByProduct").hide();
					$("#detailItems").show();
				}
				if ("viewByProduct" == mtId){
					var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventory&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels + timeAgo();
				 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
				 	$("#detailItems").hide();
				 	$("#groupByProduct").show();
				}
			}
		    if ($("#viewMethodDropdown02").length > 0){
	     		$('#viewMethodDropdown02').jqxDropDownList('val', $("#viewMethodDropdown01").val());
		    }
		    if ($("#channelId2").length > 0){
		     	var listTmp = $("#channelId").jqxComboBox('getCheckedItems');
		         for (var i = 0; i < listTmp.length; i ++){
		        	 $("#channelId2").jqxComboBox('clearSelection');
		         	$("#channelId2").jqxComboBox('checkIndex', listTmp[i].index);
		         }
		    }
		    if ($("#labelId2").length > 0){
		     	var listTmp = $("#labelId").jqxComboBox('getCheckedItems');
		         for (var i = 0; i < listTmp.length; i ++){
		        	 $("#labelId2").jqxComboBox('clearSelection');
		         	$("#labelId2").jqxComboBox('checkIndex', listTmp[i].index);
		         }
		    }
		});
		$('#txtReceived').on('change', function (event) {
			var listLabelTmp = $("#labelId").jqxComboBox('getCheckedItems');
       	 var listLabels = new Array();
       	 for (var i = 0; i < listLabelTmp.length; i ++){
       		 var row = {};
       		 row["inventoryItemLabelId"] = listLabelTmp[i].value;
       		 listLabels.push(row);
       	 }
       	 listLabels = JSON.stringify(listLabels);

			var listTmp = $("#channelId").jqxComboBox('getCheckedItems');
	   	 	var listChannels = new Array();
	   	 	for (var i = 0; i < listTmp.length; i ++){
	   	 		var row = {};
	   	 		row["salesMethodChannelEnumId"] = listTmp[i].value;
	   	 		listChannels.push(row);
	   	 	}
	   	 	listChannels = JSON.stringify(listChannels);
		    var args = event.args;
		    if (args) {
			    var mtId = $('#viewMethodDropdown01').val();
				if ("viewByInventoryItem" == mtId){
					var tmpS = $("#jqxgridItemDetail").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventoryItemDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels + timeAgo();
				 	$("#jqxgridItemDetail").jqxGrid('source', tmpS);
					$("#groupByProduct").hide();
					$("#detailItems").show();
				}
				if ("viewByProduct" == mtId){
					var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventory&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels + timeAgo();
				 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
				 	$("#detailItems").hide();
				 	$("#groupByProduct").show();
				}
			}
		    if ($("#viewMethodDropdown02").length > 0){
	     		$('#viewMethodDropdown02').jqxDropDownList('val', $("#viewMethodDropdown01").val());
		    }
		    if ($("#channelId2").length > 0){
		     	var listTmp = $("#channelId").jqxComboBox('getCheckedItems');
		         for (var i = 0; i < listTmp.length; i ++){
		        	 $("#channelId2").jqxComboBox('clearSelection');
		         	$("#channelId2").jqxComboBox('checkIndex', listTmp[i].index);
		         }
		    }
		    if ($("#labelId2").length > 0){
		     	var listTmp = $("#labelId").jqxComboBox('getCheckedItems');
		         for (var i = 0; i < listTmp.length; i ++){
		        	 $("#labelId2").jqxComboBox('clearSelection');
		         	$("#labelId2").jqxComboBox('checkIndex', listTmp[i].index);
		         }
		    }
		});
	};
	var viewMethod02 = function(container){
		var str = "<div id='viewMethodDropdown02' class='pull-right margin-top5 hide' style='margin-top: 4px;'></div>"+
		"<div id='facility' class='pull-right margin-top5 <#if parameters.facilityId?exists> hide </#if>' style='margin-top: 4px;margin-right: 5px;'><div id='jqxgridFacility'</div></div>"+
		"<div id='channelId2' class='hide pull-right margin-top5' style='margin-top: 4px;margin-right: 5px;'></div>"+
		"<div id='labelId2' class='hide pull-right margin-top5' style='margin-top: 4px;margin-right: 5px;'></div>";
		<#if (facility.ownerPartyName)?exists><#if facility.ownerPartyId != company>
		str += "<div class='pull-right' style='margin-top: 8px;padding-right: 10px;'><a style='cursor: pointer;' onclick='RequestWarehouse.request()'><i class='fa-paper-plane-o'></i>${StringUtil.wrapString(uiLabelMap.BSRequestWarehouseInventory)}</a></div>";
		</#if></#if>
		container.append(str);
		$('#viewMethodDropdown02').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', width: 200, dropDownHeight: 150, selectedIndex: 0, source: listViewMethod02, theme: theme, displayMember: 'description', valueMember: 'methodId', autoDropDownHeight: true});
		if ($("#viewMethodDropdown01").length > 0){
			$('#viewMethodDropdown02').jqxDropDownList('val', $("#viewMethodDropdown01").val());
		}
		// Create a jqxComboBox
		 $("#channelId2").jqxComboBox({placeHolder: '${StringUtil.wrapString(uiLabelMap.SalesChannel)}', checkboxes: true, source: salesChannel, displayMember: "description", valueMember: "enumId", width: 200, height: 25});
	    $("#labelId2").jqxComboBox({placeHolder: '${StringUtil.wrapString(uiLabelMap.ProductInventoryType)}', checkboxes: true, source: invLabel, displayMember: "description", valueMember: "inventoryItemLabelId", width: 200, height: 25});

	    if ($("#channelId").length > 0){
	    	var listTmp = $("#channelId").jqxComboBox('getCheckedItems');
	        for (var i = 0; i < listTmp.length; i ++){
	        	$("#channelId2").jqxComboBox('clearSelection');
	        	$("#channelId2").jqxComboBox('checkIndex', listTmp[i].index);
	        }
	    }
	    if ($("#labelId").length > 0){
	    	var listTmp = $("#labelId").jqxComboBox('getCheckedItems');
	        for (var i = 0; i < listTmp.length; i ++){
	        	$("#labelId2").jqxComboBox('clearSelection');
	        	$("#labelId2").jqxComboBox('checkIndex', listTmp[i].index);
	        }
	    }
	    FilterFacility.init();
	    $("#channelId2").on('checkChange', function (event) {
	    	if (event.args) {
    			var listLabelTmp = $("#labelId2").jqxComboBox('getCheckedItems');
    			var listLabels = new Array();
    			for (var i = 0; i < listLabelTmp.length; i ++){
	        		 var row = {};
	        		 row["inventoryItemLabelId"] = listLabelTmp[i].value;
	        		 listLabels.push(row);
    			}
    			listLabels = JSON.stringify(listLabels);

	       	 	var listTmp = $("#channelId2").jqxComboBox('getCheckedItems');
	       	 	var listChannels = new Array();
	       	 	for (var i = 0; i < listTmp.length; i ++){
	       	 		var row = {};
	       	 		row["salesMethodChannelEnumId"] = listTmp[i].value;
	       	 		listChannels.push(row);
	       	 	}
	       	 	listChannels = JSON.stringify(listChannels);
	       	 	var view = $('#viewMethodDropdown02').val();
	       	 	if ("viewByInventoryItem" == view){
					var tmpS = $("#jqxgridItemDetail").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventoryItemDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels;
				 	$("#jqxgridItemDetail").jqxGrid('source', tmpS);
					$("#groupByProduct").hide();
					$("#detailItems").show();
				}
				if ("viewByProduct" == view){
					var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventory&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels;
				 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
				 	$("#detailItems").hide();
				 	$("#groupByProduct").show();
				}
	        }
	    });

	    $("#labelId2").on('checkChange', function (event) {
	    	if (event.args) {
    			var listLabelTmp = $("#labelId2").jqxComboBox('getCheckedItems');
    			var listLabels = new Array();
    			for (var i = 0; i < listLabelTmp.length; i ++){
	        		 var row = {};
	        		 row["inventoryItemLabelId"] = listLabelTmp[i].value;
	        		 listLabels.push(row);
    			}
    			listLabels = JSON.stringify(listLabels);

	       	 	var listTmp = $("#channelId2").jqxComboBox('getCheckedItems');
	       	 	var listChannels = new Array();
	       	 	for (var i = 0; i < listTmp.length; i ++){
	       	 		var row = {};
	       	 		row["salesMethodChannelEnumId"] = listTmp[i].value;
	       	 		listChannels.push(row);
	       	 	}
	       	 	listChannels = JSON.stringify(listChannels);
	       	 	var view = $('#viewMethodDropdown02').val();
	       	 	if ("viewByInventoryItem" == view){
					var tmpS = $("#jqxgridItemDetail").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventoryItemDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels;
				 	$("#jqxgridItemDetail").jqxGrid('source', tmpS);
					$("#groupByProduct").hide();
					$("#detailItems").show();
				}
				if ("viewByProduct" == view){
					var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventory&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels;
				 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
				 	$("#detailItems").hide();
				 	$("#groupByProduct").show();
				}
	        }
	    });

		$('#viewMethodDropdown02').on('change', function (event){
			var listLabelTmp = $("#labelId2").jqxComboBox('getCheckedItems');
			var listLabels = new Array();
			for (var i = 0; i < listLabelTmp.length; i ++){
        		 var row = {};
        		 row["inventoryItemLabelId"] = listLabelTmp[i].value;
        		 listLabels.push(row);
			}
			listLabels = JSON.stringify(listLabels);
			var listTmp = $("#channelId2").jqxComboBox('getCheckedItems');
	   	 	var listChannels = new Array();
	   	 	for (var i = 0; i < listTmp.length; i ++){
	   	 		var row = {};
	   	 		row["salesMethodChannelEnumId"] = listTmp[i].value;
	   	 		listChannels.push(row);
	   	 	}
	   	 	listChannels = JSON.stringify(listChannels);
		    var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
			    var label = item.label;
			    var mtId = item.value;
				if ("viewByInventoryItem" == mtId){
					var tmpS = $("#jqxgridItemDetail").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventoryItemDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels;
				 	$("#jqxgridItemDetail").jqxGrid('source', tmpS);
					$("#groupByProduct").hide();
					$("#detailItems").show();
				}
				if ("viewByProduct" == mtId){
					var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
				 	tmpS._source.url = "jqxGeneralServicer?sname=getInventory&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}&listChannels="+listChannels+"&listLabels="+listLabels;
				 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
				 	$("#detailItems").hide();
				 	$("#groupByProduct").show();
				}
			}
		    if ($("#viewMethodDropdown01").length > 0){
	    		$('#viewMethodDropdown01').jqxDropDownList('val', $("#viewMethodDropdown02").val());
	    	}
		    if ($("#channelId").length > 0){
		    	var listTmp = $("#channelId2").jqxComboBox('getCheckedItems');
		        for (var i = 0; i < listTmp.length; i ++){
		        	$("#channelId").jqxComboBox('clearSelection');
		        	$("#channelId").jqxComboBox('checkIndex', listTmp[i].index);
		        }
		    }
		    if ($("#labelId").length > 0){
		    	var listTmp = $("#labelId2").jqxComboBox('getCheckedItems');
		        for (var i = 0; i < listTmp.length; i ++){
		        	$("#labelId").jqxComboBox('clearSelection');
		        	$("#labelId").jqxComboBox('checkIndex', listTmp[i].index);
		        }
		    }
		});
	};
	function timeAgo () {
		return "&timeAgo=" + ($("#txtReceived").length==1?$("#txtReceived").jqxComboBox("val"):$("#txtReceived2").jqxComboBox("val"));
	}
</script>
<script type="text/javascript" src="/logresources/js/inventory/filterFacility.js?v=1.1.1"></script>