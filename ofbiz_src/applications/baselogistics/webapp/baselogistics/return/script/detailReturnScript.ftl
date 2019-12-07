<script type="text/javascript">
	var localeStr = "VI"; 
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	var hasExported = false;
	var hasReserved = false;
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign returnId = parameters.returnId?if_exists/>
	var returnId = "${returnId?if_exists}";
	<#assign returnHeader = delegator.findOne("ReturnHeader", {"returnId" : parameters.returnId?if_exists}, false)/>
	
	<#assign returnRequirementCommitments = delegator.findList("ReturnRequirementCommitment", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("returnId", returnHeader.returnId), null, null, null, false)>

	var returnHeaderTypeId = '${returnHeader.returnHeaderTypeId?if_exists}';
	var destinationFacilityId = '${returnHeader.destinationFacilityId?if_exists}';
	
	<#assign createByUserLogin = delegator.findOne("UserLogin", {"userLoginId" : returnHeader.createdBy?if_exists}, false)/>
	<#assign possitions = Static["com.olbius.basehr.util.PartyUtil"].getPositionTypeOfEmplAtTime(delegator, createByUserLogin.partyId?if_exists, returnHeader.getTimestamp("entryDate"))!/>
	
	var entryDate = "${returnHeader.entryDate}";
	var uomData = [];
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descPackingUom?if_exists}';
		quantityUomData[${item_index}] = row;
		uomData.push(row);
	</#list>
	
	<#assign returnSoStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_RETURN_STTS"), null, null, null, false) />
	var statusSOData = new Array();
	<#list returnSoStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		statusSOData.push(row);
	</#list>
	
	<#assign returnPoStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PORDER_RETURN_STTS"), null, null, null, false) />
	var statusPOData = new Array();
	<#list returnPoStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		statusPOData.push(row);
	</#list>
	
	<#assign listReturnReason = delegator.findList("ReturnReason", null, null, null, null, false) />
	var listReturnReason = [
							<#if listReturnReason?exists>
								<#list listReturnReason as item>
									{
										returnReasonId: "${item.returnReasonId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapReturnReason = {
						<#if listReturnReason?exists>
							<#list listReturnReason as item>
								"${item.returnReasonId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	
	
	<#assign listReturnType = delegator.findList("ReturnType", null, null, null, null, false) />
	var listReturnType = [
							<#if listReturnType?exists>
								<#list listReturnType as item>
									{
										returnTypeId: "${item.returnTypeId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapReturnType = {
						<#if listReturnType?exists>
							<#list listReturnType as item>
								"${item.returnTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	

	
	<#assign returnHeaderType = delegator.findOne("ReturnHeaderType", {"returnHeaderTypeId" : returnHeader.returnHeaderTypeId?if_exists}, false)/>
	<#assign returnHeaderTypeId = returnHeader.returnHeaderTypeId?if_exists/>
	<#assign returnHeaderTypeDesc = StringUtil.wrapString(returnHeaderType.get('description', locale)?if_exists)/>
	<#assign status = delegator.findOne("StatusItem", {"statusId" : returnHeader.statusId?if_exists}, false)/>
	<#assign statusId = StringUtil.wrapString(status.get('description', locale)?if_exists)/>
	
	<#assign fromParty = delegator.findOne("PartyNameView", {"partyId" : returnHeader.fromPartyId?if_exists}, false)/>
	<#assign fromPartyId = Static["com.olbius.basehr.util.PartyUtil"].getPersonName(delegator, fromParty.getString("partyId"))>
	
	<#if !fromPartyId?has_content>
		<#assign fromPartyId = fromParty.groupName?if_exists>
	</#if>
	
	<#assign toParty = delegator.findOne("PartyNameView", {"partyId" : returnHeader.toPartyId?if_exists}, false)/>
	<#assign toPartyName = toParty.groupName?if_exists/>
	
	<#assign facility = delegator.findOne("Facility", {"facilityId" : returnHeader.destinationFacilityId?if_exists}, false)!/> 
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", "${company}")), null, null, null, false)>
	var facilityData = [
 	   	<#if facilities?exists>
 	   		<#list facilities as item>
   				<#assign partyFacs = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", item.facilityId, "roleTypeId", "MANAGER", "partyId", userLogin.partyId)), null, null, null, false)>
   					<#assign partyFacs = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyFacs)>
	 	   			<#if partyFacs?has_content>
		   				{
		 	   				facilityId: "${item.facilityId?if_exists}",
		 	   				facilityName: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}"
		 	   			},
	 	   			</#if>
 	   		</#list>
 	   	</#if>
 	];
	
	var receivedDate = null;
	var receivedCompleted = null;
	<#if returnHeader.statusId == "RETURN_RECEIVED" || returnHeader.statusId == "SUP_RETURN_SHIPPED">
		<#if returnHeader.statusId == "RETURN_RECEIVED">
			<#assign returnStatus1 = delegator.findList("ReturnStatus", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "RETURN_RECEIVED", "returnId", returnId)), null, null, null, false) />
		<#elseif returnHeader.statusId == "SUP_RETURN_SHIPPED">
			<#assign returnStatus1 = delegator.findList("ReturnStatus", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "SUP_RETURN_SHIPPED", "returnId", returnId)), null, null, null, false) />
		</#if>
		<#list returnStatus1 as status>
			receivedDate = '${status.statusDatetime}';
			<#break>
		</#list>
	<#elseif returnHeader.statusId == "RETURN_COMPLETED" || returnHeader.statusId == "SUP_RETURN_COMPLETED">
		<#assign returnStatus2 = delegator.findList("ReturnStatus", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "RETURN_COMPLETED", "returnId", returnId)), null, null, null, false) />
		<#list returnStatus2 as status>
			receivedCompleted = '${status.statusDatetime}';
			<#break>
		</#list>
	</#if>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "WEIGHT_MEASURE")), null, null, null, false) />
	var weightUomData = [];
   	<#if weightUoms?exists>
   		<#list weightUoms as item>
   			var row = {
   				uomId: "${item.uomId?if_exists}",
   				description: "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}"
   			};
   			weightUomData.push(row);
   			uomData.push(row);
   		</#list>
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
	uiLabelMap.AreYouSureExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureExport)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.ProductHasBeenReservedForSomeSalesOrder = "${StringUtil.wrapString(uiLabelMap.ProductHasBeenReservedForSomeSalesOrder)}";
	uiLabelMap.DoYouWantToReservesToAnotherInventory = "${StringUtil.wrapString(uiLabelMap.DoYouWantToReservesToAnotherInventory)}";
	uiLabelMap.ProductHasBeenExported = "${StringUtil.wrapString(uiLabelMap.ProductHasBeenExported)}";
	uiLabelMap.AreYouSureAccept = "${StringUtil.wrapString(uiLabelMap.AreYouSureAccept)}";
	uiLabelMap.Click = "${StringUtil.wrapString(uiLabelMap.Click)}";
	uiLabelMap.orderTo = "${StringUtil.wrapString(uiLabelMap.orderTo)}";
	uiLabelMap.chooseAnotherProductToExport = "${StringUtil.wrapString(uiLabelMap.chooseAnotherProductToExport)}";
	uiLabelMap.or = "${StringUtil.wrapString(uiLabelMap.or)}";
	uiLabelMap.click = "${StringUtil.wrapString(uiLabelMap.click)}";
	uiLabelMap.backTo = "${StringUtil.wrapString(uiLabelMap.backTo)}";
	uiLabelMap.detailScreen = "${StringUtil.wrapString(uiLabelMap.detailScreen)}";
	uiLabelMap.NotReceivedToFacilityRequired = "${StringUtil.wrapString(uiLabelMap.NotReceivedToFacilityRequired)}";
	uiLabelMap.ChooseInventoryItem = "${StringUtil.wrapString(uiLabelMap.ChooseInventoryItem)}";
	uiLabelMap.ChooseInventoryYouWant = "${StringUtil.wrapString(uiLabelMap.ChooseInventoryYouWant)}";
	uiLabelMap.ExportExactlyWhatYouReceived = "${StringUtil.wrapString(uiLabelMap.ExportExactlyWhatYouReceived)}";
	
</script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=001"></script>
<script type="text/javascript" src="/logresources/js/return/detailReturn.js?v=0.0.6"></script>