<@jqGridMinimumLib/>
<script type="text/javascript">
	if (glEditorId == undefined){
		var glEditorId = {};
	}
	var localeStr = "VI"; 
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	<#if !displayCost?has_content>
		<#assign displayCost = "Y" />
	<#else>
		<#assign displayCost = displayCost?if_exists />
	</#if>
	<#if !displayQOH?has_content>
		<#assign displayQOH = "N" />
	<#else>
		<#assign displayQOH = displayQOH?if_exists />
	</#if>
	
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
	
	if (listProductSeleted == undefined) var listProductSeleted = {};
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.ChooseExpireDate = "${uiLabelMap.ChooseExpireDate}";
	
	var currentQohTemp = 0;
</script>
<script type="text/javascript" src="/logresources/js/util/UtilValidate.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/product/listProduct.js"></script>
