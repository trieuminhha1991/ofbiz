package com.olbius.common.util;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.util.List;

public class BaseUtil {
	
	public static String getCurrentWebSite(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		String partyId = userLogin.getString("partyId");
		String webSiteId = "";
		String attrName = "webSiteId"; //EntityUtilProperties.getPropertyValue("ecommerce.properties", "party.attribute.webSiteId", "", delegator);
		GenericValue partyAttribute = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", attrName), false);
		if (UtilValidate.isNotEmpty(partyAttribute)) {
			webSiteId = partyAttribute.getString("attrValue");
		}
		if (UtilValidate.isEmpty(webSiteId)) {
			List<GenericValue> webSites = delegator.findList("WebSite", EntityCondition.makeCondition("isDefault", "Y"), null, null, null, true);
			if (UtilValidate.isNotEmpty(webSites)) {
				webSiteId = EntityUtil.getFirst(webSites).getString("webSiteId");
			}
		}
		return webSiteId;
	}
	
	public static String getCurrentVisualThemeBackOffice(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		String visualThemeId = null;
		GenericValue visualTheme = EntityUtil.getFirst(delegator.findByAnd("VisualTheme", UtilMisc.toMap("visualThemeSetId", "BACKOFFICE"), null, false));
		if (visualTheme != null) {
			visualThemeId = visualTheme.getString("visualThemeId");
		}
		return visualThemeId;
	}
    public static Timestamp convertDateStrToDate(String dateStr) {
        Timestamp date = null;
        try {
            if (UtilValidate.isNotEmpty(dateStr)) {
                Long requirementStartDateL = Long.parseLong(dateStr);
                date = new Timestamp(requirementStartDateL);
            }
        } catch (Exception e) {
            return null;
        }
        return date;
    }
}
