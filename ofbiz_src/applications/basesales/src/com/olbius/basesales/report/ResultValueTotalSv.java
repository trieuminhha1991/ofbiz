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

import javolution.util.FastList;
import javolution.util.FastMap;

public class ResultValueTotalSv extends TypeOlap{
	private final OlbiusQuery query;
	private final OlbiusQuery query2;
	private SQLProcessor processor;
	
	public ResultValueTotalSv(SQLProcessor processor, String partyId, Timestamp fromDate1, Timestamp thruDate1) {
		this.processor = processor;
		query = (OlbiusQuery) new OlbiusQuery(processor)
				.select("sum(ordh.grand_total)", "value_total").from("order_header", "ordh")
				.join(Join.INNER_JOIN, "order_role", "ordr", "ordr.order_id = ordh.order_id and ordr.role_type_id = 'SALES_EXECUTIVE'")
				.where(Condition.make("order_type_id = 'SALES_ORDER'").and("ordh.status_id = 'ORDER_COMPLETED'")
						.and("ordh.order_date between '" + getSqlDate(fromDate1) + " 00:00:00.0' and '" + getSqlDate(thruDate1) + " 23:59:59.0'")
						.andEQ("ordr.party_id", partyId));
		
		query2 = (OlbiusQuery) new OlbiusQuery(processor)
				.select(" count(distinct order_id) as order_volume, sum(sof.total) as value_total").from("sales_order_fact", "sof")
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = sof.party_from_dim_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "ppd", "ppd.dimension_id = sof.discount_dim_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "ppd2", "ppd2.dimension_id = sof.promo_dim_id")
				.where(Condition.make("ppd.product_promo_id is null").and("ppd2.product_promo_id is null").andEQ("pd.party_id", partyId).andEQ("sof.order_status", "ORDER_COMPLETED"));
	}
	
	public List<String> getValueTotalSv() {
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
	
	public List<Map<String, String>> getTurnoverValue() {
		List<Map<String, String>> list = FastList.newInstance();
		
		try {
			ResultSet resultSet = query2.getResultSet();
			while(resultSet.next()) {
				 Map<String, String> tmp = FastMap.newInstance();
				 tmp.put("count_order", resultSet.getString("order_volume"));
				 tmp.put("value_total", resultSet.getString("value_total"));
				 list.add(tmp);
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
	
	public static Map<String, Object> getValueSv(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		ResultValueTotalSv type = new ResultValueTotalSv(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), partyId, fromDate, thruDate);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getValueTotalSv());
		return result;
	}
	
	public static Map<String, Object> getTurnoverDistributor(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		ResultValueTotalSv type = new ResultValueTotalSv(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), partyId, fromDate, thruDate);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listTurnoverValue", type.getTurnoverValue());
		return result;
	}
}