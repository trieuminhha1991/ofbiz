<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
	var globalVar = {};
	
	globalVar.statusArr = [
		<#if KPIStatusList?has_content>
			<#list KPIStatusList as status>
			{
				statusId: '${status.statusId}',
				description: '${StringUtil.wrapString(status.description?if_exists)}'
			},
			</#list> 
		</#if>                       
	];
	globalVar.uomArr = [
		<#if uomList?has_content>
			<#list uomList as uom>
			{
				uomId: '${uom.uomId}',
				abbreviation: '${StringUtil.wrapString(uom.abbreviation?if_exists)}',
				description: '${StringUtil.wrapString(uom.description?if_exists)}'
			},
			</#list>
		</#if>
	];
	
	globalVar.perfCriteriaTypeArr = [
		<#if perfCriteriaTypeList?has_content>
			<#list perfCriteriaTypeList as perfCriteriaType>
			{
				perfCriteriaTypeId: '${perfCriteriaType.perfCriteriaTypeId}',
				description: '${StringUtil.wrapString(perfCriteriaType.description?if_exists)}'
			},
			</#list>
		</#if>
	];
	
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
	var uiLabelMap = {
			ThisFieldIsNotBeEmpty : '${StringUtil.wrapString(uiLabelMap.ThisFieldIsNotBeEmpty)}',
			RequireMustBeBetterThanNow : '${StringUtil.wrapString(uiLabelMap.RequireMustBeBetterThanNow)}',
			RequireValueGreaterThanFromDate : '${StringUtil.wrapString(uiLabelMap.RequireValueGreaterThanFromDate)}',
			CriteriaTypeId : '${StringUtil.wrapString(uiLabelMap.CriteriaTypeId)}',
			CriteriaTypeName : '${StringUtil.wrapString(uiLabelMap.CriteriaTypeName)}',
			CreateSuccess : '${StringUtil.wrapString(uiLabelMap.CreateSuccess)}',
			CreateNewCriteriaType : '${StringUtil.wrapString(uiLabelMap.CreateNewCriteriaType)}',
			CommonAddNew : '${StringUtil.wrapString(uiLabelMap.CommonAddNew)}',
			CommonDescription : '${StringUtil.wrapString(uiLabelMap.CommonDescription)}',
			CommonSubmit : '${StringUtil.wrapString(uiLabelMap.CommonSubmit)}',
			CommonClose : '${StringUtil.wrapString(uiLabelMap.CommonClose)}',
			CommonCancel : '${StringUtil.wrapString(uiLabelMap.CommonCancel)}',
			FieldRequired : '${StringUtil.wrapString(uiLabelMap.FieldRequired)}',
			CannotDeleteAfterCreate : '${StringUtil.wrapString(uiLabelMap.CannotDeleteAfterCreate)}',
			CreateKPIConfirm : '${StringUtil.wrapString(uiLabelMap.CreateKPIConfirm)}',
			CreateNewKPI : '${StringUtil.wrapString(uiLabelMap.CreateNewKPI)}',
			EditKPI : '${StringUtil.wrapString(uiLabelMap.EditKPI)}',
        	KPIToRating : '${StringUtil.wrapString(uiLabelMap.KPIToRating)}',
        	KPIFromRating : '${StringUtil.wrapString(uiLabelMap.KPIFromRating)}',
			HRCommonAmount : '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}',
			CommonStatus : '${StringUtil.wrapString(uiLabelMap.CommonStatus)}',
			SetupKPIPolicy : '${StringUtil.wrapString(uiLabelMap.SetupKPIPolicy)}',
			CommonAddNew : '${StringUtil.wrapString(uiLabelMap.CommonAddNew)}',
			WrongSetup : '${StringUtil.wrapString(uiLabelMap.WrongSetup)}',
			createSuccessfully : '${StringUtil.wrapString(uiLabelMap.createSuccessfully)}',
			HRUpdateConfirm : '${StringUtil.wrapString(uiLabelMap.HRUpdateConfirm)}',
			updateSuccessfully : '${StringUtil.wrapString(uiLabelMap.updateSuccessfully)}',
			KpiPunishment : '${StringUtil.wrapString(uiLabelMap.KpiPunishment)}',
			KpiReward : '${StringUtil.wrapString(uiLabelMap.KpiReward)}',
			FromDateLessThanEqualThruDate : '${StringUtil.wrapString(uiLabelMap.FromDateLessThanEqualThruDate)}',
			KpiPolicy : '${StringUtil.wrapString(uiLabelMap.KpiPolicy)}',
			KpiPolicyId : '${StringUtil.wrapString(uiLabelMap.KpiPolicyId)}',
			CommonFromDate : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}',
			CommonThruDate : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}',
			HRContainSpecialSymbol : '${StringUtil.wrapString(uiLabelMap.HRContainSpecialSymbol)}',
			TimeSetupCoincidence : '${StringUtil.wrapString(uiLabelMap.TimeSetupCoincidence)}',
			RewardPunishment : '${StringUtil.wrapString(uiLabelMap.RewardPunishment)}',
			KpiPolicyItemPointValidate : '${StringUtil.wrapString(uiLabelMap.KpiPolicyItemPointValidate)}',
			PointCannotBeNagative : '${StringUtil.wrapString(uiLabelMap.PointCannotBeNagative)}',
	};
</script>