package com.olbius.acc.report.incomestatement.services;

import com.olbius.acc.report.ReportServiceInterface;
import com.olbius.acc.report.incomestatement.entity.Income;
import com.olbius.acc.report.incomestatement.query.IncomeOlapImpl;
import com.olbius.acc.report.incomestatement.query.IncomeOlapImpl1;
import com.olbius.acc.utils.ExcelUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.services.JqxWidgetSevices;
import javolution.util.FastMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public class IncomeStatementServices implements ReportServiceInterface {
	
	public final static String module = IncomeStatementServices.class.getName();
	
	@Override
//	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
//		List<Income> results = new ArrayList<Income>();
//		List<Income> listAcctgTrans = new ArrayList<Income>();
//		List<Map<String, Object>> dataAdapter = new ArrayList<Map<String,Object>>();
//		List<String> dataFields = new ArrayList<String>();
//		Map<String, Object> result = new HashMap<String, Object>();
//		List<Map<String, Object>> data = runQuery(dctx, context);
//		try {
//			for(Map<String, Object> item : data) {
//				Income income = new Income();
//				BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(item.get("amount").toString()));
//				switch (item.get("glAccountId").toString()) {
//					case "51111":
//						income.setSaleExtIncome(amount);
//						break;
//					case "51112":
//						income.setSaleIntIncome(amount);
//						break;
//					case "5211":
//						income.setSaleDiscount(amount);
//						break;
//					case "5213":
//						income.setPromotion(amount);
//						break;
//					case "5212":
//						income.setSaleReturn(amount);
//						break;
//					case "632":
//						income.setCogs(amount);
//						break;
//					default:
//						break;
//				}
//				if (UtilValidate.isNotEmpty(item.get("productId")))
//					income.setProductId(item.get("productId").toString());
//				else income.setProductId("");				
//				income.setCategoryId(item.get("categoryId").toString());
//				if (UtilValidate.isNotEmpty(item.get("partyId")))
//					income.setPartyId(item.get("partyId").toString());
//				else income.setPartyId("");				
//				income.setTransTime(item.get("transTime").toString());
//				listAcctgTrans.add(income);
//			}
//		} catch (Exception e) {
//			Debug.logError("error : " + e.getMessage(), module);
//		}
//		
//		for(Income item: listAcctgTrans) {
//			boolean isExists = false;
//			Income tmpJ  = item;
//			for(Income income : results) {
//				Income tmpI  = income;
//				if(tmpI.getProductId().equals(tmpJ.getProductId()) 
//					&& tmpI.getCategoryId().equals(tmpJ.getCategoryId()) 
//					&& tmpI.getPartyId().equals(tmpJ.getPartyId()) 
//					&& tmpJ.getTransTime().equals(tmpI.getTransTime())) {
//					
//					if(tmpJ.getCogs().compareTo(BigDecimal.ZERO) != 0) {
//						tmpI.setCogs(tmpJ.getCogs());
//					}
//					if(tmpJ.getSaleReturn().compareTo(BigDecimal.ZERO) != 0) {
//						tmpI.setSaleReturn(tmpJ.getSaleReturn());
//					}
//					if(tmpJ.getPromotion().compareTo(BigDecimal.ZERO) != 0) {
//						tmpI.setPromotion(tmpJ.getPromotion());
//					}
//					if(tmpJ.getSaleDiscount().compareTo(BigDecimal.ZERO) != 0) {
//						tmpI.setSaleDiscount(tmpJ.getSaleDiscount());
//					}
//					if(tmpJ.getSaleIntIncome().compareTo(BigDecimal.ZERO) != 0) {
//						tmpI.setSaleIntIncome(tmpJ.getSaleIntIncome());
//					}
//					if(tmpJ.getSaleExtIncome().compareTo(BigDecimal.ZERO) != 0) {
//						tmpI.setSaleExtIncome(tmpJ.getSaleExtIncome());
//					}							
//					isExists = true;
//				}
//			}
//			if(!isExists) {
//				results.add(tmpJ);
//			}
//		}
//		
//		for(Income income : results) {
//			Map<String, Object> object = new HashMap<String, Object>();
//			object.put(Income.COGS, income.getCogs());
//			object.put(Income.GROSS_PROFIT, income.getGrossProfit());
//			object.put(Income.NET_REVENUE, income.getNetRevenue());
//			object.put(Income.SALE_RETURN, income.getSaleReturn());
//			object.put(Income.PROMOTION, income.getPromotion());
//			object.put(Income.SALE_DISCOUNT, income.getSaleDiscount());
//			object.put(Income.SALE_INCOME, income.getSaleIncome());
//			object.put(Income.TRANS_TIME, income.getTransTime());
//			object.put(Income.CATEGORY_ID, income.getCategoryId());
//			object.put(Income.PRODUCT_ID, income.getProductId());
//			object.put(Income.PARTY_ID, income.getPartyId());
//			dataAdapter.add(object);
//		}
//		dataFields.add(Income.PRODUCT_ID);
//		dataFields.add(Income.CATEGORY_ID);
//		dataFields.add(Income.PARTY_ID);
//		dataFields.add(Income.TRANS_TIME);
//		dataFields.add(Income.SALE_INCOME);
//		dataFields.add(Income.SALE_DISCOUNT);
//		dataFields.add(Income.PROMOTION);
//		dataFields.add(Income.SALE_RETURN);
//		dataFields.add(Income.NET_REVENUE);
//		dataFields.add(Income.COGS);
//		dataFields.add(Income.GROSS_PROFIT);
//		result.put("data", dataAdapter);
//		result.put("datafields", dataFields);
//		result.put("totalsize", dataAdapter.size());
//		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
//		return result;
//	}
	
	
	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = runQuery(dctx, context, "general");
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	protected Map<String, Object> runQuery(DispatchContext dctx, Map<String, Object> context, String dimension){
		Delegator delegator = dctx.getDelegator();
		
		IncomeOlapImpl1 incomeOlap = new IncomeOlapImpl1(delegator,dimension);
		incomeOlap.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType = (String) context.get(IncomeOlapImpl.DATATYPE);
        String customerId = (String) context.get(IncomeOlapImpl.CUSTOMER_ID);
        String categoryId = (String) context.get(IncomeOlapImpl.CATEGORY_ID);
        String productId = (String) context.get(IncomeOlapImpl.PRODUCT_ID);
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        
        incomeOlap.putParameter(IncomeOlapImpl.DATATYPE, dateType);
        incomeOlap.putParameter(IncomeOlapImpl.CATEGORY_ID, categoryId);
        incomeOlap.putParameter(IncomeOlapImpl.CUSTOMER_ID, customerId);
        incomeOlap.putParameter(IncomeOlapImpl.PRODUCT_ID, productId);
        incomeOlap.putParameter(IncomeOlapImpl.ORG_PARTY_ID, organizationPartyId);
        //incomeOlap.putParameter(IncomeOlapImpl.ORG_PARTY_ID, organizationPartyId);
        incomeOlap.setFromDate(fromDate);
        incomeOlap.setThruDate(thruDate);
		
        Map<String, Object> incomeStatResult = incomeOlap.execute(context);
		return incomeStatResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exportToExcel(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        Map<String, Object> getDataCtx = FastMap.newInstance();
        Map<String, Object> getDataRs = FastMap.newInstance();
        String productId = (String)parameters.get(IncomeOlapImpl.PRODUCT_ID);
        String customerId = (String)parameters.get(IncomeOlapImpl.CUSTOMER_ID);
        String categoryId = (String)parameters.get(IncomeOlapImpl.CATEGORY_ID);
        String dataType = (String)parameters.get(IncomeOlapImpl.DATATYPE);
        Timestamp fromDate = null, thruDate = null;
        try {
        	fromDate = (Timestamp) JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("fromDate"));
			thruDate = (Timestamp) JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("thruDate"));
		} catch (ParseException e2) {
			e2.printStackTrace();
			System.err.println("parse from date and thru date error in exportToExcel method of " + module);
			fromDate = UtilDateTime.nowTimestamp();
			thruDate = UtilDateTime.nowTimestamp();
		}
		try {
			getDataCtx.put("productId", productId);
			getDataCtx.put("categoryId", categoryId);
			getDataCtx.put("partyId", customerId);
			getDataCtx.put("dateType", dataType);
			getDataCtx.put("userLogin", userLogin);
			getDataCtx.put("fromDate", fromDate);
			getDataCtx.put("thruDate", thruDate);
			getDataCtx.put("limit", 0l);
			getDataCtx.put("offset", 0l);
			getDataCtx.put("init", Boolean.TRUE);
			getDataRs = dispatcher.runSync("getIncomeStatement", getDataCtx);
		} catch (GenericServiceException e1) {
			Debug.log(e1.getMessage(), module);
		}
		
		Locale locale = UtilHttp.getLocale(request);
		List<Map<String, Object>> listData = (List<Map<String, Object>>) getDataRs.get("data");
		
		//setup excel config
		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
		Sheet sheet = wb.createSheet("Sheet1");
		CellStyle csWrapText = wb.createCellStyle();
		csWrapText.setWrapText(true);
		sheet.setDisplayGridlines(true);
		sheet.setPrintGridlines(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setAutobreaks(true);
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1);
		
		sheet.setColumnWidth(0, 11 * 180);
		sheet.setColumnWidth(1, 16 * 230);
		sheet.setColumnWidth(2, 30 * 300);
		sheet.setColumnWidth(3, 16 * 320);
		sheet.setColumnWidth(4, 16 * 230);
		sheet.setColumnWidth(5, 30 * 300);
		sheet.setColumnWidth(6, 20 * 240);
		sheet.setColumnWidth(7, 20 * 240);
		sheet.setColumnWidth(8, 20 * 250);
		sheet.setColumnWidth(9, 20 * 250);
		sheet.setColumnWidth(10, 20 * 250);
		sheet.setColumnWidth(11, 20 * 250);
		sheet.setColumnWidth(12, 20 * 250);
		sheet.setColumnWidth(13, 20 * 250);
		
		/** ================ header ====================*/
		int rownum = 2;
		int totalColumnnOfTitle = 6;
		int startColTitle = 2;
		int totalExcelColumn = 13;
		Row titleRow = sheet.createRow(rownum);
		titleRow.setHeight((short) 450);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, startColTitle, totalColumnnOfTitle + startColTitle));
		ExcelUtil.createCellOfRow(titleRow, startColTitle, styles.get("cell_bold_centered_no_border_12"), null,
				UtilProperties.getMessage("BaseAccountingUiLabels", "BACCIncomeStatementOverview", locale));
		
		rownum ++;
		Row dateRow = sheet.createRow(rownum);
		dateRow.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, startColTitle, totalColumnnOfTitle + startColTitle));
		String dateTimeFormat = "dd/MM/yyyy";
		
		String dateRowContent = UtilProperties.getMessage("BaseAccountingUiLabels", "ExcelFromDate", locale) + " "
								+ UtilFormatOut.formatDateTime(fromDate, dateTimeFormat, locale, timeZone) +" "
								+ UtilProperties.getMessage("BaseAccountingUiLabels", "ExcelThruDate", locale) + " "
								+ UtilFormatOut.formatDateTime(thruDate, dateTimeFormat, locale, timeZone);
		ExcelUtil.createCellOfRow(dateRow, startColTitle, styles.get("cell_centered_no_border_10"), null, dateRowContent);
		/** ================ ./header ==================*/
		
		/** ================= body ===================*/
		rownum += 2;
		Row headerRow = sheet.createRow(rownum);
		headerRow.setHeight((short) 450);
		String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
		String titleProductId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductId", locale);
		String titleProductName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductName", locale);
		String titleCategoryType = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductCategoryType", locale);
		String titlePartyId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCustomerId", locale);
		String titlePartyName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCustomerName", locale);
		String titleTransTime = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTransactionTime", locale);
		String titleSaleIncome = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleIncome", locale);
		String titleSaleDiscount = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleDiscount", locale);
		String titlePromotion = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPromotion", locale);
		String titleSaleReturn = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleReturn", locale);
		String titleNetRevenue = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNetRevenue", locale);
		String titleCogs = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCOGS", locale);
		String titleGrossProfit = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCGrossProfit", locale);
		
		ExcelUtil.createCellOfRow(headerRow, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
		ExcelUtil.createCellOfRow(headerRow, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleProductId);
		ExcelUtil.createCellOfRow(headerRow, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titleProductName);
		ExcelUtil.createCellOfRow(headerRow, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCategoryType);
		ExcelUtil.createCellOfRow(headerRow, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePartyId);
		ExcelUtil.createCellOfRow(headerRow, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePartyName);
		ExcelUtil.createCellOfRow(headerRow, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleTransTime);
		ExcelUtil.createCellOfRow(headerRow, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleIncome);
		ExcelUtil.createCellOfRow(headerRow, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleDiscount);
		ExcelUtil.createCellOfRow(headerRow, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePromotion);
		ExcelUtil.createCellOfRow(headerRow, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleReturn);
		ExcelUtil.createCellOfRow(headerRow, 11, styles.get("cell_bold_centered_header_excel_border_10"), null, titleNetRevenue);
		ExcelUtil.createCellOfRow(headerRow, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCogs);
		ExcelUtil.createCellOfRow(headerRow, 13, styles.get("cell_bold_centered_header_excel_border_10"), null, titleGrossProfit);
		
		rownum++;
		//String defaultCurrencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
		try {
			if(UtilValidate.isNotEmpty(listData)){
				int i = 0;
				for(Map<String, Object> tempData: listData){
					i++;
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get(Income.PRODUCT_CODE));
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_centered_border_full_10"), null, tempData.get(Income.PRODUCT_NAME));
					String tempCategoryId = (String)tempData.get(Income.CATEGORY_ID);
					GenericValue category;
					category = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", tempCategoryId), false);
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, category.get("categoryName"));
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.get(Income.PARTY_CODE));
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_left_centered_border_full_10"), null, tempData.get(Income.FULL_NAME));
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_left_centered_border_full_10"), null, tempData.get(Income.TRANS_TIME));
					
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.SALE_INCOME));
					ExcelUtil.createCellOfRow(tempDataRow, 8, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.SALE_DISCOUNT));
					ExcelUtil.createCellOfRow(tempDataRow, 9, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.PROMOTION));
					ExcelUtil.createCellOfRow(tempDataRow, 10, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.SALE_RETURN) );
					ExcelUtil.createCellOfRow(tempDataRow, 11, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.NET_REVENUE));
					ExcelUtil.createCellOfRow(tempDataRow, 12, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.COGS));
					ExcelUtil.createCellOfRow(tempDataRow, 13, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.GROSS_PROFIT));
					rownum++;
				}
			}else{
				Row tempRow = sheet.createRow(rownum);
				tempRow.setHeight((short)400);
				ExcelUtil.createCellOfRow(tempRow, totalExcelColumn, styles.get("cell_normal_centered_border_full_10"), null, null);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalExcelColumn));
				ExcelUtil.createCellOfRow(tempRow, 0, styles.get("cell_normal_centered_border_full_10"), null,
						UtilProperties.getMessage("WidgetUiLabels", "wgemptydatastring", locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return;
		}
		/** ================ ./end ====================*/
		try {
			ExcelUtil.responseWrite(response, wb, "Bao_cao_doanh_thu_tong_hop");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exportToPdf(HttpServletRequest request, HttpServletResponse response) {
		
	}
}
