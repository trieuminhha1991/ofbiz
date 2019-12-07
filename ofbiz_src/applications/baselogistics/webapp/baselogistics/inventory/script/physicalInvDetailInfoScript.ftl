<style type="text/css">
.destFaContainer {
    display: none;
}
</style>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	var company = '${company?if_exists}';
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
	var facilityData = [
	   	<#if facilities?exists>
	   		<#list facilities as item>
	   			{
	   				facilityId: "${item.facilityId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false)>
	var quantityUomData = [
	   	<#if uoms?exists>
	   		<#list uoms as item>
	   			{
	   				uomId: "${item.uomId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	<#assign physicalInventory = delegator.findOne("PhysicalInventoryDetail", {"physicalInventoryId" : parameters.physicalInventoryId?if_exists}, false)/>
	var physicalInvObj = {};
	physicalInvObj['physicalInventoryId'] = '${physicalInventory.physicalInventoryId?if_exists}';
	physicalInvObj['physicalInventoryDate'] = '${physicalInventory.physicalInventoryDate?if_exists}';
	physicalInvObj['partyId'] = '${physicalInventory.partyId?if_exists}';
	physicalInvObj['facilityId'] = '${physicalInventory.facilityId?if_exists}';
	physicalInvObj['facilityName'] = '${physicalInventory.facilityName?if_exists}';
	
	var temp = "${StringUtil.wrapString(physicalInventory.generalComments?if_exists)}";
	physicalInvObj['generalComments'] = temp;
	<#assign partyDetail = Static["com.olbius.basehr.util.PartyUtil"].getPersonName(delegator, physicalInventory.partyId?if_exists)/>
	<#assign party = delegator.findOne("Party", {"partyId" : physicalInventory.partyId?if_exists}, false)/>
	
	var partyFullName = "";
	<#if partyDetail?has_content>
		partyFullName = "${partyDetail} [${party.partyCode?if_exists}]";
	<#else>
	 	partyFullName = "_NA_";
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	
	function escapeHtml(unsafe) {
	    return unsafe
	    .replace(/[\\]/g, '\\\\')
	    .replace(/[\"]/g, '\\\"')
	    .replace(/[\/]/g, '\\/')
	    .replace(/[\b]/g, '\\b')
	    .replace(/[\f]/g, '\\f')
	    .replace(/[\n]/g, '\\n')
	    .replace(/[\r]/g, '\\r')
	    .replace(/[\t]/g, '\\t');
	}
	
</script>
<script type="text/javascript" src="/logresources/js/inventory/physicalInvDetailInfo.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>