package com.olbius.jms.event;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.jms.data.Geo;
import com.olbius.jms.data.PartyGroup;
import com.olbius.jms.data.PartyTaxAuth;
import com.olbius.jms.data.PostalAddress;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.Uom;

public class OfbizDataServices {

	public static Uom getUom(Delegator delegator, String uomId) throws GenericEntityException {
		return getUom(delegator, uomId, false);
	}
	
	public static Uom getUomBus(Delegator delegator, String uomId) throws GenericEntityException {
		return getUom(delegator, uomId, true);
	}
	
	public static Uom getUom(Delegator delegator, String uomId, boolean bus) throws GenericEntityException {

		Uom uom = null;

		if (uomId == null) {
			return null;
		}

		GenericValue value = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);

		if (value != null) {
			uom = new Uom(value.getString("uomTypeId"));
			uom.setId(value.getString("uomId"), bus);
			uom.getMessageData().put("abbreviation", value.getString("abbreviation"));
			uom.getMessageData().put("description", value.getString("description"));
		}

		return uom;
	}

	public static Geo getGeo(Delegator delegator, String geoId) throws GenericEntityException {
		return getGeo(delegator, geoId, false);
	}
	
	public static Geo getGeoBus(Delegator delegator, String geoId) throws GenericEntityException {
		return getGeo(delegator, geoId, true);
	}
	
	public static Geo getGeo(Delegator delegator, String geoId, boolean bus) throws GenericEntityException {

		if (geoId == null) {
			return null;
		}

		Geo geo = null;

		GenericValue value = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);

		if (value != null) {

			geo = new Geo();

			geo.setId(value.getString("geoId"), bus);

			geo.getMessageData().put("geoTypeId", value.getString("geoTypeId"));
			geo.getMessageData().put("geoCode", value.getString("geoCode"));
			geo.getMessageData().put("geoSecCode", value.getString("geoSecCode"));
			geo.getMessageData().put("abbreviation", value.getString("abbreviation"));
			geo.getMessageData().put("geoName", value.getString("geoName"));

		}

		return geo;
	}

	public static PostalAddress getPostalAddress(Delegator delegator, String contactMechId) throws GenericEntityException {
		return getPostalAddress(delegator, contactMechId, false);
	}
	
	public static PostalAddress getPostalAddressBus(Delegator delegator, String contactMechId) throws GenericEntityException {
		return getPostalAddress(delegator, contactMechId, true);
	}
	
	public static PostalAddress getPostalAddress(Delegator delegator, String contactMechId, boolean bus) throws GenericEntityException {

		if (contactMechId == null) {
			return null;
		}

		PostalAddress address = null;

		GenericValue genericValue = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);

		if (genericValue != null) {

			address = new PostalAddress();

			address.setId(genericValue.getString("contactMechId"), bus);
			
			address.getMessageData().put("attnName", genericValue.getString("attnName"));
			address.getMessageData().put("toName", genericValue.getString("toName"));
			address.getMessageData().put("address1", genericValue.getString("address1"));
			address.getMessageData().put("address2", genericValue.getString("address2"));
			address.getMessageData().put("city", genericValue.getString("city"));
			address.getMessageData().put("directions", genericValue.getString("directions"));
			address.getMessageData().put("postalCode", genericValue.getString("postalCode"));
			address.getMessageData().put("postalCodeExt", genericValue.getString("postalCodeExt"));

			address.setCountry(getGeo(delegator, genericValue.getString("countryGeoId"), bus));
			address.setState(getGeo(delegator, genericValue.getString("stateProvinceGeoId"), bus));
			address.setDistrict(getGeo(delegator, genericValue.getString("districtGeoId"), bus));
			address.setWard(getGeo(delegator, genericValue.getString("wardGeoId"), bus));

		}

		return address;

	}

	public static PartyGroup getPartyGroup(Delegator delegator, String partyId) throws GenericEntityException {
		return getPartyGroup(delegator, partyId, false);
	}
	
	public static PartyGroup getPartyGroupBus(Delegator delegator, String partyId) throws GenericEntityException {
		return getPartyGroup(delegator, partyId, true);
	}
	
	public static PartyGroup getPartyGroup(Delegator delegator, String partyId, boolean bus) throws GenericEntityException {

		if (partyId == null) {
			return null;
		}

		PartyGroup group = null;

//		GenericValue party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);

		if (party != null) {

			group = new PartyGroup();

			group.setId(partyId, bus);
			
			GenericValue partyTmp;

			if("PERSON".equals(party.getString("partyTypeId"))) {
				partyTmp = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				String name = "";
				if(partyTmp.getString("lastName") != null) {
					name += partyTmp.getString("lastName") + " ";
				}
				if(partyTmp.getString("middleName") != null) {
					name += partyTmp.getString("middleName") + (name.isEmpty() ? "" : " ");
				}
				if(partyTmp.getString("firstName") != null) {
					name += partyTmp.getString("firstName") + (name.isEmpty() ? "" : " ");
				}
				group.getMessageData().put("groupName", name);
			} else {
				partyTmp = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				group.getMessageData().put("groupName", partyTmp.getString("groupName"));
			}
			
			group.setCurrencyUom(OfbizDataServices.getUom(delegator, (String) party.get("preferredCurrencyUomId"), bus));

//			group.setPartyTaxAuth(OfbizDataServices.getPartyTaxAuth(delegator, partyId, bus));
			
			Map<String, String> pcmpFindMap = UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION");
			
			List<GenericValue> allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", pcmpFindMap, null, false), true);
			
			GenericValue value = EntityUtil.getFirst(allPCMPs);
			
			if(value!=null) {
				group.setPostalAddress(OfbizDataServices.getPostalAddress(delegator, value.getString("contactMechId"), bus));
			}
			
		}

		return group;
	}
	
	public static Product getProduct(Delegator delegator, String productId) throws GenericEntityException {
		return getProduct(delegator, productId, false);
	}
	
	public static Product getProductBus(Delegator delegator, String productId) throws GenericEntityException {
		return getProduct(delegator, productId, true);
	}

	public static Product getProduct(Delegator delegator, String productId, boolean bus) throws GenericEntityException {

		if (productId == null) {
			return null;
		}

		Product p = null;

		GenericValue value = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);

		if (value != null) {

			p = new Product();
			
			p.setId(value.getString("productId"), bus);
			
			p.getMessageData().put("productName", value.getString("productName"));
			p.getMessageData().put("internalName", value.getString("internalName"));
			p.getMessageData().put("description", value.getString("description"));
			p.getMessageData().put("longDescription", value.getString("longDescription"));
			p.getMessageData().put("detailImageUrl", value.getString("detailImageUrl"));
			p.getMessageData().put("smallImageUrl", value.getString("smallImageUrl"));
			p.getMessageData().put("mediumImageUrl", value.getString("mediumImageUrl"));
			p.getMessageData().put("largeImageUrl", value.getString("largeImageUrl"));
			p.getMessageData().put("originalImageUrl", value.getString("originalImageUrl"));
			p.getMessageData().put("productWeight", value.getBigDecimal("productWeight"));
			p.getMessageData().put("productHeight", value.getBigDecimal("productHeight"));
			
			p.setQuantityUom(OfbizDataServices.getUom(delegator, value.getString("quantityUomId"), bus));
			p.setWeightUom(OfbizDataServices.getUom(delegator, value.getString("weightUomId"), bus));
			p.setHeightUom(OfbizDataServices.getUom(delegator, value.getString("heightUomId"), bus));

		}

		return p;
	}
	
	public static PartyTaxAuth getPartyTaxAuth(Delegator delegator, String partyId) throws GenericEntityException {
		return getPartyTaxAuth(delegator, partyId, false);
	}
	
	public static PartyTaxAuth getPartyTaxAuthBus(Delegator delegator, String partyId) throws GenericEntityException {
		return getPartyTaxAuth(delegator, partyId, true);
	}
	
	public static PartyTaxAuth getPartyTaxAuth(Delegator delegator, String partyId, boolean bus) throws GenericEntityException {
		if (partyId == null) {
			return null;
		}
		
		PartyTaxAuth partyTaxAuth = null;
		
		Map<String, String> context = UtilMisc.toMap("partyId", partyId);
		List<GenericValue> allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyTaxAuthInfo", context, null, false), true);
		GenericValue value = EntityUtil.getFirst(allPCMPs);
		
		if(value != null) {
			partyTaxAuth = new PartyTaxAuth();
			
			partyTaxAuth.setTaxAuthGeo(getGeo(delegator, value.getString("taxAuthGeoId"), bus));
			
			partyTaxAuth.setTaxAuthParty(getPartyGroup(delegator, value.getString("taxAuthPartyId"), bus));
			
			partyTaxAuth.setTaxCode(value.getString("partyTaxId"));
		}
		
		return partyTaxAuth;
	}
}
