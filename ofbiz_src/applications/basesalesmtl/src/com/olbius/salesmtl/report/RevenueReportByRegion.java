package com.olbius.salesmtl.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.entity.cache.OlbiusCache;

import javolution.util.FastList;

public class RevenueReportByRegion extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		
		putParameter("dateType", context.get("dateType"));
		putParameter("product", context.get("product[]"));
		putParameter("category", context.get("categories[]"));
		putParameter("region", context.get("region[]"));
		putParameter("orderStatus", context.get("orderStatus[]"));
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}
	
	private static final OlbiusCache<Long> CATEGORY = new OlbiusCache<Long>() {
		
		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("CategoryDimension", UtilMisc.toMap("categoryId", key), null, false));
			return value != null ? value.getLong("dimensionId") : -1;
		}
		
	};
	
	private static final OlbiusCache<Long> REGION = new OlbiusCache<Long>() {
		
		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("PartyDimension", UtilMisc.toMap("partyId", key), null, false));
			return value != null ? value.getLong("dimensionId") : -1;
		}
		
	};
	
	private List<Object> getProducts(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = ProductDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	private List<Object> getCategories(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = CATEGORY.get(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	private List<Object> getRegions(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = REGION.get(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		OlbiusQuery query0 = makeQuery();

		List<Object> products = getProducts((List<?>) getParameter("product"));
		Boolean productFlag = false;
		if(!products.isEmpty()){
			productFlag = true;
		}
		
		List<Object> categories = getCategories((List<?>) getParameter("category"));
		Boolean categoryFlag = false;
		if(!categories.isEmpty()){
			categoryFlag = true;
		}
		
		List<Object> regions = getRegions((List<?>) getParameter("region"));
		Boolean regionFlag = false;
		if(!regions.isEmpty()){
			regionFlag = true;
		}
		
		List<String> orderStatusStr = (List<String>) getParameter("orderStatus");
		if(UtilValidate.isEmpty(orderStatusStr)){
			orderStatusStr = FastList.newInstance();
			orderStatusStr.add("ORDER_COMPLETED");
		}
		List<Object> orderStatus = new ArrayList<Object>(orderStatusStr);
		Boolean oStatusFlag = false;
		if(!orderStatus.isEmpty() || orderStatus != null){
			oStatusFlag = true;
		}
		
		Condition condition = new Condition();
		condition.andBetween("date_dimension.dimension_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
		.andIn("sof.product_dim_id", products, productFlag)
		.andIn("sof.party_from_dim_id", regions, regionFlag)
		.andIn("cd.dimension_id", categories, categoryFlag)
		.andIn("sof.order_status", orderStatus, oStatusFlag)
		.and("product_promo_dimension.product_promo_id IS NULL")
		.and("cd.category_id not like '%TAX%'")
		.and("sof.order_item_status <> 'ITEM_CANCELLED'")
		.and("pcr.thru_dim_date = -1");
		
		
		query0.select("pd.product_code")
		.select("pgd.description", "region")
		.select("pd.product_id")
		.select("pd.internal_name")
		.select("cd.category_name")
		.select(new Sum("sof.quantity"), "Quantity")
		.select(new Sum("sof.total"),"Total")
		.select("sof.party_from_dim_id")
		.select("pgd.party_id")
		.select("sof.quantity_uom", "unit")
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "product_dimension", "pd", "sof.product_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "sof.product_dim_id = pcr.product_dim_id")
		.join(Join.INNER_JOIN, "category_dimension", "cd", "cd.dimension_id = pcr.category_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pgd", "sof.party_from_dim_id = pgd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "sof.order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sof.promo_dim_id")
		.where(condition)
		.groupBy("pd.product_code")
		.groupBy("pd.product_id")
		.groupBy("pd.internal_name")
		.groupBy("sof.party_from_dim_id")
		.groupBy("pgd.party_id")
		.groupBy("cd.category_name")
		.groupBy("pgd.party_id")
		.groupBy("pgd.description")
		.groupBy("sof.quantity_uom").orderBy("product_code");
		
		return query0;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("region", "region", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object)){
				return (String) object;}else{return "-";}}
		});
		addDataField("category", "category_name");
		addDataField("productCode", "product_code");
		addDataField("productName", "internal_name");
		addDataField("salesValue", "Total", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("unit", "unit");
		addDataField("salesVolume", "Quantity", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
	}
}
