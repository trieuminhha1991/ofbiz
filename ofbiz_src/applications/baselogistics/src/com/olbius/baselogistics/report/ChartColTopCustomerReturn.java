package com.olbius.baselogistics.report;

import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class ChartColTopCustomerReturn extends OlbiusOlapService {
	private OlbiusQuery query;
	private GenericValue userLogin = null;
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("filterTop", (Integer) context.get("filterTop"));
		putParameter("filterSort", (String) context.get("filterSort"));
		userLogin = (GenericValue) context.get("userLogin");
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
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

		Integer filterTop = (Integer) getParameter("filterTop");
		String filterSort = (String) getParameter("filterSort");
		if (UtilValidate.isEmpty(filterTop)) filterTop = 5;
		if (!"DESC".equals(filterSort) && !"ASC".equals(filterSort)) filterSort = "DESC";

		query.select("pdf.party_id")
		.select("pdf.party_code")
		.select("pdf.name")
		.select(new Sum("(CASE WHEN iif.total_price is null THEN 0 ELSE iif.total_price END)"), "amount")
		.from("inventory_item_fact", "iif")
		.join(Join.INNER_JOIN, "party_dimension", "pdf", "iif.party_from_dim_id = pdf.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pdt", "iif.party_to_dim_id = pdt.dimension_id")
		.groupBy("pdf.party_id")
		.groupBy("pdf.party_code")
		.groupBy("pdf.name")
		.where(Condition.makeBetween("iif.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("iif.inventory_change_type", "SALES_RETURN")
					.andEQ("pdt.party_id", company)
					.andEQ("iif.inventory_type", "RECEIVE"))
		.orderBy("amount " + filterSort)
		.limit(filterTop);
		
		return query;
	}

	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapColumnChart) {
			addXAxis("party_code");
			addYAxis("amount");
		}
	}
}
