package com.olbius.salesmtl.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.grid.OlapGrid;

import javolution.util.FastMap;

public class ReportServices {
	public static final String module = ReportServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductInventoryCustomerOlap(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		ProductInventoryCustomerOlapImp ewo = new ProductInventoryCustomerOlapImp(delegator);
		String dateType= (String) context.get("dateType");
		List<Object> productId= (List<Object>) context.get("productId[]");
		List<Object> categoryId= (List<Object>) context.get("categoryId[]");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
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
		if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
			ewo.setFromDate(fromDate);
	        ewo.setThruDate(thruDate);
	        
	        ewo.putParameter(ProductInventoryCustomerOlapImp.DATE_TYPE, dateType);
	        ewo.putParameter(ProductInventoryCustomerOlapImp.PRODUCT_ID, productId);
	        ewo.putParameter(ProductInventoryCustomerOlapImp.USER_LOGIN_ID, partyIdByFacility);
	        ewo.putParameter(ProductInventoryCustomerOlapImp.CATEGORY_ID, categoryId);
	        
	        ewo.setOlapResultType(OlapGrid.class);
	        
			result = ewo.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		}
		return result;
	}
}
