package com.olbius.dashboard;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

public class AgreementChartModel extends ChartModel{
	
	@Override
	public void buildModel(Delegator delegator) {
		Integer probAgreement = getProbationaryAgreement(delegator);
		Integer formalAgreement = getFormalAgreement(delegator);
		model.put("probAgreement", probAgreement.doubleValue());
		model.put("formalAgreement", formalAgreement.doubleValue());
		
	}
	
	private int getProbationaryAgreement(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("agreementDimId", "HDTV"), EntityJoinOperator.AND);
		List<GenericValue> probAgreements = FastList.newInstance();
		try {
			probAgreements = delegator.findList("EmplPositionTypeFact",condition , null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		int count = 0;
		for(GenericValue item: probAgreements){
			count += item.getBigDecimal("employeeNumber").intValue();
		}
		return count;
	}
	
	private int getFormalAgreement(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("agreementDimId", "HDXDTH"), EntityJoinOperator.AND);
		List<GenericValue> formalAgreements = FastList.newInstance();
		try {
			formalAgreements = delegator.findList("EmplPositionTypeFact",condition , null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		int count = 0;
		for(GenericValue item: formalAgreements){
			count += item.getBigDecimal("employeeNumber").intValue();
		}
		return count;
	}

}
