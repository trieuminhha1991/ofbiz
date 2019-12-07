<#include 'component://baselogistics/webapp/baselogistics/delivery/script/transferDeliveryCommonScript.ftl'>
<script>
	var listImage = [];
	var pathScanFile = null;
	var isStorekeeperFrom = false;
	var isStorekeeperTo = false;
	var isSpecialist = false;
	var glDeliveryId;
    var glOriginFacilityId;
    var glDeliveryStatusId;
    
    <#if parameters.transferId?has_content>
		if (typeof inTransferDetail != 'undefined') {
			inTransferDetail = true;
		}
		if (typeof glTransferId != 'undefined') {
			glTransferId = '${parameters.transferId?if_exists}';
		}
	</#if>
	
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
	
	var transferId = '${transfer.transferId}';
	var curStatusId = '${transfer.statusId}';
	
	<#assign originFacility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", transfer.originFacilityId), false)>
	<#assign destFacility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", transfer.destFacilityId), false)>
	<#assign transferShipGroup = delegator.findList("TransferItemShipGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId)), null, null, null, false)>
	<#assign originCTM = transferShipGroup.get(0).originContactMechId>
	<#assign destCTM = transferShipGroup.get(0).destContactMechId>
	<#assign shipmentMethodTypeId = transferShipGroup.get(0).shipmentMethodTypeId?if_exists>
	<#assign originFacilityAddress = delegator.findOne("PostalAddressDetail", {"contactMechId" : originCTM}, true) />
	<#assign destFacilityAddress = delegator.findOne("PostalAddressDetail", {"contactMechId" : destCTM}, true) />
	
	<#assign maySplit = transfer.maySplit>
	
	var seletion = "checkbox";
	var maySplit = "${maySplit?if_exists}"
	<#if maySplit == "N">
		seletion = "singlerow";
	<#else>
		seletion = "checkbox";
	</#if>
	
	$.ajax({
        type: "POST",
        url: "getInvByTransferAndDlv",
        data: {'transferId': '${parameters.transferId}'},
        dataType: "json",
        async: false,
        success: function(response){
            listInv = response.listData;
        },
        error: function(response){
        }
	});
	
	<#assign trasferType = delegator.findOne("TransferType", {"transferTypeId", transfer.transferTypeId}, false)>
	<#assign typeDescriptionTmp = StringUtil.wrapString(trasferType.get("description", locale))>
	
	<#assign storeKeeper = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.storekeeper"), "partyId", userLogin.partyId)), null, null, null, false)/>
	var listFacilityManage = [];
	<#list storeKeeper as item>
		listFacilityManage.push('${item.facilityId}');
	</#list>
	<#assign specialist = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.manager.specialist"), "partyId", userLogin.partyId)), null, null, null, false)/>
	<#list specialist as item>
	listFacilityManage.push('${item.facilityId}');
	</#list>
	
	<#assign createdDone = Static['com.olbius.baselogistics.util.LogisticsProductUtil'].checkAllTransferItemCreatedDelivery(delegator, transfer.transferId?if_exists)!/>;
	
</script>
