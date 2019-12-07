<@jqGridMinimumLib />
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
	</#if>
	var localeStr = '${localeStr}';
	<#assign company = Static['com.olbius.basehr.util.MultiOrganizationUtil'].getCurrentOrganization(delegator, userLogin.get('userLoginId'))! />;
	if (transferTypeData === undefined) {
		var transferTypeData = new Array();
		<#assign transferTypes = delegator.findList("TransferType", null, null, null, null, false) />
		var transferTypeData = new Array();
		<#list transferTypes as item>
			<#assign listChilds = delegator.findList("TransferType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", item.transferTypeId?if_exists), null, null, null, false) />
			<#if !(listChilds[0]?has_content && !item.parentTypeId?has_content)>
				var row = {};
				row['transferTypeId'] = "${item.transferTypeId?if_exists}";
				row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
				transferTypeData.push(row);
			</#if>
		</#list>
	}
	
	<#assign productStores = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", company)), null, null, null, false) />
	var shipmentMethodData = [];
	<#assign listIds = []>
	<#if productStores?has_content>
		<#list productStores as store>
			<#assign productStoreShipmentMethods = delegator.findList("ProductStoreShipmentMeth", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", store.productStoreId?if_exists)), null, null, null, false) />
			<#if productStoreShipmentMethods[0]?has_content>
	    		<#list productStoreShipmentMethods as meth>
		    		<#assign idNew = meth.shipmentMethodTypeId>
		        	<#assign check = true>
		        	<#list listIds as idTmp>
		        		<#if idNew == idTmp>
		        			<#assign check = false>
		        			<#break>
		        		<#else>
		        			<#assign check = true>
		        		</#if>
		        	</#list>
		        	<#if check == true>
		        		<#assign listIds = listIds + [idNew]>
		        	</#if>
	    		</#list>
	    	</#if>
		</#list>
	</#if>
	<#if listIds?has_content>
		<#list listIds as methId>
			var item = {};
			<#assign shipmentMethodType = delegator.findOne("ShipmentMethodType", {"shipmentMethodTypeId" : methId?if_exists}, false)/>
			<#assign descMeth = StringUtil.wrapString(shipmentMethodType.get('description', locale)?if_exists) />
			item['shipmentMethodTypeId'] = "${shipmentMethodType.shipmentMethodTypeId?if_exists}";
	    	item['description'] = "${descMeth?if_exists}";
	    	shipmentMethodData.push(item);
		</#list>
	</#if>
	
	var yesNoData = [];
	var itemYes = {
			value: "Y",
			description: "${StringUtil.wrapString(uiLabelMap.LogYes)}",
	}
	var itemNo = {
			value: "N",
			description: "${StringUtil.wrapString(uiLabelMap.LogNO)}",
	}
	yesNoData.push(itemYes);
	yesNoData.push(itemNo);
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
	uiLabelMap.CanNotAfterShipBeforeDate = "${uiLabelMap.CanNotAfterShipBeforeDate}";
	uiLabelMap.CanNotBeforeShipAfterDate = "${uiLabelMap.CanNotBeforeShipAfterDate}";
	uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter = "${uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter}";
	uiLabelMap.PleaseConfigShipmentMethodForSalesChannel = "${uiLabelMap.PleaseConfigShipmentMethodForSalesChannel}";
	uiLabelMap.BLProductStoreId = "${uiLabelMap.BLProductStoreId}";
	uiLabelMap.BLStoreName = "${uiLabelMap.BLStoreName}";
	uiLabelMap.CannotTransferToItSelf = "${uiLabelMap.CannotTransferToItSelf}";
	
</script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/transfer/transferNewTransferInfo.js?v=1.1.1"></script>