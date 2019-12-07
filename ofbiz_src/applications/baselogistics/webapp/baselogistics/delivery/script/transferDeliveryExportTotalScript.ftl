<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasCore=true/>
<script type="text/javascript">
	var locale = "${locale?if_exists}";
	var listProductSelected = [];
	var listProductMap = [];
	var listProductPromoMap = [];
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
		
	<#assign deliveryId = parameters.deliveryId?if_exists/>
	var deliveryId = "${deliveryId?if_exists}";
	<#assign delivery = delegator.findOne("Delivery", {"deliveryId" : parameters.deliveryId?if_exists}, false)/>
	var listDeliveryItemSelected = [];
	
	var originFacilityId = '${delivery.originFacilityId?if_exists}';
	var destFacilityId = '${delivery.destFacilityId?if_exists}';
	
	<#assign originFacility = delegator.findOne("Facility", {"facilityId" : delivery.originFacilityId?if_exists}, false)/>
	<#assign destFacility = delegator.findOne("Facility", {"facilityId" : delivery.destFacilityId?if_exists}, false)/>
	
	<#if delivery.transferId?has_content>
		<#assign transferHeader = delegator.findOne("TransferHeader", {"transferId" : delivery.transferId?if_exists}, false)/>
	</#if>
	<#assign requireDate = false>
	<#if originFacility?has_content>
		<#assign requireDate = originFacility.requireDate?if_exists>
	</#if>
	var requireDate = "${requireDate?if_exists}"
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
	
	<#assign deliveryStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false) />
	var statusData = new Array();
	<#list deliveryStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		statusData.push(row);
	</#list>
	
	<#assign condHd = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("deliveryId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, deliveryId?if_exists)>
	<#assign condStt = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, 'DELI_ITEM_APPROVED')>
	
	<#assign deliveryItems = delegator.findList("DeliveryItemGroupTransferAndInventory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(condHd, condStt)), null, null, null, false)/>
	
	<#if deliveryItems?has_content>
		<#list deliveryItems as item>
			var item = {};
			item.deliveryId = "${item.deliveryId?if_exists}";
			item.transferId = "${item.transferId?if_exists}";
			item.transferItemSeqId = "${item.transferItemSeqId?if_exists}";
			item.productId = "${item.productId?if_exists}";
			item.productCode = "${item.productCode?if_exists}";
			item.productName = "${StringUtil.wrapString(item.productName?if_exists)}";
			item.description = "${StringUtil.wrapString(item.description?if_exists)}";
			item.quantityUomId = "${item.quantityUomId?if_exists}";
			item.weightUomId = "${item.weightUomId?if_exists}";
			item.requireAmount = "${item.requireAmount?if_exists}";
			item.requireAmount = "${item.requireAmount?if_exists}";
		    <#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId?has_content && item.amountUomTypeId == 'WEIGHT_MEASURE'>
	   		 	var quantityTmp = 0;
	   		 	quantityTmp = "${item.amount?if_exists}";
				<#if locale == 'vi'>
					if (typeof quantityTmp == 'string') {
						quantityTmp = quantityTmp.replace(',', '.');
					}
				</#if>
	   		 	<#if item.amountOnHandTotal?has_content>
					var qohTmp = "${item.amountOnHandTotal?if_exists}";
					<#if locale == 'vi'>
						if (typeof qohTmp == 'string') {
							qohTmp = qohTmp.replace(',', '.');
						}
					</#if>
					
					qohTmp = parseFloat(qohTmp, null, 3);
					quantityTmp = parseFloat(quantityTmp, null, 3);
					var qty = 0;
					if (qohTmp > quantityTmp) {
						qty = quantityTmp;
					} else {
						qty = qohTmp;
					}
					item.qoh = qohTmp;
					item.quantity = qty;
			 	<#else>
			    	item.qoh = 0;
			    </#if>
				item.createdQuantity = parseFloat(quantityTmp, null, 3);
		    <#else>
		    	var quantityTmp = 0;
		    	quantityTmp = "${item.quantity?if_exists}";
				<#if locale == 'vi'>
					if (typeof quantityTmp == 'string') {
						quantityTmp = quantityTmp.replace(',', '.');
					}
				</#if>
				<#if locale == 'vi'>
					if (typeof qohTmp == 'string') {
						qohTmp = qohTmp.replace(',', '.');
					}
				</#if>
		    	<#if item.quantityOnHandTotal?has_content>
					var qohTmp = "${item.quantityOnHandTotal?if_exists}";
					qohTmp = parseFloat(qohTmp, null, 3);
					quantityTmp = parseFloat(quantityTmp, null, 3);
					var qty = 0;
					if (qohTmp > quantityTmp) {
						qty = quantityTmp;
					} else {
						qty = qohTmp;
					}
					item.qoh = qohTmp;
					item.quantity = qty;
		   		<#else>
			    	item.qoh = 0;
			    	item.quantity = 0;
			    </#if>
				item.createdQuantity = parseFloat(quantityTmp, null, 3);
		    </#if>
		    <#if item.unitPrice?has_content>
				var priceTmp = "${item.unitPrice?if_exists}";
				<#if locale == 'vi'>
					if (typeof priceTmp == 'string') {
						priceTmp = priceTmp.replace(',', '.');
					}
				</#if>
				item.unitPrice = parseFloat(priceTmp, null, 3);
			</#if>
			listProductSelected.push(item);
		</#list>
	</#if>
	
	<#assign originContactMechId = delivery.originContactMechId?if_exists>;
	<#assign destContactMechId = delivery.destContactMechId?if_exists>;
	<#assign destAddress = "">;
	<#assign originAddress = "">;
	
	<#if originContactMechId?exists> 
		<#assign address1 = delegator.findOne("PostalAddressFullNameDetail", {"contactMechId" : originContactMechId?if_exists}, false)/>
		<#if address1?exists>
			<#assign originAddress = address1.fullName?if_exists>;
		</#if>
	</#if>
	
	<#if destContactMechId?exists> 
		<#assign address2 = delegator.findOne("PostalAddressFullNameDetail", {"contactMechId" : destContactMechId?if_exists}, false)/>
		<#if address2?exists>
			<#assign destAddress = address2.fullName?if_exists>;
		</#if>
	</#if>
	
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
	
	var delivery = {};
	delivery['deliveryId'] = '${delivery.deliveryId?if_exists}';
	delivery['deliveryTypeId'] = '${delivery.deliveryTypeId?if_exists}';
	delivery['originFacilityId'] = '${delivery.originFacilityId?if_exists}';
	delivery['destFacilityId'] = '${delivery.destFacilityId?if_exists}';
	delivery['estimatedBudget'] = '${delivery.estimatedBudget?if_exists}';
	delivery['currencyUomId'] = '${delivery.currencyUomId?if_exists}';	
	delivery['statusId'] = '${delivery.statusId?if_exists}';	
	delivery['description'] = '${delivery.description?if_exists}';
	delivery['entryDate'] = '${delivery.entryDate?if_exists}';
	
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
	
	uiLabelMap.ExpireDateMustBeAfterNow = "${StringUtil.wrapString(uiLabelMap.ExpireDateMustBeAfterNow)}";
	uiLabelMap.ExpireDateMustBeBeforeManufactureDate = "${StringUtil.wrapString(uiLabelMap.ExpireDateMustBeBeforeManufactureDate)}";
	uiLabelMap.For = "${StringUtil.wrapString(uiLabelMap.For)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.ActualExportedQuantity = "${StringUtil.wrapString(uiLabelMap.ActualExportedQuantity)}";
	uiLabelMap.CannotGreaterQOHNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterQOHNumber)}";
	uiLabelMap.AreYouSureYouWantToExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureYouWantToExport)}";
	uiLabelMap.RequiredNumberSum = "${StringUtil.wrapString(uiLabelMap.RequiredNumberSum)}";
	uiLabelMap.BatchSum = "${StringUtil.wrapString(uiLabelMap.BatchSum)}";
	uiLabelMap.ExpiredDateSum = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}";
	uiLabelMap.ManufacturedDateSum = "${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)}";
	uiLabelMap.ActualDeliveryQuantitySum = "${StringUtil.wrapString(uiLabelMap.ActualDeliveryQuantitySum)}";
	
	uiLabelMap.BLNotHasInventory = "${StringUtil.wrapString(uiLabelMap.BLNotHasInventory)}";
	uiLabelMap.BLMissingInventory = "${StringUtil.wrapString(uiLabelMap.BLMissingInventory)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	uiLabelMap.BLQuantityPromoSum = "${StringUtil.wrapString(uiLabelMap.BLQuantityPromoSum)}";
	uiLabelMap.BLSLKM = "${StringUtil.wrapString(uiLabelMap.BLSLKM)}";
	uiLabelMap.UnitSum = "${StringUtil.wrapString(uiLabelMap.UnitSum)}";
	uiLabelMap.CannotGreaterPromoNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterPromoNumber)}";
	uiLabelMap.CannotGreaterCreatedNotPromoNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterCreatedNotPromoNumber)}";
	uiLabelMap.QuantitySumTotal = "${StringUtil.wrapString(uiLabelMap.QuantitySumTotal)}";
	uiLabelMap.BLQuantityEATotal = "${StringUtil.wrapString(uiLabelMap.BLQuantityEATotal)}";
	uiLabelMap.NoDataToDisplay = "${StringUtil.wrapString(uiLabelMap.NoDataToDisplay)}";
	
</script>
<script type="text/javascript" src="/logresources/js/delivery/transferDeliveryExportTotal.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>