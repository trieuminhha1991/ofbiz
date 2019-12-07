<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">	
	var path = null;
	<#assign facs = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false) />
	var facilityData = new Array();
	<#list facs as item>
		var row = {};
		row['facilityId'] = "${item.facilityId}";
		row['parentFacilityId'] = "${item.parentFacilityId?if_exists}";
		row['facilityName'] = "${StringUtil.wrapString(item.facilityName?if_exists)}";
		facilityData[${item_index}] = row;
	</#list>
	
	<#assign stores = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", company)), null, null, null, false) />
	var prodStoreData = new Array();
	<#list stores as item>
		var row = {};
		row['productStoreId'] = "${item.productStoreId}";
		row['description'] = "${StringUtil.wrapString(item.storeName?if_exists)}";
		prodStoreData.push(row);
	</#list>
	
	<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "AREA_MEASURE"), null, null, null, false)>
	var areaUomData = new Array();
	<#list listUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.abbreviation?if_exists)}";
		areaUomData.push(row);
	</#list>
	
	var listStoreKeeperParty = [];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.NameOfImagesMustBeLessThan50Character = "${StringUtil.wrapString(uiLabelMap.NameOfImagesMustBeLessThan50Character)}";
	uiLabelMap.Avatar = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.HasErrorWhenProcess = "${StringUtil.wrapString(uiLabelMap.HasErrorWhenProcess)}";
	uiLabelMap.BLNotifyFacilityCodeExists = "${StringUtil.wrapString(uiLabelMap.BLNotifyFacilityCodeExists)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
</script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js?v=1.0.5"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=1.0.5"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/facility/facilityNewFacilityTemplate.js?v=1.1.1"></script>