<@jqOlbCoreLib hasValidator=true/>
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
	<#if parameters.requirementTypeId?if_exists == 'TRANSFER_REQUIREMENT'>
	var urlDetail = 'viewTransferRequirementDetail?requirementId=';
	<#elseif parameters.requirementTypeId?if_exists == 'RETURN_REQ'>
	var urlDetail = 'viewDisRequirementDetail?requirementId=';
	<#elseif parameters.requirementTypeId?if_exists == 'EXPORT_REQUIREMENT'>
	var urlDetail = 'viewRemoveRequirementDetail?requirementId=';
	</#if>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var originFacilityId = null;
	var destFacilityId = null;
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign departments = Static["com.olbius.basehr.util.PartyUtil"].getDepartmentOfEmployee(delegator, userLogin.get("partyId"), nowTimestamp)!/>;
	var deptTmp = null;
	<#if departments?has_content>
		deptTmp = "${departments.get(0)}";
	</#if>
	<#assign conditions = 
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQRETURN_STATUS")),
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQUIREMENT_STATUS"))
		), Static["org.ofbiz.entity.condition.EntityJoinOperator"].OR)/>
	<#assign reqStatus2 = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions), null, null, null, false)>
	var statusData2 = [<#if reqStatus2?exists><#list reqStatus2 as item>{
				statusId: "${item.statusId?if_exists}",
				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
			},</#list></#if>];
	
	<#assign reqStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQRETURN_STATUS")), null, null, null, false)>
	var statusData = [<#if reqStatus?exists><#list reqStatus as item>{
  	   				statusId: "${item.statusId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},</#list></#if>];
	
	var statusIdParam = "${parameters.statusId?if_exists}";
	
	<#if parameters.requirementTypeId?has_content>
		<#assign requirementReasons = delegator.findList("RequirementEnumType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", parameters.requirementTypeId?if_exists), null, null, null, false) />
	<#else>
		<#assign requirementReasons = delegator.findList("RequirementEnumType", null, null, null, null, false) />
	</#if>
	
	<#assign enumTypeIds = []>
	
	<#list requirementReasons as reason>
		<#assign enumTypeIds = enumTypeIds + [reason.enumTypeId?if_exists]>
	</#list>
	
	<#assign reasonEnums = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, enumTypeIds), null, null, null, false)>
	var reasonEnumData = [<#if reasonEnums?exists><#list reasonEnums as item>{
  	   				enumId: "${item.enumId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},</#list></#if>];
	<#assign requirementTypes = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "PRODUCT_REQUIREMENT")), null, null, null, false) />
	var requirementTypeData = [<#if requirementTypes?exists><#list requirementTypes as item>{
  	   				requirementTypeId: "${item.requirementTypeId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},</#list></#if>];
	
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "CURRENCY_MEASURE")), null, null, null, false)>
	var currencyUomData = [<#if currencyUoms?exists><#list currencyUoms as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}"
	},</#list></#if>];
	 
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false) />
	var quantityUomData = [<#if quantityUoms?exists><#list quantityUoms as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	},</#list></#if>];
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
	var facilityData = [<#if facilities?exists><#list facilities as item>{
 	   				facilityId: "${item.facilityId?if_exists}",
 	   				facilityName: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}"
 	   			},</#list></#if>];
	
	var transferTypeData = new Array();
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
	
	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.MustSelectSameOrgAndDestFacility = "${StringUtil.wrapString(uiLabelMap.MustSelectRequirementWithSameOrgAndDestFacility)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
	uiLabelMap.YouNotYetChooseRequirement = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseRequirement)}";
	uiLabelMap.UtilCreateWithAppprovedRequirement = "${StringUtil.wrapString(uiLabelMap.UtilCreateWithAppprovedRequirement)}";
	uiLabelMap.MustSelectSamePurpose = "${StringUtil.wrapString(uiLabelMap.MustSelectSamePurpose)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.RequiredNumberSum = "${StringUtil.wrapString(uiLabelMap.RequiredNumberSum)}";
	uiLabelMap.CreatedNumberSum = "${StringUtil.wrapString(uiLabelMap.CreatedNumberSum)}";
	uiLabelMap.QuantityDelivered = "${StringUtil.wrapString(uiLabelMap.QuantityDelivered)}";
	uiLabelMap.QuantityCreateSum = "${StringUtil.wrapString(uiLabelMap.QuantityCreateSum)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.EXPRequired = "${StringUtil.wrapString(uiLabelMap.EXPRequired)}";
	uiLabelMap.RequirementId = "${StringUtil.wrapString(uiLabelMap.RequirementId)}";
	uiLabelMap.CannotGreaterRequiredNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterRequiredNumber)}";
	uiLabelMap.ExportValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ExportValueMustBeGreaterThanZero)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	
	uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter = "${StringUtil.wrapString(uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CanNotAftershipBeforeDate = "${StringUtil.wrapString(uiLabelMap.CanNotAftershipBeforeDate)}";
	uiLabelMap.CanNotBeforeshipAfterDate = "${StringUtil.wrapString(uiLabelMap.CanNotBeforeshipAfterDate)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	
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
	
</script>
<script type="text/javascript" src="/salesmtlresources/js/distributor/requirement/listRequirement.js?v=1.0.0"></script>