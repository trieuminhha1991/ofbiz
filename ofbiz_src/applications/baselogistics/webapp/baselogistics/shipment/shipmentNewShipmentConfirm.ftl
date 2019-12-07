<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="span12">
				<div class='row-fluid' style="margin-bottom: -10px !important">
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.OriginFacility}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="originFacilityIdDT" name="originFacilityId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.OriginAddress}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="originContactMechIdDT" name="originContactMechId"></div>
					   		</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.DestFacility}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="destinationFacilityIdDT" name="destinationFacilityId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.DestAddress}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="destinationContactMechIdDT" name="destinationContactMechId"></div>
					   		</div>
						</div>
					</div>
				</div>
				<div class='row-fluid' style="margin-bottom: -10px !important">
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.EstimatedShipDate}</span>
							</div>
							<div class="span7">
								<div id="estimatedShipDateDT" class="green-label"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.EstimatedArrivalDate}</span>
							</div>
							<div class="span7">
								<div id="estimatedArrivalDateDT" class="green-label">
								</div>
					   		</div>
						</div>
					</div>
					<div class="span6">
				<#--		<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.ShipmentMethod}</span>
							</div>
							<div class="span7">
								<div  class="green-label" id="shipmentMethodTypeId" name="shipmentMethodTypeId"></div>
					   		</div>
						</div>
				-->
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.EstimatedShipCost}</span>
							</div>
							<div class="span7">	
								<div id="estimatedShipCostDT" class="green-label"></div>
							</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.CurrencyUomId}</span>
							</div>
							<div class="span7">
								<div class="span5">
									<div id="currencyUomIdDT" class="green-label"></div>
								</div>
					   		</div>
						</div>
					</div>
				</div>
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div id="jqxgridInvSelected" style="width: 100%"></div>
		</div>
	</div>
</div>

<#assign dataFieldConfirm="[
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
<#assign columnlistConfirm="
{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	groupable: false, draggable: false, resizable: false,
	datafield: '', columntype: 'number', width: 50,
	cellsrenderer: function (row, column, value) {
		return '<div style=margin:4px;>' + (value + 1) + '</div>';
	}
},
{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', width: 150, pinned: true, editable: false,},
{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', width: 110, columntype: 'datetimeinput', editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', editable: false, width: 100, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', width: 100, editable: false, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
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
		},
},
{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', width: 60, editable: false,
	cellsrenderer: function(row, colum, value){
		var data = $('#jqxgridInventory').jqxGrid('getrowdata', row);
		if(value){
			return '<span>' +  getDescriptionByUomId(value) + '</span>';
		}
    }, 
},
{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', width: 110, cellsalign: 'right', editable: false,
	cellsrenderer: function(row, colum, value){
		if(value){
			return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
		}
    }, 
},
{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', width: 110, cellsalign: 'right', editable: false,
	cellsrenderer: function(row, colum, value){
		if(value){
			return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
		}
    },
},
{ text: '${uiLabelMap.TransferUnit}', datafield: 'uomId', width: 100, editable: true, filterable: false, columntype: 'dropdownlist',
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
			return '<span></span>';
		}
	},
	initeditor: function(row, value, editor){
     	editor.jqxNumberInput({disabled: false});
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
"/>

<@jqGrid filtersimplemode="true" id="jqxgridInvSelected" filterable="false" dataField=dataFieldConfirm columnlist=columnlistConfirm editable="true" showtoolbar="false"
	url="" editmode='click' selectionmode='multiplecellsadvanced'
/>
<@jqOlbCoreLib />
<script type="text/javascript" src="/logresources/js/shipment/shipmentNewConfirm.js"></script>
