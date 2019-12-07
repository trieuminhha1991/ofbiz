package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;

import javolution.util.FastList;

public class SalesExportActual extends OlbiusBuilder {

	public static final String userLoginId = null;
	
	public SalesExportActual(Delegator delegator) {
		super(delegator);
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("productId", "product_id");
		addDataField("quantity", "quantity");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addXAxis("product_id");
			addYAxis("quantity");
		}
	}
	
	private OlbiusQuery query2;
	
	public void initQuery() {
		List<Object> facilities = FastList.newInstance();
		
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		List<GenericValue> listFas = FastList.newInstance();
		List<String> listFaIds = FastList.newInstance();
		
		try {
			listFas = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", company, "facilityTypeId", "WAREHOUSE", "primaryFacilityGroupId", "FACILITY_INTERNAL")), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
		}
		listFaIds = EntityUtil.getFieldListFromEntityList(listFas, "facilityId", true);
		
		facilities = getFacilities(listFaIds);
		
		Condition cond =  new Condition();
		cond.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.andIn("fa.facility_dim_id", facilities)
		.andEQ("pd.product_type", "FINISHED_GOOD")
		.andEQ("sof.inventory_change_type", "SALES_ORDER")
		.andEQ("sof.inventory_type", "EXPORT");
		
		query2 = new OlbiusQuery(getSQLProcessor());
		query2.select("pd.product_code")
		.select("pd.product_id")
		.select("pd.product_name")
		.select(new Sum("-(CASE WHEN pd.require_amount = 'Y' THEN sof.amount_on_hand_total ELSE sof.quantity_on_hand_total END)"), "quantity")
		.from("inventory_item_fact", "sof")
		.join(Join.INNER_JOIN, "product_dimension", "pd", "sof.product_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "facility_dimension", "fa", "sof.facility_dim_id = fa.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "sof.inventory_date_dim_id = date_dimension.dimension_id")
		.where(cond)
		.groupBy("pd.product_code")
		.groupBy("pd.product_id")
		.groupBy("pd.product_name");
		
		
	}
	
	private List<Object> getFacilities(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = FacilityDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
	
	public class Test implements OlapResultQueryInterface {

		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
			try{
				ResultSet resultSet = query2.getResultSet();
				while (resultSet.next()) {
					try {
						String product = resultSet.getString("product_id");
						if(tmp.get(product)==null) {
						    tmp.put(product, new HashMap<String, Object>());
						   }
						tmp.get(product).put("quantity", resultSet.getBigDecimal("quantity"));
					} catch (Exception e) {
						Debug.logError(e.getMessage(), Test.class.getName());
					}
				}
			} catch(Exception e) {
				
			}
			return tmp;
		}
	}

	public class Test3 extends AbstractOlapChart {
		public Test3(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;
			for(String key : map.keySet()) {
				if(yAxis.get(key) == null) {
					yAxis.put(key, new ArrayList<Object>());
				}
				for(String s : map.get(key).keySet()) {
					yAxis.get(key).add(map.get(key).get(s));
					if(!xAxis.contains(s)) {
						xAxis.add(s);
					}
				}
			}
		}
	}
	
	public class PieChart implements OlapResultQueryInterface{
		@Override
		public Object resultQuery(OlapQuery query2) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				ResultSet resultSet = query2.getResultSet();
				if(resultSet.next()) {
					map.put("quantity", resultSet.getBigDecimal("quantity"));
				}
				return map;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public class PieResult extends AbstractOlapChart {

		public PieResult(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Object> map = ( Map<String, Object>) object;
			for(String s : map.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(map.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}
		
	}
	
	public class Test3Column extends AbstractOlapChart {

		public Test3Column(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;
			for(String key : map.keySet()) {
				if(yAxis.get(key) == null) {
					yAxis.put(key, new ArrayList<Object>());
				}
				for(String s : map.get(key).keySet()) {
					yAxis.get(key).add(map.get(key).get(s));
					if(!xAxis.contains(s)) {
						xAxis.add(s);
					}
				}
			}
		}
		
	}

	public class ProReg extends ReturnResultGrid {
		public ProReg() {
			setDatetype(dateType);
			addDataField("productId");
			addDataField("quantity");
		}

		private String dateType;

		public void setDatetype(String dateType) {
			this.dateType = dateType;
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("productId", result.getString("product_code"));
				map.put("quantity", result.getBigDecimal("quantity"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ProReg.class.getName());
			}
			return map;
		}
	}
}
