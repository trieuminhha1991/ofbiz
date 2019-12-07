package org.ofbiz.products;

import java.math.BigDecimal;

public class Products{
	public String product_type_id;
	public String product_name;
	public String internal_name;
	public boolean isPromo;
	private BigDecimal price;
	private String facilityId; 
	public String description;
	public String quantityUomId;
	public String categoryId;
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	
	
	
	
}