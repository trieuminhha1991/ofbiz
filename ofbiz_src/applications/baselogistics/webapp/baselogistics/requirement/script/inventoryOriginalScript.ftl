<@jqGridMinimumLib />
<script type="text/javascript">	
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	<#if fromSales?if_exists == "Y">
	var fromSales = true;
	<#else>
	var fromSales = false;
	</#if>
	
	var listInventoryItemFrom = [];
	
	var inventoryItemLabelFromId = null;
	var inventoryItemLabelToId = null;
	var requirementId = "${parameters.requirementId?if_exists}";
	
	<#assign reqItems = delegator.findList("RequirementItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementId", parameters.requirementId?if_exists)), null, null, null, false) />
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['quantityUomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		quantityUomData[${item_index}] = row;
	</#list>
	
	var requirementItemData = [];
	<#list reqItems as item>
		var row = {};
		row['requirementId'] = "${item.requirementId?if_exists}";
		row['reqItemSeqId'] = "${item.reqItemSeqId?if_exists}";
		row['productId'] = "${item.productId?if_exists}";
		row['quantity'] = "${item.quantity?if_exists}";
		requirementItemData.push(row);
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.DAYouNotYetChooseProduct = "${uiLabelMap.DAYouNotYetChooseProduct}";
	uiLabelMap.DetectProductNotHaveExpiredDate = "${uiLabelMap.DetectProductNotHaveExpiredDate}";
	uiLabelMap.YouNotYetChooseProduct = "${uiLabelMap.DAYouNotYetChooseProduct}";
	uiLabelMap.OK = "${uiLabelMap.OK}";
</script>
