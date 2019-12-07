package com.olbius.acc.report.incomestatement.exportFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.acc.report.incomestatement.entity.Income;
import com.olbius.acc.utils.ExcelUtil;

public class ExportEvents {
	@SuppressWarnings("unchecked")
	public static void exportProductIncomeStmToExcel(HttpServletRequest request, HttpServletResponse response) {
		//Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> parameters = UtilHttp.getParameterMap(request);
		String productId = (String)parameters.get("productId");
		String dataType = (String)parameters.get("dateType");
		String fromDateStr = (String)parameters.get("fromDate");
		String thruDateStr = (String)parameters.get("thruDate");
		Timestamp fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
		}
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
		}
		 Map<String, Object> context = FastMap.newInstance();
		 context.put("productId", UtilValidate.isNotEmpty(productId)? productId: null);
		 context.put("dateType", dataType);
		 context.put("userLogin", userLogin);
		 context.put("fromDate", fromDate);
		 context.put("thruDate", thruDate);
		 context.put("limit", 0l);
		 context.put("offset", 0l);
		 context.put("init", Boolean.TRUE);
		 context.put("olapType", "GRID");
		 try {
			Map<String, Object> resultServices = dispatcher.runSync("getProductIncomeStm", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultServices.get("data");
			
			/** ================== setup excel page =========================*/
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
			sheet.setColumnWidth(3, 20 * 240);
			sheet.setColumnWidth(4, 20 * 240);
			sheet.setColumnWidth(5, 20 * 250);
			sheet.setColumnWidth(6, 20 * 250);
			sheet.setColumnWidth(7, 20 * 250);
			sheet.setColumnWidth(8, 20 * 250);
			sheet.setColumnWidth(9, 20 * 250);
			sheet.setColumnWidth(10, 20 * 250);
			
			/** ================ header ====================*/
			int rownum = 2;
			int totalColumnnOfTitle = 5;
			int startColTitle = 2;
			int totalExcelColumn = 10;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 450);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, startColTitle, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(titleRow, startColTitle, styles.get("cell_bold_centered_no_border_12"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCIncomeStatementProductReport", locale).toUpperCase());
			
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
			rownum += 2;
			Row headerRow = sheet.createRow(rownum);
			headerRow.setHeight((short) 450);
			String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
			String titleProductId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductId", locale);
			String titleProductName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductName", locale);
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
			ExcelUtil.createCellOfRow(headerRow, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleTransTime);
			ExcelUtil.createCellOfRow(headerRow, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleIncome);
			ExcelUtil.createCellOfRow(headerRow, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleDiscount);
			ExcelUtil.createCellOfRow(headerRow, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePromotion);
			ExcelUtil.createCellOfRow(headerRow, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleReturn);
			ExcelUtil.createCellOfRow(headerRow, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleNetRevenue);
			ExcelUtil.createCellOfRow(headerRow, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCogs);
			ExcelUtil.createCellOfRow(headerRow, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleGrossProfit);
			
			//String defaultCurrencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
			rownum++;
			if(UtilValidate.isNotEmpty(listData)){
				int i = 0; 
				for(Map<String, Object> tempData: listData){
					i++;
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get(Income.PRODUCT_CODE));
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_centered_border_full_10"), null, tempData.get(Income.PRODUCT_NAME));
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("dateTime"));

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
			ExcelUtil.responseWrite(response, wb, "Bao_cao_doanh_thu_theo_san_pham");
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void exportCustomerIncomeStmToExcel(HttpServletRequest request, HttpServletResponse response) {
		//Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> parameters = UtilHttp.getParameterMap(request);
		String partyId = (String)parameters.get("partyId");
		String dataType = (String)parameters.get("dateType");
		String fromDateStr = (String)parameters.get("fromDate");
		String thruDateStr = (String)parameters.get("thruDate");
		Timestamp fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
		}
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
		}
		Map<String, Object> context = FastMap.newInstance();
		context.put("partyId", UtilValidate.isNotEmpty(partyId)? partyId: null);
		context.put("dateType", dataType);
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDate);
		context.put("thruDate", thruDate);
		context.put("limit", 0l);
		context.put("offset", 0l);
		context.put("init", Boolean.TRUE);
		context.put("olapType", "GRID");
		try {
			Map<String, Object> resultServices = dispatcher.runSync("getCustomerIncomeStm", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultServices.get("data");
			
			/** ================== setup excel page =========================*/
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
			sheet.setColumnWidth(3, 20 * 240);
			sheet.setColumnWidth(4, 20 * 240);
			sheet.setColumnWidth(5, 20 * 250);
			sheet.setColumnWidth(6, 20 * 250);
			sheet.setColumnWidth(7, 20 * 250);
			sheet.setColumnWidth(8, 20 * 250);
			sheet.setColumnWidth(9, 20 * 250);
			sheet.setColumnWidth(10, 20 * 250);
			
			/** ================ header ====================*/
			int rownum = 2;
			int totalColumnnOfTitle = 5;
			int startColTitle = 2;
			int totalExcelColumn = 10;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 450);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, startColTitle, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(titleRow, startColTitle, styles.get("cell_bold_centered_no_border_12"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCIncomeStatementCustomerReport", locale).toUpperCase());
			
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
			
			/** ================ body ==================*/
			rownum += 2;
			Row headerRow = sheet.createRow(rownum);
			headerRow.setHeight((short) 450);
			String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
			String titlePartyId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCustomerId", locale);
			String titlePartyName = UtilProperties.getMessage("BaseSalesUiLabels", "BSCustomerName", locale);
			String titleTransTime = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTransactionTime", locale);
			String titleSaleIncome = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleIncome", locale);
			String titleSaleDiscount = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleDiscount", locale);
			String titlePromotion = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPromotion", locale);
			String titleSaleReturn = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSaleReturn", locale);
			String titleNetRevenue = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNetRevenue", locale);
			String titleCogs = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCOGS", locale);
			String titleGrossProfit = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCGrossProfit", locale);
			
			ExcelUtil.createCellOfRow(headerRow, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
			ExcelUtil.createCellOfRow(headerRow, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePartyId);
			ExcelUtil.createCellOfRow(headerRow, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePartyName);
			ExcelUtil.createCellOfRow(headerRow, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleTransTime);
			ExcelUtil.createCellOfRow(headerRow, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleIncome);
			ExcelUtil.createCellOfRow(headerRow, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleDiscount);
			ExcelUtil.createCellOfRow(headerRow, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePromotion);
			ExcelUtil.createCellOfRow(headerRow, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSaleReturn);
			ExcelUtil.createCellOfRow(headerRow, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleNetRevenue);
			ExcelUtil.createCellOfRow(headerRow, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCogs);
			ExcelUtil.createCellOfRow(headerRow, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleGrossProfit);
			
			//String defaultCurrencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
			rownum++;
			if(UtilValidate.isNotEmpty(listData)){
				int i = 0; 
				for(Map<String, Object> tempData: listData){
					i++;
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get("customerCode"));
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_centered_border_full_10"), null, tempData.get("customerName"));
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("dateTime"));
					
					BigDecimal tempSaleIncomeAmount = (BigDecimal)tempData.get(Income.SALE_INCOME);
					BigDecimal tempSaleDiscountAmount = (BigDecimal)tempData.get(Income.SALE_DISCOUNT);
					BigDecimal tempPromotionAmount = (BigDecimal)tempData.get(Income.PROMOTION);
					BigDecimal tempSaleReturnAmount = (BigDecimal)tempData.get(Income.SALE_RETURN);
					BigDecimal tempNetRevenueAmount = (BigDecimal)tempData.get(Income.NET_REVENUE);
					BigDecimal tempCogsAmount = (BigDecimal)tempData.get(Income.COGS);
					BigDecimal tempGrossProfitAmount = (BigDecimal)tempData.get(Income.GROSS_PROFIT);
					
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_right_centered_border_full_currency_10"), null, tempSaleIncomeAmount);
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_right_centered_border_full_currency_10"), null, tempSaleDiscountAmount);
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_right_centered_border_full_currency_10"), null, tempPromotionAmount);
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_right_centered_border_full_currency_10"), null, tempSaleReturnAmount);
					ExcelUtil.createCellOfRow(tempDataRow, 8, styles.get("cell_right_centered_border_full_currency_10"), null, tempNetRevenueAmount);
					ExcelUtil.createCellOfRow(tempDataRow, 9, styles.get("cell_right_centered_border_full_currency_10"), null, tempCogsAmount);
					ExcelUtil.createCellOfRow(tempDataRow, 10, styles.get("cell_right_centered_border_full_currency_10"), null, tempGrossProfitAmount);
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
			ExcelUtil.responseWrite(response, wb, "Bao_cao_doanh_thu_theo_khach_hang");
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
