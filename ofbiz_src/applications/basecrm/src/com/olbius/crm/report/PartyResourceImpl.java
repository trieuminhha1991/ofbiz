package com.olbius.crm.report;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.GeoDimension;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.crm.report.CommunicationCampaignOlapImpl.CommunicationCampaign;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PartyResourceImpl extends AbstractOlap {

	public static final String module = PartyResourceImpl.class.getName();
	private OlbiusQuery query;
	private Delegator delegator;

	private Object getGeo(String geoId) {
		if (geoId != null) {
			return GeoDimension.D.getId(delegator, geoId);
		}
		return null;
	}

	public PartyResourceImpl(Delegator delegator) {
		setModule(module);
		this.delegator = delegator;
	}

	Map<String, Object> sum = FastMap.newInstance();

	private void initQuery() {
		String dataSourceId = (String) getParameter("dataSourceId");
		String geoId = (String) getParameter("geoId");
		OlbiusQuery queryTmp = new OlbiusQuery();
		queryTmp.select("extract(year from age(date_dimension.date_value))", "age")
				.from("party_resource_birth_date_fact", "c")
				.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = birth_date_dim_id");
		Condition cond = Condition.makeEQ("data_source_id", dataSourceId, UtilValidate.isNotEmpty(dataSourceId));
		cond.andEQ("geo_dim_id", getGeo(geoId), UtilValidate.isNotEmpty(geoId));
		queryTmp.where(cond);

		query = new OlbiusQuery(getSQLProcessor());
		query.from(queryTmp, "tmp");

		List<Map<String, ?>> type = FastList.newInstance();
		type.add(UtilMisc.toMap("start", 0, "end", 5));
		type.add(UtilMisc.toMap("start", 5, "end", 10));
		type.add(UtilMisc.toMap("start", 10, "end", 15));
		type.add(UtilMisc.toMap("start", 15, "end", 1000));
		int i = 1;
		for (Map<String, ?> s : type) {
			String start = Integer.toString((Integer) s.get("start"));
			String end = Integer.toString((Integer) s.get("end"));
			query.select("sum(case when age >= " + start + " AND age < " + end + " then 1 else 0 end)",
					"Range" + String.valueOf(i));
			i++;
		}
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}

	public class DataResource extends ReturnResultGrid {

		public DataResource() {
			addDataField("Range1");
			addDataField("Range2");
			addDataField("Range3");
			addDataField("Range4");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("Range1", result.getBigDecimal("Range1"));
				map.put("Range2", result.getBigDecimal("Range2"));
				map.put("Range3", result.getBigDecimal("Range3"));
				map.put("Range4", result.getBigDecimal("Range4"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), CommunicationCampaign.class.getName());
			}
			return map;
		}
	}

	public class PieResult extends AbstractOlapChart {

		public PieResult(OlapInterface olap, OlapResultQueryInterface query) {
			super(olap, query);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Object> map = (Map<String, Object>) object;
			for (String s : map.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(map.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}

	}

	public class PieOlapResultQuery implements OlapResultQueryInterface {
		Locale locale = null;

		@Override
		public Object resultQuery(OlapQuery query) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				ResultSet resultSet = query.getResultSet();
				if (resultSet.next()) {
					String range1 = UtilProperties.getMessage("BaseCRMUiLabels", "BCRMRange1", locale);
					String range2 = UtilProperties.getMessage("BaseCRMUiLabels", "BCRMRange2", locale);
					String range3 = UtilProperties.getMessage("BaseCRMUiLabels", "BCRMRange3", locale);
					String range4 = UtilProperties.getMessage("BaseCRMUiLabels", "BCRMRange4", locale);
					map.put(range1, resultSet.getBigDecimal("Range1"));
					map.put(range2, resultSet.getBigDecimal("Range2"));
					map.put(range3, resultSet.getBigDecimal("Range3"));
					map.put(range4, resultSet.getBigDecimal("Range4"));
				}
				return map;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public Locale getLocale() {
			return this.locale;
		}

		public void setLocale(Locale locale) {
			this.locale = locale;
		}
	}
}
