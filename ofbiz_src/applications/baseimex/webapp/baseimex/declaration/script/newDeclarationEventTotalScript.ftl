<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasCore=true hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	var productEventTypeData = [];
	
	var listProductSelected = [];
	
	<#if parameters.parentEventTypeId?has_content>
		<#assign qualityTestTypes = delegator.findList("ProductEventType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", parameters.parentEventTypeId?if_exists), null, null, null, true) />
	<#else>
		<#assign qualityTestTypes = delegator.findList("ProductEventType", null, null, null, null, true) />
	</#if>
	<#if qualityTestTypes?has_content>
		<#list qualityTestTypes as item>
			var item = {
				eventTypeId: '${item.eventTypeId?if_exists}',
				description: '${StringUtil.wrapString(item.get('description', locale)?if_exists)}'
			}
			productEventTypeData.push(item);
		</#list>
	</#if>
	
	var getQuotaTypeDesc = function (eventTypeId) {
		for (var i in productEventTypeData) {
			var x = productEventTypeData[i];
			if (x.eventTypeId == eventTypeId) {
				return x.description;
			}
		}
		return eventTypeId;
	}
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PRODUCT_EVENT_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData.push(row);
	</#list>
	
	
	
	var getStatusDesc = function (statusId) {
		for (var i in statusData) {
			var x = statusData[i];
			if (x.statusId == statusId) {
				return x.description;
			}
		}
	
		return statusId;
	}
	
	
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	function getUomDesc(uomId) {
		for (var i = 0; i < weightUomData.length; i ++) {
			if (weightUomData[i].uomId == uomId) {
				return weightUomData[i].description;
			}
		}
		for (var i = 0; i < uomData.length; i ++) {
			if (uomData[i].uomId == uomId) {
				return uomData[i].description;
			}
		}
		return uomId;
	}
	
	var locale = '${locale}';

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";

	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.BIEListQuotaHeaders = "${StringUtil.wrapString(uiLabelMap.BIEListQuotaHeaders)}";
	uiLabelMap.BIEQuotaId = "${StringUtil.wrapString(uiLabelMap.BIEQuotaId)}";
	uiLabelMap.BIEQuotaName = "${StringUtil.wrapString(uiLabelMap.BIEQuotaName)}";
	uiLabelMap.BPSupplier = "${StringUtil.wrapString(uiLabelMap.Supplier)}";
	uiLabelMap.CreatedDate = "${StringUtil.wrapString(uiLabelMap.CreatedDate)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	uiLabelMap.ValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanZero)}";
	uiLabelMap.BIECurrentQuota = "${StringUtil.wrapString(uiLabelMap.BIECurrentQuota)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.BIECurrentQuota = "${StringUtil.wrapString(uiLabelMap.BIECurrentQuota)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.CommonPartyId = "${StringUtil.wrapString(uiLabelMap.CommonPartyId)}";
	uiLabelMap.CommonPartyName = "${StringUtil.wrapString(uiLabelMap.CommonPartyName)}";
	uiLabelMap.FromDate = "${StringUtil.wrapString(uiLabelMap.FromDate)}";
	uiLabelMap.ThruDate = "${StringUtil.wrapString(uiLabelMap.ThruDate)}";
	uiLabelMap.BIETimeRangeNotTrue = "${StringUtil.wrapString(uiLabelMap.BIETimeRangeNotTrue)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.CreatedBy = "${StringUtil.wrapString(uiLabelMap.CreatedBy)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.NotChosenDateManu = "${StringUtil.wrapString(uiLabelMap.NotChosenDateManu)}";
	uiLabelMap.ExistsProductNotDate = "${StringUtil.wrapString(uiLabelMap.ExistsProductNotDate)}";
	uiLabelMap.SaveAndConPL = "${StringUtil.wrapString(uiLabelMap.SaveAndConPL)}";
	uiLabelMap.LoadFailPO = "${StringUtil.wrapString(uiLabelMap.LoadFailPO)}";
	uiLabelMap.ClearPL = "${StringUtil.wrapString(uiLabelMap.ClearPL)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BIEContainerId = "${StringUtil.wrapString(uiLabelMap.BIEContainerId)}";
	uiLabelMap.BIEBillId = "${StringUtil.wrapString(uiLabelMap.BIEBillId)}";
	uiLabelMap.BIEBillNumber = "${StringUtil.wrapString(uiLabelMap.BIEBillNumber)}";
	uiLabelMap.BIEContainer = "${StringUtil.wrapString(uiLabelMap.BIEContainer)}";
	uiLabelMap.BIEContainerNumber = "${StringUtil.wrapString(uiLabelMap.BIEContainerNumber)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.BIEContainerType = "${StringUtil.wrapString(uiLabelMap.BIEContainerType)}";
	uiLabelMap.BIEDepartureDate = "${StringUtil.wrapString(uiLabelMap.BIEDepartureDate)}";
	uiLabelMap.BIEArrivalDate = "${StringUtil.wrapString(uiLabelMap.BIEArrivalDate)}";
	
	
	uiLabelMap.OrderPO = "${StringUtil.wrapString(uiLabelMap.OrderPO)}";
	uiLabelMap.BIEVendorInvoiceNum = "${StringUtil.wrapString(uiLabelMap.BIEVendorInvoiceNum)}";
	uiLabelMap.BIEVendorOrderNum = "${StringUtil.wrapString(uiLabelMap.BIEVendorOrderNum)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.BIENetWeight = "${StringUtil.wrapString(uiLabelMap.BIENetWeight)}";
	uiLabelMap.BIEGrossWeight = "${StringUtil.wrapString(uiLabelMap.BIEGrossWeight)}";
	uiLabelMap.BIEInvoiceDate = "${StringUtil.wrapString(uiLabelMap.BIEInvoiceDate)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.globalTradeItemNumber = "${StringUtil.wrapString(uiLabelMap.globalTradeItemNumber)}";
	uiLabelMap.batchNumber = "${StringUtil.wrapString(uiLabelMap.batchNumber)}";
	uiLabelMap.orderUnits = "${StringUtil.wrapString(uiLabelMap.orderUnits)}";
	uiLabelMap.orderUomId = "${StringUtil.wrapString(uiLabelMap.orderUomId)}";
	uiLabelMap.originOrderUnit = "${StringUtil.wrapString(uiLabelMap.originOrderUnit)}";
	uiLabelMap.dateOfManufacture = "${StringUtil.wrapString(uiLabelMap.dateOfManufacture)}";
	uiLabelMap.ProductExpireDate = "${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}";
	uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.AddDetailPL = "${StringUtil.wrapString(uiLabelMap.AddDetailPL)}";
	uiLabelMap.AddPL = "${StringUtil.wrapString(uiLabelMap.AddPL)}";
	uiLabelMap.AddProduct = "${StringUtil.wrapString(uiLabelMap.BLAddProducts)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BIEContainerId = "${StringUtil.wrapString(uiLabelMap.BIEContainerId)}";
	uiLabelMap.BIEBillId = "${StringUtil.wrapString(uiLabelMap.BIEBillId)}";
	uiLabelMap.BIEBillNumber = "${StringUtil.wrapString(uiLabelMap.BIEBillNumber)}";
	uiLabelMap.BIEContainer = "${StringUtil.wrapString(uiLabelMap.BIEContainer)}";
	uiLabelMap.BIEContainerNumber = "${StringUtil.wrapString(uiLabelMap.BIEContainerNumber)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.BIEContainerType = "${StringUtil.wrapString(uiLabelMap.BIEContainerType)}";
	uiLabelMap.BIEDepartureDate = "${StringUtil.wrapString(uiLabelMap.BIEDepartureDate)}";
	uiLabelMap.BIEArrivalDate = "${StringUtil.wrapString(uiLabelMap.BIEArrivalDate)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.testedDocument = "${StringUtil.wrapString(uiLabelMap.testedDocument)}";
	uiLabelMap.quarantineDocument = "${StringUtil.wrapString(uiLabelMap.quarantineDocument)}";
	
	uiLabelMap.BLQuantityUse = "${StringUtil.wrapString(uiLabelMap.BLQuantityUse)}";
	uiLabelMap.BLQuantityRegistered = "${StringUtil.wrapString(uiLabelMap.BLQuantityRegistered)}";
	uiLabelMap.BLQuantityGreateThanQuantityAvailable = "${StringUtil.wrapString(uiLabelMap.BLQuantityGreateThanQuantityAvailable)}";
	uiLabelMap.FromDateMustBeAfterNow = "${StringUtil.wrapString(uiLabelMap.FromDateMustBeAfterNow)}";
	uiLabelMap.FromDateMustBeBeforeThruDate = "${StringUtil.wrapString(uiLabelMap.FromDateMustBeBeforeThruDate)}";
	uiLabelMap.ThruDateMustBeAfterNow = "${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterNow)}";
	uiLabelMap.MissingFromDate = "${StringUtil.wrapString(uiLabelMap.MissingFromDate)}";
	uiLabelMap.MissingThruDate = "${StringUtil.wrapString(uiLabelMap.MissingThruDate)}";
	uiLabelMap.WrongFormat = "${StringUtil.wrapString(uiLabelMap.WrongFormat)}";
	
	
</script>
<script type="text/javascript" src="/imexresources/js/declaration/newDeclarationEventTotal.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>