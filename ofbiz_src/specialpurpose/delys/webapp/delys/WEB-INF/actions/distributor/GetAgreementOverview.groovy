

agreementId = parameters.agreementId;
if (agreementId) {
	agreement = delegator.findOne("Agreement", ["agreementId" : agreementId], false);
	if (agreement) {
		context.agreement = agreement;
		List<GenericValue> agreementTerms = new ArrayList<GenericValue>();
		List<GenericValue> agreementItems = new ArrayList<GenericValue>();
		List<GenericValue> agreementWorkEffortApplics = new ArrayList<GenericValue>();
		List<GenericValue> agreementRoles = new ArrayList<GenericValue>();
		agreementTerms = delegator.findByAnd("AgreementTerm", ["agreementId" : agreementId], null, false);
		agreementItems = delegator.findByAnd("AgreementItem", ["agreementId" : agreementId], null, false);
		
		context.agreementTerms = agreementTerms;
		
		agreementItemsMapList = [];
		for (agreementItem in agreementItems) {
			agreementItemMap = [:];
			agreementItemMap.agreementItem = agreementItem;
			List<GenericValue> agreementPromo = delegator.findByAnd("AgreementPromoAppl", ["agreementItemSeqId" : agreementItem.agreementItemSeqId, "agreementId" : agreementItem.agreementId], null, false);
			List<GenericValue> agreementTerm = delegator.findByAnd("AgreementTerm", ["agreementItemSeqId" : agreementItem.agreementItemSeqId, "agreementId" : agreementItem.agreementId], null, false);
			List<GenericValue> agreementProduct = delegator.findByAnd("AgreementProductAppl", ["agreementItemSeqId" : agreementItem.agreementItemSeqId, "agreementId" : agreementItem.agreementId], null, false);
			List<GenericValue> agreementParty = delegator.findByAnd("AgreementPartyApplic", ["agreementItemSeqId" : agreementItem.agreementItemSeqId, "agreementId" : agreementItem.agreementId], null, false);
			List<GenericValue> agreementGeo = delegator.findByAnd("AgreementGeographicalApplic", ["agreementItemSeqId" : agreementItem.agreementItemSeqId, "agreementId" : agreementItem.agreementId], null, false);
			List<GenericValue> agreementFacility = delegator.findByAnd("AgreementFacilityAppl", ["agreementItemSeqId" : agreementItem.agreementItemSeqId, "agreementId" : agreementItem.agreementId], null, false);
			agreementItemMap.agreementPromo = agreementPromo;
			agreementItemMap.agreementTerm = agreementTerm;
			agreementItemMap.agreementProduct = agreementProduct;
			agreementItemMap.agreementParty = agreementParty;
			agreementItemMap.agreementGeo = agreementGeo;
			agreementItemMap.agreementFacility = agreementFacility;
			agreementItemsMapList.add(agreementItemMap);
		}
		context.agreementItemsMapList = agreementItemsMapList;
		
		agreementWorkEffortApplics = delegator.findByAnd("AgreementWorkEffortApplic", ["agreementId" : agreementId], null, false);
		context.agreementWorkEffortApplics = agreementWorkEffortApplics;
		
		agreementRoles = delegator.findByAnd("AgreementRole", ["agreementId" : agreementId], null, false);
		context.agreementRoles = agreementRoles;
	}
}