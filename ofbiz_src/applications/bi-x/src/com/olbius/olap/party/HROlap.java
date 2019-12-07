package com.olbius.olap.party;

import java.sql.SQLException;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

public interface HROlap {

	void timeTracker(String timeId) throws GenericDataSourceException, GenericEntityException, SQLException;

	void onTime(String timeId) throws GenericDataSourceException, GenericEntityException, SQLException;

	void salaryStructure(String party_person) throws GenericDataSourceException, GenericEntityException, SQLException;

	void salaryRange() throws GenericDataSourceException, GenericEntityException, SQLException;

	void salaryRangeByPosition(String dateType) throws GenericDataSourceException, GenericEntityException, SQLException;

}