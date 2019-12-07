package com.olbius.basehr.report.recruitment.query;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class RecruitmentEffectiveImpl extends AbstractOlap {
	

	public static final String STATUS = "STATUS";
	public static final String RECRUIT = "RECRUIT";
	private OlbiusQuery query;
	private Locale locale;
	
	public RecruitmentEffectiveImpl(Locale locale) {
		this.locale = locale;
	}
	@SuppressWarnings({ "unchecked" })
	private void initQuery(){
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		Condition condition2 = new Condition();
		List<Object> recruimentPlanId_list = (List<Object>) getParameter(RECRUIT);
		List<Object> statusIdList = new ArrayList<>();
		statusIdList.add("RR_REC_RECEIVE");
		statusIdList.add("RR_REC_EMPL");
		//Count passed candidate 
		OlbiusQuery query2 = OlbiusQuery.make(getSQLProcessor());
		query2.from("recruitment_fact", "rf")
		.select("rpd.recruitment_plan_id")
		.select("count(DISTINCT rf.party_dim_id)", "success")
		.join(Join.INNER_JOIN, "status_dimension", "sd", "rf.status_recruit_dim_id = sd.dimension_id")
		.join(Join.INNER_JOIN, "recruitment_plan_dimension", "rpd", "rf.recruitment_plan_dim_id = rpd.dimension_id")
		.groupBy("rpd.recruitment_plan_id")
		.where(condition2);
		condition2.andIn("sd.status_id", statusIdList);
		
		query.distinct();
		query.from("recruitment_fact", "rf")
		.select("rpd.recruitment_plan_id", "planId")
		.select("rpd.recruitment_plan_name", "planName")
		.select("count(distinct rf.party_dim_id)", "quantity")
		.select("rf.quantity", "require")
		.select("tmp.success", "success")
		.join(Join.INNER_JOIN, "recruitment_plan_dimension", "rpd", "rf.recruitment_plan_dim_id = rpd.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, query2, "tmp", "rpd.recruitment_plan_id = tmp.recruitment_plan_id")
		.groupBy("rpd.recruitment_plan_id")
		.groupBy("rpd.recruitment_plan_name")
		.groupBy("rf.quantity")
		.groupBy("tmp.success")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(recruimentPlanId_list)){
			condition.andIn("rpd.recruitment_plan_id", recruimentPlanId_list);
		}
	}

	@Override
	protected OlapQuery getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	public class effectRecruitCol implements OlapResultQueryInterface{
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String,Object>>();
			tmp.put(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "QuantityOfCandidates", locale), new HashMap<String, Object>());
			tmp.put(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "NecessaryQuantityCand", locale), new HashMap<String, Object>());
			tmp.put(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "QuanOfSuccessCands", locale), new HashMap<String, Object>());
			
			try {
				ResultSet resultSet = query.getResultSet();
				Map<String, Object> maptmp = FastMap.newInstance();
				Map<String, Object> maptmp1 = FastMap.newInstance();
				Map<String, Object> maptmp2 = FastMap.newInstance();
				while(resultSet.next()){
					try {
						String s = resultSet.getString("planId");
						maptmp.put(s, resultSet.getBigDecimal("quantity"));
						maptmp1.put(s, resultSet.getBigDecimal("require"));
						maptmp2.put(s, resultSet.getBigDecimal("success"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					tmp.get(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "QuantityOfCandidates", locale)).putAll(maptmp);
					tmp.get(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "NecessaryQuantityCand", locale)).putAll(maptmp1);
					tmp.get(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "QuanOfSuccessCands", locale)).putAll(maptmp2);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tmp;
		}
		
	}
	
	public class effectRecruitColOut extends AbstractOlapChart{

		public effectRecruitColOut(OlapInterface olap,
				OlapResultQueryInterface query) {
			super(olap, query);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
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
	
}
