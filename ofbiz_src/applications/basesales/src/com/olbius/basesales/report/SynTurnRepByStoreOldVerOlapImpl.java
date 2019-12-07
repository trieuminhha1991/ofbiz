package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SynTurnRepByStoreOldVerOlapImpl extends AbstractOlap{
	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String ORGANIZATION = "ORGANIZATION";
	public static final String CONT = "CONT";
	
	private OlbiusQuery query;
	
//	private List<String> type;
//	
//	private List<String> getType() {
//		if(type == null) {
//			ResultProductStore2 enumType = new ResultProductStore2(getSQLProcessor());
//			type = enumType.getListResultStore2();
//		}
//		return type;
//	}
	
	private List<Map<String,String>> type;
	
	private List<Map<String,String>> getType() {
		if(type == null) {
			ResultProductStore2 enumType = new ResultProductStore2(getSQLProcessor());
			type = enumType.getListResultStore2();
		}
		return type;
	}
	
	private void initQuery() {
		OlbiusQuery queryTmp2 = new OlbiusQuery();
	
		List<Object> status = (List<Object>) getParameter(ORDER_STATUS);
		String organization = (String) getParameter(ORGANIZATION);
		
		Condition condition = new Condition();
		
		queryTmp2.select("product_dimension.product_id").select("product_dimension.product_code")
		.select("sum(quantity)", "Quantity")
		.select("product_dimension.internal_name")
		.select("sum(total)", "Total")
		.select("product_store_dimension.product_store_id", "store_id")
		.select("sales_order_fact.party_from_dim_id", "region")
		.select("sales_order_fact.order_status", "status")
		.from("sales_order_fact")
		.join(Join.LEFT_OUTER_JOIN, "product_dimension", null, "sales_order_fact.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", null, "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_store_dimension", null, "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.promo_dim_id")
		.where(condition)
		.groupBy("product_dimension.dimension_id")
		.groupBy("product_store_dimension.dimension_id")
		.groupBy("region");
		queryTmp2.groupBy("status");
		queryTmp2.orderBy("product_store_dimension.dimension_id");
		
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		condition.and(Condition.makeEQ("sales_order_fact.order_status", "ORDER_COMPLETED"));
		condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
		condition.and(Condition.make("product_promo_dimension.product_promo_id IS NULL"));
		
		query = new OlbiusQuery(getSQLProcessor());
		
		query.from(queryTmp2, "TMP")
		.select("TMP.product_id").select("TMP.product_code").select("TMP.internal_name").select("TMP.region").select("party_dimension.party_id").select("TMP.status")
		.join(Join.INNER_JOIN, "party_dimension", null, "TMP.region = party_dimension.dimension_id")
		.where(Condition.makeEQ("party_dimension.party_id", organization))
		.groupBy("TMP.product_id").groupBy("TMP.product_code").groupBy("TMP.internal_name").groupBy("TMP.region").groupBy("party_dimension.party_id").groupBy("TMP.status").orderBy("TMP.product_id");
		
//		List<String> type = getType();
//		
//		for(String s: type) {
//			query.select("sum(case when TMP.store_id = '"+s+"' then TMP.Quantity else 0 end)", s);
//		}
		List<Map<String,String>> type = getType();
		
//		for(String s: type) {
//			query.select("sum(case when TMP.store_id = '"+s+"' then TMP.Quantity else 0 end)", s);
//		}
		
		for(int i = 0; i < type.size(); i++) {
			String storeId = (String) type.get(i).get("product_store_id");
			query.select("sum(case when TMP.store_id = '"+storeId+"' then TMP.Quantity else 0 end)", storeId);
		}
		
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	public class TuReSto extends ReturnResultGrid{

		public TuReSto() {
			addDataField("stt");
//			addDataField("storeId");
//			addDataField("storeName");
			addDataField("productId");
			addDataField("productName");
			addDataField("status");
//			List<String> type = getType();
//			for(String s: type) {
//				addDataField(s);
//			}
			List<Map<String,String>> type = getType();
//			for(String s: type) {
//				addDataField(s);
//			}
			for(int i = 0; i < type.size(); i++) {
				String storeId = (String) type.get(i).get("product_store_id");			
				addDataField(storeId);
			}
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			Map<String, ? extends Object> cont = (Map<String, ? extends Object>) getParameter(CONT);
			Locale locale = (Locale)cont.get("locale");
			try {
//				map.put("storeId", result.getString("store_id"));
//				map.put("storeName", result.getString("store_name"));
				map.put("productId", result.getString("product_code"));
				map.put("productName", result.getString("internal_name"));
				String sttDes = result.getString("status");
				GenericValue stt = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", sttDes), false);
				String sttResult = (String) stt.get("description", locale);
				map.put("status", sttResult);
//				List<String> type = getType();
//				for(String s: type) {
//					map.put(s, result.getBigDecimal(s));
//				}
				String hyphens = "-";
				BigDecimal zero = new BigDecimal(0);
//				List<String> type = getType();
//				for(String s: type) {
//					BigDecimal valueResult = result.getBigDecimal(s);
//					if(!zero.equals(valueResult)){
//						map.put(s, valueResult);
//					} else {
//						map.put(s, hyphens);
//					}
//				}
				List<Map<String,String>> type = getType();	
				for(int i = 0; i < type.size(); i++) {
					String storeId = (String) type.get(i).get("product_store_id");
					BigDecimal valueResult = result.getBigDecimal(storeId);
					if(!zero.equals(valueResult)){
						map.put(storeId, valueResult);
					} else {
						map.put(storeId, hyphens);
					}
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), TuReSto.class.getName());
			}
			return map;
		}
	}
	
}
