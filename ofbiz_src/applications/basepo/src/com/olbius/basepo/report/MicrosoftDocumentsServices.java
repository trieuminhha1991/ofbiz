package com.olbius.basepo.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.administration.BrandLogo;
import com.olbius.basehr.util.MultiOrganizationUtil;

public class MicrosoftDocumentsServices {
	public static String module = MicrosoftDocumentsServices.class.getName();
	public static final String resource = "BasePOUiLabels";
	public static final String euroSign = new String("\u20AC");
	public static final String leftDoubleQuotationMark = new String("\u201C");
	public static final String rightDoubleQuotationMark = new String("\u201D");
	public static final String draftingPointRightWardArrow = new String("\u279B");
	public static final String ballotBox = new String("\u2610");
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";

	public static void exportPurchaseOrderToExcel(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String orderId = request.getParameter("orderId");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		Timestamp orderDate = orderHeader.getTimestamp("orderDate");
		HttpSession session = request.getSession();
		GenericValue userLoginValue = (GenericValue) session.getAttribute("userLogin");
		String userLoginId = userLoginValue.getString("userLoginId");
		/*
		 * GenericValue userLogin = delegator.findOne("UserLogin",
		 * UtilMisc.toMap("userLoginId", userLoginId), false);
		 */
		/* String partyId = userLogin.getString("partyId"); */
		String orgId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", orgId), false);
		String groupName = "";
		if (partyGroup != null) {
			groupName = partyGroup.getString("groupName");
		}
		/*
		 * GenericValue person = delegator.findOne("Person",
		 * UtilMisc.toMap("partyId", partyId), false); String firstName =
		 * person.getString("firstName"); String middleName =
		 * person.getString("middleName"); String lastName =
		 * person.getString("lastName");
		 */

		/*
		 * String originFacilityId = orderHeader.getString("originFacilityId");
		 */
		/*
		 * Date currentDate = new Date(); long currentDateLong =
		 * currentDate.getTime(); Timestamp currentDateTime = new
		 * Timestamp(currentDateLong);
		 */
		/*
		 * List<GenericValue> listFacilityParty =
		 * delegator.findList("FacilityParty",
		 * EntityCondition.makeCondition(UtilMisc.toMap("facilityId",
		 * originFacilityId, "roleTypeId",
		 * UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES,
		 * "roleType.manager"))), null, null, null, false); String
		 * partyIdFacility = ""; for (GenericValue facilityParty :
		 * listFacilityParty) { Timestamp thruDate =
		 * facilityParty.getTimestamp("thruDate"); if(thruDate != null){
		 * if(thruDate.compareTo(currentDateTime) >= 0){ partyIdFacility =
		 * facilityParty.getString("partyId"); } }else{ partyIdFacility =
		 * facilityParty.getString("partyId"); } }
		 */

		/*
		 * List<GenericValue> listFacilityContactMech =
		 * delegator.findList("FacilityContactMech",
		 * EntityCondition.makeCondition(UtilMisc.toMap("facilityId",
		 * originFacilityId)), null, null, null, false); String
		 * contactMechIdFacility = ""; for (GenericValue facilityContactMech :
		 * listFacilityContactMech) { Timestamp thruDateContactMech =
		 * facilityContactMech.getTimestamp("thruDate"); if(thruDateContactMech
		 * != null){ if(thruDateContactMech.compareTo(currentDateTime) >= 0){
		 * contactMechIdFacility =
		 * facilityContactMech.getString("contactMechId"); } }else{
		 * contactMechIdFacility =
		 * facilityContactMech.getString("contactMechId"); } }
		 */
		String contactNumber = "";
		String lastNameStorekeeper = "";

		List<GenericValue> listOrderContactMech = delegator.findList("OrderContactMech",
				EntityCondition.makeCondition(
						UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")),
				null, null, null, false);
		GenericValue postalAddress = null;
		if (!listOrderContactMech.isEmpty()) {
			for (GenericValue orderContactMech : listOrderContactMech) {
				String contactMechId = orderContactMech.getString("contactMechId");
				postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId),
						false);
				if (postalAddress != null) {
					if (postalAddress.getString("toName") != null) {
						lastNameStorekeeper = postalAddress.getString("toName");
					}
					if (postalAddress.getString("attnName") != null) {
						contactNumber = postalAddress.getString("attnName");
					}
				}
			}
		}

		List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup",
				EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		Timestamp shipByDate = null;
		Timestamp shipAfterDate = null;
		if (!listOrderItemShipGroup.isEmpty()) {
			for (GenericValue orderItemShipGroup : listOrderItemShipGroup) {
				shipByDate = orderItemShipGroup.getTimestamp("shipByDate");
				shipAfterDate = orderItemShipGroup.getTimestamp("shipAfterDate");
			}
		}
		String address = "";
		if (postalAddress != null) {
			address = postalAddress.getString("address1");

			GenericValue wardGeo = delegator.findOne("Geo",
					UtilMisc.toMap("geoId", postalAddress.getString("wardGeoId")), false);
			if (wardGeo != null) {
				address += "," + wardGeo.getString("geoName");
			}

			GenericValue districtGeo = delegator.findOne("Geo",
					UtilMisc.toMap("geoId", postalAddress.getString("districtGeoId")), false);
			if (districtGeo != null) {
				address += "," + districtGeo.getString("geoName");
			}

			if (postalAddress.getString("city") != null) {
				address += "," + postalAddress.getString("city");
			}
		}
		// start renderExcel
		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = createStyles(wb);
		Sheet sheet = wb.createSheet("Sheet1");

		CellStyle csWrapText = wb.createCellStyle();
		csWrapText.setWrapText(true);

		// turn on gridlines
		sheet.setDisplayGridlines(true);
		sheet.setPrintGridlines(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);

		sheet.setAutobreaks(true);
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1);

		sheet.setColumnWidth(0, 15 * 350);
		sheet.setColumnWidth(1, 15 * 350);
		sheet.setColumnWidth(2, 15 * 500);
		sheet.setColumnWidth(3, 15 * 256);
		sheet.setColumnWidth(4, 15 * 250);
		sheet.setColumnWidth(5, 15 * 250);
		sheet.setColumnWidth(6, 15 * 500);
		sheet.setColumnWidth(7, 15 * 300);
		sheet.setColumnWidth(8, 15 * 300);

		/*
		 * Row imgHead = sheet.createRow(0); Cell imgCell =
		 * imgHead.createCell(0);
		 */
		int rownum = 0;
		FileInputStream is = null;
		try {
			// String imageServerPath =
			// FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms",
			// "image.management.logoPath"), null);
			// if(UtilValidate.isNotEmpty(imageServerPath) ||
			// !imageServerPath.equals("") || imageServerPath != null){
			// File file = new File(imageServerPath);
			// is = new FileInputStream(file);
			// }
			// byte[] bytesImg = IOUtils.toByteArray(is);
			// int pictureIdx = wb.addPicture(bytesImg,
			// Workbook.PICTURE_TYPE_PNG);
			// CreationHelper helper = wb.getCreationHelper();
			// Drawing drawing = sheet.createDrawingPatriarch();
			// ClientAnchor anchor = helper.createClientAnchor();
			// anchor.setCol1(0);
			// anchor.setCol2(1);
			// anchor.setRow1(0);
			// anchor.setRow2(5);
			// Picture pict = drawing.createPicture(anchor, pictureIdx);
			// pict.getPictureData();
			// rownum = 5;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				is.close();
		}
		Row khoangCachRow = sheet.createRow(rownum);
		khoangCachRow.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
		Cell khoangCachCell = khoangCachRow.createCell(0);
		khoangCachCell.setCellValue(" ");
		khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		Row titleRow = sheet.createRow(rownum);
		titleRow.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(UtilProperties.getMessage(resource, "DmsOrderPO", locale).toUpperCase());
		titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_Header"));
		rownum += 1;

		Row khoangCachRow2 = sheet.createRow(rownum);
		khoangCachRow2.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
		Cell khoangCachCell2 = khoangCachRow2.createCell(0);
		khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		Row orderInforRow = sheet.createRow(rownum);
		orderInforRow.setHeight((short) 300);
		Cell orderIdCell = orderInforRow.createCell(0);
		orderIdCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		Cell orderNameCell = orderInforRow.createCell(5);
		orderNameCell.setCellValue(UtilProperties.getMessage(resource, "DAOrderId", locale) + ":" + orderId);
		orderNameCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
		rownum += 1;

		Row khoangCachRow3 = sheet.createRow(rownum);
		khoangCachRow2.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
		Cell khoangCachCell3 = khoangCachRow3.createCell(0);
		khoangCachCell3.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		Row orderInforRow2 = sheet.createRow(rownum);
		orderInforRow2.setHeight((short) 300);
		Cell orderDateCell = orderInforRow2.createCell(5);
		orderDateCell.setCellValue(UtilProperties.getMessage(resource, "POCreateOrderDate", locale) + ":"
				+ new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(orderDate));
		orderDateCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
		rownum += 1;

		Row khoangCachRow5 = sheet.createRow(rownum);
		khoangCachRow5.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
		Cell khoangCachCell5 = khoangCachRow5.createCell(0);
		khoangCachCell5.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		List<GenericValue> listOrderItem = delegator.findList("OrderItem",
				EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		String organi = UtilProperties.getMessage(resource, "POOrganization", locale);
		String titleProductName = UtilProperties.getMessage(resource, "POProductName", locale);
		String titleQuantity = UtilProperties.getMessage(resource, "DAQuantity", locale);
		String UnitsProduct = UtilProperties.getMessage(resource, "UnitsProduct", locale);
		String BPOPrice = UtilProperties.getMessage(resource, "BPOPrice", locale);
		String shippingAddress = UtilProperties.getMessage(resource, "POShippingAddress", locale);
		String PODeliveryTime = UtilProperties.getMessage(resource, "PODeliveryTime", locale);
		String POProductId = UtilProperties.getMessage(resource, "POProductId", locale);
		String POCommunicationPartyId = UtilProperties.getMessage(resource, "POCommunicationPartyId", locale);
		List<String> titles = new FastList<String>();
		titles.add(organi);
		titles.add(POProductId);
		titles.add(titleProductName);
		titles.add(titleQuantity);
		titles.add(UnitsProduct);
		titles.add(BPOPrice);
		titles.add(shippingAddress);
		titles.add(PODeliveryTime);
		titles.add(POCommunicationPartyId);
		Row headerBreakdownAmountRow = sheet.createRow(rownum);
		headerBreakdownAmountRow.setHeight((short) 500);
		for (int i = 0; i < titles.size(); i++) {
			Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
			headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_10"));
			headerBreakdownAmountCell.setCellValue(titles.get(i));
		}
		rownum += 1;
		int index = 0;
		int a = listOrderItem.size();
		for (GenericValue x : listOrderItem) {
			Row productDetailRow = sheet.createRow(rownum);
			productDetailRow.setHeight((short) 700);

			Cell organzationCell = productDetailRow.createCell(0);
            organzationCell.setCellStyle(styles.get("cell_normal_centered_border_full_11"));
			if (index == 0) {
				int rowStart = productDetailRow.getRowNum();
				int rowEnd = a + rowStart - 1;
				sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, 0, 0));
				organzationCell.setCellValue(groupName);
			}
			String productId = x.getString("productId");
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			Cell productIdCell = productDetailRow.createCell(1);
			productIdCell.setCellValue(product.getString("productCode"));
			productIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			String internalName = product.getString("internalName");
			Cell productDescriptionCell = productDetailRow.createCell(2);
			productDescriptionCell.setCellValue(internalName);
			productDescriptionCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			Cell productQuantityCell = productDetailRow.createCell(3);
			BigDecimal quantity = (BigDecimal) x.get("quantity");
			productQuantityCell.setCellValue(new DecimalFormat("#,###.#").format(quantity));
			productQuantityCell.setCellStyle(styles.get("cell_normal_right_boder_full_10"));

			Cell Unit = productDetailRow.createCell(4);
			String quantityUomId = x.getString("quantityUomId");
			GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), true);
			if (UtilValidate.isNotEmpty(uom)) {
				Unit.setCellValue((String) uom.get("description", locale));
			}
			Unit.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			Cell unitPriceCell = productDetailRow.createCell(5);
			BigDecimal unitPrice = (BigDecimal) x.get("unitPrice");
			unitPriceCell.setCellValue(new DecimalFormat("#,###.00").format(unitPrice));
			unitPriceCell.setCellStyle(styles.get("cell_normal_right_boder_full_10"));

			Cell diaDiemGiaoHang = productDetailRow.createCell(6);
            diaDiemGiaoHang.setCellStyle(styles.get("cell_normal_centered_border_full_11"));
			if (index == 0) {
				int rowStart = productDetailRow.getRowNum();
				int rowEnd = a + rowStart - 1;
				sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, 6, 6));
				diaDiemGiaoHang.setCellValue(address);
			}

			Cell shipByDateCell = productDetailRow.createCell(7);
            shipByDateCell.setCellStyle(styles.get("cell_normal_centered_border_full_11"));
			if (index == 0) {
				int rowStart = productDetailRow.getRowNum();
				int rowEnd = a + rowStart - 1;
				sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, 7, 7));
				if (shipByDate != null && shipAfterDate != null) {
					String shipByDateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(shipByDate);
					String shipAfterDateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(shipAfterDate);
					shipByDateCell.setCellValue(shipAfterDateStr + "-" + shipByDateStr);
				}
			}

			Cell tongTienCell = productDetailRow.createCell(8);
            tongTienCell.setCellStyle(styles.get("cell_normal_centered_border_full_11"));
			if (index == 0) {
				int rowStart = productDetailRow.getRowNum();
				int rowEnd = a + rowStart - 1;
				sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, 8, 8));
				if (contactNumber.equals("")) {
					tongTienCell.setCellValue(lastNameStorekeeper);
				} else {
					tongTienCell.setCellValue(lastNameStorekeeper + "(" + contactNumber + ")");
				}
			}
			rownum += 1;
			index += 1;
		}

		Row khoangCachRow6 = sheet.createRow(rownum);
		khoangCachRow6.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
		Cell khoangCachCell6 = khoangCachRow6.createCell(0);
		khoangCachCell6.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		Map<String, Object> mapOrderRole = getPartyRoleOrder(delegator, orderId);
		String namePartySupplier = (String) mapOrderRole.get("partySupplier");

		Row orderFooterRow = sheet.createRow(rownum);
		orderFooterRow.setHeight((short) 600);
		Cell orderFooterCell1 = orderFooterRow.createCell(1);
		String receiver = UtilProperties.getMessage(resource, "POReceiver", locale);
		orderFooterCell1.setCellValue("" + namePartySupplier + "\n" + receiver);
		orderFooterCell1.setCellStyle(styles.get("cell_normal_centered_11"));
		Cell orderFooterCell2 = orderFooterRow.createCell(4);
		String createBy = UtilProperties.getMessage(resource, "POCreatedBy", locale);
		orderFooterCell2.setCellValue("Nhà thuốc Phương Chính \n" + createBy);
		orderFooterCell2.setCellStyle(styles.get("cell_normal_centered_11"));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 6));
		rownum += 1;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			byte[] bytes = baos.toByteArray();

			String purchaseStr = UtilProperties.getMessage(resource, "PurchaseExportExcel", locale);
			response.setHeader("content-disposition", "attachment;filename=" + purchaseStr + orderId + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if (baos != null)
				baos.close();
		}
	}

	private static Map<String, Object> getPartyRoleOrder(Delegator delegator, String orderId) {
		Map<String, Object> returnRe = FastMap.newInstance();
		List<GenericValue> orderRole = FastList.newInstance();
		try {
			orderRole = delegator.findList("OrderRole",
					EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")),
					null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), "module");
		}
		String partySupplierName = null;
		if (UtilValidate.isNotEmpty(orderRole)) {
			String partyId = (String) orderRole.get(0).get("partyId");
			try {
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				if (partyGroup != null) {
					partySupplierName = partyGroup.getString("groupName");
				}

			} catch (GenericEntityException e) {
				Debug.logError(e.getMessage(), "module");
			}
		}
		returnRe.put("partySupplier", partySupplierName);

		return returnRe;
	}

	private static CellStyle createNonBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		return style;
	}

	private static CellStyle createBorderedThinStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}

	private static CellStyle createBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}

	private static CellStyle createBorderedBlueStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLUE.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLUE.getIndex());
		return style;
	}

	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		DataFormat df = wb.createDataFormat();

		CellStyle style;

		Font boldCenterNoBorderFont16 = wb.createFont();
		boldCenterNoBorderFont16.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont16.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont16);
		styles.put("cell_bold_centered_no_border_16", style);

		Font boldCenterNoBorderFontHeader = wb.createFont();
		boldCenterNoBorderFontHeader.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFontHeader.setFontHeightInPoints((short) 16);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFontHeader);
		styles.put("cell_bold_centered_no_border_Header", style);

		Font boldCenterNoBorderFont11 = wb.createFont();
		boldCenterNoBorderFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont11);
		styles.put("cell_bold_centered_no_border_11", style);

		Font boldDouCenterNoBorderFont11 = wb.createFont();
		boldDouCenterNoBorderFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldDouCenterNoBorderFont11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(boldDouCenterNoBorderFont11);
		styles.put("cell_bold_dou_center_no_border_11", style);

		Font boldUnderlineItalicRightFont11 = wb.createFont();
		boldUnderlineItalicRightFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldUnderlineItalicRightFont11.setFontHeightInPoints((short) 11);
		boldUnderlineItalicRightFont11.setUnderline((byte) 1);
		boldUnderlineItalicRightFont11.setItalic(true);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(boldUnderlineItalicRightFont11);
		styles.put("cell_bold_right_underline_italic_11", style);

		Font boldLeftFont11 = wb.createFont();
		boldLeftFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldLeftFont11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(boldLeftFont11);
		styles.put("cell_bold_left_11", style);

		Font boldUnderlineItalicRightFont10 = wb.createFont();
		boldUnderlineItalicRightFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldUnderlineItalicRightFont10.setFontHeightInPoints((short) 10);
		boldUnderlineItalicRightFont10.setUnderline((byte) 1);
		boldUnderlineItalicRightFont10.setItalic(true);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(boldUnderlineItalicRightFont10);
		styles.put("cell_bold_right_underline_italic_10", style);

		Font boldLeftFont10 = wb.createFont();
		boldLeftFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldLeftFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(boldLeftFont10);
		styles.put("cell_bold_left_10", style);

		Font normalCenterNoBorderFont11 = wb.createFont();
		normalCenterNoBorderFont11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterNoBorderFont11);
		styles.put("cell_centered_no_border_11", style);

		Font normalRightNoBorderFont11 = wb.createFont();
		normalRightNoBorderFont11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(normalRightNoBorderFont11);
		styles.put("cell_right_no_border_11", style);

		Font italicRightNoBorderFont11 = wb.createFont();
		italicRightNoBorderFont11.setFontHeightInPoints((short) 11);
		italicRightNoBorderFont11.setItalic(true);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(italicRightNoBorderFont11);
		styles.put("cell_italic_right_no_border_11", style);

		Font normalCenterWrapTextFont10 = wb.createFont();
		normalCenterWrapTextFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterWrapTextFont10);
		style.setWrapText(true);
		styles.put("cell_normal_centered_wrap_text_10", style);

		Font normalCenterBorderTopFont10 = wb.createFont();
		normalCenterBorderTopFont10.setFontHeightInPoints((short) 10);
		normalCenterBorderTopFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderTopFont10);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_top_10", style);

		Font normalCenterBorderTopLeftFont10 = wb.createFont();
		normalCenterBorderTopLeftFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderTopLeftFont10);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		style.setBorderLeft(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_top_left_10", style);

		Font normalCenterBorderTopRightFont10 = wb.createFont();
		normalCenterBorderTopRightFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderTopRightFont10);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		style.setBorderRight(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_top_right_10", style);

		Font normalCenterBorderRightFont10 = wb.createFont();
		normalCenterBorderRightFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderRightFont10);
		style.setWrapText(true);
		style.setBorderRight(CellStyle.BORDER_DOUBLE);
		style.setBorderLeft(CellStyle.BORDER_NONE);
		styles.put("cell_normal_centered_wrap_text_border_right_10", style);

		Font normalCenterBorderBottomFont10 = wb.createFont();
		normalCenterBorderBottomFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalCenterBorderBottomFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderBottomFont10);
		style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_DOUBLE);
		styles.put("cell_bold_centered_wrap_text_border_bottom_10", style);

		Font normalRightBorderBottomFont10 = wb.createFont();
		normalRightBorderBottomFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalRightBorderBottomFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(normalRightBorderBottomFont10);
		style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_DOUBLE);
		styles.put("cell_bold_right_wrap_text_border_bottom_10", style);

		Font normalCenterBorderBottomNoRightFont10 = wb.createFont();
		normalCenterBorderBottomNoRightFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalCenterBorderBottomNoRightFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(normalCenterBorderBottomNoRightFont10);
		style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_DOUBLE);
		style.setBorderRight(CellStyle.BORDER_NONE);
		styles.put("cell_bold_right_wrap_text_border_bottom_no_right_10", style);

		Font normalCenterBorderBottomLeftFont10 = wb.createFont();
		normalCenterBorderBottomLeftFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderBottomLeftFont10);
		style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_DOUBLE);
		style.setBorderLeft(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_bottom_left_10", style);

		Font normalCenterBorderBottomRightFont10 = wb.createFont();
		normalCenterBorderBottomRightFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderBottomRightFont10);
		style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_DOUBLE);
		style.setBorderRight(CellStyle.BORDER_DOUBLE);
		style.setBorderLeft(CellStyle.BORDER_NONE);
		styles.put("cell_normal_centered_wrap_text_border_bottom_right_10", style);

		style = createBorderedStyle(wb);
		style.setBorderTop(CellStyle.BORDER_HAIR);
		styles.put("row_border_top", style);

		Font normalCenterFont10 = wb.createFont();
		normalCenterFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterFont10);
		styles.put("cell_normal_centered_10", style);

		Font normalCenterFontTop10 = wb.createFont();
		normalCenterFontTop10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(normalCenterFontTop10);
		styles.put("cell_normal_centered_top_10", style);

		Font normalLeftFontTop10 = wb.createFont();
		normalLeftFontTop10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(normalLeftFontTop10);
		styles.put("cell_normal_left_top_10", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setWrapText(true);
		styles.put("cell_border_centered_top", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setWrapText(true);
		styles.put("cell_border_right_top", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setWrapText(true);
		styles.put("cell_border_left_top", style);

		Font normalCenterBorderFullFont10 = wb.createFont();
		normalCenterBorderFullFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderFullFont10);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		styles.put("cell_normal_centered_border_full_10", style);

        Font normalCenterBorderFullFont11 = wb.createFont();
        normalCenterBorderFullFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
        normalCenterBorderFullFont11.setFontHeightInPoints((short) 11);
        style = createNonBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFont(normalCenterBorderFullFont11);
        style.setWrapText(true);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        styles.put("cell_normal_centered_border_full_11", style);

		Font normalRightFont10 = wb.createFont();
		normalRightFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(normalRightFont10);
		styles.put("cell_normal_right_10", style);

		Font normalLeftFont10 = wb.createFont();
		normalLeftFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftFont10);
		styles.put("cell_normal_Left_10", style);

		Font normalLeftWrapFont10 = wb.createFont();
		normalLeftWrapFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftWrapFont10);
		style.setWrapText(true);
		styles.put("cell_normal_Left_wrap_10", style);

		Font normalLeftWrapTopFont10 = wb.createFont();
		normalLeftWrapTopFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(normalLeftWrapTopFont10);
		style.setWrapText(true);
		styles.put("cell_normal_Left_wrap__top10", style);

		Font normalRightTopFont10 = wb.createFont();
		normalRightTopFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(normalRightTopFont10);
		styles.put("cell_normal_right_top_10", style);

		Font normalRightBorderNoRightFont10 = wb.createFont();
		normalRightBorderNoRightFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(normalRightBorderNoRightFont10);
		style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_NONE);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		styles.put("cell_normal_right_boder_no_right_10", style);

		Font normalRightBorderFullFont10 = wb.createFont();
		normalRightBorderFullFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(normalRightBorderFullFont10);
		style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		styles.put("cell_normal_right_boder_full_10", style);

		Font italicBoldLeftNoborderFont11 = wb.createFont();
		italicBoldLeftNoborderFont11.setFontHeightInPoints((short) 11);
		italicBoldLeftNoborderFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		italicBoldLeftNoborderFont11.setItalic(true);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(italicBoldLeftNoborderFont11);
		styles.put("cell_italic_bold_left_no_border_11", style);

		Font italicBoldLeftNoborderFont10 = wb.createFont();
		italicBoldLeftNoborderFont10.setFontHeightInPoints((short) 10);
		italicBoldLeftNoborderFont10.setItalic(true);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(italicBoldLeftNoborderFont10);
		styles.put("cell_italic_normal_left_no_border_10", style);

		Font boldLeftNoborderFont11 = wb.createFont();
		boldLeftNoborderFont11.setFontHeightInPoints((short) 11);
		boldLeftNoborderFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(boldLeftNoborderFont11);
		styles.put("cell_bold_left_no_border_11", style);

		Font boldLeftNoborderFont10 = wb.createFont();
		boldLeftNoborderFont10.setFontHeightInPoints((short) 10);
		boldLeftNoborderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldLeftNoborderFont10);
		styles.put("cell_bold_left_no_border_10", style);

		Font boldRightNoborderFont10 = wb.createFont();
		boldRightNoborderFont10.setFontHeightInPoints((short) 10);
		boldRightNoborderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(boldRightNoborderFont10);
		styles.put("cell_bold_right_no_border_10", style);

		Font boldCenterNoborderFont10 = wb.createFont();
		boldCenterNoborderFont10.setFontHeightInPoints((short) 10);
		boldCenterNoborderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoborderFont10);
		styles.put("cell_bold_center_no_border_10", style);

		Font normalCenterNoborderFont10 = wb.createFont();
		normalCenterNoborderFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(normalCenterNoborderFont10);
		styles.put("cell_normal_center_no_border_10", style);

		Font boldDouCenterNoborderFont10 = wb.createFont();
		boldDouCenterNoborderFont10.setFontHeightInPoints((short) 10);
		boldDouCenterNoborderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(boldDouCenterNoborderFont10);
		styles.put("cell_bold_dou_center_no_border_10", style);

		Font normalLeftNoBorderFont11 = wb.createFont();
		normalLeftNoBorderFont11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftNoBorderFont11);
		styles.put("cell_normal_left_no_border_11", style);

		Font normalLeftNoBorderWraptextFont11 = wb.createFont();
		normalLeftNoBorderWraptextFont11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftNoBorderWraptextFont11);
		style.setWrapText(true);
		styles.put("cell_normal_left_no_border_wrap_text_11", style);

		Font normalLeftNoBorderWraptextFont10 = wb.createFont();
		normalLeftNoBorderWraptextFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftNoBorderWraptextFont10);
		style.setWrapText(true);
		styles.put("cell_normal_left_no_border_wrap_text_10", style);

		Font normalCenterNoBorderWraptextFont10 = wb.createFont();
		normalCenterNoBorderWraptextFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterNoBorderWraptextFont10);
		style.setWrapText(true);
		styles.put("cell_normal_center_no_border_wrap_text_10", style);

		Font normalLeftNoBorderWraptextFonTopt11 = wb.createFont();
		normalLeftNoBorderWraptextFonTopt11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(normalLeftNoBorderWraptextFonTopt11);
		style.setWrapText(true);
		styles.put("cell_normal_left_no_border_wrap_text__top11", style);

		Font normalLeftNoBorderWraptextFonTopt10 = wb.createFont();
		normalLeftNoBorderWraptextFonTopt10.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(normalLeftNoBorderWraptextFonTopt10);
		style.setWrapText(true);
		styles.put("cell_normal_left_no_border_wrap_text__top10", style);

		Font normalCenterTop10 = wb.createFont();
		normalCenterTop10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(normalCenterTop10);
		style.setWrapText(true);
		styles.put("cell_normal_centered_top10", style);

		Font normalLeftNoBorderFont10 = wb.createFont();
		normalLeftNoBorderFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftNoBorderFont10);
		styles.put("cell_normal_left_no_border_10", style);

		style = createBorderedBlueStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setWrapText(true);
		styles.put("cell_centered_bordered_blue_top", style);

		Font headerFont = wb.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		styles.put("header", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("header_date", style);

		Font font1 = wb.createFont();
		font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font1);
		styles.put("cell_b", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font1);
		styles.put("cell_b_centered", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font1);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("cell_b_date", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font1);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("cell_g", style);

		Font font2 = wb.createFont();
		font2.setColor(IndexedColors.BLUE.getIndex());
		font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font2);
		styles.put("cell_bb", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font1);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("cell_bg", style);

		Font font3 = wb.createFont();
		font3.setFontHeightInPoints((short) 14);
		font3.setColor(IndexedColors.DARK_BLUE.getIndex());
		font3.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font3);
		style.setWrapText(true);
		styles.put("cell_h", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setWrapText(true);
		styles.put("cell_normal", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		styles.put("cell_normal_centered", style);

		Font fontCenteredBlueBold = wb.createFont();
		fontCenteredBlueBold.setColor(IndexedColors.BLUE.getIndex());
		fontCenteredBlueBold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontCenteredBlueBold.setFontHeightInPoints((short) 9);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(fontCenteredBlueBold);
		styles.put("cell_centered_blue_bold_9", style);

		Font fontLeftBlueNormal = wb.createFont();
		fontLeftBlueNormal.setColor(IndexedColors.BLUE.getIndex());
		fontLeftBlueNormal.setFontHeightInPoints((short) 8);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(fontLeftBlueNormal);
		styles.put("cell_left_blue_normal_8", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setWrapText(true);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("cell_normal_date", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setIndention((short) 1);
		style.setWrapText(true);
		styles.put("cell_indented", style);

		style = createBorderedStyle(wb);
		style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put("cell_blue", style);

		style = createNonBorderedStyle(wb);
		style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put("cell_blue_no_border", style);

		style = createNonBorderedStyle(wb);

		Font blueNoBorder16 = wb.createFont();
		blueNoBorder16.setFontHeightInPoints((short) 16);
		style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(blueNoBorder16);
		styles.put("cell_blue_no_border_16", style);

		/*
		 * Font normalCenterBorderTopFont10 = wb.createFont();
		 * normalCenterBorderTopFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		 * normalCenterBorderTopFont10.setFontHeightInPoints((short) 10); style
		 * = createBorderedStyle(wb);
		 * style.setAlignment(CellStyle.ALIGN_CENTER);
		 * style.setFont(normalCenterBorderTopFont11); style.setWrapText(true);
		 * style.setBorderTop(CellStyle.BORDER_DOUBLE);
		 * styles.put("cell_normal_centered_wrap_text_border_top_11", style);
		 */

		Font normalCenterTopFont10 = wb.createFont();
		normalCenterTopFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalCenterTopFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterTopFont10);
		styles.put("cell_normal_centered_10", style);

		Font normalCenterFont11 = wb.createFont();
		normalCenterFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalCenterFont10.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterFont11);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		styles.put("cell_normal_centered_11", style);

		Font normalCenterFontNotBorderThin11 = wb.createFont();
		normalCenterFontNotBorderThin11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalCenterFontNotBorderThin11.setFontHeightInPoints((short) 11);
		style = createBorderedThinStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterFontNotBorderThin11);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		styles.put("cell_normal_centered_not_border_thin_11", style);

		return styles;
	}

	@SuppressWarnings({ "unchecked" })
	public static void exportPurchaseOrderReportToExcel(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String dateType = request.getParameter("dateType");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String statusId = request.getParameter("statusId");
		String categoryId = request.getParameter("categoryId");
		String filterSaleOrder = request.getParameter("filterSaleOrderInput");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		List<String> productIdInput = null;
		List<String> statusIdInput = null;
		List<String> categoryIdInput = null;
		Locale locale = UtilHttp.getLocale(request);
		if (productId.equals("") || productId.equals("null")) {
			productIdInput = null;
		}
		if (!productId.equals("") && !productId.equals("null")) {
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if (productIdData.length != 0) {
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}

		if (statusId.equals("") || statusId.equals("null")) {
			statusIdInput = null;
		}
		if (!statusId.equals("") && !statusId.equals("null")) {
			String[] statusIdData = statusId.split(",");
			statusIdInput = new ArrayList<>();
			if (statusIdData.length != 0) {
				for (String i : statusIdData) {
					statusIdInput.add(i);
				}
			}
		}

		if (categoryId.equals("") || categoryId.equals("null")) {
			categoryIdInput = null;
		}
		if (!categoryId.equals("") && !categoryId.equals("null")) {
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if (categoryIdData.length != 0) {
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (dateType == null) {
			dateType = "DAY";
		}
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));

		String fromDateStrTime = formatDate.format(new Timestamp(fromDateLog));
		String thruDateStrTime = formatDate.format(new Timestamp(thruDateLog));

		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("statusId[]", statusIdInput);
		context.put("userLogin", userLogin);
		context.put("categoryId[]", categoryIdInput);
		context.put("filterSaleOrder", filterSaleOrder);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String titleFromDate = UtilProperties.getMessage(resource, "FromDate", locale);
			String titleThruDate = UtilProperties.getMessage(resource, "ThruDate", locale);

			Map<String, Object> resultService = dispatcher.runSync("jqGetListPurchaseOrderReport", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");

			// start renderExcel
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = createStyles(wb);
			Sheet sheet = wb.createSheet("Sheet1");
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);
			// turn on gridlines
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);

			sheet.setAutobreaks(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);

			sheet.setColumnWidth(0, 21 * 300);
			sheet.setColumnWidth(1, 21 * 300);
			sheet.setColumnWidth(2, 21 * 300);
			sheet.setColumnWidth(3, 21 * 300);
			sheet.setColumnWidth(4, 21 * 600);
			sheet.setColumnWidth(5, 21 * 300);
			sheet.setColumnWidth(6, 21 * 300);
			sheet.setColumnWidth(7, 21 * 300);
			sheet.setColumnWidth(8, 21 * 300);
			sheet.setColumnWidth(9, 21 * 450);
			sheet.setColumnWidth(10, 21 * 500);
			sheet.setColumnWidth(11, 21 * 700);
			sheet.setColumnWidth(12, 21 * 600);
			sheet.setColumnWidth(13, 21 * 350);
			sheet.setColumnWidth(14, 21 * 350);
			sheet.setColumnWidth(15, 21 * 350);
			sheet.setColumnWidth(16, 21 * 350);
			sheet.setColumnWidth(17, 21 * 350);
			sheet.setColumnWidth(18, 21 * 350);

			/* Row imgHead = sheet.createRow(0); */
			int rownum = 0;
			FileInputStream is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(
						UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
				File file = new File(imageServerPath);
				is = new FileInputStream(file);
				byte[] bytesImg = IOUtils.toByteArray(is);
				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
				CreationHelper helper = wb.getCreationHelper();
				Drawing drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setCol2(1);
				anchor.setRow1(0);
				anchor.setRow2(3);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null)
					is.close();
			}

			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 17));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;

			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			String title = UtilProperties.getMessage(resource, "POPurchaseOrderReport", locale);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 17));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(title.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;

			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 17));
			Cell dateFromToCell = dateRow.createCell(0);
			String dateFromTo = titleFromDate + ": " + fromDateStrTime + " - " + titleThruDate + ": " + thruDateStrTime;
			dateFromToCell.setCellValue(dateFromTo);
			dateFromToCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;

			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 17));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellValue(" ");
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;

			String titleOrderId = UtilProperties.getMessage(resource, "DAOrderId", locale);
			String titleOrderSales = UtilProperties.getMessage(resource, "DAOrderSales", locale);
			String titleCreateOrderDate = UtilProperties.getMessage(resource, "POCreateOrderDate", locale);
			String titlePOProductId = UtilProperties.getMessage(resource, "POProductId", locale);
			String titleProductName = UtilProperties.getMessage(resource, "POProductName", locale);
			String titleQuantity = UtilProperties.getMessage(resource, "DAAlternativeQuantity", locale);
			String titlePOExportedQuantity = UtilProperties.getMessage(resource, "POActualExportedQuantity", locale);
			String titlePOQuantityImported = UtilProperties.getMessage(resource, "POQuantityImported", locale);
			String titleQuantityUomId = UtilProperties.getMessage(resource, "QuantityUomId", locale);
			String titleDesiredDeliveryDate = UtilProperties.getMessage(resource, "PODesiredDeliveryDate", locale);
			String titleProductCatalogs = UtilProperties.getMessage(resource, "POProductCatalogs", locale);
			String titleOrderStatus = UtilProperties.getMessage(resource, "DAOrderStatus", locale);
			String titleFacilityName = UtilProperties.getMessage(resource, "POFacilityName", locale);
			String titleSupplier = UtilProperties.getMessage(resource, "POSupplier", locale);
			String titleAccountingCustomer = UtilProperties.getMessage(resource, "POAccountingCustomer", locale);
			String titleOrderDestination = UtilProperties.getMessage(resource, "POOrderDestination", locale);
			String titleReceivedDate = UtilProperties.getMessage(resource, "POReceivedDate", locale);
			String titleNumberDifferences = UtilProperties.getMessage(resource, "PONumberDifferences", locale);
			String titleReasonsDifference = UtilProperties.getMessage(resource, "POReasonsDifference", locale);
			List<String> titles = new FastList<String>();
			titles.add(titleOrderId);
			titles.add(titleOrderSales);
			titles.add(titleCreateOrderDate);
			titles.add(titlePOProductId);
			titles.add(titleProductName);
			titles.add(titleQuantityUomId);
			titles.add(titleProductCatalogs);
			titles.add(titleQuantity);
			titles.add(titleFacilityName);
			titles.add(titleSupplier);
			titles.add(titleAccountingCustomer);
			titles.add(titleOrderDestination);
			titles.add(titleDesiredDeliveryDate);
			titles.add(titleOrderStatus);
			titles.add(titleReceivedDate);
			titles.add(titlePOExportedQuantity);
			titles.add(titlePOQuantityImported);
			titles.add(titleNumberDifferences);
			titles.add(titleReasonsDifference);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 600);
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			rownum += 1;
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 500);

				Cell orderIdCell = orderDetailRow.createCell(0);
				String orderId = (String) map.get("orderId");
				orderIdCell.setCellValue(orderId);
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				Cell orderIdSOCell = orderDetailRow.createCell(1);
				String orderIdSo = (String) map.get("orderIdSo");
				orderIdSOCell.setCellValue(orderIdSo);
				orderIdSOCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				Cell dateCell = orderDetailRow.createCell(2);
				String date = (String) map.get("date");
				dateCell.setCellValue(date);
				dateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String productIdOut = (String) map.get("productId");
				Cell productCell = orderDetailRow.createCell(3);
				productCell.setCellValue(productIdOut);
				productCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String productName = (String) map.get("productName");
				Cell productNameCell = orderDetailRow.createCell(4);
				productNameCell.setCellValue(productName);
				productNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String quantityUomIdOut = (String) map.get("quantityUomId");
				GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomIdOut), false);
				String description = quantityUomIdOut;
				if (uom != null) {
					description = uom.getString("description");
				}
				Cell quantityUomIdCell = orderDetailRow.createCell(5);
				quantityUomIdCell.setCellValue(description);
				quantityUomIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String categoryIdOut = (String) map.get("categoryId");
				Cell categoryIdCell = orderDetailRow.createCell(6);
				categoryIdCell.setCellValue(categoryIdOut);
				categoryIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				BigDecimal quantity = (BigDecimal) map.get("quantity");
				double quantityDou = 0;
				if (UtilValidate.isNotEmpty(quantity)) {
					quantityDou = quantity.doubleValue();
				}
				Cell quantityCell = orderDetailRow.createCell(7);
				quantityCell.setCellValue(quantityDou);
				quantityCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String facilityId = (String) map.get("facilityId");
				Cell facilityIdCell = orderDetailRow.createCell(8);
				facilityIdCell.setCellValue(facilityId);
				facilityIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String partyId = (String) map.get("partyId");
				Cell partyIdCell = orderDetailRow.createCell(9);
				partyIdCell.setCellValue(partyId);
				partyIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String partyCustomerSO = (String) map.get("partyCustomerSO");
				Cell partyCustomerSOCell = orderDetailRow.createCell(10);
				partyCustomerSOCell.setCellValue(partyCustomerSO);
				partyCustomerSOCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String contactMechId = (String) map.get("contactMechId");
				Cell contactMechIdCell = orderDetailRow.createCell(11);
				contactMechIdCell.setCellValue(contactMechId);
				contactMechIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String shipByDate = (String) map.get("shipByDate");
				Cell shipByDateCell = orderDetailRow.createCell(12);
				shipByDateCell.setCellValue(shipByDate);
				shipByDateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String statusIdOut = (String) map.get("statusId");
				Cell statusIdCell = orderDetailRow.createCell(13);
				statusIdCell.setCellValue(statusIdOut);
				statusIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String actualArrivalDateStr = (String) map.get("actualArrivalDate");
				Cell actualArrivalDateCell = orderDetailRow.createCell(14);
				actualArrivalDateCell.setCellValue(actualArrivalDateStr);
				actualArrivalDateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				BigDecimal actualExportedQuantity = (BigDecimal) map.get("actualExportedQuantity");
				double actualExportedQuantityDou = 0;
				if (UtilValidate.isNotEmpty(actualExportedQuantity)) {
					actualExportedQuantityDou = actualExportedQuantity.doubleValue();
				}
				Cell actualExportedQuantityCell = orderDetailRow.createCell(15);
				actualExportedQuantityCell.setCellValue(actualExportedQuantityDou);
				actualExportedQuantityCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				BigDecimal quantityOnHandDiff = (BigDecimal) map.get("quantityOnHandDiff");
				double quantityOnHandDiffDou = 0;
				if (UtilValidate.isNotEmpty(quantityOnHandDiff)) {
					quantityOnHandDiffDou = quantityOnHandDiff.doubleValue();
				}
				Cell quantityOnHandDiffCell = orderDetailRow.createCell(16);
				quantityOnHandDiffCell.setCellValue(quantityOnHandDiffDou);
				quantityOnHandDiffCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				BigDecimal ratio = (BigDecimal) map.get("ratio");
				double ratioDou = 0;
				if (UtilValidate.isNotEmpty(ratio)) {
					ratioDou = ratio.doubleValue();
				}
				Cell ratioCell = orderDetailRow.createCell(17);
				ratioCell.setCellValue(ratioDou);
				ratioCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String reasonsDifferenceOut = (String) map.get("reasonsDifference");
				Cell reasonsDifferenceCell = orderDetailRow.createCell(18);
				reasonsDifferenceCell.setCellValue(reasonsDifferenceOut);
				reasonsDifferenceCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				rownum += 1;
			}

			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Calendar cal = Calendar.getInstance();
			String currentDate = dateFormat.format(cal.getTime());
			response.setHeader("content-disposition", "attachment;filename=" + "Baocaomuahang_" + currentDate + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if (baos != null)
				baos.close();
		}
	}

	public static void exportExcelMutilPOByOrderList(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String orderIdData = request.getParameter("orderIdData");
		JSONArray listOrderIdData = JSONArray.fromObject(orderIdData);
		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = createStyles(wb);
		if (listOrderIdData.size() != 0) {
			for (int j = 0; j < listOrderIdData.size(); j++) {
				GenericValue orderHeader = delegator.findOne("OrderHeader",
						UtilMisc.toMap("orderId", listOrderIdData.get(j)), false);
				Timestamp orderDate = orderHeader.getTimestamp("orderDate");
				String orderDateStr = new SimpleDateFormat("MM-dd-yyyy").format(orderDate);
				HttpSession session = request.getSession();
				GenericValue userLoginValue = (GenericValue) session.getAttribute("userLogin");
				String userLoginId = userLoginValue.getString("userLoginId");
				String orgId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", orgId), false);
				String groupName = "";
				if (partyGroup != null) {
					groupName = partyGroup.getString("groupName");
				}
				String contactNumber = "";
				String lastNameStorekeeper = "";

				List<GenericValue> listOrderContactMech = delegator.findList(
						"OrderContactMech", EntityCondition.makeCondition(UtilMisc.toMap("orderId",
								listOrderIdData.get(j), "contactMechPurposeTypeId", "SHIPPING_LOCATION")),
						null, null, null, false);
				GenericValue postalAddress = null;
				if (!listOrderContactMech.isEmpty()) {
					for (GenericValue orderContactMech : listOrderContactMech) {
						String contactMechId = orderContactMech.getString("contactMechId");
						postalAddress = delegator.findOne("PostalAddress",
								UtilMisc.toMap("contactMechId", contactMechId), false);
						if (postalAddress.getString("toName") != null) {
							lastNameStorekeeper = postalAddress.getString("toName");
						}
						if (postalAddress.getString("attnName") != null) {
							contactNumber = postalAddress.getString("attnName");
						}
					}
				}

				List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup",
						EntityCondition.makeCondition(UtilMisc.toMap("orderId", listOrderIdData.get(j))), null, null,
						null, false);
				Timestamp shipByDate = null;
				Timestamp shipAfterDate = null;
				if (!listOrderItemShipGroup.isEmpty()) {
					for (GenericValue orderItemShipGroup : listOrderItemShipGroup) {
						shipByDate = orderItemShipGroup.getTimestamp("shipByDate");
						shipAfterDate = orderItemShipGroup.getTimestamp("shipAfterDate");
					}
				}
				String address = "";
				if (postalAddress != null) {
					address = postalAddress.getString("address1");

					GenericValue wardGeo = delegator.findOne("Geo",
							UtilMisc.toMap("geoId", postalAddress.getString("wardGeoId")), false);
					if (wardGeo != null) {
						address += "," + wardGeo.getString("geoName");
					}

					GenericValue districtGeo = delegator.findOne("Geo",
							UtilMisc.toMap("geoId", postalAddress.getString("districtGeoId")), false);
					if (districtGeo != null) {
						address += "," + districtGeo.getString("geoName");
					}

					if (postalAddress.getString("city") != null) {
						address += "," + postalAddress.getString("city");
					}
				}
				// start renderExcel
				Sheet sheet = wb.createSheet(listOrderIdData.get(j) + "_" + orderDateStr);

				CellStyle csWrapText = wb.createCellStyle();
				csWrapText.setWrapText(true);

				// turn on gridlines
				sheet.setDisplayGridlines(true);
				sheet.setPrintGridlines(true);
				sheet.setFitToPage(true);
				sheet.setHorizontallyCenter(true);
				PrintSetup printSetup = sheet.getPrintSetup();
				printSetup.setLandscape(true);

				sheet.setAutobreaks(true);
				printSetup.setFitHeight((short) 1);
				printSetup.setFitWidth((short) 1);

				sheet.setColumnWidth(0, 15 * 350);
				sheet.setColumnWidth(1, 15 * 350);
				sheet.setColumnWidth(2, 15 * 500);
				sheet.setColumnWidth(3, 15 * 256);
				sheet.setColumnWidth(4, 15 * 500);
				sheet.setColumnWidth(5, 25 * 250);
				sheet.setColumnWidth(6, 15 * 400);

				Row imgHead = sheet.createRow(0);
				imgHead.createCell(0);
				int rownum = 0;
				InputStream is = null;
				try {
					is = new BrandLogo(delegator).getInputStream(request);
					byte[] bytesImg = IOUtils.toByteArray(is);
					int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
					CreationHelper helper = wb.getCreationHelper();
					Drawing drawing = sheet.createDrawingPatriarch();
					ClientAnchor anchor = helper.createClientAnchor();
					anchor.setCol1(0);
					anchor.setCol2(1);
					anchor.setRow1(0);
					anchor.setRow2(5);
					Picture pict = drawing.createPicture(anchor, pictureIdx);
					pict.getPictureData();
					rownum = 5;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (is != null)
						is.close();
				}
				Row khoangCachRow = sheet.createRow(rownum);
				khoangCachRow.setHeight((short) 400);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
				Cell khoangCachCell = khoangCachRow.createCell(0);
				khoangCachCell.setCellValue(" ");
				khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
				rownum += 1;

				Row titleRow = sheet.createRow(rownum);
				titleRow.setHeight((short) 400);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(UtilProperties.getMessage(resource, "DmsOrderPO", locale).toUpperCase());
				titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
				rownum += 1;

				Row khoangCachRow2 = sheet.createRow(rownum);
				khoangCachRow2.setHeight((short) 400);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
				Cell khoangCachCell2 = khoangCachRow2.createCell(0);
				khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
				rownum += 1;

				Row orderInforRow = sheet.createRow(rownum);
				orderInforRow.setHeight((short) 300);
				Cell orderIdCell = orderInforRow.createCell(0);
				orderIdCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
				Cell orderNameCell = orderInforRow.createCell(5);
				orderNameCell.setCellValue(
						UtilProperties.getMessage(resource, "DAOrderId", locale) + ":" + listOrderIdData.get(j));
				orderNameCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
				rownum += 1;

				Row khoangCachRow3 = sheet.createRow(rownum);
				khoangCachRow2.setHeight((short) 400);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
				Cell khoangCachCell3 = khoangCachRow3.createCell(0);
				khoangCachCell3.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
				rownum += 1;

				Row orderInforRow2 = sheet.createRow(rownum);
				orderInforRow2.setHeight((short) 300);
				Cell orderDateCell = orderInforRow2.createCell(5);
				orderDateCell.setCellValue(
						UtilProperties.getMessage(resource, "POCreateOrderDate", locale) + ":" + orderDate);
				orderDateCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
				rownum += 1;

				Row khoangCachRow5 = sheet.createRow(rownum);
				khoangCachRow5.setHeight((short) 400);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
				Cell khoangCachCell5 = khoangCachRow5.createCell(0);
				khoangCachCell5.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
				rownum += 1;

				List<GenericValue> listOrderItem = delegator.findList("OrderItem",
						EntityCondition.makeCondition(UtilMisc.toMap("orderId", listOrderIdData.get(j))), null, null,
						null, false);
				String titleProductName = UtilProperties.getMessage(resource, "POProductName", locale);
				String organi = UtilProperties.getMessage(resource, "POOrganization", locale);
				String titleQuantity = UtilProperties.getMessage(resource, "DAQuantity", locale);
				String shippingAddress = UtilProperties.getMessage(resource, "POShippingAddress", locale);
				String PODeliveryTime = UtilProperties.getMessage(resource, "PODeliveryTime", locale);
				String POProductId = UtilProperties.getMessage(resource, "POProductId", locale);
				String POCommunicationPartyId = UtilProperties.getMessage(resource, "POCommunicationPartyId", locale);
				List<String> titles = new FastList<String>();
				titles.add(organi);
				titles.add(POProductId);
				titles.add(titleProductName);
				titles.add(titleQuantity);
				titles.add(shippingAddress);
				titles.add(PODeliveryTime);
				titles.add(POCommunicationPartyId);
				Row headerBreakdownAmountRow = sheet.createRow(rownum);
				headerBreakdownAmountRow.setHeight((short) 500);
				for (int i = 0; i < titles.size(); i++) {
					Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
					headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_10"));
					headerBreakdownAmountCell.setCellValue(titles.get(i));
				}
				rownum += 1;
				int index = 0;
				int a = listOrderItem.size();
				for (GenericValue x : listOrderItem) {
					Row productDetailRow = sheet.createRow(rownum);
					productDetailRow.setHeight((short) 700);

					Cell organzationCell = productDetailRow.createCell(0);
					if (index == 0) {
						int rowStart = productDetailRow.getRowNum();
						int rowEnd = a + rowStart - 1;
						sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, 0, 0));
						organzationCell.setCellValue(groupName);
						organzationCell.setCellStyle(styles.get("cell_normal_centered_not_border_thin_11"));
					}
					String productId = x.getString("productId");
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					Cell productIdCell = productDetailRow.createCell(1);
					productIdCell.setCellValue(product.getString("productCode"));
					productIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

					String internalName = product.getString("internalName");
					Cell productDescriptionCell = productDetailRow.createCell(2);
					productDescriptionCell.setCellValue(internalName);
					productDescriptionCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

					Cell productQuantityCell = productDetailRow.createCell(3);
					BigDecimal quantity = (BigDecimal) x.get("quantity");
					productQuantityCell.setCellValue(quantity.floatValue());
					productQuantityCell.setCellStyle(styles.get("cell_normal_right_boder_full_10"));

					Cell diaDiemGiaoHang = productDetailRow.createCell(4);
					if (index == 0) {
						int rowStart = productDetailRow.getRowNum();
						int rowEnd = a + rowStart - 1;
						sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, 4, 4));
						diaDiemGiaoHang.setCellValue(address);
						diaDiemGiaoHang.setCellStyle(styles.get("cell_normal_centered_not_border_thin_11"));
					}

					Cell shipByDateCell = productDetailRow.createCell(5);
					if (index == 0) {
						int rowStart = productDetailRow.getRowNum();
						int rowEnd = a + rowStart - 1;
						sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, 5, 5));
						if (shipByDate != null && shipAfterDate != null) {
							String shipByDateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(shipByDate);
							String shipAfterDateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(shipAfterDate);
							shipByDateCell.setCellValue(shipAfterDateStr + "-" + shipByDateStr);
						}
						shipByDateCell.setCellStyle(styles.get("cell_normal_centered_not_border_thin_11"));
					}

					Cell tongTienCell = productDetailRow.createCell(6);
					if (index == 0) {
						int rowStart = productDetailRow.getRowNum();
						int rowEnd = a + rowStart - 1;
						sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, 6, 6));
						if (contactNumber.equals("")) {
							tongTienCell.setCellValue(lastNameStorekeeper);
							tongTienCell.setCellStyle(styles.get("cell_normal_centered_11"));
						} else {
							tongTienCell.setCellValue(lastNameStorekeeper + "(" + contactNumber + ")");
							tongTienCell.setCellStyle(styles.get("cell_normal_centered_11"));
						}
					}
					rownum += 1;
					index += 1;
				}

				Row khoangCachRow6 = sheet.createRow(rownum);
				khoangCachRow6.setHeight((short) 400);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
				Cell khoangCachCell6 = khoangCachRow6.createCell(0);
				khoangCachCell6.setCellValue(" ");
				khoangCachCell6.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
				rownum += 1;

				Map<String, Object> mapOrderRole = getPartyRoleOrder(delegator, (String) listOrderIdData.get(j));
				String namePartySupplier = (String) mapOrderRole.get("partySupplier");

				Row orderFooterRow = sheet.createRow(rownum);
				orderFooterRow.setHeight((short) 600);
				Cell orderFooterCell1 = orderFooterRow.createCell(1);
				String receiver = UtilProperties.getMessage(resource, "POReceiver", locale);
				orderFooterCell1.setCellValue("" + namePartySupplier + "\n" + receiver);
				orderFooterCell1.setCellStyle(styles.get("cell_normal_centered_11"));
				Cell orderFooterCell2 = orderFooterRow.createCell(4);
				String createBy = UtilProperties.getMessage(resource, "POCreatedBy", locale);
				orderFooterCell2.setCellValue("Nhà thuốc Phương Chính \n" + createBy);
				orderFooterCell2.setCellStyle(styles.get("cell_normal_centered_11"));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 3));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 6));
				rownum += 1;
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			String purchaseStr = UtilProperties.getMessage(resource, "TotalPurchaseExportExcel", locale);
			response.setHeader("content-disposition", "attachment;filename=" + purchaseStr + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if (baos != null)
				baos.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static void exportReturnProductOlapPOToExcel(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String dateType = request.getParameter("dateType");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String categoryId = request.getParameter("categoryId");
		String returnReasonId = request.getParameter("returnReasonId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> categoryIdInput = null;
		List<String> returnReasonIdInput = null;
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

		if (productId.equals("") || productId.equals("null")) {
			productIdInput = null;
		}
		if (!productId.equals("") && !productId.equals("null")) {
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if (productIdData.length != 0) {
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}

		if (facilityId.equals("") || facilityId.equals("null")) {
			facilityIdInput = null;
		}
		if (!facilityId.equals("") && !facilityId.equals("null")) {
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if (facilityIdData.length != 0) {
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}

		if (categoryId.equals("") || categoryId.equals("null")) {
			categoryIdInput = null;
		}
		if (!categoryId.equals("") && !categoryId.equals("null")) {
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if (categoryIdData.length != 0) {
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}

		if (returnReasonId.equals("") || returnReasonId.equals("null")) {
			returnReasonIdInput = null;
		}
		if (!returnReasonId.equals("") && !returnReasonId.equals("null")) {
			String[] returnReasonIdData = returnReasonId.split(",");
			returnReasonIdInput = new ArrayList<>();
			if (returnReasonIdData.length != 0) {
				for (String i : returnReasonIdData) {
					returnReasonIdInput.add(i);
				}
			}
		}

		if (dateType == null) {
			dateType = "DAY";
		}
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));

		String fromDateStrTime = formatDate.format(new Timestamp(fromDateLog));
		String thruDateStrTime = formatDate.format(new Timestamp(thruDateLog));

		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("facilityId[]", facilityIdInput);
		context.put("categoryId[]", categoryIdInput);
		context.put("returnReasonId[]", returnReasonIdInput);
		context.put("userLogin", userLogin);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String titleFromDate = UtilProperties.getMessage(resource, "FromDate", locale);
			String titleThruDate = UtilProperties.getMessage(resource, "ThruDate", locale);

			Map<String, Object> resultService = dispatcher.runSync("jqGetListReturnProductReportOlapPO", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");

			// start renderExcel
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = createStyles(wb);
			Sheet sheet = wb.createSheet("Sheet1");
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);
			// turn on gridlines
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);

			sheet.setAutobreaks(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);

			sheet.setColumnWidth(0, 21 * 300);
			sheet.setColumnWidth(1, 21 * 300);
			sheet.setColumnWidth(2, 21 * 300);
			sheet.setColumnWidth(3, 21 * 500);
			sheet.setColumnWidth(4, 21 * 300);
			sheet.setColumnWidth(5, 21 * 300);
			sheet.setColumnWidth(6, 21 * 300);
			sheet.setColumnWidth(7, 21 * 300);
			sheet.setColumnWidth(8, 21 * 300);
			sheet.setColumnWidth(9, 21 * 300);
			sheet.setColumnWidth(10, 21 * 400);
			sheet.setColumnWidth(11, 21 * 400);
			sheet.setColumnWidth(12, 21 * 300);
			sheet.setColumnWidth(13, 21 * 300);
			sheet.setColumnWidth(14, 21 * 300);
			/*
			 * Row imgHead = sheet.createRow(0); Cell imgCell =
			 * imgHead.createCell(0);
			 */
			int rownum = 0;
			FileInputStream is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(
						UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
				File file = new File(imageServerPath);
				is = new FileInputStream(file);
				byte[] bytesImg = IOUtils.toByteArray(is);
				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
				CreationHelper helper = wb.getCreationHelper();
				Drawing drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setCol2(1);
				anchor.setRow1(0);
				anchor.setRow2(3);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null)
					is.close();
			}

			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 14));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;

			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 14));
			Cell titleCell = titleRow.createCell(0);
			String title = UtilProperties.getMessage(resource, "POStatisticalReturnProductReport", locale);
			titleCell.setCellValue(title.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_11"));
			rownum += 1;

			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 14));
			Cell dateFromToCell = dateRow.createCell(0);
			String dateFromTo = titleFromDate + ": " + fromDateStrTime + " - " + titleThruDate + ": " + thruDateStrTime;
			dateFromToCell.setCellValue(dateFromTo);
			dateFromToCell.setCellStyle(styles.get("cell_bold_centered_no_border_11"));
			rownum += 1;

			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 14));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;

			String titleDateReturn = UtilProperties.getMessage(resource, "POReturnDate", locale);
			String titleReturnCode = UtilProperties.getMessage(resource, "POReturnId", locale);
			String titleProductCode = UtilProperties.getMessage(resource, "POProductId", locale);
			String titleProductName = UtilProperties.getMessage(resource, "POProductName", locale);
			String titleQuantityReturn = UtilProperties.getMessage(resource, "POQuantityReturned", locale);
			String titleQuantityUom = UtilProperties.getMessage(resource, "QuantityUomId", locale);
			String titleTotalPrice = UtilProperties.getMessage(resource, "remainingTotal", locale);
			String titleOrderSale = UtilProperties.getMessage(resource, "OrderPO", locale);
			String titleReasonReturn = UtilProperties.getMessage(resource, "PORejectReasonReturnProduct", locale);
			String titlePersonReturn = UtilProperties.getMessage(resource, "POPersonReturns", locale);
			String titlePersonReceive = UtilProperties.getMessage(resource, "POReceiver", locale);
			String titleReturnType = UtilProperties.getMessage(resource, "POGoodReturned", locale);
			String titleFacilityName = UtilProperties.getMessage(resource, "POFacilityName", locale);
			String titleStatus = UtilProperties.getMessage(resource, "POReturnStt", locale);
			String titleCategory = UtilProperties.getMessage(resource, "POCategoryName", locale);
			List<String> titles = new FastList<String>();
			titles.add(titleDateReturn);
			titles.add(titleReturnCode);
			titles.add(titleProductCode);
			titles.add(titleProductName);
			titles.add(titleQuantityReturn);
			titles.add(titleQuantityUom);
			titles.add(titleTotalPrice);
			titles.add(titleOrderSale);
			titles.add(titleReasonReturn);
			titles.add(titlePersonReturn);
			titles.add(titlePersonReceive);
			titles.add(titleReturnType);
			titles.add(titleFacilityName);
			titles.add(titleStatus);
			titles.add(titleCategory);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 600);
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			rownum += 1;
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 600);

				String date = (String) map.get("date");
				Cell dateCell = orderDetailRow.createCell(0);
				dateCell.setCellValue(date);
				dateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String returnIdIdOut = (String) map.get("returnId");
				Cell returnIdCell = orderDetailRow.createCell(1);
				returnIdCell.setCellValue(returnIdIdOut);
				returnIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String productIdOut = (String) map.get("productId");
				Cell productCell = orderDetailRow.createCell(2);
				productCell.setCellValue(productIdOut);
				productCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String productNameOut = (String) map.get("productName");
				Cell productNameCell = orderDetailRow.createCell(3);
				productNameCell.setCellValue(productNameOut);
				productNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				BigDecimal quantityReturn = (BigDecimal) map.get("returnQuantity");
				Cell quantityReturnCell = orderDetailRow.createCell(4);
				quantityReturnCell.setCellValue(quantityReturn.doubleValue());
				quantityReturnCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String quantityUomId = (String) map.get("quantityUomId");
				GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
				String decription = quantityUomId;
				if (uom != null) {
					decription = uom.getString("description");
				}
				Cell quantityUomIdCell = orderDetailRow.createCell(5);
				quantityUomIdCell.setCellValue(decription);
				quantityUomIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				int returnPrice = (Integer) map.get("returnPrice");
				Cell returnPriceCell = orderDetailRow.createCell(6);
				returnPriceCell.setCellValue(returnPrice);
				returnPriceCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String orderIdOut = (String) map.get("orderId");
				Cell orderIdCell = orderDetailRow.createCell(7);
				orderIdCell.setCellValue(orderIdOut);
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String returnReasonIdOut = (String) map.get("returnReasonId");
				GenericValue returnReason = delegator.findOne("ReturnReason",
						UtilMisc.toMap("returnReasonId", returnReasonIdOut), false);
				if (returnReason != null) {
					returnReasonIdOut = returnReason.getString("description");
				}
				Cell returnReasonIdCell = orderDetailRow.createCell(8);
				returnReasonIdCell.setCellValue(returnReasonIdOut);
				returnReasonIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String partyFromIdOut = (String) map.get("partyFromId");
				Cell partyFromIdCell = orderDetailRow.createCell(9);
				partyFromIdCell.setCellValue(partyFromIdOut);
				partyFromIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String partyToIdOut = (String) map.get("partyToId");
				Cell partyToIdCell = orderDetailRow.createCell(10);
				partyToIdCell.setCellValue(partyToIdOut);
				partyToIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String returnItemTypeId = (String) map.get("returnItemTypeId");
				GenericValue returnItemType = delegator.findOne("ReturnItemType",
						UtilMisc.toMap("returnItemTypeId", returnItemTypeId), false);
				if (returnItemType != null) {
					returnItemTypeId = returnItemType.getString("description");
				}
				Cell returnItemTypeIdCell = orderDetailRow.createCell(11);
				returnItemTypeIdCell.setCellValue(returnItemTypeId);
				returnItemTypeIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String facilityIdOut = (String) map.get("facilityName");
				Cell facilityIdCell = orderDetailRow.createCell(12);
				facilityIdCell.setCellValue(facilityIdOut);
				facilityIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String statusId = (String) map.get("statusHeaderId");
				Cell statusIdCell = orderDetailRow.createCell(13);
				statusIdCell.setCellValue(statusId);
				statusIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String categoryIdOut = (String) map.get("categoryName");
				Cell categoryIdCell = orderDetailRow.createCell(14);
				categoryIdCell.setCellValue(categoryIdOut);
				categoryIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				rownum += 1;
			}

			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Calendar cal = Calendar.getInstance();
			String currentDate = dateFormat.format(cal.getTime());
			response.setHeader("content-disposition", "attachment;filename=" + "Baocaotrahang_" + currentDate + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if (baos != null)
				baos.close();
		}
	}
}
