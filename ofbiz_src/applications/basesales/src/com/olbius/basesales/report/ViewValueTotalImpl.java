package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ViewValueTotalImpl extends AbstractOlap {
	
	public static final String ORG = "ORG";

	private OlbiusQuery query;
	
	private void initQuery() {
		String organ = (String) getParameter(ORG);
		
		query = new OlbiusQuery(getSQLProcessor());
		
		Date curDate = new Date(System.currentTimeMillis());
		
		Timestamp curTime = new Timestamp(curDate.getTime());
		
		Timestamp startMonth = UtilDateTime.getMonthStart(curTime);
		
		Date startMonthDate = new Date(startMonth.getTime());
		

		Condition condition = new Condition();
		
		query.select("sum(sales_order_fact.total)", "value_total").from("sales_order_fact")
		.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_from_dim_id")
		.where(condition);
		
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonthDate), getSqlDate(curTime)));
		condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
		condition.and(Condition.makeEQ("party_dimension.party_id", organ));
		
		
		ResultSet resultSet = null;
		try {
			resultSet = query.getResultSet();
		} catch (GenericEntityException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
		try {
			while(resultSet.next()) {
				list.add(resultSet.getString("value_total"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		while(resultSet.next()) {
//			String _key = resultSet.getString("product_id");
////			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", _key), false);
////			String internalName = product.getString("internalName");
//			if(yAxis.get(quantity) == null) {
//				yAxis.put(quantity, new ArrayList<Object>());
//			}
//			xAxis.add(_key);
//			BigDecimal quantity1 = resultSet.getBigDecimal("Quantity");
//			yAxis.get(quantity).add(quantity1);
//		}
		
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
//		public List<String> getValueFinal(OlbiusQueryInterface query) {
//			
//			try {
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				
//			}
//			
//			return list;
//		}
		
}
