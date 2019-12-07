<@jqGridMinimumLib/>
<script type="text/javascript">

	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false) />
	var quantityUomData = [
	   	<#if quantityUoms?exists>
	   		<#list quantityUoms as item>
	   			{
	   				uomId: "${item.uomId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "WEIGHT_MEASURE")), null, null, null, false) />
	var weightUomData = [
	   	<#if weightUoms?exists>
	   		<#list weightUoms as item>
	   			{
	   				uomId: "${item.uomId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	function getUomDescription(uomId) {
	 	for (x in quantityUomData) {
	 		if (quantityUomData[x].uomId == uomId) {
	 			return quantityUomData[x].description;
	 		}
	 	}
	 	for (x in weightUomData) {
	 		if (weightUomData[x].uomId == uomId) {
	 			return weightUomData[x].description;
	 		}
	 	}
 	}
 	var mapDescriptionEdit = {};
 	var mapPriceEdit = {};
	var listProductSelected = [];
	var mapProductSelected = {};
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BPOAreYouSureYouWantCreate = "${uiLabelMap.BPOAreYouSureYouWantCreate}";
	uiLabelMap.BPOPleaseSelectSupplier = "${uiLabelMap.BPOPleaseSelectSupplier}";
	uiLabelMap.BPOPleaseSelectProduct = "${uiLabelMap.BPOPleaseSelectProduct}";
	uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}";
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	uiLabelMap.wgok = "${uiLabelMap.wgok}";
	uiLabelMap.wgcancel = "${uiLabelMap.wgcancel}";
	uiLabelMap.wgupdatesuccess = "${uiLabelMap.wgupdatesuccess}";
	uiLabelMap.BSContactMechId = '${StringUtil.wrapString(uiLabelMap.BSContactMechId)}';
	uiLabelMap.BSReceiverName = '${StringUtil.wrapString(uiLabelMap.BSReceiverName)}';
	uiLabelMap.BSOtherInfo = '${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}';
	uiLabelMap.BSAddress = '${StringUtil.wrapString(uiLabelMap.BSAddress)}';
	uiLabelMap.BSCity = '${StringUtil.wrapString(uiLabelMap.BSCity)}';
	uiLabelMap.BSStateProvince = '${StringUtil.wrapString(uiLabelMap.BSStateProvince)}';
	uiLabelMap.BSCountry = '${StringUtil.wrapString(uiLabelMap.BSCountry)}';
	uiLabelMap.BSCounty = '${StringUtil.wrapString(uiLabelMap.BSCounty)}';
	uiLabelMap.BSWard = '${StringUtil.wrapString(uiLabelMap.BSWard)}';
	uiLabelMap.YouNotYetChooseProduct = '${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}';
	uiLabelMap.AreYouSureCreate = '${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}';
	uiLabelMap.CreateSuccess = '${StringUtil.wrapString(uiLabelMap.CreateSuccess)}';
	
</script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js?v=1.0.5"></script>
<@jqOlbCoreLib />
<script type="text/javascript" src="/poresources/js/returnSupplier/returnSupplierWithoutOrderNewInfoTotal.js?v=0.0.1"></script>