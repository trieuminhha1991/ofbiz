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

import java.util.HashMap;
import org.ofbiz.base.util.UtilHttp;

import com.olbius.baseecommerce.party.ProfileUtils;

tryEntity = true;
errorMessage = parameters._ERROR_MESSAGE_;
if (errorMessage) {
    tryEntity = false;
}
personData = person;
if (!tryEntity) personData = UtilHttp.getParameterMap(request);
if (!personData) personData = [:];

context.personData = personData;

def party = ProfileUtils.CustomerProfile(delegator, partyId);
if(party.birthDate) {
	party.birthDate = party.birthDate.getTime();
}
context.party = party;

if(party.TELECOM_NUMBER) {
	if(party.TELECOM_NUMBER.telecomNumber) {
		def telecomNumber = party.TELECOM_NUMBER.telecomNumber;
		context.contactNumber = telecomNumber.contactNumber;
	}
	if(party.TELECOM_NUMBER.contactMech) {
		def contactMech = party.TELECOM_NUMBER.contactMech;
		context.contactNumberId = contactMech.contactMechId
	}
}
if(party.EMAIL_ADDRESS) {
	if(party.EMAIL_ADDRESS.contactMech) {
		def contactMech = party.EMAIL_ADDRESS.contactMech;
		context.infoString = contactMech.infoString;
		context.infoStringId = contactMech.contactMechId;
	}
}
if(party.BILLING_LOCATION) {
	def postalAddress = party.BILLING_LOCATION;
	context.postalAddressId = postalAddress.contactMechId;
	context.address1 = postalAddress.address1;
	context.stateProvinceGeoId = postalAddress.stateProvinceGeoId;
	context.districtGeoId = postalAddress.districtGeoId;
}