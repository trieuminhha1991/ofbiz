package com.olbius.acc.report.incomestatement.query;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public abstract class QueryChartIncomeStatement extends OlbiusBuilder implements IncomeOlapConstant{
	protected OlbiusQuery query;
	protected String organizationPartyId;
	private String _dimension;
	protected Delegator delegator;
	public static final String MODULE = QueryChartIncomeStatement.class.getName();
	
	public QueryChartIncomeStatement(Delegator delegator,String dimension) {
		super(delegator);
		set_dimension(dimension);
	}
	
	protected void initQuery(){
		Condition cond = new Condition();
		
		query = new OlbiusQuery(getSQLProcessor());
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		cond.and(Condition.makeIn("gad.account_code",UtilMisc.toList((Object) "511")));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf");
		switch (get_dimension()) {
		case "product":
			query.select("pd.product_id", "productId");
			query.select("pd.product_code", "productCode");
			break;
		case "party":
			query.select("pgd.party_id", "partyId");
			break;
		case "category":
			query.select("cd.category_id", "categoryId");
			break;	
		default:
			break;
		}
		
		query.select("-sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_relationship", "gar", "atf.gl_account_dim_id = gar.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "gad.dimension_id = gar.parent_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id");
		switch (get_dimension()) {
		case "product":
			query.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" );
			break;
		case "party":
			query.join(Join.INNER_JOIN, "party_dimension", "pgd", "pgd.dimension_id = atf.party_dim_id");
			break;
		case "category":
			query.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
			.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pd.dimension_id = pcr.product_dim_id" )
			.join(Join.INNER_JOIN, "date_dimension", "dpcrf", "pcr.from_dim_date = dpcrf.dimension_id" )
			.join(Join.INNER_JOIN, "date_dimension", "dpcrt", "pcr.thru_dim_date = dpcrt.dimension_id" )
			.join(Join.INNER_JOIN, "category_dimension", "cd", "cd.dimension_id = pcr.category_dim_id AND cd.category_type = 'CATALOG_CATEGORY' AND dd.date_value >= dpcrf.date_value AND (dpcrt.date_value IS NULL OR dpcrt.date_value >= dd.date_value) " );
			break;	
		default:
			break;
		}
		
		query.where(cond);
		
		switch (get_dimension()) {
			case "product":
				query.groupBy("productId")
				.groupBy("productCode")
				.orderBy("productId", OlbiusQuery.DESC);
				break;
			case "party":
				query.groupBy("partyId")
				.orderBy("partyId", OlbiusQuery.DESC);
				break;
			case "category":
				query.groupBy("categoryId")
				.orderBy("categoryId", OlbiusQuery.DESC);
				break;	
			default:
				break;
		}
	}
	
	public String get_dimension() {
		return _dimension;
	}

	public void set_dimension(String _dimension) {
		this._dimension = _dimension;
	}
	
	@Override
	public void prepareResultChart() {
		super.prepareResultChart();
		if(getOlapResult() instanceof OlapPieChart){
			switch (get_dimension()) {
				case "product":
					addXAxis("productCode");
					break;
				case "party":
					addXAxis("partyId");
					break;
				case "category":
					addXAxis("categoryId");
					break;	
				default:
					break;
			}
			addYAxis("amount");
		}
	}
}