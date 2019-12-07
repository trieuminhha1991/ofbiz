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

public class LiabilityReceivableExcel {

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
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCLiabilityReceivable", locale).toUpperCase());
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
					UtilProperties.getMessage(RESOURCE, "VoucherNumber", locale),
					UtilProperties.getMessage(RESOURCE, "BACCDay", locale),
					UtilProperties.getMessage(RESOURCE, "VoucherType", locale),
					UtilProperties.getMessage(RESOURCE, "BACCDescription", locale),
					UtilProperties.getMessage(RESOURCE, "BACCProductStoreDemension", locale));

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

				if (UtilMisc.toList(0, 4, 5).contains(index)) {
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum_group, index, index));
					cell_dummy = row_group.createCell(index);
					cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
				} else {
					if (index == 1) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 2));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "CommonVoucher", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					} else if (index == 6) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCOpeningAmount", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					} else if (index == 8) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCPostingAmount", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					} else if (index == 10) {
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
			String partyId = request.getParameter("partyId");

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
			
			if (partyId != null) {
				parameters.put("partyId", new String[] { partyId });
				GenericValue party = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(party)) {
					String partyName = "";
					if (party.getString("partyTypeId").equals("PERSON")) {
						partyName = party.getString("firstName") + " " + party.getString("middleName") + " " + party.getString("lastName"); 
					} else if (party.getString("partyTypeId").equals("PARTY_GROUP")) {
						partyName = party.getString("groupName");
					}
					cellParty.setCellValue(UtilProperties.getMessage("BaseLogisticsUiLabels", "CustomerName", locale) + ": "
							+ partyId + " - " + partyName);
				}
			} else {
				cellParty.setCellValue(UtilProperties.getMessage("BaseLogisticsUiLabels", "CustomerName", locale) + ": ");
			}

			Map<String, Object> rs = dispatcher.runSync("JqxGetLiabilityReceivable",
					UtilMisc.toMap("userLogin", userLogin, "opts", new EntityFindOptions(), "parameters", parameters,
							"listAllConditions", FastList.newInstance(), "listSortFields", FastList.newInstance()));

			if (ServiceUtil.isSuccess(rs)) {
				List<Map<String, Object>> dummy = (List<Map<String, Object>>) rs.get("listIterator");
				int seq = 1;
				BigDecimal openingAmount = BigDecimal.ZERO;
				BigDecimal totalPostingCreditAmount = BigDecimal.ZERO;
				BigDecimal totalPostingDebitAmount = BigDecimal.ZERO;
				BigDecimal endingAmount = BigDecimal.ZERO;
				for (Map<String, Object> g : dummy) {
					row = sheet.createRow(rownum);
					
					cell = row.createCell(0);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(seq++);
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					cell = row.createCell(1);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("voucherCode") != null) {
						cell.setCellValue((String) g.get("voucherCode"));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(2);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("voucherDate") != null) {
						cell.setCellValue(format.format(g.get("voucherDate")));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					cell = row.createCell(3);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("voucherType") != null) {
						cell.setCellValue((String) g.get("voucherType"));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					cell = row.createCell(4);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("voucherDescription") != null) {
						cell.setCellValue((String) g.get("voucherDescription"));
					}
					if (seq == 2) {
						cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}
					
					cell = row.createCell(5);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.get("facilityName") != null) {
						cell.setCellValue((String) g.get("facilityName"));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					BigDecimal openingCreditAmount = (BigDecimal) g.get("openingCreditAmount");
					BigDecimal openingDebitAmount = (BigDecimal) g.get("openingDebitAmount");
					BigDecimal postingCreditAmount = (BigDecimal) g.get("postingCreditAmount");
					BigDecimal postingDebitAmount = (BigDecimal) g.get("postingDebitAmount");
					BigDecimal endingCreditAmount = (BigDecimal) g.get("endingCreditAmount");
					BigDecimal endingDebitAmount = (BigDecimal) g.get("endingDebitAmount");
					
					if (seq == 2) {
						if (UtilValidate.isNotEmpty(openingCreditAmount) && !openingCreditAmount.equals(BigDecimal.ZERO)) {
							openingAmount = openingCreditAmount;
						} else if (UtilValidate.isNotEmpty(openingDebitAmount) && !openingDebitAmount.equals(BigDecimal.ZERO)) {
							openingAmount = openingDebitAmount.negate();
						}
					}
					
					if (UtilValidate.isNotEmpty(postingCreditAmount)) {
						totalPostingCreditAmount = totalPostingCreditAmount.add(postingCreditAmount);
					}
					if (UtilValidate.isNotEmpty(postingDebitAmount)) {
						totalPostingDebitAmount = totalPostingDebitAmount.add(postingDebitAmount);
					}
					
					cell = row.createCell(6);
					if (UtilValidate.isNotEmpty(openingDebitAmount) && !openingDebitAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, openingDebitAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(7);
					if (UtilValidate.isNotEmpty(openingCreditAmount) && !openingCreditAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, openingCreditAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(8);
					if (UtilValidate.isNotEmpty(postingDebitAmount) && !postingDebitAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, postingDebitAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(9);
					if (UtilValidate.isNotEmpty(postingCreditAmount) && !postingCreditAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, postingCreditAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(10);
					if (UtilValidate.isNotEmpty(endingDebitAmount) && !endingDebitAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, endingDebitAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}
					
					cell = row.createCell(11);
					if (UtilValidate.isNotEmpty(endingCreditAmount) && !endingCreditAmount.equals(BigDecimal.ZERO)) {
						ExcelUtil.setCellNumber(cell, styles, endingCreditAmount);
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					rownum++;
				}
				
				endingAmount = openingAmount.add(totalPostingCreditAmount).subtract(totalPostingDebitAmount);
				
				//total posting
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
				cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCTongPhatSinh", locale));
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(5);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

				cell = row.createCell(6);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue("-");
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(7);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue("-");
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(8);
				ExcelUtil.setCellNumber(cell, styles, totalPostingDebitAmount);

				cell = row.createCell(9);
				ExcelUtil.setCellNumber(cell, styles, totalPostingCreditAmount);

				cell = row.createCell(10);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue("-");
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(11);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue("-");
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));				
				
				rownum++;
				
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
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(7);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(8);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				cell = row.createCell(9);
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				
				if (endingAmount.compareTo(BigDecimal.ZERO) >= 0) {
					cell = row.createCell(10);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
					
					cell = row.createCell(11);
					ExcelUtil.setCellNumber(cell, styles, endingAmount);
				} else {
					cell = row.createCell(10);
					ExcelUtil.setCellNumber(cell, styles, endingAmount.negate());
					
					cell = row.createCell(11);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				}
				
				rownum++;
			}

			ExcelUtil.responseWrite(response, wb, "so-chi-tiet-cong-no-phai-thu-");

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
		sheet.setColumnWidth(1, 15 * 256);
		sheet.setColumnWidth(2, 15 * 256);
		sheet.setColumnWidth(3, 30 * 256);
		sheet.setColumnWidth(4, 60 * 256);
		sheet.setColumnWidth(5, 50 * 256);
		for (int i = 6; i < 50; i++) {
			sheet.setColumnWidth(i, 25 * 256);
		}
		return sheet;
	}
}