<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField = "[
				{name: 'paymentMethodTypeId', type: 'string'}, 
				{name: 'productStoreId', type: 'string'}, 
				{name: 'paymentServiceTypeEnumId', type: 'string'}, 
				{name: 'paymentService', type: 'string'},
				{name: 'paymentCustomMethodId', type: 'string'},
				{name: 'paymentGatewayConfigId', type: 'string'},
				{name: 'paymentPropertiesPath', type: 'string'},
				{name: 'applyToAllProducts', type: 'string'},
			]"/>
<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSPaymentMethodTypeId)}', dataField: 'paymentMethodTypeId', width: '30%',
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						for(var i = 0 ; i < paymentMethodTypeList.length; i++){
							if (value == paymentMethodTypeList[i].paymentMethodTypeId){
								return '<span title = ' + paymentMethodTypeList[i].description +'>' + paymentMethodTypeList[i].description + ' - [' + paymentMethodTypeList[i].paymentMethodTypeId + ']' + '</span>';
							}
						}
						return '<span title=' + value +'>' + value + '</span>';
					}
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}', dataField: 'productStoreId', hidden: true,}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSPaymentServiceTypeEnumId)}', dataField: 'paymentServiceTypeEnumId',
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						for(var i = 0 ; i < paymentServiceTypeList.length; i++){
							if (value == paymentServiceTypeList[i].enumId){
								return '<span title = ' + paymentServiceTypeList[i].description +'>' + paymentServiceTypeList[i].description + '</span>';
							}
						}
						return '<span title=' + value +'>' + value + '</span>';
					}
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSApplyToAllProducts)}', dataField: 'applyToAllProducts', width: '20%'}, 
			"/>

<#assign tmpCreate = "false"/>
<#assign tmpDelete = "false"/>
<#if hasOlbPermission("MODULE", "SALES_STOREPAYMENT_NEW", "")>
	<#assign tmpCreate = "true"/>
</#if>
<#if hasOlbPermission("MODULE", "SALES_STOREPAYMENT_DELETE", "")>
	<#assign tmpDelete = "true"/>
</#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindowPaymentMethodNew" columnlist=columnlist dataField=dataField
		viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addType="popup" addrefresh="true" 
		jqGridMinimumLibEnable="true" mouseRightMenu="false" contextMenuId="contextMenu" 
		url="jqxGeneralServicer?sname=JQGetListProductStorePaymentMethod&productStoreId=${productStore.productStoreId?if_exists}&hasrequest=Y"
		addrow="${tmpCreate?string}" createUrl="jqxGeneralServicer?sname=createProductStorePaymentMethod&jqaction=C" addColumns="paymentMethodTypeId;productStoreId;paymentServiceTypeEnumId;paymentService;paymentCustomMethodId;paymentGatewayConfigId;paymentPropertiesPath;applyToAllProducts"
		deleterow="false" removeUrl="jqxGeneralServicer?sname=deleteProductStorePaymentMethod&jqaction=C" deleteColumn="productStoreId;paymentMethodTypeId;paymentServiceTypeEnumId" deleterow=tmpDelete 
	/>

<#assign paymentMethodTypeIds = ["EXT_COD", "EXT_OFFLINE", "CREDIT_CARD", "EXT_BILLACT"]/>
<#assign paymentMethodTypeConds = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("paymentMethodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, paymentMethodTypeIds)!/>
<#assign paymentMethodTypeList = delegator.findList("PaymentMethodType", paymentMethodTypeConds, null, orderBy, null, false)!/>

<#assign paymentgatewayConfigList = delegator.findList("PaymentGatewayConfig", null , null, orderBy,null, false)!/>
<#assign paymentCustomMedthodList = delegator.findList("CustomMethod", null , null, orderBy,null, false)!/>

<#assign paymentServiceTypeIds = ["PRDS_PAY_EXTERNAL", "PRDS_PAY_AUTH_VERIFY"]/>
<#assign paymentServiceTypeCond1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, paymentServiceTypeIds)!/>
<#assign paymentServiceTypeCond2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(paymentServiceTypeCond1, Static["org.ofbiz.entity.condition.EntityOperator"].AND, Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "PRDS_PAYSVC"))!/>
<#assign paymentServiceTypeList = delegator.findList("Enumeration", paymentServiceTypeCond2, null, null, null, false)!/>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	var paymentServiceTypeList = [
	    <#list paymentServiceTypeList as paymentServiceTypeL>
	    {
	    	enumId : "${paymentServiceTypeL.enumId}",
	    	description: "${StringUtil.wrapString(paymentServiceTypeL.get("description", locale))}"
	    },
	    </#list>	
	];
	
	var paymentMethodTypeList = [
	    <#list paymentMethodTypeList as paymentMethodTypeL>
	    {
	    	paymentMethodTypeId : "${paymentMethodTypeL.paymentMethodTypeId}",
	    	description: "${StringUtil.wrapString(paymentMethodTypeL.get("description", locale))}"
	    },
	    </#list>	
	];
	
	var paymentgatewayConfigList = [
	    <#list paymentgatewayConfigList as paymentgatewayConfigL>
	    {
	    	paymentGatewayConfigId : "${paymentgatewayConfigL.paymentGatewayConfigId}",
	    	description: "${StringUtil.wrapString(paymentgatewayConfigL.get("description", locale))}"
	    },
	    </#list>	
	];
	var paymentCustomMedthodList = [
	    <#list paymentCustomMedthodList as paymentCustomMedthodL>
	    {
	    	customMethodId : "${paymentCustomMedthodL.customMethodId}",
	    	description: "${StringUtil.wrapString(paymentCustomMedthodL.get("description", locale))}"
	    },
	    </#list>	
	];
	
	var answerList = [
			{'text': '${StringUtil.wrapString(uiLabelMap.BSYes)}', 'value': 'Y'},
			{'text': '${StringUtil.wrapString(uiLabelMap.BSNo)}', 'value': 'N'}
	];
	
	var notEmpty = '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmpty)}';
	var notSpecialCharacter = '${uiLabelMap.BSThisFieldMustNotByContainSpecialCharacter}';
	var choose = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var productStoreId = '${productStoreId?if_exists}';
	var addNew6 = "${StringUtil.wrapString(uiLabelMap.BSAddNewPaymentMethodForStore)}";
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
</script>
<#include "productStoreNewPayment.ftl">
