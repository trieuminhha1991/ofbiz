package com.olbius.security.core.cache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.entity.cache.OlbiusCache;

public abstract class OlbiusSecurityCache<T> extends OlbiusCache<T>{
	protected EntityCondition thruDateCondition(Timestamp timestamp) {

		EntityCondition condition = null;

		List<EntityCondition> entityConditions = new ArrayList<EntityCondition>();

		entityConditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

		entityConditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				new Timestamp(System.currentTimeMillis())));

		condition = EntityCondition.makeCondition(entityConditions, EntityOperator.OR);

		return condition;

	}
}
