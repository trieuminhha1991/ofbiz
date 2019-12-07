package com.olbius.baselogistics.report;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.common.util.EntityMiscUtil;

public class ReportEvens {
	public static final String module = ReportEvens.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListExporteWarehouseReportOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		ExportWarehouseOlapImp ewo = new ExportWarehouseOlapImp();
		String dateType= (String) context.get("dateType");
		List<Object> productId= (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		List<Object> enumId = (List<Object>) context.get("enumId[]"); 
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		ExportWarehouseOlapImp.ResultOutReport resultOutPOReport = ewo.new ResultOutReport();
		ewo.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gird = new OlapGrid(ewo, resultOutPOReport);
		ewo.setOlapResult(gird);
		
        if(dateType == null) {
        	dateType = "DAY";
        }
        String timePeriod = (String) context.get("timePeriod");
        Date fromDate = null;
		Date thruDate = null;
		if ("OPTIONS".equals(timePeriod)) {
			fromDate = (Date) context.get("fromDate");
	        thruDate = (Date) context.get("thruDate");
		} else {
			Calendar fromCalendar = Calendar.getInstance();
			fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
			fromCalendar.set(Calendar.MINUTE, 0);
			fromCalendar.set(Calendar.SECOND, 0);
			fromCalendar.set(Calendar.MILLISECOND, 0);
			Calendar thruCalendar = Calendar.getInstance();
			thruCalendar.set(Calendar.HOUR_OF_DAY, 23);
			thruCalendar.set(Calendar.MINUTE, 59);
			thruCalendar.set(Calendar.SECOND, 59);
			thruCalendar.set(Calendar.MILLISECOND, 999);
			switch (timePeriod) {
			case "THISWEEK":
				fromCalendar.set(Calendar.DAY_OF_WEEK, fromCalendar.getActualMinimum(Calendar.DAY_OF_WEEK));
				thruCalendar.set(Calendar.DAY_OF_WEEK, thruCalendar.getActualMaximum(Calendar.DAY_OF_WEEK));
				break;
			case "THISMONTH":
				fromCalendar.set(Calendar.DAY_OF_MONTH, fromCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
				thruCalendar.set(Calendar.DAY_OF_MONTH, thruCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				break;
			default:
				break;
			}
			fromDate = new Date(fromCalendar.getTimeInMillis());
			thruDate = new Date(thruCalendar.getTimeInMillis());
		}
        
        ewo.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        ewo.setFromDate(fromDate);
        ewo.setThruDate(thruDate);
        
        ewo.putParameter(ExportWarehouseOlapImp.DATE_TYPE, dateType);
        ewo.putParameter(ExportWarehouseOlapImp.PRODUCT_ID, productId);
        ewo.putParameter(ExportWarehouseOlapImp.FACILITY_ID, facilityId);
        ewo.putParameter(ExportWarehouseOlapImp.ENUM_ID, enumId);
        ewo.putParameter(ExportWarehouseOlapImp.CATEGORY_ID, categoryId);
        ewo.putParameter(ExportWarehouseOlapImp.USER_LOGIN_ID, partyIdByFacility);
		Map<String, Object> result = ewo.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListReceiveWarehouseReportOlap(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
        String timePeriod = (String) context.get("timePeriod");
        Date fromDate = null;
		Date thruDate = null;
		if ("OPTIONS".equals(timePeriod)) {
			fromDate = (Date) context.get("fromDate");
	        thruDate = (Date) context.get("thruDate");
		} else {
			Calendar fromCalendar = Calendar.getInstance();
			fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
			fromCalendar.set(Calendar.MINUTE, 0);
			fromCalendar.set(Calendar.SECOND, 0);
			fromCalendar.set(Calendar.MILLISECOND, 0);
			Calendar thruCalendar = Calendar.getInstance();
			thruCalendar.set(Calendar.HOUR_OF_DAY, 23);
			thruCalendar.set(Calendar.MINUTE, 59);
			thruCalendar.set(Calendar.SECOND, 59);
			thruCalendar.set(Calendar.MILLISECOND, 999);
			switch (timePeriod) {
			case "THISWEEK":
				fromCalendar.set(Calendar.DAY_OF_WEEK, fromCalendar.getActualMinimum(Calendar.DAY_OF_WEEK));
				thruCalendar.set(Calendar.DAY_OF_WEEK, thruCalendar.getActualMaximum(Calendar.DAY_OF_WEEK));
				break;
			case "THISMONTH":
				fromCalendar.set(Calendar.DAY_OF_MONTH, fromCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
				thruCalendar.set(Calendar.DAY_OF_MONTH, thruCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				break;
			default:
				break;
			}
			fromDate = new Date(fromCalendar.getTimeInMillis());
			thruDate = new Date(thruCalendar.getTimeInMillis());
		}
		if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
			ReceiveWarehouseOlapImp rwo = new ReceiveWarehouseOlapImp(delegator);
			rwo.setFromDate(fromDate);
	        rwo.setThruDate(thruDate);
	        
	        rwo.putParameter(ReceiveWarehouseOlapImp.PRODUCT_ID, (List<Object>) context.get("productId[]"));
	        rwo.putParameter(ReceiveWarehouseOlapImp.FACILITY_ID, (List<Object>) context.get("facilityId[]"));
	        rwo.putParameter(ReceiveWarehouseOlapImp.CATEGORY_ID, (List<Object>) context.get("categoryId[]"));
	        rwo.putParameter(ReceiveWarehouseOlapImp.USER_LOGIN_ID, partyIdByFacility);
	        
	        rwo.setOlapResultType(OlapGrid.class);
	        result = rwo.execute(context);
	        
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> exportReceiveWarehouseOlapLogToPdf(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException, GenericServiceException {
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String categoryId = request.getParameter("categoryId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> categoryIdInput = null;
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		if(categoryId.equals("") || categoryId.equals("null")){
			categoryIdInput = null;
		}
		if(!categoryId.equals("") && !categoryId.equals("null")){
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if(categoryIdData.length != 0){
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		if(dateType == null) {
        	dateType = "DAY";
        }
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("facilityId[]", facilityIdInput);
		context.put("categoryId[]", categoryIdInput);
		context.put("userLogin", userLogin);
		Map<String, Object> resultService =  dispatcher.runSync("jqGetListReceiveWarehouseReportOlap", context);
		List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
		return listData;
	}
	
	@SuppressWarnings({ "unchecked"})
	public static List<Map<String, Object>> exportExportWarehouseOlapLogToPdf(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException, GenericServiceException {
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String enumId = request.getParameter("enumId");
		String categoryId = request.getParameter("categoryId");
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> enumIdInput = null;
		List<String> categoryIdInput = null;
		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		if(enumId.equals("") || enumId.equals("null")){
			enumIdInput = null;
		}
		if(!enumId.equals("") && !enumId.equals("null")){
			String[] enumIdData = enumId.split(",");
			enumIdInput = new ArrayList<>();
			if(enumIdData.length != 0){
				for (String i : enumIdData) {
					enumIdInput.add(i);
				}
			}
		}
		
		if(categoryId.equals("") || categoryId.equals("null")){
			categoryIdInput = null;
		}
		if(!categoryId.equals("") && !categoryId.equals("null")){
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if(categoryIdData.length != 0){
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		if(dateType == null) {
        	dateType = "DAY";
        }
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("facilityId[]", facilityIdInput);
		context.put("enumId[]", enumIdInput);
		context.put("categoryId[]", categoryIdInput);
		context.put("userLogin", userLogin);
		Map<String, Object> resultService =  dispatcher.runSync("jqGetListExporteWarehouseReportOlap", context);
		List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
		return listData;
	}
	@SuppressWarnings("unchecked") 
	public static Map<String, Object> jqGetListInventoryReportOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		InventoryReportImp iiReport = new InventoryReportImp();
		InventoryReportImp.ResultOutReport resultOutPOReport = iiReport.new ResultOutReport();
		iiReport.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gird = new OlapGrid(iiReport, resultOutPOReport);
		iiReport.setOlapResult(gird);
		
		String dateType= (String) context.get("dateType");
		List<Object> productId= (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String facilityDistr = (String) context.get("checkNPP"); 
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
        if(dateType == null) {
        	dateType = "DAY";
        }
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        iiReport.setFromDate(fromDate);
        iiReport.setThruDate(thruDate);
        
        iiReport.putParameter(InventoryReportImp.DATE_TYPE, dateType);
        iiReport.putParameter(InventoryReportImp.PRODUCT_ID, productId);
        iiReport.putParameter(InventoryReportImp.FACILITY_ID, facilityId);
        iiReport.putParameter(InventoryReportImp.CATEGORY_ID, categoryId);
        iiReport.putParameter(InventoryReportImp.USER_LOGIN_ID, partyIdByFacility);
        iiReport.putParameter(InventoryReportImp.CHECK_NPP, facilityDistr);
		Map<String, Object> result = iiReport.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> exportInventoryOlapLogToPdf(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException, GenericServiceException {
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String categoryId = request.getParameter("categoryId");
		String checkNPP = request.getParameter("checkNPP"); 
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> categoryIdInput = null;
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		if(categoryId.equals("") || categoryId.equals("null")){
			categoryIdInput = null;
		}
		if(!categoryId.equals("") && !categoryId.equals("null")){
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if(categoryIdData.length != 0){
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		if(dateType == null) {
        	dateType = "DAY";
        }
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("facilityId[]", facilityIdInput);
		context.put("categoryId[]", categoryIdInput);
		context.put("checkNPP", checkNPP);
		context.put("userLogin", userLogin);
		Map<String, Object> resultService =  dispatcher.runSync("jqGetListInventoryReportOlap", context);
		List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
		return listData;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListReturnProductReportOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		String dateType= (String) context.get("dateType");
		List<Object> productId= (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		List<Object> enumId = (List<Object>) context.get("enumId[]");
		List<Object> returnReasonId = (List<Object>) context.get("returnReasonId[]");
		Locale locale = (Locale) context.get("locale");
		String checkNPP= (String) context.get("checkNPP");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		ReturnProductOlapImpl rpo = new ReturnProductOlapImpl();
		ReturnProductOlapImpl.ResultOutReport resultOutPOReport = rpo.new ResultOutReport();
		rpo.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gird = new OlapGrid(rpo, resultOutPOReport);
		rpo.setOlapResult(gird);
		
        if(dateType == null) {
        	dateType = "DAY";
        }
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        rpo.setFromDate(fromDate);
        rpo.setThruDate(thruDate);
        
        rpo.putParameter(ReturnProductOlapImpl.DATE_TYPE, dateType);
        rpo.putParameter(ReturnProductOlapImpl.PRODUCT_ID, productId);
        rpo.putParameter(ReturnProductOlapImpl.FACILITY_ID, facilityId);
        rpo.putParameter(ReturnProductOlapImpl.CATEGORY_ID, categoryId);
        rpo.putParameter(ReturnProductOlapImpl.ENUM_ID, enumId);
        rpo.putParameter(ReturnProductOlapImpl.RETURN_REASON_ID, returnReasonId);
        rpo.putParameter(ReturnProductOlapImpl.USER_ID, partyIdByFacility);
        rpo.putParameter(ReturnProductOlapImpl.LOCALE, locale);
        rpo.putParameter(ReturnProductOlapImpl.CHECK_NPP, checkNPP);
		Map<String, Object> result = rpo.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> exportReturnProductOlapLogToPdf(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException, GenericServiceException {
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String enumId = request.getParameter("enumId");
		String categoryId = request.getParameter("categoryId");
		String returnReasonId = request.getParameter("returnReasonId");
		String checkNPP = request.getParameter("checkNPP");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> enumIdInput = null;
		List<String> categoryIdInput = null;
		List<String> returnReasonIdInput = null;
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}

		if(enumId.equals("") || enumId.equals("null")){
			enumIdInput = null;
		}
		if(!enumId.equals("") && !enumId.equals("null")){
			String[] enumIdData = enumId.split(",");
			enumIdInput = new ArrayList<>();
			if(enumIdData.length != 0){
				for (String i : enumIdData) {
					enumIdInput.add(i);
				}
			}
		}
		
		if(categoryId.equals("") || categoryId.equals("null")){
			categoryIdInput = null;
		}
		if(!categoryId.equals("") && !categoryId.equals("null")){
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if(categoryIdData.length != 0){
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}
		
		if(returnReasonId.equals("") || returnReasonId.equals("null")){
			returnReasonIdInput = null;
		}
		if(!returnReasonId.equals("") && !returnReasonId.equals("null")){
			String[] returnReasonIdData = returnReasonId.split(",");
			returnReasonIdInput = new ArrayList<>();
			if(returnReasonIdData.length != 0){
				for (String i : returnReasonIdData) {
					returnReasonIdInput.add(i);
				}
			}
		}
		
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		if(dateType == null) {
        	dateType = "DAY";
        }
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("facilityId[]", facilityIdInput);
		context.put("categoryId[]", categoryIdInput);
		context.put("enumId[]", enumIdInput);
		context.put("returnReasonId[]", returnReasonIdInput);
		context.put("checkNPP", checkNPP);
		context.put("userLogin", userLogin);
		Map<String, Object> resultService =  dispatcher.runSync("jqGetListReturnProductReportOlap", context);
		List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
		return listData;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListTransferItemReportOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		String dateType= (String) context.get("dateType");
		List<Object> productId= (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		TransferItemOlapImp rwo = new TransferItemOlapImp();
		TransferItemOlapImp.ResultOutReport resultOutPOReport = rwo.new ResultOutReport();
		rwo.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gird = new OlapGrid(rwo, resultOutPOReport);
		rwo.setOlapResult(gird);
		
        if(dateType == null) {
        	dateType = "DAY";
        }
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        rwo.setFromDate(fromDate);
        rwo.setThruDate(thruDate);
        
        rwo.putParameter(TransferItemOlapImp.DATE_TYPE, dateType);
        rwo.putParameter(TransferItemOlapImp.PRODUCT_ID, productId);
        rwo.putParameter(TransferItemOlapImp.FACILITY_ID, facilityId);
        rwo.putParameter(TransferItemOlapImp.USER_LOGIN_ID, partyIdByFacility);
		Map<String, Object> result = rwo.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> exportTransferItemOlapToPdf(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException, GenericServiceException {
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		if(dateType == null) {
        	dateType = "DAY";
        }
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("facilityId[]", facilityIdInput);
		context.put("userLogin", userLogin);
		Map<String, Object> resultService =  dispatcher.runSync("jqGetListTransferItemReportOlap", context);
		List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
		return listData;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetReportExportWarehouseByOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	List<GenericValue> listOrderItem = FastList.newInstance();
    	List<GenericValue> listGenIterator = FastList.newInstance();
       	listSortFields.add("orderId");
       	/*Timestamp currentDateTime = UtilDateTime.nowTimestamp();*/
       	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
//    	listAllConditions.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.NOT_EQUAL, null));
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CREATED"));
    	/*listAllConditions.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN, currentDateTime));*/ 
    	listAllConditions.add(EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, null));
    	
    	List<EntityCondition> listAllConditionsResult = new ArrayList<EntityCondition>();
    	for (EntityCondition condition : listAllConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				if (UtilValidate.isEmpty(conditionSplit)) listAllConditionsResult.add(condition);
				
				String fieldName = conditionSplit.length > 0 ? (String) conditionSplit[0] : null;
				String operator = conditionSplit.length > 1 ? (String) conditionSplit[1] : null;
				String value = conditionSplit.length > 2 ? (String) conditionSplit[2].trim() : null;
				String valueFrom = null;
				String valueTo = null;
				if (conditionSplit.length > 4) {
					if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
						if ("AND".equals(conditionSplit[4].trim())) {
							operator = "RANGE";
							valueFrom = (String) conditionSplit[2].trim();
							valueTo = (String) conditionSplit[7].trim();
							valueFrom = EntityMiscUtil.cleanValue(valueFrom);
							valueTo = EntityMiscUtil.cleanValue(valueTo);
						}
					}
				}
				fieldName = EntityMiscUtil.cleanFieldName(fieldName);
				value = EntityMiscUtil.cleanValue(value);
				
				if ("estimatedDeliveryDate".equals(fieldName)) {
					if ("RANGE".equals(operator) && valueFrom != null && valueTo != null) {
						Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
						Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
						EntityCondition condEstimatedDeliveryDate = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.BETWEEN, UtilMisc.toList(valueFromTs, valueToTs));
						EntityCondition condShipAfterDate = EntityCondition.makeCondition("shipAfterDate", EntityOperator.GREATER_THAN_EQUAL_TO, valueFromTs);
						EntityCondition condShipBeforeDate = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, valueToTs);
						EntityCondition condAndRange = EntityCondition.makeCondition(condShipAfterDate, EntityOperator.AND, condShipBeforeDate);
						EntityCondition condOr = EntityCondition.makeCondition(condEstimatedDeliveryDate, EntityOperator.OR, condAndRange);
						listAllConditionsResult.add(condOr);
					}
				} 
			}
		}
    	// Fix me: Should be replace by Sales executive Role 
    	try {
    		listIterator = delegator.find("FacilityExportExpectation", EntityCondition.makeCondition(listAllConditionsResult,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		/*List<GenericValue> listOrder = delegator.findList("OrderSumTotalRowNotCount", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, null, fa);*/
    		listOrderItem = listIterator.getCompleteList();
    		for (GenericValue order : listOrderItem) {
				String ownerPartyId = order.getString("ownerPartyId");
				boolean checkIsDistributor = SalesPartyUtil.isDistributor(delegator, ownerPartyId);
				if(checkIsDistributor == false){
					listGenIterator.add(order);
				}
			}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetReportExportWarehouseByOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listGenIterator);
    	return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetReportReceiveWarehouseByOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listSortFields.add("orderId");
       	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    	listAllConditions.add(EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, null));
    	List<GenericValue> listOrderItem = FastList.newInstance();
    	List<GenericValue> listGenIterator = FastList.newInstance();
    	
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
    	String sqlCondition = ""; 
		sqlCondition = "(PARO.ORE_PARTY_ID ="+"'"+partyId+"'"+" AND PARO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER')";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		
		List<EntityCondition> listAllConditionsResult = new ArrayList<EntityCondition>();
    	for (EntityCondition condition : listAllConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				if (UtilValidate.isEmpty(conditionSplit)) listAllConditionsResult.add(condition);
				
				String fieldName = conditionSplit.length > 0 ? (String) conditionSplit[0] : null;
				String operator = conditionSplit.length > 1 ? (String) conditionSplit[1] : null;
				String value = conditionSplit.length > 2 ? (String) conditionSplit[2].trim() : null;
				String valueFrom = null;
				String valueTo = null;
				if (conditionSplit.length > 4) {
					if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
						if ("AND".equals(conditionSplit[4].trim())) {
							operator = "RANGE";
							valueFrom = (String) conditionSplit[2].trim();
							valueTo = (String) conditionSplit[7].trim();
							valueFrom = EntityMiscUtil.cleanValue(valueFrom);
							valueTo = EntityMiscUtil.cleanValue(valueTo);
						}
					}
				}
				fieldName = EntityMiscUtil.cleanFieldName(fieldName);
				value = EntityMiscUtil.cleanValue(value);
				
				if ("shipAfterDate".equals(fieldName)) {
					if ("RANGE".equals(operator) && valueFrom != null && valueTo != null) {
						Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
						Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
						EntityCondition condEstimatedDeliveryDate = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.BETWEEN, UtilMisc.toList(valueFromTs, valueToTs));
						EntityCondition condShipAfterDate = EntityCondition.makeCondition("shipAfterDate", EntityOperator.GREATER_THAN_EQUAL_TO, valueFromTs);
						EntityCondition condShipBeforeDate = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, valueToTs);
						EntityCondition condAndRange = EntityCondition.makeCondition(condShipAfterDate, EntityOperator.AND, condShipBeforeDate);
						EntityCondition condOr = EntityCondition.makeCondition(condEstimatedDeliveryDate, EntityOperator.OR, condAndRange);
						listAllConditionsResult.add(condOr);
					}
				} 
			}
		}
    	
    	try {
    		listIterator = delegator.find("ProductOrderItemAndOrderHeader", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		listOrderItem = listIterator.getCompleteList();
    		for (GenericValue order : listOrderItem) {
				String ownerPartyId = order.getString("ownerPartyId");
				boolean checkIsDistributor = SalesPartyUtil.isDistributor(delegator, ownerPartyId);
				if(checkIsDistributor == false){
					listGenIterator.add(order);
				}
			}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetReportExportWarehouseByOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listGenIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked") 
	public static Map<String, Object> jqGetListInventoryReportDistributorOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		InventoryReportImp iiReport = new InventoryReportImp();
		InventoryReportImp.ResultOutReport resultOutPOReport = iiReport.new ResultOutReport();
		iiReport.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gird = new OlapGrid(iiReport, resultOutPOReport);
		iiReport.setOlapResult(gird);
		
		String dateType= (String) context.get("dateType");
		List<Object> productId= (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		String facilityDistr = (String) context.get("checkNPP"); 
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
        if(dateType == null) {
        	dateType = "DAY";
        }
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        iiReport.setFromDate(fromDate);
        iiReport.setThruDate(thruDate);
        
        iiReport.putParameter(InventoryReportImp.DATE_TYPE, dateType);
        iiReport.putParameter(InventoryReportImp.PRODUCT_ID, productId);
        iiReport.putParameter(InventoryReportImp.FACILITY_ID, facilityId);
        iiReport.putParameter(InventoryReportImp.CATEGORY_ID, categoryId);
        iiReport.putParameter(InventoryReportImp.USER_LOGIN_ID, partyIdByFacility);
        iiReport.putParameter(InventoryReportImp.CHECK_NPP, facilityDistr);
		Map<String, Object> result = iiReport.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetReportExportExpectedUnderOrderPortlet(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition cond = EntityCondition.makeCondition();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	List<GenericValue> listOrderItem = FastList.newInstance();
    	List<GenericValue> listGenIterator = FastList.newInstance();
       	listAllConditions.add(tmpConditon);
    	listAllConditions.add(cond);
    	listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    	listAllConditions.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.NOT_EQUAL, null));
    	listAllConditions.add(EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, null));
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("estimatedDeliveryDate");
    		}
    		listIterator = delegator.find("TotalOrderQuantitySum", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		listOrderItem = listIterator.getCompleteList();
    		for (GenericValue order : listOrderItem) {
				String ownerPartyId = order.getString("ownerPartyId");
				String orderId = order.getString("orderId");
				boolean checkIsDistributor = SalesPartyUtil.isDistributor(delegator, ownerPartyId);
				List<GenericValue> listDelivery = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				if(checkIsDistributor == false){
					if(UtilValidate.isEmpty(listDelivery)){
						listGenIterator.add(order);
					}
				}
			}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetReportExportExpectedUnderOrderPortlet service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listGenIterator);
    	return successResult;
	}
	
	public static Map<String, Object> getTotalSaleOrderNeedExport(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();

		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    	String sqlCondition = "";
    	sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_FROM_VENDOR'))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRow", cond, null, null, null, true);
    	int orderCount = 0;
		if(UtilValidate.isNotEmpty(listOrder)){
			for (GenericValue totalOrderSum : listOrder) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		return result;
	}
	
	public static Date getEndOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}

	public static Date getStartOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	public static Map<String, Object> getTotalOrderNeedExportToday(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Date currentEndDate = getEndOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
		Timestamp currentEndTime = new Timestamp(currentEndDate.getTime());
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    	String sqlCondition = "";
    	sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_FROM_VENDOR') AND (OITG.OI2_ESTIMATED_DELIVERY_DATE BETWEEN"+"'"+currentFirstTime+"'"+"AND"+"'"+currentEndTime+"'"+"))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRow", cond, null, null, null, true);
    	int orderCount = 0;
		if(UtilValidate.isNotEmpty(listOrder)){
			for (GenericValue totalOrderSum : listOrder) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		return result;
	}
	
	public static Map<String, Object> getTotalOrderNotExportTime(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    	String sqlCondition = "";
    	sqlCondition = "((ORD.STATUS_ID = 'ORDER_APROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_FROM_VENDOR') AND (OITG.OI2_ESTIMATED_DELIVERY_DATE"+"<"+"'"+currentFirstTime+"'"+"))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRow", cond, null, null, null, true);
    	int orderCount = 0;
		if(UtilValidate.isNotEmpty(listOrder)){
			for (GenericValue totalOrderSum : listOrder) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		return result;
	} 
	
	public static List<EntityCondition> processOrderCondition(List<EntityCondition> listAllConditions){
    	List<EntityCondition> listAllConditionsResult = new ArrayList<EntityCondition>();
		for (EntityCondition condition : listAllConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				String fieldName = (String) conditionSplit[0];
				String operator = (String) conditionSplit[1];
				String value = (String) conditionSplit[2].trim();
				String valueFrom = null;
				String valueTo = null;
				if (conditionSplit.length > 4) {
					if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
						if ("AND".equals(conditionSplit[4].trim())) {
							operator = "RANGE";
							valueFrom = (String) conditionSplit[2].trim();
							valueTo = (String) conditionSplit[7].trim();
							valueFrom = EntityMiscUtil.cleanValue(valueFrom);
							valueTo = EntityMiscUtil.cleanValue(valueTo);
						}
					}
				}
				fieldName = EntityMiscUtil.cleanFieldName(fieldName);
				value = EntityMiscUtil.cleanValue(value);
				
				if ("shipBeforeDate".equals(fieldName)) {
					if ("RANGE".equals(operator) && valueFrom != null && valueTo != null) {
						Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
						Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
						EntityCondition condEstimatedDeliveryDate = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.BETWEEN, UtilMisc.toList(valueFromTs, valueToTs));
						EntityCondition condShipAfterDate = EntityCondition.makeCondition("shipAfterDate", EntityOperator.GREATER_THAN_EQUAL_TO, valueFromTs);
						EntityCondition condShipBeforeDate = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, valueToTs);
						EntityCondition condAndRange = EntityCondition.makeCondition(condShipAfterDate, EntityOperator.AND, condShipBeforeDate);
						EntityCondition condOr = EntityCondition.makeCondition(condEstimatedDeliveryDate, EntityOperator.OR, condAndRange);
						listAllConditionsResult.add(condOr);
					}
				} else {
					listAllConditionsResult.add(condition);
				}
			}
		}
    	return listAllConditionsResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetReportReceiveExpectedUnderOrderPortlet(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition cond = EntityCondition.makeCondition();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	List<GenericValue> listOrderItem = FastList.newInstance();
    	List<GenericValue> listGenIterator = FastList.newInstance();
    	/*Date currentDay = new Date();*/
       	listAllConditions.add(tmpConditon);
    	listAllConditions.add(cond);
    	listAllConditions = processOrderCondition(listAllConditions);
    	listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    	listAllConditions.add(EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, null));
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("estimatedDeliveryDate");
    			listSortFields.add("shipAfterDate");
    			listSortFields.add("shipBeforeDate");
    		}
    		listIterator = delegator.find("TotalOrderQuantitySum", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		listOrderItem = listIterator.getCompleteList();
    		for (GenericValue order : listOrderItem) {
				String ownerPartyId = order.getString("ownerPartyId");
				boolean checkIsDistributor = SalesPartyUtil.isDistributor(delegator, ownerPartyId);
				if(checkIsDistributor == false){
						listGenIterator.add(order);
				}
			}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetReportExportExpectedUnderOrderPortlet service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listGenIterator);
    	return successResult;
	}
	
	public static Map<String, Object> getTotalPurchaseOrderNeedReceive(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    	String sqlCondition = "";
    	sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER'))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRow", cond, null, null, null, true);
    	int orderCount = 0;
		if(UtilValidate.isNotEmpty(listOrder)){
			for (GenericValue totalOrderSum : listOrder) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		
		return result;
	}
	
	public static Map<String, Object> getTotalOrderNotReceiveTime(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		/*Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    	listAllConditions.add(EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN, currentFirstTime));
    	listAllConditions.add(EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, null));
    	List<GenericValue> listOrder = delegator.findList("TotalOrderSum", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, null, false);
		int orderCount = listOrder.size();
		result.put("orderCount", orderCount);*/ 
		
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    	String sqlCondition = "";
    	sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER') AND (OITG.OI2_SHIP_BEFORE_DATE"+"<"+"'"+currentFirstTime+"'"+"))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRow", cond, null, null, null, true);
    	int orderCount = 0;
		if(UtilValidate.isNotEmpty(listOrder)){
			for (GenericValue totalOrderSum : listOrder) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		
		return result;
	}
	
	public static Date getEndOfTomorrow(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.DAY_OF_MONTH,  calendar.get(Calendar.DAY_OF_MONTH) + 1);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}

	public static Date getStartOfTomorrow(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.DAY_OF_MONTH,  calendar.get(Calendar.DAY_OF_MONTH) + 1);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	public static Map<String, Object> getTotalOrderExportTomorrow(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Date currentDate = new Date();
		Date currentFirstTomorrow = getStartOfTomorrow(currentDate);
		Date currentEndTomorrow = getEndOfTomorrow(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstTomorrow.getTime());
		Timestamp currentEndTime = new Timestamp(currentEndTomorrow.getTime());
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    	String sqlCondition = "";
    	sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_FROM_VENDOR') AND (OITG.OI2_ESTIMATED_DELIVERY_DATE BETWEEN"+"'"+currentFirstTime+"'"+"AND"+"'"+currentEndTime+"'"+"))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRow", cond, null, null, null, true);
    	int orderCount = 0;
		if(UtilValidate.isNotEmpty(listOrder)){
			for (GenericValue totalOrderSum : listOrder) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		return result;
	} 
	
	public static Map<String, Object> getTotalOrderNeedReceiveToday(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Date currentEndDate = getEndOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
		Timestamp currentEndTime = new Timestamp(currentEndDate.getTime());
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER")); 
    	String sqlCondition = "";
    	sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER') AND (OITG.OI2_SHIP_AFTER_DATE BETWEEN"+"'"+currentFirstTime+"'"+"AND"+"'"+currentEndTime+"'"+"))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRow", cond, null, null, null, true);
    	int orderCount = 0;
		if(UtilValidate.isNotEmpty(listOrder)){
			for (GenericValue totalOrderSum : listOrder) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		return result;
	}
	
	public static Map<String, Object> getTotalOrderReceiveTomorrow(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Date currentDate = new Date();
		Date currentFirstTomorrow = getStartOfTomorrow(currentDate);
		Date currentEndTomorrow = getEndOfTomorrow(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstTomorrow.getTime());
		Timestamp currentEndTime = new Timestamp(currentEndTomorrow.getTime());
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    	String sqlCondition = "";
    	sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER') AND (OITG.OI2_SHIP_BEFORE_DATE BETWEEN"+"'"+currentFirstTime+"'"+"AND"+"'"+currentEndTime+"'"+"))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRow", cond, null, null, null, true);
    	int orderCount = 0;
		if(UtilValidate.isNotEmpty(listOrder)){
			for (GenericValue totalOrderSum : listOrder) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		return result;
	}
	
	public static Map<String, Object> loadListOrderNeedExport(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		
		Date currentDate = new Date();
		Date currentFirstTomorrow = getStartOfTomorrow(currentDate);
		Date currentEndTomorrow = getEndOfTomorrow(currentDate);
		String valueCheck = (String) context.get("valueCheck");
		Timestamp currentFirstTimeTomorrow = new Timestamp(currentFirstTomorrow.getTime());
		Timestamp currentEndTimeTomorrow = new Timestamp(currentEndTomorrow.getTime());
		
		Date currentFirstToday = getStartOfDay(currentDate);
		Date currentEndToday = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstToday.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndToday.getTime());
		
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
    	String sqlCondition = "";
    	if(valueCheck.equals("TotalOrderNoExport")){
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    		sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_FROM_VENDOR'))";
    	}
    	if(valueCheck.equals("TotalOrderNotExportTime")){ 
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    		sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_FROM_VENDOR') AND (OITG.OI2_ESTIMATED_DELIVERY_DATE"+"<"+"'"+currentFirstTimeToday+"'"+"))";
    	}
    	if(valueCheck.equals("OrderNeedExportTomorrow")){
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    		sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_FROM_VENDOR') AND (OITG.OI2_ESTIMATED_DELIVERY_DATE BETWEEN"+"'"+currentFirstTimeTomorrow+"'"+"AND"+"'"+currentEndTimeTomorrow+"'"+"))";
    	}
    	if(valueCheck.equals("TotalOrderNeedExportToday")){
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    		sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_FROM_VENDOR') AND (OITG.OI2_ESTIMATED_DELIVERY_DATE BETWEEN"+"'"+currentFirstTimeToday+"'"+"AND"+"'"+currentEndTimeToday+"'"+"))";
    	}
    	
    	if(valueCheck.equals("TotalOrderNoReceive")){
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    		sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER'))";
    	}
    	if(valueCheck.equals("TotalOrderNotReceiveTime")){ 
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    		sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER') AND (OITG.OI2_SHIP_BEFORE_DATE"+"<"+"'"+currentFirstTimeToday+"'"+"))";
    	}
    	if(valueCheck.equals("OrderNeedReceiveTomorrow")){
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    		sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER') AND (OITG.OI2_SHIP_BEFORE_DATE BETWEEN"+"'"+currentFirstTimeTomorrow+"'"+"AND"+"'"+currentEndTimeTomorrow+"'"+"))";
    	}
    	if(valueCheck.equals("TotalOrderNeedReceiveToday")){
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    		sqlCondition = "((ORD.STATUS_ID = 'ORDER_APPROVED') AND (PRO.ORE_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER') AND (OITG.OI2_SHIP_AFTER_DATE BETWEEN"+"'"+currentFirstTimeToday+"'"+"AND"+"'"+currentEndTimeToday+"'"+"))";
    	}
    	
		if(UtilValidate.isNotEmpty(sqlCondition)){
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		}
		
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listOrder = delegator.findList("OrderSumTotalRowNotCount", cond, null, null, null, false);
		result.put("listOrder", listOrder);
		return result;
	} 
	
	
	public static Map<String, Object> getTotalRequireTranferToday(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Date currentEndDate = getEndOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
		Timestamp currentEndTime = new Timestamp(currentEndDate.getTime());
		
		/*GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);*/
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
    	String sqlCondition = "";  
    	sqlCondition = "((TRH.STATUS_ID = 'TRANSFER_CREATED' OR TRH.STATUS_ID = 'TRANSFER_APPROVED') AND (TRH.STATUS_ID != 'TRANSFER_CANCELLED') AND (TRH.TRANSFER_DATE BETWEEN"+"'"+currentFirstTime+"'"+"AND"+"'"+currentEndTime+"'"+"))";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listTranferHeaderTotal = delegator.findList("TranferHeaderCountTotal", cond, null, null, null, true);
    	int transferId = 0;
		if(UtilValidate.isNotEmpty(listTranferHeaderTotal)){
			for (GenericValue totalOrderSum : listTranferHeaderTotal) {
				Long orderId = totalOrderSum.getLong("transferId");
				transferId += orderId.intValue();
			}
		}
		result.put("orderCount", transferId);
		return result;
	}
	
	public static Map<String, Object> getTotalTransferRequire(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Date currentDate = new Date();
		Date currentFirstDateToday = getStartOfDay(currentDate);
		/*Date currentEndDateToday = getEndOfDay(currentDate);*/
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstDateToday.getTime());
		/*Timestamp currentEndTimeToday = new Timestamp(currentEndDateToday.getTime());*/
		
		Date currentFirstTomorrow = getStartOfTomorrow(currentDate);
		Date currentEndTomorrow = getEndOfTomorrow(currentDate);
		Timestamp currentFirstTimeTomorrow = new Timestamp(currentFirstTomorrow.getTime());
		Timestamp currentEndTimeTomorrow = new Timestamp(currentEndTomorrow.getTime());
		
		String valueCheck = (String) context.get("checkTime");
		
		/*GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);*/
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
    	String sqlCondition = "";  
    	
		if(valueCheck.equals("TotalNotTransfer")){
			sqlCondition = "((TRH.STATUS_ID = 'TRANSFER_CREATED' OR TRH.STATUS_ID = 'TRANSFER_APPROVED') AND (TRH.STATUS_ID != 'TRANSFER_CANCELLED'))";
		}
		
		if(valueCheck.equals("NotTimelyTransferRequest")){
			sqlCondition = "((TRH.STATUS_ID = 'TRANSFER_CREATED' OR TRH.STATUS_ID = 'TRANSFER_APPROVED') AND (TRH.STATUS_ID != 'TRANSFER_CANCELLED') AND (TRH.TRANSFER_DATE"+"<"+"'"+currentFirstTimeToday+"'"+"))";
		}
		
		if(valueCheck.equals("RequestTransferTomorrow")){
			sqlCondition = "((TRH.STATUS_ID = 'TRANSFER_CREATED' OR TRH.STATUS_ID = 'TRANSFER_APPROVED') AND (TRH.STATUS_ID != 'TRANSFER_CANCELLED') AND (TRH.TRANSFER_DATE BETWEEN"+"'"+currentFirstTimeTomorrow+"'"+"AND"+"'"+currentEndTimeTomorrow+"'"+"))";
		}
		
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listTranferHeaderTotal = delegator.findList("TranferHeaderCountTotal", cond, null, null, null, true);
    	int transferCount = 0;
		if(UtilValidate.isNotEmpty(listTranferHeaderTotal)){
			for (GenericValue totalTransferSum : listTranferHeaderTotal) {
				Long transferId = totalTransferSum.getLong("transferId");
				transferCount += transferId.intValue();
			}
		}
		result.put("transferCount", transferCount);
		return result;
	}
	
	public static Map<String, Object> loadListTranfer(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		
		Date currentDate = new Date();
		Date currentFirstTomorrow = getStartOfTomorrow(currentDate);
		Date currentEndTomorrow = getEndOfTomorrow(currentDate);
		String valueCheck = (String) context.get("valueCheck");
		Timestamp currentFirstTimeTomorrow = new Timestamp(currentFirstTomorrow.getTime());
		Timestamp currentEndTimeTomorrow = new Timestamp(currentEndTomorrow.getTime());
		
		Date currentFirstToday = getStartOfDay(currentDate);
		Date currentEndToday = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstToday.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndToday.getTime());
		
    	/*GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId); */
    	String sqlCondition = "";
    	  
    	if(valueCheck.equals("TotalNotTransfer")){
    		sqlCondition = "(TRANSFER_HEADER.STATUS_ID = 'TRANSFER_CREATED' OR TRANSFER_HEADER.STATUS_ID = 'TRANSFER_APPROVED')";
    	}
    	if(valueCheck.equals("NotTimelyTransferRequest")){ 
    		sqlCondition = "((TRANSFER_HEADER.STATUS_ID = 'TRANSFER_CREATED' OR TRANSFER_HEADER.STATUS_ID = 'TRANSFER_APPROVED') AND (TRANSFER_HEADER.TRANSFER_DATE"+"<"+"'"+currentFirstTimeToday+"'"+"))";
    	}
    	if(valueCheck.equals("RequestTransferTomorrow")){
    		sqlCondition = "((TRANSFER_HEADER.STATUS_ID = 'TRANSFER_CREATED' OR TRANSFER_HEADER.STATUS_ID = 'TRANSFER_APPROVED') AND (TRANSFER_HEADER.TRANSFER_DATE BETWEEN"+"'"+currentFirstTimeTomorrow+"'"+"AND"+"'"+currentEndTimeTomorrow+"'"+"))";
    	}
    	if(valueCheck.equals("RequestsTransferredToday")){
    		sqlCondition = "((TRANSFER_HEADER.STATUS_ID = 'TRANSFER_CREATED' OR TRANSFER_HEADER.STATUS_ID = 'TRANSFER_APPROVED') AND (TRANSFER_HEADER.TRANSFER_DATE BETWEEN"+"'"+currentFirstTimeToday+"'"+"AND"+"'"+currentEndTimeToday+"'"+"))";
    	}
    	
		if(UtilValidate.isNotEmpty(sqlCondition)){
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		}
		
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listTransfer = delegator.findList("TransferHeader", cond, null, null, null, false);
		result.put("listTransfer", listTransfer);
		return result;
	} 
	
	public static Map<String, Object> getTotalProductInventoryExpire(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
    	String sqlCondition = "";
    	sqlCondition = "(INV.OWNER_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND INV.EXPIRE_DATE"+"<"+"'"+currentFirstTime+"'"+")";
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listProductInventoryExpire = delegator.findList("ProductInventoryTotalCount", cond, null, null, null, false);
    	int productCount = 0;
		if(UtilValidate.isNotEmpty(listProductInventoryExpire)){
			for (GenericValue totalProductSum : listProductInventoryExpire) { 
				Long productId = totalProductSum.getLong("productId");
				productCount += productId.intValue();
			}
		} 
		result.put("productCount", productCount);
		return result;
	}
	
	public static Map<String, Object> getTotalProductInventoryNearExpire(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
    	String sqlCondition = "";
    	sqlCondition = "(INV.OWNER_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND (DAYOFMONTH("+"INV.EXPIRE_DATE  - " + "'"+currentFirstTime+"'"+") <= PRF.THRESHOLDS_DATE) AND (DAYOFMONTH("+"INV.EXPIRE_DATE  - " + "'"+currentFirstTime+"'"+") > 0))"; 
		listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    	List<GenericValue> listProductInventoryExpire = delegator.findList("ProductInventoryNearDateTotalCount", cond, null, null, null, false);
    	int productCount = 0;
		if(UtilValidate.isNotEmpty(listProductInventoryExpire)){
			for (GenericValue totalProductSum : listProductInventoryExpire) { 
				Long productId = totalProductSum.getLong("productId");
				productCount += productId.intValue();
			}
		} 
		result.put("productCount", productCount);
		return result;
	}
	
	public static Map<String, Object> loadListProductInventoryItem(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		
		Date currentDate = new Date();
		String valueCheck = (String) context.get("valueCheck");
		Date currentFirstDate = getStartOfDay(currentDate);
		Timestamp currentFirstTime = new Timestamp(currentFirstDate.getTime());
		
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId); 
    	String sqlCondition = "";
    	List<GenericValue> listProductInv = FastList.newInstance();
    	if(valueCheck.equals("Inventory")){
    		sqlCondition = "(INV.OWNER_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND INV.EXPIRE_DATE"+"<"+"'"+currentFirstTime+"'"+")";
    		if(UtilValidate.isNotEmpty(sqlCondition)){
    			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
    		}  
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listProductInv = delegator.findList("ProductInventoryTotalCountGroupByProduct", cond, null, null, null, false);
    	}
    	if(valueCheck.equals("BLListInventoryNearExpiry")){ 
    		sqlCondition = "(INV.OWNER_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND (DATE_PART("+"'day'"+", INV.EXPIRE_DATE  - " + "'"+currentFirstTime+"'"+"::"+"timestamp" +") <= PRF.THRESHOLDS_DATE) AND (DATE_PART("+"'day'"+", INV.EXPIRE_DATE  - " + "'"+currentFirstTime+"'"+"::"+"timestamp" +") > 0))";
    		if(UtilValidate.isNotEmpty(sqlCondition)){
    			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
    		}  
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listProductInv = delegator.findList("ProductInventoryNearDateGroupByProductTotalCount", cond, null, null, null, false);
    	}
		
		result.put("listProductInv", listProductInv);
		return result;
	}  
	
	public static Map<String, Object> getTotalCustomerReturnProduct(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Date currentEndDate = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstDate.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndDate.getTime());
		String checkTime = (String) context.get("checkTime");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<GenericValue> listProductReturn = FastList.newInstance();
		
    	String sqlCondition = "";
		if(checkTime.equals("BLReqExpectImProductReturnToday")){
			sqlCondition = "(REH.TO_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND (REH.STATUS_ID='RETURN_REQUESTED' OR REH.STATUS_ID='RETURN_ACCEPTED') AND (REH.ENTRY_DATE BETWEEN"+"'"+currentFirstTimeToday+"'"+"AND"+"'"+currentEndTimeToday+"'"+"))";
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
			EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
	    	listProductReturn = delegator.findList("CustomerReturnTotalCount", cond, null, null, null, false);
		}
		if(checkTime.equals("BLReqImProductReturn")){
			sqlCondition = "(REH.TO_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND (REH.STATUS_ID='RETURN_REQUESTED' OR REH.STATUS_ID='RETURN_ACCEPTED'))"; 
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
			EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
	    	listProductReturn = delegator.findList("CustomerReturnTotalCount", cond, null, null, null, false);
		}
    	int productReturnCount = 0;
		if(UtilValidate.isNotEmpty(listProductReturn)){
			for (GenericValue totalProductSum : listProductReturn) { 
				Long returnId = totalProductSum.getLong("returnId");
				productReturnCount += returnId.intValue();
			}
		} 
		result.put("productReturnCount", productReturnCount);
		return result;
	}
	
	public static Map<String, Object> loadListProductCustomerReturn(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Date currentEndDate = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstDate.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndDate.getTime());
		String valueCheck = (String) context.get("valueCheck");
		
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId); 
    	String sqlCondition = "";
    	List<GenericValue> listProductReturn = FastList.newInstance();
    	if(valueCheck.equals("BLReqImProductReturn")){
    		sqlCondition = "(REH.TO_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND (REH.STATUS_ID='RETURN_REQUESTED' OR REH.STATUS_ID='RETURN_ACCEPTED'))"; 
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
			EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
	    	listProductReturn = delegator.findList("CustomerReturnTotalDetail", cond, null, null, null, false);
    	}   
    	if(valueCheck.equals("BLReqExpectImProductReturnToday")){ 
    		sqlCondition = "(REH.TO_PARTY_ID ="+"'"+ownerPartyId+"'"+" AND (REH.STATUS_ID='RETURN_REQUESTED' OR REH.STATUS_ID='RETURN_ACCEPTED') AND (REH.ENTRY_DATE BETWEEN"+"'"+currentFirstTimeToday+"'"+"AND"+"'"+currentEndTimeToday+"'"+"))";
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
			EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
	    	listProductReturn = delegator.findList("CustomerReturnTotalDetail", cond, null, null, null, false);
    	}
		
		result.put("listProductReturn", listProductReturn);
		return result;
	}  
}

