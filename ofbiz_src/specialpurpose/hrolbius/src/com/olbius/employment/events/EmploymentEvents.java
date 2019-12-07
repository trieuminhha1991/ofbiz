package com.olbius.employment.events;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.CommonUtil;
import com.olbius.util.SecurityUtil;

public class EmploymentEvents {
	@SuppressWarnings("unchecked")
	public static String getPositionByPositionTypeAndParty(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> listReturn = FastList.newInstance();
		String partyId = (String)parameterMap.get("partyId");
		String emplPositionTypeId = (String)parameterMap.get("emplPositionTypeId");
		String totalRow = "0";
		if(partyId != null && emplPositionTypeId != null){
			try {
				Map<String, Object> resultService = dispatcher.runSync("getPositionByPositionTypeAndParty", ServiceUtil.setServiceFields(dispatcher, "getPositionByPositionTypeAndParty", parameterMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
				listReturn = (List<Map<String,Object>>)resultService.get("listReturn");
				totalRow = (String)resultService.get("TotalRows");
			} catch (GeneralServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		request.setAttribute("listReturn", listReturn);
		request.setAttribute("TotalRows", totalRow);
		return "success";
	}
	
	public static String editOrgPostalAddress(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		String contactMechId = request.getParameter("contactMechId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("partyId", partyId);
		ctxMap.put("address1", request.getParameter("address1"));
		ctxMap.put("countryGeoId", request.getParameter("countryGeoId"));
		ctxMap.put("postalCode", "10000");
		String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
		ctxMap.put("stateProvinceGeoId", stateProvinceGeoId);
		ctxMap.put("city", stateProvinceGeoId);
		ctxMap.put("districtGeoId", request.getParameter("districtGeoId"));
		ctxMap.put("wardGeoId", request.getParameter("wardGeoId"));
		ctxMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
		Map<String, Object> resultService;
		String contactMechIdEdit = null;
		try {
			if(contactMechId != null){
				ctxMap.put("contactMechId", contactMechId);
				resultService = dispatcher.runSync("updatePartyPostalAddress", ctxMap);
				contactMechIdEdit = (String)resultService.get("contactMechId");
			}else{
				resultService = dispatcher.runSync("createPartyPostalAddress", ctxMap);
				contactMechIdEdit = (String)resultService.get("contactMechId");
			}
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute("contactMechId", contactMechIdEdit);
				String postAddressDesc = CommonUtil.getPostalAddressDetails(delegator, contactMechIdEdit);
				request.setAttribute("postAddressDesc", postAddressDesc);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				return "error";
			}
			
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		} catch (GenericEntityException e) {
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
			dispatcher.runSync("updatePartyGroup", ServiceUtil.setServiceFields(dispatcher, "updatePartyGroup", parameterMap, userLogin, timeZone, locale));
			if(partyIdFromOld != null && partyIdFromNew != null){
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
							Map<String , Object> resultService = dispatcher.runSync("createPartyRelationship", ctxMap);
							if(ServiceUtil.isSuccess(resultService)){
								TransactionUtil.commit();
							}else{
								request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
								request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							}
						}else{
							request.setAttribute(ModelService.ERROR_MESSAGE, "have no role");
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						}
						
					}
				}
			}
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
}
