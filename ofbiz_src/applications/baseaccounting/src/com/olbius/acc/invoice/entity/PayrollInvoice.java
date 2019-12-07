package com.olbius.acc.invoice.entity;

import java.math.BigDecimal;

public class PayrollInvoice extends Invoice {
	private BigDecimal payrollAmount;

	public BigDecimal getPayrollAmount() {
		return payrollAmount;
	}

	public void setPayrollAmount(BigDecimal payrollAmount) {
		this.payrollAmount = payrollAmount;
	}
}
