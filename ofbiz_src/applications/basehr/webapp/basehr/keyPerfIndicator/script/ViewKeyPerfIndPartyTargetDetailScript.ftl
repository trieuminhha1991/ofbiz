<@jqGridMinimumLib/>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript">
var uiLabelMap = {};
var globalVar = {};
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign listDirectChildDept = Static["com.olbius.basehr.util.PartyUtil"].getListDirectSubOrgOfParty(delegator, [keyPerfIndPartyTarget.partyId])!/>
<#assign listDirectEmpl = Static["com.olbius.basehr.util.PartyUtil"].getListDirectEmplOfParty(delegator, [keyPerfIndPartyTarget.partyId], nowTimestamp, nowTimestamp)!/>

globalVar.partyTargetId = "${partyTargetId}";
globalVar.directChildDeptArr = [
	<#if listDirectChildDept?has_content>
		<#list listDirectChildDept as directChildDept>
		<#assign party = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", directChildDept.partyId), false)/>
		{
			partyId: "${directChildDept.partyId}",
			groupName: "${StringUtil.wrapString(party.groupName)}"
		},
		</#list>
	</#if>
];
globalVar.keyPerfIndPartyTargetItemList = [
	<#if keyPerfIndPartyTargetItemList?has_content>
		<#list keyPerfIndPartyTargetItemList as keyPerfIndPartyTargetItem>
		{
			keyPerfIndicatorId: "${keyPerfIndPartyTargetItem.keyPerfIndicatorId}",
			keyPerfIndicatorName: "${StringUtil.wrapString(keyPerfIndPartyTargetItem.keyPerfIndicatorName)}",
		},
		</#list>
	</#if>
];
globalVar.directEmplArr = [
	<#if listDirectEmpl?has_content>
		<#list listDirectEmpl as employee>
		{
			partyId: "${employee.partyId}",
			fullName: "${StringUtil.wrapString(employee.fullName)}"
		},
		</#list>
	</#if>
];

globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
		{
			periodTypeId: '${periodType.periodTypeId}',
			description: '${StringUtil.wrapString(periodType.description)}',
			uomId: '${periodType.uomId}',
			periodLength: ${periodType.periodLength},
		},
		</#list>
	</#if>
];
globalVar.uomArr = [
	<#if uomList?has_content>
		<#list uomList as uom>
		{
			uomId: '${uom.uomId}',
			description: '${StringUtil.wrapString(uom.description)}',
			abbreviation: '${StringUtil.wrapString(uom.abbreviation)}',
		},
		</#list>
	</#if>
];
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CreateKPIAllocateTargetConfirm = "${StringUtil.wrapString(uiLabelMap.CreateKPIAllocateTargetConfirm)}";
</script>