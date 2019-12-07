package com.olbius.salesmtl.inventoryCustomer;

import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.rmi.CORBA.Util;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static com.olbius.basesales.product.ProductPriceServices.processFromDateThruDate;

public class InventoryCustomerServices {
	public static final String module = InventoryCustomerServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListInventoryCusAndProdMT(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<EntityCondition> condsPR = FastList.newInstance();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			String productIdsStr = null;
			if (parameters.containsKey("productIds") && parameters.get("productIds").length > 0) {
				productIdsStr = parameters.get("productIds")[0];
			}
			String agentChainMTId = null;
			if (parameters.containsKey("agentChainMTId") && parameters.get("agentChainMTId").length > 0) {
				agentChainMTId = parameters.get("agentChainMTId")[0];
			}
            String changeDateTypeId = SalesUtil.getParameter(parameters, "changeDateTypeId");
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            Timestamp fromDate = null;
            Timestamp thruDate = null;

            if(UtilValidate.isEmpty(changeDateTypeId)){
                Map<String, Object> processDateResult = processFromDateThruDate(parameters, nowTimestamp, locale);
                if (ServiceUtil.isError(processDateResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(processDateResult));
                }
                fromDate = (Timestamp) processDateResult.get("fromDate");
                thruDate = (Timestamp) processDateResult.get("thruDate");
            }
			List<String> productIds = new ArrayList<String>();
			if (productIdsStr != null) {
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(productIdsStr)) {
					jsonArray = JSONArray.fromObject(productIdsStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						productIds.add(jsonArray.getString(i));
					}
				}
			}
            List<GenericValue> customers = FastList.newInstance();
			if (UtilValidate.isNotEmpty(productIds)) {
				List<GenericValue> listProduct = ProductWorker.getListProduct(delegator, productIds);
				if(UtilValidate.isEmpty(changeDateTypeId)){
                    condsPR.add(EntityCondition.makeCondition("fromDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
                }else{
                    condsPR.add(EntityCondition.makeCondition("isLastUpdateInventory", EntityOperator.EQUALS, "Y"));
                }
                if(UtilValidate.isNotEmpty(agentChainMTId)){
                    listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, agentChainMTId));
                    condsPR.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, agentChainMTId));
                }
                customers = delegator.findList("MTPartyRelAndPartyAndPartyCus", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
                List<GenericValue> customerProdInventsParty = delegator.findList("MTPartyRelPartyCusAndProdInvent", EntityCondition.makeCondition(condsPR), null, null, null, false);

                Map<String, Object> customerProdInventMap = FastMap.newInstance();
				for (GenericValue cusProdInvent : customerProdInventsParty) {
				    String partyCheckInventId = (String) cusProdInvent.get("createdBy");
                    GenericValue person = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", partyCheckInventId), false);
                    customerProdInventMap.put(cusProdInvent.getString("customerId") + cusProdInvent.getString("productId"), cusProdInvent.getBigDecimal("qtyInInventory"));
                    customerProdInventMap.put(cusProdInvent.getString("customerId")+ partyCheckInventId, person.getString("fullName"));
                    customerProdInventMap.put(cusProdInvent.getString("customerId")+ "fromDate", cusProdInvent.get("fromDate"));
				}
                for (GenericValue customer : customers) {
					Map<String, Object> tempMap = FastMap.newInstance();
					tempMap.put("partyId", customer.getString("customerId"));
					tempMap.put("partyCode", customer.getString("partyCode"));
					tempMap.put("fullName", customer.getString("fullName"));
                    for (GenericValue cusProdInvent : customerProdInventsParty) {
                        String partyCheckInventId = (String) cusProdInvent.get("createdBy");
                        tempMap.put("createdBy", customerProdInventMap.get(customer.getString("customerId") + partyCheckInventId));
                        tempMap.put("fromDate", customerProdInventMap.get(customer.getString("customerId") + "fromDate"));
                    }
					for (GenericValue product : listProduct) {
						tempMap.put("prodCode_" + product.getString("productId"), null);
						tempMap.put("prodCode_" + product.getString("productId"), customerProdInventMap.get(customer.getString("customerId") + product.getString("productId")));
					}
					listIterator.add(tempMap);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCustomerAndProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

    public static Map<String, Object> jqGetListInventoryCusAndProdGT(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<Map<String, Object>> listIterator = FastList.newInstance();
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<EntityCondition> condsPR = FastList.newInstance();
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        List<EntityCondition> conds = FastList.newInstance();
        try {
            String productIdsStr = null;
            if (parameters.containsKey("productIds") && parameters.get("productIds").length > 0) {
                productIdsStr = parameters.get("productIds")[0];
            }
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            Timestamp fromDate = null;
            Timestamp thruDate = null;

            String changeDateTypeId = SalesUtil.getParameter(parameters, "changeDateTypeId");
            if(UtilValidate.isEmpty(changeDateTypeId)){
                Map<String, Object> processDateResult = processFromDateThruDate(parameters, nowTimestamp, locale);
                if (ServiceUtil.isError(processDateResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(processDateResult));
                }
                fromDate = (Timestamp) processDateResult.get("fromDate");
                thruDate = (Timestamp) processDateResult.get("thruDate");
            }

            List<String> productIds = new ArrayList<String>();
            if (productIdsStr != null) {
                JSONArray jsonArray = new JSONArray();
                if (UtilValidate.isNotEmpty(productIdsStr)) {
                    jsonArray = JSONArray.fromObject(productIdsStr);
                }
                if (jsonArray != null && jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        productIds.add(jsonArray.getString(i));
                    }
                }
            }
            if (UtilValidate.isNotEmpty(productIds)) {
                List<GenericValue> listProduct = ProductWorker.getListProduct(delegator, productIds);
                List<String> partyIds = FastList.newInstance();
                List<String> customerIds = FastList.newInstance();
                List<GenericValue> customers = FastList.newInstance();
                //Cua hang GT
                listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
                listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", EntityOperator.EQUALS, "SMCHANNEL_GT"));
                customers = delegator.findList("PartyCustomer", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
                customerIds = EntityUtil.getFieldListFromEntityList(customers, "partyId", false);

                conds.clear();
                if(UtilValidate.isEmpty(changeDateTypeId)){
                    conds.add(EntityCondition.makeCondition("fromDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
                }else{
                    conds.add(EntityCondition.makeCondition("isLastUpdateInventory", EntityOperator.EQUALS, "Y"));
                }
                conds.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, customerIds));
                List<GenericValue> customerProdInvents = delegator.findList("CustomerProductInventory", EntityCondition.makeCondition(conds), null, null, null, false);
                Map<String, Object> customerProdInventMap = FastMap.newInstance();
                for (GenericValue customerProdInvent : customerProdInvents) {
                    String partyCheckInventId = (String) customerProdInvent.get("createdBy");
                    GenericValue person = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", partyCheckInventId), false);
                    customerProdInventMap.put(customerProdInvent.getString("partyId") + customerProdInvent.getString("productId"), customerProdInvent.getBigDecimal("qtyInInventory"));
                    customerProdInventMap.put(customerProdInvent.getString("partyId")+ partyCheckInventId, person.getString("fullName"));
                    customerProdInventMap.put(customerProdInvent.getString("partyId")+ "fromDate", customerProdInvent.get("fromDate"));
                }

                for (GenericValue customer : customers) {
                    Map<String, Object> tempMap = FastMap.newInstance();
                    tempMap.put("partyId", customer.getString("partyId"));
                    tempMap.put("partyCode", customer.getString("partyCode"));
                    tempMap.put("fullName", customer.getString("fullName"));
                    for (GenericValue customerProdInvent : customerProdInvents) {
                        String partyCheckInventId = (String) customerProdInvent.get("createdBy");
                        tempMap.put("createdBy", customerProdInventMap.get(customer.getString("partyId") + partyCheckInventId));
                        tempMap.put("fromDate", customerProdInventMap.get(customer.getString("partyId") + "fromDate"));
                    }
                    for (GenericValue product : listProduct) {
                        tempMap.put("prodCode_" + product.getString("productId"), null);
                        tempMap.put("prodCode_" + product.getString("productId"), customerProdInventMap.get(customer.getString("partyId") + product.getString("productId")));
                    }
                    listIterator.add(tempMap);
                }
            }
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListCustomerAndProduct service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }
}