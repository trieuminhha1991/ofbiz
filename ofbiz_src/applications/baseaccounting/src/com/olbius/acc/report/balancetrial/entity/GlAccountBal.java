package com.olbius.acc.report.balancetrial.entity;

import java.math.BigDecimal;

public class GlAccountBal {
	private String glAccountId;
	private String accountName;
	private String isLeaf;
	private String parentId;
	private BigDecimal openingCrBalance;
	private BigDecimal openingDrBalance;
	private BigDecimal endingCrBalance;
	private BigDecimal endingDrBalance;
	private BigDecimal postedDebits;
	private BigDecimal postedCredits;
	
	public String getGlAccountId() {
		return glAccountId;
	}
	public void setGlAccountId(String glAccountId) {
		this.glAccountId = glAccountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public BigDecimal getOpeningCrBalance() {
		return openingCrBalance;
	}
	public void setOpeningCrBalance(BigDecimal openingCrBalance) {
		this.openingCrBalance = openingCrBalance;
	}
	public BigDecimal getOpeningDrBalance() {
		return openingDrBalance;
	}
	public void setOpeningDrBalance(BigDecimal openingDrBalance) {
		this.openingDrBalance = openingDrBalance;
	}
	public BigDecimal getEndingCrBalance() {
		return endingCrBalance;
	}
	public void setEndingCrBalance(BigDecimal endingCrBalance) {
		this.endingCrBalance = endingCrBalance;
	}
	public BigDecimal getEndingDrBalance() {
		return endingDrBalance;
	}
	public void setEndingDrBalance(BigDecimal endingDrBalance) {
		this.endingDrBalance = endingDrBalance;
	}
	public BigDecimal getPostedDebits() {
		return postedDebits;
	}
	public void setPostedDebits(BigDecimal postedDebits) {
		this.postedDebits = postedDebits;
	}
	public BigDecimal getPostedCredits() {
		return postedCredits;
	}
	public void setPostedCredits(BigDecimal postedCredits) {
		this.postedCredits = postedCredits;
	}
	public String getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(String isLeaf) {
		this.isLeaf = isLeaf;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}
