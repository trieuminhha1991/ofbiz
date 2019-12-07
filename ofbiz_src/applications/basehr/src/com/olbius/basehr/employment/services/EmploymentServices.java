package com.olbius.basehr.employment.services;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

//import com.olbius.basesales.party.PartyWorker;
//import com.olbius.basesales.product.ProductStoreWorker;
//import com.olbius.basesales.util.ProcessConditionUtil;
//import com.olbius.basesales.util.SalesPartyUtil;
//import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.employment.helper.EmploymentHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.common.util.EntityMiscUtil;

public class EmploymentServices {
	public static final String module = EmploymentServices.class.getName();
	public static int transactionTimeout = 3000;
	
	public static Map<String, Object> updateEmploymentOfParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String terminationReasonId = (String)context.get("terminationReasonId");
		Map<String, Object> resultService = ServiceUtil.returnSuccess();
		if(fromDate != null){
			try {
				List<GenericValue> employmentExists = delegator.findByAnd("Employment", UtilMisc.toMap("fromDate", fromDate, "partyIdTo", partyId), UtilMisc.toList("-fromDate"), false);
				Map<String, Object> ctxMap = FastMap.newInstance();
				String serviceName = "";
				Timestamp currThruDate = null;
				if(UtilValidate.isNotEmpty(employmentExists)){
					GenericValue employment = employmentExists.get(0);
					serviceName = "updateEmployment";
					ctxMap.put("partyIdTo", partyId);
					ctxMap.put("partyIdFrom", employment.get("partyIdFrom"));
					ctxMap.put("fromDate", employment.get("fromDate"));
					ctxMap.put("roleTypeIdTo", employment.get("roleTypeIdTo"));
					ctxMap.put("roleTypeIdFrom", employment.get("roleTypeIdFrom"));
					currThruDate = employment.getTimestamp("thruDate");
				}else{
					serviceName = "createEmployment";
					ctxMap.put("partyIdTo", partyId);
					ctxMap.put("partyIdFrom", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
					ctxMap.put("fromDate", fromDate);
					ctxMap.put("roleTypeIdTo", "EMPLOYEE");
					ctxMap.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
					//currThruDate = thruDate;
				}
				ctxMap.put("thruDate", thruDate);
				ctxMap.put("terminationReasonId", terminationReasonId);
				ctxMap.put("userLogin", userLogin);
				resultService = dispatcher.runSync(serviceName, ctxMap);
				//update partyrelationship
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdFrom", partyId),
								EntityJoinOperator.OR,
								EntityCondition.makeCondition("partyIdTo", partyId)));
				if(thruDate == null && currThruDate != null){
					conditions.add(EntityCondition.makeCondition("thruDate", currThruDate));
				}else if(thruDate != null){
					EntityCondition cond1 = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
							EntityJoinOperator.OR,
							EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate));
					if(currThruDate != null){
						EntityCondition cond2 = EntityCondition.makeCondition("thruDate", currThruDate);
						conditions.add(EntityCondition.makeCondition(cond1, EntityJoinOperator.OR, cond2));
					}else{
						conditions.add(cond1);
					}
				}
				List<GenericValue> partyRel = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, null, null, false);
				for(GenericValue tempGv: partyRel){
					tempGv.set("thruDate", thruDate);
					tempGv.store();
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		return resultService;
	}
	
	public static Map<String, Object> updateSecurityGroupOfEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String statusId = (String) context.get("workingStatusId");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		try {
			List<GenericValue> userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId), null, false);
			for(GenericValue userLogin: userLoginList){
			    if(UtilValidate.isNotEmpty(statusId)){
			        if("EMPL_RESIGN".equals(statusId)||"EMPL_SACKING".equals(statusId)){
                        userLogin.set("enabled", "N");
                        userLogin.set("disabledDateTime", null);
                    }else if("EMPL_WORKING".equals(statusId)){
                        userLogin.set("enabled", "Y");
                    }
                    userLogin.store();
                }
				String userLoginId = userLogin.getString("userLoginId");
				List<GenericValue> userLoginSecurityGroupList = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId), null, false);
				for(GenericValue userLoginSecurityGroup: userLoginSecurityGroupList){
					userLoginSecurityGroup.set("thruDate", thruDate);
					userLoginSecurityGroup.store();
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getEmplPosition(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		retMap.put(ModelService.SUCCESS_MESSAGE, ModelService.SUCCESS_MESSAGE);
		try {			
			List<GenericValue> emplPosList = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
			if(UtilValidate.isNotEmpty(emplPosList)){
				GenericValue emplPos = EntityUtil.getFirst(emplPosList);
				retMap.put("emplPositionId", emplPos.getString("emplPositionId"));
				retMap.put("emplPositionTypeId", emplPos.getString("emplPositionTypeId"));
				GenericValue emplPosType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPos.getString("emplPositionTypeId")), false);
				retMap.put("emplPositionTypeDesc", emplPosType.getString("description"));
			}else{
				ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "EmployeeNotOccupyPosition", locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	
	public static Map<String, Object> removeEmplFromOrg(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String emplProposalId = (String)context.get("emplProposalId");
		String disableUserLogin = (String)context.get("disableUserLogin");
		String expireEmpl = (String)context.get("expireEmpl");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String message = "";
		try {
			GenericValue emplProposal = delegator.findOne("EmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId), false);
			if(emplProposal == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindProposal", UtilMisc.toMap("emplProposalId", emplProposalId), locale));
			}
			if(!userLogin.getString("partyId").equals(emplProposal.getString("partyId"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "YouHaveNotPermission", locale));
			}
			if(!"PPSL_ACCEPTED".equals(emplProposal.getString("statusId"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "ProposalNotAccepted", locale));
			}
			GenericValue emplProposalTermination = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplProposalId), false);
			Timestamp dateTermination = emplProposalTermination.getTimestamp("dateTermination");
			if(dateTermination == null){
				dateTermination = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			}
			String isCompleteFormality = emplProposalTermination.getString("isCompleteFormality");
			if("Y".equals(isCompleteFormality)){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "ProposalCompletedFormality", locale));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
			conditions.add(EntityCondition.makeCondition("roleTypeId", "PPSL_PROPOSED"));
			List<GenericValue> emplProposalRoleType = delegator.findList("EmplProposalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> proposedList = FastList.newInstance();
			String proposedName = "";
			if(UtilValidate.isNotEmpty(emplProposalRoleType)){
				proposedList = EntityUtil.getFieldListFromEntityList(emplProposalRoleType, "partyId", true);
				proposedName = PartyHelper.getPartyName(delegator ,proposedList.get(0), false);
			}
			if("Y".equals(disableUserLogin)){
				List<GenericValue> userLoginList;
				for(String partyId: proposedList){
					userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId), null, false);
					for(GenericValue tempGv: userLoginList){
						tempGv.set("enabled", "N");
						tempGv.store();
					}
				}
			}
			if("Y".equals(expireEmpl)){
				//expire emplPosition
				List<GenericValue> emplPositionFul;
				List<GenericValue> employment;
				List<GenericValue> partyRel;
				List<EntityCondition> employmentConds = FastList.newInstance();
				employmentConds.add(EntityUtil.getFilterByDateExpr());
				
				for(String tempPartyId: proposedList){
					emplPositionFul = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(), EntityOperator.AND, EntityCondition.makeCondition("partyId", tempPartyId)), 
																null, null, null, false);
					for(GenericValue tempGv: emplPositionFul){
						tempGv.set("thruDate", dateTermination);
						tempGv.store();
					}
					EntityCondition tempConds = EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(), 
																				EntityOperator.AND, 
																				EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", tempPartyId), 
																												EntityOperator.OR, 
																												EntityCondition.makeCondition("partyIdFrom", tempPartyId)));
					employment = delegator.findList("Employment", tempConds, null, null, null, false);
					partyRel = delegator.findList("PartyRelationship", tempConds, null, null, null, false);
					for(GenericValue tempGv: employment){
						tempGv.set("thruDate", dateTermination);
						tempGv.store();
					}
					for(GenericValue tempGv: partyRel){
						tempGv.set("thruDate", dateTermination);
						tempGv.store();
					}
				}
			}
			emplProposalTermination.set("isCompleteFormality", "Y");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(dateTermination.getTime());
			message = UtilProperties.getMessage("EmploymentUiLabels", "CompleteSackingEmplFormality", UtilMisc.toMap("dateTermination", cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH)+ 1) + "/" + cal.get(Calendar.YEAR), "proposedName", proposedName), locale);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(message);
	}
	
	/*
	 * Description : get List EmplWorkOverTime JQX
	 * @param DispathContext dpct
	 * @param Map<?,?> context
	 * @return
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListEmplWorkOverTime(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String managerId = ((GenericValue) context.get("userLogin")).getString("partyId");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listEmpl = null;
		List<GenericValue> listEmplOfManager = FastList.newInstance();
		List<String> listEmplId = FastList.newInstance();
		try {
			if(UtilValidate.isNotEmpty(managerId)){
				try {
					listEmplOfManager = PartyUtil.getListEmployeeOfManager(delegator,userLogin.getString("userLoginId"));
					if(UtilValidate.isNotEmpty(listEmplOfManager)){
						for(GenericValue empl : listEmplOfManager){
							if(UtilValidate.isNotEmpty(empl.getString("partyId"))){
								listEmplId.add(empl.getString("partyId"));
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError("Can't get list employee of manager!");
				}
			}
			if(UtilValidate.isNotEmpty(listEmplId)){
				listAllConditions.add(EntityCondition.makeCondition("partyId",EntityJoinOperator.IN,listEmplId));
			}
			if(UtilValidate.isNotEmpty(listSortFields)){
				try {
					listEmpl = delegator.find("WorkOvertimeRegistration", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError("Can't get list employee work over time!");
				}
			}else{
				//List<String> orderBy = FastList.newInstance();
				listEmpl = delegator.find("WorkOvertimeRegistration", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, null, opts);				}
			if(UtilValidate.isNotEmpty(listEmpl)){
				result.put("listIterator", listEmpl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	/*
	 * Description : get List EmplWorkOverTime JQX
	 * @param DispathContext dpct
	 * @param Map<?,?> context
	 * @return
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListEmplWorkLate(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		//List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		List<GenericValue> listEmplLateSummary = FastList.newInstance();
		List<Map<String,Object>> listIterator = FastList.newInstance();
		try {
			listEmplLateSummary = delegator.findList("EmplWorkingLateSummary", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, opts, false);
			if(UtilValidate.isNotEmpty(listEmplLateSummary)){
				for(GenericValue empl : listEmplLateSummary){
					if(UtilValidate.isNotEmpty(empl.getString("partyId"))){
						Map<String,Object> map = FastMap.newInstance();
						map.put("partyId", empl.getString("partyId"));
						List<GenericValue> listReason =  delegator.findByAnd("EmplWorkingLate", UtilMisc.toMap("partyId", empl.getString("partyId"), "reasonFlag", "Y"), null, false);
						List<GenericValue> listNoReason =  delegator.findByAnd("EmplWorkingLate", UtilMisc.toMap("partyId", empl.getString("partyId"), "reasonFlag", "N"), null, false);
						map.put("reasonQuantity",listReason.size());
						map.put("NoReasonQuantity",listNoReason.size());
						listIterator.add(map);
					}
				}
			}
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplListInOrg(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listReturn", listReturn);
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Delegator delegator = dctx.getDelegator();
		int totalRows = 0;
		String partyGroupId = (String)context.get("partyGroupId");
		if(partyGroupId == null){
			retMap.put("TotalRows", String.valueOf(totalRows));
			return retMap;
		}
		int size, page;
		try{
			size = Integer.parseInt((String)context.get("pagesize"));
		}catch(Exception e){
			size = 0;
		}
		try{
			page = Integer.parseInt((String)context.get("pagenum"));
		}catch(Exception e){
			page = 0;
		}
		int start = size * page;
		int end = start + size;
		try {
			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("partyId");
			listFieldInEntity.add("partyName");
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(UtilValidate.isNotEmpty(listSortFields)){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator);
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition(condsForFieldInEntity));
			}
			if(UtilValidate.isEmpty(listSortFields)){
				sortedFieldInEntity.add("firstName");
				emplList = EntityUtil.orderBy(emplList, sortedFieldInEntity);
			}
			boolean isFilterAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = emplList.size();
				if(end > emplList.size()){
					end = emplList.size();
				}
				emplList = emplList.subList(start, end);
			}else{
				isFilterAdvance = true;
			}
			
			for(GenericValue tempGv: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = tempGv.getString("partyId");
				List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
				if(departmentList != null){
					List<String> departmentNames = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", departmentList, "partyId", "groupName");
					String departmentName = StringUtils.join(departmentNames, ", ");
					List<GenericValue> emplPos = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
					List<String> emplPosDes = EntityUtil.getFieldListFromEntityList(emplPos, "description", true);
					tempMap.put("partyId", partyId);
					tempMap.put("partyName", tempGv.get("partyName"));
					tempMap.put("emplPositionType", StringUtils.join(emplPosDes, ", "));
					tempMap.put("department", departmentName);
					listReturn.add(tempMap);
				}
			}
			if(isFilterAdvance){
				if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
					listReturn = EntityConditionUtils.doFilter(listReturn, condsForFieldNotInEntity);
				}
				if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
					listReturn = EntityConditionUtils.sortList(listReturn, sortedFieldNotInEntity);
				}
				totalRows = listReturn.size();
				if(end > totalRows){
					end = totalRows;
				}
				listReturn = listReturn.subList(start, end);
			}
			retMap.put("TotalRows", String.valueOf(totalRows));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jQGetEmplListInOrg(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		Delegator delegator = dctx.getDelegator();
		int totalRows = 0;
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>)context.get("listSortFields");
    	//EntityFindOptions opts = (EntityFindOptions)context.get("opts");
    	String partyGroupId = request.getParameter("partyGroupId");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		retMap.put("listIterator", listReturn);
		if(partyGroupId != null){
			try {
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator, null, null, listSortFields, EntityMiscUtil.processSplitListAllCondition(listAllConditions));
				totalRows = emplList.size();
				if(end > totalRows){
					end = totalRows;
				}
				emplList = emplList.subList(start, end);
				for(GenericValue tempGv: emplList){
					Map<String, Object> tempMap = tempGv.getAllFields();
					String department = tempGv.getString("department");
					String emplPositionType = tempGv.getString("emplPositionType");
					department = CommonUtil.cleanJoinStringValue(department, ",");
					emplPositionType = CommonUtil.cleanJoinStringValue(emplPositionType, ",");
					tempMap.put("emplPositionType", emplPositionType);
					tempMap.put("department", department);
					listReturn.add(tempMap);
				}
			} catch(GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	/*
	 * get Detail Of Empl Working Late 
	 * @param dpct
	 * @param context
	 * @return
	 * @param Exception
	 *	
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetInfoDetailsEmplworkingLate(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		String partyId = (String) parameters.get("partyId")[0];
		EntityListIterator InfoEmplWorkingLateDetail = null;
		Map<String,Object> result = FastMap.newInstance();
		try {
			listAllConditions.add( EntityCondition.makeCondition("partyId",partyId));
			if(UtilValidate.isNotEmpty(partyId)){
				InfoEmplWorkingLateDetail = delegator.find("EmplWorkingLate",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
			if(UtilValidate.isNotEmpty(InfoEmplWorkingLateDetail)){
				result.put("listIterator", InfoEmplWorkingLateDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	} 	
	/*
	 * get Detail Of Empl Working Late 
	 * @param dpct
	 * @param context
	 * @return
	 * @param Exception
	 *	
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListEmplClaims(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator EmplClaimsList = null;
		Map<String,Object> result = FastMap.newInstance();
		try {
			EmplClaimsList = delegator.find("EmplClaim",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(EmplClaimsList)){
				result.put("listIterator", EmplClaimsList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	/**
	 * JQgetFullNameEmployee
	 * @param DispatchContext
	 * @param Context
	 * @return
	 * @Exception
	 * 
	 * */
	public static Map<String,Object> JQgetFullNameEmployee(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		Map<String,Object> result = FastMap.newInstance();
		String partyId = (String) context.get("inputValue");
		try {
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue person = delegator.findOne("Person", false, UtilMisc.toMap("partyId", partyId));
				if(UtilValidate.isNotEmpty(person)){
					String fullName = "";
					fullName = UtilValidate.isNotEmpty(person.getString("lastName")) ? person.getString("lastName") : "";
					fullName += UtilValidate.isNotEmpty(person.getString("middleName")) ? " " + person.getString("middleName") : "";	
					fullName += UtilValidate.isNotEmpty(person.getString("firstName")) ? " " + person.getString("firstName") : "";
					if(UtilValidate.isNotEmpty(fullName)){
						result.put("outputValue", fullName);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Can't get full Name employee" + e.getMessage());
		}
	return result;	
	}
	
	/**
	 * JQgetEmployeeInOrg
	 * @param DispatchContext
	 * @param Context
	 * @return
	 * @Exception
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetEmployeeInOrg(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		try {
			List<GenericValue> listRole = delegator.findList("PartyRole", EntityCondition.makeCondition("roleTypeId","EMPLOYEE"), null, null, null, false);
			List<String> listPartyId = FastList.newInstance();
			for(GenericValue role : listRole){
				if(UtilValidate.isNotEmpty(role) && !listPartyId.contains(role.getString("partyId"))){
					listPartyId.add(role.getString("partyId"));
				}	
			}
			listAllConditions.add(EntityCondition.makeCondition("partyId",EntityJoinOperator.IN,listPartyId));
			EntityListIterator listEmpl = delegator.find("Person", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND),null,null,listSortFields,opts);
			if(UtilValidate.isNotEmpty(listEmpl)){
				result.put("listIterator", listEmpl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get list employee in Org" + e.getMessage());
		}
		return result;		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplWorkOvertimeRegis(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Map<String,Object> result = FastMap.newInstance();
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		listSortFields.add("dateRegistration");
		try {
			EntityListIterator listOvertimeWorking = delegator.find("WorkOvertimeRegistration", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			result.put("listIterator", listOvertimeWorking);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> createEmplWorkingLateExt(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		String partyIdJson = (String)context.get("partyId");
		String dateWorkingLateStr = (String)context.get("dateWorkingLate");
		String delayTimeStr = (String)context.get("delayTime");
		JSONArray partyIdArr = JSONArray.fromObject(partyIdJson);
		Timestamp dateWorkingLate = new Timestamp(Long.parseLong(dateWorkingLateStr));
		Long delayTime = Long.parseLong(delayTimeStr);
		String statusId = (String)context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String reason = (String)context.get("reason");
		
			for(int i = 0; i < partyIdArr.size(); i++){
				String partyId = partyIdArr.getJSONObject(i).getString("partyId");
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("partyId", partyId);
				ctxMap.put("dateWorkingLate", dateWorkingLate);
				ctxMap.put("delayTime", delayTime);
				ctxMap.put("statusId", statusId);
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("reason", reason);
				try {
					dispatcher.runSync("createEmplWorkingLate", ctxMap);
				} catch (GenericServiceException e) {
					
					e.printStackTrace();
				}
			}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "createSuccessfully", locale));
	}
	
	public static Map<String, Object> createEmplWorkingLate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");
		Timestamp dateWorkingLate = (Timestamp)context.get("dateWorkingLate");
		Timestamp startDate = UtilDateTime.getDayStart(dateWorkingLate);
		Timestamp endDate = UtilDateTime.getDayEnd(dateWorkingLate);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, startDate));
		conditions.add(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.LESS_THAN_EQUAL_TO, endDate));
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "createSuccessfully", locale)); 
		try {
			List<GenericValue> emplWorkingLate = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(emplWorkingLate)){
				GenericValue existEntity = emplWorkingLate.get(0);
				Timestamp tmpTimestamp = existEntity.getTimestamp("dateWorkingLate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(tmpTimestamp);
				String dateStr = DateUtil.getDateMonthYearDesc(cal);
				String partyName = PartyUtil.getPersonName(delegator, partyId);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "EmployeeDateWorkingLateExists", UtilMisc.toMap("dateWorkingLate", dateStr, "employeeName", partyName), locale));
			}
			GenericValue newEntity = delegator.makeValue("EmplWorkingLate");
			newEntity.setNonPKFields(context);
			String emplWorkingLateId = delegator.getNextSeqId("EmplWorkingLate");
			newEntity.set("emplWorkingLateId", emplWorkingLateId);
			newEntity.create();
			retMap.put("emplWorkingLateId", emplWorkingLateId);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> deleteEmplWorkingLate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String emplWorkingLateId = (String)context.get("emplWorkingLateId");
		try {
			GenericValue emplWorkingLate = delegator.findOne("EmplWorkingLate", UtilMisc.toMap("emplWorkingLateId", emplWorkingLateId), false);
			if(emplWorkingLate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "CannotFindRecordToDelete", locale));
			}
			emplWorkingLate.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplPositionInOrg(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyGroupId = request.getParameter("partyGroupId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
		int size, page = -1;
		String actualFromDateStr = request.getParameter("actualFromDate");
		String actualThruDateStr = request.getParameter("actualThruDate");
		Timestamp actualFromDate = null, actualThruDate = null;
		
		if(actualFromDateStr != null){
			actualFromDate = new Timestamp(Long.parseLong(actualFromDateStr));
		}
		if(actualThruDateStr != null){
			actualThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(actualThruDateStr)));
		}
	
		try{
			size = Integer.parseInt(parameters.get("pagesize")[0]);
		}catch(Exception e){
			size = -1;
		}
    	try{
    		page = Integer.parseInt(parameters.get("pagenum")[0]);
    	}catch(Exception e){
    		page = -1;
    	}
		
		int start = size * page;
		int end = start + size;		
		try {
			if(partyGroupId == null){
				if(userLogin != null){
					partyGroupId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				}else{
					partyGroupId = PartyUtil.getCurrentOrganization(delegator, null);
				}
			}
			boolean isManagePartyGroup = PartyUtil.checkPartyManageOrg(delegator, userLogin.getString("userLoginId"), partyGroupId, actualFromDate, actualThruDate);
			if(!isManagePartyGroup){
				return ServiceUtil.returnError("You don't manage organization " + PartyHelper.getPartyName(delegator, partyGroupId, false));
			}
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> partyListGv =  buildOrg.getChildList();
			List<String> partyListId = EntityUtil.getFieldListFromEntityList(partyListGv, "partyId", true);
			if(partyListId == null){
				partyListId = FastList.newInstance();
			}
			partyListId.add(partyGroupId);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyListId));
			conditions.add(EntityConditionUtils.makeDateConds(actualFromDate, actualThruDate, "actualFromDate", "actualThruDate"));
			
			List<GenericValue> allPositionInOrg = delegator.findList("EmplPositionSummary", EntityCondition.makeCondition(conditions), 
																		UtilMisc.toSet("partyId", "emplPositionTypeId", "totalEmplPositionId"), UtilMisc.toList("partyId", "emplPositionTypeId"), null, false);

			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("partyId");
			listFieldInEntity.add("totalEmplPositionId");
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(UtilValidate.isNotEmpty(listSortFields)){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				EntityCondition condition=EntityCondition.makeCondition(condsForFieldInEntity,EntityOperator.AND);
				List<GenericValue> listValue= new FastList<GenericValue>();
				for(GenericValue item:allPositionInOrg)
				{
					BigDecimal totalEmplPositionId= new BigDecimal(item.get("totalEmplPositionId").toString());
					item.set("totalEmplPositionId",totalEmplPositionId);
					if(condition.entityMatches(item))
					{
						listValue.add(item);
					}
				}
				allPositionInOrg=listValue;
			}
			if(UtilValidate.isEmpty(sortedFieldNotInEntity)){
				if(UtilValidate.isEmpty(sortedFieldInEntity)){
					sortedFieldInEntity.add("partyId");
				}
				allPositionInOrg = EntityUtil.orderBy(allPositionInOrg, sortedFieldInEntity);
			}
			
			boolean isFilterAndSortAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				if(end > allPositionInOrg.size()){
					end = allPositionInOrg.size();
				}
				totalRows = allPositionInOrg.size();
				allPositionInOrg = allPositionInOrg.subList(start, end);
			}
			else{
				isFilterAndSortAdvance = true;
			}
			
			for(GenericValue tempGv: allPositionInOrg){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = tempGv.getString("partyId");
				String emplPositionTypeId = tempGv.getString("emplPositionTypeId");
				
				List<String> positionNotFulfillment = EmploymentHelper.getPositionNotFulfillInPeriod(delegator, partyId, emplPositionTypeId, actualFromDate, actualThruDate);
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyHelper.getPartyName(delegator, partyId, false));
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				if(emplPositionType != null){
					tempMap.put("emplPositionTypeId", emplPositionTypeId);
					tempMap.put("emplPositionTypeDesc", emplPositionType.getString("description"));
				}else{
					System.err.println("partyId: " + partyId + "emplPositionTypeId: " + emplPositionTypeId);
				}
				tempMap.put("totalEmplPositionId", tempGv.getString("totalEmplPositionId"));
				tempMap.put("totalEmplPosNotFulfill", positionNotFulfillment != null? new BigDecimal(positionNotFulfillment.size()) : BigDecimal.ZERO);
				listReturn.add(tempMap);
			}
			if(isFilterAndSortAdvance){
				if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
					listReturn = EntityConditionUtils.doFilter(listReturn, condsForFieldNotInEntity);
				}
				if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
					listReturn = EntityConditionUtils.sortList(listReturn, sortedFieldNotInEntity);
				}
				if(end > listReturn.size()){
					end = listReturn.size();
				}
				totalRows = listReturn.size();
				listReturn = listReturn.subList(start, end);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		retMap.put("listIterator", listReturn);
		return retMap;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> getEmployeeListDetailInfo(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate);
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		String sortFieldNotInEntity = null;
		try {
			if(partyGroupId == null){
				partyGroupId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			}
			//FIXME need edit check party is manage org or child
			//boolean isManagePartyGroup = PartyUtil.checkPartyManageOrg(delegator, userLogin.getString("userLoginId"), partyGroupId, fromDate, thruDate);
			boolean isManagePartyGroup = true;
			if(!isManagePartyGroup){
				return ServiceUtil.returnError("You don't manage organization " + PartyHelper.getPartyName(delegator, partyGroupId, false));
			}
			List<EntityCondition> conditionInEntity = FastList.newInstance();
			List<EntityCondition> conditionNotInEntity = FastList.newInstance();
			Calendar cal = Calendar.getInstance();
			for(EntityCondition condition: listAllConditions){
				String cond = condition.toString();
				if(UtilValidate.isNotEmpty(cond)){
					String[] conditionSplit = cond.split(" ");
                    String fieldName = conditionSplit.length > 0 ? (String) conditionSplit[0] : null;
                    String operator = conditionSplit.length > 1 ? (String) conditionSplit[1] : null;
                    String value = conditionSplit.length > 2 ? (String) conditionSplit[2].trim() : null;

                    fieldName = EntityMiscUtil.cleanFieldName(fieldName);
					if(fieldName.contains("seniorityMonth")){
						EntityComparisonOperator<?, ?> condOpt = null;
						if(operator.equals("=")){
							condOpt = EntityJoinOperator.EQUALS;
						}else if(operator.equals(">=")){
							condOpt = EntityJoinOperator.LESS_THAN_EQUAL_TO;
						}else if(operator.equals("<=")){
							condOpt = EntityJoinOperator.GREATER_THAN_EQUAL_TO;
						}else if(operator.equals(">")){
							condOpt = EntityJoinOperator.LESS_THAN;
						}else if(operator.equals("<")){
							condOpt = EntityJoinOperator.GREATER_THAN;
						}else if(operator.equals("<>")){
							condOpt = EntityJoinOperator.NOT_EQUAL;
						}
						value = EntityMiscUtil.cleanValue(value);
						int totalMonth = Integer.parseInt(value);
						cal.add(Calendar.MONTH, -totalMonth);
						Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
						conditionInEntity.add(EntityCondition.makeCondition("dateJoinCompany", condOpt, timestamp));
					}else if(fieldName.contains("agreementDate") || fieldName.contains("agreementTypeId") || fieldName.contains("agreementThruDate")){
						conditionNotInEntity.add(condition);
					}else{
						conditionInEntity.add(condition);
					}
				}
			}
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}else{
				String sortField = listSortFields.get(0);
				if(sortField.contains("agreementDate") || sortField.contains("agreementTypeId") || sortField.contains("agreementThruDate")){
					listSortFields.clear();
					sortFieldNotInEntity = sortField;
				}else if(sortField.equals("seniorityMonth")){
					listSortFields.clear();
					listSortFields.add("-dateJoinCompany");
				}
			}
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<Map<String, Object>> listAllCond=FastList.newInstance();
			for (EntityCondition condition : conditionInEntity) {
				String cond = condition.toString();
				if(UtilValidate.isNotEmpty(cond)){
					if(cond.contains("%"))
					{
						int startcond=cond.indexOf('%');
						int endcond=cond.lastIndexOf('%');
						String subcond=cond.substring(startcond,endcond) ;
						String subcond2=subcond.replace(" ","_");
						cond=cond.replaceFirst(subcond,subcond2);
					}
					String[] conditionSplit = cond.split(" ");
					Map<String, Object> condMap = FastMap.newInstance();
					String fieldName = (String) conditionSplit[0];
					String operator = (String) conditionSplit[1];
					String value = (String) conditionSplit[2].trim();
					if (conditionSplit.length > 3) {
						if (UtilValidate.isNotEmpty(conditionSplit[3].trim())) {
							if ("AND".equals(conditionSplit[3].trim())) {
								operator = "RANGE";
								String valueFrom = (String) conditionSplit[2].trim();
								String valueTo = (String) conditionSplit[6].trim();
								valueFrom = EntityMiscUtil.cleanValue(valueFrom);
								valueTo = EntityMiscUtil.cleanValue(valueTo);

								condMap.put("valueFrom", valueFrom);
								condMap.put("valueTo", valueTo);
							}
						}
					}
					boolean hasUpper = fieldName.contains("UPPER(");
					fieldName = EntityMiscUtil.cleanFieldName(fieldName);
					value = EntityMiscUtil.cleanValue(value);
					value=value.replace("_"," ");
					condMap.put("fieldName", fieldName);
					condMap.put("operator",operator );
					condMap.put("value", value);
					condMap.put("hasUpper", hasUpper);
					listAllCond.add(condMap);

				}
			}
			if(UtilValidate.isNotEmpty(listAllCond)) {
				for(int i=0;i<listAllCond.size();i++) {
					Map<String, Object> map = listAllCond.get(i);
					String mapstring = map.get("fieldName").toString();
					if (mapstring.charAt(0) == '(') {
						mapstring = mapstring.substring(1);
						map.put("fieldName", mapstring);
						listAllCond.remove(i);
						listAllCond.add(i, map);
					}
				}
			}
			List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator, fromDate, thruDate, listSortFields, listAllCond);
			Map<String, Object> resultService = null;
			cal = Calendar.getInstance();
			java.sql.Date nowDate = new java.sql.Date(cal.getTimeInMillis());
			boolean totalRowIsSet = false;
			if(UtilValidate.isEmpty(conditionNotInEntity)){
				totalRows = emplList.size();
				if(end > totalRows){
					end = totalRows;
				}
				if(size > 0){
					emplList = emplList.subList(start, end);
				}
				totalRowIsSet = true;
			}
			for(GenericValue employeeGv: emplList){
				Map<String, Object> tempMap = employeeGv.getAllFields();
				String partyId = employeeGv.getString("partyId");
				Timestamp dateJoinCompany = employeeGv.getTimestamp("dateJoinCompany");
				//String employeeTypeDesc = employeeGv.getString("employeeTypeDesc");
				String department = employeeGv.getString("department");
				String emplPositionType = employeeGv.getString("emplPositionType");
				//employeeTypeDesc = CommonUtil.cleanJoinStringValue(employeeTypeDesc, ",");
				department = CommonUtil.cleanJoinStringValue(department, ",");
				emplPositionType = CommonUtil.cleanJoinStringValue(emplPositionType, ",");
				//tempMap.put("employeeTypeDesc", employeeTypeDesc);
				tempMap.put("emplPositionType", emplPositionType);
				tempMap.put("department", department);
				if(dateJoinCompany != null){
					int seniorityMonth = DateUtil.getMonthBetweenTwoDate(new java.sql.Date(dateJoinCompany.getTime()), nowDate);
					if(seniorityMonth > 12){
						seniorityMonth = 12;
					}
					tempMap.put("seniorityMonth", seniorityMonth);
				}
				resultService = dispatcher.runSync("getAgreementEffectivePartyInPeriod", 
						UtilMisc.toMap("partyIdTo", partyId, "fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));	
				if(ServiceUtil.isSuccess(resultService)){
					String agreementId = (String)resultService.get("agreementId");
					if(agreementId != null){
						GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
						tempMap.put("agreementDate", agreement.getTimestamp("agreementDate"));
						String agreementTypeId = agreement.getString("agreementTypeId");
						tempMap.put("agreementTypeId", agreementTypeId);
						Timestamp agreementThruDate = agreement.getTimestamp("thruDate");
						tempMap.put("agreementThruDate", agreementThruDate);
					}
				}
				if(tempMap.get("workingStatusId")!=null&&tempMap.get("workingStatusId").equals("EMPL_RESIGN")) {
					GenericValue employment = EmployeeHelper.getEmploymentOfParty(delegator, partyId, userLogin);
					String terminationReasonId = employment.getString("terminationReasonId");
					tempMap.put("terminationReasonId",terminationReasonId);
				}
				listReturn.add(tempMap);
			}
			if(UtilValidate.isNotEmpty(conditionNotInEntity)){
				listReturn = EntityMiscUtil.filterMap(listReturn, conditionNotInEntity);
			}
			if(sortFieldNotInEntity != null){
				listReturn = EntityMiscUtil.sortList(listReturn, UtilMisc.toList(sortFieldNotInEntity));
			}
			if(!totalRowIsSet && size > 0){
				totalRows = listReturn.size();
				if(end > totalRows){
					end = totalRows;
				}
				listReturn = listReturn.subList(start, end);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (ParseException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		retMap.put("TotalRows", String.valueOf(totalRows));
		retMap.put("listIterator", listReturn);
		return retMap;
	}

	public static Map<String, Object> jqGetListEmployeeDetailInfo(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> retMap = FastMap.newInstance();
		//HttpServletRequest request = (HttpServletRequest)context.get("request");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		String partyGroupId = "";
		if (parameters.containsKey("partyGroupId") && UtilValidate.isNotEmpty(parameters.get("partyGroupId"))){
			partyGroupId = parameters.get("partyGroupId")[0];
		}
		String fromDateStr = null;
		if (parameters.containsKey("fromDate") && UtilValidate.isNotEmpty(parameters.get("fromDate"))) {
			fromDateStr = parameters.get("fromDate")[0];
		}
		String thruDateStr = null;
		if (parameters.containsKey("thruDate") && UtilValidate.isNotEmpty(parameters.get("thruDate"))) {
			thruDateStr = parameters.get("thruDate")[0];
		}
		/*String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");*/
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate);
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");

		//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		String sortFieldNotInEntity = null;
		try {
			if(partyGroupId == null){
				partyGroupId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			}
			//FIXME need edit check party is manage org or child
			//boolean isManagePartyGroup = PartyUtil.checkPartyManageOrg(delegator, userLogin.getString("userLoginId"), partyGroupId, fromDate, thruDate);
			boolean isManagePartyGroup = true;
			if(!isManagePartyGroup){
				return ServiceUtil.returnError("You don't manage organization " + PartyHelper.getPartyName(delegator, partyGroupId, false));
			}
			List<EntityCondition> conditionInEntity = FastList.newInstance();
			List<EntityCondition> conditionNotInEntity = FastList.newInstance();
			Calendar cal = Calendar.getInstance();
			for(EntityCondition condition: listAllConditions){
				String cond = condition.toString();
				if(UtilValidate.isNotEmpty(cond)){
					String[] conditionSplit = cond.split(" ");
					String fieldName = (String) conditionSplit[0];
					String operator = (String) conditionSplit[1];
					String value = (String) conditionSplit[2].trim();
					fieldName = EntityMiscUtil.cleanFieldName(fieldName);
					if(fieldName.contains("seniorityMonth")){
						EntityComparisonOperator<?, ?> condOpt = null;
						if(operator.equals("=")){
							condOpt = EntityJoinOperator.EQUALS;
						}else if(operator.equals(">=")){
							condOpt = EntityJoinOperator.LESS_THAN_EQUAL_TO;
						}else if(operator.equals("<=")){
							condOpt = EntityJoinOperator.GREATER_THAN_EQUAL_TO;
						}else if(operator.equals(">")){
							condOpt = EntityJoinOperator.LESS_THAN;
						}else if(operator.equals("<")){
							condOpt = EntityJoinOperator.GREATER_THAN;
						}else if(operator.equals("<>")){
							condOpt = EntityJoinOperator.NOT_EQUAL;
						}
						value = EntityMiscUtil.cleanValue(value);
						int totalMonth = Integer.parseInt(value);
						cal.add(Calendar.MONTH, -totalMonth);
						Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
						conditionInEntity.add(EntityCondition.makeCondition("dateJoinCompany", condOpt, timestamp));
					}else if(fieldName.contains("agreementDate") || fieldName.contains("agreementTypeId") || fieldName.contains("agreementThruDate")){
						conditionNotInEntity.add(condition);
					}else{
						conditionInEntity.add(condition);
					}
				}
			}
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}else{
				String sortField = listSortFields.get(0);
				if(sortField.contains("agreementDate") || sortField.contains("agreementTypeId") || sortField.contains("agreementThruDate")){
					listSortFields.clear();
					sortFieldNotInEntity = sortField;
				}else if(sortField.equals("seniorityMonth")){
					listSortFields.clear();
					listSortFields.add("-dateJoinCompany");
				}
			}
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<Map<String, Object>> listAllCond = EntityMiscUtil.processSplitListAllCondition(conditionInEntity);
			List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator, fromDate, thruDate, listSortFields, listAllCond);
			Map<String, Object> resultService = null;
			cal = Calendar.getInstance();
			java.sql.Date nowDate = new java.sql.Date(cal.getTimeInMillis());
			boolean totalRowIsSet = false;
			if(UtilValidate.isEmpty(conditionNotInEntity)){
				totalRows = emplList.size();
				if(end > totalRows){
					end = totalRows;
				}
				if(size > 0){
					emplList = emplList.subList(start, end);
				}
				totalRowIsSet = true;
			}
			for(GenericValue employeeGv: emplList){
				Map<String, Object> tempMap = employeeGv.getAllFields();
				String partyId = employeeGv.getString("partyId");
				Timestamp dateJoinCompany = employeeGv.getTimestamp("dateJoinCompany");
				//String employeeTypeDesc = employeeGv.getString("employeeTypeDesc");
				String department = employeeGv.getString("department");
				String emplPositionType = employeeGv.getString("emplPositionType");
				//employeeTypeDesc = CommonUtil.cleanJoinStringValue(employeeTypeDesc, ",");
				department = CommonUtil.cleanJoinStringValue(department, ",");
				emplPositionType = CommonUtil.cleanJoinStringValue(emplPositionType, ",");
				//tempMap.put("employeeTypeDesc", employeeTypeDesc);
				tempMap.put("emplPositionType", emplPositionType);
				tempMap.put("department", department);
				if(dateJoinCompany != null){
					int seniorityMonth = DateUtil.getMonthBetweenTwoDate(new java.sql.Date(dateJoinCompany.getTime()), nowDate);
					if(seniorityMonth > 12){
						seniorityMonth = 12;
					}
					tempMap.put("seniorityMonth", seniorityMonth);
				}
				resultService = dispatcher.runSync("getAgreementEffectivePartyInPeriod",
						UtilMisc.toMap("partyIdTo", partyId, "fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
				if(ServiceUtil.isSuccess(resultService)){
					String agreementId = (String)resultService.get("agreementId");
					if(agreementId != null){
						GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
						tempMap.put("agreementDate", agreement.getTimestamp("agreementDate"));
						String agreementTypeId = agreement.getString("agreementTypeId");
						tempMap.put("agreementTypeId", agreementTypeId);
						Timestamp agreementThruDate = agreement.getTimestamp("thruDate");
						tempMap.put("agreementThruDate", agreementThruDate);
					}
				}
				listReturn.add(tempMap);
			}

			if(UtilValidate.isNotEmpty(conditionNotInEntity)){
				listReturn = EntityMiscUtil.filterMap(listReturn, conditionNotInEntity);
			}
			if(sortFieldNotInEntity != null){
				listReturn = EntityMiscUtil.sortList(listReturn, UtilMisc.toList(sortFieldNotInEntity));
			}
			if(!totalRowIsSet && size > 0){
				totalRows = listReturn.size();
				if(end > totalRows){
					end = totalRows;
				}
				listReturn = listReturn.subList(start, end);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (ParseException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		retMap.put("listIterator", listReturn);
		return retMap;
		//successResult.put("listIterator", listIterator);
		//return successResult;
	}
	
	public static Map<String, Object> getPositionByPositionTypeAndParty(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int size, page;
		try{
			size = Integer.parseInt((String)context.get("pagesize"));
		}catch(Exception e){
			size = 0;
		}
		try{
			page = Integer.parseInt((String)context.get("pagenum"));
		}catch(Exception e){
			page = 0;
		}
		
		int start = size * page;
		int end = start + size;
		Timestamp actualFromDate = (Timestamp)context.get("fromDate");
		Timestamp actualThruDate = (Timestamp)context.get("thruDate");
		int totalRows = 0;
    	String partyId = (String)context.get("partyId");
    	String emplPositionTypeId = (String)context.get("emplPositionTypeId");
    	List<EntityCondition> conditions = FastList.newInstance();
    	conditions.add(EntityCondition.makeCondition("partyId", partyId));
    	conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
    	conditions.add(EntityConditionUtils.makeDateConds(actualFromDate, actualThruDate, "actualFromDate", "actualThruDate"));
    	conditions.add(EntityConditionUtils.makeDateConds(actualFromDate, actualThruDate));
    	try {
			List<GenericValue> emplPositionList = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("employeePartyId"), null, false);
			List<String> emplPositionNotFullfillId = EmploymentHelper.getPositionNotFulfillInPeriod(delegator, partyId, emplPositionTypeId, actualFromDate, actualThruDate);
			List<GenericValue> emplPositionNotFullfill = delegator.findList("EmplPositionAndType", EntityCondition.makeCondition("emplPositionId", EntityJoinOperator.IN, emplPositionNotFullfillId), null, null, null, false);
			emplPositionList.addAll(emplPositionNotFullfill);
			totalRows = emplPositionList.size();
			if(end > emplPositionList.size()){
				end = emplPositionList.size();
			}
			emplPositionList = emplPositionList.subList(start, end);
			for(GenericValue tempGv: emplPositionList){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("emplPositionId", tempGv.getString("emplPositionId"));
				tempMap.put("description", tempGv.getString("description"));
				tempMap.put("partyId", tempGv.getString("partyId"));
				tempMap.put("partyCode", PartyUtil.getPartyCode(delegator, tempGv.getString("employeePartyId")));
				Timestamp fromDate = tempGv.getTimestamp("fromDate");
				if(fromDate != null){
					Timestamp thruDate = tempGv.getTimestamp("thruDate");
					tempMap.put("employeePartyName", PartyUtil.getPersonName(delegator, tempGv.getString("employeePartyId")));
					tempMap.put("employeePartyId", tempGv.getString("employeePartyId"));
					tempMap.put("fromDate", fromDate.getTime());
					if(thruDate != null){
						tempMap.put("thruDate", thruDate.getTime());
					}
				}
				Timestamp tempActualFromDate = tempGv.getTimestamp("actualFromDate");
				if(tempActualFromDate != null){
					tempMap.put("actualFromDate", tempActualFromDate.getTime());
				}
				Timestamp tempActualThruDate = tempGv.getTimestamp("actualThruDate");
				if(tempActualThruDate != null){
					tempMap.put("actualThruDate", tempActualThruDate.getTime());
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	retMap.put("TotalRows", String.valueOf(totalRows));
    	retMap.put("listReturn", listReturn);
		return retMap;
	}
	
	public static Map<String, Object> createEmplPosition(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> resultService = null;
		try {
			resultService = dispatcher.runSync("createEmplPosition", context);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return resultService;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplListByPosType(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		int totalRows = 0;
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		try {
			List<String> emplList = EmployeeHelper.getListEmplHavePositionTypeInPeriod(delegator, emplPositionTypeId,userLogin, fromDate, thruDate);
			if(UtilValidate.isEmpty(emplList)){
				retMap.put("TotalRows", String.valueOf(totalRows));
				return retMap;
			}
			EntityCondition partyCondCommon = EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplList);
			List<EntityCondition> partyConds = FastList.newInstance();
			partyConds.add(partyCondCommon);
			List<GenericValue> partyList = delegator.findList("Person", EntityCondition.makeCondition(partyConds), null, UtilMisc.toList("firstName"), null, false);
			totalRows = partyList.size();
			if(end > totalRows){
				end = totalRows;
			}
			partyList = partyList.subList(start, end);
			for(GenericValue tempGv: partyList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = tempGv.getString("partyId");
				List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, tempGv.getString("partyId"), fromDate, thruDate);
				List<String> departmentNameList = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", departmentList, "partyId","groupName");
				tempMap.put("department", StringUtils.join(departmentNameList, ", "));
				tempMap.put("partyId", partyId);
				tempMap.put("partyCode", PartyUtil.getPartyCode(delegator, partyId));
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, tempGv.getString("partyId")));
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	public static Map<String, Object> checkHRPlanningForPositionInOrg(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		//String partyId = (String)context.get("partyId");
		//Timestamp actualThruDate = (Timestamp)context.get("actualThruDate");
		//BigDecimal quantity = (BigDecimal)context.get("quantity");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Timestamp actualFromDate = (Timestamp)context.get("actualFromDate");
		Date fromDate = new Date(actualFromDate.getTime());
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		conds.add(EntityCondition.makeCondition("statusId", "HR_PLANNING_ACC"));
		conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
		conds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
		try {
			List<GenericValue> humanResourcePlanningList = delegator.findList("HRPlanningAndCustomTimePeriod", EntityCondition.makeCondition(conds), 
					null, UtilMisc.toList("-quantity"), null, false);
			if(UtilValidate.isNotEmpty(humanResourcePlanningList)){
				String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
				List<GenericValue> departmentList = buildOrg.getAllDepartmentList(delegator);
				List<String> departmentListId = EntityUtil.getFieldListFromEntityList(departmentList, "partyId", true);
				GenericValue humanResourcePlanning = humanResourcePlanningList.get(0);
				BigDecimal quantityInPlanning = humanResourcePlanning.getBigDecimal("quantity");
				Date fromDatePlanning = humanResourcePlanning.getDate("fromDate");
				Date thruDatePlanning = humanResourcePlanning.getDate("thruDate");
				Timestamp thruDatePlanningTs = new Timestamp(thruDatePlanning.getTime());
				List<EntityCondition> positionConds = FastList.newInstance();
				positionConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
				positionConds.add(EntityCondition.makeCondition("actualFromDate", EntityJoinOperator.LESS_THAN, thruDatePlanningTs));
				positionConds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, departmentListId));
				positionConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("actualThruDate", null),
																EntityJoinOperator.OR,
																EntityCondition.makeCondition("actualThruDate", EntityJoinOperator.GREATER_THAN, actualFromDate)));
				List<GenericValue> positionInPeriod = delegator.findList("EmplPosition", EntityCondition.makeCondition(positionConds), 
						null, null, null, false);
				BigDecimal nbrPositionExists = new BigDecimal(positionInPeriod.size());
				retMap.put("quantityInPlanning", quantityInPlanning);
				retMap.put("quantityPositionExists", nbrPositionExists);
				retMap.put("fromDatePlanning", fromDatePlanning);
				retMap.put("thruDatePlanning", thruDatePlanning);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getWorkOvertimeRegisterJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	//GenericValue userLogin = (GenericValue)context.get("userLogin");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-dateRegistered");
			}
			listIterator = delegator.find("WorkOvertimeRegistrationAndDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	
		return successResult; 
	}
	
	public static Map<String, Object> createEmplPositionFulfillment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	String emplPositionId = (String)context.get("emplPositionId");
    	String partyId = (String)context.get("partyId");
    	List<EntityCondition> conds = FastList.newInstance();
    	conds.add(EntityCondition.makeCondition("emplPositionId", emplPositionId));
    	conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
    	try {
			List<GenericValue> emplPositionFuls = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isNotEmpty(emplPositionFuls)){
				GenericValue emplPositionFul = emplPositionFuls.get(0);
				String partyIdFulfill = emplPositionFul.getString("employeePartyId");
				Timestamp fromDateErr = emplPositionFul.getTimestamp("fromDate");
				Timestamp thruDateErr = emplPositionFul.getTimestamp("thruDate");
				String errMsg = "";
				Map<String, Object> errMap = FastMap.newInstance();
				errMap.put("description", emplPositionFul.getString("description"));
				errMap.put("fromDateSet", DateUtil.getDateMonthYearDesc(fromDate));
				errMap.put("fromDate", DateUtil.getDateMonthYearDesc(fromDateErr));
				errMap.put("partyName", PartyUtil.getPersonName(delegator, partyIdFulfill));
				errMap.put("partyNameSet", PartyUtil.getPersonName(delegator, partyId));
				String propertyKey = "EmplPositionFulfillmentSetFromFrom";
				if(thruDate == null && thruDateErr != null){
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDateErr));
					propertyKey = "EmplPositionFulfillmentFromFromThru";
				}else if(thruDate != null && thruDateErr == null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					propertyKey = "EmplPositionFulfillmentFromThruFrom";
				}else if(thruDate != null && thruDateErr != null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDateErr));
					propertyKey = "EmplPositionFulfillmentFromThruFromThru";
				}
				errMsg = UtilProperties.getMessage("BaseHREmployeeUiLabels", propertyKey, errMap, (Locale)context.get("locale"));
				return ServiceUtil.returnError(errMsg);
			}
			Map<String, Object> resultService = dispatcher.runSync("createEmplPositionFulfillment", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> approvalDependentFamilyEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue personFamilyBackground = delegator.makeValue("PersonFamilyBackground");
		Timestamp dependentEndDate = (Timestamp)context.get("dependentEndDate");
		personFamilyBackground.setAllFields(context, false, null, null);
		personFamilyBackground.set("dependentEndDate", dependentEndDate);
		try {
			personFamilyBackground.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplPositionTypeAndClassJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("description");
			listSortFields.add("classTypeDesc");
		}
		try {
			listIterator = delegator.find("EmplPositionTypeAndClass", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> getPartyPositionAndDeptLastest(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = (String)context.get("partyId");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdTo", partyId));
		conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		Map<String, Object> results = FastMap.newInstance();
		try {
			List<GenericValue> partyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			GenericValue partyRel = partyRelList.get(0);
			String partyIdFrom = partyRel.getString("partyIdFrom");
			Timestamp fromDate = partyRel.getTimestamp("fromDate");
			Timestamp thruDate = partyRel.getTimestamp("thruDate");
			List<GenericValue> emplPositionList = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(UtilMisc.toMap("employeePartyId", partyId, "partyId", partyIdFrom)), 
					null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(emplPositionList)){
				GenericValue emplPosition = emplPositionList.get(0);
				fromDate = emplPosition.getTimestamp("fromDate");
				thruDate = emplPosition.getTimestamp("thruDate");
				results.put("emplPositionTypeId", emplPosition.get("emplPositionTypeId"));
				results.put("description", emplPosition.get("description"));
			}
			GenericValue partyFrom = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyIdFrom), false);
			results.put("fromDate", fromDate.getTime());
			results.put("thruDate", thruDate != null? thruDate.getTime() : null);
			results.put("groupName", partyFrom.get("groupName"));
			results.put("partyIdFrom", partyIdFrom);
			successResult.put("results", results);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> getInfoAccountEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = (String)context.get("partyId");
		Map<String, Object> results = FastMap.newInstance();
		String ldmStr = UtilProperties.getPropertyValue("security.properties", "login.disable.minutes");
		long loginDisableMinutes = Long.parseLong(ldmStr);
		try {
			List<GenericValue> listUserLogin = delegator.findList("PartyAndUserLoginFull", EntityCondition.makeCondition(UtilMisc.toMap("partyId",partyId)), null, null, null, false);
			GenericValue anEmpl = EntityUtil.getFirst(listUserLogin);
			results.put("userLoginId", anEmpl.getString("userLoginId"));
			results.put("lastLocale", anEmpl.getString("lastLocale"));
			results.put("hasLoggedOut", anEmpl.getString("hasLoggedOut"));
			results.put("enabled", anEmpl.getString("enabled"));
			results.put("requirePasswordChange", anEmpl.getString("requirePasswordChange"));
			long disabledDateTimeResult = 0;
			if(UtilValidate.isNotEmpty(anEmpl.getString("disabledDateTime"))){
				disabledDateTimeResult = anEmpl.getTimestamp("disabledDateTime").getTime() + loginDisableMinutes*60000;
			}
			results.put("disabledDateTime", disabledDateTimeResult);
			results.put("successiveFailedLogins", anEmpl.getString("successiveFailedLogins"));
			results.put("partyCode", anEmpl.getString("partyCode"));
			successResult.put("results", results);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> changingInfoEmplAccount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		String userLoginId = (String)context.get("userLoginId");
		String disabledDateTimeString = (String)context.get("disabledDateTime");
		long disabledDateTimeLong = Long.parseLong(disabledDateTimeString); 
		Timestamp disabledDateTime = new Timestamp(disabledDateTimeLong);
		//
		String ldmStr = UtilProperties.getPropertyValue("security.properties", "login.disable.minutes");
		long loginDisableMinutes = Long.parseLong(ldmStr);
		Timestamp reEnableTime = null;
		if (loginDisableMinutes > 0 && disabledDateTime != null) {
			reEnableTime = new Timestamp(disabledDateTime.getTime() - loginDisableMinutes * 60000);
		}
		Map<String, Object> results = FastMap.newInstance();
		try {
			GenericValue anUser = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
			if(UtilValidate.isNotEmpty(anUser)){
				String isEnabled = (String)anUser.getString("enabled");
				if(UtilValidate.isNotEmpty(isEnabled) && isEnabled.equals("N")){
					anUser.set("enabled", "Y");
					//anUser.set("disabledDateTime", disabledDateTime);
					results.put("enabled", "Y");
					//results.put("disabledDateTime", disabledDateTime);
				}else{
					anUser.set("enabled", "N");
					anUser.set("disabledDateTime", reEnableTime);
					results.put("enabled", "N");
					results.put("disabledDateTime", disabledDateTimeString);
				}
			}
			anUser.store();		
			results.put("successMessage", UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
			successResult.put("results", results);
			} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> updateEmplRelAndSecurityGroupByPositionType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyIdTo = (String)context.get("partyIdTo");
		Boolean isUpdateEmployment = (Boolean)context.get("isUpdateEmployment");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdFrom", partyIdTo),
												EntityJoinOperator.OR,
												EntityCondition.makeCondition("partyIdTo", partyIdTo)));
		conds.add(EntityCondition.makeCondition("fromDate", fromDate));
		if(!isUpdateEmployment){
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityJoinOperator.NOT_EQUAL, "EMPLOYMENT"));
		}
		try {
			String lastOrg = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> partyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
			for(GenericValue partyRel: partyRelList){
				partyRel.set("thruDate", thruDate);
				partyRel.store();
			}
			//update userLogin securityGroup
			List<GenericValue> userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyIdTo, "lastOrg", lastOrg), null, false);
			for(GenericValue tempGv: userLoginList){
				String tempUserLoginId = tempGv.getString("userLoginId");
				List<GenericValue> userLoginSecGroupList = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", tempUserLoginId, "fromDate", fromDate), null, false);
				for(GenericValue userLoginSecGroup: userLoginSecGroupList){
					userLoginSecGroup.set("thruDate", thruDate);
					userLoginSecGroup.store();
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplPositionJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0]: null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	listAllConditions.add(EntityCondition.makeCondition("employeePartyId", partyId));
    	EntityListIterator listIterator = null;
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("-fromDate");
    		}
			listIterator = delegator.find("EmplPositionAndFulfillmentAndPartyRelationship", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> expirationEmplPosition(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr)), thruDate = null;
		if(thruDateStr == null){
			thruDate = UtilDateTime.nowTimestamp();
		}else{
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
		}
		if(thruDate.before(fromDate)){
            return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "GTDateFieldRequired", locale));
        }
		String emplPositionId = (String)context.get("emplPositionId");
		try {
			GenericValue emplPosition = delegator.findOne("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId), false);
			GenericValue emplPositionFulfillment = delegator.findOne("EmplPositionFulfillment", UtilMisc.toMap("emplPositionId", emplPositionId, "partyId", partyId, "fromDate", fromDate), false);
			if(emplPosition == null || emplPositionFulfillment == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmploymentUiLabels", "PositionIsNotAssginForEmpl", locale));
			}
			String emplPositionTypeId = emplPosition.getString("emplPositionTypeId"); 
			List<GenericValue> emplPosTypePartyRelConfigList = delegator.findByAnd("EmplPosTypePartyRelConfig", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), null, false);
			List<GenericValue> emplPosTypeSecGroupConfigList = delegator.findByAnd("EmplPosTypeSecGroupConfig", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), null, false);
			EntityCondition fromDateCond = EntityCondition.makeCondition("fromDate", fromDate);
			for(GenericValue emplPosTypePartyRelConfig: emplPosTypePartyRelConfigList){
				String isFromOrgToPerson = emplPosTypePartyRelConfig.getString("isFromOrgToPerson");
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("roleTypeIdFrom", emplPosTypePartyRelConfig.getString("roleTypeIdFrom")));
				conds.add(EntityCondition.makeCondition("roleTypeIdTo", emplPosTypePartyRelConfig.getString("roleTypeIdTo")));
				conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", emplPosTypePartyRelConfig.getString("partyRelationshipTypeId")));
				if("Y".equals(isFromOrgToPerson)){
					conds.add(EntityCondition.makeCondition("partyIdTo", partyId));
				}else if("N".equals(isFromOrgToPerson)){
					conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
				}
				EntityCondition tempCond = EntityCondition.makeCondition(EntityCondition.makeCondition(conds), EntityJoinOperator.AND, fromDateCond);
				delegator.storeByCondition("PartyRelationship", UtilMisc.toMap("thruDate", thruDate), tempCond);
			}
			List<GenericValue> userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId), null, false);
			List<String> userLoginIds = EntityUtil.getFieldListFromEntityList(userLoginList, "userLoginId", false);
			EntityCondition userLoginCond = EntityCondition.makeCondition("userLoginId", EntityJoinOperator.IN, userLoginIds);
			EntityCondition fromDateUserLoginCond = EntityCondition.makeCondition(fromDateCond, userLoginCond);
			for(GenericValue emplPosTypeSecGroupConfig: emplPosTypeSecGroupConfigList){
				String groupId = emplPosTypeSecGroupConfig.getString("groupId");
				EntityCondition tempCond = EntityCondition.makeCondition(EntityCondition.makeCondition("groupId", groupId), EntityJoinOperator.AND, fromDateUserLoginCond);
				delegator.storeByCondition("UserLoginSecurityGroup", UtilMisc.toMap("thruDate", thruDate), tempCond);
			}
			emplPosition.set("actualThruDate", thruDate);
			emplPositionFulfillment.set("thruDate", thruDate);
			emplPosition.store();
			emplPositionFulfillment.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
	}
}
