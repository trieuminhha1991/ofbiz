package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.Mobile;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.mobileUtil.InventoryUtils;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.mobileUtil.OrderUtils;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.common.util.EntityMiscUtil;


public class InventoryServices implements Mobile {

	public static final String module = InventoryServices.class.getName();
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInventoryOfCusInfo(
			DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String customerId = (String) context.get("partyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> inventoryCusInfo = null;
		List<GenericValue> productOrders = null;
		List<Map<String, Object>> res = FastList.newInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		try {
			EntityCondition party = EntityCondition.makeCondition("partyId",
					EntityOperator.EQUALS, customerId);
			EntityCondition created = EntityCondition.makeCondition(
					"createdBy", EntityOperator.EQUALS,
					userLogin.getString("userLoginId"));
			Set<String> fields = UtilMisc.toSet("productId", "qtyInInventory",
					"productName", "orderDate", "orderId", "fromDate");
			List<String> orderBy = UtilMisc.toList("fromDate DESC");
			EntityCondition invenCon = EntityCondition.makeCondition(
					UtilMisc.toList(party, created), EntityOperator.AND);
			inventoryCusInfo = delegator.findList("InventoryOfCustomerDetail",
					invenCon, fields, orderBy, null, false);
			if (UtilValidate.isNotEmpty(inventoryCusInfo)) {
				long newest = 0;
				Timestamp temp = null;
				for (GenericValue inventory : inventoryCusInfo) {
					String productId = inventory.getString("productId");
					GenericValue product = delegator.findOne("Product",
							UtilMisc.toMap("productId", productId), false);
					String productName = product.getString("productName");
					String orderId = inventory.getString("orderId");
					Timestamp orderDate = inventory.getTimestamp("orderDate");
					String orderStr = null;
					if(orderDate != null){
						orderStr = simpleDateFormat.format(orderDate);
					}
					Timestamp fromDateTime = inventory.getTimestamp("fromDate");
					long fromDate = fromDateTime.getTime();
					if (newest == 0 && fromDate != 0) {
						newest = fromDate;
						temp = fromDateTime;
					}
					BigDecimal qty = inventory.getBigDecimal("qtyInInventory");
					if (newest == fromDate && qty.compareTo(BigDecimal.ZERO) == 1) {
						Map<String, Object> tempMap = FastMap.newInstance();
						Map<String, Object> image = ProductServices.getProductDetail(dctx, context);
						tempMap.putAll(image);
						tempMap.put("productId", productId);
						tempMap.put("productName", productName);
						tempMap.put("orderId", orderId);
						tempMap.put("orderDate", orderStr);
						tempMap.put("qtyInInventory",
								qty);
						tempMap.put("partyId", customerId);
						res.add(tempMap);
					} else {
						continue;
					}
				}
				/* get new order from recent check inventory */
				productOrders = OrderUtils.getNewOrder(delegator, customerId, temp);
				for(GenericValue pro : productOrders){
					Map<String,Object> proTmp  = FastMap.newInstance();
					proTmp.put("productId", pro.getString("productId"));
					proTmp.put("productName", pro.getString("productName"));
					proTmp.put("orderId", pro.getString("orderId"));
					Timestamp orderDt =  pro.getTimestamp("orderDate");
					String orderTmp = simpleDateFormat.format(orderDt);
					proTmp.put("orderDate", orderTmp);
					proTmp.put("qtyInInventory", pro.getBigDecimal("quantity"));
					proTmp.put("partyId",customerId );
					res.add(proTmp);
				}
//				List<Map<String, Object>> tempMap = FastList.newInstance();
//				res.addAll(productOrders);
			} else {
				/* get inventory first time */
				productOrders = OrderUtils.getNewOrder(delegator, customerId, null);
				res = InventoryUtils.initListInventoryObject(productOrders);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Map<String, Object> tempMap = FastMap.newInstance();
		tempMap.put("inventoryCusInfo", res);
		return tempMap;
	}
	/*
	 * submit inventory item of each customer update into database
	 */
	@SuppressWarnings("deprecation")
	public static String updateInventoryCus(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = request.getParameter("party_id");
		Locale locale = UtilHttp.getLocale(request);
		// check customer active
		Map<String, Object> checkCustomer = MobileUtils.checkCustomerActive(delegator, locale, partyId);
		if (checkCustomer.get("responseMessage").equals("error")){
			request.setAttribute(Mobile.ERROR_MESSAGE, checkCustomer.get("errorMessage").toString());
			return "error";
		}
		String checkInventoryList = new String(request.getParameter("inventory"));
		String lastCheck = request.getParameter("lastCheck");
		Timestamp fromDate = null;
        List<EntityCondition> conditions = FastList.newInstance();
        String isLastUpdateInventory = "Y";
        String isNotLast = "N";
        List<GenericValue> cusProInventOlds = FastList.newInstance();
		if(UtilValidate.isNotEmpty(lastCheck)){
			 fromDate  = new Timestamp(Long.parseLong(lastCheck));
		}
		JSONObject a = new JSONObject();
		JSONArray json = new JSONArray();
		if(UtilValidate.isNotEmpty(checkInventoryList)){
			 json = JSONArray.fromObject(checkInventoryList);
		}
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		int length = json.size();
		Date date = new Date();
		if(UtilValidate.isEmpty(fromDate)){
			 fromDate = new Timestamp(date.getTime());
		}
		String createdBy = userLogin.getString("userLoginId");
		if (userLogin != null && length != 0) {
			for (int i = 0; i < length; i++) {
			    conditions.clear();
				JSONObject inventory = json.getJSONObject(i);
				String productId = inventory.getString("productId");
				String quantity = inventory.getString("qtyInInventory");
				Map<String, Object> ctxMap = FastMap.newInstance();
				if(inventory.containsKey("orderId")){
					String orderId = inventory.getString("orderId");
					ctxMap.put("orderId", orderId);
				}

				//check Exist CustomerProductInventory record before
                conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isLastUpdateInventory",  EntityOperator.EQUALS, isLastUpdateInventory), EntityOperator.OR, EntityCondition.makeCondition("isLastUpdateInventory", EntityOperator.EQUALS, null)));
                conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
                conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
                conditions.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, createdBy));

                try {
                    cusProInventOlds = delegator.findList("CustomerProductInventory", EntityCondition.makeCondition(conditions), null, null, null, false);
                    if(UtilValidate.isNotEmpty(cusProInventOlds)){
                        for(GenericValue cusProInventOld: cusProInventOlds){
                            cusProInventOld.set("isLastUpdateInventory", isNotLast);
                            delegator.store(cusProInventOld);
                        }
                    }
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }

                BigDecimal qttBig = new BigDecimal(quantity);
				InventoryUtils.createCustomerInventory(delegator, productId, partyId, qttBig, fromDate, createdBy);
			}
			request.setAttribute("retMsg", "update_success");
			return "success";
		}

		request.setAttribute("retMsg", "login_required");
		return "error";
	}
	public static String getStoreInventoriesStatus(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException{
		String productStoreId = request.getParameter("productStoreId");
		List<Map<String, Object>> products = MobileUtils.getAllProducts(request, productStoreId);
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		Map<String, Object> in = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			for(Map<String, Object> product : products){
				in = FastMap.newInstance();
				in.put("userLogin", userLogin);
				in.put("productId", product.get("productId"));
				in.put("quantity", BigDecimal.ZERO);
				in.put("productStoreId", productStoreId);
				in.put("viewATPForAll", "Y");
				String uomId = (String)product.get("uomId");
				Boolean isOK = false;
				BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
				if (uomId != null && !"".equals(uomId)){
					in.put("uomId", uomId);
					Map<String, Object> mapUom = dispatcher.runSync("checkInventoryAvailableWithUom", in);
					isOK = (Boolean)mapUom.get("isOK");
					availableToPromiseTotal = (BigDecimal)mapUom.get("availableToPromiseTotal");
				} else {
					Map<String, Object> mapNonUom = dispatcher.runSync("checkInventoryAvailable", in);
					isOK = (Boolean)mapNonUom.get("isOK");
					availableToPromiseTotal = (BigDecimal)mapNonUom.get("availableToPromiseTotal");
				}
				product.put("available", isOK);
				product.put("availableToPromiseTotal", availableToPromiseTotal);
			}
			request.setAttribute("results", products);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage());
			return "error";
		}
		return "success";
	}

    @SuppressWarnings("deprecation")
    public static Map<String, Object> mUpdateQtyProductExpInvent(DispatchContext dcx, Map<String, Object> context){
        Delegator delegator = dcx.getDelegator();
        Map<String, Object> res = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        String listProdExp = (String) context.get("listProdExp");
        Timestamp expiredDateParam = null;
        List<EntityCondition> conditions = FastList.newInstance();
        String isRecentUpdateInventExp = "Y";
        String isNotRecentUpdate = "N";
        Timestamp fromDate = null;
        BigDecimal qtyExpInventory = BigDecimal.ZERO;
        fromDate = UtilDateTime.nowTimestamp();
        String createdBy = userLogin.getString("partyId");
        Locale locale = (Locale) context.get("locale");
        try{
            JSONArray json = new JSONArray();
            if(UtilValidate.isNotEmpty(listProdExp)){
                json = JSONArray.fromObject(listProdExp);
            }
            int length = json.size();
            //check Exist CustomerProductExpDateInventory record before
            conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isRecentUpdateInventExp",  EntityOperator.EQUALS, isRecentUpdateInventExp), EntityOperator.OR, EntityCondition.makeCondition("isRecentUpdateInventExp", EntityOperator.EQUALS, null)));
            conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
            List<GenericValue> cusProdExpDateInvents = delegator.findList("CustomerProductExpDateInventory",
                    EntityCondition.makeCondition(conditions), null, null, null, false);
            if(UtilValidate.isNotEmpty(cusProdExpDateInvents)){
                for(GenericValue cusProdExpDateInvent: cusProdExpDateInvents){
                    cusProdExpDateInvent.set("isRecentUpdateInventExp", isNotRecentUpdate);
                    delegator.store(cusProdExpDateInvent);
                }
            }
            if (UtilValidate.isNotEmpty(userLogin) && length != 0) {
                for (int i = 0; i < length; i++) {
                    conditions.clear();
                    JSONObject listJSProdExp = json.getJSONObject(i);
                    String productId = listJSProdExp.getString("productId");
                    String qtyExpInventoryStr = listJSProdExp.getString("qtyExpInventory");
                    qtyExpInventory = new BigDecimal(qtyExpInventoryStr);
                    String expiredDateStr = listJSProdExp.getString("expiredDateStr");
                    if(UtilValidate.isNotEmpty(expiredDateStr)){
                        Long expiredDateL = Long.parseLong(expiredDateStr);
                        expiredDateParam = new Timestamp(expiredDateL);
                        context.put("expiredDate", expiredDateParam);
                    }
                    if(UtilValidate.isNotEmpty(productId)){
                        GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId",productId),false);
                        if(UtilValidate.isEmpty(product)){
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSProductNotExist", locale));
                        }
                    }
                    GenericValue cusProdExpDateInventCreate = delegator.makeValidValue("CustomerProductExpDateInventory", context);
                    cusProdExpDateInventCreate.set("partyId", partyId);
                    cusProdExpDateInventCreate.set("productId", productId);
                    cusProdExpDateInventCreate.set("expiredDate", expiredDateParam);
                    cusProdExpDateInventCreate.set("qtyExpInventory", qtyExpInventory);
                    cusProdExpDateInventCreate.set("fromDate", fromDate);
                    cusProdExpDateInventCreate.set("createdBy", createdBy);
                    cusProdExpDateInventCreate.set("isRecentUpdateInventExp", isRecentUpdateInventExp);
                    cusProdExpDateInventCreate.create();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return ServiceUtil.returnError("Fatal error when create or update quantity ProductExp " + e.getMessage());
        }
        return res;
    }

    public static Map<String, Object> mGetListProductExpRecent(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<GenericValue> listProductExpRecent = FastList.newInstance();
        Integer page = 0;

        if (UtilValidate.isNotEmpty(context.get("viewIndex"))) {
            page = Integer.parseInt((String) context.get("viewIndex"));
        }
        Integer size = 10;
        if (UtilValidate.isNotEmpty(context.get("viewSize"))) {
            size = Integer.parseInt((String) context.get("viewSize"));
        }
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");

        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = FastList.newInstance();
        List<String> listSortFields = FastList.newInstance();
        EntityFindOptions opts = new EntityFindOptions();
        try {
            String partyId = (String) context.get("partyId");
            String userLoginPartyId = (String) userLogin.get("partyId");

            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("-productId");
            }

            listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId)));
            listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("createdBy", userLoginPartyId)));
            listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isRecentUpdateInventExp", EntityOperator.EQUALS, "Y")));

            Set<String> listSelectFields = new HashSet<String>();
            listSelectFields.add("productCode");
            listSelectFields.add("productId");
            listSelectFields.add("productName");
            listSelectFields.add("expiredDate");
            listSelectFields.add("qtyExpInventory");

            listIterator = EntityMiscUtil.processIterator(parameters,successResult,delegator,"ProductInventoryExpOfCustomerDetail",EntityCondition.makeCondition(listAllConditions),null,UtilMisc.toSet(listSelectFields),listSortFields, opts);
            listProductExpRecent = listIterator.getCompleteList();

        } catch (Exception e) {
            String errMsg = "Fatal error calling mGetListProductExpRecent service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listProductExpRecent", listProductExpRecent);
        successResult.put("totalRows", Integer.parseInt((String)successResult.get("TotalRows")));
        successResult.remove("TotalRows");
        return successResult;
    }
}
