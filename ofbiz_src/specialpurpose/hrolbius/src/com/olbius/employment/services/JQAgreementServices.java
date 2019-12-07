package com.olbius.employment.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.recruitment.services.JQProbationServices;

public class JQAgreementServices {
public static final String MODULE = JQProbationServices.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplAgreement(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		Set<String> setAgreementType = FastSet.newInstance();
		try {
			List<GenericValue> listAgreementType = delegator.findByAnd("AgreementType", UtilMisc.toMap("parentTypeId", "EMPLOYMENT_AGREEMENT"), null, false);
			for(GenericValue item : listAgreementType) {
				setAgreementType.add(item.getString("agreementTypeId"));
			}
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling jqGetListProbation service: " + e1.toString();
			Debug.logError(e1, errMsg, MODULE);
		}
		EntityCondition agrTypeCon = EntityCondition.makeCondition("agreementTypeId", EntityJoinOperator.IN, setAgreementType);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(agrTypeCon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("Agreement", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListProbation service: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAppendix(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		/*mapCondition.put("agreementId", parameters.get("agreementId")[0]);*/
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		EntityCondition agrItemTypeCon = EntityCondition.makeCondition("agreementItemTypeId", "AGREEMENT_APPENDIX");
		listAllConditions.add(tmpConditon);
		listAllConditions.add(agrItemTypeCon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("AgreementItem", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListAppendix service: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
}
