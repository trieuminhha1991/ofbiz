import org.ofbiz.entity.condition.EntityCondition;
statusList = delegator.findList("StatusItem",EntityCondition.makeCondition("statusTypeId","JOB_REQ_STATUS"), null, null ,null, false);
context.statusList = statusList;