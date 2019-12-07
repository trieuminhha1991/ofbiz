package com.olbius.basehr.employee.events;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Time;
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

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.util.PersonHelper;
import com.olbius.security.api.OlbiusSecurity;

public class EmployeeEvents {
	public static final String module = EmployeeEvents.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String createNewEmployee(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		try {
			Map<String, Object> paramMap = CommonUtil.getParameterMapWithFileUploaded(request);
			String partyCode = (String)paramMap.get("partyCode");
			String userLoginId = (String)paramMap.get("userLoginId");
			List<GenericValue> partyCheck = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
			if(UtilValidate.isNotEmpty(partyCheck)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "PartyHaveExists", UtilMisc.toMap("partyId", partyCode), locale));
				return "error";
			}
			GenericValue userLoginTest = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
			if(userLoginTest != null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "UserLoginHaveExists", UtilMisc.toMap("userLoginId", userLoginId), locale));
				return "error";
			}
			String birthDateStr = (String)paramMap.get("birthDate");
			if(birthDateStr != null){
				Date birthDate = new Date(Long.parseLong(birthDateStr));
				paramMap.put("birthDate", birthDate);
			}
			if(paramMap.get("idIssueDate") != null){
				paramMap.put("idIssueDate", new Date(Long.parseLong((String)paramMap.get("idIssueDate"))));
			}
			Timestamp dateJoinCompany = null;
			if(paramMap.get("dateJoinCompany") != null){
				dateJoinCompany = new Timestamp(Long.parseLong((String)paramMap.get("dateJoinCompany")));
			}else{
				dateJoinCompany = UtilDateTime.nowTimestamp();
			}
			dateJoinCompany = UtilDateTime.getDayStart(dateJoinCompany);
			paramMap.put("dateJoinCompany", dateJoinCompany);
			Date dateParticipateIns = null;
			if((String)paramMap.get("dateParticipateIns") != null){
				dateParticipateIns = new Date(Long.parseLong((String)paramMap.get("dateParticipateIns")));
			}
			if(paramMap.get("thruDate") != null){
				Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong((String)paramMap.get("thruDate"))));
				paramMap.put("thruDate", thruDate);
			}
			paramMap.put("dateParticipateIns", dateParticipateIns);
			Timestamp effectiveFromDate = null;
			if(paramMap.get("heathInsuranceFromDate") != null){
				effectiveFromDate = UtilDateTime.getMonthStart(new Timestamp(Long.parseLong((String)paramMap.get("heathInsuranceFromDate"))));
			}
			paramMap.put("heathInsuranceFromDate", effectiveFromDate);
			Timestamp effectiveThruDate = null;
			if(paramMap.get("heathInsuranceThruDate") != null){
				effectiveThruDate = UtilDateTime.getMonthEnd(new Timestamp(Long.parseLong((String)paramMap.get("heathInsuranceThruDate"))), timeZone, locale);
			}
			paramMap.put("heathInsuranceThruDate", effectiveThruDate);

			if(paramMap.get("participateFromDate") != null){
				Timestamp participateFromDate = UtilDateTime.getMonthStart(new Timestamp(Long.parseLong((String)paramMap.get("participateFromDate"))));
				paramMap.put("participateFromDate", participateFromDate);
			}
			for(Map.Entry<String, Object> entry: paramMap.entrySet()){
				if(entry.getValue() instanceof String){
					if(entry.getValue() != null && ((String)entry.getValue()).length() == 0){
						paramMap.remove(entry);
					}
				}
			}
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPerson", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			TransactionUtil.begin();
			Map<String, Object> resultService = dispatcher.runSync("createPerson", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			String partyId = (String)resultService.get("partyId");
			PartyUtil.updatePartyCode(delegator, partyId, partyCode);
			//create avatar
			List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>)paramMap.get("listFileUploaded");
			if(UtilValidate.isNotEmpty(listFileUploaded)){
				Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
				String _uploadedFile_contentType = (String)fileUploadedMap.get("_uploadedFile_contentType");
				ByteBuffer uploadedFile = (ByteBuffer)fileUploadedMap.get("uploadedFile");
				if(!CommonUtil.isImageFile(_uploadedFile_contentType)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "ImageAvatarIsNotValidForm", locale));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> imageCtx = FastMap.newInstance();
				imageCtx.put("userLogin", userLogin);
				imageCtx.put("locale", locale);
				imageCtx.put("timeZone", UtilHttp.getTimeZone(request));
				imageCtx.put("partyId", partyId);
				imageCtx.put("uploadedFile", uploadedFile);
				imageCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
				imageCtx.put("_uploadedFile_fileName", fileUploadedMap.get("_uploadedFile_fileName"));
				resultService = dispatcher.runSync("updatePersonalImage", imageCtx);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
			}
			String permanentRes = (String)paramMap.get("permanentRes");
			if(permanentRes != null){
				JSONObject jsonPermanentRes = JSONObject.fromObject(permanentRes);
				Map<String, Object> permanentResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonPermanentRes);
				if(permanentResMap != null){
					permanentResMap.put("userLogin", userLogin);
					permanentResMap.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
					permanentResMap.put("postalCode", "10000");
					permanentResMap.put("city", permanentResMap.get("stateProvinceGeoId"));
					permanentResMap.put("partyId", partyId);
					dispatcher.runSync("createPartyPostalAddress", permanentResMap);
				}
			}
			String currRes = (String)paramMap.get("permanentRes");
			if(currRes != null){
				JSONObject jsonCurrRes = JSONObject.fromObject(currRes);
				Map<String, Object> currResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonCurrRes);
				if(currResMap != null){
					currResMap.put("userLogin", userLogin);
					currResMap.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
					currResMap.put("postalCode", "10000");
					currResMap.put("city", currResMap.get("stateProvinceGeoId"));
					currResMap.put("partyId", partyId);
					dispatcher.runSync("createPartyPostalAddress", currResMap);
				}
			}
			Map<String, Object> employmentWorkInfo = ServiceUtil.setServiceFields(dispatcher, "createEmploymentWorkInfo", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			String insuranceTypeNotCompulsory = (String)paramMap.get("insuranceTypeNotCompulsory");
			if(insuranceTypeNotCompulsory != null){
				List<String> insuranceTypeIdList = FastList.newInstance();
				JSONArray insuranceTypeNotCompulsoryJson = JSONArray.fromObject(insuranceTypeNotCompulsory);
				for(int i = 0; i < insuranceTypeNotCompulsoryJson.size(); i++){
					insuranceTypeIdList.add(insuranceTypeNotCompulsoryJson.getString(i));
				}
				employmentWorkInfo.put("insuranceTypeIdList", insuranceTypeIdList);
			}
			employmentWorkInfo.put("fromDate", dateJoinCompany);
			BigDecimal salaryBaseFlat = paramMap.get("salaryBaseFlat")!= null? new BigDecimal((String)paramMap.get("salaryBaseFlat")): BigDecimal.ZERO;
			BigDecimal insuranceSalary = paramMap.get("insuranceSalary")!= null? new BigDecimal((String)paramMap.get("insuranceSalary")): BigDecimal.ZERO;
			employmentWorkInfo.put("rateAmount", salaryBaseFlat);
			employmentWorkInfo.put("insuranceSalary", insuranceSalary);
			employmentWorkInfo.put("partyIdTo", partyId);
			employmentWorkInfo.put("locale", locale);
			employmentWorkInfo.put("timeZone", timeZone);
			resultService = dispatcher.runSync("createEmploymentWorkInfo", employmentWorkInfo);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}
			
			//create userLogin
			resultService = dispatcher.runSync("createUserLogin", 
					UtilMisc.toMap("userLoginId", paramMap.get("userLoginId"),
									"enabled", "Y", "currentPassword", paramMap.get("password"),
									"currentPasswordVerify", paramMap.get("password"),
									"requirePasswordChange", "Y",
									"partyId", partyId, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			//update lastOrg new userLogin
			GenericValue userLoginNew = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("userLoginId")), false);
			userLoginNew.set("lastOrg", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
			userLoginNew.store();
			//------
			List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig", 
					UtilMisc.toMap("emplPositionTypeId", (String)paramMap.get("emplPositionTypeId")), null, false);
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			for(GenericValue tempGv: emplPosTypeSecGroupConfig){
				String groupId = tempGv.getString("groupId");
				dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", paramMap.get("userLoginId"),
																				"groupId", groupId,
																				"fromDate", dateJoinCompany,
																				"organizationId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")),
																				"userLogin", systemUserLogin));
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
			Debug.log(module + "::createNewEmployee, emplPositionTypeId = " + ((String) paramMap.get("emplPositionTypeId")));
			
			if(((String) paramMap.get("emplPositionTypeId")).contains("SALES_EXECUTIVE") || ((String) paramMap.get("emplPositionTypeId")).contains("SALES_EXECUTIVE_MT") || ((String) paramMap.get("emplPositionTypeId")).contains("SALES_EXECUTIVE_GT")) {
			    String fullName = (String) paramMap.get("firstName");
			    String partyIdfrom = (String) paramMap.get("partyIdFrom");
			    if(!UtilValidate.isEmpty(paramMap.get("middleName"))) fullName = paramMap.get("middleName") + " " + fullName;
			    if(!UtilValidate.isEmpty(paramMap.get("lastName"))) fullName = paramMap.get("lastName") + " " + fullName;
			    
			    Debug.log(module + "::createNewEmployee, START createPartySalesman, departmentId = " + partyIdfrom + 
			    		", partyCode = " + partyCode);
			    
			    dispatcher.runSync("createPartySalesman", UtilMisc.toMap("partyId", partyId,
                        "partyCode", partyCode, "fullName", fullName, "statusId", "PARTY_ENABLED",
                        "preferredCurrencyUomId", "VND", "departmentId", partyIdfrom, "userLogin", userLogin));
            }
			TransactionUtil.commit();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				e1.printStackTrace();
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				e1.printStackTrace();
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getDateJoinCompany(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Security security = (Security) request.getAttribute("security");
		HttpSession session = request.getSession(true);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		if(security.hasEntityPermission("HR_AGREEMENT", "_ADMIN", userLogin)){
			if(partyId != null){
				try {
					Timestamp dateJoinCompany = PersonHelper.getDateEmplJoinOrg(delegator, partyId);
					if(dateJoinCompany != null){
						request.setAttribute("dateJoinCompany", dateJoinCompany.getTime());
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					}else{
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "DateJoinCompanyIsNotSet", UtilHttp.getLocale(request)));
					}
				} catch (GenericEntityException e) {
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "You don't have permission");
		}
		return "success";
	}
	
	public static String getEmplPositionTypeOfEmployee(HttpServletRequest request, HttpServletResponse response){
		Security security = (Security) request.getAttribute("security");
		HttpSession session = request.getSession(true);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if(security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", userLogin)){
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			String partyId = request.getParameter("partyId");
			List<Map<String, Object>> listReturn = FastList.newInstance();
			try {
				List<GenericValue> emplPositionList = PartyUtil.getPositionTypeOfEmplAtTime(delegator, partyId, UtilDateTime.nowTimestamp());
				for(GenericValue emplPosition: emplPositionList){
					Map<String, Object> tempMap = FastMap.newInstance();
					tempMap.put("emplPositionId", emplPosition.getString("emplPositionId"));
					tempMap.put("description", emplPosition.getString("description"));
					tempMap.put("partyId", emplPosition.getString("partyId"));
					tempMap.put("emplPositionTypeId", emplPosition.getString("emplPositionTypeId"));
					tempMap.put("fulltimeFlag", emplPosition.getString("fulltimeFlag"));
					tempMap.put("groupName", PartyHelper.getPartyName(delegator, emplPosition.getString("partyId"), false));
					listReturn.add(tempMap);
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("listReturn", listReturn);
			} catch (GenericEntityException e) {
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				e.printStackTrace();
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "You don't have permission");
		}
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
			List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, createdDate);
			if(UtilValidate.isNotEmpty(departmentList)){
				List<String> departmentNames = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", departmentList, "partyId", "groupName");
				request.setAttribute("department", StringUtils.join(departmentNames, ", "));
			}
			GenericValue agreement = com.olbius.basehr.util.PartyHelper.getAgreementOfEmplAtTime(delegator, partyId, createdDate);
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
			if(employeeGv.getString("idNumber") != null){
				tempMap.put("idNumber", employeeGv.getString("idNumber"));
			}
			java.sql.Date idIssueDate = employeeGv.getDate("idIssueDate");
			if(idIssueDate != null){
				tempMap.put("idIssueDate", DateUtil.getDateMonthYearDesc(idIssueDate));
			}
			String idIssuePlace = employeeGv.getString("idIssuePlace");
			if(idIssuePlace != null){
				GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", idIssuePlace), false);
				if(geo != null){
					tempMap.put("idIssuePlace", geo.getString("geoName"));
				}else{
					tempMap.put("idIssuePlace", idIssuePlace);
				}
			}
			tempMap.put("nativeLand", employeeGv.getString("nativeLand"));
			String nationality = employeeGv.getString("nationality");
			if(nationality != null){
				GenericValue nationalityGv = delegator.findOne("Nationality", UtilMisc.toMap("nationalityId", nationality), false);
				if(nationalityGv != null){
					tempMap.put("nationality", nationalityGv.get("description"));
				}else{
					tempMap.put("nationality", nationality);
				}
			}
			tempMap.put("numberChildren", employeeGv.get("numberChildren"));
			String maritalStatusId = employeeGv.getString("maritalStatusId");
			if(maritalStatusId != null){
				GenericValue maritalStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", maritalStatusId), false);
				if(maritalStatus != null){
					tempMap.put("maritalStatus", maritalStatus.getString("description"));
				}else{
					tempMap.put("maritalStatus", maritalStatusId);
				}
			}
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
			String avatarUrl = PartyUtil.getEmployeeAvatarUrl(delegator, partyId);
			if(avatarUrl != null){
				tempMap.put("avatarUrl", avatarUrl);
			}
			request.setAttribute("partyInfo", tempMap);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getEmployeeContactInfo(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Map<String, Object> resultService;
		try {
			resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PERMANENT_RESIDENCE", "userLogin", userLogin));
			String contactMechPermanentId = (String)resultService.get("contactMechId");
			if(contactMechPermanentId != null){
				request.setAttribute("permanentResidence", CommonUtil.getPostalAddressDetails(delegator, contactMechPermanentId));
			}
			resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "CURRENT_RESIDENCE", "userLogin", userLogin));
			String contactMechCurrResidenceId = (String)resultService.get("contactMechId");
			if(contactMechCurrResidenceId != null){
				request.setAttribute("currentResidence", CommonUtil.getPostalAddressDetails(delegator, contactMechCurrResidenceId));
			}
			resultService = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL", "userLogin", userLogin));
			String emailAddress = (String)resultService.get("emailAddress");
			if(emailAddress != null){
				request.setAttribute("emailAddress", emailAddress);
			}
			resultService = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_HOME", "userLogin", userLogin));
			String countryCode = (String)resultService.get("countryCode");
			String areaCode = (String)resultService.get("areaCode");
			String contactNumber = (String)resultService.get("contactNumber");
			StringBuffer phoneNbr = new StringBuffer();
			if(countryCode != null){
				phoneNbr.append(countryCode);
			}
			if(areaCode != null){
				phoneNbr.append(" ");
				phoneNbr.append(areaCode);
			}
			if(contactNumber != null){
				phoneNbr.append(" ");
				phoneNbr.append(contactNumber);
			}
			request.setAttribute("phoneMobile", phoneNbr.toString());
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericServiceException e) {
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
	
	public static String getSearchPartyId(HttpServletRequest request, HttpServletResponse response){
		String partyId_startsWith = request.getParameter("partyId_startsWith");
		String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
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
			if(partyGroupId != null){
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> emplList = FastList.newInstance();
				if(fromDate != null){
					emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
				}else{
					emplList = buildOrg.getEmployeeInOrg(delegator);
				}
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
				partyConds = EntityCondition.makeCondition(partyConds, EntityJoinOperator.AND, EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIdList));
			}
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
	
	public static String updatePerson(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		Locale locale = UtilHttp.getLocale(request);
		if(partyId == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("EmployeeUiLabels", "EmployeeIdIsNull", locale));
			return "error";
		}
		try {
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(person == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("EmployeeUiLabels", "NoEmployeeIdIsHavePartyId", UtilMisc.toMap("partyId", partyId), locale));
				return "error";
			}
			ModelEntity personModel = person.getModelEntity();
			List<String> allFields = personModel.getAllFieldNames();
			for(String field: allFields){
				ModelField modelfield = personModel.getField(field);
				String fieldType = modelfield.getType();
				String fieldUpdateValue = request.getParameter(field);
				if(fieldUpdateValue != null){
					if("date".equals(fieldType)){
						Date date = new Date(Long.parseLong(fieldUpdateValue));
						person.set(field, date);
					}else if("currency-amount".equals(fieldType) || "currency-precise".equals(fieldType) || "fixed-point".equals(fieldType)){
						person.set(field, new BigDecimal(fieldUpdateValue));
					}else if("floating-point".equals(fieldType)){
						person.set(field, Double.parseDouble(fieldUpdateValue));
					}else if("numeric".equals(fieldType)){
						person.set(field, Long.parseLong(fieldUpdateValue));
					}else{
						person.set(field, fieldUpdateValue);
					}
					person.store();
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");			
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createPartyPostalAddr(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
		try {
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyPostalAddress", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			ctxMap.put("city", stateProvinceGeoId);
			ctxMap.put("postalCode", "10000");
			ctxMap.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("createPartyPostalAddress", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				String contactMechId = (String)resultService.get("contactMechId");
				request.setAttribute("contactMechId", contactMechId);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String updatePartyPostalAddr(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
		Map<String, Object> ctxMap;
		try {
			ctxMap = ServiceUtil.setServiceFields(dispatcher, "updatePartyPostalAddress", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			ctxMap.put("city", stateProvinceGeoId);
			ctxMap.put("postalCode", "10000");
			ctxMap.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("updatePartyPostalAddress", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				String contactMechId = (String)resultService.get("contactMechId");
				request.setAttribute("contactMechId", contactMechId);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String editPartyEmailAddr(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String contactMechId = request.getParameter("contactMechId");
		Map<String, Object> ctxMap;
		try {
			Map<String, Object> resultService;
			if(contactMechId == null){
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyEmailAddress", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
				ctxMap.put("locale", locale);
				resultService = dispatcher.runSync("createPartyEmailAddress", ctxMap);
			}else{
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "updatePartyEmailAddress", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
				ctxMap.put("locale", locale);
				resultService = dispatcher.runSync("updatePartyEmailAddress", ctxMap);
			}
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute("contactMechId", resultService.get("contactMechId"));
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String editPartyTeleNbr(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String contactMechId = request.getParameter("contactMechId");
		Map<String, Object> ctxMap;
		try {
			Map<String, Object> resultService;
			if(contactMechId == null){
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyTelecomNumber", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
				ctxMap.put("locale", locale);
				resultService = dispatcher.runSync("createPartyTelecomNumber", ctxMap);
			}else{
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "updatePartyTelecomNumber", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
				ctxMap.put("locale", locale);
				resultService = dispatcher.runSync("updatePartyTelecomNumber", ctxMap);
			}
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute("contactMechId", resultService.get("contactMechId"));
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getWorkingShiftOfEmpl(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Security security = (Security)request.getAttribute("security");
		boolean checkPerms = security.hasEntityPermission("HR_PROFILE", "_VIEW", userLogin);
		if(checkPerms){
			String partyId = userLogin.getString("partyId");
			String fromDateStr = request.getParameter("start");
			String thruDateStr = request.getParameter("end");
			Date fromDate = new Date(Long.parseLong(fromDateStr));
			Date thruDate = new Date(Long.parseLong(thruDateStr));
			List<EntityCondition> holidayConds = FastList.newInstance();
			holidayConds.add(EntityCondition.makeCondition("dateHoliday", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			holidayConds.add(EntityCondition.makeCondition("dateHoliday", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("dateWork", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conds.add(EntityCondition.makeCondition("dateWork", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			
			try {
				List<GenericValue> holidayList = delegator.findList("Holiday", EntityCondition.makeCondition(holidayConds), null, null, null, false);
				EntityCondition wsConds = null;
				if(UtilValidate.isNotEmpty(holidayList)){
					List<Date> dateHoliday = EntityUtil.getFieldListFromEntityList(holidayList, "dateHoliday", true);
					wsConds = EntityCondition.makeCondition(EntityCondition.makeCondition(conds), EntityJoinOperator.AND,
							EntityCondition.makeCondition("dateWork", EntityJoinOperator.NOT_IN, dateHoliday));
				}else{
					wsConds = EntityCondition.makeCondition(conds);
				}
				List<GenericValue> partyWorkingShiftList = delegator.findList("WSEmployeeAndWS", 
						wsConds, null, UtilMisc.toList("dateWork"), null, false);
				List<Map<String, Object>> listReturn = FastList.newInstance();
				for(GenericValue holiday: holidayList){
					Map<String, Object> tempMap = FastMap.newInstance();
					listReturn.add(tempMap);
					tempMap.put("dateWork", holiday.getDate("dateHoliday").getTime());
					tempMap.put("workTypeId", "HOLIDAY");
					tempMap.put("holidayName", holiday.getString("holidayName"));
				}
				for(GenericValue partyWorkingShift: partyWorkingShiftList){
					Map<String, Object> tempMap = FastMap.newInstance();
					Date tempDateWork = partyWorkingShift.getDate("dateWork");
					String workingShiftName = partyWorkingShift.getString("workingShiftName");
					Time shiftStartTime = null;
					Time shiftEndTime = null;
					String dayName = DateUtil.getDayName(tempDateWork);
					String workingShiftId = partyWorkingShift.getString("workingShiftId");
					GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayName), false);
					if(workingShiftDayWeek != null){
						String workTypeId = workingShiftDayWeek.getString("workTypeId");
						tempMap.put("workTypeId", workTypeId);
						if("ALL_SHIFT".equals(workTypeId)){
							shiftStartTime = partyWorkingShift.getTime("shiftStartTime");
							shiftEndTime = partyWorkingShift.getTime("shiftEndTime");
						}else if("FIRST_HALF_SHIFT".equals(workTypeId)){
							shiftStartTime = partyWorkingShift.getTime("shiftStartTime");
							if(partyWorkingShift.getTime("shiftBreakStart") != null){
								shiftEndTime = partyWorkingShift.getTime("shiftBreakStart");
							}else{
								shiftEndTime = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, partyWorkingShift.getTime("shiftEndTime"));
							}
						}else if("SECOND_HALF_SHIFT".equals(workTypeId)){
							shiftEndTime = partyWorkingShift.getTime("shiftEndTime");
							if(partyWorkingShift.getTime("shiftBreakEnd") != null){
								shiftStartTime = partyWorkingShift.getTime("shiftBreakEnd");
							}else{
								shiftStartTime = DateUtil.getMiddleTimeBetweenTwoTime(partyWorkingShift.getTime("shiftStartTime"), shiftEndTime);
							}		
						}
					}else{
						shiftStartTime = partyWorkingShift.getTime("shiftStartTime");
						shiftEndTime = partyWorkingShift.getTime("shiftEndTime");
					}
					tempMap.put("workingShiftName", workingShiftName);
					tempMap.put("dateWork", tempDateWork.getTime());
					if(shiftStartTime != null){
						tempMap.put("shiftStartTime", shiftStartTime.getTime());
					}
					if(shiftEndTime != null){
						tempMap.put("shiftEndTime", shiftEndTime.getTime());
					}
					listReturn.add(tempMap);
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("listReturn", listReturn);
			} catch (GenericEntityException e) {
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				e.printStackTrace();
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		}
		return "success";
	}
	
	public static String updatePersonName(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Security security = (Security)request.getAttribute("security");
		boolean checkPerms = security.hasEntityPermission("HR_PROFILE", "_VIEW", userLogin);
		if(checkPerms){
			String partyId = userLogin.getString("partyId");
			try {
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				if(party == null){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, "No employee have partyId: " + partyId);
					return "error";
				}
				GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				Map<String, Object> resultService = dispatcher.runSync("updatePerson", 
						UtilMisc.toMap("partyId", partyId, "firstName", request.getParameter("firstName"),
								"middleName", request.getParameter("middleName"),
								"lastName", request.getParameter("lastName"), "userLogin", systemUserLogin));
				if(ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");	
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", UtilHttp.getLocale(request)));	
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "You do't have permission");
			return "error";
		}
		return "success";
	}
	
	public static String updateEmplPassword(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        OlbiusSecurity securityOlb = (OlbiusSecurity)request.getAttribute("security");
        boolean checkPerms = securityOlb.olbiusHasPermission(userLogin, "VIEW", "MODULE", "HR_PROFILE");
		if(checkPerms){
			try {
				//GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updatePassword", UtilHttp.getParameterMap(request), userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
				context.put("userLoginId", userLogin.get("userLoginId"));
				context.put("locale", UtilHttp.getLocale(request));
				List<String> errorMessageList = FastList.newInstance(); 
				LoginServices.checkNewPassword(userLogin, request.getParameter("currentPassword"), request.getParameter("newPassword"), 
						request.getParameter("newPasswordVerify"), null, errorMessageList, false, UtilHttp.getLocale(request));
				if(UtilValidate.isNotEmpty(errorMessageList)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, errorMessageList.get(0));
					return "error";
				}
				Map<String, Object> resultService = dispatcher.runSync("updatePassword", context);
				if(ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");	
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", UtilHttp.getLocale(request)));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
				}
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "You do't have permission");
			return "error";
		}
		return "success";
	}
	
	public static String getPartyWorkInfo(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(partyId != null){
			try {
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				if(person == null){
					return "error";
				}
				String workingStatusId = EmployeeHelper.getEmplWorkingStatus(delegator, partyId);
				request.setAttribute("workingStatusId", workingStatusId);
				request.setAttribute("insuranceSocialNbr", person.getString("insuranceSocialNbr"));
				Date dateParticipateIns = person.getDate("dateParticipateIns");
				if(dateParticipateIns != null){
					request.setAttribute("dateParticipateIns", dateParticipateIns.getTime());
				}
				//request.setAttribute("workingStatusId", workingStatusId);
				GenericValue employment = EmployeeHelper.getEmploymentOfParty(delegator, partyId, userLogin);
				Timestamp fromDate = UtilDateTime.nowTimestamp(), thruDate = null;
				if(employment != null){
					thruDate = employment.getTimestamp("thruDate");
					if(thruDate != null){
						request.setAttribute("resignDate", thruDate.getTime());
					}
					request.setAttribute("terminationReasonId", employment.getString("terminationReasonId"));
				}
				Map<String, Object> resultService = dispatcher.runSync("getSalaryAmountEmpl", 
						UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
				if(ServiceUtil.isSuccess(resultService)){
					BigDecimal rateAmount = (BigDecimal)resultService.get("rateAmount");
					request.setAttribute("salaryBaseFlat", rateAmount);
					request.setAttribute("periodTypeId", resultService.get("periodTypeId"));
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		}
		return "success";
	}
	
	public static String updateEmploymentInfo(HttpServletRequest request, HttpServletResponse response){
		//String partyId = request.getParameter("partyId");
		//Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String dateJoinCompany = request.getParameter("dateJoinCompany");
		String thruDateStr = request.getParameter("thruDate");
		String dateParticipateInsStr = request.getParameter("dateParticipateIns");
		String probationaryDeadline = request.getParameter("probationaryDeadline");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		if(dateJoinCompany != null){
			paramMap.put("fromDate", new Timestamp(Long.parseLong(dateJoinCompany)));
		}
		if(thruDateStr != null){
			Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
			thruDate = UtilDateTime.getDayEnd(thruDate);
			paramMap.put("thruDate", thruDate);
		}
		if(dateParticipateInsStr != null){
			paramMap.put("dateParticipateIns", new Date(Long.parseLong(dateParticipateInsStr)));
		}
		if(probationaryDeadline != null) {
		    paramMap.put("probationaryDeadline", new Date(Long.parseLong(probationaryDeadline)));
        }
		try {
			TransactionUtil.begin();
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updatePerson", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("updatePerson", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				String errMes = CommonUtil.getErrorMessageFromService(resultService);
				request.setAttribute(ModelService.ERROR_MESSAGE, errMes);
				TransactionUtil.rollback();
				return "error";
			}

            context = ServiceUtil.setServiceFields(dispatcher, "updatePartySalesman", context, userLogin, timeZone, locale);
            dispatcher.runSync("updatePartySalesman", context);

			context = ServiceUtil.setServiceFields(dispatcher, "updateEmploymentOfParty", paramMap, userLogin, timeZone, locale);
			resultService = dispatcher.runSync("updateEmploymentOfParty", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				String errMes = CommonUtil.getErrorMessageFromService(resultService);
				request.setAttribute(ModelService.ERROR_MESSAGE, errMes);
				TransactionUtil.rollback();
				return "error";
			}
			context = ServiceUtil.setServiceFields(dispatcher, "updateSecurityGroupOfEmpl", paramMap, userLogin, timeZone, locale);
			resultService = dispatcher.runSync("updateSecurityGroupOfEmpl", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				String errMes = CommonUtil.getErrorMessageFromService(resultService);
				request.setAttribute(ModelService.ERROR_MESSAGE, errMes);
				TransactionUtil.rollback();
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
			TransactionUtil.commit();
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	public static String updatePartyOrgInfo(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		String partyIdFromNew = request.getParameter("partyIdFromNew");
		String partyIdFromOld = request.getParameter("partyIdFromOld");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			Security security = (Security) request.getAttribute("security");
			if(!security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", userLogin)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "you haven't permission");
				return "error";
			}
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updatePartyGroup", parameterMap, userLogin, timeZone, locale);
			context.put("comments", request.getParameter("comments"));
			Map<String, Object> resultService = dispatcher.runSync("updatePartyGroup", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			String address1 = request.getParameter("address1");
			String countryGeoId = request.getParameter("countryGeoId");
			String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
			if(address1 != null && countryGeoId != null && stateProvinceGeoId != null){
				String contactMechId = request.getParameter("contactMechId");
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("partyId", partyId);
				ctxMap.put("address1", request.getParameter("address1"));
				ctxMap.put("countryGeoId", request.getParameter("countryGeoId"));
				ctxMap.put("postalCode", "10000");
				ctxMap.put("stateProvinceGeoId", stateProvinceGeoId);
				ctxMap.put("city", stateProvinceGeoId);
				ctxMap.put("districtGeoId", request.getParameter("districtGeoId"));
				ctxMap.put("wardGeoId", request.getParameter("wardGeoId"));
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				if(contactMechId != null){
					ctxMap.put("contactMechId", contactMechId);
					resultService = dispatcher.runSync("updatePartyPostalAddress", ctxMap);
				}else{
					resultService = dispatcher.runSync("createPartyPostalAddress", ctxMap);
				}
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
			}
			String currOrgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if(partyIdFromOld != null && partyIdFromNew != null && !currOrgId.equals(partyId)){
				if(partyIdFromNew.equals(partyId)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "PartyIdCannotIsChildOfItSelf", locale));
					return "error";
				}
				if(PartyUtil.checkAncestorOfParty(delegator, partyId, partyIdFromNew, userLogin)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					String errMes = UtilProperties.getMessage("BaseHRUiLabels", "CannotUpdate_PartyIdToIsParentOFPartyIdFrom", 
							UtilMisc.toMap("partyIdFrom", PartyHelper.getPartyName(delegator, partyId, false), "partyIdTo", PartyHelper.getPartyName(delegator, partyIdFromNew, false)), locale);
					request.setAttribute(ModelService.ERROR_MESSAGE, errMes);
					return "error";
				}
				if(!partyIdFromOld.equals(partyIdFromNew)){
					GenericValue partyIdFromOldGv = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFromOld), false);
					GenericValue partyIdFromNewGv = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFromNew), false);
					if(partyIdFromNewGv != null && partyIdFromOldGv != null){
						TransactionUtil.begin();
						//thruDate old relationship						
						List<EntityCondition> conditions = FastList.newInstance();
						conditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
						conditions.add(EntityCondition.makeCondition("partyIdFrom", partyIdFromOld));
						conditions.add(EntityUtil.getFilterByDateExpr());
						List<GenericValue> relationshipExpire = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), 
								null, null, null, false);
						GenericValue expireGv = relationshipExpire.get(0);
						expireGv.set("thruDate", UtilDateTime.nowTimestamp());
						expireGv.store();
						//create new relationship
						Map<String, Object> ctxMap = FastMap.newInstance();
						ctxMap.put("partyIdFrom", partyIdFromNew);
						ctxMap.put("partyIdTo", partyId);
						List<String> roleOfPartyIdFrom = SecurityUtil.getCurrentRoles(partyIdFromNew, delegator);
						if(UtilValidate.isNotEmpty(roleOfPartyIdFrom)){
							ctxMap.put("roleTypeIdFrom", roleOfPartyIdFrom.get(0));
							ctxMap.put("roleTypeIdTo", expireGv.getString("roleTypeIdTo"));
							ctxMap.put("partyRelationshipTypeId", "GROUP_ROLLUP");
							ctxMap.put("userLogin", userLogin);
							resultService = dispatcher.runSync("createPartyRelationship", ctxMap);
							if(ServiceUtil.isSuccess(resultService)){
								TransactionUtil.commit();
							}else{
								request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
								request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
								return "error";
							}
						}else{
							request.setAttribute(ModelService.ERROR_MESSAGE, "have no role");
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							return "error";
						}
					}
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			e.printStackTrace();
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		return "success";
	}
	
	public static String getEditEmplInformation(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		try {
			GenericValue person = delegator.findOne("PartyAndPerson", UtilMisc.toMap("partyId", partyId), false);
			if(person == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("EmployeeUiLabels", "NoEmployeeIdIsHavePartyId", 
						UtilMisc.toMap("partyId", partyId), UtilHttp.getLocale(request)));
				return "error";
			}
			Map<String, Object> personInfo = FastMap.newInstance();
			Date idIssueDate = person.getDate("idIssueDate");
			Date birthDate = person.getDate("birthDate");
			String partyCode = person.getString("partyCode");
			if(partyCode == null){
				partyCode = partyId;
			}
			personInfo.put("partyId", partyId);
			personInfo.put("partyCode", partyCode);
			personInfo.put("lastName", person.get("lastName"));
			personInfo.put("firstName", person.get("firstName"));
			personInfo.put("middleName", person.get("middleName"));
			personInfo.put("idNumber", person.get("idNumber"));
			personInfo.put("maritalStatusId", person.get("maritalStatusId"));
			personInfo.put("ethnicOrigin", person.get("ethnicOrigin"));
			personInfo.put("religion", person.get("religion"));
			personInfo.put("nationality", person.get("nationality"));
			personInfo.put("gender", person.get("gender"));
			personInfo.put("nativeLand", person.get("nativeLand"));
			personInfo.put("idIssuePlace", person.get("idIssuePlace"));
			personInfo.put("birthPlace", person.get("birthPlace"));
			if(idIssueDate != null){
				personInfo.put("idIssueDate", idIssueDate.getTime());
			}
			if(birthDate != null){
				personInfo.put("birthDate", birthDate.getTime());
			}
			String avatarUrl = PartyUtil.getEmployeeAvatarUrl(delegator, partyId);
			if(avatarUrl != null){
				personInfo.put("avatarUrl", avatarUrl);
			}
			request.setAttribute("personInfo", personInfo);
			Map<String, Object> context = FastMap.newInstance();
			context.put("partyId", partyId);
			context.put("userLogin", userLogin);
			context.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
			Map<String, Object> resultService = dispatcher.runSync("getPartyPostalAddress", context);
			if(ServiceUtil.isSuccess(resultService)){
				String contactMechId = (String) resultService.get("contactMechId");
				if(contactMechId != null){
					GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
					Map<String, Object> permanentResInfo = FastMap.newInstance();
					permanentResInfo.put("contactMechId", contactMechId);
					permanentResInfo.put("address1", postalAddr.getString("address1"));
					permanentResInfo.put("countryGeoId", postalAddr.getString("countryGeoId"));
					permanentResInfo.put("stateProvinceGeoId", postalAddr.getString("stateProvinceGeoId"));
					permanentResInfo.put("wardGeoId", postalAddr.getString("wardGeoId"));
					permanentResInfo.put("districtGeoId", postalAddr.getString("districtGeoId"));
					request.setAttribute("permanentResInfo", permanentResInfo);
				}
			}
			context.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
			resultService = dispatcher.runSync("getPartyPostalAddress", context);
			if(ServiceUtil.isSuccess(resultService)){
				String contactMechId = (String) resultService.get("contactMechId");
				if(contactMechId != null){
					GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
					Map<String, Object> currResInfo = FastMap.newInstance();
					currResInfo.put("contactMechId", contactMechId);
					currResInfo.put("address1", postalAddr.getString("address1"));
					currResInfo.put("countryGeoId", postalAddr.getString("countryGeoId"));
					currResInfo.put("stateProvinceGeoId", postalAddr.getString("stateProvinceGeoId"));
					currResInfo.put("wardGeoId", postalAddr.getString("wardGeoId"));
					currResInfo.put("districtGeoId", postalAddr.getString("districtGeoId"));
					request.setAttribute("currResInfo", currResInfo);
				}
			}
			
			resultService = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL", "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				Map<String, Object> emailAddressInfo = FastMap.newInstance();
				String contactMechId = (String) resultService.get("contactMechId");
				if(contactMechId != null){
					emailAddressInfo.put("contactMechId", contactMechId);
					emailAddressInfo.put("emailAddress", resultService.get("emailAddress"));
					request.setAttribute("emailAddressInfo", emailAddressInfo);
				}
			}
			resultService = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE", "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				Map<String, Object> phoneMobileInfo = FastMap.newInstance();
				String contactMechId = (String) resultService.get("contactMechId");
				if(contactMechId != null){
					phoneMobileInfo.put("contactMechId", contactMechId);
					phoneMobileInfo.put("phoneMobile", resultService.get("contactNumber"));
					request.setAttribute("phoneMobileInfo", phoneMobileInfo);
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String updateEmployeeInfo(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		try {
			Map<String, Object> paramMap = CommonUtil.getParameterMapWithFileUploaded(request);
			String partyId = (String)paramMap.get("partyId");
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(person == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "NoEmployeeIdIsHavePartyId", 
						UtilMisc.toMap("partyId", partyId), UtilHttp.getLocale(request)));
				return "error";
			}
			String birthDateStr = (String)paramMap.get("birthDate");
			if(birthDateStr != null){
				Date birthDate = new Date(Long.parseLong(birthDateStr));
				paramMap.put("birthDate", birthDate);
			}
			if((String)paramMap.get("idIssueDate") != null){
				paramMap.put("idIssueDate", new Date(Long.parseLong((String)paramMap.get("idIssueDate"))));
			}
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updatePerson", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updatePerson", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			PartyUtil.updatePartyCode(delegator, partyId, (String)paramMap.get("partyCode"));
			List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>)paramMap.get("listFileUploaded");
			if(UtilValidate.isNotEmpty(listFileUploaded)){
				Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
				String _uploadedFile_contentType = (String)fileUploadedMap.get("_uploadedFile_contentType");
				ByteBuffer uploadedFile = (ByteBuffer)fileUploadedMap.get("uploadedFile");
				if(!CommonUtil.isImageFile(_uploadedFile_contentType)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "ImageAvatarIsNotValidForm", locale));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> imageCtx = FastMap.newInstance();
				imageCtx.put("userLogin", userLogin);
				imageCtx.put("locale", locale);
				imageCtx.put("timeZone", UtilHttp.getTimeZone(request));
				imageCtx.put("partyId", partyId);
				imageCtx.put("uploadedFile", uploadedFile);
				imageCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
				imageCtx.put("_uploadedFile_fileName", fileUploadedMap.get("_uploadedFile_fileName"));
				resultService = dispatcher.runSync("updatePersonalImage", imageCtx);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
			}
			String permanentRes = (String)paramMap.get("permanentRes");
			if(permanentRes != null){
				JSONObject jsonPermanentRes = JSONObject.fromObject(permanentRes);
				Map<String, Object> permanentResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonPermanentRes);
				if(permanentResMap != null){
					String serviceName = "createPartyPostalAddress";
					if(jsonPermanentRes.has("contactMechId")){
						String contactMechId = jsonPermanentRes.getString("contactMechId");
						if(contactMechId != null){
							serviceName = "updatePartyPostalAddress";
							permanentResMap.put("contactMechId", contactMechId);
						}
					}
					permanentResMap.put("userLogin", userLogin);
					permanentResMap.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
					permanentResMap.put("postalCode", "10000");
					permanentResMap.put("city", permanentResMap.get("stateProvinceGeoId"));
					permanentResMap.put("partyId", partyId);
					dispatcher.runSync(serviceName, permanentResMap);
				}
			}
			String currRes = (String)paramMap.get("currRes");
			if(currRes != null){
				JSONObject jsonCurrRes = JSONObject.fromObject(currRes);
				Map<String, Object> currResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonCurrRes);
				if(currResMap != null){
					String serviceName = "createPartyPostalAddress";
					if(jsonCurrRes.has("contactMechId")){
						String contactMechId = jsonCurrRes.getString("contactMechId");
						if(contactMechId != null){
							serviceName = "updatePartyPostalAddress";
							currResMap.put("contactMechId", contactMechId);
						}
					}
					currResMap.put("userLogin", userLogin);
					currResMap.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
					currResMap.put("postalCode", "10000");
					currResMap.put("city", currResMap.get("stateProvinceGeoId"));
					currResMap.put("partyId", partyId);
					dispatcher.runSync(serviceName, currResMap);
				}
			}
			String phoneMobile = (String)paramMap.get("phoneMobile");
			if(phoneMobile != null){
				JSONObject jsonPhoneMobile = JSONObject.fromObject(phoneMobile);
				Map<String, Object> phoneMobileMap = FastMap.newInstance();
				if(jsonPhoneMobile.has("phoneMobileNbr")){
					String serviceName = "createPartyTelecomNumber";
					if(jsonPhoneMobile.has("contactMechId")){
						String contactMechId = jsonPhoneMobile.getString("contactMechId");
						phoneMobileMap.put("contactMechId", contactMechId);
						serviceName = "updatePartyTelecomNumber";
					}
					phoneMobileMap.put("partyId", partyId);
					phoneMobileMap.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
					phoneMobileMap.put("userLogin", userLogin);
					phoneMobileMap.put("contactNumber", jsonPhoneMobile.getString("phoneMobileNbr"));
					dispatcher.runSync(serviceName, phoneMobileMap);
				}
			}
			String emailAddress = (String)paramMap.get("emailAddressInfo");
			if(emailAddress != null){
				JSONObject jsonEmailAddress = JSONObject.fromObject(emailAddress);
				Map<String, Object> emailAddressMap = FastMap.newInstance();
				if(jsonEmailAddress.has("emailAddress")){
					String serviceName = "createPartyEmailAddress";
					if(jsonEmailAddress.has("contactMechId")){
						String contactMechId = jsonEmailAddress.getString("contactMechId");
						emailAddressMap.put("contactMechId", contactMechId);
						serviceName = "updatePartyEmailAddress";
					}
					emailAddressMap.put("partyId", partyId);
					emailAddressMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
					emailAddressMap.put("emailAddress", jsonEmailAddress.getString("emailAddress"));
					emailAddressMap.put("userLogin", userLogin);
					dispatcher.runSync(serviceName, emailAddressMap);
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (FileUploadException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getEmplDeptPositionOther(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(person == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("EmployeeUiLabels", "NoEmployeeIdIsHavePartyId",
						UtilMisc.toMap("partyId", partyId), locale));
				return "error";
			}
			request.setAttribute("gender", person.get("gender"));
			Date birthDate = person.getDate("birthDate");
			if(birthDate != null){
				request.setAttribute("birthDate", birthDate.getTime());
			}
			List<String> listDept = PartyUtil.getDepartmentOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
			List<GenericValue> emplPositionTypeList = PartyUtil.getPositionTypeOfEmplAtTime(delegator, partyId, UtilDateTime.nowTimestamp());
			if(UtilValidate.isNotEmpty(emplPositionTypeList)){
				request.setAttribute("emplPositionTypeId", emplPositionTypeList.get(0).getString("emplPositionTypeId"));
			}
			if(UtilValidate.isNotEmpty(listDept)){
				List<String> rootList = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
				if(UtilValidate.isEmpty(rootList)){
					return "error";
				}
				List<String> ancestorTreeList = FastList.newInstance();
				String tempPartyId = listDept.get(0);
				while (!rootList.contains(tempPartyId)) {
					GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, tempPartyId);
					if(parentOrg == null){
						break;
					}
					String parentOrgId = parentOrg.getString("partyIdFrom");
					ancestorTreeList.add(0, parentOrgId);
					tempPartyId = parentOrgId;
				}
				request.setAttribute("ancestorTree", ancestorTreeList);
				request.setAttribute("partyIdFrom", listDept.get(0));
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getListUserLoginByPartyId(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			String lastOrg = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId, "lastOrg", lastOrg), null, false);
			request.setAttribute("listData", userLoginList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}

	public static String createNewSalesmanImports(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		try {
			Map<String, Object> paramMap = CommonUtil.getParameterMapWithFileUploaded(request);
			String partyCode = (String)paramMap.get("partyCode");
			String userLoginId = (String)paramMap.get("userLoginId");
			request.setAttribute("sequence",(String)paramMap.get("sequence"));
			List<GenericValue> partyCheck = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
			if(UtilValidate.isNotEmpty(partyCheck)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "PartyHaveExists", UtilMisc.toMap("partyId", partyCode), locale));
				return "error";
			}
			GenericValue userLoginTest = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
			if(userLoginTest != null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "UserLoginHaveExists", UtilMisc.toMap("userLoginId", userLoginId), locale));
				return "error";
			}
			String birthDateStr = (String)paramMap.get("birthDate");
			if(birthDateStr != null){
				Date birthDate = new Date(Long.parseLong(birthDateStr));
				paramMap.put("birthDate", birthDate);
			}
			if(paramMap.get("idIssueDate") != null){
				paramMap.put("idIssueDate", new Date(Long.parseLong((String)paramMap.get("idIssueDate"))));
			}
			Timestamp dateJoinCompany = null;
			if(paramMap.get("dateJoinCompany") != null){
				dateJoinCompany = new Timestamp(Long.parseLong((String)paramMap.get("dateJoinCompany")));
			}else{
				dateJoinCompany = UtilDateTime.nowTimestamp();
			}
			dateJoinCompany = UtilDateTime.getDayStart(dateJoinCompany);
			paramMap.put("dateJoinCompany", dateJoinCompany);
			Date dateParticipateIns = null;
			if((String)paramMap.get("dateParticipateIns") != null){
				dateParticipateIns = new Date(Long.parseLong((String)paramMap.get("dateParticipateIns")));
			}
			if(paramMap.get("thruDate") != null){
				Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong((String)paramMap.get("thruDate"))));
				paramMap.put("thruDate", thruDate);
			}
			paramMap.put("dateParticipateIns", dateParticipateIns);
			Timestamp effectiveFromDate = null;
			if(paramMap.get("heathInsuranceFromDate") != null){
				effectiveFromDate = UtilDateTime.getMonthStart(new Timestamp(Long.parseLong((String)paramMap.get("heathInsuranceFromDate"))));
			}
			paramMap.put("heathInsuranceFromDate", effectiveFromDate);
			Timestamp effectiveThruDate = null;
			if(paramMap.get("heathInsuranceThruDate") != null){
				effectiveThruDate = UtilDateTime.getMonthEnd(new Timestamp(Long.parseLong((String)paramMap.get("heathInsuranceThruDate"))), timeZone, locale);
			}
			paramMap.put("heathInsuranceThruDate", effectiveThruDate);

			if(paramMap.get("participateFromDate") != null){
				Timestamp participateFromDate = UtilDateTime.getMonthStart(new Timestamp(Long.parseLong((String)paramMap.get("participateFromDate"))));
				paramMap.put("participateFromDate", participateFromDate);
			}
			for(Map.Entry<String, Object> entry: paramMap.entrySet()){
				if(entry.getValue() instanceof String){
					if(entry.getValue() != null && ((String)entry.getValue()).length() == 0){
						paramMap.remove(entry);
					}
				}
			}
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPerson", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			TransactionUtil.begin();
			Map<String, Object> resultService = dispatcher.runSync("createPerson", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			String partyId = (String)resultService.get("partyId");
			PartyUtil.updatePartyCode(delegator, partyId, partyCode);
			//create avatar
			List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>)paramMap.get("listFileUploaded");
			if(UtilValidate.isNotEmpty(listFileUploaded)){
				Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
				String _uploadedFile_contentType = (String)fileUploadedMap.get("_uploadedFile_contentType");
				ByteBuffer uploadedFile = (ByteBuffer)fileUploadedMap.get("uploadedFile");
				if(!CommonUtil.isImageFile(_uploadedFile_contentType)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "ImageAvatarIsNotValidForm", locale));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> imageCtx = FastMap.newInstance();
				imageCtx.put("userLogin", userLogin);
				imageCtx.put("locale", locale);
				imageCtx.put("timeZone", UtilHttp.getTimeZone(request));
				imageCtx.put("partyId", partyId);
				imageCtx.put("uploadedFile", uploadedFile);
				imageCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
				imageCtx.put("_uploadedFile_fileName", fileUploadedMap.get("_uploadedFile_fileName"));
				resultService = dispatcher.runSync("updatePersonalImage", imageCtx);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
			}
			String permanentRes = (String)paramMap.get("permanentRes");
			if(permanentRes != null){
				JSONObject jsonPermanentRes = JSONObject.fromObject(permanentRes);
				Map<String, Object> permanentResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonPermanentRes);
				if(permanentResMap != null){
					permanentResMap.put("userLogin", userLogin);
					permanentResMap.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
					permanentResMap.put("postalCode", "10000");
					permanentResMap.put("city", permanentResMap.get("stateProvinceGeoId"));
					permanentResMap.put("partyId", partyId);
					dispatcher.runSync("createPartyPostalAddress", permanentResMap);
				}
			}
			String currRes = (String)paramMap.get("permanentRes");
			if(currRes != null){
				JSONObject jsonCurrRes = JSONObject.fromObject(currRes);
				Map<String, Object> currResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonCurrRes);
				if(currResMap != null){
					currResMap.put("userLogin", userLogin);
					currResMap.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
					currResMap.put("postalCode", "10000");
					currResMap.put("city", currResMap.get("stateProvinceGeoId"));
					currResMap.put("partyId", partyId);
					dispatcher.runSync("createPartyPostalAddress", currResMap);
				}
			}
			Map<String, Object> employmentWorkInfo = ServiceUtil.setServiceFields(dispatcher, "createEmploymentWorkInfo", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			String insuranceTypeNotCompulsory = (String)paramMap.get("insuranceTypeNotCompulsory");
			if(insuranceTypeNotCompulsory != null){
				List<String> insuranceTypeIdList = FastList.newInstance();
				JSONArray insuranceTypeNotCompulsoryJson = JSONArray.fromObject(insuranceTypeNotCompulsory);
				for(int i = 0; i < insuranceTypeNotCompulsoryJson.size(); i++){
					insuranceTypeIdList.add(insuranceTypeNotCompulsoryJson.getString(i));
				}
				employmentWorkInfo.put("insuranceTypeIdList", insuranceTypeIdList);
			}
			employmentWorkInfo.put("fromDate", dateJoinCompany);
			BigDecimal salaryBaseFlat = new BigDecimal((String)paramMap.get("salaryBaseFlat"));
			BigDecimal insuranceSalary = paramMap.get("insuranceSalary")!= null? new BigDecimal((String)paramMap.get("insuranceSalary")): BigDecimal.ZERO;
			employmentWorkInfo.put("rateAmount", salaryBaseFlat);
			employmentWorkInfo.put("insuranceSalary", insuranceSalary);
			employmentWorkInfo.put("partyIdTo", partyId);
			employmentWorkInfo.put("locale", locale);
			employmentWorkInfo.put("timeZone", timeZone);
			resultService = dispatcher.runSync("createEmploymentWorkInfo", employmentWorkInfo);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}

			//create userLogin
			resultService = dispatcher.runSync("createUserLogin",
					UtilMisc.toMap("userLoginId", paramMap.get("userLoginId"),
							"enabled", "Y", "currentPassword", paramMap.get("password"),
							"currentPasswordVerify", paramMap.get("password"),
							"requirePasswordChange", "Y",
							"partyId", partyId, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			//update lastOrg new userLogin
			GenericValue userLoginNew = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("userLoginId")), false);
			userLoginNew.set("lastOrg", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
			userLoginNew.store();
			//------
			List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig",
					UtilMisc.toMap("emplPositionTypeId", (String)paramMap.get("emplPositionTypeId")), null, false);
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			for(GenericValue tempGv: emplPosTypeSecGroupConfig){
				String groupId = tempGv.getString("groupId");
				dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", paramMap.get("userLoginId"),
						"groupId", groupId,
						"fromDate", dateJoinCompany,
						"organizationId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")),
						"userLogin", systemUserLogin));
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
			Debug.log(module + "::createNewEmployee, emplPositionTypeId = " + ((String) paramMap.get("emplPositionTypeId")));

			if(((String) paramMap.get("emplPositionTypeId")).contains("SALES_EXECUTIVE")) {
				String fullName = (String) paramMap.get("firstName");
				String partyIdfrom = (String) paramMap.get("partyIdFrom");
				if(!UtilValidate.isEmpty(paramMap.get("middleName"))) fullName = paramMap.get("middleName") + " " + fullName;
				if(!UtilValidate.isEmpty(paramMap.get("lastName"))) fullName = paramMap.get("lastName") + " " + fullName;

				Debug.log(module + "::createNewEmployee, START createPartySalesman, departmentId = " + partyIdfrom +
						", partyCode = " + partyCode);

				dispatcher.runSync("createPartySalesman", UtilMisc.toMap("partyId", partyId,
						"partyCode", partyCode, "fullName", fullName, "statusId", "PARTY_ENABLED",
						"preferredCurrencyUomId", "VND", "departmentId", partyIdfrom, "userLogin", userLogin));
			}
			TransactionUtil.commit();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				e1.printStackTrace();
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				e1.printStackTrace();
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
