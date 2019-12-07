package com.olbius.baseecommerce.party;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class ProfileServices {

    public final static String module = ProfileServices.class.getName();
    public static final String resource = "CommonUiLabels";

    public static Map<String, Object> createUserLogin(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			ModelService model = ctx.getModelService("createUserLogin");
	        Map<String, Object> inputMap = model.makeValid(context, ModelService.IN_PARAM);
	        dispatcher.runSync("createUserLogin", inputMap);
	        String userLoginId = (String) context.get("userLoginId");
	        GenericValue e = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
	        if(UtilValidate.isNotEmpty(e)){
			e.setString("lastOrg", "_NA_");
			e.store();
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
    public static Map<String, Object> checkUserExist(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String userLoginId = (String) context.get("userLoginId");
		try {
	        GenericValue e = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
	        if(UtilValidate.isNotEmpty(e)){
	        	res.put("status", "success");
	        }else{
	        	res.put("status", "error");
	        }
		} catch (Exception e) {
			e.printStackTrace();
			res.put("status", "error");
		}
		return res;
	}
}
