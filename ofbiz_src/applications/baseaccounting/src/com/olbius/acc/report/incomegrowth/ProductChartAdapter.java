package com.olbius.acc.report.incomegrowth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductChartAdapter extends DataChartAdapter{

	@Override
	public void convertData(Map<String, Object> yAxis, List<String> xAxis,  List<String> values, String key, List<Map<String, Object>> data) {
		for(String value : values) {
			List<Object> chartData = new ArrayList<Object>();
			if(data.size() > 10) {
				for(int i = 0; i < 10; i++) {
					xAxis.add((String)data.get(i).get(key));
					chartData.add(data.get(i).get(value));
				}
			}else {
				for(int i = 0; i < data.size(); i++) {
					xAxis.add((String)data.get(i).get(key));
					chartData.add(data.get(i).get(value));
				}
			}
			yAxis.put(getSeriesName(value), chartData);
		}
	}

}
