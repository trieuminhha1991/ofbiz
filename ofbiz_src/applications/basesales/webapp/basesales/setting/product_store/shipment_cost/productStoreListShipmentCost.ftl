<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<#assign dataField = "[{name: 'shipmentCostEstimateId', type: 'string'}, 
	{name: 'shipmentMethodTypeId', type: 'string'}, 
	{name: 'carrierPartyId', type: 'string'}, 
	{name: 'carrierRoleTypeId', type: 'string'}, 
	{name: 'orderFlatPrice', type: 'number'},
	{name: 'orderPricePercent', type: 'number'},
	{name: 'orderItemFlatPrice', type: 'number'},
	{name: 'productStoreId', type: 'number'},
]"/>
						
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', dataField: 'productStoreId', width: '16%', hidden: true}, 
	{text: '${StringUtil.wrapString(uiLabelMap.BSShipmentCostEstimateId)}', dataField: 'shipmentCostEstimateId', width: '15%'}, 
	{text: '${StringUtil.wrapString(uiLabelMap.BSShipmentMethodTypeId)}', dataField: 'shipmentMethodTypeId', width: '19%'}, 
	{text: '${StringUtil.wrapString(uiLabelMap.BSCarrierPartyId)}', dataField: 'carrierPartyId', width: '15%'}, 
	{text: '${StringUtil.wrapString(uiLabelMap.BSCarrierRoleTypeId)}', dataField: 'carrierRoleTypeId', width: '15%'}, 
	{text: '${StringUtil.wrapString(uiLabelMap.BSOrderFlatPrice)}', dataField: 'orderFlatPrice', width: '15%'}, 
	{text: '${StringUtil.wrapString(uiLabelMap.BSOrderPricePercent)}', dataField: 'orderPricePercent', width: '15%'}, 
	{text: '${StringUtil.wrapString(uiLabelMap.BSOrderItemFlatPrice)}', dataField: 'orderItemFlatPrice', width: '15%'}, 
"/>
						
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
	viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addrow="true" addType="popup" addrefresh="true" 
	 jqGridMinimumLibEnable="true" deleterow="false" mouseRightMenu="true" contextMenuId="contextMenu" 
	url="jqxGeneralServicer?sname=getListProductStoreShipmentCost&productStoreId=${productStore.productStoreId?if_exists}&hasrequest=Y" 
	createUrl="jqxGeneralServicer?sname=createShipmentCost&jqaction=C" addColumns="productStoreId;shipmentCostEstimateId;shipmentMethodTypeId;carrierPartyId;carrierRoleTypeId;orderFlatPrice(java.math.BigDecimal);orderPricePercent(java.math.BigDecimal);orderItemFlatPrice(java.math.BigDecimal)"
/>
		
<div id="contextMenu" style="display:none">
	<ul>
	    <li action="refresh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <li id="menuEditRow" action="update"><i class="fa-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>
	</ul>
</div>

<script type="text/javascript">
	var shipmentMethodTypeLabel = '${uiLabelMap.BSShipmentMethodTypeId}';
	var carrierPartyLabel = '${uiLabelMap.BSCarrierPartyId}';
	var carrierRoleTypeLabel = '${uiLabelMap.BSCarrierRoleTypeId}';
	var productStoreId = "${productStoreId?if_exists}";
	var validateEmpty = '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmpty)}';
	var validateNumber = '${uiLabelMap.BSValidateNumber}';
	var editSuccess = "${StringUtil.wrapString(uiLabelMap.BSSuccessK)}";
	var addNew7 = "${StringUtil.wrapString(uiLabelMap.BSAddNewShipmentCostForStore)}";
	var updatePopup3 = "${StringUtil.wrapString(uiLabelMap.BSUpdateShipmentCostForStore)}";
</script>
<@jqOlbCoreLib />

<#include "productStoreNewShipmentCost.ftl">
<#include "productStoreUpdateShipmentCost.ftl">

<script type="text/javascript">
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
        var action = $(args).attr("action"); 
        if (action == "refresh") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }
	});
</script>
