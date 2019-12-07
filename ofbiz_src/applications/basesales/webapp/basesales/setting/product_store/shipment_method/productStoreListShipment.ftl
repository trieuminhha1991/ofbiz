<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<style>
	.line-height-25{
		line-height: 25px;
	}
</style>

<#assign shipmentMethodTypeList = delegator.findList("CarrierAndShipmentMethod", null , null, orderBy, null, false)>
<#assign shipmentGatewayConfigList = delegator.findList("ShipmentGatewayConfig", null , null, orderBy, null, false)>
<#assign customMethodList = delegator.findByAnd("CustomMethod", Static["org.ofbiz.base.util.UtilMisc"].toMap("customMethodTypeId", "SHIP_EST"), null, false)>

<script type="text/javascript">
	$.jqx.theme = 'olbius';
	var theme = theme;
	
	var shipmentMethodTypeList = [
	    <#list shipmentMethodTypeList as shipmentMethodTypeL>
	    {
	    	shipmentMethodTypeId : "${shipmentMethodTypeL.shipmentMethodTypeId}",
	    	partyId: "${StringUtil.wrapString(shipmentMethodTypeL.partyId)}",
	    	description: "${StringUtil.wrapString(shipmentMethodTypeL.get("description", locale))}",
	    	roleTypeId: "${StringUtil.wrapString(shipmentMethodTypeL.roleTypeId)}"
	    },
	    </#list>	
	];
	
	var answerList = [
			{'text': '${StringUtil.wrapString(uiLabelMap.BSYes)}', 'value': 'Y'},
			{'text': '${StringUtil.wrapString(uiLabelMap.BSNo)}', 'value': 'N'}
	];
	
   	var channelData2 = [];
	
	var customMethodList = [
	    <#list customMethodList as customMethodL>
	    {
	    	customMethodId : "${customMethodL.customMethodId}",
	    	description: "${StringUtil.wrapString(customMethodL.description)}",
	    	customMethodName: "${StringUtil.wrapString(customMethodL.customMethodName)}",
	    },
	    </#list>	
	];

	var shipmentGatewayConfigList = [
	    <#list shipmentGatewayConfigList as shipmentGatewayConfigL>
	    {
	    	shipmentGatewayConfigId : "${shipmentGatewayConfigL.shipmentGatewayConfigId}",
	    	description: "${StringUtil.wrapString(shipmentGatewayConfigL.description)}",
	    },
	    </#list>	
	];
</script>

<#assign dataField = "[
			{name: 'productStoreShipMethId', type: 'string'}, 
			{name: 'productStoreId', type: 'string'}, 
			{name: 'shipmentMethodTypeId', type: 'string'}, 
			{name: 'partyId', type: 'string'},
			{name: 'roleTypeId', type: 'number'},
			{name: 'companyPartyId', type: 'string'},
			{name: 'description', type: 'string'},
			{name: 'minWeight', type: 'number'},
			{name: 'maxWeight', type: 'number'},
			{name: 'minSize', type: 'number'},
			{name: 'maxSize', type: 'number'},
			{name: 'minTotal', type: 'number'},
			{name: 'maxTotal', type: 'number'},
			{name: 'serviceName', type: 'string'},
			{name: 'allowUspsAddr', type: 'string'},
			{name: 'requireUspsAddr', type: 'string'},
			{name: 'configProps', type: 'string'},
			{name: 'shipmentCustomMethodId', type: 'string'},
			{name: 'shipmentGatewayConfigId', type: 'string'},
			{name: 'sequenceNumber', type: 'number'}
		]"/>
<#--
{text: '${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}', dataField: 'productStoreId', hidden: true}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSRoleTypeId)}', dataField: 'roleTypeId', width: '9%', hidden: true}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', width: '15%', pinned: true}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSCompanyPartyId)}', dataField: 'companyPartyId', width: '8%', hidden: true}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSMinWeight)}', dataField: 'minWeight', width: '10%'}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSMaxWeight)}', dataField: 'maxWeight', width: '10%'}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSMinSize)}', dataField: 'minSize', width: '10%'}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSMaxSize)}', dataField: 'maxSize', width: '10%'}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSMinTotal)}', dataField: 'minTotal', width: '10%'}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSMaxTotal)}', dataField: 'maxTotal', width: '10%'}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSServiceName)}', dataField: 'serviceName', width:'12%' }, 
{text: '${StringUtil.wrapString(uiLabelMap.BSConfigProps)}', dataField: 'configProps', width:'10%'}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSShipmentCustomMethodId)}', dataField: 'shipmentCustomMethodId', width:'10%'}, 
{text: '${StringUtil.wrapString(uiLabelMap.BSShipmentGatewayConfigId)}', dataField: 'shipmentGatewayConfigId', width:'10%'},
-->
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductStoreShipMethId)}', dataField: 'productStoreShipMethId', width: '20%', pinned: true, editable: false}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSShipmentMethodTypeId)}', dataField: 'shipmentMethodTypeId', width: '20%', editable: false}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSPartyId)}', dataField: 'partyId', editable: false}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSSequenceNumber)}', dataField: 'sequenceNumber', width:'16%', 
				editable: true, columntype: 'numberinput', cellsformat: 'd', cellsalign: 'right', 
				validation: function (cell, value) {
					if (value < 0) {
						return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
					}
					return true;
				},
				createeditor: function (row, cellvalue, editor) {
					editor.jqxNumberInput({decimalDigits: 0, digits: 9});
				}
			}, 
		"/>

<#assign tmpCreate = "false"/>
<#assign tmpDelete = "false"/>
<#if hasOlbPermission("MODULE", "SALES_STORESHIPMENT_NEW", "")>
	<#assign tmpCreate = "true"/>
</#if>
<#if hasOlbPermission("MODULE", "SALES_STORESHIPMENT_DELETE", "")>
	<#assign tmpDelete = "true"/>
</#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindowShipmentMethodNew" columnlist=columnlist dataField=dataField
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addrow=tmpCreate addType="popup" addrefresh="true" 
		 jqGridMinimumLibEnable="true" deleterow="false" mouseRightMenu="true" contextMenuId="contextMenu" editable="true" 
		url="jqxGeneralServicer?sname=JQGetListProductStoreShipmentMethod&productStoreId=${productStore.productStoreId?if_exists}&hasrequest=Y"
		createUrl="jqxGeneralServicer?sname=createProductStoreShipmentMethod&jqaction=C" addColumns="productStoreId;shipmentMethodTypeId;partyId;roleTypeId;sequenceNumber(java.lang.Long)"
		updateUrl="jqxGeneralServicer?jqaction=U&sname=updateProductStoreShipmentMethod" editColumns="productStoreShipMethId;sequenceNumber(java.lang.Long)"
		removeUrl="jqxGeneralServicer?sname=deleteProductStoreShipmentMethod&jqaction=C" deleteColumn="productStoreShipMethId" deleterow=tmpDelete 
	/>

<div id="contextMenu" style="display:none">
	<ul>
	    <li action="refresh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#--<li action="update"><i class="fa-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>-->
	</ul>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasDropDownList=true hasValidator=true/>
<script type="text/javascript" src="/salesresources/js/setting/settingListProductStoreShipment.js"></script>
<#include "productStoreNewShipment.ftl">
<#--<#include "productStoreUpdateShipment.ftl">-->
