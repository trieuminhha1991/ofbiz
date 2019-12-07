package com.olbius.baselogistics.report;

import com.olbius.olap.OlapFactoryInterface;

public class FacilityLOGOlapFactory implements OlapFactoryInterface{
	
	public FacilityOlapLOG newInstance() {
		
		return new GeneralOlapFacilityImp();
	
	}
	
}
