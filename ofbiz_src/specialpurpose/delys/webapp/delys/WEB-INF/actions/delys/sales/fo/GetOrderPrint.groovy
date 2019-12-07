
import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.party.contact.ContactMechWorker;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javolution.util.FastMap;

orderId = parameters.orderId;
context.orderId = orderId;

workEffortId = parameters.workEffortId;
assignPartyId = parameters.partyId;
assignRoleTypeId = parameters.roleTypeId;
fromDate = parameters.fromDate;
delegate = parameters.delegate;
if (delegate && fromDate) {
    fromDate = parameters.toFromDate;
}
context.workEffortId = workEffortId;
context.assignPartyId = assignPartyId;
context.assignRoleTypeId = assignRoleTypeId;
context.fromDate = fromDate;
context.delegate = delegate;
context.todayDate = new java.sql.Date(System.currentTimeMillis()).toString();
def partyId = null;

orderHeader = null;
orderItems = null;
orderAdjustments = null;

if (orderId) {
    orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
}

if (orderHeader) {
    // note these are overridden in the OrderViewWebSecure.groovy script if run
    context.hasPermission = true;
    context.canViewInternalDetails = true;

    orderReadHelper = new OrderReadHelper(orderHeader);
    orderItems = orderReadHelper.getOrderItems();
    orderAdjustments = orderReadHelper.getAdjustments();
    orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
    orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
    orderTerms = orderHeader.getRelated("OrderTerm", null, null, false);

    context.orderHeader = orderHeader;
    context.orderReadHelper = orderReadHelper;
    context.orderItems = orderItems;
    context.orderAdjustments = orderAdjustments;
    context.orderHeaderAdjustments = orderHeaderAdjustments;
    context.orderSubTotal = orderSubTotal;
    context.currencyUomId = orderReadHelper.getCurrency();
    context.orderTerms = orderTerms;

    // get sales reps
    context.salesReps = orderHeader.getRelated("OrderRole", [orderId : orderHeader.orderId, roleTypeId : "SALES_REP"], null, false);
    
    // get the order type
    orderType = orderHeader.orderTypeId;
    context.orderType = orderType;

    // get the display party
    displayParty = null;
    if ("PURCHASE_ORDER".equals(orderType)) {
        displayParty = orderReadHelper.getSupplierAgent();
    } else {
        displayParty = orderReadHelper.getPlacingParty();
    }
    if (displayParty) {
        partyId = displayParty.partyId;
        context.displayParty = displayParty;
        context.partyId = partyId;

        paymentMethodValueMaps = PaymentWorker.getPartyPaymentMethodValueMaps(delegator, displayParty.partyId, false);
        context.paymentMethodValueMaps = paymentMethodValueMaps;
    }

    otherAdjAmount = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false);
    context.otherAdjAmount = otherAdjAmount;

    shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
    shippingAmount = shippingAmount.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
    context.shippingAmount = shippingAmount;

    taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
    context.taxAmount = taxAmount;

    grandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
    context.grandTotal = grandTotal;

    orderItemList = orderReadHelper.getOrderItems();
    // Retrieve all non-promo items that aren't cancelled
    context.orderItemList = orderReadHelper.getOrderItems().findAll { item ->
        (item.isPromo == null || item.isPromo == 'N')  && !(item.statusId.equals('ITEM_CANCELLED'))
    }

    shippingAddress = orderReadHelper.getShippingAddress();
    context.shippingAddress = shippingAddress;

    billingAddress = orderReadHelper.getBillingAddress();
    context.billingAddress = billingAddress;

    distributorId = orderReadHelper.getDistributorId();
    context.distributorId = distributorId;

    affiliateId = orderReadHelper.getAffiliateId();
    context.affiliateId = affiliateId;

    billingAccount = orderHeader.getRelatedOne("BillingAccount", false);
    context.billingAccount = billingAccount;
    context.billingAccountMaxAmount = orderReadHelper.getBillingAccountMaxAmount();

    // get a list of all shipments, and a list of ItemIssuances per order item
    allShipmentsMap = [:];
    primaryShipments = orderHeader.getRelated("PrimaryShipment", null, null, false);
    primaryShipments.each { primaryShipment ->
        allShipmentsMap[primaryShipment.shipmentId] = primaryShipment;
    }
    itemIssuancesPerItem = [:];
    itemIssuances = orderHeader.getRelated("ItemIssuance", null, ["shipmentId", "shipmentItemSeqId"], false);
    itemIssuances.each { itemIssuance ->
        if (!allShipmentsMap.containsKey(itemIssuance.shipmentId)) {
            iiShipment = itemIssuance.getRelatedOne("Shipment", false);
            if (iiShipment) {
                allShipmentsMap[iiShipment.shipmentId] = iiShipment;
            }
        }

        perItemList = itemIssuancesPerItem[itemIssuance.orderItemSeqId];
        if (!perItemList) {
            perItemList = [];
            itemIssuancesPerItem[itemIssuance.orderItemSeqId] = perItemList;
        }
        perItemList.add(itemIssuance);
    }
    context.allShipments = allShipmentsMap.values();
    context.itemIssuancesPerItem = itemIssuancesPerItem;

    // get a list of all invoices
    orderBilling = delegator.findByAnd("OrderItemBilling", [orderId : orderId], ["invoiceId"], false);
    context.invoices = orderBilling*.invoiceId.unique();

    ecl = EntityCondition.makeCondition([
                                    EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
                                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")],
                                EntityOperator.AND);
    orderPaymentPreferences = delegator.findList("OrderPaymentPreference", ecl, null, null, null, false);
    context.orderPaymentPreferences = orderPaymentPreferences;

    // ship groups
    shipGroups = delegator.findByAnd("OrderItemShipGroup", [orderId : orderId], ["shipGroupSeqId"], false);
    context.shipGroups = shipGroups;

    // get Shipment tracking info
    osisCond = EntityCondition.makeCondition([orderId : orderId], EntityOperator.AND);
    osisOrder = ["shipmentId", "shipmentRouteSegmentId", "shipmentPackageSeqId"];
    osisFields = ["shipGroupSeqId", "shipmentId", "shipmentRouteSegmentId", "carrierPartyId", "shipmentMethodTypeId"] as Set;
    osisFields.add("shipmentPackageSeqId");
    osisFields.add("trackingCode");
    osisFields.add("boxNumber");
    osisFindOptions = new EntityFindOptions();
    osisFindOptions.setDistinct(true);
    orderShipmentInfoSummaryList = delegator.findList("OrderShipmentInfoSummary", osisCond, osisFields, osisOrder, osisFindOptions, false);
    context.orderShipmentInfoSummaryList = orderShipmentInfoSummaryList;

    customerPoNumber = null;
    orderItemList.each { orderItem ->
        customerPoNumber = orderItem.correspondingPoId;
    }
    context.customerPoNumber = customerPoNumber;

    statusChange = delegator.findByAnd("StatusValidChange", [statusId : orderHeader.statusId], null, false);
    context.statusChange = statusChange;

    currentStatus = orderHeader.getRelatedOne("StatusItem", false);
    context.currentStatus = currentStatus;

    orderHeaderStatuses = orderReadHelper.getOrderHeaderStatuses();
    context.orderHeaderStatuses = orderHeaderStatuses;

    adjustmentTypes = delegator.findList("OrderAdjustmentType", null, null, ["description"], null, false);
    context.orderAdjustmentTypes = adjustmentTypes;

    /* TODO comment: not necessary in this case
    notes = delegator.findByAnd("OrderHeaderNoteView", [orderId : orderId], ["-noteDateTime"], false);
    context.orderNotes = notes;
    showNoteHeadingOnPDF = false;
    if (notes && EntityUtil.filterByCondition(notes, EntityCondition.makeCondition("internalNote", EntityOperator.EQUALS, "N")).size() > 0) {
        showNoteHeadingOnPDF = true;
    }
    context.showNoteHeadingOnPDF = showNoteHeadingOnPDF;
    */

    cmvm = ContactMechWorker.getOrderContactMechValueMaps(delegator, orderId);
    context.orderContactMechValueMaps = cmvm;

    orderItemChangeReasons = delegator.findByAnd("Enumeration", [enumTypeId : "ODR_ITM_CH_REASON"], ["sequenceId"], false);
    context.orderItemChangeReasons = orderItemChangeReasons;

    if ("PURCHASE_ORDER".equals(orderType)) {
        // for purchase orders, we need also the supplier's postal address
        supplier = orderReadHelper.getBillFromParty();
        if (supplier) {
            supplierContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, supplier.partyId, false, "POSTAL_ADDRESS");
            context.supplierContactMechValueMaps = supplierContactMechValueMaps;
            supplierContactMechValueMaps.each { supplierContactMechValueMap ->
                contactMechPurposes = supplierContactMechValueMap.partyContactMechPurposes;
                contactMechPurposes.each { contactMechPurpose ->
                    if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
                        context.supplierGeneralContactMechValueMap = supplierContactMechValueMap;
                    } else if (contactMechPurpose.contactMechPurposeTypeId.equals("SHIPPING_LOCATION")) {
                        context.supplierShippingContactMechValueMap = supplierContactMechValueMap;
                    } else if (contactMechPurpose.contactMechPurposeTypeId.equals("BILLING_LOCATION")) {
                        context.supplierBillingContactMechValueMap = supplierContactMechValueMap;
                    } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PAYMENT_LOCATION")) {
                        context.supplierPaymentContactMechValueMap = supplierContactMechValueMap;
                    }
                }
            }
        }
        // get purchase order item types
        purchaseOrderItemTypeList = delegator.findByAnd("OrderItemType", [parentTypeId : "PURCHASE_SPECIFIC"], null, true);
        context.purchaseOrderItemTypeList = purchaseOrderItemTypeList;
    }

    // see if an approved order with all items completed exists
    context.setOrderCompleteOption = false;
    if ("ORDER_APPROVED".equals(orderHeader.statusId)) {
        expr = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_COMPLETED");
        notCreatedItems = orderReadHelper.getOrderItemsByCondition(expr);
        if (!notCreatedItems) {
            context.setOrderCompleteOption = true;
        }
    }

    // get inventory summary for each shopping cart product item
    /* TODO comment: not necessary in this case
    inventorySummary = dispatcher.runSync("getProductInventorySummaryForItems", [orderItems : orderItems]);
    context.availableToPromiseMap = inventorySummary.availableToPromiseMap;
    context.quantityOnHandMap = inventorySummary.quantityOnHandMap;
    context.mktgPkgATPMap = inventorySummary.mktgPkgATPMap;
    context.mktgPkgQOHMap = inventorySummary.mktgPkgQOHMap;*/

    // get inventory summary with respect to facility
    productStore = orderReadHelper.getProductStore();
    context.productStore = productStore;
    /* TODO comment: not necessary in this case
    if (productStore) {
    	facility = productStore.getRelatedOne("Facility", false);
        inventorySummaryByFacility = dispatcher.runSync("getProductInventorySummaryForItems", [orderItems : orderItems, facilityId : facility.facilityId]);
        context.availableToPromiseByFacilityMap = inventorySummaryByFacility.availableToPromiseMap;
        context.quantityOnHandByFacilityMap = inventorySummaryByFacility.quantityOnHandMap;
        context.facility = facility;
    }*/

    // Get a list of facilities for purchase orders to receive against.
    // These facilities must be owned by the bill-to party of the purchase order.
    // For a given ship group, the allowed facilities are the ones associated
    // to the same contact mech of the ship group.
    if ("PURCHASE_ORDER".equals(orderType)) {
        facilitiesForShipGroup = [:];
        if (orderReadHelper.getBillToParty()) {
            ownerPartyId = orderReadHelper.getBillToParty().partyId;
            Map ownedFacilities = FastMap.newInstance();
            shipGroups.each { shipGroup ->
                lookupMap = [ownerPartyId : ownerPartyId];
                if (shipGroup.contactMechId) {
                    lookupMap.contactMechId = shipGroup.contactMechId;
                }
                facilities = delegator.findByAnd("FacilityAndContactMech", lookupMap, null, true);
                facilitiesForShipGroup[shipGroup.shipGroupSeqId] = facilities;
                facilities.each { facility ->
                    ownedFacilities[facility.facilityId] = facility;
                }
            }
            context.facilitiesForShipGroup = facilitiesForShipGroup;
            // Now get the list of all the facilities owned by the bill-to-party
            context.ownedFacilities = ownedFacilities.values();
        }
    }

    // set the type of return based on type of order
    if ("SALES_ORDER".equals(orderType)) {
        context.returnHeaderTypeId = "CUSTOMER_RETURN";
        // also set the product store facility Id for sales orders
        if (productStore) {
            context.storeFacilityId = productStore.inventoryFacilityId;
            if (productStore.reqReturnInventoryReceive) {
                context.needsInventoryReceive = productStore.reqReturnInventoryReceive;
            } else {
                context.needsInventoryReceive = "Y";
            }
        }
    } else {
        context.returnHeaderTypeId = "VENDOR_RETURN";
    }

    // QUANTITY: get the returned quantity by order item map
    // TODO comment: not necessary in this case
	// context.returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities();

    // INVENTORY: construct a Set of productIds in the order for use in querying for inventory, otherwise these queries can get expensive
    productIds = orderReadHelper.getOrderProductIds();

    // INVENTORY: get the production quantity for each product and store the results in a map of productId -> quantity
    /* TODO comment: not necessary in this case
    productionMap = [:];
    productIds.each { productId ->
        if (productId) {  // avoid order items without productIds, such as bulk order items
            contextInput = [productId : productId, userLogin : userLogin];
            resultOutput = dispatcher.runSync("getProductManufacturingSummaryByFacility", contextInput);
            manufacturingInQuantitySummaryByFacility = resultOutput.summaryInByFacility;
            Double productionQuantity = 0;
            manufacturingInQuantitySummaryByFacility.values().each { manQuantity ->
                productionQuantity += manQuantity.estimatedQuantityTotal;
            }
            productionMap[productId] = productionQuantity;
        }
    }
    context.productionProductQuantityMap = productionMap;

    if (productIds.size() > 0) {
        // INVENTORY: find the number of products in outstanding sales orders for the same product store    
        requiredMap = InventoryWorker.getOutstandingProductQuantitiesForSalesOrders(productIds, delegator);
        context.requiredProductQuantityMap = requiredMap;
    
        // INVENTORY: find the quantity of each product in outstanding purchase orders
        onOrderMap = InventoryWorker.getOutstandingProductQuantitiesForPurchaseOrders(productIds, delegator);
        context.onOrderProductQuantityMap = onOrderMap;
    } else {
        context.requiredProductQuantityMap = FastMap.newInstance();
        context.onOrderProductQuantityMap = FastMap.newInstance();
    }
    */
}

paramString = "";
if (orderId) paramString += "orderId=" + orderId;
if (workEffortId) paramString += "&workEffortId=" + workEffortId;
if (assignPartyId) paramString += "&partyId=" + assignPartyId;
if (assignRoleTypeId) paramString += "&roleTypeId=" + assignRoleTypeId;
if (fromDate) paramString += "&fromDate=" + fromDate;
context.paramString = paramString;

workEffortStatus = null;
if (workEffortId && assignPartyId && assignRoleTypeId && fromDate) {
    fields = [workEffortId : workEffortId, partyId : assignPartyId, roleTypeId : assignRoleTypeId, fromDate : fromDate];
    wepa = delegator.findOne("WorkEffortPartyAssignment", fields, false);

    if ("CAL_ACCEPTED".equals(wepa?.statusId)) {
        workEffort = delegator.findOne("WorkEffort", [workEffortId : workEffortId], false);
        workEffortStatus = workEffort.currentStatusId;
        if (workEffortStatus) {
            context.workEffortStatus = workEffortStatus;
            if (workEffortStatus.equals("WF_RUNNING") || workEffortStatus.equals("WF_SUSPENDED"))
                context.inProcess = true;
        }

        if (workEffort) {
            if ("true".equals(delegate) || "WF_RUNNING".equals(workEffortStatus)) {
                actFields = [packageId : workEffort.workflowPackageId, packageVersion : workEffort.workflowPackageVersion, processId : workEffort.workflowProcessId, processVersion : workEffort.workflowProcessVersion, activityId : workEffort.workflowActivityId];
                activity = delegator.findOne("WorkflowActivity", actFields, false);
                if (activity) {
                    transitions = activity.getRelated("FromWorkflowTransition", null, ["-transitionId"], false);
                    context.wfTransitions = transitions;
                }
            }
        }
    }
}

if (orderHeader) {
    // list to find all the POSTAL_ADDRESS for the shipment party.
    orderParty = delegator.findOne("Party", [partyId : partyId], false);
    shippingContactMechList = ContactHelper.getContactMech(orderParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
    context.shippingContactMechList = shippingContactMechList;

    // list to find all the shipmentMethods from the view named "ProductStoreShipmentMethView".
    if (productStore) {
        context.productStoreShipmentMethList = delegator.findByAnd('ProductStoreShipmentMethView', [productStoreId: productStore.productStoreId], ['sequenceNumber'], true);
    }

    // Get a map of returnable items
    returnableItems = [:];
    returnableItemServiceMap = dispatcher.runSync("getReturnableItems", [orderId : orderId]);
    if (returnableItemServiceMap.returnableItems) {
        returnableItems = returnableItemServiceMap.returnableItems;
    }
    context.returnableItems = returnableItems;

    // get the catalogIds for appending items
    if (context.request != null) {
        if ("SALES_ORDER".equals(orderType) && productStore) {
            catalogCol = CatalogWorker.getCatalogIdsAvailable(delegator, productStore.productStoreId, partyId);
        } else {
            catalogCol = CatalogWorker.getAllCatalogIds(request);
        }
        if (catalogCol) {
            currentCatalogId = catalogCol[0];
            currentCatalogName = CatalogWorker.getCatalogName(request, currentCatalogId);
            context.catalogCol = catalogCol;
            context.currentCatalogId = currentCatalogId;
            context.currentCatalogName = currentCatalogName;
        }
    }
}

if (orderHeader) {
   // list to find all the POSTAL_ADDRESS for the party.
   orderParty = delegator.findOne("Party", [partyId : partyId], false);
   postalContactMechList = ContactHelper.getContactMechByType(orderParty,"POSTAL_ADDRESS", false);
   context.postalContactMechList = postalContactMechList;

   // list to find all the TELECOM_NUMBER for the party.
   telecomContactMechList = ContactHelper.getContactMechByType(orderParty,"TELECOM_NUMBER", false);
   context.telecomContactMechList = telecomContactMechList;

   // list to find all the EMAIL_ADDRESS for the party.
   emailContactMechList = ContactHelper.getContactMechByType(orderParty,"EMAIL_ADDRESS", false);
   context.emailContactMechList = emailContactMechList;
}

if (orderItems) {
    orderItem = EntityUtil.getFirst(orderItems);
    context.orderItem = orderItem;
}

// getting online ship estimates corresponding to this Order from UPS when "Hold" button will be clicked, when user packs from weight package screen.
// This case comes when order's shipping amount is  more then or less than default percentage (defined in shipment.properties) of online UPS shipping amount.

    condn = EntityCondition.makeCondition([
                                      EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId),
                                      EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SHIPMENT_PICKED")],
                                  EntityOperator.AND);
    shipments = delegator.findList("Shipment", condn, null, null, null, false);
    if (shipments) {
        pickedShipmentId = EntityUtil.getFirst(shipments).shipmentId;
        shipmentRouteSegment = EntityUtil.getFirst(delegator.findList("ShipmentRouteSegment",EntityCondition.makeCondition([shipmentId : pickedShipmentId]), null, null, null, false));
        context.shipmentRouteSegmentId = shipmentRouteSegment.shipmentRouteSegmentId;
        context.pickedShipmentId = pickedShipmentId;
        if (pickedShipmentId && shipmentRouteSegment.trackingIdNumber) {
            if ("UPS" == shipmentRouteSegment.carrierPartyId && productStore) {
                resultMap = dispatcher.runSync('upsShipmentAlternateRatesEstimate', [productStoreId: productStore.productStoreId, shipmentId: pickedShipmentId]);
                shippingRates = resultMap.shippingRates;
                shippingRateList = [];
                shippingRates.each { shippingRate ->
                    shippingMethodAndRate = [:];
                    serviceCodes = shippingRate.keySet();
                    serviceCodes.each { serviceCode ->
                        carrierShipmentMethod = EntityUtil.getFirst(delegator.findByAnd("CarrierShipmentMethod", [partyId : "UPS", carrierServiceCode : serviceCode], null, false));
                        shipmentMethodTypeId = carrierShipmentMethod.shipmentMethodTypeId;
                        rate = shippingRate.get(serviceCode);
                        shipmentMethodDescription = EntityUtil.getFirst(carrierShipmentMethod.getRelated("ShipmentMethodType", null, null, false)).description;
                        shippingMethodAndRate.shipmentMethodTypeId = carrierShipmentMethod.shipmentMethodTypeId;
                        shippingMethodAndRate.rate = rate;
                        shippingMethodAndRate.shipmentMethodDescription = shipmentMethodDescription;
                        shippingRateList.add(shippingMethodAndRate);
                    }
               }
                context.shippingRateList = shippingRateList;
            }
        }
    }

    // get orderAdjustmentId for SHIPPING_CHARGES
    orderAdjustmentId = null;
    orderAdjustments.each { orderAdjustment ->
        if(orderAdjustment.orderAdjustmentTypeId.equals("SHIPPING_CHARGES")) {
            orderAdjustmentId = orderAdjustment.orderAdjustmentId;
        }
    }
    context.orderAdjustmentId = orderAdjustmentId;
	
Calendar cal = Calendar.getInstance();
String displayPartyNameResult = "";
String displaySUPsNameResult = "";
List<String> listAddressCust = new ArrayList<String>();
Timestamp desiredDeliveryDate = null;
if (orderHeader != null) {
	if (orderHeader.orderDate != null) {
		cal.setTimeInMillis(orderHeader.orderDate.getTime());
	}
	
	displayParty = orderReadHelper.getPlacingParty();
	if (displayParty != null) {
		Map<String, Object> resultValue1 = dispatcher.runSync("getPartyNameForDate", UtilMisc.toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin));
		if (ServiceUtil.isSuccess(resultValue1)) {
			displayPartyNameResult = resultValue1.fullName;
			if (displayPartyNameResult == null || displayPartyNameResult == "") {
				displayPartyNameResult = UtilProperties.getMessage("OrderErrorUiLabels", "OrderPartyNameNotFound", locale);
			}
		}
	}
	// get SUP
	List<GenericValue> listEmployeeSUP = new ArrayList<GenericValue>();
	List<GenericValue> listRelationToSUP = delegator.findByAnd("PartyRelationship", ["partyIdTo" : displayParty.partyId, "roleTypeIdFrom" : "DELYS_SALESSUP_GT"], null, false);
	for (supItem in listRelationToSUP) {
		List<GenericValue> listEmployeeSUP0 = delegator.findByAnd("PartyRelationship", ["partyIdTo" : supItem.partyIdFrom, "partyRelationshipTypeId" : "MANAGER"], null, false);
		if (listEmployeeSUP0 != null) {
			listEmployeeSUP.addAll(listEmployeeSUP0);
		}
	}
	for (GenericValue employeeSUP : listEmployeeSUP) {
		Map<String, Object> resultValue2 = dispatcher.runSync("getPartyNameForDate", UtilMisc.toMap("partyId", employeeSUP.partyIdFrom, "userLogin", userLogin));
		if (ServiceUtil.isSuccess(resultValue2)) {
			if (displaySUPsNameResult == "") {
				displaySUPsNameResult = resultValue2.fullName;
			} else {
				displaySUPsNameResult += ", " + resultValue2.fullName;
			}
		}
	}
	if (displaySUPsNameResult == "") {
		displaySUPsNameResult = UtilProperties.getMessage("OrderUiLabels", "OrderPartyNameNotFound", locale);
	}
	
	// location ship to
	List<Map<String, GenericValue>> orderContactMechValueMaps = ContactMechWorker.getOrderContactMechValueMaps(delegator, orderId);
	for (orderContactMechValueMap in orderContactMechValueMaps) {
		String addressItem = "";
		contactMech = orderContactMechValueMap.contactMech;
		contactMechPurpose = orderContactMechValueMap.contactMechPurposeType;
		if (contactMech.contactMechTypeId == "POSTAL_ADDRESS") {
			postalAddress = orderContactMechValueMap.postalAddress;
			if (postalAddress != null) {
				/*
				 <#if postalAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${postalAddress.toName}<br /></#if>
					<#if postalAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${postalAddress.attnName}<br /></#if>
					<#if postalAddress.address1?has_content>${postalAddress.address1}<br /></#if>
					<#if postalAddress.address2?has_content>${postalAddress.address2}<br /></#if>
					<#if postalAddress.city?has_content>${postalAddress.city}</#if>
					<#if postalAddress.stateProvinceGeoId?has_content>&nbsp;
						  <#assign stateProvince = postalAddress.getRelatedOne("StateProvinceGeo", true)>
						  ${stateProvince.abbreviation?default(stateProvince.geoId)}
					</#if>
					<#if postalAddress.postalCode?has_content>, ${postalAddress.postalCode?if_exists}</#if>
					<#if postalAddress.countryGeoId?has_content><br />
						  <#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
						  ${country.get("geoName", locale)?default(country.geoId)}
					</#if>
				 */
				/*if (UtilValidate.isNotEmpty(postalAddress.toName)) {
					addressItem += postalAddress.toName;
					if (UtilValidate.isNotEmpty(postalAddress.attnName)) {
						addressItem += " (" + postalAddress.attnName + ").";
					} else {
						addressItem += "."
					}
				}*/
				if (UtilValidate.isNotEmpty(postalAddress.address1)) addressItem += postalAddress.address1 + ".";
				if (UtilValidate.isNotEmpty(postalAddress.address2)) addressItem += " " + postalAddress.address2 + ".";
				if (UtilValidate.isNotEmpty(postalAddress.city)) addressItem += " " + postalAddress.city;
				if (UtilValidate.isNotEmpty(postalAddress.countryGeoId)) {
					addressItem += ", ";
					country = postalAddress.getRelatedOne("CountryGeo", true);
					if (UtilValidate.isNotEmpty(country.get("geoName", locale))) {
						addressItem += country.get("geoName", locale);
					} else {
						addressItem += country.geoId;
					}
				}
				
				//get area
				if (postalAddress.countyGeoId != null) {
					countyGeo = postalAddress.getRelatedOne("CountyGeo", false);\
					context.areaGeoName = countyGeo.geoName;
				}
			}
		}
		listAddressCust.add(addressItem);
	}
	
	if (orderItems != null) {
		desiredDeliveryDate = orderItems[0].getTimestamp("estimatedDeliveryDate");
	}
}
context.orderDateTime = cal;
context.displayPartyNameResult = displayPartyNameResult;
context.listAddressCust = listAddressCust;
context.displaySUPsNameResult = displaySUPsNameResult;
context.desiredDeliveryDate = desiredDeliveryDate;

List<Map<String, Object>> listItemLine = new ArrayList<Map<String, Object>>();
BigDecimal taxTotalOrderItems = BigDecimal.ZERO;
BigDecimal subAmountExportOrder = BigDecimal.ZERO;
BigDecimal subAmountExportInvoice = BigDecimal.ZERO;
List<Map<String, Object>> listTaxTotal = new ArrayList<Map<String, Object>>();
if (orderItems != null) {
	for (GenericValue orderItem : orderItems) {
		GenericValue orderItemType = orderItem.getRelatedOne("OrderItemType", false);
		OrderContentWrapper orderItemContentWrapper = OrderContentWrapper.makeOrderContentWrapper(orderItem, request);
		List<GenericValue> orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem);
		if (orderHeader != null && orderHeader.orderTypeId == "SALES_ORDER") {
			BigDecimal pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem);
		}
		GenericValue product = orderItem.getRelatedOne("Product", false);
		
		String productId = orderItem.getString("productId");
		String seqId = orderItem.getString("orderItemSeqId");
		String itemDescription = orderItem.getString("itemDescription");;
		String supplierProductId = orderItem.getString("supplierProductId");
		GenericValue barcodeGeneric = delegator.findOne("GoodIdentification", ["goodIdentificationTypeId" : "SKU", "productId" : productId], false);
		String barcode = barcodeGeneric.idValue;
		Timestamp expireDate = orderItem.getTimestamp("expireDate");
		String displayPackingPerTray = "";
		
		boolean isNormal = true;
		BigDecimal alternativeQuantity = orderItem.getBigDecimal("alternativeQuantity");
		BigDecimal alternativeUnitPrice = orderItem.getBigDecimal("alternativeUnitPrice");
		if (alternativeQuantity != null && alternativeUnitPrice != null) {
			isNormal = false;
		}
		
		BigDecimal packingPerTray = null;
		Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", ["productId" : productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.productPackingUomId, "userLogin" : userLogin]);
		if (ServiceUtil.isSuccess(resultValue)) {
			packingPerTray = resultValue.convertNumber;
		}
		
		String quantityUomDescription = "";
		GenericValue quantityUomGeneric = delegator.findOne("Uom", ["uomId" : orderItem.quantityUomId], false);
		if (quantityUomGeneric != null) {
			if (quantityUomGeneric.description != null) {
				quantityUomDescription = quantityUomGeneric.description;
			} else {
				quantityUomDescription = quantityUomGeneric.uomId;
			}
		}
		
		BigDecimal quantity = null;
		BigDecimal sumTray = null;
		BigDecimal unitPrice = null;
		BigDecimal productQuantityPerTray = null;
		if (isNormal) {
			if (product.productPackingUomId != null) {
				Map<String, Object> resultValue1 = dispatcher.runSync("getConvertPackingNumber", ["productId" : productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.productPackingUomId, "userLogin" : userLogin]);
				if (ServiceUtil.isSuccess(resultValue1)) {
					productQuantityPerTray = resultValue1.convertNumber;
				}
			}
			quantity = orderItem.quantity;
			if (quantity != null && productQuantityPerTray != null) {
				sumTray = quantity.divide(productQuantityPerTray, 2, RoundingMode.HALF_UP);
			}
			unitPrice = orderItem.unitPrice;
		} else {
			Map<String, Object> resultValue1 = dispatcher.runSync("getConvertPackingNumber", ["productId" : productId, "uomFromId" : "DELYS_KHAY", "uomToId" : orderItem.quantityUomId, "userLogin" : userLogin]);
			if (ServiceUtil.isSuccess(resultValue1)) {
				productQuantityPerTray = resultValue1.convertNumber;
			}
			
			quantity = orderItem.alternativeQuantity;
			if (quantity != null && productQuantityPerTray != null) {
				sumTray = quantity.divide(productQuantityPerTray, 2, RoundingMode.HALF_UP);
			}
			unitPrice = orderItem.alternativeUnitPrice;
		}
		
		BigDecimal adjustment = OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false);
		
		// DASubTotalBeforeVAT
		BigDecimal subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments);
		if (orderItem.statusId != "ITEM_CANCELLED") {
			subAmountExportOrder = subAmountExportOrder.add(subTotalBeVAT);
		} else {
			subAmountExportOrder = subAmountExportOrder.add(subTotalBeVAT);
		}
		
		// Unit price after VAT
		BigDecimal unitPriceInvoiceAfVAT = null;
		BigDecimal subTotalInvoiceExport = null;
		List<GenericValue> listProductPriceInvoice = delegator.findByAnd("ProductPrice", ["productId" : productId, "productPriceTypeId" : "INVOICE_PRICE_GT"], null, false);
		listProductPriceInvoice = EntityUtil.filterByDate(listProductPriceInvoice);
		GenericValue productPriceInvoiceGeneric = EntityUtil.getFirst(listProductPriceInvoice);
		if (productPriceInvoiceGeneric != null) {
			unitPriceInvoiceAfVAT = productPriceInvoiceGeneric.getBigDecimal("price");
			if (subTotalBeVAT.compareTo(BigDecimal.ZERO) != 0) {
				subTotalInvoiceExport = quantity.multiply(unitPriceInvoiceAfVAT);
			}
		}
		
		if (subTotalInvoiceExport != null) {
			subAmountExportInvoice = subAmountExportInvoice.add(subTotalInvoiceExport);
		}
		
		/* Caculate tax prices sum from order items */
		List<GenericValue> orderItemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orderAdjustments);
		if (orderItemAdjustments != null) {
			for (GenericValue itemAdjustment : orderItemAdjustments) {
				GenericValue adjustmentType = itemAdjustment.getRelatedOne("OrderAdjustmentType", true);
				if (adjustmentType.orderAdjustmentTypeId == "SALES_TAX") {
					BigDecimal taxValue = OrderReadHelper.calcItemAdjustment(itemAdjustment, orderItem);
					taxTotalOrderItems = taxTotalOrderItems.add(taxValue);
					boolean isExists = false;
					for (Map<String, Object> taxTotalItem : listTaxTotal) {
						if (taxTotalItem.sourcePercentage == itemAdjustment.sourcePercentage) {
							// exists item
							BigDecimal amount = (BigDecimal) taxTotalItem.get("amount");
							amount = amount.add(itemAdjustment.getBigDecimal("amount"));
							taxTotalItem.put("amount", amount);
							if (subTotalInvoiceExport != null && subTotalBeVAT != null) {
								BigDecimal amountForInvoicePrice = amount.multiply(subTotalInvoiceExport.divide(subTotalBeVAT, 2, RoundingMode.HALF_UP));
								taxTotalItem.put("amountForIXP", amountForInvoicePrice);
							}
							isExists = true;
						}
					}
					if (!isExists) {
						// not exists item
						Map<String, Object> taxTotalItemNew = FastMap.newInstance();
						BigDecimal amount = itemAdjustment.getBigDecimal("amount");
						taxTotalItemNew.put("sourcePercentage", itemAdjustment.getBigDecimal("sourcePercentage"));
						taxTotalItemNew.put("amount", amount);
						if (subTotalInvoiceExport != null && subTotalBeVAT != null) {
							BigDecimal amountForInvoicePrice = amount.multiply(subTotalInvoiceExport.divide(subTotalBeVAT, 2, RoundingMode.HALF_UP));
							taxTotalItemNew.put("amountForIXP", amountForInvoicePrice);
						}
						// add description in first (only 1 times)
						String description = UtilProperties.getMessage("OrderUiLabels", "OrderAdjustment", locale);
						description += " " + adjustmentType.get("description",locale);
						if (itemAdjustment.description != null) description += itemAdjustment.get("description",locale);
						if (itemAdjustment.comments != null) description += " (" + itemAdjustment.comments + "). ";
						if (itemAdjustment.productPromoId != null) {
							description += "<a class='btn btn-mini btn-primary' href='/catalog/control/EditProductPromo?productPromoId=" + itemAdjustment.productPromoId + externalKeyParam + "'>";
							description += itemAdjustment.getRelatedOne("ProductPromo", false).getString("promoName") + "</a>";
						}
						if (itemAdjustment.primaryGeoId != null) {
							GenericValue primaryGeo = itemAdjustment.getRelatedOne("PrimaryGeo", true);
							if (primaryGeo.geoName != null) {
								String orderJurisdictionStr = UtilProperties.getMessage("OrderUiLabels", "OrderJurisdiction", locale);
								description += "" + orderJurisdictionStr + " " + primaryGeo.geoName + " [" + primaryGeo.abbreviation + "]. ";
							}
							if (itemAdjustment.secondaryGeoId != null) {
								GenericValue secondaryGeo = itemAdjustment.getRelatedOne("SecondaryGeo", true);
								String commonInStr = UtilProperties.getMessage("OrderUiLabels", "OrderJurisdiction", locale);
								description += "" + commonInStr + " " + secondaryGeo.geoName + " [" + secondaryGeo.abbreviation + "]). ";
							}
						}
						if (itemAdjustment.sourcePercentage != null) {
							String orderRateStr = UtilProperties.getMessage("OrderUiLabels", "OrderRate", locale);
							String template = "#,##0.###";
							sourcePercentageStr = UtilFormatOut.formatDecimalNumber(itemAdjustment.sourcePercentage.doubleValue(), template, locale);
							description += "" + orderRateStr + " " + sourcePercentageStr + "%"; //?string("0.######")
						}
						if (itemAdjustment.customerReferenceId != null) {
							String orderCustomerTaxIdStr = UtilProperties.getMessage("OrderUiLabels", "OrderCustomerTaxId", locale);
							description += "" + orderCustomerTaxIdStr + " " + itemAdjustment.customerReferenceId;
						}
						if (itemAdjustment.exemptAmount != null) {
							String orderExemptAmountStr = UtilProperties.getMessage("OrderUiLabels", "OrderExemptAmount", locale);
							description += "" + orderExemptAmountStr + " " + itemAdjustment.exemptAmount;
						}
						taxTotalItemNew.put("description", description);
						listTaxTotal.add(taxTotalItemNew);
					}
				}
			}
		}
		
		if (productId != null) {
			GenericValue productAndTaxAuthorityRate = EntityUtil.getFirst(delegator.findByAnd("ProductAndTaxAuthorityRate", ["productId" : productId], null, false));
			if (productAndTaxAuthorityRate.taxPercentage != null) {
				taxPercentage = productAndTaxAuthorityRate.taxPercentage;
				if (unitPrice != null) {
					BigDecimal taxPercentageAdd = taxPercentage.add(new BigDecimal(100));
					taxPercentageAdd = taxPercentageAdd.divide(new BigDecimal(100));
					unitPriceAfVAT = unitPrice.multiply(taxPercentageAdd);
					unitPriceAfVAT = unitPriceAfVAT.setScale(-2, BigDecimal.ROUND_HALF_UP);
				}
			}
		}
		
		Map<String, Object> itemLine = FastMap.newInstance();
		itemLine.put("seqId", seqId);
		itemLine.put("productId", productId);
		itemLine.put("productName", product.getString("productName"));
		itemLine.put("barcode", barcode);
		itemLine.put("expireDate", expireDate);
		itemLine.put("packingPerTray", packingPerTray);
		itemLine.put("quantityUomId", orderItem.getString("quantityUomId"));
		itemLine.put("quantityUomDescription", quantityUomDescription);
		itemLine.put("quantity", quantity);
		itemLine.put("sumTray", sumTray);
		itemLine.put("unitPriceBeVAT", unitPrice); // before VAT
		itemLine.put("unitPriceAfVAT", unitPriceAfVAT); // after VAT
		itemLine.put("adjustment", adjustment);
		itemLine.put("subTotalBeVAT", subTotalBeVAT);
		itemLine.put("invoicePrice", unitPriceInvoiceAfVAT);
		itemLine.put("invoiceSubTotal", subTotalInvoiceExport);
		itemLine.put("itemDescription", itemDescription);
		itemLine.put("supplierProductId", supplierProductId);
		itemLine.put("product", product);
		itemLine.put("orderItemType", orderItemType);
		listItemLine.add(itemLine);
	}
}
context.listItemLine = listItemLine;
context.listTaxTotal = listTaxTotal;