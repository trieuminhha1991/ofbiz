package com.olbius.marketing;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.crm.util.PartyHelper;

public class MarketingServices {

	public static final String module = MarketingServices.class.getName();
	public static final String resource = "POUiLabels";

	public static Map<String, Object> listPartnerBusinesses(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listPartyGroups = FastList.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			List<String> listCustomerId = MarketingUtils.getPartiesByRolesAndPartyTo(organizationId, "CONTACT",
					"INTERNAL_ORGANIZATIO", delegator, true);
			List<String> listCustomerId2 = MarketingUtils.getPartiesByRolesAndPartyTo(organizationId, "CUSTOMER",
					"INTERNAL_ORGANIZATIO", delegator, true);
			if (UtilValidate.isNotEmpty(listCustomerId2)) {
				listCustomerId.addAll(listCustomerId2);
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listCustomerId));
			conditions.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.EQUALS, "BUSINESSES"));
			List<GenericValue> listPartyBusinesses = delegator.findList("Party",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> partyId = EntityUtil.getFieldListFromEntityList(listPartyBusinesses, "partyId", true);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyId));
			List<GenericValue> listPartyGroup = delegator.findList("PartyAndPartyGroup",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : listPartyGroup) {
				Map<String, Object> partyGroup = FastMap.newInstance();
				partyGroup.put("partyId", x.get("partyId"));
				partyGroup.put("partyCode", x.get("partyCode"));
				partyGroup.put("groupName", x.get("groupName"));
				listPartyGroups.add(partyGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MarketingUtils.listCompact(listPartyGroups, context);
	}

	public static Map<String, Object> listPartnerSchool(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listPartyGroups = FastList.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			List<String> listCustomerId = MarketingUtils.getPartiesByRolesAndPartyTo(organizationId, "CONTACT",
					"INTERNAL_ORGANIZATIO", delegator, true);
			List<String> listCustomerId2 = MarketingUtils.getPartiesByRolesAndPartyTo(organizationId, "CUSTOMER",
					"INTERNAL_ORGANIZATIO", delegator, true);
			if (UtilValidate.isNotEmpty(listCustomerId2)) {
				listCustomerId.addAll(listCustomerId2);
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listCustomerId));
			conditions.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.EQUALS, "SCHOOL"));
			List<GenericValue> listPartyBusinesses = delegator.findList("Party",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> partyId = EntityUtil.getFieldListFromEntityList(listPartyBusinesses, "partyId", true);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyId));
			List<GenericValue> listPartyGroup = delegator.findList("PartyAndPartyGroup",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : listPartyGroup) {
				Map<String, Object> partyGroup = FastMap.newInstance();
				partyGroup.put("partyId", x.get("partyId"));
				partyGroup.put("partyCode", x.get("partyCode"));
				partyGroup.put("groupName", x.get("groupName"));
				listPartyGroups.add(partyGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MarketingUtils.listCompact(listPartyGroups, context);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPartnerFamily(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator dummyParty = null;
		String TotalRows = "0";
		List<Map<String, Object>> listIterator = FastList.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(
					UtilMisc.toMap("partyIdTo", organizationId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO")));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.IN,
					UtilMisc.toList("CONTACT", "CUSTOMER")));
			conditions.add(EntityCondition.makeCondition(MarketingUtils.makeCondition(context, delegator, true)));
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize;
			dummyParty = delegator.find("PartyRelationShipAndPerson", EntityCondition.makeCondition(conditions), null,
					null, UtilMisc.toList("partyIdFrom"), opts);
			List<GenericValue> listPerson = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listPerson) {
				Map<String, Object> mapPerson = FastMap.newInstance();
				String groupName = PartyHelper.getPartyName(delegator, x.getString("partyId"), true, true);
				mapPerson.put("partyId", x.getString("partyId"));
				mapPerson.put("partyCode", x.getString("partyCode"));
				mapPerson.put("groupName", groupName);
				listIterator.add(mapPerson);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dummyParty != null) {
				dummyParty.close();
			}
		}
		result.put("listIterator", listIterator);
		result.put("TotalRows", TotalRows);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPartyTo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<String> listCustomerId = SecurityUtil.getPartiesByRoles("INTERNAL_ORGANIZATIO", delegator);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listCustomerId));
			listIterator = delegator.find("PartyGroup", EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listAllEmployee(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<String> listCustomerId = SecurityUtil.getPartiesByRoles("EMPLOYEE", delegator);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listCustomerId));
			listIterator = delegator.find("Person", EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static String getPartyRole(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Map<String, Object> parameters = UtilHttp.getParameterMap(request);
		String partyId = (String) parameters.get("partyId");
		List<GenericValue> listRoleTypes = SecurityUtil.getGVCurrentRoles(partyId, delegator);
		request.setAttribute("listRoleTypes", listRoleTypes);
		request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return "success";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listContactBusinessesAndSchool(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listContactBusinesses = FastList.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			LocalDispatcher dispatcher = ctx.getDispatcher();
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = FastList.newInstance();
			List<String> listCustomerId = MarketingUtils.getPartiesByRolesAndPartyTo(organizationId, "CONTACT",
					"INTERNAL_ORGANIZATIO", delegator, true);
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listCustomerId));
			conditions.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.EQUALS, "BUSINESSES"));
			List<GenericValue> listParty = delegator.findList("Party", EntityCondition.makeCondition(conditions), null,
					null, null, false);
			List<String> listContactBusinessesId = EntityUtil.getFieldListFromEntityList(listParty, "partyId", true);
			List<GenericValue> listPartyGroup = delegator.findList("PartyGroup",
					EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listContactBusinessesId), null,
					null, opts, false);
			for (GenericValue x : listPartyGroup) {
				Map<String, Object> group = FastMap.newInstance();
				group.putAll(x);
				String partyId = x.getString("partyId");
				group.put("partyTypeId", getPartyType(delegator, partyId));
				String partyRepresentId = getPartyRepresentative(partyId, delegator);
				if (UtilValidate.isNotEmpty(partyRepresentId)) {
					group.put("personRepresent", PartyHelper.getPartyName(delegator, partyRepresentId, true, true));
				}
				Map<String, Object> getInformationPerson = dispatcher.runSync("getInformationPerson",
						UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getInformationPerson.get("partyInfo");
				group.put("contactNumber", (String) partyInfo.get("contactNumber"));
				group.put("emailAddress", (String) partyInfo.get("emailAddress"));
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							group.put("address1", (String) m.get("address1"));
							break;
						}
					}
				}
				GenericValue listPartyAttrT = delegator.findOne("PartyAttribute",
						UtilMisc.toMap("partyId", partyId, "attrName", "Teacher"), false);
				GenericValue listPartyAttrS = delegator.findOne("PartyAttribute",
						UtilMisc.toMap("partyId", partyId, "attrName", "Student"), false);
				if (UtilValidate.isNotEmpty(listPartyAttrT)) {
					group.put("numberTeacher", (String) listPartyAttrT.get("attrValue"));
				}
				if (UtilValidate.isNotEmpty(listPartyAttrS)) {
					group.put("numberStudent", (String) listPartyAttrS.get("attrValue"));
				}
				listContactBusinesses.add(group);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MarketingUtils.listCompact(listContactBusinesses, context);
	}

	private static String getPartyType(Delegator delegator, String partyId) throws GenericEntityException {
		String partyTypeId = "";
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		if (UtilValidate.isNotEmpty(party)) {
			partyTypeId = party.getString("partyTypeId");
		}
		return partyTypeId;
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	public static Map<String, Object> DmsCreateAgreementAjax(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			String jsonData = (String) context.get("productInAgreement");
			JSONArray productInAgreement = new JSONArray().fromObject(jsonData);
			Long agreementDateL = (Long) context.get("agreementDate");
			Long fromDateL = (Long) context.get("fromDate");
			Timestamp fromDate = null;
			Timestamp agreementDate = null;
			if (UtilValidate.isNotEmpty(agreementDateL)) {
				agreementDate = new Timestamp(agreementDateL);
			}
			if (UtilValidate.isNotEmpty(fromDateL)) {
				fromDate = new Timestamp(fromDateL);
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> mapCreateAgreement = FastMap.newInstance();
			mapCreateAgreement.put("description", context.get("description"));
			mapCreateAgreement.put("agreementCode", context.get("agreementCode"));
			mapCreateAgreement.put("partyIdFrom", context.get("partyIdFrom"));
			mapCreateAgreement.put("partyIdTo", context.get("partyIdTo"));
			mapCreateAgreement.put("roleTypeIdFrom", context.get("roleTypeIdFrom"));
			mapCreateAgreement.put("roleTypeIdTo", context.get("roleTypeIdTo"));
			mapCreateAgreement.put("agreementTypeId", context.get("agreementTypeId"));
			mapCreateAgreement.put("statusId", context.get("statusId"));
			mapCreateAgreement.put("agreementDate", agreementDate);
			mapCreateAgreement.put("fromDate", fromDate);
			mapCreateAgreement.put("userLogin", userLogin);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(fromDateL);
			calendar.add(calendar.MONTH, +Integer.parseInt((String) context.get("textValue1")));
			Timestamp thruDate = new Timestamp(calendar.getTimeInMillis());
			mapCreateAgreement.put("thruDate", thruDate);
			Map<String, Object> resultCreateAgreement = FastMap.newInstance();
			resultCreateAgreement = dispatcher.runSync("createAgreement", mapCreateAgreement);
			// thruDate PartyRelationship type CONTACT and INDIVIDUAL_CUSTOMER
			// of partner
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.IN,
					UtilMisc.toList("CONTACT", "INDIVIDUAL_CUSTOMER")));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", context.get("partyIdFrom"),
					"partyIdTo", context.get("partyIdTo"), "roleTypeIdTo", "INTERNAL_ORGANIZATIO")));
			List<GenericValue> listConTact = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : listConTact) {
				Map<String, Object> updatePartyRelationship = FastMap.newInstance();
				updatePartyRelationship.putAll(x);
				updatePartyRelationship.put("thruDate", new Timestamp(System.currentTimeMillis()));
				updatePartyRelationship.put("userLogin", userLogin);
				dispatcher.runSync("updatePartyRelationship", updatePartyRelationship);
			}
			// createPartyRelationship CUSTOMER between partner and company
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", context.get("partyIdFrom"), "partyIdTo", context.get("partyIdTo"),
							"roleTypeIdFrom", context.get("roleTypeIdFrom"), "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
							"partyRelationshipTypeId", "CUSTOMER_REL", "userLogin", userLogin));
			String agreementId = (String) resultCreateAgreement.get("agreementId");
			if (UtilValidate.isNotEmpty(agreementId)) {
				delegator.create("AgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName",
						"agreementType", "attrValue", context.get("agreementType")));

				checkAndCreatePartyRole(dispatcher, delegator, userLogin, context.get("partyId"),
						context.get("roleTypeId"));
				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("partyId", context.get("partyId"));
				mapCreateAgreement.put("roleTypeId", context.get("roleTypeId"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementRole", mapCreateAgreement);

				dispatcher.runSync("checkAndUpdateRelSalesExecutive",
						UtilMisc.toMap("partyId", context.get("partyIdFrom"), "salesExecutiveId",
								context.get("staffContract"), "userLogin", userLogin));
				checkAndCreatePartyRole(dispatcher, delegator, userLogin, context.get("staffContract"),
						"SALES_EXECUTIVE");
				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("partyId", context.get("staffContract"));
				mapCreateAgreement.put("roleTypeId", "SALES_EXECUTIVE");
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementRole", mapCreateAgreement);

				checkAndCreatePartyRole(dispatcher, delegator, userLogin, context.get("partyIdTo"),
						context.get("roleTypeIdTo"));
				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("partyId", context.get("partyIdTo"));
				mapCreateAgreement.put("roleTypeId", context.get("roleTypeIdTo"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementRole", mapCreateAgreement);

				checkAndCreatePartyRole(dispatcher, delegator, userLogin, context.get("partyIdFrom"),
						context.get("roleTypeIdFrom"));
				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("partyId", context.get("partyIdFrom"));
				mapCreateAgreement.put("roleTypeId", context.get("roleTypeIdFrom"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementRole", mapCreateAgreement);

				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("termTypeId", context.get("termTypeId1"));
				mapCreateAgreement.put("termValue", context.get("textValue1"));
				mapCreateAgreement.put("textValue", context.get("textValue1"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementTerm", mapCreateAgreement);

				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("termTypeId", context.get("termTypeId2"));
				mapCreateAgreement.put("textValue", context.get("textValue2"));
				mapCreateAgreement.put("termValue", context.get("textValue2"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementTerm", mapCreateAgreement);

				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("termTypeId", context.get("termTypeId3"));
				mapCreateAgreement.put("textValue", context.get("textValue3"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementTerm", mapCreateAgreement);

				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("termTypeId", context.get("termTypeId4"));
				mapCreateAgreement.put("textValue", context.get("textValue4"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementTerm", mapCreateAgreement);

				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("termTypeId", context.get("termTypeId5"));
				mapCreateAgreement.put("textValue", context.get("textValue5"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementTerm", mapCreateAgreement);

				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("termTypeId", context.get("termTypeId6"));
				mapCreateAgreement.put("textValue", context.get("textValue6"));
				mapCreateAgreement.put("description", "AND");
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementTerm", mapCreateAgreement);

				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("termTypeId", context.get("termTypeId7"));
				mapCreateAgreement.put("textValue", context.get("textValue7"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementTerm", mapCreateAgreement);

				mapCreateAgreement.clear();
				mapCreateAgreement.put("agreementId", agreementId);
				mapCreateAgreement.put("termTypeId", context.get("termTypeId8"));
				mapCreateAgreement.put("textValue", context.get("textValue8"));
				mapCreateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementTerm", mapCreateAgreement);

				for (int i = 0; i < productInAgreement.size(); i++) {
					JSONObject thisRow = productInAgreement.getJSONObject(i);
					String productId = (String) thisRow.get("productId");
					String note = (String) thisRow.get("note");
					Object price = String.valueOf(thisRow.get("price"));
					Object quantity = String.valueOf(thisRow.get("quantity"));
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setGroupingSeparator(',');
					symbols.setDecimalSeparator('.');
					String pattern = "#,##0.0#";
					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
					decimalFormat.setParseBigDecimal(true);
					price = (BigDecimal) decimalFormat.parse(price.toString());
					quantity = (BigDecimal) decimalFormat.parse(quantity.toString());

					mapCreateAgreement.clear();
					mapCreateAgreement.put("agreementId", agreementId);
					mapCreateAgreement.put("agreementItemTypeId", "AGREEMENT_PRICING_PR");
					mapCreateAgreement.put("agreementText", note);
					List<GenericValue> listProduct = delegator.findList(
							"ProductPrice", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId,
									"productPriceTypeId", "DEFAULT_PRICE", "productPricePurposeId", "PURCHASE")),
							null, null, null, false);
					if (UtilValidate.isNotEmpty(listProduct)) {
						GenericValue product = EntityUtil.getFirst(listProduct);
						mapCreateAgreement.put("currencyUomId", product.getString("quantityUomId"));
					}
					mapCreateAgreement.put("userLogin", userLogin);
					Map<String, Object> mapResultCreateAgreementItem = dispatcher.runSync("createAgreementItem",
							mapCreateAgreement);
					String agreementItemSeqId = (String) mapResultCreateAgreementItem.get("agreementItemSeqId");
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					mapCreateAgreement.clear();
					if (UtilValidate.isNotEmpty(product)) {
						mapCreateAgreement.put("quantityUomId", product.getString("quantityUomId"));
					}
					mapCreateAgreement.put("agreementId", agreementId);
					mapCreateAgreement.put("agreementItemSeqId", agreementItemSeqId);
					mapCreateAgreement.put("productId", productId);
					mapCreateAgreement.put("price", price);
					mapCreateAgreement.put("quantity", quantity);
					mapCreateAgreement.put("userLogin", userLogin);
					dispatcher.runSync("createAgreementProductAppl", mapCreateAgreement);
				}
				// createProductStoreRole
				for (String s : (List<String>) context.get("productStoreId[]")) {
					conditions.clear();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", context.get("partyIdFrom"),
							"roleTypeId", "roleTypeIdFrom", "productStoreId", s)));
					List<GenericValue> listProductStoreRole = delegator.findList("ProductStoreRole",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					if (UtilValidate.isEmpty(listProductStoreRole)) {
						mapCreateAgreement.clear();
						mapCreateAgreement.put("partyId", context.get("partyIdFrom"));
						mapCreateAgreement.put("roleTypeId", context.get("roleTypeIdFrom"));
						mapCreateAgreement.put("productStoreId", s);
						mapCreateAgreement.put("userLogin", userLogin);
						dispatcher.runSync("createProductStoreRole", mapCreateAgreement);
					}
				}
			}
			result.put("agreementId", agreementId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	private static void checkAndCreatePartyRole(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Object partyId, Object roleTypeId) throws GenericEntityException, GenericServiceException {
		GenericValue partyRole = delegator.findOne("PartyRole",
				UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
		if (UtilValidate.isEmpty(partyRole)) {
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", userLogin));
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInformationPartner(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = (String) context.get("partyId");
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			if (UtilValidate.isNotEmpty(party)) {
				if (!"PERSON".equals(party.getString("partyTypeId"))) {
					partyId = getPartyRepresentative(partyId, delegator);
				}
				Map<String, Object> getInformationPartner = dispatcher.runSync("getInformationPerson",
						UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				Map<String, Object> partnerInfo = FastMap.newInstance();
				partnerInfo.putAll((Map<String, Object>) getInformationPartner.get("partyInfo"));
				result.put("partnerInfo", partnerInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getInformationPerson(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Locale locale = (Locale) context.get("locale");
			String partyId = (String) context.get("partyId");
			GenericValue partyInfo = null;
			Map<String, Object> resultParty = FastMap.newInstance();
			String partyFullName = "";
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			String partyTypeId = party.getString("partyTypeId");
			if (partyTypeId.equals("PERSON")) {
				partyInfo = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(partyInfo)) {
					partyFullName = PartyHelper.getPartyName(delegator, partyInfo.getString("partyId"), true, true);
					String idIssuePlace = partyInfo.getString("idIssuePlace");
					resultParty.put("issuePlace", getGeoName(delegator, idIssuePlace));
					resultParty.putAll(partyInfo);
				}
			} else {
				partyInfo = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(partyInfo)) {
					partyFullName = partyInfo.getString("groupName");
				}
			}
			List<EntityCondition> EntityConditions = FastList.newInstance();
			EntityConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			EntityConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			List<GenericValue> listPartyContactMech = delegator.findList("PartyContactMechPurpose",
					EntityCondition.makeCondition(EntityConditions), null, null, null, false);
			List<Map> listAddress = FastList.newInstance();
			if (UtilValidate.isNotEmpty(listPartyContactMech)) {
				for (GenericValue x : listPartyContactMech) {
					String contactMechId = x.getString("contactMechId");
					GenericValue contactMech = delegator.findOne("ContactMech",
							UtilMisc.toMap("contactMechId", contactMechId), false);
					if (UtilValidate.isEmpty(contactMech)) {
						continue;
					}
					String contactMechTypeId = contactMech.getString("contactMechTypeId");
					switch (contactMechTypeId) {
					case "TELECOM_NUMBER":
						if ("PRIMARY_PHONE".equals(x.getString("contactMechPurposeTypeId"))) {
							GenericValue telecomNumber = delegator.findOne("TelecomNumber",
									UtilMisc.toMap("contactMechId", contactMechId), false);
							resultParty.put("contactNumber", telecomNumber.getString("contactNumber"));
						}
						break;
					case "EMAIL_ADDRESS":
						if ("PRIMARY_EMAIL".equals(x.getString("contactMechPurposeTypeId"))) {
							resultParty.put("emailAddress", contactMech.getString("infoString"));
						}
						break;
					case "POSTAL_ADDRESS":
						GenericValue postalAddress = delegator.findOne("PostalAddressAndGeo",
								UtilMisc.toMap("contactMechId", contactMechId), false);
						if (UtilValidate.isEmpty(postalAddress)) {
							break;
						}
						List<EntityCondition> conditions = FastList.newInstance();
						conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
						conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId",
								contactMechId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
						List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
								EntityCondition.makeCondition(conditions), null, null, null, false);
						if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
							for (GenericValue z : listPartyContactMechPurpose) {
								Map<String, Object> mapAddress = FastMap.newInstance();
								String contactMechPurposeTypeId = z.getString("contactMechPurposeTypeId");
								GenericValue contactMechPurposeType = delegator.findOne("ContactMechPurposeType",
										UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId), false);
								if (UtilValidate.isNotEmpty(contactMechPurposeType)) {
									String contactMechPurposeTypeStr = (String) contactMechPurposeType
											.get("description", locale);
									mapAddress.put("contactMechPurposeTypeId", contactMechPurposeTypeStr);
									mapAddress.put("contactMechPurposeType", contactMechPurposeTypeId);
									mapAddress.put("contactMechId", contactMechId);
									String address1 = "";
									if (UtilValidate.isNotEmpty(postalAddress)) {
										address1 = postalAddress.getString("address1") + ", "
												+ postalAddress.getString("wardGeoName") + ", "
												+ postalAddress.getString("districtGeoName") + ", "
												+ postalAddress.getString("stateProvinceGeoName") + ", "
												+ postalAddress.getString("countryGeoName");
									}
									if (UtilValidate.isNotEmpty(address1)) {
										address1 = address1.replaceAll(", null, ", ", ");
										address1 = address1.replaceAll(", null,", ", ");
									}
									mapAddress.put("address1", address1);
									mapAddress.put("districtGeoName", postalAddress.getString("districtGeoName"));
									listAddress.add(mapAddress);
								}
							}
						}
						break;
					default:
						break;
					}
				}
			}
			resultParty.put("partyFullName", partyFullName);
			resultParty.put("listAddress", listAddress);
			result.put("partyInfo", resultParty);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> listProductForAgreement(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			List<GenericValue> listProducts = delegator.findList("Product",
					EntityCondition.makeCondition(UtilMisc.toMap("productTypeId", "FINISHED_GOOD", "isVariant", "N")),
					null, null, null, false);
			List<Map> listProduct = FastList.newInstance();
			for (GenericValue x : listProducts) {
				Map<String, Object> mapProductsInfo = FastMap.newInstance();
				String productId = x.getString("productId");
				mapProductsInfo.put("price", getProductDefaltPrice(productId, delegator));
				mapProductsInfo.put("productId", productId);
				mapProductsInfo.put("productName", x.getString("productName"));
				mapProductsInfo.put("isVirtual", x.getString("isVirtual"));
				mapProductsInfo.put("parentId", null);
				listProduct.add(mapProductsInfo);
				List<GenericValue> listProductVariant = delegator.findList("ProductAssoc",
						EntityCondition.makeCondition(
								UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT")),
						null, null, null, false);
				for (GenericValue z : listProductVariant) {
					String productVariantId = z.getString("productIdTo");
					Map<String, Object> mapProductsInfo2 = FastMap.newInstance();
					mapProductsInfo2.put("price", getProductDefaltPrice(productVariantId, delegator));
					mapProductsInfo2.put("productId", productVariantId);
					GenericValue productVariant = delegator.findOne("Product",
							UtilMisc.toMap("productId", productVariantId), false);
					if (UtilValidate.isNotEmpty(productVariant)) {
						mapProductsInfo2.put("productName", productVariant.getString("productName"));
						mapProductsInfo2.put("isVirtual", productVariant.getString("isVirtual"));
					}
					mapProductsInfo2.put("parentId", productId);
					listProduct.add(mapProductsInfo2);
				}
			}
			result.put("listProduct", listProduct);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static BigDecimal getProductDefaltPrice(String productId, Delegator delegator)
			throws GenericEntityException {
		BigDecimal price = null;
		List<GenericValue> listProductPrice = delegator.findList("ProductPrice",
				EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId",
						"DEFAULT_PRICE", "productPricePurposeId", "PURCHASE", "currencyUomId", "VND",
						"productStoreGroupId", "_NA_")),
				null, null, null, false);
		if (UtilValidate.isNotEmpty(listProductPrice)) {
			GenericValue productPrice = EntityUtil.getFirst(listProductPrice);
			price = productPrice.getBigDecimal("price");
		}
		return price;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> loadAgreementInformationAjax(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> agreementInformation = FastMap.newInstance();
			Map<String, Object> agreementTermIds = FastMap.newInstance();
			List<Map> listProductsSaved = FastList.newInstance();
			String agreementId = (String) context.get("agreementId");
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			if (UtilValidate.isEmpty(agreement)) {
				return result;
			}
			String partyIdFrom = agreement.getString("partyIdFrom");
			List<GenericValue> listAgreementRole = delegator.findList("AgreementRole",
					EntityCondition
							.makeCondition(UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "SALES_REP")),
					null, null, null, false);
			if (UtilValidate.isEmpty(listAgreementRole)) {
				return result;
			}
			GenericValue agreementRole = EntityUtil.getFirst(listAgreementRole);
			String partyRepresentId = agreementRole.getString("partyId");
			String staffContract = "";
			List<GenericValue> listAgreementRole2 = delegator.findList("AgreementRole",
					EntityCondition
							.makeCondition(UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "SALES_EXECUTIVE")),
					null, null, null, false);
			if (UtilValidate.isNotEmpty(listAgreementRole2)) {
				GenericValue agreementRole2 = EntityUtil.getFirst(listAgreementRole2);
				staffContract = agreementRole2.getString("partyId");
			}
			List<GenericValue> listAgreementTerm = delegator.findList("AgreementTerm",
					EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
			for (GenericValue x : listAgreementTerm) {
				String termTypeId = x.getString("termTypeId");
				switch (termTypeId) {
				case "SHIPPING_ADDRESS":
					String contactMechId = x.getString("textValue");
					if (UtilValidate.isNotEmpty(contactMechId)) {
						GenericValue postalAddress = delegator.findOne("PostalAddress",
								UtilMisc.toMap("contactMechId", contactMechId), false);
						if (UtilValidate.isNotEmpty(postalAddress)) {
							agreementInformation.put("address1", postalAddress.getString("address1"));
						}
						agreementInformation.put("contactMechId", contactMechId);
						agreementTermIds.put("geoIdAddressATI", x.getString("agreementTermId"));
					}
					break;
				case "USE_PACKAGE_NETDAYS":
					agreementInformation.put("packageNetDays", x.getString("textValue"));
					agreementTermIds.put("packageNetDaysATI", x.getString("agreementTermId"));
					break;
				case "FIN_PAY_TOTAL_MIN":
					agreementInformation.put("payTotalMin", x.getString("textValue"));
					agreementTermIds.put("payTotalMinATI", x.getString("agreementTermId"));
					break;
				case "FIN_PAYMENT_METHOD":
					agreementInformation.put("paymentMethod", x.getString("textValue"));
					agreementTermIds.put("paymentMethodATI", x.getString("agreementTermId"));
					break;
				case "FIN_PAYMENT_FREQUEN":
					agreementInformation.put("paymentFrequen", x.getString("textValue"));
					agreementTermIds.put("paymentFrequenATI", x.getString("agreementTermId"));
					break;
				case "DELIVER_DATE_FREQUEN":
					String description = x.getString("description");
					if ("AND".equals(description)) {
						agreementInformation.put("deliverDate", x.getString("textValue"));
						agreementTermIds.put("deliverDateATI", x.getString("agreementTermId"));
					} else {
						agreementInformation.put("deliverDate2", x.getString("textValue"));
						agreementTermIds.put("deliverDate2ATI", x.getString("agreementTermId"));
					}
					break;
				case "SOLICITATION_METHOD":
					agreementInformation.put("solicitationMethod", x.getString("textValue"));
					agreementTermIds.put("solicitationMethodATI", x.getString("agreementTermId"));
					break;
				default:
					break;
				}
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdFrom, "roleTypeId", "CUSTOMER")));
			List<GenericValue> listProductStoreRole = delegator.findList("ProductStoreRole",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productStoreId"), null, null, false);
			List<String> listProductStoreId = EntityUtil.getFieldListFromEntityList(listProductStoreRole,
					"productStoreId", true);
			agreementInformation.put("listProductStoreId", listProductStoreId);
			agreementInformation.put("partyRepresentId", partyRepresentId);
			agreementInformation.put("staffContract", staffContract);
			agreementInformation.putAll(agreement);
			GenericValue agreementAttribute = delegator.findOne("AgreementAttribute",
					UtilMisc.toMap("agreementId", agreementId, "attrName", "agreementType"), false);
			if (UtilValidate.isNotEmpty(agreementAttribute)) {
				agreementInformation.put("agreementType", agreementAttribute.getString("attrValue"));
			}
			List<Map> listProducts = FastList.newInstance();
			List<GenericValue> listAgreementProductAppl = delegator.findList("AgreementProductAppl",
					EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
			for (GenericValue x : listAgreementProductAppl) {
				Map<String, Object> mapProducts = FastMap.newInstance();
				Map<String, Object> agreementProductApplIds = FastMap.newInstance();
				String agreementItemSeqId = x.getString("agreementItemSeqId");
				GenericValue agreementItem = delegator.findOne("AgreementItem",
						UtilMisc.toMap("agreementId", agreementId, "agreementItemSeqId", agreementItemSeqId), false);
				if (UtilValidate.isEmpty(agreementItem)) {
					continue;
				}
				String note = agreementItem.getString("agreementText");
				String productId = x.getString("productId");
				BigDecimal quantity = x.getBigDecimal("quantity");
				mapProducts.put("agreementItemSeqId", agreementItemSeqId);
				mapProducts.put("productId", productId);
				mapProducts.put("quantity", quantity);
				mapProducts.put("note", note);
				listProducts.add(mapProducts);
				agreementProductApplIds.put("agreementId", x.getString("agreementId"));
				agreementProductApplIds.put("agreementItemSeqId", x.getString("agreementItemSeqId"));
				agreementProductApplIds.put("productId", x.getString("productId"));
				listProductsSaved.add(agreementProductApplIds);
			}
			agreementInformation.put("listProducts", listProducts);
			result.put("agreementInformation", agreementInformation);
			result.put("agreementTermIds", agreementTermIds);
			result.put("listProductsSaved", listProductsSaved);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<String> getFamilyOfPerson(String partyId, Delegator delegator) throws GenericEntityException {
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom",
				"REPRESENTATIVE", "roleTypeIdTo", "HOUSEHOLD")));
		List<GenericValue> listFamily = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(listConditions), null, UtilMisc.toList("-fromDate"), null, false);
		return EntityUtil.getFieldListFromEntityList(listFamily, "partyIdTo", true);
	}

	public static String getPartyRepresentative(String partyPartnerBusinessesId, Delegator delegator)
			throws GenericEntityException {
		String partyId = "";
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(
				UtilMisc.toMap("partyIdTo", partyPartnerBusinessesId, "roleTypeIdFrom", "REPRESENTATIVE")));
		List<GenericValue> listPartyRepresentative = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(listConditions), null, null, null, false);
		if (UtilValidate.isNotEmpty(listPartyRepresentative)) {
			GenericValue partyRepresentative = EntityUtil.getFirst(listPartyRepresentative);
			partyId = partyRepresentative.getString("partyIdFrom");
		}
		return partyId;
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	public static Map<String, Object> DmsUpdateAgreementAjax(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String agreementId = (String) context.get("agreementId");
			List<String> productStoreId = (List<String>) context.get("productStoreId[]");
			Long agreementDateL = (Long) context.get("agreementDate");
			Long fromDateL = (Long) context.get("fromDate");
			Timestamp fromDate = null;
			Timestamp agreementDate = null;
			if (UtilValidate.isNotEmpty(agreementDateL)) {
				agreementDate = new Timestamp(agreementDateL);
			}
			if (UtilValidate.isNotEmpty(fromDateL)) {
				fromDate = new Timestamp(fromDateL);
			}
			Map<String, Object> mapUpdateAgreement = FastMap.newInstance();
			mapUpdateAgreement.put("agreementId", agreementId);
			mapUpdateAgreement.put("statusId", "AGREEMENT_MODIFIED");
			mapUpdateAgreement.put("agreementCode", context.get("agreementCode"));
			mapUpdateAgreement.put("description", context.get("description"));
			mapUpdateAgreement.put("agreementDate", agreementDate);
			mapUpdateAgreement.put("fromDate", fromDate);
			mapUpdateAgreement.put("userLogin", userLogin);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(fromDateL);
			calendar.add(calendar.MONTH, +Integer.parseInt((String) context.get("textValue1")));
			Timestamp thruDate = new Timestamp(calendar.getTimeInMillis());
			mapUpdateAgreement.put("thruDate", thruDate);
			dispatcher.runSync("updateAgreement", mapUpdateAgreement);
			// updateAgreementRole
			// check SALES_EXECUTIVE in AgreementRole
			List<GenericValue> agreementRoles = delegator
					.findList("AgreementRole",
							EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId",
									context.get("staffContract"), "roleTypeId", "SALES_EXECUTIVE")),
							null, null, null, false);
			if (UtilValidate.isEmpty(agreementRoles)) {
				mapUpdateAgreement.clear();
				mapUpdateAgreement.put("agreementId", agreementId);
				mapUpdateAgreement.put("partyId", context.get("staffContract"));
				mapUpdateAgreement.put("roleTypeId", "SALES_EXECUTIVE");
				mapUpdateAgreement.put("userLogin", userLogin);
				dispatcher.runSync("createAgreementRole", mapUpdateAgreement);
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_EQUAL,
						context.get("staffContract")));
				conditions.add(EntityCondition
						.makeCondition(UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "SALES_EXECUTIVE")));
				agreementRoles = delegator.findList("AgreementRole", EntityCondition.makeCondition(conditions), null,
						null, null, false);
				for (GenericValue x : agreementRoles) {
					x.remove();
				}
			}
			dispatcher.runSync("checkAndUpdateRelSalesExecutive", UtilMisc.toMap("partyId", context.get("partyIdFrom"),
					"salesExecutiveId", context.get("staffContract"), "userLogin", userLogin));

			mapUpdateAgreement.clear();
			mapUpdateAgreement.put("agreementTermId", context.get("geoIdAddressATI"));
			mapUpdateAgreement.put("textValue", context.get("textValue8"));
			mapUpdateAgreement.put("userLogin", userLogin);
			dispatcher.runSync("updateAgreementTerm", mapUpdateAgreement);

			mapUpdateAgreement.clear();
			mapUpdateAgreement.put("agreementTermId", context.get("packageNetDaysATI"));
			mapUpdateAgreement.put("textValue", context.get("textValue1"));
			mapUpdateAgreement.put("termValue", context.get("textValue1"));
			mapUpdateAgreement.put("userLogin", userLogin);
			dispatcher.runSync("updateAgreementTerm", mapUpdateAgreement);

			mapUpdateAgreement.clear();
			mapUpdateAgreement.put("agreementTermId", context.get("payTotalMinATI"));
			mapUpdateAgreement.put("textValue", context.get("textValue2"));
			mapUpdateAgreement.put("termValue", context.get("textValue2"));
			mapUpdateAgreement.put("userLogin", userLogin);
			dispatcher.runSync("updateAgreementTerm", mapUpdateAgreement);

			mapUpdateAgreement.clear();
			mapUpdateAgreement.put("agreementTermId", context.get("paymentMethodATI"));
			mapUpdateAgreement.put("textValue", context.get("textValue3"));
			mapUpdateAgreement.put("userLogin", userLogin);
			dispatcher.runSync("updateAgreementTerm", mapUpdateAgreement);

			mapUpdateAgreement.clear();
			mapUpdateAgreement.put("agreementTermId", context.get("paymentFrequenATI"));
			mapUpdateAgreement.put("textValue", context.get("textValue4"));
			mapUpdateAgreement.put("userLogin", userLogin);
			dispatcher.runSync("updateAgreementTerm", mapUpdateAgreement);

			mapUpdateAgreement.clear();
			mapUpdateAgreement.put("agreementTermId", context.get("deliverDateATI"));
			mapUpdateAgreement.put("textValue", context.get("textValue6"));
			mapUpdateAgreement.put("userLogin", userLogin);
			dispatcher.runSync("updateAgreementTerm", mapUpdateAgreement);

			mapUpdateAgreement.clear();
			mapUpdateAgreement.put("agreementTermId", context.get("deliverDate2ATI"));
			mapUpdateAgreement.put("textValue", context.get("textValue5"));
			mapUpdateAgreement.put("userLogin", userLogin);
			dispatcher.runSync("updateAgreementTerm", mapUpdateAgreement);

			mapUpdateAgreement.clear();
			mapUpdateAgreement.put("agreementTermId", context.get("solicitationMethodATI"));
			mapUpdateAgreement.put("textValue", context.get("textValue7"));
			mapUpdateAgreement.put("userLogin", userLogin);
			dispatcher.runSync("updateAgreementTerm", mapUpdateAgreement);

			String jsonData = (String) context.get("productInAgreement");
			JSONArray productInAgreement = new JSONArray().fromObject(jsonData);

			jsonData = (String) context.get("listProductsSaved");
			JSONArray listProductsSaved = new JSONArray().fromObject(jsonData);

			for (int i = 0; i < productInAgreement.size(); i++) {
				mapUpdateAgreement.clear();
				JSONObject thisRow = productInAgreement.getJSONObject(i);
				String productId = null;
				if (thisRow.containsKey("productId")) {
					productId = (String) thisRow.get("productId");
				}
				String note = null;
				if (thisRow.containsKey("note")) {
					note = String.valueOf(thisRow.get("note"));
				}
				Object price = null;
				if (thisRow.containsKey("price")) {
					price = String.valueOf(thisRow.get("price"));
				}
				Object quantity = null;
				if (thisRow.containsKey("quantity")) {
					quantity = String.valueOf(thisRow.get("quantity"));
				}
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setGroupingSeparator(',');
				symbols.setDecimalSeparator('.');
				String pattern = "#,##0.0#";
				DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
				decimalFormat.setParseBigDecimal(true);
				price = (BigDecimal) decimalFormat.parse(price.toString());
				quantity = (BigDecimal) decimalFormat.parse(quantity.toString());
				mapUpdateAgreement.put("agreementId", agreementId);
				mapUpdateAgreement.put("userLogin", userLogin);
				mapUpdateAgreement.put("agreementText", note);
				mapUpdateAgreement.put("productId", productId);
				boolean saved = false;
				// 1. Update a existing product in Agreement
				for (int r = 0; r < listProductsSaved.size(); r++) {
					JSONObject thisRowS = listProductsSaved.getJSONObject(r);
					String productIdS = null;
					if (thisRow.containsKey("productId")) {
						productIdS = (String) thisRowS.get("productId");
					}
					if (productId.equals(productIdS)) {
						saved = true;
						String agreementItemSeqId = null;
						if (thisRowS.containsKey("agreementItemSeqId")) {
							agreementItemSeqId = (String) thisRowS.get("agreementItemSeqId");
						}
						mapUpdateAgreement.put("agreementItemSeqId", agreementItemSeqId);

						// 1.a. removeProductIfUserWant
						if (quantity.equals(BigDecimal.ZERO)) {
							dispatcher.runSync("removeAgreementProductAppl", mapUpdateAgreement);
							dispatcher.runSync("removeAgreementItem", mapUpdateAgreement);
							break;
						}
						// removeProductIfUserWant
						dispatcher.runSync("updateAgreementItem", mapUpdateAgreement);

						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId),
								false);
						mapUpdateAgreement.put("quantityUomId", product.getString("quantityUomId"));
						mapUpdateAgreement.put("price", price);
						mapUpdateAgreement.put("quantity", quantity);
						dispatcher.runSync("updateAgreementProductAppl", mapUpdateAgreement);
						break;
					}
				}
				// Update a existing product in Agreement
				if (saved) {
					continue;
				}
				// 2. Add new product
				mapUpdateAgreement.put("agreementItemTypeId", "AGREEMENT_PRICING_PR");
				mapUpdateAgreement.put("agreementText", note);
				List<GenericValue> listProduct = delegator.findList(
						"ProductPrice", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId,
								"productPriceTypeId", "DEFAULT_PRICE", "productPricePurposeId", "PURCHASE")),
						null, null, null, false);
				if (UtilValidate.isNotEmpty(listProduct)) {
					GenericValue product = EntityUtil.getFirst(listProduct);
					mapUpdateAgreement.put("currencyUomId", product.getString("quantityUomId"));
				}
				Map<String, Object> mapResultCreateAgreementItem = dispatcher.runSync("createAgreementItem",
						mapUpdateAgreement);
				String agreementItemSeqId = (String) mapResultCreateAgreementItem.get("agreementItemSeqId");
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				if (UtilValidate.isNotEmpty(product)) {
					mapUpdateAgreement.put("quantityUomId", product.getString("quantityUomId"));
				}
				mapUpdateAgreement.put("agreementItemSeqId", agreementItemSeqId);
				mapUpdateAgreement.put("price", price);
				mapUpdateAgreement.put("quantity", quantity);
				dispatcher.runSync("createAgreementProductAppl", mapUpdateAgreement);
				// Add new product
			}
			// checkProductStoreRole
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition
					.makeCondition(UtilMisc.toMap("partyId", context.get("partyIdFrom"), "roleTypeId", "CUSTOMER")));
			List<GenericValue> listProductStoreRoles = delegator.findList("ProductStoreRole",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productStoreId"), null, null, false);
			List<String> listProductStoreId = EntityUtil.getFieldListFromEntityList(listProductStoreRoles,
					"productStoreId", true);
			listProductStoreId.removeAll(productStoreId);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition
					.makeCondition(UtilMisc.toMap("partyId", context.get("partyIdFrom"), "roleTypeId", "CUSTOMER")));
			conditions.add(EntityCondition.makeCondition("productStoreId", EntityJoinOperator.IN, listProductStoreId));
			List<GenericValue> listProductStoreRoles2 = delegator.findList("ProductStoreRole",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : listProductStoreRoles2) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				delegator.store(x);
			}
			for (String s : productStoreId) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", context.get("partyIdFrom"),
						"roleTypeId", "CUSTOMER", "productStoreId", s)));
				List<GenericValue> listProductStoreRole = delegator.findList("ProductStoreRole",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(listProductStoreRole)) {
					mapUpdateAgreement.clear();
					mapUpdateAgreement.put("partyId", context.get("partyIdFrom"));
					mapUpdateAgreement.put("roleTypeId", "CUSTOMER");
					mapUpdateAgreement.put("productStoreId", s);
					mapUpdateAgreement.put("userLogin", userLogin);
					dispatcher.runSync("createProductStoreRole", mapUpdateAgreement);
				}
			}
			result.put("agreementId", agreementId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	private static String cleanString(String originalString) {
		String pureString = null;
		if (UtilValidate.isNotEmpty(originalString)) {
			if (!UtilMisc.toList("null", "undefined").contains(originalString)) {
				pureString = originalString.trim();
			}
		}
		return pureString;
	}

	private static String getGeoName(Delegator delegator, String geoId) throws GenericEntityException {
		String geoName = "";
		if (UtilValidate.isNotEmpty(geoId)) {
			GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
			if (UtilValidate.isNotEmpty(geo)) {
				geoName = geo.getString("geoName");
			}
		}
		return geoName;
	}

	public static Map<String, Object> removeMemberInFamily(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		try {
			String familyId = (String) context.get("familyId");
			String partyId = (String) context.get("partyId");
			String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
			// updatePartyRelationship thrudate
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", familyId,
					"roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", "HOUSEHOLD")));
			List<GenericValue> listPartyRelationship = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : listPartyRelationship) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				delegator.store(x);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetAddressPartner(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listAddress = FastList.newInstance();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			String partyId = (String) parameters.get("partyId")[0];
			String contactMechPurposeTypeId = (String) parameters.get("contactMechPurposeTypeId")[0];
			if (UtilValidate.isNotEmpty(partyId)) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", contactMechPurposeTypeId)));
				List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				List<String> listContactMechId = EntityUtil.getFieldListFromEntityList(listPartyContactMechPurpose,
						"contactMechId", true);
				List<GenericValue> listPostalAddress = delegator.findList("PostalAddressAndGeo",
						EntityCondition.makeCondition("contactMechId", EntityJoinOperator.IN, listContactMechId), null,
						null, null, false);
				for (GenericValue x : listPostalAddress) {
					Map<String, Object> mapAddress = FastMap.newInstance();
					mapAddress.put("countryGeoId", x.getString("countryGeoId"));
					mapAddress.put("stateProvinceGeoId", x.getString("stateProvinceGeoId"));
					mapAddress.put("districtGeoId", x.getString("districtGeoId"));
					mapAddress.put("wardGeoId", x.getString("wardGeoId"));

					mapAddress.put("countryGeo", x.getString("countryGeoName"));
					mapAddress.put("stateProvinceGeo", x.getString("stateProvinceGeoName"));
					mapAddress.put("districtGeo", x.getString("districtGeoName"));
					mapAddress.put("wardGeo", x.getString("wardGeoName"));

					mapAddress.put("address1", x.getString("address1"));
					mapAddress.put("postalCode", x.getString("postalCode"));
					mapAddress.put("contactMechId", x.getString("contactMechId"));
					listAddress.add(mapAddress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MarketingUtils.listCompact(listAddress, context);
	}

	public static Map<String, Object> listMarketingPlan(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listMarketingPlan = FastList.newInstance();
		List<GenericValue> listMarketingPlanParents = delegator.findList("MarketingPlan",
				EntityCondition.makeCondition("parentPlanId", EntityJoinOperator.EQUALS, null), null, null, null,
				false);
		for (GenericValue x : listMarketingPlanParents) {
			Map<String, Object> marketingPlan = FastMap.newInstance();
			marketingPlan.putAll(x);
			List<GenericValue> listMarketingPlanChildren = delegator.findList("MarketingPlan", EntityCondition
					.makeCondition("parentPlanId", EntityJoinOperator.EQUALS, x.getString("marketingPlanId")), null,
					null, null, false);
			marketingPlan.put("rowDetail", listMarketingPlanChildren);
			listMarketingPlan.add(marketingPlan);
		}
		return MarketingUtils.listCompact(listMarketingPlan, context);
	}

	public static Map<String, Object> getMarketingEmployee(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String partyId = (String) context.get("partyId");
			if (UtilValidate.isEmpty(partyId)) {
				partyId = "DMKT";
			}
			Organization org = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> employees = org.getEmployeeInOrg(delegator);
			result.put("results", employees);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createOrStoreMember(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String rows = (String) context.get("rows");
			if (UtilValidate.isNotEmpty(rows)) {
				JSONArray rowsAray = JSONArray.fromObject(rows);
				for (int i = 0; i < rowsAray.size(); i++) {
					JSONObject o = rowsAray.getJSONObject(i);
					String familyId = null;
					if (o.containsKey("familyId")) {
						familyId = cleanString(o.getString("familyId"));
					}
					String partyId = null;
					if (o.containsKey("partyId")) {
						partyId = cleanString(o.getString("partyId"));
					}
					String roleTypeIdFrom = null;
					if (o.containsKey("roleTypeIdFrom")) {
						roleTypeIdFrom = cleanString(o.getString("roleTypeIdFrom"));
					}
					partyId = createPartyAndRelationship(delegator, dispatcher, userLogin, familyId, partyId,
							roleTypeIdFrom);
					String gender = null;
					if (o.containsKey("gender")) {
						gender = cleanString(o.getString("gender"));
					}
					String partyFullName = null;
					if (o.containsKey("partyFullName")) {
						partyFullName = cleanString(o.getString("partyFullName"));
					}
					Long birthDate = null;
					if (o.containsKey("birthDate")) {
						birthDate = o.getLong("birthDate");
					}
					Date dob = null;
					if (UtilValidate.isNotEmpty(birthDate)) {
						dob = new Date(birthDate);
					}
					Map<String, Object> mapPerson = FastMap.newInstance();
					mapPerson.put("partyId", partyId);
					mapPerson.put("gender", gender);
					mapPerson.put("birthDate", dob);
					Map<String, Object> in = FastMap.newInstance();
					in.put("userLogin", userLogin);
					in.put("fullName", partyFullName);
					Map<String, Object> out = dispatcher.runSync("processName", in);
					mapPerson.putAll(out);
					GenericValue person = delegator.makeValue("Person", mapPerson);
					delegator.createOrStore(person);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	private static String createPartyAndRelationship(Delegator delegator, LocalDispatcher dispatcher,
			GenericValue userLogin, String familyId, String partyId, String roleTypeIdFrom)
			throws GenericEntityException, GenericServiceException {
		// check party available
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		// if not available create new it
		if (UtilValidate.isEmpty(party)) {
			partyId = delegator.getNextSeqId("Party");
			delegator.create("Party",
					UtilMisc.toMap("partyId", partyId, "partyTypeId", "PERSON", "statusId", "PARTY_ENABLED"));
		}
		// make relationship with family
		dispatcher.runSync("addMemberToFamily", UtilMisc.toMap("partyId", partyId, "familyId", familyId,
				"roleTypeIdFrom", roleTypeIdFrom, "userLogin", userLogin));
		return partyId;
	}

	public static void sendNotify(LocalDispatcher dispatcher, Locale locale, List<String> partyIds, String header,
			String state, String action, String targetLink, String ntfType, String sendToGroup, String sendrecursive,
			Timestamp dateTime, GenericValue userLogin) throws GenericServiceException {
		dispatcher.runSync("createNotification",
				UtilMisc.toMap("partiesList", partyIds, "header", header, "state", state, "action", action,
						"targetLink", targetLink, "dateTime", dateTime, "ntfType", ntfType, "sendToGroup", sendToGroup,
						"sendrecursive", sendrecursive, "userLogin", userLogin));
	}

	public static Map<String, Object> loadProductPrice(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> productPrice = FastMap.newInstance();
			String productId = (String) context.get("productId");
			String currencyUomId = (String) context.get("currencyUomId");
			String termUomId = (String) context.get("termUomId");
			String taxInPrice = (String) context.get("taxInPrice");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId",
					"LIST_PRICE", "productPricePurposeId", "PURCHASE", "productStoreGroupId", "_NA_", "currencyUomId",
					currencyUomId, "termUomId", termUomId, "taxInPrice", taxInPrice)));
			List<GenericValue> listProductListPrice = delegator.findList("ProductPrice",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			BigDecimal productListPrice = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(listProductListPrice)) {
				productListPrice = EntityUtil.getFirst(listProductListPrice).getBigDecimal("price");
			}
			productPrice.put("productListPrice", productListPrice);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId",
					"DEFAULT_PRICE", "productPricePurposeId", "PURCHASE", "productStoreGroupId", "_NA_",
					"currencyUomId", currencyUomId, "termUomId", termUomId, "taxInPrice", taxInPrice)));
			List<GenericValue> listProductDefaultPrice = delegator.findList("ProductPrice",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			BigDecimal productDefaultPrice = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(listProductDefaultPrice)) {
				productDefaultPrice = EntityUtil.getFirst(listProductDefaultPrice).getBigDecimal("price");
			}
			productPrice.put("productDefaultPrice", productDefaultPrice);
			result.put("productPrice", productPrice);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listFeedbacks(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions
					.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "COM_SCHEDULED"));
			listAllConditions.add(
					EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "COM_SCHEDULED_CLOSED"));
			listAllConditions.add(EntityCondition.makeCondition("communicationEventTypeId", "FACE_TO_FACE_COMMUNI"));
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("+entryDate");
			EntityListIterator listIter = delegator.find("CommunicationEventDetail",
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields,
					opts);
			result.put("listIterator", listIter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}