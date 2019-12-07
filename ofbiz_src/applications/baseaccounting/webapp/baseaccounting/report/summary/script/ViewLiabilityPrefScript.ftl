<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
uiLabelMap.BACCOrganizationId = "${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}";
uiLabelMap.BACCFullName = "${StringUtil.wrapString(uiLabelMap.BACCFullName)}";
uiLabelMap.BACCListObject = "${StringUtil.wrapString(uiLabelMap.BACCListObject)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.BACCSeqId = "${StringUtil.wrapString(uiLabelMap.BACCSeqId)}";
uiLabelMap.BACCProductName = "${StringUtil.wrapString(uiLabelMap.BACCProductName)}";
uiLabelMap.BACCEquipQuantityUom = "${StringUtil.wrapString(uiLabelMap.BACCEquipQuantityUom)}";
uiLabelMap.BACCQuantity = "${StringUtil.wrapString(uiLabelMap.BACCQuantity)}";
uiLabelMap.BACCUnitPrice = "${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}";
uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCTotal)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.FromDateLessThanEqualThruDate = "${StringUtil.wrapString(uiLabelMap.FromDateLessThanEqualThruDate)}";
uiLabelMap.BSExportExcel = "${StringUtil.wrapString(uiLabelMap.BSExportExcel)}";
uiLabelMap.ReportCheckNotData = "${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}";
uiLabelMap.BACCXuatTraNCC = "${StringUtil.wrapString(uiLabelMap.BACCXuatTraNCC)}";
uiLabelMap.BACCXuatTraKH = "${StringUtil.wrapString(uiLabelMap.BACCXuatTraKH)}";
uiLabelMap.BACCBenAThanhToanChoBenB = "${StringUtil.wrapString(uiLabelMap.BACCBenAThanhToanChoBenB)}";
uiLabelMap.BACCBenBThanhToanChoBenA = "${StringUtil.wrapString(uiLabelMap.BACCBenBThanhToanChoBenA)}";
globalVar.enumPartyTypeArr = [
	<#if enumPartyTypeList?exists>
		<#list enumPartyTypeList as enumPartyType>
		{
			enumId: "${enumPartyType.enumId}",
			description: '${StringUtil.wrapString(enumPartyType.description)}'
		},
		</#list>
	</#if>
];

<#assign infoOrg = dispatcher.runSync("getPartyPostalAddress",{"userLogin" : userLogin,"partyId" : "${parameters.organizationPartyId?if_exists}", "contactMechPurposeTypeId": "PRIMARY_LOCATION"})>
<#assign telephoneOrg = dispatcher.runSync("getPartyTelephone",{"userLogin" : userLogin,"partyId" : "${parameters.organizationPartyId?if_exists}", "contactMechPurposeTypeId": "PRIMARY_PHONE"})>
<#assign faxOrg = dispatcher.runSync("getPartyTelephone",{"userLogin" : userLogin,"partyId" : "${parameters.organizationPartyId?if_exists}", "contactMechPurposeTypeId": "FAX_NUMBER"})>
<#assign party = delegator.findOne("PartyGroup", {"partyId" : parameters.organizationPartyId?if_exists}, false)/>
globalVar.orgId = '${parameters.organizationPartyId?if_exists}';
var address = '${StringUtil.wrapString(infoOrg.address1?if_exists)}';
<#assign districtGeo = delegator.findOne("Geo", {"geoId", infoOrg.districtGeoId?if_exists}, false) />;
<#assign stateProvinceGeo = delegator.findOne("Geo", {"geoId", infoOrg.stateProvinceGeoId?if_exists}, false) />;
<#assign countryGeo = delegator.findOne("Geo", {"geoId", infoOrg.countryGeoId?if_exists}, false) />;
<#if districtGeo?has_content>
	address += ", " + '${StringUtil.wrapString(districtGeo.geoName?if_exists)}';
</#if>
<#if stateProvinceGeo?has_content>
	address += ", " + '${StringUtil.wrapString(stateProvinceGeo.geoName?if_exists)}';
</#if>
<#if countryGeo?has_content>
	address += ", " + '${StringUtil.wrapString(countryGeo.geoName?if_exists)}';
</#if>
globalVar.orgAddress = address;
globalVar.orgFullName = '${StringUtil.wrapString(party.groupName?if_exists)}';
globalVar.orgTelephone = '${telephoneOrg.contactNumber?if_exists}';
globalVar.orgFax = '${faxOrg.contactNumber?if_exists}';
</script>