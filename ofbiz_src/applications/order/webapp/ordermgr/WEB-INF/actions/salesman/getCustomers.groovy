routeId = parameters.routeId;
context.listCustomer = delegator.findByAnd("PartyRelationshipToPartyOlbius", [partyIdFrom: routeId,partyRelationshipTypeId:"GROUP_ROLLUP"], null, true);
