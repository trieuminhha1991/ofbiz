<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.defaultCountryGeoId = "${defaultCountryGeoId?if_exists}";
globalVar.countryGeoArr = [
	<#if countryGeoList?has_content>
		<#list countryGeoList as geo>
		{
			geoId: '${StringUtil.wrapString(geo.geoId)}',
			geoName: "${StringUtil.wrapString(geo.geoName)}",
		},
		</#list>
	</#if>
];

<#assign partys = delegator.findList("PartyCustomerAndDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", "${parameters.partyId}"), null, null, null, false) />
<#assign party = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partys)>
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.EmailRequired = "${StringUtil.wrapString(uiLabelMap.EmailRequired)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CreateCustomerConfirm = "${StringUtil.wrapString(uiLabelMap.CreateCustomerConfirm)}";
uiLabelMap.ExpireCustomerRelationshipConfirm = "${StringUtil.wrapString(uiLabelMap.ExpireCustomerRelationshipConfirm)}";
</script>