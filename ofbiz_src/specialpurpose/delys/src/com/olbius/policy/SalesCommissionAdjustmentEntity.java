package com.olbius.policy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class SalesCommissionAdjustmentEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String salesCommissionId;
	private String salesPolicyId;
	private String salesPolicyRuleId;
	private String salesPolicyActionSeqId;
	private BigDecimal amount;
	private BigDecimal quantity;
	private List<String> productId;
	private List<String> categoryId;
	private String description;
	public String getSalesCommissionId() {
		return salesCommissionId;
	}
	public void setSalesCommissionId(String salesCommissionId) {
		this.salesCommissionId = salesCommissionId;
	}
	public String getSalesPolicyId() {
		return salesPolicyId;
	}
	public void setSalesPolicyId(String salesPolicyId) {
		this.salesPolicyId = salesPolicyId;
	}
	public String getSalesPolicyRuleId() {
		return salesPolicyRuleId;
	}
	public void setSalesPolicyRuleId(String salesPolicyRuleId) {
		this.salesPolicyRuleId = salesPolicyRuleId;
	}
	public String getSalesPolicyActionSeqId() {
		return salesPolicyActionSeqId;
	}
	public void setSalesPolicyActionSeqId(String salesPolicyActionSeqId) {
		this.salesPolicyActionSeqId = salesPolicyActionSeqId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public List<String> getProductId() {
		return productId;
	}
	public void setProductId(List<String> productId) {
		this.productId = productId;
	}
	public List<String> getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(List<String> categoryId) {
		this.categoryId = categoryId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
