package com.olbius.acc.excel;

import com.olbius.basesales.util.ExcelUtil;
import javolution.util.FastList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CashDetailBookExcel {

	public final static String RESOURCE = "BaseAccountingUiLabels";
	public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

	@SuppressWarnings("unchecked")
	public static void export(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		try {
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
			rownum++;

			row = sheet.createRow(rownum);
			row.setHeight((short) 400);
			cell = row.createCell(3);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 6));
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCCashDetailBook", locale).toUpperCase());
			cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum++;

			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			Cell cellDate = row.createCell(3);
			cellDate.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 6));
			cellDate.setCellStyle(styles.get("cell_bold_centered_no_border_10"));
			rownum++;

			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			Cell cellAccountId = row.createCell(3);
			cellAccountId.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 6));
			cellAccountId.setCellStyle(styles.get("cell_bold_centered_no_border_10"));

			rownum++;
			rownum++;
			rownum++;

			List<String> titles = UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BACCTransDate", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCashVoucherDate", locale),
					UtilProperties.getMessage(RESOURCE, "BACCReceipt", locale),
					UtilProperties.getMessage(RESOURCE, "BACCPayment", locale),
					UtilProperties.getMessage(RESOURCE, "BACCDescription", locale),
					UtilProperties.getMessage(RESOURCE, "BACCRecipGlAccountId", locale));

			titles.addAll(UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BACCDebitAmount", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCreditAmount", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCashBalance", locale),
					UtilProperties.getMessage(RESOURCE, "BACCAcctgTransTypeId", locale),
					UtilProperties.getMessage(RESOURCE, "BACCNote", locale)));

			int rownum_group = rownum + 1;
			row = sheet.createRow(rownum);
			Row row_group = sheet.createRow(rownum_group);
			Cell cell_dummy = null;
			row.setHeight((short) 900);
			for (String t : titles) {
				int index = titles.indexOf(t);

				cell = row.createCell(index);
				List<Integer> idxs = UtilMisc.toList(0, 1, 4, 5, 8, 9);
				idxs.add(10);
				if (idxs.contains(index)) {
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum_group, index, index));
					cell_dummy = row_group.createCell(index);
					cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
				} else {
					if (index == 2) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCCashVoucher", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					} else if (index == 6) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCArisingAmount", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					}

					cell = row_group.createCell(index);
				}
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(t);
				cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
			}
			rownum++;
			rownum++;

			String fromDate = request.getParameter("fromDate");
			String thruDate = request.getParameter("thruDate");
			String glAccountId = request.getParameter("glAccountId");

			Map<String, String[]> parameters = new HashMap<>();

			String date = "";
			if (fromDate != null) {
				parameters.put("fromDate", new String[] { fromDate });
				date += UtilProperties.getMessage(RESOURCE, "ExcelFromDate", locale) + ": " + fromDate + " ";
			}

			if (thruDate != null) {
				parameters.put("thruDate", new String[] { thruDate });
				date += UtilProperties.getMessage(RESOURCE, "ExcelThruDate", locale).toLowerCase() + ": " + thruDate;
			}
			cellDate.setCellValue(date);

			if (glAccountId != null) {
				parameters.put("glAccountId", new String[] { glAccountId });
				GenericValue glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId),
						true);
				if (glAccount != null) {
					cellAccountId.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCGlAccountId", locale) + ": "
							+ glAccountId + " - " + glAccount.getString("accountName"));
				}
			}

			Map<String, Object> rs = dispatcher.runSync("JqxGetCashDetailBook",
					UtilMisc.toMap("userLogin", userLogin, "opts", new EntityFindOptions(), "parameters", parameters,
							"listAllConditions", FastList.newInstance(), "listSortFields", FastList.newInstance()));

			if (ServiceUtil.isSuccess(rs)) {
				List<Map<String, Object>> dummy = (List<Map<String, Object>>) rs.get("listIterator");
				BigDecimal endingAmount = BigDecimal.ZERO;
				for (Map<String, Object> g : dummy) {
					row = sheet.createRow(rownum);

					cell = row.createCell(0);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("transDate") != null) {
						cell.setCellValue(format.format((Date) g.get("transDate")));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(1);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("voucherDate") != null) {
						cell.setCellValue(format.format((Timestamp) g.get("voucherDate")));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(2);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue((String) g.get("receiptVoucherID"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(3);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue((String) g.get("payVoucherID"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(4);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue((String) g.get("voucherDescription"));
					if (dummy.indexOf(g) == 0) {
						cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(5);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue((String) g.get("recipGlAccountCode"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					cell = row.createCell(6);
					ExcelUtil.setCellNumber(cell, styles, (BigDecimal) g.get("debitAmount"));

					cell = row.createCell(7);
					ExcelUtil.setCellNumber(cell, styles, (BigDecimal) g.get("creditAmount"));

					cell = row.createCell(8);
					endingAmount = (BigDecimal) g.get("balAmount");
					ExcelUtil.setCellNumber(cell, styles, endingAmount);

					cell = row.createCell(9);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue((String) g.get("voucherType"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					cell = row.createCell(10);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue((String) g.get("note"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					rownum++;
				}
				//ending amount
				row = sheet.createRow(rownum);
				cell = row.createCell(0);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(1);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

				cell = row.createCell(2);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(3);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(4);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCSoDuCuoiKy", locale));
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(5);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(6);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(7);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(8);
				ExcelUtil.setCellNumber(cell, styles, endingAmount);
				
				cell = row.createCell(9);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(10);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
			}

			ExcelUtil.responseWrite(response, wb, "so-chi-tiet-tien-mat-");

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

		for (int i = 0; i < 50; i++) {
			sheet.setColumnWidth(i, 25 * 256);
		}
		return sheet;
	}
}