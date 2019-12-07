<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
	<#assign localeStr = "EN" />
</#if>
<script>
<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE")), null, null, null, false)>

var faciData = new Array();
<#list facilities as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
	row['facilityId'] = '${item.facilityId?if_exists}';
	row['description'] = '${description?if_exists}';
	faciData[${item_index}] = row;
</#list>

function getDescriptionByFacilityId(facilityId) {
	for ( var x in faciData) {
		if (facilityId == faciData[x].facilityId) {
			return faciData[x].description;
		}
	}
}

<#assign listStatusItem = delegator.findList("StatusItem", null, null, null, null, false) />

var statusData = new Array();
<#list listStatusItem as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.description?if_exists)/>
	row['statusId'] = '${item.statusId?if_exists}';
	row['description'] = '${description?if_exists}';
	statusData[${item_index}] = row;
</#list>

function getDescriptionByStatusId(statusId) {
	for ( var x in statusData) {
		if (statusId == statusData[x].statusId) {
			return statusData[x].description;
		}
	}
}
var listStatusItem = [
						<#if listStatusItem?exists>
							<#list listStatusItem as item>
								{
									statusId: "${item.statusId?if_exists}",
									description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
								},
							</#list>
						</#if>
	                      ];
var mapStatusItem = {
					<#if listStatusItem?exists>
						<#list listStatusItem as item>
							"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
						</#list>
					</#if>	
				};
var locale = '${locale}';
$(document).ready(function () {
	locale == "vi_VN"?locale="vi":locale=locale;
});
</script>
<div id="detailItems">
	<#assign dataFieldVehicle="[
					{ name: 'orderId', type: 'string'},
					{ name: 'deliveryId', type: 'string' },
					{ name: 'originFacilityId', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'deliveryEntryId', type: 'string' },
					{ name: 'remainingSubTotal', type: 'number' },
					{ name: 'shipmentId', type: 'string' },
				]"/>
	<#assign columnlistVehicle="
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.SalesOrder}', datafield: 'orderId', align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.DeliveryId}', datafield: 'deliveryId', align: 'left', minwidth: 150,},
					{ text: '${uiLabelMap.ShipmentId}', datafield: 'shipmentId', align: 'left', minwidth: 150,},
					{ text: '${uiLabelMap.ExportFromFacility}', datafield: 'originFacilityId', align: 'left', width: 150,
						cellsrenderer: function (row, column, value) {
							if(value){
								return '<span>' + getDescriptionByFacilityId(value) + '</span>';
							}
					    }
					},
					{ text: '${uiLabelMap.LogShipper}', datafield: 'deliveryEntryId', align: 'left', width: 150,
						cellsrenderer: function(row, column, value){
							if(value){
								var shipper = value;
								  $.ajax({
									url: 'getShipperByDeliveryEntryId',
									type: 'POST',
									data: {deliveryEntryId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											shipper = data.shipper;
										}
							        }
								  });
								  return '<span title' + value + '>' + shipper + '</span>';
							}
			        	}, 
					},
					{ text: '${uiLabelMap.RemainingSubTotal}', datafield: 'remainingSubTotal', align: 'left', width: 150, cellsalign: 'right', filterable:false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridInfoSaleOrder').jqxGrid('getrowdata', row);
							var orderId = data.orderId;
							var deliveryId = data.deliveryId;
							var currencyUom = data.currencyUom;
							var orderQuantity;
							$.ajax({
								url: 'getRemainingSubTotalByDeliveryId',
								type: 'POST',
								data: {orderId: orderId, deliveryId: deliveryId},
								dataType: 'json',
								async: false,
								success : function(data) {
									if(!data._ERROR_MESSAGE_){
										orderQuantity = data.orderQuantity;
									}
								}
							});
							if(orderQuantity){
								return '<span style=\"text-align: right\">' + formatcurrency(orderQuantity, currencyUom) + '</span>';
							}
			        	}, 
					},
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'left', width: 150,
						cellsrenderer: function (row, column, value) {
							if(value){
								return '<span>' + mapStatusItem[value] + '</span>';
							}
					    }
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldVehicle columnlist=columnlistVehicle editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=JQGetListGeneralSaleOrder" customTitleProperties="ListInforSaleOrderGenaral" id="jqxgridInfoSaleOrder"
	/>
</div>
<script>

</script>