package com.olbius.service;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class SalesmanServices {
	public static Map getRoadOfSalesman(DispatchContext ctx,Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
//		EntityConditionBuilder exprBldr = new EntityConditionBuilder();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		// List stores
		List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.get("partyId")));
        List<GenericValue> listProductStore = null;
        Map<String,Object> returnResult = ServiceUtil.returnSuccess();
		
        // List Roads
        List<EntityExpr> exprs2 = FastList.newInstance();
        exprs2.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
        exprs2.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "DELYS_SALESMAN_GT"));
        exprs2.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "DELYS_ROUTE"));
        List<GenericValue> listRoute = null;
		try {
			listProductStore = delegator.findList("ProductStorePartyView", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			returnResult.put("listProductStore", listProductStore);
			listRoute = delegator.findList("PartyRelationshipFromPartyOlbius", EntityCondition.makeCondition(exprs2, EntityOperator.AND), null, null, null, false);
			returnResult.put("listRoute", listRoute);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(listProductStore.isEmpty()){
			returnResult.put("listProductStore", null);
		}
		if(listRoute.isEmpty()){
			returnResult.put("listRoute", null);
		}
		return returnResult;
	}
}
