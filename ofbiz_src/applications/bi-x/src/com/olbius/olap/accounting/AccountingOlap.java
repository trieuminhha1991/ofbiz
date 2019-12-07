package com.olbius.olap.accounting;

import java.sql.SQLException;
import java.util.List;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.olap.OlapInterface;

public interface AccountingOlap extends OlapInterface {

	void setGroup(String group);

	void setGroup(List<String> groups);
	
	void evaluateAcc(boolean dateFlag, boolean productFlag, boolean categoryFlag, boolean groupFlag, String product, String code, String currency, String debitCreditFlag,
			String dateType, boolean orig, boolean level, boolean distrib, boolean sort) throws GenericDataSourceException,
			GenericEntityException, SQLException;

}
