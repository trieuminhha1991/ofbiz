package com.olbius.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.Delegator;

public  abstract class ChartModel {
	protected Map<String, Double> model;
	
	public ChartModel() {
		model = new HashMap<String, Double>();
	}
	
	public Map<String, Double> getModel() {
		return model;
	}

	public abstract void buildModel(Delegator delegator);
	
}
