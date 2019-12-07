package com.olbius.acc.setting.entity;

import java.math.BigDecimal;

public class GlAccount {
	private String popGlAccountId;
	private String popGlAccountName;
	private BigDecimal popOpeningCrBalance;
	private BigDecimal popOpeningDrBalance;
	
	public String getPopGlAccountId() {
		return popGlAccountId;
	}
	public void setPopGlAccountId(String popGlAccountId) {
		this.popGlAccountId = popGlAccountId;
	}
	public String getPopGlAccountName() {
		return popGlAccountName;
	}
	public void setPopGlAccountName(String popGlAccountName) {
		this.popGlAccountName = popGlAccountName;
	}
	public BigDecimal getPopOpeningCrBalance() {
		return popOpeningCrBalance;
	}
	public void setPopOpeningCrBalance(BigDecimal popOpeningCrBalance) {
		this.popOpeningCrBalance = popOpeningCrBalance;
	}
	public BigDecimal getPopOpeningDrBalance() {
		return popOpeningDrBalance;
	}
	public void setPopOpeningDrBalance(BigDecimal popOpeningDrBalance) {
		this.popOpeningDrBalance = popOpeningDrBalance;
	}
}