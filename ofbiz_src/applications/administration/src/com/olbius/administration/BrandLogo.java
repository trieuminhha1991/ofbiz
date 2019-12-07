package com.olbius.administration;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.administration.util.SettingUtil;
import com.olbius.basehr.util.PartyUtil;

public class BrandLogo {
	private Delegator delegator;
	private Object partyId;
	private String originalUrl = null;
	private String originalUrlId = null;
	private String base64 = null;
	private String base64Id = null;

	public BrandLogo(Delegator delegator, Object partyId) {
		super();
		this.delegator = delegator;
		this.partyId = partyId;
		this.setPartyId(partyId);
	}

	public BrandLogo(Delegator delegator) throws GenericEntityException {
		super();
		this.delegator = delegator;
		this.partyId = PartyUtil.getRootOrganization(delegator);
		this.setPartyId(this.partyId);
	}

	public Object getPartyId() {
		return partyId;
	}

	private void setPartyId(Object object) {
		this.partyId = object;
		Map<String, String> dummy = SettingUtil.getBrandLogo(delegator, object, "BRAND_LOGO");
		this.originalUrl = dummy.get("value");
		this.originalUrlId = dummy.get("applicationSettingId");
		dummy = SettingUtil.getBrandLogo(delegator, object, "BRAND_LOGO_BASE64");
		this.base64 = dummy.get("value");
		this.base64Id = dummy.get("applicationSettingId");
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	public String getBase64() {
		return base64;
	}

	public Delegator getDelegator() {
		return delegator;
	}

	public String getOriginalUrlId() {
		return originalUrlId;
	}

	public String getBase64Id() {
		return base64Id;
	}

	public InputStream getInputStream(HttpServletRequest request) throws Exception {
		StringBuilder uri = new StringBuilder();
		uri.append(request.getScheme());
		uri.append("://");
		uri.append(request.getServerName());
		uri.append(":");
		uri.append(request.getServerPort());
		uri.append(originalUrl.replaceAll("\\\\", "/"));
		URL url = new URL(uri.toString());
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		return is;
	}

	public void store() throws GenericEntityException {
		SettingUtil.store(this);
	}
}
