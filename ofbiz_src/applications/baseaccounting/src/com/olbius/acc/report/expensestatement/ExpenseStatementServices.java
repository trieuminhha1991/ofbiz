package com.olbius.acc.report.expensestatement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.apache.poi.ss.usermodel.Workbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.olbius.acc.report.ColumnConfig;
import com.olbius.acc.report.DataType;
import com.olbius.acc.report.HSSFBuilder;
import com.olbius.acc.report.ReportServiceInterface;
import com.olbius.acc.report.SheetConfig;
import com.olbius.acc.report.WorkbookConfig;
import com.olbius.acc.report.dashboard.query.IndexAccountingOlapImplv2;
import com.olbius.acc.report.incomestatement.entity.Income;
import com.olbius.acc.report.incomestatement.query.IncomeOlapImpl;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.services.JqxWidgetSevices;

public class ExpenseStatementServices implements ReportServiceInterface{
	
	public static final String module = ExpenseStatementServices.class.getName();
	
	@Override
	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType = (String) context.get("dateType");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));        
		
		ExpenseOlapImpl grid = new ExpenseOlapImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
         
    	grid.putParameter(ExpenseOlapImpl.DATE_TYPE, dateType);
    	grid.putParameter(ExpenseOlapImpl.ORG_PARTY_ID, organizationPartyId);
        
    	grid.setFromDate(fromDate);
    	grid.setThruDate(thruDate);
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exportToExcel(HttpServletRequest request, HttpServletResponse response) {
		//Get data
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get Dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
				
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        Map<String, Object> getDataCtx = FastMap.newInstance();
        Map<String, Object> getDataRs = FastMap.newInstance();
		try {
			//Get parameters
			String productId = (String)parameters.get(IncomeOlapImpl.PRODUCT_ID);
			String customerId = (String)parameters.get(IncomeOlapImpl.CUSTOMER_ID);
			String categoryId = (String)parameters.get(IncomeOlapImpl.CATEGORY_ID);
			String dataType = (String)parameters.get(IncomeOlapImpl.DATE_TYPE);
			Timestamp fromDate = (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("fromDate"));
			Timestamp thruDate = (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("thruDate"));
			//Create Invoice
			getDataCtx.put("productId", productId);
			getDataCtx.put("categoryId", categoryId);
			getDataCtx.put("partyId", customerId);
			getDataCtx.put("dateType", dataType);
			getDataCtx.put("userLogin", userLogin);
			getDataCtx.put("fromDate", fromDate);
			getDataCtx.put("thruDate", thruDate);
			getDataRs = dispatcher.runSync("getIncomeStatement", getDataCtx);
		} catch (ParseException | GenericServiceException e1) {
			Debug.log(e1.getMessage(), module);
		}
		
		//Get Labels
		Locale locale = UtilHttp.getLocale(request);
		List<Map<String, Object>> data = (List<Map<String, Object>>) getDataRs.get("data");
		List<String> datafields = (List<String>) getDataRs.get("datafields");
		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		List<SheetConfig> sheetConfigs = new ArrayList<SheetConfig>();
		for(String field: datafields) {
			ColumnConfig columnConfig = new ColumnConfig();
			switch (field) {
				case Income.CATEGORY_ID:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCCategoryName", locale));
					columnConfig.setDataType(DataType.STRING);
					break;
				case Income.PARTY_ID:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCCustomerId", locale));
					columnConfig.setDataType(DataType.STRING);
					break;
				case Income.PRODUCT_ID:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCProductName", locale));
					columnConfig.setDataType(DataType.STRING);
					break;
				case Income.TRANS_TIME:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCTransactionTime", locale));
					columnConfig.setDataType(DataType.STRING);
					break;
				case Income.SALE_DISCOUNT:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCSaleDiscount", locale));
					columnConfig.setDataType(DataType.NUMBERIC);
					break;
				case Income.PROMOTION:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCPromotion", locale));
					columnConfig.setDataType(DataType.NUMBERIC);
					break;
				case Income.NET_REVENUE:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCNetRevenue", locale));
					columnConfig.setDataType(DataType.NUMBERIC);
					break;
				case Income.COGS:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCCOGS", locale));
					columnConfig.setDataType(DataType.NUMBERIC);
					break;
				case Income.SALE_RETURN:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCSaleReturn", locale));
					columnConfig.setDataType(DataType.NUMBERIC);
					break;
				case Income.GROSS_PROFIT:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCGrossProfit", locale));
					columnConfig.setDataType(DataType.NUMBERIC);
					break;
				case Income.SALE_INCOME:
					columnConfig.setHeader(UtilProperties.getMessage(resource, "BACCSaleIncome", locale));
					columnConfig.setDataType(DataType.NUMBERIC);
					break;
				default:
					break;
			}
			columnConfig.setName(field);
			columnConfigs.add(columnConfig);
		}
		SheetConfig sheetConfig = new SheetConfig();
		sheetConfig.setColumnConfig(columnConfigs);
		sheetConfig.setTitle(UtilProperties.getMessage(resource, "BACCIncomeStatement", locale));
		sheetConfig.setDataConfig(data);
		sheetConfig.setSheetName("incomestatement");
		WorkbookConfig config = new WorkbookConfig();
		sheetConfigs.add(sheetConfig);
		config.setSheetConfigs(sheetConfigs);
		Workbook wb = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb = new HSSFBuilder().build(sheetConfigs);
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=incomestatement.xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (Exception e) {
			Debug.log(e.getMessage(), module);
		} finally {
			if(baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					Debug.log(e.getMessage(), module);
				}
		}
	}

	@Override
	public void exportToPdf(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}
	
}
