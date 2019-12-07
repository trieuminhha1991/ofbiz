package com.olbius.basepos.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.grid.OlapGrid;

public class PosOlapGridServices{
	
	public static Map<String, Object> reportSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) throws ParseException{
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOrderOlapImpl grid = new SalesOrderOlapImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String reportType= (String) context.get("reportType");
		String dateType= (String) context.get("dateType");
        if(dateType == null) {
        	dateType = "DAY";
        }
        String productId = (String) context.get("productId");
        String categoryId = (String) context.get("categoryId");
        String productStoreId = (String) context.get("productStoreId");
        String partyId = (String) context.get("partyId");
		String sortField = (String) context.get("sortField");
		String sortOption = (String) context.get("sortOption");
		String customTime = (String) context.get("customTime");
        Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		Date beginDate = null;
		
		int monthInput = cal.get(Calendar.MONTH);
		int quarterInput = (monthInput / 3) + 1;
		int yearInput = cal.get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if("dd".equals(customTime)){
			beginDate = currentDate;
		} else if ("ww".equals(customTime)){
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			beginDate = cal.getTime();
		} else if ("mm".equals(customTime)){
			cal.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = cal.getTime();
		} else if ("qq".equals(customTime)){
			if(quarterInput == 1){
				beginDate = sdf.parse("01/01/" + yearInput);
			} else if (quarterInput == 2){
				beginDate = sdf.parse("01/04/" + yearInput);
			} else if (quarterInput == 3){
				beginDate = sdf.parse("01/07/" + yearInput);
			} else if (quarterInput == 4){
				beginDate = sdf.parse("01/10/" + yearInput);
			}
		} else if("yy".equals(customTime)){
			beginDate = sdf.parse("01/01/" + yearInput);
		}
        
		if("oo".equals(customTime)){
			grid.setFromDate(fromDate);
			grid.setThruDate(thruDate);
		} else {
			grid.setFromDate(beginDate);
			grid.setThruDate(currentDate);
		}
        
        grid.putParameter(SalesOrderOlapImpl.DATE_TYPE, dateType);
        grid.putParameter(SalesOrderOlapImpl.SORT_FIELD, sortField);
        grid.putParameter(SalesOrderOlapImpl.SORT_OPTION, sortOption);
        grid.putParameter("reportType", reportType);
        grid.putParameter("productId", productId);
        grid.putParameter("categoryId", categoryId);
        grid.putParameter("productStoreId", productStoreId);
        grid.putParameter("partyId", partyId);
        grid.putParameter("org", organization);
        
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> reportReturn(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		ReturnOlapImpl grid = new ReturnOlapImpl();
		
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String partyId = (String) context.get("partyId");
        String facilityId = (String) context.get("facilityId");
        String sortField = (String) context.get("sortField");
		String sortOption = (String) context.get("sortOption");
        
        grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
        grid.setFromDate(fromDate);
        grid.setThruDate(thruDate);
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        
        grid.putParameter(ReturnOlapImpl.SORT_FIELD, sortField);
        grid.putParameter(ReturnOlapImpl.SORT_OPTION, sortOption);
        grid.putParameter("facilityId", facilityId);
        grid.putParameter("partyId", partyId);
        grid.putParameter("org", organization);
        
        OlapGrid gridResult = new OlapGrid(grid, grid.new ReturnGrid());
		grid.setOlapResult(gridResult);
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
}
