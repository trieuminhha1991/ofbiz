/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.order.order;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;

/**
 * Session object for keeping track of the list of orders.
 * The state of the list is preserved here instead of
 * via url parameters, which can get messy.  There
 * are three types of state:  Order State, Order Type,
 * and pagination position.
 *
 * Also provides convenience methods for retrieving
 * the right set of data for a particular state.
 *
 * TODO: this can be generalized to use a set of State
 * objects, including Pagination. Think about design
 * patterns in Fowler.
 */
@SuppressWarnings("serial")
public class OrderListState implements Serializable {

    public static final String module = OrderListState.class.getName();
    public static final String SESSION_KEY = "__ORDER_LIST_STATUS__";
    public static final String SESSION_KEY_SECOND = "__ORDER_LIST_STATUS_SECOND__";
    public static final String VIEW_SIZE_PARAM = "viewSize";
    public static final String VIEW_INDEX_PARAM = "viewIndex";
    public static final String VIEW_SIZE = "VIEW_SIZE";
    public static final String VIEW_INDEX = "VIEW_INDEX";
    
    // state variables
    protected int viewSize;
    protected int viewIndex;
    protected Map<String, String> orderStatusState;
    protected Map<String, String> orderTypeState;
    protected Map<String, String> orderFilterState;
    protected int orderListSize;

    // parameter to ID maps
    protected static final Map<String, String> parameterToOrderStatusId;
    protected static final Map<String, String> parameterToOrderTypeId;
    protected static final Map<String, String> parameterToFilterId;
    static {
        Map<String, String> map = FastMap.newInstance();
        map.put("viewcompleted", "ORDER_COMPLETED");
        map.put("viewcancelled", "ORDER_CANCELLED");
        map.put("viewrejected", "ORDER_REJECTED");
        map.put("viewapproved", "ORDER_APPROVED");
        map.put("viewcreated", "ORDER_CREATED");
        map.put("viewprocessing", "ORDER_PROCESSING");
        //map.put("viewsent", "ORDER_SENT");
        map.put("viewhold", "ORDER_HOLD");
        map.put("viewnppapproved", "ORDER_NPPAPPROVED");
        map.put("viewsupapproved", "ORDER_SUPAPPROVED");
        map.put("viewsadapproved", "ORDER_SADAPPROVED");
        parameterToOrderStatusId = map;

        map = FastMap.newInstance();
        map.put("view_SALES_ORDER", "SALES_ORDER");
        map.put("view_PURCHASE_ORDER", "PURCHASE_ORDER");
        parameterToOrderTypeId = map;

        map = FastMap.newInstance();
        map.put("filterInventoryProblems", "filterInventoryProblems");
        map.put("filterAuthProblems", "filterAuthProblems");
        map.put("filterPartiallyReceivedPOs", "filterPartiallyReceivedPOs");
        map.put("filterPOsOpenPastTheirETA", "filterPOsOpenPastTheirETA");
        map.put("filterPOsWithRejectedItems", "filterPOsWithRejectedItems");
        parameterToFilterId = map;
    }

    //=============   Initialization and Request methods   ===================//

    /**
     * Initializes the order list state with default values. Do not use directly,
     * instead use getInstance().
     */
    protected OrderListState() {
        viewSize = 10;
        viewIndex = 0;
        orderStatusState = FastMap.newInstance();
        orderTypeState = FastMap.newInstance();
        orderFilterState = FastMap.newInstance();

        // defaults (TODO: configuration)
        orderStatusState.put("viewcreated", "Y");
        orderStatusState.put("viewprocessing", "Y");
        orderStatusState.put("viewapproved", "Y");
        orderStatusState.put("viewhold", "N");
        orderStatusState.put("viewcompleted", "N");
        //orderStatusState.put("viewsent", "N");
        orderStatusState.put("viewrejected", "N");
        orderStatusState.put("viewcancelled", "N");
        orderStatusState.put("viewnppapproved", "Y");
        orderStatusState.put("viewsupapproved", "Y");
        orderStatusState.put("viewsadapproved", "Y");
        orderTypeState.put("view_SALES_ORDER", "Y");
    }

    /**
     * Retrieves the current user's OrderListState from the session
     * or creates a new one with defaults.
     */
    public static OrderListState getInstance(HttpServletRequest request) {
        HttpSession session = request.getSession();
        OrderListState status = (OrderListState) session.getAttribute(SESSION_KEY);
        if (status == null) {
            status = new OrderListState();
            session.setAttribute(SESSION_KEY, status);
        }
        return status;
    }
    
    public static OrderListState getInstanceSecond(HttpServletRequest request) {
        HttpSession session = request.getSession();
        OrderListState status = (OrderListState) session.getAttribute(SESSION_KEY_SECOND);
        if (status == null) {
            status = new OrderListState();
            session.setAttribute(SESSION_KEY_SECOND, status);
        }
        return status;
    }

    /**
     * Given a request, decides what state to change.  If a parameter changeStatusAndTypeState
     * is present with value "Y", the status and type state will be updated.  Otherwise, if the
     * viewIndex and viewSize parameters are present, the pagination changes.
     */
    public void update(HttpServletRequest request) {
        if ("Y".equals(request.getParameter("changeStatusAndTypeState"))) {
            changeOrderListStates(request);
        } else {
            String viewSizeParam = request.getParameter(VIEW_SIZE_PARAM);
            String viewIndexParam = request.getParameter(VIEW_INDEX_PARAM);
            if (UtilValidate.isEmpty(viewSizeParam)) {
            	viewSizeParam = request.getParameter(VIEW_SIZE);
            }
            if (UtilValidate.isEmpty(viewIndexParam)) {
            	viewIndexParam = request.getParameter(VIEW_INDEX);
            }
            if (!UtilValidate.isEmpty(viewSizeParam) && !UtilValidate.isEmpty(viewIndexParam))
                changePaginationState(viewSizeParam, viewIndexParam);
        }
    }
    
    public void updateJQ (List<String> listStatusName, String viewIndex, String viewSize) {
    	if (listStatusName != null && listStatusName.size() > 0) {
            changeOrderListStatesJQ(listStatusName);
        } else {
            if (!UtilValidate.isEmpty(viewSize) && !UtilValidate.isEmpty(viewIndex))
                changePaginationState(viewSize, viewIndex);
        }
    }
    
    private void changeOrderListStatesJQ (List<String> listStatusName) {
        for (String param : parameterToOrderStatusId.keySet()) {
            if (listStatusName.contains(param)) {
                orderStatusState.put(param, "Y");
            } else {
                orderStatusState.put(param, "N");
            }
        }
        for (String param : parameterToOrderTypeId.keySet()) {
            if (listStatusName.contains(param)) {
                orderTypeState.put(param, "Y");
            } else {
                orderTypeState.put(param, "N");
            }
        }
        for (String param : parameterToFilterId.keySet()) {
            if (listStatusName.contains(param)) {
                orderFilterState.put(param, "Y");
            } else {
                orderFilterState.put(param, "N");
            }
        }
        viewIndex = 0;
    }
    
    private void changePaginationState(String viewSizeParam, String viewIndexParam) {
        try {
            viewSize = Integer.parseInt(viewSizeParam);
            viewIndex = Integer.parseInt(viewIndexParam);
        } catch (NumberFormatException e) {
            Debug.logWarning("Values of " + VIEW_SIZE_PARAM + " ["+viewSizeParam+"] and " + VIEW_INDEX_PARAM + " ["+viewIndexParam+"] must both be Integers. Not paginating order list.", module);
        }
    }

    private void changeOrderListStates(HttpServletRequest request) {
        for (String param : parameterToOrderStatusId.keySet()) {
            String value = request.getParameter(param);
            if ("Y".equals(value)) {
                orderStatusState.put(param, "Y");
            } else {
                orderStatusState.put(param, "N");
            }
        }
        for (String param : parameterToOrderTypeId.keySet()) {
            String value = request.getParameter(param);
            if ("Y".equals(value)) {
                orderTypeState.put(param, "Y");
            } else {
                orderTypeState.put(param, "N");
            }
        }
        for (String param : parameterToFilterId.keySet()) {
            String value = request.getParameter(param);
            if ("Y".equals(value)) {
                orderFilterState.put(param, "Y");
            } else {
                orderFilterState.put(param, "N");
            }
        }
        viewIndex = 0;
    }


    //==============   Get and Set methods   =================//


    public Map<String, String> getOrderStatusState() { return orderStatusState; }
    public Map<String, String> getOrderTypeState() { return orderTypeState; }
    public Map<String, String> getorderFilterState() { return orderFilterState; }

    public void setStatus(String param, boolean b) { orderStatusState.put(param, (b ? "Y" : "N")); }
    public void setType(String param, boolean b) { orderTypeState.put(param, (b ? "Y" : "N")); }
    
    public boolean hasStatus(String param) { return ("Y".equals(orderStatusState.get(param))); }
    public boolean hasType(String param) { return ("Y".equals(orderTypeState.get(param))); }
    public boolean hasFilter(String param) { return ("Y".equals(orderFilterState.get(param))); }

    public boolean hasAllStatus() {
        for (Iterator<String> iter = orderStatusState.values().iterator(); iter.hasNext();) {
            if (!"Y".equals(iter.next())) return false;
        }
        return true;
    }

    public int getViewSize() { return viewSize; }
    public int getViewIndex() { return viewIndex; }
    public int getSize() { return orderListSize; }

    public boolean hasPrevious() { return (viewIndex > 0); }
    public boolean hasNext() { return (viewIndex < getSize() / viewSize); }

    /**
     * Get the OrderHeaders corresponding to the state.
     */
    public List<GenericValue> getOrders(String facilityId, Timestamp filterDate, Delegator delegator) throws GenericEntityException {
        List<EntityCondition> allConditions = FastList.newInstance();

        if (facilityId != null) {
            allConditions.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
        }

        if (filterDate != null) {
            List<EntityCondition> andExprs = FastList.newInstance();
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(filterDate)));
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(filterDate)));
            allConditions.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
        }

        List<EntityCondition> statusConditions = FastList.newInstance();
        for (String status : orderStatusState.keySet()) {
            if (!hasStatus(status)) continue;
            statusConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameterToOrderStatusId.get(status)));
        }
        List<EntityCondition> typeConditions = FastList.newInstance();
        for (String type : orderTypeState.keySet()) {
            if (!hasType(type)) continue;
            typeConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, parameterToOrderTypeId.get(type)));
        }

        EntityCondition statusConditionsList = EntityCondition.makeCondition(statusConditions,  EntityOperator.OR);
        EntityCondition typeConditionsList = EntityCondition.makeCondition(typeConditions, EntityOperator.OR);
        if (statusConditions.size() > 0) {
            allConditions.add(statusConditionsList);
        }
        if (typeConditions.size() > 0) {
            allConditions.add(typeConditionsList);
        }

        EntityCondition queryConditionsList = EntityCondition.makeCondition(allConditions, EntityOperator.AND);
        EntityFindOptions options = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
        options.setMaxRows(viewSize * (viewIndex + 1));
        EntityListIterator iterator = delegator.find("OrderHeader", queryConditionsList, null, null, UtilMisc.toList("orderDate DESC"), options);
        orderListSize = iterator.getResultsSizeAfterPartialList();
        // get subset corresponding to pagination state
        List<GenericValue> orders = iterator.getPartialList(viewSize * viewIndex, viewSize);
        
        iterator.close();
        //Debug.logInfo("### size of list: " + orderListSize, module);
        return orders;
    }

    public List<GenericValue> getListOrders(String facilityId, Timestamp filterDate, Delegator delegator, List<GenericValue> productStoreIds, String productStoreId) throws GenericEntityException {
        List<EntityCondition> allConditions = FastList.newInstance();

        if (facilityId != null) {
            allConditions.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
        }

        if (filterDate != null) {
            List<EntityCondition> andExprs = FastList.newInstance();
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(filterDate)));
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(filterDate)));
            allConditions.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
        }

        List<EntityCondition> statusConditions = FastList.newInstance();
        for (String status : orderStatusState.keySet()) {
            if (!hasStatus(status)) continue;
            statusConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameterToOrderStatusId.get(status)));
        }
        List<EntityCondition> typeConditions = FastList.newInstance();
        for (String type : orderTypeState.keySet()) {
            if (!hasType(type)) continue;
            typeConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, parameterToOrderTypeId.get(type)));
        }

        EntityCondition statusConditionsList = EntityCondition.makeCondition(statusConditions,  EntityOperator.OR);
        EntityCondition typeConditionsList = EntityCondition.makeCondition(typeConditions, EntityOperator.OR);
        if (statusConditions.size() > 0) {
            allConditions.add(statusConditionsList);
        }
        if (typeConditions.size() > 0) {
            allConditions.add(typeConditionsList);
        }

        EntityCondition queryConditionsList = EntityCondition.makeCondition(allConditions, EntityOperator.AND);
        EntityFindOptions options = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
        EntityListIterator iterator = delegator.find("OrderHeader", queryConditionsList, null, null, UtilMisc.toList("orderDate DESC"), options);

        // get subset corresponding to pagination state
        List<GenericValue> allOrders = iterator.getCompleteList();
        List<GenericValue> orders = iterator.getPartialList(viewSize * viewIndex, viewSize);
        int size = viewSize * viewIndex;
        orderListSize = iterator.getResultsSizeAfterPartialList();
        iterator.close();
        List<GenericValue> listFilter = new ArrayList<GenericValue>();
        if ((productStoreId == null || productStoreId.equals("")) && productStoreIds.isEmpty()){
        	orders = new ArrayList<GenericValue>();
        } else {
        	if ((productStoreId == null || productStoreId.equals("")) && !productStoreIds.isEmpty()){
        		for (GenericValue storeId : productStoreIds){
    	        	if (!allOrders.isEmpty()){
    			        for (GenericValue value : allOrders) {
    			        	if ((String)value.get("productStoreId") != null){
    			        		String PSId = (String)value.get("productStoreId");
    			        		String PSId2 = (String)storeId.get("productStoreId");
        			        	if (PSId.equals(PSId2)){
        							listFilter.add(value);
        						}
    			        	}
    					} 
	        		}
    	        }
        	} else {
        		if (!allOrders.isEmpty()){
			        for (GenericValue value : allOrders) {
			        	if ((String)value.get("productStoreId") != null){
			        		String PSId = (String)value.get("productStoreId");
    			        	if (PSId.equals(productStoreId)){
    							listFilter.add(value);
    						}
			        	}
					} 
        		}
        	}
        }
        orderListSize = listFilter.size();
        orders = new ArrayList<GenericValue>();
        if (listFilter.size() > size){
        	if (size != 0){
        		if ((listFilter.size() - size) >= viewSize){
			    	for (int i = size; i < size + viewSize; i ++){
			    		orders.add(listFilter.get(i));
			    	}
        		} else {
        			for (int i = size; i < listFilter.size(); i ++){
			    		orders.add(listFilter.get(i));
			    	}
        		}
        	} else {
        		if (listFilter.size() >= viewSize){
		    		for (int i = 0; i < viewSize; i ++){
			    		orders.add(listFilter.get(i));
			    	}
        		} else {
        			for (int i = 0; i < listFilter.size(); i ++){
			    		orders.add(listFilter.get(i));
			    	}
        		}
        	}
        }
        else {
        	orders.addAll(listFilter);
        }
//        orderListSize = orders.size();
        //Debug.logInfo("### size of list: " + orderListSize, module);
        return orders;
    }
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder("OrderListState:\n\t");
        buff.append("viewIndex=").append(viewIndex).append(", viewSize=").append(viewSize).append("\n\t");
        buff.append(getOrderStatusState().toString()).append("\n\t");
        buff.append(getOrderTypeState().toString()).append("\n\t");
        buff.append(getorderFilterState().toString()).append("\n\t");
        return buff.toString();
    }
    
    /**
     * Get list order that product store id of orderHeader IN productStoreIds.
     * @param facilityId
     * @param filterDate
     * @param delegator
     * @param productStores	List product store need get order
     * @param productStoreId	Only this product store need get order
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> getListOrdersAdvance(String facilityId, Timestamp filterDate, Delegator delegator, List<GenericValue> productStores, String productStoreId) throws GenericEntityException {
        List<EntityCondition> allConditions = FastList.newInstance();

        if (facilityId != null) {
            allConditions.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
        }

        if (filterDate != null) {
            List<EntityCondition> andExprs = FastList.newInstance();
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(filterDate)));
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(filterDate)));
            allConditions.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
        }

        List<EntityCondition> statusConditions = FastList.newInstance();
        for (String status : orderStatusState.keySet()) {
            if (!hasStatus(status)) continue;
            statusConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameterToOrderStatusId.get(status)));
        }
        List<EntityCondition> typeConditions = FastList.newInstance();
        for (String type : orderTypeState.keySet()) {
            if (!hasType(type)) continue;
            typeConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, parameterToOrderTypeId.get(type)));
        }

        EntityCondition statusConditionsList = EntityCondition.makeCondition(statusConditions, EntityOperator.OR);
        EntityCondition typeConditionsList = EntityCondition.makeCondition(typeConditions, EntityOperator.OR);
        if (statusConditions.size() > 0) {
            allConditions.add(statusConditionsList);
        }
        if (typeConditions.size() > 0) {
            allConditions.add(typeConditionsList);
        }
        
        if ((productStoreId == null || productStoreId.equals("")) && !productStores.isEmpty()) {
        	// convert List<GenericValue> productStores to List<String> productStoreIds
        	List<String> productStoreIds = new ArrayList<String>();
        	for (GenericValue productStoreItem : productStores) {
        		if (productStoreItem.containsKey("productStoreId") && !productStoreIds.contains(productStoreItem.getString("productStoreId"))) {
        			productStoreIds.add(productStoreItem.getString("productStoreId"));
        		}
        	}
        	EntityCondition productStoreCond = EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds);
        	allConditions.add(productStoreCond);
        } else if (productStoreId != null && !productStoreId.equals("")) {
        	EntityCondition productStoreCond = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId);
        	allConditions.add(productStoreCond);
        }

        EntityCondition queryConditionsList = EntityCondition.makeCondition(allConditions, EntityOperator.AND);
        EntityFindOptions options = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
        EntityListIterator iterator = delegator.find("OrderHeader", queryConditionsList, null, null, UtilMisc.toList("orderDate DESC"), options);
        
        // get subset corresponding to pagination state
        int startIndex = viewSize * viewIndex + 1;
        List<GenericValue> orders = iterator.getPartialList(startIndex, viewSize);
        orderListSize = iterator.getResultsSizeAfterPartialList();
        iterator.close();
        return orders;
    }
    
    /**
     * Get list order that create by salesman and manage by sales administrator.
     * @param delegator
     * @param userLogin
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> getListOrdersSm(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
    	List<GenericValue> orderList = new ArrayList<GenericValue>();
    	
    	// Dynamic view entity
		DynamicViewEntity dve = new DynamicViewEntity();
		dve.addMemberEntity("PSRUL", "ProductStoreRole");
		dve.addMemberEntity("PS", "ProductStore");
		dve.addMemberEntity("PSRCU", "ProductStoreRole");
		dve.addMemberEntity("PR", "PartyRole");
		dve.addMemberEntity("ORLE", "OrderRole");
		dve.addMemberEntity("OH", "OrderHeader");
		dve.addAlias("PSRUL", "PSRUL_partyId", "partyId", null, false, false, null);
		dve.addAlias("PSRUL", "PSRUL_roleTypeId", "roleTypeId", null, false, false, null);
		dve.addAlias("PSRCU", "PSRCU_roleTypeId", "roleTypeId", null, false, false, null);
		dve.addAlias("PR", "PR_roleTypeId", "roleTypeId", null, false, false, null);
		dve.addAlias("ORLE", "ORLE_roleTypeId", "roleTypeId", null, false, false, null);
		dve.addAliasAll("OH", "", null);
		dve.addViewLink("PSRUL", "PS", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));
		dve.addViewLink("PS", "PSRCU", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));
		dve.addViewLink("PSRCU", "PR", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("partyId", "partyId")));
		dve.addViewLink("PR", "ORLE", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("partyId", "partyId")));
		dve.addViewLink("ORLE", "OH", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("PSRUL_partyId", EntityOperator.EQUALS, userLogin.getString("partyId")));
		conditions.add(EntityCondition.makeCondition("PSRUL_roleTypeId", EntityOperator.EQUALS, "SALES_ADMIN"));
		conditions.add(EntityCondition.makeCondition("PSRCU_roleTypeId", EntityOperator.EQUALS, "CUSTOMER"));
		conditions.add(EntityCondition.makeCondition("PR_roleTypeId", EntityOperator.EQUALS, "DELYS_DISTRIBUTOR"));
		conditions.add(EntityCondition.makeCondition("ORLE_roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
		
		List<EntityCondition> statusConditions = FastList.newInstance();
        for (String status : orderStatusState.keySet()) {
            if (!hasStatus(status)) continue;
            statusConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameterToOrderStatusId.get(status)));
        }
        List<EntityCondition> typeConditions = FastList.newInstance();
        for (String type : orderTypeState.keySet()) {
            if (!hasType(type)) continue;
            typeConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, parameterToOrderTypeId.get(type)));
        }

        EntityCondition statusConditionsList = EntityCondition.makeCondition(statusConditions, EntityOperator.OR);
        EntityCondition typeConditionsList = EntityCondition.makeCondition(typeConditions, EntityOperator.OR);
        if (statusConditions.size() > 0) {
        	conditions.add(statusConditionsList);
        }
        if (typeConditions.size() > 0) {
        	conditions.add(typeConditionsList);
        }
		
		EntityCondition cond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
		// list of fields to select (initial list)
		List<String> fieldsToSelect = FastList.newInstance();
		fieldsToSelect.add("orderId");
		fieldsToSelect.add("orderTypeId");
		fieldsToSelect.add("orderName");
		fieldsToSelect.add("externalId");
		fieldsToSelect.add("salesChannelEnumId");
		fieldsToSelect.add("orderDate");
		fieldsToSelect.add("priority");
		fieldsToSelect.add("entryDate");
		fieldsToSelect.add("pickSheetPrintedDate");
		fieldsToSelect.add("visitId");
		fieldsToSelect.add("statusId");
		fieldsToSelect.add("createdBy");
		fieldsToSelect.add("firstAttemptOrderId");
		fieldsToSelect.add("currencyUom");
		fieldsToSelect.add("syncStatusId");
		fieldsToSelect.add("billingAccountId");
		fieldsToSelect.add("originFacilityId");
		fieldsToSelect.add("webSiteId");
		fieldsToSelect.add("productStoreId");
		fieldsToSelect.add("terminalId");
		fieldsToSelect.add("transactionId");
		fieldsToSelect.add("autoOrderShoppingListId");
		fieldsToSelect.add("needsInventoryIssuance");
		fieldsToSelect.add("isRushOrder");
		fieldsToSelect.add("internalCode");
		fieldsToSelect.add("remainingSubTotal");
		fieldsToSelect.add("grandTotal");
		fieldsToSelect.add("isViewed");
		fieldsToSelect.add("invoicePerShipment");
		
		// sorting by order date newest first
		List<String> orderBy = UtilMisc.toList("-orderDate", "-orderId");
		
		// set distinct on so we only get one row per order
		EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
		
		// get the index for the partial list
		int lowIndex = viewSize * viewIndex + 1; //(((viewIndex - 1) * viewSize) + 1);
		int highIndex = viewSize * (viewIndex + 1);
		findOpts.setMaxRows(highIndex);
		
		EntityListIterator eli = null;
		int orderCount = 0;
		try {
			// do the lookup
			eli = delegator.findListIteratorByCondition(dve, cond, null, fieldsToSelect, orderBy, findOpts);
			orderCount = eli.getResultsSizeAfterPartialList();
			
			// get the partial list for this page
			eli.beforeFirst();
			if (orderCount > viewSize) {
				orderList = eli.getPartialList(lowIndex, viewSize);
			} else if (orderCount > 0) {
				orderList = eli.getCompleteList();
			}
			
			if (highIndex > orderCount) {
				highIndex = orderCount;
			}
	    } catch (GenericEntityException e) {
	        Debug.logError(e, module);
	    } finally {
	        if (eli != null) {
	            try {
	                eli.close();
	            } catch (GenericEntityException e) {
	                Debug.logWarning(e, e.getMessage(), module);
	            }
	        }
	    }
		orderListSize = orderCount;
    	return orderList;
    }
    
    /**
     * Get list order that product store id of orderHeader IN productStoreIds.
     * @param facilityId
     * @param filterDate
     * @param delegator
     * @param userLogin
     * @param productStores	List product store need get order
     * @param productStoreId	Only this product store need get order
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> getListOrdersPurchaseDis (String facilityId, Timestamp filterDate, Delegator delegator, GenericValue userLogin, List<GenericValue> productStores, String productStoreId) throws GenericEntityException {
    	List<GenericValue> orderList = new ArrayList<GenericValue>();
    	// Dynamic view entity
		DynamicViewEntity dve = new DynamicViewEntity();
		dve.addMemberEntity("OHAR", "OrderHeaderAndRoles");
		dve.addAliasAll("OHAR", "", null);
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId")));
		conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "PLACING_CUSTOMER"));
		
		if (facilityId != null) {
			conditions.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
		}
		
		if (filterDate != null) {
			List<EntityCondition> andExprs = FastList.newInstance();
			andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(filterDate)));
			andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(filterDate)));
			conditions.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
		}
		
		List<EntityCondition> statusConditions = FastList.newInstance();
        for (String status : orderStatusState.keySet()) {
            if (!hasStatus(status)) continue;
            statusConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameterToOrderStatusId.get(status)));
        }
        List<EntityCondition> typeConditions = FastList.newInstance();
        for (String type : orderTypeState.keySet()) {
            if (!hasType(type)) continue;
            typeConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, parameterToOrderTypeId.get(type)));
        }

        EntityCondition statusConditionsList = EntityCondition.makeCondition(statusConditions, EntityOperator.OR);
        EntityCondition typeConditionsList = EntityCondition.makeCondition(typeConditions, EntityOperator.OR);
        if (statusConditions.size() > 0) {
        	conditions.add(statusConditionsList);
        }
        if (typeConditions.size() > 0) {
        	conditions.add(typeConditionsList);
        }
        
        if ((productStoreId == null || productStoreId.equals("")) && !productStores.isEmpty()) {
	    	// convert List<GenericValue> productStores to List<String> productStoreIds
	    	List<String> productStoreIds = new ArrayList<String>();
	    	for (GenericValue productStoreItem : productStores) {
	    		if (productStoreItem.containsKey("productStoreId") && !productStoreIds.contains(productStoreItem.getString("productStoreId"))) {
	    			productStoreIds.add(productStoreItem.getString("productStoreId"));
	    		}
	    	}
	    	EntityCondition productStoreCond = EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds);
	    	conditions.add(productStoreCond);
	    } else if (productStoreId != null && !productStoreId.equals("")) {
	    	EntityCondition productStoreCond = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId);
	    	conditions.add(productStoreCond);
	    }
		
		EntityCondition cond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
		// list of fields to select (initial list)
		List<String> fieldsToSelect = FastList.newInstance();
		fieldsToSelect.add("orderId"); fieldsToSelect.add("orderTypeId"); fieldsToSelect.add("orderName"); fieldsToSelect.add("externalId");
		fieldsToSelect.add("salesChannelEnumId"); fieldsToSelect.add("orderDate"); fieldsToSelect.add("priority"); fieldsToSelect.add("entryDate");
		fieldsToSelect.add("pickSheetPrintedDate"); fieldsToSelect.add("visitId"); fieldsToSelect.add("statusId"); fieldsToSelect.add("createdBy");
		fieldsToSelect.add("firstAttemptOrderId"); fieldsToSelect.add("currencyUom"); fieldsToSelect.add("syncStatusId"); fieldsToSelect.add("billingAccountId");
		fieldsToSelect.add("originFacilityId"); fieldsToSelect.add("webSiteId"); fieldsToSelect.add("productStoreId"); fieldsToSelect.add("terminalId");
		fieldsToSelect.add("transactionId"); fieldsToSelect.add("autoOrderShoppingListId"); fieldsToSelect.add("needsInventoryIssuance"); fieldsToSelect.add("isRushOrder");
		fieldsToSelect.add("internalCode"); fieldsToSelect.add("remainingSubTotal"); fieldsToSelect.add("grandTotal"); fieldsToSelect.add("isViewed"); fieldsToSelect.add("invoicePerShipment");
		
		// sorting by order date newest first
		List<String> orderBy = UtilMisc.toList("-orderDate", "-orderId");
		
		// set distinct on so we only get one row per order
		EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
		
		// get the index for the partial list
		int lowIndex = viewSize * viewIndex + 1; //(((viewIndex - 1) * viewSize) + 1);
		int highIndex = viewSize * (viewIndex + 1);
		findOpts.setMaxRows(highIndex);
		
		EntityListIterator eli = null;
		int orderCount = 0;
		try {
			// do the lookup
			eli = delegator.findListIteratorByCondition(dve, cond, null, fieldsToSelect, orderBy, findOpts);
			orderCount = eli.getResultsSizeAfterPartialList();
			
			// get the partial list for this page
			eli.beforeFirst();
			if (orderCount > viewSize) {
				orderList = eli.getPartialList(lowIndex, viewSize);
			} else if (orderCount > 0) {
				orderList = eli.getCompleteList();
			}
			
			if (highIndex > orderCount) {
				highIndex = orderCount;
			}
	    } catch (GenericEntityException e) {
	        Debug.logError(e, module);
	    } finally {
	        if (eli != null) {
	            try {
	                eli.close();
	            } catch (GenericEntityException e) {
	                Debug.logWarning(e, e.getMessage(), module);
	            }
	        }
	    }
		orderListSize = orderCount;
    	return orderList;
    	
        /*EntityListIterator iterator = delegator.find("OrderHeaderAndRoles", queryConditionsList, null, null, UtilMisc.toList("orderDate DESC"), options);
        // get subset corresponding to pagination state
        int startIndex = viewSize * viewIndex + 1;
        List<GenericValue> orders = iterator.getPartialList(startIndex, viewSize);
        orderListSize = iterator.getResultsSizeAfterPartialList();
        iterator.close();
        return orders;*/
    }
    
    /**
     * Get list order that product store id of orderHeader IN productStoreIds.
     * @param facilityId
     * @param filterDate
     * @param delegator
     * @param productStores	List product store need get order
     * @param productStoreId	Only this product store need get order
     * @return
     * @throws GenericEntityException
     */
    public EntityListIterator getListOrdersAdvanceJQ (String facilityId, Timestamp filterDate, Delegator delegator, 
    							List<GenericValue> productStores, String productStoreId, 
    							EntityCondition mainCondition, List<String> listSortFields, EntityFindOptions opts) throws GenericEntityException {
        List<EntityCondition> allConditions = FastList.newInstance();
        if (mainCondition != null) allConditions.add(mainCondition);
        if (facilityId != null) {
            allConditions.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
        }

        if (filterDate != null) {
            List<EntityCondition> andExprs = FastList.newInstance();
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(filterDate)));
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(filterDate)));
            allConditions.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
        }

        List<EntityCondition> statusConditions = FastList.newInstance();
        for (String status : orderStatusState.keySet()) {
            if (!hasStatus(status)) continue;
            statusConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameterToOrderStatusId.get(status)));
        }
        List<EntityCondition> typeConditions = FastList.newInstance();
        for (String type : orderTypeState.keySet()) {
            if (!hasType(type)) continue;
            typeConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, parameterToOrderTypeId.get(type)));
        }

        EntityCondition statusConditionsList = EntityCondition.makeCondition(statusConditions, EntityOperator.OR);
        EntityCondition typeConditionsList = EntityCondition.makeCondition(typeConditions, EntityOperator.OR);
        if (statusConditions.size() > 0) {
            allConditions.add(statusConditionsList);
        }
        if (typeConditions.size() > 0) {
            allConditions.add(typeConditionsList);
        }
        
        if ((productStoreId == null || productStoreId.equals("")) && !productStores.isEmpty()) {
        	// convert List<GenericValue> productStores to List<String> productStoreIds
        	List<String> productStoreIds = new ArrayList<String>();
        	for (GenericValue productStoreItem : productStores) {
        		if (productStoreItem.containsKey("productStoreId") && !productStoreIds.contains(productStoreItem.getString("productStoreId"))) {
        			productStoreIds.add(productStoreItem.getString("productStoreId"));
        		}
        	}
        	EntityCondition productStoreCond = EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds);
        	allConditions.add(productStoreCond);
        } else if (productStoreId != null && !productStoreId.equals("")) {
        	EntityCondition productStoreCond = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId);
        	allConditions.add(productStoreCond);
        }

        EntityCondition queryConditionsList = EntityCondition.makeCondition(allConditions, EntityOperator.AND);
        //EntityFindOptions options = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
        EntityListIterator iterator = delegator.find("OrderHeaderAndOrderRoleFromTo", queryConditionsList, null, null, listSortFields, opts);
        //List<GenericValue> orders = iterator.getCompleteList();
        // get subset corresponding to pagination state
        //int startIndex = viewSize * viewIndex + 1;
        //List<GenericValue> orders = iterator.getPartialList(startIndex, viewSize);
        //orderListSize = iterator.getResultsSizeAfterPartialList();
        //iterator.close();
        return iterator;
    }
    
    /**
     * Get list order that create by salesman and manage by sales administrator.
     * @param delegator
     * @param userLogin
     * @return
     * @throws GenericEntityException
     */
    public EntityListIterator getListOrdersSmJQ (Delegator delegator, GenericValue userLogin, 
    		EntityCondition mainCondition, List<String> listSortFields, EntityFindOptions opts) throws GenericEntityException {
    	//List<GenericValue> orderList = new ArrayList<GenericValue>();
        
    	// Dynamic view entity
		DynamicViewEntity dve = new DynamicViewEntity();
		dve.addMemberEntity("PSRUL", "ProductStoreRole");
		dve.addMemberEntity("PS", "ProductStore");
		dve.addMemberEntity("PSRCU", "ProductStoreRole");
		dve.addMemberEntity("PR", "PartyRole");
		dve.addMemberEntity("ORLE", "OrderRole");
		dve.addMemberEntity("OH", "OrderHeader");
		dve.addMemberEntity("ORLECUST", "OrderRole");
		dve.addAlias("PSRUL", "PSRUL_partyId", "partyId", null, false, false, null);
		dve.addAlias("PSRUL", "PSRUL_roleTypeId", "roleTypeId", null, false, false, null);
		dve.addAlias("PSRCU", "PSRCU_roleTypeId", "roleTypeId", null, false, false, null);
		dve.addAlias("PR", "PR_roleTypeId", "roleTypeId", null, false, false, null);
		dve.addAlias("ORLE", "ORLE_roleTypeId", "roleTypeId", null, false, false, null);
		dve.addAlias("ORLECUST", "customerRole", "roleTypeId", null, false, false, null);
		dve.addAlias("ORLECUST", "customerId", "partyId", null, false, false, null);
		dve.addAliasAll("OH", "", null);
		dve.addViewLink("PSRUL", "PS", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));
		dve.addViewLink("PS", "PSRCU", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));
		dve.addViewLink("PSRCU", "PR", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("partyId", "partyId")));
		dve.addViewLink("PR", "ORLE", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("partyId", "partyId")));
		dve.addViewLink("ORLE", "OH", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
		dve.addViewLink("OH", "ORLECUST", Boolean.TRUE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(mainCondition);
		conditions.add(EntityCondition.makeCondition("PSRUL_partyId", EntityOperator.EQUALS, userLogin.getString("partyId")));
		conditions.add(EntityCondition.makeCondition("PSRUL_roleTypeId", EntityOperator.EQUALS, "SALES_ADMIN"));
		conditions.add(EntityCondition.makeCondition("PSRCU_roleTypeId", EntityOperator.EQUALS, "CUSTOMER"));
		conditions.add(EntityCondition.makeCondition("PR_roleTypeId", EntityOperator.EQUALS, "DELYS_DISTRIBUTOR"));
		conditions.add(EntityCondition.makeCondition("ORLE_roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
		conditions.add(EntityCondition.makeCondition("customerRole", EntityOperator.EQUALS, "PLACING_CUSTOMER"));
		
		List<EntityCondition> statusConditions = FastList.newInstance();
        for (String status : orderStatusState.keySet()) {
            if (!hasStatus(status)) continue;
            statusConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameterToOrderStatusId.get(status)));
        }
        List<EntityCondition> typeConditions = FastList.newInstance();
        for (String type : orderTypeState.keySet()) {
            if (!hasType(type)) continue;
            typeConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, parameterToOrderTypeId.get(type)));
        }

        EntityCondition statusConditionsList = EntityCondition.makeCondition(statusConditions, EntityOperator.OR);
        EntityCondition typeConditionsList = EntityCondition.makeCondition(typeConditions, EntityOperator.OR);
        if (statusConditions.size() > 0) {
        	conditions.add(statusConditionsList);
        }
        if (typeConditions.size() > 0) {
        	conditions.add(typeConditionsList);
        }
		
		EntityCondition cond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
		// list of fields to select (initial list)
		List<String> fieldsToSelect = FastList.newInstance();
		fieldsToSelect.add("orderId");
		fieldsToSelect.add("orderTypeId");
		fieldsToSelect.add("orderName");
		fieldsToSelect.add("externalId");
		fieldsToSelect.add("salesChannelEnumId");
		fieldsToSelect.add("orderDate");
		fieldsToSelect.add("priority");
		fieldsToSelect.add("entryDate");
		fieldsToSelect.add("pickSheetPrintedDate");
		fieldsToSelect.add("visitId");
		fieldsToSelect.add("statusId");
		fieldsToSelect.add("createdBy");
		fieldsToSelect.add("firstAttemptOrderId");
		fieldsToSelect.add("currencyUom");
		fieldsToSelect.add("syncStatusId");
		fieldsToSelect.add("billingAccountId");
		fieldsToSelect.add("originFacilityId");
		fieldsToSelect.add("webSiteId");
		fieldsToSelect.add("productStoreId");
		fieldsToSelect.add("terminalId");
		fieldsToSelect.add("transactionId");
		fieldsToSelect.add("autoOrderShoppingListId");
		fieldsToSelect.add("needsInventoryIssuance");
		fieldsToSelect.add("isRushOrder");
		fieldsToSelect.add("internalCode");
		fieldsToSelect.add("remainingSubTotal");
		fieldsToSelect.add("grandTotal");
		fieldsToSelect.add("isViewed");
		fieldsToSelect.add("invoicePerShipment");
		fieldsToSelect.add("customerId");
		
		// sorting by order date newest first
		//List<String> orderBy = UtilMisc.toList("-orderDate", "-orderId");
		
		// set distinct on so we only get one row per order
		//EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
		
		// get the index for the partial list
		/*int lowIndex = viewSize * viewIndex + 1; //(((viewIndex - 1) * viewSize) + 1);
		int highIndex = viewSize * (viewIndex + 1);
		findOpts.setMaxRows(highIndex);*/
		
		EntityListIterator eli = null;
		int orderCount = 0;
		try {
			// do the lookup
			eli = delegator.findListIteratorByCondition(dve, cond, null, fieldsToSelect, listSortFields, opts);
			/*orderCount = eli.getResultsSizeAfterPartialList();*/
			
			// get the partial list for this page
			/*eli.beforeFirst();
			if (orderCount > viewSize) {
				orderList = eli.getPartialList(lowIndex, viewSize);
			} else if (orderCount > 0) {
				orderList = eli.getCompleteList();
			}*/
			
			/*if (highIndex > orderCount) {
				highIndex = orderCount;
			}*/
	    } catch (GenericEntityException e) {
	        Debug.logError(e, module);
	    }/* finally {
	        if (eli != null) {
	            try {
	                eli.close();
	            } catch (GenericEntityException e) {
	                Debug.logWarning(e, e.getMessage(), module);
	            }
	        }
	    }*/
		orderListSize = orderCount;
    	return eli;
    }
    
    public EntityListIterator getListOrdersSmAdvanceJQ (String facilityId, Timestamp filterDate, Delegator delegator, 
			List<GenericValue> productStores, String productStoreId, 
			EntityCondition mainCondition, List<String> listSortFields, EntityFindOptions opts) throws GenericEntityException {
    	List<EntityCondition> allConditions = FastList.newInstance();
        if (mainCondition != null) allConditions.add(mainCondition);
        if (facilityId != null) {
            allConditions.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
        }

        if (filterDate != null) {
            List<EntityCondition> andExprs = FastList.newInstance();
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(filterDate)));
            andExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(filterDate)));
            allConditions.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
        }

        List<EntityCondition> statusConditions = FastList.newInstance();
        for (String status : orderStatusState.keySet()) {
            if (!hasStatus(status)) continue;
            statusConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameterToOrderStatusId.get(status)));
        }
        List<EntityCondition> typeConditions = FastList.newInstance();
        for (String type : orderTypeState.keySet()) {
            if (!hasType(type)) continue;
            typeConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, parameterToOrderTypeId.get(type)));
        }

        EntityCondition statusConditionsList = EntityCondition.makeCondition(statusConditions, EntityOperator.OR);
        EntityCondition typeConditionsList = EntityCondition.makeCondition(typeConditions, EntityOperator.OR);
        if (statusConditions.size() > 0) {
            allConditions.add(statusConditionsList);
        }
        if (typeConditions.size() > 0) {
            allConditions.add(typeConditionsList);
        }
        
        if ((productStoreId == null || productStoreId.equals("")) && !productStores.isEmpty()) {
        	// convert List<GenericValue> productStores to List<String> productStoreIds
        	List<String> productStoreIds = new ArrayList<String>();
        	for (GenericValue productStoreItem : productStores) {
        		if (productStoreItem.containsKey("productStoreId") && !productStoreIds.contains(productStoreItem.getString("productStoreId"))) {
        			productStoreIds.add(productStoreItem.getString("productStoreId"));
        		}
        	}
        	EntityCondition productStoreCond = EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds);
        	allConditions.add(productStoreCond);
        } else if (productStoreId != null && !productStoreId.equals("")) {
        	EntityCondition productStoreCond = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId);
        	allConditions.add(productStoreCond);
        }
        allConditions.add(EntityCondition.makeCondition("roleTypeIdSalesman", "DELYS_SALESMAN_GT"));
        allConditions.add(EntityCondition.makeCondition("roleTypeIdSup", "DELYS_SALESSUP_GT"));
        EntityCondition queryConditionsList = EntityCondition.makeCondition(allConditions, EntityOperator.AND);
        //EntityFindOptions options = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
        EntityListIterator iterator = delegator.find("OrderHeaderAnd4OrderRoleFromTo", queryConditionsList, null, null, listSortFields, opts);
        //List<GenericValue> orders = iterator.getCompleteList();
        // get subset corresponding to pagination state
        //int startIndex = viewSize * viewIndex + 1;
        //List<GenericValue> orders = iterator.getPartialList(startIndex, viewSize);
        //orderListSize = iterator.getResultsSizeAfterPartialList();
        //iterator.close();
        return iterator;
    }
}
