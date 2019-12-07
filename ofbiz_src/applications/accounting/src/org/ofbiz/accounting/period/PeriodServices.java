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

package org.ofbiz.accounting.period;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;

import org.apache.commons.lang.time.DateUtils;
import org.apache.tools.ant.types.resources.selectors.Compare;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class PeriodServices {
    
    public static String module = PeriodServices.class.getName();
    public static final String resource = "AccountingUiLabels";

    /* find the date of the last closed CustomTimePeriod, or, if none available, the earliest date available of any
     * CustomTimePeriod
     */
    public static Map<String, Object> findLastClosedDate(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        String organizationPartyId = (String) context.get("organizationPartyId"); // input parameters
        String periodTypeId = (String) context.get("periodTypeId");
        Date findDate = (Date) context.get("findDate");
        Locale locale = (Locale) context.get("locale");

        // default findDate to now
        if (findDate == null) {
            findDate = new Date(UtilDateTime.nowTimestamp().getTime());
        }

        Timestamp lastClosedDate = null;          // return parameters
        GenericValue lastClosedTimePeriod = null;
        Map<String, Object> result = ServiceUtil.returnSuccess();

        try {
            // try to get the ending date of the most recent accounting time period before findDate which has been closed
            List<EntityCondition> findClosedConditions = UtilMisc.toList(EntityCondition.makeConditionMap("organizationPartyId", organizationPartyId),
                    EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, findDate),
                    EntityCondition.makeConditionMap("isClosed", "Y"));
            if ((periodTypeId != null) && !(periodTypeId.equals(""))) {
                // if a periodTypeId was supplied, use it
                findClosedConditions.add(EntityCondition.makeConditionMap("periodTypeId", periodTypeId));
            }
            List<GenericValue> closedTimePeriods = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(findClosedConditions),
                    UtilMisc.toSet("customTimePeriodId", "periodTypeId", "isClosed", "fromDate", "thruDate"),
                    UtilMisc.toList("thruDate DESC"), null, false);

            if ((closedTimePeriods != null) && (closedTimePeriods.size() > 0) && (closedTimePeriods.get(0).get("thruDate") != null)) {
                lastClosedTimePeriod = closedTimePeriods.get(0);
                lastClosedDate = UtilDateTime.toTimestamp(lastClosedTimePeriod.getDate("thruDate"));
            } else {
                // uh oh, no time periods have been closed?  in that case, just find the earliest beginning of a time period for this organization
                // and optionally, for this period type
                Map<String, String> findParams = UtilMisc.toMap("organizationPartyId", organizationPartyId);
                if ((periodTypeId != null) && !(periodTypeId.equals(""))) {
                    findParams.put("periodTypeId", periodTypeId);
                }
                List<GenericValue> timePeriods = delegator.findByAnd("CustomTimePeriod", findParams, UtilMisc.toList("fromDate ASC"), false);
                if ((timePeriods != null) && (timePeriods.size() > 0) && (timePeriods.get(0).get("fromDate") != null)) {
                    lastClosedDate = UtilDateTime.toTimestamp(timePeriods.get(0).getDate("fromDate"));
                } else {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "AccountingPeriodCannotGet", locale));
                }
            }

            result.put("lastClosedTimePeriod", lastClosedTimePeriod);  // ok if this is null - no time periods have been closed
            result.put("lastClosedDate", lastClosedDate);  // should have a value - not null
            return result;
        } catch (GenericEntityException ex) {
            return(ServiceUtil.returnError(ex.getMessage()));
        }
    }
    
    // Create by VIETTB
    public static Map<String, Object> findPreviewCustomtimePeriodClosed(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        String organizationPartyId = (String) context.get("organizationPartyId"); // input parameters
        Date fromDate = (Date) context.get("fromDate");
        java.util.Date previewThruDate = null;
        Locale locale = (Locale) context.get("locale");
        previewThruDate = DateUtils.addDays(fromDate,-1);
        java.sql.Date pThruDate = new java.sql.Date(previewThruDate.getTime());
        Timestamp lastClosedDate = null;          // return parameters
        GenericValue lastClosedTimePeriod = null;
        Boolean isFirst = false;
        Map<String, Object> result = ServiceUtil.returnSuccess();

        try {
            // try to get the ending date of the most recent accounting time period before findDate which has been closed
            List<EntityCondition> findClosedConditions = UtilMisc.toList(EntityCondition.makeConditionMap("organizationPartyId", organizationPartyId),
                    EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, pThruDate),
                    EntityCondition.makeConditionMap("isClosed", "Y"),
                    EntityCondition.makeCondition("periodTypeId", EntityOperator.LIKE, "FISCAL_%"));
            // Don't need same periodTypeId, we need a customTimePeriod close with thruDate = previewThruDate
//            if ((periodTypeId != null) && !(periodTypeId.equals(""))) {
//                // if a periodTypeId was supplied, use it
//                findClosedConditions.add(EntityCondition.makeConditionMap("periodTypeId", periodTypeId));
//            }
            List<GenericValue> closedTimePeriods = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(findClosedConditions),
                    UtilMisc.toSet("customTimePeriodId", "periodTypeId", "isClosed", "fromDate", "thruDate"),
                    UtilMisc.toList("thruDate DESC"), null, false);

            if ((closedTimePeriods != null) && (closedTimePeriods.size() > 0) && (closedTimePeriods.get(0).get("thruDate") != null)) {
                lastClosedTimePeriod = closedTimePeriods.get(0);
                lastClosedDate = UtilDateTime.toTimestamp(lastClosedTimePeriod.getDate("thruDate"));
            } else {
                // uh oh, no time periods have been closed?  in that case, just find the earliest beginning of a time period for this organization
                // and optionally, for this period type
                List<EntityCondition> findParams = UtilMisc.toList(EntityCondition.makeConditionMap("organizationPartyId", organizationPartyId),
                        EntityCondition.makeCondition("periodTypeId", EntityOperator.LIKE, "FISCAL_%"));            	
                // Don't need same periodTypeId
//                if ((periodTypeId != null) && !(periodTypeId.equals(""))) {
//                    findParams.put("periodTypeId", periodTypeId);
//                }
                List<GenericValue> timePeriods = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(findParams), null, UtilMisc.toList("fromDate ASC"), null,false);
                if ((timePeriods != null) && (timePeriods.size() > 0) && (timePeriods.get(0).get("fromDate") != null)) {
                    lastClosedDate = UtilDateTime.toTimestamp(timePeriods.get(0).getDate("fromDate"));
                    if (lastClosedDate.compareTo(fromDate) == 0 ) isFirst = true;
                } else {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "AccountingPeriodCannotGet", locale));
                }
            }

            result.put("lastClosedTimePeriod", lastClosedTimePeriod);  // ok if this is null - no time periods have been closed with thruDate = previewThruDate
            result.put("lastClosedDate", lastClosedDate);  // should have a value - not null
            result.put("isFirst", isFirst);
            return result;
        } catch (GenericEntityException ex) {
            return(ServiceUtil.returnError(ex.getMessage()));
        }
    }
    
	public static List<Object> addAllParentCustomtimePeriod(String strCustomTimePeriodId, Delegator delegator){
		List<Object> listReturn = new ArrayList();
		String strParentCustomTimePeriod  = strCustomTimePeriodId;
		try {			
			while (strParentCustomTimePeriod != null)
			{
				GenericValue customTimePeriod = null;	
				customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", strParentCustomTimePeriod), false);
				strParentCustomTimePeriod = customTimePeriod.containsKey("parentPeriodId") ? (String) customTimePeriod.get("parentPeriodId") : null;
				if (strParentCustomTimePeriod != null) listReturn.add(strParentCustomTimePeriod);
			}
		} catch (GenericEntityException ex) {
			Debug.logError(ex, "Problem ParentCustomtimePeriod", module);
	    }
		return listReturn;
	}
    
    public static Map<String, Object> updateParentCustomTimePeriod(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String organizationPartyId = (String) context.get("organizationPartyId"); // input parameters
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<Object> listAllParentCustomPeriod = new FastList<Object>();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
        	listAllParentCustomPeriod = addAllParentCustomtimePeriod(customTimePeriodId, delegator);
        	
        	for (int i = 0; i < listAllParentCustomPeriod.size(); i++)
        	{
        		List<GenericValue> listCustomTimePeriod = null;	
                EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("isClosed", "N"),
                        EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS, listAllParentCustomPeriod.get(i).toString())),
                        EntityOperator.AND);
                
        		listCustomTimePeriod = delegator.findList("CustomTimePeriod", condition, null, null, null, false);
        		if (UtilValidate.isEmpty(listCustomTimePeriod))
        		{
        			Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "organizationPartyId", organizationPartyId, "customTimePeriodId", listAllParentCustomPeriod.get(i).toString(), "isClosed", "Y");
                    try {
                        dispatcher.runSync("updateCustomTimePeriod", input);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                    }
        		}
        	}
        } catch (GenericEntityException ex) {
        	Debug.logError(ex, "Problem updateParentCustomTimePeriod", module);
        }
        return result;
    }    
}
