package org.ofbiz.mobileservices;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import org.ofbiz.Mobile;
import org.ofbiz.base.conversion.DateTimeConverters;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import com.olbius.baseecommerce.party.ProfileUtils;
import com.olbius.crm.CallcenterServices;
import com.olbius.basehr.util.PersonHelper;

public class EmployeeServices implements Mobile {

	public static final String module = EmployeeServices.class.getName();
	public static final String resource = "AppBaseUiLabels";

	/**
	 * kieu nghi phep
	 *
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> getEmployeeLeaveType(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		try {
			List<GenericValue> leaveType = delegator.findList("EmplLeaveType",
					null, UtilMisc.toSet("leaveTypeId", "description"), null,
					null, false);
			res.put("leaveType", leaveType);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	/**
	 * ly do nghi phep
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public static  Map<String, Object> getEmployeeLeaveReason(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		try {
			List<GenericValue> leaveReasonType = delegator.findList(
					"EmplLeaveReasonType", null,
					UtilMisc.toSet("emplLeaveReasonTypeId", "description"), null, null, false);
			res.put("leaveReasonType", leaveReasonType);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	/**
	 * Working shift get data
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public static  Map<String, Object> getWorkingShift(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		try{
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(java.sql.Time.class, new JsonValueProcessor() {
				@Override
				public Object processArrayValue(Object value, JsonConfig jsonConfig) {
					Time[] datetimes = (Time[]) value;
					Long[] result = new Long[datetimes.length];
					for (int index = 0; index < datetimes.length; index++) {
						result[index] = datetimes[index].getTime();
					}
					return result;
				}

				@Override
				public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
					Time time = (Time) value;
					return time.getTime();
				}
			});
			List<GenericValue> workingShift = delegator.findList("WorkingShift", null,
						null, null, null, false);
			JSONArray json = JSONArray.fromObject(workingShift, config);
					res.put("workingShift", json.toString());
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	/**
	 * gui don xin nghi phep
	 *
	 * @param dctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, ? extends Object> createEmplLeave(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String fromDateLeaveTypeId = (String) context.get("fromDateLeaveTypeId");
		String thruDateLeaveTypeId = (String) context.get("thruDateLeaveTypeId");
		String workingShiftId = (String) context.get("workingShiftId");
		String fromDate = (String) context.get("fromDate");
		String thruDate = (String) context.get("thruDate");
		String description = (String) context.get("description");
		String emplLeaveReasonTypeId = (String) context.get("emplLeaveReasonTypeId");
		String partyId = userLogin.getString("partyId");
		SimpleDateFormat formate = new SimpleDateFormat("dd-MM-yyyy");
		Map<String, Object> res = ServiceUtil.returnSuccess();
		try {
			GenericValue permissionUserLogin = delegator.findOne("UserLogin",
			UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("userLogin", permissionUserLogin);
			ctxMap.put("partyId", partyId);
			ctxMap.put("fromDateLeaveTypeId", fromDateLeaveTypeId);
			ctxMap.put("thruDateLeaveTypeId", thruDateLeaveTypeId);
			ctxMap.put("workingShiftId", workingShiftId);
			ctxMap.put("fromDate", new Timestamp(formate.parse(fromDate)
					.getTime()));
			ctxMap.put("thruDate", new Timestamp(formate.parse(thruDate)
					.getTime()));
			ctxMap.put("description", description);
			ctxMap.put("emplLeaveReasonTypeId", emplLeaveReasonTypeId);
			Map<String, Object> out = dispatcher.runSync("createEmplLeave", ctxMap);
			res.putAll(out);
			MobileUtils.sendNotifyManager(dctx, context, "BSSalesmanLeavingNotify", "ViewListEmplLeave", "");
		}
		catch (ParseException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		} catch (Exception e) {
			if(e.getMessage().contains("Duplicate")){
				return ServiceUtil.returnError("ALDREADY_EXIST");
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}

	public static Map<String, Object> getEmpLeaveStatus(DispatchContext dctx, Map<String, Object> context)   {
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		String partyId = userLogin.getString("partyId");
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Integer year = (Integer) context.get("year");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp yearStart = UtilDateTime.getYearStart(timestamp);
		Timestamp yearEnd = UtilDateTime.getYearEnd(yearStart, timeZone, locale);
		try {
			List<GenericValue> fk = delegator.findList("EmplLeaveDetail", EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId), 
							EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, yearStart), 
							EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, yearEnd))), 
					null, UtilMisc.toList("-fromDate"), null, false);
			List<Map<String, Object>> resList = FastList.newInstance();
			for(GenericValue e : fk){
				Map<String, Object> o = FastMap.newInstance();
				o.put("emplLeaveId", e.getString("emplLeaveId"));
				Timestamp fromDate = e.getTimestamp("fromDate");
				if(fromDate != null){
					o.put("fromDate", fromDate.getTime());
				}
				Timestamp thruDate = e.getTimestamp("thruDate");
				if(thruDate != null){
					o.put("thruDate", thruDate.getTime());
				}
				o.put("reason", e.getString("reasonDescription"));
				o.put("description", e.getString("note"));
				o.put("statusId", e.get("description", locale));
				o.put("entryDate", e.get("dateApplication"));
				String wks = e.getString("workingShiftId");
				o.put("workingShiftId", wks);
				GenericValue we  = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", wks), true);
				if(we != null){
					String f = e.getString("fromDateLeaveTypeId");
					String t = e.getString("thruDateLeaveTypeId");
					if(f.equals("FIRST_HALF_DAY")){
						Time shiftStartTime = we.getTime("shiftStartTime");
						o.put("startTime", shiftStartTime.getTime());	
					}else if(f.equals("SECOND_HALF_DAY")){
						Time shiftBreakStart = we.getTime("shiftBreakStart");
						o.put("startTime", shiftBreakStart.getTime());
					}
					if(t.equals("FIRST_HALF_DAY")){
						Time shiftBreakEnd = we.getTime("shiftBreakEnd");
						o.put("endTime", shiftBreakEnd.getTime());
					}else if(t.equals("SECOND_HALF_DAY")){
						Time shiftEndTime = we.getTime("shiftEndTime");
						o.put("endTime", shiftEndTime.getTime());
					}
				}
				resList.add(o);
			}
			res.put("emlLeaveStatus", resList);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}

	/**
	 * edit avatar salesman
	 *
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> editAvatarSalesman(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		// Map requestMap = UtilHttp.getParameterMap(request);
		// request.getParameter("formData");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		// String dataImge = request.getParameter("uploadedFile");
		String fileName = (String) context.get("filename");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			GenericValue system = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);

			ByteBuffer uploadedFile = (ByteBuffer) context.get("uploadedFile");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String contentType = fileNameMap.getContentTypeFor(fileName);

			Map<String, Object> contextMap = FastMap.newInstance();
			contextMap.put("dataCategoryId", "PERSONAL");
			contextMap.put("contentTypeId", "DOCUMENT");
			contextMap.put("statusId", "CTNT_PUBLISHED");
			contextMap.put("userLogin", system);
			contextMap.put("partyId", userLogin.get("partyId"));
			contextMap.put("isPublic", "Y");
			contextMap.put("roleTypeId", "_NA_");
			contextMap.put("partyContentTypeId", "LGOIMGURL");
			contextMap.put("uploadedFile", uploadedFile);
			contextMap.put("_uploadedFile_fileName", fileName);
			contextMap.put("_uploadedFile_contentType", contentType);
			// contextMap.put("mimeTypeId", contentType);

			Map<String, Object> result = null;

			result = dispatcher.runSync("uploadPartyContentFile", contextMap);
			retMap.put("resultUpdate", result);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}

	/*@param DispatchContext dpct,Map<?,?>
	 * updateSalesMan
	 * */
	public static Map<String,Object> updateProfileMobile(DispatchContext dpct,Map<String,?extends Object> context){
		LocalDispatcher dispatcher = dpct.getDispatcher();
		Delegator delegator = dpct.getDelegator();
		String fullName = (String) context.get("fullName");
		Map<String, Object> party = CallcenterServices.demarcatePersonName(fullName);
		String firstName = (String) party.get("firstName");
		String middleName = (String) party.get("middleName");
		String lastName = (String) party.get("lastName");
		String address1 = (String) context.get("address1");
		String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
		String contactMechId = (String) context.get("contactMechId");
		String districtGeoId = (String) context.get("districtGeoId");
		String countryGeoId = (String) context.get("countryGeoId");
		String contactNumber = (String) context.get("contactNumber");
		String email = (String) context.get("email");
		String partyId = ((GenericValue) context.get("userLogin")).getString("partyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try{
			if(UtilValidate.isNotEmpty(firstName) && (UtilValidate.isNotEmpty(lastName))){
				 dispatcher.runSync("updatePersonOlbius", UtilMisc.toMap("userLogin",userLogin,"firstName", firstName,"middleName",middleName,"lastName",lastName));
			}
			List<GenericValue> fk = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
									EntityUtil.getFilterByDateExpr(),
									EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"),
									EntityCondition.makeCondition("address1", address1),
									EntityCondition.makeCondition("stateProvinceGeoId", stateProvinceGeoId),
									EntityCondition.makeCondition("districtGeoId", districtGeoId))), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(stateProvinceGeoId) && UtilValidate.isEmpty(fk)){
				GenericValue province = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId),  false);
				contactMechId = MobileUtils.createPartyPostalAddress(delegator, dispatcher, partyId, contactMechId, countryGeoId, stateProvinceGeoId, province.getString("geoName"), districtGeoId, null, address1, "EMPLOYEE", false);
				MobileUtils.createPartyContactMechPurpose(delegator, dispatcher, partyId, contactMechId, "CURRENT_RESIDENCE", true);
			}
			List<GenericValue> em = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
									EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_EMAIL"),
									EntityUtil.getFilterByDateExpr(),
									EntityCondition.makeCondition("infoString", email))), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(email) && UtilValidate.isEmail(email) && UtilValidate.isEmpty(em)){
				//	create email address createPartyEmailAddress
				ProfileUtils.createPartyEmail(delegator, dispatcher, partyId, email);
			}
			List<GenericValue> ph = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
							EntityCondition.makeCondition("contactMechPurposeTypeId", "PHONE_BILLING"),
							EntityCondition.makeCondition("contactNumber", contactNumber))), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(contactNumber) && UtilValidate.isEmpty(ph)){
				// 	createPartyTelecomNumber
				ProfileUtils.createPartyTelephone(delegator, dispatcher, partyId, contactNumber);
			}
		}catch(GenericServiceException e){
			Debug.logError("Can't update Information SalesMan"+e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}catch(Exception e){
			Debug.logError("Can't update Information SalesMan"+e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	/*
	 * update Password for SalesMan
	 *
	 * */
	public static Map<String,Object> updatePassword(DispatchContext dpct,Map<String,?extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = dpct.getDispatcher();
		String currentPassword = (String) context.get("currentPassword");
		String newPassword = (String) context.get("newPassword");
		String newPasswordVerify = (String) context.get("passwordVerify");
		try {
			Map<String,Object> result = FastMap.newInstance();
			result = dispatcher.runSync("updatePassword", UtilMisc.toMap("userLogin", userLogin,"userLoginId", userLogin.getString("userLoginId"),
														"currentPassword",currentPassword,"newPassword",newPassword,"newPasswordVerify",newPasswordVerify));
			if(ServiceUtil.isError(result)){
				return ServiceUtil.returnError("errorChangePassword");
			}
		}catch(GenericServiceException e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		} catch (Exception e) {
			Debug.logError("Can't update password SalesMan"+e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}

	@SuppressWarnings("unchecked")
	public static String getCalendarEventByMonth(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String startParam = request.getParameter("start");
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		TimeZone timeZone = TimeZone.getDefault();
		Locale locale = Locale.getDefault();
		Map<String, Object> context = FastMap.newInstance();
		Timestamp start = null;
		if (UtilValidate.isNotEmpty(startParam)) {
			start = new Timestamp(Long.parseLong(startParam));
		}
		if (start == null) {
			start = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp(),
					timeZone, locale);
		} else {
			start = UtilDateTime.getMonthStart(start, timeZone, locale);
		}
		Calendar tempCal = UtilDateTime.toCalendar(new Date(start.getTime()),
				timeZone, locale);
		int numDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		Timestamp prev = UtilDateTime
				.getMonthStart(start, -1, timeZone, locale);
		context.put("prevMillis", new Long(prev.getTime()).toString());
		Timestamp next = UtilDateTime.getDayStart(start, numDays + 1, timeZone,
				locale);
		context.put("nextMillis", new Long(next.getTime()).toString());
		Timestamp end = UtilDateTime.getMonthEnd(start, timeZone, locale);
		Timestamp getFrom = null;
		int prevMonthDays = tempCal.get(Calendar.DAY_OF_WEEK)
				- tempCal.getFirstDayOfWeek();
		if (prevMonthDays < 0)
			prevMonthDays += 7;
		tempCal.add(Calendar.DATE, -prevMonthDays);
		numDays += prevMonthDays;
		getFrom = new Timestamp(tempCal.getTimeInMillis());
		int firstWeekNum = tempCal.get(Calendar.WEEK_OF_YEAR);
		context.put("firstWeekNum", firstWeekNum);
		// also get days until the end of the week at the end of the month
		Calendar lastWeekCal = UtilDateTime.toCalendar(end, timeZone, locale);
		int monthEndDay = lastWeekCal.get(Calendar.DAY_OF_WEEK);
		Timestamp getTo = UtilDateTime.getWeekEnd(end, timeZone, locale);
		lastWeekCal = UtilDateTime.toCalendar(getTo, timeZone, locale);
		int followingMonthDays = lastWeekCal.get(Calendar.DAY_OF_WEEK)
				- monthEndDay;
		if (followingMonthDays < 0) {
			followingMonthDays += 7;
		}

		numDays += followingMonthDays;
		Map<String, Object> serviceCtx;
		try {
			serviceCtx = dispatcher.getDispatchContext().makeValidContext(
					"getWorkEffortEventsByPeriod", "IN",
					UtilHttp.getCombinedMap(request));
			serviceCtx.putAll(UtilMisc.toMap("userLogin", userLogin, "start",
					getFrom, "calendarType", "VOID", "numPeriods", numDays,
					"periodType", Calendar.DATE, "locale", locale, "timeZone",
					timeZone));
			if (context.get("entityExprList") != null) {
				serviceCtx.put("entityExprList", context.get("entityExprList"));
			}
			Map<String, Object> result = dispatcher.runSync(
					"getWorkEffortEventsByPeriod", serviceCtx);

			// context.put("periods", result.get("periods"));
			List<Map<String, Object>> periods = (List<Map<String, Object>>) result
					.get("periods");
			Map<String, Object> entry = FastMap.newInstance();
			for (Map<String, Object> period : periods) {
				List<Map<String, Object>> calendarEntries = (List<Map<String, Object>>) period
						.get("calendarEntries");
				for (Map<String, Object> calEntry : calendarEntries) {
					Timestamp startedDate = null;
					Timestamp completionDate = null;
					if (calEntry.get("workEffort") != null) {
						GenericValue workEffort = (GenericValue) calEntry
								.get("workEffort");
						if (workEffort.getString("currentStatusId")
								.equalsIgnoreCase("CAL_TENTATIVE")) {
							if (workEffort.getTimestamp("actualStartDate") != null) {
								startedDate = workEffort
										.getTimestamp("actualStartDate");
							} else {
								startedDate = workEffort
										.getTimestamp("estimatedStartDate");
							}
							if (workEffort.getTimestamp("actualCompletionDate") != null) {
								completionDate = workEffort
										.getTimestamp("actualCompletionDate");
							} else {
								completionDate = workEffort
										.getTimestamp("estimatedCompletionDate");
							}
							if (completionDate == null
									&& workEffort
											.getDouble("actualMilliSeconds") != null) {
								completionDate = new Timestamp(
										(long) (workEffort.getTimestamp(
												"actualStartDate").getTime() + workEffort
												.getDouble("actualMilliSeconds")));
							}
							if (completionDate == null
									&& workEffort
											.getDouble("estimatedMilliSeconds") != null) {
								completionDate = new Timestamp(
										(long) (workEffort.getTimestamp(
												"estimatedStartDate").getTime() + workEffort
												.getDouble("estimatedMilliSeconds")));
							}
							GenericValue workEffortEntity = delegator.findOne(
									"WorkEffort",
									UtilMisc.toMap("workEffortId", workEffort
											.getString("workEffortId")), false);

							entry.put(
									"workEffortId_"
											+ workEffort
													.getString("workEffortId"),
									UtilMisc.toMap(
											"startedDate",
											startedDate,
											"completionDate",
											completionDate,
											"workEffortName",
											workEffortEntity
													.getString("workEffortName"),
											"workEffortId", workEffort
													.getString("workEffortId")));
						}
					}
				}
			}
			context.put("periods", entry);
			context.put("maxConcurrentEntries",
					result.get("maxConcurrentEntries"));
			context.put("start", start);
			context.put("end", end);
			context.put("prev", prev);
			context.put("next", next);

		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("partyId", userLogin.get("partyId"));
		request.setAttribute("calendarEventsMonth", context);
		return "success";
	}
	/**
	 * thong tin salesman
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public static Map<String, Object> getEmployeeInfo(DispatchContext dctx,
			Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		DateTimeConverters.TimestampToString converts = new DateTimeConverters.TimestampToString();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Map<String, Object> cusMap = FastMap.newInstance();
		try {
			GenericValue party = userLogin.getRelatedOne("Party", false);
			String partyId = party.getString("partyId");
			Timestamp startDate = PersonHelper.getDateEmplJoinOrg(delegator, partyId);
			if(UtilValidate.isNotEmpty(startDate)){
				cusMap.put("startDate", converts.convert(startDate));
			}
			GenericValue contactMech = EntityUtil.getFirst(ContactHelper
					.getContactMech(party, "PRIMARY_LOCATION",
							"POSTAL_ADDRESS", false));
			GenericValue phone = EntityUtil.getFirst(ContactHelper
					.getContactMech(party, "PRIMARY_PHONE",
							"TELECOM_NUMBER", false));
			GenericValue email = EntityUtil.getFirst(ContactHelper
					.getContactMech(party, "PRIMARY_EMAIL",
							"EMAIL_ADDRESS", false));
			String partyContent = MobileUtils
					.getAvatar(delegator, partyId);
			if (partyContent != null) {
				cusMap.put("avatar", partyContent);
			}
			cusMap.put("createdTxStamp", converts.convert(
					userLogin.getTimestamp("createdTxStamp"),
					Locale.getDefault(), TimeZone.getDefault(),
					UtilDateTime.DATE_FORMAT));
			cusMap.put("lastUpdatedTxStamp", converts.convert(
					userLogin.getTimestamp("lastUpdatedTxStamp"),
					Locale.getDefault(), TimeZone.getDefault(),
					UtilDateTime.DATE_FORMAT));
			if ("PERSON".equals(party.get("partyTypeId"))) {
				GenericValue person = delegator.findOne("Person",
						UtilMisc.toMap("partyId", party.get("partyId")), false);
				if (person != null) {
					cusMap.put("firstName", person.get("firstName"));
					cusMap.put("middleName", person.get("middleName"));
					cusMap.put("lastName", person.get("lastName"));
				}
			}
			if (contactMech != null) {
				GenericValue postalAddress = contactMech.getRelatedOne(
						"PostalAddress", false);
				cusMap.put("address1", postalAddress.get("address1"));
				String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
				String districtGeoId = postalAddress.getString("districtGeoId");
				if(UtilValidate.isNotEmpty(stateProvinceGeoId)){
					cusMap.put("stateProvinceGeoId", stateProvinceGeoId);
					GenericValue city = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId),  false);
					cusMap.put("city", city.getString("geoName"));
				}
				if(UtilValidate.isNotEmpty(districtGeoId)){
					cusMap.put("districtGeoId", districtGeoId);
					GenericValue district = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId),  false);
					cusMap.put("district", district.getString("geoName"));
				}
			}
			if (phone != null) {
				GenericValue tele = phone.getRelatedOne(
						"TelecomNumber", false);
				cusMap.put("contactNumber", tele.get("contactNumber"));
			}
			if (email != null) {
				cusMap.put("email", email.get("infoString"));
			}
			List<GenericValue> partyContentList = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", partyId, "partyContentTypeId", "LGOIMGURL"), UtilMisc.toList("-fromDate"), false);
			if(UtilValidate.isNotEmpty(partyContentList)){
				GenericValue ptcontent = partyContentList.get(0);
				String contentId = ptcontent.getString("contentId");
				GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
				String dataResourceId = content.getString("dataResourceId");
				GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
				String objectInfo = dataResource.getString("objectInfo");
				cusMap.put("avatar", objectInfo);
			}
			res.put("employee", cusMap);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}

	public static Map<String, Object> getCurrentKpi(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String partyId = userLogin.getString("partyId");
			context.put("partyId", partyId);
			Map<String, Object> out = dispatcher.runSync("getListAllKPIInCurrenDateByPartyId", context);
			List<GenericValue> results = (List<GenericValue>) out.get("listIterator");
			res.put("results", results);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
}
