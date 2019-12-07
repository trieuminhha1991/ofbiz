<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<style type="text/css">
.bootbox{
    z-index: 99000 !important;
}
.modal-backdrop{
    z-index: 89000 !important;
}
</style>

<#assign orderHeader = delegator.findOne("OrderHeader", {"orderId" : parameters.orderId?if_exists}, false)/>
<script type="text/javascript">
	<#if orderHeader.statusId == "ORDER_APPROVED">
	$("#approveOrderId").hide();
	$("#holdOrderId").show();
	<#elseif orderHeader.statusId == "ORDER_HOLD">
	$("#approveOrderId").show();
	$("#holdOrderId").hide();
	</#if>
	$.jqx.theme = 'olbius';
	var theme = 'olbius';
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign allFacilityByStore = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "productStoreId", orderHeader.productStoreId?if_exists, "ownerPartyId", company)), null, null, null, false)>
	var facilityDataTemp = [];
	var orderFacToReturnData = [];
	<#list allFacilityByStore as item>
		var row = {};
		<#assign descFac = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['ownerPartyId']= "${item.ownerPartyId?if_exists}";
		row['description'] = "${descFac?if_exists?replace('\n', ' ')}";
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		facilityDataTemp.push(row);
		<#assign listInvInFac = delegator.findList("InventoryItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", item.facilityId)), null, null, null, false)>
		<#if listInvInFac?has_content>
			orderFacToReturnData.push(row);
		</#if>
	</#list>
	<#assign listStoreFacs = delegator.findList("ProductStoreFacility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", orderHeader.productStoreId?if_exists)), null, null, null, false)>
	<#if listStoreFacs?has_content>
		<#list listStoreFacs as storeFac>
			<#assign facTemp = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", storeFac.facilityId?if_exists), false)/>
			var check1 = false;
			for (var j = 0; j < facilityDataTemp.length; j ++){
				if ('${storeFac.facilityId}' == facilityDataTemp[j].facilityId){
					check1 = true;
				}
			}
			<#if company == facTemp.ownerPartyId>
				if (!check1){
					var rowTmp = {};
					<#assign descriptionTemp = StringUtil.wrapString(facTemp.facilityName?if_exists)/>
					rowTmp['facilityId'] = "${facTemp.facilityId?if_exists}";
					rowTmp['ownerPartyId']= "${facTemp.ownerPartyId?if_exists}";
					rowTmp['description'] = "${descriptionTemp?if_exists?replace('\n', ' ')}";
					rowTmp['productStoreId'] = "${facTemp.productStoreId?if_exists}";
					facilityDataTemp.push(rowTmp);
					<#assign listInvInFac = delegator.findList("InventoryItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facTemp.facilityId)), null, null, null, false)>
					<#if listInvInFac?has_content>
						orderFacToReturnData.push(rowTmp);
					</#if>
				}
			</#if>
		</#list>
	<#else>
	</#if>
	<#assign listReasonDatas = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "ORDER_PENDING_CODE")), null, null, null, false) />
	
	var listReasonData = new Array();
	<#list listReasonDatas as item>
		var row = {};
		row['enumId'] = "${item.enumId}";
		row['description'] = "${item.enumCode?if_exists} ${item.description?if_exists}";
		listReasonData[${item_index}] = row;
	</#list>
	
	<#assign reasons = delegator.findList("ReturnReason", null, null, null, null, false)>
	var returnReasonData = [];
	<#list reasons as item>
		<#assign descReason = StringUtil.wrapString(item.get("description", locale))>
		var row = {};
		row['returnReasonId'] = '${item.returnReasonId}';
		row['description'] = "${descReason?if_exists?replace('\n', ' ')}";
		returnReasonData.push(row);
	</#list>
	
	<#assign invItemStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_NON_SER_STTS"), null, null, null, false)>
	var invStatusData = [];
	<#list invItemStatus as item>
		<#assign descInvStatus = StringUtil.wrapString(item.get("description", locale))>
		var row = {};
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descInvStatus?if_exists}';
		invStatusData.push(row);
	</#list>
	var rowTmp = {};
	rowTmp['statusId'] = 'Good';
	rowTmp['description'] = '${uiLabelMap.InventoryGood}';
	invStatusData.push(rowTmp);
	
	var hasPermission = true;
	<#if hasOlbPermission("MODULE", "LOG_HOLD_REASON", "CREATE")>
		hasPermission = true;
	<#else>
		hasPermission = false;
	</#if>
	
	var orderId = "${orderHeader.orderId}";
	var GoToConfigToAddReason = "${uiLabelMap.GoToConfigToAddReason}";
</script>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.Enter1to60characters = "${StringUtil.wrapString(uiLabelMap.Enter1to60characters)}";
	uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureChange = "${StringUtil.wrapString(uiLabelMap.AreYouSureChange)}";
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.Cancel = "${StringUtil.wrapString(uiLabelMap.Cancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.PleaseChooseAReason = "${StringUtil.wrapString(uiLabelMap.PleaseChooseAReason)}";
	uiLabelMap.YouHavenotCreatePermission = "${StringUtil.wrapString(uiLabelMap.PleaseChooseAReason)}";
	uiLabelMap.PleaseEnterDescription = "${StringUtil.wrapString(uiLabelMap.PleaseEnterDescription)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.MustBeBeforeRequiredDate = "${StringUtil.wrapString(uiLabelMap.MustBeBeforeRequiredDate)}";
	
</script>