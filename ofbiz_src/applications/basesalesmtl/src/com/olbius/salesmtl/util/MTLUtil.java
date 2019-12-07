package com.olbius.salesmtl.util;

import java.util.Arrays;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;

import javolution.util.FastList;

public class MTLUtil {
	static List<String> groupPermissions_GT = Arrays.asList("HRMADMIN",
															"SALES_MANAGER",
															"SALESADMIN_MANAGER",
															"SALESADMIN_GT",
															"SALES_CSM_GT",
															"SALES_RSM_GT",
															"SALES_ASM_GT",
															"SALESSUP_GT",
															"SALESMAN_GT",
															"DISTRIBUTOR_ADMIN");
	static List<String> groupPermissions_MT = Arrays.asList("HRMADMIN",
															"SALES_MANAGER",
															"SALESADMIN_MANAGER",
															"SALESADMIN_MT",
															"SALES_CSM_MT",
															"SALES_RSM_MT",
															"SALES_ASM_MT",
															"SALESSUP_MT",
															"SALESMAN_MT",
															"DISTRIBUTOR_ADMIN");
	public static boolean partyHasSecurityGroupPermission(Delegator delegator, String groupId, Object partyId) throws GenericEntityException {
		List<GenericValue> userLogins = delegator.findList("UserLogin",
				EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId), null, null, null, false);
		for (GenericValue x : userLogins) {
			if (hasSecurityGroupPermission(delegator, groupId, x)) {
				return true;
			}
		}
		return false;
	}
	public static boolean hasSecurityGroupPermission(Delegator delegator, String groupId, GenericValue userLogin) {
		return hasSecurityGroupPermission(delegator, groupId, userLogin.getString("userLoginId"), false);
	}
	public static boolean hasSecurityGroupPermission(Delegator delegator, String groupId, GenericValue userLogin, boolean above) {
		return hasSecurityGroupPermission(delegator, groupId, userLogin.getString("userLoginId"), above);
	}
	public static boolean hasSecurityGroupPermission(Delegator delegator, String groupId, String userLoginId, boolean above) {
		boolean result = false;
		EntityListIterator userLoginSecurityGroups = null;
		try {
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
			
			List<String> groupPermissions = FastList.newInstance();
			if (groupPermissions_GT.contains(groupId)) {
				groupPermissions = groupPermissions_GT;
			} else if (groupPermissions_MT.contains(groupId)) {
				groupPermissions = groupPermissions_MT;
			}
			
			if (UtilValidate.isNotEmpty(userLoginId)) {
				List<EntityCondition> conditions = FastList.newInstance();
				if (above) {
					List<String> groupIds = FastList.newInstance();
					for (String s : groupPermissions) {
						groupIds.add(s);
						if (s.contains(groupId)) {
							break;
						}
					}
					conditions.add(EntityCondition.makeCondition("groupId", EntityJoinOperator.IN, groupIds));
				} else {
					conditions.add(EntityCondition.makeCondition("groupId", EntityJoinOperator.EQUALS, groupId));
				}
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("userLoginId", userLoginId, "organizationId", organizationId)));
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setMaxRows(1);
				findOptions.setLimit(1);
				userLoginSecurityGroups = delegator.find("UserLoginSecurityGroup",
						EntityCondition.makeCondition(conditions), null, null, null, findOptions);
				if (userLoginSecurityGroups.getResultsTotalSize() != 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			result = false;
		} finally {
			if (userLoginSecurityGroups != null) {
				try {
					userLoginSecurityGroups.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
