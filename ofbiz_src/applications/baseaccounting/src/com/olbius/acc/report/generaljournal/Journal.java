package com.olbius.acc.report.generaljournal;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Journal {
	private Timestamp transTime;
	private String description;
	private String glAccountId;
	private String glContraAccountId;
	private BigDecimal crAmount;
	private BigDecimal drAmount;
	
	public void setTransTime(Timestamp transTime) {
		this.transTime = transTime;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setGlAccountId(String glAccountId) {
		this.glAccountId = glAccountId;
	}
	public void setGlContraAccountId(String glContraAccountId) {
		this.glContraAccountId = glContraAccountId;
	}
	public void setCrAmount(BigDecimal crAmount) {
		this.crAmount = crAmount;
	}
	public void setDrAmount(BigDecimal drAmount) {
		this.drAmount = drAmount;
	}
	public Timestamp getTransTime() {
		return transTime;
	}
	public String getDescription() {
		return description;
	}
	public String getGlAccountId() {
		return glAccountId;
	}
	public String getGlContraAccountId() {
		return glContraAccountId;
	}
	public BigDecimal getCrAmount() {
		return crAmount;
	}
	public BigDecimal getDrAmount() {
		return drAmount;
	}
}
