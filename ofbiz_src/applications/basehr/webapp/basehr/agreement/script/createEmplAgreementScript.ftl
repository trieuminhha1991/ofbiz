<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
<#if windowPopupId?has_content>
	globalVar.createWindow = true;
</#if>
if(typeof(globalVar.agreementTypeArr) == 'undefined'){
	globalVar.agreementTypeArr = [
		<#if agreementTypeList?exists>
			<#list agreementTypeList as agreementType>
				{
					agreementTypeId: "${agreementType.agreementTypeId}",
					description: "${agreementType.description}"
				},
			</#list>
		</#if>
	];
}


<#if !rootOrgId?exists>
	<#assign rootOrgId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign orgRootParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
</#if>
if(typeof(globalVar.rootOrgId) == 'undefined'){
	globalVar.rootOrgId = "${rootOrgId}";
}
if(typeof(globalVar.groupName) == 'undefined'){
	globalVar.groupName = "${StringUtil.wrapString(orgRootParty.groupName)}";
}
if(typeof(globalVar.workTypeArr) == 'undefined'){
	globalVar.workTypeArr = [
		{
			workTypeId: "FULLTIME",
			description: "${StringUtil.wrapString(uiLabelMap.HREmplFulltimeFlag)}"
		},
		{
			workTypeId: "PARTTIME",
			description: "${StringUtil.wrapString(uiLabelMap.HREmplParttimeFlag)}"
		}
	]
}

if(typeof(globalVar.payrollParam) == "undefined"){
	globalVar.payrollParamArr = [
		<#if payrollParamList?exists>
			<#list payrollParamList as payrollParam>
			{
				code: "${payrollParam.code}",
				name: "${StringUtil.wrapString(payrollParam.name)}"
			},
			</#list>
		</#if>
	];
}

if(typeof(globalVar.uom_YearMonth) == 'undefined'){
	globalVar.uom_YearMonth = [
	   <#if uomArr?exists>
	   		<#list uomArr as uom>
	   			{
	   				uomId : "${uom.uomId}",
	   				description : "${uom.get('description', locale)}"
	   			},
	   		</#list>
	   </#if>
   ];
}

globalVar.suffix = "${windowPopupId?if_exists}";
globalVar.jqxWindowId = "${windowPopupId?if_exists}";
<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
	if(typeof(globalVar.expandTreeId) == 'undefined'){
		globalVar.expandTreeId = "${expandTreeId}";		
	}
<#else>
	<#assign expandTreeId="">
</#if>
<#if !nowTimestamp?exists>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
</#if>
if(typeof(globalVar.nowTimestamp) == 'undefined'){
	globalVar.nowTimestamp = ${nowTimestamp.getTime()};
}
function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	var tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}

function jqxTreeEmplRepListSelect(event){
	var item = $('#jqxTreeEmplRepList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	var tmpS = $("#EmplRepresentList").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplRepresentList").jqxGrid('source', tmpS);
}

var uiLabelMap = {};
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.NoPartyChoose = "${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.HRRequiredValueGreatherToDay = "${StringUtil.wrapString(uiLabelMap.HRRequiredValueGreatherToDay)}";
uiLabelMap.CommonAnd = "${StringUtil.wrapString(uiLabelMap.CommonAnd)}";
uiLabelMap.CreateAgreementConfirm = "${StringUtil.wrapString(uiLabelMap.CreateAgreementConfirm)}";
uiLabelMap.ErrorOccurWhenCreateAgreement = "${StringUtil.wrapString(uiLabelMap.ErrorOccurWhenCreateAgreement)}";
uiLabelMap.HRAgreementHaveCode = "${StringUtil.wrapString(uiLabelMap.HRAgreementHaveCode)}";
uiLabelMap.HRWillExpireDate = "${StringUtil.wrapString(uiLabelMap.HRWillExpireDate)}";
uiLabelMap.HRCommonWhen = "${StringUtil.wrapString(uiLabelMap.HRCommonWhen)}";
uiLabelMap.AgreementCreatedNew = "${StringUtil.wrapString(uiLabelMap.AgreementCreatedNew)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.AreYouSure = "${StringUtil.wrapString(uiLabelMap.AreYouSure)}";
uiLabelMap.AllowancesType = "${StringUtil.wrapString(uiLabelMap.AllowancesType)}";
uiLabelMap.HRCommonAmount = "${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.HREmplAllowances = "${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.HRNewAgreement = "${StringUtil.wrapString(uiLabelMap.HRNewAgreement)}";
uiLabelMap.HREditAgreement = "${StringUtil.wrapString(uiLabelMap.HREditAgreement)}";
uiLabelMap.CommonUpdate = "${StringUtil.wrapString(uiLabelMap.CommonUpdate)}";
uiLabelMap.CommonCreate = "${StringUtil.wrapString(uiLabelMap.CommonCreate)}";
uiLabelMap.ValueIsInvalid = "${StringUtil.wrapString(uiLabelMap.ValueIsInvalid)}";
uiLabelMap.CommonOr = "${StringUtil.wrapString(uiLabelMap.CommonOr)}";
uiLabelMap.OnlyInputNumberGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}";
uiLabelMap.ValueMustGreaterAgreementDate = "${StringUtil.wrapString(uiLabelMap.ValueMustGreaterAgreementDate)}";
uiLabelMap.HRCommonMonth = "${StringUtil.wrapString(uiLabelMap.HRCommonMonth)}";
uiLabelMap.HolidayYear = "${StringUtil.wrapString(uiLabelMap.HolidayYear)}";
uiLabelMap.EffectiveDayEqualOrGreaterThanContractingDay = "${StringUtil.wrapString(uiLabelMap.EffectiveDayEqualOrGreaterThanContractingDay)}";
uiLabelMap.ContractingDayEqualOrLessThanEffectiveDay = "${StringUtil.wrapString(uiLabelMap.ContractingDayEqualOrLessThanEffectiveDay)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.HRContainSpecialSymbol = "${StringUtil.wrapString(uiLabelMap.HRContainSpecialSymbol)}";
</script>