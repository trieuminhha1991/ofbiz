package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SynthesisChart extends OlbiusBuilder {
	public static final String FILTER1 = "FILTER1";
	public static final String FILTER2 = "FILTER2";
	public static final String ORG = "ORG";

	public SynthesisChart(Delegator delegator) {
		super(delegator);
	}

	private OlbiusQuery query2;
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapPieChart) {
			String filter1 = (String) getParameter(FILTER1);
			String filter2 = (String) getParameter(FILTER2);
			
			if(UtilValidate.areEqual(filter1, "channel")){
				if(UtilValidate.areEqual(filter2, "ordervolume")){
					addXAxis("channelQ");
					addYAxis("volume");
				} else {
					addXAxis("channelQ");
					addYAxis("value");
				}
			} else {
				if(UtilValidate.areEqual(filter2, "ordervolume")){
					addXAxis("store_id");
					addYAxis("volume");
				} else {
					addXAxis("store_id");
					addYAxis("value");
				}
			}
		}
	}
	
	private void initQuery() {
		String organization = (String) getParameter(ORG);
		String filter1 = (String) getParameter(FILTER1);
		String filter2 = (String) getParameter(FILTER2);
		
		query2 = OlbiusQuery.make(getSQLProcessor());
		
		if(UtilValidate.areEqual(filter1, "channel")){
			if(UtilValidate.areEqual(filter2, "ordervolume")){
				query2.from("sales_order_fact")
				.select("count(DISTINCT order_id)", "volume")
				.select("enumeration_dimension.enum_id", "channelQ")
				.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
				.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "sales_order_fact.discount_dim_id = product_promo_dimension.dimension_id")
				.where(Condition.makeEQ("party_dimension.party_id", organization).andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("product_promo_dimension.product_promo_id IS NULL"))
				.groupBy("channelQ");
			} else {
				query2.from("sales_order_fact")
				.select("enumeration_dimension.description", "channelQ")
				.select("sum(quantity)", "volume")
				.select("sum(total)", "value")
				.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
				.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "sales_order_fact.discount_dim_id = product_promo_dimension.dimension_id")
				.where(Condition.makeEQ("party_dimension.party_id", organization).andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("product_promo_dimension.product_promo_id IS NULL"))
				.groupBy("channelQ");
			}
		} else {
			if(UtilValidate.areEqual(filter2, "ordervolume")){
				query2.from("sales_order_fact")
				.select("count(DISTINCT order_id)", "volume")
				.select("product_store_dimension.product_store_id", "store_id")
				.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_store_dimension", "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "sales_order_fact.discount_dim_id = product_promo_dimension.dimension_id")
				.where(Condition.makeEQ("party_dimension.party_id", organization).andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("product_promo_dimension.product_promo_id IS NULL"))
				.groupBy("store_id");
			} else {
				query2.from("sales_order_fact")
				.select("product_store_dimension.product_store_id", "store_id")
				.select("sum(quantity)", "volume")
				.select("sum(total)", "value")
				.join(Join.INNER_JOIN, "product_store_dimension", "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "sales_order_fact.discount_dim_id = product_promo_dimension.dimension_id")
				.where(Condition.makeEQ("party_dimension.party_id", organization).andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("product_promo_dimension.product_promo_id IS NULL"))
				.groupBy("store_id");
			}
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
	
      }
