package com.olbius.acc.report.taxInvoice;

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

import javolution.util.FastList;

public class VoucherTaxReport {

	@SuppressWarnings({ "unchecked" })
	public static Map<String,Object> getJQVoucherTaxReport(DispatchContext dpct,Map<String,Object> context){
	 	Delegator delegator = dpct.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String invoiceType = "";
		if (UtilValidate.isNotEmpty(parameters.get("invoiceType"))) {
			invoiceType = parameters.get("invoiceType")[0];
		} else if (UtilValidate.isNotEmpty(context.get("invoiceType"))) {
			invoiceType = (String) context.get("invoiceType");
		}
    	try {
    		if (invoiceType != null && !"".equals(invoiceType) && ("SALES_INVOICE".equals(invoiceType) || "PURCHASE_INVOICE".equals(invoiceType))) {
    			if ("SALES_INVOICE".equals(invoiceType)) {
    				listAllConditions.add(EntityCondition.makeCondition("parentTypeId", "SALES_INVOICE"));
    				listAllConditions.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.NOT_EQUAL, "PURC_RTN_INVOICE"));
    			} else if ("PURCHASE_INVOICE".equals(invoiceType)) {
    				List<EntityCondition> condReturnSupList = FastList.newInstance();
    				condReturnSupList.add(EntityCondition.makeCondition("parentTypeId", "SALES_INVOICE"));
    				condReturnSupList.add(EntityCondition.makeCondition("invoiceTypeId", "PURC_RTN_INVOICE"));
    				EntityCondition condReturnSup = EntityCondition.makeCondition(condReturnSupList);
    				List<EntityCondition> conds = FastList.newInstance();
    				conds.add(EntityCondition.makeCondition("parentTypeId", "PURCHASE_INVOICE"));
    				conds.add(condReturnSup);
    				EntityCondition condition = EntityCondition.makeCondition(conds, EntityOperator.OR);
    				listAllConditions.add(condition);
    			}
                listSortFields.add("-issuedDate");
    			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "VoucherTaxReport", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		}
    		successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			Debug.log("error call services getJQVoucherTaxReport" + e.getMessage());
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	 }
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String,Object> getJQVoucherTaxPaymentReport(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "VoucherTaxPaymentReport", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			Debug.log("error call services getJQVoucherTaxPaymentReport" + e.getMessage());
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
}