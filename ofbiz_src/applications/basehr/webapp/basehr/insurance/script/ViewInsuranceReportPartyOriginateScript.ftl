<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var uiLabelMap = {};
var globalVar = {};
globalVar.reportId = "${reportId}";
globalVar.genderArr = [
	<#if genderList?has_content>
		<#list genderList as gender>
			{
				genderId: "${gender.genderId}",
				description: "${StringUtil.wrapString(gender.description)}"
			},
		</#list>
	</#if>
];
globalVar.insuranceOriginateTypeArr = [
	<#if insuranceOriginateTypeList?has_content>
		<#list insuranceOriginateTypeList as insuranceOriginateType>
			{
				insuranceOriginateTypeId: "${insuranceOriginateType.insuranceOriginateTypeId}",
				description: "${StringUtil.wrapString(insuranceOriginateType.description)}"
			},
		</#list>
	</#if>
];

</script>