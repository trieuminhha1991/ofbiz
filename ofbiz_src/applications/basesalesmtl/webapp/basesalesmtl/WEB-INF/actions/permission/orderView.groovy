import java.sql.Timestamp;
import java.util.List

import javax.servlet.DispatcherType;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basesales.util.SalesUtil;

boolean hasPermissionApproveOrder = false;
if (orderHeader) {
	GenericValue orderRoleOwner = EntityUtil.getFirst(delegator.findByAnd("OrderRole", ["orderId": orderHeader.orderId, "roleTypeId": "BILL_FROM_VENDOR"], null, false));
	if (orderRoleOwner != null) {
		GenericValue ownerId = delegator.findOne("Party", ["partyId": orderRoleOwner.partyId], false);
		if (ownerId.partyTypeId != "LEGAL_ORGANIZATION") hasPermissionApproveOrder = true;
	}
	
}
context.hasPermissionApproveOrder = hasPermissionApproveOrder;