<@jqOlbCoreLib hasValidator=true/>
<script type="text/javascript" src="/salesresources/js/party/contactNewShippingAddressPopup.js"></script>
<script type="text/javascript">
	<#assign countryGeoId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCountryGeo(delegator)!/>
	<#assign organizationCurrent = Static['com.olbius.basesales.util.SalesUtil'].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	var dataContactNew = {};
	dataContactNew.countryGeoId = <#if countryGeoId?exists>'${countryGeoId}'<#else>null</#if>;
	dataContactNew.stateProvinceGeoId = "<#if organizationCurrent?exists && "MN" == organizationCurrent>VNM-HCM<#else>VNM-HN2</#if>";
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.BSYes = "${StringUtil.wrapString(uiLabelMap.BSYes)}";
	uiLabelMap.BSNo = "${StringUtil.wrapString(uiLabelMap.BSNo)}";
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";
	
	jOlbUtil.setUiLabelMap("wgupdatesuccess", uiLabelMap.wgupdatesuccess);
</script>