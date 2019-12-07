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

public class Opportunity {
    public static final String module = Opportunity.class.getName();
    public static final String resourceError = "MarketingUiLabels";
    
    public static Map<String, Object> getListOpportunities(DispatchContext dctx, Map<String, ? extends Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		Set<String> fields = FastSet.newInstance();
    		fields.add("opportunityName");
    		fields.add("nextStep");
    		fields.add("estimatedAmount");
    		fields.add("nextStepDate");
    		fields.add("estimatedCloseDate");
    		fields.add("partyId");
    		fields.add("stagedescription");
    		opts.setDistinct(true);
    		List<GenericValue> contacts = delegator.findList("SalesOpportunityAndRoleDetail", tmpConditon, fields, listSortFields, opts, false);
    		successResult.put("listIterator", contacts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvItemAndOrdItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
    }
}
