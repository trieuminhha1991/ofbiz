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

public class ResultValueTotal extends TypeOlap{
	private final OlbiusQuery query;
	private final OlbiusQuery query2;
	private final OlbiusQuery query3;
	private final OlbiusQuery query4;
	private SQLProcessor processor;
	
	public ResultValueTotal(SQLProcessor processor, String organization) {
		this.processor = processor;
		Date curDate = new Date(System.currentTimeMillis());
		Timestamp curTime = new Timestamp(curDate.getTime());
		Timestamp startMonth = UtilDateTime.getMonthStart(curTime);
//		Date startMonthDate = new Date(startMonth.getTime());
		Condition condition = new Condition();
		query = (OlbiusQuery) new OlbiusQuery(processor)
				.select("sum(sales_order_fact.total)", "value_total").from("sales_order_fact")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_from_dim_id")
				.where(condition);
				
				condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
				condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
				condition.and(Condition.makeEQ("sales_order_fact.order_status", "ORDER_COMPLETED"));
				condition.and(Condition.makeEQ("party_dimension.party_id", organization));
			
//		query2 = (OlbiusQuery) new OlbiusQuery(processor)
//				.select("count(DISTINCT order_id) as order_quantity").from("sales_order_fact")
//				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
//				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_from_dim_id")
//				.where(condition);
				
		query2 = (OlbiusQuery) new OlbiusQuery(processor)
				.select("count( distinct OH.order_id) as order_quantity").from("order_header", "OH")
				.join(Join.INNER_JOIN, "product_store", "PS", "PS.product_store_id = OH.product_store_id")
				.where(Condition.makeEQ("OH.order_type_id", "SALES_ORDER").andEQ("OH.status_id", "ORDER_COMPLETED").andEQ("PS.pay_to_party_id", organization)
						.and("OH.order_date between '" + getSqlDate(startMonth) + " 00:00:00.0' and '" + getSqlDate(curTime) + " 23:59:59.0'"));
		
//		select  from  
//		inner join party_dimension on party_dimension.dimension_id = sales_order_fact.sale_executive_party_dim_id
//		inner join date_dimension on date_dimension.dimension_id = sales_order_fact.order_date_dim_id
//		where party_dimension.party_id = 'OLBMBSA001101' and 
//			date_dimension.date_value between '2016-01-01' and '2016-01-21'
		query3 =(OlbiusQuery) new OlbiusQuery(processor)
				.select("sum(total)", "value_").from("sales_order_fact")
				.join(Join.INNER_JOIN, "date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_from_dim_id");
		
//		select * from public. as 
//		inner join public. as  on 
//		where order_type_id = 'SALES_ORDER' and ( between '2016-01-01'  and '2016-04-01') 
//			and  and ordr.party_id = 'OLBSAGT0009'
		query4 = (OlbiusQuery) new OlbiusQuery(processor)
				.select("sum(ordh.grand_total)", "value_total").from("order_header", "ordh")
				.join(Join.INNER_JOIN, "order_role", "ordr", "ordr.order_id = ordh.order_id and ordr.role_type_id = 'SALES_EXECUTIVE'")
				.where(Condition.make("order_type_id = 'SALES_ORDER'").and("ordh.status_id = 'ORDER_COMPLETED'").and("ordh.order_date between '"));
		
				
	}
	
	public List<String> getValueTotal() {
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
	
	public List<String> getOrderQuantity() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = query2.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("order_quantity"));
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
	
	public static Map<String, Object> getValueReport(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultValueTotal type = new ResultValueTotal(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getValueTotal());
		return result;
	}
	
	public static Map<String, Object> getOrderQuantity(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultValueTotal type = new ResultValueTotal(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderQuantity", type.getOrderQuantity());
		return result;
	}
	
}