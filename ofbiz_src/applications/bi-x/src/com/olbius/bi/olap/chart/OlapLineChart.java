package com.olbius.bi.olap.chart;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.OlapDate;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.TypeOlap;

public class OlapLineChart extends AbstractOlapChart {

	public OlapLineChart(OlapInterface olap, OlapResultQueryInterface query) {
		super(olap, query);
	}

	private String getDateType(String dateType) {
		if ("year_month_day".equals(dateType)) {
			dateType = OlapInterface.DAY;
		}
		if ("year_and_month".equals(dateType)) {
			dateType = OlapInterface.MONTH;
		}
		if ("year_name".equals(dateType)) {
			dateType = OlapInterface.YEAR;
		}
		if ("week_and_year".equals(dateType)) {
			dateType = OlapInterface.WEEK;
		}
		if ("quarter_and_year".equals(dateType)) {
			dateType = OlapInterface.QUARTER;
		}
		return dateType;
	}
	
	protected void axis(Map<String, Map<String, Object>> map, String dateType) throws GenericDataSourceException, GenericEntityException, SQLException {

		if (map.isEmpty()) {
			return;
		}

		OlapDate olapDate = new OlapDate();
		olapDate.SQLProcessor(olap.getSQLProcessor());
		olapDate.setFromDate(new Date(olap.getFromDate().getTime()));
		olapDate.setThruDate(new Date(olap.getThruDate().getTime()));

		this.getXAxis().clear();
		this.getXAxis().addAll(olapDate.getValues(dateType));
		
		this.setDateType(getDateType(dateType));

		this.getYAxis().clear();

		for (String key : map.keySet()) {
			if (key != null && this.getYAxis().get(key) == null) {
				this.getYAxis().put(key, new ArrayList<Object>());
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

			Map<String, Map<String, Object>> map;

			if (object instanceof XAxis) {
				map = ((XAxis) object).getData();
			} else {
				map = (Map<String, Map<String, Object>>) object;
			}

			axis(map, (String) getParameter(TypeOlap.DATE_TYPE));
		} catch (Exception e) {
			Debug.logError(e, olap.getModule());
		}
	}
}
