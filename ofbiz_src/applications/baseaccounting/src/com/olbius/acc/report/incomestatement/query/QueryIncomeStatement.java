package com.olbius.acc.report.incomestatement.query;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;

import com.olbius.acc.report.incomestatement.entity.IncomeConst;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.OlbiusReturnResultCallback;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;


public abstract class QueryIncomeStatement extends OlbiusBuilder implements IncomeOlapConstant{
	protected OlbiusQuery query;
	protected String dateType;
	protected String customerId;
	protected String categoryId;
	protected String productId;
	protected String organizationPartyId;
	private static String _dimension;
	protected String hasTransTime;
	protected Delegator delegator;
	public static final String MODULE = QueryIncomeStatement.class.getName();
	
	
	public QueryIncomeStatement(Delegator delegator) {
		super(delegator);
	}
	
	protected List<Object> getGlAccount(){
		List<Object> listAccountCode = FastList.newInstance();
		List<String> listGl = UtilMisc.toList("511","5211","5212","5213","632");
		listAccountCode.addAll(listGl);
		return listAccountCode;
	}
	
	public void setHasTransTime(String hasTransTime) {
		this.hasTransTime = hasTransTime;
	}
	
	protected void initQuery(){
		Condition cond = new Condition();
		
		query = new OlbiusQuery(getSQLProcessor());
		dateType = (String) getParameter(DATATYPE);
		customerId = (String) getParameter(CUSTOMER_ID);
		categoryId = (String) getParameter(CATEGORY_ID);
		productId = (String) getParameter(PRODUCT_ID);
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		dateType = getDateType(dateType);
		if(productId != null) {
			cond.and(Condition.makeEQ("pd.product_id", productId));
		}
		if(categoryId != null) {
			cond.and(Condition.makeEQ("cd.category_id", categoryId));
		}
		if(customerId != null) {
			cond.and(Condition.makeEQ("pgd.party_id", customerId));
		}
		cond.and(Condition.makeIn("gad.gl_account_id",getGlAccount()));
		//cond.and(Condition.makeEQ("cd.category_type", "CATALOG_CATEGORY"));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		
		OlbiusQuery subQuery = new OlbiusQuery();
		subQuery.from("acctg_trans_fact", "atf").select("dd.".concat(dateType), "transTime");
		switch (QueryIncomeStatement.get_dimension()) {
		case "general":
			subQuery.select("pgd.party_id", "partyId")
			.select("pgd.party_code", "partyCode")
			.select("pgd.name", "fullName")
			.select("pd.product_id", "productId")
			.select("pd.product_code", "productCode")
			.select("pd.product_name", "productName")
			.select("cd.category_id", "categoryId");
			break;
		case "product":
			subQuery.select("pd.product_id", "productId")
			.select("pd.product_code", "productCode")
			.select("pd.product_name", "productName");
			break;
		case "party":
			subQuery.select("pgd.party_id", "partyId")
			.select("pgd.party_code", "partyCode")
			.select("pgd.name", "fullName");
			break;
		case "category":
			subQuery.select("cd.category_id", "categoryId");
			break;	
		default:
			break;
		}
		subQuery.select("sum(case when gad.account_code = '511' then (-atf.amount) else 0 end)", IncomeConst.SALE_INCOME)
		.select("sum(case when gad.account_code = '5211' then (atf.amount) else 0 end)", IncomeConst.SALE_DISCOUNT)
		.select("sum(case when gad.account_code = '5212' then (atf.amount) else 0 end)", IncomeConst.SALE_RETURN)
		.select("sum(case when gad.account_code = '5213' then (atf.amount) else 0 end)", IncomeConst.PROMOTION)
		.select("sum(case when gad.account_code = '632' then (atf.amount) else 0 end)", IncomeConst.COGS)
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_relationship", "gar", "atf.gl_account_dim_id = gar.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "gad.dimension_id = gar.parent_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pgd", "atf.party_dim_id = pgd.dimension_id" )		
		.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pd.dimension_id = pcr.product_dim_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrf", "pcr.from_dim_date = dpcrf.dimension_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrt", "pcr.thru_dim_date = dpcrt.dimension_id" )
		.join(Join.INNER_JOIN, "category_dimension", "cd", "cd.dimension_id = pcr.category_dim_id AND cd.category_type = 'CATALOG_CATEGORY' AND dd.date_value >= dpcrf.date_value AND (dpcrt.date_value IS NULL OR dpcrt.date_value >= dd.date_value) " )
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond).groupBy("transTime").orderBy("transTime", OlbiusQuery.DESC);
		
		
		switch (QueryIncomeStatement.get_dimension()) {
		case "general":
			subQuery.groupBy("partyId")
			.groupBy("partyCode")
			.groupBy("fullName")
			.groupBy("productId")
			.groupBy("productCode")
			.groupBy("productName")
			.groupBy("categoryId");
			break;
		case "product":
			subQuery.groupBy("productId")
			.groupBy("productCode")
			.groupBy("productName");
			break;
		case "party":
			subQuery.groupBy("partyId")
			.groupBy("partyCode")
			.groupBy("fullName");
			break;
		case "category":
			subQuery.groupBy("categoryId");
			break;	
		default:
			break;
		}
		
		String aliasSubQuery = "tmp";
		String netValueCol = aliasSubQuery + "." + IncomeConst.SALE_INCOME + " - " + aliasSubQuery + "." 
				+ IncomeConst.PROMOTION + " - " + aliasSubQuery + "." + IncomeConst.SALE_RETURN 
				+ " - " + aliasSubQuery + "." + IncomeConst.SALE_DISCOUNT;
		String grossProfitCol = netValueCol + " - " + aliasSubQuery + "." + IncomeConst.COGS;
		query.select("*")
		.select(netValueCol, IncomeConst.NET_REVENUE)
		.select(grossProfitCol, IncomeConst.GROSS_PROFIT)
		.from(subQuery, aliasSubQuery);
	}
	
	public static String get_dimension() {
		return _dimension;
	}

	public static void set_dimension(String _dimension) {
		QueryIncomeStatement._dimension = _dimension;
	}
	
	
	@Override
	public void prepareResultGrid() {
		switch (QueryIncomeStatement.get_dimension()) {
		case "general":
			addDataField("partyId", "partyId");
			addDataField("productId", "productId");
			addDataField("categoryId","categoryId");
			addDataField("productCode","productCode");
			addDataField("partyCode","partyCode");
			addDataField("productName","productName");
			addDataField("fullName","fullName");
			break;
		case "product":
			addDataField("productId", "productId");
			addDataField("productCode", "productCode");
			addDataField("productName", "productName");
			break;
		case "party":
			addDataField("partyId", "partyId");
			addDataField("partyCode", "partyCode");
			addDataField("fullName", "fullName");
			break;
		case "category":
			addDataField("categoryId", "categoryId");
			break;	
		default:
			break;
		}
		/*addDataField("amount511","amount511");
		addDataField("amount5211","amount5211");
		addDataField("amount5212","amount5212");
		addDataField("amount5213","amount5213");
		addDataField("amount632","amount632");*/
		addDataField(IncomeConst.SALE_INCOME, IncomeConst.SALE_INCOME);
		addDataField(IncomeConst.SALE_DISCOUNT, IncomeConst.SALE_DISCOUNT);
		addDataField(IncomeConst.SALE_RETURN, IncomeConst.SALE_RETURN);
		addDataField(IncomeConst.PROMOTION, IncomeConst.PROMOTION);
		addDataField(IncomeConst.COGS, IncomeConst.COGS);
		addDataField(IncomeConst.NET_REVENUE, IncomeConst.NET_REVENUE);
		addDataField(IncomeConst.GROSS_PROFIT, IncomeConst.GROSS_PROFIT);
		
		addDataField("transTime", new OlbiusReturnResultCallback<String>("transTime") {

			@Override
			public String get(Map<String, Object> map) {
				String transTime = "";
				if(map.get("transTime") != null && map.get("transTime") instanceof String){
					String[] tmp = ((String)map.get("transTime")).split("-");
					if(tmp != null) {
						for(int i = tmp.length -1; i >= 0; i--) {
							transTime += tmp[i];
							transTime += "/";
						}
						transTime = transTime.substring(0, transTime.length()-1);
					}
				}
				return transTime;
			}
		});
	}
}