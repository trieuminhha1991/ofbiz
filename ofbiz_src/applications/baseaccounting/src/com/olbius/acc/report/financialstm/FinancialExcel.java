package com.olbius.acc.report.financialstm;

import com.olbius.acc.excel.VoucherDeclarationExcel;
import com.olbius.basesales.util.ExcelUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

public class FinancialExcel {

	public final static String RESOURCE = "BaseAccountingUiLabels";
	public static List<Map<String, Object>> listData;

	public static void export(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		try {
			String organizationPartyId = request.getParameter("organizationPartyId");
			String customTimePeriodId = request.getParameter("customTimePeriodId");
			String reportTypeId = request.getParameter("reportTypeId");
			String flag = request.getParameter("flag");
			
			Map<String, Object> info = VoucherDeclarationExcel.info(request, delegator, userLogin, locale);

			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = ExcelUtil.createStylesNormal(wb);

			Sheet sheet = sheetSetting(wb, "Sheet1");
			int rownum = 0;

			Row row = sheet.createRow(rownum);
			Cell cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue(info.get("groupName").toString().toUpperCase());
			cell.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum++;

			row = sheet.createRow(rownum);
			cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue((String) info.get("companyAddress"));
			cell.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum++;
			
			rownum += 2;

			row = sheet.createRow(rownum);
			row.setHeight((short) 400);
			cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 4));
			String title = "BACCFSBalanceSheet";
			if ("9000".equals(reportTypeId)) {
				title = "BACCFSBalanceSheet";
			} else if ("9001".equals(reportTypeId)) {
				title = "BACCFSIncomeStatement";
			} else if ("9002".equals(reportTypeId)) {
				title = "BACCFSCashflow";
			}
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, title, locale).toUpperCase());
			cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 4));
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCTT200QD48", locale));
			cell.setCellStyle(styles.get("cell_normal_centered_no_border_10"));
			rownum++;

			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			Cell cellDate = row.createCell(0);
			cellDate.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 4));
			cellDate.setCellStyle(styles.get("cell_normal_centered_no_border_10"));
			rownum++;

			rownum += 2;
			
			String periodName = PeriodUtils.getPeriodName(customTimePeriodId, delegator);
			String previousPeriodName = PeriodUtils.getPreviousPeriodName(customTimePeriodId, delegator);

			List<String> titles = UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BACCTarget", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCode", locale),
					UtilProperties.getMessage(RESOURCE, "BACCDemonstration", locale),
					periodName, previousPeriodName);

			row = sheet.createRow(rownum);
			row.setHeight((short) 700);
			for (int i = 0; i < titles.size(); i++) {
				cell = row.createCell(i);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(titles.get(i));
				cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
			}
			rownum++;
			
			row = sheet.createRow(rownum);
			cell = row.createCell(0);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue("A");
			cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
			
			cell = row.createCell(1);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue("B");
			cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
			
			cell = row.createCell(2);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue("C");
			cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
			
			cell = row.createCell(3);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue("1");
			cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
			
			cell = row.createCell(4);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			cell.setCellValue("2");
			cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
			
			rownum++;

			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			String date = "";
			if (UtilValidate.isNotEmpty(customTimePeriod)) {
				date += UtilProperties.getMessage(RESOURCE, "ExcelFromDate", locale) + ": " + customTimePeriod.getDate("fromDate") + " ";
				date += UtilProperties.getMessage(RESOURCE, "ExcelThruDate", locale).toLowerCase() + ": " + customTimePeriod.getDate("thruDate");
			}
			cellDate.setCellValue(date);

			Map<String, Object> parameters = FastMap.newInstance();
			parameters.put("customTimePeriodId", customTimePeriodId);
			parameters.put("organizationPartyId", organizationPartyId);
			parameters.put("reportTypeId", reportTypeId);
			parameters.put("flag", flag);
			
			FinancialReportBuilder builder = new FinancialReportBuilder();
			JSONArray jsonData = builder.convertToJsonArray(parameters, delegator);
			listData = FastList.newInstance();
			convertData(jsonData);
			
			Collections.sort(listData, new Comparator<Map<String, Object>> () {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					BigDecimal code1 = (BigDecimal) o1.get("orderIndex");
					BigDecimal code2 = (BigDecimal) o2.get("orderIndex");
					return code1.compareTo(code2);
				}
			});

			if (UtilValidate.isNotEmpty(listData)) {
				for (Map<String, Object> item : listData) {
					row = sheet.createRow(rownum);
					
					String isLeaf = (String) item.get("isLeaf");
					BigDecimal value1 = (BigDecimal) item.get("value1");
					BigDecimal value2 = (BigDecimal) item.get("value2");
					String rowStyle = "";
					String rowNumberStyle = "";
					String rowNumberZeroStyle = "";
					if ("Y".equals(isLeaf)) {
						rowStyle = "cell_normal_bordered_10";
						rowNumberStyle = "cell_number_normal_bordered_10";
						rowNumberZeroStyle = "cell_number_zero_normal_bordered_10";
					} else {
						rowStyle = "cell_left_wrap_text_bordered_10";
						rowNumberStyle = "cell_number_bordered_10";
						rowNumberZeroStyle = "cell_number_zero_bordered_10";
					}
					
					cell = row.createCell(0);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (item.get("name") != null) {
						cell.setCellValue((String) item.get("name"));
					}
					cell.setCellStyle(styles.get(rowStyle));

					cell = row.createCell(1);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (item.get("code") != null) {
						cell.setCellValue((String) item.get("code"));
					}
					cell.setCellStyle(styles.get(rowStyle));
					
					cell = row.createCell(2);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (item.get("demonstration") != null) {
						cell.setCellValue((String) item.get("demonstration"));
					}
					cell.setCellStyle(styles.get(rowStyle));

					cell = row.createCell(3);
					if (UtilValidate.isNotEmpty(item.get("value1"))) {
						cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(value1.toString()));
						if (value1.compareTo(BigDecimal.ZERO) != 0) {
							cell.setCellStyle(styles.get(rowNumberStyle));
						} else {
							cell.setCellStyle(styles.get(rowNumberZeroStyle));
						}
					} else {
						cell.setCellStyle(styles.get(rowStyle));
					}

					cell = row.createCell(4);
					if (UtilValidate.isNotEmpty(item.get("value2"))) {
						cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(value2.toString()));
						if (value2.compareTo(BigDecimal.ZERO) != 0) {
							cell.setCellStyle(styles.get(rowNumberStyle));
						} else {
							cell.setCellStyle(styles.get(rowNumberZeroStyle));
						}
					} else {
						cell.setCellStyle(styles.get(rowStyle));
					}

					rownum++;
				}
			}
			String fileName = "";
			if ("9000".equals(reportTypeId)) {
				fileName = "bang-can-doi-ke-toan-";
			} else if ("9001".equals(reportTypeId)) {
				fileName = "bao-cao-ket-qua-hoat-dong-kinh-doanh-";
			} else if ("9002".equals(reportTypeId)) {
				fileName = "bao-cao-luu-chuyen-tien-te-";
			}
			ExcelUtil.responseWrite(response, wb, fileName);

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
		
		sheet.setColumnWidth(0, 50 * 256);
		sheet.setColumnWidth(1, 15 * 256);
		sheet.setColumnWidth(2, 15 * 256);
		for (int i = 3; i < 50; i++) {
			sheet.setColumnWidth(i, 25 * 256);
		}
		return sheet;
	}
	
	private static void convertData(JSONArray data) {
		for (int i = 0; i < data.size(); i++) {
			JSONObject item = (JSONObject) data.get(i);
			JSONArray children = item.getJSONArray("children");
			Map<String, Object> map = FastMap.newInstance();
			map.put("name", item.getString("name"));
			map.put("code", item.getString("code"));
			map.put("demonstration", item.getString("demonstration"));
			map.put("value1", new BigDecimal(item.getString("value1")));
			map.put("value2", new BigDecimal(item.getString("value2")));
			map.put("orderIndex", new BigDecimal(item.getString("orderIndex")));
			if (children.size() > 0) {
				map.put("isLeaf", "N");
			} else {
				map.put("isLeaf", "Y");
			}
			listData.add(map);
			if (children.size() > 0) {
				convertData(children);
			}
		}
	}
}