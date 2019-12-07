
<script>

	var transferId = "${parameters.transferId?if_exists}"
	var listProductSelected = [];
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	var quantityUomData = new Array();
	<#list quantityUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		quantityUomData.push(row);
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}";
		weightUomData.push(row);
	</#list>
	
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
</script>

<#assign orderCancelReason = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_CANCEL_CODE"}, null, false)!/>
<script type="text/javascript">
<#assign transferId = parameters.transferId?if_exists/>
<#assign transfer = delegator.findOne("TransferHeader", {"transferId" : transferId}, false)!>
<#assign checkFromStk = Static["com.olbius.baselogistics.util.LogisticsPartyUtil"].checkStorekeeperOfFacility(delegator, userLogin.partyId?if_exists, transfer.originFacilityId?if_exists)/>
<#assign checkToStk = Static["com.olbius.baselogistics.util.LogisticsPartyUtil"].checkStorekeeperOfFacility(delegator, userLogin.partyId?if_exists, transfer.destFacilityId?if_exists)/>
<#assign transferStatus = delegator.findList("TransferStatus", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId?if_exists, "statusId", "TRANSFER_DELIVERED")), null, null, null, false) />
<#assign itemShipGroup = delegator.findList("TransferItemShipGroup",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId?if_exists)), null, null, null, false) />
<#if itemShipGroup?has_content>
	<#assign originFacilityData = delegator.findOne("PostalAddressFullNameDetail", { "contactMechId" : itemShipGroup[0].originContactMechId?if_exists } , false)! />
	<#assign destFacilityData = delegator.findOne("PostalAddressFullNameDetail", { "contactMechId" : itemShipGroup[0].destContactMechId?if_exists } , false)! />
	<#assign originFacility = delegator.findOne("Facility", {"facilityId" : transfer.originFacilityId?if_exists}, false)! />
	<#assign destFacility = delegator.findOne("Facility", {"facilityId" : transfer.destFacilityId?if_exists}, false)! />
	<#if originFacilityData?has_content>
		var originFacilityData = {
			facilityId : "${StringUtil.wrapString(originFacility.facilityId?if_exists)}",
			facilityCode : "${StringUtil.wrapString(originFacility.facilityCode?if_exists)}",
			facilityName : "${StringUtil.wrapString(originFacility.facilityName?if_exists)}",
			address : "${StringUtil.wrapString(originFacilityData.address1?if_exists)}",
			contactMechId : "${StringUtil.wrapString(originFacilityData.contactMechId?if_exists)}"
		}
	</#if>
	<#if destFacilityData?has_content>
		var destFacilityData = {
			facilityId : "${StringUtil.wrapString(destFacility.facilityId?if_exists)}",
			facilityCode : "${StringUtil.wrapString(destFacility.facilityCode?if_exists)}",
			facilityName : "${StringUtil.wrapString(destFacility.facilityName?if_exists)}",
			address : "${StringUtil.wrapString(destFacilityData.address1?if_exists)}",
			contactMechId : "${StringUtil.wrapString(destFacilityData.contactMechId?if_exists)}"
		}
	</#if>
</#if>


<#if transferStatus?has_content>
	<#assign statusDatetime = transferStatus.get(0).get('statusDatetime')?if_exists />
</#if>

	var transfer = {
		originalFacilityId: "${StringUtil.wrapString(transfer.originFacilityId?if_exists)}",
		destFacilityId: "${StringUtil.wrapString(transfer.destFacilityId?if_exists)}",
		transferType: "${StringUtil.wrapString(transfer.transferTypeId?if_exists)}",
		description: "${StringUtil.wrapString(transfer.description?if_exists)}"
	}

	var statusDatetime = '${statusDatetime?if_exists}';
	var transferDate = null;
	var shipBeforeDate = null;
	var shipAfterDate = null;
	<#if transfer.transferDate?exists>
		transferDate = '${transfer.transferDate?if_exists}';
	<#else>
		<#if transfer.shipAfterDate?exists>
			shipAfterDate = '${transfer.shipAfterDate}';
		</#if>
		<#if transfer.shipBeforeDate?exists>
			shipBeforeDate = '${transfer.shipBeforeDate}';
		</#if>
	</#if>


if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
uiLabelMap.AreYouSureEdit = "${StringUtil.wrapString(uiLabelMap.AreYouSureEdit)}";
uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
uiLabelMap.AreYouSureReject = "${StringUtil.wrapString(uiLabelMap.AreYouSureReject)}";
uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
uiLabelMap.ProductProductId = "${StringUtil.wrapString(uiLabelMap.ProductProductId)}";
uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
uiLabelMap.RequiredNumberSum = "${StringUtil.wrapString(uiLabelMap.RequiredNumberSum)}";
uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
uiLabelMap.EXPRequired = "${StringUtil.wrapString(uiLabelMap.EXPRequired)}";
uiLabelMap.QuantityDelivered = "${StringUtil.wrapString(uiLabelMap.QuantityDelivered)}";
uiLabelMap.QuantityShipping = "${StringUtil.wrapString(uiLabelMap.QuantityShipping)}";
uiLabelMap.QuantityScheduled = "${StringUtil.wrapString(uiLabelMap.QuantityScheduled)}";
uiLabelMap.QuantityRemain = "${StringUtil.wrapString(uiLabelMap.QuantityRemain)}";
uiLabelMap.NotRequiredExpiredDate = "${StringUtil.wrapString(uiLabelMap.NotRequiredExpiredDate)}";
uiLabelMap.BLSGCMustNotInputNegativeValue = "${StringUtil.wrapString(uiLabelMap.BLSGCMustNotInputNegativeValue)}";
uiLabelMap.BPSearchProductToAdd = "${StringUtil.wrapString(uiLabelMap.BPSearchProductToAdd)}";
uiLabelMap.BPProductNotFound = "${StringUtil.wrapString(uiLabelMap.BPProductNotFound)}";
uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
var getLocalization = function () {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};

</script>

<@jqGridMinimumLib />
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
	</#if>
	var localeStr = '${localeStr}';
	<#assign company = Static['com.olbius.basehr.util.MultiOrganizationUtil'].getCurrentOrganization(delegator, userLogin.get('userLoginId'))! />;
	if (transferTypeData === undefined) {
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
	}
	
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
	uiLabelMap.PleaseConfigShipmentMethodForSalesChannel = "${uiLabelMap.PleaseConfigShipmentMethodForSalesChannel}";
	uiLabelMap.BLProductStoreId = "${uiLabelMap.BLProductStoreId}";
	uiLabelMap.BLStoreName = "${uiLabelMap.BLStoreName}";
	uiLabelMap.CannotTransferToItSelf = "${uiLabelMap.CannotTransferToItSelf}";
	uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
	uiLabelMap.ShipBeforeDate = "${uiLabelMap.ShipBeforeDate}";
	uiLabelMap.ShipAfterDate = "${uiLabelMap.ShipAfterDate}";
	uiLabelMap.UnitPrice = "${uiLabelMap.UnitPrice}";
	uiLabelMap.ValueMustBeGreaterThanZero = "${uiLabelMap.ValueMustBeGreaterThanZero}";
	uiLabelMap.AfterStartDate = "${uiLabelMap.AfterStartDate}";
	uiLabelMap.BeforeEndDate = "${uiLabelMap.BeforeEndDate}";
</script>
<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js?v=1.0.0"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.validator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/logresources/js/transfer/transferSearchProduct.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/transfer/transferEditTransferInfo.js"></script>
<script type="text/javascript" src="/logresources/js/transfer/transferEditTransfer.js"></script>
<script type="text/javascript" src="/logresources/js/transfer/transferEditTransferTotal.js"></script>
