package com.olbius.payroll.entity;

import java.sql.Timestamp;

public class EntityParameter {
	
	private String code;
	private String value;
	private Timestamp fromDate;
	private Timestamp thruDate;
	private String periodTypeId;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	public String getPeriodTypeId() {
		return periodTypeId;
	}
	public void setPeriodTypeId(String periodTypeId) {
		this.periodTypeId = periodTypeId;
	}
	
	
}
