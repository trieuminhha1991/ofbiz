import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;

import com.olbius.basehr.util.MultiOrganizationUtil;

import com.olbius.basesales.loyalty.*;

def partyId = parameters.partyId;
if (UtilValidate.isNotEmpty(partyId)) {
	List<EntityCondition> conditions = FastList.newInstance();
	conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "INTERNAL_ORGANIZATIO")));
	List<GenericValue> listPartyRelationship = delegator.findList("PartyRelationship",
			EntityCondition.makeCondition(conditions), null, null, null, false);
	if (UtilValidate.isNotEmpty(listPartyRelationship)) {
		context.partyRelationship = EntityUtil.getFirst(listPartyRelationship);
	}
	Map<String, Object> liability = FastMap.newInstance();
	liability.put("userLogin", userLogin);
	liability.put("organizationPartyId", organizationId);
	liability.put("partyId", partyId);
	Map<String, Object> out = dispatcher.runSync("getLiabilityParty", liability);
	context.totalPayable = out.totalPayable;
	context.totalReceivable = out.totalReceivable;
	context.totalLiability = out.totalLiability;
	BigDecimal loyaltyPoint = LoyaltyUtil.getTotalPoint(delegator, partyId, userLogin);
	context.loyaltyPoint = loyaltyPoint;
}
