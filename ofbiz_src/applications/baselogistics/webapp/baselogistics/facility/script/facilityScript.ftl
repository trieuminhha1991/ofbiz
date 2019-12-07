<@jqGridMinimumLib/>
<script type="text/javascript">
<#assign isDistributor = Static["com.olbius.basesales.util.SalesPartyUtil"].isDistributor(delegator, userLogin.getString("partyId"))!/>
var isDistributor = false;
<#if isDistributor?has_content && isDistributor == true>
	isDistributor = true;
</#if>
</script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js?v=1.0.5"></script>
<script type="text/javascript" src="/logresources/js/facility/listFacility.js?v=1.1.3"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasComboBox=true/>
<#assign localeStr = "VI" />
<#if locale = "en">
	<#assign localeStr = "EN" />
</#if>

<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.FromDateMustBeLesserThanThruDate = "${StringUtil.wrapString(uiLabelMap.FromDateMustBeLesserThanThruDate)}";
	uiLabelMap.SearchByNameOrId = "${StringUtil.wrapString(uiLabelMap.SearchByNameOrId)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.CommonCancel	= "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.NameOfImagesMustBeLessThan50Character = "${StringUtil.wrapString(uiLabelMap.NameOfImagesMustBeLessThan50Character)}";
	uiLabelMap.Location	= "${StringUtil.wrapString(uiLabelMap.Location)}";
	uiLabelMap.Inventory = "${StringUtil.wrapString(uiLabelMap.Inventory)}";
	uiLabelMap.Edit	= "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.OK	= "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.Avatar = "${StringUtil.wrapString(uiLabelMap.Avatar)}";
	uiLabelMap.NoFile = "${StringUtil.wrapString(uiLabelMap.NoFile)}";
	uiLabelMap.Choose = "${StringUtil.wrapString(uiLabelMap.Choose)}";
	uiLabelMap.Change = "${StringUtil.wrapString(uiLabelMap.Change)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.PhoneNumberMustInOto9 = "${StringUtil.wrapString(uiLabelMap.PhoneNumberMustInOto9)}";
	uiLabelMap.PhoneNumberMustBeContain10or11character = "${StringUtil.wrapString(uiLabelMap.PhoneNumberMustBeContain10or11character)}";
	uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
	uiLabelMap.FacilityIdExisted = "${StringUtil.wrapString(uiLabelMap.FacilityIdExisted)}";
	uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.CheckLinkedData = "${StringUtil.wrapString(uiLabelMap.CheckLinkedData)}";
	uiLabelMap.ViewDetailInNewPage = "${StringUtil.wrapString(uiLabelMap.ViewDetailInNewPage)}";
	uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.BLEmployeeId = "${StringUtil.wrapString(uiLabelMap.BLEmployeeId)}";
	uiLabelMap.BLEmployeeName = "${StringUtil.wrapString(uiLabelMap.BLEmployeeName)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.Role = "${StringUtil.wrapString(uiLabelMap.Role)}";
	uiLabelMap.BLNotifyFacilityCodeExists = "${StringUtil.wrapString(uiLabelMap.BLNotifyFacilityCodeExists)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "AREA_MEASURE"), null, null, null, false)>
	
	var wardData = [];
	var listStoreKeeperParty = [];
	<#assign facs = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false) />
	var facilityData = new Array();
	<#list facs as item>
		var row = {};
		row['facilityId'] = "${item.facilityId}";
		row['parentFacilityId'] = "${item.parentFacilityId?if_exists}";
		row['facilityName'] = "${StringUtil.wrapString(item.facilityName?if_exists)}";
		facilityData[${item_index}] = row;
	</#list>
	
	<#assign countries = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY")), null, null, null, false) />
	var countryData = new Array();
	<#list countries as item>
		var row = {};
		row['geoId'] = "${item.geoId}";
		row['description'] = "${StringUtil.wrapString(item.geoName?if_exists)}";
		countryData[${item_index}] = row;
	</#list>
	
	<#assign stores = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", company)), null, null, null, false) />
	var prodStoreData = new Array();
	<#list stores as item>
		var row = {};
		row['productStoreId'] = "${item.productStoreId}";
		row['description'] = "${StringUtil.wrapString(item.storeName?if_exists)}";
		prodStoreData.push(row);
	</#list>
	
	<#assign addPerm = "false">
	var hiddenParent = false;
	var parentFacilityId = null;
	<#if parameters.facilityId?has_content>
		<#assign params="jqxGeneralServicer?sname=jqGetFacilities&parentFacilityId=${parameters.facilityId}&facilityTypeId=WAREHOUSE">
		<#assign title="ListChildFacility">
		hiddenParent = true;
		<#assign parentFacilityId= parameters.facilityId?if_exists>
		
		parentFacilityId = "${parameters.facilityId}";
	<#else>
		<#assign params="jqxGeneralServicer?sname=jqGetFacilities&facilityTypeId=WAREHOUSE&facilityGroupId=FACILITY_INTERNAL">
		<#assign title="ListFacility">
		hiddenParent = false;
	</#if>
	<#if hasOlbPermission("MODULE", "LOGISTICS", "ADMIN") || hasOlbPermission("MODULE", "LOG_FACILITY", "ADMIN")>
		<#assign addPerm = "true">
	</#if>
	<#if hasOlbPermission("MODULE", "LOGISTICS", "CREATE") || hasOlbPermission("MODULE", "LOG_FACILITY", "CREATE")>
		<#assign addPerm = "true">
	</#if>
	
	var glFacilityId = null;
	
	var sourceUom = [<#list listUoms as item>{key: '${item.uomId}', value: '${item.description}'}<#if item_index!=(listUoms?size)>,</#if></#list>];
</script>