package com.olbius.googlemaps;

public class PartyIDLatLng {
	private String partyId;
	private LatLng location;
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}
	public PartyIDLatLng(String partyId, LatLng location) {
		super();
		this.partyId = partyId;
		this.location = location;
	}
	public PartyIDLatLng() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
