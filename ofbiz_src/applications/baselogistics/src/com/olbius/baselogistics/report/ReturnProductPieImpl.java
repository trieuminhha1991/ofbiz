package com.olbius.baselogistics.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ReturnProductPieImpl extends AbstractOlap{
	private OlbiusQuery query;
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	public static final String LOCALE = "LOCALE";
	public static final String FILTER_TYPE_ID = "FILTER_TYPE_ID";
	public static final String CHECK_NPP = "CHECK_NPP";
	private void initQuery(){
		String partyFacilityId = (String) getParameter(USER_LOGIN_ID);
		String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
		String checkNPP = (String) getParameter(CHECK_NPP);
		Condition condition = new Condition();
		if(filterTypeId.equals("FILTER_NPP")){ 
			query = new OlbiusQuery(getSQLProcessor());
			query.from("return_item_fact")
			.select("sum(return_item_fact.return_quantity)", "totalQuantity")
			.select("party_organization.description");
			query.join(Join.INNER_JOIN, "date_dimension", null, "status_date_dim_id = date_dimension.dimension_id")
			.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+partyFacilityId+"'", checkNPP.equals("NPP_FALSE") == true)
			.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_type_id = "+"'PARTY_GROUP'", checkNPP.equals("NPP_TRUE") == true)
			.join(Join.INNER_JOIN, "party_dimension", "party_organization", "return_item_fact.to_party_dim_id = party_organization.dimension_id");
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.makeEQ("return_item_fact.return_header_type_id", "CUSTOMER_RETURN"));
			query.where(condition);
			query.groupBy("party_organization.description");
		}
		if(filterTypeId.equals("FILTER_CATALOG")){ 
			query = new OlbiusQuery(getSQLProcessor());
			query.from("return_item_fact")
			.select("sum(return_item_fact.return_quantity)", "totalQuantity")
			.select("category_dimension.category_id");
			query.join(Join.INNER_JOIN, "date_dimension", null, "status_date_dim_id = date_dimension.dimension_id")
			.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
			.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id")
			.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
			.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'")
			.join(Join.INNER_JOIN, "date_dimension", "tdate", "product_category_relationship.thru_dim_date = tdate.dimension_id AND tdate.date_value is null")
			.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+partyFacilityId+"'", checkNPP.equals("NPP_FALSE") == true)
			.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_type_id = "+"'PARTY_GROUP'", checkNPP.equals("NPP_TRUE") == true);
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.makeEQ("return_item_fact.return_header_type_id", "CUSTOMER_RETURN"));
			query.where(condition);
			query.groupBy("category_dimension.category_id");
		}
		if(filterTypeId.equals("FILTER_RETURNER")){ 
			query = new OlbiusQuery(getSQLProcessor());
			query.from("return_item_fact")
			.select("sum(return_item_fact.return_quantity)", "totalQuantity")
			.select("party_from_dimension.name", "partyCustomer")
			.select("party_from_dimension.party_id", "partyIdCustomer")
			.join(Join.INNER_JOIN, "date_dimension", null, "status_date_dim_id = date_dimension.dimension_id")
			.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+partyFacilityId+"'", checkNPP.equals("NPP_FALSE") == true)
			.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_type_id = "+"'PARTY_GROUP'", checkNPP.equals("NPP_TRUE") == true)
			.join(Join.INNER_JOIN, "party_dimension", "party_from_dimension", "return_item_fact.from_party_dim_id = party_from_dimension.dimension_id");
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.makeEQ("return_item_fact.return_header_type_id", "CUSTOMER_RETURN"));
			query.where(condition)
			.groupBy("party_from_dimension.name")
			.groupBy("party_from_dimension.party_id");
		}
	}
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

	
	public class ResultOutReport implements OlapResultQueryInterface{
		@Override
		public Object resultQuery(OlapQuery query) {
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			/*String locale = (String) getParameter(LOCALE);*/
			String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				ResultSet resultSet = query.getResultSet();
				if(filterTypeId.equals("FILTER_NPP")){
					while(resultSet.next()) {
						String description = resultSet.getString("description");
						map.put(description, resultSet.getBigDecimal("totalQuantity"));
					}
				}
				if(filterTypeId.equals("FILTER_CATALOG")){
					while(resultSet.next()) {
						String categoryId = resultSet.getString("category_id");
						GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId), false);
						if(productCategory != null){
							categoryId = productCategory.getString("categoryName");
						}
						map.put(categoryId, resultSet.getBigDecimal("totalQuantity"));
					}
				}
				if(filterTypeId.equals("FILTER_RETURNER")){
					while(resultSet.next()) {
						String partyCustomer = resultSet.getString("partyCustomer");
						String partyIdCustomer = resultSet.getString("partyIdCustomer");
						String fullNameParty = partyCustomer+" ["+partyIdCustomer+"]";
						map.put(fullNameParty, resultSet.getBigDecimal("totalQuantity"));
					}
				}
			} catch (GenericDataSourceException e) {
				e.printStackTrace();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
		
	}
}