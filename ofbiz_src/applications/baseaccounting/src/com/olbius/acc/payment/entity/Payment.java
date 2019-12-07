package com.olbius.acc.payment.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
	//variables
	private String paymentId;
	private String paymentCode;
	private String paymentTypeId;
	private String paymentMethodId;
	private String partyIdFrom;
	private String partyIdTo;
	private String partyCodeFrom;
	private String partyCodeTo;
	private String statusId;
	private BigDecimal amount;
	private String currencyUomId;
	private String comments;
	private Timestamp effectiveDate;
	private BigDecimal amountToApply;
	private String fullNameFrom;
	private String fullNameTo;
	
	//Getters And Setters
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public void setCurrencyUomId(String currencyUomId) {
		this.currencyUomId = currencyUomId;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public void setEffectiveDate(java.sql.Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public void setAmountToApply(BigDecimal amountToApply) {
		this.amountToApply = amountToApply;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public String getPaymentCode() {
		return paymentCode;
	}
	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	public String getPaymentTypeId() {
		return paymentTypeId;
	}
	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}
	public String getStatusId() {
		return statusId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public String getCurrencyUomId() {
		return currencyUomId;
	}
	public String getComments() {
		return comments;
	}
	public Timestamp getEffectiveDate() {
		return effectiveDate;
	}
	public BigDecimal getAmountToApply() {
		return amountToApply;
	}
	public String getPartyIdFrom() {
		return partyIdFrom;
	}
	public void setPartyIdFrom(String partyIdFrom) {
		this.partyIdFrom = partyIdFrom;
	}
	public String getPartyIdTo() {
		return partyIdTo;
	}
	public void setPartyIdTo(String partyIdTo) {
		this.partyIdTo = partyIdTo;
	}
	public String getFullNameFrom() {
		return fullNameFrom;
	}
	public void setFullNameFrom(String fullNameFrom) {
		this.fullNameFrom = fullNameFrom;
	}
	public String getFullNameTo() {
		return fullNameTo;
	}
	public void setFullNameTo(String fullNameTo) {
		this.fullNameTo = fullNameTo;
	}
	public String getPaymentMethodId() {
		return paymentMethodId;
	}
	public void setPaymentMethodId(String paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}
	public String getPartyCodeFrom() {
		return partyCodeFrom;
	}
	public void setPartyCodeFrom(String partyCodeFrom) {
		this.partyCodeFrom = partyCodeFrom;
	}
	public String getPartyCodeTo() {
		return partyCodeTo;
	}
	public void setPartyCodeTo(String partyCodeTo) {
		this.partyCodeTo = partyCodeTo;
	}
}
