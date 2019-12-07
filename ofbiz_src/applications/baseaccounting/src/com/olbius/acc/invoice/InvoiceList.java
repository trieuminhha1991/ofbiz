package com.olbius.acc.invoice;

import org.ofbiz.service.DispatchContext;

import java.util.Map;

public interface InvoiceList {
	public Map<String, Object> getListInvoice(DispatchContext dispatcher, Map<String, Object> context) throws Exception;
}
