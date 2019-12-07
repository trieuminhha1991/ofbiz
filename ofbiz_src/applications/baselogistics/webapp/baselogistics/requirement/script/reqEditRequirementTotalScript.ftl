<@jqGridMinimumLib />
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript">	
	var locale = "${locale?if_exists}";
	var listProductSelected = [];
	var originFacilitySelected = null;
	var destFacilitySelected = null;
	var requirementTypeId = null;
	var reasonEnumId = null;
	
	var hidePrice = false;
	<#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
		hidePrice = false;
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	var company = '${company?if_exists}';
	
	<#assign requirementId = parameters.requirementId?if_exists/>
	
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : parameters.requirementId?if_exists}, false)!/>
	<#if requirement?exists>
		var requirement = {
			requirementId : "${requirementId?if_exists}",
			statusId : "${requirement.statusId?if_exists}",
			facilityId : "${requirement.facilityId?if_exists}",
			requirementTypeId : "${requirement.requirementTypeId?if_exists}",
			reasonEnumId : "${requirement.reasonEnumId?if_exists}",
			destFacilityId : "${requirement.destFacilityId?if_exists}",
			requirementStartDate : "${requirement.requirementStartDate?if_exists}",
			reason : "${requirement.reason?if_exists}", 
		}
		<#else>
		var requirement = null;
	</#if>
	var hideFacility = true;
	<#if requirement.facilityId?exists>
		hideFacility = false;
	</#if>
	<#if fromSales?if_exists == "Y">
	<#assign conditions = 
	Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(
			Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementTypeId", "RETURN_REQ")),
			Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementTypeId", "TRANSFER_REQUIREMENT"))
	), Static["org.ofbiz.entity.condition.EntityJoinOperator"].OR)/>
	
	<#assign requirementTypes = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions), null, null, null, false) />
		<#else>
	<#assign requirementTypes = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "PRODUCT_REQUIREMENT")), null, null, null, false) />
	</#if>
	
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
  	
  	<#assign requirementReasons = delegator.findList("RequirementEnumType", null, null, null, null, false) />
	
	<#assign enumTypeIds = []>
	
	<#list requirementReasons as reason>
		<#assign enumTypeIds = enumTypeIds + [reason.enumTypeId?if_exists]>
	</#list>
  	
  	<#assign reasonEnums = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, enumTypeIds), null, null, null, false)>
	var reasonEnumData = [
  	   	<#if reasonEnums?exists>
  	   		<#list reasonEnums as item>
  	   			{
  	   				enumId: "${item.enumId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},
  	   		</#list>
  	   	</#if>
  	];
  	
  	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE")), null, null, null, false)>
	var originFacilityData = [
	   	<#if facilities?exists>
	   		<#list facilities as item>
	   			{
	   				facilityId: "${item.facilityId?if_exists}",
	   				facilityName: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}",
	   				facilityCode : "${StringUtil.wrapString(item.get('facilityCode', locale)?if_exists)}"
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
		<#assign descWUom = StringUtil.wrapString(item.description?if_exists)/>
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
	uiLabelMap.AreYouSureEdit = "${StringUtil.wrapString(uiLabelMap.AreYouSureEdit)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.DetectProductNotHaveExpiredDate = "${StringUtil.wrapString(uiLabelMap.DetectProductNotHaveExpiredDate)}";
	uiLabelMap.ProductNotCongfigCombine = "${StringUtil.wrapString(uiLabelMap.ProductNotCongfigCombine)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
	uiLabelMap.Clear = "${StringUtil.wrapString(uiLabelMap.Clear)}";
	uiLabelMap.ProductNotCongfigAggregated = "${StringUtil.wrapString(uiLabelMap.ProductNotCongfigAggregated)}";
	uiLabelMap.BLCannotTransferSameFacility = "${StringUtil.wrapString(uiLabelMap.BLCannotTransferSameFacility)}";
	uiLabelMap.BLSGCMustNotInputNegativeValue =  "${StringUtil.wrapString(uiLabelMap.BLSGCMustNotInputNegativeValue)}";
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
	uiLabelMap.BPSearchProductToAdd = "${StringUtil.wrapString(uiLabelMap.BPSearchProductToAdd)}";
	uiLabelMap.BPProductNotFound = "${StringUtil.wrapString(uiLabelMap.BPProductNotFound)}";
	
	
</script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=1.1.1"></script>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/logresources/js/searchProduct.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/requirement/reqEditRequirementTotal.js?v=1.1.1"></script>