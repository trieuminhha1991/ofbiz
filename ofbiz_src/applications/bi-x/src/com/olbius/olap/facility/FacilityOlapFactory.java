package com.olbius.olap.facility;

import com.olbius.olap.OlapFactoryInterface;

public class FacilityOlapFactory implements OlapFactoryInterface{
	
	public FacilityOlap newInstance() {
		
		return new FacilityOlapImpl();
	
	}
	
}
