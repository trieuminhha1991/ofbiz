import org.ofbiz.service.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

organizations = delegator.findByAnd("PartyAcctgPrefAndGroupAndRole", UtilMisc.toMap("roleTypeId", "INTERNAL_ORGANIZATIO"), null, false);
context.organizations = organizations;

suppliers = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", "SUPPLIER_AGENT"), null, false);
context.suppliers = suppliers;

List<GenericValue> listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "PORT_OF_DISCHARGE")), null, null, null, false);
context.listFacility = listFacility;