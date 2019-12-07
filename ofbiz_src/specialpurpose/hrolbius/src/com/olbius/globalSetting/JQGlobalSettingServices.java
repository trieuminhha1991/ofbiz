package com.olbius.globalSetting;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;

public class JQGlobalSettingServices {
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> jqGetEmailSetting(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("EmailSetting",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);		
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
		return result;		
	}
}
