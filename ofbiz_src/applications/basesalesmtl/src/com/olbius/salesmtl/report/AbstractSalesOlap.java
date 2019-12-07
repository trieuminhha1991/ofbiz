package com.olbius.salesmtl.report;

import com.olbius.bi.olap.AbstractOlap;

public abstract class AbstractSalesOlap extends AbstractOlap implements SalesOlap{
	
	public final static String TYPE_PRODUCT_STORE = "PRODUCT_STORE";
	public final static String TYPE_PARTY_FROM = "PARTY_FROM";
	public final static String TYPE_PARTY_TO = "PARTY_TO";
	public final static String TYPE_CHANNEL = "CHANNEL";
	public final static String TYPE_METHOD_CHANNEL = "METHOD_CHANNEL";
	public final static String TYPE_PRODUCT = "PRODUCT";

	protected final static String _PRODUCT_STORE = "product_store_dimension.product_store_id";
	protected final static String _PARTY_FROM = "party_group_from_dimension.party_id";
	protected final static String _PARTY_TO = "party_group_to_dimension.party_id";
	protected final static String _CHANNEL = "sales_channel_enum.enum_id";
	protected final static String _METHOD_CHANNEL = "sales_method_channel_enum.enum_id";
	protected final static String _PRODUCT = "product_dimension.product_id";
	
	protected String getStatistic(String type) {
		return TYPE_PRODUCT_STORE.equals(type) ? _PRODUCT_STORE
				: TYPE_PARTY_FROM.equals(type) ? _PARTY_FROM
						: TYPE_PARTY_TO.equals(type) ? _PARTY_TO
								: TYPE_CHANNEL.equals(type) ? _CHANNEL
										: TYPE_METHOD_CHANNEL.equals(type) ? _METHOD_CHANNEL
											: TYPE_PRODUCT.equals(type) ? _PRODUCT : null;
	}
}
