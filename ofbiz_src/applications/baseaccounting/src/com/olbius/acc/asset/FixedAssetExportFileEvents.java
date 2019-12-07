package com.olbius.acc.asset;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.ExcelUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class FixedAssetExportFileEvents {
	@SuppressWarnings("unchecked")
	public static String exportFixedAssetReportS09DNN(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateType = (String)paramMap.get("dateType");
		String monthQuarterValue = (String)paramMap.get("monthQuarterValue");
		String year = (String)paramMap.get("year");
		String dateDescription = null;
		if("quarter".equals(dateType)){
			dateDescription = UtilProperties.getMessage("CommonUiLabels", "CommonQuarter", locale) + " " + (Integer.parseInt(monthQuarterValue) + 1);
		} else if("month".equals(dateType)){
			dateDescription = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) + " " + (Integer.parseInt(monthQuarterValue) + 1);
		} else {
			dateDescription = "";
		}
		dateDescription += " " + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale) + " " + year;
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "getFixedAssetReportS09DNNData", paramMap, userLogin, timeZone, locale);
			context.put("timeZone", timeZone);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("getFixedAssetReportS09DNNData", context);
			if(!ServiceUtil.isSuccess(resultService)){
				Debug.log("error when export S09-DNN to excel", ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			List<String> fixedAssetTypeIds = (List<String>) resultService.get("fixedAssetTypeIdList");
			Map<String, Object> fixedAssetAndTypeMap = (Map<String, Object>) resultService.get("fixedAssetAndTypeMap");
			String companyName = (String) resultService.get("companyName");
			String address = (String) resultService.get("address");
			
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
			sheet.setColumnWidth(1, 21 * 200);
			sheet.setColumnWidth(2, 16 * 200);
			sheet.setColumnWidth(3, 15 * 200);
			sheet.setColumnWidth(4, 40 * 220);
			sheet.setColumnWidth(5, 16 * 200);
			sheet.setColumnWidth(6, 20 * 210);
			sheet.setColumnWidth(7, 25 * 200);
			sheet.setColumnWidth(8, 15 * 180);
			sheet.setColumnWidth(9, 25 * 200);
			sheet.setColumnWidth(10, 25 * 200);
			sheet.setColumnWidth(11, 20 * 220);
			sheet.setColumnWidth(12, 16 * 200);
			sheet.setColumnWidth(13, 27 * 220);
			Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
			int rownum = 1;
			if(UtilValidate.isNotEmpty(fixedAssetTypeIds)){
				String dateTimeFormat = "dd/MM/yyyy";
				for(String fixedAssetTypeId: fixedAssetTypeIds){
					GenericValue fixedAssetType = delegator.findOne("FixedAssetType", UtilMisc.toMap("fixedAssetTypeId", fixedAssetTypeId), false);
					List<GenericValue> tempListFixedAsset = (List<GenericValue>)fixedAssetAndTypeMap.get(fixedAssetTypeId);
					BigDecimal purchaseCostTotal = BigDecimal.ZERO, depreciationAmountTotal = BigDecimal.ZERO, accumulatedDepTotal = BigDecimal.ZERO;
					rownum = renderHeaderFAReportS09DNN(styles, sheet, rownum, locale, dateDescription, fixedAssetType, companyName, address);
					int i = 0;
					for (GenericValue fixedAsset: tempListFixedAsset) {
						i++;
						rownum++;
						String fixedAssetId = fixedAsset.getString("fixedAssetId");
						Row rowData = sheet.createRow(rownum);
						rowData.setHeight((short) 300);
						ExcelUtil.createCellOfRow(rowData, 0, styles.get("cell_normal_centered_border_full_10"), null, String.valueOf(i));
						if (UtilValidate.isNotEmpty(fixedAsset.get("serialNumber"))){
							ExcelUtil.createCellOfRow(rowData, 1, styles.get("cell_left_centered_border_full_10"), null, fixedAsset.get("serialNumber"));
						} else {
							ExcelUtil.createCellOfRow(rowData, 1, styles.get("cell_left_centered_border_full_10"), null, "");
						}
						if (UtilValidate.isNotEmpty(fixedAsset.get("receiptDate"))){
							ExcelUtil.createCellOfRow(rowData, 2, styles.get("cell_normal_centered_border_full_10"), null, UtilFormatOut.formatDateTime(fixedAsset.getTimestamp("receiptDate"), dateTimeFormat, locale, timeZone));
						} else {
							ExcelUtil.createCellOfRow(rowData, 2, styles.get("cell_normal_centered_border_full_10"), null, "");
						}
						ExcelUtil.createCellOfRow(rowData, 3, styles.get("cell_left_centered_border_full_10"), null, fixedAsset.get("fixedAssetId") != null? fixedAsset.get("fixedAssetId"):"");
						ExcelUtil.createCellOfRow(rowData, 4, styles.get("cell_left_centered_border_full_10"), null, fixedAsset.get("fixedAssetName"));
						ExcelUtil.createCellOfRow(rowData, 5, styles.get("cell_left_centered_border_full_10"), null, fixedAsset.get("countryOrigin") != null? fixedAsset.get("countryOrigin"):"");
						ExcelUtil.createCellOfRow(rowData, 6, styles.get("cell_normal_centered_border_full_10"), null, fixedAsset.get("dateAcquired") != null? UtilFormatOut.formatDateTime(fixedAsset.getTimestamp("dateAcquired"), dateTimeFormat, locale, timeZone):"");
						
						purchaseCostTotal = purchaseCostTotal.add(fixedAsset.getBigDecimal("purchaseCost"));
						ExcelUtil.createCellOfRow(rowData, 7, styles.get("cell_right_centered_border_full_currency_10"), null, fixedAsset.get("purchaseCost"));
						ExcelUtil.createCellOfRow(rowData, 8, styles.get("cell_right_centered_border_full_quantity_10"), null, fixedAsset.getBigDecimal("monthlyDepRate").setScale(2, RoundingMode.HALF_UP) + "%");
						List<GenericValue> fixedAssetDepreciationCalcItemList = delegator.findByAnd("FixedAssetDepreciationCalcAndItem", UtilMisc.toMap("fixedAssetId", fixedAssetId), UtilMisc.toList("-voucherDate"), false);
						if(UtilValidate.isNotEmpty(fixedAssetDepreciationCalcItemList)){
							GenericValue fixedAssetDepreciationCalcItem = fixedAssetDepreciationCalcItemList.get(0);
							depreciationAmountTotal = depreciationAmountTotal.add(fixedAssetDepreciationCalcItem.getBigDecimal("depreciationAmount"));
							ExcelUtil.createCellOfRow(rowData, 9, styles.get("cell_right_centered_border_full_currency_10"), null, fixedAssetDepreciationCalcItem.get("depreciationAmount"));
						} else {
							ExcelUtil.createCellOfRow(rowData, 9, styles.get("cell_right_centered_border_full_currency_10"), null, "");
						}
						accumulatedDepTotal = accumulatedDepTotal.add(fixedAsset.getBigDecimal("accumulatedDep"));
						ExcelUtil.createCellOfRow(rowData, 10, styles.get("cell_right_centered_border_full_currency_10"), null, fixedAsset.get("accumulatedDep"));
						List<GenericValue> fixedAssetDecreaseItemList = delegator.findByAnd("FixedAssetDecreaseAndItem", UtilMisc.toMap("fixedAssetId", fixedAssetId), UtilMisc.toList("-voucherDate"), false);
						if(UtilValidate.isNotEmpty(fixedAssetDecreaseItemList)){
							GenericValue fixedAssetDecreaseItem = fixedAssetDecreaseItemList.get(0);
							ExcelUtil.createCellOfRow(rowData, 11, styles.get("cell_left_centered_border_full_10"), null, fixedAssetDecreaseItem.get("voucherNumber"));
							ExcelUtil.createCellOfRow(rowData, 12, styles.get("cell_normal_centered_border_full_10"), null, fixedAssetDecreaseItem.get("voucherDate") != null? UtilFormatOut.formatDateTime(fixedAssetDecreaseItem.getTimestamp("voucherDate"), dateTimeFormat, locale, timeZone):"");
							if (fixedAssetDecreaseItem.getString("decreaseReasonTypeId") != null){
								GenericValue decreaseReasonType = delegator.findOne("FixedAssetDecrReasonType", UtilMisc.toMap("decreaseReasonTypeId", fixedAssetDecreaseItem.getString("decreaseReasonTypeId")), false);
								ExcelUtil.createCellOfRow(rowData, 13, styles.get("cell_left_centered_border_full_10"), null, decreaseReasonType.get("description"));
							} else {
								ExcelUtil.createCellOfRow(rowData, 13, styles.get("cell_left_centered_border_full_10"), null, "");
							}
						} else {
							ExcelUtil.createCellOfRow(rowData, 11, styles.get("cell_left_centered_border_full_10"), null, "");
							ExcelUtil.createCellOfRow(rowData, 12, styles.get("cell_left_centered_border_full_10"), null, "");
							ExcelUtil.createCellOfRow(rowData, 13, styles.get("cell_left_centered_border_full_10"), null, "");
						}
					}
					rownum++;
					Row rowTotal = sheet.createRow(rownum);
					rowTotal.setHeight((short) 300);
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
					ExcelUtil.createCellOfRow(rowTotal, 0, styles.get("cell_bold_left_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetTotal", locale));
                    ExcelUtil.createCellOfRow(rowTotal, 1, styles.get("cell_bold_left_border_10"), null, "");
                    ExcelUtil.createCellOfRow(rowTotal, 2, styles.get("cell_bold_left_border_10"), null, "");
                    ExcelUtil.createCellOfRow(rowTotal, 3, styles.get("cell_bold_left_border_10"), null, "");
                    ExcelUtil.createCellOfRow(rowTotal, 4, styles.get("cell_bold_left_border_10"), null, "");
                    ExcelUtil.createCellOfRow(rowTotal, 5, styles.get("cell_bold_left_border_10"), null, "");
                    ExcelUtil.createCellOfRow(rowTotal, 6, styles.get("cell_bold_left_border_10"), null, "");
					ExcelUtil.createCellOfRow(rowTotal, 7, styles.get("cell_right_centered_border_full_currency_10"), null, purchaseCostTotal);
					ExcelUtil.createCellOfRow(rowTotal, 8, styles.get("cell_right_centered_border_full_currency_10"), null, "");
					ExcelUtil.createCellOfRow(rowTotal, 9, styles.get("cell_right_centered_border_full_currency_10"), null, depreciationAmountTotal);
					ExcelUtil.createCellOfRow(rowTotal, 10, styles.get("cell_right_centered_border_full_currency_10"), null, accumulatedDepTotal);
					ExcelUtil.createCellOfRow(rowTotal, 11, styles.get("cell_right_centered_border_full_currency_10"), null, "");
					ExcelUtil.createCellOfRow(rowTotal, 12, styles.get("cell_right_centered_border_full_currency_10"), null, "");
					ExcelUtil.createCellOfRow(rowTotal, 13, styles.get("cell_right_centered_border_full_currency_10"), null, "");
					rownum += 2;
					rownum = renderFooterFAReportS09DNN(styles, sheet, rownum, locale);
					rownum += 8;
				}
			} else {
				rownum = renderHeaderFAReportS09DNN(styles, sheet, rownum, locale, dateDescription, null, companyName, address);
				rownum += 2;
				renderFooterFAReportS09DNN(styles, sheet, rownum, locale);
			}
			ExcelUtil.responseWrite(response, wb, "so_tai_san_S09_DNN");
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	@SuppressWarnings("deprecation")
	public static String exportFixedAssetReportS11DNNExcel(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String fixedAssetId = request.getParameter("fixedAssetId");
		String dateType = request.getParameter("dateType");
		String monthQuarterValue = request.getParameter("monthQuarterValue");
		String year = request.getParameter("year");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String dateTimeFormat = "dd/MM/yyyy";
		String dateDescription = null;
		if("quarter".equals(dateType)){
			dateDescription = UtilProperties.getMessage("CommonUiLabels", "CommonQuarter", locale) + " " + (Integer.parseInt(monthQuarterValue) + 1);
		} else if("month".equals(dateType)){
			dateDescription = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale) + " " + (Integer.parseInt(monthQuarterValue) + 1);
		} else {
			dateDescription = "";
		}
		dateDescription += " " + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale) + " " + year;
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
			
			List<GenericValue> fixedAssetList = delegator.findByAnd("FixedAssetAndDetail", UtilMisc.toMap("fixedAssetId", fixedAssetId), null, false);
			if(UtilValidate.isEmpty(fixedAssetList)){
				return "error";
			}
			GenericValue fixedAsset = fixedAssetList.get(0);
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
			sheet.setColumnWidth(0, 14 * 250);
			sheet.setColumnWidth(1, 21 * 250);
			sheet.setColumnWidth(2, 45 * 250);
			sheet.setColumnWidth(3, 21 * 250);
			sheet.setColumnWidth(4, 15 * 220);
			sheet.setColumnWidth(5, 21 * 220);
			sheet.setColumnWidth(6, 21 * 220);
			Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
			int totalColumns = 7;
			int rownum = 1;
			Row row = sheet.createRow(rownum);
			row.setHeight((short)350);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, totalColumns - 3, totalColumns - 2));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOrganization", locale) + ": " + companyName);
			ExcelUtil.createCellOfRow(row, totalColumns - 3, styles.get("cell_bold_centered_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "S11DNNTemplate", locale));
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, totalColumns - 4, totalColumns - 1));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("BaseSalesUiLabels", "BSAddress", locale) + ": " + address);
			ExcelUtil.createCellOfRow(row, totalColumns - 4, styles.get("cell_centered_no_border_10"), null, "(" + UtilProperties.getMessage("BaseAccountingUiLabels", "TT133_2016_TT_BTC", locale) + ")");
			
			rownum += 3;
			row = sheet.createRow(rownum);
			row.setHeight((short) 350);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, (totalColumns - 1)/2 - 2 , (totalColumns - 1)/2 + 2));
			ExcelUtil.createCellOfRow(row, (totalColumns - 1)/2 - 2, styles.get("cell_bold_centered_no_border_12"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "TheTaiSanCoDinh", locale).toUpperCase());
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, (totalColumns - 1)/2 - 1 , (totalColumns - 1)/2 + 1));
			ExcelUtil.createCellOfRow(row, (totalColumns - 1)/2 - 1, styles.get("cell_centered_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNumber", locale) + ": " + fixedAssetId);
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, (totalColumns - 1)/2 - 2 , (totalColumns - 1)/2 + 2));
			String lapTheUiLabel = UtilProperties.getMessage("BaseAccountingUiLabels", "LapThe", locale) + " " + dateDescription;
			ExcelUtil.createCellOfRow(row, (totalColumns - 1)/2 - 2, styles.get("cell_centered_no_border_10"), null, lapTheUiLabel);
			
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			Timestamp receiptDate = fixedAsset.getTimestamp("receiptDate");
			String receiptNumber = "";
			if (UtilValidate.isNotEmpty(fixedAsset.getString("receiptNumber"))) {
				receiptNumber = fixedAsset.getString("receiptNumber");
			}
			String day = "", month = "", year1 = "";
			if (UtilValidate.isNotEmpty(receiptDate)) {
				day = String.valueOf(receiptDate.getDate());
				month = String.valueOf(receiptDate.getMonth() + 1);
				year1 = String.valueOf(receiptDate.getYear() + 1900);
			}
			String text = UtilProperties.getMessage("BaseAccountingUiLabels", "CanCuBienBanGiaoNhanTSCD", locale) + " " + receiptNumber + " "
						+ UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDay", locale).toLowerCase() + " " + day + " "
					    + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale).toLowerCase() + " " + month + " "
					    + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale).toLowerCase() + " " + year1;
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_italic_normal_left_no_border_10"), null, text);
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			text = UtilProperties.getMessage("BaseAccountingUiLabels", "TenKyHieuQuyCachTSCD", locale) + ": " + "[" + fixedAssetId + "] " + fixedAsset.getString("fixedAssetName");
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, text);
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			text = UtilProperties.getMessage("BaseAccountingUiLabels", "FixedAssetSerialNumber", locale) + ": " + (fixedAsset.get("serialNumber") != null ? fixedAsset.getString("serialNumber") : "");
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, text);
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			text = UtilProperties.getMessage("BaseAccountingUiLabels", "FixedAssetMadeInOrContruct", locale) + ": "
				+ (fixedAsset.get("countryOrigin") != null? fixedAsset.getString("countryOrigin") : "")
				+ "                                       "
				+ UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductionYear", locale) + ": " + (fixedAsset.get("yearMade") != null? String.valueOf(fixedAsset.getInteger("yearMade")) : "");
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, text);
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			text = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCManagingUsingParty", locale) + ":" + fixedAsset.getString("fullName");
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, text);
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			Timestamp dateAcquired = fixedAsset.getTimestamp("dateAcquired");
			Calendar cal = Calendar.getInstance();
			if(dateAcquired != null){
				cal.setTime(dateAcquired);
			}
			text = UtilProperties.getMessage("BaseAccountingUiLabels", "UsingFromYear", locale) + ": " + (dateAcquired != null? String.valueOf(cal.get(Calendar.YEAR)) : "");
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, text);
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCapacitySquare", locale));
			
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 0 , 0));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1 , 3));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4 , 6));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "SoHieuChungTu", locale));
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPurchaseCost", locale) + " " + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAsset", locale).toLowerCase());
            ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
            ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDepreciationValue", locale) + " " + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAsset", locale).toLowerCase());
            ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			
			/** ======================= fixed asset voucher ============================== **/
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			text = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDay", locale) + ", "
					+ UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale).toLowerCase() + ", "
					+ UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale).toLowerCase();
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, text);
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("AccountingUiLabels", "AccountingComments", locale));
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPurchaseCost", locale));
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale));
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDepreciationValue", locale));
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCongDon", locale));
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, "A");
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, "B");
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "C");
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, "1");
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, "2");
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, "3");
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, "4");
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
			calendar.set(Calendar.MONTH, Integer.parseInt(monthQuarterValue));
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
			Timestamp thruDate = UtilDateTime.getMonthEnd(sqlDate, timeZone, locale);
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("fixedAssetId", fixedAssetId));
			conds.add(EntityCondition.makeCondition("isPosted", true));
			conds.add(EntityCondition.makeCondition("voucherDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			List<GenericValue> faDepreciationCalcAndItems = delegator.findList("FixedAssetDepreciationCalcAndItem",
					EntityCondition.makeCondition(conds), null, UtilMisc.toList("year"), null, false);
			Map<String, Object> faDepMap = FastMap.newInstance();
			for (GenericValue item : faDepreciationCalcAndItems) {
				String key = item.getString("year");
				BigDecimal depreciationAmount = item.getBigDecimal("depreciationAmount");
				if (faDepMap.containsKey(key)) {
					BigDecimal value = (BigDecimal) faDepMap.get(key);
					depreciationAmount = depreciationAmount.add(value);
				} 
				faDepMap.put(key, depreciationAmount);
			}
			if(UtilValidate.isNotEmpty(faDepMap)){
				BigDecimal total = BigDecimal.ZERO;
				for (String key : faDepMap.keySet()) {
					BigDecimal depreciationAmount = (BigDecimal) faDepMap.get(key);
					total = total.add(depreciationAmount);
					rownum++;
					row = sheet.createRow(rownum);
					row.setHeight((short) 300);
					ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, fixedAsset.get("serialNumber") != null ? fixedAsset.getString("serialNumber") : "");
					ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_centered_border_full_10"), null, UtilFormatOut.formatDateTime(fixedAsset.getTimestamp("dateAcquired"), dateTimeFormat, locale, timeZone));
					ExcelUtil.createCellOfRow(row, 2, styles.get("cell_left_centered_border_full_10"), null, fixedAsset.get("description") != null ? fixedAsset.getString("description").trim() : "");
					BigDecimal purchaseCost = fixedAsset.getBigDecimal("purchaseCost");
					ExcelUtil.createCellOfRow(row, 3, styles.get("cell_right_centered_border_full_currency_10"), null, purchaseCost);
					ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_centered_border_full_10"), null, key);
					ExcelUtil.createCellOfRow(row, 5, styles.get("cell_right_centered_border_full_currency_10"), null, depreciationAmount);
					ExcelUtil.createCellOfRow(row, 6, styles.get("cell_right_centered_border_full_currency_10"), null, total);
				}
			}
			/**========================= ./end =========================**/
			
			rownum += 3;
			row = sheet.createRow(rownum);
			row.setHeight((short) 350);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , 1));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAccompanyToolsInclude", locale));
			
			/**=================== fixed asset accompany ========================**/
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1 , 2));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5 , 6));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale));
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "FixedAssetAccompanyName2", locale));
            ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseSalesUiLabels", "BSCalculateUomId", locale));
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCQuantity", locale));
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, UtilProperties.getMessage("BaseSalesUiLabels", "BSValue", locale));
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, "");

			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1 , 2));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5 , 6));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, "A");
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, "B");
            ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, "C");
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, "1");
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, "2");
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
			List<GenericValue> fixedAssetAccompanyList = delegator.findByAnd("FixedAssetAccompany", UtilMisc.toMap("fixedAssetId", fixedAssetId), UtilMisc.toList("componentName"), false);
			int i = 0;
			for(GenericValue fixedAssetAccompany: fixedAssetAccompanyList){
				rownum++;
				i++;
				row = sheet.createRow(rownum);
				row.setHeight((short) 300);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1 , 2));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5 , 6));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, String.valueOf(i));
				ExcelUtil.createCellOfRow(row, 1, styles.get("cell_left_centered_border_full_10"), null, fixedAssetAccompany.get("componentName") != null? fixedAssetAccompany.get("componentName") : "");
                ExcelUtil.createCellOfRow(row, 2, styles.get("cell_normal_centered_border_full_10"), null, "");
				ExcelUtil.createCellOfRow(row, 3, styles.get("cell_left_centered_border_full_10"), null, fixedAssetAccompany.get("unit") != null? fixedAssetAccompany.get("unit") : "");
				ExcelUtil.createCellOfRow(row, 4, styles.get("cell_right_centered_border_full_quantity_10"), null, fixedAssetAccompany.get("quantity") != null? String.valueOf(fixedAssetAccompany.get("quantity")) : "");
				if(fixedAssetAccompany.get("value") != null){
					BigDecimal accompanyValue = fixedAssetAccompany.getBigDecimal("value");
					ExcelUtil.createCellOfRow(row, 5, styles.get("cell_right_centered_border_full_currency_10"), null, accompanyValue);
				}else{
					ExcelUtil.createCellOfRow(row, 5, styles.get("cell_right_centered_border_full_currency_10"), null, "");
				}
				ExcelUtil.createCellOfRow(row, 6, styles.get("cell_normal_centered_border_full_10"), null, "");
			}
			/** ====================== ./end fixed asset accompany ==============================**/
			
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			text = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFADecrement", locale)
				  + " " + UtilProperties.getMessage("BaseAccountingUiLabels", "CommonVoucher", locale).toLowerCase()
				  + " " + UtilProperties.getMessage("BaseAccountingUiLabels", "VoucherNumber", locale).toLowerCase()
				  + "       " 
				  + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDay", locale).toLowerCase() + "    "
				  + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMonth", locale).toLowerCase() + "   "
				  + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYear", locale).toLowerCase() + "     " ;
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, text);
			
			rownum ++;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0 , totalColumns - 1));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetDecrementReason", locale));
			
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, totalColumns - 2, totalColumns - 1));
			text = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDay", locale) + "....."
					+ UtilProperties.getMessage("BaseSalesUiLabels", "BSMonthLowercase", locale) + "...."
					+ UtilProperties.getMessage("BaseSalesUiLabels", "BSYearLowercase", locale) + ".....";
			ExcelUtil.createCellOfRow(row, totalColumns - 2, styles.get("cell_italic_normal_center_no_border_10"), null, text);
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short)300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, totalColumns - 2, totalColumns - 1));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "NguoiLapBieu", locale));
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCChiefAccount", locale));
			ExcelUtil.createCellOfRow(row, totalColumns - 2, styles.get("cell_bold_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "NguoiDaiDienTheoPhapLuat", locale));
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short)300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, totalColumns - 2, totalColumns - 1));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSignatureFullName", locale));
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSignatureFullName", locale));
			ExcelUtil.createCellOfRow(row, totalColumns - 2, styles.get("cell_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "KyHoTenDongDau", locale));
			
			ExcelUtil.responseWrite(response, wb, "the_tai_san_S11_DNN");
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	private static int renderHeaderFAReportS09DNN(Map<String, CellStyle> styles, Sheet sheet, int rownum, Locale locale, String dateDescription, GenericValue fixedAssetType, String companyName, String address) {
		int totalColumns = 14;
		Row row = sheet.createRow(rownum);
		row.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, totalColumns - 4, totalColumns - 3));
		ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOrganization", locale) + ": " + companyName);
		ExcelUtil.createCellOfRow(row, totalColumns - 4, styles.get("cell_bold_centered_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "S09DNNTemplate", locale));
		
		rownum++;
		row = sheet.createRow(rownum);
		row.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, totalColumns - 5, totalColumns - 2));
		ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("BaseSalesUiLabels", "BSAddress", locale) + ": " + address);
		ExcelUtil.createCellOfRow(row, totalColumns - 5, styles.get("cell_centered_no_border_9"), null, "(" + UtilProperties.getMessage("BaseAccountingUiLabels", "TT133_2016_TT_BTC", locale) + ")");
		
		rownum += 3;
		row = sheet.createRow(rownum);
		row.setHeight((short) 350);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, (totalColumns - 1)/2 - 2 , (totalColumns - 1)/2 + 2));
		ExcelUtil.createCellOfRow(row, (totalColumns - 1)/2 - 2, styles.get("cell_bold_centered_no_border_12"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAsset", locale).toUpperCase());
		
		rownum++;
		row = sheet.createRow(rownum);
		row.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, (totalColumns - 1)/2 - 1 , (totalColumns - 1)/2 + 1));
		ExcelUtil.createCellOfRow(row, (totalColumns - 1)/2 - 1, styles.get("cell_centered_no_border_10"), null, dateDescription);
		
		rownum++;
		row = sheet.createRow(rownum);
		row.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, (totalColumns - 1)/2 - 2 , (totalColumns - 1)/2 + 2));
		String fixedAssetTypeDesc = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetTypeId", locale) + ": ";
		if (fixedAssetType != null) {
			fixedAssetTypeDesc += fixedAssetType.getString("description");
		} else {
			fixedAssetTypeDesc += "...........................";
		}
		ExcelUtil.createCellOfRow(row, (totalColumns - 1)/2 - 2, styles.get("cell_centered_no_border_10"), null, fixedAssetTypeDesc);
		
		String titleSequenceName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale);
		String titleFAIncrease = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNewFixedAsset", locale);
		String titleFADepreciation = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFADepreciation", locale);
		String titleFADecrease = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFADecrement", locale);
		String titleVoucher = UtilProperties.getMessage("BaseAccountingUiLabels", "CommonVoucher", locale);
		String titleFANameSpecSign = UtilProperties.getMessage("BaseAccountingUiLabels", "FixedAssetNameSpecSign", locale);
		String titleFAMadeIn = UtilProperties.getMessage("BaseAccountingUiLabels", "FixedAssetMadeIn", locale);
		String titleFAUsingFrom = UtilProperties.getMessage("BaseAccountingUiLabels", "UsingFromMonthYear", locale);
		String titleFAId = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetIdShort", locale);
		String titleFAPurchaseCost = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPurchaseCost", locale);
		String titleFAAccumulatedDepreciation = UtilProperties.getMessage("BaseAccountingUiLabels", "FAAccumulatedDepreciation", locale);
		String titleFADecrementReason = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetDecrementReason", locale);
		String titleVoucherID = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCVoucherID", locale);
		String titleDateMonth = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDateMonth", locale);
		String titleDepreciationPercent = UtilProperties.getMessage("BaseAccountingUiLabels", "DepreciationPercentage", locale);
		String titleDepreciationLevel = UtilProperties.getMessage("BaseAccountingUiLabels", "DepreciationLevel", locale);
		String titleDateMonthYear = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDateMonthYear", locale);
		
		rownum += 2;
		row = sheet.createRow(rownum);
		row.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 0 , 0));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1 , 7));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 8 , 10));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 11 , 13));
		ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, titleSequenceName);
		ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFAIncrease);
        ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
		ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFADepreciation);
        ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
		ExcelUtil.createCellOfRow(row, 11, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFADecrease);
        ExcelUtil.createCellOfRow(row, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
		
		rownum ++;
		row = sheet.createRow(rownum);
		row.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 3 , 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 4 , 4));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 5 , 5));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 6 , 6));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 7 , 7));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 10 , 10));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 13 , 13));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1 , 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 8 , 9));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 11 , 12));
		ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleVoucher);
        ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFAId);
		ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFANameSpecSign);
		ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFAMadeIn);
		ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFAUsingFrom);
		ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFAPurchaseCost);
		ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFADepreciation);
        ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
		ExcelUtil.createCellOfRow(row, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFAAccumulatedDepreciation);
		ExcelUtil.createCellOfRow(row, 11, styles.get("cell_bold_centered_header_excel_border_10"), null, titleVoucher);
        ExcelUtil.createCellOfRow(row, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
		ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_10"), null, titleFADecrementReason);
		
		rownum++;
		row = sheet.createRow(rownum);
		row.setHeight((short) 730);
		ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, titleVoucherID);
		ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, titleDateMonth);
        ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
        ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
		ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, titleDepreciationPercent);
		ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, titleDepreciationLevel);
        ExcelUtil.createCellOfRow(row, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
		ExcelUtil.createCellOfRow(row, 11, styles.get("cell_bold_centered_header_excel_border_10"), null, titleVoucherID);
		ExcelUtil.createCellOfRow(row, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, titleDateMonthYear);
        ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_10"), null, "");
		
		rownum++;
		row = sheet.createRow(rownum);
		ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_10"), null, "A");
		ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_10"), null, "B");
		ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_10"), null, "C");
		ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_10"), null, "D");
		ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_10"), null, "E");
		ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_10"), null, "G");
		ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_10"), null, "H");
		ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_centered_header_excel_border_10"), null, "1");
		ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_centered_header_excel_border_10"), null, "2");
		ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_centered_header_excel_border_10"), null, "3");
		ExcelUtil.createCellOfRow(row, 10, styles.get("cell_bold_centered_header_excel_border_10"), null, "4");
		ExcelUtil.createCellOfRow(row, 11, styles.get("cell_bold_centered_header_excel_border_10"), null, "I");
		ExcelUtil.createCellOfRow(row, 12, styles.get("cell_bold_centered_header_excel_border_10"), null, "K");
		ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_10"), null, "L");
		return rownum;
	}
	
	private static int renderFooterFAReportS09DNN(Map<String, CellStyle> styles, Sheet sheet, int rownum, Locale locale) {
		int totalColumns = 14;
		Row row = sheet.createRow(rownum);
		row.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
		String rowContent = " - " + UtilProperties.getMessage("BaseAccountingUiLabels", "SoTaiSanCo", locale) + "...."
							+ UtilProperties.getMessage("CommonUiLabels", "CommonPage", locale).toLowerCase() + ", "
							+ UtilProperties.getMessage("BaseAccountingUiLabels", "DanhSoTuTrang01DenTrang", locale) + "......";
		ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, rowContent);
		
		rownum++;
		row = sheet.createRow(rownum);
		row.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
		ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_no_border_10"), null, " - " + UtilProperties.getMessage("BaseAccountingUiLabels", "NgayMoSo", locale) + "...." );
		
		rownum++;
		row = sheet.createRow(rownum);
		row.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, totalColumns - 4, totalColumns - 2));
		rowContent = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCDay", locale) + "....."
				+ UtilProperties.getMessage("BaseSalesUiLabels", "BSMonthLowercase", locale) + "...."
				+ UtilProperties.getMessage("BaseSalesUiLabels", "BSYearLowercase", locale) + ".....";
		ExcelUtil.createCellOfRow(row, totalColumns - 4, styles.get("cell_centered_no_border_9"), null, rowContent);
		
		rownum += 2;
		row = sheet.createRow(rownum);
		row.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, totalColumns - 4, totalColumns - 2));
		ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "NguoiLapBieu", locale));
		ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCChiefAccount", locale));
		ExcelUtil.createCellOfRow(row, totalColumns - 4, styles.get("cell_bold_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "NguoiDaiDienTheoPhapLuat", locale));
		
		rownum++;
		row = sheet.createRow(rownum);
		row.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, totalColumns - 4, totalColumns - 2));
		ExcelUtil.createCellOfRow(row, 0, styles.get("cell_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSignatureFullName", locale));
		ExcelUtil.createCellOfRow(row, 5, styles.get("cell_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSignatureFullName", locale));
		ExcelUtil.createCellOfRow(row, totalColumns - 4, styles.get("cell_centered_no_border_9"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "KyHoTenDongDau", locale));
		
		return rownum;
	}
	
	public static String exportFADepPeriodsReportExcel(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
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
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_no_border_12"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFADepPeriodsReport", locale).toUpperCase());
			
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
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetIdShort", locale));
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "FixedAssetSerialNumber", locale));
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetName", locale));
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCUseDate", locale));
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFADepMonth", locale));
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFADepGlAccountId", locale));
			ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAllocGlAccoutId", locale));
			ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPurchaseCost", locale));
			ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPeriodFADepAmount", locale));
			ExcelUtil.createCellOfRow(row, 10, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAccumulatedFADepYearAmount", locale));
			ExcelUtil.createCellOfRow(row, 11, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "FAAccumulatedDepreciation", locale));
			ExcelUtil.createCellOfRow(row, 12, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSalvageValue", locale));
			ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSalvageTime", locale));
			
			List<GenericValue> fixedAssetTypeList = delegator.findList("FixedAssetType",
					EntityCondition.makeCondition("parentTypeId", EntityOperator.NOT_EQUAL, null), null, UtilMisc.toList("fixedAssetTypeId"), null, false);
			if (UtilValidate.isNotEmpty(fixedAssetTypeList)) {
				int faTypeSeq = 1;
				
				BigDecimal sumUnitPriceTotal = BigDecimal.ZERO;
				BigDecimal sumFAAmountMonthTotal = BigDecimal.ZERO;
				BigDecimal sumFAAmountYearTotal = BigDecimal.ZERO;
				BigDecimal sumFAAmountTotal = BigDecimal.ZERO;
				BigDecimal sumFAAmountRemainTotal = BigDecimal.ZERO;
				for (GenericValue faType : fixedAssetTypeList) {
					int seq = 1;
					
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("fixedAssetTypeId", faType.getString("fixedAssetTypeId")));
					conditions.add(EntityCondition.makeCondition("datePurchase", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
					List<GenericValue> listFixedAsset = delegator.findList("FixedAssetAndDetail", EntityCondition.makeCondition(conditions), null, null, null, false);
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("voucherDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
					conditions.add(EntityCondition.makeCondition("isPosted", true));
					List<GenericValue> faDepAndItemList = delegator.findList("FixedAssetDepreciationCalcAndItem", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-voucherDate"), null, false);
					
					if (UtilValidate.isNotEmpty(listFixedAsset)) {
						String faTypeName = (String) faType.get("description", locale);
						
						rownum++;
						row = sheet.createRow(rownum);
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 13));
						ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_centered_bold_border_full_10"), null, String.valueOf(faTypeSeq) + " - " + faTypeName);
						
						BigDecimal sumUnitPrice = BigDecimal.ZERO;
						BigDecimal sumFAAmountMonth = BigDecimal.ZERO;
						BigDecimal sumFAAmountYear = BigDecimal.ZERO;
						BigDecimal sumFAAmount = BigDecimal.ZERO;
						BigDecimal sumFAAmountRemain = BigDecimal.ZERO;
						for (GenericValue item : listFixedAsset) {
							String fixedAssetId = item.getString("fixedAssetId");
							String fixedAssetName = item.getString("fixedAssetName");
							Timestamp dateAcquired = item.getTimestamp("dateAcquired");
							String dateAcquiredStr = format.format(dateAcquired);
							BigDecimal unitPrice = item.getBigDecimal("purchaseCost");
							
							EntityCondition faCond = EntityCondition.makeCondition("fixedAssetId", fixedAssetId);
							EntityCondition fromDateCond = EntityCondition.makeCondition("voucherDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate);
							EntityCondition yearCond = EntityCondition.makeCondition("voucherDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDateYear);
							List<GenericValue> tempFADepList = EntityUtil.filterByCondition(faDepAndItemList, faCond);
							List<GenericValue> tempFADepMonthList = EntityUtil.filterByCondition(tempFADepList, fromDateCond);
							List<GenericValue> tempFADepYearList = EntityUtil.filterByCondition(tempFADepList, yearCond);
							
							Integer usefulLives = item.getLong("usefulLives").intValue();
							Integer faDepTimeRemain = usefulLives - tempFADepList.size();
							BigDecimal faDepAmount = BigDecimal.ZERO;
							for (GenericValue faDep : tempFADepList) {
								faDepAmount = faDepAmount.add(faDep.getBigDecimal("depreciationAmount"));
							}
							BigDecimal faDepAmountMonth = BigDecimal.ZERO;
							for (GenericValue faDepMonth : tempFADepMonthList) {
								faDepAmountMonth = faDepAmountMonth.add(faDepMonth.getBigDecimal("depreciationAmount"));
							}
							BigDecimal faDepAmountYear = BigDecimal.ZERO;
							for (GenericValue faDepYear : tempFADepYearList) {
								faDepAmountYear = faDepAmountYear.add(faDepYear.getBigDecimal("depreciationAmount"));
							}
							BigDecimal faDepAmountRemain = unitPrice.subtract(faDepAmount);
							
							rownum++;
							row = sheet.createRow(rownum);
							ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, String.valueOf(seq));
							ExcelUtil.createCellOfRow(row, 1, styles.get("cell_left_centered_border_full_10"), null, fixedAssetId);
							ExcelUtil.createCellOfRow(row, 2, styles.get("cell_left_centered_border_full_10"), null, item.getString("serialNumber"));
							ExcelUtil.createCellOfRow(row, 3, styles.get("cell_left_centered_border_full_10"), null, fixedAssetName);
							ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_centered_border_full_10"), null, dateAcquiredStr);
							ExcelUtil.createCellOfRow(row, 5, styles.get("cell_normal_centered_border_full_10"), null, usefulLives);
							ExcelUtil.createCellOfRow(row, 6, styles.get("cell_normal_centered_border_full_10"), null, item.getString("costGlAccountId"));
							ExcelUtil.createCellOfRow(row, 7, styles.get("cell_normal_centered_border_full_10"), null, item.getString("depGlAccountId"));
							ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_full_currency_10"), null, unitPrice);
							ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_full_currency_10"), null, faDepAmountMonth);
							ExcelUtil.createCellOfRow(row, 10, styles.get("cell_right_centered_border_full_currency_10"), null, faDepAmountYear);
							ExcelUtil.createCellOfRow(row, 11, styles.get("cell_right_centered_border_full_currency_10"), null, faDepAmount);
							ExcelUtil.createCellOfRow(row, 12, styles.get("cell_right_centered_border_full_currency_10"), null, faDepAmountRemain);
							ExcelUtil.createCellOfRow(row, 13, styles.get("cell_normal_centered_border_full_10"), null, faDepTimeRemain);
							
							seq++;
							sumUnitPrice = sumUnitPrice.add(unitPrice);
							sumFAAmountMonth = sumFAAmountMonth.add(faDepAmountMonth);
							sumFAAmountYear = sumFAAmountYear.add(faDepAmountYear);
							sumFAAmount = sumFAAmount.add(faDepAmount);
							sumFAAmountRemain = sumFAAmountRemain.add(faDepAmountRemain);
						}
						rownum++;
						row = sheet.createRow(rownum);
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
						ExcelUtil.createCellOfRow(row, 0, styles.get("cell_left_centered_bold_border_grey_10"), null,
								UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetTotal", locale) + ": " + String.valueOf(faTypeSeq) + " - " + faTypeName);
						ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_grey_currency_10"), null, sumUnitPrice);
						ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_grey_currency_10"), null, sumFAAmountMonth);
						ExcelUtil.createCellOfRow(row, 10, styles.get("cell_right_centered_border_grey_currency_10"), null, sumFAAmountYear);
						ExcelUtil.createCellOfRow(row, 11, styles.get("cell_right_centered_border_grey_currency_10"), null, sumFAAmount);
						ExcelUtil.createCellOfRow(row, 12, styles.get("cell_right_centered_border_grey_currency_10"), null, sumFAAmountRemain);
						ExcelUtil.createCellOfRow(row, 13, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, "");
						
						faTypeSeq++;
						
						sumUnitPriceTotal = sumUnitPriceTotal.add(sumUnitPrice);
						sumFAAmountMonthTotal = sumFAAmountMonthTotal.add(sumFAAmountMonth);
						sumFAAmountYearTotal = sumFAAmountYearTotal.add(sumFAAmountYear);
						sumFAAmountTotal = sumFAAmountTotal.add(sumFAAmount);
						sumFAAmountRemainTotal = sumFAAmountRemainTotal.add(sumFAAmountRemain);
					}
				}
				
				rownum++;
				row = sheet.createRow(rownum);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_header_excel_border_grey_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTotalReport", locale));
				ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_grey_currency_10"), null, sumUnitPriceTotal);
				ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_grey_currency_10"), null, sumFAAmountMonthTotal);
				ExcelUtil.createCellOfRow(row, 10, styles.get("cell_right_centered_border_grey_currency_10"), null, sumFAAmountYearTotal);
				ExcelUtil.createCellOfRow(row, 11, styles.get("cell_right_centered_border_grey_currency_10"), null, sumFAAmountTotal);
				ExcelUtil.createCellOfRow(row, 12, styles.get("cell_right_centered_border_grey_currency_10"), null, sumFAAmountRemainTotal);
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
			
			ExcelUtil.responseWrite(response, wb, "bang_khau_hao_tscd");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
}