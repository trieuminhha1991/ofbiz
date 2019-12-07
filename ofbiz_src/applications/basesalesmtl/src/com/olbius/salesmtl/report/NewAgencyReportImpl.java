package com.olbius.salesmtl.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class NewAgencyReportImpl extends OlbiusBuilder{
	public static final String MONTHH = "MONTHH";
	public static final String YEARR = "YEARR";
	public static final String QUARTERR = "QUARTERR";
	public static final String TIME_TYPE = "TIME_TYPE";
	
	public NewAgencyReportImpl(Delegator delegator) {
		super(delegator);
	}
	
	private OlbiusQuery query;
	private OlbiusQuery queryFrom;
	private OlbiusQuery queryJoin;
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("agency", "cus_code");
		addDataField("agency_name", "cus_name");
		addDataField("salesman", "se_code");
		addDataField("salesman_name", "se_name");
		addDataField("distributor");
		addDataField("value_total", "value_total");
	}
	
	private void initQuery() {
		Condition condition = new Condition();
		Condition joinCondition = new Condition();
		query = new OlbiusQuery(getSQLProcessor());
		queryFrom = new OlbiusQuery(getSQLProcessor());
		queryJoin = new OlbiusQuery(getSQLProcessor());
		
		String getMonth = (String) getParameter("MONTHH");
		String getQuarter = (String) getParameter("QUARTERR");
		String getYear = (String) getParameter("YEARR");
		String getTypeTime = (String) getParameter("TIME_TYPE");
		int monthInput = 0;
		int quarterInput = 0;
		int yearInput = 0;
		if(UtilValidate.isNotEmpty(getMonth)){
			monthInput = Integer.parseInt(getMonth); 
		}
		if(UtilValidate.isNotEmpty(getQuarter)){
			quarterInput = Integer.parseInt(getQuarter);
		}
		if(UtilValidate.isNotEmpty(getYear)){
			yearInput = Integer.parseInt(getYear);
		}
		
//		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//		String dateStr = "01/" + getMonth + "/" + getYear;
//		
//		
//		Date date1 = null;
//		try {
//			date1 = (Date) formatter.parse(dateStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		Timestamp time = new Timestamp(date1.getTime());
		
		String dateString = getMonth + "/01/" + getYear;
		
	    DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
//	    Date newDate = new Date();
	    
	    Date date1 = null;
	    try {
	    	date1 = df.parse(dateString);
//	        dateInput = new Timestamp(date1.getTime());
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
		
		condition.and("query_from.party_to_dim_id is null").andEQ("dd2.year_name", yearInput).andEQ("dd2.month_of_year", monthInput);
		
		queryFrom.distinctOn("sof.party_to_dim_id").select("sof.party_to_dim_id").from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.where(Condition.make("dd.date_value < '" + getSqlDate(date1) + "'"))
		.orderBy("sof.party_to_dim_id");
		
		queryJoin.distinctOn("sof.party_to_dim_id").select("sof.party_to_dim_id")
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.where(joinCondition).orderBy("sof.party_to_dim_id");
		joinCondition.andEQ("dd.year_name", yearInput).andEQ("dd.month_of_year", monthInput);
	
		query.select("pd.party_code", "cus_code").select("pd.description", "cus_name")
		.select("sum(sof2.total)", "value_total").select("pd2.party_code", "se_code")
		.select("pd2.description", "se_name").from(queryFrom, "query_from")
		.join(Join.RIGHT_OUTER_JOIN, queryJoin, "query_join", "query_from.party_to_dim_id = query_join.party_to_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = query_join.party_to_dim_id")
		.join(Join.INNER_JOIN, "sales_order_fact", "sof2", "sof2.party_to_dim_id = query_join.party_to_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = sof2.sale_executive_party_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd2", "dd2.dimension_id = sof2.order_date_dim_id")
		.where(condition).groupBy("pd.party_code").groupBy("pd.description").groupBy("pd2.party_code").groupBy("pd2.description");
		
	}
}
