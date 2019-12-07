<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js?v=001"></script>
<style type="text/css">
	.bootbox{
	    z-index: 990009 !important;
	}
	.modal-backdrop{
	    z-index: 890009 !important;
	}
	.loading-container{
		z-index: 999999 !important;
	}
</style>
<script>
	var facByCustomer = [];
</script>
<#include 'deliveryPrepareScript.ftl'>
<script>
	
	
	var listImage = [];
	var pathScanFile = null;
	
	var listImageExpt = [];
	var pathScanFileExpt = null;
	
	<#assign orderHeader = delegator.findOne("OrderHeader", {"orderId" : parameters.orderId?if_exists}, false)/>
	var originContactData = new Array();
	contactMechPurposeTypeId = null;
	var isStorekeeperFrom = false;
	var isStorekeeperTo = false;
	var isSpecialist = false;
	var checkStorekeeper = false;
	var listInv = [];
    var tmpValue;
    var glDeliveryId;
    var glDelivery;
    var glOriginFacilityId;
    var glDeliveryStatusId;
    var facilitySelected = null;
	var orderId = "${parameters.orderId?if_exists}";
	glOrderId = orderId;
	<#if parameters.orderId?has_content>
		inOrderDetail = true;
	</#if>
	var currencyUom = '${orderHeader.currencyUom?if_exists}';
	var orderStatus = '${orderHeader.statusId}';
	
	var listDeliveryItemData= [];
	
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	originFacilitySelected = null;
	var actualStartDategl;
	var destContactData = new Array();
	var deliveryTypeSource = new Array();
	<#assign orderDateDisplay = StringUtil.wrapString(Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone))>
	<#assign orderItemShipGroups = delegator.findList("OrderItemShipGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId?if_exists)), null, null, null, false) />
    <#if orderHeader?has_content && orderHeader.statusId == "ORDER_COMPLETED">
        var editableDI = false;
    <#else>
        var editableDI = true;
    </#if>
    var productStoreId = "${orderHeader.productStoreId?if_exists}"
    <#assign shipmentMethodIds = []>
    <#if orderItemShipGroups?has_content>
    	<#list orderItemShipGroups as group>
	    	<#assign shipmentMethodIds = shipmentMethodIds + [group.shipmentMethodTypeId?if_exists]>
	    	<#assign destAddress = delegator.findOne("PostalAddressDetail", {"contactMechId" : group.contactMechId?if_exists}, false)!/>
	    	var item = {};
	    	<#assign ctmId = StringUtil.wrapString(destAddress.contactMechId?if_exists) />
	    	<#assign descTmp = StringUtil.wrapString(destAddress.fullName?if_exists) />
	    	item['contactMechId'] = "${ctmId?if_exists}";
	    	item['description'] = "${descTmp?if_exists?replace('\n', ' ')}";
	    	destContactData.push(item);
    	</#list>
    <#else>
    </#if>
    <#assign defaultFacilityId = orderHeader.originFacilityId?if_exists/>
    <#if defaultFacilityId?has_content>
		<#assign facilityTmp = delegator.findOne("Facility", false, {"facilityId", defaultFacilityId?if_exists})>
		facilitySelected = {};
		facilitySelected.facilityId = "${facilityTmp.facilityId?if_exists}";
		facilitySelected.facilityCode = "${facilityTmp.facilityCode?if_exists}";
		facilitySelected.facilityName = "${StringUtil.wrapString(facilityTmp.facilityName?if_exists)}";
	<#else>
		<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", orderHeader.productStoreId?if_exists, "ownerPartyId", company)), null, null, null, false)!>
		<#if facilities?has_content && facilities?length &gt; 0>
			<#list facilities as item>
				facilitySelected = {};
				facilitySelected.facilityId = "${item.facilityId?if_exists}";
				facilitySelected.facilityCode = "${item.facilityCode?if_exists}";
				facilitySelected.facilityName = "${StringUtil.wrapString(item.facilityName?if_exists)}";
				<#break>
			</#list>
		</#if>
	</#if>
    
    var needsCheckShipmentMethod = false;
    var shipmentMethodData = [];
    <#if !shipmentMethodIds[0]?has_content>
    needsCheckShipmentMethod = true;
    <#assign productStoreShipmentMethods = delegator.findList("ProductStoreShipmentMeth", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", orderHeader.productStoreId?if_exists)), null, null, null, false) />
    	<#if productStoreShipmentMethods[0]?has_content>
    		<#assign listIds = []>
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
    		<#if listIds?has_content>
	    		<#list listIds as methId>
					var item = {};
	    			<#assign shipmentMethodType = delegator.findOne("ShipmentMethodType", {"shipmentMethodTypeId" : methId?if_exists}, false)/>
	    			<#assign descMeth = StringUtil.wrapString(shipmentMethodType.get('description', locale)?if_exists) />
	    			item['shipmentMethodTypeId'] = "${shipmentMethodType.shipmentMethodTypeId?if_exists}";
			    	item['description'] = "${descMeth?if_exists?replace('\n', ' ')}";
			    	shipmentMethodData.push(item);
		    	</#list>
	    	</#if>
    	</#if>
    </#if>
    
    var carrierPartyData = [];
    <#if orderHeader?has_content>
    	<#assign originProductStore = orderHeader.productStoreId?if_exists>
    </#if>
	
	if (orderItemData == undefined){
		<#assign orderItems = delegator.findList("OrderItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId?if_exists)), null, null, null, false) />
		var orderItemData = new Array();
		<#list orderItems as item>
			var row = {};
			row['orderId'] = "${item.orderId}";
			var tmp = null;
			<#if item.estimatedDeliveryDate?has_content>
				row['estimatedDeliveryDate'] = "${item.estimatedDeliveryDate.getTime()}";
				tmp = "${item.estimatedDeliveryDate.getTime()}";
			<#else>
				if (tmp){
					row['estimatedDeliveryDate'] = tmp;
				} else {
					<#if item.shipAfterDate?has_content>
						row['estimatedDeliveryDate'] = '${item.shipAfterDate.getTime()}';
					<#else>
						row['estimatedDeliveryDate'] = null;
					</#if>
				}
			</#if>
			var tmp1 = null;
			<#if item.shipBeforeDate?has_content>
				row['shipBeforeDate'] = '${item.shipBeforeDate.getTime()}';	
				tmp1 = '${item.shipBeforeDate.getTime()}';
			<#else>
				if (tmp1){
					row['shipBeforeDate'] = tmp1;	
				} else {
					row['shipBeforeDate'] = null;	
				}
			</#if>
			orderItemData.push(row);
		</#list>
	}
	var estimatedDeliveryDate = null;
	var shipBeforeDate = null;
	if (orderItemData[0].estimatedDeliveryDate != null){
		estimatedDeliveryDate = new Date(parseInt(orderItemData[0].estimatedDeliveryDate));
	} else {
		if (orderItemData[0].shipBeforeDate != null){
			estimatedDeliveryDate = orderItemData[0].shipBeforeDate;
		}
	}
	if (orderItemData[0].shipBeforeDate != null){
		shipBeforeDate = orderItemData[0].shipBeforeDate;
	} else {
		if (orderItemData[0].estimatedDeliveryDate != null){
			shipBeforeDate = orderItemData[0].estimatedDeliveryDate;
		}
	}
	
    <#assign countryList = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"), null, null, null, false) />
	var countryData = new Array();
	<#list countryList as geo>
		<#assign geoId = StringUtil.wrapString(geo.geoId) />
		<#assign geoName = StringUtil.wrapString(geo.geoName) />
		var row = {};
		row['geoId'] = "${geo.geoId}";
		row['geoName'] = "${geo.geoName}";
		countryData[${geo_index}] = row;
	</#list>
	
    // FIXME Remove all cached data, replace by using ajax request and get Json
	// data.
	
	<#assign orderStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_STATUS"), null, null, null, false)/>
	var orderStatusData = [];
	<#list orderStatus as item>
		var row = {};
		<#assign descOrderStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descOrderStatus?if_exists}";
		orderStatusData[${item_index}] = row;
	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['quantityUomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		uomData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign createdDone = Static["com.olbius.baselogistics.util.LogisticsProductUtil"].checkAllSalesOrderItemCreatedDelivery(delegator, parameters.orderId?if_exists)/>
	
	<#assign storeKeeper = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.storekeeper"), "partyId", userLogin.partyId)), null, null, null, false)/>
	var listFacilityManage = [];
	<#list storeKeeper as item>
		listFacilityManage.push('${item.facilityId}');
	</#list>
	<#assign admin = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.manager.admin"), "partyId", userLogin.partyId)), null, null, null, false)/>
	<#list admin as item>
	listFacilityManage.push('${item.facilityId}');
	</#list>
	
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
</script>
<script>
	var listParty = SalesDlvObj.getOrderRoleAndParty (orderId);
	var listPartyFrom = [];
	var listPartyTo = [];
	if (listParty.length > 0){
		for (var i = 0; i < listParty.length; i ++){
			var party = listParty[i];
			if (party.roleTypeId == "BILL_FROM_VENDOR"){
				listPartyFrom.push(party);
			}
			if (party.roleTypeId == "SHIP_TO_CUSTOMER"){
				listPartyTo.push(party);
			}
		}
	}
</script>