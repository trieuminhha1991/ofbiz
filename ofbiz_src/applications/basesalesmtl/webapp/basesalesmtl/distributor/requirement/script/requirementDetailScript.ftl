<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<style type="text/css">
.bootbox{
    z-index: 990009 !important;
}
.modal-backdrop{
    z-index: 890009 !important;
}
.loading-container{
	z-index: 999999 !important;
}
</style>
<script type="text/javascript">
	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	<#assign requirementItemAssoc = delegator.findList("RequirementItemAssoc", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("toRequirementId", requirement.requirementId), null, null, null, false)>
	<#assign requirementItemAssocChild = delegator.findList("RequirementItemAssoc", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementId", requirement.requirementId), null, null, null, false)>
	<#assign returnRequirementCommitmentChild = delegator.findList("ReturnRequirementCommitment", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementId", requirement.requirementId), null, null, null, false)>
	
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	selectFacilityClick = 0;
	var listRequirementItemSelected = [];
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign departments = Static["com.olbius.basehr.util.PartyUtil"].getDepartmentOfEmployee(delegator, userLogin.get("partyId"), nowTimestamp)!/>;
	var deptTmp = null;
	<#if departments?has_content>
		deptTmp = "${departments.get(0)}";
	</#if>
	<#assign requirementId = parameters.requirementId?if_exists/>
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : requirementId}, false)!>
	var requirementId = '${requirementId?if_exists}';
	var requirementTypeId = '${requirement.requirementTypeId?if_exists}';
	var requiredFacilityId = '${requirement.facilityId?if_exists}';
	var curStatusId = '${requirement.statusId?if_exists}';
	var purposeContactMechId = null;
	if (requirementTypeId == "EXPORT_REQUIREMENT"){
		purposeContactMechId = "SHIP_ORIG_LOCATION";
	} else {
		purposeContactMechId = "SHIPPING_LOCATION";
	}
	<#assign userLoginTmp = delegator.findOne("UserLogin", {"userLoginId" : requirement.createdByUserLogin?if_exists}, false)/>
	
	<#assign createdBy = delegator.findOne("PartyNameView", {"partyId" : userLoginTmp.partyId}, false)/>
	
	var requiredOfDepartment = null;
	<#assign departmentRequired = Static["com.olbius.basehr.util.PartyUtil"].getDepartmentOfEmployee(delegator, userLoginTmp.partyId, nowTimestamp)!/>;
	<#if departmentRequired?has_content>
		requiredOfDepartment = "${departmentRequired.get(0)}";
	</#if>
	<#assign createdByDepartment = delegator.findOne("PartyNameView", {"partyId" : departmentRequired.get(0)?if_exists}, false)/>

    var showDetail = false;
    var originFacility = {};
    <#if requirement.facilityId?exists>
        <#assign originFacility = delegator.findOne("Facility", {"facilityId" : requirement.facilityId?if_exists}, false)/>
    originFacility.facilityId = "${originFacility.facilityId?if_exists}";
    originFacility.facilityCode = "${originFacility.facilityCode?if_exists}";
    originFacility.facilityName = "${StringUtil.wrapString(originFacility.facilityName?if_exists)}";
        <#if originFacility.requireDate?has_content && "Y" == originFacility.requireDate && "REQ_COMPLETED" == requirement.statusId?if_exists>
        showDetail = true;
        </#if>
    </#if>

	var requirement = {};
	requirement['requirementId'] = '${requirement.requirementId?if_exists}';
	requirement['destFacilityId'] = '${requirement.destFacilityId?if_exists}';
	requirement['facilityId'] = '${requirement.facilityId?if_exists}';
	requirement['requirementTypeId'] = '${requirement.requirementTypeId?if_exists}';
	requirement['originFacilityId'] = '${requirement.facilityId?if_exists}';
	requirement['estimatedBudget'] = '${requirement.estimatedBudget?if_exists}';
	requirement['requiredByDate'] = '${requirement.requiredByDate?if_exists}';
	requirement['requirementStartDate'] = '${requirement.requirementStartDate?if_exists}';
	requirement['currencyUomId'] = '${requirement.currencyUomId?if_exists}';
	requirement['statusId'] = '${requirement.statusId?if_exists}';
	requirement['reasonEnumId'] = '${requirement.reasonEnumId?if_exists}';
	requirement['description'] = '${requirement.description?if_exists}';
	requirement['createdDate'] = '${requirement.description?if_exists}';
	requirement['createdByUserLogin'] = '${requirement.createdByUserLogin?if_exists}';
	
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
	<#assign conditions = 
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQRETURN_STATUS")),
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQUIREMENT_STATUS"))
		), Static["org.ofbiz.entity.condition.EntityJoinOperator"].OR)/>
	<#assign reqStatus2 = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions), null, null, null, false)>
	var reqStatusData = [<#if reqStatus2?exists><#list reqStatus2 as item>{
				statusId: "${item.statusId?if_exists}",
				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
			},</#list></#if>];
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE")), null, null, null, false)>
	var facilityData = [
 	   	<#if facilities?exists>
 	   		<#list facilities as item>
 	   			{
 	   				facilityId: "${item.facilityId?if_exists}",
 	   				facilityName: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}"
 	   			},
 	   		</#list>
 	   	</#if>
 	];
	var facilityData2 = [];
	
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
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "CURRENCY_MEASURE")), null, null, null, false)>
	var currencyUomData = [
	   	<#if currencyUoms?exists>
	   		<#list currencyUoms as item>
	   			{
	   				uomId: "${item.uomId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	<#assign storekeeper = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.storekeeper")>
	<#assign manager = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.manager.specialist")>
	var storekeeperRole = "${storekeeper}";
	var specialistRole = "${manager}";
		
	var transferTypeData = [];
	<#if requirement.requirementTypeId == "TRANSFER_REQUIREMENT">
		<#assign enumTransferTypes = delegator.findList("TransferTypeEnum", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId", requirement.reasonEnumId?if_exists)), null, null, null, false) />
		<#assign enumTransferTypes = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(enumTransferTypes)>
		transferTypeData = [
       	   	<#if enumTransferTypes?exists>
       	   		<#list enumTransferTypes as item>
       	   			<#assign transType = delegator.findOne("TransferType", {"transferTypeId" : item.transferTypeId?if_exists}, false)/>
       	   			<#if transType?has_content>
       	   			{
       	   				transferTypeId: "${item.transferTypeId?if_exists}",
       	   				description: "${StringUtil.wrapString(transType.get('description', locale)?if_exists)}"
       	   			},
       	   			</#if>
       	   		</#list>
       	   	</#if>
       	];
		
		<#assign productStores = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", company)), null, null, null, false) />
		var shipmentMethodData = [];
		<#assign listIds = []>
		<#if productStores?has_content>
			<#list productStores as store>
				<#assign productStoreShipmentMethods = delegator.findList("ProductStoreShipmentMeth", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", store.productStoreId?if_exists)), null, null, null, false) />
				<#if productStoreShipmentMethods[0]?has_content>
		    		<#list productStoreShipmentMethods as meth>
			    		<#assign idNew = meth.shipmentMethodTypeId>
			        	<#assign check = true>
			        	<#list listIds as idTmp>
			        		<#if idNew == idTmp>
			        			<#assign check = false>
			        			<#break>
			        		<#else>
			        			<#assign check = true>
			        		</#if>
			        	</#list>
			        	<#if check == true>
			        		<#assign listIds = listIds + [idNew]>
			        	</#if>
		    		</#list>
		    	</#if>
			</#list>
		</#if>
		<#if listIds?has_content>
			<#list listIds as methId>
				var item = {};
				<#assign shipmentMethodType = delegator.findOne("ShipmentMethodType", {"shipmentMethodTypeId" : methId?if_exists}, false)/>
				<#assign descMeth = StringUtil.wrapString(shipmentMethodType.get('description', locale)?if_exists) />
				item['shipmentMethodTypeId'] = "${shipmentMethodType.shipmentMethodTypeId?if_exists}";
		    	item['description'] = "${descMeth?if_exists}";
		    	shipmentMethodData.push(item);
			</#list>
		</#if>
		
		var yesNoData = [];
		var itemYes = {
				value: "Y",
				description: "${StringUtil.wrapString(uiLabelMap.LogYes)}",
		}
		var itemNo = {
				value: "N",
				description: "${StringUtil.wrapString(uiLabelMap.LogNO)}",
		}
		yesNoData.push(itemYes);
		yesNoData.push(itemNo);
		
		if (uiLabelMap == undefined) var uiLabelMap = {};
		uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
		uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
		uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
		uiLabelMap.CanNotAfterShipBeforeDate = "${uiLabelMap.CanNotAfterShipBeforeDate}";
		uiLabelMap.CanNotBeforeShipAfterDate = "${uiLabelMap.CanNotBeforeShipAfterDate}";
		uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter = "${uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter}";
	</#if>

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
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.AreYouSureExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureExport)}";
	uiLabelMap.AreYouSureSend = "${StringUtil.wrapString(uiLabelMap.AreYouSureSend)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.QuantityUomId = "${StringUtil.wrapString(uiLabelMap.QuantityUomId)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.ActualExecutedQuantity = "${StringUtil.wrapString(uiLabelMap.ActualExecutedQuantity)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.ProductExpireDate = "${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.QtyRequired = "${StringUtil.wrapString(uiLabelMap.QtyRequired)}";
	uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
	uiLabelMap.ProductManufactureDate = "${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}";
	uiLabelMap.Batch = "${StringUtil.wrapString(uiLabelMap.Batch)}";

	uiLabelMap.wgpagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}";
	uiLabelMap.wgpagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}";
	uiLabelMap.wgpagerrangestring = "${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)}";
	uiLabelMap.wgpagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
	uiLabelMap.wgpagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
	uiLabelMap.wgsortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
	uiLabelMap.wgsortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
	uiLabelMap.wgsortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
	uiLabelMap.wgemptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
	uiLabelMap.wgfilterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
	uiLabelMap.wgfilterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
	uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.wgdragDropToGroupColumn = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
	uiLabelMap.wgtodaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
	uiLabelMap.wgclearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
	
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CreatedNumberSum = "${StringUtil.wrapString(uiLabelMap.CreatedNumberSum)}";
	uiLabelMap.RequiredNumberSum = "${StringUtil.wrapString(uiLabelMap.RequiredNumberSum)}";
	uiLabelMap.QuantityCreateSum = "${StringUtil.wrapString(uiLabelMap.QuantityCreateSum)}";
	uiLabelMap.QuantityDelivered = "${StringUtil.wrapString(uiLabelMap.QuantityDelivered)}";
	uiLabelMap.EXPRequired = "${StringUtil.wrapString(uiLabelMap.EXPRequired)}";
	uiLabelMap.CannotGreaterRequiredNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterRequiredNumber)}";
	uiLabelMap.ExportValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ExportValueMustBeGreaterThanZero)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.AreYouSureConfirm = "${StringUtil.wrapString(uiLabelMap.AreYouSureConfirm)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
</script>
<script type="text/javascript" src="/salesmtlresources/js/distributor/requirement/requirementDetail.js?v=1.0.2"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>