package com.olbius.basehr.report;

import com.olbius.olap.OlapFactoryInterface;

public class PartyOlapFactory implements OlapFactoryInterface {
	
	public PartyOlap newInstance() {
		return new HROlapImpl();
	}
}
