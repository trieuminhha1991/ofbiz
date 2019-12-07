package com.olbius.acc.report.dashboard.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.apache.poi.ss.usermodel.Workbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.report.ColumnConfig;
import com.olbius.acc.report.DataType;
import com.olbius.acc.report.HSSFBuilder;
import com.olbius.acc.report.ReportServiceInterface;
import com.olbius.acc.report.SheetConfig;
import com.olbius.acc.report.WorkbookConfig;
import com.olbius.acc.report.dashboard.entity.Index;
import com.olbius.acc.report.dashboard.query.IndexAccountingOlapImpl;
import com.olbius.acc.report.dashboard.query.IndexAccountingOlapImplv2;
import com.olbius.acc.report.incomestatement.entity.Income;
import com.olbius.acc.report.incomestatement.query.IncomeOlapImpl;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.report.ResultValueTotal;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.services.JqxWidgetSevices;

public class IndexAccountingServices implements ReportServiceInterface{
	
	public final static String module = IndexAccountingServices.class.getName();
	
	@Override
	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		List<Index> results = new ArrayList<Index>();
		List<Index> listAcctgTrans = new ArrayList<Index>();
		List<Map<String, Object>> dataAdapter = new ArrayList<Map<String,Object>>();
		List<String> dataFields = new ArrayList<String>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = runQuery(dctx, context);
		for(Map<String, Object> item : data) {
			Index index = new Index();
			BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(item.get("amount").toString()));
			switch (item.get("accountCode").toString()) {
				case "511":
					index.setSaleExtIncome(amount);
					break;
				case "521":
					index.setSaleDiscount(amount);
					break;
				case "632":
					index.setCogs(amount);
					break;
				default:
					break;
			}
			listAcctgTrans.add(index);
		}
		Index tmpResult  = new Index();
		for(Index item: listAcctgTrans) {
			
			if(item.getCogs().compareTo(BigDecimal.ZERO) != 0) {
				tmpResult.setCogs(item.getCogs());
			}
			if(item.getOther().compareTo(BigDecimal.ZERO) != 0) {
				tmpResult.setOther(item.getOther());
			}
			if(item.getPromotion().compareTo(BigDecimal.ZERO) != 0) {
				tmpResult.setPromotion(item.getPromotion());
			}
			if(item.getSaleDiscount().compareTo(BigDecimal.ZERO) != 0) {
				tmpResult.setSaleDiscount(item.getSaleDiscount());
			}
			if(item.getSaleExtIncome().compareTo(BigDecimal.ZERO) != 0) {
				tmpResult.setSaleExtIncome(item.getSaleExtIncome());
			}
			if(item.getSaleIntIncome().compareTo(BigDecimal.ZERO) != 0) {
				tmpResult.setSaleIntIncome(item.getSaleIntIncome());
			}			
		}		
		
		Map<String, Object> object = new HashMap<String, Object>();
		object.put(Index.COGS, tmpResult.getCogs());
		object.put(Index.GROSS_PROFIT, tmpResult.getGrossProfit());
		object.put(Index.NET_REVENUE, tmpResult.getNetRevenue());
		object.put(Index.SALE_INCOME, tmpResult.getSaleIncome());
		object.put(Index.DEDUCTIONS, tmpResult.getRevenueDeduction());
		dataAdapter.add(object);
		
		result.put("listValue", dataAdapter);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	protected List<Map<String, Object>> runQuery(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));      		
		IndexAccountingOlapImpl indexAccResult = new IndexAccountingOlapImpl(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organizationPartyId, delegator);
		
		List<Map<String, Object>> data = indexAccResult.getObject();
		return data;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateIndexValue(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");		
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		IndexAccountingOlapImplv2 grid = new IndexAccountingOlapImplv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter(IndexAccountingOlapImplv2.ORG, organization);
 
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void exportToExcel(HttpServletRequest request, HttpServletResponse response) {
	}

	@Override
	public void exportToPdf(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
	}
}
