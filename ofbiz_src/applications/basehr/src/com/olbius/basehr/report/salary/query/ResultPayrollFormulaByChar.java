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

public class ResultPayrollFormulaByChar {
	private OlbiusQuery query;
	private SQLProcessor processor;
	public ResultPayrollFormulaByChar(SQLProcessor processor, String payroll_characteristic_id) {
		super();
		this.processor = processor;
		Condition tempCond = Condition.make("payroll_characteristic_id = '" + payroll_characteristic_id + "'");
		query = new OlbiusQuery(processor);
		query.select("code")
		.select("case when(pfd.abbreviation is not null) then pfd.abbreviation else pfd.name end", "name")
		.select("abbreviation")
		.from("payroll_formula_dimension", "pfd")
		.orderBy("name")
		.where(tempCond);
	}
	public List<Map<String, String>> getListPayrollFormulaByChar(){
		List<Map<String, String>> list = FastList.newInstance();
		
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				 Map<String, String> tmp = FastMap.newInstance();
				 tmp.put("code", resultSet.getString("code"));
				 tmp.put("name", resultSet.getString("name"));
				 tmp.put("abbreviation", resultSet.getString("abbreviation"));
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
}
