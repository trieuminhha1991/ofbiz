package com.olbius.basepos.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ReturnOlapImpl extends AbstractOlap implements PosOlap {
	private OlbiusQuery query;
	private OlbiusQuery querySum;
	
	BigDecimal sumQuantity = BigDecimal.ZERO;
	BigDecimal sumExtPrice = BigDecimal.ZERO;
	
	private void initQuery() {
		String sortField = (String) getParameter(SORT_FIELD);
		String sortOption = (String) getParameter(SORT_OPTION);
		String facilityId = (String) getParameter("facilityId");
		String partyId = (String) getParameter("partyId");
		String org = (String) getParameter("org");
		
		Condition condition = new Condition();
		query = new OlbiusQuery(getSQLProcessor());

		query.from("return_order_fact");
		query.select("currency_dimension.currency_id");
		query.select("product_dimension.product_code");
		query.select("product_dimension.internal_name");
		query.select("sum(quantity)", "_quantity");
		query.select("sum(total)", "_total");
		
		query.join(Join.INNER_JOIN, "date_dimension", null, "return_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "party", "party_dim_id = party.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension", "facility", "facility_dim_id = facility.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "owner_party", "facility.owner_party_dim_id = owner_party.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("party.party_id", partyId, partyId != null);
		condition.andEQ("facility.facility_id", facilityId, facilityId != null);
		condition.andEQ("owner_party.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("currency_dimension.currency_id");
		query.groupBy("product_dimension.product_code");
		query.groupBy("product_dimension.internal_name");
		
		if(sortOption == null){
			sortOption = "ASC";
		}
		
		query.orderBy("product_dimension.product_code", sortOption, sortField == null || sortField.equals("productId"));
		query.orderBy("product_dimension.internal_name", sortOption, sortField != null && sortField.equals("productName"));
		query.orderBy("_quantity", sortOption, sortField != null && sortField.equals("quantity"));
		query.orderBy("_total", sortOption, sortField != null && sortField.equals("total"));
	}
	
	private void initQuerySum() throws GenericDataSourceException, GenericEntityException, SQLException{
		String facilityId = (String) getParameter("facilityId");
		String partyId = (String) getParameter("partyId");
		String org = (String) getParameter("org");
		
		querySum = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		querySum.from("return_order_fact");
		querySum.select("sum(quantity)", "sum_quantity");
		querySum.select("sum(total)", "sum_total");
		
		querySum.join(Join.INNER_JOIN, "date_dimension", null, "return_date_dim_id = date_dimension.dimension_id");
		querySum.join(Join.INNER_JOIN, "party_dimension", "party", "party_dim_id = party.dimension_id");
		querySum.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		querySum.join(Join.INNER_JOIN, "facility_dimension", "facility", "facility_dim_id = facility.dimension_id");
		querySum.join(Join.INNER_JOIN, "party_dimension", "owner_party", "facility.owner_party_dim_id = owner_party.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("party.party_id", partyId, partyId != null);
		condition.andEQ("facility.facility_id", facilityId, facilityId != null);
		condition.andEQ("owner_party.party_id", org, org != null);
		querySum.where(condition);
		
		ResultSet resultSetSum = querySum.getResultSet();
		while (resultSetSum.next()) {
			sumQuantity = resultSetSum.getBigDecimal("sum_quantity") != null ? resultSetSum.getBigDecimal("sum_quantity") : BigDecimal.ZERO;
			sumExtPrice = resultSetSum.getBigDecimal("sum_total") != null ? resultSetSum.getBigDecimal("sum_total") : BigDecimal.ZERO;
		}
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	//data for chart
	public class ReturnChart implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
			try {
				ResultSet resultSet = query.getResultSet();
				while (resultSet.next()) {
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
	}
	
	//data for grid
	public class ReturnGrid extends ReturnResultGrid{

		public ReturnGrid() {
			addDataField("productId");
			addDataField("productName");
			addDataField("quantity");
			addDataField("total");
			addDataField("currency");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("productId", result.getString("product_code"));
				map.put("productName", result.getString("internal_name"));
				map.put("quantity", result.getBigDecimal("_quantity"));
				map.put("total", result.getBigDecimal("_total"));
				map.put("currency", result.getString("currency_id"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ReturnGrid.class.getName());
			}
			return map;
		}
	}
	
	@Override
	public Map<String, Object> execute() {
		Map<String, Object> map = super.execute();
		if(!isChart() && getParameter(INIT) != null) {
			try {
				initQuerySum();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					querySum.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
			map.put("sumQuantity", sumQuantity);
			map.put("sumExtPrice", sumExtPrice);
		}
		return map;
	}
}
