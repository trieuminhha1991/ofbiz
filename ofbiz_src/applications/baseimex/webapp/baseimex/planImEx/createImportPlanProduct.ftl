<style>
.cell-green-color {
    color: black !important;
    background-color: #FFCCFF !important;
}
.cell-gray-color {
	color: black !important;
	background-color: #87CEEB !important;
}

.green1 {
    color: #black;
    background-color: #DEEDF5;
}
.green1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #DEEDF5;
}

.yellow1 {
    color: black;
    background-color: #FBFF05;
}
.yellow1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #FBFF05;
}
.bluewhite {
    color: black;
    background-color: #08F5CA;
}
.bluewhite:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #08F5CA;
}
</style>
<script>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		uomData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	function getUomDesc(uomId) {
		for (var i = 0; i < weightUomData.length; i ++) {
			if (weightUomData[i].uomId == uomId) {
				return weightUomData[i].description;
			}
		}
		for (var i = 0; i < uomData.length; i ++) {
			if (uomData[i].uomId == uomId) {
				return uomData[i].description;
			}
		}
		return uomId;
	}


	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BIENumContainer = "${StringUtil.wrapString(uiLabelMap.BIENumContainer)}";
	uiLabelMap.BIERemain = "${StringUtil.wrapString(uiLabelMap.BIERemain)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.MOQ = "${StringUtil.wrapString(uiLabelMap.MOQ)}";
	uiLabelMap.BIESalesCycle = "${StringUtil.wrapString(uiLabelMap.BIESalesCycle)}";
	uiLabelMap.BIEPalletTotal = "${StringUtil.wrapString(uiLabelMap.BIEPalletTotal)}";
	uiLabelMap.BIEOrderQuantity = "${StringUtil.wrapString(uiLabelMap.BIEOrderQuantity)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.QC = "${StringUtil.wrapString(uiLabelMap.QC)}";
</script>
	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<div id="jqxStockAndPlan"></div>
<script src="/imexresources/js/import/plan/createImportPlanProduct.js?v=1.0.1"></script>