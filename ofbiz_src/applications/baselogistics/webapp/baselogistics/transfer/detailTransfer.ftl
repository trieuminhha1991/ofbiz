<#include "script/detailReturnScript.ftl"/>
<div id="inventoryItemHasBeenReservedForSalesOrder" style="display: none;">
<div>
	${uiLabelMap.ProductHasBeenReservedForSomeSalesOrder}. 
</div>
</div>
<div id="inventoryItemHasBeenExported" style="display: none;">
<div>
	${uiLabelMap.ProductHasBeenExported}. 
</div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;">
</div>
<div>
<div class="rowfluid">
	<div class="span12">
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.OrderReturnId}: </div>
				</div>  
				<div class="span7">
					<div class="green-label">${returnId}</div>
		   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.CreateBy}: </div>
				</div>  
				<div class="span7">
					<div id="createdBy" class="green-label">${fullNameCreateBy}</div>
				</div>
			</div>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.ReturnFrom}: </div>
				</div>  
				<div class="span7">
					<div id="fromPartyId" class="green-label">${fromPartyId} [${returnHeader.fromPartyId}]</div>
		   		</div>
			</div>
			<#if returnHeaderTypeId == 'CUSTOMER_RETURN'>
			<#else>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<div>${uiLabelMap.ReturnTo}: </div>
					</div>  
					<div class="span7">
						<div id="needsInventoryReceive" class="green-label">
							${toPartyName?if_exists}
						</div>
			   		</div>
				</div>
			</#if>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.ReturnType}: </div>
				</div>  
				<div class="span7">
					<div id="returnHeaderTypeId" class="green-label">${returnHeaderTypeDesc?if_exists}</div>
		   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
				<div>${uiLabelMap.CreatedDate}: </div>
			</div>  
			<div class="span7">
				<div id="entryDate" class="green-label"></div> 
				</div>
			</div>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
				<div>${uiLabelMap.OrderCurrentStatus}: </div>
			</div>  
			<div class="span7">
				<div id="statusId" class="green-label">${statusId}</div>
	   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
				<#if returnHeaderTypeId == 'CUSTOMER_RETURN'>
					<div>${uiLabelMap.ReceiveToFacility}: </div>
				<#else>
					<div>${uiLabelMap.ExportFromFacility}: </div>
				</#if>
				</div>  
				<div class="span7">
					<div id="destinationFacilityId" class="green-label">${facility.facilityName?if_exists}</div>
		   		</div>
			</div>
		</div>
	</div>
</div>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},		
					{ text: '${uiLabelMap.OrderId}', pinned: true, dataField: 'orderId', width: 150, editable:false, 
					},
					{ text: '${uiLabelMap.ProductId}', pinned: true, dataField: 'productCode', width: 150, editable:false, 
					},
					{ text: '${uiLabelMap.ProductName}', dataField: 'description', minwidth: 200, editable:false,
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', width: 160, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', width: 160, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
					{ text: '${uiLabelMap.QuantityReturned}', dataField: 'returnQuantity', width: 150, editable:false,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].uomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
						}
					},
					{ text: '${uiLabelMap.ActualExportedQuantity}', dataField: 'alternativeQuantity', width: 150, editable:false,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].uomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							if (value){
								return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
							} else {
								return '<span style=\"text-align: right\">' + data.quantity.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
							}
						}
					},
					{ text: '${uiLabelMap.UnitPrice}', dataField: 'returnPrice', width: 150, editable:false, cellsalign: 'right',
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span style=\"text-align: right\">' + formatcurrency(value, '${returnHeader.currencyUomId?if_exists}') + '<span>';
							} else {
								return '<span style=\"text-align: right\">_NA_<span>';
							}
						}
					},
					{ text: '${uiLabelMap.Reason}', dataField: 'returnReasonId', width: 200, editable:false,
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + mapReturnReason[value] + '<span>';
							}
						}
					},
					{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', editable:false, width: 150, hidden: true,
						cellsrenderer: function (row, column, value){
							if(value){
								if ('CUSTOMER_RETURN' == returnHeaderTypeId){
									for (var i = 0; i < statusSOData.length; i ++){
										if (value == statusSOData[i].statusId){
											return '<span>' + statusSOData[i].description + '<span>';
										}
									}
								} else {
									for (var i = 0; i < statusPOData.length; i ++){
										if (value == statusPOData[i].statusId){
											return '<span>' + statusPOData[i].description + '<span>';
										}
									}
								}
							} else {
								return '<span><span>';
							}
						}
					},
					{ text: '${uiLabelMap.ReturnType}', dataField: 'returnTypeId', editable:false, width: 200, 
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + mapReturnType[value] + '<span>';
							}
						}
					},
					 "/>
<#assign dataField="[{ name: 'orderId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'returnQuantity', type: 'number' },
					{ name: 'returnPrice', type: 'number' },
					{ name: 'quantity', type: 'number' },
					{ name: 'alternativeQuantity', type: 'number' },
					{ name: 'returnReasonId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'returnTypeId', type: 'string' },
					{ name: 'expireDate', type: 'date', other: 'Timestamp' },
					{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
					{ name: 'datetimeReceived', type: 'date', other: 'Timestamp' },
					]"/>
<@jqGrid filtersimplemode="true" id="jqxgridProductReturn" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="false" 
	url="jqxGeneralServicer?sname=JQGetListReturnDetail&returnId=${parameters.returnId?if_exists}" 
	customTitleProperties="ListProductReturnItem"
	jqGridMinimumLibEnable="true" bindresize="false" 
/>