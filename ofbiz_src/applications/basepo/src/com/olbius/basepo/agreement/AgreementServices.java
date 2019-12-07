package com.olbius.basepo.agreement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;

public class AgreementServices {
	public static final String module = AgreementServices.class.getName();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAgreementPurchase(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition(
					UtilMisc.toMap("partyIdFrom", organizationId, "agreementTypeId", "PURCHASE_AGREEMENT")));
			EntityListIterator listIterator = delegator.find("AgreementAndPartyNameView",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPAgreement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSupplierParty(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.findList("ListPartySupplierByRole",
					EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPAgreement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListAgreementTerms(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("agreementId", parameters.get("agreementId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			listIterator = delegator.find("AgreementTerm", EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListAgreementTerms service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListAgreementItems(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("agreementId", parameters.get("agreementId")[0]);
		if (parameters.get("agreementItemSeqId") != null)
			mapCondition.put("agreementItemSeqId", parameters.get("agreementItemSeqId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			listIterator = delegator.find("AgreementItem", EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListAgreementItems service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}
}
