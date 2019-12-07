package com.olbius.acc.asset.entity;

public class FAAllocCost extends AllocCost {
	private String fixedAssetId;
	private String fixedAssetName;
	
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
}
