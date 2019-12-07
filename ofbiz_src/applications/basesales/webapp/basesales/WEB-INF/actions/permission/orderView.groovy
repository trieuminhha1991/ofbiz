import java.sql.Timestamp;
import java.util.List

import javax.servlet.DispatcherType;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;

import com.olbius.basesales.util.SalesUtil;

boolean hasPermissionApproveOrder = false;
if (orderHeader) {
	String userLoginId = userLogin.userLoginId;
	String oraginizationIdCurrent = SalesUtil.getCurrentOrganization(delegator, userLoginId);
	
	if (oraginizationIdCurrent) {
		GenericValue orderRoleOwner = delegator.findOne("OrderRole", ["orderId": orderHeader.orderId, "roleTypeId": "BILL_FROM_VENDOR", "partyId": oraginizationIdCurrent], false);
		if (orderRoleOwner != null) context.checkPermissionSuccess = true;
	}
	
	try {
		Map<String, Object> resultCheckPermissionApproveOrder = dispatcher.runSync("checkPermissionApproveOrder", ["orderId": orderHeader.orderId, "userLogin": userLogin, "locale": locale]);
		if (ServiceUtil.isSuccess(resultCheckPermissionApproveOrder)) {
			hasPermissionApproveOrder = (Boolean) resultCheckPermissionApproveOrder.get("hasPermission");
			context.nextStatusId = resultCheckPermissionApproveOrder.nextStatusId;
		}
	} catch (Exception e) {
		Debug.logWarning("Warning at Service checkPermissionApproveOrder", "orderView");
	}
}
context.hasPermissionApproveOrder = hasPermissionApproveOrder;