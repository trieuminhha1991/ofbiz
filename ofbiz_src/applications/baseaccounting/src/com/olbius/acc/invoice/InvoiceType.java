package com.olbius.acc.invoice;

public interface InvoiceType {
	public static final String AP = "AP";
	public static final String AR = "AR";

	//purchase invoice
    public static final String PURCHASE_INVOICE = "PURCHASE_INVOICE"; // ok
    public static final String IMPORT_INVOICE = "IMPORT_INVOICE"; // ok
    public static final String CUST_RTN_INVOICE = "CUST_RTN_INVOICE"; // ok
    public static final String COMMISSION_INVOICE = "COMMISSION_INVOICE";
    public static final String PAYROL_INVOICE = "PAYROL_INVOICE"; // ok
    public static final String SETTLEMENT_INVOICE = "SETTLEMENT_INVOICE";
    //sales invoice
    public static final String SALES_INVOICE = "SALES_INVOICE"; // ok
    public static final String PURC_RTN_INVOICE = "PURC_RTN_INVOICE"; // ok
    public static final String INTEREST_INVOICE = "INTEREST_INVOICE"; // ok
    public static final String PAY_SETTLEMENT_INV = "PAY_SETTLEMENT_INV";
    public static final String GIFTS_INVOICE = "GIFTS_INVOICE";
    public static final String CANCEL_INVOICE = "CANCEL_INVOICE";

}
