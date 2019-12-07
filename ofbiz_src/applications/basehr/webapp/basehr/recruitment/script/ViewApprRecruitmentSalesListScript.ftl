<@jqGridMinimumLib/>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.statusArr = [
	<#if statusList?has_content>
		<#list statusList as status>
		{
			statusId: "${status.statusId}",
			description: "${StringUtil.wrapString(status.description)}"
		},
		</#list>
	</#if>
];
globalVar.customTimePeriodArr = [
	<#if customTimePeriodList?has_content>
		<#list customTimePeriodList as customTimePeriod>
		{
			customTimePeriodId: '${customTimePeriod.customTimePeriodId}',
			periodName: '${StringUtil.wrapString(customTimePeriod.periodName)}',
			fromDate: ${customTimePeriod.fromDate.getTime()},
			thruDate: ${customTimePeriod.thruDate.getTime()}
		},
		</#list>
	</#if>
];
globalVar.statusEmplRecArr = [
	<#if statusEmplRecList?has_content>
		<#list statusEmplRecList as status>
			{
				statusId: '${status.statusId}',
				description: "${StringUtil.wrapString(status.description?if_exists)}"
			},
		</#list>
	</#if>                
];
globalVar.recruitmentTypeEnumArr = [
	<#if recruitmentTypeEnumList?has_content>
		<#list recruitmentTypeEnumList as recruitmentTypeEnum>
		{
			enumId: "${recruitmentTypeEnum.enumId}",
			description: '${StringUtil.wrapString(recruitmentTypeEnum.description?if_exists)}'
		},
		</#list>
	</#if>
];
globalVar.genderList = [
     	<#if genderList?has_content>                   
	  		<#list genderList as gender1>
	  		{
	  			genderId : "${gender1.genderId}",
	  			description : "${StringUtil.wrapString(gender1.description)}"
	  		},
	  		</#list>	
	  	</#if>
];
uiLabelMap.CommonSubmit= "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose= "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.Sexual = "${StringUtil.wrapString(uiLabelMap.Sexual)}";
uiLabelMap.certProvisionId = "${StringUtil.wrapString(uiLabelMap.certProvisionId)}";
uiLabelMap.BirthDate = "${StringUtil.wrapString(uiLabelMap.BirthDate)}";
uiLabelMap.NativeLand = "${StringUtil.wrapString(uiLabelMap.NativeLand)}";
uiLabelMap.CurrentResidence = "${StringUtil.wrapString(uiLabelMap.CurrentResidence)}";
uiLabelMap.HRCommonEmail = "${StringUtil.wrapString(uiLabelMap.HRCommonEmail)}";
uiLabelMap.PhoneNumber = "${StringUtil.wrapString(uiLabelMap.PhoneNumber)}";
uiLabelMap.RecruitmentPosition = "${StringUtil.wrapString(uiLabelMap.RecruitmentPosition)}";
uiLabelMap.RecruitmentEnumType = "${StringUtil.wrapString(uiLabelMap.RecruitmentEnumType)}";
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
uiLabelMap.ListRecruitmentOffer = "${StringUtil.wrapString(uiLabelMap.ListRecruitmentOffer)}";
uiLabelMap.RecruitmentOffer = "${StringUtil.wrapString(uiLabelMap.RecruitmentOffer)}";
uiLabelMap.ListSalesmanNotApproval = "${StringUtil.wrapString(uiLabelMap.ListSalesmanNotApproval)}";
uiLabelMap.ApprRecruitmentSaleEmplConfirm = "${StringUtil.wrapString(uiLabelMap.ApprRecruitmentSaleEmplConfirm)}";
uiLabelMap.SalesmanProposalApproval = "${StringUtil.wrapString(uiLabelMap.SalesmanProposalApproval)}";
uiLabelMap.RecruitmentSaleEmplProprosalApprConfirm = "${StringUtil.wrapString(uiLabelMap.RecruitmentSaleEmplProprosalApprConfirm)}";
uiLabelMap.SummarySalesEmplListOfferd = '${StringUtil.wrapString(uiLabelMap.SummarySalesEmplListOfferd)}';
</script>
