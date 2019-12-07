package com.olbius.administration.synchronization;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.model.ModelViewEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class SynchronizationServices {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listRecurrenceRules(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listSortFields.add("recurrenceRuleId");
			EntityListIterator listIterator = delegator.find("RecurrenceRule",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listEntitySync(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listSortFields.add("entitySyncId");
			EntityListIterator listIterator = delegator.find("EntitySync",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listEntitySyncHistory(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listSortFields.add("-startDate");
			EntityListIterator listIterator = delegator.find("EntitySyncHistoryDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listEntityGroupEntrySync(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listSortFields.add("entitySyncId");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("isServer")) {
				String isServer = parameters.get("isServer")[0];
				if (UtilValidate.isNotEmpty(isServer)) {
					if ("Y".equals(isServer)) {
						listAllConditions
								.add(EntityCondition.makeCondition("forPullOnly", EntityJoinOperator.EQUALS, "Y"));
					} else {
						listAllConditions
								.add(EntityCondition.makeCondition("forPushOnly", EntityJoinOperator.EQUALS, "Y"));
					}
				}
			}
			EntityListIterator listIterator = delegator.find("EntityGroupEntrySync",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPOSOffline(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("forPullOnly", EntityJoinOperator.EQUALS, "Y"));
			listSortFields.add("facilityId");
			EntityListIterator listIterator = delegator.find("EntitySync",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateRecurrenceRule(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue recurrenceRule = delegator.makeValidValue("RecurrenceRule", context);
			recurrenceRule.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createEntityGroupEntries(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object entityGroupId = context.get("entityGroupId");
			Object applEnumId = context.get("applEnumId");
			List<String> entityOrPackage = (List<String>) context.get("entityOrPackage");
			for (String s : entityOrPackage) {
				delegator.create("EntityGroupEntry",
						UtilMisc.toMap("entityGroupId", entityGroupId, "entityOrPackage", s, "applEnumId", applEnumId));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> createEntityGroupEntry(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue recurrenceRule = delegator.makeValidValue("EntityGroupEntry", context);
			recurrenceRule.create();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteEntityGroupEntry(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue recurrenceRule = delegator.makeValidValue("EntityGroupEntry", context);
			recurrenceRule.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> loadEntityGroupId(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			String isServer = UtilProperties.getPropertyValue("administration.properties", "common.config.isServer");
			if ("Y".equals(isServer)) {
				conditions.add(EntityCondition.makeCondition("forPullOnly", EntityJoinOperator.EQUALS, "Y"));
			} else {
				conditions.add(EntityCondition.makeCondition("forPushOnly", EntityJoinOperator.EQUALS, "Y"));
			}
			List<GenericValue> values = delegator.findList("EntitySyncIncludeGroupSync",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			result.put("values", values);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> loadEntitiesNotInGroup(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object entityGroupId = context.get("entityGroupId");
			List<GenericValue> entityGroupEntries = delegator.findList("EntityGroupEntry",
					EntityCondition.makeCondition(
							UtilMisc.toMap("entityGroupId", entityGroupId, "applEnumId", "ESIA_INCLUDE")),
					null, null, null, true);
			List<String> entitiesInGroup = EntityUtil.getFieldListFromEntityList(entityGroupEntries, "entityOrPackage",
					true);

			ModelReader reader = delegator.getModelReader();
			List<Map<String, Object>> values = FastList.newInstance();
			Collection<String> entities = reader.getEntityNames();
			for (String entity : entities) {
				ModelEntity modelEntity = reader.getModelEntity((String) entity);
				if (modelEntity instanceof ModelViewEntity) {
					continue;
				}
				if ("org.ofbiz".equals(delegator.getEntityGroupName(modelEntity.getEntityName()))) {
					String entityName = modelEntity.getEntityName();
					if (!(entitiesInGroup.contains(entityName))) {
						Map<String, Object> value = FastMap.newInstance();
						value.put("text", entityName);
						value.put("value", entityName);
						values.add(value);
					}
				}
			}
			result.put("values", values);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> createPOSOffline(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue productStore = delegator.findOne("ProductStore",
					UtilMisc.toMap("productStoreId", context.get("productStoreId")), false);
			String entitySyncId = delegator.getNextSeqId("EntitySync");
			delegator.create("EntitySync",
					UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_NOT_STARTED", "syncSplitMillis",
							Long.valueOf("600000"), "syncEndBufferMillis", Long.valueOf("0"), "keepRemoveInfoHours",
							Double.valueOf("24"), "forPushOnly", "N", "forPullOnly", "Y", "facilityId",
							productStore.get("inventoryFacilityId")));

			delegator.create("EntitySyncIncludeGroup",
					UtilMisc.toMap("entitySyncId", entitySyncId, "entityGroupId", "ADMIN5501"));
			delegator.create("EntitySyncIncludeGroup",
					UtilMisc.toMap("entitySyncId", entitySyncId, "entityGroupId", "ACC5501"));
			delegator.create("EntitySyncIncludeGroup",
					UtilMisc.toMap("entitySyncId", entitySyncId, "entityGroupId", "HRM5501"));
			delegator.create("EntitySyncIncludeGroup",
					UtilMisc.toMap("entitySyncId", entitySyncId, "entityGroupId", "PO5501"));
			delegator.create("EntitySyncIncludeGroup",
					UtilMisc.toMap("entitySyncId", entitySyncId, "entityGroupId", "LOG5501"));
			delegator.create("EntitySyncIncludeGroup",
					UtilMisc.toMap("entitySyncId", entitySyncId, "entityGroupId", "SALES5501"));
			delegator.create("EntitySyncIncludeGroup",
					UtilMisc.toMap("entitySyncId", entitySyncId, "entityGroupId", "POS5501"));

		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> reactiveSynchronization(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue entitySync = delegator.findOne("EntitySync",
					UtilMisc.toMap("entitySyncId", context.get("entitySyncId")), false);
			entitySync.set("runStatusId", "ESR_NOT_STARTED");
			Timestamp lastSuccessfulSynchTime = entitySync.getTimestamp("lastSuccessfulSynchTime");
			if (UtilValidate.isNotEmpty(lastSuccessfulSynchTime)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(lastSuccessfulSynchTime.getTime());
				calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
				lastSuccessfulSynchTime = new Timestamp(calendar.getTimeInMillis());
				entitySync.set("lastSuccessfulSynchTime", lastSuccessfulSynchTime);
			}
			entitySync.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

}
