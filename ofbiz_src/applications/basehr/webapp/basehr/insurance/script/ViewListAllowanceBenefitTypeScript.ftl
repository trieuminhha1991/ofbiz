<@jqGridMinimumLib/>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript">
if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {};
}

uiLabelMap.HRSequenceNbr = "${StringUtil.wrapString(uiLabelMap.HRSequenceNbr)}";
uiLabelMap.IBCondition = "${StringUtil.wrapString(uiLabelMap.IBCondition)}";
uiLabelMap.IBAction = "${StringUtil.wrapString(uiLabelMap.IBAction)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.accDeleteSelectedRow = "${StringUtil.wrapString(uiLabelMap.accDeleteSelectedRow)}";
uiLabelMap.HRCommonContent = "${StringUtil.wrapString(uiLabelMap.HRCommonContent)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.ConfirmAddRuleBenefit = "${StringUtil.wrapString(uiLabelMap.ConfirmAddRuleBenefit)}";
uiLabelMap.NotifyDelete = "${StringUtil.wrapString(uiLabelMap.NotifyDelete)}";
uiLabelMap.CreateAllowanceBenefitTypeConfirm = "${StringUtil.wrapString(uiLabelMap.CreateAllowanceBenefitTypeConfirm)}";
uiLabelMap.ValueMustGreaterThanOne = "${StringUtil.wrapString(uiLabelMap.ValueMustGreaterThanOne)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
var globalVar = {};
globalVar.insBenActTimeArr = [
<#if insBenActTimeList?exists>
	<#list insBenActTimeList as insBenActTime>
	{
		enumId: "${insBenActTime.enumId}",
		description: "${insBenActTime.description}"
	},
	</#list>
</#if>
];

globalVar.insBenActSalArr = [
<#if insBenActSalList?exists>
	<#list insBenActSalList as insBenActSal>
	{
		enumId: "${insBenActSal.enumId}",
		description: "${insBenActSal.description}"
	},
	</#list> 
</#if>
];

globalVar.insBenCondOperArr = [
<#if insBenCondOperList?exists>
	<#list insBenCondOperList as insBenCondOper>
	{
		enumId: "${insBenCondOper.enumId}",
		description: "${insBenCondOper.description?if_exists}",
		enumCode: "${insBenCondOper.enumCode?if_exists}"
	},
	</#list> 
</#if>                         
];

globalVar.insBenParamArr = [
<#if insBenParamList?exists>
	<#list insBenParamList as insBenParam>
	{
		enumId: "${insBenParam.enumId}",
		description: "${insBenParam.description}"
	},
	</#list> 
</#if>                         
];

globalVar.uomArr = [
<#if uomList?exists>
	<#list uomList as uom>
	{
		uomId: "${uom.uomId}",
		abbreviation: '${uom.get("abbreviation", locale)}',
		description: "${uom.description?if_exists}"
	},
	</#list>
</#if>
];

globalVar.insBenefitTypeFreqArr = [
	<#if insBenefitTypeFreqList?has_content>
		<#list insBenefitTypeFreqList as insBenefitTypeFreq>
		{
			frequenceId: "${insBenefitTypeFreq.frequenceId}",
			description: "${StringUtil.wrapString(insBenefitTypeFreq.description)}",
		},
		</#list>
	</#if>
];
globalVar.insAllowanceBenefitClassTypeArr = [
	<#if insAllowanceBenefitClassTypeList?has_content>
		<#list insAllowanceBenefitClassTypeList as insAllowanceBenefitClassType>
		{
			benefitClassTypeId: "${insAllowanceBenefitClassType.benefitClassTypeId}",
			description: "${StringUtil.wrapString(insAllowanceBenefitClassType.description)}",
		},
		</#list>
	</#if>
];

</script>