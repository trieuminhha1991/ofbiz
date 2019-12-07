package com.olbius.acc.report.incomegrowth;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilProperties;

public abstract class DataChartAdapter {
	
	public static final String UI_LABELS = "BaseAccountingUiLabels";
	
	protected String getSeriesName(String nameKey) {
		String key = "BACC";
		key += Character.valueOf(Character.toUpperCase(nameKey.charAt(0))).toString();
		key += nameKey.substring(1);
		return UtilProperties.getMessage(UI_LABELS, key, new Locale("vi"));
	}
	
	public abstract void convertData(Map<String, Object> yAxis, List<String> xAxis, List<String> values, String key, List<Map<String, Object>> data);
}
