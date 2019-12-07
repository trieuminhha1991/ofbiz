<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpasswordinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<@jqOlbCoreLib hasGrid=true hasValidator=true/>

<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
    var filterObjData = new Object();
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
<#assign rowDetails = "function (index, parentElement, gridElement, datarecord){
	 var partyId = datarecord.partyId;
	 var id = datarecord.uid.toString();
	 var tabsdiv = $($(parentElement).children()[0]);
	 if(tabsdiv != null){
		var divContainer = tabsdiv.find('.informationDetails');
		var container = $('<div style=\"margin: 5px;\"></div>');
		container.appendTo(divContainer);
		var photocolumn = $('<div style=\"float: left; width: 15%;\"></div>');
        var leftcolumn = $('<div style=\"float: left; width: 25%;\"></div>');
        var rightcolumn = $('<div style=\"float: left; width: 60%;\"></div>');
        container.append(photocolumn);
        container.append(leftcolumn);
        container.append(rightcolumn);
        var photo = $(\"<div class='jqx-rc-all'></div>\");
        var image = $(\"<div style='margin-top: 10px;'></div>\");
        var imgurl = '/aceadmin/assets/avatars/no-avatar.png';
        var img = $('<img height=\"130\" id=\"viewAvatar' + partyId + '\" style=\"height: 130px\" src=\"' + imgurl + '\"/>');
        image.append(img);
        image.appendTo(photo);
        photocolumn.append(photo);
        var idNumber = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.IDNumber)}: </b><span id='idNumber_\" + id + \"'></span></div>\";
        var idIssueDate = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.HrolbiusidIssueDate)}: </b><span id='idIssueDate_\" + id + \"'></span></div>\";
        var idIssuePlace = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.HrolbiusidIssuePlace)}: </b><span id='idIssuePlace_\" + id + \"'></span></div>\";
        var nativeLand = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.NativeLand)}: </b><span id='nativeLand_\" + id + \"'></span></div>\";
        var ethnic = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.EthnicOrigin)}: </b><span id='ethnic_\" + id + \"'></span></div>\";
        $(leftcolumn).append(idNumber);
        $(leftcolumn).append(idIssueDate);
        $(leftcolumn).append(idIssuePlace);
        $(leftcolumn).append(nativeLand);
        $(leftcolumn).append(ethnic);
        
        var permanentResidence = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.PermanentResidence)}: </b><span id='permanentResidence_\" + id + \"'></span></div>\";
        var currentResidence = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.CurrentResidence)}: </b><span id='currentResidence_\" + id + \"'></span></div>\";
        var educationSystemTypeId = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.Level)}: </b><span id='educationSystemTypeId_\" + id + \"'></span></div>\";
        var schoolId = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.HRCommonSchool)}: </b><span  id='schoolId_\" + id + \"'></span></div>\";
        var majorId = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.HRSpecialization)}: </b><span id='majorId_\" + id + \"'></span></div>\";
        $(rightcolumn).append(permanentResidence);
        $(rightcolumn).append(currentResidence);
        $(rightcolumn).append(educationSystemTypeId);
        $(rightcolumn).append(schoolId);
        $(rightcolumn).append(majorId);
        $(tabsdiv).jqxTabs({ width: '950px', height: 220});
        $.ajax({
       	url: 'getPartyInformation',
       	data: {partyId: partyId},
       	type: 'POST',
       	success: function(data){
       		if(data.partyInfo){
       			var partyInfo = data.partyInfo;
       			if(partyInfo.ethnicOrigin){
       				$('#ethnic_' + id).text(partyInfo.ethnicOrigin);
       			}
       			if(partyInfo.idIssueDate){
       				//var idIssueDate = new Date(partyInfo.idIssueDate);
       				$('#idIssueDate_' + id).text(partyInfo.idIssueDate);
       			}
       			if(partyInfo.idIssuePlace){
       				$('#idIssuePlace_' + id).text(partyInfo.idIssuePlace);
       			}
       			if(typeof(partyInfo.idNumber) != 'undefined' && partyInfo.idNumber != null){
       				$('#idNumber_' + id).text(partyInfo.idNumber);
       			}
       			if(partyInfo.nativeLand){
       				$('#nativeLand_' + id).text(partyInfo.nativeLand);
       			}
       			if(partyInfo.permanentResidence){
       				$('#permanentResidence_' + id).text(partyInfo.permanentResidence);
       			}
       			if(partyInfo.currentResidence){
       				$('#currentResidence_' + id).text(partyInfo.currentResidence);
       			}
       			if(partyInfo.schoolId){
       				$('#schoolId_' + id).text(partyInfo.schoolId);
       			}
       			if(partyInfo.majorId){
       				$('#majorId_' + id).text(partyInfo.majorId);
       			}
       			if(partyInfo.educationSystemTypeId){
       				$('#educationSystemTypeId_' + id).text(partyInfo.educationSystemTypeId);
       			}
       			if(partyInfo.avatarUrl){
       				$('#viewAvatar' + partyId).attr('src', partyInfo.avatarUrl);
       			}else{
       				$('#viewAvatar' + partyId).attr('src', imgurl);
       			}
       		}
       	}
        });
	 }
}"/>		

<#assign rowdetailstemplateAdvance = "<ul style='margin-left: 30px;'><li class='title'>${StringUtil.wrapString(uiLabelMap.profile)}</li></ul><div class='informationDetails'></div>"/>

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)!/>
</#if>

<#if !rootOrgName?if_exists>
<#assign rootOrgName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userLogin.userLoginId, false) />
</#if>
var uiLabelMap = {
		AddNewRowConfirm: "${uiLabelMap.AddNewRowConfirm}",
		CommonSubmit: "${uiLabelMap.CommonSubmit}",
		CommonCancel: "${uiLabelMap.CommonCancel}",
		FieldRequired: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
		EmplFulfillmentPosition: '${uiLabelMap.EmplFulfillmentPosition}',
		AssignPosForEmpl: "${StringUtil.wrapString(uiLabelMap.AssignPosForEmpl)}",
		HRNotYet: "${StringUtil.wrapString(uiLabelMap.HRNotYet)}",
		EmployeeId: '${uiLabelMap.EmployeeId}',
		FromDateFulfillment: '${uiLabelMap.FromDateFulfillment}',
		PositionActualFromDate: '${uiLabelMap.PositionActualFromDate}',
		EmployeeName: '${uiLabelMap.EmployeeName}',
		HrCommonPosition: '${uiLabelMap.HrCommonPosition}',
		CommonDepartment: '${uiLabelMap.CommonDepartment}',
		DateJoinCompany: '${uiLabelMap.DateJoinCompany}',
		EnterEmployeeId: "${StringUtil.wrapString(uiLabelMap.EnterEmployeeId)}",
		AssignEmplToPositionConfirm: "${StringUtil.wrapString(uiLabelMap.AssignEmplToPositionConfirm)}",
		CommonThruDate : "${StringUtil.wrapString(uiLabelMap.CommonThruDate)}",
		ValueMustBeGreateThanZero : "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}",
		ValueMustBeGreateThanZero : "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}",
		ValueMustBeGreateThanEffectiveDate : "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanEffectiveDate)}",
		CommonClose : "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
		CommonGroup : "${StringUtil.wrapString(uiLabelMap.CommonGroup)}",
		CommonDescription : "${StringUtil.wrapString(uiLabelMap.CommonDescription)}",
		HREmplPositionTypeId : "${StringUtil.wrapString(uiLabelMap.HREmplPositionTypeId)}",
		partyId : "${StringUtil.wrapString(uiLabelMap.partyId)}",
		HRFullName : "${StringUtil.wrapString(uiLabelMap.HRFullName)}",
		HRRelationship : "${StringUtil.wrapString(uiLabelMap.HRRelationship)}",
		BirthDate : "${StringUtil.wrapString(uiLabelMap.BirthDate)}",
		HROccupation : "${StringUtil.wrapString(uiLabelMap.HROccupation)}",
		placeWork : "${StringUtil.wrapString(uiLabelMap.placeWork)}",
		PartyPhoneNumber : "${StringUtil.wrapString(uiLabelMap.PartyPhoneNumber)}",
		PersonDependent : "${StringUtil.wrapString(uiLabelMap.PersonDependent)}",
};
uiLabelMap.CreateNewEmployeeConfirm= "${StringUtil.wrapString(uiLabelMap.CreateNewEmployeeConfirm)}";
uiLabelMap.CommonSubmit= "${uiLabelMap.CommonSubmit}";
uiLabelMap.CommonClose= "${uiLabelMap.CommonClose}";
uiLabelMap.FieldRequired= '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}';
uiLabelMap.AmountValueGreaterThanZero= '${StringUtil.wrapString(uiLabelMap.AmountValueGreaterThanZero?default(''))}';
uiLabelMap.NotEmplPositionTypeChoose= "${StringUtil.wrapString(uiLabelMap.NotEmplPositionTypeChoose)}";
uiLabelMap.MustntHaveSpaceChar = "${StringUtil.wrapString(uiLabelMap.MustntHaveSpaceChar)}";
uiLabelMap.GTDateFieldRequired = "${StringUtil.wrapString(uiLabelMap.GTDateFieldRequired)}";
uiLabelMap.OnlyInputNumberGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}";
uiLabelMap.LTCurrentDateRequired = "${StringUtil.wrapString(uiLabelMap.LTCurrentDateRequired)}";
uiLabelMap.BirthDateBefIdentifyCardDay = "${StringUtil.wrapString(uiLabelMap.BirthDateBefIdentifyCardDay)}";
uiLabelMap.IdentifyDayGreaterBirthDate = "${StringUtil.wrapString(uiLabelMap.IdentifyDayGreaterBirthDate)}";
uiLabelMap.PassLengthOverFive = "${StringUtil.wrapString(uiLabelMap.PassLengthOverFive)}";
uiLabelMap.PasswordInvalid ="${StringUtil.wrapString(uiLabelMap.PasswordInvalid)}";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.CommonYes = '${StringUtil.wrapString(uiLabelMap.CommonYes)?default("")}';
uiLabelMap.CommonNo = '${StringUtil.wrapString(uiLabelMap.CommonNo)?default("")}';
uiLabelMap.CommonClose = '${StringUtil.wrapString(uiLabelMap.CommonClose)?default("")}';
uiLabelMap.UpdateWorkingStatusIdRemind = '${StringUtil.wrapString(uiLabelMap.UpdateWorkingStatusIdRemind)?default("")}';
uiLabelMap.CommonFromDate = '${StringUtil.wrapString(uiLabelMap.CommonFromDate)?default("")}';
uiLabelMap.CommonThruDate = '${StringUtil.wrapString(uiLabelMap.CommonThruDate)?default("")}';
uiLabelMap.accAddNewRow = '${StringUtil.wrapString(uiLabelMap.accAddNewRow)?default("")}';
uiLabelMap.FieldRequired = '${StringUtil.wrapString(uiLabelMap.FieldRequired)?default("")}';
uiLabelMap.CommonSubmit = '${StringUtil.wrapString(uiLabelMap.CommonSubmit)?default("")}';
uiLabelMap.CommonCancel = '${StringUtil.wrapString(uiLabelMap.CommonCancel)?default("")}';
uiLabelMap.ValueEqualOrAfterDate = '${StringUtil.wrapString(uiLabelMap.ValueEqualOrAfterDate)?default("")}';
uiLabelMap.wgdelete = '${StringUtil.wrapString(uiLabelMap.wgdelete)?default("")}';
uiLabelMap.CannotDeleteRow = '${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)?default("")}';
uiLabelMap.wgdeleteconfirm = '${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)?default("")}';
uiLabelMap.wgok = '${StringUtil.wrapString(uiLabelMap.wgok)?default("")}';
uiLabelMap.wgcancel = '${StringUtil.wrapString(uiLabelMap.wgcancel)?default("")}';
uiLabelMap.NotifyDelete = '${StringUtil.wrapString(uiLabelMap.NotifyDelete)?default("")}';

uiLabelMap.HRFullName = "${StringUtil.wrapString(uiLabelMap.HRFullName)}";
uiLabelMap.HRRelationship = "${StringUtil.wrapString(uiLabelMap.HRRelationship)}";
uiLabelMap.BirthDate = "${StringUtil.wrapString(uiLabelMap.BirthDate)}";
uiLabelMap.HROccupation = "${StringUtil.wrapString(uiLabelMap.HROccupation)}";
uiLabelMap.placeWork = "${StringUtil.wrapString(uiLabelMap.placeWork)}";
uiLabelMap.PartyPhoneNumber = "${StringUtil.wrapString(uiLabelMap.PartyPhoneNumber)}";
uiLabelMap.PersonDependent = "${StringUtil.wrapString(uiLabelMap.PersonDependent)}";
uiLabelMap.FamilyMembers = "${StringUtil.wrapString(uiLabelMap.FamilyMembers)}";
uiLabelMap.IsPersonDependent = "${StringUtil.wrapString(uiLabelMap.IsPersonDependent)}";
uiLabelMap.HRApprove = "${StringUtil.wrapString(uiLabelMap.HRApprove)}";
uiLabelMap.DependentDeductionEnd = "${StringUtil.wrapString(uiLabelMap.DependentDeductionEnd)}";
uiLabelMap.DependentDeductionStart = "${StringUtil.wrapString(uiLabelMap.DependentDeductionStart)}";
uiLabelMap.HRCommonRegister = "${StringUtil.wrapString(uiLabelMap.HRCommonRegister)}";
uiLabelMap.PleaseSelectOption = "${StringUtil.wrapString(uiLabelMap.PleaseSelectOption)}";
uiLabelMap.ChangeAvatar = "${StringUtil.wrapString(uiLabelMap.ChangeAvatar)}";
uiLabelMap.ClickToChooseNewAvatar = "${StringUtil.wrapString(uiLabelMap.ClickToChooseNewAvatar)}";
uiLabelMap.ResignDateAfterNowDate = "${StringUtil.wrapString(uiLabelMap.ResignDateAfterNowDate)}";
uiLabelMap.AddNewRowConfirm = "${StringUtil.wrapString(uiLabelMap.AddNewRowConfirm)}";
uiLabelMap.EmailRequired = "${StringUtil.wrapString(uiLabelMap.EmailRequired)}";
uiLabelMap.OnlyNumberInput = "${StringUtil.wrapString(uiLabelMap.OnlyNumberInput)}";
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}";
uiLabelMap.NotSetting = "${StringUtil.wrapString(uiLabelMap.NotSetting)}";
uiLabelMap.NewPositionTypeMustBeDiffOldPositionType = "${StringUtil.wrapString(uiLabelMap.NewPositionTypeMustBeDiffOldPositionType)}";
uiLabelMap.NewPositionFromDateGreaterThanOldPositionFromDate = "${StringUtil.wrapString(uiLabelMap.NewPositionFromDateGreaterThanOldPositionFromDate)}";
uiLabelMap.OnlyAllowValueAfterOldPositionThruDateOneDay = "${StringUtil.wrapString(uiLabelMap.OnlyAllowValueAfterOldPositionThruDateOneDay)}";
uiLabelMap.DateJoinCompanyMustLessThanResignDate = "${StringUtil.wrapString(uiLabelMap.DateJoinCompanyMustLessThanResignDate)}";
uiLabelMap.ChangePositionAndDeptConfirm = "${StringUtil.wrapString(uiLabelMap.ChangePositionAndDeptConfirm)}";
uiLabelMap.password_did_not_match_verify_password = '${StringUtil.wrapString(uiLabelMap["password_did_not_match_verify_password"])}';
uiLabelMap.HREmplPositionTypeId = '${StringUtil.wrapString(uiLabelMap.HREmplPositionTypeId)}';
uiLabelMap.CommonGroup = '${StringUtil.wrapString(uiLabelMap.CommonGroup)}';
uiLabelMap.CommonDescription = '${StringUtil.wrapString(uiLabelMap.CommonDescription)}';
uiLabelMap.CreatePositionTypeForOrgConfirm = '${StringUtil.wrapString(uiLabelMap.CreatePositionTypeForOrgConfirm)}';
uiLabelMap.HRCheckName = '${StringUtil.wrapString(uiLabelMap.HRCheckName)}';
uiLabelMap.HRCheckFullName = '${StringUtil.wrapString(uiLabelMap.HRCheckFullName)}';
uiLabelMap.HRCheckSpecialCharacter = '${StringUtil.wrapString(uiLabelMap.HRCheckSpecialCharacter)}';
uiLabelMap.HRCheckPhone = '${StringUtil.wrapString(uiLabelMap.HRCheckPhone)}';
uiLabelMap.HRPhoneIsNotValid = '${StringUtil.wrapString(uiLabelMap.HRPhoneIsNotValid)}';
uiLabelMap.HRCharacterIsNotValid = '${StringUtil.wrapString(uiLabelMap.HRCharacterIsNotValid)}';
uiLabelMap.HRBirthDateBeforeToDay = '${StringUtil.wrapString(uiLabelMap.HRBirthDateBeforeToDay)}';
uiLabelMap.HRCheckId = '${StringUtil.wrapString(uiLabelMap.HRCheckId)}';
uiLabelMap.HRCheckIdCard = '${StringUtil.wrapString(uiLabelMap.HRCheckIdCard)}';
uiLabelMap.HRIdCardIsNotValid = '${StringUtil.wrapString(uiLabelMap.HRIdCardIsNotValid)}';
uiLabelMap.HRCheckAddress = '${StringUtil.wrapString(uiLabelMap.HRCheckAddress)}';
uiLabelMap.HREmailIsNotValid = '${StringUtil.wrapString(uiLabelMap.HREmailIsNotValid)}';
uiLabelMap.HRCheckEmail = '${StringUtil.wrapString(uiLabelMap.HRCheckEmail)}';
uiLabelMap.HRCheckInsurance = '${StringUtil.wrapString(uiLabelMap.HRCheckInsurance)}';
uiLabelMap.ResetPasswordConfirm = '${StringUtil.wrapString(uiLabelMap.ResetPasswordConfirm)}';
uiLabelMap.HrCommonPosition = '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}';
uiLabelMap.ExpireEmplPositionWarning = '${StringUtil.wrapString(uiLabelMap.ExpireEmplPositionWarning)}';
uiLabelMap.HRLock = '${StringUtil.wrapString(uiLabelMap.HRLock)}';
uiLabelMap.HRUnLock = '${StringUtil.wrapString(uiLabelMap.HRUnLock)}';
uiLabelMap.HRYes = '${StringUtil.wrapString(uiLabelMap.HRYes)}';
uiLabelMap.HRNo = '${StringUtil.wrapString(uiLabelMap.HRNo)}';
uiLabelMap.HRUnLocked = '${StringUtil.wrapString(uiLabelMap.HRUnLocked)}';
uiLabelMap.HRLocked = '${StringUtil.wrapString(uiLabelMap.HRLocked)}';
uiLabelMap.HRLoggedOut = '${StringUtil.wrapString(uiLabelMap.HRLoggedOut)}';
uiLabelMap.HRLoggedIn = '${StringUtil.wrapString(uiLabelMap.HRLoggedIn)}';
uiLabelMap.HRAfterNowDate = '${StringUtil.wrapString(uiLabelMap.HRAfterNowDate)}';
uiLabelMap.HRChangeInfoEmplAccount = '${StringUtil.wrapString(uiLabelMap.HRChangeInfoEmplAccount)}';

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());


var globalVar = {
		monthStart: ${monthStart.getTime()},
		monthEnd: ${monthEnd.getTime()},
		rootPartyArr: [
   			<#if rootOrgList?has_content>
   				<#list rootOrgList as rootOrgId>
   				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
   				{
   					partyId: "${rootOrgId}",
   					partyName: "${StringUtil.wrapString(rootOrg.groupName)}"
   				},
   				</#list>
   			</#if>
   		],
   		statusWorkingArr:  [
   			<#if statusWorkingList?has_content>
   				<#list statusWorkingList as status>
   				{
   					statusId: '${status.statusId}',
   					description: '${StringUtil.wrapString(status.description)}'
   				},
   				</#list>
   			</#if>
   		],
   		terminationReasonArr: [
           	<#if terminationReasonList?has_content>
           		<#list terminationReasonList as terminationReason>
           		{
           			terminationReasonId: "${terminationReason.terminationReasonId}",
           			description: "${StringUtil.wrapString(terminationReason.description)}"
           		},
           		</#list>
           	</#if>
   		],
   		agreementTypeArr: [
           	<#if agreementTypeList?has_content>
           		<#list agreementTypeList as agreementType>
           		{
           			agreementTypeId: "${agreementType.agreementTypeId}",
           			description: "${StringUtil.wrapString(agreementType.description)}"
           		},
           		</#list>
           	</#if>
   		],
   		nowTimestamp: ${nowTimestamp.getTime()}
}

var genderArr = [
	<#if genderList?has_content>                 
		<#list genderList as gender>
		{
			genderId: '${gender.genderId}',
			description: '${StringUtil.wrapString(gender.get("description", locale))}'
		},
		</#list>
	</#if>	
];
<#assign ethnicOriginList = delegator.findByAnd("EthnicOrigin", null, null, false)/>
var ethnicOriginArr = [
	<#list ethnicOriginList as ethnicOrigin>
		{ethnicOriginId: '${ethnicOrigin.ethnicOriginId}', description: '${StringUtil.wrapString(ethnicOrigin.description)}?default("")'}
		<#if ethnicOrigin_has_next>
		,
		</#if>
	</#list>
];

<#assign partyRelationshipType = delegator.findByAnd("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "FAMILY"), null, false)>
var partyRelationshipType = [
		<#list partyRelationshipType as partyRelationship>
       		{
       			partyRelationshipTypeId : "${partyRelationship.partyRelationshipTypeId}",
       			partyRelationshipName : "${StringUtil.wrapString(partyRelationship.partyRelationshipName?default(''))}"
       		},
       	</#list>	
];

globalVar.dependentStatusList = [
	<#if dependentStatusList?has_content>
		<#list dependentStatusList as dependentStatus>
			{
				statusId: '${dependentStatus.statusId}',
				description: '${StringUtil.wrapString(dependentStatus.description)}'
			},
		</#list>
	</#if>
];

globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
			{
				periodTypeId: '${periodType.periodTypeId}',
				description: '${StringUtil.wrapString(periodType.description)}'
			},
		</#list>
	</#if>               
];

<#if security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", session)>
globalVar.isHrDirectory = "true";
<#else>
globalVar.isHrDirectory = "false";
</#if>

<#if countryGeoIdDefault?has_content>
	globalVar.countryGeoIdDefault = "${countryGeoIdDefault}";
</#if>


var uiLabelMap = {
		messageRequire: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
		close: "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
		CommonClose: "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
		commonCountryCode: '${StringUtil.wrapString(uiLabelMap.CommonCountryCode)}',
		CommonAreaCode: '${StringUtil.wrapString(uiLabelMap.CommonAreaCode)}',
		PartyPhoneNumber: '${StringUtil.wrapString(uiLabelMap.PartyPhoneNumber)}',
		filterselectallstring: '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})',
		ErrorWhenUpdate: "${uiLabelMap.ErrorWhenUpdate}",
		ResultUpdate: "${uiLabelMap.ResultUpdate}",
		PleaseChooseAcc: "${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc)}...",
		OnlyNumberInput: "${StringUtil.wrapString(uiLabelMap.OnlyNumberInput)}",
		CommonSubmit: "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
		CommonCancel: "${StringUtil.wrapString(uiLabelMap.CommonCancel)}",
		PasswordConfirmNotMatch: "${StringUtil.wrapString(uiLabelMap.PasswordConfirmNotMatch)}",
		ValueMustBeGreateThanZero : "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}",
		CommonOk : "${StringUtil.wrapString(uiLabelMap.CommonOk)}",
		IdentifyDayGreaterBirthDate : "${StringUtil.wrapString(uiLabelMap.IdentifyDayGreaterBirthDate)}",
		BirthDateBefIdentifyCardDay : "${StringUtil.wrapString(uiLabelMap.BirthDateBefIdentifyCardDay)}",
		IllegalCharacters : "${StringUtil.wrapString(uiLabelMap.IllegalCharacters)}",
		ChangeAvatar : "${StringUtil.wrapString(uiLabelMap.ChangeAvatar)}",
		ClickToChooseNewAvatar : "${StringUtil.wrapString(uiLabelMap.ClickToChooseNewAvatar)}",
		IllegalCharactersAndSpace : "${StringUtil.wrapString(uiLabelMap.IllegalCharactersAndSpace)}",
		IllegalCharacters : "${StringUtil.wrapString(uiLabelMap.IllegalCharacters)}",
		BirthDateBeforeToDay : "${StringUtil.wrapString(uiLabelMap.BirthDateBeforeToDay)}",
		RequiredOneOrTwoDotChar : "${StringUtil.wrapString(uiLabelMap.RequiredOneOrTwoDotChar)}",
		EmailFormInvalid : "${StringUtil.wrapString(uiLabelMap.EmailFormInvalid)}",
		FieldRequired : "${StringUtil.wrapString(uiLabelMap.FieldRequired)}",
		UpdateSuccessfully : "${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}",
		UpdateError : "${StringUtil.wrapString(uiLabelMap.UpdateError)}",
		PersonFamilyBackgroundIsNotDeclare : "${StringUtil.wrapString(uiLabelMap.PersonFamilyBackgroundIsNotDeclare)}",
		HRCharacterIsNotValid : "${StringUtil.wrapString(uiLabelMap.HRCharacterIsNotValid)}",
		HRPhoneIsNotValid : "${StringUtil.wrapString(uiLabelMap.HRPhoneIsNotValid)}",
};

uiLabelMap.CreateNewEmployeeConfirm= "${StringUtil.wrapString(uiLabelMap.CreateNewEmployeeConfirm)}";
uiLabelMap.CommonSubmit= "${uiLabelMap.CommonSubmit}";
uiLabelMap.CommonClose= "${uiLabelMap.CommonClose}";
uiLabelMap.FieldRequired= '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}';
uiLabelMap.AmountValueGreaterThanZero= '${StringUtil.wrapString(uiLabelMap.AmountValueGreaterThanZero?default(''))}';
uiLabelMap.NotEmplPositionTypeChoose= "${StringUtil.wrapString(uiLabelMap.NotEmplPositionTypeChoose)}";
uiLabelMap.MustntHaveSpaceChar = "${StringUtil.wrapString(uiLabelMap.MustntHaveSpaceChar)}";
uiLabelMap.GTDateFieldRequired = "${StringUtil.wrapString(uiLabelMap.GTDateFieldRequired)}";
uiLabelMap.OnlyInputNumberGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}";
uiLabelMap.LTCurrentDateRequired = "${StringUtil.wrapString(uiLabelMap.LTCurrentDateRequired)}";
uiLabelMap.BirthDateBefIdentifyCardDay = "${StringUtil.wrapString(uiLabelMap.BirthDateBefIdentifyCardDay)}";
uiLabelMap.IdentifyDayGreaterBirthDate = "${StringUtil.wrapString(uiLabelMap.IdentifyDayGreaterBirthDate)}";
uiLabelMap.PassLengthOverFive = "${StringUtil.wrapString(uiLabelMap.PassLengthOverFive)}";
uiLabelMap.PasswordInvalid ="${StringUtil.wrapString(uiLabelMap.PasswordInvalid)}";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.CommonYes = '${StringUtil.wrapString(uiLabelMap.CommonYes)?default("")}';
uiLabelMap.CommonNo = '${StringUtil.wrapString(uiLabelMap.CommonNo)?default("")}';
uiLabelMap.CommonClose = '${StringUtil.wrapString(uiLabelMap.CommonClose)?default("")}';
uiLabelMap.UpdateWorkingStatusIdRemind = '${StringUtil.wrapString(uiLabelMap.UpdateWorkingStatusIdRemind)?default("")}';
uiLabelMap.CommonFromDate = '${StringUtil.wrapString(uiLabelMap.CommonFromDate)?default("")}';
uiLabelMap.CommonThruDate = '${StringUtil.wrapString(uiLabelMap.CommonThruDate)?default("")}';
uiLabelMap.accAddNewRow = '${StringUtil.wrapString(uiLabelMap.accAddNewRow)?default("")}';
uiLabelMap.FieldRequired = '${StringUtil.wrapString(uiLabelMap.FieldRequired)?default("")}';
uiLabelMap.CommonSubmit = '${StringUtil.wrapString(uiLabelMap.CommonSubmit)?default("")}';
uiLabelMap.CommonCancel = '${StringUtil.wrapString(uiLabelMap.CommonCancel)?default("")}';
uiLabelMap.ValueEqualOrAfterDate = '${StringUtil.wrapString(uiLabelMap.ValueEqualOrAfterDate)?default("")}';
uiLabelMap.wgdelete = '${StringUtil.wrapString(uiLabelMap.wgdelete)?default("")}';
uiLabelMap.CannotDeleteRow = '${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)?default("")}';
uiLabelMap.wgdeleteconfirm = '${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)?default("")}';
uiLabelMap.wgok = '${StringUtil.wrapString(uiLabelMap.wgok)?default("")}';
uiLabelMap.wgcancel = '${StringUtil.wrapString(uiLabelMap.wgcancel)?default("")}';
uiLabelMap.NotifyDelete = '${StringUtil.wrapString(uiLabelMap.NotifyDelete)?default("")}';

uiLabelMap.HRFullName = "${StringUtil.wrapString(uiLabelMap.HRFullName)}";
uiLabelMap.HRRelationship = "${StringUtil.wrapString(uiLabelMap.HRRelationship)}";
uiLabelMap.BirthDate = "${StringUtil.wrapString(uiLabelMap.BirthDate)}";
uiLabelMap.HROccupation = "${StringUtil.wrapString(uiLabelMap.HROccupation)}";
uiLabelMap.placeWork = "${StringUtil.wrapString(uiLabelMap.placeWork)}";
uiLabelMap.PartyPhoneNumber = "${StringUtil.wrapString(uiLabelMap.PartyPhoneNumber)}";
uiLabelMap.PersonDependent = "${StringUtil.wrapString(uiLabelMap.PersonDependent)}";
uiLabelMap.FamilyMembers = "${StringUtil.wrapString(uiLabelMap.FamilyMembers)}";
uiLabelMap.IsPersonDependent = "${StringUtil.wrapString(uiLabelMap.IsPersonDependent)}";
uiLabelMap.HRApprove = "${StringUtil.wrapString(uiLabelMap.HRApprove)}";
uiLabelMap.DependentDeductionEnd = "${StringUtil.wrapString(uiLabelMap.DependentDeductionEnd)}";
uiLabelMap.DependentDeductionStart = "${StringUtil.wrapString(uiLabelMap.DependentDeductionStart)}";
uiLabelMap.HRCommonRegister = "${StringUtil.wrapString(uiLabelMap.HRCommonRegister)}";
uiLabelMap.PleaseSelectOption = "${StringUtil.wrapString(uiLabelMap.PleaseSelectOption)}";
uiLabelMap.ChangeAvatar = "${StringUtil.wrapString(uiLabelMap.ChangeAvatar)}";
uiLabelMap.ClickToChooseNewAvatar = "${StringUtil.wrapString(uiLabelMap.ClickToChooseNewAvatar)}";
uiLabelMap.ResignDateAfterNowDate = "${StringUtil.wrapString(uiLabelMap.ResignDateAfterNowDate)}";
uiLabelMap.AddNewRowConfirm = "${StringUtil.wrapString(uiLabelMap.AddNewRowConfirm)}";
uiLabelMap.EmailRequired = "${StringUtil.wrapString(uiLabelMap.EmailRequired)}";
uiLabelMap.OnlyNumberInput = "${StringUtil.wrapString(uiLabelMap.OnlyNumberInput)}";
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}";
uiLabelMap.NotSetting = "${StringUtil.wrapString(uiLabelMap.NotSetting)}";
uiLabelMap.NewPositionTypeMustBeDiffOldPositionType = "${StringUtil.wrapString(uiLabelMap.NewPositionTypeMustBeDiffOldPositionType)}";
uiLabelMap.NewPositionFromDateGreaterThanOldPositionFromDate = "${StringUtil.wrapString(uiLabelMap.NewPositionFromDateGreaterThanOldPositionFromDate)}";
uiLabelMap.OnlyAllowValueAfterOldPositionThruDateOneDay = "${StringUtil.wrapString(uiLabelMap.OnlyAllowValueAfterOldPositionThruDateOneDay)}";
uiLabelMap.DateJoinCompanyMustLessThanResignDate = "${StringUtil.wrapString(uiLabelMap.DateJoinCompanyMustLessThanResignDate)}";
uiLabelMap.ChangePositionAndDeptConfirm = "${StringUtil.wrapString(uiLabelMap.ChangePositionAndDeptConfirm)}";
uiLabelMap.password_did_not_match_verify_password = '${StringUtil.wrapString(uiLabelMap["password_did_not_match_verify_password"])}';
uiLabelMap.HREmplPositionTypeId = '${StringUtil.wrapString(uiLabelMap.HREmplPositionTypeId)}';
uiLabelMap.CommonGroup = '${StringUtil.wrapString(uiLabelMap.CommonGroup)}';
uiLabelMap.CommonDescription = '${StringUtil.wrapString(uiLabelMap.CommonDescription)}';
uiLabelMap.CreatePositionTypeForOrgConfirm = '${StringUtil.wrapString(uiLabelMap.CreatePositionTypeForOrgConfirm)}';
uiLabelMap.HRCheckName = '${StringUtil.wrapString(uiLabelMap.HRCheckName)}';
uiLabelMap.HRCheckFullName = '${StringUtil.wrapString(uiLabelMap.HRCheckFullName)}';
uiLabelMap.HRCheckSpecialCharacter = '${StringUtil.wrapString(uiLabelMap.HRCheckSpecialCharacter)}';
uiLabelMap.HRCheckPhone = '${StringUtil.wrapString(uiLabelMap.HRCheckPhone)}';
uiLabelMap.HRPhoneIsNotValid = '${StringUtil.wrapString(uiLabelMap.HRPhoneIsNotValid)}';
uiLabelMap.HRCharacterIsNotValid = '${StringUtil.wrapString(uiLabelMap.HRCharacterIsNotValid)}';
uiLabelMap.HRBirthDateBeforeToDay = '${StringUtil.wrapString(uiLabelMap.HRBirthDateBeforeToDay)}';
uiLabelMap.HRCheckId = '${StringUtil.wrapString(uiLabelMap.HRCheckId)}';
uiLabelMap.HRCheckIdCard = '${StringUtil.wrapString(uiLabelMap.HRCheckIdCard)}';
uiLabelMap.HRIdCardIsNotValid = '${StringUtil.wrapString(uiLabelMap.HRIdCardIsNotValid)}';
uiLabelMap.HRCheckAddress = '${StringUtil.wrapString(uiLabelMap.HRCheckAddress)}';
uiLabelMap.HREmailIsNotValid = '${StringUtil.wrapString(uiLabelMap.HREmailIsNotValid)}';
uiLabelMap.HRCheckEmail = '${StringUtil.wrapString(uiLabelMap.HRCheckEmail)}';
uiLabelMap.HRCheckInsurance = '${StringUtil.wrapString(uiLabelMap.HRCheckInsurance)}';
uiLabelMap.ResetPasswordConfirm = '${StringUtil.wrapString(uiLabelMap.ResetPasswordConfirm)}';
uiLabelMap.HrCommonPosition = '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}';
uiLabelMap.ExpireEmplPositionWarning = '${StringUtil.wrapString(uiLabelMap.ExpireEmplPositionWarning)}';
uiLabelMap.HRLock = '${StringUtil.wrapString(uiLabelMap.HRLock)}';
uiLabelMap.HRUnLock = '${StringUtil.wrapString(uiLabelMap.HRUnLock)}';
uiLabelMap.HRYes = '${StringUtil.wrapString(uiLabelMap.HRYes)}';
uiLabelMap.HRNo = '${StringUtil.wrapString(uiLabelMap.HRNo)}';
uiLabelMap.HRUnLocked = '${StringUtil.wrapString(uiLabelMap.HRUnLocked)}';
uiLabelMap.HRLocked = '${StringUtil.wrapString(uiLabelMap.HRLocked)}';
uiLabelMap.HRLoggedOut = '${StringUtil.wrapString(uiLabelMap.HRLoggedOut)}';
uiLabelMap.HRLoggedIn = '${StringUtil.wrapString(uiLabelMap.HRLoggedIn)}';
uiLabelMap.HRAfterNowDate = '${StringUtil.wrapString(uiLabelMap.HRAfterNowDate)}';
uiLabelMap.HRChangeInfoEmplAccount = '${StringUtil.wrapString(uiLabelMap.HRChangeInfoEmplAccount)}';
uiLabelMap.HRDepartmentId = '${StringUtil.wrapString(uiLabelMap.HRDepartmentId)}';
uiLabelMap.HRDepartmentManagerId = '${StringUtil.wrapString(uiLabelMap.HRDepartmentManagerId)}';
uiLabelMap.HRDepartmentManagerName = '${StringUtil.wrapString(uiLabelMap.HRDepartmentManagerName)}';


</script>