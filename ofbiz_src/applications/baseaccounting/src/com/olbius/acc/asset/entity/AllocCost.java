package com.olbius.acc.asset.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class AllocCost {
	//Variables
	private String seqId;
	private Timestamp allocDate;
	private BigDecimal amount;
	private Long monthNumber;
	private BigDecimal monthlyAllocAmount;
	private BigDecimal preAccumulatedAllocAmount;
	private BigDecimal accumulatedAllocAmount;
	private BigDecimal allowAmount;
	private BigDecimal remainingValue;
	private String note;
	private String uomId;
	private String partyId;
	
	public String getSeqId() {
		return seqId;
	}
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	
	public Timestamp getAllocDate() {
		return allocDate;
	}
	public void setAllocDate(Timestamp allocDate) {
		this.allocDate = allocDate;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Long getMonthNumber() {
		return monthNumber;
	}
	public void setMonthNumber(Long monthNumber) {
		this.monthNumber = monthNumber;
	}
	public BigDecimal getMonthlyAllocAmount() {
		return monthlyAllocAmount;
	}
	public void setMonthlyAllocAmount(BigDecimal monthlyAllocAmount) {
		this.monthlyAllocAmount = monthlyAllocAmount;
	}
	public BigDecimal getPreAccumulatedAllocAmount() {
		return preAccumulatedAllocAmount;
	}
	public void setPreAccumulatedAllocAmount(BigDecimal preAccumulatedAllocAmount) {
		this.preAccumulatedAllocAmount = preAccumulatedAllocAmount;
	}
	public BigDecimal getAccumulatedAllocAmount() {
		return accumulatedAllocAmount;
	}
	public void setAccumulatedAllocAmount(BigDecimal accumulatedAllocAmount) {
		this.accumulatedAllocAmount = accumulatedAllocAmount;
	}
	public BigDecimal getAllowAmount() {
		return allowAmount;
	}
	public void setAllowAmount(BigDecimal allowAmount) {
		this.allowAmount = allowAmount;
	}
	public BigDecimal getRemainingValue() {
		return remainingValue;
	}
	public void setRemainingValue(BigDecimal remainingValue) {
		this.remainingValue = remainingValue;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getUomId() {
		return uomId;
	}
	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
}