package com.olbius.acc.report.olap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.acc.utils.ExcelUtil;

public class ExcelAccountingReport {
	 public static final String module = ExcelAccountingReport.class.getName();
	 public static final String resource = "widgetUiLabels";
	 public static final String resourceError = "widgetErrorUiLabels";
	 //private static JSONArray costCenters;
	 
	@SuppressWarnings("unchecked")
	public static void exportImpExpStockWarehouseOlapToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Timestamp thruDate = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("productId", productId);
		context.put("fromDate", fromDate);
		context.put("thruDate", thruDate);
		context.put("facilityId", facilityId);
		context.put("userLogin", userLogin);
		context.put("limit", 0l);
		context.put("offset", 0l);
		context.put("init", Boolean.TRUE);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("jqGetListImpExpStockWarehouseReportOlap", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
			
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
			
			sheet.setColumnWidth(0, 7 * 180);
			sheet.setColumnWidth(1, 15 * 250);
			sheet.setColumnWidth(2, 22 * 400);
			sheet.setColumnWidth(3, 15 * 250);
			sheet.setColumnWidth(4, 22 * 350);
			sheet.setColumnWidth(5, 11 * 180);
			sheet.setColumnWidth(6, 14 * 180);
			sheet.setColumnWidth(7, 20 * 200);
			sheet.setColumnWidth(8, 14 * 180);
			sheet.setColumnWidth(9, 20 * 200);
			sheet.setColumnWidth(10, 14 * 180);
			sheet.setColumnWidth(11, 20 * 200);
			sheet.setColumnWidth(12, 14 * 180);
			sheet.setColumnWidth(13, 20 * 200);
			
			/** ================ header ====================*/
			int rownum = 2;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 450);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 8));
			ExcelUtil.createCellOfRow(titleRow, 3, styles.get("cell_bold_centered_no_border_12"), null, 
					UtilProperties.getMessage("BaseLogisticsUiLabels", "InventoryReportTotal", locale));
			
			rownum++;
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
			String dateTimeFormat = "dd/MM/yyyy";
			String dateRowContent = UtilProperties.getMessage("BaseAccountingUiLabels", "ExcelFromDate", locale) + " " 
					+ UtilFormatOut.formatDateTime(fromDate, dateTimeFormat, locale, timeZone) +" "
					+ UtilProperties.getMessage("BaseAccountingUiLabels", "ExcelThruDate", locale) + " "
					+ UtilFormatOut.formatDateTime(thruDate, dateTimeFormat, locale, timeZone);
			ExcelUtil.createCellOfRow(dateRow, 4, styles.get("cell_centered_no_border_10"), null, dateRowContent);
			/** ================ ./header ==================*/
			
			/** ================= body ===================*/
			rownum += 2;
			String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
			String titleProductId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductId", locale);
			String titleProductName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductName", locale);
			String titleFacilityId= UtilProperties.getMessage("BaseLogisticsUiLabels", "FacilityId", locale);
			String titleFacilityName = UtilProperties.getMessage("BaseLogisticsUiLabels", "FacilityName", locale);
			String titleQtyUomId = UtilProperties.getMessage("ProductUiLabels", "ProductUnitOfMeasure", locale);
			String titleQuantity = UtilProperties.getMessage("AccountingUiLabels", "AccountingQuantity", locale);
			String titleAmount = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmount", locale);
			String titleOpeningStock = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpeningStock", locale);
			String titleImportStock = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCImportStock", locale);
			String titleExportStock = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCExportStock", locale);
			String titleEndingStock = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEndingStock", locale);
			Row headerRow1 = sheet.createRow(rownum);
			headerRow1.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 7));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 8, 9));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 10, 11));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 12, 13));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 0, 0));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 1, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 2, 2));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 3, 3));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 4, 4));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 5, 5));
			
			ExcelUtil.createCellOfRow(headerRow1, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
			ExcelUtil.createCellOfRow(headerRow1, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleProductId);
			ExcelUtil.createCellOfRow(headerRow1, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titleProductName);
			ExcelUtil.createCellOfRow(headerRow1, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFacilityId);
			ExcelUtil.createCellOfRow(headerRow1, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFacilityName);
			ExcelUtil.createCellOfRow(headerRow1, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQtyUomId);
			ExcelUtil.createCellOfRow(headerRow1, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleOpeningStock);
			ExcelUtil.createCellOfRow(headerRow1, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleImportStock);
			ExcelUtil.createCellOfRow(headerRow1, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleExportStock);
			ExcelUtil.createCellOfRow(headerRow1, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, titleEndingStock);
			ExcelUtil.createCellOfRow(headerRow1, 13, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			
			rownum++;
			Row headerRow2 = sheet.createRow(rownum);
			headerRow2.setHeight((short) 400);
			ExcelUtil.createCellOfRow(headerRow2, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQuantity);
			ExcelUtil.createCellOfRow(headerRow2, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAmount);
			ExcelUtil.createCellOfRow(headerRow2, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQuantity);
			ExcelUtil.createCellOfRow(headerRow2, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAmount);
			ExcelUtil.createCellOfRow(headerRow2, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQuantity);
			ExcelUtil.createCellOfRow(headerRow2, 11, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAmount);
			ExcelUtil.createCellOfRow(headerRow2, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQuantity);
			ExcelUtil.createCellOfRow(headerRow2, 13, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAmount);
			
			rownum++;
			if(UtilValidate.isNotEmpty(listData)){
				int i = 0;
				for(Map<String, Object> tempData: listData){
					i++;
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.get("productCode"));
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_centered_border_full_10"), null, tempData.get("productName"));
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("facilityId"));
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.get("facilityName"));
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_left_centered_border_full_10"), null, tempData.get("quantityUomId"));
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_right_centered_border_full_quantity_10"), null, tempData.get("openingQuantity"));
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get("openingAmount"));
					ExcelUtil.createCellOfRow(tempDataRow, 8, styles.get("cell_right_centered_border_full_quantity_10"), null, tempData.get("importQuantity"));
					ExcelUtil.createCellOfRow(tempDataRow, 9, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get("importAmount"));
					ExcelUtil.createCellOfRow(tempDataRow, 10, styles.get("cell_right_centered_border_full_quantity_10"), null, tempData.get("exportQuantity"));
					ExcelUtil.createCellOfRow(tempDataRow, 11, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get("exportAmount"));
					ExcelUtil.createCellOfRow(tempDataRow, 12, styles.get("cell_right_centered_border_full_quantity_10"), null, tempData.get("endingQuantity"));
					ExcelUtil.createCellOfRow(tempDataRow, 13, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get("endingAmount"));
					rownum++;
				}
			}else{
				Row tempRow = sheet.createRow(rownum);
				tempRow.setHeight((short)400);
				ExcelUtil.createCellOfRow(tempRow, 0, styles.get("cell_normal_centered_border_full_10"), null, null);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 13));
				ExcelUtil.createCellOfRow(tempRow, 0, styles.get("cell_normal_centered_border_full_10"), null, 
						UtilProperties.getMessage("WidgetUiLabels", "wgemptydatastring", locale));
			}
			/** ================= body ===================*/
			ExcelUtil.responseWrite(response, wb, "Bao_cao_xuat_nhap_ton");
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if(baos != null)baos.close();
		}
	}

}
