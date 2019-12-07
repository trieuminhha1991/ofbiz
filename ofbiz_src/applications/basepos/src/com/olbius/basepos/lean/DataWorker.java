package com.olbius.basepos.lean;

import java.math.BigDecimal;

import org.ofbiz.entity.Delegator;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.transaction.WebPosTransaction;

public class DataWorker {
	public WebPosSession posSession;
	public ShoppingCart cart = null;
	public LocalDispatcher dispatcher;
	public Delegator delegator;
	public WebPosTransaction webPosTransaction;
	private String returnId;
	private String orderId;
	private BigDecimal returnGrandTotal;
	
	public DataWorker(WebPosSession posSession){
		this.posSession = posSession;
		this.cart = posSession.getCart();
		dispatcher = this.posSession.getDispatcher();
		delegator = dispatcher.getDelegator();
		this.webPosTransaction = posSession.getCurrentTransaction();
	}


	public String getReturnId() {
		return returnId;
	}


	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}


	public BigDecimal getReturnGrandTotal() {
		return returnGrandTotal;
	}


	public void setReturnGrandTotal(BigDecimal returnGrandTotal) {
		this.returnGrandTotal = returnGrandTotal;
	}


	public String getOrderId() {
		return orderId;
	}


	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
