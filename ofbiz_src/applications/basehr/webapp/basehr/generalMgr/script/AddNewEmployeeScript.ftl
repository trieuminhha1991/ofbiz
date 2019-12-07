<script type="text/javascript">
<#assign listReligionTypes = delegator.findList("Religion", null, null, null, null, false) />
<#assign listNationalityTypes = delegator.findList("Nationality", null, null, null, null, false) />
<#assign ethnicOriginList = delegator.findList("EthnicOrigin", null , null, null,null, false)>
<#assign maritalStatusList = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "MARITAL_STATUS"), orderBy, false)>

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
<#assign defaultPwd = Static["com.olbius.basesales.util.SalesUtil"].getSystemConfigValue(delegator, "DEFAULT_PWD")!/>
<#if !nowTimestamp?exists>
	<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
</#if>
var defaultPwd="${defaultPwd?if_exists}"
if(typeof(globalVar) == "undefined"){
	globalVar = {};
}
if(typeof(uiLabelMap) == "undefined"){
	uiLabelMap = {};
}
globalVar.defaultSuffix = "${defaultSuffix?if_exists}";
globalVar.profileInfoDiv = "generalInfo";		
globalVar.rootPartyArr = [
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
globalVar.nowTimestamp = ${nowTimestamp.getTime()};


var globalObjectAddNewEmpl = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

<#if defaultCountry?has_content>
	globalVar.defaultCountry = "${defaultCountry}";
</#if>
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
globalVar.insuranceTypeArr = [
	<#if insuranceTypeList?has_content>
		<#list insuranceTypeList as insuranceType>
		{
			insuranceTypeId: "${insuranceType.insuranceTypeId}",
			description: "${StringUtil.wrapString(insuranceType.description)}",
			isCompulsory: "${StringUtil.wrapString(insuranceType.isCompulsory)}",
			employeeRate: "${insuranceType.employeeRate * 100}".replace(/,/g,".")
		},
		</#list>
	</#if>
];
var initSourceGeneral = (function(){
        var statusWorkingArr = [
			<#if statusWorkingList?has_content>
				<#list statusWorkingList as status>
				{
					statusId: '${status.statusId}',
					description: '${StringUtil.wrapString(status.description)}'
				},
				</#list>
			</#if>
		];
        
        var terminationReasonArr = [
        	<#if terminationReasonList?has_content>
        		<#list terminationReasonList as terminationReason>
        		{
        			terminationReasonId: "${terminationReason.terminationReasonId}",
        			description: "${StringUtil.wrapString(terminationReason.description)}"
        		},
        		</#list>
        	</#if>
		];
		
		return {
			statusWorkingArr: statusWorkingArr,
			terminationReasonArr: terminationReasonArr
		};
}());
</script>
