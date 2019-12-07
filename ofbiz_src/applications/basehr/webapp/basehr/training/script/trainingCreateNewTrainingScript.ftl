<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	var globalVar = {};
}

<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
	if(typeof(globalVar.expandTreeId) == 'undefined'){
		globalVar.expandTreeId = "${expandTreeId}";		
	}
<#else>
	<#assign expandTreeId="">
</#if>

if(!globalVar.hasOwnProperty("trainingFormTypeArr")){
	globalVar.trainingFormTypeArr = [
		<#if trainingFormTypeList?exists>
			<#list trainingFormTypeList as trainingFormType>
			{
				trainingFormTypeId: "${trainingFormType.trainingFormTypeId}",
				description: "${StringUtil.wrapString(trainingFormType.description?if_exists)}"
			},
			</#list>
		</#if>                                 
	];
}

if(!globalVar.hasOwnProperty("trainingPurposeTypeArr")){
	globalVar.trainingPurposeTypeArr = [
		<#if trainingPurposeTypeList?exists>
			<#list trainingPurposeTypeList as trainingPurposeType>
			{
				trainingPurposeTypeId: "${trainingPurposeType.trainingPurposeTypeId}",
				description: "${StringUtil.wrapString(trainingPurposeType.description?if_exists)}"
			},
			</#list>
		</#if>
	];
}

if(typeof(globalVar.trainingResultTypeArr) == 'undefined'){
	globalVar.trainingResultTypeArr = [
		<#if trainingResultTypeList?has_content>
			<#list trainingResultTypeList as trainingResultType>
			{
				resultTypeId: '${trainingResultType.resultTypeId}',
				description: '${StringUtil.wrapString(trainingResultType.description?if_exists)}'
			},
			</#list>
		</#if>                             
	];
}

globalVar.createNewSuffix = "${popWindowId}";
if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {};
}
<#if !nowTimestamp?exists>
	<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
	if(!globalVar.hasOwnProperty("nowTimestamp")){
		globalVar.nowTimestamp = ${nowTimestamp.getTime()};
	}
</#if>

var partyProvider = [];
<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
<#assign providers = delegator.findList("PartyRelationship", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("partyIdFrom", company, "roleTypeIdTo", "TRAINING_PROVIDER")), null, null, null, false) />
<#assign providers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(providers)>
<#list providers as pro>
	<#assign partyGr = delegator.findOne("PartyGroup", {"partyId" : pro.partyIdTo?if_exists}, false)/>
	<#assign partyName = StringUtil.wrapString(partyGr.groupName?if_exists)!/>
	var row = {};
	row['partyId'] = "${pro.partyIdTo?if_exists}";
	row['partyName'] = "${partyName?if_exists}";
	partyProvider.push(row);
</#list>

uiLabelMap.HRSkillType = "${StringUtil.wrapString(uiLabelMap.HRSkillType)}";
uiLabelMap.SkillTypeId = "${StringUtil.wrapString(uiLabelMap.SkillTypeId)}";
uiLabelMap.HRSkillTypeParent = "${StringUtil.wrapString(uiLabelMap.HRSkillTypeParent)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.RequrimentLevelSkillTrainingCourse = "${StringUtil.wrapString(uiLabelMap.RequrimentLevelSkillTrainingCourse)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.EmplExpectedAttendance = "${StringUtil.wrapString(uiLabelMap.EmplExpectedAttendance)}";
uiLabelMap.HREmplList = "${StringUtil.wrapString(uiLabelMap.HREmplList)}";
uiLabelMap.OnlyEmplInRegisterList = "${StringUtil.wrapString(uiLabelMap.OnlyEmplInRegisterList)}";
uiLabelMap.AllEmployee = "${StringUtil.wrapString(uiLabelMap.AllEmployee)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.EstimatedThruDateMustGreaterEqualFromDate = "${StringUtil.wrapString(uiLabelMap.EstimatedThruDateMustGreaterEqualFromDate)}";
uiLabelMap.RegisterThruDateMustGreaterEqualFromDate = "${StringUtil.wrapString(uiLabelMap.RegisterThruDateMustGreaterEqualFromDate)}";
uiLabelMap.RegisterThruDateMustLessThanStartDateTraining = "${StringUtil.wrapString(uiLabelMap.RegisterThruDateMustLessThanStartDateTraining)}";
uiLabelMap.ConfirmCreateNewTrainingCourse = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateNewTrainingCourse)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
</script>