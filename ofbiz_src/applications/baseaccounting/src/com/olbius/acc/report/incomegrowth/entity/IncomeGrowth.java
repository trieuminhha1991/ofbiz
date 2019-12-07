package com.olbius.acc.report.incomegrowth.entity;

import java.math.BigDecimal;

public class IncomeGrowth {
	private BigDecimal grossProfit1;
	private BigDecimal saleIncome1;
	private BigDecimal grossProfit2;
	private BigDecimal saleIncome2;
	private BigDecimal grossProfitRate;
	private BigDecimal saleIncomeRate;
	public BigDecimal getGrossProfit1() {
		return grossProfit1;
	}
	public void setGrossProfit1(BigDecimal grossProfit1) {
		this.grossProfit1 = grossProfit1;
	}
	public BigDecimal getSaleIncome1() {
		return saleIncome1;
	}
	public void setSaleIncome1(BigDecimal saleIncome1) {
		this.saleIncome1 = saleIncome1;
	}
	public BigDecimal getGrossProfit2() {
		return grossProfit2;
	}
	public void setGrossProfit2(BigDecimal grossProfit2) {
		this.grossProfit2 = grossProfit2;
	}
	public BigDecimal getSaleIncome2() {
		return saleIncome2;
	}
	public void setSaleIncome2(BigDecimal saleIncome2) {
		this.saleIncome2 = saleIncome2;
	}
	public BigDecimal getGrossProfitRate() {
		if(grossProfit1.compareTo(BigDecimal.ZERO) != 0) {
			grossProfitRate = (grossProfit2.subtract(grossProfit1)).divide(grossProfit1, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
		}
		return grossProfitRate;
	}
	public void setGrossProfitRate(BigDecimal grossProfitRate) {
		this.grossProfitRate = grossProfitRate;
	}
	public BigDecimal getSaleIncomeRate() {
		if(saleIncome1.compareTo(BigDecimal.ZERO) != 0) {
			saleIncomeRate = (saleIncome2.subtract(saleIncome1)).divide(saleIncome1, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
		}
		return saleIncomeRate;
	}
	public void setSaleIncomeRate(BigDecimal saleIncomeRate) {
		this.saleIncomeRate = saleIncomeRate;
	}
}
