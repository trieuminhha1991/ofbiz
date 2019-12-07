package com.olbius.basehr.report.salary.query;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;

public class ResultPayrollItemType {
	private OlbiusQuery query;
	private SQLProcessor processor;
	private String isParent;
	public ResultPayrollItemType(SQLProcessor processor) {
		this(processor, "N");
	}
	
	public ResultPayrollItemType(SQLProcessor processor, String isParent) {
		super();
		this.processor = processor;
		this.isParent = isParent;
		query = new OlbiusQuery(processor);
		Condition tempCond = Condition.make("payroll_item_type_id is not null"); 
		query.select("payroll_item_type_id").select("description").select("sequence_num")
		.from("payroll_item_type_dimension")
		.orderBy("sequence_num");
		if("Y".equals(isParent)){
			tempCond.and(Condition.make("parent_type_id is null"));
		}
		query.where(tempCond);
	}
	public List<Map<String, String>> getListPayrollItemType(){
		List<Map<String, String>> list = FastList.newInstance();
		
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				 Map<String, String> tmp = FastMap.newInstance();
				 tmp.put("payroll_item_type_id", resultSet.getString("payroll_item_type_id"));
				 tmp.put("description", resultSet.getString("description"));
				 tmp.put("sequence_num", resultSet.getString("sequence_num"));
				 list.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(processor != null) {
				 try {
					processor.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	public String getIsParent() {
		return isParent;
	}
	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}
}
