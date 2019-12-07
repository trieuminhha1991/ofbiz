package com.olbius.acc.liability;

import java.math.BigDecimal;

public class Liability {
	//Properties
	private String partyIdFrom;
	private String fullNameFrom;
	private String organizationPartyId;
	private String orgName;
	private BigDecimal totalLiability;
	private BigDecimal totalPayable;
	private BigDecimal totalReceivable;
	private String TotalRows;
	//Getters And Setters
	public String getTotalRows(){
		return this.TotalRows;
	}
	public void setTotalRows(String Totalrows){
		this.TotalRows = Totalrows;
	}
	public String getFullNameFrom(){
		return this.fullNameFrom;
	}
	public void setFullNameFrom(String fullName){
		this.fullNameFrom = fullName;
	}
	public String getOrgName(){
		return this.orgName;
	}
	public void setOrgName(String orgName){
		this.orgName = orgName;
	}
	public String getPartyIdFrom() {
		return partyIdFrom;
	}
	public void setPartyIdFrom(String customerPartyId) {
		this.partyIdFrom = customerPartyId;
	}
	public String getOrganizationPartyId() {
		return organizationPartyId;
	}
	public void setOrganizationPartyId(String organizationPartyId) {
		this.organizationPartyId = organizationPartyId;
	}
	public BigDecimal getTotalLiability() {
		return totalLiability;
	}
	public void setTotalLiability(BigDecimal totalLiability) {
		this.totalLiability = totalLiability;
	}
	public BigDecimal getTotalPayable() {
		return totalPayable;
	}
	public void setTotalPayable(BigDecimal totalPayable) {
		this.totalPayable = totalPayable;
	}
	public BigDecimal getTotalReceivable() {
		return totalReceivable;
	}
	public void setTotalReceivable(BigDecimal totalReceivable) {
		this.totalReceivable = totalReceivable;
	}
}
