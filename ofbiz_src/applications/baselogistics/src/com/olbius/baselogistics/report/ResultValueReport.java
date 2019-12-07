package com.olbius.baselogistics.report;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ResultValueReport extends TypeOlap{
	private OlbiusQuery queryReceiveTotal;
	private SQLProcessor processor;
	private OlbiusQuery queryTopProductReceive;
	private OlbiusQuery queryTopLeastProductReceive;
	/*private OlbiusQuery queryTopProductExport;
	private OlbiusQuery queryTopLeastProductExport;*/
	
	public ResultValueReport(SQLProcessor processor, String organization) {
		this.processor = processor;
		Date curDate = new Date(System.currentTimeMillis());
		Timestamp curTime = new Timestamp(curDate.getTime());
		Timestamp startMonth = UtilDateTime.getMonthStart(curTime);
		Condition condition = new Condition();
		queryReceiveTotal = (OlbiusQuery) new OlbiusQuery(processor)
				.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantityOnHandTotal")
				.from("inventory_item_fact")
				.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+organization+"'")
				.where(condition);
				condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
				condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
		
		queryTopProductReceive = (OlbiusQuery) new OlbiusQuery(processor)
				.select("product_dimension.internal_name")
				.select("currency_dimension.description")
				.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantityOnHandTotal")
				.from("inventory_item_fact")
				.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+organization+"'")
				.join(Join.INNER_JOIN, "currency_dimension", null, "inventory_item_fact.uom_dim_id = currency_dimension.dimension_id")
				.where(condition)
				.groupBy("product_dimension.internal_name")
				.groupBy("currency_dimension.description");
				condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
				condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
				queryTopProductReceive.orderBy("quantityOnHandTotal", OlbiusQuery.DESC, true);
				queryTopProductReceive.limit(1);
		
		queryTopLeastProductReceive = (OlbiusQuery) new OlbiusQuery(processor)
				.select("product_dimension.internal_name")
				.select("currency_dimension.description")
				.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantityOnHandTotal")
				.from("inventory_item_fact")
				.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "currency_dimension", null, "inventory_item_fact.uom_dim_id = currency_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+organization+"'")
				.where(condition)
				.groupBy("product_dimension.internal_name")
				.groupBy("currency_dimension.description");
				condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
				condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
				queryTopLeastProductReceive.orderBy("quantityOnHandTotal", OlbiusQuery.ASC, true);
				queryTopLeastProductReceive.limit(1);
	}
	
	public List<String> getValueTotalReceiveReport() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = queryReceiveTotal.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("quantityOnHandTotal"));
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
		
		return list;
	}
	
	public List<String> getTopProductReceiveMost() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = queryTopProductReceive.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("quantityOnHandTotal"));
				list.add(resultSet.getString("internal_name"));
				list.add(resultSet.getString("description"));
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
		
		return list;
	}
	
	public List<String> getTopLeastProductReceiveMost() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = queryTopLeastProductReceive.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("quantityOnHandTotal"));
				list.add(resultSet.getString("internal_name"));
				list.add(resultSet.getString("description"));
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
		
		return list;
	}
	
	public static Map<String, Object> getValueReportReceive(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultValueReport type = new ResultValueReport(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getValueTotalReceiveReport());
		return result;
	}
	
	public static Map<String, Object> getMostReceiveProductReportOlap(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultValueReport type = new ResultValueReport(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getTopProductReceiveMost());
		return result;
	}
	
	public static Map<String, Object> getLeastReceiveProductReportOlap(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultValueReport type = new ResultValueReport(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getTopLeastProductReceiveMost());
		return result;
	}
}
