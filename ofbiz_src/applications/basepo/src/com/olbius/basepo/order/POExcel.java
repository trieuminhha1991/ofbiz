package com.olbius.basepo.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.basepo.utils.ExcelUtil;
import com.olbius.basesales.order.OrderReadHelper;
import com.olbius.product.util.ProductUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

public class POExcel {

	public static final String COLON = ": ";
	public static final String DOT = ".";
	public static final String PO_RESOURCE = "BasePOUiLabels";
	public static final String LOG_RESOURCE = "BaseLogisticsUiLabels";
	public static final String SALES_RESOURCE = "BaseSalesUiLabels";

	public static void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String orderId = request.getParameter("orderId");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		try {
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = ExcelUtil.createStylesNormal(wb);
			styles.putAll(createStylesNormal(wb));
			once(request, delegator, locale, timeZone, wb, styles, orderId, "Sheet1");
			ExcelUtil.responseWrite(response, wb, "don-dat-hang-");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportMutil(HttpServletRequest request, HttpServletResponse response) throws Exception {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		SimpleDateFormat format = new SimpleDateFormat("ddMMyy");

		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = ExcelUtil.createStylesNormal(wb);
		styles.putAll(createStylesNormal(wb));

		String orderIdData = request.getParameter("orderIdData");
		JSONArray listOrderIdData = JSONArray.fromObject(orderIdData);
		for (Object orderId : listOrderIdData) {
			once(request, delegator, locale, timeZone, wb, styles, orderId, orderId + "-" + format.format(new Date()));
		}
		ExcelUtil.responseWrite(response, wb, "don-dat-hang-");
	}

	@SuppressWarnings("unchecked")
	private static void once(HttpServletRequest request, GenericDelegator delegator, Locale locale, TimeZone timeZone,
			Workbook wb, Map<String, CellStyle> styles, Object orderId, String sheetName) throws Exception {
		if (UtilValidate.isEmpty(orderId)) {
			throw new Exception(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSGetPurchaseOrderIdIsError", locale));
		}
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);

		Map<String, Object> info = poInfo(request, delegator, locale, orderHeader, orderId);

		String preferredCurrencyUomId = (String) info.get("preferredCurrencyUomId");

		Sheet sheet = sheetSetting(wb, sheetName);
		int rownum = ExcelUtil.insertLogo(wb, sheet);

		Row row = sheet.createRow(rownum);
		row.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
		Cell cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(PO_RESOURCE, "POOrderFormTitle", locale));
		cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum+=2;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
		cell = row.createCell(0);
		cell.setCellValue((String) info.get("groupName"));
		cell.setCellStyle(styles.get("cell_bordered_red_9"));
		cell = row.createCell(5);
		cell.setCellValue(UtilProperties.getMessage(PO_RESOURCE, "DAOrderId", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(6);
		cell.setCellValue((String) orderId);
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(7);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "Status", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(8);
		cell.setCellValue((String) info.get("currentStatus"));
		cell.setCellStyle(styles.get("cell_normal_10"));
		rownum++;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,1));
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "Address", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,2,4));
		cell = row.createCell(2);
		cell.setCellValue((String) info.get("companyAddress"));
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(5);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "CreatedDate", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,6,7));
		cell = row.createCell(6);
		cell.setCellValue(UtilFormatOut.formatDateTime(orderHeader.getTimestamp("orderDate"), "dd/MM/yyyy HH:mm:ss",
				locale, timeZone));
		cell.setCellStyle(styles.get("cell_normal_10"));
		rownum++;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,1));
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "BLTaxId", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(2);
		cell.setCellValue((String) info.get("taxIdCompany"));
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(5);
		cell.setCellValue(UtilProperties.getMessage(SALES_RESOURCE, "BSShipBeforeDate", locale) + COLON);
		cell.setCellStyle(styles.get("cell_bordered_red_9"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,6,7));
		cell = row.createCell(6);
		cell.setCellValue(UtilFormatOut.formatDateTime(orderHeader.getTimestamp("shipBeforeDate"),
				"dd/MM/yyyy HH:mm:ss", locale, timeZone));
		cell.setCellStyle(styles.get("cell_normal_10"));
		rownum++;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,1));
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(SALES_RESOURCE, "BSPhone", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(2);
		cell.setCellValue((String) info.get("contactNumber"));
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(3);
		cell.setCellValue("Fax" + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(5);
		cell.setCellValue(UtilProperties.getMessage(SALES_RESOURCE, "BSShipAfterDate", locale) + COLON);
		cell.setCellStyle(styles.get("cell_bordered_red_9"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,6,7));
		cell = row.createCell(6);
		cell.setCellValue(UtilFormatOut.formatDateTime(orderHeader.getTimestamp("shipAfterDate"), "dd/MM/yyyy HH:mm:ss",
				locale, timeZone));
		cell.setCellStyle(styles.get("cell_normal_10"));
		rownum += 2;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(PO_RESOURCE, "PODeliveryLocation", locale));
		cell.setCellStyle(styles.get("cell_bordered_dark_blue_9"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,5,9));
		cell = row.createCell(5);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "Supplier", locale));
		cell.setCellStyle(styles.get("cell_bordered_dark_blue_9"));
		rownum++;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,1));
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "Facility", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(2);
		cell.setCellValue((String) info.get("facilityName"));
		cell.setCellStyle(styles.get("cell_normal_10"));
        cell = row.createCell(3);
        cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "FacilityId", locale) + COLON);
        cell.setCellStyle(styles.get("cell_normal_10"));
        cell = row.createCell(4);
        if (UtilValidate.isNotEmpty(info.get("facilityCode"))) {
        	cell.setCellValue((String) info.get("facilityCode"));
		} else {
			cell.setCellValue((String) info.get("facilityId"));
		}
        cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(5);
		cell.setCellValue(UtilProperties.getMessage(PO_RESOURCE, "RoleSeller", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
		sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,6,7));
		cell = row.createCell(6);
		cell.setCellValue((String) info.get("supplierName"));
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(8);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "SupplierId", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
        cell = row.createCell(9);
        cell.setCellValue((String) info.get("supplierId"));
        cell.setCellStyle(styles.get("cell_normal_10"));
		rownum++;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum+1,0,1));
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "Address", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_vtop_wrap_text_10"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum+1,2,4));
		cell = row.createCell(2);
		cell.setCellValue((String) info.get("facilityAddress"));
		cell.setCellStyle(styles.get("cell_normal_vtop_wrap_text_10"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum+1,5,5));
		cell = row.createCell(5);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "Address", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_vtop_wrap_text_10"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum+1,6,9));
		cell = row.createCell(6);
		cell.setCellValue((String) info.get("supplierAddress"));
		cell.setCellStyle(styles.get("cell_normal_vtop_wrap_text_10"));
		rownum+=2;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,1));
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(PO_RESOURCE, "BPContactInfo", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,2,4));
		cell = row.createCell(2);
		cell.setCellValue((String) info.get("facilityContactNumber"));
		cell.setCellStyle(styles.get("cell_normal_10"));
		cell = row.createCell(5);
		cell.setCellValue(UtilProperties.getMessage(PO_RESOURCE, "BPContactInfo", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,6,9));
		cell = row.createCell(6);
		cell.setCellValue((String) info.get("supplierContactNumber"));
		cell.setCellStyle(styles.get("cell_normal_10"));
		rownum+=2;

		List<String> titles = UtilMisc.toList(UtilProperties.getMessage(SALES_RESOURCE, "BSSTT", locale),
				UtilProperties.getMessage(LOG_RESOURCE, "ProductCodeSum", locale),
				UtilProperties.getMessage(SALES_RESOURCE, "BSProductName", locale),
				UtilProperties.getMessage(LOG_RESOURCE, "BLPrimaryUPC", locale),
				UtilProperties.getMessage(SALES_RESOURCE, "BSUom", locale),
				UtilProperties.getMessage(PO_RESOURCE, "BPPacking", locale));
		titles.addAll(UtilMisc.toList(UtilProperties.getMessage(SALES_RESOURCE, "BSQuantity", locale),
				UtilProperties.getMessage(LOG_RESOURCE, "UnitPrice", locale),
				UtilProperties.getMessage(SALES_RESOURCE, "BSAdjustment", locale),
				UtilProperties.getMessage(SALES_RESOURCE, "BSItemTotal", locale)));

		row = sheet.createRow(rownum);
		row.setHeight((short) 900);
        for (String t : titles) {
            cell = row.createCell(titles.indexOf(t));
            cell.setCellValue(t);
            cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));

        }
		rownum++;

		Map<String, Object> item = (Map<String, Object>) info.get("orderItems");
		List<Map<String, Object>> listItemLine = (List<Map<String, Object>>) item.get("listItemLine");
		List<Map<String, Object>> listTaxTotal = (List<Map<String, Object>>) item.get("listTaxTotal");
		List<GenericValue> orderHeaderAdjustments = (List<GenericValue>) item.get("orderHeaderAdjustments");
		List<GenericValue> orderAdjustments = (List<GenericValue>) item.get("orderAdjustments");
		BigDecimal orderSubTotal = (BigDecimal) info.get("orderSubTotal");
		BigDecimal otherAdjAmount = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true,
				false, false);
		BigDecimal taxAmount = (BigDecimal) OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments)
				.get("taxGrandTotal");
		BigDecimal grandTotal = (BigDecimal) item.get("grandTotal");

		int stt = 1;
		for (Map<String, Object> x : listItemLine) {
			row = sheet.createRow(rownum);
			int _count = 0;

			cell = row.createCell(_count);
			cell.setCellValue(stt);
			cell.setCellStyle(styles.get("cell_center_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue((String) (x.get("productCode")));
			cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue((String) x.get("itemDescription"));
			cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue(getBarcodePrimary(delegator, x.get("productId")));
			cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue((String) x.get("quantityUomDescription"));
			cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue(UtilFormatOut.formatQuantity((BigDecimal) x.get("packing")));
			cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue(UtilFormatOut.formatQuantity((BigDecimal) x.get("quantity")));
			cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue(
					UtilFormatOut.formatCurrency((BigDecimal) x.get("unitPriceBeVAT"), preferredCurrencyUomId, locale, 2));
			cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue(
					UtilFormatOut.formatCurrency((BigDecimal) x.get("adjustment"), preferredCurrencyUomId, locale, 2));
			cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
			_count++;

			cell = row.createCell(_count);
			cell.setCellValue(
					UtilFormatOut.formatCurrency((BigDecimal) x.get("subTotalBeVAT"), preferredCurrencyUomId, locale, 2));
			cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
			_count++;

			rownum++;
			stt++;
		}

		for (Map<String, Object> x : listTaxTotal) {
			row = sheet.createRow(rownum);
            sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,2,7));
			cell = row.createCell(2);
			cell.setCellValue((String) x.get("description"));
			cell.setCellStyle(styles.get("cell_right_10"));

			BigDecimal amount = (BigDecimal) x.get("amount");
			if (UtilValidate.isNotEmpty(amount)) {
				cell = row.createCell(9);
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
					amount = amount.abs();
				}
				cell.setCellValue(UtilFormatOut.formatCurrency(amount, preferredCurrencyUomId, locale, 2));
				cell.setCellStyle(styles.get("cell_right_10"));
			}
			rownum++;
		}
		for (GenericValue x : orderHeaderAdjustments) {
			GenericValue adjustmentType = x.getRelatedOne("OrderAdjustmentType", false);
			BigDecimal adjustmentAmount = OrderReadHelper.calcOrderAdjustment(x, orderSubTotal);
			if (!BigDecimal.ZERO.equals(adjustmentAmount)) {
				row = sheet.createRow(rownum);

				if (UtilValidate.isNotEmpty(x.get("comments")) || UtilValidate.isNotEmpty(x.get("description"))) {
                    sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,2,7));
					cell = row.createCell(2);
					cell.setCellValue(x.getString("comments") + x.getString("description")
							+ adjustmentType.get("description", locale));
					cell.setCellStyle(styles.get("cell_bold_right_no_border_10"));
				}
				cell = row.createCell(9);
				if (adjustmentAmount.compareTo(BigDecimal.ZERO) < 0) {
					adjustmentAmount = adjustmentAmount.abs();
				}
				cell.setCellValue(UtilFormatOut.formatCurrency(adjustmentAmount, preferredCurrencyUomId, locale, 2));
				cell.setCellStyle(styles.get("cell_right_10"));

				rownum++;
			}
		}

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,2,7));
		cell = row.createCell(2);
		cell.setCellValue(UtilProperties.getMessage(SALES_RESOURCE, "BSOrderItemsSubTotal", locale));
		cell.setCellStyle(styles.get("cell_bold_right_no_border_10"));
		if (orderSubTotal.compareTo(BigDecimal.ZERO) < 0) {
			orderSubTotal = orderSubTotal.abs();
		}
		cell = row.createCell(9);
		cell.setCellValue(UtilFormatOut.formatCurrency(orderSubTotal, preferredCurrencyUomId, locale, 2));
		cell.setCellStyle(styles.get("cell_right_10"));
		rownum++;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,2,7));
        cell = row.createCell(2);
		cell.setCellValue(UtilProperties.getMessage(SALES_RESOURCE, "BSTotalOrderAdjustments", locale));
		cell.setCellStyle(styles.get("cell_bold_right_no_border_10"));
		if (otherAdjAmount.compareTo(BigDecimal.ZERO) < 0) {
			otherAdjAmount = otherAdjAmount.abs();
		}
		cell = row.createCell(9);
		cell.setCellValue(UtilFormatOut.formatCurrency(otherAdjAmount, preferredCurrencyUomId, locale, 2));
		cell.setCellStyle(styles.get("cell_right_10"));
		rownum++;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,2,7));
        cell = row.createCell(2);
		cell.setCellValue(UtilProperties.getMessage(SALES_RESOURCE, "BSTotalSalesTax", locale));
		cell.setCellStyle(styles.get("cell_bold_right_no_border_10"));
		if (taxAmount.compareTo(BigDecimal.ZERO) < 0) {
			taxAmount = taxAmount.abs();
		}
		cell = row.createCell(9);
		cell.setCellValue(UtilFormatOut.formatCurrency(taxAmount, preferredCurrencyUomId, locale, 2));
		cell.setCellStyle(styles.get("cell_right_10"));
		rownum++;

		row = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,2,7));
        cell = row.createCell(2);
		cell.setCellValue(UtilProperties.getMessage(SALES_RESOURCE, "BSTotalAmountPayment", locale));
		cell.setCellStyle(styles.get("cell_bold_right_no_border_10"));
		if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
			grandTotal = grandTotal.abs();
		}
		cell = row.createCell(9);
		cell.setCellValue(UtilFormatOut.formatCurrency(grandTotal, preferredCurrencyUomId, locale, 2));
		cell.setCellStyle(styles.get("cell_bold_right_no_border_10"));
		rownum++;

		row = sheet.createRow(rownum);
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(LOG_RESOURCE, "Notes", locale) + COLON);
		cell.setCellStyle(styles.get("cell_normal_10"));

	}

	private static Map<String, Object> poInfo(HttpServletRequest request, GenericDelegator delegator, Locale locale,
			GenericValue orderHeader, Object orderId) throws Exception {
		Map<String, Object> info = FastMap.newInstance();

		String organizationId = PartyUtil.getRootOrganization(delegator, null);
		info.putAll(delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), true));

		GenericValue currentStatus = orderHeader.getRelatedOne("StatusItem", false);
		if (UtilValidate.isNotEmpty(currentStatus)) {
			info.put("currentStatus", currentStatus.get("description", locale));
		}

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

		conditions.clear();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition
				.makeCondition(UtilMisc.toMap("partyId", organizationId, "contactMechPurposeTypeId", "PRIMARY_PHONE")));
		dummy = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conditions), null, null,
				null, true);
		if (UtilValidate.isNotEmpty(dummy)) {
			info.put("contactNumber",
					delegator.findOne("TelecomNumber",
							UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), true)
							.getString("contactNumber"));
		}

		conditions.clear();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", organizationId)));
		dummy = delegator.findList("PartyTaxAuthInfo", EntityCondition.makeCondition(conditions), null, null, null,
				true);
		if (UtilValidate.isNotEmpty(dummy)) {
			info.put("taxIdCompany", EntityUtil.getFirst(dummy).getString("partyTaxId"));
		}

		conditions.clear();
		conditions.add(EntityCondition
				.makeCondition(UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
		dummy = delegator.findList("OrderContactMech", EntityCondition.makeCondition(conditions), null, null, null,
				false);
		if (UtilValidate.isNotEmpty(dummy)) {
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contactMechId",
					EntityUtil.getFirst(dummy).get("contactMechId"), "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
			dummy = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(conditions), null,
					null, null, true);
			if (UtilValidate.isNotEmpty(dummy)) {
				GenericValue fa = delegator.findOne("Facility",
						UtilMisc.toMap("facilityId", EntityUtil.getFirst(dummy).get("facilityId")), true);
				Object facilityId = EntityUtil.getFirst(dummy).get("facilityId");
				info.put("facilityName", fa.getString("facilityName"));
				info.put("facilityCode", fa.getString("facilityCode"));
				info.put("facilityId", facilityId);

				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", "PRIMARY_PHONE")));
				dummy = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(conditions),
						null, null, null, true);
				if (UtilValidate.isNotEmpty(dummy)) {
					info.put("facilityContactNumber",
							delegator
									.findOne("TelecomNumber",
											UtilMisc.toMap("contactMechId",
													EntityUtil.getFirst(dummy).get("contactMechId")),
											true)
									.getString("contactNumber"));
				}
			}
		}

		List<Map<String, GenericValue>> orderContactMechValueMaps = org.ofbiz.party.contact.ContactMechWorker
				.getOrderContactMechValueMaps(delegator, (String) orderId);
		for (Map<String, GenericValue> x : orderContactMechValueMaps) {
			GenericValue contactMech = x.get("contactMech");
			if ("POSTAL_ADDRESS".equals(contactMech.get("contactMechTypeId"))) {
				GenericValue postalAddress = x.get("postalAddress");
				if (UtilValidate.isNotEmpty(postalAddress)) {
					GenericValue postalAddressFullNameDetail = EntityUtil
							.getFirst(delegator.findByAnd("PostalAddressFullNameDetail",
									UtilMisc.toMap("contactMechId", postalAddress.get("contactMechId")), null, false));
					String addressFullName = "";
					if (postalAddress.get("toName") != null)
						addressFullName += postalAddress.get("toName");
					if (postalAddress.get("attnName") != null)
						addressFullName += " (" + postalAddress.get("attnName") + ")";
					if (postalAddress.get("toName") != null || postalAddress.get("attnName") != null)
						addressFullName += ". ";
					if (UtilValidate.isNotEmpty(postalAddressFullNameDetail)) {
						addressFullName += postalAddressFullNameDetail.get("fullName");
					}
					info.put("facilityAddress", addressFullName);
					break;
				}
			}
		}

		OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);
		info.put("orderSubTotal", orderReadHelper.getNewOrderItemsSubTotal());
		GenericValue displayParty = orderReadHelper.getSupplierAgent();
		if (UtilValidate.isNotEmpty(displayParty)) {
			info.put("supplierName", displayParty.get("groupName"));
			info.put("supplierId", PartyUtil.getPartyCode(delegator, displayParty.getString("partyId")));
			info.put("preferredCurrencyUomId", orderHeader.getString("currencyUom"));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", displayParty.get("partyId"),
					"contactMechPurposeTypeId", "PRIMARY_LOCATION")));
			dummy = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conditions), null, null,
					null, false);
			if (UtilValidate.isNotEmpty(dummy)) {
				String fullName = delegator
						.findOne("PostalAddressDetail",
								UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false)
						.getString("fullName");
				if (UtilValidate.isNotEmpty(fullName)) {
					fullName = fullName.replaceAll(", __", "");
				}
				info.put("supplierAddress", fullName);
			}

			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", displayParty.get("partyId"),
					"contactMechPurposeTypeId", "PRIMARY_PHONE")));
			dummy = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conditions), null, null,
					null, true);
			if (UtilValidate.isNotEmpty(dummy)) {
				info.put("supplierContactNumber",
						delegator
								.findOne("TelecomNumber",
										UtilMisc.toMap("contactMechId",
												EntityUtil.getFirst(dummy).get("contactMechId")),
										true)
								.getString("contactNumber"));
			}
		}
		info.put("orderItems", orderItems(request, delegator, locale, orderHeader, orderId));
		return info;
	}

	private static Map<String, Object> orderItems(HttpServletRequest request, GenericDelegator delegator, Locale locale,
			GenericValue orderHeader, Object orderId) throws Exception {
		Map<String, Object> item = FastMap.newInstance();
		List<Map<String, Object>> listItemLine = FastList.newInstance();
		List<Map<String, Object>> listTaxTotal = new ArrayList<Map<String, Object>>();
		BigDecimal taxTotalOrderItems = BigDecimal.ZERO;

		OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);
		List<GenericValue> orderItems = null;
		if ("ORDER_CANCELLED".equals(orderHeader.get("statusId"))) {
			orderItems = orderReadHelper.getOrderItems();
		} else {
			orderItems = orderReadHelper.getValidOrderItems();
		}

		List<GenericValue> orderAdjustments = orderReadHelper.getAdjustments();

		BigDecimal grandTotal = OrderReadHelper.getNewOrderGrandTotal(orderItems, orderReadHelper.getNewAdjustments());

		boolean hasPromoSettlement = false;
		if (orderItems != null) {
			for (GenericValue orderItem : orderItems) {
				if (!hasPromoSettlement && "PRODPROMO_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId")))
					hasPromoSettlement = true;
				GenericValue orderItemType = orderItem.getRelatedOne("OrderItemType", false);
				GenericValue product = orderItem.getRelatedOne("Product", false);

				String requireAmount = product.getString("requireAmount");
				String productId = orderItem.getString("productId");
				String seqId = orderItem.getString("orderItemSeqId");
				String itemDescription = orderItem.getString("itemDescription");
				String supplierProductId = orderItem.getString("supplierProductId");
				List<GenericValue> listBarcodeGeneric = delegator.findByAnd("GoodIdentification",
						UtilMisc.toMap("goodIdentificationTypeId", "SKU", "productId", productId), null, false);
				String barcode = null;
				if (UtilValidate.isNotEmpty(listBarcodeGeneric)) {
					GenericValue barcodeGeneric = null;
					if (UtilValidate.isNotEmpty(orderItem.get("quantityUomId"))) {
						barcodeGeneric = EntityUtil.getFirst(EntityUtil.filterByAnd(listBarcodeGeneric,
								UtilMisc.toMap("uomId", orderItem.get("quantityUomId"))));
					} else {
						barcodeGeneric = EntityUtil.getFirst(listBarcodeGeneric);
					}
					if (UtilValidate.isNotEmpty(barcodeGeneric))
						barcode = barcodeGeneric.getString("idValue");
				}
				Timestamp expireDate = orderItem.getTimestamp("expireDate");

				boolean isNormal = true;
				BigDecimal alternativeQuantity = orderItem.getBigDecimal("alternativeQuantity");
				BigDecimal alternativeUnitPrice = orderItem.getBigDecimal("alternativeUnitPrice");
				if (alternativeQuantity != null && alternativeUnitPrice != null) {
					isNormal = false;
				}
				String quantityUomDescription = "";
				GenericValue quantityUomGeneric = delegator.findOne("Uom",
						UtilMisc.toMap("uomId", orderItem.get("quantityUomId")), false);
				if (quantityUomGeneric != null) {
					if (quantityUomGeneric.getString("description") != null) {
						quantityUomDescription = (String) quantityUomGeneric.get("description", locale);
					} else {
						quantityUomDescription = quantityUomGeneric.getString("uomId");
					}
				}

				String weightUomDescription = "";
				if (orderItem.get("weightUomId") != null) {
					GenericValue weightUomGeneric = delegator.findOne("Uom",
							UtilMisc.toMap("uomId", orderItem.get("weightUomId")), false);
					if (weightUomGeneric != null) {
						weightUomDescription = weightUomGeneric.getString("abbreviation");
					}
				}

				BigDecimal packing = UomWorker.customConvertUom(productId, orderItem.getString("quantityUomId"),
						product.getString("quantityUomId"), BigDecimal.ONE, delegator);
				BigDecimal quantity = null;
				BigDecimal selectedAmount = orderItem.getBigDecimal("selectedAmount");
				BigDecimal sumTray = null;
				BigDecimal unitPrice = null;
				BigDecimal productQuantityPerTray = null;
				BigDecimal baseQuantity = orderItem.getBigDecimal("quantity");
				BigDecimal basePrice = orderItem.getBigDecimal("unitPrice");
				if (!isNormal) {
					quantity = orderItem.getBigDecimal("alternativeQuantity");
					unitPrice = orderItem.getBigDecimal("alternativeUnitPrice");
				}
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					quantity = selectedAmount;
					unitPrice = unitPrice.divide(selectedAmount, 3, RoundingMode.HALF_UP);
				}
				// Modify by VietTB
				BigDecimal adjustment;
				// DASubTotalBeforeVAT
				BigDecimal subTotalBeVAT;
				if (("Y").equals(orderItem.get("isPromo"))) {
					List<GenericValue> orderAdjustmentss = delegator.findList("OrderAdjustment",
							EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
					adjustment = OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustmentss, true, false,
							false);
					subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustmentss);
				} else {
					adjustment = BigDecimal.ZERO;
					subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments);
				}

				BigDecimal subAmountExportOrder = BigDecimal.ZERO;
				if (orderItem.get("statusId") != "ITEM_CANCELLED") {
					subAmountExportOrder = subAmountExportOrder.add(subTotalBeVAT);
				} else {
					subAmountExportOrder = subAmountExportOrder.add(subTotalBeVAT);
				}

				// Unit price after VAT
				BigDecimal unitPriceInvoiceAfVAT = null;
				BigDecimal subTotalInvoiceExport = null;
				List<GenericValue> listProductPriceInvoice = delegator.findByAnd("ProductPrice",
						UtilMisc.toMap("productId", productId, "productPriceTypeId", "INVOICE_PRICE_GT"), null, false);
				listProductPriceInvoice = EntityUtil.filterByDate(listProductPriceInvoice);
				GenericValue productPriceInvoiceGeneric = EntityUtil.getFirst(listProductPriceInvoice);
				if (productPriceInvoiceGeneric != null) {
					unitPriceInvoiceAfVAT = productPriceInvoiceGeneric.getBigDecimal("price");
					if (subTotalBeVAT.compareTo(BigDecimal.ZERO) != 0) {
						subTotalInvoiceExport = quantity.multiply(unitPriceInvoiceAfVAT);
					}
				}
				BigDecimal subAmountExportInvoice = BigDecimal.ZERO;
				if (subTotalInvoiceExport != null) {
					subAmountExportInvoice = subAmountExportInvoice.add(subTotalInvoiceExport);
				}

				String productPromoId = "";
				String productPromoRuleId = "";
				String productPromoActionSeqId = "";
				boolean isAdd = false;
				boolean isPromo = false;
				if ("Y".equals(orderItem.get("isPromo"))) {
					isPromo = true;
				} else {
					isAdd = true;
				}

				/* Caculate tax prices sum from order items */
				List<GenericValue> orderItemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem,
						orderAdjustments);
				if (orderItemAdjustments != null) {
					for (GenericValue itemAdjustment : orderItemAdjustments) {
						GenericValue adjustmentType = itemAdjustment.getRelatedOne("OrderAdjustmentType", true);
						if ("SALES_TAX".equals(adjustmentType.get("orderAdjustmentTypeId"))) {
							BigDecimal taxValue = OrderReadHelper.calcItemAdjustment(itemAdjustment, orderItem);
							taxTotalOrderItems = taxTotalOrderItems.add(taxValue);
							boolean isExists = false;
							for (Map<String, Object> taxTotalItem : listTaxTotal) {
								if (UtilValidate.isNotEmpty(taxTotalItem.get("sourcePercentage")) && taxTotalItem
										.get("sourcePercentage").equals(itemAdjustment.get("sourcePercentage"))) {
									// exists item
									BigDecimal amount = (BigDecimal) taxTotalItem.get("amount");
									amount = amount.add(itemAdjustment.getBigDecimal("amount"));
									taxTotalItem.put("amount", amount);
									if (subTotalInvoiceExport != null && subTotalBeVAT != null) {
										BigDecimal amountForInvoicePrice = amount.multiply(
												subTotalInvoiceExport.divide(subTotalBeVAT, 2, RoundingMode.HALF_UP));
										taxTotalItem.put("amountForIXP", amountForInvoicePrice);
									}
									isExists = true;
								}
							}
							if (!isExists) {
								// not exists item
								Map<String, Object> taxTotalItemNew = FastMap.newInstance();
								BigDecimal amount = itemAdjustment.getBigDecimal("amount");
								taxTotalItemNew.put("sourcePercentage",
										itemAdjustment.getBigDecimal("sourcePercentage"));
								taxTotalItemNew.put("amount", amount);
								if (subTotalInvoiceExport != null && subTotalBeVAT != null) {
									BigDecimal amountForInvoicePrice = amount.multiply(
											subTotalInvoiceExport.divide(subTotalBeVAT, 2, RoundingMode.HALF_UP));
									taxTotalItemNew.put("amountForIXP", amountForInvoicePrice);
								}
								// add description in first (only 1 times)
								String description = UtilProperties.getMessage("OrderUiLabels", "OrderAdjustment",
										locale);
								description += " " + adjustmentType.get("description", locale);
								if (itemAdjustment.get("description") != null)
									description += itemAdjustment.get("description", locale);
								if (itemAdjustment.get("comments") != null)
									description += " (" + itemAdjustment.get("comments") + "). ";
								if (itemAdjustment.get("productPromoId") != null) {
									description += "<a class='btn btn-mini btn-primary' href='/catalog/control/EditProductPromo?productPromoId="
											+ itemAdjustment.get("productPromoId") + "'>";
									description += itemAdjustment.getRelatedOne("ProductPromo", false)
											.getString("promoName") + "</a>";
								}
								String descriptionLog = description;
								if (itemAdjustment.get("primaryGeoId") != null) {
									GenericValue primaryGeo = itemAdjustment.getRelatedOne("PrimaryGeo", true);
									if (primaryGeo.get("geoName") != null) {
										String orderJurisdictionStr = UtilProperties.getMessage("OrderUiLabels",
												"OrderJurisdiction", locale);
										description += " " + orderJurisdictionStr + " " + primaryGeo.get("geoName")
												+ " [" + primaryGeo.get("abbreviation") + "]. ";
									}
									if (itemAdjustment.get("secondaryGeoId") != null) {
										GenericValue secondaryGeo = itemAdjustment.getRelatedOne("SecondaryGeo", true);
										String commonInStr = UtilProperties.getMessage("OrderUiLabels",
												"OrderJurisdiction", locale);
										description += " " + commonInStr + " " + secondaryGeo.get("geoName") + " ["
												+ secondaryGeo.get("abbreviation") + "]). ";
									}
								}
								if (itemAdjustment.get("sourcePercentage") != null) {
									String orderRateStr = UtilProperties.getMessage("OrderUiLabels", "OrderRate",
											locale);
									String template = "#,##0.###";
									String sourcePercentageStr = UtilFormatOut.formatDecimalNumber(
											itemAdjustment.getBigDecimal("sourcePercentage").doubleValue(), template,
											locale);
									description += " " + orderRateStr + " " + sourcePercentageStr + "%"; // ?string("0.######");
									descriptionLog += " " + orderRateStr + " " + sourcePercentageStr + "%"; // ?string("0.######");
								}
								if (itemAdjustment.get("customerReferenceId") != null) {
									String orderCustomerTaxIdStr = UtilProperties.getMessage("OrderUiLabels",
											"OrderCustomerTaxId", locale);
									description += " " + orderCustomerTaxIdStr + " "
											+ itemAdjustment.get("customerReferenceId");
								}
								if (itemAdjustment.get("exemptAmount") != null) {
									String orderExemptAmountStr = UtilProperties.getMessage("OrderUiLabels",
											"OrderExemptAmount", locale);
									description += " " + orderExemptAmountStr + " "
											+ itemAdjustment.get("exemptAmount");
								}
								taxTotalItemNew.put("description", description);
								taxTotalItemNew.put("descriptionLog", descriptionLog);
								listTaxTotal.add(taxTotalItemNew);
							}
						}

						// check is promo product
						if (isPromo) {
							if ("PROMOTION_ADJUSTMENT".equals(itemAdjustment.get("orderAdjustmentTypeId")) 
									&& itemAdjustment.get("productPromoId") != null
									&& itemAdjustment.get("productPromoRuleId") != null
									&& itemAdjustment.get("productPromoActionSeqId") != null) {
								boolean isSearched = false;
								// duyet danh sach order item da them vao gio
								for (Map<String, Object> oItem : listItemLine) {
									if (orderItem.get("productId") != null
											&& orderItem.get("productId") == oItem.get("productId")
											&& orderItem.get("quantityUomId") == oItem.get("quantityUomId")
											&& itemAdjustment.get("productPromoId") == oItem.get("productPromoId")
											&& itemAdjustment.get("productPromoRuleId") == oItem
													.get("productPromoRuleId")
											&& itemAdjustment.get("productPromoActionSeqId") == oItem
													.get("productPromoActionSeqId")) {
										isSearched = true;

										BigDecimal osumTray = BigDecimal.ZERO;
										BigDecimal oquantity = (BigDecimal) oItem.get("quantity");
										BigDecimal oadjustment = (BigDecimal) oItem.get("adjustment");
										BigDecimal osubTotalBeVAT = (BigDecimal) oItem.get("subTotalBeVAT");
										if (oquantity != null && productQuantityPerTray != null) {
											osumTray = oquantity.divide(productQuantityPerTray, 2,
													RoundingMode.HALF_UP);
										}
										if (oItem.get("isNormal") != null) {
											oquantity = orderItem.getBigDecimal("quantity");
											unitPrice = orderItem.getBigDecimal("unitPrice");
										} else {
											oquantity = orderItem.getBigDecimal("alternativeQuantity");
											unitPrice = orderItem.getBigDecimal("alternativeUnitPrice");
										}

										// cong them vao item
										oquantity = oquantity.add((BigDecimal) oItem.get("quantity"));
										oadjustment = oadjustment.add(adjustment);
										oadjustment = oadjustment.add((BigDecimal) oItem.get("adjustment"));
										osubTotalBeVAT = osubTotalBeVAT.add((BigDecimal) oItem.get("subTotalBeVAT"));
										oItem.put("quantity", oquantity);
										oItem.put("sumTray", osumTray);
										oItem.put("quantity", oquantity);
										oItem.put("adjustment", oadjustment);
										oItem.put("subTotalBeVAT", osubTotalBeVAT);
										oItem.put("productPromoId", oItem.get("productPromoId"));
										oItem.put("productPromoRuleId", oItem.get("productPromoRuleId"));
										oItem.put("productPromoActionSeqId", oItem.get("productPromoActionSeqId"));
									}
								}
								if (!isSearched) {
									isAdd = true;
									productPromoId = itemAdjustment.getString("productPromoId");
									productPromoRuleId = itemAdjustment.getString("productPromoRuleId");
									productPromoActionSeqId = itemAdjustment.getString("productPromoActionSeqId");
								}
							} else if ("PROMOTION_ADJUSTMENT".equals(itemAdjustment.get("orderAdjustmentTypeId")) 
									&& itemAdjustment.get("productPromoId") == null
									&& itemAdjustment.get("productPromoRuleId") == null
									&& itemAdjustment.get("productPromoActionSeqId") == null) {
								isAdd = true;
							}
						}
					}
				}

				if (isAdd) {
					GenericValue pr = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					BigDecimal convertQuantityByAmount = selectedAmount;
					String weightUomId = orderItem.getString("weightUomId");
					String baseWeightUomId = pr.getString("weightUomId");
					String baseQuantityUomId = pr.getString("quantityUomId");
					BigDecimal convertNumber = BigDecimal.ONE;
					if (weightUomId != null && !"".equals(weightUomId) && requireAmount != null
							&& requireAmount == "Y") {
						GenericValue conversion = null;
						conversion = delegator.findOne("UomConversion", false,
								UtilMisc.toMap("uomId", weightUomId, "uomIdTo", baseWeightUomId));
						if (conversion == null) {
							conversion = delegator.findOne("UomConversion", false,
									UtilMisc.toMap("uomId", baseWeightUomId, "uomIdTo", weightUomId));
							if (conversion != null) {
								convertQuantityByAmount = convertQuantityByAmount
										.divide(conversion.getBigDecimal("conversionFactor"), 3, RoundingMode.HALF_UP);
							}
						} else {
							convertQuantityByAmount = convertQuantityByAmount
									.multiply(conversion.getBigDecimal("conversionFactor"));
						}
						convertNumber = BigDecimal.ONE;
					} else {
						if (orderItem.getString("quantityUomId") != null && baseQuantityUomId != null) {
							convertNumber = ProductUtil.getConvertPackingNumber(delegator, pr.getString("productId"),
									orderItem.getString("quantityUomId"), baseQuantityUomId);
						}
					}

					Map<String, Object> itemLine = FastMap.newInstance();
					itemLine.put("seqId", seqId);
					itemLine.put("productId", productId);
					itemLine.put("convertNumber", convertNumber);
					itemLine.put("productCode", product.getString("productCode"));
					itemLine.put("productName", product.getString("productName"));
					itemLine.put("barcode", barcode);
					itemLine.put("expireDate", expireDate);
					itemLine.put("quantityUomId", orderItem.getString("quantityUomId"));
					itemLine.put("quantityUomDescription", quantityUomDescription);
					itemLine.put("packing", packing);
					itemLine.put("weightUomId", weightUomId);
					itemLine.put("weightUomDescription", weightUomDescription);
					itemLine.put("quantity", quantity);
					itemLine.put("convertQuantityByAmount", convertQuantityByAmount);
					itemLine.put("selectedAmount", selectedAmount);
					itemLine.put("sumTray", sumTray);
					itemLine.put("unitPriceBeVAT", unitPrice); // before VAT
					itemLine.put("baseUnitPriceBeVAT", basePrice); // before VAT
					itemLine.put("adjustment", adjustment);
					itemLine.put("subTotalBeVAT", subTotalBeVAT);
					itemLine.put("invoicePrice", unitPriceInvoiceAfVAT);
					itemLine.put("invoiceSubTotal", subTotalInvoiceExport);
					itemLine.put("itemDescription", itemDescription);
					itemLine.put("supplierProductId", supplierProductId);
					itemLine.put("product", product);
					itemLine.put("orderItemType", orderItemType);
					if (isPromo) {
						itemLine.put("isPromo", "Y");
					} else {
						itemLine.put("isPromo", "N");
					}
					itemLine.put("productPromoId", productPromoId);
					itemLine.put("productPromoRuleId", productPromoRuleId);
					itemLine.put("productPromoActionSeqId", productPromoActionSeqId);
					itemLine.put("isNormal", isNormal);
					itemLine.put("productQuantityPerTray", productQuantityPerTray);
					itemLine.put("comments", orderItem.get("comments"));
					itemLine.put("orderItem", orderItem);
					itemLine.put("orderItemTypeId", orderItem.get("orderItemTypeId"));
					itemLine.put("baseQuantity", baseQuantity);
					itemLine.put("basePrice", basePrice);
					itemLine.put("requireAmount", requireAmount);
					listItemLine.add(itemLine);
				}
			}
		}

		EntityCondition ordCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		EntityCondition isProCond = EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, "Y");
		EntityCondition topCond = EntityCondition.makeCondition(UtilMisc.toList(ordCond, isProCond),
				EntityOperator.AND);
		List<GenericValue> listProOrderItems = delegator.findList("OrderItem", topCond, null, null, null, false);
		List<GenericValue> orderHeaderAdjustments = orderReadHelper.getNewOrderHeaderAdjustments(listProOrderItems);

		item.put("listItemLine", listItemLine);
		item.put("listTaxTotal", listTaxTotal);
		item.put("orderHeaderAdjustments", orderHeaderAdjustments);
		item.put("orderAdjustments", orderAdjustments);
		item.put("grandTotal", grandTotal);
		return item;
	}

	private static Map<String, CellStyle> createStylesNormal(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style;
		Font font;

		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 9);
		font.setColor(IndexedColors.RED.getIndex());
		style = ExcelUtil.createNonBorderedStyle(wb);
		style.setFont(font);
		styles.put("cell_bordered_red_9", style);

		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 9);
		font.setColor(IndexedColors.DARK_BLUE.getIndex());
		style = ExcelUtil.createNonBorderedStyle(wb);
		style.setFont(font);
		styles.put("cell_bordered_dark_blue_9", style);

        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 10);
        style = ExcelUtil.createNonBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setFont(font);
        styles.put("cell_bold_right_no_border_10", style);

        font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        style = ExcelUtil.createNonBorderedStyle(wb);
        style.setWrapText(true);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        style.setFont(font);
        styles.put("cell_normal_vtop_wrap_text_10", style);

		return styles;
	}

	private static Sheet sheetSetting(Workbook wb, String sheetName) {
		Sheet sheet = wb.createSheet(sheetName);
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
		sheet.setColumnWidth(1, 12 * 256);
		sheet.setColumnWidth(2, 35 * 256);
		sheet.setColumnWidth(3, 18 * 256);
		sheet.setColumnWidth(4, 10 * 256);
		sheet.setColumnWidth(5, 18 * 256);
		sheet.setColumnWidth(6, 15 * 256);
		sheet.setColumnWidth(7, 15 * 256);
		sheet.setColumnWidth(8, 15 * 256);
		sheet.setColumnWidth(9, 15 * 256);
		sheet.setColumnWidth(13, 15 * 256);
		return sheet;
	}

	private static String getBarcodePrimary(GenericDelegator delegator, Object productId) {
		String barcode = "";
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("goodIdentificationTypeId", EntityJoinOperator.EQUALS, "SKU"));
			conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
			List<GenericValue> dummy = delegator.findList("GoodIdentificationBarcodePrimary",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> idValue = EntityUtil.getFieldListFromEntityList(dummy, "idValue", true);
			if (UtilValidate.isNotEmpty(idValue)) {
				barcode = idValue.toString().replaceAll(";", ", ").replace("[", "").replace("]", "");
			}
		} catch (Exception e) {
		}
		return barcode;
	}
}