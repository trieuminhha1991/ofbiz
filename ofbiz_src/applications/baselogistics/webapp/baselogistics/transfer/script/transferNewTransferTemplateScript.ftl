<@jqGridMinimumLib />
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript">	
	if (glEditorId == undefined){
		var glEditorId = {};
	}
	var listProductSelected = [];
	var originFacility = null;
	var destFacility = null;
	var transferTypeId =null;
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>;
	var company = '${company?if_exists}';
	
	<#assign transferTypes = delegator.findList("TransferType", null, null, null, null, false) />
	var transferTypeData = new Array();
	<#list transferTypes as item>
		<#assign listChilds = delegator.findList("TransferType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", item.transferTypeId?if_exists), null, null, null, false) />
		<#if !(listChilds[0]?has_content && !item.parentTypeId?has_content)>
			var row = {};
			row['transferTypeId'] = "${item.transferTypeId?if_exists}";
			row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
			transferTypeData.push(row);
		</#if>
	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		quantityUomData.push(row);
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign descWUom = StringUtil.wrapString(item.abbreviation?if_exists)/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descWUom?if_exists}";
		weightUomData.push(row);
	</#list>

	var getUomDesc = function (uomId){
		for (var i in quantityUomData) {
			if (quantityUomData[i].uomId == uomId) {
				return quantityUomData[i].description;
			}
		}
		for (var i in weightUomData) {
			if (weightUomData[i].uomId == uomId) {
				return weightUomData[i].description;
			}
		}
	}
	
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	var quantityUomData = new Array();
	<#list quantityUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		quantityUomData.push(row);
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}";
		weightUomData.push(row);
	</#list>
	
	function getUomDescription(uomId) {
		for (var x in weightUomData) {
			if (weightUomData[x].uomId == uomId) return weightUomData[x].description;
		}
		for (var x in quantityUomData) {
			if (quantityUomData[x].uomId == uomId) return quantityUomData[x].description;
		}
	}	
	var acc = false;
	<#if security.hasPermission("ACCOUNTING_VIEW", userLogin)>
		acc = true;
	</#if>
	var cellclassname = function (row, column, value, data) {
		var data = $('#jqxgridProduct').jqxGrid('getrowdata',row);
    	if (column == 'quantity') {
			return 'background-prepare';
    	} 
	}
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	uiLabelMap.BLHasProductNotEnoughInv = "${StringUtil.wrapString(uiLabelMap.BLHasProductNotEnoughInv)}";
	uiLabelMap.BLCannotTransferSameFacility = "${StringUtil.wrapString(uiLabelMap.BLCannotTransferSameFacility)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.BSDiscountinuePurchase = "${StringUtil.wrapString(uiLabelMap.BSDiscountinuePurchase)}";
	uiLabelMap.BSDiscountinueSales = "${StringUtil.wrapString(uiLabelMap.BSDiscountinueSales)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";	
	uiLabelMap.BSAverageCost = "${StringUtil.wrapString(uiLabelMap.BSAverageCost)}";	
	uiLabelMap.BPOTotal = "${StringUtil.wrapString(uiLabelMap.BPOTotal)}";	
	uiLabelMap.Note = "${StringUtil.wrapString(uiLabelMap.Note)}";	
	uiLabelMap.OrderItemsSubTotal = "${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)}";	
	uiLabelMap.BLTimeDistanceNotValid = "${StringUtil.wrapString(uiLabelMap.BLTimeDistanceNotValid)}";	
	
</script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/transfer/transferNewTransferTemplate.js?v=1.1.1"></script>