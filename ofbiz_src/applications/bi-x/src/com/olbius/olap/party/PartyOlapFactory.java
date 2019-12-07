package com.olbius.olap.party;

import com.olbius.olap.OlapFactoryInterface;

public class PartyOlapFactory implements OlapFactoryInterface {
	
	public PartyOlap newInstance() {
//		return new PartyOlapImpl();
		return new HROlapImpl();
	}
	
}
