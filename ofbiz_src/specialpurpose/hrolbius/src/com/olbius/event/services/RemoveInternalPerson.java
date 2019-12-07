package com.olbius.event.services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;


public class RemoveInternalPerson {
	public static final String module = RemoveInternalPerson.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	
	public static Map<String, Object> removeInternalPerson(DispatchContext dpCtx,
			Map<String, ? extends Object> context){
		Delegator delegator= dpCtx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		
		String partyIdTo= (String)context.get("partyIdTo");
		String partyIdFrom=(String)context.get("partyIdFrom");
		String roleTypeIdFrom=(String)context.get("roleTypeIdFrom");
		String roleTypeIdTo=(String)context.get("roleTypeIdTo");
		Date today= new Date();
		Timestamp sqlday= new Timestamp(today.getTime());
		EntityCondition condition1= EntityCondition.makeCondition("partyIdTo",partyIdTo);
		EntityCondition condition2= EntityCondition.makeCondition("partyIdFrom",partyIdFrom);
		EntityCondition condition3= EntityCondition.makeCondition("roleTypeIdFrom",roleTypeIdFrom);
		EntityCondition condition4=EntityCondition.makeCondition("roleTypeIdTo",roleTypeIdTo);
		EntityCondition conditionList= EntityCondition.makeCondition(EntityJoinOperator.AND,condition1,condition2,condition3,condition4);
		try {
			List<GenericValue> employ= delegator.findList("Employment",conditionList,null, null,null, false);
			if(employ==null||employ.isEmpty()){
				return ServiceUtil.returnError(UtilProperties.getMessage(
						resourceNoti, "deleteErro", locale));
			}
			else{
				for(GenericValue item:employ){
					item.put("thruDate", sqlday);
					item.store();
					
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "deleteErro",
					new Object[] { e.getMessage() }, locale));
		}
		
			return ServiceUtil.returnSuccess(UtilProperties.getMessage(
						resourceNoti, "Delete success", locale));
		
	}
			
}
