<@jqGridMinimumLib/>
<script type="text/javascript">
	
	<#assign returnId = parameters.returnId?if_exists>
	<#assign returnHeader = delegator.findOne("ReturnHeader", {"returnId" : returnId}, false)!>
	var returnId = "${returnId?if_exists}";
	<#if returnHeader.statusId != "SUP_RETURN_ACCEPTED" && returnHeader.statusId != "SUP_RETURN_REQUESTED">
		window.location.href = "viewGeneralReturnSupplier?returnId=" + returnId;
	</#if>
	
	<#assign cond1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition('returnId', returnId)>
	<#assign cond2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition('statusId',  Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "SUP_RETURN_CANCELLED")>
	<#assign returnItems = delegator.findList("ReturnItemAndProduct", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(cond1, cond2), null, null, null, false) />
	
	<#assign toParty = delegator.findOne("PartyGroup", {"partyId" : returnHeader.toPartyId?if_exists}, false)!>
	<#assign currencyUomId = returnHeader.currencyUomId?if_exists>
	<#assign facility = delegator.findOne("Facility", {"facilityId" : returnHeader.destinationFacilityId?if_exists}, false)!>
	
	<#if facility?has_content>
		var facilitySelected = {
			facilityCode: "${facility.facilityCode?if_exists}",
			facilityName: "${facility.facilityName?if_exists}",
			facilityId: "${facility.facilityId?if_exists}",
		}
	</#if>
	var partyId = "${toParty.partyId?if_exists}";
	var toPartyName = "${StringUtil.wrapString(toParty.groupName?if_exists)}";
	var currencyUomId = "${currencyUomId?if_exists}";
	var entryDate = new Date("${returnHeader.entryDate?if_exists}");
	var description = "${StringUtil.wrapString(returnHeader.description?if_exists?string?trim!'\n')}";
	var facilityName = "${StringUtil.wrapString(facility.facilityName?if_exists)}";
	var facilityId = "${facility.facilityId?if_exists}";
	var ownerPartyId = "${facility.ownerPartyId?if_exists}";
	var listProductSelected = [];
	var returnItemInitData = [];
	<#list returnItems as item>
		var map = {};
		map.returnId = "${item.returnId?if_exists}";
		map.returnItemSeqId = "${item.returnItemSeqId?if_exists}";
		map.returnReasonId = "${item.returnReasonId?if_exists}";
		map.productId = "${item.productId?if_exists}";
		map.productCode = "${item.productCode?if_exists}";
		map.productName = "${item.productName?if_exists}";
		map.requireAmount = "${item.requireAmount?if_exists}";
		map.baseQuantityUomId = "${item.baseQuantityUomId?if_exists}";
		map.baseWeightUomId = "${item.baseWeightUomId?if_exists}";
		<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId?has_content && item.amountUomTypeId == 'WEIGHT_MEASURE'>
			var qty = "${item.returnAmount?if_exists}";
			if ("${locale}" == "vi") {
				qty = qty.replace(",", ".");
			} 
			var x = parseFloat(qty, 2, null);
			map.returnQuantity = x;
			map.quantity = x;
		<#else>
			map.returnQuantity = parseInt("${item.returnQuantity?if_exists}");
			map.quantity = parseInt("${item.returnQuantity?if_exists}");
		</#if> 
		map.quantityUomId = "${item.quantityUomId?if_exists}";
		map.returnPrice = parseFloat("${item.returnPrice?if_exists}".replace(",", ".")).toFixed(2);
		map.returnPriceTmp = parseFloat("${item.returnPrice?if_exists}".replace(",", ".")).toFixed(2);

		returnItemInitData.push(map);
		listProductSelected.push(map);
	</#list>
	
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
	uiLabelMap.BPSearchProductToAdd = '${StringUtil.wrapString(uiLabelMap.BPSearchProductToAdd)}';
	uiLabelMap.ValueMustBeGreaterThanZero = '${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanZero)}';
	uiLabelMap.BPProductNotFound = '${StringUtil.wrapString(uiLabelMap.BPProductNotFound)}';
	uiLabelMap.AreYouSureSave = '${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}';
	uiLabelMap.OrderItemsSubTotal = "${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)}";	
	uiLabelMap.AreYouSureUpdate = '${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}';
	
</script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=1.0.5"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasCore=false hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/poresources/js/returnSupplier/supReturnEditSupReturnTemplate.js?v=0.0.9"></script>
<script type="text/javascript" src="/poresources/js/searchProductToAdd.js?v=0.0.5"></script>