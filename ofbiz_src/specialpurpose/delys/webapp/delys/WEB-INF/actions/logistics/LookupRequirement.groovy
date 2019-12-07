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

/*
 * This script is also referenced by the ecommerce's screens and
 * should not contain order component's specific code.
 */

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyLoginId = userLogin.get('partyId');
context.partyLoginId = partyLoginId;

java.util.Date date= new java.util.Date();
exprList = [];
exprRoleList = [];
requirementId = parameters.requirementId;
requirementStartDate = parameters.requirementStartDate;
requirementByDate = parameters.requirementByDate;
statusId = parameters.statusId;
partyId = parameters.partyId;
List<GenericValue> listRequirements = new ArrayList<GenericValue>();
if (requirementId != null){
	expr = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
	exprList.add(expr);
} else {
	if (partyId != null){
		expr = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
		exprRoleList.add(expr);
		expr = EntityCondition.makeCondition("RoleTypeId", EntityOperator.EQUALS, "OWNER");
		exprRoleList.add(expr);
		List<GenericValue> listRequestor = new ArrayList<GenericValue>();
		Cond = EntityCondition.makeCondition(exprRoleList, EntityOperator.AND);
		listRequestor = delegator.findList("RequirementRole", Cond, null, null, null, false);
		if (!listRequestor.isEmpty()){
			for (GenericValue requestor : listRequestor){
				GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requestor.get("requirementId")));
				if (requirement){
					listRequirements.add(requirement);
				}
			}
		}
		if (!listRequirements.isEmpty()){
			for (GenericValue req : listRequirements){
				expr = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, req.get("requirementId"));
				exprList.add(expr);
				if (requirementStartDate != null){
					expr = EntityCondition.makeCondition("requirementStartDate", EntityOperator.EQUALS, requirementStartDate);
					exprList.add(expr);
				}
				if (requirementByDate != null) {
					expr = EntityCondition.makeCondition("requirementByDate", EntityOperator.EQUALS, requirementByDate);
					exprList.add(expr);
				}
				if (statusId != null){
					expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId);
					exprList.add(expr);
				}
			}
		}
	}
}

Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

listProductStoresParty = delegator.findList("ProductStoreRole", Cond, null, ["partyId"], null, false);
listProductStoresParty = EntityUtil.filterByDate(listProductStoresParty);

List<GenericValue> listProductStores = new ArrayList<GenericValue>();
if (!listProductStoresParty.isEmpty()){
	for (GenericValue item : listProductStoresParty) {
		GenericValue prStore = null;
		prStoreId = item.get('productStoreId');
		prStore = delegator.findOne("ProductStore", [productStoreId : prStoreId], false);
		listProductStores.add(prStore);
	}
} else {
	context.NoPermission = "Y";
}

if (!listProductStores.isEmpty()){
	context.listProductStores = listProductStores;
} else {
}