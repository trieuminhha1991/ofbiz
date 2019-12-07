import org.ofbiz.entity.condition.EntityCondition;

employeeList = delegator.findList("PartyPersonPartyRole", EntityCondition.makeCondition("roleTypeId", "EMPLOYEE"), null ,null, null, false);
context.employeeList = employeeList;