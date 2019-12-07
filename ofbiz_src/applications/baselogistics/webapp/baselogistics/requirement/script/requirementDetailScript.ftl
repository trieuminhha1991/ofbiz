<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	selectFacilityClick = 0;
	var listRequirementItemSelected = [];
	
	var hidePrice = true;
	<#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
		hidePrice = false;
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign requirementId = parameters.requirementId?if_exists/>
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : requirementId}, false)!>
	var requirementId = '${requirementId?if_exists}';
	var requirementTypeId = '${requirement.requirementTypeId?if_exists}';
	var hidden = false;
	if ("CHANGEDATE_REQUIREMENT" == requirementTypeId) {
		hidden = true;
	}
	var requiredFacilityId = '${requirement.facilityId?if_exists}';
	var curStatusId = '${requirement.statusId?if_exists}';
	<#assign userLoginTmp = delegator.findOne("UserLogin", {"userLoginId" : requirement.createdByUserLogin?if_exists}, false)/>
	<#assign createdBy = delegator.findOne("PartyNameView", {"partyId" : userLoginTmp.partyId}, false)/>

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
	
	<#assign requirementStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQUIREMENT_STATUS")), null, null, null, false) />
	var reqStatusData = [
   	   	<#if requirementStatus?exists>
   	   		<#list requirementStatus as item>
   	   			{
   	   				statusId: "${item.statusId?if_exists}",
   	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
   	   			},
   	   		</#list>
   	   	</#if>
   	];
	
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
	
	var destFacility = {};
	<#if requirement.destFacilityId?exists>
		<#assign destFacility = delegator.findOne("Facility", {"facilityId" : requirement.destFacilityId?if_exists}, false)/>
		destFacility.facilityId = "${destFacility.facilityId?if_exists}";		
		destFacility.facilityCode = "${destFacility.facilityCode?if_exists}";		
		destFacility.facilityName = "${StringUtil.wrapString(destFacility.facilityName?if_exists)}";		
	</#if>
	
	var requirement = {};
	var temp = "${StringUtil.wrapString(requirement.description?if_exists?replace('\n', ' '))}";
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
	requirement['description'] = "${StringUtil.wrapString(requirement.reason?if_exists)}";
	requirement['createdDate'] = '${requirement.createdDate?if_exists}';
	requirement['createdByUserLogin'] = '${requirement.createdByUserLogin?if_exists}';
	
	var listProductSelected = [];
	
	var noteData = [];
	<#assign requirementNotes = delegator.findList("RequirementNote", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementId", requirementId)), null, null, null, false) />
	<#if requirementNotes?has_content>
		<#list requirementNotes as noteReq>
			<#assign note = delegator.findOne("NoteData", {"noteId" : noteReq.noteId?if_exists}, false)!/>
			var note = {};
			note["noteId"] = "${note.noteId}";
			note["noteInfo"] = "${StringUtil.wrapString(note.noteInfo?if_exists)}";
			note["noteName"] = "${StringUtil.wrapString(note.noteName?if_exists)}";
			note["noteDateTime"] = new Date("${note.noteDateTime?if_exists}");
			noteData.push(note);
		</#list>
	</#if>
	
	function escapeHtml(unsafe) {
	    return unsafe
	    .replace(/[\\]/g, '\\\\')
	    .replace(/[\"]/g, '\\\"')
	    .replace(/[\/]/g, '\\/')
	    .replace(/[\b]/g, '\\b')
	    .replace(/[\f]/g, '\\f')
	    .replace(/[\n]/g, '\\n')
	    .replace(/[\r]/g, '\\r')
	    .replace(/[\t]/g, '\\t');
	 }
	 
	 var uomData = [];
	 uomData.concat(quantityUomData);
	 uomData.concat(weightUomData);
	 
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

    var isShowExpireDate = <#if isShowExpireDate?exists>${isShowExpireDate?c}<#else>false</#if>;

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
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.ProductExpireDate = "${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}";
	uiLabelMap.QtyRequired = "${StringUtil.wrapString(uiLabelMap.QtyRequired)}";
	uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
	uiLabelMap.ProductManufactureDate = "${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}";
	
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
	uiLabelMap.AreYouSureEdit = "${StringUtil.wrapString(uiLabelMap.AreYouSureEdit)}";
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
	uiLabelMap.AreYouSureExchange = "${StringUtil.wrapString(uiLabelMap.AreYouSureExchange)}";
	uiLabelMap.ExecutedExpireDate = "${StringUtil.wrapString(uiLabelMap.ExecutedExpireDate)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.AreYouSureReject = "${StringUtil.wrapString(uiLabelMap.AreYouSureReject)}";
	uiLabelMap.ManufactureDate = "${StringUtil.wrapString(uiLabelMap.ManufactureDate)}";
	uiLabelMap.Batch = "${StringUtil.wrapString(uiLabelMap.Batch)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.BPOTotal = "${StringUtil.wrapString(uiLabelMap.BPOTotal)}";
	uiLabelMap.TransferFromRequirementCreatedDone = "${StringUtil.wrapString(uiLabelMap.TransferFromRequirementCreatedDone)}";
	uiLabelMap.ActualExportedQuantity = "${StringUtil.wrapString(uiLabelMap.ActualExportedQuantity)}";
	uiLabelMap.ActualReceivedQuantity = "${StringUtil.wrapString(uiLabelMap.ActualReceivedQuantity)}";
</script>
<script type="text/javascript" src="/logresources/js/requirement/requirementDetail.js?v=1.1.3"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
