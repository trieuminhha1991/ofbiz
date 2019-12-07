package com.olbius.employee.events;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.employee.helper.EmployeeHelper;
import com.olbius.util.CommonUtil;
import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.PartyUtil;
import com.olbius.util.PersonHelper;
import com.olbius.util.SecurityUtil;

public class EmployeeEvents {
	public static String createEmployee(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		//HttpSession session = request.getSession();
		//GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		Map<String, Object> resultsService = FastMap.newInstance();
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			resultsService = dispatcher.runSync("createEmployee", ServiceUtil.setServiceFields(dispatcher, "createEmployee", parameterMap, system, timeZone, locale));
			if(ServiceUtil.isSuccess(resultsService)){
				String partyId = (String)resultsService.get("partyId");
				EmployeeHelper.createEmplRelatedInfo(dispatcher, partyId, parameterMap, system, timeZone, locale);
				request.setAttribute("_EVENT_MESSAGE_", resultsService.get(ModelService.SUCCESS_MESSAGE));
				request.setAttribute("partyId", partyId);
			}else{
				request.setAttribute("_ERROR_MESSAGE_", resultsService.get(ModelService.ERROR_MESSAGE));
				return "error";
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("RecruitmentUiLabels", "ErrorCreateEmplInfo", locale));
			return "error";
		} catch (GeneralServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("RecruitmentUiLabels", "ErrorCreateEmplInfo", locale));
			return "error";
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("RecruitmentUiLabels", "ErrorCreateEmplInfo", locale));
			return "error";
		}
		return "success";
	}
	
	public static String createEmployeeAndAddToOrg(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String internalOrgId = request.getParameter("internalOrgId");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String familyList = request.getParameter("familyList");
		String educationList = request.getParameter("educationList");
		String workingProcessList = request.getParameter("workingProcessList");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> personFamily = FastList.newInstance();
		List<Map<String, Object>> personEducation = FastList.newInstance();
		List<Map<String, Object>> personWorkingProcess = FastList.newInstance();
		if(familyList != null){
			JSONArray familyListJson = JSONArray.fromObject(familyList);
			for(int i = 0; i < familyListJson.size(); i++){
				JSONObject family = familyListJson.getJSONObject(i);
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("lastName", family.getString("lastName"));
				tempMap.put("middleName", family.getString("middleName"));
				tempMap.put("firstName", family.getString("firstName"));
				tempMap.put("partyRelationshipTypeId", family.getString("partyRelationshipTypeId"));
				String birthDateStr = family.getString("birthDate");
				if(birthDateStr != null){
					tempMap.put("birthDate", new Date(Long.parseLong(birthDateStr)));	
				}
				tempMap.put("occupation", family.getString("occupation"));
				tempMap.put("placeWork", family.getString("placeWork"));
				tempMap.put("phoneNumber", family.getString("phoneNumber"));
				tempMap.put("emergencyContact", family.getString("emergencyContact"));
				personFamily.add(tempMap);
			}
		}
		if(educationList != null){
			JSONArray educationListJson = JSONArray.fromObject(educationList);
			for(int i = 0; i < educationListJson.size(); i++){
				JSONObject education = educationListJson.getJSONObject(i);
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("schoolId", education.getString("schoolId"));
				tempMap.put("majorId", education.getString("majorId"));
				tempMap.put("studyModeTypeId", education.getString("studyModeTypeId"));
				tempMap.put("degreeClassificationTypeId", education.getString("classificationTypeId"));
				tempMap.put("educationSystemTypeId", education.getString("educationSystemTypeId"));
				String fromDateStr = education.getString("fromDate");
				String thruDateStr = education.getString("thruDate");
				if(fromDateStr != null){
					tempMap.put("educationFromDate", new Timestamp(Long.parseLong(fromDateStr)));
				}
				if(thruDateStr != null){
					tempMap.put("educationThruDate", new Timestamp(Long.parseLong(thruDateStr)));
				}
				personEducation.add(tempMap);
			}
		}
		if(workingProcessList != null){
			JSONArray workingProcessListJson = JSONArray.fromObject(workingProcessList);
			for(int i = 0; i < workingProcessListJson.size(); i++){
				JSONObject workingProcess = workingProcessListJson.getJSONObject(i);
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("companyName", workingProcess.getString("companyName"));
				tempMap.put("emplPositionTypeIdWorkProcess", workingProcess.getString("emplPositionTypeId"));
				tempMap.put("jobDescription", workingProcess.getString("jobDescription"));
				tempMap.put("payroll", workingProcess.getString("payroll"));
				tempMap.put("terminationReasonId", workingProcess.getString("terminationReasonId"));
				tempMap.put("rewardDiscrip", workingProcess.getString("rewardDiscrip"));
				String fromDateStr = workingProcess.getString("fromDate");
				String thruDateStr = workingProcess.getString("thruDate");
				if(fromDateStr != null){
					tempMap.put("workProcess_fromDate", new Timestamp(Long.parseLong(fromDateStr)));
				}
				if(thruDateStr != null){
					tempMap.put("workProcess_thruDate", new Timestamp(Long.parseLong(thruDateStr)));
				}
				personWorkingProcess.add(tempMap);
			}
		}
		try {
			if(internalOrgId != null){
				TransactionUtil.begin();
				Map<String, Object> employeeCtx = FastMap.newInstance();
				employeeCtx.putAll(parameterMap);
				String birthDateStr = request.getParameter("birthDate");
				if(birthDateStr != null){
					employeeCtx.put("birthDate", new Date(Long.parseLong(birthDateStr)));
				}
				if(request.getParameter("idIssueDate") != null){
					employeeCtx.put("idIssueDate", new Date(Long.parseLong(request.getParameter("idIssueDate"))));
				}
				Map<String, Object> resultService = dispatcher.runSync("createPerson", ServiceUtil.setServiceFields(dispatcher, "createPerson", employeeCtx, userLogin, timeZone, locale));
				if(ServiceUtil.isSuccess(resultService)){
					String partyId = (String)resultService.get("partyId");
					resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "EMPLOYEE", "userLogin", userLogin));
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						return "error";
					}
					resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "DELYS_HRM", "userLogin", userLogin));
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						return "error";
					}
					EmployeeHelper.createEmployeeContact(dispatcher, parameterMap, partyId, userLogin);
					EmployeeHelper.createEmployeeFamily(dispatcher, personFamily, userLogin, partyId);
					EmployeeHelper.createEmployeeEducation(dispatcher, personEducation, userLogin, partyId, timeZone, locale);
					EmployeeHelper.createEmployeeWorkingProcess(dispatcher, personWorkingProcess, userLogin, partyId, timeZone, locale);
					
					//create relationship between employee and organization
						
					List<String> listRoleInternalOrgId = SecurityUtil.getCurrentRoles(internalOrgId, delegator);
					if(!listRoleInternalOrgId.contains("INTERNAL_ORGANIZATIO")){
						GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", internalOrgId), false);
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("EmployeeUiLabels", "CannotAddEmplToOrg_OrgHaveNotRole", UtilMisc.toMap("partyGroupName", partyGroup.getString("groupName")),  locale));
						return "error";
					}
					resultService = dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("partyIdFrom", internalOrgId, 
							"partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "roleTypeIdTo", "EMPLOYEE", 
							"partyRelationshipTypeId", "EMPLOYMENT", "userLogin", userLogin));
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						return "error";
					}
					String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator);
					resultService = dispatcher.runSync("createEmployment", UtilMisc.toMap("roleTypeIdFrom", "INTERNAL_ORGANIZATIO", 
							"roleTypeIdTo", "EMPLOYEE", "partyIdFrom", companyId, "partyIdTo", partyId, "userLogin", userLogin));
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						return "error";
					}
					//create userLogin
					/*Map<String, Object> userLoginCtx = FastMap.newInstance();
					userLoginCtx.putAll(parameterMap);
					userLoginCtx.put("userLogin", userLogin);
					resultService = dispatcher.runSync("createHrmUserLogin", ServiceUtil.setServiceFields(dispatcher, "createHrmUserLogin", userLoginCtx, userLogin, timeZone, locale));
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						return "error";
					}*/
					TransactionUtil.commit();
				}
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("EmployeeUiLabels", "CannotFindPartyGroupToAddEmpl", locale));
				return "error";
			}
			
		}catch (GeneralServiceException e) {
			e.printStackTrace();
		}catch (GenericServiceException e) {
			e.printStackTrace();
		}catch (GenericTransactionException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
		return "success";
	}
	
	public static String createEmplTerminationPpsl(HttpServletRequest request, HttpServletResponse response){
		String dateTerminationStr = request.getParameter("dateTermination");
		Timestamp dateTermination = new Timestamp(Long.parseLong(dateTerminationStr));
		dateTermination = UtilDateTime.getDayEnd(dateTermination);
		String terminationReasonId = request.getParameter("terminationReasonId");
		String otherReason = request.getParameter("otherReason");
		String comment = request.getParameter("comment");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		//Locale locale = UtilHttp.getLocale(request);
		//TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> serviceCtx = FastMap.newInstance();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			TransactionUtil.begin();
			serviceCtx.put("dateTermination", dateTermination);
			serviceCtx.put("terminationTypeId", "RESIGN");
			serviceCtx.put("terminationReasonId", terminationReasonId);
			serviceCtx.put("otherReason", otherReason);
			serviceCtx.put("comment", comment);
			serviceCtx.put("userLogin", userLogin);
			serviceCtx.put("partyId", userLogin.getString("partyId"));
			serviceCtx.put("statusId", "TER_PPSL_CREATED");
			Map<String, Object> resultService = dispatcher.runSync("createEmplTerminationPpsl", serviceCtx);
			if(ServiceUtil.isSuccess(resultService)){
				String emplTerminationProposalId = (String)resultService.get("emplTerminationProposalId");
				serviceCtx.clear();
				serviceCtx.put("emplTerminationProposalId", emplTerminationProposalId);
				serviceCtx.put("userLogin", userLogin);
				resultService = dispatcher.runSync("createWorkFlowApprTermination", serviceCtx);
				if(ServiceUtil.isSuccess(resultService)){
					TransactionUtil.commit();
					GenericValue emplTerminationPpsl = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId), false);
					String statusId = emplTerminationPpsl.getString("statusId");
					GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
					request.setAttribute("status", statusItem.get("description"));
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, resultService.get(ModelService.SUCCESS_MESSAGE));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
					TransactionUtil.rollback();
				}
			}
		} catch (GenericServiceException e){
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericTransactionException e){
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getEmplTerminationPpslInfo(HttpServletRequest request, HttpServletResponse response){
		String emplTerminationProposalId = request.getParameter("emplTerminationProposalId");
		//HttpSession session = request.getSession();
		//GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		try {
			GenericValue emplTerminationPpsl = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId), false);
			if(emplTerminationPpsl == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("EmployeeUiLabels", "NotFoundTerminationPPsl", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId), locale));
				return "error";
			}
			Timestamp createdDate = emplTerminationPpsl.getTimestamp("createdDate");
			String partyId = emplTerminationPpsl.getString("partyId");
			List<GenericValue> posFulfilment = PartyUtil.getPositionTypeOfEmplAtTime(delegator, partyId, createdDate);
			if(UtilValidate.isNotEmpty(posFulfilment)){
				request.setAttribute("emplPositionType", posFulfilment.get(0).getString("description")); 
			}
			GenericValue party = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			request.setAttribute("gender", party.getString("gender"));
			Date birthDate = party.getDate("birthDate");
			if(birthDate != null){
				request.setAttribute("birthDate", birthDate.getTime());
			}
			Timestamp dateJoinCompany = PersonHelper.getDateEmplJoinOrg(delegator, partyId);
			if(dateJoinCompany != null){
				request.setAttribute("dateJoinCompany", dateJoinCompany.getTime());
			}
			GenericValue department = PartyUtil.getDepartmentOfEmployee(delegator, partyId, createdDate);
			if(department != null){
				String departmentId = department.getString("partyIdFrom");
				GenericValue partyDepartment = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", departmentId), false);
				request.setAttribute("department", partyDepartment.getString("groupName"));
			}
			GenericValue agreement = com.olbius.util.PartyHelper.getAgreementOfEmplAtTime(delegator, partyId, createdDate);
			if(agreement != null){
				request.setAttribute("agreementId", agreement.getString("agreementId"));
				GenericValue agreementType = delegator.findOne("AgreementType", UtilMisc.toMap("agreementTypeId", agreement.getString("agreementTypeId")), false);
				request.setAttribute("agreementTypeId", agreementType.getString("description"));
				request.setAttribute("fromDate", agreement.getTimestamp("fromDate").getTime());
				Timestamp thruDate = agreement.getTimestamp("thruDate");
				if(thruDate != null){
					request.setAttribute("thruDate", thruDate.getTime());
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		return "success";
	}
	
	public static String approvalTerminationProposal(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    Delegator delegator = (Delegator)request.getAttribute("delegator");
	    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	    TimeZone timeZone = UtilHttp.getTimeZone(request);
	    Locale locale = UtilHttp.getLocale(request);
	    String requestId = request.getParameter("requestId");
	    try {
			TransactionUtil.begin();
			Map<String, Object> resultService = dispatcher.runSync("approvalWorkFlowRequest", ServiceUtil.setServiceFields(dispatcher, "approvalWorkFlowRequest", parameterMap, userLogin, timeZone, locale));
			if(ServiceUtil.isSuccess(resultService)){
				GenericValue workFlowRequestAtt = delegator.findOne("WorkFlowRequestAttr", UtilMisc.toMap("requestId", requestId, "attrName", "emplTerminationProposalId"), false);
				String emplTerminationProposalId = workFlowRequestAtt.getString("attrValue");
				String statusId = EmployeeHelper.updateEmplTerminationStatus(delegator, emplTerminationProposalId, requestId);
				if("TER_PPSL_ACCEPTED".equals(statusId)){
					dispatcher.runSync("expireEmplRelationship", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, "userLogin", userLogin));
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, resultService.get(ModelService.SUCCESS_MESSAGE));
				String ntfId = request.getParameter("ntfId");
				if(ntfId != null){
					dispatcher.runSync("updateNotification", UtilMisc.toMap("ntfId", ntfId, "userLogin", userLogin));
				}
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
				TransactionUtil.rollback();
				return "error";
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getPartyInformation(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    Delegator delegator = (Delegator)request.getAttribute("delegator");
	    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	    Map<String, Object> tempMap = FastMap.newInstance();
	    Map<String, Object> resultService;
	    try {
			GenericValue employeeGv = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			tempMap.putAll(PartyUtil.getHighestPartyEdu(delegator, partyId));
			tempMap.put("idNumber", employeeGv.getString("idNumber"));
			java.sql.Date idIssueDate = employeeGv.getDate("idIssueDate");
			if(idIssueDate != null){
				tempMap.put("idIssueDate", idIssueDate.getTime());
			}
			tempMap.put("idIssuePlace", employeeGv.getString("idIssuePlace"));
			tempMap.put("nativeLand", employeeGv.getString("nativeLand"));
			if(employeeGv.getString("ethnicOrigin") != null){
				GenericValue ethnicOrigin = delegator.findOne("EthnicOrigin", UtilMisc.toMap("ethnicOriginId", employeeGv.getString("ethnicOrigin")), false);
				tempMap.put("ethnicOrigin", ethnicOrigin.getString("description"));
			}
			resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PERMANENT_RESIDENCE", "userLogin", userLogin));
			String contactMechPermanentId = (String)resultService.get("contactMechId");
			if(contactMechPermanentId != null){
				tempMap.put("permanentResidence", CommonUtil.getPostalAddressDetails(delegator, contactMechPermanentId));
			}
			resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "CURRENT_RESIDENCE", "userLogin", userLogin));
			String contactMechCurrResidenceId = (String)resultService.get("contactMechId");
			if(contactMechCurrResidenceId != null){
				tempMap.put("currentResidence", CommonUtil.getPostalAddressDetails(delegator, contactMechCurrResidenceId));
			}
			tempMap.put("religion", employeeGv.getString("religion"));
			request.setAttribute("partyInfo", tempMap);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getSearchPartyId(HttpServletRequest request, HttpServletResponse response){
		String partyId_startsWith = request.getParameter("partyId_startsWith");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		EntityCondition partyConds = EntityCondition.makeCondition("partyId", EntityJoinOperator.LIKE, "%" + partyId_startsWith + "%");
		String maxRowStr = request.getParameter("maxRows");
		int maxRow = 0;
		try{
			maxRow = Integer.parseInt(maxRowStr);
		}catch(Exception e){
			maxRow = 0;
		}
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			List<GenericValue> partyList = delegator.findList("Person", partyConds, null, UtilMisc.toList("partyId"), null, false);
			if(maxRow > partyList.size()){
				maxRow = partyList.size();
			}
			partyList = partyList.subList(0, maxRow);
			for(GenericValue party: partyList){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("partyId", party.getString("partyId"));
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, party.getString("partyId")));
				listReturn.add(tempMap);
			}
			request.setAttribute("listParty", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
