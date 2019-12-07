package com.olbius.elasticsearch.product;

import java.util.HashMap;
import java.util.Map;

import io.searchbox.annotations.JestId;

public class ProductIndex {
	@JestId
	private String id;

	private String productId;

	private String productCode;

	private String productName;

	private String brandName;

	private String primaryProductCategoryId;

	private String partyId;

	private Long orderDate;

	private Map<String, String> location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getPrimaryProductCategoryId() {
		return primaryProductCategoryId;
	}

	public void setPrimaryProductCategoryId(String primaryProductCategoryId) {
		this.primaryProductCategoryId = primaryProductCategoryId;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Long getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Long orderDate) {
		this.orderDate = orderDate;
	}

	public Map<String, String> getLocation() {
		if (location == null) {
			location = new HashMap<>();
		}
		return location;
	}

	public void setLocation(Map<String, String> location) {
		this.location = location;
	}

}
