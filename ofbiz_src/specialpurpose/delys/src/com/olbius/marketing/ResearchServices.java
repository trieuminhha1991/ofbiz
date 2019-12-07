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
package com.olbius.marketing;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;

/**
 * MarketingServices contains static service methods for Marketing Campaigns and
 * Contact Lists. See the documentation in marketing/servicedef/services.xml and
 * use the service reference in webtools. Comments in this file are
 * implemntation notes and technical details.
 */
public class ResearchServices {

	public static final String module = ResearchServices.class.getName();
	public static final String resourceMarketing = "MarketingUiLabels";
	public static final String resourceOrder = "OrderUiLabels";

	public static Map<String, Object> createResearchCampagin(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String campaignName = (String) context.get("campaignName");
		String campaignSummary = (String) context.get("campaignSummary");
		Long people = Long.parseLong((String) context.get("people"));
		String marketingPlace = (String) context.get("marketingPlace");
		String tmpEst = (String) context.get("estimatedCost");
		BigDecimal estimatedCost = tmpEst != null ? new BigDecimal(tmpEst) : new BigDecimal(0);
		String tmpBudget = (String) context.get("budgetedCost");
		BigDecimal budgetedCost = tmpBudget != null ? new BigDecimal(tmpBudget) : new BigDecimal(0);
		String isActive = (String) context.get("isActive");
		String productId = (String) context.get("productId");
		String costList = (String) context.get("costList");
		try {
			GenericValue inset = delegator.makeValue("MarketingCampaign");
			String marketingCampaignId = delegator
					.getNextSeqId("MarketingCampaign");
			inset.set("marketingCampaignId", marketingCampaignId);
			inset.set("campaignName", campaignName);
			inset.set("campaignSummary", campaignSummary);
			inset.set("statusId", "MKTG_CAMP_PLANNED");
			inset.set("estimatedCost", estimatedCost);
			inset.set("budgetedCost", budgetedCost);
			inset.set("isActive", isActive);
			inset.set("fromDate", fromDate);
			inset.set("thruDate", thruDate);
			inset.set("createdByUserLogin", userLogin.get("userLoginId"));
			delegator.create(inset);
			/* Marketing delys info */
			GenericValue mkdelys = delegator.makeValue("MarketingInfo");
			mkdelys.set("people", people);
			mkdelys.set("marketingCampaignId", marketingCampaignId);
			mkdelys.set("marketingTypeId", "RESEARCH");
			delegator.create(mkdelys);
			/*create place*/
			Map<String, Object> pl = FastMap.newInstance();
			pl.put("userLogin", userLogin);
			pl.put("marketingCampaignId", marketingCampaignId);
			pl.put("geoId", marketingPlace);
			dispatcher.runSync("createMarketingPlace", pl);
			Map<String, Object> tmpMp = FastMap.newInstance();
			if(productId != null){
				JSONArray products = JSONArray.fromObject(productId);
				for (int i = 0; i < products.size(); i++) {
					String prId = products.getString(i);
					if (!prId.isEmpty()) {
						tmpMp = FastMap.newInstance();
						tmpMp.put("userLogin", userLogin);
						tmpMp.put("productId", prId);
						tmpMp.put("marketingCampaignId", marketingCampaignId);
						dispatcher.runSync("createMarketingProductSimple", tmpMp);
					}
				}
			}
			if(costList != null){
				JSONArray costs = JSONArray.fromObject(costList);
				for (int j = 0; j < costs.size(); j++) {
					JSONObject co = costs.getJSONObject(j);
					String id = co.getString("id");
					JSONArray listCost = co.getJSONArray("content");
					for (int k = 0; k < listCost.size(); k++) {
						JSONObject tmp = listCost.getJSONObject(k);
						tmpMp = FastMap.newInstance();
						tmpMp.put("userLogin", userLogin);
						tmpMp.put("marketingCampaignId", marketingCampaignId);
						tmpMp.put("marketingCostTypeId", id);
						if(tmp.has("description")){
							tmpMp.put("description", tmp.getString("description"));
						}
						if(tmp.has("unitPrice")){
							tmpMp.put("unitPrice", new BigDecimal(tmp.getString("unitPrice")));
						}
						if(tmp.has("quantity")){
							tmpMp.put("quantity", new BigDecimal(tmp.getString("quantity")));
						}
						dispatcher.runSync("createMarketingCost", tmpMp);					
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
			return res;
		}
		res.put("message", "success");
		return res;
	}
}
