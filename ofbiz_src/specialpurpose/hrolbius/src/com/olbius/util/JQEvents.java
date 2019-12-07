package com.olbius.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ModelService;

import com.olbius.recruitment.helper.RecruitmentDataPreparation;
import com.olbius.services.JqxWidgetSevices;

public class JQEvents {
	
	public static final String MODULE = JQEvents.class.getName();
	
	public static String getPartyRole(HttpServletRequest request, HttpServletResponse response) {
		//Get Delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		//Get parameters
		Map<String, Object> parameters = UtilHttp.getParameterMap(request);
		
		String partyId = (String)parameters.get("partyId");
		List<GenericValue> listRoleTypes =  SecurityUtil.getGVCurrentRoles(partyId, delegator);
		request.setAttribute("listRoleTypes", listRoleTypes);
		request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return "success";
	}
	
	public static String getResourceInPlan(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
        try {
        	  //Get parameters
            Map<String, Object> parameters = UtilHttp.getParameterMap(request);
            String partyId = (String)parameters.get("partyId");
            String emplPositionTypeId = (String)parameters.get("emplPositionTypeId");
			Timestamp fromDate = (Timestamp) JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("fromDate"));
			long result = RecruitmentDataPreparation.getResourceInPlan(delegator, partyId, emplPositionTypeId, fromDate);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			request.setAttribute("resourceInPlan", result);
        } catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), MODULE);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return "error";
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), MODULE);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return "error";
		}
        return "success";
	}
	
	public static String getPartyName(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
        try {
        	  //Get parameters
            Map<String, Object> parameters = UtilHttp.getParameterMap(request);
            String partyId = (String)parameters.get("partyId");
			String  partyName = PartyUtil.getPartyName(delegator, partyId);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			request.setAttribute("partyName", partyName);
		} catch (Exception e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			return "error";
		}
        return "success";
	}
	
	public static String getManagerOfEmpl(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
        try {
        	  //Get parameters
            Map<String, Object> parameters = UtilHttp.getParameterMap(request);
            String partyId = (String)parameters.get("partyId");
			String  managerId = PartyUtil.getManagerOfEmpl(delegator, partyId);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			request.setAttribute("managerId", managerId);
		} catch (Exception e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			return "error";
		}
        return "success";
	}
	
	public static String getEmplPositionType(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
        try {
        	  //Get parameters
            Map<String, Object> parameters = UtilHttp.getParameterMap(request);
            String emplPositionTypeId = (String)parameters.get("emplPositionTypeId");
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			request.setAttribute("description", emplPositionType.getString("description"));
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), MODULE);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			return "error";
		}
        return "success";
	}
	
	public static String getRecruitmentType(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
        try {
        	  //Get parameters
            Map<String, Object> parameters = UtilHttp.getParameterMap(request);
            String recruitmentTypeId = (String)parameters.get("recruitmentTypeId");
			GenericValue recruitmentType = delegator.findOne("RecruitmentType", UtilMisc.toMap("recruitmentTypeId", recruitmentTypeId), false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			request.setAttribute("description", recruitmentType.getString("description"));
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), MODULE);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			return "error";
		}
        return "success";
	}
}
