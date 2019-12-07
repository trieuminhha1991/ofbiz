import java.util.List;

List<GenericValue> quotations = delegator.findByAnd("ProductQuotation", null, null, false);

localData = [];
if (quotations != null) {
	for (quotation in quotations) {
		row = [:];
		row.productQuotationId = quotation.productQuotationId;
		row.quotationName = quotation.quotationName;
		row.salesChannel = quotation.salesChannel;
		row.currencyUomId = quotation.currencyUomId;
		row.fromDate = quotation.fromDate;
		row.thruDate = quotation.thruDate;
		row.statusId = quotation.statusId;
		partyApplies = "";
		roleTypes = delegator.findByAnd("ProductQuotationRoleTypeAndRoleType", ["productQuotationId" : quotation.productQuotationId], null, false);
		if (roleTypes != null) {
			if (roleTypes.size() == 1) {
				roleType = roleTypes.get(0);
				if (roleType.description != null) {
					partyApplies += roleType.description;
				} else {
					partyApplies += roleType.roleTypeId;
				}
			} else if (roleTypes.size() > 1) {
				roleType0 = roleTypes.get(0);
				if (roleType0.description != null) partyApplies += roleType0.description;
				else partyApplies += roleType0.roleTypeId;
				
				for (i = 1; i < roleTypes.size(); i++) {
					roleType = roleTypes.get(i);
					if (roleType.description != null) {
						partyApplies += ", " + roleType.description;
					} else {
						partyApplies += ", " + roleType.roleTypeId;
					}
				}
			}
		}
		row.partyApplies = partyApplies;
		localData.add(row);
	}
}
context.localData = localData;

