package com.olbius.bi.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.olap.bi.sales.SalesOrderCountOlap;
import com.olbius.olap.bi.sales.SalesTotalOlap;

public class SalesOrderServices {
	
	public final static String module = AccountingServices.class.getName();
	
	public static Map<String, Object> salesOrderTotal(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
//		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType= (String) context.get("dateType");
		
        Boolean taxFlag = (Boolean) context.get("taxFlag");		
        
        String show = (String) context.get("show");
        
        List<?> type = (List<?>) context.get("type[]");
        
        Map<String, String> map = new HashMap<String, String>();
        
        for(Object s : type) {
        	String tmp = (String) s;
        	tmp = (String) context.get(tmp);
        	map.put((String) s, tmp);
        }
        
        String currency = (String) context.get("currency");
        
        Boolean quantity = (Boolean) context.get("quantity");
        
        SalesTotalOlap olap = new SalesTotalOlap();
        
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		olap.setOlapResult(new OlapLineChart(olap, olap.new SalesAmountTotalChartResult()));
		
        olap.setFromDate(fromDate);
        olap.setThruDate(thruDate);
        
        olap.putParameter(TypeOlap.DATE_TYPE, dateType);
        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.TAX_FLAG, taxFlag);
        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.SHOW, show);
        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.TYPE, map);
        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.CURRENCY, currency);
        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.OLAP_QUANTITY, quantity);
//        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.ORGAN, organization);
        
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

		return result;
	}
	
	public static Map<String, Object> salesOrderCount(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();

		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        String dateType= (String) context.get("dateType");
		
        List<?> type = (List<?>) context.get("type[]");
        
        Map<String, String> map = new HashMap<String, String>();
        
        if(type != null) {
        	for(Object s : type) {
            	String tmp = (String) s;
            	tmp = (String) context.get(tmp);
            	if(tmp != null) {
            		map.put((String) s, tmp);
            	}
            }
        }
        
        SalesOrderCountOlap olap = new SalesOrderCountOlap();
        
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		olap.setOlapResult(new OlapLineChart(olap, olap.new SalesOrderCountChartResult()));
		
        olap.setFromDate(fromDate);
        olap.setThruDate(thruDate);
        
        olap.putParameter(TypeOlap.DATE_TYPE, dateType);
        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.TYPE, map);
        
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

		return result;
	}
}
