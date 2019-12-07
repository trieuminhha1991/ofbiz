<@jqGridMinimumLib />
<style type="text/css">
.span7 label{
	text-align:right;
	width:300px;
} 
.ace-file-input .remove {
	right: 20px;
	padding-left: 2px;
}
.ace-file-input label span {
	float: left;
}
.ace-file-input {
    position: relative;
    height: 25px;
    line-height: 25px;
    margin-bottom: 0px;
}
</style>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "AREA_MEASURE"), null, null, null, false)>
	
	var wardData = [];
	
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
	
	var parentFacilityId = null;
	<#if parameters.parentFacilityId?has_content>
		parentFacilityId = "${parameters.parentFacilityId?if_exists}";
	</#if>
	
	<#assign hasPerm = true>
	<#assign addPerm = "false">
	<#if parameters.facilityId?has_content>
		<#assign params="jqxGeneralServicer?sname=getFacilities&parentFacilityId=${parameters.facilityId}&facilityTypeId=WAREHOUSE">
		<#assign title="ListChildFacility">
	<#else>
		<#assign params="jqxGeneralServicer?sname=getFacilities&facilityTypeId=WAREHOUSE">
		<#assign title="ListFacility">
	</#if>
	<#if hasOlbPermission("MODULE", "LOGISTICS", "ADMIN") || hasOlbPermission("MODULE", "LOG_FACILITY", "ADMIN")>
		<#assign addPerm = "true">
	<#elseif !hasOlbPermission("MODULE", "LOGISTICS", "VIEW") && !hasOlbPermission("MODULE", "LOG_FACILITY", "VIEW")>
		<#assign hasPerm = false>
	</#if>
	<#if hasOlbPermission("MODULE", "LOGISTICS", "CREATE") || hasOlbPermission("MODULE", "LOG_FACILITY", "CREATE")>
		<#assign addPerm = "true">
	</#if>

	<#if hasPerm = false>
		<div class="alert alert-danger">
			<strong>
				<i class="ace-icon fa fa-times"></i>
				${uiLabelMap.noViewPerm}
			</strong>
		</div>
	</#if>
	
	var glFacilityId = null;
	
	var sourceUom = [<#list listUoms as item>{key: '${item.uomId}', value: '${item.description}'}<#if item_index!=(listUoms?size)>,</#if></#list>];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
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
	uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
	uiLabelMap.FacilityIdExisted = "${StringUtil.wrapString(uiLabelMap.FacilityIdExisted)}";
	
	uiLabelMap.NoFile = "${StringUtil.wrapString(uiLabelMap.NoFile)}";
	uiLabelMap.Choose = "${StringUtil.wrapString(uiLabelMap.Choose)}";
	uiLabelMap.LOGChange = "${StringUtil.wrapString(uiLabelMap.LOGChange)}";
	
	uiLabelMap.FacilityIdExisted = "${StringUtil.wrapString(uiLabelMap.FacilityIdExisted)}";
	
</script>
<script type="text/javascript" src="/logresources/js/facility/facilityNewFacilityInfo.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js?v=1.0.5"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>