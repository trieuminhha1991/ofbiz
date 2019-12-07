package com.olbius.acc.report.incomestatement.services;

import com.olbius.acc.report.ReportServiceInterface;
import com.olbius.acc.report.incomestatement.entity.Income;
import com.olbius.acc.report.incomestatement.query.CatIncomeChartImpl;
import com.olbius.acc.report.incomestatement.query.CatIncomeOlapImpl;
import com.olbius.acc.report.incomestatement.query.IncomeOlapConstant;
import com.olbius.acc.report.incomestatement.query.IncomeOlapImpl;
import com.olbius.acc.utils.ExcelUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.grid.OlapGrid;
import javolution.util.FastMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class CatIncomeStmServices implements ReportServiceInterface {

	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> runQuery(DispatchContext dctx, Map<String, Object> context, String dimension){
		Delegator delegator = dctx.getDelegator();
		
		CatIncomeOlapImpl incomeOlap = new CatIncomeOlapImpl(delegator,dimension);
		incomeOlap.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType = (String) context.get(IncomeOlapImpl.DATATYPE);
        String categoryId = (String) context.get(IncomeOlapImpl.CATEGORY_ID);
        String hasTime = (String) context.get(IncomeOlapImpl.HAS_TIME);
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        
        incomeOlap.putParameter(IncomeOlapImpl.DATATYPE, dateType);
        incomeOlap.putParameter(IncomeOlapImpl.CATEGORY_ID, categoryId);
        incomeOlap.putParameter(IncomeOlapImpl.HAS_TIME, hasTime);
        incomeOlap.putParameter(IncomeOlapImpl.ORG_PARTY_ID, organizationPartyId);
        
        incomeOlap.setFromDate(fromDate);
        incomeOlap.setThruDate(thruDate);
        incomeOlap.setHasTransTime(hasTime);
		
		//Set limit is null
        context.put("limit", 0l);
		
        Map<String, Object> incomeStatResult = incomeOlap.execute(context);
		List<Map<String, Object>> data = (List<Map<String, Object>>)incomeStatResult.get("data");
		return data;
	}
	
	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		List<String> dataFields = new ArrayList<String>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = runQuery(dctx, context, "category");
		
		dataFields.add(Income.CATEGORY_ID);
		dataFields.add(Income.TRANS_TIME);
		dataFields.add(Income.SALE_INCOME);
		dataFields.add(Income.SALE_DISCOUNT);
		dataFields.add(Income.PROMOTION);
		dataFields.add(Income.SALE_RETURN);
		dataFields.add(Income.NET_REVENUE);
		dataFields.add(Income.COGS);
		dataFields.add(Income.GROSS_PROFIT);
		result.put("data", data);
		result.put("datafields", dataFields);
		result.put("totalsize", data != null? data.size() : 0);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	/*@Override
	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		List<Income> results = new ArrayList<Income>();
		List<Income> listAcctgTrans = new ArrayList<Income>();
		List<Map<String, Object>> dataAdapter = new ArrayList<Map<String,Object>>();
		List<String> dataFields = new ArrayList<String>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = runQuery(dctx, context);
		String hasTime = (String) context.get(IncomeOlapImpl.HAS_TIME);
		for(Map<String, Object> item : data) {
			Income income = new Income();
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
			income.setCategoryId(item.get("categoryId").toString());
			if(hasTime.equals("Y")) {
				income.setTransTime(item.get("transTime").toString());
			}
			listAcctgTrans.add(income);
		}
		for(Income item: listAcctgTrans) {
			boolean isExists = false;
			Income tmpJ  = item;
			for(Income income : results) {
				Income tmpI  = income;
				if(hasTime.equals("Y") && tmpI.getCategoryId().equals(tmpJ.getCategoryId()) 
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
					if(tmpJ.getSaleExtIncome().compareTo(BigDecimal.ZERO) != 0) {
						tmpI.setSaleExtIncome(tmpJ.getSaleExtIncome());
					}
					if(tmpJ.getSaleIntIncome().compareTo(BigDecimal.ZERO) != 0) {
						tmpI.setSaleIntIncome(tmpJ.getSaleIntIncome());
					}					
					isExists = true;
				}else if (hasTime.equals("N") && tmpI.getCategoryId().equals(tmpJ.getCategoryId())) {
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
		
		for(Income income : results) {
			Map<String, Object> object = new HashMap<String, Object>();
			object.put(Income.COGS, income.getCogs());
			object.put(Income.GROSS_PROFIT, income.getGrossProfit());
			object.put(Income.NET_REVENUE, income.getNetRevenue());
			object.put(Income.SALE_RETURN, income.getSaleReturn());
			object.put(Income.PROMOTION, income.getPromotion());
			object.put(Income.SALE_DISCOUNT, income.getSaleDiscount());
			object.put(Income.SALE_INCOME, income.getSaleIncome());
			if(hasTime.equals("Y")) {
				object.put(Income.TRANS_TIME, income.getTransTime());
			}
			object.put(Income.CATEGORY_ID, income.getCategoryId());
			dataAdapter.add(object);
		}
		dataFields.add(Income.CATEGORY_ID);
		if(hasTime.equals("Y")) {
			dataFields.add(Income.TRANS_TIME);
		}
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
	}*/
	
	public static Map<String, Object> evaluateCatIncomePieChart(DispatchContext ctx, Map<String, ? extends Object> context)throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		
		CatIncomeChartImpl chart = new CatIncomeChartImpl(delegator,"category");
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
     	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
        chart.putParameter(IncomeOlapConstant.ORG_PARTY_ID, organizationPartyId);
        chart.setOlapResultType(OlapPieChart.class);
       /* CatIncomeQuery query = chart.new CatIncomeQuery();
        CatIncomePie pieResult = chart.new CatIncomePie(chart, query);*/
		
		/*chart.setOlapResult(pieResult);*/
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void exportToExcel(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        Map<String, Object> ctxMap = FastMap.newInstance();
        String categoryId = (String)parameters.get(IncomeOlapImpl.CATEGORY_ID);
        String dataType = (String)parameters.get(IncomeOlapImpl.DATATYPE);
        String fromDateStr = (String)parameters.get("fromDate");
        String thruDateStr = (String)parameters.get("thruDate");
        Timestamp fromDate = null, thruDate = null;
        if(fromDateStr != null){
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
		}
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
		}
		ctxMap.put("categoryId", categoryId);
		ctxMap.put("dateType", dataType);
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("fromDate", fromDate);
		ctxMap.put("thruDate", thruDate);
		ctxMap.put("limit", 0l);
		ctxMap.put("offset", 0l);
		ctxMap.put("init", Boolean.TRUE);
		try {
			Map<String, Object> resultServices = dispatcher.runSync("getCategoryIncomeStm", ctxMap);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultServices.get("data");
			
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
			sheet.setColumnWidth(4, 20 * 240);
			sheet.setColumnWidth(5, 20 * 250);
			sheet.setColumnWidth(6, 20 * 250);
			sheet.setColumnWidth(7, 20 * 250);
			sheet.setColumnWidth(8, 20 * 250);
			sheet.setColumnWidth(9, 20 * 250);
			sheet.setColumnWidth(10, 20 * 250);
			
			/** ================ header ====================*/
			int rownum = 2;
			int totalColumnnOfTitle = 6;
			int startColTitle = 2;
			int totalExcelColumn = 10;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 450);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, startColTitle, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(titleRow, startColTitle, styles.get("cell_bold_centered_no_border_12"), null,
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCategoryIncomeStatementReport", locale));
			
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
			String titleCategoryId = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductCategoryId", locale);
			String titleCategoryName = UtilProperties.getMessage("BaseSalesUiLabels", "BSCategoryName", locale);
			String titleTransTime = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTransactionTime", locale);
			String titleSaleIncome = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleIncome", locale);
			String titleSaleDiscount = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleDiscount", locale);
			String titlePromotion = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPromotion", locale);
			String titleSaleReturn = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleReturn", locale);
			String titleNetRevenue = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNetRevenue", locale);
			String titleCogs = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCOGS", locale);
			String titleGrossProfit = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCGrossProfit", locale);
			
			ExcelUtil.createCellOfRow(headerRow, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
			ExcelUtil.createCellOfRow(headerRow, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCategoryId);
			ExcelUtil.createCellOfRow(headerRow, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCategoryName);
			ExcelUtil.createCellOfRow(headerRow, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleTransTime);
			ExcelUtil.createCellOfRow(headerRow, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleIncome);
			ExcelUtil.createCellOfRow(headerRow, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleDiscount);
			ExcelUtil.createCellOfRow(headerRow, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePromotion);
			ExcelUtil.createCellOfRow(headerRow, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleReturn);
			ExcelUtil.createCellOfRow(headerRow, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleNetRevenue);
			ExcelUtil.createCellOfRow(headerRow, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCogs);
			ExcelUtil.createCellOfRow(headerRow, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleGrossProfit);
			
			rownum++;
			//String defaultCurrencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
			
			if(UtilValidate.isNotEmpty(listData)){
				int i = 0;
				for(Map<String, Object> tempData: listData){
					i++;
					String tempCategoryId = (String)tempData.get(Income.CATEGORY_ID);
					GenericValue tempCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", tempCategoryId), false);
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempCategoryId);
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_centered_border_full_10"), null, tempCategory.get("categoryName"));
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get(Income.TRANS_TIME));
					
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.SALE_INCOME));
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.SALE_DISCOUNT));
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.PROMOTION));
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.SALE_RETURN) );
					ExcelUtil.createCellOfRow(tempDataRow, 8, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.NET_REVENUE));
					ExcelUtil.createCellOfRow(tempDataRow, 9, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.COGS));
					ExcelUtil.createCellOfRow(tempDataRow, 10, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get(Income.GROSS_PROFIT));
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
			/** ================ ./end ====================*/
			ExcelUtil.responseWrite(response, wb, "Bao_cao_doanh_theo_danh_muc");
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return;
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void exportToPdf(HttpServletRequest request, HttpServletResponse response) {
	}
	
}
