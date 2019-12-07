<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>

<script type="text/javascript">
var globalVar = {};
<#assign cal = Static["java.util.Calendar"].getInstance()/>
globalVar.YEAR = ${cal.get(Static["java.util.Calendar"].YEAR)};

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
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
		   		
globalVar.emplLeaveReasonArr = [
	<#if emplLeaveReasonList?has_content>
		<#list emplLeaveReasonList as emplLeaveReason>
			{
				emplLeaveReasonTypeId: '${emplLeaveReason.emplLeaveReasonTypeId}',
				description: "${StringUtil.wrapString(emplLeaveReason.description)}",
				<#if emplLeaveReason.rateBenefit?exists>
					<#assign rateBenefit = emplLeaveReason.rateBenefit * 100/>  
					rateBenefit: '${rateBenefit}'
				</#if>
			},
		</#list>
	</#if>
];		  

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

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {
			FromDateLessThanEqualThruDate : '${StringUtil.wrapString(uiLabelMap.FromDateLessThanEqualThruDate)}',
			GTDateFieldRequired : '${StringUtil.wrapString(uiLabelMap.GTDateFieldRequired)}',
	};
}

</script>