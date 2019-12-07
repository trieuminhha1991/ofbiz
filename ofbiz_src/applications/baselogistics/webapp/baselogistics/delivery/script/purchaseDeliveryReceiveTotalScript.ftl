<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasCore=true/>
<script type="text/javascript">
	var locale = "${locale?if_exists}";
	var listProductSelected = [];
	var listProductMap = [];
	
	var hidePrice = true;
	<#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
		hidePrice = false;
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
		
	<#assign deliveryId = parameters.deliveryId?if_exists/>
	var deliveryId = "${deliveryId?if_exists}";
	<#assign delivery = delegator.findOne("Delivery", {"deliveryId" : parameters.deliveryId?if_exists}, false)/>
	<#assign orderId = delivery.orderId?if_exists/>
	
	<#assign orderHeader = delegator.findOne("OrderHeader", {"orderId" : orderId?if_exists}, false)/>
	var currencyUomId = "${orderHeader.currencyUom?if_exists}";
	
	var listDeliveryItemSelected = [];
	var curStatusId = '${delivery.statusId?if_exists}';
	var destFacilityId = '${delivery.destFacilityId?if_exists}';
	
	<#assign destFacility = delegator.findOne("Facility", {"facilityId" : delivery.destFacilityId?if_exists}, false)/>
	<#if delivery.destContactMechId?has_content>
		<#assign destAddress = delegator.findOne("PostalAddressFullNameDetail", {"contactMechId" : delivery.destContactMechId?if_exists}, false)/>
	</#if>
	
	<#assign requireDate = false>
	<#if destFacility?has_content>
		<#assign requireDate = destFacility.requireDate?if_exists>
	</#if>
	
	var createdDate = "${delivery.createDate}";
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
	
	<#assign status = delegator.findOne("StatusItem", {"statusId" : delivery.statusId?if_exists}, false)/>
	<#assign statusDesc = StringUtil.wrapString(status.get('description', locale)?if_exists)/>
	
	<#assign deliveryItems = delegator.findList("DeliveryItemView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("deliveryId", deliveryId), null, null, null, false)/>
	
	<#if deliveryItems?has_content>
		<#list deliveryItems as item>
			var item = {};
			<#assign orderItem = delegator.findOne("OrderItem", {"orderId" : orderId?if_exists, "orderItemSeqId": item.fromOrderItemSeqId?if_exists}, false)!/>
			var orderQuantityUomId = "${orderItem.quantityUomId?if_exists}";
			var quantityUomId = "${item.quantityUomId?if_exists}";
			var convertNumber = 1;
			if (orderQuantityUomId != quantityUomId) {
				<#assign convertNumber = Static["com.olbius.product.util.ProductUtil"].getConvertPackingNumber(delegator, item.productId?if_exists, orderItem.quantityUomId?if_exists, item.quantityUomId?if_exists)!>
				convertNumber = parseFloat("${convertNumber?if_exists}");
			}
			item.deliveryId = "${item.deliveryId?if_exists}";
			item.deliveryItemSeqId = "${item.deliveryItemSeqId?if_exists}";
			item.productId = "${item.productId?if_exists}";
			item.orderId = "${item.fromOrderId?if_exists}";
			item.orderItemSeqId = "${item.fromOrderItemSeqId?if_exists}";
			item.productCode = "${item.productCode?if_exists}";
			item.productName = "${StringUtil.wrapString(item.productName?if_exists)}";
			item.description = "${StringUtil.wrapString(item.description?if_exists)}";
			item.quantityUomId = quantityUomId;
			item.orderQuantityUomId = orderQuantityUomId;
			item.weightUomId = "${item.weightUomId?if_exists}";
			item.isPromo = "${item.isPromo?if_exists}";
			item.convertNumber = convertNumber;
			item.requireAmount = "${item.requireAmount?if_exists}";
			var unitPrice = '${item.unitPrice?if_exists}';
			<#if locale == 'vi'>
				if (typeof unitPrice == 'string') {
					unitPrice = unitPrice.replace('.', '');
					unitPrice = unitPrice.replace(',', '.');
				}
			</#if>
			var quantity = 0;
			var requiredQuantity = 0;
		    <#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId?has_content && item.amountUomTypeId == 'WEIGHT_MEASURE'>
				var quantityTmp = "${item.amount?if_exists}";
				<#if locale == 'vi'>
					quantityTmp = quantityTmp.replace('.', '');
					quantityTmp = quantityTmp.replace(',', '.');
				</#if>
				item.amountUomTypeId = "${item.amountUomTypeId?if_exists}";
				quantity = parseFloat(quantityTmp, null, 3);
				requiredQuantity = parseFloat(quantityTmp, null, 3);
				unitPrice = '${item.alternativeUnitPrice?if_exists/item.selectedAmount}';
		    <#else>
			    var quantityTmp = "${item.quantity?if_exists}";
				<#if locale == 'vi'>
					quantityTmp = quantityTmp.replace('.', '');
					quantityTmp = quantityTmp.replace(',', '.');
				</#if>
				quantity = parseFloat(quantityTmp, null, 3);
				requiredQuantity = parseFloat(quantityTmp, null, 3);
		    </#if>
	     	var quantityQC = 0;
		    var quantityEA = 0;
		    if (orderQuantityUomId != quantityUomId && convertNumber >= 1){
			    quantityQC = quantity/convertNumber;
			    quantityEA = quantity - quantityQC * convertNumber;
		    } else {
		    	quantityEA = quantity;
		    }
		    item.quantityQC = quantityQC;
		    item.quantityEA = quantityEA;
		    item.quantity = quantity;
		    item.requiredQuantity = requiredQuantity;
		    item.unitPrice = parseFloat(unitPrice, null, 3);
			listProductSelected.push(item);
		</#list>
	</#if>
	var countItem = '${deliveryItems.size()}';
	
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
	delivery['originFacilityId'] = '${delivery.originFacilityId?if_exists}';
	delivery['destFacilityId'] = '${delivery.destFacilityId?if_exists}';
	delivery['createDate'] = '${delivery.createDate?if_exists}';
	delivery['statusId'] = '${delivery.statusId?if_exists}';	
	delivery['description'] = '${delivery.description?if_exists}';
	
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
	
	uiLabelMap.BLQuantityEATotal = "${StringUtil.wrapString(uiLabelMap.BLQuantityEATotal)}";
	uiLabelMap.BLQuantityByQCUom = "${StringUtil.wrapString(uiLabelMap.BLQuantityByQCUom)}";
	uiLabelMap.BLQuantityByEAUom = "${StringUtil.wrapString(uiLabelMap.BLQuantityByEAUom)}";
	uiLabelMap.BLPackingForm = "${StringUtil.wrapString(uiLabelMap.BLPackingForm)}";
	uiLabelMap.NoDataToDisplay = "${StringUtil.wrapString(uiLabelMap.NoDataToDisplay)}";
</script>
<script type="text/javascript" src="/logresources/js/delivery/purchaseDeliveryReceiveTotal.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>