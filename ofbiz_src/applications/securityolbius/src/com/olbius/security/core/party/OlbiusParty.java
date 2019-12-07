package com.olbius.security.core.party;

import java.sql.Timestamp;

public class OlbiusParty implements Comparable<OlbiusParty>{

	private String partyId;

	private Timestamp fromDate;

	private Timestamp thruDate;

	public OlbiusParty(String partyId) {
		this.partyId = partyId;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public Timestamp getThruDate() {
		return thruDate;
	}

	public void setThruDate(Timestamp thruDate) {
		this.thruDate = thruDate;
	}

	public OlbiusParty mergeTime(OlbiusParty party) {

		if (!isWithIn(party)) {
			return null;
		}

		OlbiusParty p = new OlbiusParty(partyId);

		p.setFromDate(max(fromDate, party.getFromDate()));
		p.setThruDate(min(thruDate, party.getThruDate()));

		return p;

	}
	
	public boolean isMember(Timestamp timestamp) {
		if(fromDate.before(timestamp) && (thruDate == null || thruDate.after(timestamp))) {
			return true;
		}
		return false;
	}

	public OlbiusParty extendTime(OlbiusParty party) {

		if (!isWithIn(party)) {
			return null;
		}

		OlbiusParty p = new OlbiusParty(partyId);

		p.setFromDate(min(fromDate, party.getFromDate()));
		
		p.setThruDate(max(thruDate, party.getThruDate()));

		return p;
	}

	public boolean isWithIn(OlbiusParty party) {

		if (party.getThruDate() != null && party.getThruDate().before(fromDate)) {
			return false;
		}

		if (thruDate != null && thruDate.before(party.getFromDate())) {
			return false;
		}

		return true;
	}

	private Timestamp min(Timestamp timestamp, Timestamp timestamp2) {
		if (timestamp == null) {
			return timestamp2;
		}
		if (timestamp2 == null) {
			return timestamp;
		}
		if (timestamp.before(timestamp2)) {
			return timestamp;
		} else {
			return timestamp2;
		}
	}

	private Timestamp max(Timestamp timestamp, Timestamp timestamp2) {
		if (timestamp == null) {
			return timestamp;
		}
		if (timestamp2 == null) {
			return timestamp;
		}
		if (timestamp.after(timestamp2)) {
			return timestamp;
		} else {
			return timestamp2;
		}
	}

	@Override
	public int compareTo(OlbiusParty o) {
		return fromDate.compareTo(o.getFromDate());
	}
}
