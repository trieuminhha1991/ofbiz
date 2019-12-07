<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
<#assign listReligionTypes = delegator.findList("Religion", null, null, null, null, false) />
<#assign listNationalityTypes = delegator.findList("Nationality", null, null, null, null, false) />
<#assign ethnicOriginList = delegator.findList("EthnicOrigin", null , null, null,null, false)>
<#assign maritalStatusList = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "MARITAL_STATUS"), orderBy, false)>

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var globalVar = {};
var uiLabelMap = {};
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
globalVar.ethnicOriginList = [
 	<#list ethnicOriginList as ethnicOrigin1>
	{
		ethnicOriginId : "${ethnicOrigin1.ethnicOriginId}",
		description : "${StringUtil.wrapString(ethnicOrigin1.description)}"
	},
	</#list>	
];
globalVar.religionTypes = [
      	<#if listReligionTypes?has_content>
			<#list listReligionTypes as religionT>
			{
		    	religionId : "${religionT.religionId}",
		        description : "${StringUtil.wrapString(religionT.description)}"
			},
			</#list>	
		</#if>
];
globalVar.nationalityTypes = [
     	<#if listNationalityTypes?has_content>
			<#list listNationalityTypes as nationalityT >
		    {
		    	nationalityId : "${nationalityT.nationalityId}",
		    	description : "${StringUtil.wrapString(nationalityT.description)}"
		    },
		    </#list>	
		</#if>
];

globalVar.maritalStatusList = [
	<#if maritalStatusList?has_content>
	  	<#list maritalStatusList as maritalStatus1>
		{
			maritalStatusId : "${maritalStatus1.statusId}",
			description : "${StringUtil.wrapString(maritalStatus1.description)}"
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
globalVar.geoArr =  [
        <#if geoList?has_content>
			<#list geoList as geo>
				{
					geoId: '${geo.geoId}',
					geoName: "${StringUtil.wrapString(geo.geoName?if_exists)}"
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
globalVar.geoCountryList = [
  		<#if geoCountryList?has_content>
 			<#list geoCountryList as geo>
 				{
 					geoId: '${geo.geoId}',
 					geoName: "${StringUtil.wrapString(geo.geoName?if_exists)}"
 				},
 			</#list>
 		</#if>                  	
];
globalVar.statusArr = [
  		<#if statusList?has_content>
 			<#list statusList as status>
 				{
 					statusId: '${status.statusId}',
 					description: "${StringUtil.wrapString(status.description?if_exists)}"
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
<#if defaultCountry?has_content>
	globalVar.defaultCountry = "${defaultCountry}";
</#if>

uiLabelMap.CommonSubmit= "${uiLabelMap.CommonSubmit}";
uiLabelMap.CommonClose= "${uiLabelMap.CommonClose}";
uiLabelMap.FieldRequired= '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}';
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.LTCurrentDateRequired = "${StringUtil.wrapString(uiLabelMap.LTCurrentDateRequired)}";
uiLabelMap.BirthDateBefIdentifyCardDay = "${StringUtil.wrapString(uiLabelMap.BirthDateBefIdentifyCardDay)}";
uiLabelMap.MustntHaveSpaceChar = "${StringUtil.wrapString(uiLabelMap.MustntHaveSpaceChar)}";
uiLabelMap.IdentifyDayGreaterBirthDate = "${StringUtil.wrapString(uiLabelMap.IdentifyDayGreaterBirthDate)}";
uiLabelMap.OnlyInputNumberGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}";
uiLabelMap.AddNewEmplToSalesRecruitmentWarning = "${StringUtil.wrapString(uiLabelMap.AddNewEmplToSalesRecruitmentWarning)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.Sexual = "${StringUtil.wrapString(uiLabelMap.Sexual)}";
uiLabelMap.certProvisionId = "${StringUtil.wrapString(uiLabelMap.certProvisionId)}";
uiLabelMap.BirthDate = "${StringUtil.wrapString(uiLabelMap.BirthDate)}";
uiLabelMap.NativeLand = "${StringUtil.wrapString(uiLabelMap.NativeLand)}";
uiLabelMap.CurrentResidence = "${StringUtil.wrapString(uiLabelMap.CurrentResidence)}";
uiLabelMap.HRCommonEmail = "${StringUtil.wrapString(uiLabelMap.HRCommonEmail)}";
uiLabelMap.PhoneNumber = "${StringUtil.wrapString(uiLabelMap.PhoneNumber)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.ListEmplRecruited = "${StringUtil.wrapString(uiLabelMap.ListEmplRecruited)}";
uiLabelMap.RecruitmentPosition = "${StringUtil.wrapString(uiLabelMap.RecruitmentPosition)}";
uiLabelMap.RecruitmentEnumType = "${StringUtil.wrapString(uiLabelMap.RecruitmentEnumType)}";
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
uiLabelMap.RecruitmentOffer = "${StringUtil.wrapString(uiLabelMap.RecruitmentOffer)}";
uiLabelMap.ListRecruitmentOffer = "${StringUtil.wrapString(uiLabelMap.ListRecruitmentOffer)}";
uiLabelMap.RecruitmentSaleEmplOfferConfirm = '${StringUtil.wrapString(uiLabelMap.RecruitmentSaleEmplOfferConfirm)}';
</script>