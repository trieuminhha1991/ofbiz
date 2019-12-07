package com.olbius.basepos.transaction;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.basepos.cart.PosCheckoutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartPaymentInfo;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.returnOrder.ReturnEvents;

public class WebPosTransaction {
	public static final String resource_error = "BasePosErrorUiLabels";
	public static final String module = WebPosTransaction.class.getName();
	public static final int NO_PAYMENT = 0;
	public static final int INTERNAL_PAYMENT = 1;
	public static final int EXTERNAL_PAYMENT = 2;

	private PosCheckoutHelper ch = null;
	private GenericValue txLog = null;
	private String transactionId = null;
	private String orderId = null;

	private String returnId = null;
	private String partyId = null;
	private boolean isOpen = false;
	private int drawerIdx = 0;
	private GenericValue shipAddress = null;
	private WebPosSession webPosSession = null;
	private int cartDiscount = -1;
	private BigDecimal percentAmount = BigDecimal.ZERO;
	private boolean discountPercent = false;
	private boolean flagDiscount; // if flageDiscount equal true, it don't
									// accept discount shopping cart
	private Map<String, Integer> itemDiscounts = FastMap.newInstance();

	public WebPosTransaction(WebPosSession session) {
		String transactionId = null;
		this.webPosSession = session;
		this.partyId = "_NA_";
		Delegator delegator = session.getDelegator();
		ShoppingCart cart = session.getCart();
		this.ch = new PosCheckoutHelper(session.getDispatcher(), delegator, cart);
		cart.setChannelType("POS_SALES_CHANNEL");
		cart.setFacilityId(session.getFacilityId());
		cart.setTerminalId(session.getId());

		this.flagDiscount = false;
		/*
		 * if (session.getUserLogin() != null) {
		 * cart.addAdditionalPartyRole(session.getUserLogin().getString(
		 * "partyId"), "SALES_REP"); }
		 */

		// setup the TX log
		try {
			transactionId = getNextOrderId();
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		if (UtilValidate.isEmpty(transactionId)) {
			transactionId = delegator.getNextSeqId("OrderHeader"); // default
																	// transactionId
																	// =
																	// orderHeaderId
		}

		this.transactionId = transactionId;

		txLog = delegator.makeValue("PosTerminalLog");
		txLog.set("posTerminalLogId", this.transactionId);
		txLog.set("posTerminalId", session.getId());
		txLog.set("transactionId", transactionId);
		txLog.set("userLoginId", session.getUserLoginId());
		txLog.set("statusId", "POSTX_ACTIVE");

		// hoandv add new filed posTerminalState
		String terminalSateId = this.getPosTerminalStateId();
		if (UtilValidate.isNotEmpty(terminalSateId)) {
			txLog.set("posTerminalStateId", terminalSateId);
		}
		txLog.set("logStartDateTime", UtilDateTime.nowTimestamp());
		try {
			txLog.create();
			cart.setTransactionId(transactionId);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Unable to create TX log - not fatal", module);
		}

		Debug.logInfo("Created WebPosTransaction [" + this.transactionId + "]", module);
	}

	public String getNextOrderId() throws GenericEntityException {
		String orderId = null;
		ShoppingCart cart = webPosSession.getCart();
		Delegator delegator = webPosSession.getDelegator();
		LocalDispatcher dispatcher = webPosSession.getDispatcher();
		GenericValue userLogin = webPosSession.getUserLogin();
		String productStoreId = cart.getProductStoreId();
		String orderTypeId = cart.getOrderType();
		if (UtilValidate.isNotEmpty(productStoreId)) {
			GenericValue productStore = delegator.findOne("ProductStore",
					UtilMisc.toMap("productStoreId", productStoreId), false);
			String orgPartyId = null;
			if (productStore != null) {
				orgPartyId = productStore.getString("payToPartyId");
				Map<String, Object> getNextOrderIdContext = FastMap.newInstance();
				getNextOrderIdContext.put("partyId", orgPartyId);
				getNextOrderIdContext.put("userLogin", userLogin);
				if ((orderTypeId.equals("SALES_ORDER")) || (productStoreId != null)) {
					getNextOrderIdContext.put("productStoreId", productStoreId);
				}
				if (UtilValidate.isEmpty(orderId)) {
					try {
						Map<String, Object> getNextOrderIdResult = dispatcher.runSync("getNextOrderId",
								getNextOrderIdContext);
						if (ServiceUtil.isError(getNextOrderIdResult)) {

						}
						orderId = (String) getNextOrderIdResult.get("orderId");
					} catch (GenericServiceException e) {
						return orderId;
					}
				}
			}

		}
		return orderId;

	}

	public void createPosTerminalLog(Delegator delegator, WebPosSession session) {
		// setup the TX log
		/* this.transactionId = delegator.getNextSeqId("OrderHeader"); */// default
																			// transactionId
																			// =
																			// orderHeaderId
		String transactionId = null;
		try {
			transactionId = getNextOrderId();
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		if (UtilValidate.isEmpty(transactionId)) {
			transactionId = delegator.getNextSeqId("OrderHeader");
		}
		this.transactionId = transactionId;

		txLog = delegator.makeValue("PosTerminalLog");
		txLog.set("posTerminalLogId", transactionId);
		txLog.set("posTerminalId", session.getId());
		txLog.set("transactionId", transactionId);
		txLog.set("userLoginId", session.getUserLoginId());
		txLog.set("statusId", "POSTX_ACTIVE");

		// hoandv add new filed posTerminalState
		String terminalSateId = this.getPosTerminalStateId();
		if (UtilValidate.isNotEmpty(terminalSateId)) {
			txLog.set("posTerminalStateId", terminalSateId);
		}
		txLog.set("logStartDateTime", UtilDateTime.nowTimestamp());
		try {
			txLog.create();
		} catch (GenericEntityException e) {
			Debug.logError(e, "Unable to create TX log - not fatal", module);
		}

		Debug.logInfo("Created WebPosTransaction [" + this.transactionId + "]", module);
	}

	public String getUserLoginId() {
		return webPosSession.getUserLoginId();
	}

	public int getDrawerNumber() {
		return drawerIdx + 1;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public String getTerminalId() {
		return webPosSession.getId();
	}

	public String getFacilityId() {
		return webPosSession.getFacilityId();
	}

	public String getTerminalLogId() {
		return txLog.getString("posTerminalLogId");
	}

	public boolean isOpen() {
		this.isOpen = false;
		GenericValue terminalState = this.getTerminalState();
		if (terminalState != null) {
			if ((terminalState.getDate("closedDate")) == null) {
				this.isOpen = true;
			}
		}
		return this.isOpen;
	}

	public GenericValue getTerminalState() {
		Delegator delegator = webPosSession.getDelegator();
		List<GenericValue> states = null;
		try {
			String userLoginId = this.getUserLoginId();
			states = delegator.findList("PosTerminalState",
					EntityCondition.makeCondition(
							UtilMisc.toMap("posTerminalId", webPosSession.getId(), "openedByUserLoginId", userLoginId)),
					null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		states = EntityUtil.filterByDate(states, UtilDateTime.nowTimestamp(), "openedDate", "closedDate", true);
		return EntityUtil.getFirst(states);
	}

	public void closeTx() {
		if (UtilValidate.isNotEmpty(txLog)) {
			txLog.set("statusId", "POSTX_CLOSED");
			txLog.set("itemCount", new Long(getCart().size()));
			txLog.set("logEndDateTime", UtilDateTime.nowTimestamp());

			// hoandv add new field posTerminalStateId
			txLog.set("posTerminalStateId", getPosTerminalStateId());
			try {
				txLog.store();
			} catch (GenericEntityException e) {
				Debug.logError(e, "Unable to store TX log - not fatal", module);
			}
			getCart().clear();
			Debug.logInfo("Transaction closed", module);
		}
	}

	public void paidInOut(String type) {
		if (UtilValidate.isNotEmpty(txLog)) {
			txLog.set("statusId", "POSTX_PAID_" + type);
			txLog.set("logEndDateTime", UtilDateTime.nowTimestamp());

			// hoandv add new field posTerminalStateId
			txLog.set("posTerminalStateId", getPosTerminalStateId());
			try {
				txLog.store();
			} catch (GenericEntityException e) {
				Debug.logError(e, "Unable to store TX log - not fatal", module);
			}
			webPosSession.setCurrentTransaction(null);
			Debug.logInfo("Paid " + type, module);
		}
	}

	public void modifyPrice(int cartLineIdx, BigDecimal price) {
		ShoppingCartItem item = (ShoppingCartItem) getCart().findCartItem(cartLineIdx);
		if (UtilValidate.isNotEmpty(item)) {
			Debug.logInfo("Modify item price " + item.getProductId() + "/" + price, module);
			String quantityUomId = (String) item.getAttribute("quantityUomId");
			if (UtilValidate.isNotEmpty(quantityUomId)) {
				item.setAttribute("alternativeUnitPrice", price);
			}
			item.setBasePrice(price);
			item.setDisplayPrice(price);
		} else {
			Debug.logInfo("Item " + cartLineIdx + " not found", module);
		}
	}

	public void calcTax() {
		try {
			ch.calcAndAddTax(this.getStoreOrgAddress());
		} catch (GeneralException e) {
			Debug.logError(e, module);
		}
	}

	public BigDecimal processSale(HttpServletRequest request, HttpServletResponse response) throws GeneralException {
		BigDecimal grandTotal = this.getGrandTotal();
		BigDecimal paymentAmt = this.getPaymentTotal();
		HttpSession session = request.getSession();
		ShoppingCart cart = getCart();
		cart.setAllShipmentMethodTypeId("NO_SHIPPING");
		Locale locale = request.getLocale();
		Delegator delegator = cart.getDelegator();
		String productStoreId = cart.getProductStoreId();
		GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId),
				false);
		String company = productStore.getString("payToPartyId");

		cart.haveContainReturnItem();
		try {
			TransactionUtil.begin(7200);
			if (cart.isWholeCartIsReturn()) {
				cart.setCartContainedReturn(true);
				String returnWholeCart = ReturnEvents.returnDirectly(request, response);
				if (returnWholeCart.equals("success")) {
					String returnId = (String) session.getAttribute("returnId");
					txLog.set("statusId", "POSTX_RETURNED");
					txLog.set("returnId", returnId);
					txLog.set("logEndDateTime", UtilDateTime.nowTimestamp());
					// hoandv add new field posTerminalStateId
					txLog.set("posTerminalStateId", getPosTerminalStateId());
					try {
						txLog.store();
					} catch (GenericEntityException e) {
						Debug.logError(e, "Unable to store TX log - not fatal", module);
					}
				}
			} else {
				if (cart.isCartContainedReturn()) {
					cart.setCartContainedReturn(false);
					cart.setOrderId(this.transactionId);
					cart.setBillFromVendorPartyId(company);
					// cart.setBillToCustomerPartyId(partyId);
					createOrderAndProcessPayment(request, response);
					cart.setCartContainedReturn(true);
					// create a new posTemrinalLog
					createPosTerminalLog(delegator, webPosSession);
					String returnPartylyCart = ReturnEvents.returnDirectly(request, response);
					if (returnPartylyCart.equals("success")) {
						String returnId = (String) session.getAttribute("returnId");
						txLog.set("statusId", "POSTX_RETURNED");
						txLog.set("returnId", returnId);
						txLog.set("logEndDateTime", UtilDateTime.nowTimestamp());
						// hoandv add new field posTerminalStateId
						txLog.set("posTerminalStateId", getPosTerminalStateId());
						try {
							txLog.store();
						} catch (GenericEntityException e) {
							Debug.logError(e, "Unable to store TX log - not fatal", module);
						}
					}
				} else {
					cart.setOrderId(this.transactionId);
					cart.setBillFromVendorPartyId(company);
					// cart.setBillToCustomerPartyId(partyId);
					createOrderAndProcessPayment(request, response);
				}
			}

			TransactionUtil.commit();
		} catch (Exception e) {
			Debug.logError("Can not create order", module);
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return grandTotal;
		}
		BigDecimal change = grandTotal.subtract(paymentAmt);
		return change;
	}

	public void createOrderAndProcessPayment(HttpServletRequest request, HttpServletResponse response)
			throws GeneralException {
		/*
		 * GenericValue userLogin = null; userLogin =
		 * webPosSession.getDelegator().findOne("UserLogin",
		 * UtilMisc.toMap("userLoginId", "webposadmin"), false);
		 */
		GenericValue userLogin = webPosSession.getUserLogin();
		Debug.logInfo("Processing the payment(s)", module);
		Map<String, ? extends Object> orderRes = UtilGenerics.cast(ch.createOrder(userLogin));
		if (orderRes != null && ServiceUtil.isError(orderRes)) {
			throw new GeneralException(ServiceUtil.getErrorMessage(orderRes));
		} else if (orderRes != null) {
			this.orderId = (String) orderRes.get("orderId");
		}
		// process the payment(s)
		Debug.logInfo("Processing the payment(s)", module);
		Map<String, ? extends Object> payRes = null;
		try {
			payRes = UtilGenerics.cast(ch.processPayment(
					ProductStoreWorker.getProductStore(webPosSession.getProductStoreId(), webPosSession.getDelegator()),
					userLogin, true));
		} catch (GeneralException e) {
			Debug.logError(e, module);
			throw e;
		}

		if (payRes != null && ServiceUtil.isError(payRes)) {
			throw new GeneralException(ServiceUtil.getErrorMessage(payRes));
		} else {
			txLog.set("statusId", "POSTX_SOLD");
			txLog.set("orderId", orderId);
			txLog.set("itemCount", new Long(getCart().size()));
			txLog.set("logEndDateTime", UtilDateTime.nowTimestamp());
			// hoandv add new field posTerminalStateId
			txLog.set("posTerminalStateId", getPosTerminalStateId());
			try {
				txLog.store();
			} catch (GenericEntityException e) {
				Debug.logError(e, "Unable to store TX log - not fatal", module);
			}
		}
	}

	private synchronized GenericValue getStoreOrgAddress() {
		if (UtilValidate.isEmpty(this.shipAddress)) {
			// locate the store's physical address - use this for tax
			GenericValue facility = null;
			try {
				facility = webPosSession.getDelegator().findOne("Facility",
						UtilMisc.toMap("facilityId", webPosSession.getFacilityId()), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			if (UtilValidate.isEmpty(facility)) {
				return null;
			}

			List<GenericValue> fcp = null;
			try {
				fcp = facility.getRelated("FacilityContactMechPurpose",
						UtilMisc.toMap("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION"), null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			fcp = EntityUtil.filterByDate(fcp);
			GenericValue purp = EntityUtil.getFirst(fcp);
			if (UtilValidate.isNotEmpty(purp)) {
				try {
					this.shipAddress = webPosSession.getDelegator().findOne("PostalAddress",
							UtilMisc.toMap("contactMechId", purp.getString("contactMechId")), false);
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
				}
			}
		}
		return this.shipAddress;
	}

	public void clearPayments() {
		Debug.logInfo("all payments cleared from sale", module);
		getCart().clearPayments();
	}

	public void clearPayment(int index) {
		Debug.logInfo("removing payment " + index, module);
		getCart().clearPayment(index);
	}

	public void clearPayment(String id) {
		Debug.logInfo("removing payment " + id, module);
		getCart().clearPayment(id);
	}

	public CartPaymentInfo getPaymentInfo(int index) {
		return getCart().getPaymentInfo(index);
	}

	public String getPaymentMethodTypeId(int index) {
		return getCart().getPaymentInfo(index).paymentMethodTypeId;
	}

	public int checkPaymentMethodType(String paymentMethodTypeId) {
		Map<String, ? extends Object> fields = UtilMisc.toMap("paymentMethodTypeId", paymentMethodTypeId,
				"productStoreId", webPosSession.getProductStoreId());
		List<GenericValue> values = null;
		try {
			values = webPosSession.getDelegator().findList("ProductStorePaymentSetting",
					EntityCondition.makeCondition(fields), null, null, null, true);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}

		final String externalCode = "PRDS_PAY_EXTERNAL";
		if (UtilValidate.isEmpty(values)) {
			return NO_PAYMENT;
		} else {
			boolean isExternal = true;
			Iterator<GenericValue> i = values.iterator();
			while (i.hasNext() && isExternal) {
				GenericValue v = i.next();
				if (!externalCode.equals(v.getString("paymentServiceTypeEnumId"))) {
					isExternal = false;
				}
			}

			if (isExternal) {
				return EXTERNAL_PAYMENT;
			} else {
				return INTERNAL_PAYMENT;
			}
		}
	}

	public void updateTxLog() throws GenericEntityException {
		if (returnId != null) {
			txLog.set("statusId", "POSTX_RETURNED");
			txLog.set("returnId", returnId);
			txLog.set("logEndDateTime", UtilDateTime.nowTimestamp());
			// hoandv add new field posTerminalStateId
			txLog.set("posTerminalStateId", getPosTerminalStateId());
			txLog.store();
		}
		if (orderId != null) {
			txLog.set("statusId", "POSTX_SOLD");
			txLog.set("orderId", orderId);
			txLog.set("logEndDateTime", UtilDateTime.nowTimestamp());
			txLog.set("itemCount", new Long(getCart().size()));
			txLog.store();
		}
	}

	public static int getNoPaymentCode() {
		return NO_PAYMENT;
	}

	public static int getExternalPaymentCode() {
		return EXTERNAL_PAYMENT;
	}

	public static int getInternalPaymentCode() {
		return INTERNAL_PAYMENT;
	}

	public BigDecimal addPayment(String id, BigDecimal amount) {
		return this.addPayment(id, amount, null, null);
	}

	public BigDecimal addPayment(String id, BigDecimal amount, String refNum, String authCode) {
		Debug.logInfo("Added payment " + id + "/" + amount, module);
		if ("CASH".equals(id)) {
			// clear cash payments first; so there is only one
			getCart().clearPayment(id);
		}
		getCart().addPaymentAmount(id, amount, refNum, authCode, true, true, false);
		return this.getTotalDue();
	}

	public void processPayment(String id, BigDecimal amount) {
		Debug.logInfo("Added payment " + id + "/" + amount, module);
		if ("CASH".equals(id)) {
			// clear cash payments first; so there is only one
			getCart().clearPayment(id);
		}
		getCart().addPaymentAmount(id, amount, null, null, true, true, false);
	}

	public BigDecimal processAmount(BigDecimal amount) throws GeneralException {
		if (UtilValidate.isEmpty(amount)) {
			Debug.logInfo("Amount is empty; assumption is full amount : " + this.getTotalDue(), module);
			amount = this.getTotalDue();
			if (amount.compareTo(BigDecimal.ZERO) <= 0) {
				throw new GeneralException();
			}
		}
		return amount;
	}

	public synchronized void processNoPayment(String paymentMethodTypeId) {
		try {
			BigDecimal amount = processAmount(null);
			Debug.logInfo("Processing [" + paymentMethodTypeId + "] Amount : " + amount, module);
			// add the payment
			addPayment(paymentMethodTypeId, amount, null, null);
		} catch (GeneralException e) {
		}
	}

	public synchronized void processExternalPayment(String paymentMethodTypeId, BigDecimal amount, String refNum) {
		if (refNum == null) {
			return;
		}
		try {
			amount = processAmount(amount);
			Debug.logInfo("Processing [" + paymentMethodTypeId + "] Amount : " + amount, module);

			// add the payment
			addPayment(paymentMethodTypeId, amount, refNum, null);
		} catch (GeneralException e) {
			Debug.logError(e, module);
			return;
		}
	}

	public synchronized void processCreditCardOffline(String id, BigDecimal amount) {
		Debug.logInfo("Added payment " + id + "/" + amount, module);
		try {
			getCart().addPaymentAmount(id, amount, true);
		} catch (Exception e) {
			Debug.logError(e, module);
			return;
		}
	}

	public synchronized void processOrderReturned(String id, BigDecimal amount) {
		Debug.logInfo("Added payment " + id + "/" + amount, module);
		try {
			getCart().addPaymentAmount(id, amount, true);
		} catch (Exception e) {
			Debug.logError(e, module);
			return;
		}
	}

	public synchronized void processGiftCardPayment(String paymentMethodTypeId, BigDecimal amount, String refNum,
			String accountNumber) {
		if (refNum == null) {
			return;
		}
		try {
			amount = processAmount(amount);
			Debug.logInfo("Processing [" + paymentMethodTypeId + "] Amount : " + amount, module);

			// add the payment
			Map<String, Object> params = UtilMisc.toMap("giftCardNumber", accountNumber, "giftCardPin", refNum,
					"giftCardAmount", amount.toString(), "addGiftCard", Boolean.TRUE, "partyId", partyId);
			Map<String, Object> result = ch.checkGiftCard(params, null);
			getCart().addPaymentAmount(result.get("paymentMethodId").toString(), (BigDecimal) result.get("amount"),
					true);
		} catch (GeneralException e) {
			Debug.logError(e, module);
			return;
		}
	}

	public String makeCreditCardVo(String cardNumber, String expDate, String firstName, String lastName) {
		LocalDispatcher dispatcher = webPosSession.getDispatcher();
		String expMonth = expDate.substring(0, 2);
		String expYear = expDate.substring(2);
		// two digit year check -- may want to re-think this
		if (expYear.length() == 2) {
			expYear = "20" + expYear;
		}

		Map<String, Object> svcCtx = FastMap.newInstance();
		svcCtx.put("userLogin", webPosSession.getUserLogin());
		svcCtx.put("partyId", partyId);
		svcCtx.put("cardNumber", cardNumber);
		svcCtx.put("firstNameOnCard", firstName == null ? "" : firstName);
		svcCtx.put("lastNameOnCard", lastName == null ? "" : lastName);
		svcCtx.put("expMonth", expMonth);
		svcCtx.put("expYear", expYear);
		svcCtx.put("cardType", UtilValidate.getCardType(cardNumber));

		Map<String, Object> svcRes = null;
		try {
			svcRes = dispatcher.runSync("createCreditCard", svcCtx);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return null;
		}
		if (ServiceUtil.isError(svcRes)) {
			Debug.logError(ServiceUtil.getErrorMessage(svcRes) + " - " + svcRes, module);
			return null;
		} else {
			return (String) svcRes.get("paymentMethodId");
		}
	}

	public void setPaymentRefNum(int paymentIndex, String refNum, String authCode) {
		Debug.logInfo("setting payment index reference number " + paymentIndex + " / " + refNum + " / " + authCode,
				module);
		ShoppingCart.CartPaymentInfo inf = getCart().getPaymentInfo(paymentIndex);
		inf.refNum[0] = refNum;
		inf.refNum[1] = authCode;
	}

	/* CVV2 code should be entered when a card can't be swiped */
	public void setPaymentSecurityCode(String paymentId, String refNum, String securityCode) {
		Debug.logInfo("setting payment security code " + paymentId, module);
		int paymentIndex = getCart().getPaymentInfoIndex(paymentId, refNum);
		ShoppingCart.CartPaymentInfo inf = getCart().getPaymentInfo(paymentIndex);
		inf.securityCode = securityCode;
		inf.isSwiped = false;
	}

	/* Track2 data should be sent to processor when a card is swiped. */
	public void setPaymentTrack2(String paymentId, String refNum, String securityCode) {
		Debug.logInfo("setting payment security code " + paymentId, module);
		int paymentIndex = getCart().getPaymentInfoIndex(paymentId, refNum);
		ShoppingCart.CartPaymentInfo inf = getCart().getPaymentInfo(paymentIndex);
		inf.securityCode = securityCode;
		inf.isSwiped = true;
	}

	/* Postal code should be entered when a card can't be swiped */
	public void setPaymentPostalCode(String paymentId, String refNum, String postalCode) {
		Debug.logInfo("setting payment security code " + paymentId, module);
		int paymentIndex = getCart().getPaymentInfoIndex(paymentId, refNum);
		ShoppingCart.CartPaymentInfo inf = getCart().getPaymentInfo(paymentIndex);
		inf.postalCode = postalCode;
	}

	public BigDecimal getTaxTotal() {
		return getCart().getTotalSalesTax();
	}

	public BigDecimal getGrandTotal() {
		return getCart().getGrandTotal();
	}

	public int getNumberOfPayments() {
		return getCart().selectedPayments();
	}

	public BigDecimal getPaymentTotal() {
		return getCart().getPaymentTotal();
	}

	public BigDecimal getTotalQuantity() {
		return getCart().getTotalQuantity();
	}

	public BigDecimal getGrandTotalNotAdj() {
		return getCart().getGrandTotalNotAdjusment();
	}

	public BigDecimal getTotalDue() {
		BigDecimal grandTotal = this.getGrandTotal();
		BigDecimal paymentAmt = this.getPaymentTotal();
		return grandTotal.subtract(paymentAmt);
	}

	public void addDiscount(BigDecimal discount, boolean percent) {
		Delegator delegator = webPosSession.getDelegator();
		GenericValue adjustment = delegator.makeValue("OrderAdjustment");
		ShoppingCart cart = webPosSession.getCart();
		String orderAdjustmentTypeId = "DISCOUNT_ADJUSTMENT_NOTAX";
		if (UtilValidate.isNotEmpty(cart)) {
			String productStoreId = cart.getProductStoreId();
			GenericValue productStore = null;
			try {
				productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId),
						false);
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
			}
			if (UtilValidate.isNotEmpty(productStore)) {
				String showPricesWithVatTax = productStore.getString("showPricesWithVatTax");
				if (showPricesWithVatTax.equals("N")) {
					orderAdjustmentTypeId = "DISCOUNT_ADJUSTMENT";
				}
			}
		}
		adjustment.set("orderAdjustmentTypeId", orderAdjustmentTypeId);
		adjustment.set("amount", discount);
		BigDecimal temp = new BigDecimal("100");
		BigDecimal grandTotal = this.getGrandTotal();
		BigDecimal discounttAmount = discount.negate();
		BigDecimal percentAmount = BigDecimal.ZERO;
		if (discounttAmount.compareTo(BigDecimal.ZERO) != 0) {
			percentAmount = discounttAmount.divide(grandTotal, 4, BigDecimal.ROUND_HALF_UP);
		}
		this.percentAmount = percentAmount.multiply(temp);
		this.cartDiscount = getCart().addAdjustment(adjustment);
		this.discountPercent = percent;
		this.flagDiscount = true;
	}

	public void addDiscountItem(int cartLineIdx, BigDecimal discount, boolean percent) {
		Delegator delegator = webPosSession.getDelegator();
		ShoppingCart cart = webPosSession.getCart();
		String orderAdjustmentTypeId = "DISCOUNT_ADJUSTMENT_NOTAX";
		GenericValue adjustment = delegator.makeValue("OrderAdjustment");
		if (UtilValidate.isNotEmpty(cart)) {
			String productStoreId = cart.getProductStoreId();
			GenericValue productStore = null;
			try {
				productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId),
						false);
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
			}
			if (UtilValidate.isNotEmpty(productStore)) {
				String showPricesWithVatTax = productStore.getString("showPricesWithVatTax");
				if (showPricesWithVatTax.equals("N")) {
					orderAdjustmentTypeId = "DISCOUNT_ADJUSTMENT";
				}
			}
		}
		adjustment.set("orderAdjustmentTypeId", orderAdjustmentTypeId);
		if (percent) {
			adjustment.set("sourcePercentage", discount);
		} else {
			adjustment.set("amount", discount);
		}
		ShoppingCartItem cartItem = (ShoppingCartItem) getCart().findCartItem(cartLineIdx);
		String productId = cartItem.getProductId();
		if (UtilValidate.isNotEmpty(cartItem)) {
			Debug.logInfo("add item adjustment for product " + productId, module);
			List<GenericValue> adjustments = cartItem.getAdjustments();
			for (GenericValue gvAdjustment : adjustments) {
				cartItem.removeAdjustment(gvAdjustment);
			}
			cartItem.addAdjustment(adjustment);
		} else {
			if (cartDiscount > -1) {
				getCart().removeAdjustment(cartDiscount);
			}
			cartDiscount = getCart().addAdjustment(adjustment);
		}
	}

	public String addProductPromoCode(String code) {
		String result = getCart().addProductPromoCode(code, webPosSession.getDispatcher());
		calcTax();
		return result;
	}

	public ShoppingCart getCart() {
		return webPosSession.getCart();
	}

	public int getCartDiscount() {
		return cartDiscount;
	}

	public boolean isDiscountPercent() {
		return discountPercent;
	}

	public BigDecimal getPercentAmount() {
		return percentAmount;
	}

	public boolean isFlagDiscount() {
		return flagDiscount;
	}

	public void setFlagDiscount(boolean flagDiscount) {
		this.flagDiscount = flagDiscount;
	}

	public Map<String, Integer> getItemDiscounts() {
		return itemDiscounts;
	}

	public void setItemDiscounts(Map<String, Integer> itemDiscounts) {
		this.itemDiscounts = itemDiscounts;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	public String getPosTerminalStateId() {
		GenericValue terminalState = this.getTerminalState();
		if (UtilValidate.isNotEmpty(terminalState)) {
			String terminalStateId = terminalState.getString("posTerminalStateId");
			return terminalStateId;
		} else {
			return null;
		}
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
