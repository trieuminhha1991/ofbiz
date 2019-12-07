import org.ofbiz.service.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.olbius.basehr.util.MultiOrganizationUtil;

organizations = delegator.findByAnd("PartyAcctgPrefAndGroupAndRole", UtilMisc.toMap("roleTypeId", "INTERNAL_ORGANIZATIO"), null, false);
context.organizations = organizations;

GenericValue userLogin = (GenericValue)context.get("userLogin");
String userLoginId = userLogin.getString("userLoginId");
context.userLoginId= userLoginId;

List<GenericValue> listSupplierParty = delegator.findList("ListPartySupplierByRole", null, null, null, null, false);

//suppliers = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", "SUPPLIER_AGENT"), null, false);
context.suppliers = listSupplierParty;

List<GenericValue> listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "PORT_OF_DISCHARGE")), null, null, null, false);
context.listFacility = listFacility;


String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
context.organizationPartyId = companyStr;
