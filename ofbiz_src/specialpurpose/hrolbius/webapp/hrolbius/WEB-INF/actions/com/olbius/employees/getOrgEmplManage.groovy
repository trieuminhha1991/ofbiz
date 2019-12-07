import org.ofbiz.base.util.UtilProperties;

import com.olbius.util.PartyUtil;

partyId = userLogin.getString("partyId");
Properties generalProp = UtilProperties.getProperties("general");
String defaultOrganizationPartyId = (String)generalProp.get("ORGANIZATION_PARTY");
if(PartyUtil.isAdmin(partyId, delegator)){
	orgId = defaultOrganizationPartyId;
}else{
	orgId = PartyUtil.getOrgByManager(partyId, delegator);
}
context.orgId = orgId;