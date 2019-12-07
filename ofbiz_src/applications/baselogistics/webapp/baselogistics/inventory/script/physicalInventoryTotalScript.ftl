<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	var physicalInventoryId = "${parameters.physicalInventoryId?if_exists}";
	<#assign types = ["PRODUCT_PACKING", "WEIGHT_MEASURE"]>
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, types), null, null, null, false) />
	var uomData = 
	[
		<#list uomList as uom>
		{
			uomId: "${uom.uomId}",
			<#if uom.uomTypeId == "WEIGHT_MEASURE">
				description: "${StringUtil.wrapString(uom.get('abbreviation', locale)?if_exists)}"
			<#else>
				description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
			</#if>
		},
		</#list>
	];
	function getUomDescription(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
	}
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_NON_SER_STTS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	
	<#assign varianceReasons = delegator.findList("VarianceReason", null, null, null, null, false)/>
	var reasonData = [];
	<#list varianceReasons as item>
		var row = {};
		<#assign descReason = StringUtil.wrapString(item.get('description', locale))>
		row['varianceReasonId'] = "${item.varianceReasonId}";
		row['description'] = "${descReason?if_exists}";
		reasonData.push(row);
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.ChooseReasonAndQuantityBeforeSelect = "${StringUtil.wrapString(uiLabelMap.ChooseReasonAndQuantityBeforeSelect)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.ProductManufactureDate = "${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}";
	uiLabelMap.ProductExpireDate = "${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}";
	uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.Batch = "${StringUtil.wrapString(uiLabelMap.Batch)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	uiLabelMap.Reason = "${StringUtil.wrapString(uiLabelMap.Reason)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.InventoryGood = "${StringUtil.wrapString(uiLabelMap.InventoryGood)}";
	uiLabelMap.QuantityOnHandTotal = "${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}";
	uiLabelMap.ListProduct = "${StringUtil.wrapString(uiLabelMap.ListProduct)}";
	
</script>
<script type="text/javascript" src="/logresources/js/inventory/physicalInventoryTotal.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>