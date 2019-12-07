package com.olbius.acc.equip;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.ExcelUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class EquipmentExportFileEvents {
	@SuppressWarnings("unchecked")
	public static String exportEquipmentOverviewReportExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String year = (String)paramMap.get("year");
		String month = (String)paramMap.get("month");
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "getEquipmentOverviewReportData", paramMap, userLogin, timeZone, locale);
			context.put("timeZone", timeZone);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("getEquipmentOverviewReportData", context);
			if(!ServiceUtil.isSuccess(resultService)){
				Debug.log("error when export overview equipment report to excel", ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			List<Map<String, Object>> listData = (List<Map<String, Object>>)resultService.get("listData");
			
			/**=============== header =====================*/
			Workbook wb = new HSSFWorkbook();
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);

			Sheet sheet = wb.createSheet("Sheet1");
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			sheet.setAutobreaks(true);

			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);
			
			sheet.setColumnWidth(0, 8 * 150);
			sheet.setColumnWidth(1, 20 * 200);
			sheet.setColumnWidth(2, 35 * 350);
			sheet.setColumnWidth(3, 13 * 200);
			sheet.setColumnWidth(4, 13 * 200);
			sheet.setColumnWidth(5, 25 * 200);
			sheet.setColumnWidth(6, 13 * 200);
			sheet.setColumnWidth(7, 21 * 200);
			sheet.setColumnWidth(8, 13 * 200);
			sheet.setColumnWidth(9, 21 * 200);
			sheet.setColumnWidth(10, 13 * 200);
			sheet.setColumnWidth(11, 13 * 200);
			sheet.setColumnWidth(12, 21 * 200);
			
			Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
			int rownum = 3;
			Row row = sheet.createRow(rownum);
			row.setHeight((short)350);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 9));
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_no_border_12"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquipmentOverviewReport", locale).toUpperCase());
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 8));
			String dateDescription = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) + " " + String.valueOf(Integer.parseInt(month) + 1) + " "
									+ UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale) + " " + year;
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_centered_no_border_10"), null, dateDescription);
			
			String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
			String titleEquipmentId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquipmentId", locale);
			String titleEquipmentName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquimentName", locale);
			String titleIncrease = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCIncrease", locale);
			String titleDecrease = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDecrease", locale);
			String titleAllocated = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAllocated", locale);
			String titleRemain = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCRemain", locale);
			String titleQuantity = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCQuantity", locale);
			String titleAllocTimes = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAllowTimes", locale);
			String titleCommonValue = UtilProperties.getMessage("CommonUiLabels", "CommonValue", locale);
			String titleTimes = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTimes", locale);
			
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 0, 0));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 1, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 2, 2));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 5));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 7));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 8, 9));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 10, 12));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleEquipmentId);
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titleEquipmentName);
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleIncrease);
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
            ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAllocated);
            ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleDecrease);
            ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleRemain);
            ExcelUtil.createCellOfRow(row, 11, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
            ExcelUtil.createCellOfRow(row, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 450);
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
            ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQuantity);
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAllocTimes);
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCommonValue);
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleTimes);
			ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCommonValue);
			ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQuantity);
			ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCommonValue);
			ExcelUtil.createCellOfRow(row, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleQuantity);
			ExcelUtil.createCellOfRow(row, 11, styles.get("cell_bold_centered_header_excel_border_10"), null, titleAllocTimes);
			ExcelUtil.createCellOfRow(row, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, titleCommonValue);
			
			int i = 0;
			if(UtilValidate.isNotEmpty(listData)){
				for(Map<String, Object> data: listData){
					rownum++;
					i++;
					row = sheet.createRow(rownum);
					row.setHeight((short) 300);
					ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(row, 1, styles.get("cell_left_centered_border_full_10"), null, data.get("equipmentId"));
					ExcelUtil.createCellOfRow(row, 2, styles.get("cell_left_centered_border_full_10"), null, data.get("equipmentName"));
					ExcelUtil.createCellOfRow(row, 3, styles.get("cell_right_centered_border_full_quantity_10"), null, data.get("quantity"));
					ExcelUtil.createCellOfRow(row, 4, styles.get("cell_right_centered_border_full_quantity_10"), null, data.get("allocationTimes"));
					ExcelUtil.createCellOfRow(row, 5, styles.get("cell_right_centered_border_full_currency_10"), null, data.get("totalPrice"));
					ExcelUtil.createCellOfRow(row, 6, styles.get("cell_right_centered_border_full_quantity_10"), null, (Integer)data.get("allocatedCount") >= 0 ? data.get("allocatedCount"): "");
					ExcelUtil.createCellOfRow(row, 7, styles.get("cell_right_centered_border_full_currency_10"), null, ((BigDecimal)data.get("allocatedAmount")).compareTo(BigDecimal.ZERO) >= 0? data.get("allocatedAmount"): "");
					ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_full_quantity_10"), null, (Integer)data.get("quantityDecrease") >= 0? data.get("quantityDecrease"): "");
					ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_full_currency_10"), null, ((BigDecimal)data.get("decreaseTotal")).compareTo(BigDecimal.ZERO) >= 0? data.get("decreaseTotal") : "");
					ExcelUtil.createCellOfRow(row, 10, styles.get("cell_right_centered_border_full_quantity_10"), null, (Integer)data.get("quantityRemain") >= 0? data.get("quantityRemain"): "");
					ExcelUtil.createCellOfRow(row, 11, styles.get("cell_right_centered_border_full_quantity_10"), null, (Integer)data.get("allocationTimeRemain") >= 0? data.get("allocationTimeRemain"): "");
					ExcelUtil.createCellOfRow(row, 12, styles.get("cell_right_centered_border_full_currency_10"), null, (BigDecimal)data.get("amountRemain"));
				}
				rownum++;
				row = sheet.createRow(rownum);
				row.setHeight((short) 300);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountTotal", locale));
                ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
                ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
				ExcelUtil.createCellOfRow(row, 3, styles.get("cell_right_centered_border_full_quantity_10"), null, resultService.get("sumQuantity"));
				ExcelUtil.createCellOfRow(row, 4, styles.get("cell_right_centered_border_full_quantity_10"), null, resultService.get("sumAllocationTimes"));
				ExcelUtil.createCellOfRow(row, 5, styles.get("cell_right_centered_border_full_currency_10"), null, resultService.get("sumTotalPrice"));
				ExcelUtil.createCellOfRow(row, 6, styles.get("cell_right_centered_border_full_quantity_10"), null, (Integer)resultService.get("sumAllocatedCount") >= 0? resultService.get("sumAllocatedCount") : "");
				ExcelUtil.createCellOfRow(row, 7, styles.get("cell_right_centered_border_full_currency_10"), null, ((BigDecimal)resultService.get("sumAllocatedAmount")).compareTo(BigDecimal.ZERO) >= 0? resultService.get("sumAllocatedAmount") : "");
				ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_full_quantity_10"), null, (Integer)resultService.get("sumQuantityDecrease") >= 0? resultService.get("sumQuantityDecrease") : "");
				ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_full_currency_10"), null, ((BigDecimal)resultService.get("sumDecreaseTotal")).compareTo(BigDecimal.ZERO) >= 0? resultService.get("sumDecreaseTotal") : "");
				ExcelUtil.createCellOfRow(row, 10, styles.get("cell_right_centered_border_full_quantity_10"), null, (Integer)resultService.get("sumQuantityRemain") >= 0? resultService.get("sumQuantityRemain") : "");
				ExcelUtil.createCellOfRow(row, 11, styles.get("cell_right_centered_border_full_quantity_10"), null, (Integer)resultService.get("sumAllocationTimeRemain") >= 0? resultService.get("sumAllocationTimeRemain") : "");
				ExcelUtil.createCellOfRow(row, 12, styles.get("cell_right_centered_border_full_currency_10"), null, (BigDecimal)resultService.get("sumAmountRemain"));
			} else {
				rownum++;
				row = sheet.createRow(rownum);
				row.setHeight((short) 300);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 11));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, UtilProperties.getMessage("WidgetUiLabels", "wgemptydatastring", locale));
			}
			ExcelUtil.responseWrite(response, wb, "bang_tong_hop_ccdc");
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String exportEquipmentAllocationsReportExcel(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String year = (String) paramMap.get("year");
		String month = (String) paramMap.get("month");
		String fromDateStr = (String) paramMap.get("fromDate");
		String thruDateStr = (String) paramMap.get("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate, timeZone, locale);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromDate);
		calendar.set(Calendar.YEAR, Integer.parseInt(year));
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DATE, 1);
		Timestamp fromDateYear = new Timestamp(calendar.getTimeInMillis());
		fromDateYear = UtilDateTime.getDayStart(fromDateYear);
		try {
			//get info
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), true);
			String companyName = "";
			if (UtilValidate.isNotEmpty(partyGroup)) {
				companyName = partyGroup.getString("groupName");
			}

			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", organizationId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
			List<GenericValue> dummy = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conditions), null, null, null, true);
			String address = "";
			if (UtilValidate.isNotEmpty(dummy)) {
				address = delegator.findOne("PostalAddressDetail", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false).getString("fullName");
				if (UtilValidate.isNotEmpty(address)) {
					address = address.replaceAll(", __", "");
				}
			}
			
			/**=============== header =====================*/
			Workbook wb = new HSSFWorkbook();
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);

			Sheet sheet = wb.createSheet("Sheet1");
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			sheet.setAutobreaks(true);

			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);
			
			sheet.setColumnWidth(0, 8 * 150);
			sheet.setColumnWidth(1, 15 * 200);
			sheet.setColumnWidth(2, 12 * 200);
			sheet.setColumnWidth(3, 26 * 350);
			sheet.setColumnWidth(4, 14 * 200);
			sheet.setColumnWidth(5, 12 * 200);
			sheet.setColumnWidth(6, 11 * 200);
			sheet.setColumnWidth(7, 11 * 200);
			sheet.setColumnWidth(8, 20 * 200);
			sheet.setColumnWidth(9, 18 * 200);
			sheet.setColumnWidth(10, 20 * 200);
			sheet.setColumnWidth(11, 20 * 200);
			sheet.setColumnWidth(12, 20 * 200);
			sheet.setColumnWidth(13, 10 * 200);
			
			Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
			int rownum = 0;
			Row row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 10, 13));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_centered_bold_no_border_full_10"), null, companyName);
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
			String dateTime = format.format(nowTimestamp);
			ExcelUtil.createCellOfRow(row, 10, styles.get("cell_left_centered_no_border_full_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPrintDate", locale) + ": " + dateTime);
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 10, 13));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_centered_bold_no_border_full_10"), null, address);
			
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short)400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 13));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_no_border_12"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquipmentAllocateReport", locale).toUpperCase());
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 13));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_centered_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAssetBook", locale) + ": ");
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 13));
			String dateDescription = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) + " " + String.valueOf(Integer.parseInt(month) + 1) + " "
					+ UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale) + " " + year;
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_centered_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTimePeriod", locale) + ": " + dateDescription);
			
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short) 500);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale));
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquipmentId", locale));
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquipmentSerialNumber", locale));
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquimentName", locale));
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCUseDate", locale));
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonthNumber", locale));
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCGlAccountAllocation", locale));
			ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAllocGlAccoutId", locale));
			ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPurchaseCost", locale));
			ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPeriodAllocationAmount", locale));
			ExcelUtil.createCellOfRow(row, 10, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAccumulatedAllocYearAmount", locale));
			ExcelUtil.createCellOfRow(row, 11, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAccumulatedAlloc", locale));
			ExcelUtil.createCellOfRow(row, 12, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSalvageValue", locale));
			ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSalvageTime", locale));
			
			List<GenericValue> equipmentTypeList = delegator.findList("EquipmentType", null, null, UtilMisc.toList("equipmentTypeId"), null, false);
			if (UtilValidate.isNotEmpty(equipmentTypeList)) {
				int equipmentTypeSeq = 1;
				
				BigDecimal sumUnitPriceTotal = BigDecimal.ZERO;
				BigDecimal sumAllocatedAmountMonthTotal = BigDecimal.ZERO;
				BigDecimal sumAllocatedAmountYearTotal = BigDecimal.ZERO;
				BigDecimal sumAllocatedAmountTotal = BigDecimal.ZERO;
				BigDecimal sumAllocatedAmountRemainTotal = BigDecimal.ZERO;
				for (GenericValue equipmentType : equipmentTypeList) {
					int seq = 1;
					
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("equipmentTypeId", equipmentType.getString("equipmentTypeId")));
					conditions.add(EntityCondition.makeCondition("dateArising", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
					List<GenericValue> equipmentList = delegator.findList("EquipmentIncreaseAndItem", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("equipmentId"), null, false);
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("isPosted", true));
					conditions.add(EntityCondition.makeCondition("voucherDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
					List<GenericValue> equipmentAllocateAndItemList = delegator.findList("EquipmentAllocateAndItem", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-voucherDate"), null, false);
					
					if (UtilValidate.isNotEmpty(equipmentList)) {
						String equipmentTypeName = (String) equipmentType.get("description", locale);
						
						rownum++;
						row = sheet.createRow(rownum);
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 13));
						ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_centered_bold_border_full_10"), null, String.valueOf(equipmentTypeSeq) + " - " + equipmentTypeName);
						
						BigDecimal sumUnitPrice = BigDecimal.ZERO;
						BigDecimal sumAllocatedAmountMonth = BigDecimal.ZERO;
						BigDecimal sumAllocatedAmountYear = BigDecimal.ZERO;
						BigDecimal sumAllocatedAmount = BigDecimal.ZERO;
						BigDecimal sumAllocatedAmountRemain = BigDecimal.ZERO;
						for (GenericValue item : equipmentList) {
							String equipmentId = item.getString("equipmentId");
							String equipmentName = item.getString("equipmentName");
							Timestamp dateArising = item.getTimestamp("dateArising");
							String dateArisingStr = format.format(dateArising);
							Integer allocationTimes =  item.getInteger("allocationTimes");
							BigDecimal unitPrice = item.getBigDecimal("totalPrice");
							
							List<GenericValue> equipmentAllocItemStores = delegator.findList("EquipmentAllocItemStore", EntityCondition.makeCondition("equipmentId", equipmentId), null, null, null, false);
							List<GenericValue> equipmentAllocItemParties = delegator.findList("EquipmentAllocItemParty", EntityCondition.makeCondition("equipmentId", equipmentId), null, null, null, false);
							String costGlAccountId = "";
							if (UtilValidate.isNotEmpty(equipmentAllocItemStores)) {
								GenericValue equipmentAllocItemStore = equipmentAllocItemStores.get(0);
								if (UtilValidate.isNotEmpty(equipmentAllocItemStore.get("debitGlAccountId"))) {
									costGlAccountId = equipmentAllocItemStore.getString("debitGlAccountId");
								}
							} else if (UtilValidate.isNotEmpty(equipmentAllocItemParties)) {
								GenericValue equipmentAllocItemParty = equipmentAllocItemParties.get(0);
								if (UtilValidate.isNotEmpty(equipmentAllocItemParty.get("debitGlAccountId"))) {
									costGlAccountId = equipmentAllocItemParty.getString("debitGlAccountId");
								}
							} else {
								GenericValue fixedAssetTypeGlAccount = delegator.findOne("FixedAssetTypeGlAccount",
										UtilMisc.toMap("fixedAssetTypeId", equipmentType.getString("equipmentTypeId"), "fixedAssetId", "_NA_", "organizationPartyId", organizationId), false);
								if (UtilValidate.isNotEmpty(fixedAssetTypeGlAccount)) {
									if (UtilValidate.isNotEmpty(fixedAssetTypeGlAccount.get("depGlAccountId"))) {
										costGlAccountId = fixedAssetTypeGlAccount.getString("depGlAccountId");
									}
								}
							}
							
							EntityCondition equipmentCond = EntityCondition.makeCondition("equipmentId", equipmentId);
							EntityCondition fromDateCond = EntityCondition.makeCondition("voucherDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate);
							EntityCondition yearCond = EntityCondition.makeCondition("voucherDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDateYear);
							List<GenericValue> tempEquipmentAllocateList = EntityUtil.filterByCondition(equipmentAllocateAndItemList, equipmentCond);
							List<GenericValue> tempEquipmentAllocateMonthList = EntityUtil.filterByCondition(tempEquipmentAllocateList, fromDateCond);
							List<GenericValue> tempEquipmentAllocateYearList = EntityUtil.filterByCondition(tempEquipmentAllocateList, yearCond);
							
							Integer allocationTimeRemain = allocationTimes - tempEquipmentAllocateList.size();
							BigDecimal allocatedAmount = BigDecimal.ZERO;
							for (GenericValue equipmentAllocate : tempEquipmentAllocateList) {
								allocatedAmount = allocatedAmount.add(equipmentAllocate.getBigDecimal("allocatedAmount"));
							}
							BigDecimal allocatedAmountMonth = BigDecimal.ZERO;
							for (GenericValue equipmentAllocateMonth : tempEquipmentAllocateMonthList) {
								allocatedAmountMonth = allocatedAmountMonth.add(equipmentAllocateMonth.getBigDecimal("allocatedAmount"));
							}
							BigDecimal allocatedAmountYear = BigDecimal.ZERO;
							for (GenericValue equipmentAllocateYear : tempEquipmentAllocateYearList) {
								allocatedAmountYear = allocatedAmountYear.add(equipmentAllocateYear.getBigDecimal("allocatedAmount"));
							}
							BigDecimal allocatedAmountRemain = unitPrice.subtract(allocatedAmount);
							
							rownum++;
							row = sheet.createRow(rownum);
							ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, String.valueOf(seq));
							ExcelUtil.createCellOfRow(row, 1, styles.get("cell_left_centered_border_full_10"), null, equipmentId);
							ExcelUtil.createCellOfRow(row, 2, styles.get("cell_left_centered_border_full_10"), null, "");
							ExcelUtil.createCellOfRow(row, 3, styles.get("cell_left_centered_border_full_10"), null, equipmentName);
							ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_centered_border_full_10"), null, dateArisingStr);
							ExcelUtil.createCellOfRow(row, 5, styles.get("cell_normal_centered_border_full_10"), null, allocationTimes);
							ExcelUtil.createCellOfRow(row, 6, styles.get("cell_normal_centered_border_full_10"), null, item.getString("debitGlAccountId"));
							ExcelUtil.createCellOfRow(row, 7, styles.get("cell_normal_centered_border_full_10"), null, costGlAccountId);
							ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_full_currency_10"), null, unitPrice);
							ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_full_currency_10"), null, allocatedAmountMonth);
							ExcelUtil.createCellOfRow(row, 10, styles.get("cell_right_centered_border_full_currency_10"), null, allocatedAmountYear);
							ExcelUtil.createCellOfRow(row, 11, styles.get("cell_right_centered_border_full_currency_10"), null, allocatedAmount);
							ExcelUtil.createCellOfRow(row, 12, styles.get("cell_right_centered_border_full_currency_10"), null, allocatedAmountRemain);
							ExcelUtil.createCellOfRow(row, 13, styles.get("cell_normal_centered_border_full_10"), null, allocationTimeRemain);
							
							seq++;
							sumUnitPrice = sumUnitPrice.add(unitPrice);
							sumAllocatedAmountMonth = sumAllocatedAmountMonth.add(allocatedAmountMonth);
							sumAllocatedAmountYear = sumAllocatedAmountYear.add(allocatedAmountYear);
							sumAllocatedAmount = sumAllocatedAmount.add(allocatedAmount);
							sumAllocatedAmountRemain = sumAllocatedAmountRemain.add(allocatedAmountRemain);
						}
						rownum++;
						row = sheet.createRow(rownum);
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
						ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_centered_bold_border_grey_10"), null,
								UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetTotal", locale) + ": " + String.valueOf(equipmentTypeSeq) + " - " + equipmentTypeName);
						ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_grey_currency_10"), null, sumUnitPrice);
						ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_grey_currency_10"), null, sumAllocatedAmountMonth);
						ExcelUtil.createCellOfRow(row, 10, styles.get("cell_right_centered_border_grey_currency_10"), null, sumAllocatedAmountYear);
						ExcelUtil.createCellOfRow(row, 11, styles.get("cell_right_centered_border_grey_currency_10"), null, sumAllocatedAmount);
						ExcelUtil.createCellOfRow(row, 12, styles.get("cell_right_centered_border_grey_currency_10"), null, sumAllocatedAmountRemain);
						ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, "");
						
						equipmentTypeSeq++;
						
						sumUnitPriceTotal = sumUnitPriceTotal.add(sumUnitPrice);
						sumAllocatedAmountMonthTotal = sumAllocatedAmountMonthTotal.add(sumAllocatedAmountMonth);
						sumAllocatedAmountYearTotal = sumAllocatedAmountYearTotal.add(sumAllocatedAmountYear);
						sumAllocatedAmountTotal = sumAllocatedAmountTotal.add(sumAllocatedAmount);
						sumAllocatedAmountRemainTotal = sumAllocatedAmountRemainTotal.add(sumAllocatedAmountRemain);
					}
				}
				
				rownum++;
				row = sheet.createRow(rownum);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTotalReport", locale));
				ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_grey_currency_10"), null, sumUnitPriceTotal);
				ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_grey_currency_10"), null, sumAllocatedAmountMonthTotal);
				ExcelUtil.createCellOfRow(row, 10, styles.get("cell_right_centered_border_grey_currency_10"), null, sumAllocatedAmountYearTotal);
				ExcelUtil.createCellOfRow(row, 11, styles.get("cell_right_centered_border_grey_currency_10"), null, sumAllocatedAmountTotal);
				ExcelUtil.createCellOfRow(row, 12, styles.get("cell_right_centered_border_grey_currency_10"), null, sumAllocatedAmountRemainTotal);
				ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, "");
				
				rownum += 2;
				row = sheet.createRow(rownum);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 13));
				ExcelUtil.createCellOfRow(row, 6, styles.get("cell_italic_normal_center_no_border_10"), null,
						UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDay", locale) + " ...." + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) + " ...."
						+ UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale) + " ....");
				
				rownum++;
				row = sheet.createRow(rownum);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 5));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 13));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_center_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "NguoiLapBieu", locale));
				ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_center_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCChiefAccount", locale));
				ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_center_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCApprove", locale));
				
				rownum++;
				row = sheet.createRow(rownum);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 5));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 13));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_italic_normal_center_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSignAndName", locale));
				ExcelUtil.createCellOfRow(row, 3, styles.get("cell_italic_normal_center_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSignAndName", locale));
				ExcelUtil.createCellOfRow(row, 6, styles.get("cell_italic_normal_center_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSignAndName", locale));
			} else {
				rownum++;
				row = sheet.createRow(rownum);
				row.setHeight((short) 300);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 11));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, UtilProperties.getMessage("WidgetUiLabels", "wgemptydatastring", locale));
			}
			
			ExcelUtil.responseWrite(response, wb, "bang_phan_bo_ccdc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
}