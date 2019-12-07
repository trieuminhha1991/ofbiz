package com.olbius.olap.accounting;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.InnerJoin;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.Query;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class AccountingOlapImplv2 extends OlbiusBuilder {
	public static final String DATE_FLAG = "DATE_FLAG"; 
	public static final String PRODUCT_FLAG = "PRODUCT_FLAG";
	public static final String CATEGORY_FLAG = "CATEGORY_FLAG"; 
	public static final String GROUP_FLAG = "GROUP_FLAG";
	public static final String PRODUCT = "PRODUCT";
	public static final String CODE = "CODE";
	public static final String CURRENCY = "CURRENCY";
	public static final String DEBIT_CREDIT_FLAG ="DEBIT_CREDIT_FLAG";
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String ORIG = "ORIG";
	public static final String LEVEL = "LEVEL";
	public static final String DISTRIB = "DISTRIB";
	public static final String SORT = "SORT";
	public static final String GROUP = "GROUP";
	public static final String LIMIT = "LIMIT";
	public static final String GROUPS = "GROUPS";
	private List<Object> groups;
	
	public AccountingOlapImplv2(Delegator delegator) {
		super(delegator);
	}

	private OlbiusQuery query;

	@Override
	public void prepareResultChart() {
		Boolean orig = (Boolean) getParameter(ORIG);
		Boolean dateFlag = (Boolean) getParameter(DATE_FLAG);
		Boolean productFlag = (Boolean) getParameter(PRODUCT_FLAG);
		Boolean categoryFlag = (Boolean) getParameter(CATEGORY_FLAG);
		Boolean groupFlag = (Boolean) getParameter(GROUP_FLAG);
		Boolean level = (Boolean) getParameter(LEVEL);
		Boolean distrib = (Boolean) getParameter(DISTRIB);
		Boolean sort = (Boolean) getParameter(SORT);
		String dateType = (String) getParameter(DATE_TYPE);
		String product = (String) getParameter(PRODUCT);
		String code = (String) getParameter(CODE);
		String group = (String) getParameter(GROUP);
		String currency = (String) getParameter(CURRENCY);
		String debitCreditFlag = (String) getParameter(DEBIT_CREDIT_FLAG);
		long limit = (long) getParameter(LIMIT);
		dateType = getDateType(dateType);
		
		if(getOlapResult() instanceof OlapLineChart) {		
			getOlapResult().putParameter(DATE_TYPE, dateType);
			if (dateFlag)
			{
				if(productFlag && !groupFlag) {
					addSeries("product_id");
				}
				else if(categoryFlag && !groupFlag) {
					addSeries("category_id");
				}
				else
					addSeries("party_id");
			}
			addXAxis(dateType);
			addYAxis("_amount");
		}
		
		if(getOlapResult() instanceof OlapColumnChart) {

				if(productFlag && !groupFlag) {
					addXAxis("product_id");
				}
				else if(categoryFlag && !groupFlag) {
					addXAxis("category_id");
				}
				else
					addXAxis("party_id");
			addYAxis("_amount");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		Boolean orig = (Boolean) getParameter(ORIG);
		Boolean dateFlag = (Boolean) getParameter(DATE_FLAG);
		Boolean productFlag = (Boolean) getParameter(PRODUCT_FLAG);
		Boolean categoryFlag = (Boolean) getParameter(CATEGORY_FLAG);
		Boolean groupFlag = (Boolean) getParameter(GROUP_FLAG);
		Boolean level = (Boolean) getParameter(LEVEL);
		Boolean distrib = (Boolean) getParameter(DISTRIB);
		Boolean sort = (Boolean) getParameter(SORT);
		String dateType = (String) getParameter(DATE_TYPE);
		String product = (String) getParameter(PRODUCT);
		String code = (String) getParameter(CODE);
		String group = (String) getParameter(GROUP);
		String currency = (String) getParameter(CURRENCY);
		String debitCreditFlag = (String) getParameter(DEBIT_CREDIT_FLAG);
		long limit = (long) getParameter(LIMIT);
		dateType = getDateType(dateType);
		groups = (List<Object>) getParameter(GROUPS);
		
		query = new OlbiusQuery(getSQLProcessor());
		
		query.from("acctg_document_list_fact");
		query.select("date_dimension.".concat(dateType), dateFlag.booleanValue());
		query.select("pd3.party_id");
		query.select("gl_account_relationship.parent_dim_id");
		query.select("currency_dimension.dimension_id", "orig_currency_uom_dim_id",  orig.booleanValue());
		query.select("currency_dimension.dimension_id", "currency_uom_dim_id", !orig.booleanValue());
		query.select("CASE WHEN DR_AMOUNT > 0 THEN 'D' ELSE 'C' END", "debit_credit_flag");
		query.select("gad2.account_code", "account_code");
		query.select("product_id", productFlag.booleanValue());
		query.select("product_name", "internal_name", productFlag.booleanValue());
		query.select("category_id", categoryFlag.booleanValue());
		query.select("category_name", categoryFlag.booleanValue());
		query.select("SUM(DR_AMOUNT + CR_AMOUNT)", "_amount", orig.booleanValue());

		query.join(Join.INNER_JOIN, "date_dimension", "TRANSACTION_DATE = date_dimension.DATE_VALUE");
		query.join(Join.INNER_JOIN, "gl_account_dimension", "gad1", "gad1.GL_ACCOUNT_ID = acctg_document_list_fact.GL_ACCOUNT_ID");
		query.join(Join.INNER_JOIN, "gl_account_relationship", "gl_account_relationship.dimension_id = gad1.dimension_id");
		query.join(Join.INNER_JOIN, "gl_account_dimension", "gad2", "gl_account_relationship.parent_dim_id = gad2.dimension_id");
        query.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.PARTY_ID = acctg_document_list_fact.ORGANIZATION_PARTY_ID");
        query.join(Join.INNER_JOIN, "party_dimension", "pd1", "pd1.PARTY_ID = acctg_document_list_fact.party_id");

        OlbiusQuery tmp = new OlbiusQuery();
		Condition condition = new Condition();
		
		tmp.from("party_group_relationship");
		tmp.select("party_group_relationship.dimension_id");
		tmp.select("party_group_relationship.parent_dim_id");
		tmp.select("from_date_dimension.date_value", "from_date");
		tmp.select("thru_date_dimension.date_value", "thru_date");
		
		tmp.join(Join.INNER_JOIN, "date_dimension", "from_date_dimension", "from_date_dim_id = from_date_dimension.dimension_id");
		tmp.join(Join.INNER_JOIN, "date_dimension", "thru_date_dimension", "thru_date_dim_id = thru_date_dimension.dimension_id");
		condition.and("party_group_relationship.relationship_type = \'DISTRIBUTION\'", !orig.booleanValue() && level.booleanValue() && !distrib.booleanValue());
		condition.and("party_group_relationship.relationship_type != 'DISTRIBUTION' OR party_group_relationship.relationship_type IS NULL" + " AND party_group_relationship.parent_dim_id != party_group_relationship.dimension_id", !orig.booleanValue() && !level.booleanValue() && !distrib.booleanValue());
		condition.and("party_group_relationship.parent_dim_id = party_group_relationship.dimension_id", !orig.booleanValue() && distrib.booleanValue());
		tmp.where(condition);		
		
		Condition conditionJoin = new Condition();
		conditionJoin.and("pd.dimension_id = group_relationship.dimension_id", orig.booleanValue());
		conditionJoin.and("pd1.party_id = group_relationship.dimension_id", !orig.booleanValue());
		conditionJoin.and("(date_dimension.date_value >= group_relationship.from_date AND (group_relationship.thru_date IS NULL OR date_dimension.date_value < group_relationship.thru_date))");
		
		query.join(Join.INNER_JOIN, tmp, "group_relationship", conditionJoin);
		
		query.join(Join.INNER_JOIN, "party_dimension", "pd3", "group_relationship.parent_dim_id = pd3.dimension_id");
		query.join(Join.INNER_JOIN, "currency_dimension", "currency_dimension.CURRENCY_ID = acctg_document_list_fact.CURRENCY_ID");
		Condition cds = Condition.makeEQ("gad2.account_code", code).andEQ("currency_dimension.currency_id", currency).andBetween("date_dimension.date_value", getSqlDate(fromDate),getSqlDate(thruDate));
		if("D".equals(debitCreditFlag)) cds = cds.and("DR_AMOUNT > 0");
                else cds = cds.and("CR_AMOUNT > 0");
		cds = cds.andEQ("pd.party_id", group, group!=null && !group.isEmpty())
                .andEQ("product_id", product,productFlag.booleanValue() && product!=null && !product.isEmpty())
                .andEQ("category_id", product,categoryFlag.booleanValue() && product!=null && !product.isEmpty())
                .andIn("pd.party_id", groups, groups!=null && !groups.isEmpty());
		query.where(cds);
		query.groupBy("date_dimension.".concat(dateType), dateFlag.booleanValue());
		query.groupBy("product_id", productFlag.booleanValue());
		query.groupBy("category_id", categoryFlag.booleanValue());
		query.groupBy("pd3.party_id");
		query.groupBy("gl_account_relationship.parent_dim_id");
		query.groupBy("currency_dimension.DIMENSION_ID");
		query.groupBy("debit_credit_flag");
		query.groupBy("gad2.account_code");
		
		query.orderBy("date_dimension.".concat(dateType), dateFlag.booleanValue());
		String sortDes;
		if (sort == null || sort.equals(true)) 
			sortDes = "ASC";
		else sortDes = "DESC";
		query.orderBy("_amount", sortDes);
		query.limit(limit);
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}

