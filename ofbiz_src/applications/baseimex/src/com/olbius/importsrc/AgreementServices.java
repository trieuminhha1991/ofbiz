package com.olbius.importsrc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.*;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;
import com.olbius.util.JsonUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AgreementServices {
	
	public static final String module = AgreementServices.class.getName();
	public static final String resource = "BaseImExUiLabels";
	public static final String IMEX_PROPERTIES = "imex.properties";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listBillAndContainer(DispatchContext ctx, Map<String, ? extends Object> context)
			throws ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String billIdCtx = null;
		if (parameters.containsKey("billId") && parameters.get("billId").length > 0) {
			billIdCtx = (String) parameters.get("billId")[0];
		}
		if (!"".equals(billIdCtx) && billIdCtx != null) {
			EntityCondition billCond = EntityCondition.makeCondition(UtilMisc.toMap("billId", billIdCtx));
			listAllConditions.add(billCond);
		}
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<GenericValue> listBL = new ArrayList<GenericValue>();
		List<String> orderBy = FastList.newInstance();
		orderBy.add("+billId");
		try {
			Set<String> fieldSl = FastSet.newInstance();
			fieldSl.add("billId");
			fieldSl.add("billNumber");
			fieldSl.add("partyIdFrom");
			fieldSl.add("departureDate");
			fieldSl.add("arrivalDate");
			if (listSortFields.isEmpty()) {
				listSortFields.add("-billId");
			}
			listBL = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "BillOfLading", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			
			if (!UtilValidate.isEmpty(listBL)) {
				for (GenericValue x : listBL) {
					Map<String, Object> row = new HashMap<String, Object>();
					List<Map<String, Object>> listDetailEqualBillId = new ArrayList<Map<String, Object>>();
					row.putAll(x);
					String billId = (String) x.get("billId");
					String partyIdFrom = (String) x.get("partyIdFrom");
					GenericValue objPartyFullNameDetail = null;
					try {
						objPartyFullNameDetail = delegator.findOne("PartyFullNameDetail", false, UtilMisc.toMap("partyId", partyIdFrom));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne PartyFullNameDetail: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (UtilValidate.isNotEmpty(objPartyFullNameDetail)) {
						row.put("partyFromName", objPartyFullNameDetail.getString("fullName"));
						row.put("partyFromCode", objPartyFullNameDetail.getString("partyCode"));
					}
					List<GenericValue> listContainer = delegator.findList("Container",
							EntityCondition.makeCondition(
									UtilMisc.toMap("billId", billId, "containerTypeId", "STANDARD_CONTAINER")),
							null, null, null, false);
					if (!UtilValidate.isEmpty(listContainer)) {
						for (GenericValue container : listContainer) {
							String containerId = (String) container.get("containerId");
							String containerNumber = (String) container.get("containerNumber");
							String sealNumber = (String) container.get("sealNumber");
							String externalOrderNumber = "";
							String agreementName = null;
							String agreementId = null;
							BigDecimal netWeightTotal = new BigDecimal(0);
							BigDecimal grossWeightTotal = new BigDecimal(0);
							Long packingUnitTotal = new Long(0);
							List<GenericValue> listPackingListHeader = delegator.findList("PackingListHeader",
									EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId)), null,
									null, null, false);
							if (!UtilValidate.isEmpty(listPackingListHeader)) {
								int checkSize = listPackingListHeader.size();
								for (GenericValue packingList : listPackingListHeader) {
									// get order of supplier
									checkSize--;
									externalOrderNumber += "SAP-" + (String) packingList.get("externalOrderNumber");
									if (checkSize != 0) {
										externalOrderNumber += " & ";
									} else {
										// get agreement of container
										String purchaseOrderId = (String) packingList.get("purchaseOrderId");
										List<GenericValue> listAgreementAndOrder = delegator
												.findList("AgreementAndOrder",
														EntityCondition.makeCondition(
																UtilMisc.toMap("orderId", purchaseOrderId)),
														null, null, null, false);
										if (!UtilValidate.isEmpty(listAgreementAndOrder)) {
											GenericValue agreement = EntityUtil.getFirst(listAgreementAndOrder);
											GenericValue agreementAttr = delegator
													.findOne("AgreementAndAgreementAttribute",
															UtilMisc.toMap("agreementId",
																	(String) agreement.get("agreementId"), "attrName",
																	"AGREEMENT_NAME"),
															false);
											agreementName = (String) agreementAttr.get("attrValue");
											agreementId = (String) agreement.get("agreementId");
										}
									}
									// Sum Karton
									GenericValue packingListDetailSum = delegator.findOne("PackingListDetailSum",
											UtilMisc.toMap("packingListId", (String) packingList.get("packingListId")),
											false);
									if (packingListDetailSum != null && packingListDetailSum.containsKey("packingUnit")
											&& packingListDetailSum.get("packingUnit") != null) {
										packingUnitTotal += (Long) packingListDetailSum.getLong("packingUnit");
									}
									// Sum netWeight
									netWeightTotal = netWeightTotal.add((BigDecimal) packingList.get("netWeightTotal"));
									// Sum gross Weight
									grossWeightTotal = grossWeightTotal
											.add((BigDecimal) packingList.get("grossWeightTotal"));
								}
							}

							Map<String, Object> rowDetail = FastMap.newInstance();
							rowDetail.put("containerId", containerId);
							rowDetail.put("containerNumber", containerNumber);
							rowDetail.put("sealNumber", sealNumber);
							rowDetail.put("externalOrderNumber", externalOrderNumber);
							rowDetail.put("agreementName", agreementName);
							rowDetail.put("netWeightTotal", netWeightTotal);
							rowDetail.put("grossWeightTotal", grossWeightTotal);
							rowDetail.put("packingUnitTotal", packingUnitTotal);
							rowDetail.put("agreementId", agreementId);
							listDetailEqualBillId.add(rowDetail);
						}
					}
					row.put("rowDetail", listDetailEqualBillId);
					listIterator.add(row);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> listProductShelfLife(DispatchContext ctx, Map<String, ?> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listQualityPublications = delegator.findList("QualityPublication", null,
				UtilMisc.toSet("productId", "qualityPublicationName", "fromDate", "thruDate", "expireDate"), null, null,
				false);
		result.put("listProductShelfLife", listQualityPublications);
		return result;
	}

	public static Map<String, Object> getExternalOrderType(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> listOrderType = FastList.newInstance();
		try {
			listOrderType = delegator.findList("ExternalOrderType", null,
					UtilMisc.toSet("externalOrderTypeId", "externalOrderTypeName"), null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Map<String, Object> mapResult = FastMap.newInstance();
		try {
			mapResult = dispatcher.runSync("getAgreementNotBill", UtilMisc.toMap("userLogin", userLogin));
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		result.put("listAgreementNotBill", mapResult.get("listAgreementNotBill"));
		result.put("listOrderType", listOrderType);
		return result;
	}

	public static Map<String, Object> getAgreementNotBill(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("agreementId");
		fieldToSelects.add("agreementCode");
		List<GenericValue> listAgreementNotBill = delegator.findList(
				"Agreement", EntityCondition.makeCondition(UtilMisc.toMap("statusId",
						"AGREEMENT_PROCESSING", "agreementTypeId", "PURCHASE_AGREEMENT")),
				fieldToSelects, null, null, false);
		result.put("listAgreementNotBill", listAgreementNotBill);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPurchaseAgreements(DispatchContext ctx,
			Map<String, ? extends Object> context) {

		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listIterator = FastList.newInstance();
		
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("agreementTypeId", "PURCHASE_AGREEMENT");
		mapCondition.put("attrName", "AGREEMENT_NAME");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		if (listSortFields.isEmpty()){
			listSortFields.add("-agreementId");
		}
		if (parameters.containsKey("statusId") && parameters.get("statusId").length > 0) {
			String[] statusArr = parameters.get("statusId");
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, Arrays.asList(statusArr)));
		}
		
		try {
			listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PurchaseAgreementDetail", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling listPurchaseAgreements service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	
		
	}

	public static Map<String, Object> getInfomationAgreementsInBill(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String billId = (String) context.get("billId");
		String containerId = (String) context.get("containerId");
		List<GenericValue> listOrderItemTotalInBill = FastList.newInstance();
		EntityCondition condition = null;
		if (UtilValidate.isEmpty(containerId)) {
			condition = EntityCondition.makeCondition(UtilMisc.toMap("billId", billId));
		} else {
			condition = EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId));
		}
		try {
			listOrderItemTotalInBill = delegator.findList("OrderItemTotalInBill", condition, null, null, null, false);
		} catch (Exception e) {
			return result;
		}
		if (UtilValidate.isEmpty(listOrderItemTotalInBill)) {
			return result;
		}
		List<GenericValue> listAgreements = FastList.newInstance();
		List<String> orderIds = EntityUtil.getFieldListFromEntityList(listOrderItemTotalInBill, "orderId", true);
		
		if (!orderIds.isEmpty()){
			listAgreements.addAll(delegator.findList("AgreementAndOrder",
					EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds), null, null, null, false));
		}
		StringBuilder agreementsName = new StringBuilder();
		for (GenericValue x : listAgreements) {
			String agreementId = (String) x.get("agreementId");
			GenericValue agreementAttribute = delegator.findOne("AgreementAttribute", false,
					UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"));
			if (UtilValidate.isNotEmpty(agreementAttribute)) {
				agreementsName.append((String) agreementAttribute.get("attrValue") + "; ");
			}
		}
		agreementsName.delete(agreementsName.length() - 2, agreementsName.length());
		GenericValue agreementAndOrder = EntityUtil.getFirst(listAgreements);
		String agreementId = agreementAndOrder.getString("agreementId");
		GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
		Map<String, Object> mapInfomationAgreements = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(agreement)) {
			String partyIdFrom = (String) agreement.get("partyIdFrom");
			String partyIdTo = (String) agreement.get("partyIdTo");

			List<GenericValue> listETDTerms = delegator.findList("AgreementTerm",
					EntityCondition.makeCondition(
							UtilMisc.toMap("agreementId", agreementId, "termTypeId", "ETD_AGREEMENT_TERM")),
					null, null, null, false);
			List<GenericValue> listETATerms = delegator.findList("AgreementTerm",
					EntityCondition.makeCondition(
							UtilMisc.toMap("agreementId", agreementId, "termTypeId", "ETA_AGREEMENT_TERM")),
					null, null, null, false);
			List<GenericValue> listFinAccounts = delegator.findList("AgreementTerm",
					EntityCondition
							.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "FIN_ACC_TERM")),
					null, null, null, false);
			List<GenericValue> listPortOfChargeTerms = delegator.findList("AgreementTerm",
					EntityCondition
							.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "PORT_OF_CHARGE")),
					null, null, null, false);
			List<GenericValue> listPartialShipmentTerms = delegator.findList("AgreementTerm",
					EntityCondition.makeCondition(
							UtilMisc.toMap("agreementId", agreementId, "termTypeId", "PARTIAL_SHIPMENT")),
					null, null, null, false);
			List<GenericValue> listTransshipmentTerms = delegator.findList("AgreementTerm",
					EntityCondition
							.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "TRANS_SHIPMENT")),
					null, null, null, false);
			List<GenericValue> listDefaultCurrencyTerms = delegator.findList("AgreementTerm",
					EntityCondition.makeCondition(
							UtilMisc.toMap("agreementId", agreementId, "termTypeId", "DEFAULT_PAY_CURRENCY")),
					null, null, null, false);
			List<GenericValue> listOtherCurrencyTerms = delegator.findList("AgreementTerm",
					EntityCondition.makeCondition(
							UtilMisc.toMap("agreementId", agreementId, "termTypeId", "OTHER_PAY_CURRENCY")),
					null, null, null, false);
			List<GenericValue> listOrderByAgreements = delegator.findList("AgreementAndOrder",
					EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
			List<GenericValue> listRepresents = delegator.findList("AgreementRole",
					EntityCondition
							.makeCondition(UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "REPRESENT_LEGAL")),
					null, null, null, false);
			List<GenericValue> listPhoneFroms = delegator.findList(
					"AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId,
							"partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_PHONE")),
					null, null, null, false);
			List<GenericValue> listFaxFroms = delegator.findList(
					"AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId,
							"partyId", partyIdFrom, "contactMechPurposeTypeId", "FAX_NUMBER")),
					null, null, null, false);
			List<GenericValue> listAddressFroms = delegator.findList(
					"AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId,
							"partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_LOCATION")),
					null, null, null, false);
			List<GenericValue> listAddressTos = delegator.findList(
					"AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId,
							"partyId", partyIdTo, "contactMechPurposeTypeId", "PRIMARY_LOCATION")),
					null, null, null, false);
			List<GenericValue> listEmailTos = delegator.findList(
					"AgreementPartyContactMech", EntityCondition.makeCondition(UtilMisc.toMap("agreementId",
							agreementId, "partyId", partyIdTo, "contactMechTypeId", "EMAIL_ADDRESS")),
					null, null, null, false);

			listETDTerms = EntityUtil.filterByDate(listETDTerms);
			listETATerms = EntityUtil.filterByDate(listETATerms);
			listFinAccounts = EntityUtil.filterByDate(listFinAccounts);
			listPortOfChargeTerms = EntityUtil.filterByDate(listPortOfChargeTerms);
			listOrderByAgreements = EntityUtil.filterByDate(listOrderByAgreements);
			listDefaultCurrencyTerms = EntityUtil.filterByDate(listDefaultCurrencyTerms);
			listOtherCurrencyTerms = EntityUtil.filterByDate(listOtherCurrencyTerms);
			listTransshipmentTerms = EntityUtil.filterByDate(listTransshipmentTerms);
			listPartialShipmentTerms = EntityUtil.filterByDate(listPartialShipmentTerms);
			String currentETDTerm = null;
			String currentETATerm = null;
			String currentPortTerm = null;
			if (!listETDTerms.isEmpty()) {
				currentETDTerm = (String) listETDTerms.get(0).get("textValue");
			}
			if (!listETATerms.isEmpty()) {
				currentETATerm = (String) listETATerms.get(0).get("textValue");
			}
			if (!listPortOfChargeTerms.isEmpty()) {
				currentPortTerm = (String) listPortOfChargeTerms.get(0).get("textValue");
			}
			List<GenericValue> listFinAccountFroms = new ArrayList<GenericValue>();
			List<GenericValue> listFinAccountTos = new ArrayList<GenericValue>();
			if (!listFinAccounts.isEmpty()) {
				for (GenericValue finTerm : listFinAccounts) {
					String finAccId = (String) finTerm.get("textValue");
					GenericValue finAcc = delegator.findOne("FinAccount", false,
							UtilMisc.toMap("finAccountId", finAccId));
					if (finAcc != null) {
						String organizationPartyId = (String) finAcc.get("organizationPartyId");
						if (partyIdFrom.equals(organizationPartyId)) {
							listFinAccountFroms.add(finAcc);
						} else {
							if (partyIdTo.equals(organizationPartyId)) {
								listFinAccountTos.add(finAcc);
							}
						}
					}
				}
			}
			List<GenericValue> listProducts = new ArrayList<GenericValue>();
			List<GenericValue> listProductPlanByOrders = new ArrayList<GenericValue>();
			if (!listOrderByAgreements.isEmpty()) {
				for (GenericValue order : listOrderByAgreements) {
					List<GenericValue> orderItems = delegator.findList("SupplierProductAndOrderItem",
							EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String) order.get("orderId"))),
							null, null, null, false);
					if (!orderItems.isEmpty()) {
						listProducts.addAll(orderItems);
					}
					listProductPlanByOrders = delegator.findList("ProductPlanAndOrder",
							EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String) order.get("orderId"))),
							null, null, null, false);
					listProductPlanByOrders = EntityUtil.filterByDate(listProductPlanByOrders);
				}
			}
//			List<GenericValue> listPlanAndLots = new ArrayList<GenericValue>();
			String productPackingUomId = UtilProperties.getPropertyValue(IMEX_PROPERTIES, "imex.import.packing.uom.pallet");
//			if (!listProductPlanByOrders.isEmpty()) {
//				String productPlanId = (String) listProductPlanByOrders.get(0).get("productPlanId");
//				listPlanAndLots = delegator.findList("ProductPlanAndLot",
//						EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null,
//						false);
//				if (!listPlanAndLots.isEmpty()) {
//					productPackingUomId = (String) listPlanAndLots.get(0).get("productPackingUomId");
//				}
//			}
			String currencyUomId = null;
			if (!listDefaultCurrencyTerms.isEmpty()) {
				currencyUomId = (String) listDefaultCurrencyTerms.get(0).get("textValue");
			}
			String representPartyId = null;
			if (!listRepresents.isEmpty()) {
				representPartyId = (String) listRepresents.get(0).get("partyId");
			}
			String contactMechPhoneFromId = null;
			String contactMechFaxFromId = null;
			String contactMechAddressFromId = null;
			String contactMechAddressToId = null;
			String contactMechEmailToId = null;
			if (!listPhoneFroms.isEmpty()) {
				contactMechPhoneFromId = (String) listPhoneFroms.get(0).get("contactMechId");
			}
			if (!listFaxFroms.isEmpty()) {
				contactMechFaxFromId = (String) listFaxFroms.get(0).get("contactMechId");
			}
			if (!listAddressFroms.isEmpty()) {
				contactMechAddressFromId = (String) listAddressFroms.get(0).get("contactMechId");
			}
			if (!listAddressTos.isEmpty()) {
				contactMechAddressToId = (String) listAddressTos.get(0).get("contactMechId");
			}
			if (!listEmailTos.isEmpty()) {
				contactMechEmailToId = (String) listEmailTos.get(0).get("contactMechId");
			}
			String transshipment = null;
			String partialShipment = null;
			if (!listTransshipmentTerms.isEmpty()) {
				transshipment = (String) listTransshipmentTerms.get(0).get("textValue");
			}
			if (!listPartialShipmentTerms.isEmpty()) {
				partialShipment = (String) listPartialShipmentTerms.get(0).get("textValue");
			}

			mapInfomationAgreements.put("transshipment", transshipment);
			mapInfomationAgreements.put("partialShipment", partialShipment);
			mapInfomationAgreements.put("currentETDTerm", currentETDTerm);
			mapInfomationAgreements.put("currentETATerm", currentETATerm);
			mapInfomationAgreements.put("currentPortTerm", currentPortTerm);
			mapInfomationAgreements.put("currencyUomId", currencyUomId);
			mapInfomationAgreements.put("representPartyId", representPartyId);
			mapInfomationAgreements.put("listFinAccountTos", listFinAccountTos);
			mapInfomationAgreements.put("listFinAccountFroms", listFinAccountFroms);
			mapInfomationAgreements.put("contactMechPhoneFromId", contactMechPhoneFromId);
			mapInfomationAgreements.put("contactMechFaxFromId", contactMechFaxFromId);
			mapInfomationAgreements.put("contactMechAddressFromId", contactMechAddressFromId);
			mapInfomationAgreements.put("contactMechAddressToId", contactMechAddressToId);
			mapInfomationAgreements.put("contactMechEmailToId", contactMechEmailToId);
			mapInfomationAgreements.put("productPackingUomId", productPackingUomId);
			mapInfomationAgreements.put("listOtherCurrencyTerms", listOtherCurrencyTerms);
		}
		mapInfomationAgreements.put("listOrderItemTotalInBill", listOrderItemTotalInBill);
		mapInfomationAgreements.put("listAgreementName", agreementsName.toString());
		mapInfomationAgreements.put("agreement", agreement);
		result.put("mapInfomationAgreements", mapInfomationAgreements);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JQGetBillForAcc(DispatchContext ctx, Map<String, ? extends Object> context)
			throws ParseException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		SimpleDateFormat yearMonthDayFormat2 = new SimpleDateFormat("dd/MM/yyyy");
		Map<String, String[]> param = (Map<String, String[]>) context.get("parameters");
		String party = (String) param.get("party")[0];
		List<GenericValue> listGe = new ArrayList<GenericValue>();
		try {
			listGe = delegator.findList("BillOfLading", EntityCondition.makeCondition(listAllConditions), null,
					listSortFields, opts, false);
			List<GenericValue> listBL = EntityUtil.filterByDate(listGe);
			if (!UtilValidate.isEmpty(listBL)) {
				for (GenericValue x : listBL) {
					Map<String, Object> row = new HashMap<String, Object>();
					String billId = (String) x.get("billId");
					row.put("billId", billId);
					row.put("billNumber", (String) x.get("billNumber"));
					Timestamp deparDate = (Timestamp) x.get("departureDate");
					if (deparDate != null) {
						String departureDate = yearMonthDayFormat2.format(new Date(deparDate.getTime()));
						row.put("departureDate", departureDate);
					}
					Timestamp arrivalDateStamp = (Timestamp) x.get("arrivalDate");
					if (arrivalDateStamp != null) {
						String arrivalDate = yearMonthDayFormat2.format(new Date(arrivalDateStamp.getTime()));
						row.put("arrivalDate", arrivalDate);
					}

					List<GenericValue> listDetailCost = delegator.findList("CostAccBaseAndType",
							EntityCondition.makeCondition(
									UtilMisc.toMap("costAccountingTypeId", "COST_BILLOFLA", "departmentId", party)),
							null, null, null, false);
					List<GenericValue> listDetail = EntityUtil.filterByDate(listDetailCost);
					List<Map<String, String>> rowDetail = new ArrayList<Map<String, String>>();
					if (!UtilValidate.isEmpty(listDetail)) {
						for (GenericValue detail : listDetail) {
							Map<String, String> childDetail = new HashMap<String, String>();
							String costAccBaseId = (String) detail.get("costAccBaseId");
							childDetail.put("costAccBaseId", costAccBaseId);
							childDetail.put("invoiceItemTypeId", (String) detail.get("invoiceItemTypeId"));
							childDetail.put("description", (String) detail.get("description"));
							childDetail.put("billId", billId);
							String costBillAccId = "";
							BigDecimal costPriceTemporary = new BigDecimal(0);
							BigDecimal costPriceActual = new BigDecimal(0);
							List<GenericValue> listCostBillAcc = delegator.findList("CostBillAccounting",
									EntityCondition.makeCondition(
											UtilMisc.toMap("costAccBaseId", costAccBaseId, "billOfLadingId", billId)),
									null, null, null, false);
							if (!UtilValidate.isEmpty(listCostBillAcc)) {
								GenericValue costBillAcc = EntityUtil.getFirst(listCostBillAcc);
								costBillAccId = (String) costBillAcc.get("costBillAccountingId");
								costPriceTemporary = (BigDecimal) costBillAcc.get("costPriceTemporary");
								costPriceActual = (BigDecimal) costBillAcc.get("costPriceActual");
							}
							childDetail.put("costBillAccountingId", costBillAccId);
							childDetail.put("costPriceTemporary", Integer.toString(costPriceTemporary.intValue()));
							childDetail.put("costPriceActual", Integer.toString(costPriceActual.intValue()));
							rowDetail.add(childDetail);
						}
					}
					row.put("rowDetail", rowDetail);
					listIterator.add(row);
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> updateCostBillAcc(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String billId = (String) context.get("billId");
		String invoiceItemTypeId = (String) context.get("invoiceItemTypeId");
		String costAccBaseId = (String) context.get("costAccBaseId");
		BigDecimal exchangedRate = BigDecimal.ONE;
		if(UtilValidate.isNotEmpty(context.get("exchangedRate"))) {
		    exchangedRate = new BigDecimal((String) context.get("exchangedRate"));
        }
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		GenericValue costBaseId = null;
		GenericValue costBaseOrderId = null;
		try {
			List<GenericValue> listCostBaseId = delegator.findList(
					"CostAccBase", EntityCondition.makeCondition(UtilMisc.toMap("costAccBaseId", costAccBaseId,
							"invoiceItemTypeId", invoiceItemTypeId, "costAccountingTypeId", "COST_BILLOFLA")),
					null, null, null, false);
			costBaseId = EntityUtil.getFirst(listCostBaseId);
		} catch (GenericEntityException e2) {
			return ServiceUtil.returnError(e2.getMessage());
		}
		List<GenericValue> listOrderCostAccBase = new ArrayList<GenericValue>();
		if (costBaseId != null) {
			try {
				listOrderCostAccBase = delegator
						.findList("CostAccBase",
								EntityCondition.makeCondition(UtilMisc.toMap("departmentId",
										(String) costBaseId.get("departmentId"), "invoiceItemTypeId", invoiceItemTypeId,
										"costAccountingTypeId", "COST_ORDER")),
								null, null, null, false);
				costBaseOrderId = EntityUtil.getFirst((EntityUtil.filterByDate(listOrderCostAccBase)));

			} catch (GenericEntityException e1) {
				return ServiceUtil.returnError(e1.getMessage());
			}
		}
		String costBillAccountingId = (String) context.get("costBillAccountingId");
		String costPriceTemporary = (String) context.get("costPriceTemporary");
		String costPriceActual = (String) context.get("costPriceActual");
		BigDecimal costTemp = new BigDecimal(costPriceTemporary);
		BigDecimal costAc = new BigDecimal(costPriceActual);

		if (UtilValidate.isEmpty(costBillAccountingId)) {
			costBillAccountingId = delegator.getNextSeqId("CostBillAccounting");
			GenericValue costBillAcc = delegator.makeValue("CostBillAccounting");
			costBillAcc.put("costBillAccountingId", costBillAccountingId);
			costBillAcc.put("costAccBaseId", costAccBaseId);
			costBillAcc.put("billOfLadingId", billId);
			costBillAcc.put("costPriceTemporary", costTemp);
			costBillAcc.put("costPriceActual", costAc);
			costBillAcc.put("exchangedRate", exchangedRate);
			try {
				delegator.create(costBillAcc);
				Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("billId", billId);
				mapContext.put("costAccBaseId", costBaseOrderId != null ? costBaseOrderId.get("costAccBaseId") : null);
				mapContext.put("applicationBaseId", costBaseOrderId.get("applicationBaseId"));
                mapContext.put("costPriceTemporary", costTemp);
                mapContext.put("exchangedRate", exchangedRate);
				mapContext.put("userLogin", userLogin);
				try {
					Map<String, Object> rs = dispatcher.runSync("allocationCostFromBLToOrder", mapContext);
					if (ServiceUtil.isError(rs)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
					}
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when run service allocationCostFromBLToOrder: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		} else {
			GenericValue costBillAcc;
			try {
				costBillAcc = delegator.findOne("CostBillAccounting",
						UtilMisc.toMap("costBillAccountingId", costBillAccountingId), false);
				costBillAcc.put("costPriceTemporary", costTemp);
                costBillAcc.put("costPriceActual", costAc);
                costBillAcc.put("exchangedRate", exchangedRate);
				delegator.store(costBillAcc);

				Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("billId", billId);
				mapContext.put("costAccBaseId", (String) costBaseOrderId.get("costAccBaseId"));
				mapContext.put("applicationBaseId", (String) costBaseOrderId.get("applicationBaseId"));
				mapContext.put("costPriceTemporary", costTemp);
                mapContext.put("exchangedRate", exchangedRate);
                mapContext.put("userLogin", userLogin);
				try {
					Map<String, Object> rs = dispatcher.runSync("allocationCostFromBLToOrder", mapContext);
					if (ServiceUtil.isError(rs)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
					}
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when run service allocationCostFromBLToOrder: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		result.put("costBillAccountingId", costBillAccountingId);
		return result;
	}

	public static Map<String, Object> getDocumentCustomsByContainer(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> resultReturn = FastMap.newInstance();
		String containerId = (String) context.get("containerId");
		String documentCustomsTypeId = (String) context.get("documentCustomsTypeId");
		String documentCustomsId = "";
		String registerNumber = "";
		String registerDate = "";
		String sampleSendDate = "";
		List<GenericValue> listDocumentCustoms = delegator.findList("DocumentCustoms",
				EntityCondition.makeCondition(
						UtilMisc.toMap("containerId", containerId, "documentCustomsTypeId", documentCustomsTypeId)),
				null, null, null, false);
		GenericValue documentCustoms = null;
		if (!UtilValidate.isEmpty(listDocumentCustoms)) {
			documentCustoms = EntityUtil.getFirst(listDocumentCustoms);
			documentCustomsId = (String) documentCustoms.get("documentCustomsId");
			registerNumber = (String) documentCustoms.get("registerNumber");
			registerDate = ((Date) documentCustoms.get("registerDate")).toString();
			sampleSendDate = ((Date) documentCustoms.get("sampleSendDate")).toString();
		}
		result.put("documentCustomsId", documentCustomsId);
		result.put("registerNumber", registerNumber);
		result.put("registerDate", registerDate);
		result.put("sampleSendDate", sampleSendDate);
		resultReturn.put("resultListDoc", result);
		return resultReturn;
	}

	@SuppressWarnings("static-access")
	public static Map<String, Object> deleteAgreementFromBillAjax(DispatchContext ctx, Map<String, ?> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String jsonData = (String) context.get("data");
		JSONArray jsonArr = new JSONArray().fromObject(jsonData);
		for (int i = 0; i < jsonArr.size(); i++) {
			JSONObject rowData = jsonArr.getJSONObject(i);
			String agreementId = (String) rowData.get("agreementId");
			String billAgreementId = (String) rowData.get("billAgreementId");
			String billId = (String) rowData.get("billId");
			String orderId = (String) rowData.get("orderId");
			String containerId = (String) rowData.get("containerId");

			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			String curSttAgreement = (String) agreement.get("statusId");
			if (curSttAgreement.equals("AGREEMENT_PROCESSING")) {
				agreement.put("statusId", "AGREEMENT_SENT");
				try {
					delegator.store(agreement);
				} catch (GenericEntityException e) {
					break;
				}
			}
			GenericValue bOLAndAgreement = delegator.findOne("BillOfLadingAndAgreement",
					UtilMisc.toMap("billAgreementId", billAgreementId), false);
			if (bOLAndAgreement != null) {
				try {
					delegator.removeValue(bOLAndAgreement);
				} catch (GenericEntityException e) {
					break;
				}
			}
			// remove orderandcontainer, container
			GenericValue orderAndContainer = delegator.findOne("OrderAndContainer",
					UtilMisc.toMap("orderId", orderId, "billId", billId), false);
			if (orderAndContainer != null) {
				try {
					delegator.removeValue(orderAndContainer);
				} catch (GenericEntityException e) {
					break;
				}
			}
			GenericValue container = delegator.findOne("Container", UtilMisc.toMap("containerId", containerId), false);
			if (container != null) {
				try {
					delegator.removeValue(container);
				} catch (GenericEntityException e) {
					break;
				}
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("agreementId");
		fieldToSelects.add("attrValue");
		List<GenericValue> listAgreementNotBill = delegator.findList(
				"AgreementAndAgreementAttribute", EntityCondition.makeCondition(UtilMisc.toMap("statusId",
						"AGREEMENT_PROCESSING", "agreementTypeId", "PURCHASE_AGREEMENT", "attrName", "AGREEMENT_NAME")),
				fieldToSelects, null, null, false);
		result.put("listAgreementNotBill", listAgreementNotBill);
		return result;
	}

	public static Map<String, Object> updatePurchaseAgreement(DispatchContext ctx, Map<String, Object> context)
			throws ParseException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispathcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String agreementId = null;
		String listOrderItems = (String) context.get("orderItems");
		String productPlanId = (String) context.get("productPlanId");
		JSONArray arrOrderItems = JSONArray.fromObject(listOrderItems);

		String portOfDischargeId = (String) context.get("portOfDischargeId");
		String partyIdFrom = (String) context.get("partyIdFrom");
		String addressIdFrom = (String) context.get("addressIdFrom");
		String telephoneIdFrom = (String) context.get("telephoneIdFrom");
		String faxNumberIdFrom = (String) context.get("faxNumberIdFrom");
		String transshipment = (String) context.get("transshipment");
		String partialShipment = (String) context.get("partialShipment");
		String partyIdTo = (String) context.get("partyIdTo");
		String addressIdTo = (String) context.get("addressIdTo");
		String emailAddressIdTo = (String) context.get("emailAddressIdTo");
		
		String finAccountId = null;
		if (UtilValidate.isNotEmpty(context.get("finAccountId"))) {
			finAccountId = (String) context.get("finAccountId");
		}
		
		String exportPort = null;
		if (UtilValidate.isNotEmpty(context.get("exportPort"))) {
			exportPort = (String) context.get("exportPort");
		}
		
		Map<String, Object> mapParameters = new FastMap<String, Object>();
		Timestamp agreementDate = new Timestamp((Long) context.get("agreementDate"));
		String agreementName = (String) context.get("agreementName");
		Timestamp fromDate = null;
		String currencyUomId = null;
		if (UtilValidate.isNotEmpty(context.get("currencyUomId"))) {
			currencyUomId = (String) context.get("currencyUomId");
		}
		context.put("agreementDate", agreementDate);
		fromDate = agreementDate;
		try {
			context.put("statusId", "AGREEMENT_CREATED");
			Map<String, Object> resultTmp = dispathcher.runSync("createAgreement", context);
			agreementId = (String) resultTmp.get("agreementId");

			mapParameters.put("agreementId", agreementId);
			mapParameters.put("partyId", partyIdFrom);
			mapParameters.put("contactMechId", addressIdFrom);
			mapParameters.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
			mapParameters.put("userLogin", userLogin);
			mapParameters.put("userLogin", userLogin);
			mapParameters.put("fromDate", fromDate);
			dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);

			mapParameters = new FastMap<String, Object>();
			mapParameters.put("agreementId", agreementId);
			mapParameters.put("partyId", partyIdFrom);
			mapParameters.put("contactMechId", telephoneIdFrom);
			mapParameters.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
			mapParameters.put("userLogin", userLogin);
			mapParameters.put("fromDate", fromDate);
			dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);

			mapParameters = new FastMap<String, Object>();
			mapParameters.put("agreementId", agreementId);
			mapParameters.put("partyId", partyIdFrom);
			mapParameters.put("contactMechId", faxNumberIdFrom);
			mapParameters.put("contactMechPurposeTypeId", "FAX_NUMBER");
			mapParameters.put("userLogin", userLogin);
			mapParameters.put("fromDate", fromDate);
			dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);

			mapParameters = new FastMap<String, Object>();
			mapParameters.put("agreementId", agreementId);
			mapParameters.put("partyId", partyIdTo);
			mapParameters.put("contactMechId", addressIdTo);
			mapParameters.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
			mapParameters.put("userLogin", userLogin);
			mapParameters.put("fromDate", fromDate);
			dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);

			mapParameters = new FastMap<String, Object>();
			mapParameters.put("agreementId", agreementId);
			mapParameters.put("partyId", partyIdTo);
			mapParameters.put("contactMechId", emailAddressIdTo);
			mapParameters.put("contactMechTypeId", "EMAIL_ADDRESS");
			mapParameters.put("userLogin", userLogin);
			mapParameters.put("fromDate", fromDate);
			dispathcher.runSync("createAgreementPartyContactMech", mapParameters);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		if (UtilValidate.isNotEmpty(finAccountId)) {
			GenericValue objFinAccount = null;
			try {
				objFinAccount = delegator.findOne("FinAccount", false, UtilMisc.toMap("finAccountId", finAccountId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne FinAccount: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (UtilValidate.isNotEmpty(objFinAccount)) {
				updateAgreementTerm(delegator, agreementId, "FIN_PAYMENT_BANK_ACCOUNT", finAccountId);
			}
		}

		updateAgreementTerm(delegator, agreementId, "TRANS_SHIPMENT", transshipment);
		updateAgreementTerm(delegator, agreementId, "PARTIAL_SHIPMENT", partialShipment);

		if (agreementName != null) {
			GenericValue agrName = delegator.makeValue("AgreementAttribute");
			agrName.put("agreementId", agreementId);
			agrName.put("attrName", "AGREEMENT_NAME");
			agrName.put("attrValue", agreementName);
			try {
				delegator.createOrStore(agrName);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		if (portOfDischargeId != null) {
			updateAgreementTerm(delegator, agreementId, "PORT_OF_CHARGE", portOfDischargeId);
		}
		if (UtilValidate.isNotEmpty(exportPort)) {
			updateAgreementTerm(delegator, agreementId, "PORT_EXPORT", exportPort);
		}
		Locale locale = (Locale)context.get("locale");
		String agreementItemSeqId = null;
		if (currencyUomId != null) {
			updateAgreementTerm(delegator, agreementId, "DEFAULT_PAY_CURRENCY", currencyUomId);
			GenericValue item = delegator.makeValue("AgreementItem");
			delegator.setNextSubSeqId(item, "agreementItemSeqId", 5, 1);
			item.put("agreementId", agreementId);
			item.put("agreementItemTypeId", "AGREEMENT_PRICING_PR");
			item.put("currencyUomId", currencyUomId);
			try {
				delegator.createOrStore(item);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
			agreementItemSeqId = item.getString("agreementItemSeqId");
		} else {
			ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIECurrencyUomNotFoundToCreateAgreement", locale));
		}
		
		int size = arrOrderItems.size();
		for (int i = 0; i < size; i++) {
			JSONObject orderItem = arrOrderItems.getJSONObject(i);
			String productId = (String) orderItem.get("productId");
			String priceStr = orderItem.getString("lastPrice");
			String quantityStr = orderItem.getString("quantity");
			BigDecimal quantity = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(quantityStr)) {
				quantity = new BigDecimal(quantityStr);
			}
			if (quantity.compareTo(BigDecimal.ZERO) > 0){
				try {
					List<GenericValue> listSupplierProduct = delegator
							.findList(
									"SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId,
											"currencyUomId", currencyUomId, "partyId", partyIdTo)),
									null, null, null, false);
					if (UtilValidate.isEmpty(listSupplierProduct)) {
						GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						Map<String, Object> mapSupp = FastMap.newInstance();
						mapSupp.put("partyId", partyIdTo);
						mapSupp.put("currencyUomId", currencyUomId);
						mapSupp.put("quantityUomId", objProduct.getString("quantityUomId"));
						mapSupp.put("userLogin", userLogin);
						mapSupp.put("availableFromDate", ((Long) context.get("fromDate")).toString());
						mapSupp.put("lastPrice", priceStr);
						mapSupp.put("minimumOrderQuantity", "1");
						mapSupp.put("productId", productId);
	
						try {
							dispatcher.runSync("addNewSupplierForProductId", mapSupp);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError(e.getMessage());
						}
					}
	
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError(e.getMessage());
				}
				BigDecimal price = new BigDecimal(priceStr);
				// create AgreementProductAppl
				String orderId = null;
				if (orderItem.containsKey("orderId")){
					orderId = orderItem.getString("orderId");
				}
				String orderItemSeqId = null;
				if (orderItem.containsKey("orderItemSeqId")){
					orderItemSeqId = orderItem.getString("orderItemSeqId");
				}
				String quantityUomId = null;
				if (orderItem.containsKey("quantityUomId")){
					quantityUomId = orderItem.getString("quantityUomId");
				}
				GenericValue item = delegator.makeValue("AgreementProductAppl");
				item.put("agreementItemSeqId", agreementItemSeqId);
				item.put("agreementId", agreementId);
				item.put("productId", productId);
				item.put("orderId", orderId);
				item.put("orderItemSeqId", orderItemSeqId);
				item.put("quantityUomId", quantityUomId);
				item.put("price", price);
				item.put("quantity", quantity);
				try {
					delegator.createOrStore(item);
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError(e.getMessage());
				}
			}
		}
		// update productplan relation agreement
		GenericValue objAgreement = null;
		try {
			objAgreement = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Agreement: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String orderId = null;
		if (UtilValidate.isNotEmpty(context.get("orderId"))) {
			orderId = (String) context.get("orderId");
		}
		if (UtilValidate.isNotEmpty(orderId)) {
			GenericValue agreementAndOrderNew = delegator.makeValue("AgreementAndOrder");
			agreementAndOrderNew.put("orderId", orderId);
			agreementAndOrderNew.put("agreementId", agreementId);
			agreementAndOrderNew.put("fromDate", UtilDateTime.nowTimestamp());
			try {
				delegator.createOrStore(agreementAndOrderNew);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
			if (UtilValidate.isEmpty(productPlanId)) {
				List<GenericValue> listProductPlanAndOrder = FastList.newInstance();
				try {
					listProductPlanAndOrder = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition("orderId", orderId), null, null,
							null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList ProductPlanAndOrder: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listProductPlanAndOrder.isEmpty()){
					productPlanId = listProductPlanAndOrder.get(0).getString("productPlanId");
				}
			}
		}
		if (UtilValidate.isNotEmpty(productPlanId)) {
			objAgreement.put("productPlanId", productPlanId);
			try {
				delegator.store(objAgreement);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		
		result.put("agreementId", agreementId);
		result.put("productPlanId", productPlanId);
		return result;
	}
	
	public static Map<String, Object> editPurchaseAgreement(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String agreementId = (String)context.get("agreementId");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Locale locale = (Locale)context.get("locale");
		GenericValue agreementGV = null;
		try {
			agreementGV = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Agreement: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(agreementGV)) {
			String errMsg = "OLBIUS: Fatal error when updatePurchaseAgreementItem - Agreement not found!: " + agreementId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		String agreementName = null;
		if (UtilValidate.isNotEmpty(context.get("agreementName"))) {
			agreementName = (String) context.get("agreementName");	
		}
		
		if (agreementName != null) {
			GenericValue agrName = delegator.makeValue("AgreementAttribute");
			agrName.put("agreementId", agreementId);
			agrName.put("attrName", "AGREEMENT_NAME");
			agrName.put("attrValue", agreementName);
			try {
				delegator.createOrStore(agrName);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		
		String agreementCode = null;
		if (UtilValidate.isNotEmpty(context.get("agreementCode"))) {
			agreementCode = (String) context.get("agreementCode");
			
		}
		Timestamp agreementDate = null;
		if (UtilValidate.isNotEmpty(context.get("agreementDate"))) {
			String agreementDateStr = (String)context.get("agreementDate");
			if (UtilValidate.isNotEmpty(agreementDateStr)) {
				agreementDate = new Timestamp(new Long(agreementDateStr));
			}
		}
		Timestamp fromDate = null;
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			String fromDateStr = (String)context.get("fromDate");
			if (UtilValidate.isNotEmpty(fromDateStr)) {
				fromDate = new Timestamp(new Long(fromDateStr));
			}
		}
		Timestamp thruDate = null;
		if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
			String thruDateStr = (String)context.get("thruDate");
			if (UtilValidate.isNotEmpty(thruDateStr)) {
				thruDate = new Timestamp(new Long(thruDateStr));
			}
		}
		agreementGV.put("agreementDate", agreementDate);
		agreementGV.put("fromDate", fromDate);
		agreementGV.put("thruDate", thruDate);
		agreementGV.put("agreementCode", agreementCode);
		//update agreement
		try {
			agreementGV.store();
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when Store Agreement: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String listTest = (String) context.get("listProductUpdates");
		// update agreemnent product apply and agreement item
		if (UtilValidate.isNotEmpty(context.get("listProductUpdates"))) {
			String productStr = (String)context.get("listProductUpdates");
			List<Map<String, Object>> listProducts = null;
			try {
				listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", productStr);
			} catch (ParseException e) {
				return ServiceUtil.returnError("OLBIUS: updateAgreement error when JqxWidgetSevices.convert ! " + e.toString());
			}
			
			for (Map<String, Object> pt : listProducts) {
				if ( UtilValidate.isNotEmpty(pt.get("agreementItemSeqId"))) {
					String agreementItemSeqId = (String)pt.get("agreementItemSeqId");
					String productId = (String)pt.get("productId");
					GenericValue ProductAppl = null;
					GenericValue AgreementItem = null;
					try {
						AgreementItem = delegator.findOne("AgreementItem", false, UtilMisc.toMap("agreementId", agreementId, "agreementItemSeqId", agreementItemSeqId));
						ProductAppl = delegator.findOne("AgreementProductAppl", false, UtilMisc.toMap("agreementId", agreementId, "agreementItemSeqId", agreementItemSeqId, "productId",productId ));
					} catch (GenericEntityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if (UtilValidate.isNotEmpty(pt.get("currencyUomId"))) {
						String currencyUomId = (String)pt.get("currencyUomId");
						AgreementItem.put("currencyUomId", currencyUomId);
					}
					if (UtilValidate.isNotEmpty(pt.get("quantity"))) {
						String quantityStr = (String)pt.get("quantity");
						if (UtilValidate.isNotEmpty(quantityStr)) {
							ProductAppl.put("quantity", new BigDecimal(quantityStr));
						}
					}
					if (UtilValidate.isNotEmpty(pt.get("lastPrice"))) {
						String lastPriceStr = (String)pt.get("lastPrice");
						if (UtilValidate.isNotEmpty(lastPriceStr)) {
							ProductAppl.put("price", new BigDecimal(lastPriceStr));
						}
					}
					try {
						ProductAppl.store();
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: updatePurchaseAgreementProductAppl error! " + e.toString());
					}
					try {
						AgreementItem.store();
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: updatePurchaseAgreementItem error! " + e.toString());
					}
				}
			}
		}
		// add
		if (UtilValidate.isNotEmpty(context.get("listProductNews"))) {
			String productStr = (String)context.get("listProductNews");
			List<Map<String, Object>> listProducts = null;
			try {
				listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", productStr);
			} catch (ParseException e) {
				return ServiceUtil.returnError("OLBIUS: updateAgreement error when JqxWidgetSevices.convert ! " + e.toString());
			}
			
			for (Map<String, Object> pt : listProducts) {
				if ( UtilValidate.isNotEmpty(pt.get("productId"))) {
					String productId = (String)pt.get("productId");
					String quantityStr = null;
					if (UtilValidate.isNotEmpty(pt.get("quantity"))){
						quantityStr = (String)pt.get("quantity");
					}
					String lastPriceStr = null;
					if (UtilValidate.isNotEmpty(pt.get("lastPrice"))){
						lastPriceStr = (String)pt.get("lastPrice");
					}
					if (UtilValidate.isNotEmpty(productId)) {
						BigDecimal quantity = null;
						BigDecimal lastPrice = null;
						String currencyUomId = null; 
						if (UtilValidate.isNotEmpty(quantityStr)){
							quantity = new BigDecimal(quantityStr);
						}
						if (UtilValidate.isNotEmpty(lastPriceStr)){
							lastPrice = new BigDecimal(lastPriceStr);
						}
						if (UtilValidate.isNotEmpty(pt.get("currencyUomId"))) {
							currencyUomId = (String)pt.get("currencyUomId");
						}
						Map<String, Object> mapTmp = FastMap.newInstance();
						mapTmp.put("agreementId", agreementId);
						mapTmp.put("quantity", quantity);
						mapTmp.put("price", lastPrice);
						mapTmp.put("productId", productId);
						mapTmp.put("currencyUomId", currencyUomId);
						
						// add agreement item
						String agreementItemSeqId = null;
						if (currencyUomId != null) {
							updateAgreementTerm(delegator, agreementId, "DEFAULT_PAY_CURRENCY", currencyUomId);
							GenericValue item = delegator.makeValue("AgreementItem");
							delegator.setNextSubSeqId(item, "agreementItemSeqId", 5, 1);
							item.put("agreementId", agreementId);
							item.put("agreementItemTypeId", "AGREEMENT_PRICING_PR");
							item.put("currencyUomId", currencyUomId);
							try {
								delegator.createOrStore(item);
							} catch (GenericEntityException e) {
								return ServiceUtil.returnError(e.getMessage());
							}
							agreementItemSeqId = item.getString("agreementItemSeqId");
						} else {
							ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIECurrencyUomNotFoundToCreateAgreement", locale));
						}
						
						//add agreement product appl
						String orderId = null;
						if (UtilValidate.isNotEmpty(pt.get("orderId"))){
							orderId = (String)pt.get("orderId");
						}
						String orderItemSeqId = null;
						if (UtilValidate.isNotEmpty(pt.get("orderItemSeqId"))){
							orderItemSeqId = (String)pt.get("orderItemSeqId");
						}
						String quantityUomId = null;
						if (UtilValidate.isNotEmpty(pt.get("quantityUomId"))){
							quantityUomId = (String)pt.get("quantityUomId");
						}
						GenericValue item = delegator.makeValue("AgreementProductAppl");
						item.put("agreementItemSeqId", agreementItemSeqId);
						item.put("agreementId", agreementId);
						item.put("productId", productId);
						item.put("orderId", orderId);
						item.put("orderItemSeqId", orderItemSeqId);
						item.put("quantityUomId", quantityUomId);
						item.put("price", lastPrice);
						item.put("quantity", quantity);
						try {
							delegator.createOrStore(item);
						} catch (GenericEntityException e) {
							return ServiceUtil.returnError(e.getMessage());
						}
					}
				}
			}
		}
		
		
		// cancel
		if (UtilValidate.isNotEmpty(context.get("listProductCancels"))) {
			String productStr = (String)context.get("listProductCancels");
			List<Map<String, Object>> listProducts = null;
			try {
				listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", productStr);
			} catch (ParseException e) {
				return ServiceUtil.returnError("OLBIUS: updateAgreement error when JqxWidgetSevices.convert ! " + e.toString());
			}
			for (Map<String, Object> pt : listProducts) {
				String agreementItemSeqId = (String)pt.get("agreementItemSeqId");
				String productId = (String)pt.get("productId");
				try {
					GenericValue ProductAppl = delegator.findOne("AgreementProductAppl", false, UtilMisc.toMap("agreementId", agreementId, "agreementItemSeqId", agreementItemSeqId, "productId", productId)); 
					ProductAppl.remove();
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError("OLBIUS: CancelAgreementProductAppl error! " + e.toString());
				}
				
			}
		}
		
		success.put("agreementId", agreementId);
		return success;
	}


	public static void updateAgreementTerm(Delegator delegator, String agreementId, String termTypeIdValue, String textValue) {
		try {
			List<GenericValue> listTerm = delegator.findList("AgreementTerm",
					EntityCondition
							.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", termTypeIdValue)),
					null, null, null, false);
			listTerm = EntityUtil.filterByDate(listTerm);
			Boolean check = false;
			if (!listTerm.isEmpty()) {
				for (GenericValue term : listTerm) {
					if (!textValue.equals((String) term.get("textValue"))) {
						term.put("thruDate", UtilDateTime.nowTimestamp());
						delegator.store(term);
					} else
						check = true;
				}
			}
			if (!check) {
				GenericValue currentTerm = delegator.makeValue("AgreementTerm");
				currentTerm.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
				currentTerm.put("termTypeId", termTypeIdValue);
				currentTerm.put("agreementId", agreementId);
				currentTerm.put("textValue", textValue);
				currentTerm.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.createOrStore(currentTerm);
			}
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
	}

	public static Map<String, Object> createPurchaseOrderFromAgreement(DispatchContext ctx, Map<String, Object> context) {
		String agreementId = (String)context.get("agreementId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue objAgreement = null;
		try {
			objAgreement = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Agreement: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		if (UtilValidate.isEmpty(objAgreement)) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BEIAgeementNotFound", locale));
		}
		if (objAgreement.getTimestamp("fromDate").compareTo(UtilDateTime.nowTimestamp()) > 0 || objAgreement.getTimestamp("agreementDate").compareTo(UtilDateTime.nowTimestamp()) > 0){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIECannotCreatePOBeforeAgreementEffective", locale));
		}
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		List<GenericValue> listAgreementAndOrder = FastList.newInstance();
		try {
			listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementAndOrder: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listAgreementAndOrder.isEmpty()){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIEPOHasBeenCreatedForAgreement", locale));
		}
		
		String currencyUomId = null;
		conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		conds.add(EntityCondition.makeCondition("agreementItemTypeId", "AGREEMENT_PRICING_PR"));
		
		List<GenericValue> listAgreementItem = FastList.newInstance();
		try {
			listAgreementItem = delegator.findList("AgreementItem", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listAgreementItem.isEmpty()) currencyUomId = listAgreementItem.get(0).getString("currencyUomId");
		
		objAgreement.getString("currencyUomId");
		String partyIdTo = objAgreement.getString("partyIdTo");
		String partyIdFrom = objAgreement.getString("partyIdFrom");
		String contactMechId = null;
		
		conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		conds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
		conds.add(EntityCondition.makeCondition("partyId", partyIdTo));
//		conds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listAgreementPartyCTMPurpose = FastList.newInstance();
		try {
			listAgreementPartyCTMPurpose = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementPartyCTMPurpose: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listAgreementPartyCTMPurpose.isEmpty()){
			contactMechId = listAgreementPartyCTMPurpose.get(0).getString("contactMechId");
		}
		String productPlanId = objAgreement.getString("productPlanId");
		
		conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		List<GenericValue> listAgreementProductAppl = FastList.newInstance();
		try {
			listAgreementProductAppl = delegator.findList("AgreementProductAppl", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementProductAppl: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		List<Map<String, Object>> listItems = FastList.newInstance();
		String listOrderItems = null;
		if (!listAgreementProductAppl.isEmpty()){
			for (GenericValue product : listAgreementProductAppl) {
				Map<String, Object> map = FastMap.newInstance();
				map.put("productId", product.getString("productId"));
				map.put("quantity", product.getBigDecimal("quantity"));
				map.put("lastPrice", product.getBigDecimal("price"));
				listItems.add(map);
			}
		}
		listOrderItems = JsonUtil.convertListMapToJSON(listItems);
		Map<String, Object> map = FastMap.newInstance();
		map.put("orderItems", listOrderItems);
		map.put("userLogin", userLogin);
		map.put("currencyUomId", currencyUomId);
		map.put("partyIdFrom", partyIdTo);
		map.put("contactMechId", contactMechId);
		Long fromDate = objAgreement.getTimestamp("fromDate").getTime();
		Long thruDate = objAgreement.getTimestamp("thruDate").getTime();
		map.put("shipAfterDate", fromDate.toString());
		map.put("shipBeforeDate", thruDate.toString());
		map.put("productPlanId", productPlanId);
		if (UtilValidate.isNotEmpty(productPlanId)) {
			GenericValue objProductPlanHeader = null;
			try {
				objProductPlanHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne ProductPlanHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			map.put("customTimePeriodId", objProductPlanHeader.get("customTimePeriodId"));
		}
		
		String originFacilityId = null;
		conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("ownerPartyId", partyIdFrom));
		conds.add(EntityCondition.makeCondition("primaryFacilityGroupId", "FACILITY_INTERNAL"));
		conds.add(EntityCondition.makeCondition("facilityTypeId", "WAREHOUSE"));
		
		if (UtilValidate.isEmpty(objAgreement.get("destFacilityId"))) {
			List<EntityCondition> cond2s = FastList.newInstance();
			cond2s.add(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null));
			cond2s.add(EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
			
			conds.add(EntityCondition.makeCondition(cond2s, EntityOperator.OR));
			List<GenericValue> listFacility = FastList.newInstance();
			try {
				listFacility = delegator.findList("Facility", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listFacility.isEmpty()) originFacilityId = listFacility.get(0).getString("facilityId");
		} else {
			originFacilityId = objAgreement.getString("destFacilityId");
		}
		map.put("originFacilityId", originFacilityId);
		String orderId = null;
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			map.put("locale", "en");
			Map<String, Object> order = dispatcher.runSync("createNewPurchaseOrder", map);
			if (ServiceUtil.isError(order)){
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(order));
			}
			orderId = (String) order.get("orderId");
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		if (UtilValidate.isNotEmpty(orderId)) {
			objAgreement.put("hasOrdered", "Y");
			try {
				delegator.store(objAgreement);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when store Agreement: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			
			GenericValue objOrderHeader = null;
			try {
				objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne OrderHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			objOrderHeader.set("shipBeforeDate", objAgreement.getTimestamp("fromDate"));
			objOrderHeader.set("shipAfterDate", objAgreement.getTimestamp("thruDate"));
			objOrderHeader.put("primaryAgreementId", agreementId);
			try {
				delegator.store(objOrderHeader);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
			GenericValue agreementAndOrderNew = delegator.makeValue("AgreementAndOrder");
			agreementAndOrderNew.put("orderId", orderId);
			agreementAndOrderNew.put("agreementId", agreementId);
			agreementAndOrderNew.put("fromDate", UtilDateTime.nowTimestamp());
			try {
				delegator.createOrStore(agreementAndOrderNew);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	
	public static Map<String, Object> getPackingListByContainer(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		String containerId = (String) context.get("containerId");
		Set<String> fld = FastSet.newInstance();
		Map<String, Object> mapResult = FastMap.newInstance();
		List<GenericValue> listMapAgreement = FastList.newInstance();
		fld.add("packingListId");
		fld.add("packingListNumber");
		fld.add("purchaseOrderId");
		List<GenericValue> listPackingList = delegator.findList("PackingListHeader",
				EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId)), fld, null, null, false);
		if (!UtilValidate.isEmpty(listPackingList)) {
			for (GenericValue packingList : listPackingList) {
				String orderId = (String) packingList.get("purchaseOrderId");
				Map<String, Object> contextTmp = new HashMap<String, Object>();
				contextTmp.put("orderId", orderId);
				contextTmp.put("userLogin", userLogin);
				try {
					mapResult = dispatcher.runSync("getAgreementByOrder", contextTmp);
					if (mapResult.containsKey("listAgreement")) {
						GenericValue agreementMap = (GenericValue) mapResult.get("listAgreement");
						listMapAgreement.add(agreementMap);
					}
				} catch (GenericServiceException e) {
				}
			}
		}
		result.put("listAgreement", listMapAgreement);
		result.put("listPackingList", listPackingList);
		return result;
	}

	public static Map<String, Object> doSomethingWhenSelectPLNumber(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> resultReturn = FastMap.newInstance();
		String packingListId = (String) context.get("packingListId");
		GenericValue packingListHeader = delegator.findOne("PackingListHeader",
				UtilMisc.toMap("packingListId", packingListId), false);
		result.put("packingListDate", ((Date) packingListHeader.get("packingListDate")).toString());
		result.put("packingListId", (String) packingListHeader.get("packingListId"));
		result.put("externalOrderNumber", (String) packingListHeader.get("externalOrderNumber"));
		result.put("externalOrderTypeId", (String) packingListHeader.get("externalOrderTypeId"));
		result.put("externalInvoiceNumber", (String) packingListHeader.get("externalInvoiceNumber"));
		result.put("externalInvoiceDate", ((Date) packingListHeader.get("externalInvoiceDate")).toString());
		result.put("netWeightTotal", (BigDecimal) packingListHeader.get("netWeightTotal"));
		result.put("grossWeightTotal", (BigDecimal) packingListHeader.get("grossWeightTotal"));
		GenericValue packingListDetailSum = delegator.findOne("PackingListDetailSum",
				UtilMisc.toMap("packingListId", packingListId), false);
		Long packingUnit = new Long(0);
		if (packingListDetailSum != null) {
			packingUnit = (Long) packingListDetailSum.get("packingUnit");
		}
		result.put("packingUnit", packingUnit);
		resultReturn.put("resultPackingListHeader", result);
		return resultReturn;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetPackingListDetail(DispatchContext dpx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String[]> param = (Map<String, String[]>) context.get("parameters");
		String packingListId = (String) param.get("packingListId")[0];
		List<GenericValue> listPackingListDetail = delegator.findList("PackingListDetail",
				EntityCondition.makeCondition(UtilMisc.toMap("packingListId", packingListId)), null, null, null, false);
		result.put("listIterator", listPackingListDetail);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetListOrderItemsAjax(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String agreementId = (String) parameters.get("agreementId")[0];
		List<GenericValue> listAgreeAndOrder = delegator.findList("AgreementAndOrder",
				EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
		String orderId = "";
		if (!UtilValidate.isEmpty(listAgreeAndOrder)) {
			GenericValue agreementAndOrder = EntityUtil.getFirst(listAgreeAndOrder);
			orderId = (String) agreementAndOrder.get("orderId");
		}
		List<String> sortBy = FastList.newInstance();
		sortBy.add("orderItemSeqId");
		List<EntityExpr> exprs = UtilMisc.toList(
				EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
				EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
		EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(exprs, EntityOperator.AND);
		listAllConditions.add(ecl);
		List<GenericValue> listOrderItem = delegator.findList("OrderItemAndProduct",
				EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSortFields, null,
				false);
		List<Map<String, Object>> listIterator = FastList.newInstance();
		for (GenericValue orderItem : listOrderItem) {
			Map<String, Object> map = FastMap.newInstance();
			map.putAll(orderItem);
			map.put("originOrderUnit", (BigDecimal) orderItem.getBigDecimal("quantity"));
			map.put("packingUnit", 0);
			map.put("orderUnit", (BigDecimal) orderItem.getBigDecimal("quantity"));
			listIterator.add(map);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> createContainerAndPackingList(DispatchContext ctx, Map<String, Object> context)
			throws GenericServiceException {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = FastMap.newInstance();
		String packingList = (String) context.get("packingList");
		Map<String, Object> re = FastMap.newInstance();
		re = getJsonAndUpdateContainerAndPL(packingList, ctx, userLogin);
		String packingListId = (String) re.get("packingListId");
		String purchaseOrderId = (String) re.get("purchaseOrderId");
		String packingListDetail = (String) context.get("packingListDetail");
		result.put("containerId", (String) re.get("containerId"));
		getJsonAndUpdatePackingListDetail(packingListDetail, ctx, userLogin, packingListId, purchaseOrderId);
		Map<String, Object> contextTmp = new HashMap<String, Object>();
		contextTmp.put("containerId", (String) re.get("containerId"));
		contextTmp.put("userLogin", userLogin);
		Map<String, Object> mapResult = FastMap.newInstance();
		try {
			mapResult = dispatcher.runSync("getDetailContainer", contextTmp);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		result.put("resultContainer", mapResult);
		return result;
	}

	public static Map<String, Object> getJsonAndUpdateContainerAndPL(String packingList, DispatchContext ctx,
			GenericValue userLogin) throws GenericServiceException {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		JSONObject packingListJson = JSONObject.fromObject(packingList);
		String billId = (String) packingListJson.get("billId");
		String packingListNumber = (String) packingListJson.get("packingListNumber");
		Long packingListDateLong = (Long) packingListJson.get("packingListDate");
		java.sql.Date packingListDate = new java.sql.Date(packingListDateLong);
		String externalInvoiceNumber = (String) packingListJson.get("invoiceNumber");
		Long externalInvoiceDateLong = (Long) packingListJson.get("invoiceDate");
		java.sql.Date externalInvoiceDate = new java.sql.Date(externalInvoiceDateLong);
		String externalOrderNumber = (String) packingListJson.get("orderNumberSupp");
		String externalOrderTypeId = (String) packingListJson.get("orderTypeSuppId");
		String containerId = (String) packingListJson.get("containerId");
		String purchaseAgreementId = (String) packingListJson.get("agreementId");
		
		BigDecimal netWeightTotal = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(packingListJson.get("totalNetWeight"))) {
			String netWeightTotalStr = (String) packingListJson.get("totalNetWeight");
			netWeightTotal = new BigDecimal(netWeightTotalStr);
		}
		
		BigDecimal grossWeightTotal = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(packingListJson.get("totalGrossWeight"))) {
			String grossWeightTotalStr = (String) packingListJson.get("totalGrossWeight");
			grossWeightTotal = new BigDecimal(grossWeightTotalStr);
		}
		
		String packingListId = (String) packingListJson.get("packingListId");
		String destFacilityId = (String) packingListJson.get("destFacilityId");
		String sealNumber = (String) packingListJson.get("sealNumber");
		String containerNumber = (String) packingListJson.get("containerNumber");
		String purchaseOrderId = null;
		// update status agreement
		Map<String, Object> contextTmpSttAgreement = new HashMap<String, Object>();
		contextTmpSttAgreement.put("agreementId", purchaseAgreementId);
		contextTmpSttAgreement.put("statusId", "AGREEMENT_PROCESSING");
		contextTmpSttAgreement.put("userLogin", userLogin);
		dispatcher.runSync("updateStatusAgreement", contextTmpSttAgreement);
		// get order from AgreementAndOrder
		Map<String, Object> contextTmpAgreement = new HashMap<String, Object>();
		contextTmpAgreement.put("agreementId", purchaseAgreementId);
		contextTmpAgreement.put("userLogin", userLogin);
		Map<String, Object> order = dispatcher.runSync("getPurchaseOrderByAgreementId", contextTmpAgreement);
		purchaseOrderId = (String) order.get("orderId");
		// update Container
		Map<String, Object> contextTmpContainer = new HashMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		GenericValue objContainer = null;
		try {
			objContainer = delegator.findOne("Container", false, UtilMisc.toMap("containerId", containerId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Container: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		if (UtilValidate.isEmpty(objContainer)) {
			contextTmpContainer.put("containerId", containerId);
			contextTmpContainer.put("containerNumber", containerNumber);
			contextTmpContainer.put("containerTypeId", "STANDARD_CONTAINER");
			contextTmpContainer.put("sealNumber", sealNumber);
			contextTmpContainer.put("billId", billId);
			contextTmpContainer.put("userLogin", userLogin);
			Map<String, Object> mapCont = dispatcher.runSync("updateContainer", contextTmpContainer);
			containerId = (String) mapCont.get("containerId");
		}
		
		// update OrderAndContainer
		Map<String, Object> contextTmpOrderAndContainer = new HashMap<String, Object>();
		contextTmpOrderAndContainer.put("containerId", containerId);
		contextTmpOrderAndContainer.put("orderId", purchaseOrderId);
		contextTmpOrderAndContainer.put("billId", billId);
		contextTmpOrderAndContainer.put("userLogin", userLogin);
		dispatcher.runSync("updateOrderAndContainer", contextTmpOrderAndContainer);

		Map<String, Object> contextTmpPL = new HashMap<String, Object>();
		contextTmpPL.put("packingListId", packingListId);
		contextTmpPL.put("agreementId", purchaseAgreementId);
		contextTmpPL.put("destFacilityId", destFacilityId);
		contextTmpPL.put("packingListNumber", packingListNumber);
		contextTmpPL.put("packingListDate", packingListDate);
		contextTmpPL.put("externalInvoiceNumber", externalInvoiceNumber);
		contextTmpPL.put("externalInvoiceDate", externalInvoiceDate);
		contextTmpPL.put("externalOrderNumber", externalOrderNumber);
		contextTmpPL.put("externalOrderTypeId", externalOrderTypeId);
		contextTmpPL.put("containerId", containerId);
		contextTmpPL.put("purchaseOrderId", purchaseOrderId);
		contextTmpPL.put("netWeightTotal", netWeightTotal);
		contextTmpPL.put("grossWeightTotal", grossWeightTotal);
		contextTmpPL.put("userLogin", userLogin);
		Map<String, Object> result = dispatcher.runSync("updatePackingListHeader", contextTmpPL);
		return result;
	}

	public static void getJsonAndUpdatePackingListDetail(String listPackingListDetail, DispatchContext ctx,
			GenericValue userLogin, String packingListId, String purchaseOrderId) throws GenericServiceException {

		JSONArray jsonArrPLDetail = JSONArray.fromObject(listPackingListDetail);
		if (jsonArrPLDetail.size() > 0) {
			for (int i = 0; i < jsonArrPLDetail.size(); i++) {
				JSONObject jsonObjPLDetail = jsonArrPLDetail.getJSONObject(i);
				// String packingListId = null;
				// if(jsonObjPLDetail.containsKey("packingListId")){
				// packingListId = (String)jsonObjPLDetail.get("packingListId");
				// }
				String packingListSeqId = null;
				if (jsonObjPLDetail.containsKey("packingListSeqId")) {
					packingListSeqId = (String) jsonObjPLDetail.get("packingListSeqId");
				}
				String orderId = null;
				if (jsonObjPLDetail.containsKey("orderId")) {
					orderId =  jsonObjPLDetail.getString("orderId");
				}
				String orderItemSeqId = null;
				if (jsonObjPLDetail.containsKey("orderItemSeqId")) {
					orderItemSeqId =  jsonObjPLDetail.getString("orderItemSeqId");
				}
				String productId = null;
				if (jsonObjPLDetail.containsKey("productId")) {
					productId =  jsonObjPLDetail.getString("productId");
				}
				String batchNumber = null;
				if (jsonObjPLDetail.containsKey("batchNumber")
						&& !jsonObjPLDetail.getString("batchNumber").equals("")
						&& jsonObjPLDetail.getString("batchNumber").toString() != null
						&& jsonObjPLDetail.getString("batchNumber").toString() != "null") {
					batchNumber = jsonObjPLDetail.getString("batchNumber");
				}
				String gtin = null;
				if (jsonObjPLDetail.containsKey("globalTradeItemNumber")
						&& !jsonObjPLDetail.getString("globalTradeItemNumber").equals("")
						&& jsonObjPLDetail.getString("globalTradeItemNumber").toString() != null
						&& jsonObjPLDetail.getString("globalTradeItemNumber").toString() != "null") {
					gtin = jsonObjPLDetail.getString("globalTradeItemNumber");
				}
				Long orderUnit = null;
				if (jsonObjPLDetail.containsKey("orderUnit")) {
					Integer orderUnitInt = jsonObjPLDetail.getInt("orderUnit");
					orderUnit = orderUnitInt.longValue();
				}
				Long packingUnit = null;
				if (jsonObjPLDetail.containsKey("packingUnit")) {
					Integer packingUnitInt = jsonObjPLDetail.getInt("packingUnit");
					packingUnit = packingUnitInt.longValue();
				}
				Timestamp datetimeManufactured = null;
				if (jsonObjPLDetail.containsKey("datetimeManufactured")) {
					Long datetimeManufacturedLong = jsonObjPLDetail.getLong("datetimeManufactured");
					datetimeManufactured = new Timestamp(datetimeManufacturedLong);

				}
				Timestamp expireDate = null;
				if (jsonObjPLDetail.containsKey("expireDate")) {
					Long expireDateLong = jsonObjPLDetail.getLong("expireDate");
					expireDate = new Timestamp(expireDateLong);
					
				}
				BigDecimal originOrderUnit = new BigDecimal(0);
				if (jsonObjPLDetail.containsKey("originOrderUnit")
						&& !jsonObjPLDetail.getString("originOrderUnit").equals("")
						&& jsonObjPLDetail.getString("originOrderUnit") != ""
						&& jsonObjPLDetail.getString("originOrderUnit") != null
						&& jsonObjPLDetail.getString("originOrderUnit") != "null") {
					Integer originOrderUnitInt = jsonObjPLDetail.getInt("originOrderUnit");
					originOrderUnit = new BigDecimal(originOrderUnitInt);
				}

				LocalDispatcher dispatcher = ctx.getDispatcher();
				// update orderItem
//				Map<String, Object> contextTmpOrderItem = new HashMap<String, Object>();
//				contextTmpOrderItem.put("orderId", purchaseOrderId);
//				contextTmpOrderItem.put("orderItemSeqId", orderItemSeqId);
//				contextTmpOrderItem.put("quantity", new BigDecimal(orderUnit));
//				Timestamp datetimeManufacturedTimestamp = new Timestamp(datetimeManufactured.getTime());
//				Timestamp expireDateTimestamp = new Timestamp(expireDate.getTime());
//				contextTmpOrderItem.put("datetimeManufactured", datetimeManufacturedTimestamp);
//				contextTmpOrderItem.put("expireDate", expireDateTimestamp);
//				contextTmpOrderItem.put("productId", productId);
//				contextTmpOrderItem.put("userLogin", userLogin);
//				Map<String, Object> orderItemMap = dispatcher.runSync("updateOrderItemFromPackingList",
//						contextTmpOrderItem);
//				orderItemSeqId = (String) orderItemMap.get("orderItemSeqId");
				// update packing list detail
				Map<String, Object> contextTmpPL = new HashMap<String, Object>();
				contextTmpPL.put("packingListId", packingListId);
				contextTmpPL.put("packingListSeqId", packingListSeqId);
				contextTmpPL.put("orderId", orderId);
				contextTmpPL.put("orderItemSeqId", orderItemSeqId);
				contextTmpPL.put("productId", productId);
				contextTmpPL.put("batchNumber", batchNumber);
				contextTmpPL.put("globalTradeItemNumber", gtin);
				contextTmpPL.put("packingUnit", packingUnit);
				contextTmpPL.put("orderUnit", orderUnit);
				contextTmpPL.put("originOrderUnit", originOrderUnit);
				contextTmpPL.put("datetimeManufactured", datetimeManufactured);
				contextTmpPL.put("expireDate", expireDate);
				contextTmpPL.put("userLogin", userLogin);
				dispatcher.runSync("updatePackingListDetail", contextTmpPL);
			}
		}
	}

	public static Map<String, Object> getDetailContainer(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		String containerId = (String) context.get("containerId");
		String externalOrderNumber = "";
		BigDecimal netWeightTotal = new BigDecimal(0);
		BigDecimal grossWeightTotal = new BigDecimal(0);
		Long packingUnitTotal = new Long(0);
		List<GenericValue> listPackingListHeader = FastList.newInstance();
		try {
			listPackingListHeader = delegator.findList("PackingListHeader",
					EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId)), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> listPackingList = FastList.newInstance();
		if (!UtilValidate.isEmpty(listPackingListHeader)) {
			int checkSize = listPackingListHeader.size();
			for (GenericValue packingList : listPackingListHeader) {
				Map<String, Object> mapPL = FastMap.newInstance();
				mapPL.put("packingListId", (String) packingList.get("packingListId"));
				mapPL.put("packingListNumber", (String) packingList.get("packingListNumber"));
				listPackingList.add(mapPL);
				// get order of supplier
				checkSize--;
				externalOrderNumber += "SAP-" + (String) packingList.get("externalOrderNumber");
				if (checkSize != 0) {
					externalOrderNumber += " & ";
				}
				// Sum Karton
				GenericValue packingListDetailSum = null;
				try {
					packingListDetailSum = delegator.findOne("PackingListDetailSum",
							UtilMisc.toMap("packingListId", (String) packingList.get("packingListId")), false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				if (packingListDetailSum != null && packingListDetailSum.get("packingUnit") != null) {
					packingUnitTotal += (Long) packingListDetailSum.getLong("packingUnit");
				}
				// Sum netWeight
				netWeightTotal = netWeightTotal.add((BigDecimal) packingList.get("netWeightTotal"));
				// Sum gross Weight
				grossWeightTotal = grossWeightTotal.add((BigDecimal) packingList.get("grossWeightTotal"));

			}
		}
		Map<String, Object> rowDetail = FastMap.newInstance();
		rowDetail.put("externalOrderNumber", externalOrderNumber);
		rowDetail.put("netWeightTotal", netWeightTotal.toString());
		rowDetail.put("grossWeightTotal", grossWeightTotal.toString());
		rowDetail.put("packingUnitTotal", packingUnitTotal.toString());
		rowDetail.put("listPackingListHeader", listPackingList);
		return rowDetail;
	}

	public static Map<String, Object> getPurchaseOrderByAgreementId(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String agreementId = (String) context.get("agreementId");
		List<GenericValue> listAgreementAndOrder = FastList.newInstance();
		try {
			listAgreementAndOrder = delegator.findList("AgreementAndOrder",
					EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String orderId = null;
		if (!UtilValidate.isEmpty(listAgreementAndOrder)) {
			GenericValue order = EntityUtil.getFirst(listAgreementAndOrder);
			orderId = (String) order.get("orderId");
			result.put("orderId", orderId);
		}
		return result;
	}

	public static Map<String, Object> updateOrderItemFromPackingList(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String orderId = (String) context.get("orderId");
		String orderItemSeqId = (String) context.get("orderItemSeqId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		BigDecimal documentQuantity = (BigDecimal) context.get("quantity");
		String quantityStr = documentQuantity.toString();
		String productId = (String) context.get("productId");
		Timestamp datetimeManufactured = null;
		if (context.get("datetimeManufactured") != null) {
			datetimeManufactured = (Timestamp) context.get("datetimeManufactured");
		}
		Timestamp expireDate = null;
		if (context.get("expireDate") != null) {
			expireDate = (Timestamp) context.get("expireDate");
		}
		String overridePriceMapCtx = null;
		if (context.get("overidePriceMap") != null) {
			overridePriceMapCtx = (String) context.get("overidePriceMap");
		}
		String itemPriceMapCtx = null;
		if (context.get("itemPriceMap") != null) {
			itemPriceMapCtx = (String) context.get("itemPriceMap");
		}
		List<String> listOrderItemSeq = new ArrayList<String>();
		try {
			List<GenericValue> listOrderItems = delegator.findList("OrderItem",
					EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
			if (!UtilValidate.isEmpty(listOrderItems)) {
				for (GenericValue orItem : listOrderItems) {
					String orderItemSeqIdCheck = (String) orItem.get("orderItemSeqId");
					listOrderItemSeq.add(orderItemSeqIdCheck);
				}
			}
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		try {
			List<GenericValue> listShipGroupSeqId;
			GenericValue shipGroupId = null;
			listShipGroupSeqId = delegator.findList("OrderItemShipGroup",
					EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			if (!UtilValidate.isEmpty(listShipGroupSeqId)) {
				shipGroupId = EntityUtil.getFirst(listShipGroupSeqId);
			}
			GenericValue orderItem = delegator.findOne("OrderItem", false,
					UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
			// update order item if exi
			if (orderItem != null) {
				BigDecimal quantity = orderItem.getBigDecimal("alternativeQuantity");
				if ((quantity == null) || (quantity.compareTo(BigDecimal.ZERO) == 0)) {
					if (shipGroupId != null) {
						Map<String, String> itemDescriptionMap = FastMap.newInstance();
						Map<String, String> itemReasonMap = FastMap.newInstance();
						Map<String, String> itemCommentMap = FastMap.newInstance();
						Map<String, String> itemAttributesMap = FastMap.newInstance();
						Map<String, String> itemEstimatedShipDateMap = FastMap.newInstance();
						Map<String, String> itemEstimatedDeliveryDateMap = FastMap.newInstance();
						String shipGroupSeqId = (String) shipGroupId.get("shipGroupSeqId");
						Map<String, String> itemQtyMap = FastMap.newInstance();
						Map<String, String> itemPriceMap = FastMap.newInstance();
						Map<String, String> overridePriceMap = FastMap.newInstance();
						if (overridePriceMapCtx != null) {
							overridePriceMap.put(orderItemSeqId, overridePriceMapCtx);
						}
						if (itemPriceMapCtx != null) {
							itemPriceMap.put(orderItemSeqId, itemPriceMapCtx);
						} else {
							itemPriceMap.put(orderItemSeqId, "0");
						}
						itemQtyMap.put(orderItemSeqId + ":" + shipGroupSeqId, quantityStr);
						itemReasonMap.put(orderItemSeqId, "");
						itemCommentMap.put(orderItemSeqId, "");
						// itemEstimatedShipDateMap.put("isdm_" +
						// orderItemSeqId, "");
						// itemEstimatedDeliveryDateMap.put("iddm_" +
						// orderItemSeqId, "");
						// overridePriceMap.put(orderItemSeqId, "N");
						Map<String, Object> contextTmp = new HashMap<String, Object>();
						contextTmp.put("orderId", orderId);
						contextTmp.put("orderTypeId", "PURCHASE_ORDER");
						contextTmp.put("itemQtyMap", itemQtyMap);
						contextTmp.put("itemPriceMap", itemPriceMap);
						contextTmp.put("itemDescriptionMap", itemDescriptionMap);
						contextTmp.put("itemReasonMap", itemReasonMap);
						contextTmp.put("itemCommentMap", itemCommentMap);
						contextTmp.put("itemAttributesMap", itemAttributesMap);
						contextTmp.put("itemShipDateMap", itemEstimatedShipDateMap);
						contextTmp.put("itemDeliveryDateMap", itemEstimatedDeliveryDateMap);
						contextTmp.put("overridePriceMap", overridePriceMap);
						GenericValue admin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "admin"),
								false);
						contextTmp.put("userLogin", admin);
						// contextTmp.put("userLogin", userLogin);
						try {
							// dispatcher.runSync("updateOrderItemsPOImport",
							// contextTmp);
							dispatcher.runSync("updateOrderItems", contextTmp);
						} catch (GenericServiceException e) {
							e.printStackTrace();
						}
					}
					if (expireDate != null && datetimeManufactured != null) {
						GenericValue orderItemUpdateDate = delegator.findOne("OrderItem", false,
								UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
						orderItemUpdateDate.put("productId", productId);
						orderItemUpdateDate.put("expireDate", expireDate);
						orderItemUpdateDate.put("datetimeManufactured", datetimeManufactured);
						delegator.store(orderItemUpdateDate);
					}
					// orderItem.put("quantity", documentQuantity);
				} else {
					orderItem.put("alternativeQuantity", documentQuantity);
				}
			}

			else {// append item order if seqItem not exist
				if (shipGroupId != null) {
					String shipGroupSeqId = (String) shipGroupId.get("shipGroupSeqId");
					Map<String, Object> contextTmp = new HashMap<String, Object>();
					contextTmp.put("orderId", orderId);
					contextTmp.put("shipGroupSeqId", shipGroupSeqId);
					contextTmp.put("productId", productId);
					contextTmp.put("quantity", documentQuantity);
					contextTmp.put("expireDate", expireDate);
					contextTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("appendOrderItem", contextTmp);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
				}
				List<GenericValue> listOrderItems2 = delegator.findList("OrderItem",
						EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
				if (!UtilValidate.isEmpty(listOrderItems2)) {
					for (GenericValue orItem : listOrderItems2) {
						String orderItemSeqIdCheck = (String) orItem.get("orderItemSeqId");
						if (!listOrderItemSeq.contains(orderItemSeqIdCheck)) {
							orderItemSeqId = orderItemSeqIdCheck;
							orItem.put("datetimeManufactured", datetimeManufactured);
							orItem.put("expireDate", expireDate);
							delegator.store(orItem);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("orderId", orderId);
		result.put("orderItemSeqId", orderItemSeqId);
		return result;
	}

	public static Map<String, Object> updateDocumentCustomsAjax(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		String containerId = (String) context.get("containerId");
		String documentCustomsId = (String) context.get("documentCustomsId");
		String documentCustomsTypeId = (String) context.get("documentCustomsTypeId");
		String registerNumber = (String) context.get("registerNumber");
		String registerDate = (String) context.get("registerDate");
		String sampleSendDate = (String) context.get("sampleSendDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> contextTmp = new HashMap<String, Object>();
		contextTmp.put("containerId", containerId);
		contextTmp.put("documentCustomsId", documentCustomsId);
		contextTmp.put("documentCustomsTypeId", documentCustomsTypeId);
		contextTmp.put("registerNumber", registerNumber);
		contextTmp.put("registerDate", new java.sql.Date(Long.parseLong(registerDate)));
		contextTmp.put("sampleSendDate", new java.sql.Date(Long.parseLong(sampleSendDate)));
		contextTmp.put("userLogin", userLogin);
		try {
			dispatcher.runSync("updateDocumentCustoms", contextTmp);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static List<String> getListUomToConvert(Delegator delegator, String productId, String uomFromId,
			String uomToId) {
		Queue<String> listConfigs = new LinkedList<String>();
		List<String> listUomToConvert = new ArrayList<String>();
		listUomToConvert.add(uomToId);
		listConfigs.clear();
		listConfigs.add(uomToId);
		while (!listConfigs.isEmpty()) {
			String uomCur = listConfigs.remove();
			try {
				if (UtilValidate.isNotEmpty(uomFromId) && !uomFromId.equals(uomCur)) {
					List<GenericValue> listConfigParents = delegator.findList("ConfigPacking",
							EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", uomCur)),
							null, null, null, false);
					if (!listConfigParents.isEmpty()) {
						for (GenericValue cf : listConfigParents) {
							String uomParentId = (String) cf.get("uomFromId");
							if (!listUomToConvert.contains(uomParentId)) {
								listConfigs.add(uomParentId);
								listUomToConvert.add(uomParentId);
							}
						}
					} else {
						listUomToConvert.remove(uomCur);
					}
				} else {
					break;
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return listUomToConvert;
	}

	public static BigDecimal getProductConvertNumber(Delegator delegator, BigDecimal convert, String productId,
			String uomFromId, String uomToId, List<String> listUomToConvert) {
		try {
			List<GenericValue> listConfigs = delegator.findList("ConfigPacking",
					EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", uomToId)), null,
					null, null, false);
			listConfigs = EntityUtil.filterByDate(listConfigs, UtilDateTime.nowTimestamp(), "fromDate", "thruDate",
					true);
			if (!listConfigs.isEmpty()) {
				boolean check = false;
				String uomParentCur = null;
				for (GenericValue cf : listConfigs) {
					if (listUomToConvert.contains((String) cf.get("uomFromId"))) {
						convert = convert.multiply(cf.getBigDecimal("quantityConvert"));
						uomParentCur = (String) cf.get("uomFromId");
						check = true;
						break;
					}
				}
				if (check) {
					if (!uomFromId.equals(uomParentCur)) {
						convert = getProductConvertNumber(delegator, convert, productId, uomFromId, uomParentCur,
								listUomToConvert);
					} else {
						return convert;
					}
				} else {
					return BigDecimal.ONE;
				}
			} else {
				return BigDecimal.ONE;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return convert;
	}

	public static Map<String, Object> allocationCostFromBLToOrder(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String billId = (String) context.get("billId");
		String costAccBaseId = (String) context.get("costAccBaseId");
        Locale locale = (Locale) context.get("locale");
		String applicationBaseId = (String) context.get("applicationBaseId");
		BigDecimal costPriceTemporary = (BigDecimal) context.get("costPriceTemporary");
		BigDecimal exchangedRate = (BigDecimal) context.get("exchangedRate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			List<GenericValue> listOrderByBL = delegator.findList("OrderAndContainer",
					EntityCondition.makeCondition(UtilMisc.toMap("billId", billId)), null, null, null, false);
			if (!UtilValidate.isEmpty(listOrderByBL)) {
				// tinh theo tieu thuc phan bo trung binh
				if (applicationBaseId.equals("COST_AVG")) {
					int sizeListOrder = listOrderByBL.size();
					for (GenericValue order : listOrderByBL) {
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("oneOrder", new BigDecimal(1));
						mapContext.put("perOrder", new BigDecimal(sizeListOrder));
						mapContext.put("totalCost", costPriceTemporary);
						mapContext.put("orderId", order.get("orderId"));
                        mapContext.put("costAccBaseId", costAccBaseId);
                        mapContext.put("exchangedRate", exchangedRate);
						mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateCostOrder", mapContext);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError(e.getMessage());
						}
					}
				}
				// tinh theo tieu thuc phan bo so luong san pham trong order
				if (applicationBaseId.equals("COST_QUANTITY")) {
					BigDecimal totalQuantity = new BigDecimal(0);
					List<Map<String, Object>> listOrder = new ArrayList<Map<String, Object>>();
					for (GenericValue order1 : listOrderByBL) {
						Map<String, Object> mapOrder = new FastMap<String, Object>();
						List<GenericValue> listOrderItem = delegator.findList("OrderItem",
								EntityCondition
										.makeCondition(UtilMisc.toMap("orderId", (String) order1.get("orderId"))),
								null, null, null, false);
						BigDecimal totalOneOrderBig = new BigDecimal(0);
						for (GenericValue orderItem : listOrderItem) {
							BigDecimal quantityItem = (BigDecimal) orderItem.get("quantity");
							totalOneOrderBig = quantityItem.add(totalOneOrderBig);
						}
						mapOrder.put("orderId", (String) order1.get("orderId"));
						mapOrder.put("quantity", totalOneOrderBig);
						listOrder.add(mapOrder);
						totalQuantity = totalQuantity.add(totalOneOrderBig);

					}
					for (Map<String, Object> map : listOrder) {
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("oneOrder", (BigDecimal) map.get("quantity"));
						mapContext.put("perOrder", totalQuantity);
						mapContext.put("totalCost", costPriceTemporary);
						mapContext.put("orderId", (String) map.get("orderId"));
						mapContext.put("costAccBaseId", costAccBaseId);
                        mapContext.put("exchangedRate", exchangedRate);
                        mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateCostOrder", mapContext);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError(e.getMessage());
						}
					}

				}
				// tinh theo tieu thuc phan bo khoi luong 1 order
				if (applicationBaseId.equals("COST_WEIGHT")) {
					BigDecimal totalWeight = new BigDecimal(0);
					List<Map<String, Object>> listOrder = new ArrayList<Map<String, Object>>();
					for (GenericValue order1 : listOrderByBL) {
						Map<String, Object> mapOrder = new FastMap<String, Object>();
						List<GenericValue> listOrderItem = delegator.findList("OrderItem",
								EntityCondition
										.makeCondition(UtilMisc.toMap("orderId", (String) order1.get("orderId"))),
								null, null, null, false);
						BigDecimal totalOneOrderBig = new BigDecimal(0);
						for (GenericValue orderItem : listOrderItem) {
							BigDecimal quantityItem = (BigDecimal) orderItem.get("quantity");
							GenericValue product = delegator.findOne("Product",
									UtilMisc.toMap("productId", (String) orderItem.get("productId")), false);
							BigDecimal weight = new BigDecimal(0);
							if (UtilValidate.isNotEmpty(product.get("weight"))) {
								weight = (BigDecimal) product.get("weight");
							} else if (UtilValidate.isNotEmpty(product.get("productWeight"))){
								weight = (BigDecimal) product.get("productWeight");
							}
							if (weight.compareTo(BigDecimal.ZERO) <= 0){
								return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIEProductDoNotConfigWeightYet", locale) + ": " + orderItem.get("productId"));
							}
							BigDecimal weightOrderItem = weight.multiply(quantityItem);
							totalOneOrderBig = weightOrderItem.add(totalOneOrderBig);
						}
						mapOrder.put("orderId", (String) order1.get("orderId"));
						mapOrder.put("quantity", totalOneOrderBig);
						listOrder.add(mapOrder);
						totalWeight = totalWeight.add(totalOneOrderBig);

					}
					for (Map<String, Object> map : listOrder) {
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("oneOrder", (BigDecimal) map.get("quantity"));
						mapContext.put("perOrder", totalWeight);
						mapContext.put("totalCost", costPriceTemporary);
						mapContext.put("orderId", (String) map.get("orderId"));
						mapContext.put("costAccBaseId", costAccBaseId);
                        mapContext.put("exchangedRate", exchangedRate);
                        mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateCostOrder", mapContext);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError(e.getMessage());
						}
					}

				}
				// tinh theo tieu thuc phan bo gia tri order
				if (applicationBaseId.equals("COST_VALUE")) {
					BigDecimal totalValue = new BigDecimal(0);
					GenericValue billOfLading = delegator.findOne("BillOfLading", UtilMisc.toMap("billId", billId),
							false);
					Timestamp arrivalDate = new Timestamp(System.currentTimeMillis());
					if ((Timestamp) billOfLading.get("arrivalDate") != null) {
						arrivalDate = (Timestamp) billOfLading.get("arrivalDate");
					}
					List<Map<String, Object>> listOrder = new ArrayList<Map<String, Object>>();
					for (GenericValue order1 : listOrderByBL) {
						Map<String, Object> mapOrder = new FastMap<String, Object>();

						GenericValue orderHeader = delegator.findOne("OrderHeader",
								UtilMisc.toMap("orderId", (String) order1.get("orderId")), false);
						String currencyUom = (String) orderHeader.get("currencyUom");
						BigDecimal grandTotal = (BigDecimal) orderHeader.get("grandTotal");
						List<GenericValue> uomConvert = delegator.findList("UomConversionDated",
								EntityCondition.makeCondition(UtilMisc.toMap("uomId", currencyUom, "uomIdTo", "VND")),
								null, null, null, false);
						GenericValue valueVND = EntityUtil.getFirst(EntityUtil.filterByDate(uomConvert, arrivalDate));
						Double valueConvert = new Double(1);
						if (valueVND != null) {
							valueConvert = (Double) valueVND.get("conversionFactor");
						}

						BigDecimal valueConvertBig = BigDecimal.valueOf(valueConvert);
						BigDecimal valueOneOrder = valueConvertBig.multiply(grandTotal);
						totalValue = totalValue.add(valueOneOrder);
						mapOrder.put("orderId", (String) order1.get("orderId"));
						mapOrder.put("quantity", valueOneOrder);
						listOrder.add(mapOrder);
					}
					for (Map<String, Object> map : listOrder) {
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("oneOrder", (BigDecimal) map.get("quantity"));
						mapContext.put("perOrder", totalValue);
						mapContext.put("totalCost", costPriceTemporary);
						mapContext.put("orderId", (String) map.get("orderId"));
						mapContext.put("costAccBaseId", costAccBaseId);
                        mapContext.put("exchangedRate", exchangedRate);
                        mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateCostOrder", mapContext);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError(e.getMessage());
						}
					}
				}
				else if ("COST_PERCENTAGE".equals(applicationBaseId)) {
				    for(GenericValue order : listOrderByBL) {
                        List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", order.get("orderId")), null, null, null, false);
				        BigDecimal quantity = BigDecimal.ONE;
				        BigDecimal perOrder = BigDecimal.ONE;
				        BigDecimal totalCost = BigDecimal.ZERO;
				        GenericValue costAccBase = delegator.findOne("CostAccBase", UtilMisc.toMap("costAccBaseId", costAccBaseId), false);
				        String invoiceItemTypeId = costAccBase.getString("invoiceItemTypeId");
				        for(GenericValue orderItem : orderItems) {
                            BigDecimal itemQuantity = orderItem.getBigDecimal("quantity");
                            String productId = orderItem.getString("productId");
                            BigDecimal unitPrice = orderItem.getBigDecimal("unitPrice");
                            List<EntityCondition> conds = FastList.newInstance();
                            conds.add(EntityCondition.makeCondition("productId", productId));
                            conds.add(EntityUtil.getFilterByDateExpr());
                            if("PINV_IMPTAX_ITEM".equals(invoiceItemTypeId)) {
                                conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.LIKE, "TAX_IMP_%"));
                            }
                            else {
                                conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.LIKE, "TAX_EXCISE_%"));
                            }
                            GenericValue productCategoryMember = EntityUtil.getFirst(delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conds), null, null, null, false));
                            if(UtilValidate.isEmpty(productCategoryMember)) {
                                if("PINV_IMPTAX_ITEM".equals(invoiceItemTypeId))
                                    return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductNotHaveImportTax", UtilMisc.toMap("productCode", orderItem.get("productId")), locale));
                                else return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductNotHaveExciseTax", UtilMisc.toMap("productCode", orderItem.get("productId")), locale));
                            }
                            String productCategoryId = productCategoryMember.getString("productCategoryId");
                            GenericValue rate = EntityUtil.getFirst(delegator.findList("TaxAuthorityRateProduct", EntityCondition.makeCondition("productCategoryId", productCategoryId), null, null, null, false));
                            BigDecimal percentage = rate.getBigDecimal("taxPercentage");
                            totalCost = totalCost.add(itemQuantity.multiply(percentage).multiply(unitPrice).divide(new BigDecimal(100)));
                        }
                        totalCost = totalCost.multiply(exchangedRate);
                        Map<String, Object> mapContext = new HashMap<String, Object>();
                        mapContext.put("oneOrder", quantity);
                        mapContext.put("perOrder", perOrder);
                        mapContext.put("totalCost", totalCost);
                        mapContext.put("orderId", order.get("orderId"));
                        mapContext.put("costAccBaseId", costAccBaseId);
                        mapContext.put("exchangedRate", exchangedRate);
                        mapContext.put("userLogin", userLogin);
                        try {
                            dispatcher.runSync("updateCostOrder", mapContext);
                        } catch (GenericServiceException e) {
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    }
                }
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> getInfomationPackingList(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String containerId = (String) context.get("containerId");
		int netWeightTotal = 0;
		int grossWeightTotal = 0;
		int orderUnit = 0;
		List<GenericValue> listPackingListHeader = delegator.findList("PackingListHeader",
				EntityCondition.makeCondition("containerId", containerId), null, null, null, false);
		if (UtilValidate.isNotEmpty(listPackingListHeader)) {
			for (GenericValue x : listPackingListHeader) {
				BigDecimal thisNetWeightTotal = x.getBigDecimal("netWeightTotal");
				netWeightTotal += thisNetWeightTotal.intValue();
				BigDecimal thisGrossWeightTotal = x.getBigDecimal("grossWeightTotal");
				grossWeightTotal += thisGrossWeightTotal.intValue();
				String packingListId = x.getString("packingListId");
				List<GenericValue> listPackingListDetail = delegator.findList("PackingListDetail",
						EntityCondition.makeCondition("packingListId", packingListId), null, null, null, false);
				for (GenericValue z : listPackingListDetail) {
					Long longOrderUnit = z.getLong("orderUnit");
					orderUnit += longOrderUnit.intValue();
				}
			}
		}
		result.put("netWeightTotal", netWeightTotal);
		result.put("grossWeightTotal", grossWeightTotal);
		result.put("orderUnit", orderUnit);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetContainers(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		String billId = null;
		if (parameters.containsKey("billId") && parameters.get("billId").length > 0) {
			billId = (String) parameters.get("billId")[0];
		}
		if (UtilValidate.isNotEmpty(billId)) {
			listAllConditions.add(EntityCondition.makeCondition("billId", billId));
		}
		try {
			if (listSortFields.isEmpty()){
				listSortFields.add("-containerId");
			}
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ContainerAndBillLading", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetContainers service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPackingLists(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listPackingLists = FastList.newInstance();
		String billId = null;
		if (parameters.containsKey("billId") && parameters.get("billId").length > 0) {
			billId = (String) parameters.get("billId")[0];
		}
		if (UtilValidate.isNotEmpty(billId)) {
			List<GenericValue> listContainer = FastList.newInstance();
			try {
				listContainer = delegator.findList("Container", EntityCondition.makeCondition("billId", billId), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList Container: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			List<String> containerIds = EntityUtil.getFieldListFromEntityList(listContainer, "containerId", true);
			if (!containerIds.isEmpty()){
				listAllConditions.add(EntityCondition.makeCondition("containerId", EntityOperator.IN, containerIds));
			} else {
				successResult.put("listIterator", listPackingLists);
				return successResult;
			}
		}
		String containerId = null;
		if (parameters.containsKey("containerId") && parameters.get("containerId").length > 0) {
			containerId = (String) parameters.get("containerId")[0];
		}
		if (UtilValidate.isNotEmpty(containerId)) {
			listAllConditions.add(EntityCondition.makeCondition("containerId", containerId));
		}
		
		String agreementId = null;
		if (parameters.containsKey("agreementId") && parameters.get("agreementId").length > 0) {
			agreementId = (String) parameters.get("agreementId")[0];
		}
		if (UtilValidate.isNotEmpty(agreementId)) {
			List<GenericValue> listAgreementAndOrder = FastList.newInstance();
			try {
				listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition("agreementId", agreementId), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList AgreementAndOrder: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listAgreementAndOrder.isEmpty()){
				List<String> orderIds = EntityUtil.getFieldListFromEntityList(listAgreementAndOrder, "orderId", true);
				if (!orderIds.isEmpty()){
					listAllConditions.add(EntityCondition.makeCondition("purchaseOrderId", orderIds));
				}
			}
		}
		try {
			if (listSortFields.isEmpty()){
				listSortFields.add("-packingListId");
			}
			listPackingLists = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PackingListHeaderDetail", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetPackingLists service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listPackingLists);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetBillOfLading(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		try {
			if (listSortFields.isEmpty()){
				listSortFields.add("-billId");
			}
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "BillOfLading", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetBillOfLading service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	
	
	public static Map<String, Object> getOrderItemByAgreements(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String agreementId = (String) context.get("agreementId");
		List<GenericValue> listAgreeAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
		List<String> orderIds = EntityUtil.getFieldListFromEntityList(listAgreeAndOrder, "orderId", true);
		List<EntityExpr> exprs = UtilMisc.toList(
				EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds),
				EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
		List<GenericValue> listOrderItem = delegator.findList("OrderItemAndProduct", EntityCondition.makeCondition(exprs, EntityJoinOperator.AND), null, null, null, false);
		List<Map<String, Object>> listIterator = FastList.newInstance();
		for (GenericValue orderItem : listOrderItem) {
			Map<String, Object> map = FastMap.newInstance();
			map.putAll(orderItem);
			map.put("originOrderUnit", (BigDecimal) orderItem.getBigDecimal("quantity"));
			map.put("packingUnit", 0);
			map.put("orderUnit", (BigDecimal) orderItem.getBigDecimal("quantity"));
			listIterator.add(map);
		}
		result.put("listProducts", listIterator);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetBillCost(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers;
		String billId;
		if (parameters.containsKey("billId") && parameters.get("billId").length > 0) {
			billId = parameters.get("billId")[0];
			if (UtilValidate.isNotEmpty(billId)) {
				listAllConditions.add(EntityCondition.makeCondition("billOfLadingId", billId));
			}
		}
		try {
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "CostBillAccountingDetail", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetBillCost service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetCostAccBase(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		String costAccountingTypeId = null;
		if (parameters.containsKey("costAccountingTypeId") && parameters.get("costAccountingTypeId").length > 0) {
			costAccountingTypeId = (String) parameters.get("costAccountingTypeId")[0];
			if (UtilValidate.isNotEmpty(costAccountingTypeId)) {
				listAllConditions.add(EntityCondition.makeCondition("costAccountingTypeId", costAccountingTypeId));
			}
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", company));
		try {
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "CostAccBaseAndType", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetCostAccBase service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	
	public static Map<String, Object> createPortOfDischarge(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String address = (String) context.get("address");
		String facilityName = (String) context.get("facilityName");
		String facilityCode = (String) context.get("facilityCode");
		String countryGeoId = (String) context.get("countryGeoId");
		String provinceGeoId = (String) context.get("provinceGeoId");
		String districtGeoId = (String) context.get("districtGeoId");
		String wardGeoId = (String) context.get("wardGeoId");
		
		GenericValue facility = delegator.makeValue("Facility");
		facility.set("facilityName", facilityName);
		facility.set("facilityCode", facilityCode);
		facility.set("primaryFacilityGroupId", "FACILITY_OTHER");
		facility.set("facilityTypeId", "PORT");
		String facilityId = delegator.getNextSeqId("facility");
		facility.set("facilityId", facilityId);
		
		try {
			delegator.create(facility);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createPortOfDischarge service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}

		GenericValue contactMech = delegator.makeValue("ContactMech");
		contactMech.put("contactMechId", delegator.getNextSeqId("ContactMech"));
		contactMech.put("contactMechTypeId", "POSTAL_ADDRESS");
		try {
			delegator.create(contactMech);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createPortOfDischarge service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		GenericValue facContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
		facContactMechPurpose.put("facilityId", facilityId);
		facContactMechPurpose.put("contactMechId", contactMech.get("contactMechId"));
		facContactMechPurpose.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
		facContactMechPurpose.put("fromDate", UtilDateTime.nowTimestamp());
		try {
			delegator.create(facContactMechPurpose);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createPortOfDischarge service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		GenericValue postalAddress = delegator.makeValue("PostalAddress");
		postalAddress.put("contactMechId", contactMech.get("contactMechId"));
		postalAddress.put("address1", address);
		postalAddress.put("countryGeoId", countryGeoId);
		postalAddress.put("stateProvinceGeoId", provinceGeoId);
		postalAddress.put("districtGeoId", districtGeoId);
		postalAddress.put("wardGeoId", wardGeoId);
		try {
			delegator.create(postalAddress);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createPortOfDischarge service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		result.put("facilityId", facilityId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPackingListItems(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		String packingListId = null;
		if (parameters.containsKey("packingListId") && parameters.get("packingListId").length > 0) {
			packingListId = (String) parameters.get("packingListId")[0];
			listAllConditions.add(EntityCondition.makeCondition("packingListId", packingListId));
		}
		try {
			if (listSortFields.isEmpty()){
				listSortFields.add("productId");
			}
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PackingListDetailAndOrderAndProduct", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetPackingListItems service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	

	public static Map<String, Object> getAgreementProductAppls(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		
		String agreementId = null;
		if (UtilValidate.isNotEmpty(context.get("agreementId"))) {
			agreementId = (String) context.get("agreementId");
		}
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		conds.add(EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		
		List<GenericValue> listAgreementProductAppl = FastList.newInstance();
		try {
			listAgreementProductAppl = delegator.findList("AgreementProductApplView", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementProductAppl: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		result.put("listProducts", listAgreementProductAppl);
		return result;
	}
	
	public static Map<String, Object> createImportDocument(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> map = FastMap.newInstance();
		map.put("billNumber", context.get("billNumber"));
		map.put("partyIdFrom", context.get("partyIdFrom"));
		map.put("description", context.get("description"));
		Long departureDate = null;
		if (UtilValidate.isNotEmpty(context.get("departureDate"))) {
			departureDate = (Long) context.get("departureDate");
			map.put("departureDate", new Timestamp(departureDate));
		}
		
		Long arrivalDate = null;
		if (UtilValidate.isNotEmpty(context.get("arrivalDate"))) {
			arrivalDate = (Long) context.get("arrivalDate");
			map.put("arrivalDate", new Timestamp(arrivalDate));
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		map.put("userLogin", userLogin);
		String billId = null;
		try {
			Map<String, Object> rs = dispatcher.runSync("updateBillOfLading", map);
			if (ServiceUtil.isError(rs)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
			}
			billId = (String)rs.get("billId");
		} catch (GenericServiceException e) {
			String errMsg = "OLBIUS: Fatal error when run service service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(billId)) {
			map = FastMap.newInstance();
			map.put("userLogin", userLogin);
			map.put("billId", billId);
			map.put("description", context.get("contDescription"));
			map.put("containerNumber", context.get("containerNumber"));
			map.put("containerTypeId", context.get("containerTypeId"));
			map.put("sealNumber", context.get("sealNumber"));
			map.put("description", context.get("contDescription"));
			String containerId = null;
			try {
				Map<String, Object> rs = dispatcher.runSync("updateContainer", map);
				if (ServiceUtil.isError(rs)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
				}
				containerId = (String)rs.get("containerId");
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run service updateContainer: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (UtilValidate.isNotEmpty(containerId)) {
				String packingList = null;
				
				map = FastMap.newInstance();
				map.put("containerId", containerId);
				map.put("billId", billId);
				map.put("agreementId", context.get("agreementId"));
				map.put("packingListNumber", context.get("packingListNumber"));
				map.put("orderNumberSupp", context.get("orderNumberSupp"));
				map.put("orderTypeSuppId", context.get("orderTypeSuppId"));
				map.put("invoiceNumber", context.get("invoiceNumber"));
				map.put("totalNetWeight", context.get("totalNetWeight"));
				map.put("totalGrossWeight", context.get("totalGrossWeight"));
				map.put("packingListDate", context.get("packingListDate"));
				map.put("invoiceDate", context.get("invoiceDate"));
				map.put("destFacilityId", context.get("destFacilityId"));
				
				packingList = convertMapToJSON(map);
				map = FastMap.newInstance();
				map.put("userLogin", userLogin);
				map.put("packingList", packingList.toString());
				map.put("packingListDetail", context.get("packingListDetail"));
				
				try {
					Map<String, Object> rs = dispatcher.runSync("createContainerAndPackingList", map);
					if (ServiceUtil.isError(rs)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
					}
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when run service createContainerAndPackingList: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			}
		}
		result.put("billId", billId);
		return result;
	}

	public static Map<String, Object> createAccCostForProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
	    Delegator delegator = ctx.getDelegator();
	    Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    String orderId = (String) context.get("orderId");
	    String costAccountingId =(String) context.get("costAccountingId");
	    String productId = (String) context.get("productId");
	    BigDecimal totalCost = (BigDecimal) context.get("totalCost");
	    GenericValue costProduct = delegator.makeValue("CostProductInOrder");
	    String costProductInOrderId = delegator.getNextSeqId("CostProductInOrder");
	    costProduct.set("costProductInOrderId", costProductInOrderId);
	    costProduct.set("orderId", orderId);
	    costProduct.set("costAccountingId", costAccountingId);
	    costProduct.set("totalCost", totalCost);
	    costProduct.set("currencyUomId", context.get("currencyUomId"));
	    costProduct.set("productId", productId);
        try {
            costProduct.create();
            successResult.put("costProductInOrderId", costProductInOrderId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }
	
	public static String convertMapToJSON(Map<String, Object> map) {       
        JSONObject json_obj = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            json_obj.put(key,value);
        }
   	    return json_obj.toString();
   	}
}