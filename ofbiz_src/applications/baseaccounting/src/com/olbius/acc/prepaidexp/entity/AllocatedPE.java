package com.olbius.acc.prepaidexp.entity;

import java.math.BigDecimal;

public class AllocatedPE {
	private String prepaidExpId;
	private String prepaidExpName;
	private BigDecimal amount;
	
	public String getPrepaidExpId() {
		return prepaidExpId;
	}
	public void setPrepaidExpId(String prepaidExpId) {
		this.prepaidExpId = prepaidExpId;
	}
	public String getPrepaidExpName() {
		return prepaidExpName;
	}
	public void setPrepaidExpName(String prepaidExpName) {
		this.prepaidExpName = prepaidExpName;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
