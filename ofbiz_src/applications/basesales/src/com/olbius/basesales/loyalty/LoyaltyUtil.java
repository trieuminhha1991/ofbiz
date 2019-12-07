package com.olbius.basesales.loyalty;

import java.math.BigDecimal;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;

import com.olbius.basehr.util.MultiOrganizationUtil;

public class LoyaltyUtil {
	public static final String module = LoyaltyUtil.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
	public static BigDecimal getCurrentPointCustomer(Delegator delegator, String partyId, String productStoreId, GenericValue userLogin) throws GenericEntityException {
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,(String) userLogin.get("userLoginId"));
		
		BigDecimal point = BigDecimal.ZERO;
		List<GenericValue> listLoyaltyPoint = delegator.findByAnd("LoyaltyPoint", UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId, 
				"ownerPartyId", organizationPartyId), UtilMisc.toList("loyaltyPointId"), false);
		
		if (UtilValidate.isNotEmpty(listLoyaltyPoint)){
			point = point.add(listLoyaltyPoint.get(0).getBigDecimal("point"));
		}
		
		return point;
	}
	public static BigDecimal getTotalPoint(Delegator delegator, String partyId, GenericValue userLogin) throws GenericEntityException {
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,(String) userLogin.get("userLoginId"));
		BigDecimal point = BigDecimal.ZERO;
		EntityListIterator list = null;
		EntityFindOptions options = new EntityFindOptions();
		options.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
		list = delegator.find("LoyaltyPoint", EntityCondition.makeCondition(
				UtilMisc.toList(
					EntityCondition.makeCondition("partyId", partyId),
					EntityCondition.makeCondition("ownerPartyId", organizationPartyId))), null, null, null, options);
		GenericValue e = null;
		while((e = list.next()) != null){
			point = point.add(e.getBigDecimal("point"));
		}
		list.close();
		return point;
	}
}
