<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true />
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#if !rootOrgList?exists>
	<#assign rootOrgList = [Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)]/>
</#if>
var globalVar = {};
var uiLabelMap = {};
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

globalVar.postedArr = [
   	{isPosted: "ALL", description: '${StringUtil.wrapString(uiLabelMap.CommonAll)}'},                       
   	{isPosted: "Y", description: '${StringUtil.wrapString(uiLabelMap.BACCPostted)}'},                       
   	{isPosted: "N", description: '${StringUtil.wrapString(uiLabelMap.BACCNotPostted)}'},                       
];

globalVar.orgUseTypeArr = [
	{type: 'productStore', description: '${StringUtil.wrapString(uiLabelMap.BSProductStore)}'},                           
	{type: 'internalOrganization', description: '${StringUtil.wrapString(uiLabelMap.BACCInternalOrganization)}'},                           
];

globalVar.rootPartyArr = [
	<#if rootOrgList?has_content>
		<#list rootOrgList as rootOrgId>
		<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
		{
			partyId: "${rootOrgId}",
			partyName: "${StringUtil.wrapString(rootOrg.groupName)}"
		},
		</#list>
	</#if>
];
globalVar.monthArr = [
	{month: 0, description: '${StringUtil.wrapString(uiLabelMap.BACCJanuary)}'},                    
	{month: 1, description: '${StringUtil.wrapString(uiLabelMap.BACCFebruary)}'},                    
	{month: 2, description: '${StringUtil.wrapString(uiLabelMap.BACCMarch)}'},                    
	{month: 3, description: '${StringUtil.wrapString(uiLabelMap.BACCApril)}'},                    
	{month: 4, description: '${StringUtil.wrapString(uiLabelMap.BACCMay)}'},                    
	{month: 5, description: '${StringUtil.wrapString(uiLabelMap.BACCJune)}'},                    
	{month: 6, description: '${StringUtil.wrapString(uiLabelMap.BACCJuly)}'},                    
	{month: 7, description: '${StringUtil.wrapString(uiLabelMap.BACCAugust)}'},                    
	{month: 8, description: '${StringUtil.wrapString(uiLabelMap.BACCSeptember)}'},                    
	{month: 9, description: '${StringUtil.wrapString(uiLabelMap.BACCOctober)}'},                    
	{month: 10, description: '${StringUtil.wrapString(uiLabelMap.BACCNovember)}'},                    
	{month: 11, description: '${StringUtil.wrapString(uiLabelMap.BACCDecember)}'},                    
];

<#if defaultCostGlAccountId?exists>
globalVar.defaultCostGlAccountId = "${defaultCostGlAccountId}";
</#if>

<#if defaultCreditGlAccountId?exists>
globalVar.defaultCreditGlAccountId = "${defaultCreditGlAccountId}"
</#if>

<#if defaultDebitGlAccountId?exists>
globalVar.defaultDebitGlAccountId = "${defaultDebitGlAccountId}"
</#if>

uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.BACCEquipmentId = "${StringUtil.wrapString(uiLabelMap.BACCEquipmentId)}";
uiLabelMap.BACCUnpostedConfirm = "${StringUtil.wrapString(uiLabelMap.BACCUnpostedConfirm)}";
uiLabelMap.BACCEquimentName = "${StringUtil.wrapString(uiLabelMap.BACCEquimentName)}";
uiLabelMap.BACCTotalAllocatedAmount = "${StringUtil.wrapString(uiLabelMap.BACCTotalAllocatedAmount)}";
uiLabelMap.BACCAllocationAmountOfUsingEquipment = "${StringUtil.wrapString(uiLabelMap.BACCAllocationAmountOfUsingEquipment)}";
uiLabelMap.BACCEquipment = "${StringUtil.wrapString(uiLabelMap.BACCEquipment)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.BACCTotalCost = "${StringUtil.wrapString(uiLabelMap.BACCTotalCost)}";
uiLabelMap.BACCAllocPartyId = "${StringUtil.wrapString(uiLabelMap.BACCAllocPartyId)}";
uiLabelMap.BACCPercent = "${StringUtil.wrapString(uiLabelMap.BACCPercent)}";
uiLabelMap.BACCAmount = "${StringUtil.wrapString(uiLabelMap.BACCAmount)}";
uiLabelMap.BACCCostGlAccount = "${StringUtil.wrapString(uiLabelMap.BACCCostGlAccount)}";
uiLabelMap.BACCAllocation = "${StringUtil.wrapString(uiLabelMap.BACCAllocation)}";
uiLabelMap.ValueMustBeLessThanEqualTotalAmount = "${StringUtil.wrapString(uiLabelMap.ValueMustBeLessThanEqualTotalAmount)}";
uiLabelMap.EquipmentIsNotSelected = "${StringUtil.wrapString(uiLabelMap.EquipmentIsNotSelected)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonAll = "${StringUtil.wrapString(uiLabelMap.CommonAll)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.EquipmentIsAllocatedForParty_updateConfirm = "${StringUtil.wrapString(uiLabelMap.EquipmentIsAllocatedForParty_updateConfirm)}";
uiLabelMap.BACCEquipmentAllocatePercentNotEqual100 = "${StringUtil.wrapString(uiLabelMap.BACCEquipmentAllocatePercentNotEqual100)}";
uiLabelMap.BACCCreateEquipmentAllocateConfirm = "${StringUtil.wrapString(uiLabelMap.BACCCreateEquipmentAllocateConfirm)}";
uiLabelMap.BACCConfirmDelete = "${StringUtil.wrapString(uiLabelMap.BACCConfirmDelete)}";
uiLabelMap.BSProductStoreId = "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}";
uiLabelMap.BSStoreName = "${StringUtil.wrapString(uiLabelMap.BSStoreName)}";
uiLabelMap.DebitAccount = "${StringUtil.wrapString(uiLabelMap.DebitAccount)}";
uiLabelMap.CreditAccount = "${StringUtil.wrapString(uiLabelMap.CreditAccount)}";
uiLabelMap.BACCPrepaidExpensesAccount = "${StringUtil.wrapString(uiLabelMap.BACCPrepaidExpensesAccount)}";
uiLabelMap.BACCInstrumentToolsAccount = "${StringUtil.wrapString(uiLabelMap.BACCInstrumentToolsAccount)}";
uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCAmountTotal)}";
uiLabelMap.CommonSelect = "${StringUtil.wrapString(uiLabelMap.CommonSelect)}";
uiLabelMap.BACCPleaseChooseAcc = "${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc)}";
uiLabelMap.BACCYouNotYetChooseEquipment = "${StringUtil.wrapString(uiLabelMap.BACCYouNotYetChooseEquipment)}";
uiLabelMap.DateArising = "${StringUtil.wrapString(uiLabelMap.DateArising)}";
</script>