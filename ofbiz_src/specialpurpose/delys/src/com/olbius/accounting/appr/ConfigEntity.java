package com.olbius.accounting.appr;

import java.math.BigDecimal;

public class ConfigEntity {
	
	//Attribute
	private String productName;
	private String uomFrom;
	private BigDecimal quantityConvert;
	private String uomTo;
	
	//Getters
	public String getProductName() {
		return productName;
	}
	public String getUomFrom() {
		return uomFrom;
	}
	public String getUomTo() {
		return uomTo;
	}
	public BigDecimal getQuantityConvert() {
		return quantityConvert;
	}
	//Setters
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public void setUomFrom(String uomFrom) {
		this.uomFrom = uomFrom;
	}
	public void setUomTo(String uomTo) {
		this.uomTo = uomTo;
	}
	public void setQuantityConvert(BigDecimal quantityConvert) {
		this.quantityConvert = quantityConvert;
	}
	
	
}
