package com.olbius.util;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

public class EntityUtil extends org.ofbiz.entity.util.EntityUtil{
	
	public static String MODULE = EntityUtil.class.getName();
	
	public static String getEmplAgreementSeqId(Delegator delegator) {
		String emplAgreementSeqId = "";
		Set<String> setAgreementType = FastSet.newInstance();
		try {
			List<GenericValue> listAgreementType = delegator.findByAnd("AgreementType", UtilMisc.toMap("parentTypeId", "EMPLOYMENT_AGREEMENT"), null, false);
			for(GenericValue item : listAgreementType) {
				setAgreementType.add(item.getString("agreementTypeId"));
			}
			List<GenericValue> agreementList = delegator.findList("Agreement", EntityCondition.makeCondition("agreementTypeId", EntityJoinOperator.IN, setAgreementType), null, null, null, false);
			emplAgreementSeqId += (agreementList.size() + 1);
			emplAgreementSeqId += "/HĐLĐ/DELYS-";
			emplAgreementSeqId += Calendar.getInstance().get(Calendar.YEAR);
		} catch (GenericEntityException e1) {
			Debug.logError(e1.getMessage(), MODULE);
		}
		return emplAgreementSeqId;
	}
}
