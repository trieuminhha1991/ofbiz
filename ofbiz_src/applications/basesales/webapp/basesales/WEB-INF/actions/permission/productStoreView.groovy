import java.sql.Timestamp;
import java.util.List

import org.ofbiz.base.util.Debug

import com.olbius.basehr.util.SecurityUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil
import com.olbius.basesales.util.SalesPartyUtil.EmplRoleEnum;

import org.ofbiz.entity.util.EntityUtil;
import com.olbius.security.util.SecurityUtil;

boolean hasPermissionView = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_PRODUCTSTOREST_VIEW");
boolean isDistributor = SalesPartyUtil.isDistributor(delegator, userLogin.partyId);
if (isDistributor) {
	String userLoginPartyId = userLogin.partyId;
	String productStoreId = parameters.productStoreId;
	if (productStoreId) {
		List<GenericValue> productStores = delegator.findByAnd("ProductStore", ["productStoreId" : productStoreId, "payToPartyId" : userLoginPartyId], null, false);
		if (productStores) {
			hasPermissionView = true;
		}
	}
} else {
	if (hasPermissionView) {
		hasPermissionView = false;
		
		// get org by userlogin
		if (userLogin.partyId) {
			String userLoginId = userLogin.userLoginId;
			String productStoreId = parameters.productStoreId;
			if (productStoreId) {
				String orgId = SalesUtil.getCurrentOrganization(delegator, userLoginId);
				try {
					List<GenericValue> productStores = delegator.findByAnd("ProductStore", ["productStoreId" : productStoreId, "payToPartyId" : orgId], null, false);
					if (productStores) {
						hasPermissionView = true;
					} else {
						GenericValue productStore = delegator.findOne("ProductStore", ["productStoreId" : productStoreId], false);
						if (productStore) {
							boolean isCurrentOrg = SalesPartyUtil.hasRole(delegator, productStore.payToPartyId, EmplRoleEnum.DISTRIBUTOR);
							if (isCurrentOrg) {
								hasPermissionView = true;
							}
						}
						context.selectedSubMenuItem = "listProductStoresDis";
					}
				} catch (Exception e) {
					println("Warning at view product store: Parameter isStoreCompany is null");
				}
			}
		}
		
	} else {
		hasPermissionView = false;
	}
}
context.hasPermissionView = hasPermissionView;
