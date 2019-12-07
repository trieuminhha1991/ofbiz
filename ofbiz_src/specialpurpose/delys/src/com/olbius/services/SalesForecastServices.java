package com.olbius.services;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SalesPartyUtil;
import com.olbius.util.SetUtil;

public class SalesForecastServices {
	public static final String module = SalesStatementServices.class.getName();
    public static final String resource = "DelysAdminUiLabels";
    public static final String resource_error = "DelysAdminErrorUiLabels";
    
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesForecastGroup(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("customTimePeriodId") && parameters.get("customTimePeriodId").length > 0) {
				String customTimePeriodId = parameters.get("customTimePeriodId")[0];
				if (UtilValidate.isNotEmpty(customTimePeriodId)) listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			}
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			List<String> listPeriodId = EntityUtil.getFieldListFromEntityList(delegator.findList("SalesForecast", tmpConditon, null, listSortFields, opts, false), "customTimePeriodId", true);
			if (UtilValidate.isNotEmpty(listPeriodId)) {
				listIterator = EntityUtil.filterByCondition(SalesPartyUtil.getListAscendantPeriod(delegator, listPeriodId), EntityCondition.makeCondition("periodTypeId", "SALES_YEAR"));
				if (listIterator != null) listIterator = (List<GenericValue>) SetUtil.removeDuplicateElementInList(listIterator);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesForecastGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
}
