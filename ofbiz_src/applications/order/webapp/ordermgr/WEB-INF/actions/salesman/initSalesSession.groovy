import org.ofbiz.entity.condition.*;

partyId = parameters.userLogin.partyId;//request.getSession().getAttribute("person").partyId;
exprBldr = new EntityConditionBuilder();
context.listProductStore = delegator.findList("ProductStorePartyView", exprBldr.LIKE(partyId: partyId), null, null, null, false);

partyRelationshipTypeId = "DELYS_SALESMAN";
context.listRoute = delegator.findByAnd("PartyRelationshipFromPartyOlbius", [partyIdTo: partyId,partyRelationshipTypeId:"DELYS_SALESMAN"], null, true);
