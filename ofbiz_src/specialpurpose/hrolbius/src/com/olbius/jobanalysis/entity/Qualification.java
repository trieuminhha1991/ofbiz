package com.olbius.jobanalysis.entity;

public class Qualification {
	private String partyQualTypeId;
	private String description;
	private boolean selected;
	public String getPartyQualTypeId() {
		return partyQualTypeId;
	}
	public void setPartyQualTypeId(String partyQualTypeId) {
		this.partyQualTypeId = partyQualTypeId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
