package com.olbius.administration.setting;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.administration.util.SettingUtil;

/**
 * @author minhhoa
 * @since May, 2016
 */
public abstract class ApplicationSetting {

	private Delegator delegator;
	private Object applicationSettingId;
	private Object applicationSettingTypeId;
	private Object applicationSettingEnumId;
	private Object partyId = "_NA_";
	private Object value;

	public ApplicationSetting(Delegator delegator, Object applicationSettingId, Object applicationSettingTypeId,
			Object applicationSettingEnumId, Object partyId) {
		super();
		this.delegator = delegator;
		this.applicationSettingId = applicationSettingId;
		this.applicationSettingTypeId = applicationSettingTypeId;
		this.applicationSettingEnumId = applicationSettingEnumId;
		this.partyId = partyId;
	}

	public Delegator getDelegator() {
		return delegator;
	}

	public Object getApplicationSettingId() {
		return applicationSettingId;
	}

	public Object getApplicationSettingTypeId() {
		return applicationSettingTypeId;
	}

	public Object getApplicationSettingEnumId() {
		return applicationSettingEnumId;
	}

	public Object getPartyId() {
		return partyId;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		GenericValue applicationSetting = SettingUtil.getValueApplicationSetting(delegator, applicationSettingId,
				partyId, applicationSettingTypeId, applicationSettingEnumId);
		if (UtilValidate.isNotEmpty(applicationSetting)) {
			value = applicationSetting.get("value");
		}
		return value;
	}

	public void store() throws GenericEntityException {
		SettingUtil.store(this);
	}
}
