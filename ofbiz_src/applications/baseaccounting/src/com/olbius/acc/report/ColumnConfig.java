package com.olbius.acc.report;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelField;

import java.util.*;


public class ColumnConfig {
	private String header;
	private DataType dataType;
	private String name;
	private String entityName;
	private String fieldDescribe;
	private Cell cell;
	private int width = 35*256;
	
	public String getHeader() {
		return header;
	}
	
	public void setHeader(String header) {
		this.header = header;
	}
	
	public String getFieldDescribe() {
		return fieldDescribe;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public void setDescribe(String entityName,String fieldDescribe) {
		this.fieldDescribe = fieldDescribe;
		this.entityName = entityName;
	}
	
	public Object getValueDescribe(String fieldDescribe,String... primaryKeyValue) throws GenericEntityException
	{
		
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		List<ModelField> pkList =  delegator.getModelEntity(this.getEntityName()).getPkFields();
		Set<String> listKey = new HashSet<>();
			
		for(ModelField mf : pkList){
			if(mf.getIsPk())
				listKey.add(mf.getName());
		}
		
		if(listKey.size() != primaryKeyValue.length) 
			return null;
		else 
		{
			Map<String,String> map = new HashMap<>();
			if(pkList.size() == 1)
				map.put(pkList.get(0).getName(), primaryKeyValue[0]);
			else
			{
				
			}
			if(map.isEmpty()) 
				return "";
			
			GenericValue finder = delegator.findOne(this.getEntityName(), false, map);
			if(finder != null)
				return finder.getString(fieldDescribe);
		}
		
		
		
		return "";
	}
	
	public void setCell(Cell cell)
	{
		this.cell = cell;
	}
	public Cell getCell()
	{
		return this.cell;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWidth() {
		return width;
	}
	public void getWidthAuto(Sheet sheet, int column) {
		sheet.autoSizeColumn(column);
	}
	public void setWidth(int width) {
		this.width = width;
	}
}
