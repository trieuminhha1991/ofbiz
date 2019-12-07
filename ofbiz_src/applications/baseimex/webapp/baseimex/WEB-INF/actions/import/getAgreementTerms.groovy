import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

GenericValue agreement = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
if (agreement != null){
	
	String partyIdFrom = (String)agreement.get("partyIdFrom");
	String partyIdTo = (String)agreement.get("partyIdTo");
	GenericValue agreementAttr = delegator.findOne("AgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"), false);
	context.agreementNameEdit = agreementAttr.getString("attrValue");
	List<GenericValue> listETDTerms = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "ETD_AGREEMENT_TERM")), null, null, null, false);
	List<GenericValue> listETATerms = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "ETA_AGREEMENT_TERM")), null, null, null, false);
	List<GenericValue> listFinAccounts = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "FIN_PAYMENT_BANK_ACCOUNT")), null, null, null, false);
	List<GenericValue> listPortOfChargeTerms = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "PORT_OF_CHARGE")), null, null, null, false);
	List<GenericValue> listPartialShipmentTerms = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "PARTIAL_SHIPMENT")), null, null, null, false);
	List<GenericValue> listTransshipmentTerms = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "TRANS_SHIPMENT")), null, null, null, false);
	List<GenericValue> listDefaultCurrencyTerms = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "DEFAULT_PAY_CURRENCY")), null, null, null, false);
	List<GenericValue> listOtherCurrencyTerms = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "OTHER_PAY_CURRENCY")), null, null, null, false);
	List<GenericValue> listOrderByAgreements = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
	List<GenericValue> listRepresents = delegator.findList("AgreementRole", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "REPRESENT_LEGAL")), null, null, null, false);
	List<GenericValue> listPhoneFroms = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_PHONE")), null, null, null, false);
	List<GenericValue> listFaxFroms = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdFrom, "contactMechPurposeTypeId", "FAX_NUMBER")), null, null, null, false);
	List<GenericValue> listAddressFroms = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_LOCATION")), null, null, null, false);
	List<GenericValue> listAddressTos = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdTo, "contactMechPurposeTypeId", "PRIMARY_LOCATION")), null, null, null, false);
	List<GenericValue> listEmailTos = delegator.findList("AgreementPartyContactMech", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdTo, "contactMechTypeId", "EMAIL_ADDRESS")), null, null, null, false);
	
	listETDTerms = EntityUtil.filterByDate(listETDTerms);
	listETATerms = EntityUtil.filterByDate(listETATerms);
	listFinAccounts = EntityUtil.filterByDate(listFinAccounts);
	listPortOfChargeTerms = EntityUtil.filterByDate(listPortOfChargeTerms);
	listOrderByAgreements = EntityUtil.filterByDate(listOrderByAgreements);
	listDefaultCurrencyTerms = EntityUtil.filterByDate(listDefaultCurrencyTerms);
	listOtherCurrencyTerms = EntityUtil.filterByDate(listOtherCurrencyTerms);
	listTransshipmentTerms = EntityUtil.filterByDate(listTransshipmentTerms);
	listPartialShipmentTerms = EntityUtil.filterByDate(listPartialShipmentTerms);
	String currentETDTerm = null;
	String currentETATerm = null;
	String currentPortTerm = null;
	if (!listETDTerms.isEmpty()){
		currentETDTerm = (String)listETDTerms.get(0).get("textValue");
	}
	if (!listETATerms.isEmpty()){
		currentETATerm = (String)listETATerms.get(0).get("textValue");
	}
	if (!listPortOfChargeTerms.isEmpty()){
		currentPortTerm = (String)listPortOfChargeTerms.get(0).get("textValue");
	}
	List<GenericValue> listFinAccountFroms = new ArrayList<GenericValue>();
	List<GenericValue> listFinAccountTos = new ArrayList<GenericValue>();
	if(!listFinAccounts.isEmpty()){
		for (GenericValue finTerm : listFinAccounts){
			String finAccId = (String)finTerm.get("textValue");
			GenericValue finAcc = delegator.findOne("FinAccount", false, UtilMisc.toMap("finAccountId", finAccId));
			if (finAcc != null){
				String organizationPartyId = (String)finAcc.get("organizationPartyId");
				if (partyIdFrom.equals(organizationPartyId)){
					listFinAccountFroms.add(finAcc);
				} else {
					if (partyIdTo.equals(organizationPartyId)){
						listFinAccountTos.add(finAcc);
					}
				}
			}
		}
	}
	List<GenericValue> listProducts = new ArrayList<GenericValue>();
	List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
	listProducts = delegator.findList("AgreementProductApplView", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
	
	List<GenericValue> listProductPlanByOrders = new ArrayList<GenericValue>();
	/*if (!listOrderByAgreements.isEmpty()){
		listProducts = new ArrayList<GenericValue>();
		for (GenericValue order : listOrderByAgreements){
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)order.get("orderId"))), null, null, null, false);
			if (!orderItems.isEmpty()){
				listProducts.addAll(orderItems);
			}
			listProductPlanByOrders = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)order.get("orderId"))), null, null, null, false);
			listProductPlanByOrders = EntityUtil.filterByDate(listProductPlanByOrders);
		}
	}
	*/
	List<GenericValue> listPlanAndLots = new ArrayList<GenericValue>();
	String productPackingUomId = null;
	/* if (!listProductPlanByOrders.isEmpty()){
		String productPlanId = (String)listProductPlanByOrders.get(0).get("productPlanId");
		listPlanAndLots = delegator.findList("ProductPlanAndLot", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
		if (!listPlanAndLots.isEmpty()){
			productPackingUomId = (String)listPlanAndLots.get(0).get("productPackingUomId");
		}
	}
	*/
	String currencyUomId = null;
	if (!listDefaultCurrencyTerms.isEmpty()){
		currencyUomId = (String)listDefaultCurrencyTerms.get(0).get("textValue");
	}
	String representPartyId = null;
	if (!listRepresents.isEmpty()){
		representPartyId = (String)listRepresents.get(0).get("partyId");
	}
	String contactMechPhoneFromId = null;
	String contactMechFaxFromId = null;
	String contactMechAddressFromId = null;
	String contactMechAddressToId = null;
	String contactMechEmailToId = null;
	if (!listPhoneFroms.isEmpty()){
		contactMechPhoneFromId = (String)listPhoneFroms.get(0).get("contactMechId");
	}
	if (!listFaxFroms.isEmpty()){
		contactMechFaxFromId = (String)listFaxFroms.get(0).get("contactMechId");
	}
	if (!listAddressFroms.isEmpty()){
		contactMechAddressFromId = (String)listAddressFroms.get(0).get("contactMechId");
	}
	if (!listAddressTos.isEmpty()){
		contactMechAddressToId = (String)listAddressTos.get(0).get("contactMechId");
	}
	if (!listEmailTos.isEmpty()){
		contactMechEmailToId = (String)listEmailTos.get(0).get("contactMechId");
	}
	String transshipment = null;
	String partialShipment = null;
	if (!listTransshipmentTerms.isEmpty()){
		transshipment = (String)listTransshipmentTerms.get(0).get("textValue");
	}
	if (!listPartialShipmentTerms.isEmpty()){
		partialShipment = (String)listPartialShipmentTerms.get(0).get("textValue");
	}
	
	GenericValue fromAddress = delegator.findOne("PostalAddressDetail", false, UtilMisc.toMap("contactMechId", contactMechAddressFromId)); 
	
	
	context.fromAddress = fromAddress;
	
	context.transshipment = transshipment;
	context.partialShipment = partialShipment;
	context.currentETDTerm = currentETDTerm;
	context.currentETATerm = currentETATerm;
	context.currentPortTerm = currentPortTerm;
	context.currencyUomId = currencyUomId;
	context.representPartyId = representPartyId;
	context.listFinAccountTos = listFinAccountTos;
	context.listFinAccountFroms = listFinAccountFroms;
	context.listProducts = listProducts;
	context.contactMechPhoneFromId = contactMechPhoneFromId;
	context.contactMechFaxFromId = contactMechFaxFromId;
	context.contactMechAddressFromId = contactMechAddressFromId;
	context.contactMechAddressToId = contactMechAddressToId;
	context.contactMechEmailToId = contactMechEmailToId;
	context.productPackingUomId = productPackingUomId;
	context.listOtherCurrencyTerms = listOtherCurrencyTerms;
}