<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasCore=true/>
<script type="text/javascript">
	var locale = "${locale?if_exists}";
	
	var hidePrice = true;
	<#if hasOlbPermission("MODULE", "RETURN_PRICE_SO", "VIEW")>
		hidePrice = false;
	</#if>
	var listProductSelected = [];
	var listProductMap = [];
	var facilitySelected = null;
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
		
	<#assign returnId = parameters.returnId?if_exists/>
	var returnId = "${returnId?if_exists}";
	<#assign returnHeader = delegator.findOne("ReturnHeaderDetail", {"returnId" : parameters.returnId?if_exists}, false)!/>
	var listReturnItemSelected = [];
	var returnTypeId = '${returnHeader.returnTypeId?if_exists}';
	var curStatusId = '${returnHeader.statusId?if_exists}';
	var facilityId = '${returnHeader.facilityId?if_exists}';
	
	<#assign originFacility = delegator.findOne("Facility", {"facilityId" : returnHeader.destinationFacilityId?if_exists}, false)!/>
	
	<#assign requireDate = "N">
	<#if originFacility?exists && originFacility?has_content>
		facilitySelected = {
			facilityId: '${originFacility.facilityId?if_exists}',
			facilityCode: '${originFacility.facilityCode?if_exists}',
			facilityName: '${StringUtil.wrapString(originFacility.get("facilityName", locale))}',
		};
		<#assign requireDate = originFacility.requireDate?if_exists>
	</#if>
	
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
	
	<#assign returnStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_RETURN_STTS"), null, null, null, false) />
	var statusData = new Array();
	<#list returnStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		statusData.push(row);
	</#list>
	
	<#assign returnType = delegator.findOne("ReturnHeaderType", {"returnHeaderTypeId" : returnHeader.returnHeaderTypeId?if_exists}, false)/>
	<#assign returnHeaderTypeId = returnHeader.returnHeaderTypeId?if_exists/>
	<#assign returnHeaderTypeDesc = StringUtil.wrapString(returnType.get('description', locale)?if_exists)/>
	
	<#assign status = delegator.findOne("StatusItem", {"statusId" : returnHeader.statusId?if_exists}, false)/>
	<#assign statusDesc = StringUtil.wrapString(status.get('description', locale)?if_exists)/>
	
	<#assign returnItems = delegator.findList("ReturnItemDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("returnId", returnId), null, null, null, false)/>
	
	<#if returnItems?has_content>
		<#list returnItems as item>
			var item = {};
			item.returnId = "${item.returnId?if_exists}";
			item.returnItemSeqId = "${item.returnItemSeqId?if_exists}";
			item.productId = "${item.productId?if_exists}";
			item.productCode = "${item.productCode?if_exists}";
			item.productName = "${StringUtil.wrapString(item.productName?if_exists)}";
			item.description = "${StringUtil.wrapString(item.description?if_exists)}";
			item.quantityUomId = "${item.quantityUomId?if_exists}";
			item.weightUomId = "${item.weightUomId?if_exists}";
			item.requireAmount = "${item.requireAmount?if_exists}";
			var returnPrice = '${item.returnPrice?if_exists}';
			<#if locale == 'vi'>
				if (typeof returnPrice == 'string') {
					returnPrice = returnPrice.replace('.', '');
					returnPrice = returnPrice.replace(',', '.');
				}
			</#if>
		    item.returnPrice = parseFloat(returnPrice, null, 3);
		    <#if item.requireAmount?has_content && item.requireAmount == 'Y'>
				var quantityTmp = "${item.returnAmount?if_exists}";
				<#if item.receivedAmount?has_content>
					var rc = "${item.receivedAmount?if_exists}"
					quantityTmp = parseFloat(quantityTmp, null, 3) - parseFloat(rc, null, 3);
					item.receivedQuantity = "${item.receivedAmount?if_exists}";
				</#if>
				<#if locale == 'vi'>
					quantityTmp = quantityTmp.replace('.', '');
					quantityTmp = quantityTmp.replace(',', '.');
				</#if>
				item.quantity = parseFloat(quantityTmp, null, 3);
				item.returnQuantity = parseFloat(quantityTmp, null, 3);
		    <#else>
		    	var quantityTmp = "${item.returnQuantity?if_exists}";
			    <#if item.receivedQuantity?has_content>
			    	var rc = "${item.receivedQuantity?if_exists}"
					quantityTmp = parseFloat(quantityTmp, null, 3) - parseFloat(rc, null, 3);
					item.receivedQuantity = "${item.receivedQuantity?if_exists}";
				</#if>
				item.quantity = quantityTmp;
				item.returnQuantity = "${item.returnQuantity?if_exists}";
		    </#if>
			listProductSelected.push(item);
		</#list>
	</#if>
	var countItem = '${returnItems.size()}';
	
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
	
	var returnHeader = {};
	returnHeader['returnId'] = '${returnHeader.returnId?if_exists}';
	returnHeader['returnHeaderTypeId'] = '${returnHeader.returnHeaderTypeId?if_exists}';
	returnHeader['facilityId'] = '${returnHeader.facilityId?if_exists}';
	returnHeader['currencyUomId'] = '${returnHeader.currencyUomId?if_exists}';	
	returnHeader['statusId'] = '${returnHeader.statusId?if_exists}';	
	returnHeader['description'] = '${returnHeader.description?if_exists}';
	returnHeader['entryDate'] = '${returnHeader.description?if_exists}';
	returnHeader['createdBy'] = '${returnHeader.createdBy?if_exists}';
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureExport)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.CommonOK)}";
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
	uiLabelMap.BLReceivedNumberSum = "${StringUtil.wrapString(uiLabelMap.BLReceivedNumberSum)}";
	uiLabelMap.UnitSum = "${StringUtil.wrapString(uiLabelMap.UnitSum)}";
	uiLabelMap.BLCannotGreaterThanNotReceivedQuantity = "${StringUtil.wrapString(uiLabelMap.BLCannotGreaterThanNotReceivedQuantity)}";
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	uiLabelMap.YouNotYetChooseFacility = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseFacility)}";
	uiLabelMap.NoDataToDisplay = "${StringUtil.wrapString(uiLabelMap.NoDataToDisplay)}";
</script>
<script type="text/javascript" src="/logresources/js/return/custReturnReceiveReturnTotal.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>