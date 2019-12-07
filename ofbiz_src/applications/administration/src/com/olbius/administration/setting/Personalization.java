package com.olbius.administration.setting;

import org.ofbiz.entity.Delegator;

/**
 * @author minhhoa
 * @since May, 2016
 */
public class Personalization extends ApplicationSetting {

	public Personalization(Delegator delegator, Object applicationSettingId, Object applicationSettingTypeId,
			Object applicationSettingEnumId, Object partyId) {
		super(delegator, applicationSettingId, applicationSettingTypeId, applicationSettingEnumId, partyId);
	}

	public enum PersonalizationType {
		BIZ_MENU
	}

	public enum PersonalizationEnum {
		BIZ_MENU_STATE
	}
}
