package com.olbius.olap.bi.sales;

import com.olbius.bi.olap.OlapFactoryInterface;

public class SalesOlapFactory implements OlapFactoryInterface {

	public final static String SALES_AMOUNT = "SALES_AMOUNT";
	
	private String sales;
	
	public SalesOlapFactory(String sales) {
		this.sales = sales;
	}
	
	public SalesOlap newInstance() {
		if(SALES_AMOUNT.equals(sales)) {
			return new SalesTotalOlap();
		}
		return null;
	}
}
