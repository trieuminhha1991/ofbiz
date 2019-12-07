<@jqGridMinimumLib/>
<script type="text/javascript">
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.BarcodeWidth = "${StringUtil.wrapString(uiLabelMap.BarcodeWidth)}";
	uiLabelMap.BarcodeHeight = "${StringUtil.wrapString(uiLabelMap.BarcodeHeight)}";
	uiLabelMap.ExportValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ExportValueMustBeGreaterThanZero)}";
	uiLabelMap.ValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanZero)}";
	uiLabelMap.AreYouSurePrint = "${StringUtil.wrapString(uiLabelMap.AreYouSurePrint)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.DefaultConfig = "${StringUtil.wrapString(uiLabelMap.DefaultConfig)}";
	
	var theme = 'olbius';
	
	var pageSizeData = [];
	var pageA3 = {
		id: 'A3',
		value: 'A3',
	}
	var pageA4 = {
		id: 'A4',
		value: 'A4',
	}
	var pageA5 = {
		id: 'A5',
		value: 'A5',
	}
	var pageCustom = {
		id: 'freeStyle',
		value: 'freeStyle',
	}
	pageSizeData.push(pageA3);
	pageSizeData.push(pageA4);
	pageSizeData.push(pageA5);
	pageSizeData.push(pageCustom);
	
</script>
<script type="text/javascript" src="/logresources/js/util/UtilValidate.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/product/printBarCodeProducts.js"></script>
