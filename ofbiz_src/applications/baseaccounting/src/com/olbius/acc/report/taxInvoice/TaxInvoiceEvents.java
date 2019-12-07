package com.olbius.acc.report.taxInvoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.utils.ExcelUtil;

public class TaxInvoiceEvents {
	@SuppressWarnings("unchecked")
	public static String exportExcelPurchaseInvoiceTax(HttpServletRequest request, HttpServletResponse response){
		//Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String monthStr = (String)paramMap.get("month");
		String quarterStr = (String)paramMap.get("quarter");
		String yearStr = (String)paramMap.get("year");
		String calendarType = (String)paramMap.get("calendarType");
		try {
			Map<String, Object> resultService = dispatcher.runSync("getLastupdatedOlapServices", UtilMisc.toMap("service", "invoiceTaxJob"));
			if(!ServiceUtil.isSuccess(resultService)){
				return "error";
			}
			Long lastupdated = (Long)resultService.get("lastupdated");
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "purchaseInvoiceTaxReport", paramMap, userLogin, timeZone, locale);
			context.put("olapType", "GRID");
			context.put("limit", 0l);
			context.put("init", Boolean.TRUE);
			context.put("offset", 0l);
			context.put("timeZone", UtilHttp.getTimeZone(request));
			context.put("locale", UtilHttp.getLocale(request));
			if(lastupdated != null){
				context.put("serviceTimestamp", lastupdated);
			}
			resultService = dispatcher.runSync("purchaseInvoiceTaxReport", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return "error";
			}
			List<Map<String, Object>> listData = (List<Map<String, Object>>)resultService.get("data");
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
			sheet.setColumnWidth(1, 15 * 230);
			sheet.setColumnWidth(2, 15 * 270);
			sheet.setColumnWidth(3, 15 * 320);
			sheet.setColumnWidth(4, 15 * 210);
			sheet.setColumnWidth(5, 15 * 240);
			sheet.setColumnWidth(6, 17 * 240);
			sheet.setColumnWidth(7, 18 * 270);
			/**=============== header =====================*/
			int rownum = 3;
			int totalColumnnOfTitle = 5;
			int startColTitle = 2;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, startColTitle, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(titleRow, startColTitle, styles.get("cell_bold_centered_no_border_12"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "PurchasingReportStatisticsTitleExcel", locale));
			
			rownum ++;
			Row noteRow = sheet.createRow(rownum);
			noteRow.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(noteRow, 0, styles.get("cell_centered_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "AttachVATDeclarations", locale));
			
			rownum++;
			Row periodVATRow = sheet.createRow(rownum);
			periodVATRow.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			
			StringBuffer periodVATCellValue = new StringBuffer(UtilProperties.getMessage("BaseAccountingUiLabels", "TaxPeriod", locale));
			periodVATCellValue.append(": ");
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			if(yearStr != null && yearStr.trim().length() > 0){
				year = Integer.parseInt(yearStr);
			}
			if("MONTH".equals(calendarType) || calendarType == null){
				int month = cal.get(Calendar.MONTH);
				if(monthStr != null && monthStr.trim().length() > 0){
					month = Integer.parseInt(monthStr);
				}
				periodVATCellValue.append(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) 
											+ " " + String.valueOf(month + 1)
											+ " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSYearLowercase", locale)
											+ " " + String.valueOf(year)
											+ " / " + UtilProperties.getMessage("BaseSalesUiLabels", "BSQuarter", locale)
											+ "...." + UtilProperties.getMessage("BaseSalesUiLabels", "BSYearLowercase", locale) + ".....");
			}else if("QUARTER".equals(calendarType)){
				int quarter = (int)Math.floor((cal.get(Calendar.MONTH) + 3) / 3);
				if(quarterStr != null && quarterStr.trim().length() > 0){
					quarter = Integer.parseInt(quarterStr);
				}
				periodVATCellValue.append(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) 
											+ "...."
											+ " " + UtilProperties.getMessage("BaseAccountingUiLabels", "BSYearLowercase", locale)
											+ "...."
											+ " / " + UtilProperties.getMessage("BaseAccountingUiLabels", "BSQuarter", locale)
											+ " " + String.valueOf(quarter) 
											+ UtilProperties.getMessage("BaseAccountingUiLabels", "BSYearLowercase", locale) 
											+ String.valueOf(year));
			}
			ExcelUtil.createCellOfRow(periodVATRow, 0, styles.get("cell_centered_no_border_10"), null, periodVATCellValue.toString());
			
			rownum += 2;
			Row partyRow = sheet.createRow(rownum);
			partyRow.setHeight((short)300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(partyRow, 0, styles.get("cell_left_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "VATPaidParty", locale));
			
			rownum++;
			Row taxCodeRow = sheet.createRow(rownum);
			taxCodeRow.setHeight((short)300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(taxCodeRow, 0, styles.get("cell_left_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTaxCode", locale));
			
			rownum += 2;
			Row uomRow = sheet.createRow(rownum);
			uomRow.setHeight((short)300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			String uomCellValue = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCurrencyUom", locale) + ": VND";
			ExcelUtil.createCellOfRow(uomRow, 0, styles.get("cell_right_no_border_10"), null, uomCellValue);
			/** ============== ./ header ================ */
			/**=============== body ======================*/
			rownum++;
			Row headerRow = sheet.createRow(rownum);
			headerRow.setHeight((short) 800);
			String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
			String titleInvoiceId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvSerialNumber", locale);
			String titleInvoiceDate = UtilProperties.getMessage("BaseAccountingUiLabels", "DateMonthYearInvoiceDateExcel", locale);
			String titlePartyName = UtilProperties.getMessage("BaseAccountingUiLabels", "SellerName", locale);
			String titlePartyTaxCode = UtilProperties.getMessage("BaseAccountingUiLabels", "accApPartyTaxId", locale);
			String titleAmountNotVAT = UtilProperties.getMessage("BaseAccountingUiLabels", "PurchaseAmountNotTaxExcel", locale);
			String titleVATAmount = UtilProperties.getMessage("BaseAccountingUiLabels", "VATAmount", locale);
			String titleNotes = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNote", locale);
			
			ExcelUtil.createCellOfRow(headerRow, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
			ExcelUtil.createCellOfRow(headerRow, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleInvoiceId);
			ExcelUtil.createCellOfRow(headerRow, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titleInvoiceDate);
			ExcelUtil.createCellOfRow(headerRow, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePartyName);
			ExcelUtil.createCellOfRow(headerRow, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePartyTaxCode);
			ExcelUtil.createCellOfRow(headerRow, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAmountNotVAT);
			ExcelUtil.createCellOfRow(headerRow, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleVATAmount);
			ExcelUtil.createCellOfRow(headerRow, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleNotes);
			
			rownum++;
			Row numberRow = sheet.createRow(rownum);
			numberRow.setHeight((short)300);
			
			//danh so cot
			for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
				ExcelUtil.createCellOfRow(numberRow, i, styles.get("cell_normal_centered_border_10"), null, "[" + String.valueOf(i + 1) + "]");
			}
			
			rownum++;
			Row type1Row = sheet.createRow(rownum);
			type1Row.setHeight((short)500);
			ExcelUtil.createCellOfRow(type1Row, totalColumnnOfTitle + startColTitle, styles.get("cell_left_centered_border_full_10"), null, null);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(type1Row, 0, styles.get("cell_left_centered_border_full_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceTaxPurchaseType1", locale));
			
			rownum++;
			if(UtilValidate.isNotEmpty(listData)){
				int i = 0;
				for(Map<String, Object> tempData: listData){
					i++;
					//String tempCurrencyUom = (String)tempData.get("currency_id");
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get("invoice_id"));
					
					Timestamp invoiceDate = (Timestamp)tempData.get("invoice_date");
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_border_full_date_10"), null, invoiceDate);
					
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("party_name"));
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.get("tax_code"));
					
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_right_centered_border_full_currency_10"), null, (BigDecimal)tempData.get("amount"));
					
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_right_centered_border_full_currency_10"), null, (BigDecimal)tempData.get("tax_amount"));
					
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_left_centered_border_full_10"), null, "");
					
					rownum++;
				}
			}else{
				Row tempRow = sheet.createRow(rownum);
				for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
					ExcelUtil.createCellOfRow(tempRow, i, styles.get("cell_left_centered_border_full_10"), null, null);
				}
				rownum++;
			}
			Row type1TotalRow = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(type1TotalRow, 0, styles.get("cell_bold_left_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountTotal", locale));
			ExcelUtil.createCellOfRow(type1TotalRow, 1, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 2, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 3, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 4, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 5, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 6, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 7, styles.get("cell_bold_left_border_10"), null, "");
			
			rownum++;
			Row type2Row = sheet.createRow(rownum);
			type2Row.setHeight((short)350);
			ExcelUtil.createCellOfRow(type2Row, totalColumnnOfTitle + startColTitle, styles.get("cell_left_centered_border_full_10"), null, null);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(type2Row, 0, styles.get("cell_left_centered_border_full_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceTaxPurchaseType2", locale));
			rownum++;
			Row tempRow = sheet.createRow(rownum);
			for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
				ExcelUtil.createCellOfRow(tempRow, i, styles.get("cell_left_centered_border_full_10"), null, null);
			}
			
			rownum++;
			Row type2TotalRow = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(type2TotalRow, 0, styles.get("cell_bold_left_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountTotal", locale));
			ExcelUtil.createCellOfRow(type2TotalRow, 1, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 2, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 3, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 4, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 5, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 6, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 7, styles.get("cell_bold_left_border_10"), null, "");
			
			rownum++;
			Row type3Row = sheet.createRow(rownum);
			type3Row.setHeight((short)350);
			ExcelUtil.createCellOfRow(type3Row, totalColumnnOfTitle + startColTitle, styles.get("cell_left_centered_border_full_10"), null, null);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(type3Row, 0, styles.get("cell_left_centered_border_full_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceTaxPurchaseType3", locale));
			rownum++;
			Row tempRow3 = sheet.createRow(rownum);
			for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
				ExcelUtil.createCellOfRow(tempRow3, i, styles.get("cell_left_centered_border_full_10"), null, null);
			}
			
			rownum++;
			Row type3TotalRow = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(type3TotalRow, 0, styles.get("cell_bold_left_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountTotal", locale));
			ExcelUtil.createCellOfRow(type3TotalRow, 1, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 2, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 3, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 4, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 5, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 6, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 7, styles.get("cell_bold_left_border_10"), null, "");
			/**==================== ./body ==================*/
			/**==================== footer ==================*/
			rownum+= 2;
			Row totalVAT1 = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 4));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
			ExcelUtil.createCellOfRow(totalVAT1, 0, styles.get("cell_normal_Left_wrap__top10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "PurchaseInvoiceReportVATExcelNote1", locale));
			
			rownum++;
			Row totalVAT2 = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 4));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
			ExcelUtil.createCellOfRow(totalVAT2, 0, styles.get("cell_normal_Left_wrap__top10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "PurchaseInvoiceReportVATExcelNote2", locale));
			
			rownum += 2;
			Row dateMonthYeaRow = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
			ExcelUtil.createCellOfRow(dateMonthYeaRow, 5, styles.get("cell_normal_center_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "DateMonthYearExcel", locale));
			
			rownum++;
			Row taxPayerSignRow = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
			ExcelUtil.createCellOfRow(taxPayerSignRow, 5, styles.get("cell_normal_center_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "TaxPayerExcel", locale));
			
			rownum++;
			Row taxPayerRepresentativeRow = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
			ExcelUtil.createCellOfRow(taxPayerRepresentativeRow, 5, styles.get("cell_normal_center_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "TaxPayerRepresentativeExcel", locale));
			
			rownum ++;
			Row signRow = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
			ExcelUtil.createCellOfRow(signRow, 5, styles.get("cell_normal_center_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "SignedAndSealedExcel", locale));
			/**===========./footer=================*/
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				wb.write(baos);
				byte[] bytes = baos.toByteArray();
				String fileName = "Bangkemuavao";
				response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
				response.setContentType("application/vnd.xls");
				response.getOutputStream().write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if (baos != null){
					try {
						baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return "error";
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return "error";
		} 
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String exportExcelSalesInvoiceTax(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String monthStr = (String)paramMap.get("month");
		String quarterStr = (String)paramMap.get("quarter");
		String yearStr = (String)paramMap.get("year");
		String calendarType = (String)paramMap.get("calendarType");
		try {
			Map<String, Object> resultService = dispatcher.runSync("getLastupdatedOlapServices", UtilMisc.toMap("service", "invoiceTaxJob"));
			if(!ServiceUtil.isSuccess(resultService)){
				return "error";
			}
			Long lastupdated = (Long)resultService.get("lastupdated");
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "purchaseInvoiceTaxReport", paramMap, userLogin, timeZone, locale);
			context.put("olapType", "GRID");
			context.put("limit", 0l);
			context.put("init", Boolean.TRUE);
			context.put("offset", 0l);
			context.put("timeZone", UtilHttp.getTimeZone(request));
			context.put("locale", UtilHttp.getLocale(request));
			if(lastupdated != null){
				context.put("serviceTimestamp", lastupdated);
			}
			context.put("productCategoryId", "TAX_VAT_KCT");
			resultService = dispatcher.runSync("saleInvoiceTaxReport", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return "error";
			}
			List<Map<String, Object>> listDataNoTax = (List<Map<String, Object>>)resultService.get("data");
			
			context.put("productCategoryId", "TAX_VAT_0");
			resultService = dispatcher.runSync("saleInvoiceTaxReport", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return "error";
			}
			List<Map<String, Object>> listDataZeroPercentTax = (List<Map<String, Object>>)resultService.get("data");
			
			context.put("productCategoryId", "TAX_VAT_5");
			resultService = dispatcher.runSync("saleInvoiceTaxReport", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return "error";
			}
			List<Map<String, Object>> listDataFivePercentTax = (List<Map<String, Object>>)resultService.get("data");
			
			context.put("productCategoryId", "TAX_VAT_10");
			resultService = dispatcher.runSync("saleInvoiceTaxReport", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return "error";
			}
			List<Map<String, Object>> listDataTenPercentTax = (List<Map<String, Object>>)resultService.get("data");
			
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
			sheet.setColumnWidth(1, 15 * 230);
			sheet.setColumnWidth(2, 15 * 270);
			sheet.setColumnWidth(3, 15 * 320);
			sheet.setColumnWidth(4, 15 * 210);
			sheet.setColumnWidth(5, 15 * 240);
			sheet.setColumnWidth(6, 17 * 240);
			sheet.setColumnWidth(7, 18 * 270);
			
			/** ================ header ====================*/
			int rownum = 3;
			int totalColumnnOfTitle = 5;
			int startColTitle = 2;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, startColTitle, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(titleRow, startColTitle, styles.get("cell_bold_centered_no_border_12"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "SalesReportStatisticsTitleExcel", locale));
			
			rownum ++;
			Row noteRow = sheet.createRow(rownum);
			noteRow.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(noteRow, 0, styles.get("cell_centered_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "AttachVATDeclarations", locale));
			
			rownum++;
			Row periodVATRow = sheet.createRow(rownum);
			periodVATRow.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			
			StringBuffer periodVATCellValue = new StringBuffer(UtilProperties.getMessage("BaseAccountingUiLabels", "TaxPeriod", locale));
			periodVATCellValue.append(": ");
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			if(yearStr != null && yearStr.trim().length() > 0){
				year = Integer.parseInt(yearStr);
			}
			if("MONTH".equals(calendarType) || calendarType == null){
				int month = cal.get(Calendar.MONTH);
				if(monthStr != null && monthStr.trim().length() > 0){
					month = Integer.parseInt(monthStr);
				}
				periodVATCellValue.append(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) 
											+ " " + String.valueOf(month + 1)
											+ " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSYearLowercase", locale)
											+ " " + String.valueOf(year)
											+ " / " + UtilProperties.getMessage("BaseSalesUiLabels", "BSQuarter", locale)
											+ "...." + UtilProperties.getMessage("BaseSalesUiLabels", "BSYearLowercase", locale) + ".....");
			}else if("QUARTER".equals(calendarType)){
				int quarter = (int)Math.floor((cal.get(Calendar.MONTH) + 3) / 3);
				if(quarterStr != null && quarterStr.trim().length() > 0){
					quarter = Integer.parseInt(quarterStr);
				}
				periodVATCellValue.append(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) 
											+ "...."
											+ " " + UtilProperties.getMessage("BaseAccountingUiLabels", "BSYearLowercase", locale)
											+ "...."
											+ " / " + UtilProperties.getMessage("BaseAccountingUiLabels", "BSQuarter", locale)
											+ " " + String.valueOf(quarter) 
											+ UtilProperties.getMessage("BaseAccountingUiLabels", "BSYearLowercase", locale) 
											+ String.valueOf(year));
			}
			ExcelUtil.createCellOfRow(periodVATRow, 0, styles.get("cell_centered_no_border_10"), null, periodVATCellValue.toString());
			
			rownum += 2;
			Row partyRow = sheet.createRow(rownum);
			partyRow.setHeight((short)300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(partyRow, 0, styles.get("cell_left_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "VATPaidParty", locale));
			
			rownum++;
			Row taxCodeRow = sheet.createRow(rownum);
			taxCodeRow.setHeight((short)300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(taxCodeRow, 0, styles.get("cell_left_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTaxCode", locale));
			
			rownum += 2;
			Row uomRow = sheet.createRow(rownum);
			uomRow.setHeight((short)300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			String uomCellValue = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCurrencyUom", locale) + ": VND";
			ExcelUtil.createCellOfRow(uomRow, 0, styles.get("cell_right_no_border_10"), null, uomCellValue);
			/**====== ./ header ======= */
			
			/**======== body ========== */
			rownum++;
			Row headerRow = sheet.createRow(rownum);
			headerRow.setHeight((short) 800);
			String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
			String titleInvoiceId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvSerialNumber", locale);
			String titleInvoiceDate = UtilProperties.getMessage("BaseAccountingUiLabels", "DateMonthYearInvoiceDateExcel", locale);
			String titlePartyName = UtilProperties.getMessage("BaseAccountingUiLabels", "SellerName", locale);
			String titlePartyTaxCode = UtilProperties.getMessage("BaseAccountingUiLabels", "accApPartyTaxId", locale);
			String titleAmountNotVAT = UtilProperties.getMessage("BaseAccountingUiLabels", "PurchaseAmountNotTaxExcel", locale);
			String titleVATAmount = UtilProperties.getMessage("BaseAccountingUiLabels", "VATAmount", locale);
			String titleNotes = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNote", locale);
			
			ExcelUtil.createCellOfRow(headerRow, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
			ExcelUtil.createCellOfRow(headerRow, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleInvoiceId);
			ExcelUtil.createCellOfRow(headerRow, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titleInvoiceDate);
			ExcelUtil.createCellOfRow(headerRow, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePartyName);
			ExcelUtil.createCellOfRow(headerRow, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titlePartyTaxCode);
			ExcelUtil.createCellOfRow(headerRow, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAmountNotVAT);
			ExcelUtil.createCellOfRow(headerRow, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleVATAmount);
			ExcelUtil.createCellOfRow(headerRow, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleNotes);
			
			rownum++;
			Row numberRow = sheet.createRow(rownum);
			numberRow.setHeight((short)300);
			
			//danh so cot
			for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
				ExcelUtil.createCellOfRow(numberRow, i, styles.get("cell_normal_centered_border_10"), null, "[" + String.valueOf(i + 1) + "]");
			}
			
			rownum++;
			Row type1Row = sheet.createRow(rownum);
			type1Row.setHeight((short)350);
			ExcelUtil.createCellOfRow(type1Row, totalColumnnOfTitle + startColTitle, styles.get("cell_left_centered_border_full_10"), null, null);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(type1Row, 0, styles.get("cell_left_centered_border_full_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceTaxSalesType1", locale));
			
			rownum++;
			if(UtilValidate.isNotEmpty(listDataNoTax)){
				int i = 0;
				for(Map<String, Object> tempData: listDataNoTax){
					i++;
					String tempCurrencyUom = (String)tempData.get("currency_id");
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get("invoice_id"));
					
					Timestamp invoiceDate = (Timestamp)tempData.get("invoice_date");
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_border_full_date_10"), null, invoiceDate);
					
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("party_name"));
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.get("tax_code"));
					
					String tempAmount = UtilFormatOut.formatCurrency((BigDecimal)tempData.get("amount"), tempCurrencyUom, locale, 2);
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_left_centered_border_full_10"), null, tempAmount);
					
					String tempTaxAmount = UtilFormatOut.formatCurrency((BigDecimal)tempData.get("tax_amount"), tempCurrencyUom, locale, 2);
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_left_centered_border_full_10"), null, tempTaxAmount);
					
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_left_centered_border_full_10"), null, "");
					
					rownum++;
				}
			}else{
				Row tempRow = sheet.createRow(rownum);
				for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
					ExcelUtil.createCellOfRow(tempRow, i, styles.get("cell_left_centered_border_full_10"), null, null);
				}
				rownum++;
			}
			
			Row type1TotalRow = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(type1TotalRow, 0, styles.get("cell_bold_left_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountTotal", locale));
			ExcelUtil.createCellOfRow(type1TotalRow, 1, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 2, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 3, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 4, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 5, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 6, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type1TotalRow, 7, styles.get("cell_bold_left_border_10"), null, "");
			
			rownum++;
			Row type2Row = sheet.createRow(rownum);
			type2Row.setHeight((short)350);
			ExcelUtil.createCellOfRow(type2Row, totalColumnnOfTitle + startColTitle, styles.get("cell_left_centered_border_full_10"), null, null);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(type2Row, 0, styles.get("cell_left_centered_border_full_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceTaxSalesType2", locale));
			
			rownum++;
			if(UtilValidate.isNotEmpty(listDataZeroPercentTax)){
				int i = 0;
				for(Map<String, Object> tempData: listDataZeroPercentTax){
					i++;
					String tempCurrencyUom = (String)tempData.get("currency_id");
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get("invoice_id"));
					
					Timestamp invoiceDate = (Timestamp)tempData.get("invoice_date");
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_border_full_date_10"), null, invoiceDate);
					
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("party_name"));
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.get("tax_code"));
					
					String tempAmount = UtilFormatOut.formatCurrency((BigDecimal)tempData.get("amount"), tempCurrencyUom, locale, 2);
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_left_centered_border_full_10"), null, tempAmount);
					
					String tempTaxAmount = UtilFormatOut.formatCurrency((BigDecimal)tempData.get("tax_amount"), tempCurrencyUom, locale, 2);
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_left_centered_border_full_10"), null, tempTaxAmount);
					
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_left_centered_border_full_10"), null, "");
					
					rownum++;
				}
			}else{
				Row tempRow = sheet.createRow(rownum);
				for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
					ExcelUtil.createCellOfRow(tempRow, i, styles.get("cell_left_centered_border_full_10"), null, null);
				}
				rownum++;
			}
			
			Row type2TotalRow = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(type2TotalRow, 0, styles.get("cell_bold_left_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountTotal", locale));
			ExcelUtil.createCellOfRow(type2TotalRow, 1, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 2, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 3, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 4, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 5, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 6, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type2TotalRow, 7, styles.get("cell_bold_left_border_10"), null, "");
			
			rownum++;
			Row type3Row = sheet.createRow(rownum);
			type3Row.setHeight((short)350);
			ExcelUtil.createCellOfRow(type3Row, totalColumnnOfTitle + startColTitle, styles.get("cell_left_centered_border_full_10"), null, null);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(type3Row, 0, styles.get("cell_left_centered_border_full_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceTaxSalesType3", locale));
			
			rownum++;
			if(UtilValidate.isNotEmpty(listDataFivePercentTax)){
				int i = 0;
				for(Map<String, Object> tempData: listDataFivePercentTax){
					i++;
					String tempCurrencyUom = (String)tempData.get("currency_id");
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get("invoice_id"));
					
					Timestamp invoiceDate = (Timestamp)tempData.get("invoice_date");
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_border_full_date_10"), null, invoiceDate);
					
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("party_name"));
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.get("tax_code"));
					
					String tempAmount = UtilFormatOut.formatCurrency((BigDecimal)tempData.get("amount"), tempCurrencyUom, locale, 2);
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_left_centered_border_full_10"), null, tempAmount);
					
					String tempTaxAmount = UtilFormatOut.formatCurrency((BigDecimal)tempData.get("tax_amount"), tempCurrencyUom, locale, 2);
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_left_centered_border_full_10"), null, tempTaxAmount);
					
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_left_centered_border_full_10"), null, "");
					
					rownum++;
				}
			}else{
				Row tempRow = sheet.createRow(rownum);
				for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
					ExcelUtil.createCellOfRow(tempRow, i, styles.get("cell_left_centered_border_full_10"), null, null);
				}
				rownum++;
			}
			
			Row type3TotalRow = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(type3TotalRow, 0, styles.get("cell_bold_left_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountTotal", locale));
			ExcelUtil.createCellOfRow(type3TotalRow, 1, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 2, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 3, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 4, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 5, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 6, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type3TotalRow, 7, styles.get("cell_bold_left_border_10"), null, "");
			
			rownum++;
			Row type4Row = sheet.createRow(rownum);
			type4Row.setHeight((short)350);
			ExcelUtil.createCellOfRow(type4Row, totalColumnnOfTitle + startColTitle, styles.get("cell_left_centered_border_full_10"), null, null);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle + startColTitle));
			ExcelUtil.createCellOfRow(type4Row, 0, styles.get("cell_left_centered_border_full_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceTaxSalesType4", locale));
			
			rownum++;
			if(UtilValidate.isNotEmpty(listDataTenPercentTax)){
				int i = 0;
				for(Map<String, Object> tempData: listDataTenPercentTax){
					i++;
					String tempCurrencyUom = (String)tempData.get("currency_id");
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get("invoice_id"));
					
					Timestamp invoiceDate = (Timestamp)tempData.get("invoice_date");
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_border_full_date_10"), null, invoiceDate);
					
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("party_name"));
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.get("tax_code"));
					
					String tempAmount = UtilFormatOut.formatCurrency((BigDecimal)tempData.get("amount"), tempCurrencyUom, locale, 2);
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_left_centered_border_full_10"), null, tempAmount);
					
					String tempTaxAmount = UtilFormatOut.formatCurrency((BigDecimal)tempData.get("tax_amount"), tempCurrencyUom, locale, 2);
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_left_centered_border_full_10"), null, tempTaxAmount);
					
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_left_centered_border_full_10"), null, "");
					
					rownum++;
				}
			}else{
				Row tempRow = sheet.createRow(rownum);
				for(int i = 0; i <= totalColumnnOfTitle + startColTitle; i++){
					ExcelUtil.createCellOfRow(tempRow, i, styles.get("cell_left_centered_border_full_10"), null, null);
				}
				rownum++;
			}
			
			Row type4TotalRow = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(type4TotalRow, 0, styles.get("cell_bold_left_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountTotal", locale));
			ExcelUtil.createCellOfRow(type4TotalRow, 1, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type4TotalRow, 2, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type4TotalRow, 3, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type4TotalRow, 4, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type4TotalRow, 5, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type4TotalRow, 6, styles.get("cell_bold_left_border_10"), null, "");
			ExcelUtil.createCellOfRow(type4TotalRow, 7, styles.get("cell_bold_left_border_10"), null, "");
			/**============= ./end body =============*/
			/**============= footer ================*/
			rownum+= 2;
			Row totalVAT1 = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 4));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
			ExcelUtil.createCellOfRow(totalVAT1, 0, styles.get("cell_normal_Left_wrap__top10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "SalesInvoiceReportVATExcelNote1", locale));
			
			rownum++;
			Row totalVAT2 = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 4));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
			ExcelUtil.createCellOfRow(totalVAT2, 0, styles.get("cell_normal_Left_wrap__top10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "SalesInvoiceReportVATExcelNote2", locale));
			
			rownum += 2;
			Row dateMonthYeaRow = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
			ExcelUtil.createCellOfRow(dateMonthYeaRow, 5, styles.get("cell_normal_center_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "DateMonthYearExcel", locale));
			
			rownum++;
			Row taxPayerSignRow = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
			ExcelUtil.createCellOfRow(taxPayerSignRow, 5, styles.get("cell_normal_center_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "TaxPayerExcel", locale));
			
			rownum++;
			Row taxPayerRepresentativeRow = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
			ExcelUtil.createCellOfRow(taxPayerRepresentativeRow, 5, styles.get("cell_normal_center_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "TaxPayerRepresentativeExcel", locale));
			
			rownum ++;
			Row signRow = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
			ExcelUtil.createCellOfRow(signRow, 5, styles.get("cell_normal_center_no_border_10"), null, 
					UtilProperties.getMessage("BaseAccountingUiLabels", "SignedAndSealedExcel", locale));
			/**============= ./end footer ==========*/
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				wb.write(baos);
				byte[] bytes = baos.toByteArray();
				String fileName = "Bangkebanra";
				response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
				response.setContentType("application/vnd.xls");
				response.getOutputStream().write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if (baos != null){
					try {
						baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return "error";
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}
}
