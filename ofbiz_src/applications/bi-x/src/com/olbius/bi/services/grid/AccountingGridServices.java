package com.olbius.bi.services.grid;

import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.olap.bi.accounting.AccountingOlapImpl;

public class AccountingGridServices {
	public static Map<String, Object> evaluateAcc(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		
//		AccountingOlapGrid grid = new AccountingOlapGridImpl();
		
		AccountingOlapImpl grid = new AccountingOlapImpl();
		
		Boolean orig = (Boolean) context.get("orig");		
		if(orig == null) {
			orig = false;
		}
        
		String dateType= (String) context.get("dateType");
        if(dateType == null) {
        	dateType = "DAY";
        }
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
		
        grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        grid.setFromDate(fromDate);
        grid.setThruDate(thruDate);
        
        grid.putParameter(AccountingOlapImpl.DATE_TYPE, dateType);
        
        grid.putParameter(AccountingOlapImpl.ORIG, orig);
        
		Map<String, Object> result = grid.execute(context);
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
