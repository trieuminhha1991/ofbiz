<@jqGridMinimumLib />
<script type="text/javascript">
	var localeStr = "VI"; 
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign returnId = parameters.returnId?if_exists/>
	var returnId = "${returnId?if_exists}";
	var shipmentId = "${parameters.shipmentId?if_exists}"
	<#assign returnHeader = delegator.findOne("ReturnHeader", {"returnId" : parameters.returnId?if_exists}, false)/>
	var listReturnItemSelected = [];
	var returnHeaderTypeId = '${returnHeader.returnHeaderTypeId?if_exists}';
	var destinationFacilityId = '${returnHeader.destinationFacilityId?if_exists}';
	
	var currencyUomId = "${returnHeader.currencyUomId?if_exists}";
	
	var entryDate = "${returnHeader.entryDate}";
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descPackingUom?if_exists}';
		quantityUomData[${item_index}] = row;
	</#list>
	
	<#assign wuoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list wuoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descPackingUom?if_exists}';
		weightUomData[${item_index}] = row;
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
	
	<#assign invStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_NON_SER_STTS"), null, null, null, false)/>
	var invStatusData = [];
	var rowGood = {};
	rowGood['statusId'] = "Good";
	rowGood['description'] = "${uiLabelMap.InventoryGood}";
	invStatusData.push(rowGood);
	<#list invStatuses as item>
		var row = {};
		<#assign descInvStatus = StringUtil.wrapString(item.get('description', locale))>
		row['inventoryStatusId'] = "${item.statusId}";
		row['description'] = "${descInvStatus?if_exists}";
		invStatusData.push(row);
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
	
	<#if returnHeader.destinationFacilityId?has_content>
		<#assign facility = delegator.findOne("Facility", {"facilityId" : returnHeader.destinationFacilityId?if_exists}, false)/>
	</#if>
	
	<#assign returnItems = delegator.findList("ReturnItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("returnId", returnId), null, null, null, false)/>
	<#assign orderId = ''>
	var countItem = '${returnItems.size()}';
	<#if returnItems?has_content>
	<#if returnItems.get(0).getString("orderId")?has_content>
		<#assign orderId = returnItems.get(0).getString("orderId")>
	</#if>
	</#if>
	
	<#assign manager = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "roleType.manager")>
	<#assign listRoles = []>
	<#assign listRoles = listRoles + [manager]>
	<#assign facilities = Static['com.olbius.baselogistics.util.LogisticsPartyUtil'].getFacilityByRolesAndFacilityType(delegator, userLogin.partyId?if_exists, listRoles, "WAREHOUSE")! />;
	
	var facilityReturnData = [];
	<#if facilities?has_content>
	<#list facilities as item>
		var row = {};
		<#assign descFac = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['description'] = "${descFac?if_exists}";
		facilityReturnData.push(row);
	</#list>
	<#else>
		<#assign storeFacilities = delegator.findList("ProductStoreFacility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", (orderHeader.productStoreId)?if_exists)), null, null, null, false)>
		<#if storeFacilities?has_content>
			<#list storeFacilities as storeFac>
				<#assign facTemp = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", storeFac.facilityId?if_exists), false)/>
				var check1 = false;
				for (var j = 0; j < facilityReturnData.length; j ++){
					if ('${storeFac.facilityId}' == facilityReturnData[j].facilityId){
						check1 = true;
					}
				}
				<#if company == facTemp.ownerPartyId>
					if (!check1){
						var rowTmp = {};
						<#assign descriptionTemp = StringUtil.wrapString(facTemp.facilityName?if_exists)/>
						rowTmp['facilityId'] = "${facTemp.facilityId?if_exists}";
						rowTmp['ownerPartyId']= "${facTemp.ownerPartyId?if_exists}";
						rowTmp['description'] = "${descriptionTemp?if_exists}";
						rowTmp['productStoreId'] = "${facTemp.productStoreId?if_exists}";
						facilityReturnData.push(rowTmp);
					}
				</#if>
			</#list>
		<#else>
		</#if>
	</#if>
	
	function getUomDescription(uomId) {
		for (var i = 0; i < quantityUomData.length; i ++) {
			if (uomId == quantityUomData[i].uomId) return quantityUomData[i].description;
		}
		for (var i = 0; i < weightUomData.length; i ++) {
			if (uomId == weightUomData[i].uomId) return weightUomData[i].description;
		}
	}	
	
	var listReturnItemData = [];
	
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
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.AreYouSureYouWantToImport = "${StringUtil.wrapString(uiLabelMap.AreYouSureYouWantToImport)}";
	uiLabelMap.YouNotYetChooseExpireDate = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseExpireDate)}";
	uiLabelMap.YouNotYetChooseDatetimeManufactured = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseDatetimeManufactured)}";
	uiLabelMap.YouNotYetChooseShipment = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseShipment)}";
	uiLabelMap.PleaseChooseAllProductOrEnterReceiveQuantityToZero = "${StringUtil.wrapString(uiLabelMap.PleaseChooseAllProductOrEnterReceiveQuantityToZero)}";
	uiLabelMap.AllItemInListMustBeUpdated = "${StringUtil.wrapString(uiLabelMap.AllItemInListMustBeUpdated)}";
	
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.ManufactureDate = "${StringUtil.wrapString(uiLabelMap.ManufactureDate)}";
	uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
	uiLabelMap.QuantityReturned = "${StringUtil.wrapString(uiLabelMap.QuantityReturned)}";
	uiLabelMap.Batch = "${StringUtil.wrapString(uiLabelMap.Batch)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.Reason = "${StringUtil.wrapString(uiLabelMap.Reason)}";
	uiLabelMap.ProductStatus = "${StringUtil.wrapString(uiLabelMap.ProductStatus)}";
	uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
	uiLabelMap.ActualExportedQuantity = "${StringUtil.wrapString(uiLabelMap.ActualExportedQuantity)}";
	uiLabelMap.ReturnType = "${StringUtil.wrapString(uiLabelMap.ReturnType)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.ManufactureDateMustBeBeforeNow = "${StringUtil.wrapString(uiLabelMap.ManufactureDateMustBeBeforeNow)}";
	uiLabelMap.ManufactureDateMustBeBeforeExpireDate = "${StringUtil.wrapString(uiLabelMap.ManufactureDateMustBeBeforeExpireDate)}";
	uiLabelMap.ExpireDateMustBeBeforeManufactureDate = "${StringUtil.wrapString(uiLabelMap.ExpireDateMustBeBeforeManufactureDate)}";
	uiLabelMap.ExportValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ExportValueMustBeGreaterThanZero)}";
	uiLabelMap.DmsFieldRequired = "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.InventoryGood = "${StringUtil.wrapString(uiLabelMap.InventoryGood)}";
	
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
	
	uiLabelMap.QuantityNotEntered = "${StringUtil.wrapString(uiLabelMap.QuantityNotEntered)}";
	uiLabelMap.MissingExpireDate = "${StringUtil.wrapString(uiLabelMap.MissingExpireDate)}";
	uiLabelMap.MissingManufactureDate = "${StringUtil.wrapString(uiLabelMap.MissingManufactureDate)}";
	uiLabelMap.PleaseChooseProductAndFulfillData = "${StringUtil.wrapString(uiLabelMap.PleaseChooseProductAndFulfillData)}";
	uiLabelMap.MissingBacth = "${StringUtil.wrapString(uiLabelMap.MissingBacth)}";
	
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.ExpireDateMustBeAfterNow = "${StringUtil.wrapString(uiLabelMap.ExpireDateMustBeAfterNow)}";
	uiLabelMap.For = "${StringUtil.wrapString(uiLabelMap.For)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.CannotGreaterRequiredNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterRequiredNumber)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	uiLabelMap.IsPromo = "${StringUtil.wrapString(uiLabelMap.IsPromo)}";
	uiLabelMap.Weight = "${StringUtil.wrapString(uiLabelMap.Weight)}";

	<#if fromSales?if_exists == "Y">
	var fromSales = true;
	<#else>
	var fromSales = false;
	</#if>
	
</script>
<script type="text/javascript" src="/logresources/js/return/receiveReturn.js"></script>