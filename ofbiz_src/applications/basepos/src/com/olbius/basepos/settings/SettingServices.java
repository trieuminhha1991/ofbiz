package com.olbius.basepos.settings;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class SettingServices {
	public static final String module = SettingServices.class.getName();
	public static final String resource = "BasePosUiLabels";
	public static final String resource_error = "BasePosErrorUiLabels";

	public static Map<String, Object> createTerminalPOS(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue posTerminal = delegator.findOne("PosTerminal", UtilMisc.toMap("posTerminalId", context.get("posTerminalId")), false);
			if (UtilValidate.isNotEmpty(posTerminal)) {
				Locale locale = (Locale) context.get("locale");
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BPOSTerminalIdExisted", locale));
			}
			GenericValue newTeminal = delegator.makeValidValue("PosTerminal", context);
			newTeminal.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateTerminalPOS(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue posTerminal = delegator.findOne("PosTerminal", UtilMisc.toMap("posTerminalId", context.get("posTerminalId")), false);
			if (UtilValidate.isNotEmpty(posTerminal)) {
				posTerminal = delegator.makeValidValue("PosTerminal", context);
				posTerminal.store();
			} else {
				Locale locale = (Locale) context.get("locale");
				result = ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BPOSNotFoundTerminalPOS", locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteTerminalPOS(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue posTerminal = delegator.findOne("PosTerminal", UtilMisc.toMap("posTerminalId", context.get("posTerminalId")), false);
			if (UtilValidate.isNotEmpty(posTerminal)) {
				posTerminal.remove();
			} else {
				Locale locale = (Locale) context.get("locale");
				result = ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BPOSNotFoundTerminalPOS", locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listConfigPrintOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			EntityListIterator listIterator = delegator.find("ConfigPrintOrderAndStore",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListConfigPrintOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}

	public static Map<String, Object> createConfigPrintOrderPOS(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue configPrint = delegator.findOne("ConfigPrintOrder", UtilMisc.toMap("productStoreId", context.get("productStoreId")), false);
			if (UtilValidate.isNotEmpty(configPrint)) {
				Locale locale = (Locale) context.get("locale");
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BPOSConfigPrintOrderInStoreExisted",
								UtilMisc.toMap("productStoreId", context.get("productStoreId")), locale));
			}
			GenericValue newConfig = delegator.makeValidValue("ConfigPrintOrder", context);
			newConfig.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateConfigPrintOrderPOS(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue configPrint = delegator.findOne("ConfigPrintOrder", UtilMisc.toMap("productStoreId", context.get("productStoreId")), false);
			if (UtilValidate.isNotEmpty(configPrint)) {
				configPrint = delegator.makeValidValue("ConfigPrintOrder", context);
				configPrint.store();
			} else {
				Locale locale = (Locale) context.get("locale");
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
						"BPOSNotFoundConfigPrintOrderWithStore", UtilMisc.toMap("productStoreId", context.get("productStoreId")), locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteConfigPrintOrderPOS(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue configPrint = delegator.findOne("ConfigPrintOrder", UtilMisc.toMap("productStoreId", context.get("productStoreId")), false);
			if (UtilValidate.isNotEmpty(configPrint)) {
				try {
					configPrint.remove();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			} else {
				Locale locale = (Locale) context.get("locale");
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
						"BPOSNotFoundConfigPrintOrderWithStore", UtilMisc.toMap("productStoreId", context.get("productStoreId")), locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static String getInfoOfPayment(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId",request.getParameter("paymentId")), false);
			if (UtilValidate.isNotEmpty(payment)) {
				request.setAttribute("paymentTypeId", payment.getString("paymentTypeId"));
				request.setAttribute("partyIdTo", payment.getString("partyIdTo"));
				request.setAttribute("amount", payment.getBigDecimal("amount"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getInfoOfOrder(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", request.getParameter("orderId")), false);
			if (UtilValidate.isNotEmpty(order)) {
				request.setAttribute("grandTotal", order.getBigDecimal("grandTotal"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getInvoiceFromOrder(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> invoice = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("orderId", request.getParameter("orderId")), null, true);
			if (UtilValidate.isNotEmpty(invoice)) {
				request.setAttribute("invoiceId", invoice.get(0).getString("invoiceId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getInvoiceFromRequirement(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> invoice = delegator.findByAnd("RequirementItemBilling", UtilMisc.toMap("requirementId", request.getParameter("requirementId")), null, true);
			if (UtilValidate.isNotEmpty(invoice)) {
				request.setAttribute("invoiceId", invoice.get(0).getString("invoiceId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getOrderAgreement(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, request.getParameter("agreementId")));
			List<GenericValue> agreements = delegator.findList("AgreementAndOrder",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(agreements)) {
				request.setAttribute("orderId", agreements.get(0).getString("orderId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getCustomTimePeriodIdYear(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> period = delegator.findByAnd("CustomTimePeriod",
					UtilMisc.toMap("periodName", request.getParameter("year"), "periodTypeId", "COMMERCIAL_YEAR"), null, true);
			if (UtilValidate.isNotEmpty(period)) {
				request.setAttribute("customTimePeriodId", period.get(0).getString("customTimePeriodId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getCustomTimePeriodIdWeek(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException, ParseException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			String fromDateStr = request.getParameter("fromDate");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date parsed = format.parse(fromDateStr);
			Date fromDate = new Date(parsed.getTime());
			List<GenericValue> period = delegator.findByAnd("CustomTimePeriod",
					UtilMisc.toMap("fromDate", fromDate, "periodTypeId", "COMMERCIAL_WEEK"), null, true);
			if (UtilValidate.isNotEmpty(period)) {
				request.setAttribute("customTimePeriodId", period.get(0).getString("customTimePeriodId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getProductPlan(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, (String) userLogin.get("userLoginId"));
			List<GenericValue> plan = delegator.findByAnd("ProductPlanHeader",
					UtilMisc.toMap("customTimePeriodId", request.getParameter("customTimePeriodId"), "organizationPartyId", organizationPartyId), null, true);
			if (UtilValidate.isNotEmpty(plan)) {
				request.setAttribute("productPlanId", plan.get(0).getString("productPlanId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getSalesStatement(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, (String) userLogin.get("userLoginId"));
			List<GenericValue> salesStatements = delegator.findByAnd("SalesStatement",
					UtilMisc.toMap("customTimePeriodId", request.getParameter("customTimePeriodId"), "organizationPartyId", organizationPartyId,
							"salesStatementTypeId", request.getParameter("salesStatementTypeId"), "parentSalesStatementId", null), null, true);
			if (UtilValidate.isNotEmpty(salesStatements)) {
				request.setAttribute("salesStatementId", salesStatements.get(0).getString("salesStatementId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getPaymentFromReturnSupplier(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> listReturnBilling = delegator.findByAnd("ReturnItemBilling",
					UtilMisc.toMap("returnId", request.getParameter("returnId")), null, false);
			if (UtilValidate.isNotEmpty(listReturnBilling)) {
				GenericValue returnBilling = listReturnBilling.get(0);
				String invoiceId = returnBilling.getString("invoiceId");
				List<GenericValue> listPayment = delegator.findByAnd("PaymentApplication",
						UtilMisc.toMap("invoiceId", invoiceId), null, false);
				if (UtilValidate.isNotEmpty(listPayment)) {
					request.setAttribute("paymentId", listPayment.get(0).getString("paymentId"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getListINVByRequirement(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<Map<String, Object>> listData = FastList.newInstance();
			List<GenericValue> listProduct = delegator.findByAnd("RequirementItem",
					UtilMisc.toMap("requirementId", request.getParameter("requirementId")), null, false);
			if (UtilValidate.isNotEmpty(listProduct)) {
				for (GenericValue pro : listProduct) {
					String productId = pro.getString("productId");
					List<GenericValue> listINV = delegator.findByAnd("InventoryItem",
							UtilMisc.toMap("productId", productId, "facilityId", request.getParameter("facilityId")), null, false);
					if (UtilValidate.isNotEmpty(listINV)) {
						for (GenericValue item : listINV) {
							Map<String, Object> inv = FastMap.newInstance();
							inv.put("productId", productId);
							inv.put("quantity", pro.getBigDecimal("quantity"));
							inv.put("inventoryItemId", item.getString("inventoryItemId"));
							inv.put("quantityOnHandTotal", item.getBigDecimal("quantityOnHandTotal"));
							listData.add(inv);
						}
					}
				}
			}
			request.setAttribute("listData", listData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	@SuppressWarnings("unchecked")
	public static String getListReturnOrderItem(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		try {
			List<Map<String, Object>> listReturnOrderItem = FastList.newInstance();
			List<GenericValue> returnItemTypeList = delegator.findByAnd("ReturnItemTypeMap",
					UtilMisc.toMap("returnHeaderTypeId", "CUSTOMER_RETURN"), null, false);
			Map<Object, Object> returnItemTypeMap = FastMap.newInstance();
			for (GenericValue item : returnItemTypeList) {
				returnItemTypeMap.put(item.get("returnItemMapKey"), item.getString("returnItemTypeId"));
			}
			Map<String, Object> returnRes = dispatcher.runSync("getReturnableItems", UtilMisc.toMap("orderId", request.getParameter("orderId")));
			if (UtilValidate.isNotEmpty(returnRes)) {
				Map<GenericValue, Map<String, Object>> returnableItems = (Map<GenericValue, Map<String, Object>>) returnRes
						.get("returnableItems");
				for (GenericValue orderItem : returnableItems.keySet()) {
					if (orderItem.getEntityName() != "OrderAdjustment") {
						String returnItemType = (String) returnItemTypeMap
								.get(returnableItems.get(orderItem).get("itemTypeKey"));
						GenericValue orderHeader = orderItem.getRelatedOne("OrderHeader", false);
						String currencyDefault = orderHeader.getString("currencyUom");
						GenericValue product = orderItem.getRelatedOne("Product", false);
						BigDecimal returnPrice = BigDecimal.ZERO;
						if (product.getString("productTypeId") == "ASSET_USAGE_OUT_IN") {
							returnPrice = BigDecimal.ZERO;
						} else {
							returnPrice = (BigDecimal) returnableItems.get(orderItem).get("returnablePrice");
						}
						Map<String, Object> item = FastMap.newInstance();
						item.put("returnItemTypeId", returnItemType);
						item.put("orderId", orderItem.getString("orderId"));
						item.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
						item.put("description", orderItem.getString("description"));
						item.put("productId", orderItem.getString("productId"));
						item.put("quantity", orderItem.getBigDecimal("quantity"));
						item.put("returnableQuantity", returnableItems.get(orderItem).get("returnableQuantity"));
						item.put("returnQuantity", returnableItems.get(orderItem).get("returnableQuantity"));
						item.put("unitPrice", orderItem.getString("unitPrice"));
						item.put("returnPrice", returnPrice);
						item.put("returnReasonId", "RTN_PHONE_FALSE");
						item.put("returnTypeId", "RTN_REFUND");
						item.put("expectedItemStatus", "INV_RETURNED");
						item.put("currencyUomId", currencyDefault);
						listReturnOrderItem.add(item);
					}
				}
			}
			request.setAttribute("listReturnOrderItem", listReturnOrderItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getListProductStoreExhibition(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> listProductStore = delegator.findByAnd("ProductStorePromoExtAppl",
					UtilMisc.toMap("productPromoId", request.getParameter("productPromoId")), null, false);
			List<String> listStore = FastList.newInstance();
			if (UtilValidate.isNotEmpty(listProductStore)) {
				for (GenericValue store : listProductStore) {
					listStore.add(store.getString("productStoreId"));
				}
			}
			request.setAttribute("listStore", listStore);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getPromoRuleCustomer(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> listRule = delegator.findByAnd("ProductPromoExtRegister", UtilMisc.toMap("productPromoId",
					request.getParameter("productPromoId"), "partyId", request.getParameter("partyId"), "statusId", "PROMO_REGISTRATION_ACCEPTED"), null, false);
			if (UtilValidate.isNotEmpty(listRule)) {
				request.setAttribute("productPromoRuleId", listRule.get(0).getString("productPromoRuleId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String getListCustomerPromosRegistrationSup(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			List<String> getAllRoute = FastList.newInstance();
/*			List<GenericValue> routes = delegator.findList("RouteDetail",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("createdByUserLogin", userLogin.getString("userLoginId")),
									EntityCondition.makeCondition("partyTypeId", "SALES_ROUTE"),
									EntityCondition.makeCondition("statusId", "PARTY_ENABLED"))),
					UtilMisc.toSet("partyId"), UtilMisc.toList("partyId"), null, false);
			if (UtilValidate.isNotEmpty(routes)) {
				for (GenericValue e : routes) {
					getAllRoute.add(e.getString("partyId"));
				}
			}*/
			List<GenericValue> routes = delegator.findList("Route",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("managerId", userLogin.getString("partyId")),
							EntityCondition.makeCondition("statusId", "ROUTE_ENABLED"))),
					UtilMisc.toSet("routeId"), UtilMisc.toList("routeId"), null, false);
			if (UtilValidate.isNotEmpty(routes)) {
				for (GenericValue e : routes) {
					getAllRoute.add(e.getString("routeId"));
				}
			}
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(EntityUtil.getFilterByDateExpr("relFromDate", "relThruDate"));
			listCond.add(EntityUtil.getFilterByDateExpr());
			listCond.add(EntityCondition.makeCondition("parStatusId", "PARTY_ENABLED"));
			listCond.add(EntityCondition.makeCondition("roleTypeIdFrom", "ROUTE"));
			listCond.add(EntityCondition.makeCondition("roleTypeIdTo", "CUSTOMER"));
			listCond.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, getAllRoute));
			listCond.add(EntityCondition.makeCondition("productPromoId", request.getParameter("productPromoId")));
			listCond.add(EntityCondition.makeCondition("statusId", request.getParameter("statusId")));

			List<GenericValue> listCustomerPromosRegistrationSup = delegator.findList(
					"PartyRelationshipAndPartyToPromosExt", EntityCondition.makeCondition(listCond), null, null, null, false);

			List<Map<String, Object>> listData = FastList.newInstance();
			if (UtilValidate.isNotEmpty(listCustomerPromosRegistrationSup)) {
				for (GenericValue item : listCustomerPromosRegistrationSup) {
					Map<String, Object> itemMap = FastMap.newInstance();
					itemMap.put("partyId", item.getString("partyId"));
					itemMap.put("productPromoId", request.getParameter("productPromoId"));
					itemMap.put("productPromoRuleId", item.getString("productPromoRuleId"));
					Timestamp fromDateT = item.getTimestamp("fromDate");
					Long fromDate = fromDateT.getTime();
					itemMap.put("fromDate", fromDate.toString());
					listData.add(itemMap);
				}
			}
			request.setAttribute("listData", listData);
			request.setAttribute("totalRow", listData.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

}