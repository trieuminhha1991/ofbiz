package com.olbius.basepos.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.InnerJoin;
import com.olbius.bi.olap.query.join.Join;

public class SalesOrderOlapImpl extends OlbiusBuilder{
	public SalesOrderOlapImpl(Delegator delegator) {
		super(delegator);
	}

	private OlbiusQuery query;
	private OlbiusQuery querySum;
	private String dateType;
	private String reportType;
	public static final String TYPE = "TYPE";
	public static final String SORT_FIELD = "SORT_FIELD";
	public static final String SORT_OPTION = "SORT_OPTION";
	
	BigDecimal sumQuantity = BigDecimal.ZERO;
	BigDecimal sumExtPrice = BigDecimal.ZERO;
	
	@Override
	public void prepareResultGrid() {
		addDataField("currency", "currency_id");
		addDataField("quantity", "_quantity");
		addDataField("extPrice", "_ext_price");
		dateType = (String) getParameter(DATE_TYPE);
		dateType = getDateType(dateType);
		reportType = (String) getParameter("reportType");
		if (reportType.equals("salesDetail")){
			addDataField("date", dateType);
			addDataField("orderId", "order_id");
		} else if (reportType.equals("departmentSummary")){
			addDataField("categoryId", "category_id");
			addDataField("categoryName", "category_name");
		} else if (reportType.equals("itemSummary")){
			addDataField("categoryId", "category_id");
			addDataField("categoryName", "category_name");
			addDataField("productId", "product_code");
			addDataField("productName", "product_name");
		} else if (reportType.equals("customerSummary")){
			addDataField("partyId", "party_id");
			addDataField("partyName", "full_name");
		} else if (reportType.equals("detailCustomer")){
			addDataField("partyId", "party_id");
			addDataField("partyName", "full_name");
			addDataField("productId", "product_code");
			addDataField("productName", "product_name");
		}
	}
	
	private void initQuery() {
		reportType = (String) getParameter("reportType");
		dateType = (String) getParameter(DATE_TYPE);
		String sortField = (String) getParameter(SORT_FIELD);
		String sortOption = (String) getParameter(SORT_OPTION);
		String productId = (String) getParameter("productId");
		String categoryId = (String) getParameter("categoryId");
		String productStoreId = (String) getParameter("productStoreId");
		String partyId = (String) getParameter("partyId");
		String org = (String) getParameter("org");
		dateType = getDateType(dateType);
		
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("sales_order_fact", "sof")
			.select("date_dimension.".concat(dateType), reportType.equals("salesDetail"))
			.select("order_id", reportType.equals("salesDetail"))
			.select("category_dimension.category_id", reportType.equals("departmentSummary")||reportType.equals("itemSummary"))
			.select("category_dimension.category_name", reportType.equals("departmentSummary")||reportType.equals("itemSummary"))
			.select("product_dimension.product_code", reportType.equals("itemSummary")||reportType.equals("detailCustomer"))
			.select("product_dimension.product_name", reportType.equals("itemSummary")||reportType.equals("detailCustomer"))
			.select("party_to.party_id", reportType.equals("customerSummary")||reportType.equals("detailCustomer"))
			.select("coalesce(party_to.last_name,'') || ' ' || coalesce(party_to.middle_name,'') || ' ' || coalesce(party_to.first_name,'')", "full_name", reportType.equals("customerSummary")||reportType.equals("detailCustomer"))
			.select("currency_dimension.currency_id").select("sum(quantity)", "_quantity")
			.select("sum(total)", "_ext_price");

		
		query.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_store_dimension", null, "product_store_dim_id = product_store_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "party_from", "party_from_dim_id = party_from.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "party_to", "party_to_dim_id = party_to.dimension_id", 
				reportType.equals("salesDetail")||reportType.equals("customerSummary")||reportType.equals("detailCustomer"));
		query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id",
				reportType.equals("itemSummary")||reportType.equals("salesDetail")||reportType.equals("detailCustomer"));
		query.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pcr.product_dim_id = sof.product_dim_id",
				!reportType.equals("salesDetail"));
		query.join(Join.INNER_JOIN, "date_dimension", "from_date", "pcr.from_dim_date = from_date.dimension_id",
				!reportType.equals("salesDetail"));
		query.join(Join.INNER_JOIN, "date_dimension", "thru_date", "pcr.thru_dim_date = thru_date.dimension_id",
				!reportType.equals("salesDetail"));
		
		Join category = new InnerJoin();
		Condition condCate = new Condition();
		condCate.and("pcr.category_dim_id = category_dimension.dimension_id");
		condCate.andEQ("category_dimension.category_type", "CATALOG_CATEGORY");
		condCate.and("date_dimension.date_value >= from_date.date_value");
		condCate.and(Condition.make("thru_date.date_value IS NULL").or("thru_date.date_value >= date_dimension.date_value"));
		category.table("category_dimension").on(condCate);
		query.join(category, !reportType.equals("salesDetail"));
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
//		condition.andEQ("enumeration_dimension.enum_id", "SMCHANNEL_POS");
		condition.andEQ("sof.order_status", "ORDER_COMPLETED");
		condition.and("sof.return_id IS NULL");
		condition.andEQ("product_code", productId, productId != null && reportType.equals("salesDetail"));
		condition.andEQ("category_id", categoryId, categoryId != null && 
				(reportType.equals("itemSummary")||reportType.equals("customerSummary")||reportType.equals("detailCustomer")));
		condition.andEQ("product_store_id", productStoreId, !"all".equals(productStoreId));
		condition.andEQ("party_to.party_id", partyId, partyId != null && 
				(reportType.equals("salesDetail")||reportType.equals("detailCustomer")));
		condition.andEQ("party_from.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("currency_dimension.currency_id")
			.groupBy("date_dimension.".concat(dateType), dateType != null && reportType.equals("salesDetail"))
			.groupBy("order_id", reportType.equals("salesDetail"))
			.groupBy("category_dimension.category_id", reportType.equals("departmentSummary")||reportType.equals("itemSummary"))
			.groupBy("category_dimension.category_name", reportType.equals("departmentSummary")||reportType.equals("itemSummary"))
			.groupBy("product_dimension.product_code", reportType.equals("itemSummary")||reportType.equals("detailCustomer"))
			.groupBy("product_dimension.product_name", reportType.equals("itemSummary")||reportType.equals("detailCustomer"))
			.groupBy("party_to.party_id", reportType.equals("customerSummary")||reportType.equals("detailCustomer"))
			.groupBy("party_to.first_name", reportType.equals("customerSummary")||reportType.equals("detailCustomer"))
			.groupBy("party_to.middle_name", reportType.equals("customerSummary")||reportType.equals("detailCustomer"))
			.groupBy("party_to.last_name", reportType.equals("customerSummary")||reportType.equals("detailCustomer"));
		
		if(sortOption == null){
			sortOption = "ASC";
		}
		
		query.orderBy("date_dimension.".concat(dateType), sortOption, dateType != null && (sortField == null || sortField.equals("date")) && reportType.equals("salesDetail"))
		.orderBy("order_id",  sortOption, sortField != null && sortField.equals("orderId") && reportType.equals("salesDetail"))
		.orderBy("_quantity",  sortOption, sortField != null && sortField.equals("quantity"))
		.orderBy("_ext_price",  sortOption, sortField != null && sortField.equals("extPrice"))
		.orderBy("category_dimension.category_id",  sortOption, (sortField == null || sortField.equals("categoryId")) && reportType.equals("departmentSummary"))
		.orderBy("category_dimension.category_name",  sortOption, sortField != null && sortField.equals("categoryName") && (reportType.equals("departmentSummary")||reportType.equals("itemSummary")))
		.orderBy("product_dimension.product_code",  sortOption, sortField != null && sortField.equals("productId") && (reportType.equals("itemSummary")||reportType.equals("detailCustomer")))
		.orderBy("product_dimension.product_name",  sortOption, sortField != null && sortField.equals("productName") && (reportType.equals("itemSummary")||reportType.equals("detailCustomer")))
		.orderBy("party_to.party_id",  sortOption, (sortField == null || sortField.equals("partyId")) && (reportType.equals("customerSummary")||reportType.equals("detailCustomer")))
		.orderBy("party_to.last_name",  sortOption, sortField != null && sortField.equals("partyName") && (reportType.equals("customerSummary")||reportType.equals("detailCustomer")));
	}
	
	private void initQuerySum() throws GenericDataSourceException, GenericEntityException, SQLException{
		reportType = (String) getParameter("reportType");
		String productId = (String) getParameter("productId");
		String partyId = (String) getParameter("partyId");
		String productStoreId = (String) getParameter("productStoreId");
		String org = (String) getParameter("org");
		
		querySum = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		querySum.from("sales_order_fact", "sof")
			.select("sum(quantity)", "sum_quantity").select("sum(total)", "sum_ext_price");
		
		querySum.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		querySum.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		querySum.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id");
		querySum.join(Join.INNER_JOIN, "party_dimension", "party_from", "party_from_dim_id = party_from.dimension_id");
		querySum.join(Join.INNER_JOIN, "party_dimension", "party_to", "party_to_dim_id = party_to.dimension_id");
		querySum.join(Join.INNER_JOIN, "product_store_dimension", null, "product_store_dim_id = product_store_dimension.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
//		condition.andEQ("enumeration_dimension.enum_id", "SMCHANNEL_POS");
		condition.andEQ("sof.order_status", "ORDER_COMPLETED");
		condition.and("sof.return_id IS NULL");
		condition.andEQ("product_code", productId, productId != null && reportType.equals("salesDetail"));
		condition.andEQ("product_store_id", productStoreId, !"all".equals(productStoreId));
		condition.andEQ("party_to.party_id", partyId, partyId != null && 
				(reportType.equals("salesDetail")||reportType.equals("detailCustomer")));
		condition.andEQ("party_from.party_id", org, org != null);
		querySum.where(condition);
		
		ResultSet resultSetSum = querySum.getResultSet();
		while (resultSetSum.next()) {
			sumQuantity = resultSetSum.getBigDecimal("sum_quantity") != null ? resultSetSum.getBigDecimal("sum_quantity") : BigDecimal.ZERO;
			sumExtPrice = resultSetSum.getBigDecimal("sum_ext_price") != null ? resultSetSum.getBigDecimal("sum_ext_price") : BigDecimal.ZERO;
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
	public class SalesOrderChart implements OlapResultQueryInterface {
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
