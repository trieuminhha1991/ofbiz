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
package com.olbius.obb.facebook;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class ProductSocial {

	public static String addInteraction(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		String user = "";
		if(userLogin != null){
			user = (String) userLogin.get("partyId");
		}
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String fbId = (String) context.get("fbid");
		String status = (String) context.get("status");
		String productId = (String) context.get("productId");
		String primKey = delegator.getNextSeqId("ProductSocial");
		GenericValue productSocial = delegator.makeValue("ProductSocial");
		productSocial.put("productSocialId", primKey);
		productSocial.put("productId", productId);
		productSocial.put("partyId", user);
		productSocial.put("fbId", fbId);
		productSocial.put("status", status);
		try {
			delegator.createOrStore(productSocial);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("msg", e.getMessage());
			return "error";
		}
		request.setAttribute("msg", "Insert success");
		return "success";
	}
}
