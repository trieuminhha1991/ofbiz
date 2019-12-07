package com.olbius.administration.security;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

public class SecurityServices {
	public static Map<String, Object> loadApplicationModuleTree(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object moduleId = context.get("moduleId");
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(moduleId)) {
				conditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
						moduleHierarchyRollDown(delegator, moduleId)));
			}
			conditions.add(EntityCondition.makeCondition("applicationType", EntityJoinOperator.EQUALS, "MODULE"));
			List<GenericValue> olbiusApplications = delegator.findList("OlbiusApplication",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("application"), null, false);
			result.put("value", olbiusApplications);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateOlbiusApplication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue olbiusApplication = delegator.makeValidValue("OlbiusApplication", context);
			olbiusApplication.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> createOlbiusPartyPermission(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue olbiusApplication = delegator.makeValidValue("OlbiusPartyPermission", context);
			olbiusApplication.create();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateOlbiusPartyPermission(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue olbiusApplication = delegator.makeValidValue("OlbiusPartyPermission", context);
			olbiusApplication.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listUserLoginInModule(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			olbiusPartyPermissionasStandardized(delegator);
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("applicationId")) {
				String applicationId = parameters.get("applicationId")[0];
				if (UtilValidate.isNotEmpty(applicationId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listAllConditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
							applicationHierarchyRollUp(delegator, applicationId)));
					if (parameters.containsKey("permissionId")) {
						String permissionId = parameters.get("permissionId")[0];
						if (UtilValidate.isNotEmpty(permissionId)) {
							List<EntityCondition> conditions = FastList.newInstance();
							conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
							conditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
									applicationHierarchyRollUp(delegator, applicationId)));
							conditions.add(EntityCondition.makeCondition("permissionId", EntityJoinOperator.IN,
									Arrays.asList(permissionId.split(","))));
							List<GenericValue> dummy = delegator.findList("OlbiusPartyPermission",
									EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
							listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
									EntityUtil.getFieldListFromEntityList(dummy, "partyId", true)));
						}
					}
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							partyHasPermissionInApplication(delegator, applicationId)));
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							partiesInOrg(delegator, (GenericValue) context.get("userLogin"))));
					listSortFields.add("partyId");
					EntityListIterator listIterator = delegator.find("OlbiusPartyPermissionDetail",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listGroupInModule(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			olbiusPartyPermissionasStandardized(delegator);
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("applicationId")) {
				String applicationId = parameters.get("applicationId")[0];
				if (UtilValidate.isNotEmpty(applicationId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listAllConditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
							applicationHierarchyRollUp(delegator, applicationId)));
					if (parameters.containsKey("permissionId")) {
						String permissionId = parameters.get("permissionId")[0];
						if (UtilValidate.isNotEmpty(permissionId)) {
							List<EntityCondition> conditions = FastList.newInstance();
							conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
							conditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
									applicationHierarchyRollUp(delegator, applicationId)));
							conditions.add(EntityCondition.makeCondition("permissionId", EntityJoinOperator.IN,
									Arrays.asList(permissionId.split(","))));
							List<GenericValue> dummy = delegator.findList("OlbiusPartyPermission",
									EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
							listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
									EntityUtil.getFieldListFromEntityList(dummy, "partyId", true)));
						}
					}
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							partyHasPermissionInApplication(delegator, applicationId)));
					listAllConditions.add(
							EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.EQUALS, "SECURITY_GROUP"));
					listSortFields.add("partyId");
					EntityListIterator listIterator = delegator.find("OlbiusPartyPermissionGroup",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static List<Object> partyHasPermissionInApplication(Delegator delegator, Object applicationId)
			throws GenericEntityException {
		List<Object> parties = FastList.newInstance();
		List<Object> applications = applicationHierarchyRollUp(delegator, applicationId);
		List<EntityCondition> conditions = FastList.newInstance();
		List<GenericValue> olbiusPartyPermissions = FastList.newInstance();
		List<Object> override = permissionOverrideInApplication(delegator, applicationId);
		for (Object x : applications) {
			conditions.clear();
			olbiusPartyPermissions.clear();
			if (x.equals(applicationId)) {
				conditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.EQUALS, x));
				olbiusPartyPermissions = delegator.findList("OlbiusPartyPermission",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isNotEmpty(olbiusPartyPermissions)) {
					parties.addAll(EntityUtil.getFieldListFromEntityList(olbiusPartyPermissions, "partyId", true));
				}
			} else {
				conditions.add(EntityCondition.makeCondition("permissionId", EntityJoinOperator.IN, override));
				conditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.EQUALS, x));
				olbiusPartyPermissions = delegator.findList("OlbiusPartyPermission",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isNotEmpty(olbiusPartyPermissions)) {
					parties.addAll(EntityUtil.getFieldListFromEntityList(olbiusPartyPermissions, "partyId", true));
				}
			}
		}
		return parties;
	}

	private static List<Object> permissionOverrideInApplication(Delegator delegator, Object applicationId)
			throws GenericEntityException {
		List<Object> override = FastList.newInstance();
		List<GenericValue> olbiusOverridePermissions = FastList.newInstance();
		List<Object> applications = applicationHierarchyRollUp(delegator, applicationId);
		for (Object x : applications) {
			olbiusOverridePermissions = delegator.findList("OlbiusOverridePermission",
					EntityCondition.makeCondition("applicationId", EntityJoinOperator.EQUALS, x), null, null, null, false);
			if (UtilValidate.isNotEmpty(olbiusOverridePermissions)) {
				override.addAll(EntityUtil.getFieldListFromEntityList(olbiusOverridePermissions, "overridePermissionId", true));
			}
		}
		Set<Object> setOverride = FastSet.newInstance();
		for (Object x : override) {
			setOverride.add(x);
			GenericValue olbiusPermission = delegator.findOne("OlbiusPermission",
					UtilMisc.toMap("permissionIncludeId", x), false);
			if (UtilValidate.isNotEmpty(olbiusPermission)) {
				setOverride.add(olbiusPermission.get("permissionId"));
			}
		}
		override.clear();
		override.addAll(setOverride);
		return override;
	}

	@SuppressWarnings("unused")
	private static List<Object> permissionOverrideInApplications(Delegator delegator, List<Object> applicationIds)
			throws GenericEntityException {
		List<Object> override = FastList.newInstance();
		for (Object applicationId : applicationIds) {
			List<GenericValue> olbiusOverridePermissions = FastList.newInstance();
			List<Object> applications = applicationHierarchyRollUp(delegator, applicationId);
			for (Object x : applications) {
				olbiusOverridePermissions = delegator.findList("OlbiusOverridePermission",
						EntityCondition.makeCondition("applicationId", EntityJoinOperator.EQUALS, x), null, null, null, false);
				if (UtilValidate.isNotEmpty(olbiusOverridePermissions)) {
					override.addAll(EntityUtil.getFieldListFromEntityList(olbiusOverridePermissions, "overridePermissionId", true));
				}
			}
			Set<Object> setOverride = FastSet.newInstance();
			for (Object x : override) {
				setOverride.add(x);
				GenericValue olbiusPermission = delegator.findOne("OlbiusPermission",
						UtilMisc.toMap("permissionIncludeId", x), false);
				if (UtilValidate.isNotEmpty(olbiusPermission)) {
					setOverride.add(olbiusPermission.get("permissionId"));
				}
			}
			override.clear();
			override.addAll(setOverride);
		}
		return override;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listOlbiusPermissionOfParty(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("applicationId") && parameters.containsKey("partyId")
					&& parameters.containsKey("moduleId")) {
				String applicationId = parameters.get("applicationId")[0];
				String partyId = parameters.get("partyId")[0];
				String moduleId = parameters.get("moduleId")[0];
				if (UtilValidate.isNotEmpty(applicationId) && UtilValidate.isNotEmpty(partyId)
						&& UtilValidate.isNotEmpty(moduleId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listAllConditions.add(
							EntityCondition.makeCondition("applicationId", EntityJoinOperator.EQUALS, applicationId));
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
					if (!applicationId.equals(moduleId)) {
						listAllConditions.add(EntityCondition.makeCondition("permissionId", EntityJoinOperator.IN,
								permissionOverrideInApplication(delegator, moduleId)));
					}
					listSortFields.add("permissionId");
					EntityListIterator listIterator = delegator.find("OlbiusPartyPermission",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listOlbiusPermissionOfAction(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("applicationId") && parameters.containsKey("type")) {
				String applicationId = parameters.get("applicationId")[0];
				String type = parameters.get("type")[0];
				if (UtilValidate.isNotEmpty(applicationId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");

					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							partyHasPermissionInApplication(delegator, applicationId)));
					listSortFields.add("permissionId");

					List<EntityCondition> conditions1 = FastList.newInstance();
					List<EntityCondition> conditions2 = FastList.newInstance();

					conditions1.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
							applicationHierarchyRollUp(delegator, applicationId)));
					conditions1.add(EntityCondition.makeCondition("permissionId", EntityJoinOperator.IN,
							permissionOverrideInApplication(delegator, applicationId)));

					conditions2.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.EQUALS, applicationId));

					listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition(conditions1),
							EntityJoinOperator.OR, EntityCondition.makeCondition(conditions2)));
					String entity = "OlbiusPartyPermissionAndUser";
					if ("GROUP".equals(type)) {
						entity = "OlbiusPartyPermissionAndGroup";
						listAllConditions.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.EQUALS, "SECURITY_GROUP"));
					}
					EntityListIterator listIterator = delegator.find(entity,
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> loadPermissionOfPartyInApplication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<GenericValue> dummy = delegator
					.findList(
							"OlbiusPartyPermission", EntityCondition.makeCondition(UtilMisc.toMap("applicationId",
									context.get("applicationId"), "partyId", context.get("partyId"))), null, null, null, true);
			result.put("permissions", EntityUtil.getFieldListFromEntityList(dummy, "permissionId", true));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static void olbiusPartyPermissionasStandardized(Delegator delegator) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("allow", EntityJoinOperator.EQUALS, null));
		EntityListIterator listIterator = delegator.find("OlbiusPartyPermission",
				EntityCondition.makeCondition(conditions), null, null, null, null);
		GenericValue value = null;
		while ((value = listIterator.next()) != null) {
			value.set("allow", true);
			value.store();
		}
		if (listIterator != null) {
			listIterator.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listUserLogin(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listSortFields.add("partyId");
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
					partiesInOrg(delegator, (GenericValue) context.get("userLogin"))));
			EntityListIterator listIterator = delegator.find("UserLoginDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listUserLoginNotInApplication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("moduleId")) {
				String moduleId = parameters.get("moduleId")[0];
				if (UtilValidate.isNotEmpty(moduleId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listSortFields.add("partyId");
					List<Object> partyHasPermissionInApplication = partyHasPermissionInApplication(delegator, moduleId);
					if (UtilValidate.isNotEmpty(partyHasPermissionInApplication)) {
						listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_IN,
								partyHasPermissionInApplication));
					}
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							partiesInOrg(delegator, (GenericValue) context.get("userLogin"))));
					EntityListIterator listIterator = delegator.find("UserLoginDetail",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listGroupNotInApplication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("moduleId")) {
				String moduleId = parameters.get("moduleId")[0];
				if (UtilValidate.isNotEmpty(moduleId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listSortFields.add("partyId");
					List<Object> partyHasPermissionInApplication = partyHasPermissionInApplication(delegator, moduleId);
					if (UtilValidate.isNotEmpty(partyHasPermissionInApplication)) {
						listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_IN,
								partyHasPermissionInApplication));
					}
					listAllConditions.add(
							EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.EQUALS, "SECURITY_GROUP"));
					EntityListIterator listIterator = delegator.find("Party",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static List<Object> partiesInOrg(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		Organization buildOrg = PartyUtil.buildOrg(delegator, organizationId, true, false);
		return EntityUtil.getFieldListFromEntityList(buildOrg.getEmployeeInOrg(delegator), "partyId", true);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> addUsersToModule(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			List<String> parties = (List<String>) context.get("parties[]");
			List<String> permissionIds = (List<String>) context.get("permissionIds[]");
			Object fromDate = context.get("fromDate");
			if (UtilValidate.isEmpty(fromDate)) {
				fromDate = new Timestamp(System.currentTimeMillis());
			}
			Map<String, Object> user = UtilMisc.toMap("applicationId", context.get("applicationId"), "fromDate",
					fromDate, "thruDate", context.get("thruDate"), "allow", context.get("allow"));
			// createOlbiusPartyPermission
			for (String partyId : parties) {
				user.put("partyId", partyId);
				for (String permissionId : permissionIds) {
					user.put("permissionId", permissionId);
					dispatcher.runSync("createOlbiusPartyPermission", user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> loadModuleDiagram(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object applicationId = context.get("applicationId");
			List<Map<String, Object>> diagram = FastList.newInstance();
			Map<String, Object> module = FastMap.newInstance();
			// add this module
			GenericValue olbiusApplication = delegator.findOne("OlbiusApplication",
					UtilMisc.toMap("applicationId", applicationId), false);
			module.put("id", olbiusApplication.get("applicationId"));
			module.put("parent", olbiusApplication.get("moduleId"));
			module.put("text", olbiusApplication.get("name"));
			diagram.add(module);
			if (UtilValidate.isNotEmpty(olbiusApplication.get("moduleId"))) {
				diagram.addAll(moduleParent(delegator, olbiusApplication.get("moduleId")));
			}
			diagram.addAll(moduleChildren(delegator, olbiusApplication.get("applicationId")));
			result.put("diagram", diagram);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static List<Map<String, Object>> moduleParent(Delegator delegator, Object moduleId)
			throws GenericEntityException {
		List<Map<String, Object>> diagram = FastList.newInstance();
		Map<String, Object> module = FastMap.newInstance();
		GenericValue olbiusApplication = delegator.findOne("OlbiusApplication",
				UtilMisc.toMap("applicationId", moduleId), false);
		module.put("id", olbiusApplication.get("applicationId"));
		module.put("parent", olbiusApplication.get("moduleId"));
		module.put("text", olbiusApplication.get("name"));
		diagram.add(module);
		if (UtilValidate.isNotEmpty(olbiusApplication.get("moduleId"))) {
			diagram.addAll(moduleParent(delegator, olbiusApplication.get("moduleId")));
		}
		return diagram;
	}

	private static List<Map<String, Object>> moduleChildren(Delegator delegator, Object applicationId)
			throws GenericEntityException {
		List<Map<String, Object>> diagram = FastList.newInstance();
		List<GenericValue> olbiusApplications = delegator.findList("OlbiusApplication",
				EntityCondition.makeCondition(UtilMisc.toMap("moduleId", applicationId, "applicationType", "MODULE")),
				null, null, null, false);
		for (GenericValue x : olbiusApplications) {
			Map<String, Object> module = FastMap.newInstance();
			module.put("id", x.get("applicationId"));
			module.put("parent", x.get("moduleId"));
			module.put("text", x.get("name"));
			diagram.add(module);
			diagram.addAll(moduleChildren(delegator, x.get("applicationId")));
		}
		return diagram;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listActionInModule(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			olbiusPartyPermissionasStandardized(delegator);
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("applicationId")) {
				String applicationId = parameters.get("applicationId")[0];
				if (UtilValidate.isNotEmpty(applicationId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");

					if (parameters.containsKey("permissionId")) {
						String permissionId = parameters.get("permissionId")[0];
						if (UtilValidate.isNotEmpty(permissionId)) {
							List<EntityCondition> conditions = FastList.newInstance();
							conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
							conditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
									applicationHierarchyRollUp(delegator, applicationId)));
							conditions.add(EntityCondition.makeCondition("permissionId", EntityJoinOperator.IN,
									Arrays.asList(permissionId.split(","))));
							List<GenericValue> dummy = delegator.findList("OlbiusPartyPermission",
									EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
							listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
									EntityUtil.getFieldListFromEntityList(dummy, "partyId", true)));
						}
					}
					listAllConditions.add(EntityCondition.makeCondition("moduleId", EntityJoinOperator.EQUALS, applicationId));
					listAllConditions.add(EntityCondition.makeCondition("applicationType", EntityJoinOperator.EQUALS, "ENTITY"));
					listSortFields.add("applicationId");
					EntityListIterator listIterator = delegator.find("OlbiusApplication",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listApplicationOverride(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("applicationId") && parameters.containsKey("permissionId")
					&& parameters.containsKey("applicationType")) {
				String applicationId = parameters.get("applicationId")[0];
				String permissionId = parameters.get("permissionId")[0];
				String applicationType = parameters.get("applicationType")[0];
				if (UtilValidate.isNotEmpty(applicationId) && UtilValidate.isNotEmpty(permissionId)
						&& UtilValidate.isNotEmpty(applicationType)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listAllConditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
							applicationHierarchyRollDown(delegator, applicationId)));
					listAllConditions.add(EntityCondition.makeCondition("applicationType", EntityJoinOperator.EQUALS,
							applicationType));
					listSortFields.add("applicationId");
					EntityListIterator listIterator = delegator.find("OlbiusApplication",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listApplicationOfParty(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			olbiusPartyPermissionasStandardized(delegator);
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("partyId") && parameters.containsKey("applicationType")) {
				String partyId = parameters.get("partyId")[0];
				String applicationType = parameters.get("applicationType")[0];
				if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(applicationType)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listAllConditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.IN,
							applicationPartyHasPermission(delegator, partyId)));
					listAllConditions.add(EntityCondition.makeCondition("applicationType", EntityJoinOperator.EQUALS,
							applicationType));
					listSortFields.add("applicationId");
					EntityListIterator listIterator = delegator.find("OlbiusApplication",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static List<Object> applicationPartyHasPermission(Delegator delegator, Object partyId)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("allow", EntityJoinOperator.NOT_EQUAL, false));
		conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
		List<GenericValue> olbiusPartyPermissions = delegator.findList("OlbiusPartyPermission",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		return EntityUtil.getFieldListFromEntityList(olbiusPartyPermissions, "applicationId", true);
	}

	private static List<Object> moduleHierarchyRollUp(Delegator delegator, Object applicationId)
			throws GenericEntityException {
		List<Object> applicationIds = FastList.newInstance();
		applicationIds.add(applicationId);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.EQUALS, applicationId));
		conditions.add(EntityCondition.makeCondition("applicationType", EntityJoinOperator.EQUALS, "MODULE"));
		List<GenericValue> olbiusApplications = delegator.findList("OlbiusApplication",
				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("application"), null, false);
		for (GenericValue x : olbiusApplications) {
			Object moduleId = x.get("moduleId");
			if (UtilValidate.isNotEmpty(moduleId)) {
				applicationIds.addAll(moduleHierarchyRollUp(delegator, moduleId));
			}
		}
		return applicationIds;
	}

	private static List<Object> moduleHierarchyRollDown(Delegator delegator, Object moduleId)
			throws GenericEntityException {
		List<Object> applicationIds = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("moduleId", EntityJoinOperator.EQUALS, moduleId));
		conditions.add(EntityCondition.makeCondition("applicationType", EntityJoinOperator.EQUALS, "MODULE"));
		List<GenericValue> olbiusApplications = delegator.findList("OlbiusApplication",
				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("application"), null, false);
		for (GenericValue x : olbiusApplications) {
			applicationIds.add(x.get("applicationId"));
			applicationIds.addAll(moduleHierarchyRollDown(delegator, x.get("applicationId")));
		}
		return applicationIds;
	}

	private static List<Object> applicationHierarchyRollUp(Delegator delegator, Object applicationId)
			throws GenericEntityException {
		List<Object> applicationIds = FastList.newInstance();
		applicationIds.add(applicationId);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("applicationId", EntityJoinOperator.EQUALS, applicationId));
		List<GenericValue> olbiusApplications = delegator.findList("OlbiusApplication",
				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("application"), null, false);
		for (GenericValue x : olbiusApplications) {
			Object moduleId = x.get("moduleId");
			if (UtilValidate.isNotEmpty(moduleId)) {
				applicationIds.addAll(moduleHierarchyRollUp(delegator, moduleId));
			}
		}
		return applicationIds;
	}

	private static List<Object> applicationHierarchyRollDown(Delegator delegator, Object moduleId)
			throws GenericEntityException {
		List<Object> applicationIds = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("moduleId", EntityJoinOperator.EQUALS, moduleId));
		List<GenericValue> olbiusApplications = delegator.findList("OlbiusApplication",
				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("application"), null, false);
		for (GenericValue x : olbiusApplications) {
			applicationIds.add(x.get("applicationId"));
			applicationIds.addAll(moduleHierarchyRollDown(delegator, x.get("applicationId")));
		}
		return applicationIds;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listUserGroupSecurity(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listSortFields.add("partyId");
			listAllConditions
					.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.EQUALS, "SECURITY_GROUP"));
			EntityListIterator listIterator = delegator.find("Party", EntityCondition.makeCondition(listAllConditions),
					null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listUserInGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("partyId")) {
				String partyId = parameters.get("partyId")[0];
				if (UtilValidate.isNotEmpty(partyId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listSortFields.add("partyId");
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							partiesInSecurityGroup(delegator, partyId)));
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							partiesInOrg(delegator, (GenericValue) context.get("userLogin"))));
					EntityListIterator listIterator = delegator.find("UserLoginDetail",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listUserNotInGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("partyId")) {
				String partyId = parameters.get("partyId")[0];
				if (UtilValidate.isNotEmpty(partyId)) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					listSortFields.add("partyId");
					List<Object> partiesInSecurityGroup = partiesInSecurityGroup(delegator, partyId);
					if (UtilValidate.isNotEmpty(partiesInSecurityGroup)) {
						listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_IN,
								partiesInSecurityGroup));
					}
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							partiesInOrg(delegator, (GenericValue) context.get("userLogin"))));
					EntityListIterator listIterator = delegator.find("UserLoginDetail",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static List<Object> partiesInSecurityGroup(Delegator delegator, Object partyId)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom",
				"SECURITY_GROUP", "partyRelationshipTypeId", "SECURITY_GROUP_REL")));
		List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		return EntityUtil.getFieldListFromEntityList(partyRelationships, "partyIdTo", true);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> addUserToGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<String> parties = (List<String>) context.get("parties[]");
			Object groupId = context.get("groupId");
			GenericValue partyRole = delegator.findOne("PartyRole",
					UtilMisc.toMap("partyId", groupId, "roleTypeId", "SECURITY_GROUP"), false);
			if (UtilValidate.isEmpty(partyRole)) {
				// createPartyRole
				dispatcher.runSync("createPartyRole",
						UtilMisc.toMap("partyId", groupId, "roleTypeId", "SECURITY_GROUP", "userLogin", userLogin));
			}
			for (String x : parties) {
				partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", x, "roleTypeId", "EMPLOYEE"),
						false);
				if (UtilValidate.isEmpty(partyRole)) {
					// createPartyRole
					dispatcher.runSync("createPartyRole",
							UtilMisc.toMap("partyId", x, "roleTypeId", "EMPLOYEE", "userLogin", userLogin));
				}
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("partyIdFrom", groupId, "partyIdTo", x, "roleTypeIdFrom", "SECURITY_GROUP",
								"roleTypeIdTo", "EMPLOYEE", "partyRelationshipTypeId", "SECURITY_GROUP_REL")));
				List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(partyRelationships)) {
					dispatcher.runSync("createPartyRelationship",
							UtilMisc.toMap("partyIdFrom", groupId, "partyIdTo", x, "roleTypeIdFrom", "SECURITY_GROUP",
									"roleTypeIdTo", "EMPLOYEE", "partyRelationshipTypeId", "SECURITY_GROUP_REL",
									"userLogin", userLogin));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> deleteUserInGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object groupId = context.get("groupId");
			Object partyId = context.get("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(
					UtilMisc.toMap("partyIdFrom", groupId, "partyIdTo", partyId, "roleTypeIdFrom", "SECURITY_GROUP",
							"roleTypeIdTo", "EMPLOYEE", "partyRelationshipTypeId", "SECURITY_GROUP_REL")));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : partyRelationships) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> resetPassword(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", context.get("userLoginId")), false);
			userLogin.set("currentPassword", "$SHA$qVmjS8sN$Qf6kZF-Xjl0DXm9kOTbQ2XysTkg");
			userLogin.set("requirePasswordChange", "Y");
			userLogin.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
}
