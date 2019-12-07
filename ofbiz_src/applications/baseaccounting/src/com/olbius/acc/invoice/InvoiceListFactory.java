package com.olbius.acc.invoice;

public class InvoiceListFactory implements InvoiceType {
	public static InvoiceList getInvoiceList(String invoiceType) {
		switch (invoiceType) {
		case AR:
			return new ArInvoiceList();
		case AP:
			return new ApInvoiceList();
		default:
			return new ArInvoiceList();
		}
	}
}
