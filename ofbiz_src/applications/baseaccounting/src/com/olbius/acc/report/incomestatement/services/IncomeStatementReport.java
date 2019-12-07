package com.olbius.acc.report.incomestatement.services;

import com.olbius.acc.report.incomestatement.entity.IncomeConst;
import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.services.OlbiusOlapService;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class IncomeStatementReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private ReturnResultGridIncomeStatement result = new ReturnResultGridIncomeStatement();

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", context.get("dateType"));
		putParameter("category", context.get("category[]"));
		putParameter("product", context.get("product[]"));
		putParameter("productStore", context.get("productStore[]"));
		putParameter("party", context.get("party[]"));
		putParameter("reportType", context.get("reportType"));
		putParameter("userLogin", context.get("userLogin"));
	}
	
	protected List<Object> getGlAccount(){
		List<Object> listAccountCode = FastList.newInstance();
		Account acc = null;
		List<Account> listAccount = null;
		acc = AccountBuilder.buildAccount("632", delegator);
		listAccount = acc.getListChild();
		for(Account item : listAccount) {
			AccountEntity accEntity = item.getAcc();
		 	//if(accEntity.isLeaf()) {
		 		listAccountCode.add(accEntity.getGlAccountId());
		 	//}
		}
		listAccountCode.add("632");
		acc = AccountBuilder.buildAccount("511", delegator);
		listAccount = acc.getListChild();
		for(Account item : listAccount) {
			AccountEntity accEntity = item.getAcc();
		 	//if(accEntity.isLeaf()) {
		 		listAccountCode.add(accEntity.getGlAccountId());
		 	//}
		}
		listAccountCode.add("511");
		acc = AccountBuilder.buildAccount("521", delegator);
		listAccount = acc.getListChild();
		for(Account item : listAccount) {
			AccountEntity accEntity = item.getAcc();
		 	//if(accEntity.isLeaf()) {
		 		listAccountCode.add(accEntity.getGlAccountId());
		 	//}
		}
		listAccountCode.add("521");
		return listAccountCode;
	}	
	
	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		OlbiusQuery query = makeQuery();
		OlbiusQuery queryTmp = makeQuery();
		
		List<Object> parties = (List<Object>) getParameter("party");
		List<Object> products = (List<Object>) getParameter("product");
		List<Object> categories = (List<Object>) getParameter("category");
		List<Object> productStores = (List<Object>) getParameter("productStore");
		String dateType = getDateType((String) getParameter("dateType"));
		String reportType = (String) getParameter("reportType");
		GenericValue userLogin = (GenericValue) getParameter("userLogin");
		
		switch (dateType) {
		case "year_name":
			queryTmp.select("year_name", "transTime");
			break;
		case "quarter_and_year":
			queryTmp.select("quarter_and_year", "transTime");
			break;
		case "year_and_month":
			queryTmp.select("year_and_month", "transTime");
			break;
		case "week_and_year":
			queryTmp.select("week_and_year", "transTime");
			break;
		case "year_month_day":
			queryTmp.select("transaction_date", "transTime");
			break;
		default:
			break;
		}
		
		switch (reportType) {
		case "general":
			queryTmp.select("party_id", "partyId")
				.select("party_code", "partyCode")
				.select("party_name", "fullName")
				.select("product_id", "productId")
				.select("product_code", "productCode")
				.select("product_name", "productName")
				.select("category_id", "categoryId")
				.select("category_name", "categoryName")
				.select("product_store_id", "productStoreId")
				.select("product_store_name", "productStoreName");
			break;
		case "product":
			queryTmp.select("product_id", "productId")
				.select("product_code", "productCode")
				.select("product_name", "productName");
			break;
		case "party":
			queryTmp.select("party_id", "partyId")
				.select("party_code", "partyCode")
				.select("party_name", "fullName");
			break;
		case "category":
			queryTmp.select("category_id", "categoryId")
				.select("category_name", "categoryName");
			break;
		case "productStore":
			queryTmp.select("product_store_id", "productStoreId")
				.select("product_store_name", "productStoreName");
			break;			
		default:
			break;
		}
		
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Condition cond = new Condition();
		cond.andIn("party_id", parties, parties != null).andIn("product_id", products, products != null)
			.andIn("product_store_id", productStores, productStores != null).andIn("category_id", categories, categories != null)
			.andIn("gl_account_id", getGlAccount()).andBetween("transaction_date", getSqlDate(fromDate), getSqlDate(thruDate))
			.andEQ("organization_party_id", organizationPartyId).andNotEQ("acctg_trans_type_id", "CLOSING_ENTRY");
		
		queryTmp.select("sum(case when account_code like '511%' then (cr_amount - dr_amount) else 0 end)", IncomeConst.SALE_INCOME)
			.select("sum(case when account_code like '5211%' then (dr_amount - cr_amount) else 0 end)", IncomeConst.SALE_DISCOUNT)
			.select("sum(case when account_code like '5212%' then (dr_amount - cr_amount) else 0 end)", IncomeConst.SALE_RETURN)
			.select("sum(case when account_code like '5213%' then (dr_amount - cr_amount) else 0 end)", IncomeConst.PROMOTION)
			.select("sum(case when account_code like '632%' then (dr_amount - cr_amount) else 0 end)", IncomeConst.COGS)
			.from("acctg_document_list_fact").where(cond).groupBy("transTime").orderBy("transTime", OlbiusQuery.DESC);
		
		switch (reportType) {
		case "general":
			queryTmp.groupBy("partyId")
				.groupBy("partyCode")
				.groupBy("fullName")
				.groupBy("productId")
				.groupBy("productCode")
				.groupBy("productName")
				.groupBy("categoryId")
				.groupBy("categoryName")
				.groupBy("productStoreId")
				.groupBy("productStoreName");
			break;
		case "product":
			queryTmp.groupBy("productId")
				.groupBy("productCode")
				.groupBy("productName");
			break;
		case "party":
			queryTmp.groupBy("partyId")
				.groupBy("partyCode")
				.groupBy("fullName");
			break;
		case "category":
			queryTmp.groupBy("categoryId")
				.groupBy("categoryName");
			break;
		case "productStore":
			queryTmp.groupBy("productStoreId")
				.groupBy("productStoreName");
			break;			
		default:
			break;
		}
		
		String aliasSubQuery = "tmp";
		String netValueCol = aliasSubQuery + "." + IncomeConst.SALE_INCOME + " - " + aliasSubQuery + "."
				+ IncomeConst.PROMOTION + " - " + aliasSubQuery + "." + IncomeConst.SALE_RETURN
				+ " - " + aliasSubQuery + "." + IncomeConst.SALE_DISCOUNT;
		String grossProfitCol = netValueCol + " - " + aliasSubQuery + "." + IncomeConst.COGS;
		query.select(aliasSubQuery + ".*").select(netValueCol, IncomeConst.NET_REVENUE).select(grossProfitCol, IncomeConst.GROSS_PROFIT)
			.from(queryTmp, aliasSubQuery);
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String reportType = (String) getParameter("reportType");
		switch (reportType) {
		case "general":
			addDataField("partyId", "partyId");
			addDataField("productId", "productId");
			addDataField("categoryId","categoryId");
			addDataField("categoryName","categoryName");
			addDataField("productCode","productCode");
			addDataField("partyCode","partyCode");
			addDataField("productName","productName");
			addDataField("fullName","fullName");
			addDataField("productStoreId", "productStoreId");
			addDataField("productStoreName","productStoreName");			
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
			addDataField("categoryName","categoryName");
			break;
		case "productStore":
			addDataField("productStoreId", "productStoreId");
			addDataField("productStoreName","productStoreName");
			break;				
		default:
			break;
		}
		addDataField(IncomeConst.SALE_INCOME, IncomeConst.SALE_INCOME);
		addDataField(IncomeConst.SALE_DISCOUNT, IncomeConst.SALE_DISCOUNT);
		addDataField(IncomeConst.SALE_RETURN, IncomeConst.SALE_RETURN);
		addDataField(IncomeConst.PROMOTION, IncomeConst.PROMOTION);
		addDataField(IncomeConst.COGS, IncomeConst.COGS);
		addDataField(IncomeConst.NET_REVENUE, IncomeConst.NET_REVENUE);
		addDataField(IncomeConst.GROSS_PROFIT, IncomeConst.GROSS_PROFIT);
		addDataField("transTime", "transTime");
	}

	@Override
	protected OlapResultQueryInterface returnResultGrid() {
		return result;
	}

	private class ReturnResultGridIncomeStatement extends ReturnResultGridEx {
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = super.getObject(result);
			try {
				String reportType = (String) getParameter("reportType");
				switch (reportType) {
				case "general":
					map.put("partyId", result.getString("partyId"));
					map.put("productId", result.getString("productId"));
					map.put("categoryId", result.getString("categoryId"));
					map.put("categoryName", result.getString("categoryName"));
					map.put("productCode", result.getString("productCode"));
					map.put("partyCode", result.getString("partyCode"));
					map.put("productName", result.getString("productName"));
					map.put("fullName", result.getString("fullName"));
					map.put("productStoreId", result.getString("productStoreId"));
					map.put("productStoreName", result.getString("productStoreName"));					
					break;
				case "product":
					map.put("productId", result.getString("productId"));
					map.put("productCode", result.getString("productCode"));
					map.put("productName", result.getString("productName"));
					break;
				case "party":
					map.put("partyId", result.getString("partyId"));
					map.put("partyCode", result.getString("partyCode"));
					map.put("fullName", result.getString("fullName"));
					break;
				case "category":
					map.put("categoryId", result.getString("categoryId"));
					map.put("categoryName", result.getString("categoryName"));
					break;
				case "productStore":
					map.put("productStoreId", result.getString("productStoreId"));
					map.put("productStoreName", result.getString("productStoreName"));
					break;						
				default:
					break;
				}
				
				map.put(IncomeConst.SALE_INCOME, result.getBigDecimal(IncomeConst.SALE_INCOME));
				map.put(IncomeConst.SALE_DISCOUNT, result.getBigDecimal(IncomeConst.SALE_DISCOUNT));
				map.put(IncomeConst.SALE_RETURN, result.getBigDecimal(IncomeConst.SALE_RETURN));
				map.put(IncomeConst.PROMOTION, result.getBigDecimal(IncomeConst.PROMOTION));
				map.put(IncomeConst.COGS, result.getBigDecimal(IncomeConst.COGS));
				map.put(IncomeConst.NET_REVENUE, result.getBigDecimal(IncomeConst.NET_REVENUE));
				map.put(IncomeConst.GROSS_PROFIT, result.getBigDecimal(IncomeConst.GROSS_PROFIT));
				
				String transTime = "";
				if(result.getObject("transTime") != null && map.get("transTime") instanceof String){
					String[] tmp = ((String)map.get("transTime")).split("-");
					if(tmp != null) {
						for(int i = tmp.length -1; i >= 0; i--) {
							transTime += tmp[i];
							transTime += "/";
						}
						transTime = transTime.substring(0, transTime.length()-1);
					}
					map.put("transTime", transTime);
				} else if(result.getObject("transTime") != null && map.get("transTime") instanceof Date)
				{
					map.put("transTime", map.get("transTime"));
				}
				else map.put("transTime", transTime);
							
			} catch (Exception e) {
				Debug.logError(e, getModule());
			}
			
			return map;
		}
	}
}