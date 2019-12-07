<#include "script/listInventoryScript.ftl"/>
<div>
	<#assign dataFieldDetail="[
					{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string'},
					{ name: 'productName', type: 'string' },
					{ name: 'facilityId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'inventoryItemId', type: 'string' },
					{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
					{ name: 'expireDate', type: 'date', other: 'Timestamp'},
					{ name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'statusDesc', type: 'string' },
					{ name: 'lotId', type: 'string' },
					{ name: 'uomId', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'listQuantityUoms', type: 'string' },
					{ name: 'listInventoryItemIds', type: 'string' },
				]"/>
	<#assign columnlistDetail="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						groupable: false, draggable: false, resizable: false,
						datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (value + 1) + '</div>';
						}
					},
					{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', width: 130, pinned: true, editable: false,},
					{ text: '${uiLabelMap.ProductName}', datafield: 'productName', width: 150, pinned: true, editable: false,},
					{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', width: 110, columntype: 'datetimeinput', editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', editable: false, width: 100, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', width: 100, editable: false, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
					{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', width: 60, editable: false,
						cellsrenderer: function(row, colum, value){
							var data = $('#jqxgridInventory').jqxGrid('getrowdata', row);
							if(value){
								return '<span>' +  getDescriptionByUomId(value) + '</span>';
							}
						}
					},
					{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', width: 110, cellsalign: 'right', editable: false,
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
							}
						}
					},
					{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', width: 110, cellsalign: 'right', editable: false,
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
							}
						}
					},
					{ text: '${uiLabelMap.TransferUnitSum}', datafield: 'uomId', width: 60, editable: true, filterable: false, columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							if (value){
								return '<span>' +  getDescriptionByUomId(value) + '</span>';
							} else {
								var data = $('#jqxgridInventory').jqxGrid('getrowdata', row);
								var id = data.uid;
								$('#jqxgridInventory').jqxGrid('setcellvaluebyid', id, 'uomId', data.quantityUomId);
							}
							return false;
						},
					 	initeditor: function (row, cellvalue, editor) {
					 		var packingUomData = new Array();
							var data = $('#jqxgridInventory').jqxGrid('getrowdata', row);
							var itemSelected = data['quantityUomId'];
							var packingUomIdArray = data['listQuantityUoms'];
							for (var i = 0; i < packingUomIdArray.length; i++) {
								var uomId = packingUomIdArray[i];
								var row = {};
								row['description'] = '' + getDescriptionByUomId(uomId);
								row['uomId'] = '' + uomId;
								packingUomData[i] = row;
							}
					 		var sourceDataPacking = {
				                localdata: packingUomData,
				                datatype: 'array'
				            };
				            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
				            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
				            editor.jqxDropDownList('selectItem', itemSelected);
				      	}
					},
					{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', columntype: 'numberinput', cellClassName: 'focus-color',  cellsalign: 'right', width: 112, editable: true, filterable: false,
						cellsrenderer: function(row, column, value){
							if (value){
								return '<span style=\"text-align: right;\">' +  value.toLocaleString('${localeStr}') + '</span>';
							} else {
								if (listInvChanged.length > 0){
									var data = $('#jqxgridInventory').jqxGrid('getrowdata', row);
									for (var t = 0; t < listInvChanged.length; t ++){
										if (olb != null && olb != undefined && '' != olb){
											var olb = listInvChanged[t];
											if (olb.lotId == data.lotId && olb.facilityId == data.facilityId && olb.expireDate.getTime() == data.expireDate.getTime() && olb.datetimeManufactured.getTime() == data.datetimeManufactured.getTime() && olb.datetimeReceived.getTime() == data.datetimeReceived.getTime() && olb.productId == data.productId ){
												return '<span style=\"text-align: right;\">' +  olb.quantity.toLocaleString('${localeStr}') + '</span>';
												break;
											}
										}
									}
									return '<span></span>';
								}
								return '<span></span>';
							}
						},
						initeditor: function(row, value, editor){
                         	var data = $('#jqxgridInventory').jqxGrid('getrowdata', row);
							for (var t = 0; t < listInvChanged.length; t ++){
								var olb = listInvChanged[t];
								if (olb != null && olb != undefined && '' != olb){
									if (olb.lotId == data.lotId && olb.facilityId == data.facilityId && olb.expireDate.getTime() == data.expireDate.getTime() && olb.datetimeManufactured.getTime() == data.datetimeManufactured.getTime() && olb.datetimeReceived.getTime() == data.datetimeReceived.getTime() && olb.productId == data.productId ){
										editor.jqxNumberInput('val', olb.quantity);
										break;
									}
								}
							}
					    },
					    validation: function (cell, value) {
					    	var data = $('#jqxgridInventory').jqxGrid('getrowdata', cell.row);
					        if (value < 0) {
					            return { result: false, message: '${uiLabelMap.NumberGTZ}'};
					        }
					        if (value > data.quantityOnHandTotal) {
					            return { result: false, message: '${uiLabelMap.CannotGreaterQOHNumber}'};
					        }
					        return true;
					    },
					},
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', minwidth: 100, filtertype: 'checkedlist', editable: false,
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
					}"/>
	
	<@jqGrid filtersimplemode="true" id="jqxgridInventory" filterable="true" dataField=dataFieldDetail columnlist=columnlistDetail editable="true" showtoolbar="false"
		url="jqxGeneralServicer?sname=getInventoryItemAndProduct&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}" editmode='click' selectionmode='multiplecellsadvanced'
	/>
</div>
${setContextField("dataFieldInvItems", dataFieldDetail)}
${setContextField("columnlistInvItems", columnlistDetail)}