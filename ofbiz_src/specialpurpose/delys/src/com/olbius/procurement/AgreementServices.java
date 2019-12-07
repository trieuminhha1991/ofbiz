package com.olbius.procurement;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


public class AgreementServices {
	public static final String module = AgreementServices.class.getName();
	public static final String resource = "DelysProcurementLabels";
    public static final String resourceError = "widgetErrorUiLabels";

    public static Map<String, Object> createAgreementPO(DispatchContext ctx, Map<String, Object> context) throws GenericServiceException {
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        try{
        	GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
        	context.put("userLogin", userLogin);
        	dispatcher.runSync("createAgreement", context);
        }catch(Exception e){
        	e.printStackTrace();
        	return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "CreateAgreementError", locale));
        }
        return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "CreateAgreementSuccess", locale));
	}
    public static Map<String, Object> getPartyRole(DispatchContext ctx, Map<String, Object> context) throws GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> res = FastMap.newInstance();
        String partyId = (String) context.get("partyId");
        try{
        	List<GenericValue> listRoleTypes = delegator.findList("PartyRole", EntityCondition.makeCondition("partyId", partyId), null, null, null, true);
        	res.put("listRoleTypes", listRoleTypes);
        }catch(Exception e){
        	e.printStackTrace();
        }
        return res;
	}
    
}
