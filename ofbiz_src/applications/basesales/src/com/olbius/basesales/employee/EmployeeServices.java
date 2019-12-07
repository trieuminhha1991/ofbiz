package com.olbius.basesales.employee;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.common.util.EntityMiscUtil;

public class EmployeeServices {
	public static final String module = EmployeeServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListHistoryLoginPOS(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-fromDate");
			}
			opts.setDistinct(true);
			EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "LoginHistoryPOS", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListHistoryLoginPOS service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}
}
