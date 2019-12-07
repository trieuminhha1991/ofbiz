package com.olbius.acc.prepaidexp.entity;

import com.olbius.acc.asset.entity.AllocCost;

public class PEAllocCost extends AllocCost{
	private String prepaidExpId;
	private String prepaidExpName;
	
	public String getPrepaidExpId() {
		return prepaidExpId;
	}
	public void setPrepaidExpId(String prepaidExpId) {
		this.prepaidExpId = prepaidExpId;
	}
	public String getPrepaidExpName() {
		return prepaidExpName;
	}
	public void setPrepaidExpName(String prepaidExpName) {
		this.prepaidExpName = prepaidExpName;
	}
}
