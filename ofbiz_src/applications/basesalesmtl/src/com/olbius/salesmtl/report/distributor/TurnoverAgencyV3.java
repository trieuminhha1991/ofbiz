package com.olbius.salesmtl.report.distributor;

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
import com.olbius.bi.olap.cache.dimension.CategoryDimension;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.entity.cache.OlbiusCache;

public class TurnoverAgencyV3 extends OlbiusOlapService {
	private OlbiusQuery query;
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		
		putParameter("dateType", context.get("dateType"));
		putParameter("category", context.get("category[]"));
		putParameter("orderStatus", context.get("orderStatus[]"));
	}
	
	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private static final OlbiusCache<Long> ORDER_STATUS = new OlbiusCache<Long>() {
		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("StatusDimension", UtilMisc.toMap("statusId", key), null, false));
			return value != null ? value.getLong("dimensionId") : -1;
		}
		
	};
	
	private List<Object> getCategories(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = CategoryDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	private List<Object> getOrderStatus(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = ORDER_STATUS.get(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	private OlbiusQuery init() {
		OlbiusQuery query = makeQuery();
		
		List<Object> categories = getCategories((List<?>) getParameter("categories"));
		List<Object> orderStatus = getOrderStatus((List<?>) getParameter("order_status"));
		
		String agencyId = (String) getParameter("agencyId");
		String flagDis = (String) getParameter("flagDis");
		String organ = "MB";
		
		query.select("product_dimension.product_code")
		.select("product_store_dimension.product_store_id")   
		.select("product_store_dimension.store_name")   
		.select("product_dimension.internal_name")
		.select(new Sum("quantity"), "Quantity")
		.select(new Sum("total"), "Total")
		.select("pd.party_code", "agency_id", UtilValidate.isNotEmpty(agencyId))
		.select("pd.description", "agency_name", UtilValidate.isNotEmpty(agencyId))
		.select("'all_object' as agency_id", UtilValidate.isEmpty(agencyId))
		.select("'all_object2' as agency_name", UtilValidate.isEmpty(agencyId))
		.select("product_store_dimension.product_store_id || '-' || product_dimension.product_code", "description")
		.select("category_dimension.category_name")
		.select("sales_order_fact.quantity_uom", "unit")
		.from("product_dimension")
		.join(Join.INNER_JOIN, "sales_order_fact", "sales_order_fact.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "sales_order_fact.party_to_dim_id = pd.dimension_id", UtilValidate.isNotEmpty(agencyId))
		.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.discount_dim_id")
		.join(Join.INNER_JOIN, "product_store_dimension", "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.sale_executive_party_dim_id = party_dimension.dimension_id", UtilValidate.isNotEmpty(flagDis))
		.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.party_from_dim_id = party_dimension.dimension_id", UtilValidate.isEmpty(flagDis))
		.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id")
		.where(Condition
				.makeBetween("date_dimension.dimension_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
//				.and(Condition.makeIn("product_store_dimension.product_store_id", productStoreId, productStoreId != null))
//				.and(Condition.makeIn("party_dimension.party_id", salesmanList), UtilValidate.isNotEmpty(flagSM) && !salesmanList.isEmpty())
				.andEQ("party_dimension.party_id", organ, UtilValidate.isEmpty(flagDis))
				.andIn("sales_order_fact.order_status", orderStatus, !orderStatus.isEmpty())
				.and("category_dimension.category_id not like '%TAX%'")
				.and("product_promo_dimension.product_promo_id IS NULL")
				.andIn("category_dimension.category_id", categories, !categories.isEmpty())
				.and("sales_order_fact.return_id is null")
				.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")
				.andEQ("pd.party_id", agencyId, UtilValidate.isNotEmpty(agencyId))
		)
		.groupBy("product_dimension.product_code")
//		.groupBy("product_store_dimension.product_store_id")
		.groupBy("pd.party_code", UtilValidate.isNotEmpty(agencyId))
		.groupBy("product_store_dimension.store_name")
		.groupBy("category_dimension.category_name")
		.groupBy("pd.description", UtilValidate.isNotEmpty(agencyId))
		.groupBy("product_dimension.internal_name")
		.groupBy("sales_order_fact.quantity_uom")
		;
		
		return query;
		
	}
	
	@Override
	public void prepareResultGrid() {
		String flagDis = (String) getParameter("flagDis");
		addDataField("stt");
		addDataField("productStoreName",  "store_name");
		if(UtilValidate.isNotEmpty(flagDis)){
			addDataField("agencyId", "agency_id", new ReturnResultCallback<String>() {
				@Override
				public String get(Object object) {
					if(!"all_object".equals(object)){ 
						return (String) object; 
					} else{ 
						String all = "all";
						return all; 
					}
				}
			});
			addDataField("agencyName", "agency_name", new ReturnResultCallback<String>() {
				@Override
				public String get(Object object) {
					if(!"all_object2".equals(object)){ 
						return (String) object; 
					} else{ 
						String all = "all";
						return all; 
					}
				}
			});
		}
		addDataField("category", "category_name");
		addDataField("percent");
		addDataField("productId", "product_code");
		addDataField("productName", "internal_name");
		addDataField("quantity1", "Quantity", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("total1", "Total", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("unit", "unit");
	}

//	public static final String PRODUCT_STORE = "PRODUCT_STORE";
//	public static final String ORDER_STATUS = "ORDER_STATUS";
//	public static final String ORG = "ORG";
//	public static final String CATEGORY = "CATEGORY";
//	public static final String QUANTITY = "QUANTITY";
//	public static final String PARTY = "PARTY";
//	public static final String FLAGSM = "FLAGSM";
//	public static final String AGENCY_ID = "AGENCY_ID";
//	public static final String ALL = "ALL";
	
//	public TurnoverAgencyV3(Delegator delegator) {
//		super(delegator);
//	}

	
//	@Override
//	public void prepareResultGrid() {
//		String flagSM = (String) getParameter(FLAGSM);
//		addDataField("stt");
//		addDataField("productStoreName",  "store_name");
//		if(UtilValidate.isNotEmpty(flagSM)){
//			addDataField("agencyId", "agency_id", new ReturnResultCallback<String>() {
//				@Override
//				public String get(Object object) {
//					if(!"all_object".equals(object)){ 
//						return (String) object; 
//					} else{ 
//						String all = (String) getParameter(ALL);
//						return all; 
//					}
//				}
//			});
//			addDataField("agencyName", "agency_name", new ReturnResultCallback<String>() {
//				@Override
//				public String get(Object object) {
//					if(!"all_object2".equals(object)){ 
//						return (String) object; 
//					} else{ 
//						String all = (String) getParameter(ALL);
//						return all; 
//					}
//				}
//			});
//		}
//		addDataField("category", "category_name");
//		addDataField("percent");
//		addDataField("productId", "product_code");
//		addDataField("productName", "internal_name");
//		addDataField("quantity1", "Quantity", new ReturnResultCallback<BigDecimal>() {
//			@Override
//			public BigDecimal get(Object object) {
//				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
//			}
//		});
//		addDataField("total1", "Total", new ReturnResultCallback<BigDecimal>() {
//			@Override
//			public BigDecimal get(Object object) {
//				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
//			}
//		});
//		addDataField("unit", "unit");
//	}
//	
//	@Override
//	public void prepareResultChart() {
//		if(getOlapResult() instanceof OlapColumnChart) {
//			addSeries("store_name");
//			addXAxis("product_code");
//			addYAxis("Total");
//		}
//		
//		if(getOlapResult() instanceof OlapPieChart) {
//			addXAxis("description");
//			addYAxis("Total");
//		}
//		
//	}
	
//	@SuppressWarnings("unchecked")
//	private void initQuery() {
//		List<Object> productStoreId = (List<Object>) getParameter(PRODUCT_STORE);
//		List<Object> categoryId = (List<Object>) getParameter(CATEGORY);
//		String organ = (String) getParameter(ORG);
//		String status = (String) getParameter(ORDER_STATUS);
//		String flagSM = (String) getParameter(FLAGSM);
//		String agencyId = (String) getParameter(AGENCY_ID);
//		List<Object> salesmanList = (List<Object>) getParameter(PARTY);
//		Condition condition = new Condition();
//		
//		query2 = new OlbiusQuery(getSQLProcessor());
//		
//		query2.from("product_dimension")
//		.select("product_dimension.product_code")
//		.select("product_store_dimension.product_store_id")   
//		.select("product_store_dimension.store_name")   
//		.select("product_dimension.internal_name")
//		.select("sum(quantity)", "Quantity")
//		.select("sum(total)", "Total")
//		.select("pd.party_code", "agency_id", UtilValidate.isNotEmpty(agencyId))
//		.select("pd.description", "agency_name", UtilValidate.isNotEmpty(agencyId))
//		.select("'all_object' as agency_id", UtilValidate.isEmpty(agencyId))
//		.select("'all_object2' as agency_name", UtilValidate.isEmpty(agencyId))
//		.select("product_store_dimension.product_store_id || '-' || product_dimension.product_code", "description")
//		.select("category_dimension.category_name")
//		.select("sales_order_fact.quantity_uom", "unit")
//		.join(Join.INNER_JOIN, "sales_order_fact", "sales_order_fact.product_dim_id = product_dimension.dimension_id")
//		.join(Join.INNER_JOIN, "party_dimension", "pd", "sales_order_fact.party_to_dim_id = pd.dimension_id", UtilValidate.isNotEmpty(agencyId))
//		.join(Join.INNER_JOIN,"date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
//		.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.discount_dim_id")
//		.join(Join.INNER_JOIN, "product_store_dimension", "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id");
//		if(UtilValidate.isNotEmpty(flagSM)){
//			query2.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.sale_executive_party_dim_id = party_dimension.dimension_id");
//		}else{
//			query2.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.party_from_dim_id = party_dimension.dimension_id");
//		}
//		query2.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
//		.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id")
//		.where(condition);
//		query2.groupBy("product_dimension.product_code")
//		.groupBy("product_store_dimension.product_store_id").groupBy("pd.party_code", UtilValidate.isNotEmpty(agencyId))
//		.groupBy("product_store_dimension.store_name").groupBy("category_dimension.category_name").groupBy("pd.description", UtilValidate.isNotEmpty(agencyId))
//		.groupBy("product_dimension.internal_name").groupBy("sales_order_fact.quantity_uom");
//		
//		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
//		if(productStoreId != null){
//			condition.and(Condition.makeIn("product_store_dimension.product_store_id", productStoreId, productStoreId != null));
//		}
//		if(UtilValidate.isNotEmpty(flagSM)){
//			if(salesmanList.isEmpty()){
//
//			}else{
//				condition.and(Condition.makeIn("party_dimension.party_id", salesmanList));
//			}
//		}else{
//			condition.and(Condition.makeEQ("party_dimension.party_id", organ));
//		}
//		condition.andEQ("sales_order_fact.order_status", status, status != null).and("category_dimension.category_id not like '%TAX%'")
//		.and("product_promo_dimension.product_promo_id ISNULL");
//		if(categoryId != null){
//			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
//		}
////		condition.and(Condition.make("sales_order_fact.return_id isnull"));
//		if("ORDER_CANCELLED".equals(status)){
//			condition.and(Condition.makeEQ("sales_order_fact.order_status", status, status != null));
//			condition.and(Condition.make("sales_order_fact.order_item_status = 'ITEM_CANCELLED'"));
//		} else {
//			condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
//		}
//		condition.andEQ("pd.party_id", agencyId, UtilValidate.isNotEmpty(agencyId));
//	}
	

}
