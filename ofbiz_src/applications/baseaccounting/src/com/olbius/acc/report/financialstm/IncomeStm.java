package com.olbius.acc.report.financialstm;

import java.math.BigDecimal;

public class IncomeStm extends FinancialStm {
	private String name;
	private String demonstration;
	private BigDecimal value1;
	private BigDecimal value2;
	private BigDecimal orderIndex;
	private String code;
	private String targetId;
	private String displaySign;

	public String getDisplaySign() {
		return displaySign;
	}

	public void setDisplaySign(String displaySign) {
		this.displaySign = displaySign;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDemonstration() {
		return demonstration;
	}

	public void setDemonstration(String demonstration) {
		this.demonstration = demonstration;
	}

	public BigDecimal getValue1() {
		return value1;
	}

	public void setValue1(BigDecimal value1) {
		this.value1 = value1;
	}

	public BigDecimal getValue2() {
		return value2;
	}

	public void setValue2(BigDecimal value2) {
		this.value2 = value2;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public BigDecimal getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(BigDecimal orderIndex) {
		this.orderIndex = orderIndex;
	}	
}