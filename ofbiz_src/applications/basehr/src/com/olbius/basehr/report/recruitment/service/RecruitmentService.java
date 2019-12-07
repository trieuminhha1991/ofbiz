package com.olbius.basehr.report.recruitment.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.report.recruitment.query.RecruitmentAnalysisCostImpl;
import com.olbius.basehr.report.recruitment.query.RecruitmentCostImpl;
import com.olbius.basehr.report.recruitment.query.RecruitmentEffectiveImpl;
import com.olbius.basehr.report.recruitment.query.RecruitmentOlapImpl;
import com.olbius.basehr.report.recruitment.query.RecruitmentPlanBoardImpl;
import com.olbius.basehr.report.recruitment.query.RecruitmentRoundImpl;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.grid.OlapGrid;

public class RecruitmentService {
	@SuppressWarnings({ "static-access", "unchecked" })
	public static Map<String, Object> getSuccessfullCandidatesDetail(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<String> recruimentPlanId = (List<String>) context.get("recruimentPlanId[]");
		List<String> statusId_list = FastList.newInstance();
		statusId_list.add("RR_REC_RECEIVE");
		statusId_list.add("RR_REC_EMPL");
		RecruitmentOlapImpl grid = new RecruitmentOlapImpl(delegator);
		
		grid.setOlapResultType(OlapGrid.class);
		
		
		if(UtilValidate.isNotEmpty(recruimentPlanId)){
			grid.putParameter(grid.RECRUIT, recruimentPlanId);
		}
		grid.putParameter(grid.STATUS, statusId_list);
		
		Map<String, Object> result = grid.execute(context);
		return result;
		
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public static Map<String, Object> effectivelyRecruitingChart(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<String> recruimentPlanId = (List<String>) context.get("recruimentPlanId[]");
		
		RecruitmentEffectiveImpl chart = new RecruitmentEffectiveImpl(locale);
		RecruitmentEffectiveImpl.effectRecruitCol col = chart.new effectRecruitCol();
		RecruitmentEffectiveImpl.effectRecruitColOut colOut = chart.new effectRecruitColOut(chart, col);
		
		chart.setOlapResult(colOut);
		
		chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		if(UtilValidate.isNotEmpty(recruimentPlanId)){
			chart.putParameter(chart.RECRUIT, recruimentPlanId);
		}
		
		Map<String, Object> result = chart.execute(context);
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public static Map<String, Object> getRecruitmentPlanBoardOlap(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<String> recruimentPlanId = (List<String>) context.get("recruimentPlanId[]");
		RecruitmentPlanBoardImpl grid = new RecruitmentPlanBoardImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		if(UtilValidate.isNotEmpty(recruimentPlanId)){
			grid.putParameter(grid.RECUIT, recruimentPlanId);
		}
		Map<String, Object> result = grid.execute(context);
		return result;
		
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public static Map<String, Object> getRecruitmentRoundOlap(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<String> recruimentPlanId = (List<String>) context.get("recruimentPlanId[]");
		
		RecruitmentRoundImpl grid = new RecruitmentRoundImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		if(UtilValidate.isNotEmpty(recruimentPlanId)){
			grid.putParameter(grid.RECRUIT, recruimentPlanId);
		}
		Map<String, Object> result = grid.execute(context);
		return result;
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public static Map<String, Object> getRecruitmentCostOlap(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<String> recruimentPlanId = (List<String>) context.get("recruimentPlanId[]");
		
		RecruitmentCostImpl grid = new RecruitmentCostImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		if(UtilValidate.isNotEmpty(recruimentPlanId)){
			grid.putParameter(grid.RECRUIT, recruimentPlanId);
		}
		Map<String, Object> result = grid.execute(context);
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public static Map<String, Object> analysisRecruitCostChart(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String orgId = (String) context.get("orgId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		List<String> recruitCosCat = (List<String>) context.get("recruitCostCatTypeId[]");
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String rootOrg = PartyUtil.getRootOrganization(delegator, userLoginId);
		Organization buildOrg = PartyUtil.buildOrg(delegator, rootOrg, true, false);
		List<GenericValue> deptList = buildOrg.getDirectChildList(delegator);
		List<String> deptIdList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(deptList)){
			deptIdList = EntityUtil.getFieldListFromEntityList(deptList, "partyId", true);
		}
		
		List<GenericValue> listRecruitment = FastList.newInstance();
		try {
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(EntityCondition.makeCondition("recruitmentThruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			listCond.add(EntityCondition.makeCondition("recruitmentFromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			if(UtilValidate.isNotEmpty(orgId)){
				if(orgId.equals(rootOrg)){
					listCond.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, deptIdList));
				}else{
					listCond.add(EntityCondition.makeCondition("partyId",orgId));
				}
			}else{
				listCond.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, deptIdList));
			}
			listRecruitment = delegator.findList("RecruitmentPlan", 
					EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> listRecruitmentPlanId = FastList.newInstance();
		if(UtilValidate.isNotEmpty(listRecruitment)){
			listRecruitmentPlanId = EntityUtil.getFieldListFromEntityList(listRecruitment, "recruitmentPlanId", true);
		}
		RecruitmentAnalysisCostImpl chart = new RecruitmentAnalysisCostImpl(delegator);
		chart.setOlapResultType(OlapColumnChart.class);
		
		chart.putParameter(chart.RECRUIT, listRecruitmentPlanId);
		chart.putParameter(chart.RECRUITCOSTCAT, recruitCosCat);
		
		Map<String, Object> result = chart.execute(context);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruimentListByOrg(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String rootOrg = PartyUtil.getRootOrganization(delegator, userLoginId);
		Organization buildOrg = PartyUtil.buildOrg(delegator, rootOrg, true, false);
		List<GenericValue> deptList = buildOrg.getDirectChildList(delegator);
		List<String> deptIdList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(deptList)){
			deptIdList = EntityUtil.getFieldListFromEntityList(deptList, "partyId", true);
		}
		Map<String, Object> sucessResult = FastMap.newInstance();
		List<GenericValue> listRecruitment = FastList.newInstance();
		List<Map<String,Object>> listIterator = FastList.newInstance();
		try {
			List<EntityCondition> listCond = FastList.newInstance();
			if(UtilValidate.isNotEmpty(parameters)){
				if(parameters.containsKey("fromDate") && parameters.get("fromDate")[0] != null){
					String fromDateStr = parameters.get("fromDate")[0];
					Timestamp fromDate = DateUtil.convertStringTypeLongToTimestamp(fromDateStr);
					listCond.add(EntityCondition.makeCondition("recruitmentThruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
				}
				if(parameters.containsKey("thruDate") && parameters.get("thruDate")[0] != null){
					String thruDateStr = parameters.get("thruDate")[0];
					Timestamp thruDate = DateUtil.convertStringTypeLongToTimestamp(thruDateStr);
					listCond.add(EntityCondition.makeCondition("recruitmentFromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
				}
				if(parameters.containsKey("partyId") && parameters.get("partyId")[0] != null){
					String partyId = parameters.get("partyId")[0];
					if(partyId.equals(rootOrg)){
						listCond.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, deptIdList));
					}else{
						listCond.add(EntityCondition.makeCondition("partyId",partyId));
					}
				}
			}
			listRecruitment = delegator.findList("RecruitmentPlan", 
					EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, null, false);
			for (GenericValue g : listRecruitment) {
				Map<String, Object> maptmp = FastMap.newInstance();
				maptmp.put("value", g.getString("recruitmentPlanId"));
				maptmp.put("text", g.getString("recruitmentPlanName"));
				maptmp.put("partyId", g.getString("partyId"));
				listIterator.add(maptmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		sucessResult.put("listIterator", listIterator);
		return sucessResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitCostCatType(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> sucessResult = ServiceUtil.returnSuccess();
		List<Map<String,Object>> listIterator = FastList.newInstance();
		List<Map<String,Object>> listReturn = FastList.newInstance();
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		try {
			Map<String, Object> maptmp = getRecruimentListByOrg(ctx, context);
			listIterator = (List<Map<String,Object>>) maptmp.get("listIterator");
			List<String> listRecruitmentPlanId = FastList.newInstance();
			for (Map<String,Object> map : listIterator) {
				String s = (String) map.get("value");
				listRecruitmentPlanId.add(s);
			}
			List<GenericValue> listRecruitPlanCost = delegator.findList("RecruitmentPlanCostItem",
					EntityCondition.makeCondition("recruitmentPlanId", EntityJoinOperator.IN, listRecruitmentPlanId), null, null, opts, false);
			List<String> listRecruitPlanCostItemTypeId = FastList.newInstance();
			if(UtilValidate.isNotEmpty(listRecruitPlanCost)){
				listRecruitPlanCostItemTypeId = EntityUtil.getFieldListFromEntityList(listRecruitPlanCost, "recruitCostItemTypeId", true);
			}
			List<String> listRecruitCosCatTypeId = FastList.newInstance();
			if(UtilValidate.isNotEmpty(listRecruitPlanCostItemTypeId)){
				List<GenericValue> recruitPlanCosItemTypeList = delegator.findList("RecruitmentCostItemType",
						EntityCondition.makeCondition("recruitCostItemTypeId",EntityJoinOperator.IN, listRecruitPlanCostItemTypeId), null, null, opts, false);
				if(UtilValidate.isNotEmpty(recruitPlanCosItemTypeList)){
					listRecruitCosCatTypeId = EntityUtil.getFieldListFromEntityList(recruitPlanCosItemTypeList, "recruitCostCatTypeId", true);
				}
			}
			if(UtilValidate.isNotEmpty(listRecruitCosCatTypeId)){
				for (String s : listRecruitCosCatTypeId) {
					GenericValue tmp = delegator.findOne("RecruitmentCostCategoryType", UtilMisc.toMap("recruitCostCatTypeId", s), false);
					Map<String,Object> map = FastMap.newInstance();
					map.put("value", tmp.getString("recruitCostCatTypeId"));
					map.put("text", tmp.getString("recruitCostCatName"));
					listReturn.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sucessResult.put("listIterator", listReturn);
		return sucessResult;
		
	}
}
