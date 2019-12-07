package com.olbius.crm;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CRMCampaignServices {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCampaigns(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("-fromDate");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("marketingTypeId", "CALLCAMPAIGN"));
			listAllConditions
					.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "MKTG_CAMP_DELETED"));
			EntityListIterator listIterator = delegator.find("MarketingCampaignDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> checkCampaignExist(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			List<GenericValue> list = delegator.findList("MarketingCampaign",
					EntityCondition.makeCondition(UtilMisc.toMap("marketingCampaignId", marketingCampaignId)), null,
					null, null, false);
			if (UtilValidate.isNotEmpty(list)) {
				result.put("result", "EXIST");
			} else {
				result.put("result", "NOT_EXIST");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", "ERROR");
		}
		return result;
	}

	public static Map<String, Object> getEmployee(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String partyId = (String) context.get("partyId");
			if (UtilValidate.isEmpty(partyId)) {
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
						userLogin.getString("userLoginId"));
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition
						.makeCondition(UtilMisc.toMap("partyIdFrom", organizationId, "roleTypeIdFrom", "SUBSIDIARY",
								"roleTypeIdTo", "CALLCENTER_DEPT", "partyRelationshipTypeId", "GROUP_ROLLUP")));
				List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
						EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdTo"), null, null, false);
				if (UtilValidate.isNotEmpty(partyRelationships)) {
					partyId = EntityUtil.getFirst(partyRelationships).getString("partyIdTo");
				}
			}
			Organization org = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> employees = org.getEmployeeInOrg(delegator);
			List<Map<String, Object>> listEmployees = FastList.newInstance();
			for (GenericValue x : employees) {
				Map<String, Object> employee = FastMap.newInstance();
				employee.putAll(x);
				employee.put("partyFullName", x.get("fullName"));
				employee.put("partyDetail", x.get("fullName") + " [" + x.getString("partyCode") + "]");
				listEmployees.add(employee);
			}
			result.put("results", listEmployees);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createCallCampaignAndContact(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			String isActive = (String) context.get("isActive");
			ModelService model = ctx.getModelService("createCRMMarketingCampaign");
			Map<String, Object> inputMap = model.makeValid(context, ModelService.IN_PARAM);
			String isDone = (String) context.get("isDone");
			if (isDone.equals("Y")) {
				inputMap.put("statusId", "MKTG_CAMP_COMPLETED");
			} else {
				if (isActive.equals("Y")) {
					inputMap.put("statusId", "MKTG_CAMP_INPROGRESS");
				} else {
					inputMap.put("statusId", "MKTG_CAMP_PLANNED");
				}
			}
			Map<String, Object> marketing = dispatcher.runSync("createCRMMarketingCampaign", inputMap);
			String marketingCampaignId = (String) marketing.get("marketingCampaignId");
			result.put("marketingCampaignId", marketingCampaignId);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil
					.returnError(UtilProperties.getMessage("CustomMarketingUiLabels", "CannotCreateCampaign", locale));
		}
		return result;
	}

	public static Map<String, Object> updateCallCampaignAndContact(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			String isActive = (String) context.get("isActive");
			ModelService model = ctx.getModelService("updateCRMMarketingCampaign");
			Map<String, Object> inputMap = model.makeValid(context, ModelService.IN_PARAM);
			String isDone = (String) context.get("isDone");
			if (isDone.equals("Y")) {
				inputMap.put("statusId", "MKTG_CAMP_COMPLETED");
			} else {
				if (isActive.equals("Y")) {
					inputMap.put("statusId", "MKTG_CAMP_INPROGRESS");
				} else {
					inputMap.put("statusId", "MKTG_CAMP_PLANNED");
				}
			}
			dispatcher.runSync("updateCRMMarketingCampaign", inputMap);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil
					.returnError(UtilProperties.getMessage("CustomMarketingUiLabels", "CannotCreateCampaign", locale));
		}
		return result;
	}

	public static Map<String, Object> removeCallCampaign(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		EntityListIterator list = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			Map<String, Object> inputMap = FastMap.newInstance();
			inputMap.put("userLogin", userLogin);
			inputMap.put("marketingCampaignId", marketingCampaignId);
			inputMap.put("statusId", "MKTG_CAMP_DELETED");
			dispatcher.runSync("updateMarketingCampaign", inputMap);
			list = delegator.find("PartyCampaignRelationship",
					EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId), null, null, null, null);
			GenericValue e = null;
			long currentMillis = System.currentTimeMillis();
			Date currentDate = Date.valueOf(new Date(currentMillis).toString());
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
			while ((e = list.next()) != null) {
				e.set("thruDate", currentTimestamp);
				e.setString("statusId", "CONTACT_REJECTED");
				e.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil
					.returnError(UtilProperties.getMessage("CustomMarketingUiLabels", "CannotRemoveCampaign", locale));
		} finally {
			if (list != null) {
				try {
					list.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static Map<String, Object> createMarketingCampaign(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			GenericValue inset = delegator.makeValidValue("MarketingCampaign", context);
			inset.set("createdByUserLogin", userLoginId);
			inset.set("lastModifiedByUserLogin", userLoginId);
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			if (UtilValidate.isEmpty(marketingCampaignId)) {
				marketingCampaignId = delegator.getNextSeqId("MarketingCampaign");
				inset.setString("marketingCampaignId", marketingCampaignId);
			}
			delegator.createOrStore(inset);
			result.put("marketingCampaignId", marketingCampaignId);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil
					.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotCreateCampaign", locale));
		}
		return result;
	}

	public static Map<String, Object> updateMarketingCampaign(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String userLoginId = userLogin.getString("userLoginId");
			if (!UtilValidate.isEmpty(marketingCampaignId)) {
				GenericValue inset = delegator.findOne("MarketingCampaign",
						UtilMisc.toMap("marketingCampaignId", marketingCampaignId), false);
				inset.set("lastModifiedByUserLogin", userLoginId);
				inset.setNonPKFields(context);
				inset.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil
					.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotUpdateCampaign", locale));
		}
		return result;
	}

	public static Map<String, Object> loadCampaignTerms(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> campaignTerms = FastMap.newInstance();
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		List<GenericValue> marketingCampaignAttributes = delegator.findList("MarketingCampaignAttribute",
				EntityCondition.makeCondition("marketingCampaignId", EntityJoinOperator.EQUALS, marketingCampaignId),
				null, null, null, false);
		for (GenericValue x : marketingCampaignAttributes) {
			campaignTerms.put(x.getString("attrName"), x.getString("attrValue"));
		}
		result.put("campaignTerms", campaignTerms);
		return result;
	}

	/* Get contact resource with condition in parameters */
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> getContactResource(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		EntityListIterator list = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			Map<String, String[]> param = (Map<String, String[]>) context.get("parameters");
			String entryDateFrom = null;
			String entryDateTo = null;
			String dataSourceId = null;
			String resultEnumId = null;
			String birthDateFrom = null;
			String birthDateTo = null;
			String areaGeoId = null;
			if (UtilValidate.isNotEmpty(param.get("entryDateFrom"))) {
				entryDateFrom = (String) param.get("entryDateFrom")[0];
			}
			if (UtilValidate.isNotEmpty(param.get("entryDateTo"))) {
				entryDateTo = (String) param.get("entryDateTo")[0];
			}
			if (UtilValidate.isNotEmpty(param.get("resultEnumId"))) {
				resultEnumId = (String) param.get("resultEnumId")[0];
			}
			if (UtilValidate.isNotEmpty(param.get("dataSourceId"))) {
				dataSourceId = (String) param.get("dataSourceId")[0];
			}
			if (UtilValidate.isNotEmpty(param.get("birthDateFrom"))) {
				birthDateFrom = (String) param.get("birthDateFrom")[0];
			}
			if (UtilValidate.isNotEmpty(param.get("birthDateTo"))) {
				birthDateTo = (String) param.get("birthDateTo")[0];
			}
			if (UtilValidate.isNotEmpty(param.get("areaGeoId"))) {
				areaGeoId = (String) param.get("areaGeoId")[0];
			}
			int pagesize = (param.get("pagesize")[0] != null) ? Integer.parseInt(param.get("pagesize")[0]) : null;
			int pagenum = (param.get("pagenum")[0] != null) ? Integer.parseInt(param.get("pagenum")[0]) : null;
			int startIndex = pagesize * pagenum + 1;
			Map<String, Object> inputMap = FastMap.newInstance();
			inputMap.put("opts", opts);
			inputMap.put("listSortFields", listSortFields);
			inputMap.put("listAllConditions", listAllConditions);
			inputMap.put("userLogin", userLogin);
			inputMap.put("dataSourceId", dataSourceId);
			inputMap.put("resultEnumId", resultEnumId);
			inputMap.put("birthDateTo", birthDateTo);
			inputMap.put("birthDateFrom", birthDateFrom);
			inputMap.put("entryDateFrom", entryDateFrom);
			inputMap.put("entryDateTo", entryDateTo);
			inputMap.put("areaGeoId", areaGeoId);
			Map<String, Object> out = dispatcher.runSync("getContactResourceContext", inputMap);
			list = (EntityListIterator) out.get("listIterator");
			if (list == null) {
				return result;
			}
			List<GenericValue> reList = list.getPartialList(startIndex, pagesize);
			List<Map<String, Object>> finalList = FastList.newInstance();
			for (GenericValue e : reList) {
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(e);
				String partyId = e.getString("partyIdFrom");
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
						UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				o.put("contactNumber", (String) partyInfo.get("contactNumber"));
				List<Map<String, Object>> listAddress = (List<Map<String, Object>>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							o.put("address", (String) m.get("address1"));
							break;
						}
					}
				}
				finalList.add(o);
			}
			int totalRow = list.getResultsTotalSize();
			result.put("listIterator", finalList);
			result.put("TotalRows", String.valueOf(totalRow));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (list != null) {
				try {
					list.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/*
	 * core search customer by condition Get contact with condition in context
	 * map
	 */
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> getContactResourceContext(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator list = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			if (opts == null) {
				opts = new EntityFindOptions();
			}
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			if (listAllConditions == null) {
				listAllConditions = FastList.newInstance();
			}
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			if (listSortFields == null) {
				listSortFields = FastList.newInstance();
			}
			Timestamp entryDateFrom = null;
			Timestamp entryDateTo = null;
			StringBuilder dataSource = new StringBuilder();
			StringBuilder resultEnum = new StringBuilder();
			StringBuilder areaGeo = new StringBuilder();
			Date birthDateFrom = null;
			Date birthDateTo = null;
			boolean flag = false;
			if (UtilValidate.isNotEmpty(context.get("entryDateFrom"))) {
				String entryFrom = (String) context.get("entryDateFrom");
				Long et = new Long(entryFrom);
				entryDateFrom = new Timestamp(et);
				flag = true;
			}
			if (UtilValidate.isNotEmpty(context.get("entryDateTo"))) {
				String entryFrom = (String) context.get("entryDateTo");
				Long et2 = new Long(entryFrom);
				entryDateTo = new Timestamp(et2);
				flag = true;
			}
			if (UtilValidate.isNotEmpty(context.get("resultEnumId"))) {
				resultEnum = new StringBuilder((String) context.get("resultEnumId"));
				flag = true;
			}
			if (UtilValidate.isNotEmpty(context.get("dataSourceId"))) {
				dataSource = new StringBuilder((String) context.get("dataSourceId"));
				flag = true;
			}
			if (UtilValidate.isNotEmpty(context.get("birthDateFrom"))) {
				String birthFrom = (String) context.get("birthDateFrom");
				Long bd1 = new Long(birthFrom);
				birthDateFrom = new Date(bd1);
				flag = true;
			}
			if (UtilValidate.isNotEmpty(context.get("birthDateTo"))) {
				String birthTo = (String) context.get("birthDateTo");
				Long bd2 = new Long(birthTo);
				birthDateTo = new Date(bd2);
				flag = true;
			}
			if (UtilValidate.isNotEmpty(context.get("areaGeoId"))) {
				areaGeo = new StringBuilder((String) context.get("areaGeoId"));
				flag = true;
			}
			if (!flag) {
				return result;
			}
			StringBuilder entity = new StringBuilder("PartyResource");
			String dataSourceId = dataSource.toString();
			String resultEnumId = resultEnum.toString();
			String areaGeoId = areaGeo.toString();
			String multi = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if (UtilValidate.isNotEmpty(dataSourceId)) {
				listAllConditions.add(EntityCondition.makeCondition("dataSourceId", dataSourceId));
			}
			if (birthDateFrom != null || birthDateTo != null) {
				if (UtilValidate.isNotEmpty(birthDateFrom)) {
					listAllConditions.add(EntityCondition.makeCondition("memBirthDate",
							EntityOperator.GREATER_THAN_EQUAL_TO, birthDateFrom));
				}
				if (UtilValidate.isNotEmpty(birthDateTo)) {
					listAllConditions.add(EntityCondition.makeCondition("memBirthDate",
							EntityOperator.LESS_THAN_EQUAL_TO, birthDateTo));
				}
			}
			if (UtilValidate.isNotEmpty(areaGeoId)) {
				listAllConditions.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.IN,
						getContactMechInRegion(delegator, areaGeoId)));
			}
			if (UtilValidate.isNotEmpty(resultEnumId)) {
				JSONArray results = JSONArray.fromObject(resultEnumId);
				List<EntityCondition> resultCond = FastList.newInstance();
				for (int i = 0; i < results.size(); i++) {
					String o = results.getString(i);
					resultCond.add(EntityCondition.makeCondition("resultEnumTypeId", o));
				}
				listAllConditions.add(EntityCondition.makeCondition(resultCond, EntityOperator.OR));
			}
			if (entryDateFrom != null) {
				listAllConditions.add(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO,
						entryDateFrom));
			}
			if (entryDateTo != null) {
				listAllConditions.add(
						EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, entryDateTo));
			}
			listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDate", "thruDate"));
			if (UtilValidate.isNotEmpty(entryDateFrom) || UtilValidate.isNotEmpty(entryDateTo)
					|| UtilValidate.isNotEmpty(resultEnumId)) {
				if (UtilValidate.isNotEmpty(areaGeoId)) {
					entity = new StringBuilder("PartyResourceCommunicationArea");
				} else {
					entity = new StringBuilder("PartyResourceCommunication");
				}
			} else if (UtilValidate.isNotEmpty(areaGeoId)) {
				entity = new StringBuilder("PartyResourceArea");
			}
			if (UtilValidate.isNotEmpty(birthDateFrom) || UtilValidate.isNotEmpty(birthDateTo)) {
				entity = entity.append("Member");
				listAllConditions.add(EntityCondition.makeCondition("totalPartyIdTo", multi));
			} else {
				listAllConditions.add(EntityCondition.makeCondition("partyIdTo", multi));
			}
			List<EntityCondition> condtemp = FastList.newInstance();

			condtemp.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "CONTACT_REJECTED"));
			condtemp.add(EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,
							new Timestamp(System.currentTimeMillis())),
					EntityCondition.makeCondition("thruDate", null)), EntityOperator.OR));
			list = delegator.find("PartyCampaignRelationship", EntityCondition.makeCondition(condtemp), null, null,
					null, null);
			GenericValue e = null;
			List<String> partyIds = FastList.newInstance();
			while ((e = list.next()) != null) {
				partyIds.add(e.getString("partyIdTo"));
			}
			if (UtilValidate.isNotEmpty(partyIds)) {
				listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_IN, partyIds));
			}
			String entityName = entity.toString();
			Set<String> fields = UtilMisc.toSet("repPartyId", "repLastName", "repMiddleName", "repFirstName",
					"partyIdFrom", "partyIdTo");
			fields.add("repBirthDate");
			fields.add("fromDate");
			fields.add("partyCode");
			fields.add("repGender");
			opts.setDistinct(true);
			EntityListIterator list2 = delegator.find(entityName, EntityCondition.makeCondition(listAllConditions),
					null, fields, listSortFields, opts);
			result.put("listIterator", list2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (list != null) {
				try {
					list.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private static List<String> getContactMechInRegion(Delegator delegator, String regionGeoId)
			throws GenericEntityException {
		List<GenericValue> geoAssocs = delegator.findList("GeoAssoc",
				EntityCondition.makeCondition(UtilMisc.toMap("geoId", regionGeoId, "geoAssocTypeId", "REGIONS")),
				UtilMisc.toSet("geoIdTo"), null, null, false);
		List<String> geoIdTos = EntityUtil.getFieldListFromEntityList(geoAssocs, "geoIdTo", true);
		List<GenericValue> postalAddresses = delegator.findList("PostalAddress",
				EntityCondition.makeCondition("stateProvinceGeoId", EntityJoinOperator.IN, geoIdTos),
				UtilMisc.toSet("contactMechId"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(postalAddresses, "contactMechId", true);
	}

	public static Map<String, Object> saveCampaignTerms(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			dispatcher.runSync("createCampaignAttribute", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"attrName", "areaGeoId", "attrValue", context.get("areaGeoId"), "userLogin", userLogin));
			dispatcher.runSync("createCampaignAttribute", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"attrName", "birthDateFrom", "attrValue", context.get("birthDateFrom"), "userLogin", userLogin));
			dispatcher.runSync("createCampaignAttribute", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"attrName", "birthDateTo", "attrValue", context.get("birthDateTo"), "userLogin", userLogin));
			dispatcher.runSync("createCampaignAttribute", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"attrName", "dataSourceId", "attrValue", context.get("dataSourceId"), "userLogin", userLogin));
			dispatcher.runSync("createCampaignAttribute", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"attrName", "entryDateFrom", "attrValue", context.get("entryDateFrom"), "userLogin", userLogin));
			dispatcher.runSync("createCampaignAttribute", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"attrName", "entryDateTo", "attrValue", context.get("entryDateTo"), "userLogin", userLogin));
			dispatcher.runSync("createCampaignAttribute", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"attrName", "entryType", "attrValue", context.get("entryType"), "userLogin", userLogin));
			dispatcher.runSync("createCampaignAttribute",
					UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "attrName", "entryTypeNumber",
							"attrValue", context.get("entryTypeNumber"), "userLogin", userLogin));
			dispatcher.runSync("createCampaignAttribute", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"attrName", "resultEnumId", "attrValue", context.get("resultEnumId"), "userLogin", userLogin));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> getContactCampaignReport(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		EntityListIterator tmp = null;
		try {
			Map<String, Object> inputMap = FastMap.newInstance();
			List<GenericValue> attr = delegator.findList("MarketingCampaignAttribute",
					EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId), null, null, null, false);
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<String> fkCond = UtilMisc.toList("birthDateFrom", "birthDateTo", "entryDateFrom", "entryDateTo",
					"dataSourceId", "areaGeoId");
			fkCond.add("resultEnumId");
			for (GenericValue e : attr) {
				String attrName = e.getString("attrName");
				if (fkCond.contains(attrName)) {
					inputMap.put(e.getString("attrName"), e.getString("attrValue"));
				}
			}
			inputMap.put("userLogin", userLogin);
			Map<String, Object> out = dispatcher.runSync("getContactResourceContext", inputMap);
			EntityListIterator totalList = (EntityListIterator) out.get("listIterator");
			int totalNotAssigned = totalList.getResultsTotalSize();
			totalList.close();
			EntityCondition cond = EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
							EntityCondition.makeCondition("statusId", "CONTACT_COMPLETED")));
			tmp = delegator.find("PartyCampaignRelationship", cond, null, null, null, null);
			int completed = tmp.getResultsTotalSize();
			tmp.close();
			cond = EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
							EntityCondition.makeCondition(
									UtilMisc.toList(EntityCondition.makeCondition("statusId", "CONTACT_ASSIGNED"),
											EntityCondition.makeCondition("statusId", "CONTACT_COMPLETED"),
											EntityCondition.makeCondition("statusId", "CONTACT_INPROGRESS")),
									EntityOperator.OR)));
			tmp = delegator.find("PartyCampaignRelationship", cond, null, null, null, null);
			int assigned = tmp.getResultsTotalSize();
			int total = totalNotAssigned + assigned;
			int remain = total - completed;
			Map<String, Object> obj = FastMap.newInstance();
			obj.put("total", total);
			obj.put("completed", completed);
			obj.put("uncompleted", remain);
			obj.put("assigned", assigned);
			obj.put("notassigned", totalNotAssigned);
			result.put("result", obj);
		} catch (Exception e) {
			Debug.log(e.getMessage());
		} finally {
			if (tmp != null) {
				tmp.close();
			}
		}
		return result;
	}

	/* party campaign relationship */
	public static Map<String, Object> createPartyCampaignRelationship(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			GenericValue inset = delegator.makeValidValue("PartyCampaignRelationship", context);
			inset.set("createdByUserLogin", userLoginId);
			inset.set("lastModifiedByUserLogin", userLoginId);
			inset.set("isCall", "N");
			delegator.create(inset);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreatePartyCampaignRelationshiop", locale));
		}
		return result;
	}

	public static Map<String, Object> updatePartyCampaignRelationship(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String partyIdFrom = (String) context.get("partyIdFrom");
			String partyIdTo = (String) context.get("partyIdTo");
			String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
			String roleTypeIdTo = (String) context.get("roleTypeIdTo");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			String userLoginId = userLogin.getString("userLoginId");
			if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(partyIdTo)
					&& UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo)
					&& UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(marketingCampaignId)) {
				Map<String, Object> cond = UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo,
						"roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo, "fromDate", fromDate,
						"marketingCampaignId", marketingCampaignId);
				GenericValue inset = delegator.findOne("PartyCampaignRelationship", cond, false);
				if (UtilValidate.isNotEmpty(inset)) {
					inset.set("lastModifiedByUserLogin", userLoginId);
					inset.setNonPKFields(context);
					inset.store();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreatePartyCampaignRelationshiop", locale));
		}
		return result;
	}

	/* campaign attribute */
	public static Map<String, Object> createCampaignAttribute(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue inset = delegator.makeValidValue("MarketingCampaignAttribute", context);
			delegator.createOrStore(inset);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreateMarketingCampaignAttribute", locale));
		}
		return result;
	}

	public static Map<String, Object> updateCampaignAttribute(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String attrName = (String) context.get("attrName");
			if (UtilValidate.isNotEmpty(marketingCampaignId) && UtilValidate.isNotEmpty(attrName)) {
				Map<String, Object> cond = UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "attrName",
						attrName);
				GenericValue inset = delegator.findOne("MarketingCampaignAttribute", cond, false);
				inset.setNonPKFields(context);
				inset.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreatePartyCampaignRelationshiop", locale));
		}
		return result;
	}

	/* campaign event */
	public static Map<String, Object> createPartyCampaignCommEvent(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue inset = delegator.makeValidValue("PartyCampaignCommEvent", context);
			delegator.create(inset);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreatePartyCampaignCommEvent", locale));
		}
		return result;
	}

	public static Map<String, Object> updatePartyCampaignCommEvent(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String partyIdFrom = (String) context.get("partyIdFrom");
			String partyIdTo = (String) context.get("partyIdTo");
			String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
			String roleTypeIdTo = (String) context.get("roleTypeIdTo");
			String fromDate = (String) context.get("fromDate");
			String communicationEventId = (String) context.get("communicationEventId");
			if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(partyIdTo)
					&& UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo)
					&& UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(marketingCampaignId)
					&& UtilValidate.isNotEmpty(communicationEventId)) {
				Map<String, Object> cond = UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo,
						"roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo, "fromDate", fromDate,
						"marketingCampaignId", marketingCampaignId, "communicationEventId", communicationEventId);
				GenericValue inset = delegator.findOne("PartyCampaignCommEvent", cond, false);
				inset.setNonPKFields(context);
				inset.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreatePartyCampaignRelationshiop", locale));
		}
		return result;
	}

	public static Map<String, Object> assignContactToParty(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		try {
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String ct = (String) context.get("contacts");
			String partyId = (String) context.get("partyId");
			Timestamp currentTimestamp = UtilDateTime.nowTimestamp();
			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				Long fd = (Long) context.get("fromDate");
				Long td = (Long) context.get("thruDate");
				Timestamp thruDate = null;
				if (UtilValidate.isNotEmpty(td)) {
					thruDate = new Timestamp(td);
				}
				long sys = currentTimestamp.getTime();
				if (sys < fd) {
					currentTimestamp = new Timestamp(fd);
				}
				if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(ct)) {
					JSONArray contacts = JSONArray.fromObject(ct);
					for (int i = 0; i < contacts.size(); i++) {
						JSONObject party = contacts.getJSONObject(i);
						String partyIdTo = party.getString("partyId");
						long fdTmp = party.getLong("fromDate");
						if (UtilValidate.isNotEmpty(partyIdTo)) {
							Map<String, Object> in = FastMap.newInstance();
							in.put("marketingCampaignId", marketingCampaignId);
							in.put("partyIdTo", partyIdTo);
							in.put("roleTypeIdFrom", "CALLCENTER_EMPL");
							in.put("roleTypeIdTo", "CONTACT");
							in.put("fromDate", new Timestamp(fdTmp));
							EntityCondition cond = EntityCondition.makeCondition(in);
							List<EntityCondition> conditions = FastList.newInstance();
							conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
							conditions.add(cond);
							List<GenericValue> old = delegator.findList("PartyCampaignRelationship",
									EntityCondition.makeCondition(conditions), null, null, null, false);
							if (UtilValidate.isNotEmpty(old)) {
								for (GenericValue e : old) {
									String cur = e.getString("partyIdFrom");
									if (!cur.equals(partyId)) {
										e.set("statusId", "CONTACT_REJECTED");
										e.set("thruDate", currentTimestamp);
										e.store();
									}
								}
							}
							in.put("fromDate", currentTimestamp);
							in.put("partyIdFrom", partyId);
							in.put("thruDate", thruDate);
							in.put("statusId", "CONTACT_ASSIGNED");
							in.put("userLogin", userLogin);
							dispatcher.runSync("createPartyCampaignRelationship", in);
						}
					}
				}
			} else {
				Map<String, Object> in = FastMap.newInstance();
				if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(ct)) {
					Long td = (Long) context.get("thruDate");
					Timestamp thruDate = null;
					if (UtilValidate.isNotEmpty(td)) {
						thruDate = new Timestamp(td);
					}
					JSONArray contacts = JSONArray.fromObject(ct);
					for (int i = 0; i < contacts.size(); i++) {
						JSONObject party = contacts.getJSONObject(i);
						String partyIdTo = party.getString("partyId");
						if (UtilValidate.isNotEmpty(partyIdTo)) {
							if ("MB".equals(organizationId)) {
								in.put("marketingCampaignId", "INDEFINITE_CAMPAIGN");
							} else if ("MN".equals(organizationId)) {
								in.put("marketingCampaignId", "INDEFINITE_CP_MN");
							}
							in.put("partyIdTo", partyIdTo);
							in.put("roleTypeIdFrom", "CALLCENTER_EMPL");
							in.put("roleTypeIdTo", "CONTACT");
							in.put("fromDate", currentTimestamp);
							in.put("partyIdFrom", partyId);
							in.put("thruDate", thruDate);
							in.put("statusId", "CONTACT_ASSIGNED");
							in.put("userLogin", userLogin);
							dispatcher.runSync("createPartyCampaignRelationship", in);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreatePartyCampaignRelationshiop", locale));
		}
		return result;
	}

	public static Map<String, Object> unassignContactToParty(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String partyId = (String) context.get("partyId");
			Long fd = (Long) context.get("fromDate");
			String ct = (String) context.get("contacts");
			Timestamp currentTimestamp = UtilDateTime.nowTimestamp();
			long sys = currentTimestamp.getTime();
			if (sys < fd) {
				currentTimestamp = new Timestamp(fd);
			}
			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(ct)) {
				JSONArray contacts = JSONArray.fromObject(ct);
				for (int i = 0; i < contacts.size(); i++) {
					JSONObject party = contacts.getJSONObject(i);
					String partyIdTo = party.getString("partyId");
					long fromDate = party.getLong("fromDate");
					if (UtilValidate.isNotEmpty(partyIdTo)) {
						Map<String, Object> in = FastMap.newInstance();
						in.put("marketingCampaignId", marketingCampaignId);
						in.put("partyIdTo", partyIdTo);
						in.put("roleTypeIdFrom", "CALLCENTER_EMPL");
						in.put("roleTypeIdTo", "CONTACT");
						in.put("fromDate", new Timestamp(fromDate));
						in.put("partyIdFrom", partyId);
						GenericValue curCon = delegator.findOne("PartyCampaignRelationship", in, false);
						if (UtilValidate.isNotEmpty(curCon)) {
							curCon.put("statusId", "CONTACT_REJECTED");
							curCon.set("thruDate", currentTimestamp);
							curCon.store();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreatePartyCampaignRelationshiop", locale));
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> getEmployeeContactReport(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator tmp = null;
		try {
			Map<String, String[]> param = (Map<String, String[]>) context.get("parameters");
			String marketingCampaignId;
			if (UtilValidate.isNotEmpty(param.get("marketingCampaignId"))) {
				marketingCampaignId = param.get("marketingCampaignId")[0];
			} else {
				marketingCampaignId = "";
			}
			Map<String, Object> employees = getEmployee(ctx, context);
			List<Map<String, Object>> listEmployee = (List<Map<String, Object>>) employees.get("results");
			int total = 0;
			int completed = 0;
			int remain = 0;
			List<Map<String, Object>> resList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(listEmployee)) {
				for (Map<String, Object> e : listEmployee) {
					Map<String, Object> o = FastMap.newInstance();
					String partyId = (String) e.get("partyId");
					EntityCondition cond = EntityCondition
							.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", partyId),
									EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
									EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())));
					tmp = delegator.find("PartyCampaignRelationship", cond, null, null, null, null);
					total = tmp.getResultsTotalSize();
					tmp.close();
					EntityCondition cond2 = EntityCondition
							.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", partyId),
									EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
									EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()),
									EntityCondition.makeCondition("statusId", "CONTACT_COMPLETED")));
					tmp = delegator.find("PartyCampaignRelationship", cond2, null, null, null, null);
					completed = tmp.getResultsTotalSize();
					tmp.close();
					remain = total - completed;
					o.put("partyId", partyId);
					o.put("partyCode", e.get("partyCode"));
					o.put("partyFullName", e.get("partyFullName"));
					o.put("total", total);
					o.put("completed", completed);
					o.put("uncompleted", remain);
					resList.add(o);
				}
			}
			result.put("listIterator", resList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tmp != null) {
				try {
					tmp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> autoAssignContact(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		EntityListIterator list = null;
		try {
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			Long fd = (Long) context.get("fromDate");
			Timestamp fromDate = null;
			Timestamp cur = UtilDateTime.nowTimestamp();
			long curtmp = cur.getTime();
			if (UtilValidate.isNotEmpty(fd)) {
				fromDate = new Timestamp(fd);
				long fdtmp = fromDate.getTime();
				if (curtmp < fdtmp) {
					cur = new Timestamp(fdtmp);
				}
			}
			Long td = (Long) context.get("thruDate");
			Timestamp thruDate = null;
			if (UtilValidate.isNotEmpty(td)) {
				thruDate = new Timestamp(td);
				long thdtmp = thruDate.getTime();
				if (curtmp >= thdtmp) {
					return ServiceUtil.returnError(UtilProperties.getMessage("CallCenterUiLabels",
							"CannotCreatePartyCampaignRelationshiop", locale));
				}
			}
			if (UtilValidate.isNotEmpty(context.get("quantity"))) {
				JSONObject quantityEmployee = JSONObject.fromObject(context.get("quantity"));
				Iterator<String> parties = quantityEmployee.keySet().iterator();
				ModelService model = ctx.getModelService("getContactResourceContext");
				Map<String, Object> inputMap = model.makeValid(context, ModelService.IN_PARAM);
				EntityFindOptions opts = new EntityFindOptions();
				opts.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
				inputMap.put("opts", opts);
				Map<String, Object> out = dispatcher.runSync("getContactResourceContext", inputMap);
				list = (EntityListIterator) out.get("listIterator");
				GenericValue to;
				String partyIdFrom = parties.next();
				int quantity = quantityEmployee.getInt(partyIdFrom);
				while ((to = list.next()) != null) {
					if (quantity <= 0) {
						if (parties.hasNext()) {
							partyIdFrom = parties.next();
							quantity = quantityEmployee.getInt(partyIdFrom);
						} else {
							break;
						}
					}
					String partyIdTo = to.getString("partyIdFrom");
					if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(partyIdTo)) {
						Map<String, Object> in = FastMap.newInstance();
						in.put("userLogin", userLogin);
						in.put("marketingCampaignId", marketingCampaignId);
						in.put("partyIdFrom", partyIdFrom);
						in.put("partyIdTo", partyIdTo);
						in.put("roleTypeIdFrom", "CALLCENTER_EMPL");
						in.put("roleTypeIdTo", "CONTACT");
						in.put("fromDate", cur);
						in.put("thruDate", thruDate);
						in.put("statusId", "CONTACT_ASSIGNED");
						dispatcher.runSync("createPartyCampaignRelationship", in);
						quantity--;
					}
				}
				list.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (list != null) {
				list.close();
			}
			return ServiceUtil.returnError(
					UtilProperties.getMessage("CallCenterUiLabels", "CannotCreatePartyCampaignRelationshiop", locale));
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return result;
	}

	public static Map<String, Object> getDataResources(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String query = (String) context.get("searchKey");
		EntityListIterator list = null;
		try {
			EntityFindOptions ops = new EntityFindOptions();
			ops.setDistinct(true);
			ops.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
			List<EntityCondition> cond = FastList.newInstance();
			List<GenericValue> lisres = null;
			cond.add(EntityCondition.makeCondition("dataSourceId", EntityOperator.NOT_EQUAL, null));
			if (UtilValidate.isNotEmpty(query)) {
				EntityCondition e = EntityCondition.makeCondition("dataSourceId", EntityOperator.LIKE,
						"%" + query + "%");
				cond.add(e);
				list = delegator.find("Party", EntityCondition.makeCondition(cond), null,
						UtilMisc.toSet("dataSourceId"), UtilMisc.toList("+dataSourceId"), ops);
				int size = list.getResultsTotalSize();
				if (size >= 10) {
					lisres = list.getPartialList(0, 10);
				} else {
					lisres = list.getPartialList(0, size);
				}
			} else {
				list = delegator.find("Party", EntityCondition.makeCondition(cond), null,
						UtilMisc.toSet("dataSourceId"), UtilMisc.toList("+dataSourceId"), ops);
				lisres = list.getCompleteList();
			}
			result.put("result", lisres);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return result;
	}

	public static Map<String, Object> checkDataHasAssigned(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		EntityListIterator list = null;
		try {
			list = delegator
					.find("PartyCampaignRelationship",
							EntityCondition.makeCondition(UtilMisc
									.toList(EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
											EntityCondition.makeCondition("statusId",
													EntityOperator.IN, UtilMisc.toList("CONTACT_ASSIGNED",
															"CONTACT_COMPLETED", "CONTACT_INPROGRESS")))),
							null, null, null, null);
			int length = list.getResultsTotalSize();
			if (length != 0) {
				result.put("hasData", true);
			} else {
				result.put("hasData", false);
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			result.put("hasData", false);
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return result;
	}

	public static Map<String, Object> moveDataCustomer(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		EntityListIterator listPartyCampaignRelationship = null;
		try {
			String partyIdFrom = (String) context.get("partyIdFrom");
			String partyIdTo = (String) context.get("partyIdTo");
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			EntityFindOptions opts = new EntityFindOptions();
			opts.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
			GenericValue campaign = delegator.findOne("MarketingCampaign",
					UtilMisc.toMap("marketingCampaignId", marketingCampaignId), false);
			Timestamp campaignThruDate = campaign.getTimestamp("thruDate");
			List<EntityExpr> thruDateListCond = UtilMisc
					.toList(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
			if (UtilValidate.isNotEmpty(campaignThruDate)) {
				thruDateListCond.add(
						EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, campaignThruDate));
			}
			EntityCondition thruDateCond = EntityCondition.makeCondition(thruDateListCond, EntityOperator.OR);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(thruDateCond);
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
					"partyIdFrom", partyIdFrom, "roleTypeIdFrom", "CALLCENTER_EMPL", "roleTypeIdTo", "CONTACT")));
			listPartyCampaignRelationship = delegator.find("PartyCampaignRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, opts);
			GenericValue partyCampaignRelationship = null;
			int length = listPartyCampaignRelationship.getResultsTotalSize();
			while ((partyCampaignRelationship = listPartyCampaignRelationship.next()) != null) {
				partyCampaignRelationship.setString("statusId", "CONTACT_REJECTED");
				partyCampaignRelationship.set("thruDate", new Timestamp(System.currentTimeMillis()));
				partyCampaignRelationship.store();
				dispatcher.runSync("createPartyCampaignRelationship",
						UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyIdFrom", partyIdTo,
								"partyIdTo", partyCampaignRelationship.getString("partyIdTo"), "roleTypeIdFrom",
								"CALLCENTER_EMPL", "roleTypeIdTo", "CONTACT", "fromDate",
								new Timestamp(System.currentTimeMillis()), "statusId", "CONTACT_ASSIGNED", "userLogin",
								context.get("userLogin")));
			}
			result.put("customers", String.valueOf(length));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		} finally {
			if (listPartyCampaignRelationship != null) {
				listPartyCampaignRelationship.close();
			}
		}
		return result;
	}
}
