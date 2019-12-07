package com.olbius.bi.olap.chart;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;

public class ReturnResultChart implements OlapResultQueryInterface, ReturnResultChartInterface {

	private String seriesDefaultName = "default";

	private String series;

	private String xAxis;

	private String yAxis;

	private Map<String, ReturnResultCallback<?>> callbacks;

	public ReturnResultChart() {
		callbacks = new HashMap<String, ReturnResultCallback<?>>();
	}

	@Override
	public void addSeries(String name, ReturnResultCallback<?> callback) {
		series = name;
		callbacks.put(name, callback);
	}

	@Override
	public void addSeries(String name) {
		addSeries(name, null);
	}

	@Override
	public void addXAxis(String name, ReturnResultCallback<?> callback) {
		xAxis = name;
		callbacks.put(name, callback);
	}

	@Override
	public void addXAxis(String name) {
		addXAxis(name, null);
	}

	@Override
	public void addYAxis(String name, ReturnResultCallback<?> callback) {
		yAxis = name;
		callbacks.put(name, callback);
	}

	@Override
	public void addYAxis(String name) {
		addYAxis(name, null);
	}

	@Override
	public String getSeriesDefaultName() {
		return seriesDefaultName;
	}

	@Override
	public void setSeriesDefaultName(String seriesDefaultName) {
		this.seriesDefaultName = seriesDefaultName;
	}

	@Override
	public Object resultQuery(OlapQuery query) {

		Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
		
		XAxis axis = new XAxis();
		
		axis.setData(tmp);

		boolean flag = series == null;

		if (flag) {
			tmp.put(getSeriesDefaultName(), new HashMap<String, Object>());
		}

		try {
			ResultSet resultSet = query.getResultSet();
			while (resultSet.next()) {
				try {

					String seriesName;

					if (!flag) {
						seriesName = resultSet.getString(series);
						if (callbacks.get(series) != null) {
							seriesName = (String) callbacks.get(series).get(seriesName);
						}

						if (tmp.get(seriesName) == null) {
							tmp.put(seriesName, new HashMap<String, Object>());
						}
					} else {
						seriesName = getSeriesDefaultName();
					}

					String xAxis = callbacks.get(this.xAxis) != null ? (String) callbacks.get(this.xAxis).get(resultSet.getString(this.xAxis))
							: resultSet.getString(this.xAxis);
					
					axis.add(xAxis);

					Object yAxis = callbacks.get(this.yAxis) != null ? callbacks.get(this.xAxis).get(resultSet.getObject(this.yAxis))
							: resultSet.getObject(this.yAxis);
					
					tmp.get(seriesName).put(xAxis, yAxis);
					
				} catch (Exception e) {
					Debug.logError(e.getMessage(), ReturnResultChart.class.getName());
				}
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), ReturnResultChart.class.getName());
		}

		return axis;
	}

}
