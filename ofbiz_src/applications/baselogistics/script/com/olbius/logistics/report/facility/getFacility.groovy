import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.Delegator

Delegator dlg = delegator

values = dlg.findByAnd("Facility", UtilMisc.toMap("ownerPartyId", userLogin.getString("partyId")), null, false)

facilities = []

for(value in values) {
    facilities << value.getString("facilityId")
}

if(facilities.size() > 0) {
    text = "["

    flag = false

    for(facility in facilities) {
        if(flag) {
            text += ","
        }
        text += "\'" + facility + "\'"
        flag = true;
    }

    text += "]"

    context.facilities = text
}
