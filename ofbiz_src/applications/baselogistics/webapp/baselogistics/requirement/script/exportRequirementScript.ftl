<@jqGridMinimumLib/>
<script type="text/javascript">
	var localeStr = "VI"; 
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	
	var facilityData = [];
	<#assign manager = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "roleType.manager")>
	<#assign listRoles = []>
	<#assign listRoles = listRoles + [manager]>
	
	<#assign facilities = Static['com.olbius.baselogistics.util.LogisticsPartyUtil'].getFacilityByRolesAndFacilityType(delegator, userLogin.partyId?if_exists, listRoles, "WAREHOUSE")! />;
	<#assign listFacIds = []>
	<#list facilities as item>
		<#assign listFacIds = listFacIds + [item.facilityId?if_exists]>
	</#list>
	
	<#list facilities as item>
		var row = {};
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['facilityName'] = "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}";
		facilityData.push(row);
	</#list>
	
	<#assign requirementItems = delegator.findList("RequirementItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementId", parameters.requirementId?if_exists)), null, null, null, false)>
	<#assign listPrIds = []>
	<#list requirementItems as itemTmp>
		<#assign listPrIds = listPrIds + [itemTmp.productId?if_exists]>
	</#list>
	
	var countItem = '${requirementItems.size()}';
	<#assign condInvStt1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, null)>
	<#assign condInvStt2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "INV_AVAILABLE")>
	<#assign condInvStatus = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(condInvStt1, condInvStt2), Static["org.ofbiz.entity.condition.EntityOperator"].OR)>
	<#assign condInvFa = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listFacIds)>
	<#assign condInvOwner = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, company)>
	
	<#assign condInvPr = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listPrIds)>
	
	<#assign condInvQOH = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("quantityOnHandTotal", Static["org.ofbiz.entity.condition.EntityOperator"].GREATER_THAN, Static["java.math.BigDecimal"].ZERO)>
	
	<#assign listInvConds = Static["org.ofbiz.base.util.UtilMisc"].toList(condInvFa, condInvOwner, condInvStatus, condInvQOH, condInvPr)>
	<#assign allInvConds = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(listInvConds, Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
	
	<#assign listOrderBy= ["expireDate"]>;
	<#assign listInvs = delegator.findList("InventoryAndItemProduct", allInvConds, null, listOrderBy, null, false)>
	
	var listInv = new Array();
	<#list listInvs as item>
		var row = {};
		row['inventoryItemId'] = "${item.inventoryItemId?if_exists}";
		row['expireDate'] = "${item.expireDate?if_exists}";
		row['datetimeReceived'] = "${item.datetimeReceived?if_exists}";
		row['datetimeManufactured'] = "${item.datetimeManufactured?if_exists}";
		row['quantityOnHandTotal'] = "${item.quantityOnHandTotal?if_exists}";
		row['amountOnHandTotal'] = "${item.amountOnHandTotal?if_exists}";
		row['availableToPromiseTotal'] = "${item.availableToPromiseTotal?if_exists}";
		row['productId'] = "${item.productId?if_exists}";
		row['quantityUomId'] = "${item.quantityUomId?if_exists}";
		row['weightUomId'] = "${item.weightUomId?if_exists}";
		row['facilityId'] = "${item.facilityId?if_exists}";
		listInv.push(row);
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
	uiLabelMap.AreYouSureYouWantToExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureYouWantToExport)}";
	uiLabelMap.ExportProduct = "${StringUtil.wrapString(uiLabelMap.ExportProduct)}";
	uiLabelMap.AllItemInListMustBeUpdated = "${StringUtil.wrapString(uiLabelMap.AllItemInListMustBeUpdated)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.YouNotYetChooseExpireDate = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseExpireDate)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.InventoryItem = "${StringUtil.wrapString(uiLabelMap.InventoryItem)}";
	uiLabelMap.NotHasExpDate = "${StringUtil.wrapString(uiLabelMap.NotHasExpDate)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.NotEnough = "${StringUtil.wrapString(uiLabelMap.NotEnough)}";
	uiLabelMap.FacilityNotEnoughProduct = "${StringUtil.wrapString(uiLabelMap.FacilityNotEnoughProduct)}";
	uiLabelMap.DmsFieldRequired = "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}";
	uiLabelMap.ExportQuantity = "${StringUtil.wrapString(uiLabelMap.ExportQuantity)}";
	uiLabelMap.ExportValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ExportValueMustBeGreaterThanZero)}";
	uiLabelMap.RequiredNumber = "${StringUtil.wrapString(uiLabelMap.RequiredNumber)}";
	uiLabelMap.EXPRequired = "${StringUtil.wrapString(uiLabelMap.EXPRequired)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.QuantityExported = "${StringUtil.wrapString(uiLabelMap.QuantityExported)}";
	uiLabelMap.ManufacturedDateSum = "${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)}";
	uiLabelMap.ExpiredDateSum = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}";
	uiLabelMap.ReceivedDateSum = "${StringUtil.wrapString(uiLabelMap.ReceivedDateSum)}";
	uiLabelMap.LogInventoryItem = "${StringUtil.wrapString(uiLabelMap.LogInventoryItem)}";
	uiLabelMap.NotEnoughDetail = "${StringUtil.wrapString(uiLabelMap.NotEnoughDetail)}";
	uiLabelMap.ReceivedDateSum = "${StringUtil.wrapString(uiLabelMap.ReceivedDateSum)}";
	uiLabelMap.CannotGreaterRequiredNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterRequiredNumber)}";
	uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother)}";
	uiLabelMap.ExpireDateNotEnter = "${StringUtil.wrapString(uiLabelMap.ExpireDateNotEnter)}";
	uiLabelMap.PleaseEnterQuantityExported = "${StringUtil.wrapString(uiLabelMap.PleaseEnterQuantityExported)}";
	uiLabelMap.ProductMissExpiredDate = "${StringUtil.wrapString(uiLabelMap.ProductMissExpiredDate)}";
	uiLabelMap.ProductMissDatetimeManufactured = "${StringUtil.wrapString(uiLabelMap.ProductMissDatetimeManufactured)}";
	uiLabelMap.ProductMissDatetimeReceived = "${StringUtil.wrapString(uiLabelMap.ProductMissDatetimeReceived)}";
</script>
<script type="text/javascript" src="/logresources/js/requirement/exportRequirement.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>