package com.olbius.salesmtl.report;

import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.bi.accounting.AccountingOlap;

public class ProductInventoryCustomerOlapImp extends OlbiusBuilder implements AccountingOlap{
	
	public ProductInventoryCustomerOlapImp(Delegator delegator) {
		super(delegator);
	}


	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	private OlbiusQuery query;
	private String dateType;
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		dateType = (String) getParameter(DATE_TYPE);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);
		dateType = getDateType(dateType);
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("product_inventory_customer_fact")
		.select("date_dimension.".concat(dateType))
		.select("product_dimension.product_code")
		.select("product_dimension.internal_name")
		.select("party_dimension.party_code")
		.select("party_dimension.name")
		.select("product_inventory_customer_fact.quantity")
		.select("product_inventory_customer_fact.created_by")
		.select("array_to_string(array_agg(category_dimension.category_name), ',')", "category_name")
		.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'")
		.join(Join.INNER_JOIN, "party_dimension", "party_dimension", "party_dim_id = party_dimension.dimension_id");
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		if(categoryId != null){
			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId!=null));
		}
		query.where(condition);
		query.groupBy("date_dimension.".concat(dateType))
		.groupBy("product_dimension.product_code")
		.groupBy("product_dimension.internal_name")
		.groupBy("party_dimension.party_code")
		.groupBy("party_dimension.name")
		.groupBy("product_inventory_customer_fact.quantity")
		.groupBy("product_inventory_customer_fact.created_by")
		.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC)
		.orderBy("product_dimension.product_code");
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	@Override
	public void prepareResultGrid() {
		addDataField("date", dateType);
		addDataField("productCode", "product_code");
		addDataField("internalName", "internal_name");
		addDataField("quantity", "quantity");
		addDataField("partyCode", "party_code");
		addDataField("partyName", "name");
		addDataField("categoryName", "category_name");
	}
}
