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
	
	var facilityData = [];
	<#assign manager = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "roleType.manager")>
	<#assign listRoles = []>
	<#assign listRoles = listRoles + [manager]>
	
	<#assign listFacilities = Static['com.olbius.baselogistics.util.LogisticsPartyUtil'].getFacilityByRolesAndFacilityType(delegator, userLogin.partyId?if_exists, listRoles, "WAREHOUSE")! />;
	<#list listFacilities as item>
		var row = {};
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['description'] = "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}";
		facilityData.push(row);
	</#list>
	
	var partyData = [];
	<#if !hasOlbPermission("ENTITY", "LOGISTICS", "VIEW")>
		var row = {};
		row['partyId'] = "${userLogin.partyId?if_exists}";
		<#assign name = Static['com.olbius.basehr.util.PartyUtil'].getPersonName(delegator, userLogin.partyId?if_exists)!>
		<#if name?has_content>
			row['description'] = "${name?if_exists}";
		<#else>
			row['description'] = "${userLogin.partyId?if_exists}";
		</#if>
		partyData.push(row);
	<#else>
		<#assign listParties = Static['com.olbius.basehr.util.SecurityUtil'].getPartiesByRolesWithCurrentOrg(userLogin, Static['org.ofbiz.base.util.UtilProperties'].getPropertyValue('baselogistics.properties', 'role.storekeeper'), delegator)>
		<#assign listParties2 = Static['com.olbius.basehr.util.SecurityUtil'].getPartiesByRolesWithCurrentOrg(userLogin, Static['org.ofbiz.base.util.UtilProperties'].getPropertyValue('baselogistics.properties', 'role.manager.specialist'), delegator)>
		<#assign listParties = listParties + listParties2>
		<#list listParties as item>
			var partyIdTmp = "${item?if_exists}";
			var row = {};
			row['partyId'] = partyIdTmp;
			<#assign name = Static['com.olbius.basehr.util.PartyUtil'].getPersonName(delegator, item?if_exists)>
			<#assign party = delegator.findOne("Party", {"partyId" : item?if_exists}, false)/>
			row['description'] = "${StringUtil.wrapString(name?if_exists)} - ${StringUtil.wrapString(party.partyCode?if_exists)}";
			var check = false;
			for (var i = 0; i < partyData.length; i ++){
				if (partyData[i].partyId == partyIdTmp){
					check = true; break;
				}
			}
			if (!check){
				partyData.push(row);
			}
		</#list>
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
	uiLabelMap.CannotAfterNow = "${uiLabelMap.CannotAfterNow}";
	
</script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/inventory/newPhysicalInventoryInfo.js"></script>