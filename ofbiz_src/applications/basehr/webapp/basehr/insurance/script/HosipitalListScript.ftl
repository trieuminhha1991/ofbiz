<script type="text/javascript">
var uiLabelMapHospialList = {
		MedicalPlace: '${StringUtil.wrapString(uiLabelMap.MedicalPlace)}'	,
		InsuranceHospital: '${StringUtil.wrapString(uiLabelMap.InsuranceHospital)}',
		PartyState: '${uiLabelMap.PartyState}',
		accAddNewRow: '${StringUtil.wrapString(uiLabelMap.accAddNewRow)}',
		FieldRequired: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}',
		MustntHaveSpaceChar : '${StringUtil.wrapString(uiLabelMap.MustntHaveSpaceChar)}',
		updateSuccessfully : '${StringUtil.wrapString(uiLabelMap.updateSuccessfully)}',
		InvalidChar : '${StringUtil.wrapString(uiLabelMap.InvalidChar)}',
};
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
<#if defaultSuffix?has_content>
globalVar.defaultSuffix = "${defaultSuffix}";
<#else>
globalVar.defaultSuffix = "";	
</#if>

if(typeof(stateProvinceGeoArr) == 'undefined'){
	var stateProvinceGeoArr = [
      	<#if listStateProvinceGeoVN?has_content>
      		<#list listStateProvinceGeoVN as geo>
      			{
      				geoId: '${geo.geoId}',
      				geoName: '${StringUtil.wrapString(geo.geoName)}',
      				codeNumber: '${geo.codeNumber?if_exists}'
      			},
      		</#list>
      	</#if>
   ];
}
</script>
