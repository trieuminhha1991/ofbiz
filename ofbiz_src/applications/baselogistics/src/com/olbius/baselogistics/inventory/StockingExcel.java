package com.olbius.baselogistics.inventory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.ExcelUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class StockingExcel {

	public final static String RESOURCE = "DmsUiLabels";
	public static String module = StockingExcel.class.getName();
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	public static Map<String, Object> getData(LocalDispatcher dispatcher, Locale locale, GenericValue userLogin,
			TimeZone timeZone, Map<String, String[]> paramsExtend, Integer pagesize, Integer pagenum) {
		Map<String, Object> result = FastMap.newInstance();
		try {
			paramsExtend.put("pagenum", new String[] { pagenum.toString() });
			paramsExtend.put("pagesize", new String[] { pagesize.toString() });
			paramsExtend.put("sname", new String[] { "JQGetListStockEventAggregated" });
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("parameters", paramsExtend);
			context.put("userLogin", userLogin);
			context.put("timeZone", timeZone);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("jqxGridGeneralServicer", context);
			if (ServiceUtil.isSuccess(resultService)) {
				result.put("listIterator", resultService.get("results"));
				result.put("TotalRows", resultService.get("TotalRows"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static void export(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			Locale locale = UtilHttp.getLocale(request);
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			TimeZone timeZone = UtilHttp.getTimeZone(request);

			Map<String, String[]> params = request.getParameterMap();
			Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);

			String eventId = paramsExtend.get("eventId") != null ? ((String[]) paramsExtend.get("eventId"))[0] : null;
			GenericValue stockEvent = delegator.findOne("StockEvent", UtilMisc.toMap("eventId", eventId), false);
			Object facilityId = stockEvent.get("facilityId");
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);

			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = ExcelUtil.createStylesNormal(wb);

			Sheet sheet = sheetSetting(wb, "Total");
			int rownum = ExcelUtil.insertLogo(wb, sheet);

			Row row = sheet.createRow(rownum);
			Cell cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue(facility.getString("facilityName"));
			cell.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum++;

			row = sheet.createRow(rownum);
			cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue(facility.getString("productStoreId"));
			cell.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum++;

			row = sheet.createRow(rownum);
			row.setHeight((short) 600);
			cell = row.createCell(4);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 5));
			cell.setCellValue(
					UtilProperties.getMessage("BaseLogisticsUiLabels", "PhysicalInventory", locale).toUpperCase());
			cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			rownum++;

			List<String> titles = FastList.newInstance(); 
			
			titles.add(UtilProperties.getMessage("DmsUiLabels", "DmsSequenceId", locale));
			titles.add(UtilProperties.getMessage("BaseSalesUiLabels", "BSProductId", locale));
			titles.add(UtilProperties.getMessage("BaseSalesUiLabels", "BSProductName", locale));
			titles.add(UtilProperties.getMessage("BaseLogisticsUiLabels", "BLCategoryProduct", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "DmsSoLuongKiem", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "DmsSLKiemCheo", locale));
			titles.add(UtilProperties.getMessage("BaseLogisticsUiLabels", "QOH", locale));

			titles.addAll(UtilMisc.toList(UtilProperties.getMessage("BaseLogisticsUiLabels", "Deviation", locale),
					UtilProperties.getMessage("BaseLogisticsUiLabels", "UnitPrice", locale),
					UtilProperties.getMessage("WebPosSettingUiLabels", "SettingTotalCostDiff", locale)));

			row = sheet.createRow(rownum);
			row.setHeight((short) 900);
			for (String t : titles) {
				cell = row.createCell(titles.indexOf(t));
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(t.toUpperCase());
				cell.setCellStyle(styles.get("cell_yellow_bold_centered_wrap_text_bordered_10"));
			}
			rownum++;

			Integer pagesize = 100;
			int count = 0;
			Integer pagenum = 0;
			boolean _continue = true;

			while (_continue) {
				Map<String, Object> data = getData(dispatcher, locale, userLogin, timeZone, paramsExtend, pagesize,
						pagenum);
				pagenum++;
				List<GenericValue> listIterator = (List<GenericValue>) data.get("listIterator");
				int totalRows = Integer.valueOf((String) data.get("TotalRows"));
				if (UtilValidate.isEmpty(listIterator)) {
					_continue = false;
				}
				if (count >= totalRows) {
					_continue = false;
				}
				for (GenericValue x : listIterator) {
					int _count = 0;
					count++;
					row = sheet.createRow(rownum);

					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
					cell.setCellValue(count);
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					_count++;

					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(x.getString("productCode"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					_count++;

					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(x.getString("productName"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					_count++;
					
					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(x.getString("primaryProductCategoryId"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					_count++;

					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (UtilValidate.isNotEmpty(x.getBigDecimal("quantity"))) {
						cell.setCellValue(x.getBigDecimal("quantity").doubleValue());
					} else {
						cell.setCellValue(0);
					}
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					_count++;

					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (UtilValidate.isNotEmpty(x.getBigDecimal("quantityRecheck"))) {
						cell.setCellValue(x.getBigDecimal("quantityRecheck").doubleValue());
					} else {
						cell.setCellValue(0);
					}
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					_count++;

					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (UtilValidate.isNotEmpty(x.getBigDecimal("lastInventoryCount"))) {
						cell.setCellValue(x.getBigDecimal("lastInventoryCount").doubleValue());
					} else {
						cell.setCellValue(0);
					}
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					_count++;

					BigDecimal quantityDifference = x.getBigDecimal("quantityDifference");
					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
					if (UtilValidate.isNotEmpty(quantityDifference)) {
						cell.setCellValue(quantityDifference.doubleValue());
					} else {
						cell.setCellValue(0);
					}
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					_count++;

					BigDecimal unitPrice = x.getBigDecimal("unitPrice");
					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
					if (UtilValidate.isNotEmpty(unitPrice)) {
						cell.setCellValue(unitPrice.doubleValue());
					} else {
						cell.setCellValue(0);
					}
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					_count++;

					BigDecimal priceDifference = BigDecimal.ZERO;
					if (UtilValidate.isNotEmpty(quantityDifference)) {
						if (UtilValidate.isNotEmpty(unitPrice)) {
							priceDifference = quantityDifference.multiply(unitPrice);
						}
					}
					cell = row.createCell(_count);
					cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
					if (UtilValidate.isNotEmpty(priceDifference)) {
						cell.setCellValue(priceDifference.doubleValue());
					} else {
						cell.setCellValue(0);
					}
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					_count++;

					rownum++;
				}
			}
			ExcelUtil.responseWrite(response, wb, "kiem-ke-" + facilityId + "-");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Sheet sheetSetting(Workbook wb, String sheetName) {
		Sheet sheet = wb.createSheet(sheetName);
		CellStyle csWrapText = wb.createCellStyle();
		csWrapText.setWrapText(true);

		// turn on gridLines
		sheet.setDisplayGridlines(true);
		sheet.setPrintGridlines(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		sheet.setAutobreaks(true);

		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1);

		// A
		sheet.setColumnWidth(0, 10 * 256);
		// B
		sheet.setColumnWidth(1, 25 * 256);
		// C
		sheet.setColumnWidth(2, 40 * 256);
		// D
		for (int i = 3; i < 20; i++) {
			sheet.setColumnWidth(i, 20 * 256);
		}
		return sheet;
	}

}