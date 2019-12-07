<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
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

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp)/>
globalVar.nowTimestamp = ${startDate.getTime()};
globalVar.startDate = ${startDate.getTime()};
globalVar.partyId = "${userLogin.partyId}";

<#assign ptr = delegator.findOne("Party", {"partyId" : userLogin.partyId?if_exists}, false)/>
globalVar.partyCode = "${ptr.partyCode?if_exists}";

globalVar.partyName = '${StringUtil.wrapString(Static["com.olbius.basehr.util.PartyUtil"].getPersonName(delegator, userLogin.partyId))}';
<#assign emplPositionList = Static["com.olbius.basehr.util.PartyUtil"].getPositionTypeOfEmplAtTime(delegator, userLogin.partyId, nowTimestamp)/>
<#assign departmentList = Static["com.olbius.basehr.util.PartyUtil"].getDepartmentOfEmployee(delegator, userLogin.partyId, nowTimestamp)/>
globalVar.emplPositionType = "<#if emplPositionList?has_content><#list emplPositionList as emplPosition>${StringUtil.wrapString(emplPosition.description)}<#if emplPosition_has_next>, </#if></#list></#if>";
globalVar.partyIdFrom = '<#if departmentList?has_content><#list departmentList as departmentId>${StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, departmentId, false))}<#if departmentId_has_next>, </#if></#list></#if>';
var uiLabelMap = {};
uiLabelMap.CancelEmplLeaveApplConfirm = "${StringUtil.wrapString(uiLabelMap.CancelEmplLeaveApplConfirm)}";
</script>
