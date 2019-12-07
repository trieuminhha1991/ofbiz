package com.olbius.acc.report.incomestatement.services;

import com.olbius.acc.report.ReportServiceInterface;
import com.olbius.acc.report.incomestatement.entity.Income;
import com.olbius.acc.report.incomestatement.query.IncomeOlapConstant;
import com.olbius.acc.report.incomestatement.query.IncomeOlapImpl;
import com.olbius.acc.report.incomestatement.query.ProductIncomeChartImpl;
import com.olbius.acc.report.incomestatement.query.ProductIncomeOlapImpl;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.grid.OlapGrid;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

public class ProductIncomeStmServices implements ReportServiceInterface {

	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> runQuery(DispatchContext dctx, Map<String, Object> context, String dimension){
		Delegator delegator = dctx.getDelegator();
		
		ProductIncomeOlapImpl incomeOlap = new ProductIncomeOlapImpl();
		incomeOlap.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType = (String) context.get(IncomeOlapImpl.DATATYPE);
        String productId = (String) context.get(IncomeOlapImpl.PRODUCT_ID);
        String hasTime = (String) context.get(IncomeOlapImpl.HAS_TIME);
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        
        incomeOlap.putParameter(IncomeOlapImpl.DATATYPE, dateType);
        incomeOlap.putParameter(IncomeOlapImpl.PRODUCT_ID, productId);
        incomeOlap.putParameter(IncomeOlapImpl.HAS_TIME, hasTime);
        incomeOlap.putParameter(IncomeOlapImpl.ORG_PARTY_ID, organizationPartyId);
        
        incomeOlap.setFromDate(fromDate);
        incomeOlap.setThruDate(thruDate);
        //incomeOlap.setHasTransTime(hasTime);
        
		//Set limit is null
        context.put("limit", 0l);
		
        Map<String, Object> incomeStatResult = incomeOlap.execute(context);
		List<Map<String, Object>> data = (List<Map<String, Object>>)incomeStatResult.get("data");
		return data;
	}
	
	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		List<Map<String, Object>> dataAdapter = new ArrayList<Map<String,Object>>();
		List<String> dataFields = new ArrayList<String>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = runQuery(dctx, context, "product");
		
		if(UtilValidate.isNotEmpty(data)){
			for(Map<String,Object> d : data){
				Map<String, Object> object = new HashMap<String, Object>();
				object.put(Income.COGS, d.get("amount632"));
				BigDecimal netRevenue = ((BigDecimal) d.get("amount511")).subtract((BigDecimal)d.get("amount5213")).subtract((BigDecimal) d.get("amount5212")).subtract((BigDecimal) d.get("amount5211"));
				object.put(Income.NET_REVENUE, netRevenue );
				BigDecimal grossProfit = (netRevenue != null ? netRevenue : BigDecimal.ZERO).subtract((BigDecimal)  d.get("amount632"));
				object.put(Income.GROSS_PROFIT, grossProfit);
				object.put(Income.SALE_RETURN,(BigDecimal) d.get("amount5212"));
				object.put(Income.PROMOTION,(BigDecimal)  d.get("amount5213"));
				object.put(Income.SALE_DISCOUNT, d.get("amount5211"));
				object.put(Income.SALE_INCOME, (BigDecimal) d.get("amount511"));
				//object.put(Income.TRANS_TIME, d.get("transTime"));
				object.put(Income.PRODUCT_ID, d.get("productId"));
				object.put("productCode", d.get("productCode"));
				object.put("productName", d.get("productName"));
				dataAdapter.add(object);
			}
		}
		
		dataFields.add(Income.PRODUCT_ID);
		//dataFields.add(Income.TRANS_TIME);
		dataFields.add(Income.SALE_INCOME);
		dataFields.add(Income.SALE_DISCOUNT);
		dataFields.add(Income.PROMOTION);
		dataFields.add(Income.SALE_RETURN);
		dataFields.add(Income.NET_REVENUE);
		dataFields.add(Income.COGS);
		dataFields.add(Income.GROSS_PROFIT);
		dataFields.add("productCode");
		dataFields.add("productName");
		result.put("data", dataAdapter);
		result.put("datafields", dataFields);
		result.put("totalsize", dataAdapter.size());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateProductIncomePieChart(DispatchContext ctx, Map<String, ? extends Object> context)throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		
		ProductIncomeChartImpl chart = new ProductIncomeChartImpl(delegator,"product");
		chart.setDelegator(delegator);
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        
        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
        chart.putParameter(IncomeOlapConstant.ORG_PARTY_ID, organizationPartyId);
		
//        ProductIncomeQuery query = chart.new ProductIncomeQuery();
//        ProductIncomePie pieResult = chart.new ProductIncomePie(chart, query);
		
        chart.setOlapResultType(OlapPieChart.class);
//		chart.setOlapResult(pieResult);
		Map<String, Object> result = chart.execute(context);
        
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
