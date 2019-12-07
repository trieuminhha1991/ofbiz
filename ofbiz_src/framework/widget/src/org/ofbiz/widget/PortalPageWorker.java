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
package org.ofbiz.widget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.conversion.JSONConverters;
import org.ofbiz.base.json.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * PortalPageWorker Class
 */
public class PortalPageWorker {

    public static final String module = PortalPageWorker.class.getName();

    public PortalPageWorker() { }

    public String renderPortalPageAsTextExt(Delegator delegator, String portalPageId, Map<String, Object> templateContext,
            boolean cache) throws GeneralException, IOException {
        return "success";
    }

    /**
    * Returns a list of PortalPages that have the specified parentPortalPageId as parent.
    * If a specific PortalPage exists for the current userLogin it is returned instead of the original one.
    */
    public static List<GenericValue> getPortalPages(String parentPortalPageId, Map<String, Object> context) {
        List<GenericValue> portalPages = null;
        if (UtilValidate.isNotEmpty(parentPortalPageId)) {
            Delegator delegator = WidgetWorker.getDelegator(context);
            try {
                // first get public pages
                EntityCondition cond =
                    EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, "_NA_"),
                        EntityCondition.makeCondition(UtilMisc.toList(
                                EntityCondition.makeCondition("portalPageId", EntityOperator.EQUALS, parentPortalPageId),
                                EntityCondition.makeCondition("parentPortalPageId", EntityOperator.EQUALS, parentPortalPageId)),
                                EntityOperator.OR)),
                        EntityOperator.AND);
                portalPages = delegator.findList("PortalPage", cond, null, null, null, false);
                List<GenericValue> userPortalPages = new ArrayList<GenericValue>();
                if (UtilValidate.isNotEmpty(context.get("userLogin"))) { // check if a user is logged in
                    String userLoginId = ((GenericValue)context.get("userLogin")).getString("userLoginId");
                    // replace with private pages
                    for (GenericValue portalPage : portalPages) {
                        cond = EntityCondition.makeCondition(UtilMisc.toList(
                                EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, userLoginId),
                                EntityCondition.makeCondition("originalPortalPageId", EntityOperator.EQUALS, portalPage.getString("portalPageId"))),
                                EntityOperator.AND);
                        List <GenericValue> privatePortalPages = delegator.findList("PortalPage", cond, null, null, null, false);
                        if (UtilValidate.isNotEmpty(privatePortalPages)) {
                            userPortalPages.add(privatePortalPages.get(0));
                        } else {
                            userPortalPages.add(portalPage);
                        }
                    }
                    // add any other created private pages
                    cond = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, userLoginId),
                            EntityCondition.makeCondition("originalPortalPageId", EntityOperator.EQUALS, null),
                            EntityCondition.makeCondition("parentPortalPageId", EntityOperator.EQUALS, parentPortalPageId)),
                            EntityOperator.AND);
                    userPortalPages.addAll(delegator.findList("PortalPage", cond, null, null, null, false));
                }
                portalPages = EntityUtil.orderBy(userPortalPages, UtilMisc.toList("sequenceNum"));
            } catch (GenericEntityException e) {
                Debug.logError("Could not retrieve portalpages:" + e.getMessage(), module);
            }
        }
        return portalPages;
    }

    /**
    * Returns the PortalPage with the specified portalPageId.
    * If a specific PortalPage exists for the current userLogin it is returned instead of the original one.
    */
    public static GenericValue getPortalPage(String portalPageId, Map<String, Object> context) {
        GenericValue portalPage = null;
        if (UtilValidate.isNotEmpty(portalPageId)) {
            Delegator delegator = WidgetWorker.getDelegator(context);
            try {
                // Get the current userLoginId
                String userLoginId = "_NA_";
                if (UtilValidate.isNotEmpty(context.get("userLogin"))) { // check if a user is logged in
                    userLoginId = ((GenericValue)context.get("userLogin")).getString("userLoginId");
                }
                
                // Get the PortalPage ensuring that it is either owned by the user or a system page
                EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("portalPageId", EntityOperator.EQUALS, portalPageId),
                    EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, "_NA_"),
                        EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, userLoginId)),
                        EntityOperator.OR)),
                    EntityOperator.AND);
                List <GenericValue> portalPages = delegator.findList("PortalPage", cond, null, null, null, false);
                if (UtilValidate.isNotEmpty(portalPages)) {
                    portalPage = EntityUtil.getFirst(portalPages);
                }
                
                // If a derived PortalPage private to the user exists, returns this instead of the system one
                cond = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("originalPortalPageId", EntityOperator.EQUALS, portalPageId),
                        EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, userLoginId)),
                        EntityOperator.AND);
                List <GenericValue> privateDerivedPortalPages = delegator.findList("PortalPage", cond, null, null, null, false);
                if (UtilValidate.isNotEmpty(privateDerivedPortalPages)) {
                    portalPage = EntityUtil.getFirst(privateDerivedPortalPages);
                }
            } catch (GenericEntityException e) {
                Debug.logError("Could not retrieve portalpage:" + e.getMessage(), module);
            }
        }
        return portalPage;
    }

    /**
    * Checks if the user is allowed to configure the PortalPage.
    * PortalPage configuration is allowed if he is the PortalPage owner or he has got the PORTALPAGE_ADMIN permission
    */   
    public static Boolean userIsAllowedToConfigure(String portalPageId, Map<String, Object> context) {
        Boolean userIsAllowed = false;

        if (UtilValidate.isNotEmpty(portalPageId)) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            if (UtilValidate.isNotEmpty(userLogin)) {
                String userLoginId = (String) userLogin.get("userLoginId");
                Security security = (Security) context.get("security");

                Boolean hasPortalAdminPermission = security.hasPermission("PORTALPAGE_ADMIN", userLogin);
                try {
                    Delegator delegator = WidgetWorker.getDelegator(context);
                    GenericValue portalPage = delegator.findOne("PortalPage", UtilMisc.toMap("portalPageId", portalPageId),false);

                    if (UtilValidate.isNotEmpty(portalPage)) {
                        String ownerUserLoginId = (String) portalPage.get("ownerUserLoginId");
                        // Users with PORTALPAGE_ADMIN permission can configure every Portal Page
                        userIsAllowed = (ownerUserLoginId.equals(userLoginId) || hasPortalAdminPermission);
                    }
                } catch (GenericEntityException e) {
                    return false;
                }
            }
        }

        return userIsAllowed;       
    }
    /**
     * Olbius update portlet location in dashboard
     **/
    @SuppressWarnings("unchecked")
	public static Map<String, Object> updatePagePortletLocation(DispatchContext dctx, Map<String, Object> context){
	Map<String, Object> res = ServiceUtil.returnSuccess();
	String pt = (String) context.get("data");
	Delegator delegator = dctx.getDelegator();
	GenericValue userLogin = (GenericValue) context.get("userLogin");
	try{
		List<Object> portlets = JSONConverters.JSONToList.convert(JSON.from(pt));
		if(portlets != null){
			String userLoginId = userLogin.getString("userLoginId");
			for(Object portlet : portlets){
				Map<String, Object> obj = (Map<String, Object>) portlet;
				String portalPageId = (String) obj.get("portalPageId");
				String portalPortletId = (String) obj.get("portalPortletId");
				String porletSeqId = (String) obj.get("porletSeqId");
				int columnSeqId = (int) obj.get("columnSeqId");
				int rowSeqId = (int) obj.get("rowSeqId");
				int colspan = (int) obj.get("colspan");
				int rowspan = (int) obj.get("rowspan");
				updatePortalPagePortlet(delegator, userLoginId, portalPageId, portalPortletId, porletSeqId, String.valueOf(columnSeqId), String.valueOf(rowSeqId), colspan, rowspan);
			}
		}
	}catch (Exception e){
		Debug.log(e.getMessage());
		return ServiceUtil.returnError(e.getMessage());
	}
	return res;
    }
    public static void updatePortalPagePortlet(Delegator delegator, String userLoginId, String portalPageId, String portalPortletId, String porletSeqId,
											String columnSeqId, String rowSeqId, int colspan, int rowspan) throws GenericEntityException{
		Map<String, Object> cond = UtilMisc.toMap("portalPageId", portalPageId, "portalPortletId", portalPortletId, "portletSeqId", porletSeqId);
		Map<String, Object> cond2 = FastMap.newInstance();
		cond2.putAll(cond);
		cond2.put("userLoginId", userLoginId);
    	List<GenericValue> tmp = delegator.findList("PortalPagePortlet", EntityCondition.makeCondition(cond2), null, null, null, false);
    	GenericValue portlet = EntityUtil.getFirst(tmp);
		boolean isCreatable = false;
		if(portlet == null){
			isCreatable = true;
			portlet = delegator.findOne("PortalPagePortlet", cond, false);
		}
		StringBuilder t1 = new StringBuilder();
		int length = columnSeqId.length();
		int remain = 5 - length;
		t1.append(StringUtils.repeat("0", remain));
		t1.append(columnSeqId);
		portlet.set("columnSeqId", t1.toString());
		t1 = new StringBuilder();
		length = rowSeqId.length();
		remain = 5 - length;
		t1.append(StringUtils.repeat("0", remain));
		t1.append(rowSeqId);
		portlet.set("rowSeqId", t1.toString());
		portlet.set("colspan", colspan);
		portlet.set("rowspan", rowspan);
		if(isCreatable){
			porletSeqId = delegator.getNextSeqId("PortalPagePortlet");
			portlet.set("portletSeqId", porletSeqId);
			portlet.set("userLoginId", userLoginId);
			portlet.create();
		}else{
			portlet.store();
		}
    }
}
