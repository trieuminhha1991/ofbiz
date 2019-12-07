import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;

GenericValue userLogin = (GenericValue)context.get("userLogin");
String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

String partyId = userLogin.getString("partyId");

List<String> listPartyId = PartyUtil.getDepartmentOfEmployee(delegator, partyId, new Timestamp(System.currentTimeMillis()));

context.listPartyId = listPartyId;

for(int i = 0; i < listPartyId.size(); i++){
	List<GenericValue> listPartyRela = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", companyStr, "partyIdTo", listPartyId[i])), null, null, null, false);
	listPartyRela = EntityUtil.filterByDate(listPartyRela);
	if(UtilValidate.isNotEmpty(listPartyRela)){
		context.departmentId = listPartyId[i];
	}
}

