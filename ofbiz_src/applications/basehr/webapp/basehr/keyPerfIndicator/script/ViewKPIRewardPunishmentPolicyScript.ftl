<@jqGridMinimumLib/>
<#assign listEnumeration = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "KPI_CALC_TYPE"), null, null, null, false)!/>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
    var globalVar = {};
    globalVar.enumKpi = [
         <#if enumKpIList?has_content>
             <#list enumKpIList as e>
         		{
                    enumId : '${e.enumId}',
                    description : '${e.description?if_exists}'
                },
             </#list>
         </#if>
    ];
    var listEnumerationsCalcType = [
        <#if enumerationsCalcType?exists>
            <#list enumerationsCalcType as item>
                {
                    enumId: "${item.enumId?if_exists}",
                    description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
                },
            </#list>
        </#if>
    ];

var uiLabelMap = {};
uiLabelMap.PerfCriteriaRateLevelList = "${StringUtil.wrapString(uiLabelMap.PerfCriteriaRateLevelList)}";
uiLabelMap.HRCommonClassification = "${StringUtil.wrapString(uiLabelMap.HRCommonClassification)}";
uiLabelMap.CommonFrom = "${StringUtil.wrapString(uiLabelMap.CommonFrom)}";
uiLabelMap.HRCommonToUppercase = "${StringUtil.wrapString(uiLabelMap.HRCommonToUppercase)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.PerfCriteriaRateLevelList = "${StringUtil.wrapString(uiLabelMap.PerfCriteriaRateLevelList)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ValueMustGreaterOrEqualThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustGreaterOrEqualThanZero)}";
uiLabelMap.ValueMustGreaterThanFrom = "${StringUtil.wrapString(uiLabelMap.ValueMustGreaterThanFrom)}";
uiLabelMap.ValueMustBeGreateThanEffectiveDate = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanEffectiveDate)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.ConfirmCreateNewKPIRating = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateNewKPIRating)}";
uiLabelMap.ConfirmCreatePerfCriteriaPolicy = "${StringUtil.wrapString(uiLabelMap.ConfirmCreatePerfCriteriaPolicy)}";
uiLabelMap.CreateRewardPunishmentKPIPolicy = "${StringUtil.wrapString(uiLabelMap.CreateRewardPunishmentKPIPolicy)}";
uiLabelMap.EditRewardPunishmentKPIPolicy = "${StringUtil.wrapString(uiLabelMap.EditRewardPunishmentKPIPolicy)}";
uiLabelMap.KPIFromRating = "${StringUtil.wrapString(uiLabelMap.KPIFromRating)}";
uiLabelMap.KPIToRating = "${StringUtil.wrapString(uiLabelMap.KPIToRating)}";
uiLabelMap.HRCommonAmount = "${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}";
uiLabelMap.RewardPunishment = "${StringUtil.wrapString(uiLabelMap.RewardPunishment)}";
uiLabelMap.SetupKPIPolicy = "${StringUtil.wrapString(uiLabelMap.SetupKPIPolicy)}";
uiLabelMap.HRKeyPerfIndApplyId = "${StringUtil.wrapString(uiLabelMap.HRKeyPerfIndApplyId)}";
uiLabelMap.CommonEdit = "${StringUtil.wrapString(uiLabelMap.CommonEdit)}";
uiLabelMap.HREditKPIPolicy = "${StringUtil.wrapString(uiLabelMap.HREditKPIPolicy)}";
uiLabelMap.HRCalcTypeEnumId = "${StringUtil.wrapString(uiLabelMap.HRCalcTypeEnumId)}";
uiLabelMap.HRCalcExpression = "${StringUtil.wrapString(uiLabelMap.HRCalcExpression)}";
uiLabelMap.HRCalcFormula = "${StringUtil.wrapString(uiLabelMap.HRCalcFormula)}";
uiLabelMap.HRCalcConst = "${StringUtil.wrapString(uiLabelMap.HRCalcConst)}";
uiLabelMap.HRBaseOnTarget = "${StringUtil.wrapString(uiLabelMap.HRBaseOnTarget)}";
uiLabelMap.HRBaseOnActual = "${StringUtil.wrapString(uiLabelMap.HRBaseOnActual)}";
</script>