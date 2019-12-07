package com.olbius.activemq.receive.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

public class TaxServices {

	public static final String module = TaxServices.class.getName();

	public static Map<String, Object> taxAuthority(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {

		LocalDispatcher dispatcher = ctx.getDispatcher();

		Delegator delegator = ctx.getDelegator();

		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String partyId = (String) context.get("partyId");
		String geoId = (String) context.get("geoId");
		String partyTaxId = (String) context.get("partyTaxId");
		String glAccountId = (String) context.get("glAccountId");
		String organizationPartyId = (String) context.get("organizationPartyId");
		String taxCode = (String) context.get("taxCode");

		String[] strings = new String[] { "TAX_VAT_0", "TAX_VAT_5", "TAX_VAT_10", "TAX_VAT_KCT", "TAX_VAT_KPTH" };

		for(String s: strings) {
			GenericValue value = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", s), false);
			if(value == null) {
				value = delegator.findOne("ProductCategoryType", UtilMisc.toMap("productCategoryTypeId", "TAX_CATEGORY"), false);
				if(value == null) {
					value = delegator.makeValue("ProductCategoryType");
					value.set("productCategoryTypeId", "TAX_CATEGORY");
					value.set("hasTable", "N");
					value.set("description", "Tax");
					value.create();
				}
				value = delegator.makeValue("ProductCategory");
				value.set("productCategoryId", s);
				value.set("productCategoryTypeId", "TAX_CATEGORY");
				value.create();
			}
		}
		
		run(dispatcher,
				delegator, userLogin, "TaxAuthority", UtilMisc.toMap("taxAuthGeoId", (Object) geoId, "taxAuthPartyId", partyTaxId,
						"requireTaxIdForExemption", "Y", "includeTaxInPrice", "N"),
				UtilMisc.toMap("taxAuthGeoId", geoId, "taxAuthPartyId", partyTaxId));

		for (String s : strings) {
			run(dispatcher, delegator, userLogin, "TaxAuthorityCategory",
					UtilMisc.toMap("taxAuthGeoId", (Object) geoId, "taxAuthPartyId", partyTaxId, "productCategoryId", s),
					UtilMisc.toMap("taxAuthGeoId", geoId, "taxAuthPartyId", partyTaxId, "productCategoryId", s));
		}

		if(glAccountId != null && !glAccountId.isEmpty()) {
			run(dispatcher, delegator, userLogin, "TaxAuthorityGlAccount",
					UtilMisc.toMap("taxAuthGeoId", (Object) geoId, "taxAuthPartyId", partyTaxId, "organizationPartyId", organizationPartyId,
							"glAccountId", glAccountId),
					UtilMisc.toMap("taxAuthGeoId", geoId, "taxAuthPartyId", partyTaxId, "organizationPartyId", organizationPartyId));
		}
		

		List<String> key = new ArrayList<String>();

		key.add("taxAuthorityRateSeqId");

		for (String s : strings) {
			BigDecimal decimal;
			switch (s) {
			case "TAX_VAT_5":
				decimal = new BigDecimal(5);
				break;
			case "TAX_VAT_10":
				decimal = new BigDecimal(10);
				break;
			default:
				decimal = new BigDecimal(0);
				break;
			}
			runByAnd(dispatcher, delegator, userLogin, "TaxAuthorityRateProduct",
					UtilMisc.toMap("taxAuthGeoId", geoId, "taxAuthPartyId", partyTaxId, "productCategoryId", s, "taxAuthorityRateTypeId", "VAT_TAX",
							"minItemPrice", new BigDecimal(0), "minPurchase", new BigDecimal(0), "taxShipping", "N", "taxPercentage", decimal,
							"taxPromotions", "Y"),
					UtilMisc.toMap("taxAuthGeoId", geoId, "taxAuthPartyId", partyTaxId, "productCategoryId", s, "taxAuthorityRateTypeId", "VAT_TAX"),
					key);
		}

		key = new ArrayList<String>();

		key.add("fromDate");

		runByAnd(dispatcher, delegator, userLogin, "PartyTaxAuthInfo",
				UtilMisc.toMap("taxAuthGeoId", (Object) geoId, "taxAuthPartyId", partyTaxId, "partyId", partyId, "isExempt", "N", "isNexus", "Y", "partyTaxId", taxCode),
				UtilMisc.toMap("taxAuthGeoId", geoId, "taxAuthPartyId", partyTaxId, "partyId", partyId), key);

		Map<String, Object> result = new HashMap<String, Object>();

		return result;

	}

	private static void runByAnd(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String service, Map<String, Object> map,
			Map<String, ?> map2, List<String> key) throws GenericEntityException, GenericServiceException {

		List<GenericValue> values = delegator.findByAnd(service, map2, null, false);

		map.put("userLogin", userLogin);

		if (values == null || values.isEmpty()) {
			dispatcher.runSync("create" + service, map);
		} else {

			if (key != null) {
				for (String s : key) {
					map.put(s, values.get(0).get(s));
				}

			}

			dispatcher.runSync("update" + service, map);
		}
	}

	private static void run(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String service, Map<String, Object> map,
			Map<String, ?> map2) throws GenericEntityException, GenericServiceException {

		GenericValue value = delegator.findOne(service, map2, false);

		map.put("userLogin", userLogin);

		if (value == null) {
			dispatcher.runSync("create" + service, map);
		} else {
			dispatcher.runSync("update" + service, map);
		}

	}

}
