import java.util.ArrayList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.util.*;
import java.util.Calendar;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import com.olbius.basehr.util.PartyHelper;
import org.ofbiz.entity.util.EntityUtil;


partyAddressTo = "";
partyAddressFrom = "";

if(payment != null){
	String partyIdFrom = (String)payment.get("partyIdFrom");
	String partyIdTo = (String)payment.get("partyIdTo");
	List<GenericValue> partyContactMech = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_LOCATION")), null, null, null, false);
	partyContactMech = EntityUtil.filterByDate(partyContactMech);
	
	if (!partyContactMech.isEmpty()){
		String contactMechId = partyContactMech.get(0).getString("contactMechId");
		partyAddressFrom = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", contactMechId));			
	}
	
	List<GenericValue> partyContactMechTo = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdTo, "contactMechPurposeTypeId", "PRIMARY_LOCATION")), null, null, null, false);
	partyContactMechTo = EntityUtil.filterByDate(partyContactMechTo);
	
	if (!partyContactMechTo.isEmpty()){
		String contactMechToId = partyContactMechTo.get(0).getString("contactMechId");
		partyAddressTo = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", contactMechToId));			
	}
}

context.partyAddressFrom = partyAddressFrom;
context.partyAddressTo = partyAddressTo;