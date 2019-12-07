package com.olbius.basehr.payroll.entity;

import java.io.Serializable;

public class EntitySalaryAmount implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String partyId;
	private String amount;
	private String orgId;
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
}
