package com.olbius.basepo.contactMech;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.contact.ContactMechWorker;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ContactMechServices {

	public static final String module = ContactMechServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels.xml";

	public static Map<String, Object> loadContactMechByFacilityId(DispatchContext dpx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dpx.getDelegator();
		try {
			String facilityId = (String) context.get("facilityId");
			String partySupplierId = (String) context.get("partySupplierId");
			List<GenericValue> listFacilityContactMech = delegator.findList("FacilityContactMech",
					EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
			listFacilityContactMech = EntityUtil.filterByDate(listFacilityContactMech);
			String address1 = "";
			String countryGeoId = "";
			String stateProvinceGeoId = "";
			String contactMechId = "";
			List<GenericValue> listContactMech = FastList.newInstance();
			GenericValue orderParty = delegator.findOne("Party", UtilMisc.toMap("partyId", partySupplierId), false);
			Collection<GenericValue> shippingContactMechList = ContactHelper.getContactMech(orderParty,
					"SHIPPING_LOCATION", "POSTAL_ADDRESS", false);

			if (!listFacilityContactMech.isEmpty()) {
				for (GenericValue facilityContactMech : listFacilityContactMech) {
					contactMechId = (String) facilityContactMech.get("contactMechId");
					GenericValue postalAddress = delegator.findOne("PostalAddress",
							UtilMisc.toMap("contactMechId", contactMechId), false);
					if (postalAddress != null) {
						address1 = (String) postalAddress.get("address1");
						countryGeoId = (String) postalAddress.get("countryGeoId");
						stateProvinceGeoId = (String) postalAddress.get("stateProvinceGeoId");
						listContactMech.add(postalAddress);
					}
				}
			}
			int size = listContactMech.size();

			if (UtilValidate.isNotEmpty(shippingContactMechList) && shippingContactMechList != null) {
				List<GenericValue> listCTM = (List<GenericValue>) shippingContactMechList;
				for (GenericValue ctm : listCTM) {
					String ctmId = ctm.getString("contactMechId");
					GenericValue postalAdd = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", ctmId),
							false);
					listContactMech.add(postalAdd);
				}
			}
			result.put("size", size);
			result.put("listContactMech", listContactMech);
			result.put("contactMechId", contactMechId);
			result.put("address1", address1);
			result.put("countryGeoId", countryGeoId);
			result.put("stateProvinceGeoId", stateProvinceGeoId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> updatePostalAddressPO(DispatchContext dpx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String contactMechId = (String) context.get("contactMechId");
		String toName = (String) context.get("toName");
		String attnName = (String) context.get("attnName");
		try {
			GenericValue postal = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId),
					false);
			if (postal != null) {
				postal.put("toName", toName);
				postal.put("attnName", attnName);
				delegator.store(postal);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListContactMechByFacility(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			List<Map<String, Object>> listIterator = FastList.newInstance();
			if (parameters.containsKey("facilityId")) {
				String facilityId = parameters.get("facilityId")[0];
				if (UtilValidate.isNotEmpty(facilityId)) {
					listAllConditions.add(EntityCondition.makeCondition(
							UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
					listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					List<GenericValue> facilityContactMechPurposes = delegator.findList("FacilityContactMechPurpose",
							EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
					for (GenericValue x : facilityContactMechPurposes) {
						Map<String, Object> itemMap = FastMap.newInstance();
						GenericValue shippingAddress = delegator.findOne("PostalAddress",
								UtilMisc.toMap("contactMechId", x.get("contactMechId")), false);
						if (UtilValidate.isNotEmpty(shippingAddress)) {
							itemMap.put("contactMechId", shippingAddress.get("contactMechId"));
							itemMap.put("toName", shippingAddress.get("toName"));
							itemMap.put("attnName", shippingAddress.get("attnName"));
							itemMap.put("address1", shippingAddress.get("address1"));
							itemMap.put("address2", shippingAddress.get("address2"));
							itemMap.put("city", shippingAddress.get("city"));
							itemMap.put("stateProvinceGeoId", shippingAddress.get("stateProvinceGeoId"));
							itemMap.put("postalCode", shippingAddress.get("postalCode"));

							String countryGeoId = shippingAddress.getString("countryGeoId");
							itemMap.put("countryGeoId", countryGeoId);
							itemMap.put("countryGeoName", countryGeoId);

							String stateProvinceGeoId = shippingAddress.getString("stateProvinceGeoId");
							itemMap.put("stateProvinceGeoId", stateProvinceGeoId);
							itemMap.put("stateProvinceGeoName", stateProvinceGeoId);

							String districtGeoId = shippingAddress.getString("districtGeoId");
							itemMap.put("districtGeoId", districtGeoId);
							itemMap.put("districtGeoName", districtGeoId);

							String wardGeoId = shippingAddress.getString("wardGeoId");
							itemMap.put("wardGeoId", wardGeoId);
							itemMap.put("wardGeoName", wardGeoId);

							itemMap.put("countryGeoName", ContactMechWorker.getGeoName(delegator, countryGeoId));
							itemMap.put("stateProvinceGeoName",
									ContactMechWorker.getGeoName(delegator, stateProvinceGeoId));
							itemMap.put("districtGeoName", ContactMechWorker.getGeoName(delegator, districtGeoId));
							itemMap.put("wardGeoName", ContactMechWorker.getGeoName(delegator, wardGeoId));
							listIterator.add(itemMap);
						}
					}
					result.put("listIterator", listIterator);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListContactMechByFacility service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}
}
