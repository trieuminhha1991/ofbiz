import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

emplPositionTypeId = parameters.emplPositionTypeId;
EntityCondition en1 =  EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId);
EntityCondition en2 =  EntityCondition.makeCondition("emplPositionTypeId", null);
EntityCondition en = EntityCondition.makeCondition(EntityJoinOperator.OR, en1, en2);
standardRatingList = delegator.findList("StandardRating", en, null, null, null, false);
context.standardRatingList = standardRatingList;