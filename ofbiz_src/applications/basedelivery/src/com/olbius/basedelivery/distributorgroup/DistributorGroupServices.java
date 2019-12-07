package com.olbius.basedelivery.distributorgroup;

import com.olbius.basesales.util.CRMUtils;
import com.olbius.basesales.util.SalesUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

;

/**
 * Created by user on 11/30/17.
 */
public class DistributorGroupServices {
    public static final String module = DistributorGroupServices.class.getName();
    public static final String resource_error = "BaseSalesErrorUiLabels";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListDistributorAndGroup(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<Map<String, Object>> listDistributorGroup = FastList.newInstance();
        String TotalRows = "0";

        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");

        try {
            Map<String, Object> listDistributor = dispatcher.runSync("JQGetListDistributor", UtilMisc.toMap("userLogin", userLogin, "locale", locale, "listAllConditions", listAllConditions, "listSortFields", listSortFields, "opts", opts, "parameters", parameters));
            List<Map<String, Object>> distributors = (List<Map<String, Object>>) listDistributor.get("listIterator");
            TotalRows = (String) listDistributor.get("TotalRows");
            for(Map<String, Object> distributor : distributors) {
                String partyId = (String) distributor.get("partyId");
                String productStoreGroupId = null;
                String productStoreGroupName = null;
                List<GenericValue> productStores = delegator.findList("ProductStore", EntityCondition.makeCondition("payToPartyId", EntityOperator.EQUALS, partyId), null, null, null, false);
                if(productStores != null && productStores.size() > 0) {
                    for(GenericValue productStore : productStores) {
                        productStoreGroupId = (String) productStore.get("primaryStoreGroupId");
                        System.out.println("" + productStore.get("partyCode"));
                        GenericValue productStoreGroup = delegator.findOne("ProductStoreGroup", UtilMisc.toMap("productStoreGroupId", productStoreGroupId),false);
                        if(productStoreGroup != null) {
                            productStoreGroupId = (String) productStoreGroup.get("productStoreGroupId");
                            productStoreGroupName = (String) productStoreGroup.get("productStoreGroupName");
                        }
                    }
                }
                distributor.put("productStoreGroupId", productStoreGroupId);
                distributor.put("productStoreGroupName", productStoreGroupName);
                listDistributorGroup.add(distributor);
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        successResult.put("listIterator", listDistributorGroup);
        successResult.put("TotalRows", TotalRows);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListDistributorsByDistributorGroupId(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        EntityListIterator dummy = null;
        List<Map<String, Object>> distributors = FastList.newInstance();
        String TotalRows = "0";
        try {
            Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
            int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
            int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
            int start = pageNum * pagesize + 1;
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            String productStoreGroupId = SalesUtil.getParameter(parameters, "productStoreGroupId");

            List<EntityCondition> conditions = FastList.newInstance();
            conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
            conditions.add(EntityCondition
                    .makeCondition(UtilMisc.toMap("roleTypeIdTo", "INTERNAL_ORGANIZATIO",
                            "roleTypeIdFrom", "DISTRIBUTOR", "partyRelationshipTypeId", "DISTRIBUTOR_REL")));
            conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
            if(productStoreGroupId != null && !productStoreGroupId.isEmpty()) {
                List<String> partyIds = DistributorGroupWorker.getListPartyIdByProductStoreGroupId(delegator, productStoreGroupId);
                conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
            }
            dummy = delegator.find("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(conditions), null, null, listSortFields, opts);

            TotalRows = String.valueOf(dummy.getResultsTotalSize());

            List<GenericValue> parties = dummy.getPartialList(start, pagesize);
            for (GenericValue x : parties) {
                Map<String, Object> party = FastMap.newInstance();
                party.put("partyId", x.get("partyId"));
                party.put("partyCode", x.get("partyCode"));
                party.put("statusId", x.get("statusId"));
                party.put("groupName", x.get("groupName"));
                party.put("preferredCurrencyUomId", x.get("preferredCurrencyUomId"));
                party.put("officeSiteName", x.get("officeSiteName"));
                Map<String, Object> getPartyInfo = dispatcher.runSync("getPartyInformation",
                        UtilMisc.toMap("partyId", x.get("partyId"), "userLogin", userLogin));
                Map<String, Object> partyInfo = (Map<String, Object>) getPartyInfo.get("partyInfo");
                party.put("contactNumber", (String) partyInfo.get("contactNumber"));
                party.put("emailAddress", (String) partyInfo.get("emailAddress"));
                List<Map<String, Object>> listAddress = (List<Map<String, Object>>) partyInfo.get("listAddress");
                if (UtilValidate.isNotEmpty(listAddress)) {
                    for (Map<String, Object> m : listAddress) {
                        if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
                            party.put("address1", (String) m.get("address1"));
                            break;
                        }
                    }
                }
                distributors.add(party);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dummy != null) {
                dummy.close();
            }
        }
        result.put("listIterator", distributors);
        result.put("TotalRows", TotalRows);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createDistributorGroup(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
//
//        Security security = ctx.getSecurity();
//        if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_NEW")) {
//            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
//        }
//
        List<String> distributorList = (List<String>) context.get("distributorList");
        String description = (String) context.get("description");
        String name = (String) context.get("productStoreGroupName");
        List<String> errorMessageList = FastList.newInstance();

        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        if (UtilValidate.isEmpty(distributorList)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouNotYetChooseRow", locale));
        }

        String psgId = "PSG" + delegator.getNextSeqId("ProductStoreGroup");
        GenericValue psg = delegator.makeValue("ProductStoreGroup", UtilMisc.<String, Object>toMap(
                "productStoreGroupId", psgId,
                "productStoreGroupName", name,
                "description", description, //Mô tả
                "productStoreGroupTypeId", "SHIPPING")); //Trạng thái
        try {
            delegator.create(psg);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create Trip entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }

        // update delivery status
        if(!DistributorGroupWorker.updateProductStoreGroupId(delegator, distributorList, psgId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }
        successResult.put("productStoreGroupId", psgId);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> updateDistributorGroup(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
//
//        Security security = ctx.getSecurity();
//        if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_NEW")) {
//            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
//        }
//
        List<String> distributorList = (List<String>) context.get("distributorList");
        String description = (String) context.get("description");
        String psgId = (String) context.get("productStoreGroupId");
        List<String> errorMessageList = FastList.newInstance();

        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        if (UtilValidate.isEmpty(distributorList)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouNotYetChooseRow", locale));
        }

        // update delivery status
        if(!DistributorGroupWorker.removeProductStoreGroupId(delegator, psgId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }
        // update delivery status
        if(!DistributorGroupWorker.updateProductStoreGroupId(delegator, distributorList, psgId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }
        successResult.put("productStoreGroupId", psgId);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetProductStoreGroup(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator del = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
        try {
            List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            EntityCondition tmpCondition = null;
            tmpCondition = EntityCondition.makeCondition(listAllConditions);
            List<GenericValue> productStoreGroup  = del.findList("ProductStoreGroup", tmpCondition, null, listSortFields, opts, false);
            listIterator.addAll(productStoreGroup);
        } catch (Exception e) {
            e.printStackTrace();
            successResult = ServiceUtil.returnError("error");
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }
}
