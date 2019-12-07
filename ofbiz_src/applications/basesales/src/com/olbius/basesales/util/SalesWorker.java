package com.olbius.basesales.util;

import java.sql.Timestamp;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;

import com.olbius.common.util.BaseUtil;
import com.olbius.security.util.SecurityUtil;

public class SalesWorker {
	private static final String module = SalesWorker.class.getName();
	
	public static boolean checkPermissionEditQuotation(GenericValue productQuotation, String productQuotationId, Delegator delegator, GenericValue userLogin, Security security) {
		if (delegator == null || userLogin == null) return false;
		if (productQuotation == null && UtilValidate.isEmpty(productQuotationId)) {
			return false;
		}
		if (productQuotation == null) {
			try {
				productQuotation = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling checkPermissionEditQuotation method: " + e.toString();
				Debug.logError(e, errMsg, module);
				return false;
			}
		}
		if (productQuotation == null) {
			return false;
		}
		
		if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATION_EDIT")) {
			return false;
		}
		
		boolean checkThruDate = false;
		Timestamp nowTimstamp = UtilDateTime.nowTimestamp();
		Timestamp thruDate = productQuotation.getTimestamp("thruDate");
		if (thruDate == null || thruDate.compareTo(nowTimstamp) > 0) {
			checkThruDate = true;
		}
		if (!checkThruDate) return false;
		
		boolean checkStatus = false;
		String currentStatusId = productQuotation.getString("statusId");
		if (currentStatusId != null) {
			if (currentStatusId.equals("QUOTATION_CREATED")) {
				checkStatus = true;
			} else if (!currentStatusId.equals("QUOTATION_CANCELLED")) {
				String quotationAllowEditSpecialStr = SalesUtil.getSystemConfigValue(delegator, "quotationAllowEditSpecial");
				if (quotationAllowEditSpecialStr != null && "true".equals(quotationAllowEditSpecialStr.toLowerCase())) {
					checkStatus = true;
				}
			}
		}
		if (checkThruDate && checkStatus) {
			return true;
		}
		return false;
	}
	
	public static String getImageTextBase64(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		String resourceValue = null;
		
		String visualThemeId = BaseUtil.getCurrentVisualThemeBackOffice(delegator, userLogin);
		GenericValue logoFirst = EntityUtil.getFirst(delegator.findByAnd("ImageDataTextResource", UtilMisc.toMap("visualThemeId", visualThemeId, "resourceTypeEnumId", "VT_LOGO_IMAGE_BASE64"), null, false));
		if (logoFirst != null) {
			resourceValue = logoFirst.getString("resourceValue");
		}
		
		return resourceValue;
	}
}
