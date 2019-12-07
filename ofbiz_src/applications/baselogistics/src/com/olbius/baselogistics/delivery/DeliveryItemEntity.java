package com.olbius.baselogistics.delivery;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DeliveryItemEntity {
	
	//Attribute
	private String deliveryId;
	private String deliveryItemSeqId;
	private String productId;
	private String productCode;
	private String productName;
	private String unit;
	private String code;
	private String batch;
	private String fromOrderId;
	private String fromOrderItemSeqId;
	private BigDecimal selectedAmount;
	private BigDecimal actualExportedQuantity;
	private BigDecimal actualDeliveredQuantity;

	private BigDecimal actualExportedAmount;
	private BigDecimal actualDeliveredAmount;
	private BigDecimal quantity;
	private BigDecimal amount;
	private BigDecimal unitPrice;
	private BigDecimal alternativeUnitPrice;
	private BigDecimal unitPriceWithTax;
	private BigDecimal convertNumber;
	private BigDecimal qcQuantity;
	private BigDecimal eaQuantity;
	private Timestamp actualExpireDate;
	private BigDecimal total;
	private Timestamp actualManufacturedDate;
	private String lotAndManufacturedDate;
	private String lotId;
	private String sku;
	private String statusId;
	private String isPromo;
	private String isKg;
	//Getters
	
	public String getIsPromo() {
		return isPromo;
	}
	public String getDeliveryId() {
		return deliveryId;
	}
	public String getFromOrderId() {
		return fromOrderId;
	}
	public String getFromOrderItemSeqId() {
		return fromOrderItemSeqId;
	}
	public String getSku() {
		return sku;
	}
	public String getDeliveryItemSeqId() {
		return deliveryItemSeqId;
	}
	public String getProductId() {
		return productId;
	}
	public String getLotId() {
		return lotId;
	}
	public String getLotAndManufacturedDate() {
		return lotAndManufacturedDate;
	}
	public String getProductCode() {
		return productCode;
	}
	public String getBatch() {
		return batch;
	}
	public BigDecimal getActualDeliveredQuantity() {
		return actualDeliveredQuantity;
	}
	public Timestamp getActualExpireDate() {
		return actualExpireDate;
	}
	public Timestamp getActualManufacturedDate() {
		return actualManufacturedDate;
	}
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
	public BigDecimal getQuantity() {
		return quantity;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public BigDecimal getAlternativeUnitPrice() {
		return alternativeUnitPrice;
	}
	public BigDecimal getUnitPriceWithTax() {
		return unitPriceWithTax;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
	public BigDecimal getConvertNumber() {
		return convertNumber;
	}
	public BigDecimal getQcQuantity() {
		return qcQuantity;
	}
	public BigDecimal getEaQuantity() {
		return eaQuantity;
	}
	public BigDecimal getActualExportedAmount() {
		return actualExportedAmount;
	}
	public BigDecimal getActualDeliveredAmount() {
		return actualDeliveredAmount;
	}
	public String getStatusId() {
		return statusId;
	}
	//Setters
	
	public void setSku(String sku) {
		this.sku = sku;
	}
	public void setIsPromo(String isPromo) {
		this.isPromo = isPromo;
	}
	public void setFromOrderId(String fromOrderId) {
		this.fromOrderId = fromOrderId;
	}
	public void setFromOrderItemSeqId(String fromOrderItemSeqId) {
		this.fromOrderItemSeqId = fromOrderItemSeqId;
	}
	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}
	public void setDeliveryItemSeqId(String deliveryItemSeqId) {
		this.deliveryItemSeqId = deliveryItemSeqId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public void setLotId(String lotId) {
		this.lotId = lotId;
	}
	public void setLotAndManufacturedDate(String lotAndManufacturedDate) {
		this.lotAndManufacturedDate = lotAndManufacturedDate;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public void setActualDeliveredQuantity(BigDecimal actualDeliveredQuantity) {
		this.actualDeliveredQuantity = actualDeliveredQuantity;
	}
	public void setActualExpireDate(Timestamp actualExpireDate) {
		this.actualExpireDate = actualExpireDate;
	}
	public void setActualManufacturedDate(Timestamp actualManufacturedDate) {
		this.actualManufacturedDate = actualManufacturedDate;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setActualExportedQuantity(BigDecimal actualExportedQuantity) {
		this.actualExportedQuantity = actualExportedQuantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public void setAlternativeUnitPrice(BigDecimal alternativeUnitPrice) {
		this.alternativeUnitPrice = alternativeUnitPrice;
	}
	public void setUnitPriceWithTax(BigDecimal unitPriceWithTax) {
		this.unitPriceWithTax = unitPriceWithTax;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public void setConvertNumber(BigDecimal convertNumber) {
		this.convertNumber = convertNumber;
	}
	public void setQcQuantity(BigDecimal qcQuantity) {
		this.qcQuantity = qcQuantity;
	}
	public void setEaQuantity(BigDecimal eaQuantity) {
		this.eaQuantity = eaQuantity;
	}
	public void setActualExportedAmount(BigDecimal actualExportedAmount) {
		this.actualExportedAmount = actualExportedAmount;
	}
	public void setActualDeliveredAmount(BigDecimal actualDeliveredAmount) {
		this.actualDeliveredAmount = actualDeliveredAmount;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public String getIsKg() {
		return isKg;
	}
	public void setIsKg(String isKg) {
		this.isKg = isKg;
	}
	public BigDecimal getSelectedAmount() {
		return selectedAmount;
	}
	public void setSelectedAmount(BigDecimal selectedAmount) {
		this.selectedAmount = selectedAmount;
	}
}
