import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.Delegator

Delegator dlg = delegator

party = dlg.findOne("Party", UtilMisc.toMap("partyId", parameters.partyId), false)

context.party = party