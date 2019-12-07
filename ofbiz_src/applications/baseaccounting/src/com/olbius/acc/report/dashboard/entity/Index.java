package com.olbius.acc.report.dashboard.entity;

import java.math.BigDecimal;

import com.olbius.acc.report.incomestatement.entity.IncomeConst;


public class Index implements IncomeConst{
	
	//variables
	private BigDecimal saleIncome = BigDecimal.ZERO;
	private BigDecimal saleExtIncome = BigDecimal.ZERO;
	private BigDecimal saleIntIncome = BigDecimal.ZERO;
	private BigDecimal netRevenue = BigDecimal.ZERO;
	private BigDecimal grossProfit = BigDecimal.ZERO;
	private BigDecimal cogs = BigDecimal.ZERO;
	private BigDecimal other = BigDecimal.ZERO;
	private BigDecimal promotion = BigDecimal.ZERO;
	private BigDecimal saleDiscount = BigDecimal.ZERO;
	private BigDecimal revenueDeduction = BigDecimal.ZERO;
	
	
	//Getters And Setters
	public BigDecimal getSaleExtIncome() {
		if(saleExtIncome != null) {
			return saleExtIncome.abs();
		}
		return saleExtIncome;
	}
	
	public void setSaleExtIncome(BigDecimal saleExtIncome) {
		this.saleExtIncome = saleExtIncome;
	}
	
	public BigDecimal getSaleIntIncome() {
		if(saleIntIncome != null) {
			return saleIntIncome.abs();
		}
		return saleIntIncome;
	}
	
	public void setSaleIntIncome(BigDecimal saleIntIncome) {
		this.saleIntIncome = saleIntIncome;
	}	
	
	/**
	 * SaleIncome = SaleExtIncome + SaleIntIncome
	 * @return
	 */
	public BigDecimal getSaleIncome() {
		saleIncome = getSaleIntIncome().add(getSaleExtIncome());
		return saleIncome;
	}
	public void setSaleIncome(BigDecimal saleIncome) {
		this.saleIncome = saleIncome;
	}	
	
	/**
	 * SaleIncome = SaleExtIncome + SaleIntIncome
	 * @return
	 */
	public BigDecimal getRevenueDeduction() {
		revenueDeduction = getPromotion().add(getOther()).add(getSaleDiscount());
		return revenueDeduction;
	}
	public void setRevenueDeduction(BigDecimal revenueDeduction) {
		this.revenueDeduction = revenueDeduction;
	}		
	/**
	 * NetRevenue = SaleIncome - Promotion - SaleDiscount - Other
	 * @return
	 */
	public BigDecimal getNetRevenue() {
		netRevenue = getSaleIncome().subtract(promotion).subtract(saleDiscount).subtract(other);
		return netRevenue;
	}
	public void setNetRevenue(BigDecimal netRevenue) {
		this.netRevenue = netRevenue;
	}
	/**
	 * GrossProfit = NetRevenue - cogs
	 * @return
	 */
	public BigDecimal getGrossProfit() {
		grossProfit = getNetRevenue().subtract(cogs);
		return grossProfit;
	}
	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}
	public BigDecimal getCogs() {
		return cogs;
	}
	public void setCogs(BigDecimal cogs) {
		this.cogs = cogs;
	}
	public BigDecimal getOther() {
		return other;
	}
	public void setOther(BigDecimal other) {
		this.other = other;
	}
	public BigDecimal getPromotion() {
		return promotion;
	}
	public void setPromotion(BigDecimal promotion) {
		this.promotion = promotion;
	}
	public BigDecimal getSaleDiscount() {
		return saleDiscount;
	}
	public void setSaleDiscount(BigDecimal saleDiscount) {
		this.saleDiscount = saleDiscount;
	}
}
