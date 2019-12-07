package com.olbius.elasticsearch.customer;

import com.olbius.elasticsearch.object.GeoPoint;
import com.olbius.elasticsearch.object.Index;

import io.searchbox.annotations.JestId;

public class CustomerIndex implements Index {
	@JestId
	private String id;

	private String partyId;

	private String routePartyId;

	private String supPartyId;

	private String customerId;

	private String partyIdTo;

	private String stateProvinceGeoId;

	private String districtGeoId;

	private GeoPoint location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getRoutePartyId() {
		return routePartyId;
	}

	public void setRoutePartyId(String routePartyId) {
		this.routePartyId = routePartyId;
	}

	public String getSupPartyId() {
		return supPartyId;
	}

	public void setSupPartyId(String supPartyId) {
		this.supPartyId = supPartyId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getPartyIdTo() {
		return partyIdTo;
	}

	public void setPartyIdTo(String partyIdTo) {
		this.partyIdTo = partyIdTo;
	}

	public String getStateProvinceGeoId() {
		return stateProvinceGeoId;
	}

	public void setStateProvinceGeoId(String stateProvinceGeoId) {
		this.stateProvinceGeoId = stateProvinceGeoId;
	}

	public String getDistrictGeoId() {
		return districtGeoId;
	}

	public void setDistrictGeoId(String districtGeoId) {
		this.districtGeoId = districtGeoId;
	}

}
