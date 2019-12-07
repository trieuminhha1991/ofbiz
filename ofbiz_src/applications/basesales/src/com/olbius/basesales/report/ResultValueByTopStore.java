package com.olbius.basesales.report;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ResultValueByTopStore extends TypeOlap{
	private final OlbiusQuery query;
	private SQLProcessor processor;
	
	public ResultValueByTopStore(SQLProcessor processor, String organization) {
		this.processor = processor;
		Date curDate = new Date(System.currentTimeMillis());
		Timestamp curTime = new Timestamp(curDate.getTime());
		Timestamp startMonth = UtilDateTime.getMonthStart(curTime);
//		Date startMonthDate = new Date(startMonth.getTime());
		Condition condition = new Condition();
		query = (OlbiusQuery) new OlbiusQuery(processor)
				.select("product_store_dimension.store_name", "store_name")
				.select("sum(sales_order_fact.total)", "value_total").from("sales_order_fact")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_from_dim_id")
				.join(Join.INNER_JOIN, "product_store_dimension", "product_store_dimension.dimension_id = sales_order_fact.product_store_dim_id")
				.where(condition).groupBy("product_store_dimension.dimension_id").orderBy("value_total", OlbiusQuery.DESC).limit(1);
				
				condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
				condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
				condition.and(Condition.makeEQ("party_dimension.party_id", organization));
				condition.and(Condition.makeEQ("sales_order_fact.order_status", "ORDER_COMPLETED"));
				
				
//		select enumeration_dimension.description, sum(total) as value_total from sales_order_fact
//		inner join date_dimension on order_date_dim_id = date_dimension.dimension_id
//		inner join party_dimension on party_dimension.dimension_id = sales_order_fact.party_from_dim_id
//		inner join  on 
//		where (date_dimension.date_value BETWEEN '2015-11-01' AND '2015-11-30')
//			and (sales_order_fact.return_id isnull) and (party_dimension.party_id = 'MB')
//		group by enumeration_dimension.dimension_id order by value_total desc limit 1
	}
	
	public List<String> getTopStore() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = query.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("store_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(processor != null) {
				 try {
					processor.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
		}
		return list2;
	}
	
	public List<String> getTopValueTotal() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("value_total"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(processor != null) {
				 try {
					processor.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public static Map<String, Object> getTopValueReport(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultValueByTopStore type = new ResultValueByTopStore(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listTopValue", type.getTopValueTotal());
		result.put("listTopStore", type.getTopStore());
		
		return result;
	}
	
}