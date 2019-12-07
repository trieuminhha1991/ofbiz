package com.olbius.accounting.appr;

import java.math.BigDecimal;

public class DeliveryItemEntity {
	
	//Attribute
	private String productName;
	private String unit;
	private String code;
	private BigDecimal actualExportedQuantity;
	private BigDecimal unitPrice;
	private String total;
	//Getters
	public String getProductName() {
		return productName;
	}
	public String getUnit() {
		return unit;
	}
	public String getCode() {
		return code;
	}
	public BigDecimal getActualExportedQuantity() {
		return actualExportedQuantity;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public String getTotal() {
		return total;
	}
	
	//Setters
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setActualExportedQuantity(BigDecimal actualExportedQuantity) {
		this.actualExportedQuantity = actualExportedQuantity;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public void setTotal(String total) {
		this.total = total;
	}
}
