<@jqGridMinimumLib />
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript">	
	var locale = "${locale?if_exists}";
	var listProductSelected = [];
	var originFacilitySelected = null;
	var destFacilitySelected = null;
	var requirementTypeId = null;
	var reasonEnumId = null;
	
	var hidePrice = true;
	<#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
		hidePrice = false;
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	var company = '${company?if_exists}';
	
	<#assign requirementTypes = delegator.findList("RequirementType", null, null, null, null, false) />
	var requirementTypeData = [
	   	<#if requirementTypes?exists>
	   		<#list requirementTypes as item>
	   			{
	   				requirementTypeId: "${item.requirementTypeId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	
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
	
	var getReqTypeDesc = function (requirementTypeId){
		for (var i in requirementTypeData) {
			if (requirementTypeData[i].requirementTypeId == requirementTypeId) {
				return requirementTypeData[i].description;
			}
		}
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.DetectProductNotHaveExpiredDate = "${StringUtil.wrapString(uiLabelMap.DetectProductNotHaveExpiredDate)}";
	uiLabelMap.ProductNotCongfigCombine = "${StringUtil.wrapString(uiLabelMap.ProductNotCongfigCombine)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
	uiLabelMap.Clear = "${StringUtil.wrapString(uiLabelMap.Clear)}";
	uiLabelMap.ProductNotCongfigAggregated = "${StringUtil.wrapString(uiLabelMap.ProductNotCongfigAggregated)}";
	uiLabelMap.BLCannotTransferSameFacility = "${StringUtil.wrapString(uiLabelMap.BLCannotTransferSameFacility)}";
	
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.BSDiscountinuePurchase = "${StringUtil.wrapString(uiLabelMap.BSDiscountinuePurchase)}";
	uiLabelMap.BSDiscountinueSales = "${StringUtil.wrapString(uiLabelMap.BSDiscountinueSales)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";	
	uiLabelMap.BPOTotal = "${StringUtil.wrapString(uiLabelMap.BPOTotal)}";	
	uiLabelMap.Note = "${StringUtil.wrapString(uiLabelMap.Note)}";	
	uiLabelMap.OrderItemsSubTotal = "${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)}";	
	
	
</script>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/requirement/reqNewRequirementTotal.js?v=1.1.1"></script>