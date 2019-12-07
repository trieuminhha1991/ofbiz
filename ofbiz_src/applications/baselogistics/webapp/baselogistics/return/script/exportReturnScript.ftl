<@jqGridMinimumLib/>
<script type="text/javascript">
	var listReturnItemData = [];
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign returnId = parameters.returnId?if_exists/>
	var returnId = "${returnId?if_exists}";
	var shipmentId = "${parameters.shipmentId?if_exists}"
	<#assign returnHeader = delegator.findOne("ReturnHeader", {"returnId" : parameters.returnId?if_exists}, false)/>
	<#assign returnItems = delegator.findList("ReturnItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("returnId", returnId), null, null, null, false)/>
	var currencyUomId = '${returnHeader.currencyUomId?if_exists}';
	var countItem = '${returnItems.size()}';
	var listReturnItemSelected = [];
	var returnHeaderTypeId = '${returnHeader.returnHeaderTypeId?if_exists}';
	var destinationFacilityId = '${returnHeader.destinationFacilityId?if_exists}';
	
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
	
	<#assign returnPoStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PORDER_RETURN_STTS"), null, null, null, false) />
	var statusPOData = new Array();
	<#list returnPoStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		statusPOData.push(row);
	</#list>
	
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
	<#assign fromPartyName = Static["com.olbius.basehr.util.PartyUtil"].getPersonName(delegator, fromParty.getString("partyId"))>
	
	<#if !fromPartyId?has_content>
		<#assign fromPartyName = fromParty.groupName?if_exists>
	</#if>
	
	<#assign toParty = delegator.findOne("PartyNameView", {"partyId" : returnHeader.toPartyId?if_exists}, false)/>
	<#assign toPartyName = toParty.groupName?if_exists/>
	
	
	<#assign orderId = ''>
	<#if returnItems?has_content>
		<#if returnItems.get(0).get("orderId")?has_content>
			<#assign orderId = returnItems.get(0).getString("orderId")>
		</#if>
	</#if>
	
	<#assign facilities = delegator.findList("Facility", null, null, null, null, false)!>
	
	var facilityReturnData = [];
	<#assign listFacIds = []>
	<#list facilities as item>
		var row = {};
		<#assign descFac = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['description'] = "${descFac?if_exists}";
		facilityReturnData.push(row);
		<#assign listFacIds = listFacIds + [item.facilityId?if_exists]>
	</#list>
	
	<#assign listPrIds = []>
	var returnItemData = [];
	<#list returnItems as returnItemTmp>
		var item = {};
		item["returnId"] = "${returnItemTmp.returnId}";
		item["returnItemSeqId"] = "${returnItemTmp.returnItemSeqId}";
		item["productId"] = "${returnItemTmp.productId}";
		item["returnQuantity"] = "${returnItemTmp.returnQuantity}";
		returnItemData.push(item);
		<#assign listPrIds = listPrIds + [returnItemTmp.productId?if_exists]>
	</#list>
	
	<#assign condInvFa = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listFacIds)>
	<#assign condInvOwner = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, company)>
	
	<#assign condInvStt1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, null)>
	<#assign condInvStt2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "INV_AVAILABLE")>
	
	<#assign condInvPr = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listPrIds)>
	
	<#assign condInvQOH = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("quantityOnHandTotal", Static["org.ofbiz.entity.condition.EntityOperator"].GREATER_THAN, Static["java.math.BigDecimal"].ZERO)>
	
	<#assign condInvStatus = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(condInvStt1, condInvStt2), Static["org.ofbiz.entity.condition.EntityOperator"].OR)>
	
	<#assign listInvConds = Static["org.ofbiz.base.util.UtilMisc"].toList(condInvFa, condInvOwner, condInvStatus, condInvQOH, condInvPr)>
	<#assign allInvConds = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(listInvConds, Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
	
	<#assign listOrderBy = ["expireDate"]>;
	<#assign listInvs = delegator.findList("InventoryAndItemProduct", allInvConds, null, listOrderBy, null, false)>
	
	var listInvTmp = new Array();
	<#list listInvs as inv>
		<#assign qoh = inv.quantityOnHandTotal>
		<#assign aoh = inv.amountOnHandTotal?if_exists>
		<#if qoh &gt; 0>
			var row = {};
			row['inventoryItemId'] = "${inv.inventoryItemId?if_exists}";
			row['expireDate'] = "${inv.expireDate?if_exists}";
			row['datetimeReceived'] = "${inv.datetimeReceived?if_exists}";
			row['datetimeManufactured'] = "${inv.datetimeManufactured?if_exists}";
			row['quantityOnHandTotal'] = "${qoh}";
			row['amountOnHandTotal'] = "${aoh?if_exists}";
			row['availableToPromiseTotal'] = "${inv.availableToPromiseTotal?if_exists}";
			row['productId'] = "${inv.productId?if_exists}";
			row['quantityUomId'] = "${inv.quantityUomId?if_exists}";
			row['weightUomId'] = "${inv.weightUomId?if_exists}";
			row['facilityId'] = "${inv.facilityId?if_exists}";
			listInvTmp.push(row);
		</#if>
	</#list>

    var isCurFac = true;
    <#list returnItems as item>
        <#assign facilityId = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, returnHeader.destinationFacilityId)>
        <#assign productId = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, item.productId)>
        <#assign listInvConds = Static["org.ofbiz.base.util.UtilMisc"].toList(facilityId, productId)>
        <#assign allInvConds = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(listInvConds, Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
        <#assign curFacility = delegator.findList("InventoryItemTotalDetailAndSupplier", allInvConds, null, null, null, false)>
    if(item.returnQuantity > ${curFacility[0].quantityOnHandTotal}) {
        isCurFac = false;
    }
    </#list>
	
	<#if returnHeader.destinationFacilityId?has_content>
		var facilityEnoughId = "${returnHeader.destinationFacilityId?if_exists}";
	<#else>
		if (facilityReturnData.length > 0) {
			facilityEnoughId = facilityReturnData[0].facilityId;
		}
	</#if>

    if(isCurFac) facilityEnoughId = destinationFacilityId;
    var curFacilityId = facilityEnoughId;
    var listInv = [];
    for (var x in listInvTmp) {
        if (listInvTmp[x].facilityId == curFacilityId) {
            listInv.push(listInvTmp[x]);
        }
    }
    if(!isCurFac) {
        for (var f = 0; f < facilityReturnData.length; f ++){
            var fId = facilityReturnData[f].facilityId;
            var listInvByFa = [];
            for (var i = 0; i < listInv.length; i ++){
                if (fId == listInv[i].facilityId){
                    listInvByFa.push(listInv[i]);
                }
            }
            if (listInvByFa.length <= 0) {
                continue;
            }
            var enough = true;
            for (var i = 0; i < returnItemData.length; i ++){
                var checkEnough = false;
                var qtyReturn = returnItemData[i].returnQuantity;
                var qtyInv = 0;
                var productIdTmp = returnItemData[i].productId;
                for (var j = 0; j < listInvByFa.length; j ++){
                    if (productIdTmp == listInvByFa[j].productId){
                        qtyInv = qtyInv + listInvByFa[j].quantityOnHandTotal;
                    }
                    if (qtyInv >= qtyReturn) {
                        checkEnough = true;
                        break;
                    }
                }
                if (checkEnough == false){
                    enough = false;
                    break;
                }
            }
            if (enough == true){
                facilityEnoughId = fId;
                break;
            }
        }
    }
	
	var locale = "${locale}";
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureExport)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.ProductHasBeenReservedForSomeSalesOrder = "${StringUtil.wrapString(uiLabelMap.ProductHasBeenReservedForSomeSalesOrder)}";
	uiLabelMap.DoYouWantToReservesToAnotherInventory = "${StringUtil.wrapString(uiLabelMap.DoYouWantToReservesToAnotherInventory)}";
	uiLabelMap.ProductHasBeenExported = "${StringUtil.wrapString(uiLabelMap.ProductHasBeenExported)}";
	uiLabelMap.AreYouSureAccept = "${StringUtil.wrapString(uiLabelMap.AreYouSureAccept)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.AreYouSureYouWantToExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureYouWantToExport)}";
	uiLabelMap.ExportProduct = "${StringUtil.wrapString(uiLabelMap.ExportProduct)}";
	uiLabelMap.AllItemInListMustBeUpdated = "${StringUtil.wrapString(uiLabelMap.AllItemInListMustBeUpdated)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.YouNotYetChooseExpireDate = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseExpireDate)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.OrderId = "${StringUtil.wrapString(uiLabelMap.OrderId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.LogInventoryItem = "${StringUtil.wrapString(uiLabelMap.LogInventoryItem)}";
	uiLabelMap.NotHasExpDate = "${StringUtil.wrapString(uiLabelMap.NotHasExpDate)}";
	uiLabelMap.NotEnough = "${StringUtil.wrapString(uiLabelMap.NotEnough)}";
	uiLabelMap.FacilityNotEnoughProduct = "${StringUtil.wrapString(uiLabelMap.FacilityNotEnoughProduct)}";
	uiLabelMap.DmsFieldRequired = "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}";
	uiLabelMap.QuantityReturned = "${StringUtil.wrapString(uiLabelMap.QuantityReturned)}";
	uiLabelMap.ExportValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ExportValueMustBeGreaterThanZero)}";
	uiLabelMap.RequiredNumber = "${StringUtil.wrapString(uiLabelMap.RequiredNumber)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.ExportedQuantity = "${StringUtil.wrapString(uiLabelMap.ExportedQuantity)}";
	uiLabelMap.ExpiredDateSum = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}";
	uiLabelMap.ManufacturedDateSum = "${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)}";
	uiLabelMap.ReceivedDateSum = "${StringUtil.wrapString(uiLabelMap.ReceivedDateSum)}";
	uiLabelMap.CannotGreaterRequiredNumber = "${StringUtil.wrapString(uiLabelMap.CannotGreaterRequiredNumber)}";
	uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother)}";
	
	uiLabelMap.ExpireDateNotEnter = "${StringUtil.wrapString(uiLabelMap.ExpireDateNotEnter)}";
	uiLabelMap.NotEnoughDetail = "${StringUtil.wrapString(uiLabelMap.NotEnoughDetail)}";
	uiLabelMap.PleaseEnterQuantityExported = "${StringUtil.wrapString(uiLabelMap.PleaseEnterQuantityExported)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.ProductMissExpiredDate = "${StringUtil.wrapString(uiLabelMap.ProductMissExpiredDate)}";
	uiLabelMap.ProductMissDatetimeManufactured = "${StringUtil.wrapString(uiLabelMap.ProductMissDatetimeManufactured)}";
	uiLabelMap.ProductMissDatetimeReceived = "${StringUtil.wrapString(uiLabelMap.ProductMissDatetimeReceived)}";
	uiLabelMap.ListProduct = "${StringUtil.wrapString(uiLabelMap.ListProduct)}";
	uiLabelMap.BLExportQuantityLessThanRequiredQuantity = "${StringUtil.wrapString(uiLabelMap.BLExportQuantityLessThanRequiredQuantity)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.QuantityNotEntered = "${StringUtil.wrapString(uiLabelMap.QuantityNotEntered)}";
	uiLabelMap.AddRow = "${StringUtil.wrapString(uiLabelMap.AddRow)}";
	
</script>
<script type="text/javascript" src="/logresources/js/return/exportReturn.js?v=0.0.7"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=0.0.5"></script>