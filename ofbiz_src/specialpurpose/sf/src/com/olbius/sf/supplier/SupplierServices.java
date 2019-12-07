package com.olbius.sf.supplier;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;


public class SupplierServices {
	public static final String module = SupplierServices.class.getName();
    public static final String resource = "sfUiLabels";
    public static final String resourceError = "sfNotificationUiLabels";

	public static Map<String, Object> findSupplier(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        // set the page parameters
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }
        result.put("viewIndex", Integer.valueOf(viewIndex));

        int viewSize = 20;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 20;
        }
        result.put("viewSize", Integer.valueOf(viewSize));

        // get the lookup flag
        String lookupFlag = (String) context.get("lookupFlag");

        // blank param list
        String paramList = "";

        List<GenericValue> partyList = null;
        int partyListSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        String showAll = (context.get("showAll") != null ? (String) context.get("showAll") : "N");
        paramList = paramList + "&lookupFlag=" + lookupFlag + "&showAll=" + showAll;

        // create the dynamic view entity
        DynamicViewEntity dynamicView = new DynamicViewEntity();

        // default view settings
        dynamicView.addMemberEntity("PT", "Party");
        dynamicView.addAlias("PT", "partyId");
        dynamicView.addAlias("PT", "statusId");
        dynamicView.addAlias("PT", "partyTypeId");
        dynamicView.addAlias("PT", "createdDate");
        dynamicView.addAlias("PT", "lastModifiedDate");
        dynamicView.addRelation("many", "", "UserLogin", ModelKeyMap.makeKeyMapList("partyId"));

        // define the main condition & expression list
        List<EntityCondition> andExprs = FastList.newInstance();
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // fields we need to select; will be used to set distinct
        fieldsToSelect.add("partyId");
        fieldsToSelect.add("statusId");
        fieldsToSelect.add("createdDate");

        // get the params
        String partyId = (String) context.get("partyId");
        String statusId = (String) context.get("statusId");
        String userLoginId = (String) context.get("userLoginId");
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        String facebookId = (String) context.get("facebookId");

        // check for a partyId
        if (UtilValidate.isNotEmpty(partyId)) {
            paramList = paramList + "&partyId=" + partyId;
            andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+partyId+"%")));
        }

        // now the statusId - send ANY for all statuses; leave null for just enabled; or pass a specific status
        if (statusId != null) {
            paramList = paramList + "&statusId=" + statusId;
            if (!"ANY".equalsIgnoreCase(statusId)) {
                andExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
            }
        } else {
            // NOTE: _must_ explicitly allow null as it is not included in a not equal in many databases... odd but true
            andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")));
        }
        // ----
        // UserLogin Fields
        // ----

        // filter on user login
        if (UtilValidate.isNotEmpty(userLoginId)) {
            paramList = paramList + "&userLoginId=" + userLoginId;

            // modify the dynamic view
            dynamicView.addMemberEntity("UL", "UserLogin");
            dynamicView.addAlias("UL", "userLoginId");
            dynamicView.addViewLink("PT", "UL", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

            // add the expr
            andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+userLoginId+"%")));

            fieldsToSelect.add("userLoginId");
        }

        // ----
        // Supplier Fields
        // ----

        // modify the dynamic view
        dynamicView.addMemberEntity("SF", "SellerFriend");
        dynamicView.addAlias("SF", "firstName");
        dynamicView.addAlias("SF", "facebookId");
        dynamicView.addAlias("SF", "lastName");
        dynamicView.addViewLink("PT", "SF", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

        fieldsToSelect.add("firstName");
        fieldsToSelect.add("lastName");
        fieldsToSelect.add("facebookId");
        orderBy.add("facebookId");
        orderBy.add("lastName");
        orderBy.add("firstName");

        // filter on firstName
        if (UtilValidate.isNotEmpty(firstName)) {
            paramList = paramList + "&firstName=" + firstName;
            andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+firstName+"%")));
        }

        // filter on lastName
        if (UtilValidate.isNotEmpty(lastName)) {
            paramList = paramList + "&lastName=" + lastName;
            andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+lastName+"%")));
        }

        // filter on facebookId
        if (UtilValidate.isNotEmpty(facebookId)) {
            paramList = paramList + "&facebookId=" + facebookId;
            andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facebookId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+facebookId+"%")));
        }

        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyTypeId"), EntityOperator.LIKE, EntityFunction.UPPER("SUPPLIER")));
        // build the main condition
        if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);

        Debug.logInfo("In findParty mainCond=" + mainCond, module);

        String sortField = (String) context.get("sortField");
        if(UtilValidate.isNotEmpty(sortField)){
            orderBy.add(sortField);
        }

        // do the lookup
        if (mainCond != null || "Y".equals(showAll)) {
            try {
                // get the indexes for the partial list
                lowIndex = viewIndex * viewSize + 1;
                highIndex = (viewIndex + 1) * viewSize;

                // set distinct on so we only get one row per order
                EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, highIndex, true);
                // using list iterator
                EntityListIterator pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);

                // get the partial list for this page
                partyList = pli.getPartialList(lowIndex, viewSize);

                // attempt to get the full size
                partyListSize = pli.getResultsSizeAfterPartialList();
                if (highIndex > partyListSize) {
                    highIndex = partyListSize;
                }

                // close the list iterator
                pli.close();
            } catch (GenericEntityException e) {
                String errMsg = "Failure in party find operation, rolling back transaction: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "PartyLookupPartyError",
                        UtilMisc.toMap("errMessage", e.toString()), locale));
            }
        } else {
            partyListSize = 0;
        }

        if (partyList == null) partyList = FastList.newInstance();
        result.put("partyList", partyList);
        result.put("partyListSize", Integer.valueOf(partyListSize));
        result.put("paramList", paramList);
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("lowIndex", Integer.valueOf(lowIndex));

        return result;
    }
}
