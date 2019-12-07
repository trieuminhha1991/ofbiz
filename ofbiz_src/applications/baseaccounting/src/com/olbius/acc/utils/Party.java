package com.olbius.acc.utils;

import org.ofbiz.entity.GenericValue;

public class Party {
	private String partyId;
	private String fullName;
	
	public Party(GenericValue party) {
		this.partyId = party.getString("partyId");
		if(party.getString("partyTypeId").equals("PERSON")) {
			this.fullName = (party.getString("lastName") != null ? party.getString("lastName") : "") 
							+ " " + (party.getString("middleName") != null ? party.getString("middleName") : "") 
							+ " " + (party.getString("firstName") != null ? party.getString("firstName") : ""); 
		}else {
			this.fullName = party.getString("groupName");
		}
	}
	
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
