package com.olbius.salesmtl.report;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;

import com.olbius.basesales.report.ResultProductStore;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class NewCustomerSynthesisImpl extends OlbiusBuilder{
	public static final String ORG = "ORG";
	public static final String LEVEL = "LEVEL";
	public static final String FLAG_CHILD = "FLAG_CHILD";
	public static final String FLAG_PARENT = "FLAG_PARENT";
	public static final String DEP_ID = "DEP_ID";
	public static final String MONTHH = "MONTHH";
	public static final String YEARR = "YEARR";
	public static final String LIMIT_SALESMAN = "LIMIT_SALESMAN";
	public static final String FLAG_TYPE_PERSON = "FLAG_TYPE_PERSON";
	
	public NewCustomerSynthesisImpl(Delegator delegator) {
		super(delegator);
	}
	
	private List<Map<String,String>> type;
	
	@SuppressWarnings("unused")
	private List<Map<String,String>> getType() {
		if(type == null) {
			ResultProductStore enumType = new ResultProductStore(getSQLProcessor());
			type = enumType.getListResultStore();
		}
		return type;
	}
	
	private OlbiusQuery query;
	private OlbiusQuery fromQuery;
	private OlbiusQuery joinQuery;
	
	@Override
	public void prepareResultGrid() {
		addDataField("depName", "name");
		addDataField("depId", "party_id");
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	private void initQuery() {
		Condition condition = new Condition();
		Condition fromCondition = new Condition();
		Condition joinCondition = new Condition();
		query = new OlbiusQuery(getSQLProcessor());
		fromQuery = new OlbiusQuery(getSQLProcessor());
		joinQuery = new OlbiusQuery(getSQLProcessor());
		String getYear = (String) getParameter(YEARR);
		String getMonth = (String) getParameter(MONTHH);
		String dateString = getMonth + "/01/" + getYear;
		
	    DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
	    Date startDate;
	    Timestamp dateInput = null;
	    try {
	        startDate = df.parse(dateString);
	        dateInput = new Timestamp(startDate.getTime());
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
//		String level = (String) getParameter(LEVEL);
//		String depId = (String) getParameter(DEP_ID);
//		Boolean flagChild = (Boolean) getParameter(FLAG_CHILD);
//		String flagParent = (String) getParameter(FLAG_PARENT);
		
//		select distinct abc.party_to_dim_id, ,  as aaa, sum(soff.total) from (   
//				select distinct sof.party_to_dim_id from public.sales_order_fact as 
//				inner join public. as dd on 
//				where dd.date_value < '2016-03-01' order by 
//			) as def
//			right join (
//				select distinct sof.party_to_dim_id from public.sales_order_fact as sof 
//				inner join public. as dd on 
//				where dd.date_value >= '2016-03-01' order by sof.party_to_dim_id
//			) as abc on (abc.party_to_dim_id = def.party_to_dim_id)
//			inner join public.sales_order_fact as soff on soff.party_to_dim_id = abc.party_to_dim_id
//			inner join public.party_dimension as pdd on pdd.dimension_id = abc.party_to_dim_id
//			inner join public.product_dimension as prdd on prdd.dimension_id = soff.product_dim_id
//			where 
//			group by , pdd.party_id, aaa
//			order by 
		
		fromQuery.distinctOn("sof.party_to_dim_id").from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.where(fromCondition).orderBy("sof.party_to_dim_id");
		fromCondition.and("dd.date_value < '" + dateInput + "'" );
		
		joinQuery.distinctOn("sof.party_to_dim_id").from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.where(joinCondition).orderBy("sof.party_to_dim_id");
		joinCondition.and("dd.date_value >= '" + dateInput + "'");
		
		query.distinctOn("abc.party_to_dim_id").select("pdd.party_id").select("pdd.name", "name1").select("sum(soff.total)", "value_total")
		.from(fromQuery, "def")
		.join(Join.RIGHT_OUTER_JOIN, joinQuery, "abc", "abc.party_to_dim_id = def.party_to_dim_id")
		.where(condition).groupBy("abc.party_to_dim_id").groupBy("name1").orderBy("pdd.party_id");
		condition.and("def.party_to_dim_id is null");
	}
}
