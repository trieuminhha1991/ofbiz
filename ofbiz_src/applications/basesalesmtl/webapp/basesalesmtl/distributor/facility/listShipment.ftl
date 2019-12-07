<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "popup/receiveProduct.ftl"/>

<#assign localeStr="vi" />
<#if locale!="vi">
    <#assign localeStr="en" />
</#if>
    
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var sourceGridDetail =
    {
        localdata: datarecord.rowDetail,
        datatype: 'local',
        datafields:
        [
			{ name: 'productId', type: 'string'},
			{ name: 'productName', type: 'string'},
			{ name: 'quantityUomId', type: 'string'},
			{ name: 'inventoryItemTypeId', type: 'string'},
			{ name: 'currencyUomId', type: 'string'},
			{ name: 'unitListPrice', type: 'number'},
			{ name: 'quantity', type: 'number'},
			{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
			{ name: 'expireDate', type: 'date', other: 'Timestamp'}
        ],
        id: 'productId',
        addrow: function (rowid, rowdata, position, commit) {
        	
            commit(true);
        },
        deleterow: function (rowid, commit) {
        	
        	commit(true);
        },
        updaterow: function (rowid, newdata, commit) {
            
            commit(true);
        }
    };
    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
    grid.jqxGrid({
    	localization: getLocalization(),
        width: '98%',
        height: '92%',
        theme: 'olbius',
        source: dataAdapterGridDetail,
        sortable: true,
        editable: false,
        pagesize: 5,
 		pageable: true,
        altrows: true,
        columns: [
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (row + 1) + '</div>';
				    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', width: 200},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsProductName)}', datafield: 'productName', minWidth: 200},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity', width: 200, align: 'right',
					cellsrenderer: function(row, colum, value){
				       return '<span style=\"text-align: right\">' + value.toLocaleString(locale) + '</span>';
				   	}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsQuantityUomId)}', datafield: 'quantityUomId', width: 150,
					cellsrenderer: function(row, colum, value){
					   value?value=mapQuantityUom[value]:value;
				       return '<span>' + value + '</span>';
				   	}
				},
				{ text: multiLang.ManufactureDate, dataField: 'datetimeManufactured', width: 150, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy'},
				 { text: multiLang.ExpireDate, dataField: 'expireDate', width: 150,columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy'}]
    });
}"/>

<#assign dataField="[{ name: 'deliveryId', type: 'string'},
					{ name: 'shipmentId', type: 'string'},
					{ name: 'orderId', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'partyNameFrom', type: 'string'},
					{ name: 'originFacilityName', type: 'string'},
					{ name: 'originAddress', type: 'string'},
					{ name: 'destFacilityId', type: 'string'},
					{ name: 'destFacilityName', type: 'string'},
					{ name: 'destAddress', type: 'string'},
					{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'},
					{ name: 'rowDetail', type: 'string'}]"/>
<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSDeliveryId)}', datafield: 'deliveryId', minWidth: 150},
					{ text: '${uiLabelMap.BSStatus}', datafield: 'statusId', filtertype: 'checkedlist', width: 200,
						 cellsrenderer: function(row, colum, value){
								value?value=mapStatusItem[value]:value;
								return '<span>' + value + '</span>';
						 },createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'statusId', valueMember: 'statusId' ,
	                            renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
								    return mapStatusItem[value];
				                }
							});
						 }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'orderId', width: 150,
						 cellsrenderer: function(row, colum, value) {
							var data = $('#listShipment').jqxGrid('getrowdata', row);
							return \"<span><a href='viewOrder?orderId=\" + data.orderId + \"' target='_blank'>\" + data.orderId + \"</a></span>\";
						 }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.OriginFacility)}', datafield: 'originFacilityName', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DestFacility)}', datafield: 'destFacilityName', width:200},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSDeliveryDateEarly)}', datafield: 'estimatedArrivalDate', width: 200, cellsformat: 'HH:mm:ss dd/MM/yyyy', filtertype: 'range'},
					"/>
				
<@jqGrid addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" id="listShipment"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=JQGetListShipment" 
		contextMenuId="contextMenu" mouseRightMenu="true"
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"
	/>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='receiveProduct'><i class="icon-edit"></i>&nbsp;&nbsp;${uiLabelMap.ReceiveProduct}</li>
	</ul>
</div>

<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide"></div>
</div>

<#assign listQuantityUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false) />
<#assign invItemTypes = delegator.findList("InventoryItemType", null, null, null, null, false)>
<script>
	var invItemTypes = [<#if invItemTypes?exists><#list invItemTypes as item>{
		inventoryItemTypeId: '${item.inventoryItemTypeId?if_exists}',
		description: '${StringUtil.wrapString(item.get("description", locale)?if_exists)}'
	},</#list></#if>];
	var mapItemTypes = {<#if invItemTypes?exists><#list invItemTypes as item>
		'${item.inventoryItemTypeId?if_exists}': '${StringUtil.wrapString(item.get("description", locale)?if_exists)}',
	</#list></#if>};
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: '${StringUtil.wrapString(item.get("description", locale)?if_exists)}'
	},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
	'${item.statusId?if_exists}': '${StringUtil.wrapString(item.get("description", locale)?if_exists)}',
	</#list></#if>};
	var mapQuantityUom = {<#if listQuantityUom?exists><#list listQuantityUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
	</#list></#if>};
	var gridSelecting;
	$(document).ready(function() {
		Grid.addContextMenuHoverStyle($("#listShipment"), $("#contextMenu"));
		$("#contextMenu").jqxMenu({ theme: 'olbius', width: 230, height: 30, autoOpenPopup: false, mode: 'popup'});
		$('body').on('click', function() {
			$("#contextMenu").jqxMenu('close');
		});
		$("#contextMenu").on('shown', function () {
			var rowIndexSelected = $('#listShipment').jqxGrid('getSelectedRowindex');
			var statusId = $("#listShipment").jqxGrid('getcellvalue', rowIndexSelected, "statusId");
			var destFacilityId = $("#listShipment").jqxGrid('getcellvalue', rowIndexSelected, "destFacilityId");
			if (statusId != 'DLV_EXPORTED' || destFacilityId) {
				$("#contextMenu").jqxMenu('disable', 'receiveProduct', true);
			} else {
				$("#contextMenu").jqxMenu('disable', 'receiveProduct', false);
			}
		});
		$("#contextMenu").on('itemclick', function (event) {
	        var args = event.args;
	        var itemId = $(args).attr('id');
	        switch (itemId) {
			case "receiveProduct":
				var rowIndexSelected = $('#listShipment').jqxGrid('getSelectedRowindex');
				var rowDetail = $("#listShipment").jqxGrid('getcellvalue', rowIndexSelected, "rowDetail");
				facilityId = $("#listShipment").jqxGrid('getcellvalue', rowIndexSelected, "destFacilityId");
				shipmentId = $("#listShipment").jqxGrid('getcellvalue', rowIndexSelected, "shipmentId");
				deliveryId = $("#listShipment").jqxGrid('getcellvalue', rowIndexSelected, "deliveryId");
				if (facilityId) {
					$("#txtLookupFacility").jqxDropDownList("val", facilityId);
				}
		    	ReceiveProducts.open(rowDetail);
				break;
			default:
				break;
			}
		});
	});
	var facilityId, shipmentId, deliveryId;
	var locale = "${localeStr}";
</script>