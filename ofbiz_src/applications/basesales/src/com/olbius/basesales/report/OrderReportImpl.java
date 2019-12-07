package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;
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
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class OrderReportImpl extends AbstractOlap{
	public static final String ORG = "ORG";
	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String CHANNEL = "CHANNEL";
	public static final String DEL_STATUS = "DEL_STATUS";
	public static final String FROM_DATE_2 = "FROM_DATE_2";
	public static final String THRU_DATE_2 = "THRU_DATE_2";
	public static final String CONT = "CONT";
	public static final String FILTER_DATE = "FILTER_DATE";
	public static final String FLAGRO = "FLAGRO";
	
	private OlbiusQuery query;
	private OlbiusQuery fromQuery;
	private OlbiusQuery joinQuery;
	private List<Map<String,String>> type;
	
	private List<Map<String,String>> getType() {
		if(type == null) {
			ResultProductStore enumType = new ResultProductStore(getSQLProcessor());
			type = enumType.getListResultStore();
		}
		return type;
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		String organ = (String) getParameter(ORG);
		List<Object> status = (List<Object>) getParameter(ORDER_STATUS);
		List<Object> channel = (List<Object>) getParameter(CHANNEL);
		Date fromDate2 = (Date) getParameter(FROM_DATE_2);
		Date thruDate2 = (Date) getParameter(THRU_DATE_2);
		String flagRO = (String) getParameter(FLAGRO);
		String statusCancel = null;
		if(UtilValidate.isNotEmpty(status) && "all".equals(status.get(0))){
			statusCancel = (String) status.get(1);
		}
		
		query = new OlbiusQuery(getSQLProcessor());
		fromQuery = new OlbiusQuery(getSQLProcessor());
		joinQuery = new OlbiusQuery(getSQLProcessor());
		
		Condition fromCondition = new Condition();
		
		fromQuery.select("DISTINCT sales_order_fact.order_id, product_dimension.product_id, SUM(quantity) AS volumeTotal, product_dimension.product_name, SUM(total) AS valueTotal, product_dimension.product_code, sales_order_fact.call_center_party_dim_id as cace_dim, sales_order_fact.sale_executive_party_dim_id as saex_dim, sales_order_fact.party_to_dim_id as cus_dim, sales_order_fact.order_date as order_date, sales_order_fact.actual_arrival_date as delivery_date, sales_order_fact.a_delivery_date as delivery_date_s, sales_order_fact.ship_after_date as ship_after_date, sales_order_fact.ship_before_date as ship_before_date, enumeration_dimension.description as channel_name, sales_order_fact.order_addr as o_address, sales_order_fact.country_dim_id as o_country, sales_order_fact.state_dim_id as o_state, sales_order_fact.district_dim_id as o_district, sales_order_fact.ward_dim_id as o_ward,  sales_order_fact.road_dim_id as o_road, sales_order_fact.order_status as o_status")
		.from("sales_order_fact")
		.join(Join.LEFT_OUTER_JOIN, "product_dimension", null, "sales_order_fact.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "sales_order_fact.order_date_dim_id = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd2", "sales_order_fact.a_delivery_date_dim_id = dd2.dimension_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", null, "product_promo_dimension.dimension_id = sales_order_fact.promo_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "ORGAN", "ORGAN.dimension_id = sales_order_fact.party_from_dim_id")
		.where(fromCondition)
		.groupBy("product_dimension.dimension_id").groupBy("cace_dim")
		.groupBy("saex_dim").groupBy("cus_dim").groupBy("sales_order_fact.order_id").groupBy("delivery_date").groupBy("delivery_date_s")
		.groupBy("order_date").groupBy("channel_name").groupBy("o_address").groupBy("o_country").groupBy("ship_after_date").groupBy("ship_before_date")
		.groupBy("o_state").groupBy("o_district").groupBy("o_ward").groupBy("o_road").groupBy("o_status")
		.orderBy("sales_order_fact.order_id");
		
//		if("RO".equals(flagRO)){
//			fromCondition.and(Condition.make("product_promo_dimension.product_promo_id NOTNULL"));
//		} else {
//		}
		fromCondition.and(Condition.make("product_promo_dimension.product_promo_id IS NULL"));
		if("RO".equals(flagRO)){
			fromCondition.and(Condition.make("sales_order_fact.return_id is NOT NULL"));
		} else {
			fromCondition.and(Condition.make("sales_order_fact.return_id IS NULL"));
		}
		
		fromCondition.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if((UtilValidate.isNotEmpty(fromDate2)) && (UtilValidate.isNotEmpty(thruDate2))){
			fromCondition.and((Condition.make("(sales_order_fact.a_delivery_date between '" + getSqlDate(fromDate2) + " 00:00:00.0' and '" + getSqlDate(thruDate2) + " 23:59:59.0')")).or(Condition.make("sales_order_fact.ship_after_date >= '" + getSqlDate(fromDate2) + " 00:00:00.0 ' AND sales_order_fact.ship_before_date <= '"+ getSqlDate(thruDate2) + " 23:59:59.0'")));
		}
		fromCondition.and(Condition.makeEQ("ORGAN.party_id", organ));
		if(UtilValidate.isNotEmpty(status)){
			fromCondition.and(Condition.makeIn("sales_order_fact.order_status", status, status != null));
		}
		if(UtilValidate.isNotEmpty(status)){
			if((status.toArray()).length == 1 && "ORDER_CANCELLED".equals(statusCancel)){
				fromCondition.and(Condition.make("sales_order_fact.order_item_status = 'ITEM_CANCELLED'"));
				fromCondition.and(Condition.make("sales_order_fact.order_status  = 'ORDER_CANCELLED'"));
			} else {
				fromCondition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
			}
		} else {
			fromCondition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
		}
		if(UtilValidate.isNotEmpty(channel)){
			fromCondition.and(Condition.makeIn("enumeration_dimension.enum_id", channel, channel != null));
		}
		
		joinQuery.select("sales_order_fact.order_id", "orderJoin").select("SUM(sales_order_fact.total)", "valueJoin")
		.select("SUM(sales_order_fact.tax)", "taxJoin")
		.from("sales_order_fact");
		if(UtilValidate.isNotEmpty(status)){
			if((status.toArray()).length == 1 && "ORDER_CANCELLED".equals(statusCancel)){
				joinQuery.where(Condition.make("sales_order_fact.order_item_status = 'ITEM_CANCELLED'"));
			} else {
				joinQuery.where(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
			}
		} else {
			joinQuery.where(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
		}
		joinQuery.groupBy("orderJoin");
		
		query.select("TMP.order_id").select("CUSTOMER.party_type_id", "cus_type").select("TMP.order_date").select("TMP.channel_name").select("TMP.delivery_date").select("TMP.o_status")
		.select("TMP.delivery_date_s").select("TMP.ship_after_date").select("TMP.ship_before_date")
		.select("JOINCACE.party_id", "cace_id")
		.select("COALESCE(JOINCACE.last_name, '' ) || ' ' || COALESCE(JOINCACE.middle_name, '') || ' ' || COALESCE(JOINCACE.first_name, '')", "cace_name")
		.select("JOINSAEX.party_id", "saex_id")
		.select("COALESCE(JOINSAEX.last_name, '' ) || ' ' || COALESCE(JOINSAEX.middle_name, '') || ' ' || COALESCE(JOINSAEX.first_name, '')", "saex_name")
		.select("CUSTOMER.party_id", "cus_id")
		.select("COALESCE(CUSTOMER.last_name, '' ) || ' ' || COALESCE(CUSTOMER.middle_name, '') || ' ' || COALESCE(CUSTOMER.first_name, '')", "per_name")
		.select("CUSTOMER.primary_address", "cus_addr").select("CUSTOMER.phone_number", "cus_phone").select("CUSTOMER.name", "cus_gr_name")
		.select("JOINVALUE.valueJoin").select("JOINVALUE.taxJoin")
		.select("COUNTRYGEO.geo_name", "order_country").select("STATEGEO.geo_name", "order_state")
		.select("DISTRICTGEO.geo_name", "order_district").select("WARDGEO.geo_name", "order_ward")
		.select("ROADGEO.geo_name", "order_road").select("TMP.o_address");
		
		List<Map<String,String>> type = getType();
		for(int i = 0; i < type.size(); i++) {
			String productId = (String) type.get(i).get("product_id");
			query.select("sum(case when TMP.product_code = '"+productId+"' then TMP.volumeTotal else 0 end)", '"' + productId + '"');
		}
		
		query.from(fromQuery, "TMP")
		.join(Join.INNER_JOIN, joinQuery, "JOINVALUE", "JOINVALUE.orderJoin = TMP.order_id")
		.join(Join.INNER_JOIN, "party_dimension", "JOINCACE", "TMP.cace_dim = JOINCACE.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "JOINSAEX", "TMP.saex_dim = JOINSAEX.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "party_dimension", "CUSTOMER", "CUSTOMER.dimension_id = TMP.cus_dim")
		.join(Join.LEFT_OUTER_JOIN, "geo_dimension", "COUNTRYGEO", "COUNTRYGEO.dimension_id = TMP.o_country")
		.join(Join.LEFT_OUTER_JOIN, "geo_dimension", "STATEGEO", "STATEGEO.dimension_id = TMP.o_state")
		.join(Join.LEFT_OUTER_JOIN, "geo_dimension", "DISTRICTGEO", "DISTRICTGEO.dimension_id = TMP.o_district")
		.join(Join.LEFT_OUTER_JOIN, "geo_dimension", "WARDGEO", "WARDGEO.dimension_id = TMP.o_ward")
		.join(Join.LEFT_OUTER_JOIN, "geo_dimension", "ROADGEO", "ROADGEO.dimension_id = TMP.o_road")
		.groupBy("TMP.cace_dim").groupBy("TMP.saex_dim").groupBy("TMP.channel_name")
		.groupBy("JOINVALUE.taxJoin").groupBy("JOINVALUE.valueJoin")
		.groupBy("TMP.order_id").groupBy("TMP.order_date")
		.groupBy("TMP.delivery_date").groupBy("TMP.delivery_date_s").groupBy("TMP.ship_after_date").groupBy("TMP.ship_before_date")
		.groupBy("cace_id").groupBy("cace_name").groupBy("saex_id").groupBy("saex_name")
		.groupBy("cus_id").groupBy("per_name").groupBy("cus_addr").groupBy("cus_phone").groupBy("cus_gr_name")
		.groupBy("o_address").groupBy("order_country").groupBy("cus_type")
		.groupBy("order_state").groupBy("order_district").groupBy("order_ward").groupBy("order_road").groupBy("TMP.o_status")
		.orderBy("TMP.order_id");
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	public class OrderResult extends ReturnResultGrid {
		public OrderResult() {
			addDataField("stt");
			addDataField("orderId");
			addDataField("orderDate");
			addDataField("channel");
			addDataField("caceId");
			addDataField("caceName");
			addDataField("saexId");
			addDataField("saexName");
			addDataField("cusId");
			addDataField("cusName");
			addDataField("cusAddress");
			addDataField("cusPhone");
			addDataField("orderAddress");
			addDataField("orderRoad");
			addDataField("orderWard");
			addDataField("orderDistrict");
			addDataField("orderState");
			addDataField("orderCountry");
			addDataField("orderStatus");
			addDataField("orderStatusId");
			addDataField("expectedDeliveryDate");
			addDataField("expectedDeliveryRangeDate");
			addDataField("actualDeliveryDate");
			addDataField("deliveryStatus");
			addDataField("deliveryStatusId");
			addDataField("orderNote");
			addDataField("orderValue");
			addDataField("orderTax");
			List<Map<String,String>> type = getType();
			for(int i = 0; i < type.size(); i++) {
				String productId = (String) type.get(i).get("product_id");			
				addDataField(productId);
			}
		}
		
		@SuppressWarnings({ "unchecked" })
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			Map<String, ? extends Object> cont = (Map<String, ? extends Object>) getParameter(CONT);
			Locale locale = (Locale)cont.get("locale");
			Date today = new Date(System.currentTimeMillis());
			String delStatus = (String) getParameter(DEL_STATUS);
			
			try {
				String cusType = result.getString("cus_type");
				String orderIdResult = result.getString("order_id");
				String orderDateResult = result.getString("order_date");
				String orderStatus = result.getString("o_status");
				GenericValue statusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", orderStatus), false);
				String orderStatusResult = (String) statusId.get("description", locale);
				String channelResult = result.getString("channel_name");
				String caceIdResult = result.getString("cace_id");
				String caceNameResult = result.getString("cace_name");
				String saexIdResult = result.getString("saex_id");
				String saexNameResult = result.getString("saex_name");;
				
				map.put("orderId", orderIdResult);
				map.put("orderDate", orderDateResult);
				map.put("channel", channelResult);
				map.put("caceId", caceIdResult);
				map.put("caceName", caceNameResult);
				map.put("saexId", saexIdResult);
				map.put("saexName", saexNameResult);
				
				String cusIdResult = result.getString("cus_id");
				String cusNameResult;
				String cusAddressResult = result.getString("cus_addr");
				String cusPhoneResult = result.getString("cus_phone");
				if("PERSON".equals(cusType)){
					cusNameResult = result.getString("per_name");
					map.put("cusName", cusNameResult);
				} else if(!"PERSON".equals(cusType)){
					cusNameResult = result.getString("cus_gr_name");
					map.put("cusName", cusNameResult);
				}
				map.put("cusId", cusIdResult);
				map.put("cusAddress", cusAddressResult);
				map.put("cusPhone", cusPhoneResult);
				
				String orderAddressResult = result.getString("o_address");
				String orderCountryResult = result.getString("order_country");
				String orderStateResult = result.getString("order_state");
				String orderDistrictResult = result.getString("order_district");
				String orderWardResult = result.getString("order_ward");
				String orderRoadResult = result.getString("order_road");
				String actualDeliverDateResult = result.getString("delivery_date");
				String expectedDeliveryDateResult = result.getString("delivery_date_s");
				String deliveryRangeDateResult = null;
				if(UtilValidate.isNotEmpty(result.getString("ship_after_date")) && UtilValidate.isNotEmpty(result.getString("ship_before_date"))){
					deliveryRangeDateResult = result.getString("ship_after_date") + " - " + result.getString("ship_before_date");
				}
				Date deliverDate = result.getDate("delivery_date");
				BigDecimal valueOrderResult = result.getBigDecimal("valueJoin");
				BigDecimal taxOrderResult = result.getBigDecimal("taxJoin");
				String deliveryStatusId = "DELI_ENTRY_COMPLETED";
				
				map.put("orderAddress", orderAddressResult);
				map.put("orderRoad", orderRoadResult);
				map.put("orderWard", orderWardResult);
				map.put("orderDistrict", orderDistrictResult);
				map.put("orderState", orderStateResult);
				map.put("orderCountry", orderCountryResult);
				map.put("orderStatusId", orderStatus);
				map.put("orderStatus", orderStatusResult);
				map.put("expectedDeliveryDate", expectedDeliveryDateResult);
				map.put("expectedDeliveryRangeDate", deliveryRangeDateResult);
				map.put("actualDeliveryDate", actualDeliverDateResult);
				map.put("orderValue", valueOrderResult);
				map.put("orderTax", taxOrderResult);
				
				String hyphens = "-";
				BigDecimal zero = new BigDecimal(0);
				List<Map<String,String>> type = getType();	
				for(int i = 0; i < type.size(); i++) {
					String productId = (String) type.get(i).get("product_id");
					BigDecimal valueResult = result.getBigDecimal(productId);
					if(!zero.equals(valueResult)){
						map.put(productId, valueResult);
					} else {
						map.put(productId, hyphens);
					}
				}
				if(UtilValidate.isNotEmpty(deliverDate) && orderStatus.equals("ORDER_COMPLETED")){
					if(today.after(deliverDate)){
						map.put("deliveryStatus", delStatus);
						map.put("deliveryStatusId", deliveryStatusId);
					}else{
						map.put("deliveryStatus", "");
						map.put("deliveryStatusId", "");
					}
				}else{
					map.put("deliveryStatus", "");
					map.put("deliveryStatusId", "");
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), OrderResult.class.getName());
			}
			
			return map;
		}
		
	}
}