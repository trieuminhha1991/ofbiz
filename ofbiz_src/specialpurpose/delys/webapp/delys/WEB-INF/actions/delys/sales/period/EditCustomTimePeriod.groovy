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

import java.util.*;
import java.net.*;
import org.ofbiz.security.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityCondition;

findOrganizationPartyId = parameters.findOrganizationPartyId;
if (findOrganizationPartyId) {
    context.findOrganizationPartyId = findOrganizationPartyId;
}

currentCustomTimePeriodId = parameters.currentCustomTimePeriodId;
if (currentCustomTimePeriodId) {
    context.currentCustomTimePeriodId = currentCustomTimePeriodId;
}

currentCustomTimePeriod = currentCustomTimePeriodId ? delegator.findOne("CustomTimePeriod", [customTimePeriodId : currentCustomTimePeriodId], false) : null;
if (currentCustomTimePeriod) {
    context.currentCustomTimePeriod = currentCustomTimePeriod;
}

currentPeriodType = currentCustomTimePeriod ? currentCustomTimePeriod.getRelatedOne("PeriodType", true) : null;
if (currentPeriodType) {
    context.currentPeriodType = currentPeriodType;
}

conditionList = [];
if (findOrganizationPartyId) conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, findOrganizationPartyId));
if (currentCustomTimePeriodId) conditionList.add(EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS, currentCustomTimePeriodId));
conditions = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

orConditionList = [];
orConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_MONTH"));
orConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_QUARTER"));
orConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_YEAR"));
orConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_WEEK"));
orConditions = EntityCondition.makeCondition(orConditionList, EntityOperator.OR);

mainConditionList = [];
mainConditionList.add(orConditions);
mainConditionList.add(conditions);
mainConditions = EntityCondition.makeCondition(mainConditionList, EntityOperator.AND);

customTimePeriods = delegator.findList("CustomTimePeriod", mainConditions, null, ["periodTypeId", "periodNum", "fromDate"], null, true);
context.customTimePeriods = customTimePeriods;

allCustomTimePeriods = delegator.findList("CustomTimePeriod", mainConditions, null, ["organizationPartyId", "parentPeriodId", "periodTypeId", "periodNum", "fromDate"], null, false);
context.allCustomTimePeriods = allCustomTimePeriods;

periodTypes = delegator.findList("PeriodType", orConditions, null, ["description"], null, true);
context.periodTypes = periodTypes;

newPeriodTypeId = "FISCAL_YEAR";
if ("FISCAL_YEAR".equals(currentCustomTimePeriod?.periodTypeId)) {
    newPeriodTypeId = "FISCAL_QUARTER";
}
if ("FISCAL_QUARTER".equals(currentCustomTimePeriod?.periodTypeId)) {
    newPeriodTypeId = "FISCAL_MONTH";
}
if ("FISCAL_MONTH".equals(currentCustomTimePeriod?.periodTypeId)) {
    newPeriodTypeId = "FISCAL_WEEK";
}
if ("FISCAL_BIWEEK".equals(currentCustomTimePeriod?.periodTypeId)) {
    newPeriodTypeId = "FISCAL_WEEK";
}
if ("FISCAL_WEEK".equals(currentCustomTimePeriod?.periodTypeId)) {
    newPeriodTypeId = "";
}

context.newPeriodTypeId = newPeriodTypeId;
