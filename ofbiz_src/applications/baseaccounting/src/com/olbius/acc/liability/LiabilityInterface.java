package com.olbius.acc.liability;


import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;

import com.olbius.acc.invoice.entity.Invoice;
import com.olbius.acc.payment.entity.Payment;

public interface LiabilityInterface {
	public List<Liability> getListLiabilities(Map<String,Object> context,DispatchContext dpct, String partyId) throws GenericServiceException;
	public List<Invoice> getListInvoices(Map<String,Object> context, DispatchContext ctx) throws GeneralException;
	public List<Payment> getListPayments(Map<String,Object> context, DispatchContext ctx) throws GenericServiceException, GenericEntityException;
}
