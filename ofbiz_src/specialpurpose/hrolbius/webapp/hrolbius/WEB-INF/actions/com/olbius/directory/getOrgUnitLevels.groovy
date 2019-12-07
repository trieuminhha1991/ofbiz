import org.ofbiz.entity.condition.EntityCondition;
orgUnitLevels = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId","ORGANIZATION_UNIT"), null, null ,null, false);
orgUnitLevelsById = delegator.findList("OrgUnitRole", null, null, null ,null, false);
context.orgUnitLevels = orgUnitLevels;
context.orgUnitLevelsById = orgUnitLevelsById;