<@jqGridMinimumLib/>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
<#if security.hasEntityPermission("HR_AGREEMENT", "_ADMIN", session)>
	globalVar.hasPermission = true;
<#else>
	globalVar.hasPermission = false;
</#if>
if(typeof(globalVar.agreementTypeArr) == 'undefined'){
	globalVar.agreementTypeArr = [
		<#if agreementTypeList?exists>
			<#list agreementTypeList as agreementType>
				{
					agreementTypeId: "${agreementType.agreementTypeId}",
					description: "${StringUtil.wrapString(agreementType.description)}"
				},
			</#list>
		</#if>
	];
}

globalVar.agreementDuration = [
	<#if agreementDurationList?exists>
	<#list agreementDurationList as periodType>
		{
			periodTypeId: '${periodType.periodTypeId}',
			description: '${StringUtil.wrapString(periodType.description)}',
			periodLength: ${periodType.periodLength},
			uomId: '${periodType.uomId}'
		},
	</#list>
	</#if>
];

globalVar.agreementPeriodUomArr = [
	<#if agreementPeriodUomList?has_content>
		<#list agreementPeriodUomList as uom>
		{
			uomId: '${uom.uomId}',
			abbreviation: '${StringUtil.wrapString(uom.get("abbreviation", locale))}',
			description: '${StringUtil.wrapString(uom.get("description", locale))}'
		},
		</#list>
	</#if>
];
globalVar.emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
		{
			emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
			description: '${StringUtil.wrapString(emplPositionType.get("description", locale))}'
		},
		</#list>
	</#if>
];
globalVar.agreementStatus = [
	{
		statusId: "",
		description: "${StringUtil.wrapString(uiLabelMap.CommonAll)}"
	},                          
	<#if agreementStatusList?exists>
	<#list agreementStatusList as status>
		{
			statusId: "${status.statusId}",
			description: "${StringUtil.wrapString(status.description)}"
		},
	</#list>
	</#if>                         
];
</script>