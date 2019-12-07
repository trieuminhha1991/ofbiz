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

import org.ofbiz.base.util.*
import org.ofbiz.party.contact.*
import javolution.util.*
//context.nowStr = UtilDateTime.nowTimestamp();
//
facilityId = parameters.facilityId;
//facility = delegator.findOne("Facility", [facilityId : facilityId], false);
//facilityType = null;
//if (!facility) {
//  context.facility = delegator.makeValue("Facility", null);
//  context.facilityType = delegator.makeValue("FacilityType", null);
//} else {
//  facilityType = facility.getRelatedOne("FacilityType", false);
//}
//context.facility = facility;
//context.facilityType = facilityType;
//context.facilityId = facilityId;
//
//showOld = "true".equals(request.getParameter("SHOW_OLD"));
//context.showOld = new Boolean(showOld);
//

facilityId = parameters.facilityId;
List<Map<String, Object>> contactMeches = ContactMechWorker.getFacilityContactMechValueMaps(delegator, facilityId, false, null);
println("HUNGNC " + contactMeches);
Map<String, Object> postalAddress = FastMap.newInstance();
for (Map<String, Object> x : contactMeches) {
	postalAddress = x.get("postalAddress");
	if (postalAddress) {
		break;
	}
}
if (postalAddress) {
	if (postalAddress.get("countryGeoId")) {
		geo = delegator.findOne("Geo", [geoId : postalAddress.countryGeoId], false);
	    context.countryGeoId = geo.geoName;
	}
	if (postalAddress.get("stateProvinceGeoId")) {
		geo = delegator.findOne("Geo", [geoId : postalAddress.stateProvinceGeoId], false);
		context.stateProvinceGeoId = geo.geoName;
	}
	if (postalAddress.get("districtGeoId")) {
		geo = delegator.findOne("Geo", [geoId : postalAddress.districtGeoId], false);
		context.districtGeoId = geo.geoName;
	}
	if (postalAddress.get("wardGeoId")) {
		geo = delegator.findOne("Geo", [geoId : postalAddress.wardGeoId], false);
		context.wardGeoId = geo.geoName;
	}
}
context.postalAddress = postalAddress;

def telecomNumber;
for (Map<String, Object> x : contactMeches) {
	telecomNumber = x.get("telecomNumber");
	if (telecomNumber) {
		break;
	}
}
context.telecomNumber = telecomNumber;