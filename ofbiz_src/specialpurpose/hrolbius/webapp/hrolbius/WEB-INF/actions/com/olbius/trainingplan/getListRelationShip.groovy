import com.olbius.util.PartyUtil;

partyGroupIdLogin = PartyUtil.getOrgByManager(userLogin.partyId, delegator);
context.partyGroupIdLogin = partyGroupIdLogin;