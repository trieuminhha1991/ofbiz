package com.olbius.salesmtl.report;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesDelayCustomer extends TypeOlap {
	private final OlbiusQuery query;
	private final OlbiusQuery fromQuery;
	private final OlbiusQuery joinQuery;
	private SQLProcessor processor;
	
//<<<<<<< 642692b67911fd17fdbf631c3a966759ceaa8d6e
//	public SalesDelayCustomer(SQLProcessor processor, String salesmanId, int days) {
//=======
	public SalesDelayCustomer(SQLProcessor processor, String salesmanId, Boolean userType, int days) {
//>>>>>>> 20160408 update report
		this.processor = processor;
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, - days);
		Date today30 = cal.getTime();
		Condition condition = new Condition();
//		Condition condition2 = new Condition();
		
		condition.andEQ("cr.role_type_id_to", "CUSTOMER", userType == false)
		.andEQ("cr.role_type_id_from", "CUSTOMER", userType == true)
		.andEQ("pd2.party_id", salesmanId);
		
		fromQuery = (OlbiusQuery) new OlbiusQuery(processor).from("party_dimension", "pd")
			.select("distinct pd.party_id as customer_id")
			.join(Join.INNER_JOIN, "customer_relationship", "cr", "cr.group_dim_id = pd.dimension_id", userType == false) //salesman
			.join(Join.INNER_JOIN, "customer_relationship", "cr", "cr.person_dim_id = pd.dimension_id", userType == true) //distributor
			.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = cr.person_dim_id", userType == false)
			.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = cr.group_dim_id", userType == true)
			.where(condition);
		
		joinQuery = (OlbiusQuery) new OlbiusQuery(processor).select("distinct pd.party_id as cus_id")
			.from("public.sales_order_fact", "sof")
			.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = sof.sale_executive_party_dim_id", userType == false)
			.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = sof.party_from_dim_id", userType == true)
			.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
			.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = sof.party_to_dim_id")
			.where(Condition.makeBetween("dd.date_value", getSqlDate(today30), getSqlDate(today)).andEQ("pd2.party_id", salesmanId));
		
		query =(OlbiusQuery) new OlbiusQuery(processor).select("distinct tmp.customer_id")
			.from(fromQuery, "tmp")
			.join(Join.LEFT_OUTER_JOIN, joinQuery, "join1", "tmp.customer_id = join1.cus_id")
			.where(Condition.make("join1.cus_id is null"));
	}
	
	public List<String> getDelayCustomer() {
		List<String> list = new ArrayList<String>();
		
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("customer_id"));
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
	
	public static Map<String, Object> getListDelayCustomer(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String salesmanId = userLogin.getString("partyId");
//<<<<<<< 642692b67911fd17fdbf631c3a966759ceaa8d6e
		Integer days = (Integer) context.get("days");
		if(days == null){
			days = 30;
		}
//		SalesDelayCustomer type = new SalesDelayCustomer(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), salesmanId, days);
//=======
//		String uType = (String) context.get("userType"); 
		String disId = (String) context.get("distributorId");
//		Boolean userType = false;
//		if("salesman".equals(uType)){
//			userType = false;
//		} else 
//		if("distributor".equals(uType)){
		Boolean	userType = true;
//		}
		
		SalesDelayCustomer type = new SalesDelayCustomer(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), disId, userType, days);
//>>>>>>> 20160408 update report
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listCustomer", type.getDelayCustomer());
		return result;
	}
}

