package com.olbius.bi.olap.chart;

import java.util.List;
import java.util.Map;

public interface OlapChartInterface {
	
	Map<String, List<Object>> getYAxis();
	
	List<Object> getYAxis(String key);
	
	List<String> getXAxis();
	
}
