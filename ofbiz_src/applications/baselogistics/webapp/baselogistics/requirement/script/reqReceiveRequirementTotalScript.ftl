<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasCore=true/>
<script type="text/javascript">
	var locale = "${locale?if_exists}";
	var listProductSelected = [];
	var listProductMap = [];
	
	var hidePrice = true;
	<#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
		hidePrice = false;
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
		
	<#assign requirementId = parameters.requirementId?if_exists/>
	var requirementId = "${requirementId?if_exists}";
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : parameters.requirementId?if_exists}, false)/>
	var listRequirementItemSelected = [];
	var requirementTypeId = '${requirement.requirementTypeId?if_exists}';
	var curStatusId = '${requirement.statusId?if_exists}';
	var facilityId = '${requirement.facilityId?if_exists}';
	
	<#assign originFacility = delegator.findOne("Facility", {"facilityId" : requirement.facilityId?if_exists}, false)/>
	<#if requirement.contactMechId?has_content>
		<#assign originAddress = delegator.findOne("PostalAddressFullNameDetail", {"contactMechId" : requirement.contactMechId?if_exists}, false)/>
	</#if>
	
	<#assign requireDate = false>
	<#if originFacility?has_content>
		<#assign requireDate = originFacility.requireDate?if_exists>
	</#if>
	
	<#assign userLoginTmp = delegator.findOne("UserLogin", {"userLoginId" : requirement.createdByUserLogin?if_exists}, false)/>
	
	<#assign createdBy = delegator.findOne("PartyNameView", {"partyId" : userLoginTmp.partyId}, false)/>
	<#assign partyCreatedBy = delegator.findOne("Party", {"partyId" : userLoginTmp.partyId}, false)/>
	
	var createdDate = "${requirement.createdDate}";
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
		<#assign descWUom = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descWUom?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign requirementStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "REQUIREMENT_STATUS"), null, null, null, false) />
	var statusData = new Array();
	<#list requirementStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		statusData.push(row);
	</#list>
	
	var reasonEnumId = "${requirement.reasonEnumId?if_exists}";
	
	<#assign requirementType = delegator.findOne("RequirementType", {"requirementTypeId" : requirement.requirementTypeId?if_exists}, false)/>
	<#assign requirementTypeId = requirement.requirementTypeId?if_exists/>
	<#assign requirementTypeDesc = StringUtil.wrapString(requirementType.get('description', locale)?if_exists)/>
	<#assign status = delegator.findOne("StatusItem", {"statusId" : requirement.statusId?if_exists}, false)/>
	<#assign statusDesc = StringUtil.wrapString(status.get('description', locale)?if_exists)/>
	
	<#assign enum = delegator.findOne("Enumeration", {"enumId" : requirement.reasonEnumId?if_exists}, false)/>
	<#assign reasonDesc = StringUtil.wrapString(enum.get('description', locale)?if_exists)/>
	
	<#assign createdBy = delegator.findOne("PartyNameView", {"partyId" : userLogin.partyId?if_exists}, false)/>
	
	<#assign requirementItems = delegator.findList("RequirementItemDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementId", requirementId), null, null, null, false)/>
	
	<#if requirementItems?has_content>
		<#list requirementItems as item>
			var item = {};
			item.requirementId = "${item.requirementId?if_exists}";
			item.reqItemSeqId = "${item.reqItemSeqId?if_exists}";
			item.productId = "${item.productId?if_exists}";
			item.productCode = "${item.productCode?if_exists}";
			item.productName = "${StringUtil.wrapString(item.productName?if_exists)}";
			item.description = "${StringUtil.wrapString(item.description?if_exists)}";
			item.quantityUomId = "${item.quantityUomId?if_exists}";
			item.weightUomId = "${item.weightUomId?if_exists}";
			item.requireAmount = "${item.requireAmount?if_exists}";
			var unitCost = '${item.unitCost?if_exists}';
			<#if locale == 'vi'>
				if (typeof unitCost == 'string') {
					unitCost = unitCost.replace('.', '');
					unitCost = unitCost.replace(',', '.');
				}
			</#if>
		    item.unitCost = parseFloat(unitCost, null, 3);
		    <#if item.requireAmount?has_content && item.requireAmount == 'Y'>
				var quantityTmp = "${item.quantity?if_exists}";
				<#if locale == 'vi'>
					quantityTmp = quantityTmp.replace('.', '');
					quantityTmp = quantityTmp.replace(',', '.');
				</#if>
				item.quantity = parseFloat(quantityTmp, null, 3);
				item.requiredQuantity = parseFloat(quantityTmp, null, 3);
		    <#else>
			    item.quantity = "${item.quantity?if_exists}";
				item.requiredQuantity = "${item.quantity?if_exists}";
		    </#if>
			listProductSelected.push(item);
		</#list>
	</#if>
	var countItem = '${requirementItems.size()}';
	
	function getUomDesc(uomId) {
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
	
	var requirement = {};
	requirement['requirementId'] = '${requirement.requirementId?if_exists}';
	requirement['requirementTypeId'] = '${requirement.requirementTypeId?if_exists}';
	requirement['facilityId'] = '${requirement.facilityId?if_exists}';
	requirement['estimatedBudget'] = '${requirement.estimatedBudget?if_exists}';
	requirement['requiredByDate'] = '${requirement.requiredByDate?if_exists}';
	requirement['requirementStartDate'] = '${requirement.requirementStartDate?if_exists}';
	requirement['currencyUomId'] = '${requirement.currencyUomId?if_exists}';	
	requirement['statusId'] = '${requirement.statusId?if_exists}';	
	requirement['reasonEnumId'] = '${requirement.reasonEnumId?if_exists}';
	requirement['description'] = '${requirement.description?if_exists}';
	requirement['createdDate'] = '${requirement.description?if_exists}';
	requirement['createdByUserLogin'] = '${requirement.createdByUserLogin?if_exists}';
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureExport)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureAccept = "${StringUtil.wrapString(uiLabelMap.AreYouSureAccept)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.AreYouSureYouWantToImport = "${StringUtil.wrapString(uiLabelMap.AreYouSureYouWantToImport)}";
	uiLabelMap.YouNotYetChooseExpireDate = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseExpireDate)}";
	uiLabelMap.YouNotYetChooseDatetimeManufactured = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseDatetimeManufactured)}";
	uiLabelMap.YouNotYetChooseShipment = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseShipment)}";
	uiLabelMap.PleaseChooseAllProductOrEnterReceiveQuantityToZero = "${StringUtil.wrapString(uiLabelMap.PleaseChooseAllProductOrEnterReceiveQuantityToZero)}";
	uiLabelMap.AllItemInListMustBeUpdated = "${StringUtil.wrapString(uiLabelMap.AllItemInListMustBeUpdated)}";
	uiLabelMap.YouNotYetChooseDatetimeManufactured = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseDatetimeManufactured)}";
	uiLabelMap.YouNotYetChooseExpireDate = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseExpireDate)}";
	uiLabelMap.YouNotYetChooseShipment = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseShipment)}";
	uiLabelMap.YouNotYetChooseUnitCost = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseUnitCost)}";
	uiLabelMap.ListProduct = "${StringUtil.wrapString(uiLabelMap.ListProduct)}";
	uiLabelMap.DetailInfo = "${StringUtil.wrapString(uiLabelMap.DetailInfo)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.DmsFieldRequired = "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}";
	uiLabelMap.ReceiveQuantity = "${StringUtil.wrapString(uiLabelMap.ReceiveQuantity)}";
	uiLabelMap.QuantityMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.QuantityMustBeGreateThanZero)}";
	uiLabelMap.RequiredNumber = "${StringUtil.wrapString(uiLabelMap.RequiredNumber)}";
	uiLabelMap.EXPRequired = "${StringUtil.wrapString(uiLabelMap.EXPRequired)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.ReceivedQuantity = "${StringUtil.wrapString(uiLabelMap.ReceivedQuantity)}";
	uiLabelMap.ManufactureDate = "${StringUtil.wrapString(uiLabelMap.ManufactureDate)}";
	uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
	uiLabelMap.RequiredNumber = "${StringUtil.wrapString(uiLabelMap.RequiredNumber)}";
	uiLabelMap.CannotGreaterRequiredNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterRequiredNumber)}";
	uiLabelMap.ExpireDateNotEnter = "${StringUtil.wrapString(uiLabelMap.ExpireDateNotEnter)}";
	uiLabelMap.PleaseEnterQuantityReceive = "${StringUtil.wrapString(uiLabelMap.PleaseEnterQuantityExported)}";
	uiLabelMap.Batch = "${StringUtil.wrapString(uiLabelMap.Batch)}";
	uiLabelMap.ManufactureDateMustBeBeforeNow = "${StringUtil.wrapString(uiLabelMap.ManufactureDateMustBeBeforeNow)}";
	uiLabelMap.PleaseEnterQuantityReceive = "${StringUtil.wrapString(uiLabelMap.PleaseEnterQuantityExported)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.TheExpiredDateFieldNotYetBeEntered = "${StringUtil.wrapString(uiLabelMap.TheExpiredDateFieldNotYetBeEntered)}";
	uiLabelMap.MissingManufactureDate = "${StringUtil.wrapString(uiLabelMap.MissingManufactureDate)}";
	uiLabelMap.MissingBacth = "${StringUtil.wrapString(uiLabelMap.MissingBacth)}";
	uiLabelMap.ReceivedNumber = "${StringUtil.wrapString(uiLabelMap.ReceivedNumber)}";
	uiLabelMap.CannotGreaterRequiredNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterRequiredNumber)}";
	uiLabelMap.TotalEnteredQuantityMustBeGreatedZero = "${StringUtil.wrapString(uiLabelMap.TotalEnteredQuantityMustBeGreatedZero)}";
	
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
	uiLabelMap.RequiredNumberSum = "${StringUtil.wrapString(uiLabelMap.RequiredNumberSum)}";
	uiLabelMap.BatchSum = "${StringUtil.wrapString(uiLabelMap.BatchSum)}";
	uiLabelMap.ExpiredDateSum = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}";
	uiLabelMap.ManufacturedDateSum = "${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)}";
	uiLabelMap.ManufactureDateMustBeBeforeNow = "${StringUtil.wrapString(uiLabelMap.ManufactureDateMustBeBeforeNow)}";
	uiLabelMap.ManufactureDateMustBeBeforeExpireDate = "${StringUtil.wrapString(uiLabelMap.ManufactureDateMustBeBeforeExpireDate)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	
	uiLabelMap.ExpireDateMustBeAfterNow = "${StringUtil.wrapString(uiLabelMap.ExpireDateMustBeAfterNow)}";
	uiLabelMap.ExpireDateMustBeBeforeManufactureDate = "${StringUtil.wrapString(uiLabelMap.ExpireDateMustBeBeforeManufactureDate)}";
	uiLabelMap.For = "${StringUtil.wrapString(uiLabelMap.For)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.ActualReceivedQuantity = "${StringUtil.wrapString(uiLabelMap.ActualReceivedQuantity)}";
	uiLabelMap.Delete = "${StringUtil.wrapString(uiLabelMap.Delete)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.BLTotalCannotGreaterReceiveNumber = "${StringUtil.wrapString(uiLabelMap.BLTotalCannotGreaterReceiveNumber)}";
	uiLabelMap.BLCannotLessThanTotalQuantityDetail = "${StringUtil.wrapString(uiLabelMap.BLCannotLessThanTotalQuantityDetail)}";
	uiLabelMap.ActualReceivedQuantitySum = "${StringUtil.wrapString(uiLabelMap.ActualReceivedQuantitySum)}";
	uiLabelMap.NoDataToDisplay = "${StringUtil.wrapString(uiLabelMap.NoDataToDisplay)}";
	
</script>
<script type="text/javascript" src="/logresources/js/requirement/reqReceiveRequirementTotal.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>