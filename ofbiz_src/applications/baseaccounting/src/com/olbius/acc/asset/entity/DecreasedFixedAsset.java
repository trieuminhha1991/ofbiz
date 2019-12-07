package com.olbius.acc.asset.entity;

import java.math.BigDecimal;

public class DecreasedFixedAsset extends FixedAsset {
	private BigDecimal remainValue;

	public BigDecimal getRemainValue() {
		return remainValue;
	}

	public void setRemainValue(BigDecimal remainValue) {
		this.remainValue = remainValue;
	}
}
