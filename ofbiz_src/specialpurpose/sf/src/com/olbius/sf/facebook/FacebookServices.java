package com.olbius.sf.facebook;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class FacebookServices {
	public static String updateFacebookSettings(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String fid = (String) context.get("fbsettingid");
		String appid = (String) context.get("appid");
		String pageurl = (String) context.get("pageurl");
		String fbtheme = (String) context.get("fbTheme");
		String commentWidth = (String) context.get("commentWidth");
		String accessToken = (String) context.get("accessToken");
		String supplierPage = (String) context.get("supplierpage");
		GenericValue productSocial = delegator.makeValue("FacebookSettings");
		productSocial.put("fbSettingId", fid);
		productSocial.put("fbAppId", appid);
		productSocial.put("fbPageUrl", pageurl);
		productSocial.put("fbTheme", fbtheme);
		productSocial.put("fbCommentWidth", commentWidth);
		productSocial.put("fbAccessToken", accessToken);
		productSocial.put("fbSupplierPage", supplierPage);
		try {
			delegator.createOrStore(productSocial);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("msg", e.getMessage());
			return "error";
		}
		request.setAttribute("msg", "success");
		return "success";
	}
}
