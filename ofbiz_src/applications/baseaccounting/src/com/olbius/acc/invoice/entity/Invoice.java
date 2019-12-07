package com.olbius.acc.invoice.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Invoice {
	//Variable 
	private String invoiceId;
	private String invoiceTypeId;
	private Timestamp invoiceDate;
    private Timestamp verifiedDate;
    private Timestamp dueDate;
	private String statusId;
	private String newStatusId;
	private String description;
	private String partyIdFrom;
	private String fullNameFrom;
	private String fullNameTo;
	private String partyId;
	private String partyCode;
	private String partyCodeFrom;
	private BigDecimal total;
	private String currencyUomId;
	private String amountToApply;
	private BigDecimal payrollAmount;
	
	//Getters And Setters
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getInvoiceTypeId() {
		return invoiceTypeId;
	}
	public void setInvoiceTypeId(String invoiceTypeId) {
		this.invoiceTypeId = invoiceTypeId;
	}
	public Timestamp getInvoiceDate() {
		return invoiceDate;
	}
	public Timestamp getVerifiedDate() {
		return verifiedDate;
	}
	public void setInvoiceDate(Timestamp invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public void setDueDate(Timestamp dueDate) {this.dueDate = dueDate; }
	public Timestamp getDueDate() { return this.dueDate; }
	public void setVerifiedDate(Timestamp verifiedDate) {
		this.verifiedDate = verifiedDate;
	}
	public String getStatusId() {
		return statusId;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public String getNewStatusId() {
		return newStatusId;
	}
	public void setNewStatusId(String newStatusId) {
		this.newStatusId = newStatusId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPartyIdFrom() {
		return partyIdFrom;
	}
	public void setPartyIdFrom(String partyIdFrom) {
		this.partyIdFrom = partyIdFrom;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public String getCurrencyUomId() {
		return currencyUomId;
	}
	public void setCurrencyUomId(String currencyUomId) {
		this.currencyUomId = currencyUomId;
	}
	public String getAmountToApply() {
		return amountToApply;
	}
	public void setAmountToApply(String amountToApply) {
		this.amountToApply = amountToApply;
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
	public BigDecimal getPayrollAmount() {
		return payrollAmount;
	}
	public void setPayrollAmount(BigDecimal payrollAmount) {
		this.payrollAmount = payrollAmount;
	}
	public String getPartyCode() {
		return partyCode;
	}
	public void setPartyCode(String partyCode) {
		this.partyCode = partyCode;
	}
	public String getPartyCodeFrom() {
		return partyCodeFrom;
	}
	public void setPartyCodeFrom(String partyCodeFrom) {
		this.partyCodeFrom = partyCodeFrom;
	}
}
