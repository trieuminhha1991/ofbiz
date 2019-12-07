package com.olbius.bi.services;

import com.olbius.olap.accounting.AccountingOlapFactory;
import com.olbius.olap.facility.FacilityOlapFactory;
import com.olbius.olap.party.PartyOlapFactory;

public class OlapServiceFactory {
	
	public final static AccountingOlapFactory ACCOUNTING = new AccountingOlapFactory();
	
	public final static FacilityOlapFactory FACILITY = new FacilityOlapFactory();
	
	public final static PartyOlapFactory PARTY = new PartyOlapFactory();
	
}
