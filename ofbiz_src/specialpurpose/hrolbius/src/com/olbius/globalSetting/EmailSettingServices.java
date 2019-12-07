package com.olbius.globalSetting;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class EmailSettingServices {
	public static final String module = EmailSettingServices.class.getName();
	
	public static Map<String, Object> createEmailSetting(DispatchContext dctx, Map<String,Object> context){
		Delegator delegator = dctx.getDelegator();
		//Get parameters
		String authUser = (String)context.get("authUser");
		String authPass = (String)context.get("authPass");
		String primaryFlag = (String)context.get("primaryFlag");
		boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));
		GenericValue emailSetting = delegator.makeValue("EmailSetting");
		String emailSettingId = delegator.getNextSeqId("EmailSetting");
		emailSetting.set("authPass", useEncryption ? HashCrypt.cryptUTF8(getHashType(), null, authPass) : authPass);
		emailSetting.put("authUser", authUser);
		emailSetting.put("primaryFlag", primaryFlag);
		emailSetting.put("emailSettingId", emailSettingId);
		
		try {
			if("Y".equals(primaryFlag)) {
				List<GenericValue> listEmailSetting = delegator.findByAnd("EmailSetting", UtilMisc.toMap("primaryFlag", "Y"), null, false);
				for(GenericValue item : listEmailSetting) {
					item.put("primaryFlag", "N");
					item.store();
				}
			}
			emailSetting.create();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("emailSettingId", emailSettingId);
		return result;
	}
	
	public static Map<String, Object> deleteEmailSetting(DispatchContext dctx, Map<String,Object> context){
		Delegator delegator = dctx.getDelegator();
		//Get parameters
		String emailSettingId = (String)context.get("emailSettingId");
		try {
			GenericValue emailSetting = delegator.findOne("EmailSetting", UtilMisc.toMap("emailSettingId", emailSettingId), false); 
			if(emailSetting.getString("primaryFlag").equals("Y")) {
				return ServiceUtil.returnError("Bạn không thể xóa, email đang được sử dụng");
			}else {
				delegator.removeValue(emailSetting);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	
	public static Map<String, Object> updateEmailSetting(DispatchContext dctx, Map<String,Object> context){
		Delegator delegator = dctx.getDelegator();
		//Get parameters
		String emailSettingId = (String)context.get("emailSettingId");
		String authUser = (String)context.get("authUser");
		String primaryFlag = (String)context.get("primaryFlag");
		try {
			GenericValue emailSetting = delegator.findOne("EmailSetting", UtilMisc.toMap("emailSettingId", emailSettingId), false); 
			if("Y".equals(primaryFlag)) {
				List<GenericValue> listEmailSetting = delegator.findByAnd("EmailSetting", UtilMisc.toMap("primaryFlag", "Y"), null, false);
				for(GenericValue item : listEmailSetting) {
					item.put("primaryFlag", "N");
					item.store();
				}
			}
			emailSetting.put("primaryFlag", primaryFlag);
			emailSetting.put("authUser", authUser);
			emailSetting.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	
	public static String getHashType() {
        String hashType = UtilProperties.getPropertyValue("security.properties", "password.encrypt.hash.type");

        if (UtilValidate.isEmpty(hashType)) {
            Debug.logWarning("Password encrypt hash type is not specified in security.properties, use SHA", module);
            hashType = "SHA";
        }

        return hashType;
    }
}
