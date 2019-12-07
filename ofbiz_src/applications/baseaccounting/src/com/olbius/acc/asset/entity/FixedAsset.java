package com.olbius.acc.asset.entity;

import java.math.BigDecimal;

public class FixedAsset {
	private String fixedAssetId;
	private String fixedAssetName;
	private BigDecimal totalDep;
	
	public String getFixedAssetId() {
		return fixedAssetId;
	}
	public void setFixedAssetId(String fixedAssetId) {
		this.fixedAssetId = fixedAssetId;
	}
	public String getFixedAssetName() {
		return fixedAssetName;
	}
	public void setFixedAssetName(String fixedAssetName) {
		this.fixedAssetName = fixedAssetName;
	}
	public BigDecimal getTotalDep() {
		return totalDep;
	}
	public void setTotalDep(BigDecimal totalDep) {
		this.totalDep = totalDep;
	}
}
