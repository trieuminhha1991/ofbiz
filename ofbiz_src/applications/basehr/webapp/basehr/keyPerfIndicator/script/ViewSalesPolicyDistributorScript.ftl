<@jqGridMinimumLib/>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};

<#assign accSAM = Static['com.olbius.basehr.util.SecurityUtil'].hasRole("SALES_MANAGER", userLogin.getString("partyId"), delegator)!/>;
<#assign accSADM = Static['com.olbius.basehr.util.SecurityUtil'].hasRole("SALESADMIN_MANAGER", userLogin.getString("partyId"), delegator)!/>;
<#if accSADM || accSAM>
    <#assign addrow = "true" />
    globalVar.isSAM = true;
<#else>
    <#assign addrow = "false" />
    globalVar.isSAM = false;
</#if>

globalVar.enumOperatorArr = [
		<#if enumOperatorList?has_content>
			<#list enumOperatorList as enumOperator>
			{
				enumId: "${enumOperator.enumId}",
				enumCode : "${enumOperator.enumCode}",
				description: "${enumOperator.description}"
			},
			</#list>
		</#if>
];
globalVar.enumOperatorGreaterArr = [
	<#if enumOperatorGreaterList?has_content>
		<#list enumOperatorGreaterList as enumOperator>
		{
			enumId: "${enumOperator.enumId}",
			enumCode : "${enumOperator.enumCode}",
			description: "${enumOperator.description}"
		},
		</#list>
	</#if>
];
globalVar.enumOperatorLessThanArr = [
	<#if enumOperatorLessThanList?has_content>
		<#list enumOperatorLessThanList as enumOperator>
		{
			enumId: "${enumOperator.enumId}",
			enumCode : "${enumOperator.enumCode}",
			description: "${enumOperator.description}"
		},
		</#list>
	</#if>
];

globalVar.ruleEnumArr = [
	<#if ruleEnumList?has_content>
		<#list ruleEnumList as enumRule>
		{
			enumId: "${enumRule.enumId}",
			enumCode : "${enumRule.enumCode}",
			description: "${StringUtil.wrapString(enumRule.description)}"
		},
		</#list>
	</#if>
];

uiLabelMap.DAProductName = "${StringUtil.wrapString(uiLabelMap.DAProductName)}";
uiLabelMap.HRCommonQuantity = "${StringUtil.wrapString(uiLabelMap.HRCommonQuantity)}";
uiLabelMap.TargetByProduct = "${StringUtil.wrapString(uiLabelMap.TargetByProduct)}";
uiLabelMap.BSCondition = "${StringUtil.wrapString(uiLabelMap.BSCondition)}";
uiLabelMap.BSAnd = "${StringUtil.wrapString(uiLabelMap.BSAnd)}";
uiLabelMap.BonusLevel = "${StringUtil.wrapString(uiLabelMap.BonusLevel)}";
uiLabelMap.BSAddNew = "${StringUtil.wrapString(uiLabelMap.BSAddNew)}";
uiLabelMap.SalesBonus = "${StringUtil.wrapString(uiLabelMap.SalesBonus)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.SKUBonus = "${StringUtil.wrapString(uiLabelMap.SKUBonus)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ValueIsInvalid = "${StringUtil.wrapString(uiLabelMap.ValueIsInvalid)}";
uiLabelMap.ConditionIsNotSetting = "${StringUtil.wrapString(uiLabelMap.ConditionIsNotSetting)}";
uiLabelMap.TurnoverActual = "${StringUtil.wrapString(uiLabelMap.TurnoverActual)}";
uiLabelMap.SKUCompletionPercent = "${StringUtil.wrapString(uiLabelMap.SKUCompletionPercent)}";
uiLabelMap.ActualTargetPercent = "${StringUtil.wrapString(uiLabelMap.ActualTargetPercent)}";
uiLabelMap.MustGreaterThanEffectiveDate = "${StringUtil.wrapString(uiLabelMap.MustGreaterThanEffectiveDate)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.HrCreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.HrCreateNewConfirm)}";
uiLabelMap.TurnoverActualShort = "${StringUtil.wrapString(uiLabelMap.TurnoverActualShort)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.CommonEdit = "${StringUtil.wrapString(uiLabelMap.CommonEdit)}";
uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
uiLabelMap.NotifyDelete = "${StringUtil.wrapString(uiLabelMap.NotifyDelete)}";
</script>