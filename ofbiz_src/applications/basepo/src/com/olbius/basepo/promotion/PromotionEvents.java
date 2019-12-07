package com.olbius.basepo.promotion;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

public class PromotionEvents {
	public static final String module = PromotionEvents.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";

	public static final String MULTI_ROW_DELIMITER = "_";
	public static final String MULTI_ROW_DELIMITER_SEQ = "_r_";
	public static final String MULTI_ROW_DELIMITER_SEQ_COND = "_c_";
	public static final String MULTI_ROW_DELIMITER_SEQ_ACT = "_a_";

	@SuppressWarnings("unchecked")
	public static String createProductPromoAdvance(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		Security security = (Security) request.getAttribute("security");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PROMOPO_NEW")) {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
			return "error";
		}

		// Get parameter information general
		String productPromoId = request.getParameter("productPromoId");
		String promoName = request.getParameter("promoName");
		String productPromoTypeId = "PURCHASE_PROMO"; //request.getParameter("productPromoTypeId");
		String[] supplierIdsStr = request.getParameterValues("supplierIds");
		String promoText = request.getParameter("promoText");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String showToCustomer = request.getParameter("showToCustomer");
		String requireCode = request.getParameter("requireCode");
		String useLimitPerOrderStr = request.getParameter("useLimitPerOrder");
		String useLimitPerCustomerStr = request.getParameter("useLimitPerCustomer");
		String useLimitPerPromotionStr = request.getParameter("useLimitPerPromotion");
		Long useLimitPerOrder = null;
		Long useLimitPerCustomer = null;
		Long useLimitPerPromotion = null;

		Timestamp fromDate = null;
		Timestamp thruDate = null;
		try {
			if (UtilValidate.isNotEmpty(fromDateStr)) {
				Long fromDateL = Long.parseLong(fromDateStr);
				fromDate = new Timestamp(fromDateL);
			}
			if (UtilValidate.isNotEmpty(thruDateStr)) {
				Long thruDateL = Long.parseLong(thruDateStr);
				thruDate = new Timestamp(thruDateL);
			}
			if (UtilValidate.isNotEmpty(useLimitPerOrderStr)) useLimitPerOrder = Long.parseLong(useLimitPerOrderStr);
			if (UtilValidate.isNotEmpty(useLimitPerCustomerStr)) useLimitPerCustomer = Long.parseLong(useLimitPerCustomerStr);
			if (UtilValidate.isNotEmpty(useLimitPerPromotionStr)) useLimitPerPromotion = Long.parseLong(useLimitPerPromotionStr);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
			return "error";
		}
		if (useLimitPerOrder != null && useLimitPerOrder <= 0) useLimitPerOrder = null;
		if (useLimitPerCustomer != null && useLimitPerCustomer <= 0) useLimitPerCustomer = null;
		if (useLimitPerPromotion != null && useLimitPerPromotion <= 0) useLimitPerPromotion = null;

		List<Object> errMsgList = FastList.newInstance();

		boolean beganTx = false;
		String productPromoIdSuccess = "";
		try {
			// begin the transaction
			beganTx = TransactionUtil.begin(7200);
			String controlDirective = null;
			List<String> supplierIds = null;
			if (UtilValidate.isNotEmpty(supplierIdsStr)) supplierIds = Arrays.asList(supplierIdsStr);
			
			String organizationPartyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Map<String, Object> contextMap = FastMap.newInstance();
			contextMap.put("productPromoId", productPromoId);
			contextMap.put("promoName", promoName);
			contextMap.put("productPromoTypeId", productPromoTypeId);
			contextMap.put("supplierIds", supplierIds);
			contextMap.put("promoText", promoText);
			contextMap.put("fromDate", fromDate);
			contextMap.put("thruDate", thruDate);
			contextMap.put("showToCustomer", showToCustomer);
			contextMap.put("requireCode", requireCode);
			contextMap.put("useLimitPerOrder", useLimitPerOrder);
			contextMap.put("useLimitPerCustomer", useLimitPerCustomer);
			contextMap.put("useLimitPerPromotion", useLimitPerPromotion);
			contextMap.put("organizationPartyId", organizationPartyId);
			contextMap.put("userLogin", userLogin);
			contextMap.put("locale", locale);
			Map<String, Object> result0 = dispatcher.runSync("createProductPromoCustomPO", contextMap);
			// no values for price and paramMap (a context for adding attributes)
			controlDirective = SalesUtil.processResult(result0, request);
			if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
				try {
					TransactionUtil.rollback(beganTx, "Failure in processing Create product promo callback", null);
				} catch (Exception e1) {
					Debug.logError(e1, module);
				}
				return "error";
			}

			productPromoIdSuccess = (String) result0.get("productPromoId");

			// Get the parameters as a MAP, remove the productId and quantity
			// params.
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);

			// The number of multi form rows is retrieved
			int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
			if (rowCount < 1) {
				Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			} else {
				for (int i = 0; i < rowCount; i++) {
					// process list rule (condition, action)
					String ruleName = null;
					String ruleText = null;

					controlDirective = null; // re-initialize each time
					String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i; // current suffix after each field id

					// get the productId
					if (paramMap.containsKey("ruleName" + thisSuffix)) {
						ruleName = (String) paramMap.remove("ruleName" + thisSuffix);
					}
					if (paramMap.containsKey("ruleText" + thisSuffix)) {
						ruleText = (String) paramMap.remove("ruleText" + thisSuffix);
					}

					if (UtilValidate.isEmpty(ruleName)) {
						continue;
					}

					String productPromoRuleId = "";
					Map<String, Object> result1 = dispatcher.runSync("createProductPromoRuleCustom",
							UtilMisc.<String, Object> toMap("productPromoId", productPromoIdSuccess, "ruleName",
									ruleName, "ruleText", ruleText, "userLogin", userLogin, "locale", locale));
					// no values for price and paramMap (a context for adding attributes)
					controlDirective = SalesUtil.processResult(result1, request);
					if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
						try {
							TransactionUtil.rollback(beganTx,
									"Failure in processing Create product promo rule callback", null);
						} catch (Exception e1) {
							Debug.logError(e1, module);
						}
						return "error";
					}
					productPromoRuleId = (String) result1.get("productPromoRuleId");

					// The number of multi form rows is retrieved: Condition
					int rowCountSeqCond = SalesUtil.getMultiFormRowCountSeq(paramMap, String.valueOf(i),
							MULTI_ROW_DELIMITER_SEQ_COND);
					if (rowCountSeqCond < 1) {
						Debug.logWarning("No rows seq to process, as rowCountSeqCondition = " + rowCountSeqCond,
								module);
					} else {
						for (int j = 0; j < rowCountSeqCond; j++) {
							String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_COND + j + UtilHttp.MULTI_ROW_DELIMITER + i; // current suffix after each field id

							List<String> productIdListCond = FastList.newInstance();
							String productPromoApplEnumId = null;
							String includeSubCategories = null;
							List<String> productCatIdListCond = FastList.newInstance();
							String inputParamEnumId = null;
							String operatorEnumId = null;
							String condValue = null;
							String usePriceWithTax = null;
							String isRemoveCond = "N";

							if (paramMap.containsKey("isRemoveCond" + thisSuffixSeq)) {
								isRemoveCond = (String) paramMap.remove("isRemoveCond" + thisSuffixSeq);
							}
							if ("Y".equals(isRemoveCond)) {
								continue;
							}

							if (paramMap.containsKey("productIdListCond" + thisSuffixSeq)) {
								Object productIdListCondObj = (Object) paramMap.remove("productIdListCond" + thisSuffixSeq);
								if (productIdListCondObj instanceof String) {
									productIdListCond.add(productIdListCondObj.toString());
								} else if (productIdListCondObj instanceof List) {
									productIdListCond = (List<String>) productIdListCondObj;
								}
							}

							if (paramMap.containsKey("productPromoApplEnumId" + thisSuffixSeq)) {
								productPromoApplEnumId = (String) paramMap.remove("productPromoApplEnumId" + thisSuffixSeq);
							}
							if (paramMap.containsKey("includeSubCategories" + thisSuffixSeq)) {
								includeSubCategories = (String) paramMap.remove("includeSubCategories" + thisSuffixSeq);
							}
							if (paramMap.containsKey("productCatIdListCond" + thisSuffixSeq)) {
								Object productCatIdListCondObj = (Object) paramMap
										.remove("productCatIdListCond" + thisSuffixSeq);
								if (productCatIdListCondObj instanceof String) {
									productCatIdListCond.add(productCatIdListCondObj.toString());
								} else if (productCatIdListCondObj instanceof List) {
									productCatIdListCond = (List<String>) productCatIdListCondObj;
								}
							}

							if (paramMap.containsKey("inputParamEnumId" + thisSuffixSeq)) {
								inputParamEnumId = (String) paramMap.remove("inputParamEnumId" + thisSuffixSeq);
							}
							if (paramMap.containsKey("operatorEnumId" + thisSuffixSeq)) {
								operatorEnumId = (String) paramMap.remove("operatorEnumId" + thisSuffixSeq);
							}
							if (paramMap.containsKey("condValue" + thisSuffixSeq)) {
								condValue = (String) paramMap.remove("condValue" + thisSuffixSeq);
							}
							if (paramMap.containsKey("usePriceWithTax" + thisSuffixSeq)) {
								usePriceWithTax = (String) paramMap.remove("usePriceWithTax" + thisSuffixSeq);
							}

							if (UtilValidate.isNotEmpty(condValue)) {
								Map<String, Object> result2 = dispatcher.runSync("createProductPromoCondCustom",
										UtilMisc.<String, Object> toMap("productPromoId", productPromoIdSuccess,
												"productPromoRuleId", productPromoRuleId, 
												"productIdListCond", productIdListCond, "productPromoApplEnumId", productPromoApplEnumId,
												"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
												"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
												"condValue", condValue, "userLogin", userLogin, "locale", locale, "usePriceWithTax", usePriceWithTax));
								// no values for price and paramMap (a context for adding attributes)
								controlDirective = SalesUtil.processResult(result2, request);
								if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
									try {
										TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
									} catch (Exception e1) {
										Debug.logError(e1, module);
									}
									return "error";
								}
							} else {
								errMsgList.add(UtilProperties.getMessage(resource_error, "BSConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoCondSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
							}
						}
					}

					// The number of multi form rows is retrieved: Action
					int rowCountSeqAction = SalesUtil.getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_ACT);
					if (rowCountSeqAction < 1) {
						Debug.logWarning("No rows seq to process, as rowCountSeqAction = " + rowCountSeqAction, module);
					} else {
						for (int j = 0; j < rowCountSeqAction; j++) {
							String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_ACT + j + UtilHttp.MULTI_ROW_DELIMITER + i; // current suffix after each field id

							String orderAdjustmentTypeId = null;
							List<String> productIdListAction = FastList.newInstance();
							String productPromoApplEnumIdAction = null;
							String includeSubCategoriesAction = null;
							List<String> productCatIdListAction = FastList.newInstance();
							String productPromoActionEnumId = null;
							BigDecimal quantity = null;
							String quantityStr = null;
							BigDecimal amount = null;
							String amountStr = null;
							String isRemoveAction = "N";
							String productPromoActionOperEnumId = null;
							String isCheckInv = "N";

							if (paramMap.containsKey("isRemoveAction" + thisSuffixSeq)) {
								isRemoveAction = (String) paramMap.remove("isRemoveAction" + thisSuffixSeq);
							}
							if ("Y".equals(isRemoveAction)) {
								continue;
							}

							if (paramMap.containsKey("orderAdjustmentTypeId" + thisSuffixSeq)) {
								orderAdjustmentTypeId = (String) paramMap.remove("orderAdjustmentTypeId" + thisSuffixSeq);
							}

							if (paramMap.containsKey("productIdListAction" + thisSuffixSeq)) {
								Object productIdListActionObj = (Object) paramMap.remove("productIdListAction" + thisSuffixSeq);
								if (productIdListActionObj instanceof String) {
									productIdListAction.add(productIdListActionObj.toString());
								} else if (productIdListActionObj instanceof List) {
									productIdListAction = (List<String>) productIdListActionObj;
								}
							}

							if (paramMap.containsKey("productCatIdListAction" + thisSuffixSeq)) {
								Object productCatIdListActionObj = (Object) paramMap.remove("productCatIdListAction" + thisSuffixSeq);
								if (productCatIdListActionObj instanceof String) {
									productCatIdListAction.add(productCatIdListActionObj.toString());
								} else if (productCatIdListActionObj instanceof List) {
									productCatIdListAction = (List<String>) productCatIdListActionObj;
								}
							}

							if (paramMap.containsKey("productPromoApplEnumIdAction" + thisSuffixSeq)) {
								productPromoApplEnumIdAction = (String) paramMap.remove("productPromoApplEnumIdAction" + thisSuffixSeq);
							}
							if (paramMap.containsKey("includeSubCategoriesAction" + thisSuffixSeq)) {
								includeSubCategoriesAction = (String) paramMap.remove("includeSubCategoriesAction" + thisSuffixSeq);
							}
							if (paramMap.containsKey("productPromoActionEnumId" + thisSuffixSeq)) {
								productPromoActionEnumId = (String) paramMap.remove("productPromoActionEnumId" + thisSuffixSeq);
							}
							if (paramMap.containsKey("quantity" + thisSuffixSeq)) {
								quantityStr = (String) paramMap.remove("quantity" + thisSuffixSeq);
							}
							if (UtilValidate.isNotEmpty(quantityStr)) {
								// parse the quantity
								try {
									quantity = new BigDecimal(quantityStr);
								} catch (Exception e) {
									Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
									quantity = BigDecimal.ZERO;
								}
							}

							// get the selected amount
							if (paramMap.containsKey("amount" + thisSuffixSeq)) {
								amountStr = (String) paramMap.remove("amount" + thisSuffixSeq);
							}

							// parse the amount
							if (UtilValidate.isNotEmpty(amountStr)) {
								try {
									amount = new BigDecimal(amountStr);
								} catch (Exception e) {
									Debug.logWarning(e, "Problem parsing amount string: " + amountStr, module);
									amount = null;
								}
							}
							if (paramMap.containsKey("productPromoActionOperEnumId" + thisSuffixSeq)) {
								productPromoActionOperEnumId = (String) paramMap.remove("productPromoActionOperEnumId" + thisSuffixSeq);
							}
							/*if (paramMap.containsKey("isCheckInv" + thisSuffixSeq)) {
								isCheckInv = (String) paramMap.remove("isCheckInv" + thisSuffixSeq);
							}*/
							// if (productPromoActionOperEnumId == null)
							// productPromoActionOperEnumId = "PPAOP_AND";
							if (quantity != null || amount != null) {
								// Debug.logInfo("Attempting to add to cart with
								// productId = ", module);
								Map<String, Object> result3 = dispatcher.runSync("createProductPromoActionCustom",
										UtilMisc.<String, Object> toMap("productPromoId", productPromoIdSuccess,
												"productPromoRuleId", productPromoRuleId, "productIdListAction", productIdListAction, 
												"orderAdjustmentTypeId", orderAdjustmentTypeId, 
												"productPromoApplEnumId", productPromoApplEnumIdAction, "includeSubCategories", includeSubCategoriesAction, 
												"productCatIdListAction", productCatIdListAction, "productPromoActionEnumId", productPromoActionEnumId, 
												"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale,
												"operatorEnumId", productPromoActionOperEnumId, "isCheckInv", isCheckInv));
								// no values for price and paramMap (a context for adding attributes)
								controlDirective = SalesUtil.processResult(result3, request);
								if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
									try {
										TransactionUtil.rollback(beganTx, "Failure in processing Create product promo action callback", null);
									} catch (Exception e1) {
										Debug.logError(e1, module);
									}
									return "error";
								}
							} else {
								errMsgList.add(UtilProperties.getMessage(resource_error, "BSApplyValueOfActionInTheRuleMustBeNotEmpty", UtilMisc.toMap("productPromoActionSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			try {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
			} catch (Exception e1) {
				Debug.logError(e1, module);
			}
			errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
			return "error";
		} catch (Throwable t) {
			Debug.logError(t, module);
			request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
			try {
				TransactionUtil.rollback(beganTx, t.getMessage(), t);
			} catch (Exception e2) {
				Debug.logError(e2, module);
			}
			errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
			return "error";
		} finally {
			if (UtilValidate.isNotEmpty(errMsgList)) {
				try {
					TransactionUtil.rollback(beganTx, "Have error when process", null);
				} catch (Exception e2) {
					Debug.logError(e2, module);
				}
				request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
				return "error";
			} else {
				// commit the transaction
				try {
					TransactionUtil.commit(beganTx);
				} catch (Exception e) {
					Debug.logError(e, module);
				}
			}
		}
		request.setAttribute("productPromoId", productPromoIdSuccess);
		return "success";
	}

	@SuppressWarnings("unchecked")
	public static String updateProductPromoAdvance(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		Security security = (Security) request.getAttribute("security");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PROMOPO_EDIT")) {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission", locale));
			return "error";
		}

		// Get parameter information general
		String productPromoId = request.getParameter("productPromoId");
		String promoName = request.getParameter("promoName");
		String[] supplierIdsStr = request.getParameterValues("supplierIds");
		String promoText = request.getParameter("promoText");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String showToCustomer = request.getParameter("showToCustomer");
		String requireCode = request.getParameter("requireCode");
		String useLimitPerOrderStr = request.getParameter("useLimitPerOrder");
		String useLimitPerCustomerStr = request.getParameter("useLimitPerCustomer");
		String useLimitPerPromotionStr = request.getParameter("useLimitPerPromotion");
		Long useLimitPerOrder = null;
		Long useLimitPerCustomer = null;
		Long useLimitPerPromotion = null;

		Timestamp fromDate = null;
		Timestamp thruDate = null;
		try {
			if (UtilValidate.isNotEmpty(fromDateStr)) {
				Long fromDateL = Long.parseLong(fromDateStr);
				fromDate = new Timestamp(fromDateL);
			}
			if (UtilValidate.isNotEmpty(thruDateStr)) {
				Long thruDateL = Long.parseLong(thruDateStr);
				thruDate = new Timestamp(thruDateL);
			}

			if (UtilValidate.isNotEmpty(useLimitPerOrderStr))
				useLimitPerOrder = Long.parseLong(useLimitPerOrderStr);
			if (UtilValidate.isNotEmpty(useLimitPerCustomerStr))
				useLimitPerCustomer = Long.parseLong(useLimitPerCustomerStr);
			if (UtilValidate.isNotEmpty(useLimitPerPromotionStr))
				useLimitPerPromotion = Long.parseLong(useLimitPerPromotionStr);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
			return "error";
		}
		if (useLimitPerOrder != null && useLimitPerOrder <= 0)
			useLimitPerOrder = null;
		if (useLimitPerCustomer != null && useLimitPerCustomer <= 0)
			useLimitPerCustomer = null;
		if (useLimitPerPromotion != null && useLimitPerPromotion <= 0)
			useLimitPerPromotion = null;

		if (UtilValidate.isEmpty(productPromoId)) {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSPromotionIdMustNotBeEmpty", locale));
			return "error";
		}

		List<Object> errMsgList = FastList.newInstance();

		boolean beganTx = false;
		try {
			GenericValue productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
			if (productPromo == null) {
				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission", locale));
				return "error";
			}

			// begin the transaction
			beganTx = TransactionUtil.begin(7200);
			String controlDirective = null;
			List<String> supplierIds = null;
			if (UtilValidate.isNotEmpty(supplierIdsStr)) supplierIds = Arrays.asList(supplierIdsStr);

			Map<String, Object> contextMap = FastMap.newInstance();
			contextMap.put("productPromoId", productPromoId);
			contextMap.put("promoName", promoName);
			contextMap.put("promoText", promoText);
			contextMap.put("fromDate", fromDate);
			contextMap.put("thruDate", thruDate);
			contextMap.put("showToCustomer", showToCustomer);
			contextMap.put("requireCode", requireCode);
			contextMap.put("useLimitPerOrder", useLimitPerOrder);
			contextMap.put("useLimitPerCustomer", useLimitPerCustomer);
			contextMap.put("useLimitPerPromotion", useLimitPerPromotion);
			contextMap.put("userLogin", userLogin);
			contextMap.put("locale", locale);
			Map<String, Object> result0 = dispatcher.runSync("updateProductPromo", contextMap);
			// no values for price and paramMap (a context for adding attributes)
			controlDirective = SalesUtil.processResult(result0, request);
			if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
				try {
					TransactionUtil.rollback(beganTx, "Failure in processing Create product promo callback", null);
				} catch (Exception e1) {
					Debug.logError(e1, module);
				}
				return "error";
			}

			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
			// update list supplier promo appl
			List<GenericValue> listSupplierPromoAppl = delegator.findByAnd("SupplierPromoAppl",
					UtilMisc.toMap("productPromoId", productPromoId), null, false);
			if (listSupplierPromoAppl != null) {
				List<String> supplierIdAppls = EntityUtil.getFieldListFromEntityList(listSupplierPromoAppl, "partyId",
						true);
				for (GenericValue supPromoAppl : listSupplierPromoAppl) {
					if (supplierIds != null && supplierIds.contains(supPromoAppl.getString("partyId"))) {
						if (fromDate.equals(supPromoAppl.getTimestamp("fromDate"))) {
							// no action
						} else {
							// thruDate this supPromoAppl record + create new record with fromDate
							supPromoAppl.put("thruDate", nowTimestamp);
							tobeStored.add(supPromoAppl);
							GenericValue newSupplierPromoAppl = delegator.makeValue("SupplierPromoAppl");
							newSupplierPromoAppl.put("partyId", supPromoAppl.get("partyId"));
							newSupplierPromoAppl.put("productPromoId", supPromoAppl.get("productPromoId"));
							newSupplierPromoAppl.put("fromDate", fromDate);
							tobeStored.add(newSupplierPromoAppl);
						}
					} else {
						// this record was deleted
						supPromoAppl.put("thruDate", nowTimestamp);
						tobeStored.add(supPromoAppl);
					}
				}
				if (supplierIds != null) {
					for (String supplierId : supplierIds) {
						if (supplierIdAppls.contains(supplierId)) {
							// no action
						} else {
							// create new
							GenericValue newSupplierPromoAppl = delegator.makeValue("SupplierPromoAppl");
							newSupplierPromoAppl.put("partyId", supplierId);
							newSupplierPromoAppl.put("productPromoId", productPromoId);
							newSupplierPromoAppl.put("fromDate", fromDate);
							tobeStored.add(newSupplierPromoAppl);
						}
					}
				}
			}

			delegator.storeAll(tobeStored);

			List<GenericValue> listProductPromoProduct = delegator.findByAnd("ProductPromoProduct", UtilMisc.toMap("productPromoId", productPromoId), null, false);
			List<GenericValue> listProductPromoCategory = delegator.findByAnd("ProductPromoCategory", UtilMisc.toMap("productPromoId", productPromoId), null, false);

			// Get the parameters as a MAP, remove the productId and quantity params.
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);

			// The number of multi form rows is retrieved
			int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
			if (rowCount < 1) {
				Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			} else {
				for (int i = 0; i < rowCount; i++) {
					// process list rule (condition, action)
					String ruleName = null;
					String ruleText = null;
					String productPromoRuleId = "";
					boolean isRuleExisted = false;
					String isRemoveRule = "N";

					controlDirective = null; // re-initialize each time
					String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i; // current suffix after each field id

					// get the ruleName
					if (paramMap.containsKey("ruleName" + thisSuffix)) {
						ruleName = (String) paramMap.remove("ruleName" + thisSuffix);
					}
					if (paramMap.containsKey("ruleText" + thisSuffix)) {
						ruleText = (String) paramMap.remove("ruleText" + thisSuffix);
					}
					// get the ruleId
					if (paramMap.containsKey("productPromoRuleId" + thisSuffix)) {
						productPromoRuleId = (String) paramMap.remove("productPromoRuleId" + thisSuffix);
					}
					if (paramMap.containsKey("isRemoveRule" + thisSuffix)) {
						isRemoveRule = (String) paramMap.remove("isRemoveRule" + thisSuffix);
					}

					GenericValue productPromoRule = delegator.findOne("ProductPromoRule", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId), false);
					if (productPromoRule != null) {
						isRuleExisted = true;
					}

					if ("Y".equals(isRemoveRule)) {
						if (isRuleExisted) {
							// delete condition
							Map<String, Object> result2 = dispatcher.runSync("deleteProductPromoRule", UtilMisc.<String, Object> toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "userLogin", userLogin, "locale", locale));
							// no values for price and paramMap (a context for adding attributes)
							controlDirective = SalesUtil.processResult(result2, request);
							if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
								try {
									TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
								} catch (Exception e1) {
									Debug.logError(e1, module);
								}
								return "error";
							}
							continue;
						} else {
							continue;
						}
					}

					if (UtilValidate.isEmpty(ruleName)) {
						continue;
					}

					Map<String, Object> result1 = null;
					if (isRuleExisted) {
						result1 = dispatcher.runSync("updateProductPromoRule",
								UtilMisc.<String, Object> toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, 
										"ruleName", ruleName, "ruleText", ruleText, "userLogin", userLogin, "locale", locale));
					} else {
						result1 = dispatcher.runSync("createProductPromoRuleCustom",
								UtilMisc.<String, Object> toMap("productPromoId", productPromoId, "ruleName", ruleName, "ruleText", ruleText, "userLogin", userLogin, "locale", locale));
						if (result1 != null) productPromoRuleId = (String) result1.get("productPromoRuleId");
					}
					// no values for price and paramMap (a context for adding attributes)
					controlDirective = SalesUtil.processResult(result1, request);
					if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
						try {
							TransactionUtil.rollback(beganTx, "Failure in processing Create product promo rule callback", null);
						} catch (Exception e1) {
							Debug.logError(e1, module);
						}
						return "error";
					}

					// The number of multi form rows is retrieved: Condition
					int rowCountSeqCond = SalesUtil.getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_COND);
					if (rowCountSeqCond < 1) {
						Debug.logWarning("No rows seq to process, as rowCountSeqCondition = " + rowCountSeqCond, module);
					} else {
						for (int j = 0; j < rowCountSeqCond; j++) {
							String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_COND + j + UtilHttp.MULTI_ROW_DELIMITER + i; // current suffix after each field id
							String productPromoCondSeqId = null;
							List<String> productIdListCond = FastList.newInstance();
							String productPromoApplEnumId = null;
							String includeSubCategories = null;
							List<String> productCatIdListCond = FastList.newInstance();
							String inputParamEnumId = null;
							String operatorEnumId = null;
							String condValue = null;
							String usePriceWithTax = null;
							String isRemoveCond = "N";

							if (paramMap.containsKey("isRemoveCond" + thisSuffixSeq)) {
								isRemoveCond = (String) paramMap.remove("isRemoveCond" + thisSuffixSeq);
							}
							if (paramMap.containsKey("productPromoCondSeqId" + thisSuffixSeq)) {
								productPromoCondSeqId = (String) paramMap.remove("productPromoCondSeqId" + thisSuffixSeq);
							}

							boolean isCondExisted = false;
							GenericValue productPromoCond = delegator.findOne("ProductPromoCond", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId), false);
							if (productPromoCond != null) isCondExisted = true;

							if ("Y".equals(isRemoveCond)) {
								if (isRuleExisted && isCondExisted) {
									// delete condition
									Map<String, Object> result2 = dispatcher.runSync("deleteProductPromoCond", UtilMisc.<String, Object> toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId, "userLogin", userLogin, "locale", locale));
									// no values for price and paramMap (a context for adding attributes)
									controlDirective = SalesUtil.processResult(result2, request);
									if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
										try {
											TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
										} catch (Exception e1) {
											Debug.logError(e1, module);
										}
										return "error";
									}
									continue;
								} else {
									continue;
								}
							}

							if (paramMap.containsKey("productIdListCond" + thisSuffixSeq)) {
								Object productIdListCondObj = (Object) paramMap
										.remove("productIdListCond" + thisSuffixSeq);
								if (productIdListCondObj instanceof String) {
									productIdListCond.add(productIdListCondObj.toString());
								} else if (productIdListCondObj instanceof List) {
									productIdListCond = (List<String>) productIdListCondObj;
								}
							}

							if (paramMap.containsKey("productPromoApplEnumId" + thisSuffixSeq)) {
								productPromoApplEnumId = (String) paramMap.remove("productPromoApplEnumId" + thisSuffixSeq);
							}
							if (paramMap.containsKey("includeSubCategories" + thisSuffixSeq)) {
								includeSubCategories = (String) paramMap.remove("includeSubCategories" + thisSuffixSeq);
							}
							if (paramMap.containsKey("productCatIdListCond" + thisSuffixSeq)) {
								Object productCatIdListCondObj = (Object) paramMap.remove("productCatIdListCond" + thisSuffixSeq);
								if (productCatIdListCondObj instanceof String) {
									productCatIdListCond.add(productCatIdListCondObj.toString());
								} else if (productCatIdListCondObj instanceof List) {
									productCatIdListCond = (List<String>) productCatIdListCondObj;
								}
							}

							if (paramMap.containsKey("inputParamEnumId" + thisSuffixSeq)) {
								inputParamEnumId = (String) paramMap.remove("inputParamEnumId" + thisSuffixSeq);
							}
							if (paramMap.containsKey("operatorEnumId" + thisSuffixSeq)) {
								operatorEnumId = (String) paramMap.remove("operatorEnumId" + thisSuffixSeq);
							}
							if (paramMap.containsKey("condValue" + thisSuffixSeq)) {
								condValue = (String) paramMap.remove("condValue" + thisSuffixSeq);
							}
							if (paramMap.containsKey("usePriceWithTax" + thisSuffixSeq)) {
								usePriceWithTax = (String) paramMap.remove("usePriceWithTax" + thisSuffixSeq);
							}

							if (isRuleExisted && isCondExisted) {
								if (UtilValidate.isNotEmpty(condValue)) {
									Map<String, Object> result2 = dispatcher.runSync("updateProductPromoCond",
											UtilMisc.<String, Object> toMap("productPromoId", productPromoId,
													"productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId, 
													"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
													"condValue", condValue, "userLogin", userLogin, "locale", locale, 
													"usePriceWithTax", usePriceWithTax));
									// "includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond,
									// "productIdListCond", productIdListCond, "productPromoApplEnumId", productPromoApplEnumId,
									// no values for price and paramMap (a context for adding attributes)
									controlDirective = SalesUtil.processResult(result2, request);
									if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
										try {
											TransactionUtil.rollback(beganTx, "Failure in processing delete product promo condition callback", null);
										} catch (Exception e1) {
											Debug.logError(e1, module);
										}
										return "error";
									}

									List<GenericValue> tobeStoredCond = new LinkedList<GenericValue>();
									List<GenericValue> tobeDeletedCond = new LinkedList<GenericValue>();
									List<GenericValue> listProductPromoProductCond = EntityUtil.filterByAnd(listProductPromoProduct,
											UtilMisc.toMap("productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId));
									if (listProductPromoProductCond != null) {
										List<String> productIdAppls = EntityUtil.getFieldListFromEntityList(listProductPromoProductCond, "productId", true);
										for (GenericValue productAppl : listProductPromoProductCond) {
											if (productIdListCond.contains(productAppl.getString("productId"))) {
												// no action
											} else {
												// this record was deleted
												tobeDeletedCond.add(productAppl);
											}
										}
										for (String productId : productIdListCond) {
											if (productIdAppls.contains(productId)) {
												// no action
											} else {
												// create new
												GenericValue newProductPromoProduct = delegator.makeValue("ProductPromoProduct");
												newProductPromoProduct.put("productPromoId", productPromoId);
												newProductPromoProduct.put("productPromoRuleId", productPromoRuleId);
												newProductPromoProduct.put("productPromoActionSeqId", "_NA_");
												newProductPromoProduct.put("productPromoCondSeqId", productPromoCondSeqId);
												newProductPromoProduct.put("productId", productId);
												newProductPromoProduct.put("productPromoApplEnumId", productPromoApplEnumId);
												tobeStoredCond.add(newProductPromoProduct);
											}
										}
									}
									List<GenericValue> listProductPromoCategoryCond = EntityUtil.filterByAnd(listProductPromoCategory,
											UtilMisc.toMap("productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId));
									if (listProductPromoCategoryCond != null) {
										List<String> categoryIdAppls = EntityUtil.getFieldListFromEntityList(listProductPromoCategoryCond, "productCategoryId", true);
										for (GenericValue categoryAppl : listProductPromoCategoryCond) {
											if (productCatIdListCond.contains(categoryAppl.getString("productCategoryId"))) {
												// no action
											} else {
												// this record was deleted
												tobeDeletedCond.add(categoryAppl);
											}
										}
										for (String productCategoryId : productCatIdListCond) {
											if (categoryIdAppls.contains(productCategoryId)) {
												// no action
											} else {
												// create new
												GenericValue newProductPromoCategory = delegator.makeValue("ProductPromoCategory");
												newProductPromoCategory.put("productPromoId", productPromoId);
												newProductPromoCategory.put("productPromoRuleId", productPromoRuleId);
												newProductPromoCategory.put("productPromoActionSeqId", "_NA_");
												newProductPromoCategory.put("productPromoCondSeqId", productPromoCondSeqId);
												newProductPromoCategory.put("productCategoryId", productCategoryId);
												newProductPromoCategory.put("andGroupId", "_NA_");
												newProductPromoCategory.put("productPromoApplEnumId", productPromoApplEnumId);
												newProductPromoCategory.put("includeSubCategories", includeSubCategories);
												tobeStoredCond.add(newProductPromoCategory);
											}
										}
									}
									delegator.storeAll(tobeStoredCond);
									delegator.removeAll(tobeDeletedCond);
								} else {
									errMsgList.add(UtilProperties.getMessage(resource_error, "BSConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoCondSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
								}
							} else {
								if (UtilValidate.isNotEmpty(condValue)) {
									Map<String, Object> result2 = dispatcher.runSync("createProductPromoCondCustom",
											UtilMisc.<String, Object> toMap("productPromoId", productPromoId,
													"productPromoRuleId", productPromoRuleId, 
													"productIdListCond", productIdListCond, "productPromoApplEnumId", productPromoApplEnumId,
													"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
													"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
													"condValue", condValue, "userLogin", userLogin, "locale", locale,
													"usePriceWithTax", usePriceWithTax));
									// no values for price and paramMap (a context for adding attributes)
									controlDirective = SalesUtil.processResult(result2, request);
									if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
										try {
											TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
										} catch (Exception e1) {
											Debug.logError(e1, module);
										}
										return "error";
									}
								} else {
									errMsgList.add(UtilProperties.getMessage(resource_error, "BSConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoCondSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
								}
							}
						}
					}

					// The number of multi form rows is retrieved: Action
					int rowCountSeqAction = SalesUtil.getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_ACT);
					if (rowCountSeqAction < 1) {
						Debug.logWarning("No rows seq to process, as rowCountSeqAction = " + rowCountSeqAction, module);
					} else {
						for (int j = 0; j < rowCountSeqAction; j++) {
							String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_ACT + j + UtilHttp.MULTI_ROW_DELIMITER + i; // current suffix after each field id
							String productPromoActionSeqId = null;
							String orderAdjustmentTypeId = null;
							List<String> productIdListAction = FastList.newInstance();
							String productPromoApplEnumIdAction = null;
							String includeSubCategoriesAction = null;
							List<String> productCatIdListAction = FastList.newInstance();
							String productPromoActionEnumId = null;
							BigDecimal quantity = null;
							String quantityStr = null;
							BigDecimal amount = null;
							String amountStr = null;
							String isRemoveAction = "N";
							String productPromoActionOperEnumId = null;
							String isCheckInv = "N";

							if (paramMap.containsKey("isRemoveAction" + thisSuffixSeq)) {
								isRemoveAction = (String) paramMap.remove("isRemoveAction" + thisSuffixSeq);
							}
							if (paramMap.containsKey("productPromoActionSeqId" + thisSuffixSeq)) {
								productPromoActionSeqId = (String) paramMap.remove("productPromoActionSeqId" + thisSuffixSeq);
							}

							boolean isActionExisted = false;
							GenericValue productPromoAction = delegator.findOne("ProductPromoAction", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId), false);
							if (productPromoAction != null)
								isActionExisted = true;

							if ("Y".equals(isRemoveAction)) {
								if (isRuleExisted && isActionExisted) {
									// delete condition
									Map<String, Object> result2 = dispatcher.runSync("deleteProductPromoAction", UtilMisc.<String, Object> toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId, "userLogin", userLogin, "locale", locale));
									// no values for price and paramMap (a context for adding attributes)
									controlDirective = SalesUtil.processResult(result2, request);
									if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
										try {
											TransactionUtil.rollback(beganTx, "Failure in processing delete product promo action callback", null);
										} catch (Exception e1) {
											Debug.logError(e1, module);
										}
										return "error";
									}
									continue;
								} else {
									continue;
								}
							}

							if (paramMap.containsKey("orderAdjustmentTypeId" + thisSuffixSeq)) {
								orderAdjustmentTypeId = (String) paramMap.remove("orderAdjustmentTypeId" + thisSuffixSeq);
							}

							if (paramMap.containsKey("productIdListAction" + thisSuffixSeq)) {
								Object productIdListActionObj = (Object) paramMap.remove("productIdListAction" + thisSuffixSeq);
								if (productIdListActionObj instanceof String) {
									productIdListAction.add(productIdListActionObj.toString());
								} else if (productIdListActionObj instanceof List) {
									productIdListAction = (List<String>) productIdListActionObj;
								}
							}

							if (paramMap.containsKey("productCatIdListAction" + thisSuffixSeq)) {
								Object productCatIdListActionObj = (Object) paramMap.remove("productCatIdListAction" + thisSuffixSeq);
								if (productCatIdListActionObj instanceof String) {
									productCatIdListAction.add(productCatIdListActionObj.toString());
								} else if (productCatIdListActionObj instanceof List) {
									productCatIdListAction = (List<String>) productCatIdListActionObj;
								}
							}

							if (paramMap.containsKey("productPromoApplEnumIdAction" + thisSuffixSeq)) {
								productPromoApplEnumIdAction = (String) paramMap.remove("productPromoApplEnumIdAction" + thisSuffixSeq);
							}
							if (paramMap.containsKey("includeSubCategoriesAction" + thisSuffixSeq)) {
								includeSubCategoriesAction = (String) paramMap.remove("includeSubCategoriesAction" + thisSuffixSeq);
							}
							if (paramMap.containsKey("productPromoActionEnumId" + thisSuffixSeq)) {
								productPromoActionEnumId = (String) paramMap.remove("productPromoActionEnumId" + thisSuffixSeq);
							}
							if (paramMap.containsKey("quantity" + thisSuffixSeq)) {
								quantityStr = (String) paramMap.remove("quantity" + thisSuffixSeq);
							}
							if (UtilValidate.isNotEmpty(quantityStr)) {
								// parse the quantity
								try {
									quantity = new BigDecimal(quantityStr);
								} catch (Exception e) {
									Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
									quantity = BigDecimal.ZERO;
								}
							}

							// get the selected amount
							if (paramMap.containsKey("amount" + thisSuffixSeq)) {
								amountStr = (String) paramMap.remove("amount" + thisSuffixSeq);
							}

							// parse the amount
							if (UtilValidate.isNotEmpty(amountStr)) {
								try {
									amount = new BigDecimal(amountStr);
								} catch (Exception e) {
									Debug.logWarning(e, "Problem parsing amount string: " + amountStr, module);
									amount = null;
								}
							}
							if (paramMap.containsKey("productPromoActionOperEnumId" + thisSuffixSeq)) {
								productPromoActionOperEnumId = (String) paramMap.remove("productPromoActionOperEnumId" + thisSuffixSeq);
							}
							/*if (paramMap.containsKey("isCheckInv" + thisSuffixSeq)) {
								isCheckInv = (String) paramMap.remove("isCheckInv" + thisSuffixSeq);
							}*/
							// if (productPromoActionOperEnumId == null)
							// productPromoActionOperEnumId = "PPAOP_AND";
							if (isRuleExisted && isActionExisted) {
								if (quantity != null || amount != null) {
									// Debug.logInfo("Attempting to add to cart
									// with productId = ", module);
									Map<String, Object> result3 = dispatcher.runSync("updateProductPromoAction",
											UtilMisc.<String, Object> toMap("productPromoId", productPromoId,
													"productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId, 
													"orderAdjustmentTypeId", orderAdjustmentTypeId, 
													"productPromoActionEnumId", productPromoActionEnumId, 
													"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale, 
													"operatorEnumId", productPromoActionOperEnumId, "isCheckInv", isCheckInv));
									// "includeSubCategories", includeSubCategoriesAction, 
									// "productCatIdListAction", productCatIdListAction,
									// "productIdListAction", productIdListAction, "productPromoApplEnumId", productPromoApplEnumIdAction,
									// no values for price and paramMap (a context for adding attributes)
									controlDirective = SalesUtil.processResult(result3, request);
									if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
										try {
											TransactionUtil.rollback(beganTx, "Failure in processing Create product promo action callback", null);
										} catch (Exception e1) {
											Debug.logError(e1, module);
										}
										return "error";
									}
								} else {
									errMsgList.add(UtilProperties.getMessage(resource_error, "BSApplyValueOfActionInTheRuleMustBeNotEmpty", UtilMisc.toMap("productPromoActionSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
								}

								List<GenericValue> tobeStoredAction = new LinkedList<GenericValue>();
								List<GenericValue> tobeDeletedAction = new LinkedList<GenericValue>();
								List<GenericValue> listProductPromoProductCond = EntityUtil.filterByAnd(listProductPromoProduct, UtilMisc.toMap("productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId));
								if (listProductPromoProductCond != null) {
									List<String> productIdAppls = EntityUtil.getFieldListFromEntityList(listProductPromoProductCond, "productId", true);
									for (GenericValue productAppl : listProductPromoProductCond) {
										if (productIdListAction.contains(productAppl.getString("productId"))) {
											// no action
										} else {
											// this record was deleted
											tobeDeletedAction.add(productAppl);
										}
									}
									for (String productId : productIdListAction) {
										if (productIdAppls.contains(productId)) {
											// no action
										} else {
											// create new
											GenericValue newProductPromoProduct = delegator.makeValue("ProductPromoProduct");
											newProductPromoProduct.put("productPromoId", productPromoId);
											newProductPromoProduct.put("productPromoRuleId", productPromoRuleId);
											newProductPromoProduct.put("productPromoActionSeqId", productPromoActionSeqId);
											newProductPromoProduct.put("productPromoCondSeqId", "_NA_");
											newProductPromoProduct.put("productId", productId);
											newProductPromoProduct.put("productPromoApplEnumId", productPromoApplEnumIdAction);
											tobeStoredAction.add(newProductPromoProduct);
										}
									}
								}
								List<GenericValue> listProductPromoCategoryCond = EntityUtil.filterByAnd(listProductPromoCategory, UtilMisc.toMap("productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId));
								if (listProductPromoCategoryCond != null) {
									List<String> categoryIdAppls = EntityUtil.getFieldListFromEntityList( listProductPromoCategoryCond, "productCategoryId", true);
									for (GenericValue categoryAppl : listProductPromoCategoryCond) {
										if (productCatIdListAction.contains(categoryAppl.getString("productCategoryId"))) {
											// no action
										} else {
											// this record was deleted
											tobeDeletedAction.add(categoryAppl);
										}
									}
									for (String productCategoryId : productCatIdListAction) {
										if (categoryIdAppls.contains(productCategoryId)) {
											// no action
										} else {
											// create new
											GenericValue newProductPromoCategory = delegator.makeValue("ProductPromoCategory");
											newProductPromoCategory.put("productPromoId", productPromoId);
											newProductPromoCategory.put("productPromoRuleId", productPromoRuleId);
											newProductPromoCategory.put("productPromoActionSeqId", productPromoActionSeqId);
											newProductPromoCategory.put("productPromoCondSeqId", "_NA_");
											newProductPromoCategory.put("productCategoryId", productCategoryId);
											newProductPromoCategory.put("andGroupId", "_NA_");
											newProductPromoCategory.put("productPromoApplEnumId", productPromoApplEnumIdAction);
											newProductPromoCategory.put("includeSubCategories", includeSubCategoriesAction);
											tobeStoredAction.add(newProductPromoCategory);
										}
									}
								}
								delegator.storeAll(tobeStoredAction);
								delegator.removeAll(tobeDeletedAction);
							} else {
								if (quantity != null || amount != null) {
									// Debug.logInfo("Attempting to add to cart with productId = ", module);
									Map<String, Object> result3 = dispatcher.runSync("createProductPromoActionCustom",
											UtilMisc.<String, Object> toMap("productPromoId", productPromoId,
													"productPromoRuleId", productPromoRuleId, "productIdListAction", productIdListAction, 
													"orderAdjustmentTypeId", orderAdjustmentTypeId,
													"productPromoApplEnumId", productPromoApplEnumIdAction, "includeSubCategories", includeSubCategoriesAction,
													"productCatIdListAction", productCatIdListAction, "productPromoActionEnumId", productPromoActionEnumId, 
													"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale, 
													"operatorEnumId", productPromoActionOperEnumId, "isCheckInv", isCheckInv));
									// no values for price and paramMap (a context for adding attributes)
									controlDirective = SalesUtil.processResult(result3, request);
									if (controlDirective.equals("error")) { // if the add to cart failed, then get out of this loop right away
										try {
											TransactionUtil.rollback(beganTx, "Failure in processing Create product promo action callback", null);
										} catch (Exception e1) {
											Debug.logError(e1, module);
										}
										return "error";
									}
								} else {
									errMsgList.add(UtilProperties.getMessage(resource_error, "BSApplyValueOfActionInTheRuleMustBeNotEmpty", UtilMisc.toMap("productPromoActionSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			try {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
			} catch (Exception e1) {
				Debug.logError(e1, module);
			}
			errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
			return "error";
		} catch (Throwable t) {
			Debug.logError(t, module);
			request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
			try {
				TransactionUtil.rollback(beganTx, t.getMessage(), t);
			} catch (Exception e2) {
				Debug.logError(e2, module);
			}
			errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
			return "error";
		} finally {
			// commit the transaction
			try {
				TransactionUtil.commit(beganTx);
			} catch (Exception e) {
				Debug.logError(e, module);
			}
		}
		if (UtilValidate.isNotEmpty(errMsgList)) {
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
			return "error";
		}
		request.setAttribute("productPromoId", productPromoId);
		return "success";
	}
}
