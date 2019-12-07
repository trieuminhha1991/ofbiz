<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};

<#assign accSAM = Static['com.olbius.basehr.util.SecurityUtil'].hasRole("SALES_MANAGER", userLogin.getString("partyId"), delegator)!/>;
<#assign accSADM = Static['com.olbius.basehr.util.SecurityUtil'].hasRole("SALESADMIN_MANAGER", userLogin.getString("partyId"), delegator)!/>;
<#if accSADM || accSAM>
    <#assign addrow = "true" />
    <#assign editable = "true" />
<#else>
    <#assign addrow = "false" />
    <#assign editable = "false" />
</#if>

globalVar.statusArr = [
                       
];
globalVar.salesYearCustArr = [
	<#if salesYearCustList?has_content>
		<#list salesYearCustList as salesYearCust>
		<#assign fromDate = salesYearCust.fromDate/>
		<#assign cal = Static["java.util.Calendar"].getInstance()/>
		${cal.setTimeInMillis(fromDate.getTime())}
		{
			customTimePeriodId: "${salesYearCust.customTimePeriodId}",
			periodName: "${StringUtil.wrapString(salesYearCust.periodName)}",
			year: ${cal.get(Static["java.util.Calendar"].YEAR)}
		},
		</#list>
	</#if>
];
var uiLabelMap = {};
uiLabelMap.ConfirmCreatePerfCriteriaAssessDistributor = "${StringUtil.wrapString(uiLabelMap.ConfirmCreatePerfCriteriaAssessDistributor)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.BSDistributorId = "${StringUtil.wrapString(uiLabelMap.BSDistributorId)}";
uiLabelMap.DADistributorName = "${StringUtil.wrapString(uiLabelMap.DADistributorName)}";
uiLabelMap.HRCommonBonus = "${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}";
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
uiLabelMap.DistributorInSumarizing = "${StringUtil.wrapString(uiLabelMap.DistributorInSumarizing)}";
uiLabelMap.DAListDistributor = "${StringUtil.wrapString(uiLabelMap.DAListDistributor)}";
uiLabelMap.TargetByProduct = "${StringUtil.wrapString(uiLabelMap.TargetByProduct)}";
uiLabelMap.Target = "${StringUtil.wrapString(uiLabelMap.Target)}";
uiLabelMap.HRCommonActual = "${StringUtil.wrapString(uiLabelMap.HRCommonActual)}";
uiLabelMap.HRCommonBonus = "${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}";
uiLabelMap.SalesBonusDistributor = "${StringUtil.wrapString(uiLabelMap.SalesBonusDistributor)}";
uiLabelMap.SummarizingBonusDistributorDetail = "${StringUtil.wrapString(uiLabelMap.SummarizingBonusDistributorDetail)}";
uiLabelMap.EffectiveFromDate = "${StringUtil.wrapString(uiLabelMap.EffectiveFromDate)}";
uiLabelMap.CommonThruDate = "${StringUtil.wrapString(uiLabelMap.CommonThruDate)}";
uiLabelMap.BonusPolicyNameShort = "${StringUtil.wrapString(uiLabelMap.BonusPolicyNameShort)}";
uiLabelMap.BonusPolicyNameAppl = "${StringUtil.wrapString(uiLabelMap.BonusPolicyNameAppl)}";
uiLabelMap.ViewSummarizingBonusDistributor = "${StringUtil.wrapString(uiLabelMap.ViewSummarizingBonusDistributor)}";
uiLabelMap.SalesBonusPolicyIsNotSelected = "${StringUtil.wrapString(uiLabelMap.SalesBonusPolicyIsNotSelected)}";
uiLabelMap.SalesBonusPolicyIsNotCreated = "${StringUtil.wrapString(uiLabelMap.SalesBonusPolicyIsNotCreated)}";
uiLabelMap.HrCreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.HrCreateNewConfirm)}";
</script>