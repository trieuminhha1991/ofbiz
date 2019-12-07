import org.ofbiz.entity.condition.EntityCondition;

emplPositionTypeId = parameters.emplPositionTypeId;
standardRatingId = parameters.standardRatingId;

jobRatingList = delegator.findList("ListJobRating", EntityCondition.makeCondition("standardRatingId", standardRatingId), null, null, null, false);
context.jobRatingList = jobRatingList;