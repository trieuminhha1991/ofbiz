package com.olbius.acc.payment;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

public interface PaymentList {
	Map<String, Object> getListPayments(DispatchContext dispatcher, Map<String, Object> context) throws Exception;
	
}
