package com.olbius.crm.report;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ResultEmployee {
	private final OlbiusQuery query;
	
	private SQLProcessor processor;
	
	public ResultEmployee(SQLProcessor processor) {
		this.processor = processor;
		query = (OlbiusQuery) new OlbiusQuery(processor).select("DISTINCT party_dimension.party_id as party_id")
				.from("communication_event_fact")
				.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = communication_event_fact.party_from_dim_id")
				.where(Condition.make("party_id is NOT NULL").and(Condition.makeEQ("communication_event_fact.status_call", "Y"))).orderBy("party_id");
	}
	
	public List<String> getListEmployee() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("party_id"));
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
	
	public static Map<String, Object> getListEmployee(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		ResultEmployee type = new ResultEmployee(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listEmployeee", type.getListEmployee());
		return result;
	}
}