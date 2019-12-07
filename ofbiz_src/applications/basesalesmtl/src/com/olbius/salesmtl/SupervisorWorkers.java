package com.olbius.salesmtl;

import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 3/21/18.
 */
public class SupervisorWorkers {
	public static final String module = SupervisorWorkers.class.getName();
	
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListAgentsInner(LocalDispatcher localDispatcher, Delegator delegator, GenericValue userLogin, Map<String, String[]> parameters, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) throws GenericEntityException, GenericServiceException {
    	Map<String, Object> result = FastMap.newInstance();
    	boolean isSearch = true;
    	
    	String userLoginPartyId = userLogin.getString("partyId");
    	//String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        opts.setDistinct(true);

        Debug.log(module + "::getListAgentsInner, userLoginPartyId = " + userLoginPartyId);
        if (parameters.containsKey("sD")) {
            if ("N".equals(parameters.get("sD")[0])) {
                listAllConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
                Debug.log(module + "::getListAgentsInner, add condition with PARTY_ENABLED");
            }
        }

        if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
            //listAllConditions.add(EntityCondition.makeCondition("distributorId", EntityJoinOperator.IN, PartyWorker.getDistributorIdsBySup(delegator, userLoginPartyId)));
        	listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityJoinOperator.EQUALS,userLoginPartyId));
        	 Debug.log(module + "::getListAgentsInner, is SalesSup");
        	 
        } else if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
            listAllConditions.add(EntityCondition.makeCondition("salesmanId", userLoginPartyId));
            Debug.log(module + "::getListAgentsInner, is Salesman");
        } else if (SalesPartyUtil.isDistributor(delegator, userLoginPartyId)) {
            listAllConditions.add(EntityCondition.makeCondition("distributorId", userLoginPartyId));
            Debug.log(module + "::getListAgentsInner, is Distributor");
        } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
        	List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
        	if (UtilValidate.isEmpty(listSupIds)) {
        		isSearch = false;
        	} else if (listSupIds.size() == 1) {
        		listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
        	} else {
        		listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
        	}
        } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
        	List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
        	if (UtilValidate.isEmpty(listSupIds)) {
        		isSearch = false;
        	} else if (listSupIds.size() == 1) {
        		listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
        	} else {
        		listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
        	}
        } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
        	List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
        	if (UtilValidate.isEmpty(listSupIds)) {
        		isSearch = false;
        	} else if (listSupIds.size() == 1) {
        		listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
        	} else {
        		listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
        	}
        } else if (SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
            listAllConditions.add(EntityCondition.makeCondition("distributorId", EntityJoinOperator.IN, PartyWorker.getDisOfRetailSalesAdminGT(delegator, userLoginPartyId)));
        } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)) {
        	//OLD
        	//else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId)) {
            //listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityJoinOperator.IN, SupUtil.getManagerIdsOfChildDeptBySalesManager(delegator,userLogin)));
        	//} end.OLD
        	
        	String currentOrgId = SalesUtil.getCurrentOrganization(delegator, userLogin);
        	List<String> listDistIds = PartyWorker.getDistributorByOrg(delegator, currentOrgId , Boolean.FALSE);
        	if (UtilValidate.isEmpty(listDistIds)) {
        		isSearch = false;
        	} else if (listDistIds.size() == 1) {
        		listAllConditions.add(EntityCondition.makeCondition("distributorId", listDistIds.get(0)));
        	} else {
        		listAllConditions.add(EntityCondition.makeCondition("distributorId", EntityOperator.IN, listDistIds));
        	}
        } else {
        	isSearch = false;
        }
        
        // if don't have permission query data then return empty
        if (!isSearch) return result;

        // get data
        String distributorId = SalesUtil.getParameter(parameters, "partyIdFrom");
        if (UtilValidate.isNotEmpty(distributorId)) {
            listAllConditions.add(EntityCondition.makeCondition("distributorId", distributorId));
        }
        
        List<Map<String, Object>> agents = FastList.newInstance();
        //Get data
        List<GenericValue> listIterator = FastList.newInstance();
        String routeId = SalesUtil.getParameter(parameters, "routeId");
        if (UtilValidate.isNotEmpty(routeId)) {
            listAllConditions.add(EntityUtil.getFilterByDateExpr());
            listAllConditions.add(EntityCondition.makeCondition("routeId", routeId));
            listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "PartyCustomerAndRouteFullDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } else {
        	listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyTypeId",null), EntityJoinOperator.OR, EntityCondition.makeCondition("partyTypeId",EntityJoinOperator.NOT_EQUAL,"END_CUSTOMER")));
            listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "PartyCustomerFullDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        }
        result.put("listIterator", listIterator);
        return result;
    }
}
