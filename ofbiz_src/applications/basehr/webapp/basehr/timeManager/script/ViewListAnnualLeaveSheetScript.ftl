<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js" type="text/javascript"></script>

<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
<#assign startYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(nowTimestamp)/>
<#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(nowTimestamp, timeZone, locale)/>
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
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
</script>