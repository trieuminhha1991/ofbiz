package com.olbius.event;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ModelService;

public class CommonEvents {
	public static String getPostalAddressGeoDetail(HttpServletRequest request, HttpServletResponse respone){
		String contactMechId = request.getParameter("contactMechId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		if(contactMechId != null){
			try {
				GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
				if(postalAddress != null){
					request.setAttribute("address1", postalAddress.getString("address1"));
					request.setAttribute("countryGeoId", postalAddress.getString("countryGeoId"));
					request.setAttribute("stateProvinceGeoId", postalAddress.getString("stateProvinceGeoId"));
					request.setAttribute("districtGeoId", postalAddress.getString("districtGeoId"));
					request.setAttribute("wardGeoId", postalAddress.getString("wardGeoId"));
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";
			}
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		return "success";
	}
	
	public static String getCustomTimePeriodByParent(HttpServletRequest request, HttpServletResponse respone){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String parentPeriodId = request.getParameter("parentPeriodId");
		try {
			List<GenericValue> listCustomTimePeriod = delegator.findByAnd("CustomTimePeriod", UtilMisc.toMap("parentPeriodId", parentPeriodId), UtilMisc.toList("fromDate"), false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			for(GenericValue tmpGv: listCustomTimePeriod){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("customTimePeriodId", tmpGv.getString("customTimePeriodId"));
				tempMap.put("periodName", tmpGv.getString("periodName"));
				tempMap.put("fromDate", tmpGv.getDate("fromDate").getTime());
				tempMap.put("thruDate", tmpGv.getDate("thruDate").getTime());
				listReturn.add(tempMap);
			}
			request.setAttribute("listCustomTimePeriod", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
