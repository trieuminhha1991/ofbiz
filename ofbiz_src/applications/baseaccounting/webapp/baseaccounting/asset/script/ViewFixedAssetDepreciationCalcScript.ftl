<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true />
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
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

uiLabelMap.BACCFixedAssetIdShort = "${StringUtil.wrapString(uiLabelMap.BACCFixedAssetIdShort)}";
uiLabelMap.BACCFixedAssetName = "${StringUtil.wrapString(uiLabelMap.BACCFixedAssetName)}";
uiLabelMap.OrganizationUsed = "${StringUtil.wrapString(uiLabelMap.OrganizationUsed)}";
uiLabelMap.DebitAccount = "${StringUtil.wrapString(uiLabelMap.DebitAccount)}";
uiLabelMap.CreditAccount = "${StringUtil.wrapString(uiLabelMap.CreditAccount)}";
uiLabelMap.BACCPurchaseCost = "${StringUtil.wrapString(uiLabelMap.BACCPurchaseCost)}";
uiLabelMap.BACCMonthlyDepRate = "${StringUtil.wrapString(uiLabelMap.BACCMonthlyDepRate)}";
uiLabelMap.BACCDepreciationAmount = "${StringUtil.wrapString(uiLabelMap.BACCDepreciationAmount)}";
uiLabelMap.FixedAssetDepreciationCalc = "${StringUtil.wrapString(uiLabelMap.FixedAssetDepreciationCalc)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.BACCPurCostAcc = "${StringUtil.wrapString(uiLabelMap.BACCPurCostAcc)}";
uiLabelMap.BACCdepGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCdepGlAccountId)}";
uiLabelMap.BACCMonthlyDepAmount = "${StringUtil.wrapString(uiLabelMap.BACCMonthlyDepAmount)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CreateFixedAssetDepreciationCalcConfirm = "${StringUtil.wrapString(uiLabelMap.CreateFixedAssetDepreciationCalcConfirm)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.BACCPostedFixAssetConfirm = "${StringUtil.wrapString(uiLabelMap.BACCPostedFixAssetConfirm)}";
uiLabelMap.BACCAllocGlAccoutId = "${StringUtil.wrapString(uiLabelMap.BACCAllocGlAccoutId)}";
uiLabelMap.BACCDateAcquired = "${StringUtil.wrapString(uiLabelMap.BACCDateAcquired)}";
uiLabelMap.DateOfIncrease = "${StringUtil.wrapString(uiLabelMap.DateOfIncrease)}";
uiLabelMap.BACCExpectedEndOfLife = "${StringUtil.wrapString(uiLabelMap.BACCExpectedEndOfLife)}";
uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCAmountTotal)}";
uiLabelMap.BACCYouNotYetChooseFA = "${StringUtil.wrapString(uiLabelMap.BACCYouNotYetChooseFA)}";
</script>