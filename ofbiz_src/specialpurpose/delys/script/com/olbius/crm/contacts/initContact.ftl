var uiLabelMap = {
	sender : '${uiLabelMap.sender}',
	receiver: '${uiLabelMap.receiver}',
	InTime : '${uiLabelMap.InTime}',
	communicationType : '${uiLabelMap.communicationType}'
};
var locale = "${locale}";
var commType = [<#if communicationEventTypes?exists><#list communicationEventTypes as type>{communicationEventTypeId: '${type.communicationEventTypeId}', description: '${StringUtil.wrapString(type.description)?default("")}'},</#list></#if>]
var genderData = [{
	gender: 'M',
	description : "${uiLabelMap.Male}"
},{
	gender : 'F',
	description : "${uiLabelMap.Female}"
}];
<#assign listMaritalStatus = delegator.findList("MaritalStatus", null, null, null, null, false) >
var maritalStatusData = [<#list listMaritalStatus as item>
	{<#assign description = StringUtil.wrapString(item.description?if_exists) />
		'maritalStatusId' : '${item.maritalStatusId}',
		'description' : '${description}'
	},
</#list>];

<#assign listEthnicOrigin = delegator.findList("EthnicOrigin", null, null, null, null, false) >
var ethnicOriginData = [<#list listEthnicOrigin as item>{
	<#assign description = StringUtil.wrapString(item.description?if_exists) />
	'maritalStatusId' : '${item.ethnicOriginId}',
	'description' : '${description}'
},</#list>];

<#assign listReligion = delegator.findList("Religion", null, null, null, null, false) >
var religionData = [<#list listReligion as item>{
	<#assign description = StringUtil.wrapString(item.description?if_exists) />
	'religionId' : '${item.religionId}',
	'description' : '${description}'
},</#list>];
//Prepare data for employment app source type 
<#assign listEmplAppSourceTypes = delegator.findList("EmploymentAppSourceType", null, null, null, null, false) >
var emplAppSourceTypeData = [<#list listEmplAppSourceTypes as item>
	{
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		employmentAppSourceTypeId : '${item.employmentAppSourceTypeId}',
		description : '${description}'
	},
</#list>];
<#assign countrylList = delegator.findByAnd("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"), null, false)>
var countryData = [<#list countrylList as item>{
	<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
	'geoId' : '${item.geoId}',
	'geoName' : "${description}"
},</#list>];
