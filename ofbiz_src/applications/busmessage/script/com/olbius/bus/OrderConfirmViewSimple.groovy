
import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import com.olbius.basesales.order.OrderReadHelper;
import org.ofbiz.order.order.OrderContentWrapper;

import javolution.util.FastMap;

orderId = parameters.orderId;
context.orderId = orderId;

orderHeader = null;
orderItems = null;
orderAdjustments = null;

if (orderId) {
	
	orderConfirm = delegator.findOne("OrderConfirm", [orderId : orderId], false);
	
	if(orderConfirm) {
		orderConfirmId = orderConfirm.get("orderConfirmId");
		
		orderHeader = delegator.findOne("OrderHeader", [orderId : orderConfirmId], false);
	}
	
}

context.orderHeader = orderHeader;

if (orderHeader) {
	// note these are overridden in the OrderViewWebSecure.groovy script if run
	context.hasPermission = true;
	context.canViewInternalDetails = true;

	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();
	orderAdjustments = orderReadHelper.getAdjustments();
	//orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
	//Edit by ViettB
	//orderHeaderAdjustments = orderReadHelper.getNewOrderHeaderAdjustments(orderAdjustments);
	ordCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
	isProCond = EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, "Y");	
	topCond = EntityCondition.makeCondition([ordCond, isProCond], EntityOperator.AND);
	List<GenericValue> listProOrderItems = delegator.findList("OrderItem", topCond, null, null, null, false);
	orderHeaderAdjustments = orderReadHelper.getNewOrderHeaderAdjustments(listProOrderItems);	
	orderSubTotal = orderReadHelper.getNewOrderItemsSubTotal();
	orderTerms = orderHeader.getRelated("OrderTerm", null, null, false);

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
	
	orh = new OrderReadHelper(orderAdjustments, orderItems);
	context.localOrderReadHelper = orh;

	shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
	shippingAmount = shippingAmount.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
	context.shippingAmount = shippingAmount;

	taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
	context.taxAmount = taxAmount;

	//grandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
	grandTotal = OrderReadHelper.getNewOrderGrandTotal(orderItems, orderReadHelper.getNewAdjustments());	
	context.grandTotal = grandTotal;

	orderItemList = orderReadHelper.getOrderItems();
	// Retrieve all non-promo items that aren't cancelled
	context.orderItemList = orderReadHelper.getOrderItems().findAll { item ->
		//(item.isPromo == null || item.isPromo == 'N')  && !(item.statusId.equals('ITEM_CANCELLED'))
		!(item.statusId.equals('ITEM_CANCELLED'))
	}
	
	// TODOCHANGE get list order item association with ship group
	List<GenericValue> orderItemSGList = delegator.findByAnd("OrderItemAndShipGroupAssoc", UtilMisc.toMap("orderId", orderHeader.orderId), UtilMisc.toList("orderItemSeqId"), false);
	// Retrieve all non-promo items that aren't cancelled
	context.orderItemSGList = orderItemSGList.findAll { item ->
		((item.isPromo == null || item.isPromo == 'N') || "PRODPROMO_ORDER_ITEM" == item.orderItemTypeId)  && !(item.statusId.equals('ITEM_CANCELLED'))
	}
	
	context.orderItemSGPromoList = orderItemSGList.findAll { item ->
		!(item.statusId.equals('ITEM_CANCELLED'))
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


	orderItemDatas = [];
	orderItemList.each { orderItem ->
		BigDecimal cancelQuantity = orderItem.get("cancelQuantity");
		BigDecimal quantity = orderItem.get("quantity");
		if ( cancelQuantity != null ) {
			quantityOrdered = quantity.subtract(cancelQuantity);
		} else {
			quantityOrdered = quantity;
		}
		OISGAssContents = [];
		shipGroups.each { shipGroup ->
			OISGAssContents.addAll(EntityUtil.filterByAnd(shipGroup.getRelated("OrderItemShipGroupAssoc"), UtilMisc.toMap("orderItemSeqId", orderItem.getString("orderItemSeqId"))));
		}
		BigDecimal totalQuantityPlanned = 0;
		OISGAssContents.each { OISGAssContent ->
		   BigDecimal cancelQty = OISGAssContent.get("cancelQuantity");
		   BigDecimal qty = OISGAssContent.get("quantity");
		   if (qty != null) {
			   totalQuantityPlanned = totalQuantityPlanned.add(qty);
		   }
		   if (cancelQty != null){
			   OISGAssContent.set("quantity", qty.subtract(cancelQty));
		   } else {
			   OISGAssContent.set("quantity", qty);
		   }
		}
		totalQuantityToPlan = totalQuantityPlanned - quantityOrdered;
		BigDecimal quantityNotAvailable = 0;
		List<GenericValue> oisgirs = orderItem.getRelated("OrderItemShipGrpInvRes", null, null, false);
		for (GenericValue oisgir : oisgirs) {
			if (UtilValidate.isNotEmpty(oisgir.get("quantityNotAvailable"))) {
				quantityNotAvailable = quantityNotAvailable.add(oisgir.getBigDecimal("quantityNotAvailable"));
			}
		}
		orderItemData = [:];
		orderItemData.put("orderItem", orderItem);
		orderItemData.put("OISGAssContents", OISGAssContents);
		orderItemData.put("product", orderItem.getRelatedOne("Product", false));
		orderItemData.put("quantityOrdered", quantityOrdered);
		orderItemData.put("totalQuantityPlanned", totalQuantityPlanned);
		orderItemData.put("totalQuantityToPlan", totalQuantityToPlan);
		orderItemData.put("quantityNotAvailable", quantityNotAvailable);
		orderItemDatas.add(orderItemData);
	}
	context.put("orderItemDatas", orderItemDatas);
	
	// create the actualDate for calendar
	actualDateCal = Calendar.getInstance();
	actualDateCal.setTime(new java.util.Date());
	actualDateCal.set(Calendar.HOUR_OF_DAY, actualDateCal.getActualMinimum(Calendar.HOUR_OF_DAY));
	actualDateCal.set(Calendar.MINUTE, actualDateCal.getActualMinimum(Calendar.MINUTE));
	actualDateCal.set(Calendar.SECOND, actualDateCal.getActualMinimum(Calendar.SECOND));
	actualDateCal.set(Calendar.MILLISECOND, actualDateCal.getActualMinimum(Calendar.MILLISECOND));
	actualDateTs = new Timestamp(actualDateCal.getTimeInMillis());
	actualDateStr = actualDateTs.toString();
	actualDateStr = actualDateStr.substring(0, actualDateStr.indexOf('.'));
	context.put("actualDateStr", actualDateStr);

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
	
	if (userLogin) {
		isPrivateOrg = true;
	} else {
		isPrivateOrg = false;
	}
	if (isPrivateOrg) {
		notes = delegator.findByAnd("OrderHeaderNoteView", [orderId : orderId], ["-noteDateTime"], false);
		context.orderNotes = notes;
	} else {
		notes = delegator.findByAnd("OrderHeaderNoteView", [orderId : orderId, internalNote : 'N'], ["-noteDateTime"], false);
		context.orderNotes = notes;
	}

	showNoteHeadingOnPDF = false;
	if (notes && EntityUtil.filterByCondition(notes, EntityCondition.makeCondition("internalNote", EntityOperator.EQUALS, "N")).size() > 0) {
		showNoteHeadingOnPDF = true;
	}
	context.showNoteHeadingOnPDF = showNoteHeadingOnPDF;

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

	/* TODO comment: not necessary in this case
	// get inventory summary for each shopping cart product item
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
		if (facility) {
			inventorySummaryByFacility = dispatcher.runSync("getProductInventorySummaryForItems", [orderItems : orderItems, facilityId : facility.facilityId]);
			context.availableToPromiseByFacilityMap = inventorySummaryByFacility.availableToPromiseMap;
			context.quantityOnHandByFacilityMap = inventorySummaryByFacility.quantityOnHandMap;
			context.facility = facility;
		}
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
	/* TODO comment: not necessary in this case - need*/
	context.returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities();

	// INVENTORY: construct a Set of productIds in the order for use in querying for inventory, otherwise these queries can get expensive
	productIds = orderReadHelper.getOrderProductIds();

	// INVENTORY: get the production quantity for each product and store the results in a map of productId -> quantity
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

// TODOCHANGE: get from OrderViewWebSecure.groovy
if (orderHeader) {
	orderContentWrapper = OrderContentWrapper.makeOrderContentWrapper(orderHeader, request);
	context.orderContentWrapper = orderContentWrapper;
}

List<GenericValue> orderItemShipGroupList = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
Timestamp defaultItemDeliveryDate = null;
for(GenericValue orderItemShipGroup: orderItemShipGroupList){
	 defaultItemDeliveryDate = orderItemShipGroup.getTimestamp("shipByDate");
}
context.defaultItemDeliveryDate = defaultItemDeliveryDate;

