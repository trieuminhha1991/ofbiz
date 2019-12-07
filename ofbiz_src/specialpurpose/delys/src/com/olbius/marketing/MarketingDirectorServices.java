package com.olbius.marketing;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class MarketingDirectorServices {
	public static final String module = MarketingDirectorServices.class
			.getName();
	public static final String resourceMarketing = "MarketingUiLabels";
	public static final String resourceCustom = "DelysMarketingUiLabels";

	public static Map<String, Object> getListRequestCeo(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		/* if don't has accountant check price */
		List<EntityCondition> listCond1 = FastList.newInstance();
		listCond1.add(EntityCondition.makeCondition("statusId",
				"MKTG_CAMP_MDRAP"));
		List<EntityCondition> listCondType1 = FastList.newInstance();
		listCondType1.add(EntityCondition.makeCondition("marketingTypeId",
				"RESEARCH_FREQ"));
		listCondType1.add(EntityCondition.makeCondition("marketingTypeId",
				"SAMPLING_ACT"));
		listCondType1.add(EntityCondition.makeCondition("marketingTypeId",
				"DISCOUNT"));
		listCond1.add(EntityCondition.makeCondition(listCondType1,
				EntityOperator.OR));
		/* if has accountant check price */
		List<EntityCondition> listCond2 = FastList.newInstance();
		listCond2.add(EntityCondition.makeCondition("statusId",
				"MKTG_CAMP_CKPRI"));
		List<EntityCondition> listCondType2 = FastList.newInstance();
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"RESEARCH_UNED"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"GIFT"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"DIGITAL"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"AFFILIATE"));
		listCond2.add(EntityCondition.makeCondition(listCondType2,
				EntityOperator.OR));
		/* add all condition */
		List<EntityCondition> chkPrOrNot = FastList.newInstance();
		chkPrOrNot.add(EntityCondition.makeCondition(listCond1,
				EntityOperator.AND));
		chkPrOrNot.add(EntityCondition.makeCondition(listCond2,
				EntityOperator.AND));
		listAllConditions.add(EntityCondition.makeCondition(chkPrOrNot,
				EntityOperator.OR));
		listAllConditions.add(EntityCondition.makeCondition("isActive", "Y"));
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("marketingCampaignId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("MarketingCampaignDetail", tmpCond,
					null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> getListRequestAccCheckPrice(
			DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		/* if has accountant check price */
		List<EntityCondition> listCond2 = FastList.newInstance();
		listCond2.add(EntityCondition.makeCondition("statusId",
				"MKTG_CAMP_MDRAP"));
		List<EntityCondition> listCondType2 = FastList.newInstance();
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"RESEARCH_UNED"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"GIFT"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"DIGITAL"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"AFFILIATE"));
		listCond2.add(EntityCondition.makeCondition(listCondType2,
				EntityOperator.OR));
		/* add all condition */
		listAllConditions.add(EntityCondition.makeCondition(listCond2,
				EntityOperator.AND));
		listAllConditions.add(EntityCondition.makeCondition("isActive", "Y"));
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("marketingCampaignId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("MarketingCampaignDetail", tmpCond,
					null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> getListRequestAccApprove(
			DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		/* if has accountant check price */
		List<EntityCondition> listCond2 = FastList.newInstance();
		listCond2.add(EntityCondition.makeCondition("statusId",
				"MKTG_CAMP_MDRAP"));
		List<EntityCondition> listCondType2 = FastList.newInstance();
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"RESEARCH_UNED"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"GIFT"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"DIGITAL"));
		listCondType2.add(EntityCondition.makeCondition("marketingTypeId",
				"AFFILIATE"));
		listCond2.add(EntityCondition.makeCondition(listCondType2,
				EntityOperator.OR));
		/* add all condition */
		listAllConditions.add(EntityCondition.makeCondition(listCond2,
				EntityOperator.AND));
		listAllConditions.add(EntityCondition.makeCondition("isActive", "Y"));
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("marketingCampaignId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("MarketingCampaignDetail", tmpCond,
					null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> getListRequest(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String[] typetmp = parameters.get("type");
		String type = typetmp[0];
		if (type.isEmpty()) {
			successResult.put("listIterator", listIterator);
			return successResult;
		}
		if (type.equals("res")) {
			List<EntityCondition> tmp = FastList.newInstance();
			tmp.add(EntityCondition.makeCondition("marketingTypeId",
					"RESEARCH_FREQ"));
			tmp.add(EntityCondition.makeCondition("marketingTypeId",
					"RESEARCH_UNED"));
			listAllConditions.add(EntityCondition.makeCondition(tmp,
					EntityOperator.OR));
		} else if (type.equals("spl")) {
			listAllConditions.add(EntityCondition.makeCondition(
					"marketingTypeId", "SAMPLING_ACT"));
		} else if (type.equals("gift")) {
			listAllConditions.add(EntityCondition.makeCondition(
					"marketingTypeId", "TRADE_PROMOS"));
		} else if (type.equals("disc")) {
			listAllConditions.add(EntityCondition.makeCondition(
					"marketingTypeId", "DISCOUNT"));
		} else if (type.equals("aff")) {
			listAllConditions.add(EntityCondition.makeCondition(
					"marketingTypeId", "AFFILIATE"));
		} else if (type.equals("dig")) {
			listAllConditions.add(EntityCondition.makeCondition(
					"marketingTypeId", "DIGITAL"));
		} else {
			successResult.put("listIterator", listIterator);
			return successResult;
		}
		listAllConditions.add(EntityCondition.makeCondition("statusId",
				"MKTG_CAMP_PLANNED"));
		listAllConditions.add(EntityCondition.makeCondition("isActive", "Y"));
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("marketingCampaignId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("MarketingCampaignDetail", tmpCond,
					null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> acceptRequest(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successReMap = FastMap.newInstance();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");

		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String status = "";
			Map<String, Object> input = FastMap.newInstance();
			input.put("userLogin", userLogin);
			input.put("partyId", userLogin.getString("partyId"));
			Map<String, Object> role = dispatcher.runSync(
					"getAllRolePartyInOrg", input);
			List<Map<String, Object>> allListRole = (List<Map<String, Object>>) role
					.get("allRoleTypeEmplInRelationship");
			Map<String, Object> cond = FastMap.newInstance();
			cond.put("marketingCampaignId", marketingCampaignId);
			GenericValue mk = delegator.findOne("MarketingCampaignDetail",
					false, cond);
			String current_status = mk.getString("statusId");
			GenericValue marketing = delegator.makeValue("MarketingCampaign");
			marketing.set("marketingCampaignId", marketingCampaignId);
			Map<String, Object> ceo = FastMap.newInstance();
			ceo.put("partyGroupId", "company");
			ceo.put("partyGroupRoleTypeId", "INTERNAL_ORGANIZATIO");
			ceo.put("emplRoleTypeId", "CEO");
			Map<String, Object> chiefAcc = FastMap.newInstance();
			chiefAcc.put("partyGroupId", "DAC");
			chiefAcc.put("partyGroupRoleTypeId", "ACCT_DEPT");
			chiefAcc.put("emplRoleTypeId", "MANAGER");
			Map<String, Object> mkD = FastMap.newInstance();
			mkD.put("partyGroupId", "DMA");
			mkD.put("partyGroupRoleTypeId", "MARKETING_DEPT");
			mkD.put("emplRoleTypeId", "MANAGER");
			if (current_status.equals("MKTG_CAMP_PLANNED")
					&& containsMap(allListRole, mkD)) {
				status = "MKTG_CAMP_MDRAP";
				marketing.set("statusId", status);
				delegator.store(marketing);
			} else if (current_status.equals("MKTG_CAMP_COMPLETED")
					&& containsMap(allListRole, chiefAcc)) {
				status = "MKTG_CAMP_APPAY";
				marketing.set("statusId", status);
				delegator.store(marketing);
			} else if (current_status.equals("MKTG_CAMP_MDRAP")) {
				String marketingTypeId = mk.getString("marketingTypeId");
				if (marketingTypeId.equals("RESEARCH_UNED")
						|| marketingTypeId.equals("GIFT")
						|| marketingTypeId.equals("DIGITAL")
						|| marketingTypeId.equals("AFFILIATE")
						&& containsMap(allListRole, chiefAcc)) {
					status = "MKTG_CAMP_CKPRI";
					marketing.set("statusId", status);
					delegator.store(marketing);
				} else if(containsMap(allListRole, ceo)){
					status = "MKTG_CAMP_CEOAP";
					marketing.set("statusId", status);
					delegator.store(marketing);
					List<GenericValue> products = delegator.findList(
							"MarketingSamplingProduct", EntityCondition
									.makeCondition("marketingCampaignId",
											marketingCampaignId), null, null,
							null, false);
					if (!products.isEmpty()) {
						Map<String, Object> logis = FastMap.newInstance();
						logis.putAll(input);
						logis.put("products", products);
						if (marketingTypeId.equals("SAMPLING_ACT")) {
							logis.put("requirementTypeId", "MK_SAMPLING_REQ");
						} else {
							logis.put("requirementTypeId", "MARKETING_REQ");
						}
						dispatcher.runSync("createLogisticRequirement", logis);
					}

				}
			} else if (current_status.equals("MKTG_CAMP_CKPRI") && containsMap(allListRole, ceo)) {
				status = "MKTG_CAMP_CEOAP";
				marketing.set("statusId", status);
				delegator.store(marketing);
			}
			successReMap.put("message", status);
			successReMap.put("status", "success");
		} catch (Exception e) {
			e.printStackTrace();
			successReMap.put("status", "error");
		}
		return successReMap;
	}

	public static Map<String, Object> refuseRequest(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successReMap = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		Locale locale = (Locale) context.get("locale");
		String message = UtilProperties.getMessage(resourceCustom,
				"updateSuccess", locale);
		String status = "";
		try {
			Map<String, Object> input = FastMap.newInstance();
			input.put("userLogin", userLogin);
			input.put("partyId", userLogin.getString("partyId"));
			Map<String, Object> role = dispatcher.runSync(
					"getAllRolePartyInOrg", input);
			List<Map<String, Object>> allListRole = (List<Map<String, Object>>) role
					.get("allRoleTypeEmplInRelationship");
			Map<String, Object> cond = FastMap.newInstance();
			cond.put("marketingCampaignId", marketingCampaignId);
			GenericValue mk = delegator.findOne("MarketingCampaignDetail",
					false, cond);
			String current_status = mk.getString("statusId");
			GenericValue marketing = delegator.makeValue("MarketingCampaign");
			marketing.set("marketingCampaignId", marketingCampaignId);
			Map<String, Object> ceo = FastMap.newInstance();
			ceo.put("partyGroupId", "company");
			ceo.put("partyGroupRoleTypeId", "INTERNAL_ORGANIZATIO");
			ceo.put("emplRoleTypeId", "CEO");
			Map<String, Object> chiefAcc = FastMap.newInstance();
			chiefAcc.put("partyGroupId", "DAC");
			chiefAcc.put("partyGroupRoleTypeId", "ACCT_DEPT");
			chiefAcc.put("emplRoleTypeId", "MANAGER");
			Map<String, Object> mkD = FastMap.newInstance();
			mkD.put("partyGroupId", "DMA");
			mkD.put("partyGroupRoleTypeId", "MARKETING_DEPT");
			mkD.put("emplRoleTypeId", "MANAGER");
			if (current_status.equals("MKTG_CAMP_PLANNED")
					&& containsMap(allListRole, mkD)) {
				status = "MKTG_CAMP_MDRRJ";
				marketing.set("statusId", status);
				delegator.store(marketing);
			} else if (current_status.equals("MKTG_CAMP_COMPLETED")
					&& containsMap(allListRole, chiefAcc)) {
				status = "MKTG_CAMP_RFPAY";
				marketing.set("statusId", status);
				delegator.store(marketing);
			} else if (current_status.equals("MKTG_CAMP_MDRAP")) {
				String marketingTypeId = mk.getString("marketingTypeId");
				if (marketingTypeId.equals("RESEARCH_UNED")
						|| marketingTypeId.equals("GIFT")
						|| marketingTypeId.equals("DIGITAL")
						|| marketingTypeId.equals("AFFILIATE") && containsMap(allListRole, chiefAcc)) {
					status = "MKTG_CAMP_RJPRI";
					marketing.set("statusId", status);
					delegator.store(marketing);
				} else if(containsMap(allListRole, ceo)){
					status = "MKTG_CAMP_CEORJ";
					marketing.set("statusId", status);
					delegator.store(marketing);
				}
			} else if (current_status.equals("MKTG_CAMP_CKPRI") && containsMap(allListRole, ceo)) {
				status = "MKTG_CAMP_CEORJ";
				marketing.set("statusId", status);
				delegator.store(marketing);
			}

			if (!status.isEmpty()) {
				/* create new note */
				Map<String, Object> noteMap = dispatcher.runSync(
						"createNoteMarketing", UtilMisc.toMap("status", status,
								"note", (String) context.get("note"),
								"userLogin", userLogin));
				String noteId = (String) noteMap.get("noteId");
				/* insert marketing note */
				dispatcher.runSync("createMarketingCampaignNote", UtilMisc
						.toMap("marketingCampaignId", marketingCampaignId,
								"noteId", noteId));
			}
			successReMap.put("message", status);
			successReMap.put("status", "success");
		} catch (Exception e) {
			e.printStackTrace();
			successReMap.put("status", "error");
		}
		return successReMap;
	}

	public static boolean containsMap(List<Map<String, Object>> input,
			Map<String, Object> key) {
		for (Map<String, Object> tmp : input) {
			if (mapsAreEqual(key, tmp)) {
				return true;
			}
		}
		return false;
	}

	public static boolean mapsAreEqual(Map<String, Object> mapA,
			Map<String, Object> mapB) {

		try {
			for (String k : mapB.keySet()) {
				System.out.println("dm equals"
						+ mapA.get(k).equals(mapB.get(k)));
				if (!mapA.get(k).equals(mapB.get(k))) {
					return false;
				}
			}
			for (String y : mapA.keySet()) {
				if (!mapB.containsKey(y)) {
					return false;
				}
			}
		} catch (NullPointerException np) {
			return false;
		}
		return true;
	}

	/* create note */
	public static Map<String, Object> createNote(DispatchContext ctx,
			Map<String, Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String status = (String) context.get("status");
		String note = (String) context.get("note");
		try {
			GenericValue noteTb = delegator.makeValue("NoteData");
			String noteId = delegator.getNextSeqId("NoteData");
			res.put("noteId", noteId);
			Date date = new Date();
			noteTb.set("noteId", noteId);
			noteTb.set("noteName", status);
			noteTb.set("noteInfo", note);
			noteTb.set("noteDateTime", new Timestamp(date.getTime()));
			noteTb.set("noteParty", partyId);
			delegator.create(noteTb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/* create marketing note */
	public static Map<String, Object> createMarketingCampaignNote(
			DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		String noteId = (String) context.get("noteId");
		try {
			GenericValue marketingNote = delegator
					.makeValue("MarketingCampaignNote");
			marketingNote.set("marketingCampaignId", marketingCampaignId);
			marketingNote.set("noteId", noteId);
			delegator.create(marketingNote);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/* get marketing campaign by id */
	public static Map<String, Object> getRequestDetail(DispatchContext ctx,
			Map<String, Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String id = (String) context.get("id");
		try {
			EntityCondition marketing = EntityCondition.makeCondition(
					"marketingCampaignId", id);
			List<GenericValue> products = delegator.findList(
					"MarketingCampaignProductDetail", marketing, null, null,
					null, false);
			List<GenericValue> costs = delegator.findList(
					"MarketingCampaignCostDetail", marketing, null, null, null,
					false);
			Map<String, Object> mk = FastMap.newInstance();
			mk.put("products", products);
			mk.put("costs", costs);
			res.put("data", mk);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
