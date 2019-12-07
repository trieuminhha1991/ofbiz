package com.olbius.acc.report.financialstm;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class PeriodUtils {
	
	public static final String module = PeriodUtils.class.getName();
	
	public static String getPeriodName(String customTimePeriodId, Delegator delegator) {
		GenericValue period = null;
		try {
			period = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e) {
			if(Debug.errorOn()) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Debug.log(e.getStackTrace().toString(), module);
		}
		if(period != null) {
			return period.getString("periodName");
		}else {
			return null;
		}
	}
	
	public static String getPreviousPeriodName(String customTimePeriodId, Delegator delegator) {
		String previousPeriodId = BalanceWorker.getPreviousPeriod(customTimePeriodId, delegator);
		if(UtilValidate.isEmpty(previousPeriodId)) {
			previousPeriodId = customTimePeriodId;
		}
		return getPeriodName(previousPeriodId, delegator);
	}
}