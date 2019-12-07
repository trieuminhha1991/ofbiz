<@jqGridMinimumLib/>
<script type="text/javascript">
	var localeStr = "VI"; 
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
	
	<#assign listFacIds = []>
	<#list facilities as item>
		<#assign listFacIds = listFacIds + [item.facilityId?if_exists]>
	</#list>
	
	if (facilityData === undefined && facilityData.length < 0){
		var facilityData = [
		<#if facilities?exists>
	   		<#list facilities as item>
	   			{
	   				facilityId: "${item.facilityId?if_exists}",
	   				facilityName: "${StringUtil.wrapString(item.facilityName?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	   	]
	}
	
	<#assign requirementItems = delegator.findList("RequirementItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementId", parameters.requirementId?if_exists)), null, null, null, false)>
	<#assign listPrIds = []>
	<#list requirementItems as itemTmp>
		<#assign listPrIds = listPrIds + [itemTmp.productId?if_exists]>
	</#list>
	
	var countItem = '${requirementItems.size()}';
	<#if requirement.reasonEnumId == "EXPORT_CANCEL">
		<#assign condInvStt1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, null)>
		<#assign condInvStt2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "INV_AVAILABLE")>
		<#assign condInvStatus = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(condInvStt1, condInvStt2), Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
	<#else>
		<#assign condInvStt1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, null)>
		<#assign condInvStt2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "INV_AVAILABLE")>
		<#assign condInvStatus = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(condInvStt1, condInvStt2), Static["org.ofbiz.entity.condition.EntityOperator"].OR)>
	</#if>
	<#assign condInvFa = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listFacIds)>
	<#assign condInvOwner = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, company)>
	
	<#assign condInvPr = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listPrIds)>
	
	<#assign condInvQOH = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("quantityOnHandTotal", Static["org.ofbiz.entity.condition.EntityOperator"].GREATER_THAN, Static["java.math.BigDecimal"].ZERO)>
	
	<#assign listInvConds = Static["org.ofbiz.base.util.UtilMisc"].toList(condInvFa, condInvOwner, condInvStatus, condInvQOH, condInvPr)>
	<#assign allInvConds = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(listInvConds, Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
	<#assign listInvs = delegator.findList("InventoryAndItemProduct", allInvConds, null, null, null, false)>

	<#assign listReasonAndLables = delegator.findList("InventoryLabelChangeReason", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("reasonEnumId", requirement.reasonEnumId)), null, null, null, false)>
	<#assign listReasonAndLables = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(listReasonAndLables)>
	var changeToLabel = null;
	<#if listReasonAndLables?has_content>
		<#assign label = listReasonAndLables[0].fromInventoryLabelId>
		changeToLabel = "${listReasonAndLables[0].toInventoryLabelId?if_exists}"
		<#assign invLabel = delegator.findOne("InventoryItemLabel", false, Static["org.ofbiz.base.util.UtilMisc"].toMap("inventoryItemLabelId", label?if_exists))>
		<#assign labelType = invLabel.inventoryItemLabelTypeId?if_exists>
	</#if>
	var listInv = new Array();
	<#list listInvs as item>
		<#if label?has_content>
			<#assign listInvByLabel = delegator.findList("InventoryItemLabelAppl", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("inventoryItemLabelId", label?if_exists, "inventoryItemLabelTypeId", labelType?if_exists, "inventoryItemId", item.inventoryItemId)), null, null, null, false)>
			<#if listInvByLabel?has_content>
			<#else>
				var row = {};
				row['inventoryItemId'] = "${item.inventoryItemId?if_exists}";
				row['expireDate'] = "${item.expireDate?if_exists}";
				row['datetimeReceived'] = "${item.datetimeReceived?if_exists}";
				row['datetimeManufactured'] = "${item.datetimeManufactured?if_exists}";
				row['quantityOnHandTotal'] = "${item.quantityOnHandTotal?if_exists}";
				row['availableToPromiseTotal'] = "${item.availableToPromiseTotal?if_exists}";
				row['productId'] = "${item.productId?if_exists}";
				row['quantityUomId'] = "${item.quantityUomId?if_exists}";
				row['facilityId'] = "${item.facilityId?if_exists}";
				listInv.push(row);
			</#if>
		</#if>
	</#list>
	var curFacilityId;
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureExport)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.ProductHasBeenReservedForSomeSalesOrder = "${StringUtil.wrapString(uiLabelMap.ProductHasBeenReservedForSomeSalesOrder)}";
	uiLabelMap.DoYouWantToReservesToAnotherInventory = "${StringUtil.wrapString(uiLabelMap.DoYouWantToReservesToAnotherInventory)}";
	uiLabelMap.ProductHasBeenExported = "${StringUtil.wrapString(uiLabelMap.ProductHasBeenExported)}";
	uiLabelMap.AreYouSureAccept = "${StringUtil.wrapString(uiLabelMap.AreYouSureAccept)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.AreYouSureChange = "${StringUtil.wrapString(uiLabelMap.AreYouSureChange)}";
	uiLabelMap.ExportProduct = "${StringUtil.wrapString(uiLabelMap.ExportProduct)}";
	uiLabelMap.AllItemInListMustBeUpdated = "${StringUtil.wrapString(uiLabelMap.AllItemInListMustBeUpdated)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.YouNotYetChooseExpireDate = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseExpireDate)}";
	
</script>
<script type="text/javascript" src="/logresources/js/requirement/inventoryItem.js"></script>