package com.olbius.olap.party;

import java.sql.SQLException;
import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.olap.OlapInterface;

public interface PartyOlap extends OlapInterface {

	public final static String PARTY_DIMENSION = "party_dimension";
	public final static String PARTY_GROUP_DIMENSION = "party_group_dimension";
	public final static String PERSON_FACT = "person_fact";
	public final static String PARTY_RELATIONSHIP_FACT = "party_relationship_fact";
	
	public final static String EDU_SYS = "EDU_SYS";
	public final static String CLASSIFICATION = "CLASSIFICATION";
	public final static String SCHOOL = "SCHOOL";
	public final static String STUDY_MODE = "STUDY_MODE";
	public final static String MAJOR = "MAJOR";
	
	public final static String POSITION = "POSITION";
	public final static String POSITION_TYPE = "POSITION_TYPE";
	
	void personBirth(boolean gender) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void setGroup(List<?> list, boolean olap, Delegator delegator) throws GenericEntityException;
	
	void setGroup(String group);
	
	void gender() throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void member(String dateType, boolean cur, List<?> groups) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void personOlap(String dateType, boolean ft) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void school(String type) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void position(String type) throws GenericDataSourceException, GenericEntityException, SQLException;
}
