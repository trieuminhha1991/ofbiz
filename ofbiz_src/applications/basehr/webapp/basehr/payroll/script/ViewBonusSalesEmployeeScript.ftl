<@jqGridMinimumLib/>
<script src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign prevMonthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp, 0, -1)/>
<#assign prevMonthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>	
<script type="text/javascript">
var statusArr = [
	<#if statusList?has_content>
		<#list statusList as status>
			{
				statusId: "${status.statusId}",
				description: "${StringUtil.wrapString(status.description)}"
			},
		</#list>
	</#if>
];

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>

var globalVar = {
		rootPartyArr: [
   			<#if rootOrgList?has_content>
   				<#list rootOrgList as rootOrgId>
   				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
   				{
   					partyId: "${rootOrgId}",
   					partyName: "${rootOrg.groupName}"
   				},
   				</#list>
   			</#if>
   		],
		prevMonthStart: ${prevMonthStart.getTime()},
		prevMonthEnd: ${prevMonthEnd.getTime()}	
};

var uiLabelMap = {};
uiLabelMap.NoDataRecordSelectedToConfirm = "${StringUtil.wrapString(uiLabelMap.NoDataRecordSelectedToConfirm)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.AreYouSure = "${StringUtil.wrapString(uiLabelMap.AreYouSure)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

</script>