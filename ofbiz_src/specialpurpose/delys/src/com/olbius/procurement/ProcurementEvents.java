package com.olbius.procurement;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class ProcurementEvents {
	public static final String resource = "DelysProcurementLabels";
	public static String sendProposalToCeo(HttpServletRequest request, HttpServletResponse respons){
		String requirementId = request.getParameter("requirementId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String errorMessage = "";
		if (UtilValidate.isNotEmpty(requirementId)) {
			GenericValue requirement = null;
			try {
				requirement  = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(UtilValidate.isNotEmpty(requirement)){
				String statusId = requirement.getString("statusId");
				Map<String, Object> result = new HashMap<String, Object>();
				if(statusId.equalsIgnoreCase("REQ_CREATED") || statusId.equalsIgnoreCase("REQ_IN_PROGRESS")){
					String statusType = "REQ_IN_PROGRESS";
						Map<String, Object> updateProcurementProposal = new HashMap<String, Object>();
						updateProcurementProposal.put("userLogin", userLogin);
						updateProcurementProposal.put("statusId", statusType);
						updateProcurementProposal.put("requirementId", requirementId);
						try {
							dispatcher.runSync("updateRequirement", updateProcurementProposal);
						} catch (GenericServiceException e1) {
							// TODO Auto-generated catch block
							errorMessage = ServiceUtil.getErrorMessage(result);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
						/*delegator.store(requirement);*/
						Map<String, Object> createNotification = new HashMap<String, Object>();
						createNotification.put("userLogin", userLogin);
						createNotification.put("state", "open");
						Date nowTime = new Date();
						Timestamp nowTimeStamp = new Timestamp(nowTime.getTime());
						
						String header = UtilProperties.getMessage(resource, "PleaseApproveShoppingProposal", UtilMisc.toMap("proposalId", requirementId), locale);
						createNotification.put("dateTime", nowTimeStamp);
						createNotification.put("header", header);
						createNotification.put("ntfType", "one");
						createNotification.put("action", "viewProcurementProposal?requirementId=" + requirementId);
						createNotification.put("targetLink", requirementId);
						createNotification.put("roleTypeId", "DELYS_CEO");
						
						try {
							result = dispatcher.runSync("createNotification", createNotification);
						} catch (GenericServiceException e) {
							
							// TODO Auto-generated catch block
							errorMessage = ServiceUtil.getErrorMessage(result);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
							
						}
				}else{
					 errorMessage = UtilProperties.getMessage(resource, "NotSendProposalToCeo", UtilMisc.toMap("requirementId", requirementId), locale);
					 request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					 return "error";
				}
			}
			
			
		}
		return "success";
	}
	public static String approveRequirement(HttpServletRequest request, HttpServletResponse respons) throws GenericEntityException {
		String requirementId = request.getParameter("requirementId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String errorMessage = "";
		Security security = (Security) request.getAttribute("security");
		boolean hasPermisson = security.hasEntityPermission("PROCUREMENT", "_UPDATE", userLogin);
        if(!hasPermisson){
        	errorMessage = UtilProperties.getMessage(resource, "NotEnoughPermissionToEditThisProposal", locale);
        	request.setAttribute("_ERROR_MESSAGE_", errorMessage);
        }
        if(UtilValidate.isNotEmpty(requirementId)){
        	String result = updateRequirement(requirementId, "approve", request);
        	return result;
        }
        
		return "success";
	}
	public static String rejectRequirement(HttpServletRequest request, HttpServletResponse respons) throws GenericEntityException{
		String requirementId = request.getParameter("requirementId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String errorMessage = "";
		Security security = (Security) request.getAttribute("security");
		boolean hasPermisson = security.hasEntityPermission("PROCUREMENT", "_UPDATE", userLogin);
        if(!hasPermisson){
        	errorMessage = UtilProperties.getMessage(resource, "NotEnoughPermissionToEditThisProposal", locale);
        	request.setAttribute("_ERROR_MESSAGE_", errorMessage);
        }
        if(UtilValidate.isNotEmpty(requirementId)){
        	String result = updateRequirement(requirementId, "reject", request);
        	return result;
        }
        
		return "success";
	}
	public static String updateRequirement(String requirementId, String action, HttpServletRequest request) throws GenericEntityException{
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String errorMessage = "";
		GenericValue requirement = null;
		try {
			requirement  = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(requirement)){
			String statusId = requirement.getString("statusId");
			Map<String, Object> result = new HashMap<String, Object>();
			
			if(!statusId.equalsIgnoreCase("REQ_CREATED")){
				String statusType = "REQ_IN_PROGRESS";
				if(action.equalsIgnoreCase("reject")){
					statusType = "REQ_REJECTED";
				}else if(action.equalsIgnoreCase("approve")){
					statusType = "REQ_APPROVED";
				}
				boolean beganTx = TransactionUtil.begin();
				
				
				Map<String, Object> updateProcurementProposal = new HashMap<String, Object>();
				updateProcurementProposal.put("userLogin", userLogin);
				updateProcurementProposal.put("statusId", statusType);
				updateProcurementProposal.put("requirementId", requirementId);
				try {
					dispatcher.runSync("updateRequirement", updateProcurementProposal);
				} catch (GenericServiceException e1) {
					// TODO Auto-generated catch block
					errorMessage = ServiceUtil.getErrorMessage(result);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
				/*delegator.store(requirement);*/
				Map<String, Object> createNotification = new HashMap<String, Object>();
				createNotification.put("userLogin", userLogin);
				createNotification.put("state", "open");
				Date nowTime = new Date();
				Timestamp nowTimeStamp = new Timestamp(nowTime.getTime());
				
				String header = "";
				String[] parameterForUiLabel = new String[2];
				parameterForUiLabel[0] = requirementId;
				parameterForUiLabel[1] = userLogin.getString("userLoginId");
				if(action.equalsIgnoreCase("reject")){
				
					header = UtilProperties.getMessage(resource, "CanceledShoppingProposal", parameterForUiLabel, locale);
					
					
				}else if(action.equalsIgnoreCase("approve")){
					header = UtilProperties.getMessage(resource, "ApprovedShoppingProposal" ,parameterForUiLabel, locale);
					
				} 
				createNotification.put("targetLink", requirementId);
				createNotification.put("action", "viewProcurementProposal?requirementId=" + requirementId);
				createNotification.put("roleTypeId", "DELYS_PROCUREMENT");
				createNotification.put("dateTime", nowTimeStamp);
				createNotification.put("header", header);
				createNotification.put("ntfType", "one");
				try {
					result = dispatcher.runSync("createNotification", createNotification);
					if(!ServiceUtil.isError(result)){
						 TransactionUtil.commit(beganTx);
						
					}
				} catch (GenericServiceException e) {
					
					// TODO Auto-generated catch block
					errorMessage = ServiceUtil.getErrorMessage(result);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}else{
				 errorMessage = UtilProperties.getMessage(resource, "CanNotProcessTheProcurementProposal", UtilMisc.toMap("requirementId", requirementId), locale);
				 request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				 return "error";
			}
			
			
		}
		
		return "success";
		
		
	}
	public static String rejectedFromProcurementDepartment(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String requirementId = request.getParameter("requirementId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		
		String errorMessage = "";
		Security security = (Security) request.getAttribute("security");
		boolean hasPermisson = security.hasEntityPermission("PROCUREMENT", "_UPDATE", userLogin);
        if(!hasPermisson){
        	errorMessage = UtilProperties.getMessage(resource, "NotEnoughPermissionToEditThisProposal", locale);
        	request.setAttribute("_ERROR_MESSAGE_", errorMessage);
        }
		GenericValue requirement = null;
		String createdByUserLogin = "";
		try {
			requirement  = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(requirement)){
			
			String statusId = requirement.getString("statusId");
			createdByUserLogin = requirement.getString("createdByUserLogin");
			GenericValue createBy = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", createdByUserLogin), false);
			
			String partyId = createBy.getString("partyId");
		
			Map<String, Object> result = new HashMap<String, Object>();
			if(statusId.equalsIgnoreCase("REQ_CREATED") || statusId.equalsIgnoreCase("REQ_IN_PROGRESS") || statusId.equalsIgnoreCase("REQ_REJECTED")){
				String	statusType = "REQ_REJECTED";
				boolean beganTx = TransactionUtil.begin();
				Map<String, Object> updateProcurementProposal = new HashMap<String, Object>();
				updateProcurementProposal.put("userLogin", userLogin);
				updateProcurementProposal.put("statusId", statusType);
				updateProcurementProposal.put("requirementId", requirementId);
				try {
					dispatcher.runSync("updateRequirement", updateProcurementProposal);
				} catch (GenericServiceException e1) {
					// TODO Auto-generated catch block
					errorMessage = ServiceUtil.getErrorMessage(result);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
				/*delegator.store(requirement);*/
				Map<String, Object> createNotification = new HashMap<String, Object>();
				createNotification.put("userLogin", userLogin);
				createNotification.put("state", "open");
				Date nowTime = new Date();
				Timestamp nowTimeStamp = new Timestamp(nowTime.getTime());
				String[] parameterForUiLabel = new String[2];
				parameterForUiLabel[0] = requirementId;
				parameterForUiLabel[1] = userLogin.getString("userLoginId");
				String header = UtilProperties.getMessage(resource, "CanceledShoppingProposal", parameterForUiLabel, locale);
				createNotification.put("targetLink", requirementId);
				createNotification.put("action", "viewProcurementProposal?requirementId=" + requirementId);
				createNotification.put("partyId", partyId);
				createNotification.put("dateTime", nowTimeStamp);
				createNotification.put("header", header);
				createNotification.put("ntfType", "one");
				try {
					result = dispatcher.runSync("createNotification", createNotification);
					if(!ServiceUtil.isError(result)){
						 TransactionUtil.commit(beganTx);
						
					}
				} catch (GenericServiceException e) {
					
					// TODO Auto-generated catch block
					errorMessage = ServiceUtil.getErrorMessage(result);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}else{
				 errorMessage = UtilProperties.getMessage(resource, "CanNotProcessTheProcurementProposal", UtilMisc.toMap("requirementId", requirementId), locale);
				 request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				 return "error";
			}
			
		}
		
		return "success";
		
		
	}
	

}
