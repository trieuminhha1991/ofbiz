package com.olbius.salesmtl.party;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/*
 * by dungkhmt@gmail
 */
public class SalesmanService {
	public static String module = SalesmanService.class.getName();
    
	public static Map<String, Object> getSalesmanOfASalesSup(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		String salesSupId = (String) context.get("salesSupId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		
		Debug.log(module + "::getSalesmanOfASalesSup, userLoginId = " + userLoginId + ", salesSupId = " + salesSupId);
		try{
			Delegator delegator = ctx.getDelegator();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("salesSupId", EntityOperator.EQUALS, salesSupId));
			conds.add(EntityUtil.getFilterByDateExpr("fromDate1","thruDate1"));
			conds.add(EntityUtil.getFilterByDateExpr("fromDate2","thruDate2"));
			
			List<GenericValue> lst = delegator.findList("SalesmanAndSalesSup",
					EntityCondition.makeCondition(conds),
					null,
					null,
					null,
					false);
			retSucc.put("salesman", lst);		
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	public static Map<String, Object> getSalesmanCheckInHistory(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String fromDateStr = (String) context.get("fromDate");
		String thruDateStr = (String) context.get("thruDate");
		
		
		Timestamp fromDate = null;
        Timestamp thruDate = null;
        try {
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
	        	Long fromDateL = Long.parseLong(fromDateStr);
	        	fromDate = new Timestamp(fromDateL);
	        }
	        if (UtilValidate.isNotEmpty(thruDateStr)) {
	        	Long thruDateL = Long.parseLong(thruDateStr);
	        	thruDate = new Timestamp(thruDateL);
	        }
        } catch (Exception ex) {
        	ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
        }
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		ArrayList<String> sort = new ArrayList<String>();
		sort.add("checkInDate");
		Debug.log(module + "::getSalesmanCheckInHistory, userLoginId = " + userLoginId + ", partyId = " + partyId);
		try{
			Delegator delegator = ctx.getDelegator();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conds.add(EntityCondition.makeCondition("checkInDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
			List<GenericValue> lst = delegator.findList("CheckInHistoryDetailGeo",
					EntityCondition.makeCondition(conds),
					null,
					sort,
					null,
					false);
			retSucc.put("customers", lst);		
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
}
