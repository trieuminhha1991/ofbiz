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
partyId = userLogin.get('partyId');
context.partyId = partyId;

java.util.Date date= new java.util.Date();
exprList = [];
exprOrList = [];
expr = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
exprList.add(expr);
expr = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER");
exprList.add(expr);
Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

listFacilityParty = delegator.findList("FacilityParty", Cond, null, ["partyId"], null, false);
listFacilityParty = EntityUtil.filterByDate(listFacilityParty);

List<GenericValue> listFacilities = new ArrayList<GenericValue>();
List<String> listFacilityId= new ArrayList<String>();
if (!listFacilityParty.isEmpty()){
	for (GenericValue item : listFacilityParty) {
		GenericValue facility = null;
		facilityId = item.get('facilityId');
		facility = delegator.findOne("Facility", [facilityId : facilityId], false); 
		listFacilities.add(facility);
		listFacilityId.add(facilityId);
	}
} else {
	context.NoPermission = "Y";
}

if (!listFacilities.isEmpty()){
//	for (GenericValue fac : listFacilities){
//		listChildFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", (String)fac.get("facilityId"))), null, ["partyId"], null, false);
//	}
	context.listFacilities = listFacilities;
	context.listFacilityId = listFacilityId;
} else {
}