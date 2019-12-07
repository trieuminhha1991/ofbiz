package com.olbius.security.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.olbius.entity.cache.OlbiusCache;
import com.olbius.security.api.Application;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.api.Permission;
import com.olbius.security.util.SecurityUtil;

public class SecurityServices {

	public static final String module = SecurityServices.class.getName();

	public static Map<String, Object> clearCache(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {

		if (!(ctx.getSecurity() instanceof OlbiusSecurity)) {
			return ServiceUtil.returnSuccess();
		}

		String entity = (String) context.get("entity");

		boolean distributed = (Boolean)context.get("distributed");

		if (distributed && ctx.getDelegator().useDistributedCacheClear()) {
			ctx.getDispatcher().runAsync("distributedClearCache", UtilMisc.toMap("entity", entity, "distributed", Boolean.FALSE), false);
		} else {
			if (entity == null || entity.isEmpty()) {
				SecurityUtil.getOlbiusSecurity(ctx.getSecurity()).getProvider().clearCache();
			} else {
				SecurityUtil.getOlbiusSecurity(ctx.getSecurity()).getProvider().clearCache(entity);
			}
		}
		
		return ServiceUtil.returnSuccess();
	}

	private static OlbiusCache<Set<String>> relationCache = new OlbiusCache<Set<String>>() {

		@Override
		public Set<String> loadCache(Delegator delegator, String key) throws Exception {

			Set<String> set = new TreeSet<String>();

			ModelEntity model = delegator.getModelEntity(key);
			Iterator<ModelRelation> iterator = model.getRelationsIterator();

			while (iterator.hasNext()) {
				ModelRelation relation = iterator.next();
				for (ModelKeyMap keyMap : relation.getKeyMaps()) {
					set.add(keyMap.getFieldName());
				}
			}

			return set;
		}

	};

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createOrUpdateValue(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String entity = (String) context.get("entity");

		Map<String, String> value = (Map<String, String>) context.get("value");

		if(value.get("check") != null && !value.get("check").isEmpty()) {
			String[] tmp = value.get("check").trim().split(",");
			Map<String, Object> map = new HashMap<String, Object>();
			for(String s : tmp) {
				map.put(s, value.get(s));
			}
			List<GenericValue> list = delegator.findByAnd(entity, map, null, false);
			if(!list.isEmpty()) {
				return ServiceUtil.returnSuccess();
			}
			value.remove("check");
		}
		
		ModelEntity model = delegator.getModelEntity(entity);

		Map<String, Object> tmp = new HashMap<String, Object>();
		Map<String, Object> tmp2 = new HashMap<String, Object>();

		Set<String> set = new TreeSet<String>();

		int i = 0;

		boolean createFlag = false;

		for (String s : model.getPkFieldNames()) {
			if (value.get(s) != null) {
				i++;
				tmp.put(s, value.get(s));
			} else if (relationCache.get(delegator, entity).contains(s) || "fromDate".equals(s)) {
				i++;
				createFlag = true;
				set.add(s);
			}
		}
		
		for(String s: value.keySet()) {
			if(tmp.get(s) == null) {
				
				if("true".equals(value.get(s)) || "false".equals(value.get(s))) {
					tmp2.put(s, Boolean.valueOf(value.get(s)));
				} else if(" ".equals(value.get(s))) {
					tmp2.put(s, null);
				} else {
					tmp2.put(s, value.get(s));
				}
			}
		}

		if (model.getPkFieldNames().size() > i) {
			throw new GenericServiceException("Error pk size");
		}

		if (tmp.get("fromDate") != null) {
			tmp.put("fromDate", new Timestamp(Long.parseLong((String) tmp.get("fromDate"))));
		}

		if (createFlag) {
			for (String s : set) {
				if ("fromDate".equals(s)) {
					tmp.put("fromDate", new Timestamp(System.currentTimeMillis()));
				} else {
					tmp.put(s, delegator.getNextSeqId(entity));
				}
			}
		}

		GenericValue genericValue = delegator.findOne(entity, tmp, false);

		if (genericValue != null) {
			if(!SecurityUtil.getOlbiusSecurity(ctx.getSecurity()).olbiusHasPermission(userLogin, Permission.UPDATE, Application.ENTITY, entity)) {
				throw new GenericServiceException("Update " + entity + " : Access deny!");
			}
			genericValue.putAll(tmp2);
		} else {
			if(!SecurityUtil.getOlbiusSecurity(ctx.getSecurity()).olbiusHasPermission(userLogin, Permission.CREATE, Application.ENTITY, entity)) {
				throw new GenericServiceException("Create " + entity + " : Access deny!");
			}
			genericValue = delegator.makeValue(entity);
			genericValue.putAll(tmp);
			genericValue.putAll(tmp2);
		}

		delegator.createOrStore(genericValue);

		return ServiceUtil.returnSuccess();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> removeValue(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		String entity = (String) context.get("entity");

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		if(!SecurityUtil.getOlbiusSecurity(ctx.getSecurity()).olbiusHasPermission(userLogin, Permission.DELETE, Application.ENTITY, entity)) {
			throw new GenericServiceException("Delete " + entity + " : Access deny!");
		}
		
		Map<String, String> value = (Map<String, String>) context.get("value");

		ModelEntity model = delegator.getModelEntity(entity);

		Map<String, Object> tmp = new HashMap<String, Object>();

		int i = 0;

		for (String s : model.getPkFieldNames()) {
			if (value.get(s) != null) {
				i++;
				tmp.put(s, value.get(s));
			}
		}

		if (model.getPkFieldNames().size() > i) {
			throw new GenericServiceException("Error pk size");
		}

		if (tmp.get("fromDate") != null) {
			tmp.put("fromDate", new Timestamp(Long.parseLong((String) tmp.get("fromDate"))));
		}
		
		GenericValue genericValue = delegator.findOne(entity, tmp, false);

		if (genericValue != null) {
			delegator.removeValue(genericValue);
		}

		return ServiceUtil.returnSuccess();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> thruDateValue(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		String entity = (String) context.get("entity");
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		if(!SecurityUtil.getOlbiusSecurity(ctx.getSecurity()).olbiusHasPermission(userLogin, Permission.UPDATE, Application.ENTITY, entity)) {
			throw new GenericServiceException("Update " + entity + " : Access deny!");
		}
		
		Timestamp time = (Timestamp) context.get("time");
		
		if(time == null) {
			time = new Timestamp(System.currentTimeMillis());
		}

		Map<String, String> value = (Map<String, String>) context.get("value");

		ModelEntity model = delegator.getModelEntity(entity);

		Map<String, Object> tmp = new HashMap<String, Object>();

		int i = 0;

		for (String s : model.getPkFieldNames()) {
			if (value.get(s) != null) {
				i++;
				tmp.put(s, value.get(s));
			}
		}

		if (model.getPkFieldNames().size() > i) {
			throw new GenericServiceException("Error pk size");
		}

		if (tmp.get("fromDate") != null) {
			tmp.put("fromDate", new Timestamp(Long.parseLong((String) tmp.get("fromDate"))));
		}
		
		GenericValue genericValue = delegator.findOne(entity, tmp, false);

		if (genericValue != null) {
			genericValue.put("thruDate", time);
			delegator.store(genericValue);
		}

		return ServiceUtil.returnSuccess();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> viewEntity(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

		if (parameters.get("entity") == null || parameters.get("entity").length == 0) {
			throw new GenericServiceException("entity not found!");
		}

		String entity = parameters.get("entity")[0];
		
		if(!SecurityUtil.getOlbiusSecurity(ctx.getSecurity()).olbiusHasPermission(userLogin, Permission.VIEW, Application.ENTITY, entity)) {
			throw new GenericServiceException(entity + " : Access deny!");
		}

		String[] conditionField = parameters.get("conditionField");
		String[] conditionValue = parameters.get("conditionValue");
		String[] conditionGroup = parameters.get("conditionGroup");

		Map<String, EntityCondition> conditions = new HashMap<String, EntityCondition>();

		List<EntityCondition> list = new ArrayList<EntityCondition>();

		if (conditionField != null && conditionField.length > 0) {
			for (int i = 0; i < conditionField.length; i++) {
				EntityCondition condition = EntityCondition.makeCondition(conditionField[i], EntityOperator.EQUALS,
						conditionValue[i]);
				if (conditionGroup != null && conditionGroup.length > 0) {
					if (conditions.get(conditionGroup[i]) == null) {
						conditions.put(conditionGroup[i], condition);
					} else {
						condition = EntityCondition.makeCondition(EntityOperator.OR, condition,
								conditions.get(conditionGroup[i]));
						conditions.put(conditionGroup[i], condition);
					}
				} else {
					list.add(condition);
				}
			}
		}

		if (!conditions.isEmpty()) {
			for (String s : conditions.keySet()) {
				list.add(conditions.get(s));
			}
		}

		listAllConditions.addAll(list);

		delegator.getModelReader().getModelEntity(entity);

		listSortFields.add("-lastUpdatedStamp");
		
		listIterator = delegator.find(entity, EntityCondition.makeCondition(listAllConditions, EntityOperator.AND),
				null, null, listSortFields, opts);

		successResult.put("listIterator", listIterator);
		return successResult;
	}

}
