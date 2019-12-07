<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpopover.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
var globalVar = {};
var uiLabelMap = {};
<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
	globalVar.createContextMenu = true;
<#else>
	globalVar.createContextMenu= false;
</#if>
globalVar.userLoginPartyId = "${userLogin.partyId}";
globalVar.startDate = ${startDate.getTime()};
globalVar.emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
		{
			emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
			description: '${StringUtil.wrapString(emplPositionType.description)}',
		},			
		</#list>
	</#if>
];
<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
	globalVar.hasPermission = true;
<#else>	
	globalVar.hasPermission = false;
</#if>
globalVar.recruitmentFormTypeArr = [
	<#if recruitmentFormTypeList?has_content>
		<#list recruitmentFormTypeList as recruitmentFormType>
		{
			recruitmentFormTypeId: '${recruitmentFormType.recruitmentFormTypeId}',
			description: '${StringUtil.wrapString(recruitmentFormType.description)}'
		},
		</#list>
	</#if>
];

globalVar.rootPartyArr =  [
	<#if rootOrgList?has_content>
		<#list rootOrgList as rootOrgId>
		<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
		{
			partyId: "${rootOrgId}",
			partyName: "${rootOrg.groupName}"
		},
		</#list>
	</#if>
];

globalVar.statusArr = [
	<#if statusList?has_content>
		<#list statusList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description)}'
		},
		</#list>
	</#if>
];

globalVar.recruitReqEnumArr = [
	<#if recruitReqEnumList?has_content>
		<#list recruitReqEnumList as enumRecruitReq>
		{
			enumId: "${enumRecruitReq.enumId}",
			description: "${StringUtil.wrapString(enumRecruitReq.description)}"
		},
		</#list>
	</#if>                               
];
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
<#if selectYearCustomTimePeriodId?has_content>
	globalVar.selectYearCustomTimePeriodId = "${selectYearCustomTimePeriodId}";
</#if>
uiLabelMap.RecruitmentCriteria = "${StringUtil.wrapString(uiLabelMap.RecruitmentCriteria)}"; 
uiLabelMap.removeRecruitmentCriteria = "${StringUtil.wrapString(uiLabelMap.removeRecruitmentCriteria)}"; 
uiLabelMap.HRAddCondition = "${StringUtil.wrapString(uiLabelMap.HRAddCondition)}"; 
uiLabelMap.HRRemoveCondition = "${StringUtil.wrapString(uiLabelMap.HRRemoveCondition)}"; 
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}"; 
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}"; 
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"; 
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}"; 
uiLabelMap.ConfirmCreateRecruitRequirement = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateRecruitRequirement)}"; 
uiLabelMap.EditRecruitmentRequirement = "${StringUtil.wrapString(uiLabelMap.EditRecruitmentRequirement)}"; 
uiLabelMap.CommonCreate = "${StringUtil.wrapString(uiLabelMap.CommonCreate)}"; 
uiLabelMap.CommonUpdate = "${StringUtil.wrapString(uiLabelMap.CommonUpdate)}"; 
uiLabelMap.HRCommonNotSetting = "${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}"; 
uiLabelMap.PleaseSelectOption = "${StringUtil.wrapString(uiLabelMap.PleaseSelectOption)}"; 
uiLabelMap.ApproveRecruitmentReqConfirm = "${StringUtil.wrapString(uiLabelMap.ApproveRecruitmentReqConfirm)}"; 
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}"; 
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}"; 
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}"; 
uiLabelMap.AddNewRecruitmentRequire = "${StringUtil.wrapString(uiLabelMap.AddNewRecruitmentRequire)}"; 
uiLabelMap.HRCondition = "${StringUtil.wrapString(uiLabelMap.HRCondition)}"; 
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}"; 
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}"; 
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}"; 
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}"; 
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}"; 
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}"; 
uiLabelMap.RecruitmentRequirementPosition = "${StringUtil.wrapString(uiLabelMap.RecruitmentRequirementPosition)}"; 
uiLabelMap.RecruitmentAnticipateQuantityNotes = "${StringUtil.wrapString(uiLabelMap.RecruitmentAnticipateQuantityNotes)}"; 
uiLabelMap.RecruitmentQuantityPlanAndUnplanedMustGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.RecruitmentQuantityPlanAndUnplanedMustGreaterThanZero)}"; 
uiLabelMap.ValueMustLessThanValueAppr = "${StringUtil.wrapString(uiLabelMap.ValueMustLessThanValueAppr)}"; 
uiLabelMap.AcceptRecruitmentRequireConfirm = "${StringUtil.wrapString(uiLabelMap.AcceptRecruitmentRequireConfirm)}"; 
uiLabelMap.RejectRecruitmentRequireConfirm = "${StringUtil.wrapString(uiLabelMap.RejectRecruitmentRequireConfirm)}"; 
uiLabelMap.CancelRecruitmentRequireConfirm = "${StringUtil.wrapString(uiLabelMap.CancelRecruitmentRequireConfirm)}"; 
uiLabelMap.RecruitmentReason = "${StringUtil.wrapString(uiLabelMap.RecruitmentReason)}"; 
uiLabelMap.HasErrorWhenProcess = "${StringUtil.wrapString(uiLabelMap.HasErrorWhenProcess)}"; 
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}"; 
</script>