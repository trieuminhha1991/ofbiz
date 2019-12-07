package com.olbius.bi.olap.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;

public class OlapPieChart extends AbstractOlapChart {

	public OlapPieChart(OlapInterface olap, OlapResultQueryInterface query) {
		super(olap, query);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void result(Object object) {
		try {

			if (object instanceof XAxis) {
				XAxis tmp = (XAxis) object;

				for (String s : tmp.getData().keySet()) {

					xAxis.clear();
					yAxis.clear();

					Map<String, Object> map = tmp.getData().get(s);
					for (String key : map.keySet()) {
						xAxis.add(key);
						List<Object> list = new ArrayList<Object>();
						list.add(map.get(key));
						yAxis.put(key, list);
					}
				}

			} else {
				Map<String, Object> map = (Map<String, Object>) object;
				for (String key : map.keySet()) {
					xAxis.add(key);
					List<Object> list = new ArrayList<Object>();
					list.add(map.get(key));
					yAxis.put(key, list);
				}
			}

		} catch (Exception e) {
			Debug.logError(e.getMessage(), olap.getModule());
		}
	}

}
