import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.basehr.util.MultiOrganizationUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityOperator;

GenericValue userLogin = (GenericValue)context.get("userLogin");
String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
context.organizationPartyId = companyStr;

GenericValue partyOrg = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", companyStr), false);

context.partyOrg = partyOrg;
