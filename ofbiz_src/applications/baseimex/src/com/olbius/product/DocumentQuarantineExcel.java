package com.olbius.product;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.olbius.quota.QuotaExcel;
import com.olbius.util.AgreementUtil;

import javolution.util.FastList;

public class DocumentQuarantineExcel {

	public final static String RESOURCE = "BaseImExUiLabels";
	public final static String RESOURCE_LOG = "BaseLogisticsUiLabels";
	public static String module = QuotaExcel.class.getName();
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	public static final String euroSign = new String("\u20AC");
	public static final String leftDoubleQuotationMark = new String("\u201C");
	public static final String rightDoubleQuotationMark = new String("\u201D");
	public static final String draftingPointRightWardArrow = new String("\u279B");
	public static final String ballotBox = new String("\u2610");
	
	public static void export(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String eventId = request.getParameter("eventId");
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("userLogin", userLogin);
		context.put("eventId", eventId);
		
		GenericValue objProductEvent = null;
		try {
			objProductEvent = delegator.findOne("ProductEvent", false, UtilMisc.toMap("eventId", eventId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductEvent: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		List<GenericValue> listPackingListItem = FastList.newInstance();
		try {
			listPackingListItem = delegator.findList("PackingListDetail", EntityCondition.makeCondition("packingListId", objProductEvent.getString("packingListId")), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PackingListItem: " + e.toString();
			Debug.logError(e, errMsg, module);
		}

		BigDecimal totalQuantity = BigDecimal.ZERO;
		BigDecimal totalCost = BigDecimal.ZERO;
		String packing = "";
		String quantityUomId = null;
		for (GenericValue item : listPackingListItem) {
			BigDecimal quantity = new BigDecimal(item.getLong("orderUnit"));
			totalQuantity = totalQuantity.add(quantity);
			GenericValue orderItem = item.getRelatedOne("OrderItem", false);
			quantityUomId = orderItem.getString("quantityUomId");
			if (UtilValidate.isEmpty(quantityUomId)) {
				GenericValue product = orderItem.getRelatedOne("Product", false);
				quantityUomId = product.getString("quantityUomId");
			}
			if (UtilValidate.isNotEmpty(orderItem)) {
				BigDecimal unitPrice = orderItem.getBigDecimal("unitPrice");
				totalCost = totalCost.add(quantity.multiply(unitPrice));
			}
		}
		
		GenericValue objUom = null;
		try {
			objUom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", quantityUomId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Uom: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		if (UtilValidate.isNotEmpty(objUom)) {
			packing = objUom.getString("description");
		}
		GenericValue packingList = objProductEvent.getRelatedOne("PackingListHeader", false);
		
		GenericValue agreement = objProductEvent.getRelatedOne("Agreement", false);
		GenericValue supplier = null;
		try {
			supplier = delegator.findOne("PartyFullNameDetail", false, UtilMisc.toMap("partyId", agreement.getString("partyIdTo")));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne PartyFullNameDetail: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		GenericValue customer = null;
		try {
			customer = delegator.findOne("PartyFullNameDetail", false, UtilMisc.toMap("partyId", agreement.getString("partyIdFrom")));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne PartyFullNameDetail: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		String agreementId = agreement.getString("agreementId");
		String partyIdFrom = agreement.getString("partyIdFrom");
		String partyIdTo = agreement.getString("partyIdTo");
		
		String placeOfManufacture = AgreementUtil.getAgreementPartyAddress(delegator, agreementId, partyIdTo, "PRIMARY_LOCATION");;
		String exporter = supplier.getString("fullName");
		String countryExport = AgreementUtil.getAgreementPartyGeoAddress(delegator, agreementId, partyIdTo, "PRIMARY_LOCATION", "GEOCOUTRY");
		
		String CustomsExport = "";
		String customsImport = "";
		List<String> port = AgreementUtil.getAgreementTermTextValue(delegator, agreement.getString("agreementId"), "PORT_OF_CHARGE");
		for (String u : port) {
			if (UtilValidate.isEmpty(customsImport)) {
				customsImport = u;
			} else {
				customsImport = customsImport + ", " + u;
			}
		}
		List<String> portExport = AgreementUtil.getAgreementTermTextValue(delegator, agreement.getString("agreementId"), "PORT_EXPORT");
		for (String u : portExport) {
			if (UtilValidate.isEmpty(CustomsExport)) {
				CustomsExport = u;
			} else {
				CustomsExport = CustomsExport + ", " + u;
			}
		}
		String importer = customer.getString("fullName");
		String transportation = "Tàu biển";
		String locationQuarantine = customsImport;
		
		String quantity = totalQuantity.toString();
		
		String netWeightTotal = packingList.getBigDecimal("netWeightTotal").toString();
		String grossWeightTotal = packingList.getBigDecimal("grossWeightTotal").toString();
		
		String uomId = packing;
		String agreementName = agreement.getString("agreementCode");
		String quarantineNumber = objProductEvent.getString("eventCode");

		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = createStyles(wb);
		Sheet sheet = wb.createSheet("sheet1");

		Font normal10 = wb.createFont();
		normal10.setFontHeightInPoints((short) 10);

		Font italic10 = wb.createFont();
		italic10.setFontHeightInPoints((short) 10);
		italic10.setItalic(true);

		CellStyle csWrapText = wb.createCellStyle();
		csWrapText.setWrapText(true);

		sheet.setColumnWidth(0, 10 * 256);
		sheet.setColumnWidth(1, 10 * 256);
		sheet.setColumnWidth(2, 10 * 256);
		sheet.setColumnWidth(3, 10 * 256);
		sheet.setColumnWidth(4, 10 * 256);
		sheet.setColumnWidth(5, 10 * 256);
		sheet.setColumnWidth(6, 10 * 256);
		sheet.setColumnWidth(7, 35 * 256);
		// turn on gridlines
		sheet.setDisplayGridlines(true);
		sheet.setPrintGridlines(false);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);

		sheet.setAutobreaks(true);
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1);

		int rownum = 0;

		Row row_0 = sheet.createRow(rownum);
		row_0.setHeight((short) 380);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_00 = row_0.createCell(0);
		cell_00.setCellValue("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM".toUpperCase());
		cell_00.setCellStyle(styles.get("cell_centered_no_border_11"));
		rownum += 1;

		Row row_1 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_10 = row_1.createCell(0);
		cell_10.setCellValue("ĐỘC LẬP TỰ DO HẠNH PHÚC");
		cell_10.setCellStyle(styles.get("cell_normal_centered_10"));
		rownum += 1;

		Row row_2 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_20 = row_2.createCell(0);
		cell_20.setCellValue("============");
		cell_20.setCellStyle(styles.get("cell_normal_centered_10"));
		rownum += 1;

		Row row_3 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_30 = row_3.createCell(0);
		cell_30.setCellValue("Phụ Lục");
		cell_30.setCellStyle(styles.get("cell_normal_right_10"));
		rownum += 1;

		Row row_4 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_40 = row_4.createCell(0);
		cell_40.setCellValue("Hà Nội, Ngày     Tháng       năm       ");
		cell_40.setCellStyle(styles.get("cell_italic_right_no_border_11"));
		rownum += 1;

		Row row_5 = sheet.createRow(rownum);
		row_5.setHeight((short) 380);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_50 = row_5.createCell(0);
		cell_50.setCellValue("GIẤY ĐĂNG KÝ KIỂM DỊCH(*)".toUpperCase());
		cell_50.setCellStyle(styles.get("cell_bold_centered_no_border_11"));
		rownum += 1;

		Row row_6 = sheet.createRow(rownum);
		row_6.setHeight((short) 350);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 7));
		Cell cell_60 = row_6.createCell(0);
		cell_60.setCellValue("Kính gửi:");
		cell_60.setCellStyle(styles.get("cell_bold_right_underline_italic_10"));
		Cell cell_61 = row_6.createCell(3);
		cell_61.setCellValue("  Cơ quan Thú y vùng VI(**)");
		cell_61.setCellStyle(styles.get("cell_bold_left_10"));
		rownum += 1;

		Row row_7 = sheet.createRow(rownum);
		row_7.setHeight((short) 350);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 7));
		Cell cell_70 = row_7.createCell(0);
		cell_70.setCellValue("Tên tổ chức, cá nhân đăng ký:");
		cell_70.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_71 = row_7.createCell(3);
		cell_71.setCellValue(customer.getString("fullName"));
		cell_71.setCellStyle(styles.get("cell_bold_left_10"));
		rownum += 1;

		Row row_8 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_80 = row_8.createCell(0);
		cell_80.setCellValue("Địa chỉ: " + AgreementUtil.getAgreementPartyAddress(delegator, agreementId, partyIdFrom, "PRIMARY_LOCATION"));
		cell_80.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_9 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
		Cell cell_90 = row_9.createCell(0);
		cell_90.setCellValue("Điện thoại: 04 35558118");
		cell_90.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_91 = row_9.createCell(2);
		cell_91.setCellValue("Fax: 04 35558119");
		cell_91.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_92 = row_9.createCell(4);
		cell_92.setCellValue("Email: olbius@info.com.vn");
		cell_92.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_10 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_100 = row_10.createCell(0);
		cell_100.setCellValue("Đề nghị quý Cơ quan kiểm tra lô hàng(***): Nhập khẩu");
		cell_100.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_11 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_110 = row_11.createCell(0);
		cell_110.setCellValue("1.");
		cell_110.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_111 = row_11.createCell(1);
		cell_111.setCellValue("Tên hàng: (Chi tiết theo danh mục sản phẩm đính kèm).");
		cell_111.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_12 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_120 = row_12.createCell(0);
		cell_120.setCellValue("2.");
		cell_120.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_121 = row_12.createCell(1);
		HSSFRichTextString rStr12 = new HSSFRichTextString("Nơi sản xuấ: " + placeOfManufacture);
		rStr12.applyFont(0, 13, normal10);
		rStr12.applyFont(13, rStr12.length(), italic10);
		cell_121.setCellValue(rStr12);
		cell_121.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_13 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_130 = row_13.createCell(0);
		cell_130.setCellValue("3.");
		cell_130.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_131 = row_13.createCell(1);
		HSSFRichTextString rStr13 = new HSSFRichTextString("Số lượng:  " + quantity + "  " + uomId);
		rStr13.applyFont(0, 9, normal10);
		rStr13.applyFont(9, rStr13.length(), italic10);
		cell_131.setCellValue(rStr13);
		cell_131.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_14 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_140 = row_14.createCell(0);
		cell_140.setCellValue("4.");
		cell_140.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_141 = row_14.createCell(1);
		HSSFRichTextString rStr14 = new HSSFRichTextString("Kích cỡ cá thể (đối với hàng hoá là thuỷ sản):");
		rStr14.applyFont(0, 14, normal10);
		rStr14.applyFont(14, rStr14.length(), italic10);
		cell_141.setCellValue(rStr14);
		cell_141.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_15 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_150 = row_15.createCell(0);
		cell_150.setCellValue("5.");
		cell_150.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_151 = row_15.createCell(1);
		HSSFRichTextString rStr15 = new HSSFRichTextString("Trọng lượng tịnh: " + netWeightTotal + " Kg");
		rStr15.applyFont(0, 17, normal10);
		rStr15.applyFont(17, rStr15.length(), italic10);
		cell_151.setCellValue(rStr15);
		cell_151.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_16 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_160 = row_16.createCell(0);
		cell_160.setCellValue("6.");
		cell_160.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_161 = row_16.createCell(1);
		HSSFRichTextString rStr16 = new HSSFRichTextString("Trọng lượng cả bì: " + grossWeightTotal + " Kg");
		rStr16.applyFont(0, 18, normal10);
		rStr16.applyFont(18, rStr16.length(), italic10);
		cell_161.setCellValue(rStr16);
		cell_161.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_17 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_170 = row_17.createCell(0);
		cell_170.setCellValue("7.");
		cell_170.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_171 = row_17.createCell(1);
		HSSFRichTextString rStr17 = new HSSFRichTextString("Loại bao bì: nhựa bao phim");
		rStr17.applyFont(0, 12, normal10);
		rStr17.applyFont(12, rStr17.length(), italic10);
		cell_171.setCellValue(rStr17);
		cell_171.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_18 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_180 = row_18.createCell(0);
		cell_180.setCellValue("8.");
		cell_180.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_181 = row_18.createCell(1);
		HSSFRichTextString rStr18 = new HSSFRichTextString("Số hợp đồng hoặc số chứng từ thanh toán: " + agreementName);
		rStr18.applyFont(0, 40, normal10);
		rStr18.applyFont(40, rStr18.length(), italic10);
		cell_181.setCellValue(rStr18);
		cell_181.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_19 = sheet.createRow(rownum);
		row_19.setHeight((short) 500);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_190 = row_19.createCell(0);
		cell_190.setCellValue("9.");
		cell_190.setCellStyle(styles.get("cell_normal_centered_top10"));
		Cell cell_191 = row_19.createCell(1);
		HSSFRichTextString rStr19 = new HSSFRichTextString("Tổ chức, cá nhân xuất khẩu: " + exporter);
		rStr19.applyFont(0, 27, normal10);
		rStr19.applyFont(27, rStr19.length(), italic10);
		cell_191.setCellValue(rStr19);
		cell_191.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_10"));
		rownum += 1;

		Row row_20 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_200 = row_20.createCell(0);
		cell_200.setCellValue("10.");
		cell_200.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_201 = row_20.createCell(1);
		HSSFRichTextString rStr20 = new HSSFRichTextString("Nước xuất khẩu: " + countryExport);
		rStr20.applyFont(0, 15, normal10);
		rStr20.applyFont(15, rStr20.length(), italic10);
		cell_201.setCellValue(rStr20);
		cell_201.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_21 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_210 = row_21.createCell(0);
		cell_210.setCellValue("11.");
		cell_210.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_211 = row_21.createCell(1);
		HSSFRichTextString rStr21 = new HSSFRichTextString("Cửa khẩu xuất: " + CustomsExport);
		rStr21.applyFont(0, 14, normal10);
		rStr21.applyFont(14, rStr21.length(), italic10);
		cell_211.setCellValue(rStr21);
		cell_211.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_22 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_220 = row_22.createCell(0);
		cell_220.setCellValue("12.");
		cell_220.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_221 = row_22.createCell(1);
		HSSFRichTextString rStr22 = new HSSFRichTextString("Tổ chức, cá nhân nhập khẩu: " + importer);
		rStr22.applyFont(0, 27, normal10);
		rStr22.applyFont(27, rStr22.length(), italic10);
		cell_221.setCellValue(rStr22);
		cell_221.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_23 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_230 = row_23.createCell(0);
		cell_230.setCellValue("13.");
		cell_230.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_231 = row_23.createCell(1);
		HSSFRichTextString rStr23 = new HSSFRichTextString("Nước nhập khẩu: Việt Nam");
		rStr23.applyFont(0, 15, normal10);
		rStr23.applyFont(15, rStr23.length(), italic10);
		cell_231.setCellValue(rStr23);
		cell_231.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_24 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_240 = row_24.createCell(0);
		cell_240.setCellValue("14.");
		cell_240.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_241 = row_24.createCell(1);
		HSSFRichTextString rStr24 = new HSSFRichTextString("Phương tiện vận chuyển: " + transportation);
		rStr24.applyFont(0, 24, normal10);
		rStr24.applyFont(24, rStr24.length(), italic10);
		cell_241.setCellValue(rStr24);
		cell_241.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_25 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_250 = row_25.createCell(0);
		cell_250.setCellValue("15.");
		cell_250.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_251 = row_25.createCell(1);
		HSSFRichTextString rStr25 = new HSSFRichTextString("Cửa khẩu nhập: " + customsImport);
		rStr25.applyFont(0, 14, normal10);
		rStr25.applyFont(14, rStr25.length(), italic10);
		cell_251.setCellValue(rStr25);
		cell_251.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_26 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_260 = row_26.createCell(0);
		cell_260.setCellValue("16.");
		cell_260.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_261 = row_26.createCell(1);
		HSSFRichTextString rStr26 = new HSSFRichTextString("Mục đích sử dụng: Kinh doanh");
		rStr26.applyFont(0, 17, normal10);
		rStr26.applyFont(17, rStr26.length(), italic10);
		cell_261.setCellValue(rStr26);
		cell_261.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_27 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_270 = row_27.createCell(0);
		cell_270.setCellValue("17.");
		cell_270.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_271 = row_27.createCell(1);
		HSSFRichTextString rStr27 = new HSSFRichTextString(
				"Giấy phép cho kiểm dịch nhập khẩu: Số: " + quarantineNumber);
		rStr27.applyFont(0, 35, normal10);
		rStr27.applyFont(35, rStr27.length(), italic10);
		cell_271.setCellValue(rStr27);
		cell_271.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_28 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_280 = row_28.createCell(0);
		cell_280.setCellValue("18.");
		cell_280.setCellStyle(styles.get("cell_normal_centered_top10"));
		Cell cell_281 = row_28.createCell(1);
		HSSFRichTextString rStr28 = new HSSFRichTextString("Giấy chứng nhận kiểm dịch của nước xuất hàng: Số:  ");
		rStr28.applyFont(0, rStr28.length(), normal10);
		cell_281.setCellValue(rStr28);
		cell_281.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_10"));
		rownum += 1;

		Row row_29 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_290 = row_29.createCell(0);
		cell_290.setCellValue("19.");
		cell_290.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_291 = row_29.createCell(1);
		cell_291.setCellValue("Địa điểm kiểm dịch: " + locationQuarantine);
		cell_291.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_30 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_300 = row_30.createCell(0);
		cell_300.setCellValue("20.");
		cell_300.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_301 = row_30.createCell(1);
		cell_301.setCellValue("Địa điểm nuôi trồng (nếu có)");
		cell_301.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_31 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_310 = row_31.createCell(0);
		cell_310.setCellValue("21.");
		cell_310.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_311 = row_31.createCell(1);
		HSSFRichTextString rStr31 = new HSSFRichTextString("Thời gian kiểm dịch: ");
		rStr31.applyFont(0, rStr31.length(), normal10);
		cell_311.setCellValue(rStr31);
		cell_311.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_32 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_320 = row_32.createCell(0);
		cell_320.setCellValue("22.");
		cell_320.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_321 = row_32.createCell(1);
		cell_321.setCellValue("Địa điểm giám sát: ");
		cell_321.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_33 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_330 = row_33.createCell(0);
		cell_330.setCellValue("23.");
		cell_330.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_331 = row_33.createCell(1);
		cell_331.setCellValue("Thời gian giám sát:");
		cell_331.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_34 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_340 = row_34.createCell(0);
		cell_340.setCellValue("24.");
		cell_340.setCellStyle(styles.get("cell_normal_centered_10"));
		Cell cell_341 = row_34.createCell(1);
		HSSFRichTextString rStr34 = new HSSFRichTextString("Số bản giấy chứng nhận kiểm dịch cần cấp: 03 bản");
		rStr34.applyFont(0, 42, normal10);
		rStr34.applyFont(42, rStr34.length(), italic10);
		cell_341.setCellValue(rStr34);
		cell_341.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_35 = sheet.createRow(rownum);
		row_35.setHeight((short) 500);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_350 = row_35.createCell(0);
		cell_350.setCellValue(
				"Chúng tôi xin cam kết: Đảm bảo nguyên trạng hàng hoá nhập khẩu, đưa về đúng địa điểm, đúng thời gian được đăng ký và chỉ đưa hàng hoá ra lưu thông sau khi được quý cơ quan cấp giấy chứng nhận kiểm dịch (****).");
		cell_350.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_10"));
		rownum += 1;

		Row row_36 = sheet.createRow(rownum);
		row_36.setHeight((short) 500);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
		Cell cell_360 = row_36.createCell(5);
		cell_360.setCellValue("CÔNG TY CỔ PHẦN ĐẦU TƯ \n OLBIUS");
		cell_360.setCellStyle(styles.get("cell_normal_centered_wrap_text_10"));
		rownum += 5;

		Row row_37 = sheet.createRow(rownum);
		row_37.setHeight((short) 380);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_370 = row_37.createCell(0);
		cell_370.setCellValue("XÁC NHẬN CỦA CƠ QUAN KIỂM DỊCH ĐỘNG VẬT".toUpperCase());
		cell_370.setCellStyle(styles.get("cell_bold_centered_no_border_11"));
		rownum += 2;

		Row row_38 = sheet.createRow(rownum);
		row_38.setHeight((short) 600);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_380 = row_38.createCell(0);
		cell_380.setCellValue(
				"Đồng ý vận chuyển đưa hàng hoá về địa điểm .....................................................................................");
		cell_380.setCellStyle(styles.get("cell_normal_left_top_10"));
		rownum += 1;

		Row row_39 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_390 = row_39.createCell(0);
		cell_390.setCellValue("Để làm thủ tục kiểm dịch vào hồi ......., Ngày ....... Tháng ........ Năm 201...");
		cell_390.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_40 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
		Cell cell_400 = row_40.createCell(4);
		cell_400.setCellValue("Vào sổ số:...................., Ngày ....... Tháng ........ Năm 201..");
		cell_400.setCellStyle(styles.get("cell_normal_centered_wrap_text_10"));
		rownum += 1;

		Row row_41 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
		Cell cell_410 = row_41.createCell(4);
		cell_410.setCellValue("....................Cơ quan thú y vùng VI ........................(**)");
		cell_410.setCellStyle(styles.get("cell_normal_centered_wrap_text_10"));
		rownum += 1;

		Row row_42 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
		Cell cell_420 = row_42.createCell(4);
		HSSFRichTextString rStr42 = new HSSFRichTextString("(ký, đóng dấu, ghi rõ họ tên)");
		rStr42.applyFont(italic10);
		cell_420.setCellValue(rStr42);
		cell_420.setCellStyle(styles.get("cell_normal_centered_wrap_text_10"));
		rownum += 1;

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String date = dateFormat.format(System.currentTimeMillis());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "GiayDangKyKiemDich_" + date + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if (baos != null)
				baos.close();
		}
	}

	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		DataFormat df = wb.createDataFormat();

		CellStyle style;

		Font boldCenterNoBorderFont16 = wb.createFont();
		boldCenterNoBorderFont16.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont16.setFontHeightInPoints((short) 16);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont16);
		styles.put("cell_bold_centered_no_border_16", style);

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
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		styles.put("cell_normal_centered_border_full_10", style);

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

		return styles;
	}
	
	private static CellStyle createNonBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
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

}
