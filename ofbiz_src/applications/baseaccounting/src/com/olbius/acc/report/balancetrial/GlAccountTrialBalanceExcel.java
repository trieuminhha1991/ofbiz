package com.olbius.acc.report.balancetrial;

import com.olbius.acc.excel.VoucherDeclarationExcel;
import com.olbius.acc.report.balancetrial.entity.GlAccountBal;
import com.olbius.basesales.util.ExcelUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GlAccountTrialBalanceExcel {

	public final static String RESOURCE = "BaseAccountingUiLabels";
	public static Map<String, Map<String, Object>> glAccountMap;
	public static String organizationPartyId;
	public static String customTimePeriodId;
	public static GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

	@SuppressWarnings("unchecked")
	public static void export(HttpServletRequest request, HttpServletResponse response) {
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
			
			rownum += 2;

			row = sheet.createRow(rownum);
			row.setHeight((short) 400);
			cell = row.createCell(2);
			cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 5));
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCBalanceTrialReport", locale).toUpperCase());
			cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum++;

			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			Cell cellDate = row.createCell(2);
			cellDate.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 5));
			cellDate.setCellStyle(styles.get("cell_normal_centered_no_border_10"));
			rownum++;

			rownum += 2;

			List<String> titles = UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BACCAccountCode", locale),
					UtilProperties.getMessage(RESOURCE, "BACCAccountName", locale));

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

				if (UtilMisc.toList(0, 1).contains(index)) {
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum_group, index, index));
					cell_dummy = row_group.createCell(index);
					cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
				} else {
					if (index == 2) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCOpeningBalance", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					} else if (index == 4) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCPostedAmount", locale));
						cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
						
						cell_dummy = row.createCell(index + 1);
						cell_dummy.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
					} else if (index == 6) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index + 1));
						cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
						cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCEndingBalance", locale));
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

			organizationPartyId = request.getParameter("organizationPartyId");
			customTimePeriodId = request.getParameter("customTimePeriodId");
			
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			String date = "";
			if (UtilValidate.isNotEmpty(customTimePeriod)) {
				date += UtilProperties.getMessage(RESOURCE, "ExcelFromDate", locale) + ": " + customTimePeriod.getDate("fromDate") + " ";
				date += UtilProperties.getMessage(RESOURCE, "ExcelThruDate", locale).toLowerCase() + ": " + customTimePeriod.getDate("thruDate");
			}

			cellDate.setCellValue(date);

			Map<String, Object> rs = dispatcher.runSync("getListJqTrialBalanceAccount",
					UtilMisc.toMap("userLogin", userLogin, "organizationPartyId", organizationPartyId, "customTimePeriodId", customTimePeriodId));

			if (ServiceUtil.isSuccess(rs)) {
				List<GlAccountBal> dummy = (List<GlAccountBal>) rs.get("listBal");
				glAccountMap = FastMap.newInstance();
				for (GlAccountBal g : dummy) {
					Map<String, Object> map = FastMap.newInstance();
					map.put("openingDrBalance", g.getOpeningDrBalance());
					map.put("openingCrBalance", g.getOpeningCrBalance());
					map.put("postedDebits", g.getPostedDebits());
					map.put("postedCredits", g.getPostedCredits());
					map.put("endingDrBalance", g.getEndingDrBalance());
					map.put("endingCrBalance", g.getEndingCrBalance());
					glAccountMap.put(g.getGlAccountId(), map);
				}
				for (GlAccountBal g : dummy) {
					row = sheet.createRow(rownum);
					
					String isLeaf = g.getIsLeaf();
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
					if (g.getGlAccountId() != null) {
						cell.setCellValue(g.getGlAccountId());
					}
					cell.setCellStyle(styles.get(rowStyle));

					cell = row.createCell(1);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					if (g.getAccountName() != null) {
						cell.setCellValue(g.getAccountName());
					}
					cell.setCellStyle(styles.get(rowStyle));
					
					BigDecimal openingCreditAmount = getAmount("openingCrBalance", g.getGlAccountId());
					BigDecimal openingDebitAmount = getAmount("openingDrBalance", g.getGlAccountId());
					BigDecimal postingCreditAmount = getAmount("postedCredits", g.getGlAccountId());
					BigDecimal postingDebitAmount = getAmount("postedDebits", g.getGlAccountId());
					BigDecimal endingCreditAmount = getAmount("endingCrBalance", g.getGlAccountId());
					BigDecimal endingDebitAmount = getAmount("endingDrBalance", g.getGlAccountId());
					
					cell = row.createCell(2);
					if (UtilValidate.isNotEmpty(openingDebitAmount)) {
						cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(openingDebitAmount.toString()));
						if (openingDebitAmount.compareTo(BigDecimal.ZERO) != 0) {
							cell.setCellStyle(styles.get(rowNumberStyle));
						} else {
							cell.setCellStyle(styles.get(rowNumberZeroStyle));
						}
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(3);
					if (UtilValidate.isNotEmpty(openingCreditAmount)) {
						cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(openingCreditAmount.toString()));
						if (openingCreditAmount.compareTo(BigDecimal.ZERO) != 0) {
							cell.setCellStyle(styles.get(rowNumberStyle));
						} else {
							cell.setCellStyle(styles.get(rowNumberZeroStyle));
						}
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(4);
					if (UtilValidate.isNotEmpty(postingDebitAmount)) {
						cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(postingDebitAmount.toString()));
						if (postingDebitAmount.compareTo(BigDecimal.ZERO) != 0) {
							cell.setCellStyle(styles.get(rowNumberStyle));
						} else {
							cell.setCellStyle(styles.get(rowNumberZeroStyle));
						}
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(5);
					if (UtilValidate.isNotEmpty(postingCreditAmount)) {
						cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(postingCreditAmount.toString()));
						if (postingCreditAmount.compareTo(BigDecimal.ZERO) != 0) {
							cell.setCellStyle(styles.get(rowNumberStyle));
						} else {
							cell.setCellStyle(styles.get(rowNumberZeroStyle));
						}
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					cell = row.createCell(6);
					if (UtilValidate.isNotEmpty(endingDebitAmount)) {
						cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(endingDebitAmount.toString()));
						if (endingDebitAmount.compareTo(BigDecimal.ZERO) != 0) {
							cell.setCellStyle(styles.get(rowNumberStyle));
						} else {
							cell.setCellStyle(styles.get(rowNumberZeroStyle));
						}
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}
					
					cell = row.createCell(7);
					if (UtilValidate.isNotEmpty(endingCreditAmount)) {
						cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(endingCreditAmount.toString()));
						if (endingCreditAmount.compareTo(BigDecimal.ZERO) != 0) {
							cell.setCellStyle(styles.get(rowNumberStyle));
						} else {
							cell.setCellStyle(styles.get(rowNumberZeroStyle));
						}
					} else {
						cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					}

					rownum++;
				}
			}

			ExcelUtil.responseWrite(response, wb, "bang-can-doi-tai-khoan-");

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
		
		sheet.setColumnWidth(0, 12 * 256);
		sheet.setColumnWidth(1, 35 * 256);
		for (int i = 2; i < 50; i++) {
			sheet.setColumnWidth(i, 22 * 256);
		}
		return sheet;
	}
	
	private static BigDecimal getAmount(String columnField, String glAccountId) throws GenericEntityException {
		BigDecimal amount = (BigDecimal) glAccountMap.get(glAccountId).get(columnField);
		List<String> listChild = getChild(glAccountId);
		if (UtilValidate.isNotEmpty(listChild)) {
			for (String acc : listChild) {
				amount = amount.add(getAmount(columnField, acc));
			}
		}
		return amount;
	}
	
	private static List<String> getChild(String glAccountId) throws GenericEntityException {
		List<String> listChild = FastList.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
		conds.add(EntityCondition.makeCondition("parentGlAccountId", glAccountId));
		List<GenericValue> glAccountChilds = delegator.findList("GlAccountOrgDetail", EntityCondition.makeCondition(conds),
				null, UtilMisc.toList("glAccountId"), null, false);
		if (UtilValidate.isNotEmpty(glAccountChilds)) {
			listChild = EntityUtil.getFieldListFromEntityList(glAccountChilds, "glAccountId", true);
		}
		return listChild;
	}
}