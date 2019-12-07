<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	var allowSelected = false;
	listInvChanged = [];
	var facilityId = '${parameters.facilityId?if_exists}';
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	<#assign uomTypes = ["WEIGHT_MEASURE", "PRODUCT_PACKING"]>
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, uomTypes), null, null, null, false) />
	var uomData = 
	[
		<#list uomList as uom>
		{
			uomId: "${uom.uomId}",
			<#if uom.uomTypeId == 'WEIGHT_MEASURE'>
				description: "${StringUtil.wrapString(uom.get('abbreviation', locale)?if_exists)}"
			<#else>
				description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
			</#if>
		},
		</#list>
	];
	
	<#assign weightUomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "WEIGHT_MEASURE"), null, null, null, false) />
	var weighUomData = 
	[
		<#list weightUomList as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('abbreviation', locale)?if_exists)}"
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
		row['negativeNumber'] = "${item.negativeNumber?if_exists}";
		reasonData.push(row);
	</#list>
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
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
	uiLabelMap.DmsFieldRequired = "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.CannotGreaterThanActualQuantityOnHand = "${StringUtil.wrapString(uiLabelMap.CannotGreaterThanActualQuantityOnHand)}";
	uiLabelMap.ThatReasonHasBeenConfigToIncreaseInventory = "${StringUtil.wrapString(uiLabelMap.ThatReasonHasBeenConfigToIncreaseInventory)}";
	uiLabelMap.QuantityOnHandTotal = "${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}";
</script>
<script type="text/javascript" src="/logresources/js/inventory/listInventoryPhysical.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>