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

public class RevenueReportByCustomerType extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		
//		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String dsaId = userLogin.getString("partyId");
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		
		putParameter("dateType", context.get("dateType"));
		putParameter("product", context.get("product[]"));
		putParameter("customerType", context.get("customerType[]"));
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}
	
	private static final OlbiusCache<Long> CUSTOMER_TYPE = new OlbiusCache<Long>() {
		
		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("PartyTypeDimension", UtilMisc.toMap("partyTypeId", key), null, false));
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
	
	private List<Object> getCustomerType(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = CUSTOMER_TYPE.get(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	private OlbiusQuery init() {
		OlbiusQuery query0 = makeQuery();

		List<Object> products = getProducts((List<?>) getParameter("product"));
		Boolean productFlag = false;
		if(!products.isEmpty()){
			productFlag = true;
		}
		
		List<Object> customerType = getCustomerType((List<?>) getParameter("customerType"));
		Boolean customerTypeFlag = false;
		if(!customerType.isEmpty()){
			customerTypeFlag = true;
		}
		
		Condition condition = new Condition();
		condition.andBetween("dd.dimension_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
		.andIn("ptd.dimension_id", customerType, customerTypeFlag)
		.andIn("prd.dimension_id", products, productFlag);
		
		query0.select("ptd.party_type_id").select("ptd.description")
		.select("prd.product_code").select("prd.internal_name")
		.select(new Sum("sof.total"), "valuee").select(new Sum("sof.quantity"),"volumee")
		.select("sof.quantity_uom")
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = sof.party_to_dim_id")
		.join(Join.INNER_JOIN, "party_type_dimension", "ptd", "ptd.dimension_id = pd.party_type_dim_id")
		.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = sof.product_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.where(condition)
		.groupBy("sof.quantity_uom").groupBy("ptd.party_type_id").groupBy("ptd.description")
		.groupBy("prd.product_code").groupBy("prd.internal_name");
		return query0;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("customerType", "party_type_id");
		addDataField("customerTypeName", "description");
		addDataField("productCode", "product_code");
		addDataField("productName", "internal_name");
		addDataField("salesValue", "valuee", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("unit", "quantity_uom");
		addDataField("salesVolume", "volumee", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
	}
}
