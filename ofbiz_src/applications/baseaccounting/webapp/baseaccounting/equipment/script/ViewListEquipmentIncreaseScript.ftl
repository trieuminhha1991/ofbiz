<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.postedArr = [
	{isPosted: "ALL", description: '${StringUtil.wrapString(uiLabelMap.CommonAll)}'},                       
	{isPosted: "Y", description: '${StringUtil.wrapString(uiLabelMap.BACCPostted)}'},                       
	{isPosted: "N", description: '${StringUtil.wrapString(uiLabelMap.BACCNotPostted)}'},                       
];
uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
uiLabelMap.BACCConfirmDelete = "${StringUtil.wrapString(uiLabelMap.BACCConfirmDelete)}";
uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
uiLabelMap.BACCEquipmentId = "${StringUtil.wrapString(uiLabelMap.BACCEquipmentId)}";
uiLabelMap.BACCEquimentName = "${StringUtil.wrapString(uiLabelMap.BACCEquimentName)}";
uiLabelMap.BACCEquipQuantityUom = "${StringUtil.wrapString(uiLabelMap.BACCEquipQuantityUom)}";
uiLabelMap.BACCAllowTimes = "${StringUtil.wrapString(uiLabelMap.BACCAllowTimes)}";
uiLabelMap.DebitAccount = "${StringUtil.wrapString(uiLabelMap.DebitAccount)}";
uiLabelMap.BACCCostGlAccount = "${StringUtil.wrapString(uiLabelMap.BACCCostGlAccount)}";
uiLabelMap.BACCPrepaidExpensesAccount = "${StringUtil.wrapString(uiLabelMap.BACCPrepaidExpensesAccount)}";
uiLabelMap.BACCInstrumentToolsAccount = "${StringUtil.wrapString(uiLabelMap.BACCInstrumentToolsAccount)}";
uiLabelMap.BACCQuantity = "${StringUtil.wrapString(uiLabelMap.BACCQuantity)}";
uiLabelMap.BACCUnitPrice = "${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}";
uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCTotal)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.EquipmentIsNotSelected = "${StringUtil.wrapString(uiLabelMap.EquipmentIsNotSelected)}";
uiLabelMap.CreateEquipmentIncreaseConfirm = "${StringUtil.wrapString(uiLabelMap.CreateEquipmentIncreaseConfirm)}";
uiLabelMap.BACCUnpostedConfirm = "${StringUtil.wrapString(uiLabelMap.BACCUnpostedConfirm)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.BACCDepAmount = "${StringUtil.wrapString(uiLabelMap.BACCDepAmount)}";
<#if defaultCostGlAccountId?exists>
	globalVar.defaultCostGlAccountId = "${defaultCostGlAccountId}";
</#if>
</script>