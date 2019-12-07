<script type="text/javascript">
	var listOrderIds = [];
	
	<#assign currencys = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)>
	var currencyUomData = [];
	<#list currencys as item>
		var currency = {};
		<#assign desUom = StringUtil.wrapString(item.abbreviation?if_exists)/>
		currency['uomId'] = "${item.uomId?if_exists}";
		currency['description'] = "${desUom?if_exists}";
		currencyUomData.push(currency);
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.BSValueIsNotEmptyK = "${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmptyK)}";
	uiLabelMap.BPOAreYouSureYouWantCreate = "${StringUtil.wrapString(uiLabelMap.BPOAreYouSureYouWantCreate)}";
	uiLabelMap.BPOPleaseSelectSupplier = "${StringUtil.wrapString(uiLabelMap.BPOPleaseSelectSupplier)}";
	uiLabelMap.BPOPleaseSelectProduct = "${StringUtil.wrapString(uiLabelMap.BPOPleaseSelectProduct)}";
	uiLabelMap.BSYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseProduct)}";
	uiLabelMap.BSAreYouSureYouWantToCreate = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCreate)}";
	uiLabelMap.BPOPleaseSelectProductQuantity = "${StringUtil.wrapString(uiLabelMap.BPOPleaseSelectProductQuantity)}";
	uiLabelMap.BPORestrictMOQ = "${StringUtil.wrapString(uiLabelMap.BPORestrictMOQ)}";
	uiLabelMap.BPOSequenceId = "${StringUtil.wrapString(uiLabelMap.BPOSequenceId)}";
	uiLabelMap.BPOContactMechId = "${StringUtil.wrapString(uiLabelMap.BPOContactMechId)}";
	uiLabelMap.BPOReceiveName = "${StringUtil.wrapString(uiLabelMap.BPOReceiveName)}";
	uiLabelMap.BPOOtherInfo = "${StringUtil.wrapString(uiLabelMap.BPOOtherInfo)}";
	uiLabelMap.BPOAddress1 = "${StringUtil.wrapString(uiLabelMap.BPOAddress1)}";
	uiLabelMap.BPOCity = "${StringUtil.wrapString(uiLabelMap.BPOCity)}";
	uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
	uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.BSContactMechId = '${StringUtil.wrapString(uiLabelMap.BSContactMechId)}';
	uiLabelMap.BSReceiverName = '${StringUtil.wrapString(uiLabelMap.BSReceiverName)}';
	uiLabelMap.BSOtherInfo = '${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}';
	uiLabelMap.BSAddress = '${StringUtil.wrapString(uiLabelMap.BSAddress)}';
	uiLabelMap.BSCity = '${StringUtil.wrapString(uiLabelMap.BSCity)}';
	uiLabelMap.BSStateProvince = '${StringUtil.wrapString(uiLabelMap.BSStateProvince)}';
	uiLabelMap.BSCountry = '${StringUtil.wrapString(uiLabelMap.BSCountry)}';
	uiLabelMap.BSCounty = '${StringUtil.wrapString(uiLabelMap.BSCounty)}';
	uiLabelMap.BSWard = '${StringUtil.wrapString(uiLabelMap.BSWard)}';
	uiLabelMap.SequenceId = '${StringUtil.wrapString(uiLabelMap.SequenceId)}';
	uiLabelMap.OrderId = '${StringUtil.wrapString(uiLabelMap.OrderId)}';
	uiLabelMap.OrderDate = '${StringUtil.wrapString(uiLabelMap.OrderDate)}';	
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.YouAreNotChooseOrderYet = "${StringUtil.wrapString(uiLabelMap.YouAreNotChooseOrderYet)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.BLFacilityName = "${StringUtil.wrapString(uiLabelMap.BLFacilityName)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
</script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/poresources/js/returnSupplier/supReturnEditSupReturnInfo.js?v=1.1.1"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.validator.js"></script>