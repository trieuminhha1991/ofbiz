package com.olbius.basehr.report.absent.query;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class AbsentOlapImpl extends AbstractOlap {
	public static final String  EMPLOYEEID = "EMPLOYEEID";
	public static final String FROMDATE = "FROMDATE";
	public static final String THRUDATE = "THRUDATE";
	public static final String ORG = "ORG";
	
	private OlbiusQuery query;

	public AbsentOlapImpl() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	private void initQuery() {
		List<Object> employeeId = (List<Object>) getParameter(EMPLOYEEID);
		List<Object> org = (List<Object>) getParameter(ORG);
		
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("absent_fact", "af")
		.select("party_dimension.party_id")
		.select("eld.empl_leave_id")
		.select("elrtd.empl_leave_reason_type_id")
		.select("ws.working_shift_id")
		.select("dd.date_value", "from_date")
		.select("dd1.date_value", "thru_date")
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
		
		if(UtilValidate.isNotEmpty(employeeId)){
			condition.and(condition.makeIn("party_dimension.party_id", employeeId));
		}
		if(UtilValidate.isNotEmpty(org)){
			condition.and(condition.makeIn("pd1.party_id", org));
		}
		condition.and(Condition.make("dd.date_value", "<", getSqlDate(thruDate)));
		condition.and(Condition.make("dd1.date_value", ">", getSqlDate(fromDate)));
	}
	
	
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null){
			initQuery();
		}
		// TODO Auto-generated method stub
		return query;
	}
	
	public class getData extends ReturnResultGrid {
		private List<String> ListemplLeaveReasonTypeId;
		public getData() {
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			addDataField("STT");
			addDataField("fromDate");
			addDataField("thruDate");
			addDataField("partyId");
			addDataField("partyName");
			List<GenericValue> listEmplReasonTypeLeave = FastList.newInstance();
			try {
				listEmplReasonTypeLeave = delegator.findList("EmplLeaveReasonType", null, null, null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(UtilValidate.isNotEmpty(listEmplReasonTypeLeave)){
				this.ListemplLeaveReasonTypeId = EntityUtil.getFieldListFromEntityList(listEmplReasonTypeLeave, "emplLeaveReasonTypeId", true);
				for (String s : ListemplLeaveReasonTypeId) {
					addDataField(s);
				}
			}
		}
		
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = FastMap.newInstance();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			try {
				String fullName = result.getString("cus1_name");
				Timestamp fromDate = result.getTimestamp("from_date");
				Timestamp thruDate = result.getTimestamp("thru_date");
				map.put("partyName", fullName);
				map.put("fromDate", fromDate);
				map.put("thruDate", thruDate);
				map.put("partyId", result.getString("party_id"));
				for (int i = 0; i < ListemplLeaveReasonTypeId.size(); i++) {
					String empl_leave_reason_type_id = result.getString("empl_leave_reason_type_id");
					String s = ListemplLeaveReasonTypeId.get(i);
					if(empl_leave_reason_type_id.equals(s)){
						GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", result.getString("empl_leave_id")), false);
						if(UtilValidate.isNotEmpty(emplLeave)){
							float day_s = EmployeeHelper.getNbrDayLeave(delegator, emplLeave);
							map.put(s, day_s);
						}else{
							map.put(s, null);
						}
					}else{
						map.put(s, null);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
			
			return map;
		}
	}
}


