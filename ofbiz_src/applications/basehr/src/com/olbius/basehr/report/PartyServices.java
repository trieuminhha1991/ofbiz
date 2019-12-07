package com.olbius.basehr.report;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastList;
import javolution.util.FastMap;

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
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.report.HROlap;
import com.olbius.basehr.report.HROlapImpl;
import com.olbius.basehr.report.PartyOlap;
import com.olbius.basehr.report.workprocess.PartyOlapImpl;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class PartyServices {
	
	public final static String module = PartyServices.class.getName();
	
	public static Map<String, Object> personBirth(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		Boolean gender = (Boolean) context.get("gender");
		
		if(gender == null) {
			gender = false;
		}
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		((HROlapImpl) olap).setLocale((Locale) context.get("locale"));
		
		try {
			olap.setGroup(group);
			olap.personBirth(gender);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> gender(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		((HROlapImpl) olap).setLocale((Locale) context.get("locale"));
		
		try {
			olap.setGroup(group);
			olap.gender();
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> member(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		List<?> groups = (List<?>) context.get("group[]");
		
		String group = (String) context.get("group");
		
		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String dateType = (String) context.get("dateType");
		
		Boolean child = (Boolean) context.get("child");
		
		Boolean cur = (Boolean) context.get("cur");
		
		if(child == null) {
			child = false;
		}
		
		if(cur == null) {
			cur = false;
		}
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			olap.member(dateType, cur, groups);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> personOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String dateType = (String) context.get("dateType");
		
		Boolean ft = (Boolean) context.get("ft");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			olap.personOlap(dateType, ft);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> school(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		String type = (String) context.get("type");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		((HROlapImpl) olap).setLocale((Locale) context.get("locale"));
		
		try {
			olap.setGroup(group);
			olap.school(type);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> position(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		String type = (String) context.get("type");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		try {
			olap.setGroup(group);
			olap.position(type);;
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> getAllPartyParent(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		List<GenericValue> values = null;
		
		Set<String> _set = new TreeSet<String>();
		
		Set<String> __set = new TreeSet<String>();
		
		try {
			values = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"), null, UtilMisc.toList("partyIdTo"), null, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		for(GenericValue value : values) {
			_set.add(value.getString("partyIdFrom"));
			__set.add(value.getString("partyIdTo"));
		}
		
		Set<String> _pr = new TreeSet<String>();
		
		for(String s : _set) {
			if(!__set.contains(s)) {
				_pr.add(s);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		List<String> parent = new ArrayList<String>();
		parent.addAll(_pr);
		result.put("parent", parent);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;

	}
	
	public static Map<String, Object> getChildParty(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		List<GenericValue> values = null;
		
		String parent = (String) context.get("parent");
		
		Set<String> _set = new TreeSet<String>();
		
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
		conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parent));
		
		try {
			values = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("partyIdTo"), null, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		for(GenericValue value : values) {
			_set.add(value.getString("partyIdTo"));
		}
		
		Map<String, Object> result = FastMap.newInstance();
		List<String> child = new ArrayList<String>();
		child.addAll(_set);
		result.put("child", child);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;

	}
	
	public static Map<String, Object> timeTracker(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String timeId = (String) context.get("timeId");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			((HROlap) olap).timeTracker(timeId);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> onTime(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String timeId = (String) context.get("timeId");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			((HROlap) olap).onTime(timeId);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("static-access")
	public static Map<String, Object> partyFluct(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String root = PartyUtil.getRootOrganization(delegator, userLoginId);
		String group = (String) context.get("group");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String dateType = (String) context.get("dateType");
		List<String> listGroupId = FastList.newInstance();
		try {
			if(UtilValidate.isNotEmpty(group)){
				Organization buildOrg = PartyUtil.buildOrg(delegator, group, true, false);
				List<GenericValue> listGroup = buildOrg.getAllDepartmentList(delegator);
				if(UtilValidate.isNotEmpty(listGroup)){
					listGroupId = EntityUtil.getFieldListFromEntityList(listGroup, "partyId", true);
					listGroupId.add(group);
				}else{
					listGroupId.add(group);
				}
			}else{
				Organization buildOrg = PartyUtil.buildOrg(delegator, root, true, false);
				List<GenericValue> listGroup = buildOrg.getAllDepartmentList(delegator);
				if(UtilValidate.isNotEmpty(listGroup)){
					listGroupId = EntityUtil.getFieldListFromEntityList(listGroup, "partyId", true);
					listGroupId.add(root);
				}else{
					listGroupId.add(root);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		PartyOlapImpl chart = new PartyOlapImpl();
		PartyOlapImpl.PartyFluctColumn column = chart.new PartyFluctColumn();
		PartyOlapImpl.PartyFluctColumnOut colout = chart.new PartyFluctColumnOut(chart, column);
		chart.setOlapResult(colout);
		
		chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		chart.setFromDate(fromDate);
		chart.setThruDate(thruDate);
		chart.putParameter(chart.GROUP, listGroupId);
		chart.putParameter(chart.TYPE, dateType);
		
		Map<String, Object> result = chart.execute(context);
		
		return result;
	}
	
	public static Map<String, Object> getRecruitmentReqruired(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> succesResult = ServiceUtil.returnSuccess();
		Timestamp now = UtilDateTime.nowTimestamp();
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String partyId = userLogin.getString("partyId");
		String userLoginId = userLogin.getString("userLoginId");
		String departmentId = "";
		BigDecimal total = new BigDecimal(0);
		List<GenericValue> recruitmentRequired = FastList.newInstance();
		List<String> emplPositionTypeList = FastList.newInstance();
		try {
			List<String> listDepartment = PartyUtil.getDepartmentOfEmployee(delegator, userLoginId, now, null);
			if(UtilValidate.isNotEmpty(listDepartment)){
				departmentId = listDepartment.get(0);
			}
			List<String> listDepartmentAndChild = FastList.newInstance();
			
			Organization buildOrg = PartyUtil.buildOrg(delegator, departmentId, true, false);
			List<GenericValue> listChildList = buildOrg.getAllDepartmentList(delegator);
			if(UtilValidate.isNotEmpty(listChildList)){
				listDepartmentAndChild = EntityUtil.getFieldListFromEntityList(listChildList, "partyId", true);
				listDepartmentAndChild.add(departmentId);
			}else{
				listDepartmentAndChild.add(departmentId);
			}
			
			List<GenericValue> emplPositionType = delegator.findList("EmplPosition", 
				EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listDepartmentAndChild), null, null, opts, false);
			if(UtilValidate.isNotEmpty(emplPositionType)){
				emplPositionTypeList = EntityUtil.getFieldListFromEntityList(emplPositionType, "emplPositionTypeId", true);
			}
			if(UtilValidate.isNotEmpty(emplPositionTypeList)){
				for (String s : emplPositionTypeList) {
					String customTimePeriodId = "";
					Timestamp fromDate = UtilDateTime.getMonthStart(now);
					java.sql.Date fromDate_date = new java.sql.Date(fromDate.getTime());
					List<EntityCondition> listCond1 = FastList.newInstance();
					listCond1.add(EntityCondition.makeCondition("fromDate", fromDate_date));
					listCond1.add(EntityCondition.makeCondition("periodTypeId", "MONTHLY"));
					List<GenericValue> customTimePeriod = delegator.findList("CustomTimePeriod",
							EntityCondition.makeCondition(listCond1, EntityJoinOperator.AND), null, null, opts, false);
					if(UtilValidate.isNotEmpty(customTimePeriod)){
						GenericValue g = EntityUtil.getFirst(customTimePeriod);
						customTimePeriodId = g.getString("customTimePeriodId");
					}
					
					List<EntityCondition> listCond = FastList.newInstance();
//					listCond.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
					listCond.add(EntityCondition.makeCondition("emplPositionTypeId", s));
					listCond.add(EntityCondition.makeCondition("statusId", "RECREQ_HR_WAIT"));
					
					recruitmentRequired = delegator.findList("RecruitmentRequire", 
							EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, opts, false);
					if(UtilValidate.isNotEmpty(recruitmentRequired)){
						GenericValue g = EntityUtil.getFirst(recruitmentRequired);
						BigDecimal q = g.getBigDecimal("quantity");
						total = total.add(q);
					}
				}	
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		succesResult.put("emplPositionTypeList", emplPositionTypeList);
		succesResult.put("total", total);
		return succesResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentRequiredIdDept(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> succesResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		EntityListIterator listIterator = null;
		try {
			List<String> emplPositionTypeList = FastList.newInstance();
			Map<String, Object> map = getRecruitmentReqruired(ctx, context);
			if(UtilValidate.isNotEmpty(map)){
				emplPositionTypeList = (List<String>) map.get("emplPositionTypeList");
			}
			if(UtilValidate.isNotEmpty(emplPositionTypeList)){
				listAllConditions.add(EntityCondition.makeCondition("emplPositionTypeId", EntityJoinOperator.IN, emplPositionTypeList));
				listAllConditions.add(EntityCondition.makeCondition("statusId", "RECREQ_HR_WAIT"));
			}
			listIterator = delegator.find("RecruitmentRequiredDetail", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		succesResult.put("listIterator", listIterator);
		return succesResult;
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> getRecruitmentPlanBoardSchedule(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> succesResult = ServiceUtil.returnSuccess();
		Timestamp now = UtilDateTime.nowTimestamp();
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyid = userLogin.getString("partyId");
		int total = 0;
		try {
			List<EntityCondition> listCond = FastList.newInstance();
			Timestamp t = UtilDateTime.getMonthStart(now);
			java.sql.Date date = new java.sql.Date(t.getTime());
//			listCond.add(EntityCondition.makeCondition("customTimeDate", date));
			listCond.add(EntityCondition.makeCondition("partyId", partyid));
			listCond.add(EntityCondition.makeCondition("recruitmentThruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, now));
			List<GenericValue> listPlanBoard = delegator.findList("RecruitmentPlanBoardAndRecruitment", 
					EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, opts, false);
			if(UtilValidate.isNotEmpty(listPlanBoard)){
				for (GenericValue g : listPlanBoard) {
					total += 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		succesResult.put("total", total);
		return succesResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentPlanTime(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> succesResult = ServiceUtil.returnSuccess();
		Timestamp now = UtilDateTime.nowTimestamp();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		EntityListIterator listIterator = null;
		
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = userLogin.getString("partyId");
			Timestamp t = UtilDateTime.getMonthStart(now);
			java.sql.Date date = new java.sql.Date(t.getTime());
//			listAllConditions.add(EntityCondition.makeCondition("customTimeDate", date));
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("recruitmentThruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, now));
			listIterator = delegator.find("RecruitmentPlanBoardAndRecruitment", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		succesResult.put("listIterator", listIterator);
		return succesResult;
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> getRecruitmentCandidateJudge(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> succesResult = ServiceUtil.returnSuccess();
		Timestamp now = UtilDateTime.nowTimestamp();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		List<String> listRecruitmentCurrentId = FastList.newInstance();
		int total = 0;
		List<GenericValue> listG1 = FastList.newInstance();
		try {
			Timestamp t = UtilDateTime.getMonthStart(now);
			java.sql.Date date = new java.sql.Date(t.getTime());
			List<EntityCondition> listCond = FastList.newInstance();
//			listCond.add(EntityCondition.makeCondition("customTimeDate", date));
			listCond.add(EntityCondition.makeCondition("partyId", partyId));
			listCond.add(EntityCondition.makeCondition("recruitmentThruDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO, now));
			
			List<GenericValue> listG = delegator.findList("RecruitmentPlanBoardAndRecruitment", 
					EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, opts, false);
			if(UtilValidate.isNotEmpty(listG)){
				listRecruitmentCurrentId = EntityUtil.getFieldListFromEntityList(listG, "recruitmentPlanId", true);
			}
			if(UtilValidate.isNotEmpty(listRecruitmentCurrentId)){
				List<EntityCondition> listCond1 = FastList.newInstance();
				listCond1.add(EntityCondition.makeCondition("recruitmentPlanId", EntityJoinOperator.IN, listRecruitmentCurrentId));
				listCond1.add(EntityCondition.makeCondition("interviewerId", partyId));
				listG1 = delegator.findList("RecruitmentPlanRoundInterviewer", 
						EntityCondition.makeCondition(listCond1, EntityJoinOperator.AND), null, null, opts, false);
				if(UtilValidate.isNotEmpty(listG1)){
					for (GenericValue g : listG1) {
						long r = g.getLong("roundOrder");
						List<EntityCondition> listCond2 = FastList.newInstance();
						listCond2.add(EntityCondition.makeCondition("recruitmentPlanId", g.getString("recruitmentPlanId")));
						listCond2.add(EntityCondition.makeCondition("roundOrder", r));
						listCond2.add(EntityCondition.makeCondition("statusId", "RR_RECRUITING"));
						List<GenericValue> listG2 = delegator.findList("RecruitmentRoundCandidate", 
								EntityCondition.makeCondition(listCond2, EntityJoinOperator.AND), null, null, opts, false);
						if(UtilValidate.isNotEmpty(listG2)){
							for (GenericValue g2 : listG2) {
								total += 1;
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		succesResult.put("listG", listG1);
		succesResult.put("total", total);
		return succesResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentCandidateWating(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> succesResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		EntityListIterator listIterator = null;
		try {
			Map<String, Object> map = getRecruitmentCandidateJudge(ctx, context);
			if(UtilValidate.isNotEmpty(map)){
				List<GenericValue> listG = (List<GenericValue>) map.get("listG");
				List<String> listRecruitmentPlanId = FastList.newInstance();
				List<Long> listRound = FastList.newInstance();
				if(UtilValidate.isNotEmpty(listG)){
					for (GenericValue g : listG) {
						long r = g.getLong("roundOrder");
						listRound.add(r);
						listRecruitmentPlanId.add(g.getString("recruitmentPlanId"));
					}
				}
				listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", EntityJoinOperator.IN, listRecruitmentPlanId));
				listAllConditions.add(EntityCondition.makeCondition("roundOrder", EntityJoinOperator.IN, listRound));
				listAllConditions.add(EntityCondition.makeCondition("statusId", "RR_RECRUITING"));
				listIterator = delegator.find("RecruitmentRoundCandidateDetail", 
						EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		succesResult.put("listIterator", listIterator);
		return succesResult;
	}
	
}
