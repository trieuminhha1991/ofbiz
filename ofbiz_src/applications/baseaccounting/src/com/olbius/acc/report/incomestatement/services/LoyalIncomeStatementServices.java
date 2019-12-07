package com.olbius.acc.report.incomestatement.services;

import com.olbius.acc.report.ReportServiceInterface;
import com.olbius.acc.report.incomestatement.entity.Income;
import com.olbius.acc.report.incomestatement.entity.LoyalIncome;
import com.olbius.acc.report.incomestatement.query.IncomeOlapImpl;
import com.olbius.acc.report.incomestatement.query.LoyalIncomeOlapImpl;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

public class LoyalIncomeStatementServices implements ReportServiceInterface {
	protected List<Map<String, Object>> runQuery(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		
		LoyalIncomeOlapImpl incomeOlap = new LoyalIncomeOlapImpl(delegator);
		incomeOlap.setDelegator(delegator);
		
		ReturnResultGrid incomeStatResult = incomeOlap.new LoyalIncomeStatementResult();
		/*incomeOlap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));*/
		OlapGrid gird = new OlapGrid(incomeOlap, incomeStatResult);
		incomeOlap.setOlapResult(gird);
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType = (String) context.get(IncomeOlapImpl.DATATYPE);
        String groupId = (String) context.get(IncomeOlapImpl.GROUP_ID);
        String categoryId = (String) context.get(IncomeOlapImpl.CATEGORY_ID);
        String productId = (String) context.get(IncomeOlapImpl.PRODUCT_ID);
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
                
        incomeOlap.putParameter(IncomeOlapImpl.DATATYPE, dateType);
        incomeOlap.putParameter(IncomeOlapImpl.CATEGORY_ID, categoryId);
        incomeOlap.putParameter(IncomeOlapImpl.GROUP_ID, groupId);
        incomeOlap.putParameter(IncomeOlapImpl.PRODUCT_ID, productId);
        incomeOlap.putParameter(IncomeOlapImpl.ORG_PARTY_ID, organizationPartyId);
        
        incomeOlap.setFromDate(fromDate);
        incomeOlap.setThruDate(thruDate);
		
        //Set limit is null
        context.put("limit", -1l);
        
		incomeOlap.execute(context);
		List<Map<String, Object>> data = incomeStatResult.getData();
		return data;
	}
	
	@Override
	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		List<LoyalIncome> results = new ArrayList<LoyalIncome>();
		List<LoyalIncome> listAcctgTrans = new ArrayList<LoyalIncome>();
		List<Map<String, Object>> dataAdapter = new ArrayList<Map<String,Object>>();
		List<String> dataFields = new ArrayList<String>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = runQuery(dctx, context);
		for(Map<String, Object> item : data) {
			LoyalIncome income = new LoyalIncome();
			BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(item.get("amount").toString()));
			switch (item.get("glAccountId").toString()) {
				case "51111":
					income.setSaleExtIncome(amount);
					break;
				case "51112":
					income.setSaleIntIncome(amount);
					break;
				case "5211":
					income.setSaleDiscount(amount);
					break;
				case "5213":
					income.setPromotion(amount);
					break;
				case "5212":
					income.setSaleReturn(amount);
					break;
				case "632":
					income.setCogs(amount);
					break;
				default:
					break;
			}
			if (UtilValidate.isNotEmpty(item.get("productId")))
			income.setProductId(item.get("productId").toString());
			else income.setProductId("");
			income.setCategoryId(item.get("categoryId").toString());
			income.setGroupId(item.get("groupId").toString());
			income.setTransTime(item.get("transTime").toString());
			listAcctgTrans.add(income);
		}
		for(LoyalIncome item: listAcctgTrans) {
			boolean isExists = false;
			LoyalIncome tmpJ  = item;
			for(LoyalIncome income : results) {
				LoyalIncome tmpI  = income;
				if(tmpI.getProductId().equals(tmpJ.getProductId()) 
					&& tmpI.getCategoryId().equals(tmpJ.getCategoryId()) 
					&& tmpI.getGroupId().equals(tmpJ.getGroupId()) 
					&& tmpJ.getTransTime().equals(tmpI.getTransTime())) {
					
					if(tmpJ.getCogs().compareTo(BigDecimal.ZERO) != 0) {
						tmpI.setCogs(tmpJ.getCogs());
					}
					if(tmpJ.getSaleReturn().compareTo(BigDecimal.ZERO) != 0) {
						tmpI.setSaleReturn(tmpJ.getSaleReturn());
					}
					if(tmpJ.getPromotion().compareTo(BigDecimal.ZERO) != 0) {
						tmpI.setPromotion(tmpJ.getPromotion());
					}
					if(tmpJ.getSaleDiscount().compareTo(BigDecimal.ZERO) != 0) {
						tmpI.setSaleDiscount(tmpJ.getSaleDiscount());
					}
					if(tmpJ.getSaleIntIncome().compareTo(BigDecimal.ZERO) != 0) {
						tmpI.setSaleIntIncome(tmpJ.getSaleIntIncome());
					}
					if(tmpJ.getSaleExtIncome().compareTo(BigDecimal.ZERO) != 0) {
						tmpI.setSaleExtIncome(tmpJ.getSaleExtIncome());
					}							
					isExists = true;
				}
			}
			if(!isExists) {
				results.add(tmpJ);
			}
		}
		
		for(LoyalIncome income : results) {
			Map<String, Object> object = new HashMap<String, Object>();
			object.put(Income.COGS, income.getCogs());
			object.put(Income.GROSS_PROFIT, income.getGrossProfit());
			object.put(Income.NET_REVENUE, income.getNetRevenue());
			object.put(Income.SALE_RETURN, income.getSaleReturn());
			object.put(Income.PROMOTION, income.getPromotion());
			object.put(Income.SALE_DISCOUNT, income.getSaleDiscount());
			object.put(Income.SALE_INCOME, income.getSaleIncome());
			object.put(Income.TRANS_TIME, income.getTransTime());
			object.put(Income.CATEGORY_ID, income.getCategoryId());
			object.put(Income.PRODUCT_ID, income.getProductId());
			object.put(Income.GROUP_ID, income.getGroupId());
			dataAdapter.add(object);
		}
		dataFields.add(Income.PRODUCT_ID);
		dataFields.add(Income.CATEGORY_ID);
		dataFields.add(Income.GROUP_ID);
		dataFields.add(Income.TRANS_TIME);
		dataFields.add(Income.SALE_INCOME);
		dataFields.add(Income.SALE_DISCOUNT);
		dataFields.add(Income.PROMOTION);
		dataFields.add(Income.SALE_RETURN);
		dataFields.add(Income.NET_REVENUE);
		dataFields.add(Income.COGS);
		dataFields.add(Income.GROSS_PROFIT);
		result.put("data", dataAdapter);
		result.put("datafields", dataFields);
		result.put("totalsize", dataAdapter.size());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	@Override
	public void exportToExcel(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportToPdf(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}
}
