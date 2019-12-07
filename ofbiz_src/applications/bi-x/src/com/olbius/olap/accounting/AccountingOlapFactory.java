package com.olbius.olap.accounting;

import com.olbius.olap.OlapFactoryInterface;

public class AccountingOlapFactory implements OlapFactoryInterface{
	
	public AccountingOlap newInstance() {

		return new AccountingOlapImpl();

	}
	
}
