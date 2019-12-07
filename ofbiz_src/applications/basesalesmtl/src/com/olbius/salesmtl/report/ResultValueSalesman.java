package com.olbius.salesmtl.report;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
//import org.ofbiz.service.GenericServiceException;
//import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

//import com.olbius.acc.report.dashboard.query.IndexAccountingOlapImplv2;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.party.PartyWorker;
//import com.olbius.bi.olap.AbstractOlap;
//import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.TypeOlap;
//import com.olbius.bi.olap.grid.OlapGrid;
//import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.salesmtl.util.MTLUtil;

import javolution.util.FastList;

public class ResultValueSalesman extends TypeOlap {
	public static final String ORGANIZATION = "ORGANIZATION";
	private final OlbiusQuery query3;
	private final OlbiusQuery query3sa;
	private final OlbiusQuery query2;
	private final OlbiusQuery query;
//	private final OlbiusQuery querysa;
	private final OlbiusQuery queryDis;
	private final OlbiusQuery query4;
	private final OlbiusQuery query4sa;
	private final OlbiusQuery query5;
	private SQLProcessor processor;
	
//	@Override
//	protected OlapQuery getQuery() {
////		// TODO Auto-generated method stub
//		return null;
//	}
	
	public ResultValueSalesman(SQLProcessor processor, List<Object> partyId, String organ) {
		this.processor = processor;
		Date curDate = new Date(System.currentTimeMillis());
		Timestamp curTime = new Timestamp(curDate.getTime());
		Timestamp startMonth = UtilDateTime.getMonthStart(curTime);
//		Date startMonthDate = new Date(startMonth.getTime());
		//select count() from  
//		inner join party_dimension on party_dimension.dimension_id = sales_order_fact.sale_executive_party_dim_id
//				where party_dimension.party_id = 'OLBMBSA001101' and order_status = 'ORDER_APPROVED'
		query = (OlbiusQuery) new OlbiusQuery(processor)
//				.select("count(distinct order_id)", "order_intransit").from("sales_order_fact")
//				.join(Join.INNER_JOIN, "date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
//				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.sale_executive_party_dim_id")
//				.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
//				.where(Condition.makeIn("party_dimension.party_id", partyId).and("sales_order_fact.order_status = 'ORDER_APPROVED'").and("date_dimension.date_value", getSqlDate(curTime)).and("organ.party_id", Condition.NOT_EQ, organ));
				.select("count(distinct order_header.order_id) as in_transit_order").from("order_header")
				.join(Join.INNER_JOIN, "order_role", "order_role.order_id = order_header.order_id")
				.where(Condition.makeEQ("order_role.role_type_id", "SALES_EXECUTIVE").andEQ("order_header.status_id", "ORDER_APPROVED")
						.and("order_header.order_date between '" + getSqlDate(curTime) + " 00:00:00.0' and '" + getSqlDate(curTime) + " 23:59:59.0'")
						.andIn("order_role.party_id", partyId));
		
		queryDis = (OlbiusQuery) new OlbiusQuery(processor)
				.select("count(distinct order_header.order_id) as in_transit_order_2").from("order_header")
				.join(Join.INNER_JOIN, "order_role", "order_role.order_id = order_header.order_id")
				.where(Condition.makeEQ("order_role.role_type_id", "SALES_EXECUTIVE").andEQ("order_header.status_id", "ORDER_APPROVED")
						.and("order_header.order_date between '" + getSqlDate(curTime) + " 00:00:00.0' and '" + getSqlDate(curTime) + " 23:59:59.0'")
						.andIn("order_role.party_id", partyId));
				
		query2 = (OlbiusQuery) new OlbiusQuery(processor)
				.select("count(distinct order_header.order_id) as complete_order").from("order_header")
				.join(Join.INNER_JOIN, "order_role", "order_role.order_id = order_header.order_id")
				.where(Condition.makeEQ("order_role.role_type_id", "SALES_EXECUTIVE").andEQ("order_header.status_id", "ORDER_COMPLETED")
						.and("order_header.order_date between '" + getSqlDate(curTime) + " 00:00:00.0' and '" + getSqlDate(curTime) + " 23:59:59.0'")
						.andIn("order_role.party_id", partyId));
		
		query4 = (OlbiusQuery) new OlbiusQuery(processor)
//				.select("count(distinct order_id)", "order_cancel").from("sales_order_fact")
//				.join(Join.INNER_JOIN, "date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
//				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.sale_executive_party_dim_id")
//				.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
//				.where(Condition.makeIn("party_dimension.party_id", partyId).and("sales_order_fact.order_status = 'ORDER_CANCELLED'").andEQ("date_dimension.date_value", getSqlDate(curTime)).and("organ.party_id", Condition.NOT_EQ, organ));
				.select("count(distinct order_header.order_id) as cancel_order").from("order_header")
				.join(Join.INNER_JOIN, "order_role", "order_role.order_id = order_header.order_id")
				.where(Condition.makeEQ("order_role.role_type_id", "SALES_EXECUTIVE").andEQ("order_header.status_id", "ORDER_CANCELLED")
						.and("order_header.order_date between '" + getSqlDate(curTime) + " 00:00:00.0' and '" + getSqlDate(curTime) + " 23:59:59.0'")
						.andIn("order_role.party_id", partyId));
		
		query4sa = (OlbiusQuery) new OlbiusQuery(processor)
				.select("count(distinct order_header.order_id) as cancel_order").from("order_header")
				.join(Join.INNER_JOIN, "order_role", "order_role.order_id = order_header.order_id")
				.where(Condition.makeEQ("order_role.role_type_id", "SALESADMIN_EMPL").andEQ("order_header.status_id", "ORDER_CANCELLED")
						.and("order_header.order_date between '" + getSqlDate(curTime) + " 00:00:00.0' and '" + getSqlDate(curTime) + " 23:59:59.0'")
						.andIn("order_role.party_id", partyId));
				
		query3 =(OlbiusQuery) new OlbiusQuery(processor)
				.select("sum(total)", "value_").from("sales_order_fact")
				.join(Join.INNER_JOIN, "date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.sale_executive_party_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
				.where(Condition.makeIn("party_dimension.party_id", partyId).and("sales_order_fact.order_status = 'ORDER_COMPLETED'").andBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)).and("organ.party_id", Condition.NOT_EQ, organ));
		
		query3sa =(OlbiusQuery) new OlbiusQuery(processor)
				.select("sum(total)", "value_").from("sales_order_fact")
				.join(Join.INNER_JOIN, "date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.sale_executive_party_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
				.where(Condition.makeIn("party_dimension.party_id", partyId).and("sales_order_fact.order_status = 'ORDER_COMPLETED'").andBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)).andEQ("organ.party_id", organ));
	
		query5 = (OlbiusQuery) new OlbiusQuery(processor)
			.select("count(distinct order_header.order_id) as new_order").from("order_header")
			.join(Join.INNER_JOIN, "order_role", "order_role.order_id = order_header.order_id")
			.where(Condition.makeEQ("order_header.status_id", "ORDER_CREATED").andIn("order_role.party_id", partyId));
	}
	
	public List<String> getValueTotal() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = query3.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("value_"));
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
	
	public List<String> getValueTotalSA() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = query3sa.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("value_"));
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
	
	public List<String> getOrderQuantityIntransit() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = query.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("in_transit_order"));
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
		return list2;
	}
	
//	public List<String> getOrderQuantityIntransitSA() {
//		List<String> list2 = new ArrayList<String>();
//		
//		try {
//			ResultSet resultSet2 = querysa.getResultSet();
//			while(resultSet2.next()) {
//				list2.add(resultSet2.getString("in_transit_order"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(processor != null) {
//				try {
//					processor.close();
//				} catch (GenericDataSourceException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return list2;
//	}
	
	public List<String> getOrderQuantityIntransitDis() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = queryDis.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("in_transit_order_2"));
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
		return list2;
	}
	
	public List<String> getOrderQuantityCompleted() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = query2.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("complete_order"));
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
		return list2;
	}
	
	public List<String> getOrderQuantityCancelled() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = query4.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("cancel_order"));
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
		return list2;
	}
	
	public List<String> getOrderQuantityCancelledSA() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = query4sa.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("cancel_order"));
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
		return list2;
	}
	
	public List<String> getOrderNeedApprove() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = query5.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("new_order"));
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
		return list2;
	}
	
	public static Map<String, Object> getValueReport(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String userId = userLogin.getString("userLoginId");
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> parties = FastList.newInstance();
		
		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
			parties = UtilMisc.toList(userLogin.getString("partyId"));
		} else {
			String userLogId = (String) userLogin.get("partyId");
			parties = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		}
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		
		List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), listSalesmanId, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getValueTotal());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getValueReportSA(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String positionType = (String) context.get("positionType");
		List<Object> listSalesAdmin = FastList.newInstance();
		if(UtilValidate.isNotEmpty(positionType) && "SA".equals(positionType)){
			Object saId = (Object) userLogin.getString("partyId");
			listSalesAdmin = FastList.newInstance();
			listSalesAdmin.add(saId);		
		} else if (UtilValidate.isNotEmpty(positionType) && "SA".equals(positionType)){
			//get sales admin list by sales admin manager
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
			List<String> listEmployee = FastList.newInstance();
			if (ServiceUtil.isSuccess(ccc)) {
				listEmployee = (List<String>) ccc.get("listEmployee");
			}
			listSalesAdmin = new ArrayList<Object>(listEmployee);
		}
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), listSalesAdmin, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getValueTotalSA());
		return result;
	}
	
	public static Map<String, Object> getValueDisReport(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		String userId = userLogin.getString("userLoginId");
		
//		List<EntityCondition> listAllConditions = FastList.newInstance();
//        List<String> parties = FastList.newInstance();
        
        String userLogId = (String) userLogin.get("partyId");
		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
		String supId = listSupId.get(0);
		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
		String tmp_first = tmp1.get(0);
		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
		List<Object> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
        
//        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//        	parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			parties = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
//		}
//        
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//        
//        List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), salesman, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getValueTotal());
		return result;
	}
	
	//order need approve
	public static Map<String, Object> getApproveOrderSA(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Object salesAdmin = userLogin.getString("partyId");
		List<Object> listSalesAdmin = FastList.newInstance();
		listSalesAdmin.add(salesAdmin);
		
//		List<Object> listSalesmanId = FastList.newInstance();
//		
//		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//			parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			String userLogId = (String) userLogin.get("partyId");
//			parties = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
//		}
//		
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		
//		List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
//		LocalDispatcher dispatcher = dctx.getDispatcher();
//		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALESADMIN_EMPL"));
//		List<String> listEmployee = null;
//		if (ServiceUtil.isSuccess(ccc)) {
//			listEmployee = (List<String>) ccc.get("listEmployee");
//		}
//		List<Object> listSalesmanId = new ArrayList<Object>(listEmployee);
		
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesAdmin, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listApproveOrder", type.getOrderNeedApprove());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getApproveOrderSAD(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		Object salesAdmin = userLogin.getString("partyId");
//		List<Object> listSalesAdmin = FastList.newInstance();
//		listSalesAdmin.add(salesAdmin);
		
//		List<Object> listSalesmanId = FastList.newInstance();
//		
//		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//			parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			String userLogId = (String) userLogin.get("partyId");
//			parties = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
//		}
//		
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		
//		List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = FastList.newInstance();
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
		List<Object> listSalesAdmin = new ArrayList<Object>(listEmployee);
		
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesAdmin, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listApproveOrder", type.getOrderNeedApprove());
		return result;
	}
	
	public static Map<String, Object> getOrderQuantityIntransit(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		String userId = userLogin.getString("userLoginId");
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> parties = FastList.newInstance();
		
		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
			parties = UtilMisc.toList(userLogin.getString("partyId"));
		} else {
			String userLogId = (String) userLogin.get("partyId");
			parties = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		}
		
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		
		List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesmanId, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderIntransit", type.getOrderQuantityIntransit());
		return result;
	}
	
	public static Map<String, Object> getOrderQuantityIntransitSA(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		Object saId = (Object) userLogin.getString("partyId");
		
		List<Object> listSalesmanId = FastList.newInstance();
		listSalesmanId.add(saId);
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesmanId, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderIntransit", type.getOrderQuantityIntransit());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getOrderQuantityIntransitSADM(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		//get sales admin list by sales admin manager
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = FastList.newInstance();
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
		List<Object> listSalesAdmin = new ArrayList<Object>(listEmployee);
		
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesAdmin, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderIntransit", type.getOrderQuantityIntransit());
		return result;
	}
	
	public static Map<String, Object> getOrderQuantityIntransitDis(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		String userId = userLogin.getString("userLoginId");
		
		String userLogId = (String) userLogin.get("partyId");
//		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
//		String supId = listSupId.get(0);
//		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
//		String tmp_first = tmp1.get(0);
//		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
//		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
//		List<Object> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
		
		List<String> salesmanId = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, userLogId);
		List<Object> salesman = new ArrayList<Object>(salesmanId);
		
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), salesman, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderIntransit", type.getOrderQuantityIntransitDis());
		return result;
	}
	
	public static Map<String, Object> getOrderQuantityCompleted(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		String userId = userLogin.getString("userLoginId");
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> parties = FastList.newInstance();
		
		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
			parties = UtilMisc.toList(userLogin.getString("partyId"));
		} else {
			String userLogId = (String) userLogin.get("partyId");
			parties = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		}
		
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		
		List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesmanId, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderCompleted", type.getOrderQuantityCompleted());
		return result;
	}
	
//	public static Map<String, Object> getOrderQuantityCompletedv2(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException {
//		Delegator delegator = ctx.getDelegator();
//		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		ResultValueSalesman grid = new ResultValueSalesman(delegator);
//		grid.setOlapResultType(OlapGrid.class);
//		
//		grid.putParameter(ResultValueSalesman.ORGANIZATION, organization);
// 
//		Map<String, Object> result = grid.execute(context);
//		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
//		return result;
//	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getOrderQuantityCompletedSA(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String positionType = (String) context.get("positionType");
		List<Object> listSalesAdmin = FastList.newInstance();
		if(UtilValidate.isNotEmpty(positionType) && "SA".equals(positionType)){
			Object saId = (Object) userLogin.getString("partyId");
			listSalesAdmin.add(saId);
		} else if (UtilValidate.isNotEmpty(positionType) && "SADM".equals(positionType)) {
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
			List<String> listEmployee = FastList.newInstance();
			if (ServiceUtil.isSuccess(ccc)) {
				listEmployee = (List<String>) ccc.get("listEmployee");
			}
			listSalesAdmin = new ArrayList<Object>(listEmployee);
		}
		
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesAdmin, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderCompleted", type.getOrderQuantityCompleted());
		return result;
	}
	
	public static Map<String, Object> getOrderQuantityCompletedDis(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		String userId = userLogin.getString("userLoginId");
		
		String userLogId = (String) userLogin.get("partyId");
		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
		String supId = listSupId.get(0);
		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
		String tmp_first = tmp1.get(0);
		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
		List<Object> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);

//		List<EntityCondition> listAllConditions = FastList.newInstance();
//        List<String> parties = FastList.newInstance();
//        
//        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//        	parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			parties = EntityUtil.getFieldListFromEntityList(
//					PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
//					, "partyId", true);
//		}
//        
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//        
//        List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), salesman, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderCompleted", type.getOrderQuantityCompleted());
		return result;
	}
	
	public static Map<String, Object> getOrderQuantityCancelled(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		String userId = userLogin.getString("userLoginId");
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> parties = FastList.newInstance();
		
		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
			parties = UtilMisc.toList(userLogin.getString("partyId"));
		} else {
			String userLogId = (String) userLogin.get("partyId");
			parties = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		}
		
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		
		List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesmanId, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderCancelled", type.getOrderQuantityCancelled());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getOrderQuantityCancelledSA(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String positionType = (String) context.get("positionType");
		List<Object> listSalesAdmin = FastList.newInstance();
		if(UtilValidate.isNotEmpty(positionType) && "SA".equals(positionType)){
			Object saId = (Object) userLogin.getString("partyId");
			listSalesAdmin = FastList.newInstance();
			listSalesAdmin.add(saId);
		} else if(UtilValidate.isNotEmpty(positionType) && "SADM".equals(positionType)) {
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
			List<String> listEmployee = FastList.newInstance();
			if (ServiceUtil.isSuccess(ccc)) {
				listEmployee = (List<String>) ccc.get("listEmployee");
			}
			listSalesAdmin = new ArrayList<Object>(listEmployee);
		}
		
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), listSalesAdmin, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderCancelled", type.getOrderQuantityCancelledSA());
		return result;
	}
	
	public static Map<String, Object> getOrderQuantityCancelledDis(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		String userId = userLogin.getString("userLoginId");
		
		String userLogId = (String) userLogin.get("partyId");
		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
		String supId = listSupId.get(0);
		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
		String tmp_first = tmp1.get(0);
		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
		List<Object> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);

//		List<EntityCondition> listAllConditions = FastList.newInstance();
//        List<String> parties = FastList.newInstance();
//        
//        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//        	parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			parties = EntityUtil.getFieldListFromEntityList(
//					PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
//					, "partyId", true);
//		}
//        
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//        
//        List<Object> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		ResultValueSalesman type = new ResultValueSalesman(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), salesman, organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listOrderCancelled", type.getOrderQuantityCancelled());
		return result;
	}

}