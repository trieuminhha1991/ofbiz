package com.olbius.globalSetting;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class GlobalSettingEvents {
	public static String updateEmplPositionTypeWorkWeek(HttpServletRequest request, HttpServletResponse respone){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		String mondayWS = request.getParameter("monday");
		String tuesdayWS = request.getParameter("tuesday");
		String wednesdayWS = request.getParameter("wednesday");
		String thursdayWS = request.getParameter("thursday");
		String fridayWS = request.getParameter("friday");
		String saturdayWS = request.getParameter("saturday");
		String sundayWS = request.getParameter("sunday");
		List<String> wsDayOfWeek = FastList.newInstance();
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		
		try {
			//update for monday
			if("CA_NGAY".equals(mondayWS)){
				//get all workShift
				//wsMonday = get all workShift 
				wsDayOfWeek.add("CA_SANG");
				wsDayOfWeek.add("CA_CHIEU");
			}else{
				 wsDayOfWeek.add(mondayWS);
			}
			dispatcher.runSync("updateEmplPositionTypeWorkWeek", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "dayOfWeek", "MONDAY", "workShiftId", wsDayOfWeek, "userLogin", userLogin));
			
			//update tuesday
			wsDayOfWeek.clear();
			if("CA_NGAY".equals(tuesdayWS)){
				wsDayOfWeek.add("CA_SANG");
				wsDayOfWeek.add("CA_CHIEU");
			}else{
				 wsDayOfWeek.add(tuesdayWS);
			}
			dispatcher.runSync("updateEmplPositionTypeWorkWeek", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "dayOfWeek", "TUESDAY", "workShiftId", wsDayOfWeek, "userLogin", userLogin));
			
			//update wednesday
			wsDayOfWeek.clear();
			if("CA_NGAY".equals(wednesdayWS)){
				wsDayOfWeek.add("CA_SANG");
				wsDayOfWeek.add("CA_CHIEU"); 
			}else{
				 wsDayOfWeek.add(wednesdayWS);
			}
			dispatcher.runSync("updateEmplPositionTypeWorkWeek", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "dayOfWeek", "WEDNESDAY", "workShiftId", wsDayOfWeek, "userLogin", userLogin));
			
			//update thursday
			wsDayOfWeek.clear();
			if("CA_NGAY".equals(thursdayWS)){
				wsDayOfWeek.add("CA_SANG");
				wsDayOfWeek.add("CA_CHIEU");
			}else{
				 wsDayOfWeek.add(thursdayWS);
			}
			dispatcher.runSync("updateEmplPositionTypeWorkWeek", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "dayOfWeek", "THURSDAY", "workShiftId", wsDayOfWeek, "userLogin", userLogin));
			
			//update friday
			wsDayOfWeek.clear();
			if("CA_NGAY".equals(fridayWS)){
				wsDayOfWeek.add("CA_SANG");
				wsDayOfWeek.add("CA_CHIEU");
			}else{
				 wsDayOfWeek.add(fridayWS);
			}
			dispatcher.runSync("updateEmplPositionTypeWorkWeek", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "dayOfWeek", "FRIDAY", "workShiftId", wsDayOfWeek, "userLogin", userLogin));
			
			//update saturday
			wsDayOfWeek.clear();
			if("CA_NGAY".equals(saturdayWS)){
				wsDayOfWeek.add("CA_SANG");
				wsDayOfWeek.add("CA_CHIEU");
			}else{
				 wsDayOfWeek.add(saturdayWS);
			}
			dispatcher.runSync("updateEmplPositionTypeWorkWeek", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "dayOfWeek", "SATURDAY", "workShiftId", wsDayOfWeek, "userLogin", userLogin));
			
			//update sunday
			wsDayOfWeek.clear();
			if("CA_NGAY".equals(sundayWS)){
				wsDayOfWeek.add("CA_SANG");
				wsDayOfWeek.add("CA_CHIEU");
			}else{
				 wsDayOfWeek.add(sundayWS);
			}
			dispatcher.runSync("updateEmplPositionTypeWorkWeek", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "dayOfWeek", "SUNDAY", "workShiftId", wsDayOfWeek, "userLogin", userLogin));
			
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
