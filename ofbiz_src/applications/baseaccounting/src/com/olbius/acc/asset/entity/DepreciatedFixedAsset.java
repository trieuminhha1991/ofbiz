package com.olbius.acc.asset.entity;

import java.math.BigDecimal;

public class DepreciatedFixedAsset extends FixedAsset {
	private BigDecimal depreciation;
	
	public BigDecimal getDepreciation() {
		return depreciation;
	}
	public void setDepreciation(BigDecimal depreciation) {
		this.depreciation = depreciation;
	}
}
