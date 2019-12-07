package com.olbius.bi.olap.chart;

import com.olbius.bi.olap.ReturnResultCallback;

public interface ReturnResultChartInterface {
	
	public void addSeries(String name, ReturnResultCallback<?> callback);

	public void addSeries(String name);

	public void addXAxis(String name, ReturnResultCallback<?> callback);

	public void addXAxis(String name);

	public void addYAxis(String name, ReturnResultCallback<?> callback);

	public void addYAxis(String name);

	public String getSeriesDefaultName();

	public void setSeriesDefaultName(String seriesDefaultName);
}
