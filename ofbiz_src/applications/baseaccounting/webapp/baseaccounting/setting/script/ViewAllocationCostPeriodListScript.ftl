<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
<#assign orgId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
<#assign listOrganization = Static["com.olbius.basehr.util.PartyUtil"].getListDirectSubOrgOfParty(delegator, [orgId])/>
<#assign partyAcctgPreference = delegator.findOne("PartyAcctgPreference", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", orgId), false)/>
<#if !listOrganization?has_content>
	<#assign organization = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", orgId), false)/>
	<#assign listOrganization = [organization]/>
</#if>
var globalVar = {};
var uiLabelMap = {};
globalVar.baseCurrencyUomId = "${partyAcctgPreference.baseCurrencyUomId}";
globalVar.organizationArr = [
	<#list listOrganization as organization>
	<#assign partyGroup = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", organization.partyId), false)/>
	{
		partyId: "${partyGroup.partyId}",
		groupName: "${StringUtil.wrapString(partyGroup.groupName)}"
	},
	</#list>
];

globalVar.allocationCostTypeArr = [
	<#if allocationCostTypeList?exists>
		<#list allocationCostTypeList as allocationCostType>
		{
			allocationCostTypeId: "${allocationCostType.allocationCostTypeId}",
			description: '${StringUtil.wrapString(allocationCostType.get("description", locale))}'
		},
		</#list>
	</#if>
];	

uiLabelMap.BACCGlAccountCode = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountCode)}";
uiLabelMap.BACCGlAccountName = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountName)}";
uiLabelMap.TotalCost = "${StringUtil.wrapString(uiLabelMap.TotalCost)}";
uiLabelMap.AllocationCostType = "${StringUtil.wrapString(uiLabelMap.AllocationCostType)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.BACCQuantity = "${StringUtil.wrapString(uiLabelMap.BACCQuantity)}";
uiLabelMap.BACCPercent = "${StringUtil.wrapString(uiLabelMap.BACCPercent)}";
uiLabelMap.BACCToalPercent = "${StringUtil.wrapString(uiLabelMap.BACCToalPercent)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.BSValidateDate = "${StringUtil.wrapString(uiLabelMap.BSValidateDate)}";
uiLabelMap.TotalRateAllocGlAccount = "${StringUtil.wrapString(uiLabelMap.TotalRateAllocGlAccount)}";
uiLabelMap.BACCNotEqualOneHundredPercent = "${StringUtil.wrapString(uiLabelMap.BACCNotEqualOneHundredPercent)}";
uiLabelMap.CreateAllocationCostPeriodConfirm = "${StringUtil.wrapString(uiLabelMap.CreateAllocationCostPeriodConfirm)}";
uiLabelMap.CostAllocationDetailShort = "${StringUtil.wrapString(uiLabelMap.CostAllocationDetailShort)}";
</script>