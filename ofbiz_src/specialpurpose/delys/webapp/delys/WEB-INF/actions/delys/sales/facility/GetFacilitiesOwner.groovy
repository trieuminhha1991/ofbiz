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

import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

productStores = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreRoleDetail", ["partyId" : userLogin.partyId, roleTypeId : "MANAGER"], ["productStoreId", "storeName"], true), true);
context.productStores = productStores;

if (productStores) {
	List<GenericValue> listProductStoreFacilities = new ArrayList<GenericValue> ();
	for (productStoreItem in productStores) {
		listProductStoreFacilityItem = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreFacility", ["productStoreId" : productStoreItem.productStoreId], ["sequenceNum", "fromDate"], false), false);
		if (listProductStoreFacilityItem) {
			listProductStoreFacilities.addAll(listProductStoreFacilityItem);
		}
	}
	
	List<GenericValue> listFacilities = new ArrayList<GenericValue>();
	List<String> listFacilityId= new ArrayList<String>();
	for (item in listProductStoreFacilities) {
		GenericValue facility = null;
		facilityId = item.facilityId;
		facility = delegator.findOne("Facility", [facilityId : facilityId], false);
		listFacilities.add(facility);
		listFacilityId.add(facilityId);
	}
	
	if (listProductStoreFacilities){
		context.listFacilities = listFacilities;
		context.listFacilityId = listFacilityId;
	}
}

