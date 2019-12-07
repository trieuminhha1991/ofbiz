package com.olbius.salesmtl;

import com.olbius.salesmtl.util.RouteUtils;
import com.olbius.salesmtl.util.SupUtil;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.util.*;

public class AgentServices {
	public static final String module = AgentServices.class.getName();
	public static final String resource = "widgetUiLabels";
	public static final String resourceError = "widgetErrorUiLabels";
	public static final String resource_error = "OrderUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";

    @SuppressWarnings("unchecked")
	public static Map<String, Object> setAgentStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String statusId = (String) context.get("statusId");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = ServiceUtil.returnSuccess();
        try {
            GenericValue party = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
            if (UtilValidate.isEmpty(party)) {
                ServiceUtil.returnError("Error: setAgentStatus");
            }
            if (party.get("statusId") == null) { // old records
                party.set("statusId", "PARTY_ENABLED");
            } else party.set("statusId", statusId);
            party.store();
            ctx.getDispatcher().runSync("setPartyStatus", context);

            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("partyId", partyId));
            conds.add(EntityCondition.makeCondition("statusId", "PARTY_APPROVED"));
            List<GenericValue> temporaryParties = delegator.findList("TemporaryParty", EntityCondition.makeCondition(conds), null,null,null,false);
            if (UtilValidate.isNotEmpty(temporaryParties)) {
                GenericValue temporaryParty = temporaryParties.get(0);
                String routeId = temporaryParty.getString("routeId");
                if (UtilValidate.isNotEmpty(routeId)) {
                    RouteUtils.createRouteStore(delegator,partyId,routeId);
                }
            }
            // enabled customer to route.
            conds.clear();
            conds.add(EntityCondition.makeCondition("customerId", partyId));
            conds.add(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null));
            List<GenericValue> routeCustomers = delegator.findList("RouteCustomer", EntityCondition.makeCondition(conds), null,null,null,false);
            for (GenericValue routeCustomer : routeCustomers) {
                String routeId = routeCustomer.getString("routeId");
                GenericValue route = delegator.findOne("Route", UtilMisc.toMap("routeId", routeId), false);
                if (UtilValidate.isNotEmpty(route) && SupUtil.ROUTE_ENABLED.equals(route.getString("statusId"))){
                    routeCustomer.set("thruDate", null);
                    delegator.store(routeCustomer);
                }
                SupUtil.generateSaleRouteScheduleDetailDateForOneCustomerAdded(delegator, routeId, partyId);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                "party.update.write_failure", new Object[] { e.getMessage() }, locale));
    } catch (GenericServiceException e) {
            Debug.logError(e, e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "party.update.write_failure", new Object[] { e.getMessage() }, locale));
        }
        return results;
    }
}
