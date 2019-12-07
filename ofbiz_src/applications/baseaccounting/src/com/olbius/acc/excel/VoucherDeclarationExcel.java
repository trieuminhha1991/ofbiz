package com.olbius.acc.excel;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.util.ExcelUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VoucherDeclarationExcel {

	public final static String RESOURCE = "BaseAccountingUiLabels";
	public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

	public static void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		EntityListIterator iterator = null;
		try {
			Map<String, Object> info = info(request, delegator, userLogin, locale);

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
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BACCVoucherDeclaration", locale).toUpperCase());
			cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum++;

			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			Cell cellDate = row.createCell(3);
			cellDate.setCellType(ExcelUtil.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 6));
			cellDate.setCellStyle(styles.get("cell_bold_centered_no_border_10"));
			rownum++;
			rownum++;
			rownum++;

			List<String> titles = UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BACCVoucherDate2", locale),
					UtilProperties.getMessage(RESOURCE, "BACCVoucherId", locale),
					UtilProperties.getMessage(RESOURCE, "BACCVoucherNumber", locale),
					UtilProperties.getMessage(RESOURCE, "BACCVoucherNumberSystem", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCustomerId", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCustomerName", locale));

			titles.addAll(UtilMisc.toList(
					UtilProperties.getMessage(RESOURCE, "BACCDescription", locale),
					UtilProperties.getMessage(RESOURCE, "BACCAcctgTransTypeId", locale),
					UtilProperties.getMessage(RESOURCE, "BACCGlAccountCode", locale),
					UtilProperties.getMessage(RESOURCE, "BACCRecipGlAccountCode", locale),
					UtilProperties.getMessage(RESOURCE, "BACCDebitAmount", locale),
					UtilProperties.getMessage(RESOURCE, "BACCCreditAmount", locale)));
			titles.addAll(UtilMisc.toList(
					UtilProperties.getMessage(RESOURCE, "BACCGlAccountName", locale),
					UtilProperties.getMessage(RESOURCE, "BACCRecipGlAccountName", locale),
					UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderId", locale),
					UtilProperties.getMessage("BaseSalesUiLabels", "BSReturnId", locale),
					UtilProperties.getMessage(RESOURCE, "BACCProductId", locale),
					UtilProperties.getMessage(RESOURCE, "BACCProductName", locale)));

			row = sheet.createRow(rownum);
			row.setHeight((short) 900);
			for (String t : titles) {
				cell = row.createCell(titles.indexOf(t));
				cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
				cell.setCellValue(t);
				cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
			}
			rownum++;

			String fd = request.getParameter("fromDate");
			String td = request.getParameter("thruDate");
			String partyId = request.getParameter("partyId");
			String partyName = request.getParameter("partyName");
			String accountCode = request.getParameter("accountCode");

			Map<String, String[]> parameters = new HashMap<>();

			Timestamp fromDate = fd != null ? UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fd))) : null;
			Timestamp thruDate = td != null ? UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(td))) : null;

			List<EntityCondition> listAllConditions = FastList.newInstance();
			String date = "";
			if (fromDate != null) {
				listAllConditions.add(EntityCondition.makeCondition("documentDate",
						EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
				date += UtilProperties.getMessage(RESOURCE, "ExcelFromDate", locale) + ": " + format.format(fromDate)
						+ " ";
			}

			if (thruDate != null) {
				listAllConditions.add(
						EntityCondition.makeCondition("documentDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
				date += UtilProperties.getMessage(RESOURCE, "ExcelThruDate", locale).toLowerCase() + ": "
						+ format.format(thruDate);
			}
			cellDate.setCellValue(date);

			if (partyId != null) {
				listAllConditions
						.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.LIKE, "%" + partyId + "%"));
			}

			if (partyName != null) {
				listAllConditions.add(
						EntityCondition.makeCondition("partyName", EntityJoinOperator.LIKE, "%" + partyName + "%"));
			}

			if (accountCode != null) {
				listAllConditions.add(
						EntityCondition.makeCondition("accountCode", EntityJoinOperator.LIKE, "%" + accountCode + "%"));
			}

			List<String> listSortFields = FastList.newInstance();

			Map<String, Object> rs = dispatcher.runSync("JqxGetVoucherDeclaration",
					UtilMisc.toMap("userLogin", userLogin, "opts", new EntityFindOptions(), "parameters", parameters,
							"listAllConditions", listAllConditions, "listSortFields", listSortFields));

			if (ServiceUtil.isSuccess(rs)) {
				iterator = (EntityListIterator) rs.get("listIterator");
				BigDecimal drAmount = BigDecimal.ZERO;
				BigDecimal crAmount = BigDecimal.ZERO;
				GenericValue g = (GenericValue) iterator.next();
				while (g != null) {
					row = sheet.createRow(rownum);

					cell = row.createCell(0);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(format.format(g.getTimestamp("documentDate")));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(1);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("documentId"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(2);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("voucherCode"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					
					cell = row.createCell(3);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("documentNumber"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(4);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("partyId"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(5);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("partyName"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(6);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("description"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
					
					cell = row.createCell(7);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					GenericValue acctgTransType = delegator.findOne("AcctgTransType",
							UtilMisc.toMap("acctgTransTypeId", g.getString("acctgTransTypeId")), false);
					if (UtilValidate.isNotEmpty(acctgTransType)) {
						cell.setCellValue((String) acctgTransType.get("description", locale));
					}
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(8);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("accountCode"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(9);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("accountRecipCode"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(10);
					ExcelUtil.setCellNumber(cell, styles, g.getBigDecimal("drAmount"));

					cell = row.createCell(11);
					ExcelUtil.setCellNumber(cell, styles, g.getBigDecimal("crAmount"));

					drAmount = drAmount.add(g.getBigDecimal("drAmount"));
					crAmount = crAmount.add(g.getBigDecimal("crAmount"));

					cell = row.createCell(12);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("accountName"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(13);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("accountRecipName"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(14);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("orderId"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(15);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("returnId"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(16);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("productCode"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					cell = row.createCell(17);
					cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
					cell.setCellValue(g.getString("productName"));
					cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));

					g = (GenericValue) iterator.next();
					rownum++;
				}
				row = sheet.createRow(rownum);

				cell = row.createCell(10);
				ExcelUtil.setCellNumber(cell, styles, drAmount);

				cell = row.createCell(11);
				ExcelUtil.setCellNumber(cell, styles, crAmount);
			}

			ExcelUtil.responseWrite(response, wb, "bang-ke-khai-chung-tu-");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (iterator != null)
				iterator.close();
		}
	}

	public static Map<String, Object> info(HttpServletRequest request, GenericDelegator delegator,
                                           GenericValue userLogin, Locale locale) throws Exception {
		Map<String, Object> info = FastMap.newInstance();

		String organizationId = PartyUtil.getRootOrganization(delegator, userLogin.getString("userLoginId"));
		info.putAll(delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), true));

		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(
				UtilMisc.toMap("partyId", organizationId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
		List<GenericValue> dummy = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(conditions), null, null, null, true);
		if (UtilValidate.isNotEmpty(dummy)) {
			String fullName = delegator
					.findOne("PostalAddressDetail",
							UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false)
					.getString("fullName");
			if (UtilValidate.isNotEmpty(fullName)) {
				fullName = fullName.replaceAll(", __", "");
			}
			info.put("companyAddress", fullName);
		}
		return info;
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
