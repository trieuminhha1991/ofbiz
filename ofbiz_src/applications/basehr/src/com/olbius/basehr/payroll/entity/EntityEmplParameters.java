package com.olbius.basehr.payroll.entity;

import java.util.List;

public class EntityEmplParameters {
	private String partyId;
	private List<EntityParameter> emplParameters;
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public List<EntityParameter> getEmplParameters() {
		return emplParameters;
	}
	public void setEmplParameters(List<EntityParameter> emplParameters) {
		this.emplParameters = emplParameters;
	}
	
}
