package com.olbius.basehr.report.absent.query;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class AbsentOlapImplChart extends AbstractOlap{

	public static final String FROMDATE = "FROMDATE";
	public static final String THRUDATE = "THRUDATE";
	public static final String ORG = "ORG";
	private OlbiusQuery query;
	
	@SuppressWarnings({ "unchecked", "static-access" })
	private void initQuery() {
		List<Object> org = (List<Object>) getParameter(ORG);
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("absent_fact", "af")
		.select("party_dimension.party_id")
		.select("eld.empl_leave_id")
		.select("elrtd.empl_leave_reason_type_id")
		.select("ws.working_shift_id")
		.select("dd.date_value", "from_date")
		.select("dd1.date_value", "thru_date")
		/*.select("af.approver_party_id")*/
		.select("af.empl_timekeeping_sign_id")
		.select("af.description")
		.select("af.is_benefit_social_ins")
		.select("af.parent_type_id")
		.select("af.is_benefit_social_ins")
		.select("af.is_benefit_sal")
		.select("af.description1")
		.select("COALESCE( party_dimension.last_name, '' ) || ' ' || COALESCE( party_dimension.middle_name, '' ) || ' ' || COALESCE( party_dimension.first_name, '' )", "cus1_name")
		.join(Join.INNER_JOIN, "party_dimension", "af.party_dim_id = party_dimension.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension","dd", "af.from_date_dim = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension","dd1", "af.thru_date_dim = dd1.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "af.department_party_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd1", "af.org_party_dim_id = pd1.dimension_id" )
		.join(Join.INNER_JOIN, "empl_leave_reason_type_dimension", "elrtd", "af.empl_leave_reason_type_dim_id = elrtd.dimension_id")
		.join(Join.INNER_JOIN, "empl_leave_dimension", "eld", "af.empl_leave_dim_id = eld.dimension_id")
		.join(Join.INNER_JOIN, "working_shift_dimension", "ws", "af.working_shift_dim_id = ws.dimension_id")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(org)){
			condition.and(condition.makeIn("pd1.party_id", org));
		}
		condition.and(Condition.make("dd.date_value", "<", getSqlDate(thruDate)));
		condition.and(Condition.make("dd1.date_value", ">", getSqlDate(fromDate)));
	}
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	public class absentTimeColumn implements OlapResultQueryInterface{
		
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String,Object>>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			tmp.put("quantity", new HashMap<String, Object>());
			try {
				ResultSet resultSet = query.getResultSet();
				Map<String, Object> maptmp = FastMap.newInstance();
				while (resultSet.next()) {
					try {
						String emplLeaveReasonTypeId = (String) resultSet.getString("empl_leave_reason_type_id");
						GenericValue emplLeaveReasonType = delegator.findOne("EmplLeaveReasonType", UtilMisc.toMap("emplLeaveReasonTypeId", emplLeaveReasonTypeId), false);
						String description = emplLeaveReasonType.getString("description");
						List<GenericValue> listEmplReasonTypeLeave = delegator.findList("EmplLeaveReasonType", null, null, null, null, false);
						List<String> Listdescription = EntityUtil.getFieldListFromEntityList(listEmplReasonTypeLeave, "description", true);
						GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", resultSet.getString("empl_leave_id")), false);
						for (String s : Listdescription) {
							float day_s = 0;
							if(s.equals(description)){
								if(UtilValidate.isNotEmpty(emplLeave)){
									day_s = EmployeeHelper.getNbrDayLeave(delegator, emplLeave);
									if(UtilValidate.isNotEmpty(maptmp.get(description)) && maptmp.containsKey(description)){
										day_s += (float) maptmp.get(description);
									}
								}
								maptmp.put(description, day_s);
							}else{
								if(maptmp.containsKey(s)){
									continue;
								}else{
									maptmp.put(s, day_s);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
				tmp.get("quantity").putAll(maptmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tmp;
		}
		
	}
	public class absentTimeColumnOut extends AbstractOlapChart{

		public absentTimeColumnOut(OlapInterface olap,
				OlapResultQueryInterface query) {
			super(olap, query);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@Override
		protected void result(Object object) {
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> map = (Map<String, Map<String,Object>>) object;
			for (String key : map.keySet()) {
				if(yAxis.get(key) == null) {
					yAxis.put(key, new ArrayList<Object>());
				}
				for (String s : map.get(key).keySet()) {
					yAxis.get(key).add(map.get(key).get(s));
					if(!xAxis.contains(s)) {
						xAxis.add(s);
					}
				}
			}
		}
		
	}
	
	public class absentTimePie implements OlapResultQueryInterface{
		
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Object> map = new HashMap<String, Object>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			try {
				ResultSet resultSet = query.getResultSet();
				while (resultSet.next()) {
					String emplLeaveReasonTypeId = (String) resultSet.getString("empl_leave_reason_type_id");
					GenericValue emplLeaveReasonType = delegator.findOne("EmplLeaveReasonType", UtilMisc.toMap("emplLeaveReasonTypeId", emplLeaveReasonTypeId), false);
					String description = emplLeaveReasonType.getString("description");
					List<GenericValue> listEmplReasonTypeLeave = delegator.findList("EmplLeaveReasonType", null, null, null, null, false);
					List<String> Listdescription = EntityUtil.getFieldListFromEntityList(listEmplReasonTypeLeave, "description", true);
					GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", resultSet.getString("empl_leave_id")), false);
					for (String s : Listdescription) {
						float day_s = 0;
						if(s.equals(description)){
							if(UtilValidate.isNotEmpty(emplLeave)){
								day_s = EmployeeHelper.getNbrDayLeave(delegator, emplLeave);
								if(UtilValidate.isNotEmpty(map.get(description)) && map.containsKey(description)){
									day_s += (float) map.get(description);
								}
							}
							map.put(description, day_s);
						}else{
							if(map.containsKey(s)){
								continue;
							}else{
								map.put(s, day_s);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return map;
		}
		
	}
	
	public class absentTimePieOut extends AbstractOlapChart{

		public absentTimePieOut(OlapInterface olap,
				OlapResultQueryInterface query) {
			super(olap, query);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@Override
		protected void result(Object object) {
			Map<String, Object> tmp = ( Map<String, Object>) object;
			for(String s : tmp.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(tmp.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}
		
	}

}
