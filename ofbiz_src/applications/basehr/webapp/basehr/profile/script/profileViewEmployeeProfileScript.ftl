<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpasswordinput.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var globalVar = {
		<#if defaultCountry?exists>
			defaultCountry:"${defaultCountry}",
		</#if>
		<#if !permanentResidence.contactMechId?has_content>
			addPermanentResidenceBtn: true,
		</#if>
		<#if !currentResidence.contactMech?has_content>
			addCurrentResidenceBtn: true,
		</#if>
		<#if !partyEmail.emailAddress?exists>
			addEmailAddressBtn: true,
		</#if>
		userLogin_partyId: "${userLogin.partyId}",
		<#if !(personFamilyBackgroundEmercy?has_content && (personFamilyBackgroundEmercy?size > 0))>
			emergencyNotSetting: true,
		</#if>
		<#if lookupPerson.lastName?exists>
		lastName: "${StringUtil.wrapString(lookupPerson.lastName)}",
		</#if>
		<#if lookupPerson.middleName?exists>
		middleName: "${StringUtil.wrapString(lookupPerson.middleName)}",
		</#if>
		<#if lookupPerson.firstName?exists>
		firstName: '${StringUtil.wrapString(lookupPerson.firstName)}' 
		</#if>
};

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

var personFamilyBackgroundArr = [
		<#if personFamilyBackgroundList?has_content>
			<#list personFamilyBackgroundList as personFamilyBackground>
				<#assign telephoneNbr = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin", userLogin, "partyId", personFamilyBackground.partyFamilyId, "contactMechPurposeTypeId", "PRIMARY_PHONE"))/>
				{
					personFamilyBackgroundId: "${personFamilyBackground.personFamilyBackgroundId}",
					partyName: '${personFamilyBackground.lastName?if_exists} ${personFamilyBackground.middleName?if_exists} ${personFamilyBackground.firstName?if_exists}',
					partyRelationshipTypeId: "${personFamilyBackground.partyRelationshipTypeId}",
					telephone: '${telephoneNbr.countryCode?if_exists} ${telephoneNbr.areaCode?if_exists} ${telephoneNbr.contactNumber?if_exists}'
				},
			</#list>
		</#if>
];

<#assign partyRelationshipType = delegator.findByAnd("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "FAMILY"), null, false)>
var partyData = new Array();
var partyRelationshipType = [
		<#list partyRelationshipType as partyRelationship>
       		{
       			partyRelationshipTypeId : "${partyRelationship.partyRelationshipTypeId}",
       			partyRelationshipName : "${StringUtil.wrapString(partyRelationship.partyRelationshipName?default(''))}"
       		},
       	</#list>	
];

<#assign listReligionTypes = delegator.findList("Religion", null, null, null, null, false) />
<#assign listNationalityTypes = delegator.findList("Nationality", null, null, null, null, false) />
<#assign ethnicOriginList = delegator.findList("EthnicOrigin", null , null, null,null, false)>
<#assign maritalStatusList = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "MARITAL_STATUS"), orderBy, false)>
<#assign genderList = delegator.findList("Gender", null , null, orderBy,null, false)>

var geoArr = [
	<#if geoList?has_content>
		<#list geoList as geo>
			{
				geoId: '${geo.geoId}',
				geoName: '${StringUtil.wrapString(geo.geoName?if_exists)}'
			},
		</#list>
	</#if>
];

var geoCountryArr = [
	<#if geoCountryList?has_content>
		<#list geoCountryList as geo>
			{
				geoId: '${geo.geoId}',
				geoName: "${StringUtil.wrapString(geo.geoName?if_exists)}"
			},
		</#list>
	</#if>
];

var religionTypes = [
	<#if listReligionTypes?has_content>
		<#list listReligionTypes as religionT>
		{
	    	religionId : "${religionT.religionId}",
	        description : "${StringUtil.wrapString(religionT.description)}"
		},
		</#list>	
	</#if>
];

var nationalityTypes = [
	<#if listNationalityTypes?has_content>
		<#list listNationalityTypes as nationalityT >
	    {
	    	nationalityId : "${nationalityT.nationalityId}",
	    	description : "${StringUtil.wrapString(nationalityT.description)}"
	    },
	    </#list>	
	</#if>
];

var ethnicOriginList = [
	<#list ethnicOriginList as ethnicOrigin1>
	{
		ethnicOriginId : "${ethnicOrigin1.ethnicOriginId}",
		description : "${StringUtil.wrapString(ethnicOrigin1.description)}"
	},
	</#list>	
];

var maritalStatusList = [
	<#list maritalStatusList as maritalStatus1>
	{
		maritalStatusId : "${maritalStatus1.statusId}",
		description : "${StringUtil.wrapString(maritalStatus1.description)}"
	},
	</#list>	
];
 
var genderList = [
	<#if genderList?has_content>                   
		<#list genderList as gender1>
		{
			genderId : "${gender1.genderId}",
			description : "${StringUtil.wrapString(gender1.description)}"
		},
		</#list>	
	</#if>
];

var personInfo = {
		<#if lookupPerson.gender?exists>
		gender: "${lookupPerson.gender}",
		</#if>
		<#if lookupPerson.birthDate?exists>
		birthDate: "${lookupPerson.birthDate.getTime()}",
		</#if>
		<#if lookupPerson.idNumber?exists>
		idNumber: "${lookupPerson.idNumber}",
		</#if>
		<#if lookupPerson.idIssuePlace?exists>
		idIssuePlace: "${lookupPerson.idIssuePlace}",
		</#if>
		<#if lookupPerson.idIssueDate?exists>
		idIssueDate: "${lookupPerson.idIssueDate.getTime()}",	
		</#if>
		<#if lookupPerson.maritalStatusId?exists>
		maritalStatusId: "${lookupPerson.maritalStatusId}",
		</#if>
		<#if lookupPerson.numberChildren?exists>
		numberChildren: "${lookupPerson.numberChildren}",
		</#if>
		<#if lookupPerson.nativeLand?exists>
		nativeLand: "${lookupPerson.nativeLand}",
		</#if>
		<#if lookupPerson.ethnicOrigin?exists>
		ethnicOrigin: "${lookupPerson.ethnicOrigin}",
		</#if>
		<#if lookupPerson.religion?exists>
		religion: "${lookupPerson.religion}",
		</#if>
		<#if lookupPerson.nationality?exists>
		nationality: "${lookupPerson.nationality}",
		</#if>
		<#if lookupPerson.passportNumber?exists>
		passportNumber: "${lookupPerson.passportNumber}",
		</#if>
		<#if lookupPerson.passportIssuePlace?exists>
		passportIssuePlace: "${lookupPerson.passportIssuePlace}",
		</#if>
		<#if lookupPerson.passportIssueDate?exists>
		passportIssueDate: "${lookupPerson.passportIssueDate.getTime()}",
		</#if>
		<#if lookupPerson.passportExpiryDate?exists>
		passportExpiryDate: "${lookupPerson.passportExpiryDate.getTime()}"
		</#if>
};
var personPermanentResidenceContact = {
		<#if permanentResidence.contactMechId?has_content>
		contactMechId: "${permanentResidence.contactMechId}",
		address1: "${StringUtil.wrapString(permanentResidence.address1)}",
		</#if>
		<#if permanentResidence.countryGeoId?exists>
		countryGeoId: "${permanentResidence.countryGeoId}",
		</#if>
		<#if permanentResidence.stateProvinceGeoId?exists>
		stateProvinceGeoId: "${permanentResidence.stateProvinceGeoId}",
		</#if>
		<#if permanentResidence.countyGeoId?exists>
		countyGeoId: "${permanentResidence.countyGeoId}",
		</#if>
};
var personCurrentResidenceContact = {
		<#if currentResidence.contactMechId?has_content>
		contactMechId: "${currentResidence.contactMechId}",
		address1: "${StringUtil.wrapString(currentResidence.address1)}",
		</#if>
		<#if currentResidence.countryGeoId?exists>
		countryGeoId: "${currentResidence.countryGeoId}",
		</#if>
		<#if currentResidence.stateProvinceGeoId?exists>
		stateProvinceGeoId: "${currentResidence.stateProvinceGeoId}",
		</#if>
		<#if currentResidence.countyGeoId?exists>
		countyGeoId: "${currentResidence.countyGeoId}",
		</#if>
};

var personEmailContactMech = {
		<#if partyEmail.contactMechId?has_content>
		contactMechId: "${partyEmail.contactMechId}",
		emailAddress: "${StringUtil.wrapString(partyEmail.emailAddress)}"
		</#if>
};
var personPhoneNbrContactMech = {
		<#if phoneNumber.contactMechId?has_content>
		phoneNumberContactMechId: "${phoneNumber.contactMechId}",
		</#if>
		<#if phoneNumber.countryCode?exists>
		countryCode: "${StringUtil.wrapString(phoneNumber.countryCode)}",
		</#if>
		<#if phoneNumber.areaCode?exists>
		areaCode: "${StringUtil.wrapString(phoneNumber.areaCode)}",
		</#if>
		<#if phoneNumber.contactNumber?exists>
		contactNumber: "${StringUtil.wrapString(phoneNumber.contactNumber)}"
		</#if>
};
var personMobileContactMech = {
		<#if mobileNumber.contactMechId?has_content>
		contactMechId: "${mobileNumber.contactMechId}",
		</#if>
		<#if mobileNumber.countryCode?exists>
		countryCode: "${StringUtil.wrapString(mobileNumber.countryCode)}",
		</#if>
		<#if mobileNumber.areaCode?exists>
		areaCode: "${StringUtil.wrapString(mobileNumber.areaCode)}",
		</#if>
		<#if mobileNumber.contactNumber?exists>
		contactNumber: "${StringUtil.wrapString(mobileNumber.contactNumber)}"
		</#if>
};
</script>