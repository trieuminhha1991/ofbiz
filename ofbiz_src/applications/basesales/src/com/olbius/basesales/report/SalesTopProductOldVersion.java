package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.query.Query;
import com.olbius.olap.AbstractOlap;
import com.olbius.olap.OlapInterface;

public class SalesTopProductOldVersion extends AbstractOlap implements OlapInterface{
	public void topProductColumn(String topProduct, String statusSales, String productStoreId, String quantity, String orderStatus, String organ) throws GenericDataSourceException, GenericEntityException, SQLException {
		Query query = new Query(getSQLProcessor());
		
//		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		String popular = "F";
		
		query.setFrom("sales_order_fact", null);
		query.addSelect("product_dimension.product_id");
		query.addSelect("product_dimension.product_name");
		query.addSelect("sum(quantity)", "Quantity");
		
		query.addInnerJoin("date_dimension", null, "sales_order_fact.order_date_dim_id = date_dimension.dimension_id" );
		query.addInnerJoin("party_dimension", null, "sales_order_fact.party_from_dim_id = party_dimension.dimension_id");
		query.addInnerJoin("product_dimension", null, "sales_order_fact.product_dim_id = product_dimension.dimension_id");
		query.addInnerJoin("product_store_dimension", null, "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id");
		query.addConditionBetweenObj("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		if (productStoreId != null){
			query.addConditionEQ("product_store_dimension.product_store_id", productStoreId);
		}
		query.addConditionEQ("sales_order_fact.order_status", orderStatus);
		query.addConditionEQ("party_dimension.party_id", organ);
		query.addCondition("sales_order_fact.return_id is null");
		query.addGroupBy("product_dimension.dimension_id");
		if(statusSales.equals(popular)){
			query.addOrderBy("Quantity DESC");
		}else{
			query.addOrderBy("Quantity ASC");
		}
		query.limit(new Long(topProduct));
		
		ResultSet resultSet = query.getResultSet();
		
		while(resultSet.next()) {
			String _key = resultSet.getString("product_id");
			if(yAxis.get(quantity) == null) {
				yAxis.put(quantity, new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity1 = resultSet.getBigDecimal("Quantity");
			yAxis.get(quantity).add(quantity1);
		}
	} 
}
