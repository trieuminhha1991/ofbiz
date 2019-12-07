package com.olbius.policy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class SalesCommissionEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String salesCommissionId;
	private String partyId;
	private String salesStatementId;
	private Timestamp fromDate;
	private Timestamp thruDate;
	private BigDecimal amount;
	private boolean hasQuantity; // is true if commission give product or product in category.
	private List<SalesCommissionAdjustmentEntity> listSalesCommissionsAdj;
	public String getSalesCommissionId() {
		return salesCommissionId;
	}
	public void setSalesCommissionId(String salesCommissionId) {
		this.salesCommissionId = salesCommissionId;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getSalesStatementId() {
		return salesStatementId;
	}
	public void setSalesStatementId(String salesStatementId) {
		this.salesStatementId = salesStatementId;
	}
	public boolean isHasQuantity() {
		return hasQuantity;
	}
	public void setHasQuantity(boolean hasQuantity) {
		this.hasQuantity = hasQuantity;
	}
	public List<SalesCommissionAdjustmentEntity> getListSalesCommissionsAdj() {
		return listSalesCommissionsAdj;
	}
	public void setListSalesCommissionsAdj(
			List<SalesCommissionAdjustmentEntity> listSalesCommissionsAdj) {
		this.listSalesCommissionsAdj = listSalesCommissionsAdj;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getThruDate() {
		return thruDate;
	}
	public void setThruDate(Timestamp thruDate) {
		this.thruDate = thruDate;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
