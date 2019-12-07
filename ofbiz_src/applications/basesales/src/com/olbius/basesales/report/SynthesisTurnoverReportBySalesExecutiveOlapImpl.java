package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SynthesisTurnoverReportBySalesExecutiveOlapImpl extends OlbiusBuilder{
	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String CHANNEL = "CHANNEL";
	public static final String PARTY = "PARTY";
	public static final String FLAGSM = "FLAGSM";
	public static final String DESCRIPTION = "DESCRIPTION";
	
	private OlbiusQuery query;
	
	public SynthesisTurnoverReportBySalesExecutiveOlapImpl(Delegator delegator) {
		super(delegator);
	}
	
	private List<Map<String,String>> type;
	
	private List<Map<String,String>> getType() {
		if(type == null) {
			ResultProductStore enumType = new ResultProductStore(getSQLProcessor());
			type = enumType.getListResultStore();
		}
		return type;
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("staffId", "party_id", new ReturnResultCallback<String>() {
			@Override public String get(Object object) { if(UtilValidate.isNotEmpty(object)){ return (String) object; }else{ return "-"; }}
		});
		addDataField("staffName", "full_name", new ReturnResultCallback<String>() {
			@Override public String get(Object object) { if(UtilValidate.isNotEmpty(object) && !"  ".equals(object) ){ return (String) object;}else{ return "-"; }}
		});
		addDataField("volumeTotal", "turnoverVolumeTotal");
		addDataField("valueTotal", "turnoverTotal");
		addDataField("valueTotalVAT", "turnoverTotalVAT");
		List<Map<String,String>> type = getType();
		for(int i = 0; i < type.size(); i++) {
			String productId = (String) type.get(i).get("product_id");			
			addDataField(productId+"_q", productId+"_q", new ReturnResultCallback<Object>() { 
				@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
					return (BigDecimal) object;}else{return "-";}}
			});
			addDataField(productId+"_t", productId+"_t", new ReturnResultCallback<Object>() { 
				@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
					return (BigDecimal) object;}else{return "-";}}
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		OlbiusQuery queryTmp2 = new OlbiusQuery();
		OlbiusQuery queryJoin1 = new OlbiusQuery();
		
		queryTmp2 = new OlbiusQuery(getSQLProcessor());
		queryJoin1 = new OlbiusQuery(getSQLProcessor());
		
		List<Object> status = (List<Object>) getParameter(ORDER_STATUS);
		String channel = (String) getParameter(CHANNEL);
		String partyId = (String) getParameter("partyId");
		String flagSM = (String) getParameter(FLAGSM);
		List<Object> salesmanList = (List<Object>) getParameter(PARTY);
		Condition condition = new Condition();
		
		queryTmp2.select("product_dimension.product_id")
		.select("product_dimension.product_code")
		.select("sum(quantity)", "Quantity")
		.select("product_dimension.product_name")
		.select("sum(total)", "Total")
		.select("sales_order_fact.sale_executive_party_dim_id")
		.select("party_dimension.party_id")
		.select("COALESCE(party_dimension.last_name, '') || ' ' || COALESCE(party_dimension.middle_name, '') || ' ' || COALESCE(party_dimension.first_name, '') AS full_name")
		.from("sales_order_fact")
		.join(Join.LEFT_OUTER_JOIN, "product_dimension", null, "sales_order_fact.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", null, "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.sale_executive_party_dim_id = party_dimension.dimension_id");
		if(UtilValidate.isNotEmpty(flagSM)){
			queryTmp2.join(Join.INNER_JOIN, "party_dimension", "salesman", "sales_order_fact.sale_executive_party_dim_id = salesman.dimension_id");
		}
		queryTmp2.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
		.where(condition)
		.groupBy("product_dimension.dimension_id")
		.groupBy("party_dimension.dimension_id")
		.groupBy("sales_order_fact.sale_executive_party_dim_id")
		.orderBy("party_dimension.party_id");
		
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if(UtilValidate.isNotEmpty(status) && status.toArray().length == 1 && "all".equals(status.get(0))){
			
		} else {
			condition.and(Condition.makeIn("sales_order_fact.order_status", status, !"all".equals(status)));
		}
		if(!"all".equals(channel)){
			condition.and(Condition.makeEQ("enumeration_dimension.enum_id", channel, channel != null));
		}
		if(UtilValidate.isNotEmpty(partyId)){
			condition.and(Condition.makeEQ("party_dimension.party_id", partyId, partyId != null));
		}
		if(UtilValidate.isNotEmpty(flagSM)){
			condition.andIn("salesman.party_id", salesmanList);
		}
		condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("sales_order_fact.total is not null");
		
		query = new OlbiusQuery(getSQLProcessor());
		
		queryJoin1.select("party_dimension.party_id").select("SUM(total) AS Total1").select("SUM(total + tax) AS TotalVAT").select("SUM(quantity) AS Quantity1").select("sales_order_fact.sale_executive_party_dim_id")
		.from("sales_order_fact").join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.sale_executive_party_dim_id = party_dimension.dimension_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
		.where(condition)
		.groupBy("party_dimension.dimension_id")
		.groupBy("sales_order_fact.sale_executive_party_dim_id")
		.orderBy("party_dimension.party_id");
		
		query.from(queryTmp2, "TMP")
		.select("TMP.sale_executive_party_dim_id").select("TMP.party_id").select("TMP.full_name")
		.select("JOIN1.Total1", "turnoverTotal").select("JOIN1.Quantity1", "turnoverVolumeTotal").select("JOIN1.TotalVAT", "turnoverTotalVAT")
		.join(Join.INNER_JOIN, queryJoin1, "JOIN1", "TMP.sale_executive_party_dim_id = JOIN1.sale_executive_party_dim_id")
		.where(Condition.make("TMP.party_id is not null"))
		.groupBy("TMP.sale_executive_party_dim_id").groupBy("TMP.party_id").groupBy("turnoverTotal").groupBy("turnoverVolumeTotal").groupBy("turnoverTotalVAT")
		.groupBy("TMP.full_name").orderBy("TMP.party_id");

		List<Map<String,String>> type = getType();
		
		for(int i = 0; i < type.size(); i++) {
			String productId = (String) type.get(i).get("product_id");
			query.select("sum(case when TMP.product_id = '"+productId+"' then TMP.Quantity else 0 end)", '"' + productId +"_q"+ '"');
			query.select("sum(case when TMP.product_id = '"+productId+"' then TMP.Total else 0 end)", '"' + productId +"_t"+ '"');
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}
