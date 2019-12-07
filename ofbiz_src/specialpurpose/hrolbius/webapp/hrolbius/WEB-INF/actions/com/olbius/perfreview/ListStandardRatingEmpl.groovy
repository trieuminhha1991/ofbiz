import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

employeePartyId = parameters.employeePartyId;
context.employeePartyId = employeePartyId;

GenericValue userLogin = (GenericValue) context.get("userLogin");
String partyId = userLogin.getString("partyId");
context.partyId = partyId;

String emplPositionTypeId = parameters.emplPositionTypeId;

EntityCondition en1 =  EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId);
EntityCondition en2 =  EntityCondition.makeCondition("emplPositionTypeId", null);
EntityCondition en = EntityCondition.makeCondition(EntityJoinOperator.OR, en1, en2);
context.standardRatings = delegator.findList("StandardRating", en, null, null, null, false);