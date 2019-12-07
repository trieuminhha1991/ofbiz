import org.ofbiz.party.party.PartyHelper;

import com.olbius.util.PartyUtil;

context.ceoPartyId = PartyUtil.getCEO(delegator);
context.ceoName = PartyHelper.getPartyName(delegator, context.ceoPartyId, false);
context.departMgrName = PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false); 