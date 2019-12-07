package com.olbius.basehr.util;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class MultiOrganization implements OrganizationConfig {

	@Override
	public String getLastOrganization(Delegator delegator, String userLoginId) {
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		if(userLogin != null) {
			return userLogin.getString("lastOrg");
		}else {
			return null;
		}
	}
	
	@Override
	public String getCurrentOrganization(Delegator delegator, String userLoginId) {
		// TODO Auto-generated method stub
		try {
			return PartyUtil.getCurrentOrganization(delegator, userLoginId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return null;
	}
}
