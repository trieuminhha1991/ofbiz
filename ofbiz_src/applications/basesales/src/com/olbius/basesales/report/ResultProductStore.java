package com.olbius.basesales.report;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ResultProductStore {
	private final OlbiusQuery query;
	
	private SQLProcessor processor;
	
	public ResultProductStore(SQLProcessor processor) {
		this.processor = processor;
		query = (OlbiusQuery) new OlbiusQuery(processor).select("DISTINCT(product_id), product_code, internal_name")
				.from("product_dimension")
				.join(Join.INNER_JOIN, "sales_order_fact", null, "sales_order_fact.product_dim_id = product_dimension.dimension_id")
				.where(Condition.make("product_id is NOT NULL").and(Condition.make("sales_order_fact.quantity is NOT NULL")))
				.groupBy("product_dimension.dimension_id").orderBy("product_id DESC");
	}
	
	public List<Map<String, String>> getListResultStore() {
		List<Map<String, String>> list = FastList.newInstance();
		
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				 Map<String, String> tmp = FastMap.newInstance();
				 tmp.put("product_id", resultSet.getString("product_id"));
				 tmp.put("product_code", resultSet.getString("product_code"));
				 tmp.put("internal_name", resultSet.getString("internal_name"));
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
		
//		try {
//			ResultSet resultSet = query.getResultSet();
//			while(resultSet.next()) {
//				list.add(resultSet.getString("product_code"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(processor != null) {
//				 try {
//					processor.close();
//				} catch (GenericDataSourceException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
		return list;
	}
	
	public static Map<String, Object> getListResultStore(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		ResultProductStore type = new ResultProductStore(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listResultStore", type.getListResultStore());
		return result;
	}
	
}
