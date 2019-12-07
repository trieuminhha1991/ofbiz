package com.olbius.baseecommerce.common;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class CommonServices {

    public final static String module = CommonServices.class.getName();
    public static final String resource = "CommonUiLabels";

    public static Map<String, Object> createUserLoginSecurityGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String userLoginId = (String) context.get("userLoginId");
		String organizationId = (String) context.get("organizationId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		String groupId = (String) context.get("groupId");
		try {
			delegator.create("UserLoginSecurityGroup",
					UtilMisc.toMap("userLoginId", userLoginId, "organizationId", organizationId, "fromDate", fromDate, "groupId", groupId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return res;
	}
}
