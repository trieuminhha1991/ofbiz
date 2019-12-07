/*******************************************************************************
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
 *******************************************************************************/

package com.olbius.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;

import com.meterware.pseudoserver.HttpRequest;

public class Event {
    public static final String module = Event.class.getName();
    public static final String resourceError = "MarketingUiLabels";
    
    public static Map<String, Object> getListEventAssignByMe(DispatchContext dctx, Map<String, ? extends Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		String userLoginId = (String) userLogin.get("userLoginId");
    		EntityCondition assignbyme = EntityCondition.makeCondition(
					"createdByUserLogin", EntityOperator.EQUALS, userLoginId);
    		listAllConditions.add(assignbyme);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		Set<String> fields = FastSet.newInstance();
    		fields.add("workEffortId");
    		fields.add("workEffortPurposeTypeId");
    		fields.add("description");
    		fields.add("priority");
    		fields.add("estimatedStartDate");
    		fields.add("estimatedCompletionDate");
    		fields.add("actualStartDate");
    		fields.add("actualCompletionDate");
    		fields.add("partyId");
    		opts.setDistinct(true);
    		List<GenericValue> contacts = delegator.findList("WorkEffortFindView", tmpConditon, fields, listSortFields, opts, false);
    		successResult.put("listIterator", contacts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvItemAndOrdItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
    }
    public static Map<String, Object> getListEventAssignToMe(DispatchContext dctx, Map<String, ? extends Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		String userLoginId = (String) userLogin.get("userLoginId");
    		EntityCondition assigntome = EntityCondition.makeCondition(
					"partyId", EntityOperator.EQUALS, userLoginId);
    		listAllConditions.add(assigntome);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		Set<String> fields = FastSet.newInstance();
    		fields.add("workEffortId");
    		fields.add("description");
    		fields.add("priority");
    		fields.add("estimatedStartDate");
    		fields.add("estimatedCompletionDate");
    		fields.add("actualStartDate");
    		fields.add("actualCompletionDate");
    		fields.add("createdByUserLogin");
    		opts.setDistinct(true);
    		List<GenericValue> contacts = delegator.findList("WorkEffortFindView", tmpConditon, fields, listSortFields, opts, false);
    		successResult.put("listIterator", contacts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvItemAndOrdItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
    }
}
