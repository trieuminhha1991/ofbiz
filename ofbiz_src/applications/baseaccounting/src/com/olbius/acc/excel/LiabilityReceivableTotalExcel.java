package com.olbius.acc.excel;

import com.olbius.basesales.util.ExcelUtil;
import javolution.util.FastList;
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
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LiabilityReceivableTotalExcel {

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
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCLiabilityReceivableTotal", locale).toUpperCase());
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
			
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			Cell cellParty = row.createCell(3);
			cellParty.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 6));
			cellParty.setCellStyle(styles.get("cell_bold_centered_no_border_10"));
			rownum++;

			rownum++;
			rownum++;
			rownum++;

			List<String> titles = UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BACCSeqId", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCustomerId", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCustomerName", locale));
			titles.addAll(UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BACCDebitAmount", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCreditAmount", locale),
					UtilProperties.getMessage(RESOURCE, "BACCDebitAmount", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCreditAmount", locale),
					UtilProperties.getMessage(RESOURCE, "BACCDebitAmount", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCreditAmount", locale)));

			int rownum_group = rownum + 1;
			row = sheet.createRow(rownum);
			Row row_group = sheet.createRow(rownum_group);
			Cell cell_dummy = null;
			row.setHeight((short) 900);
			for (int i = 0; i < titles.size(); i++) {
				int index = i;

				cell = row.createCell(index);

				if (UtilMisc.toList(0, 1, 2).contains(index)) {
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum_group, index, index));
					cell_dummy = row_group.createCell(index);
					cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
				} else {
					if (index == 3) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCOpeningAmount", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					} else if (index == 5) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCPostingAmount", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					} else if (index == 7) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCEndingAmount", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					}

					cell = row_group.createCell(index);
				}
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(titles.get(index));
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
				GenericValue glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), false);
				if (glAccount != null) {
					cellAccountId.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCGlAccountId", locale) + ": "
							+ glAccountId + " - " + glAccount.getString("accountName"));
				}
			} else {
				cellAccountId.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCGlAccountId", locale) + ": ");
			}

			Map<String, Object> rs = dispatcher.runSync("JqxGetLiabilityReceivableTotal",
					UtilMisc.toMap("userLogin", userLogin, "opts", new EntityFindOptions(), "parameters", parameters,
							"listAllConditions", FastList.newInstance(), "listSortFields", FastList.newInstance()));

			if (ServiceUtil.isSuccess(rs)) {
				List<Map<String, Object>> dummy = (List<Map<String, Object>>) rs.get("listIterator");
				int seq = 1;
				BigDecimal totalOpeningCreditAmount = BigDecimal.ZERO;
				BigDecimal totalOpeningDebitAmount = BigDecimal.ZERO;
				BigDecimal totalPostingCreditAmount = BigDecimal.ZERO;
				BigDecimal totalPostingDebitAmount = BigDecimal.ZERO;
				BigDecimal totalEndingCreditAmount = BigDecimal.ZERO;
				BigDecimal totalEndingDebitAmount = BigDecimal.ZERO;
				for (Map<String, Object> g : dummy) {
					row = sheet.createRow(rownum);
					
					cell = row.createCell(0);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(seq++);
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					cell = row.createCell(1);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("partyCode") != null) {
						cell.setCellValue((String) g.get("partyCode"));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					cell = row.createCell(2);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("partyName") != null) {
						cell.setCellValue((String) g.get("partyName"));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
										
					BigDecimal openingCreditAmount = (BigDecimal) g.get("openingCreditAmount");
					BigDecimal openingDebitAmount = (BigDecimal) g.get("openingDebitAmount");
					BigDecimal postingCreditAmount = (BigDecimal) g.get("postingCreditAmount");
					BigDecimal postingDebitAmount = (BigDecimal) g.get("postingDebitAmount");
					BigDecimal endingCreditAmount = (BigDecimal) g.get("endingCreditAmount");
					BigDecimal endingDebitAmount = (BigDecimal) g.get("endingDebitAmount");
					
					totalOpeningCreditAmount = totalOpeningCreditAmount.add(openingCreditAmount);
					totalOpeningDebitAmount = totalOpeningDebitAmount.add(openingDebitAmount);
					totalPostingCreditAmount = totalPostingCreditAmount.add(postingCreditAmount);
					totalPostingDebitAmount = totalPostingDebitAmount.add(postingDebitAmount);
					totalEndingCreditAmount = totalEndingCreditAmount.add(endingCreditAmount);
					totalEndingDebitAmount = totalEndingDebitAmount.add(endingDebitAmount);
					
					cell = row.createCell(3);
					if (UtilValidate.isNotEmpty(openingDebitAmount) && !openingDebitAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, openingDebitAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(4);
					if (UtilValidate.isNotEmpty(openingCreditAmount) && !openingCreditAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, openingCreditAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(5);
					if (UtilValidate.isNotEmpty(postingDebitAmount) && !postingDebitAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, postingDebitAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(6);
					if (UtilValidate.isNotEmpty(postingCreditAmount) && !postingCreditAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, postingCreditAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(7);
					if (UtilValidate.isNotEmpty(endingDebitAmount) && !endingDebitAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, endingDebitAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(8);
					if (UtilValidate.isNotEmpty(endingCreditAmount) && !endingCreditAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, endingCreditAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					rownum++;
				}
				
				//total
				row = sheet.createRow(rownum);
				cell = row.createCell(0);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(1);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				
				cell = row.createCell(2);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCAmountTotal", locale));
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(3);
				if (!totalOpeningDebitAmount.equals(BigDecimal.ZERO)) {
					ExcelUtil.setCellNumber(cell, styles, totalOpeningDebitAmount);
				} else {
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue("-");
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				}
				
				cell = row.createCell(4);
				if (!totalOpeningCreditAmount.equals(BigDecimal.ZERO)) {
					ExcelUtil.setCellNumber(cell, styles, totalOpeningCreditAmount);
				} else {
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue("-");
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				}
				
				cell = row.createCell(5);
				if (!totalPostingDebitAmount.equals(BigDecimal.ZERO)) {
					ExcelUtil.setCellNumber(cell, styles, totalPostingDebitAmount);
				} else {
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue("-");
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				}

				cell = row.createCell(6);
				if (!totalPostingCreditAmount.equals(BigDecimal.ZERO)) {
					ExcelUtil.setCellNumber(cell, styles, totalPostingCreditAmount);
				} else {
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue("-");
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				}

				cell = row.createCell(7);
				if (!totalEndingDebitAmount.equals(BigDecimal.ZERO)) {
					ExcelUtil.setCellNumber(cell, styles, totalEndingDebitAmount);
				} else {
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue("-");
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				}

				cell = row.createCell(8);
				if (!totalEndingCreditAmount.equals(BigDecimal.ZERO)) {
					ExcelUtil.setCellNumber(cell, styles, totalEndingCreditAmount);
				} else {
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue("-");
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				}
				rownum++;
			}
			ExcelUtil.responseWrite(response, wb, "so-tong-hop-cong-no-phai-thu-");
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
		
		sheet.setColumnWidth(0, 10 * 256);
		sheet.setColumnWidth(1, 18 * 256);
		sheet.setColumnWidth(2, 50 * 256);
		for (int i = 3; i < 50; i++) {
			sheet.setColumnWidth(i, 25 * 256);
		}
		return sheet;
	}
}