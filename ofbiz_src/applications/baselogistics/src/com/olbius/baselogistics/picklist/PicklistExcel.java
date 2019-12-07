package com.olbius.baselogistics.picklist;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.baselogistics.picklist.picker.InventoryPicker3;
import com.olbius.basesales.util.ExcelUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PicklistExcel {

	public final static String RESOURCE = "BaseLogisticsUiLabels";
	public static String module = PicklistExcel.class.getName();
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	public static void export(HttpServletRequest request, HttpServletResponse response) throws IOException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		try {
			String picklistId = request.getParameter("picklistId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, picklistId));
			conditions.add(
					EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.NOT_EQUAL, "PICKITEM_CANCELLED"));
			List<GenericValue> picklistBins = delegator.findList("PicklistItemSum2",
					EntityCondition.makeCondition(conditions), null, null, null, false);

			List<String> orderIds = EntityUtil.getFieldListFromEntityList(picklistBins, "orderId", true);

			if (UtilValidate.isNotEmpty(orderIds)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds));
				List<GenericValue> shippingLocations = delegator.findList("OrderAndShippingLocation",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(shippingLocations, "contactMechId",
						true);
				InventoryPicker3 picker = new InventoryPicker3(delegator, userLogin, locale, orderIds, contactMechIds,
						picklistId);
				ExcelUtil.responseWrite(response,
						once(response, delegator, locale, userLogin, picker, orderIds, contactMechIds, picklistId),
						"phieu-soan-hang-" + picklistId + "-");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Workbook once(HttpServletResponse response, GenericDelegator delegator, Locale locale,
			GenericValue userLogin, InventoryPicker3 picker, List<String> orderIds, List<String> contactMechIds,
			String picklistId) throws Exception {
		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = ExcelUtil.createStylesNormal(wb);

		Sheet sheet = sheetSetting(wb, "Total");
		int rownum = ExcelUtil.insertLogo(wb, sheet);

		GenericValue picklist = delegator.findOne("Picklist", UtilMisc.toMap("picklistId", picklistId), false);

		Row row = sheet.createRow(rownum);
		Cell cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsMaNhanVienTaoCapNhat", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(1);
		cell.setCellValue(getPartyPickByRole(delegator, picklistId, "PICKING_CREATOR"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsSoDonHang", locale));
		cell.setCellValue(orderIds.toString().replaceAll(";", ", ").replace("[", "").replace("]", ""));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		row.setHeight((short) 300);
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsMaNhanVienSoan", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));

		Cell cellNhanVienSoan = row.createCell(1);
		cellNhanVienSoan.setCellStyle(styles.get("cell_normal_cell_subtitle"));

		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsNgayDonHang", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(getOrderDate(delegator, orderIds));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		row.setHeight((short) 400);
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsMaNhanVienKiem", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));

		Cell cellNhanVienKiem = row.createCell(1);
		cellNhanVienKiem.setCellStyle(styles.get("cell_normal_cell_subtitle"));

		cell = row.createCell(2);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 3));
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsPrepareProduct", locale).toUpperCase());
		cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsTrangThai", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(getOrderStatus(delegator, locale, orderIds));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		Cell cellSPS = row.createCell(4);
		cellSPS.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsSoPhieuSoan", locale));
		cellSPS.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cellSPS = row.createCell(5);
		cellSPS.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsNgaySoan", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(format.format(picklist.getTimestamp("picklistDate")));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		Map<String, String> customerx = getCustomer(delegator, orderIds);

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomer", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(customerx.get("customerName"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomerPhone", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(customerx.get("contactNumber"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomerAddress", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(customerx.get("shippingAddress"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		List<String> titles = UtilMisc.toList(UtilProperties.getMessage("BaseSalesUiLabels", "BSSTT", locale),
				UtilProperties.getMessage(RESOURCE, "BLDmsSKU", locale),
				UtilProperties.getMessage(RESOURCE, "BLDmsTenHang", locale),
				UtilProperties.getMessage("BaseLogisticsUiLabels", "BLCategoryProduct", locale),
				UtilProperties.getMessage("BasePOUiLabels", "BSUPCCode", locale),
				UtilProperties.getMessage(RESOURCE, "BLDmsViTriKe", locale));

		titles.addAll(UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BLDmsQuyCach", locale),
				UtilProperties.getMessage(RESOURCE, "BLDmsDonViTinh", locale),
				UtilProperties.getMessage(RESOURCE, "BLDmsTongLuongSoanThung", locale),
				UtilProperties.getMessage(RESOURCE, "BLDmsTongLuongSoanLe", locale),
				UtilProperties.getMessage(RESOURCE, "BLDmsTongLuongSoanCanChiaLe", locale)));

		row = sheet.createRow(rownum);
		row.setHeight((short) 900);
		for (String t : titles) {
			cell = row.createCell(titles.indexOf(t));
			cell.setCellValue(t);
			cell.setCellStyle(styles.get("cell_yellow_bold_centered_wrap_text_bordered_10"));
		}
		rownum += 1;

		Map<String, List<Map<String, Object>>> picklistBins = picker.getAllItems();
		Set<String> picklistBinIds = picklistBins.keySet();

		boolean next = false;
		for (String picklistBinId : picklistBinIds) {
			List<Map<String, Object>> items = picklistBins.get(picklistBinId);
			int count = 0;

			if (next) {
				sheet = sheetSetting(wb, "PS (" + picklistBinId + ")");
				rownum = onceSheet(delegator, wb, sheet, styles, picklistBinId, titles, locale, picklistId, orderIds,
						picklist);
				rownum += 1;
			} else {
				cellSPS.setCellValue(picklistBinId);
				cellNhanVienSoan.setCellValue(getPartyPickBinByRole(delegator, picklistBinId, "PICKING_PICKER"));
				cellNhanVienKiem.setCellValue(getPartyPickBinByRole(delegator, picklistBinId, "PICKING_CHECKER"));
				wb.setSheetName(wb.getSheetIndex(sheet), "PS (" + picklistBinId + ")");
			}
			next = true;
			for (Map<String, Object> x : items) {

				count++;
				row = sheet.createRow(rownum);
				int _count = 0;
				// STT
				cell = row.createCell(_count);
				cell.setCellValue(count);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// SKU
				cell = row.createCell(_count);
				cell.setCellValue(x.get("productCode").toString());
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Ten Hang
				cell = row.createCell(_count);
				cell.setCellValue(x.get("productName").toString());
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Nganh Hang
				cell = row.createCell(_count);
				cell.setCellValue(x.get("primaryProductCategoryId").toString());
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// UPC
				cell = row.createCell(_count);
				cell.setCellValue((String) x.get("barcode"));
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Vi tri ke
				cell = row.createCell(_count);
				cell.setCellValue((String) x.get("location"));
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Quy Cach
				cell = row.createCell(_count);
				cell.setCellValue(x.get("quantityConvert").toString());
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				_count++;
				// Don vi tinh
				cell = row.createCell(_count);
				cell.setCellValue(x.get("quantityUomId").toString());
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Tong luong soan (Thung)
				cell = row.createCell(_count);
				cell.setCellValue(x.get("divided").toString());
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				_count++;
				// Tong luong soan (le)
				cell = row.createCell(_count);
				cell.setCellValue(x.get("remainder").toString());
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				_count++;
				// Tong luong soan can chia (le)
				cell = row.createCell(_count);
				cell.setCellValue(x.get("quantity").toString());
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				_count++;
				rownum += 1;
			}
		}

		Map<String, Integer> sheetName = FastMap.newInstance();
		for (String s : contactMechIds) {
			String customer = s;
			List<GenericValue> dummy = delegator.findList("ProductStoreRoleAndPostalAddress3",
					EntityCondition.makeCondition("contactMechId", EntityJoinOperator.EQUALS, s), null, null, null,
					false);
			String customerName = "";
			String contactNumber = "";
			String shippingAddress = "";
			if (UtilValidate.isNotEmpty(dummy)) {
				GenericValue customerMtl = EntityUtil.getFirst(dummy);
				customer = customerMtl.getString("partyCode");
				customerName = customerMtl.get("partyCode") + " - " + customerMtl.get("partyName");
				contactNumber = customerMtl.getString("contactNumber");
				shippingAddress = customerMtl.getString("fullName");
			}
			if (sheetName.containsKey(s) && UtilValidate.isNotEmpty(sheetName.get(s))) {
				customer = customer + "(" + sheetName.get(s) + ")";
				sheetName.put(s, sheetName.get(s) + 1);
			} else {
				sheetName.put(s, 1);
			}
			createSheetItem(delegator, locale, wb, styles, titles, picker, s, customer, customerName, contactNumber,
					shippingAddress, picklistId);
		}
		return wb;
	}

	private static int onceSheet(GenericDelegator delegator, Workbook wb, Sheet sheet, Map<String, CellStyle> styles,
			String picklistBinId, List<String> titles, Locale locale, Object picklistId, List<String> orderIds,
			GenericValue picklist) throws Exception {
		int rownum = ExcelUtil.insertLogo(wb, sheet);

		Row row = sheet.createRow(rownum);
		Cell cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsMaNhanVienTaoCapNhat", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(1);
		cell.setCellValue(getPartyPickByRole(delegator, picklistId, "PICKING_CREATOR"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsSoDonHang", locale));
		cell.setCellValue(orderIds.toString().replaceAll(";", ", ").replace("[", "").replace("]", ""));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		row.setHeight((short) 300);
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsMaNhanVienSoan", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));

		cell = row.createCell(1);
		cell.setCellValue(getPartyPickBinByRole(delegator, picklistBinId, "PICKING_PICKER"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));

		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsNgayDonHang", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(getOrderDate(delegator, orderIds));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		row.setHeight((short) 400);
		cell = row.createCell(0);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsMaNhanVienKiem", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));

		cell = row.createCell(1);
		cell.setCellValue(getPartyPickBinByRole(delegator, picklistBinId, "PICKING_CHECKER"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));

		cell = row.createCell(2);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 3));
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsPrepareProduct", locale).toUpperCase());
		cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsTrangThai", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(getOrderStatus(delegator, locale, orderIds));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsSoPhieuSoan", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(picklistBinId);
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsNgaySoan", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(format.format(picklist.getTimestamp("picklistDate")));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		Map<String, String> customer = getCustomer(delegator, orderIds);

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomer", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(customer.get("customerName"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomerPhone", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(customer.get("contactNumber"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomerAddress", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(customer.get("shippingAddress"));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		row.setHeight((short) 900);
		for (String t : titles) {
			cell = row.createCell(titles.indexOf(t));
			cell.setCellValue(t);
			cell.setCellStyle(styles.get("cell_yellow_bold_centered_wrap_text_bordered_10"));
		}
		return rownum;
	}

	private static void createSheetItem(GenericDelegator delegator, Locale locale, Workbook wb,
			Map<String, CellStyle> styles, List<String> titles, InventoryPicker3 picker, String contactMechId,
			String customer, String customerName, String contactNumber, String shippingAddress, String picklistId)
			throws Exception {
		Sheet sheet = sheetSetting(wb, customer);
		int rownum = ExcelUtil.insertLogo(wb, sheet);

		List<String> orderIds = picker.getOrders(contactMechId);
		String picklistBinId = getPicklistBinId(delegator, picklistId, orderIds);

		Row row = sheet.createRow(rownum);
		Cell cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsSoPhieuSoan", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(picklistBinId);
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsSoDonHang", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(orderIds.toString().replaceAll(";", ", ").replace("[", "").replace("]", ""));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsNgayDonHang", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(getOrderDate(delegator, orderIds));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsTrangThai", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(getOrderStatus(delegator, locale, orderIds));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomer", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(customerName);
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomerPhone", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(contactNumber);
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		cell = row.createCell(4);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsCustomerAddress", locale));
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		cell = row.createCell(5);
		cell.setCellValue(shippingAddress);
		cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
		rownum += 1;

		row = sheet.createRow(rownum);
		row.setHeight((short) 400);
		cell = row.createCell(1);
		cell.setCellValue(UtilProperties.getMessage(RESOURCE, "BLDmsPrepareProduct", locale).toUpperCase());
		cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		row = sheet.createRow(rownum);
		row.setHeight((short) 900);
		for (String t : titles) {
			cell = row.createCell(titles.indexOf(t));
			cell.setCellValue(t);
			cell.setCellStyle(styles.get("cell_yellow_bold_centered_wrap_text_bordered_10"));
		}
		rownum += 1;

		List<Map<String, Object>> products = picker.getItem(contactMechId);
		if (UtilValidate.isNotEmpty(products)) {
			int count = 0;
			for (Map<String, Object> x : products) {
				count++;
				int _count = 0;
				row = sheet.createRow(rownum);
				// STT
				cell = row.createCell(_count);
				cell.setCellValue(count);
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// SKU
				cell = row.createCell(_count);
				cell.setCellValue(x.get("productCode").toString());
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Ten Hang
				cell = row.createCell(_count);
				cell.setCellValue(x.get("productName").toString());
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Nganh Hang
				cell = row.createCell(_count);
				cell.setCellValue(x.get("primaryProductCategoryId").toString());
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// UPC
				cell = row.createCell(_count);
				cell.setCellValue((String) x.get("barcode"));
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Vi tri ke
				cell = row.createCell(_count);
				cell.setCellValue((String) x.get("location"));
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Quy Cach
				cell = row.createCell(_count);
				cell.setCellValue(x.get("quantityConvert").toString());
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				_count++;
				// Don vi tinh
				cell = row.createCell(_count);
				cell.setCellValue(x.get("quantityUomId").toString());
				cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
				_count++;
				// Tong luong soan (Thung)
				cell = row.createCell(_count);
				cell.setCellValue(x.get("divided").toString());
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				_count++;
				// Tong luong soan (le)
				cell = row.createCell(_count);
				cell.setCellValue(x.get("remainder").toString());
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				_count++;
				// Tong luong soan can chia (le)
				cell = row.createCell(_count);
				cell.setCellValue(x.get("quantity").toString());
				cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
				_count++;
				rownum += 1;
			}
		}
	}

	private static String getPartyPickByRole(GenericDelegator delegator, Object picklistId, Object roleTypeId) {
		String partyCode = "";
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(
					EntityCondition.makeCondition(UtilMisc.toMap("picklistId", picklistId, "roleTypeId", roleTypeId)));
			List<GenericValue> picklistRoles = delegator.findList("PicklistRole",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : picklistRoles) {
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", x.get("partyId")), false);
				partyCode += party.get("partyCode") + ", ";
			}
			if (UtilValidate.isNotEmpty(partyCode)) {
				partyCode = partyCode.substring(0, partyCode.length() - 2);
			}
		} catch (Exception e) {
		}
		return partyCode;
	}

	private static String getPartyPickBinByRole(GenericDelegator delegator, Object picklistBinId, Object roleTypeId) {
		String partyCode = "";
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition
					.makeCondition(UtilMisc.toMap("picklistBinId", picklistBinId, "roleTypeId", roleTypeId)));
			List<GenericValue> picklistRoles = delegator.findList("PicklistBinRole",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : picklistRoles) {
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", x.get("partyId")), false);
				partyCode += party.get("partyCode") + ", ";
			}
			if (UtilValidate.isNotEmpty(partyCode)) {
				partyCode = partyCode.substring(0, partyCode.length() - 2);
			}
		} catch (Exception e) {
		}
		return partyCode;
	}

	private static String getOrderDate(GenericDelegator delegator, List<String> orderIds) {
		String orderDate = "";
		try {
			List<GenericValue> orderHeaders = delegator.findList("OrderHeader",
					EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds), null, null, null, false);
			Set<String> orderDates = new HashSet<>();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			for (GenericValue x : orderHeaders) {
				orderDates.add(format.format(x.getTimestamp("orderDate")));
			}
			for (String s : orderDates) {
				orderDate += s + ", ";
			}
			if (UtilValidate.isNotEmpty(orderDate)) {
				orderDate = orderDate.substring(0, orderDate.length() - 2);
			}
		} catch (Exception e) {
		}
		return orderDate;
	}

	private static String getOrderStatus(GenericDelegator delegator, Locale locale, List<String> orderIds) {
		String orderStatus = "";
		try {
			List<GenericValue> orderHeaders = delegator.findList("OrderHeader",
					EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds), null, null, null, false);
			Set<String> statusIds = new HashSet<>();
			for (GenericValue x : orderHeaders) {
				statusIds.add(x.getString("statusId"));
			}
			for (String s : statusIds) {
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", s), false);
				if (UtilValidate.isNotEmpty(statusItem)) {
					orderStatus += statusItem.get("description", locale) + ", ";
				}
			}
			if (UtilValidate.isNotEmpty(orderStatus)) {
				orderStatus = orderStatus.substring(0, orderStatus.length() - 2);
			}
		} catch (Exception e) {
		}
		return orderStatus;
	}

	private static String getPicklistBinId(GenericDelegator delegator, Object picklistId, List<String> primaryOrderId) {
		String picklistBinId = "";
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, picklistId));
			if (UtilValidate.isNotEmpty(primaryOrderId)) {
				conditions.add(EntityCondition.makeCondition("primaryOrderId", EntityJoinOperator.IN, primaryOrderId));
			}
			List<GenericValue> picklistBins = delegator.findList("PicklistBin",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> picklistBinIds = EntityUtil.getFieldListFromEntityList(picklistBins, "picklistBinId", true);
			if (UtilValidate.isNotEmpty(picklistBinIds)) {
				picklistBinId = picklistBinIds.toString().replaceAll(";", ", ").replace("[", "").replace("]", "");
			}
		} catch (Exception e) {
		}
		return picklistBinId;
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

		sheet.setColumnWidth(0, 20 * 256);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 40 * 256);
		sheet.setColumnWidth(3, 25 * 256);
		for (int i = 4; i < 100; i++) {
			sheet.setColumnWidth(i, 20 * 256);
		}
		return sheet;
	}

	private static Map<String, String> getCustomer(GenericDelegator delegator, List<String> orderIds) {
		Map<String, String> customer = FastMap.newInstance();
		try {
			if (UtilValidate.isNotEmpty(orderIds)) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds));
				List<GenericValue> shippingLocations = delegator.findList("OrderAndShippingLocation",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(shippingLocations, "contactMechId",
						true);
				List<GenericValue> dummy = delegator.findList("ProductStoreRoleAndPostalAddress3",
						EntityCondition.makeCondition("contactMechId", EntityJoinOperator.IN, contactMechIds), null,
						null, null, false);
				if (UtilValidate.isNotEmpty(dummy)) {
					GenericValue customerMtl = EntityUtil.getFirst(dummy);
					customer.put("customerName",
							customerMtl.get("partyCode") + " - " + customerMtl.get("partyName"));
					customer.put("contactNumber", customerMtl.getString("contactNumber"));
					customer.put("shippingAddress", customerMtl.getString("fullName"));
				}
			}
		} catch (Exception e) {
		}
		return customer;
	}
}
