package com.olbius.administration.util;

import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.administration.BrandLogo;
import com.olbius.administration.setting.ApplicationSetting;

import javolution.util.FastList;
import javolution.util.FastMap;

public class SettingUtil {

	public static GenericValue getValueApplicationSetting(Delegator delegator, Object applicationSettingId,
			Object partyId, Object applicationSettingTypeId, Object applicationSettingEnumId) {
		GenericValue applicationSetting = GenericValue.NULL_VALUE;
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(applicationSettingId)) {
				conditions.add(EntityCondition.makeCondition("applicationSettingId", EntityJoinOperator.EQUALS,
						applicationSettingId));
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
			}
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("applicationSettingTypeId",
					applicationSettingTypeId, "applicationSettingEnumId", applicationSettingEnumId)));
			List<GenericValue> applicationSettings = delegator.findList("ApplicationSetting",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(applicationSettings)) {
				applicationSetting = EntityUtil.getFirst(applicationSettings);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return applicationSetting;
	}

	public static Map<String, String> getBrandLogo(Delegator delegator, Object partyId,
			Object applicationSettingEnumId) {
		Map<String, String> brandLogo = FastMap.newInstance();
		GenericValue applicationSetting = getValueApplicationSetting(delegator, null, partyId, "LOGO_GROUP_IMG",
				applicationSettingEnumId);
		if (UtilValidate.isNotEmpty(applicationSetting)) {
			brandLogo.put("value", applicationSetting.getString("value"));
			brandLogo.put("applicationSettingId", applicationSetting.getString("applicationSettingId"));
		}
		return brandLogo;
	}

	public static void store(BrandLogo logo) throws GenericEntityException {
		logo.getDelegator().storeByCondition("ApplicationSetting", UtilMisc.toMap("value", logo.getOriginalUrl()),
				EntityCondition.makeCondition(UtilMisc.toMap("applicationSettingId", logo.getOriginalUrlId(),
						"applicationSettingTypeId", "LOGO_GROUP_IMG", "partyId", logo.getPartyId(),
						"applicationSettingEnumId", "BRAND_LOGO")));
		logo.getDelegator().storeByCondition("ApplicationSetting", UtilMisc.toMap("value", logo.getBase64()),
				EntityCondition.makeCondition(UtilMisc.toMap("applicationSettingId", logo.getBase64Id(),
						"applicationSettingTypeId", "LOGO_GROUP_IMG", "partyId", logo.getPartyId(),
						"applicationSettingEnumId", "BRAND_LOGO_BASE64")));
	}

	public static void store(ApplicationSetting setting) throws GenericEntityException {
		setting.getDelegator().storeByCondition("ApplicationSetting", UtilMisc.toMap("value", setting.getValue()),
				EntityCondition.makeCondition(UtilMisc.toMap("applicationSettingId", setting.getApplicationSettingId(),
						"applicationSettingTypeId", setting.getApplicationSettingTypeId(), "partyId",
						setting.getPartyId(), "applicationSettingEnumId", setting.getApplicationSettingEnumId())));
	}

	public static void trustAllCerts() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
				}
			} };
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
