<script type="text/javascript">
	var createPurchasePlanGlobalObject = {};
	createPurchasePlanGlobalObject.urlNavigation = "<@ofbizUrl>listPO</@ofbizUrl>";

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.SettingAddress = "${StringUtil.wrapString(uiLabelMap.SettingAddress)}";
	uiLabelMap.SettingCompanyName = "${StringUtil.wrapString(uiLabelMap.SettingCompanyName)}";
	uiLabelMap.SettingCity = "${StringUtil.wrapString(uiLabelMap.SettingCity)}";
	uiLabelMap.SettingPhoneNumber = "${StringUtil.wrapString(uiLabelMap.SettingPhoneNumber)}";
	uiLabelMap.SettingEmail = "${StringUtil.wrapString(uiLabelMap.SettingEmail)}";
	uiLabelMap.SettingLastName = "${StringUtil.wrapString(uiLabelMap.SettingLastName)}";
	uiLabelMap.SettingFirstName = "${StringUtil.wrapString(uiLabelMap.SettingFirstName)}";
	uiLabelMap.SettingMobilePhone = "${StringUtil.wrapString(uiLabelMap.SettingMobilePhone)}";
	uiLabelMap.SettingBusinessPhone = "${StringUtil.wrapString(uiLabelMap.SettingBusinessPhone)}";
	uiLabelMap.SettingHomePhone = "${StringUtil.wrapString(uiLabelMap.SettingHomePhone)}";
	uiLabelMap.SettingChooseSupplier = "${StringUtil.wrapString(uiLabelMap.SettingChooseSupplier)}";
	uiLabelMap.SettingMobilePhone = "${StringUtil.wrapString(uiLabelMap.SettingMobilePhone)}";
	uiLabelMap.SettingMobilePhone = "${StringUtil.wrapString(uiLabelMap.SettingMobilePhone)}";
	uiLabelMap.SettingSelectFromFacilityToTransfer = "${StringUtil.wrapString(uiLabelMap.SettingSelectFromFacilityToTransfer)}";
	uiLabelMap.SettingSelectCategory = "${StringUtil.wrapString(uiLabelMap.SettingSelectCategory)}";
	uiLabelMap.SettingYouAreTypingNotCorrect = "${StringUtil.wrapString(uiLabelMap.SettingYouAreTypingNotCorrect)}";
	uiLabelMap.SettingDoNotHaveAnyItem = "${StringUtil.wrapString(uiLabelMap.SettingDoNotHaveAnyItem)}";
	uiLabelMap.SettingAreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.SettingAreYouSureCreate)}";
	uiLabelMap.SettingFacilityId = "${StringUtil.wrapString(uiLabelMap.SettingFacilityId)}";
	uiLabelMap.SettingFacilityName = "${StringUtil.wrapString(uiLabelMap.SettingFacilityName)}";
	uiLabelMap.SettingSummaryQOH = "${StringUtil.wrapString(uiLabelMap.SettingSummaryQOH)}";
	uiLabelMap.SettingTransferQty = "${StringUtil.wrapString(uiLabelMap.SettingTransferQty)}";
	uiLabelMap.SettingQOO = "${StringUtil.wrapString(uiLabelMap.SettingQOO)}";
	uiLabelMap.SettingQPDL = "${StringUtil.wrapString(uiLabelMap.SettingQPDL)}";
	uiLabelMap.SettingQPDS = "${StringUtil.wrapString(uiLabelMap.SettingQPDS)}";
	uiLabelMap.SettingLIDL = "${StringUtil.wrapString(uiLabelMap.SettingLIDL)}";
	uiLabelMap.SettingLIDS = "${StringUtil.wrapString(uiLabelMap.SettingLIDS)}";
	uiLabelMap.SettingQuantityIsMustLessZero = "${StringUtil.wrapString(uiLabelMap.SettingQuantityIsMustLessZero)}";
	uiLabelMap.SettingQuantityIsMustGreaterZero = "${StringUtil.wrapString(uiLabelMap.SettingQuantityIsMustGreaterZero)}";
	uiLabelMap.SettingPickStandard = "${StringUtil.wrapString(uiLabelMap.SettingPickStandard)}";
	uiLabelMap.SettingQtyBox = "${StringUtil.wrapString(uiLabelMap.SettingQtyBox)}";
	uiLabelMap.SettingTotalPOQuantity = "${StringUtil.wrapString(uiLabelMap.SettingTotalPOQuantity)}";
	uiLabelMap.SettingUnitCostPurchase = "${StringUtil.wrapString(uiLabelMap.SettingUnitCostPurchase)}";
	uiLabelMap.SettingTotalCost = "${StringUtil.wrapString(uiLabelMap.SettingTotalCost)}";
	uiLabelMap.SettingQty = "${StringUtil.wrapString(uiLabelMap.SettingQty)}";
	uiLabelMap.SettingItemMissingFieldsQuantity = "${StringUtil.wrapString(uiLabelMap.SettingItemMissingFieldsQuantity)}";
	uiLabelMap.SettingItemMissingFieldsCost = "${StringUtil.wrapString(uiLabelMap.SettingItemMissingFieldsCost)}";
	uiLabelMap.SettingProductID = "${StringUtil.wrapString(uiLabelMap.SettingProductID)}";
	uiLabelMap.SettingProductName = "${StringUtil.wrapString(uiLabelMap.SettingProductName)}";
	uiLabelMap.SettingLastsold = "${StringUtil.wrapString(uiLabelMap.SettingLastsold)}";
	uiLabelMap.SettingLastReceived = "${StringUtil.wrapString(uiLabelMap.SettingLastReceived)}";
	uiLabelMap.SettingQtyPic = "${StringUtil.wrapString(uiLabelMap.SettingQtyPic)}";
	uiLabelMap.SettingQuantityCheck = "${StringUtil.wrapString(uiLabelMap.SettingQuantityCheck)}";
	uiLabelMap.SettingNotes = "${StringUtil.wrapString(uiLabelMap.SettingNotes)}";
	uiLabelMap.SettingSystemInfo = "${StringUtil.wrapString(uiLabelMap.SettingSystemInfo)}";
	uiLabelMap.SettingNotes = "${StringUtil.wrapString(uiLabelMap.SettingNotes)}";
	uiLabelMap.SettingSystemInfo = "${StringUtil.wrapString(uiLabelMap.SettingSystemInfo)}";
	uiLabelMap.SettingPurchaseOrder = "${StringUtil.wrapString(uiLabelMap.SettingPurchaseOrder)}";
	uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.NotAllowEmpty = "${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}";
	uiLabelMap.SettingStatus = "${StringUtil.wrapString(uiLabelMap.SettingStatus)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.CanNotAfterShipBeforeDate2 = "${StringUtil.wrapString(uiLabelMap.CanNotAfterShipBeforeDate2)}";
	uiLabelMap.CanNotBeforeShipAfterDate2 = "${StringUtil.wrapString(uiLabelMap.CanNotBeforeShipAfterDate2)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.QuantityTotalAndPartNotTrue = "${StringUtil.wrapString(uiLabelMap.QuantityTotalAndPartNotTrue)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.QuantityNotEntered = "${StringUtil.wrapString(uiLabelMap.QuantityNotEntered)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCTotal)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.Yes = "${StringUtil.wrapString(uiLabelMap.Yes)}";
	uiLabelMap.No = "${StringUtil.wrapString(uiLabelMap.No)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.BPOAddress1 = "${StringUtil.wrapString(uiLabelMap.BPOAddress1)}";
	uiLabelMap.POEmailAddr = "${StringUtil.wrapString(uiLabelMap.POEmailAddr)}";
	uiLabelMap.POTelecomNumber = "${StringUtil.wrapString(uiLabelMap.POTelecomNumber)}";
	uiLabelMap.POSupplier = "${StringUtil.wrapString(uiLabelMap.POSupplier)}";
	uiLabelMap.ReceiveToFacility = "${StringUtil.wrapString(uiLabelMap.ReceiveToFacility)}";
	uiLabelMap.wgadderror = "${StringUtil.wrapString(uiLabelMap.wgadderror)}";
	uiLabelMap.wgaddsuccess = "${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}";
	uiLabelMap.DmsSequenceId = "${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}";
	
	
	var facilityData = [<#list listFacility as facility>{
		facilityId: "${facility.facilityId?if_exists}",
		description: "${StringUtil.wrapString(facility.facilityName?if_exists)}",
	},</#list>];
</script>

<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>

<script type="text/javascript" src="/posresources/js/common/Common.js"></script>
<script type="text/javascript" src="/posresources/js/common/jqx.dropdown.js"></script>
<script type="text/javascript" src="/posresources/js/common/jqx.window.js"></script>
<script type="text/javascript" src="/poresources/js/pos/createPurchasePlan.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>