package com.olbius.acc.report.logistics;

import java.io.IOException;
import java.util.HashMap;
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
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.utils.ExcelUtil;

public class ExportFileEvents {
	@SuppressWarnings("unchecked")
	public static void exportExccelInventoryAverageCost(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String facilityId = request.getParameter("facilityId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> context = FastMap.newInstance();
		Map<String, String[]> params = request.getParameterMap();
		Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);
		paramsExtend.put("pagesize", new String[]{"0"});
		paramsExtend.put("sname", new String[]{"getListInventoryAverageCost"});
		context.put("parameters", paramsExtend);
		context.put("userLogin", userLogin);
		context.put("timeZone", timeZone);
		context.put("locale", locale);
		try {
			Map<String, Object> resultService = dispatcher.runSync("jqxGridGeneralServicer", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return;
			}
			List<Map<String, Object>> listData = (List<Map<String, Object>>)resultService.get("results");
			
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
			sheet.setColumnWidth(3, 11 * 180);
			sheet.setColumnWidth(4, 20 * 200);
			sheet.setColumnWidth(5, 20 * 180);
			sheet.setColumnWidth(6, 25 * 200);
			
			/** ================ header ====================*/
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			int rownum = 2;
			Row row = sheet.createRow(rownum);
			row.setHeight((short) 450);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 6));
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_no_border_12"), null, UtilProperties.getMessage("BaseLogisticsUiLabels", "LogInventoryReport", locale).toUpperCase());
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 450);
			String title = UtilProperties.getMessage("BaseLogisticsUiLabels", "Facility", locale);
			if(facility != null){
				title += ": " + facility.getString("facilityName");
			}
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 6));
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_no_border_11"), null, title);
			
			/** ================ ./header ====================*/
			
			/** ================= body ===================*/
			rownum += 2;
			String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
			String titleProductId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductId", locale);
			String titleProductName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductName", locale);
			String titleQtyUomId = UtilProperties.getMessage("ProductUiLabels", "ProductUnitOfMeasure", locale);
			String titleAverageCost = UtilProperties.getMessage("ProductUiLabels", "ProductAverageCost", locale);
			String titleTotalQtyFacility = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTotalQuantityFacility", locale);
			String titleTotalAmountFacility = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTotalAmountFacility", locale);
			
			Row headerRow = sheet.createRow(rownum);
			headerRow.setHeight((short) 500);
			ExcelUtil.createCellOfRow(headerRow, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
			ExcelUtil.createCellOfRow(headerRow, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleProductId);
			ExcelUtil.createCellOfRow(headerRow, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titleProductName);
			ExcelUtil.createCellOfRow(headerRow, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQtyUomId);
			ExcelUtil.createCellOfRow(headerRow, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAverageCost);
			ExcelUtil.createCellOfRow(headerRow, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleTotalQtyFacility);
			ExcelUtil.createCellOfRow(headerRow, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleTotalAmountFacility);
			
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
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.get("quantityUomDesc"));
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get("productAverageCost"));
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_right_centered_border_full_quantity_10"), null, tempData.get("totalQuantityOnHand"));
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.get("totalInventoryCost"));
					rownum++;
				}
			}else{
				Row tempRow = sheet.createRow(rownum);
				tempRow.setHeight((short)400);
				ExcelUtil.createCellOfRow(tempRow, 0, styles.get("cell_normal_centered_border_full_10"), null, null);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
				ExcelUtil.createCellOfRow(tempRow, 0, styles.get("cell_normal_centered_border_full_10"), null, 
						UtilProperties.getMessage("WidgetUiLabels", "wgemptydatastring", locale));
			}
			/** ================ ./body ===================*/
			ExcelUtil.responseWrite(response, wb, "Bao_cao_ton_kho");
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
