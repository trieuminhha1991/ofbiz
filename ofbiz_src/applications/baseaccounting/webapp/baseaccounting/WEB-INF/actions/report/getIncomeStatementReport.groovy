import org.ofbiz.entity.Delegator;   
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

List<EntityCondition> listAllConditions = FastList.newInstance();
List<String> listSortFields = FastList.newInstance();

listAllConditions.add(EntityUtil.getFilterByDateExpr());
listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyRelationshipTypeId", "CUSTOMER_REL"), 
	EntityJoinOperator.OR, EntityCondition.makeCondition("partyRelationshipTypeId", "CONTACT_REL")));
listSortFields.add("firstName");

customers = delegator.findList("PartyRelationshipAndPartyFrom", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
    		
context.customers = customers;