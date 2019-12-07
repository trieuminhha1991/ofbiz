<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	var listProductSelected = [];
	var facilitySelected = null;
	var shippingSelected = null;
	var agreementSelected = null;
	var containerParamId = null;
	<#if parameters.containerId?has_content>
		containerParamId = '${parameters.containerId?if_exists}';
	</#if>
	
	var containerTypeData = [];
	<#assign containerTypes = delegator.findList("ContainerType", null, null, null, null, true) />
	
	<#if containerTypes?has_content>
		<#list containerTypes as item>
			var item = {
				containerTypeId: '${item.containerTypeId?if_exists}',
				description: '${StringUtil.wrapString(item.get('description', locale)?if_exists)}'
			}
			containerTypeData.push(item);
		</#list>
	</#if>
	
	var getContainerTypeDesc = function (containerTypeId) {
		for (var i in containerTypeData) {
			var x = containerTypeData[i];
			if (x.containerTypeId == containerTypeId) {
				return x.description;
			}
		}
		return containerTypeId;
	}
	
	
	<#assign agreeStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false)/>
	var agreeStatusData = [];
	<#list agreeStatuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		agreeStatusData.push(row);
	</#list>
	
	var getStatusDesc = function (statusId) {
		for (var i in agreeStatusData) {
			var x = agreeStatusData[i];
			if (x.statusId == statusId) {
				return x.description;
			}
		}
		return statusId;
	}
	
	var locale = '${locale}';

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.packingListNumber = "${StringUtil.wrapString(uiLabelMap.packingListNumber)}";
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
	
	
	uiLabelMap.BIEPackingListId = "${StringUtil.wrapString(uiLabelMap.BIEPackingListId)}";
	uiLabelMap.BIEAgreementId = "${StringUtil.wrapString(uiLabelMap.BIEAgreementId)}";
	uiLabelMap.OrderPO = "${StringUtil.wrapString(uiLabelMap.OrderPO)}";
	uiLabelMap.BIEVendorInvoiceNum = "${StringUtil.wrapString(uiLabelMap.BIEVendorInvoiceNum)}";
	uiLabelMap.BIEVendorOrderNum = "${StringUtil.wrapString(uiLabelMap.BIEVendorOrderNum)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.BIENetWeight = "${StringUtil.wrapString(uiLabelMap.BIENetWeight)}";
	uiLabelMap.BIEGrossWeight = "${StringUtil.wrapString(uiLabelMap.BIEGrossWeight)}";
	uiLabelMap.BIEPackingListDate = "${StringUtil.wrapString(uiLabelMap.BIEPackingListDate)}";
	uiLabelMap.BIEInvoiceDate = "${StringUtil.wrapString(uiLabelMap.BIEInvoiceDate)}";
	uiLabelMap.BIEPackingList = "${StringUtil.wrapString(uiLabelMap.BIEPackingList)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.globalTradeItemNumber = "${StringUtil.wrapString(uiLabelMap.globalTradeItemNumber)}";
	uiLabelMap.batchNumber = "${StringUtil.wrapString(uiLabelMap.batchNumber)}";
	uiLabelMap.packingUnits = "${StringUtil.wrapString(uiLabelMap.packingUnits)}";
	uiLabelMap.packingUomId = "${StringUtil.wrapString(uiLabelMap.packingUomId)}";
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
	
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	
	uiLabelMap.FacilityId = "${StringUtil.wrapString(uiLabelMap.FacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	
	uiLabelMap.CommonId = "${StringUtil.wrapString(uiLabelMap.CommonId)}";
	uiLabelMap.CommonName = "${StringUtil.wrapString(uiLabelMap.CommonName)}";
	uiLabelMap.AgreementId = "${StringUtil.wrapString(uiLabelMap.AgreementId)}";
	uiLabelMap.AgreementName = "${StringUtil.wrapString(uiLabelMap.AgreementName)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	
</script>
<script type="text/javascript" src="/imexresources/js/import/createImportDocumentTotal.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>