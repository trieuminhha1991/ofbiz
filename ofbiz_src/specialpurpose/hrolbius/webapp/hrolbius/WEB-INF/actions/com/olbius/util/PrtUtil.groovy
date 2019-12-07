import com.olbius.util.PartyUtil;

//Get Head of HR
headHRId = PartyUtil.getHrmAdmin(delegator);
context.headHRId = headHRId;

//Get CEO
ceoId = PartyUtil.getCEO(delegator);
context.ceoId = ceoId;