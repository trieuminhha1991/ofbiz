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
uiLabelMap.BACCEquipmentId = "${StringUtil.wrapString(uiLabelMap.BACCEquipmentId)}";
uiLabelMap.BACCConfirmDelete = "${StringUtil.wrapString(uiLabelMap.BACCConfirmDelete)}";
uiLabelMap.BACCEquimentName = "${StringUtil.wrapString(uiLabelMap.BACCEquimentName)}";
uiLabelMap.BACCQuantityInUse = "${StringUtil.wrapString(uiLabelMap.BACCQuantityInUse)}";
uiLabelMap.BACCQuantityDecreased = "${StringUtil.wrapString(uiLabelMap.BACCQuantityDecreased)}";
uiLabelMap.BACCUnitPrice = "${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}";
uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCTotal)}";
uiLabelMap.BACCDecrementReasonTypeId = "${StringUtil.wrapString(uiLabelMap.BACCDecrementReasonTypeId)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ValueMustBeLessThanQuantitInUse = "${StringUtil.wrapString(uiLabelMap.ValueMustBeLessThanQuantitInUse)}";
uiLabelMap.EquipmentIsNotSelected = "${StringUtil.wrapString(uiLabelMap.EquipmentIsNotSelected)}";
uiLabelMap.CreateEquipmentDecreaseConfirm = "${StringUtil.wrapString(uiLabelMap.CreateEquipmentDecreaseConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.BACCUnpostedConfirm = "${StringUtil.wrapString(uiLabelMap.BACCUnpostedConfirm)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.BACCPrepaidExpensesAccount = "${StringUtil.wrapString(uiLabelMap.BACCPrepaidExpensesAccount)}";
uiLabelMap.BACCEquipmentDecrementAccount = "${StringUtil.wrapString(uiLabelMap.BACCEquipmentDecrementAccount)}";
uiLabelMap.BACCRemainingValue = "${StringUtil.wrapString(uiLabelMap.BACCRemainingValue)}";
uiLabelMap.BACCAllocatedValue = "${StringUtil.wrapString(uiLabelMap.BACCAllocatedValue)}";
</script>