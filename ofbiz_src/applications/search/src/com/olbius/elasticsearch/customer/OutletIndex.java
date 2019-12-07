package com.olbius.elasticsearch.customer;

import io.searchbox.annotations.JestId;

import com.olbius.elasticsearch.object.GeoPoint;
import com.olbius.elasticsearch.object.Index;

public class OutletIndex implements Index {
	@JestId
	private String id;

	private String partyId;

	private GeoPoint location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}
	
	
}
