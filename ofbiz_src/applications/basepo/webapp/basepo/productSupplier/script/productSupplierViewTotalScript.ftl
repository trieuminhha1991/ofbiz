<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script>
	var POPleaseSelect = '${StringUtil.wrapString(uiLabelMap.POPleaseSelect)}';
	var filterchoosestring = '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}';
	var POAreYouSureAddItem = '${uiLabelMap.POAreYouSureAddItem}';
	var wgcancel = '${StringUtil.wrapString(uiLabelMap.wgcancel)}';
	var wgok = '${StringUtil.wrapString(uiLabelMap.wgok)}';
	var POCheckIsEmptyCreateLocationFacility = '${StringUtil.wrapString(uiLabelMap.POCheckIsEmptyCreateLocationFacility)}';
	var POCheckGreaterThan = '${StringUtil.wrapString(uiLabelMap.POCheckGreaterThan)}';
	var POCheckIsEmptyCreateLocationFacility = '${StringUtil.wrapString(uiLabelMap.POCheckIsEmptyCreateLocationFacility)}';
	var filterchoosestring = '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}';
	var POProductId = '${uiLabelMap.POProductId}';
	var POProductName = '${uiLabelMap.BPOProductName}';
	var POSuccessful = '${StringUtil.wrapString(uiLabelMap.POSuccessful)}';
	var POCheckSupplierProductExits = '${StringUtil.wrapString(uiLabelMap.POCheckSupplierProductExits)}';
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.POSupplierId = "${uiLabelMap.POSupplierId}";
	uiLabelMap.POSupplierName = "${uiLabelMap.POSupplierName}";
	uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate = "${uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate}";
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	
	var canDropShipData = [{id:'Y', description:"${uiLabelMap.CommonYes}"},  {id:'N', description:"${uiLabelMap.CommonNo}"}];
	function fixSelectAll(dataList) {
		var sourceST = {
		        localdata: dataList,
		        datatype: "array"
		    };
			var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
	    var uniqueRecords2 = filterBoxAdapter2.records;
			uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
	return uniqueRecords2;
	}
	
	<#if locale = "vi">
		var decimalSeparator = ",";
	<#else>
		var decimalSeparator = ".";
	</#if>
</script>