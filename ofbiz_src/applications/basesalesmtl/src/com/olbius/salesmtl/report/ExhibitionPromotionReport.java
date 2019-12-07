package com.olbius.salesmtl.report;

import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class ExhibitionPromotionReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private GenericValue userLogin = null;
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		userLogin = (GenericValue) context.get("userLogin");
		int monthh = Integer.parseInt((String) context.get("monthh"));
		int yearr = Integer.parseInt((String) context.get("yearr"));
		putParameter("monthh", monthh);
		putParameter("yearr", yearr);
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private OlbiusQuery init() {
		int monthh = (int) getParameter("monthh");
		int yearr = (int) getParameter("yearr");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Condition condition = new Condition();
		condition.and("(pped.product_promo_type_id = 'PROMO_EXHIBITION' OR pped.product_promo_type_id = 'PROMO_ACCUMULATION')").andEQ("dd.month_of_year", monthh).andEQ("dd.year_name", yearr)
		.andEQ("pped.organization_party_id", company);
		
		query = makeQuery();
		
		query.select("pped.product_promo_id", "special_promo_id").select("pped.promo_name", "special_promo_name")
		.select("pd.party_code", "customer_code").select("pd.name", "customer_name")
		.from("product_promo_ext_dimension", "pped")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = pped.party_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = pped.from_date_dim")
		.where(condition).orderBy("special_promo_id");
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("special_promo_id", "special_promo_id");
		addDataField("special_promo_name", "special_promo_name");
		addDataField("customer_code", "customer_code");
		addDataField("customer_name", "customer_name");
	}
}