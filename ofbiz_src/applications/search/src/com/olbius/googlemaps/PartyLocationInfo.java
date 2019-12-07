package com.olbius.googlemaps;

public class PartyLocationInfo {
	private PartyIDLatLng[] list;

	public PartyIDLatLng[] getList() {
		return list;
	}

	public void setList(PartyIDLatLng[] list) {
		this.list = list;
	}

	public PartyLocationInfo(PartyIDLatLng[] list) {
		super();
		this.list = list;
	}

	public PartyLocationInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
