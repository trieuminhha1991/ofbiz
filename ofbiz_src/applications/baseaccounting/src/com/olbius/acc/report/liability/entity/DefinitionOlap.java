package com.olbius.acc.report.liability.entity;

public interface DefinitionOlap {
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String TYPE = "TYPE";
	public static final String SORT_FIELD = "SORT_FIELD";
	public static final String SORT_OPTION = "SORT_OPTION";
	
	public final static String TYPE_PRODUCT_STORE = "PRODUCT_STORE";
	public final static String TYPE_PARTY_FROM = "PARTY_FROM";
	public final static String TYPE_CHANNEL = "CHANNEL";
	public final static String TYPE_PRODUCT = "PRODUCT";
	public final static String TYPE_FACILITY = "FACILITY";
	public final static String TYPE_SUPPLIER = "SUPPLIER";
	public final static String TYPE_EMPLOYEE = "EMPLOYEE";
	public final static String TYPE_GL_ACCOUNT = "GL_ACCOUNT";
	public final static String TYPE_ORGANIZATION = "ORGANIZATION";
	
	public final static String _PRODUCT_STORE = "product_store_dimension.product_store_id";
	public final static String _PARTY_FROM = "party_person_dimension.party_id";
	public final static String _CHANNEL = "sales_order_fact.sales_channel_enum_id";
	public final static String _PRODUCT = "product_dimension.product_id";
	public final static String _FACILITY = "facility_dimension.facility_id";
	public final static String _SUPPLIER = "party_group_dimension.party_id";
	public final static String _EMPLOYEE = "employee.party_id";
	public final static String _GL_ACCOUNT = "gl_account_dimension.gl_account_id";
	
}
