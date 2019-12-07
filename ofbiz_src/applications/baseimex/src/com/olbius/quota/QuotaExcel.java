package com.olbius.quota;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.ExcelUtil;
import com.olbius.product.util.ProductUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class QuotaExcel {
	public final static String RESOURCE = "BaseImExUiLabels";
	public final static String RESOURCE_LOG = "BaseLogisticsUiLabels";
	public static String module = QuotaExcel.class.getName();
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	public static Map<String, Object> getData(Delegator delegator, GenericValue userLogin, String quotaId) {
		Map<String, Object> result = FastMap.newInstance();
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("quotaId", quotaId));
			List<GenericValue> listProducts = FastList.newInstance();
			try {
				listProducts = delegator.findList("QuotaItemAndProductAvailable", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList QuotaItemAndProductAvailable: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			result.put("listProducts", listProducts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static void export(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			Locale locale = UtilHttp.getLocale(request);
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

			Map<String, String[]> params = request.getParameterMap();
			Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);

			String quotaId = paramsExtend.get("quotaId") != null ? ((String[]) paramsExtend.get("quotaId"))[0] : null;
			GenericValue objQuotaHeader = null;
			try {
				objQuotaHeader = delegator.findOne("QuotaHeader", false, UtilMisc.toMap("quotaId", quotaId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne QuotaHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
			
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = ExcelUtil.createStylesNormal(wb);

			Sheet sheet = sheetSetting(wb, "Total");
			int rownum = ExcelUtil.insertLogo(wb, sheet);

			Row row = sheet.createRow(rownum);
			row.setHeight((short) 700);
			Cell cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BIEListProductQuota", locale));
			cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 4));
			
			rownum++;

			row = sheet.createRow(rownum);
			cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
			cell.setCellValue(format.format(objQuotaHeader.getTimestamp("createdDate")));
			cell.setCellStyle(styles.get("cell_bold_centered_no_border_8"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
			rownum++;

			List<String> titles = FastList.newInstance(); 
			
			titles.add(UtilProperties.getMessage(RESOURCE_LOG, "SequenceId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE_LOG, "ProductId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE_LOG, "ProductName", locale));
			titles.add(UtilProperties.getMessage(RESOURCE_LOG, "Unit", locale));
			titles.add(UtilProperties.getMessage(RESOURCE_LOG, "Quantity", locale));

			row = sheet.createRow(rownum);
			row.setHeight((short) 800);
			for (String t : titles) {
				cell = row.createCell(titles.indexOf(t));
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(t.toUpperCase());
				cell.setCellStyle(styles.get("cell_yellow_bold_centered_wrap_text_bordered_10"));
			}
			rownum++;
			sheet.createFreezePane(5, 3);
			int count = 0;

			Map<String, Object> data = getData(delegator, userLogin, quotaId);
			List<GenericValue> listIterator = (List<GenericValue>) data.get("listProducts");
			for (GenericValue x : listIterator) {
				String productId = x.getString("productId");
				String uomId = x.getString("uomId");
				GenericValue objUom = null;
				try {
					objUom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", uomId));
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findOne Uom: " + e.toString();
					Debug.logError(e, errMsg, module);
				}
				boolean isWeight = ProductUtil.isWeightProduct(delegator, productId);
				int _count = 0;
				count++;
				row = sheet.createRow(rownum);

				cell = row.createCell(_count);
				cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
				cell.setCellValue(count);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				sheet.setColumnWidth(_count, 8*256);
				_count++;
				

				cell = row.createCell(_count);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(x.getString("productCode"));
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				sheet.setColumnWidth(_count, 15*256);
				_count++;

				cell = row.createCell(_count);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(x.getString("productName"));
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				sheet.setColumnWidth(_count, 40*256);
				_count++;
				
				cell = row.createCell(_count);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				if (UtilValidate.isNotEmpty(objUom)) {
					cell.setCellValue(objUom.getString("description"));
				} else {
					cell.setCellValue(uomId);
				}
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				sheet.setColumnWidth(_count, 15*256);
				_count++;
				
				cell = row.createCell(_count);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				if (UtilValidate.isNotEmpty(x.getBigDecimal("quotaQuantity"))) {
					if (isWeight){
						cell.setCellValue(x.getBigDecimal("quotaQuantity").doubleValue());
					} else {
						cell.setCellValue(x.getBigDecimal("quotaQuantity").intValue());
					}
				} else {
					cell.setCellValue(0);
				}
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				sheet.setColumnWidth(_count, 20*256);
				_count++;
				
				rownum++;
			}
			ExcelUtil.responseWrite(response, wb, "SAN-PHAM-XIN-HAN-NGACH-" + quotaId + "-");
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
