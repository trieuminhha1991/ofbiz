package com.olbius.acc.report.incomestatement.entity;

import java.math.BigDecimal;


public class Income implements IncomeConst{
	
	//variables
	private String transTime;
	private BigDecimal saleIncome = BigDecimal.ZERO;
	private BigDecimal saleExtIncome = BigDecimal.ZERO;
	private BigDecimal saleIntIncome = BigDecimal.ZERO;
	private BigDecimal netRevenue = BigDecimal.ZERO;
	private BigDecimal grossProfit = BigDecimal.ZERO;
	private BigDecimal cogs = BigDecimal.ZERO;
	private BigDecimal saleReturn = BigDecimal.ZERO;
	private BigDecimal promotion = BigDecimal.ZERO;
	private BigDecimal saleDiscount = BigDecimal.ZERO;
	private String productId;
	private String categoryId;
	private String partyId;
	
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
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
	
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	/**
	 * NetRevenue = SaleIncome - Promotion - SaleDiscount - Other
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
	 * NetRevenue = SaleIncome - Promotion - SaleDiscount - Other
	 * @return
	 */
	public BigDecimal getNetRevenue() {
		netRevenue = getSaleIncome().subtract(promotion).subtract(saleDiscount).subtract(saleReturn);
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
	public BigDecimal getSaleReturn() {
		return saleReturn;
	}
	public void setSaleReturn(BigDecimal saleReturn) {
		this.saleReturn = saleReturn;
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
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
}
