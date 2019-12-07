import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.basehr.util.MultiOrganizationUtil;

String orderId = parameters.orderId;
List<GenericValue> listSupplierParty = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "SUPPLIER_AGENT")), null, null, null, false);
String partyId = listSupplierParty.get(0).getString("partyId");
context.partySupplier = partyId;
