package com.olbius.basesales.reports;

import java.util.Date;
import java.util.Map;

import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class OtherUsePromoCodeReport extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		//putParameter("xAxisName", (String) context.get("xAxisName"));
		putParameter("fileName", "OtherUsePromoCodeReport"); // cache the specific file
		
		//putParameter("vendor_id", SalesUtil.getCurrentOrganization(delegator, (GenericValue) context.get("userLogin")));
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private OlbiusQuery init() {
		OlbiusQuery query = makeQuery();
		
		//String dateType = getDateType((String) getParameter("dateType"));
		//String columDateType = "ETYDIM.".concat(dateType);
		//String vendor_id = (String) getParameter("vendor_id");
		
		query.select("PPD.product_promo_id")
			.select("PPD.promo_name")
			.select("CUSTD.name", "customer_name")
			.select("SOPCF.product_promo_code_id")
			.select("SOPCF.order_id")
			.select("SOPCF.total_discount_amount")
			.from("sales_order_promo_code_fact", "SOPCF")
			.join(Join.INNER_JOIN, "product_promo_dimension", "PPD", "SOPCF.product_promo_dim_id = PPD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "CUSTD", "SOPCF.party_dim_id = CUSTD.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOPCF.entry_date_dim_id = ETYDIM.dimension_id")
			.where(Condition.makeBetween("entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate())));
					//.andEQ("VENDD.party_id", vendor_id)
					//.andEQ("SOF.order_status_id", orderStatusId, orderStatusId != null)
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		//String dateType = getDateType((String) getParameter("dateType"));
		//addDataField("dateTime", dateType);
		addDataField("product_promo_id", "product_promo_id");
		addDataField("promo_name", "promo_name");
		addDataField("customer_name", "customer_name");
		addDataField("product_promo_code_id", "product_promo_code_id");
		addDataField("order_id", "order_id");
		addDataField("total_discount_amount", "total_discount_amount");
	}
	
	/*@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			//addSeries("category_name");
			addXAxis("product_code");
			addYAxis("total_amount");
		} else if(getOlapResult() instanceof OlapPieChart) {
			String xAxisName = (String) getParameter("xAxisName");
			if (UtilValidate.isEmpty(xAxisName)) xAxisName = "product_code";
			addXAxis(xAxisName);
			addYAxis("total_amount");
		}
	}*/
}
