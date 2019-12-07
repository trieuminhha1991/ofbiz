package com.olbius.bi.olap.chart;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;

public class OlapColumnChart extends AbstractOlapChart {

	public OlapColumnChart(OlapInterface olap, OlapResultQueryInterface query) {
		super(olap, query);
	}

	private void axis(Map<String, Map<String, Object>> map)
			throws GenericDataSourceException, GenericEntityException, SQLException {

		if (map.isEmpty()) {
			return;
		}

		for (String key : map.keySet()) {
			if (key != null && this.getYAxis().get(key) == null) {
				this.getYAxis().put(key, new ArrayList<Object>());
			}
			for (String s : map.get(key).keySet()) {
				if (!this.xAxis.contains(s)) {
					this.xAxis.add(s);
				}
			}
		}

		for (String s : this.getXAxis()) {

			for (String key : map.keySet()) {
				if (key != null) {
					if (map.get(key).get(s) != null) {
						this.getYAxis().get(key).add(map.get(key).get(s));
					} else {
						this.getYAxis().get(key).add(new Integer(0));
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void result(Object object) {
		try {

			this.getXAxis().clear();

			this.getYAxis().clear();

			Map<String, Map<String, Object>> map;
			
			if(object instanceof XAxis) {
				XAxis tmp = (XAxis) object;
				this.getXAxis().addAll(tmp.getXAxis());
				map = (Map<String, Map<String, Object>>) tmp.getData();
			} else {
				map = (Map<String, Map<String, Object>>) object;
			}
			
			axis(map);
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), olap.getModule());
		}
	}

}
