package com.olbius.crm.report;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ResultCustomer extends TypeOlap{
	private final OlbiusQuery query;
	private final OlbiusQuery query2;
	private SQLProcessor processor;
	
	public ResultCustomer(SQLProcessor processor, String organization) {
		this.processor = processor;
		Date curDate = new Date(System.currentTimeMillis());
		Timestamp curTime = new Timestamp(curDate.getTime());
		Timestamp startMonth = UtilDateTime.getMonthStart(curTime);
//		Date startMonthDate = new Date(startMonth.getTime());
		Condition condition = new Condition();
		Condition condition2 = new Condition();
//		inner join  on 
		query = (OlbiusQuery) new OlbiusQuery(processor)
				.select("count(person_relationship.person_dim_id)", "volume").from("person_relationship")
				.join(Join.INNER_JOIN, "date_dimension", "person_relationship.from_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "organ", "person_relationship.group_dim_id = organ.dimension_id")
				.where(condition);
				
				condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
				condition.andEQ("organ.party_id", organization);
				
//				select  from 
//				inner join  on 
//				inner join  on 
//				inner join  on 
//				inner join  as organ on 
//				where  and  = 'MB'
			
		query2 = (OlbiusQuery) new OlbiusQuery(processor)
				.select("count(*)", "calls_number").from("communication_event_fact")
				.join(Join.INNER_JOIN, "date_dimension", null, "date_dimension.dimension_id = communication_event_fact.entry_date_dim_id")
				.join(Join.INNER_JOIN, "party_person_relationship", null, "communication_event_fact.party_from_dim_id = party_person_relationship.person_dim_id")
				.join(Join.INNER_JOIN, "party_group_relationship", null, "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "organ", "party_group_relationship.parent_dim_id = organ.dimension_id")
				.where(condition2)
				;
				condition2.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
				condition2.andEQ("organ.party_id", organization);
				condition2.and("status_call='Y'");
				
	}
	
	public List<String> getCustomer() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("volume"));
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
	
	public List<String> getCalls() {
		List<String> list2 = new ArrayList<String>();
		
		try {
			ResultSet resultSet2 = query2.getResultSet();
			while(resultSet2.next()) {
				list2.add(resultSet2.getString("calls_number"));
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
		return list2;
	}
	
	public static Map<String, Object> getCustomerNumbers(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultCustomer type = new ResultCustomer(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getCustomer());
		return result;
	}
//	
	public static Map<String, Object> getCallsNumbers(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultCustomer type = new ResultCustomer(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listCallsValue", type.getCalls());
		return result;
	}
	
}