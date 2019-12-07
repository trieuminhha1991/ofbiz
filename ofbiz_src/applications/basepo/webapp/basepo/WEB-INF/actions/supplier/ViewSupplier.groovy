import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;

import com.olbius.basepo.supplier.SupplierServices;

if (party) {
	def parties = delegator.findList("PartySupplierAndContactMech",
			EntityCondition.makeCondition(UtilMisc.toMap("partyId", party.partyId)), null, null, null, false);
	if (parties) {
		def supplier = EntityUtil.getFirst(parties);
		if (supplier) {
			def uom = delegator.findOne("Uom",
					UtilMisc.toMap("uomId", supplier.preferredCurrencyUomId), false);
			if (uom) {
				supplier.preferredCurrencyUomId = uom.get("description", locale);
			}
		}
		context.supplier = supplier;
	}

	def partyTax = SupplierServices.getPartyTaxAuthInfo(delegator, party.partyId);
	if (partyTax) {
		if (partyTax.taxAuth) {
			def taxAuthorityAndDetail = delegator.findList("TaxAuthorityAndDetail",
					EntityCondition.makeCondition(UtilMisc.toMap("taxAuthGeoId", partyTax.taxAuth.split("\\|")[0], "taxAuthPartyId", partyTax.taxAuth.split("\\|")[1])), null, null, null, false);
			if (taxAuthorityAndDetail) {
				def taxAuthority = EntityUtil.getFirst(taxAuthorityAndDetail);
				partyTax.taxAuth = taxAuthority.groupName;
			}
		}
	}
	context.partyTax = partyTax;
}