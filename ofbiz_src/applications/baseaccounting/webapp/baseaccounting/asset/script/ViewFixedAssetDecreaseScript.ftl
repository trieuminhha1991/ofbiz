<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.decreaseReasonTypeArr = [
	<#if decreaseReasonTypeList?has_content>
		<#list decreaseReasonTypeList as decreaseReasonType>
		{
			decreaseReasonTypeId: '${decreaseReasonType.decreaseReasonTypeId}',
			description: '${StringUtil.wrapString(decreaseReasonType.description)}'
		},
		</#list>
	</#if>
];
uiLabelMap.BACCFixedAssetIdShort = "${StringUtil.wrapString(uiLabelMap.BACCFixedAssetIdShort)}";
uiLabelMap.BACCFixedAssetName = "${StringUtil.wrapString(uiLabelMap.BACCFixedAssetName)}";
uiLabelMap.OrganizationUsed = "${StringUtil.wrapString(uiLabelMap.OrganizationUsed)}";
uiLabelMap.BACCDepreciationGlAccount = "${StringUtil.wrapString(uiLabelMap.BACCDepreciationGlAccount)}";
uiLabelMap.BACCPurCostAcc = "${StringUtil.wrapString(uiLabelMap.BACCPurCostAcc)}";
uiLabelMap.BACCRemainValueGlAccount = "${StringUtil.wrapString(uiLabelMap.BACCRemainValueGlAccount)}";
uiLabelMap.BACCPurchaseCost = "${StringUtil.wrapString(uiLabelMap.BACCPurchaseCost)}";
uiLabelMap.BACCDepreciation = "${StringUtil.wrapString(uiLabelMap.BACCDepreciation)}";
uiLabelMap.AccumulatedDepreciationValue = "${StringUtil.wrapString(uiLabelMap.AccumulatedDepreciationValue)}";
uiLabelMap.BACCRemainingValue = "${StringUtil.wrapString(uiLabelMap.BACCRemainingValue)}";
uiLabelMap.BACCDecreasedFixedAsset = "${StringUtil.wrapString(uiLabelMap.BACCDecreasedFixedAsset)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.FixedAssetIsNotSelected = "${StringUtil.wrapString(uiLabelMap.FixedAssetIsNotSelected)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CreateFixedAssetDscreaseConfirm = "${StringUtil.wrapString(uiLabelMap.CreateFixedAssetDscreaseConfirm)}";
uiLabelMap.BACCCreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.BACCCreateNewConfirm)}";
uiLabelMap.BACCEditFixedAssetDecrease = "${StringUtil.wrapString(uiLabelMap.BACCEditFixedAssetDecrease)}";
uiLabelMap.BACCAddFixedAssetDecrease = "${StringUtil.wrapString(uiLabelMap.BACCAddFixedAssetDecrease)}";
uiLabelMap.BACCPostedFixAssetConfirm = "${StringUtil.wrapString(uiLabelMap.BACCPostedFixAssetConfirm)}";
uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
uiLabelMap.BACCTKChiPhiThanhLy = "${StringUtil.wrapString(uiLabelMap.BACCTKChiPhiThanhLy)}";
</script>