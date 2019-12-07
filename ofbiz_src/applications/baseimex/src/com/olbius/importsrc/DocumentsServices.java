package com.olbius.importsrc;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
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
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.product.util.ProductUtil;

import javolution.util.FastList;

public class DocumentsServices {

	public static String module = DocumentsServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String euroSign = new String("\u20AC");
	public static final String leftDoubleQuotationMark = new String("\u201C");
	public static final String rightDoubleQuotationMark = new String("\u201D");
	public static final String draftingPointRightWardArrow = new String("\u279B");
	public static final String ballotBox = new String("\u2610");
	public static final String IMEX_PROPERTIES = "imex.properties";

	@SuppressWarnings("unchecked")
	public static void exportAgreementToQuarantineToExcel(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String billId = request.getParameter("billId");
		String containerId = request.getParameter("containerId");
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("userLogin", userLogin);
		if (UtilValidate.isEmpty(containerId)) {
			context.put("billId", billId);
		} else {
			context.put("containerId", containerId);
		}
		Map<String, Object> results = null;
		try {
			results = dispatcher.runSync("getInfomationAgreementsInBill", context);
		} catch (GenericServiceException e) {
			response.getWriter().print("GenericServiceException");
		}
		if (UtilValidate.isEmpty(results)) {
			response.getWriter().print("mapInfomationAgreement null");
			response.flushBuffer();
			return;
		}
		Map<String, Object> mapInfomationAgreements = (Map<String, Object>) results.get("mapInfomationAgreements");
		Locale locale = (Locale) results.get("locale");
		String currencyUomId = (String) mapInfomationAgreements.get("currencyUomId");
		String thisSignCurrency = euroSign;
		if (currencyUomId.equals("USD")) {
			thisSignCurrency = "$";
		}
		String sheetName = "Sheet1";
		String fileName = "AgreementToQuarantine.xls";
		List<String> title = new FastList<String>();
		title.add("No.");
		title.add("Description");
		title.add("Unit");
		title.add("Quantity");
		title.add("Unit Price \n " + "(" + thisSignCurrency + ")");
		title.add("Good value \n " + "(" + thisSignCurrency + ")");
		title.add("Remark");
		Workbook wbProduct = renderAgreementsToQuarantineInBill(sheetName, fileName, title, mapInfomationAgreements,
				delegator, locale);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wbProduct.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + fileName);
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if (baos != null)
				baos.close();
		}
	}

	@SuppressWarnings("unchecked")
	private static Workbook renderAgreementsToQuarantineInBill(String sheetName, String fileName, List<String> titles,
			Map<String, Object> data, GenericDelegator delegator, Locale locale)
			throws IOException, GenericEntityException {
		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = createStyles(wb);
		Sheet sheet = wb.createSheet(sheetName);

		CellStyle csWrapText = wb.createCellStyle();
		csWrapText.setWrapText(true);

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

		sheet.setColumnWidth(0, 21 * 256);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 11 * 256);
		sheet.setColumnWidth(3, 11 * 256);
		sheet.setColumnWidth(4, 11 * 256);
		sheet.setColumnWidth(5, 15 * 256);
		sheet.setColumnWidth(6, 11 * 256);
		sheet.setColumnWidth(7, 11 * 256);

		Row imgHead = sheet.createRow(0);
		imgHead.createCell(0);
		int rownum = 0;
		// FileInputStream is = null;
		// try {
		// String imageServerPath =
		// FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("delys",
		// "image.management.logoPath"), null);
		// File file = new File(imageServerPath);
		// is = new FileInputStream(file);
		// byte[] bytesImg = IOUtils.toByteArray(is);
		// int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
		// CreationHelper helper = wb.getCreationHelper();
		// Drawing drawing = sheet.createDrawingPatriarch();
		// ClientAnchor anchor = helper.createClientAnchor();
		// anchor.setCol1(0);
		// anchor.setCol2(8);
		// anchor.setRow1(0);
		// anchor.setRow2(4);
		// Picture pict = drawing.createPicture(anchor, pictureIdx);
		// pict.getPictureData();
		// rownum = 5;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }finally{
		// if(is!=null)is.close();
		// }
		Row titleRow = sheet.createRow(rownum);
		titleRow.setHeight((short) 380);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("CONTRACT".toUpperCase());
		titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		Row agreementNameRow = sheet.createRow(rownum);
		agreementNameRow.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell agreementNameCell = agreementNameRow.createCell(0);
		agreementNameCell.setCellValue("No. ");
		agreementNameCell.setCellStyle(styles.get("cell_centered_no_border_11"));
		rownum += 1;

		Row purcharseDateRow = sheet.createRow(rownum);
		purcharseDateRow.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell purcharseDateCell = purcharseDateRow.createCell(0);
		purcharseDateCell.setCellValue("This purchase order is made on ");
		purcharseDateCell.setCellStyle(styles.get("cell_centered_no_border_11"));
		rownum += 1;

		Row betweenRow = sheet.createRow(rownum);
		betweenRow.setHeight((short) 300);
		Cell betweenCell = betweenRow.createCell(0);
		betweenCell.setCellValue("Between");
		betweenCell.setCellStyle(styles.get("cell_italic_bold_left_no_border_11"));
		rownum += 1;

		GenericValue agreement = (GenericValue) data.get("agreement");
		Row delysCompanyRow = sheet.createRow(rownum);
		delysCompanyRow.setHeight((short) 300);
		Cell delysCompanyCell = delysCompanyRow.createCell(0);
		String partyIdFrom = (String) agreement.get("partyIdFrom");
		GenericValue purchaser = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyIdFrom), false);
		String purchaserName = "";
		if (UtilValidate.isNotEmpty(purchaser))
			purchaserName = (String) purchaser.get("groupName", locale);
		delysCompanyCell.setCellValue(purchaserName);
		delysCompanyCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row delysCompanyAddressRow = sheet.createRow(rownum);
		delysCompanyAddressRow.setHeight((short) 300);
		Cell delysCompanyAddressCell = delysCompanyAddressRow.createCell(0);
		String contactMechAddressFromId = (String) data.get("contactMechAddressFromId");
		GenericValue addressFrom = delegator.findOne("PostalAddress",
				UtilMisc.toMap("contactMechId", contactMechAddressFromId), false);
		String strAddressFrom = "";
		if (UtilValidate.isNotEmpty(addressFrom))
			strAddressFrom = (String) addressFrom.get("address1", locale);
		delysCompanyAddressCell.setCellValue("Address: " + strAddressFrom);
		delysCompanyAddressCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row delysCompanyTelAndFaxRow = sheet.createRow(rownum);
		delysCompanyTelAndFaxRow.setHeight((short) 300);
		Cell delysCompanyTelCell = delysCompanyTelAndFaxRow.createCell(0);
		String contactMechPhoneFromId = (String) data.get("contactMechPhoneFromId");
		String contactMechFaxFromId = (String) data.get("contactMechFaxFromId");
		GenericValue phoneFrom = delegator.findOne("TelecomNumber",
				UtilMisc.toMap("contactMechId", contactMechPhoneFromId), false);
		GenericValue faxFrom = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechFaxFromId),
				false);
		String phoneContactNumber = "";
		String faxContactNumber = "";
		if (UtilValidate.isNotEmpty(phoneFrom))
			phoneContactNumber = (String) phoneFrom.get("contactNumber");
		if (UtilValidate.isNotEmpty(faxFrom))
			faxContactNumber = (String) faxFrom.get("contactNumber");
		delysCompanyTelCell.setCellValue("TEL: " + phoneContactNumber);
		delysCompanyTelCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		Cell delysCompanyFaxCell = delysCompanyTelAndFaxRow.createCell(5);
		delysCompanyFaxCell.setCellValue("FAX: " + faxContactNumber);
		delysCompanyFaxCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row delysCompanyBankRow = sheet.createRow(rownum);
		delysCompanyTelAndFaxRow.setHeight((short) 300);
		Cell delysCompanyBankNameCell = delysCompanyBankRow.createCell(0);
		delysCompanyBankNameCell.setCellValue("");
		delysCompanyBankNameCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		Cell delysCompanyBankAccCell = delysCompanyBankRow.createCell(5);
		List<GenericValue> listFinAccountFroms = (List<GenericValue>) data.get("listFinAccountFroms");
		String accountCode = "";
		if (UtilValidate.isNotEmpty(listFinAccountFroms)) {
			accountCode = (String) listFinAccountFroms.get(0).get("finAccountCode");
		}
		delysCompanyBankAccCell.setCellValue("Account: " + accountCode);
		delysCompanyBankAccCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.createRow(rownum);
		rownum += 1;

		Row representedByRow = sheet.createRow(rownum);
		representedByRow.setHeight((short) 300);
		Cell representedByCell = representedByRow.createCell(0);
		String representPartyId = (String) data.get("representPartyId");
		GenericValue representParty = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", representPartyId),
				false);
		StringBuilder representPartyName = new StringBuilder();
		if (UtilValidate.isNotEmpty(representParty)) {
			if (UtilValidate.isNotEmpty(representParty.get("firstName")))
				representPartyName.append(representParty.get("firstName") + " ");
			if (UtilValidate.isNotEmpty(representParty.get("middleName")))
				representPartyName.append(representParty.get("middleName") + " ");
			if (UtilValidate.isNotEmpty(representParty.get("lastName")))
				representPartyName.append(representParty.get("lastName") + " ");
			if (UtilValidate.isNotEmpty(representParty.get("groupName")))
				representPartyName.append(representParty.get("groupName"));
			if (UtilValidate.isNotEmpty(representParty.get("partyId")))
				representPartyName.append("-" + representParty.get("partyId"));
		}
		representedByCell.setCellValue("Represented by " + representPartyName);
		representedByCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row calledDelysRow = sheet.createRow(rownum);
		calledDelysRow.setHeight((short) 300);
		Cell calledDelysCell = calledDelysRow.createCell(0);
		calledDelysCell.setCellValue(
				"Here in after called " + leftDoubleQuotationMark + "The Purchaser" + rightDoubleQuotationMark);
		calledDelysCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row andRow = sheet.createRow(rownum);
		andRow.setHeight((short) 300);
		Cell andCell = andRow.createCell(0);
		andCell.setCellValue("And");
		andCell.setCellStyle(styles.get("cell_italic_bold_left_no_border_11"));
		rownum += 1;

		Row zottCompanyRow = sheet.createRow(rownum);
		zottCompanyRow.setHeight((short) 300);
		Cell zottCompanyCell = zottCompanyRow.createCell(0);
		String partyIdTo = (String) agreement.get("partyIdTo");
		GenericValue supplier = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", partyIdTo), false);
		String supplierName = "";
		if (UtilValidate.isNotEmpty(supplier))
			supplierName = (String) supplier.get("groupName", locale);
		zottCompanyCell.setCellValue(supplierName);
		zottCompanyCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row zottCompanyAddressRow = sheet.createRow(rownum);
		zottCompanyAddressRow.setHeight((short) 300);
		Cell zottCompanyAddressCell = zottCompanyAddressRow.createCell(0);
		String contactMechAddressToId = (String) data.get("contactMechAddressToId");
		GenericValue addressTo = new GenericValue();
		addressTo = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechAddressToId), false);
		String strAddressTo = "";
		if (UtilValidate.isNotEmpty(addressTo))
			strAddressTo = (String) addressTo.get("address1");
		zottCompanyAddressCell.setCellValue("Address: " + strAddressTo);
		zottCompanyAddressCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row calledZottRow = sheet.createRow(rownum);
		calledZottRow.setHeight((short) 300);
		Cell calledZottCell = calledZottRow.createCell(0);
		calledZottCell.setCellValue(
				"Here in after called " + leftDoubleQuotationMark + "The Supplier" + rightDoubleQuotationMark);
		calledZottCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.createRow(rownum);
		rownum += 1;

		Row contractDetailsRow = sheet.createRow(rownum);
		contractDetailsRow.setHeight((short) 300);
		Cell contractDetailCell = contractDetailsRow.createCell(0);
		contractDetailCell.setCellValue("1. CONTRACT DETAIL");
		contractDetailCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row contractContainRow = sheet.createRow(rownum);
		contractContainRow.setHeight((short) 300);
		Cell contractContainCell = contractContainRow.createCell(0);
		contractContainCell.setCellValue(
				"                  This contract contains the other one: " + data.get("listAgreementName"));
		contractContainCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row contractValueRow = sheet.createRow(rownum);
		contractValueRow.setHeight((short) 300);
		Cell contractValueCell = contractValueRow.createCell(0);
		contractValueCell.setCellValue("2. CONTRACT VALUE");
		contractValueCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row totalPurchaseRow = sheet.createRow(rownum);
		totalPurchaseRow.setHeight((short) 300);
		Cell totalPurchaseCell = totalPurchaseRow.createCell(0);
		totalPurchaseCell.setCellValue("2.1.The total purchase order price is ");
		totalPurchaseCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell totalPurchaseValueCell = totalPurchaseRow.createCell(4);

		totalPurchaseValueCell.setCellStyle(styles.get("cell_bold_right_no_border_10"));
		Cell totalPurchaseValueUnitCell = totalPurchaseRow.createCell(5);
		totalPurchaseValueUnitCell.setCellValue((String) data.get("currencyUomId"));
		totalPurchaseValueUnitCell.setCellStyle(styles.get("cell_bold_left_no_border_10"));
		rownum += 1;

		Row inWordRow = sheet.createRow(rownum);
		inWordRow.setHeight((short) 300);
		Cell inWordCell = inWordRow.createCell(0);
		inWordCell.setCellValue("In words:");
		inWordCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
		Cell nameCell = inWordRow.createCell(5);
		nameCell.setCellValue("");
		nameCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row otherNoteRow = sheet.createRow(rownum);
		otherNoteRow.setHeight((short) 300);
		Cell otherNoteCell = otherNoteRow.createCell(0);
		otherNoteCell.setCellValue("EUROS eighty five thousand, three hundered and twenty and cents fifty four only");
		otherNoteCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row breakdownAmountRow = sheet.createRow(rownum);
		breakdownAmountRow.setHeight((short) 300);
		Cell breakdownAmountCell = breakdownAmountRow.createCell(0);
		breakdownAmountCell.setCellValue("2.2 The breakdown of this amount is as follows:");
		breakdownAmountCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row headerBreakdownAmountRow = sheet.createRow(rownum);
		headerBreakdownAmountRow.setHeight((short) 500);
		for (int i = 0; i < titles.size(); i++) {
			Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
			if (titles.get(i).equals("Remark")) {
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 7));
				Cell headerBreakdownAmount2Cell = headerBreakdownAmountRow.createCell(i + 1);
				headerBreakdownAmount2Cell
						.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_right_10"));
			}
			headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			headerBreakdownAmountCell.setCellValue(titles.get(i));
		}
		rownum += 1;
		BigDecimal palet = BigDecimal.ZERO;
		float totalGoodValue = 0;
		float totalPallets = 0;
		String uomUnit = "";
		GenericValue uom = new GenericValue();
		List<GenericValue> listProducts = (List<GenericValue>) data.get("listOrderItemTotalInBill");
		for (GenericValue x : listProducts) {
			Row productDetailRow = sheet.createRow(rownum);
			Cell productIdCell = productDetailRow.createCell(0);
			String productId = x.getString("productId");
			productIdCell.setCellValue(productId);
			productIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			String description = x.getString("description");
			Cell productDescriptionCell = productDetailRow.createCell(1);
			productDescriptionCell.setCellValue(description);
			productDescriptionCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			String quantityUomId = x.getString("purchaseUomId");
			
			String productPackingUomId = UtilProperties.getPropertyValue(IMEX_PROPERTIES, "imex.import.packing.uom.pallet");
			
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
			palet = ProductUtil.getConvertPackingNumber(delegator, productId, productPackingUomId, quantityUomId); 
			if (UtilValidate.isNotEmpty(uom))
				uomUnit = (String) uom.get("description");

			Cell productQuantityUnitCell = productDetailRow.createCell(2);
			productQuantityUnitCell.setCellValue(uomUnit);
			productQuantityUnitCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			Cell productQuantityCell = productDetailRow.createCell(3);
			BigDecimal productQuantity = (BigDecimal) x.get("quantity");
			productQuantityCell.setCellValue(productQuantity.floatValue());
			productQuantityCell.setCellStyle(styles.get("cell_normal_right_boder_full_10"));

			Cell productPriceCell = productDetailRow.createCell(4);
			BigDecimal productPrice = (BigDecimal) x.get("unitPrice");
			if (UtilValidate.isNotEmpty(productPrice)) {
				productPriceCell.setCellValue(productPrice.toString());
			}
			productPriceCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			Cell goodValueCell = productDetailRow.createCell(5);
			float goodValue = productQuantity.floatValue() * productPrice.floatValue();
			totalGoodValue += goodValue;
			goodValueCell.setCellValue(goodValue);
			goodValueCell.setCellStyle(styles.get("cell_normal_right_boder_full_10"));

			Cell palletCell = productDetailRow.createCell(6);
			float pallet = productQuantity.floatValue() / palet.floatValue();
			totalPallets += pallet;
			palletCell.setCellValue(pallet);
			palletCell.setCellStyle(styles.get("cell_normal_right_boder_no_right_10"));

			Cell palletsCell = productDetailRow.createCell(7);
			palletsCell.setCellValue("KAR");
			palletsCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_right_10"));

			rownum += 1;
		}
		Row totalRow = sheet.createRow(rownum);
		Cell totalCell = totalRow.createCell(1);
		totalCell.setCellValue("TOTAL");
		totalCell.setCellStyle(styles.get("cell_bold_centered_wrap_text_border_bottom_10"));

		Cell blankCell0 = totalRow.createCell(0);
		blankCell0.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_bottom_left_10"));

		Cell blankCell2 = totalRow.createCell(2);
		blankCell2.setCellStyle(styles.get("cell_bold_centered_wrap_text_border_bottom_10"));
		Cell blankCell3 = totalRow.createCell(3);
		blankCell3.setCellStyle(styles.get("cell_bold_right_wrap_text_border_bottom_10"));
		blankCell3.setCellStyle(styles.get("cell_bold_centered_wrap_text_border_bottom_10"));
		Cell blankCell4 = totalRow.createCell(4);
		blankCell4.setCellStyle(styles.get("cell_bold_centered_wrap_text_border_bottom_10"));

		Cell totalGoodValueCell = totalRow.createCell(5);
		totalGoodValueCell.setCellValue(totalGoodValue);
		totalGoodValueCell.setCellStyle(styles.get("cell_bold_right_wrap_text_border_bottom_10"));

		Cell totalPalletCell = totalRow.createCell(6);
		totalPalletCell.setCellValue(totalPallets);
		totalPalletCell.setCellStyle(styles.get("cell_bold_right_wrap_text_border_bottom_no_right_10"));

		Cell palletCell = totalRow.createCell(7);
		palletCell.setCellValue("KAR");
		palletCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_bottom_right_10"));
		totalPurchaseValueCell.setCellValue(totalGoodValue);
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row ratioBetweenRow = sheet.createRow(rownum);
		ratioBetweenRow.setHeight((short) 300);
		Cell ratioBetweenCell = ratioBetweenRow.createCell(0);
		ratioBetweenCell.setCellValue("2.3     All the payment could be combine ratio between ");
		ratioBetweenCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row termsOfDeliveryRow = sheet.createRow(rownum);
		termsOfDeliveryRow.setHeight((short) 300);
		Cell termsOfDeliveryCell = termsOfDeliveryRow.createCell(0);
		termsOfDeliveryCell.setCellValue("3.     TERMS OF DELIVERY");
		termsOfDeliveryCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row dateOfShipmentRow = sheet.createRow(rownum);
		dateOfShipmentRow.setHeight((short) 300);
		Cell dateOfShipmentCell = dateOfShipmentRow.createCell(0);
		dateOfShipmentCell.setCellValue("3.1     The date of shipment: ");
		dateOfShipmentCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row dateOfShipment1Row = sheet.createRow(rownum);
		dateOfShipment1Row.setHeight((short) 300);
		Cell dateOfShipment1Cell = dateOfShipment1Row.createCell(0);
		dateOfShipment1Cell.setCellValue("     ETD:");
		dateOfShipment1Cell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell dateOfShipment1ValueCell = dateOfShipment1Row.createCell(1);
		String currentETDTerm = (String) data.get("currentETDTerm");
		GenericValue etd = new GenericValue();
		etd = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", currentETDTerm), false);
		Date currentETD = null;
		if (UtilValidate.isNotEmpty(etd))
			currentETD = (Date) etd.get("fromDate");
		if (UtilValidate.isNotEmpty(currentETD)) {
			dateOfShipment1ValueCell.setCellValue(currentETD.toString());
		}
		dateOfShipment1ValueCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row dateOfShipment2Row = sheet.createRow(rownum);
		dateOfShipment2Row.setHeight((short) 300);
		Cell dateOfShipment2Cell = dateOfShipment2Row.createCell(0);
		dateOfShipment2Cell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell dateOfShipment2ValueCell = dateOfShipment2Row.createCell(1);
		String currentPortTerm = (String) data.get("currentPortTerm");
		GenericValue eta = new GenericValue();
		GenericValue port = new GenericValue();
		eta = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", currentETDTerm), false);
		port = delegator.findOne("Facility", UtilMisc.toMap("facilityId", currentPortTerm), false);
		Date currentETA = null;
		String facilityName = "";
		if (UtilValidate.isNotEmpty(eta))
			currentETA = (Date) eta.get("fromDate");
		if (UtilValidate.isNotEmpty(port))
			facilityName = (String) port.get("facilityName");
		if (UtilValidate.isNotEmpty(currentETA)) {
			dateOfShipment2ValueCell.setCellValue(currentETA.toString());
		}
		dateOfShipment2ValueCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		dateOfShipment2Cell.setCellValue("     ETA " + facilityName + ":");
		rownum += 1;

		Row portOfDischargingRow = sheet.createRow(rownum);
		portOfDischargingRow.setHeight((short) 300);
		Cell portOfDischargingCell = portOfDischargingRow.createCell(0);
		portOfDischargingCell.setCellValue("3.2     Port of discharging: " + facilityName + ".");
		portOfDischargingCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row allowedRow = sheet.createRow(rownum);
		allowedRow.setHeight((short) 300);
		Cell allowedCell = allowedRow.createCell(0);
		allowedCell.setCellValue("3.3     Transshipment is allowed. Partial shipment is allowed.");
		allowedCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row termOfPaymentRow = sheet.createRow(rownum);
		termOfPaymentRow.setHeight((short) 300);
		Cell termOfPaymentCell = termOfPaymentRow.createCell(0);
		termOfPaymentCell.setCellValue("4.     TERM OF PAYMENT");
		termOfPaymentCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row termOfPayment1Row = sheet.createRow(rownum);
		termOfPayment1Row.setHeight((short) 300);
		Cell termOfPayment1Cell = termOfPayment1Row.createCell(0);
		termOfPayment1Cell.setCellValue("The transfer of money date: TTR within 60 days from date of Bill of Lading.");
		termOfPayment1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row termOfPayment2Row = sheet.createRow(rownum);
		termOfPayment2Row.setHeight((short) 600);
		Cell termOfPayment2Cell = termOfPayment2Row.createCell(0);
		termOfPayment2Cell.setCellValue(
				"In favor of the Supplier, issued by a Vietnamese bank through the advising bank and the Purchaser shall \n inform by fax to the Supplier immediately.");
		termOfPayment2Cell.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_11"));
		rownum += 1;

		List<GenericValue> listFinAccountTos = (List<GenericValue>) data.get("listFinAccountTos");
		GenericValue finAccount = new GenericValue();
		GenericValue finAccountAtrBIC = new GenericValue();
		GenericValue finAccountAtrIBAN = new GenericValue();
		for (GenericValue finAccount1 : listFinAccountTos) {
			finAccount = delegator.findOne("FinAccount",
					UtilMisc.toMap("finAccountId", finAccount1.get("finAccountId")), false);
			finAccountAtrBIC = delegator.findOne("FinAccountAttribute",
					UtilMisc.toMap("finAccountId", finAccount1.get("finAccountId"), "attrName", "BIC"), false);
			finAccountAtrIBAN = delegator.findOne("FinAccountAttribute",
					UtilMisc.toMap("finAccountId", finAccount1.get("finAccountId"), "attrName", "IBAN"), false);

			String finAccountName = "";
			String finAccountAtrBICName = "";
			String finAccountAtrIBANName = "";
			if (UtilValidate.isNotEmpty(finAccount))
				finAccountName = (String) finAccount.get("finAccountName");
			if (UtilValidate.isNotEmpty(finAccountAtrBIC))
				finAccountAtrBICName = (String) finAccountAtrBIC.get("attrValue");
			if (UtilValidate.isNotEmpty(finAccountAtrIBAN))
				finAccountAtrIBANName = (String) finAccountAtrIBAN.get("attrValue");

			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
			Row supplierRow = sheet.createRow(rownum);
			supplierRow.setHeight((short) 300);
			Cell supplierCell = supplierRow.createCell(0);
			supplierCell.setCellValue(" Supplier bank: ");
			supplierCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
			Cell supplier1Cell = supplierRow.createCell(2);
			supplier1Cell.setCellValue(finAccountName);
			supplier1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
			rownum += 1;

			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
			Row ibanCodeRow = sheet.createRow(rownum);
			ibanCodeRow.setHeight((short) 300);
			Cell ibanCodeCell = ibanCodeRow.createCell(2);
			ibanCodeCell.setCellValue("IBAN Code: " + finAccountAtrIBANName);
			ibanCodeCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
			rownum += 1;

			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
			Row bicCodeRow = sheet.createRow(rownum);
			bicCodeRow.setHeight((short) 300);
			Cell bicCodeCell = bicCodeRow.createCell(2);
			bicCodeCell.setCellValue("BIC code: " + finAccountAtrBICName);
			bicCodeCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
			rownum += 1;
		}

		Row beneficiaryRow = sheet.createRow(rownum);
		beneficiaryRow.setHeight((short) 300);
		Cell beneficiaryCell = beneficiaryRow.createCell(0);
		beneficiaryCell.setCellValue("Beneficiary:");
		beneficiaryCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell beneficiary1Cell = beneficiaryRow.createCell(2);
		beneficiary1Cell.setCellValue(supplierName);
		beneficiary1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
		Row beneficiary2Row = sheet.createRow(rownum);
		beneficiary2Row.setHeight((short) 300);
		Cell beneficiary2Cell = beneficiary2Row.createCell(2);
		beneficiary2Cell.setCellValue("Address: " + strAddressTo);
		beneficiary2Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row packingRow = sheet.createRow(rownum);
		packingRow.setHeight((short) 300);
		Cell packingCell = packingRow.createCell(0);
		packingCell.setCellValue("5.     PACKING");
		packingCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row packing2Row = sheet.createRow(rownum);
		packing2Row.setHeight((short) 800);
		Cell packing2Cell = packing2Row.createCell(0);
		packing2Cell.setCellValue(
				"The product shall be delivered and packed properly for transportation according to the international \n export standard and protected to ensure safety of the goods in transportation, transit, and transshipment, \n normal handling.");
		packing2Cell.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row packing3Row = sheet.createRow(rownum);
		packing3Row.setHeight((short) 800);
		Cell packing3Cell = packing3Row.createCell(0);
		packing3Cell.setCellValue(
				"Before packing, such preventive measure as basic treatment, polyethylene film wrapping and bigger than \n five millimeters thickness outer carton with tighten belt etc... subject to mature and requirement of goods \n avoided any strike directly to product.");
		packing3Cell.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentationRow = sheet.createRow(rownum);
		documentationRow.setHeight((short) 300);
		Cell documentationCell = documentationRow.createCell(0);
		documentationCell.setCellValue("6.     DOCUMENTATION");
		documentationCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentation1Row = sheet.createRow(rownum);
		documentation1Row.setHeight((short) 300);
		Cell documentation1Cell = documentation1Row.createCell(0);
		documentation1Cell.setCellValue("According the requirement of product's specification.");
		documentation1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentation2Row = sheet.createRow(rownum);
		documentation2Row.setHeight((short) 300);
		Cell documentation2Cell = documentation2Row.createCell(0);
		documentation2Cell.setCellValue("-     Invoice");
		documentation2Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentation3Row = sheet.createRow(rownum);
		documentation3Row.setHeight((short) 300);
		Cell documentation3Cell = documentation3Row.createCell(0);
		documentation3Cell.setCellValue("-     Packing List (Delivery note)");
		documentation3Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentation4Row = sheet.createRow(rownum);
		documentation4Row.setHeight((short) 300);
		Cell documentation4Cell = documentation4Row.createCell(0);
		documentation4Cell.setCellValue("-     Bill of Lading");
		documentation4Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row transportationRow = sheet.createRow(rownum);
		transportationRow.setHeight((short) 300);
		Cell transportationCell = transportationRow.createCell(0);
		transportationCell.setCellValue("7.     TRANSPORTATION");
		transportationCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row transportation1Row = sheet.createRow(rownum);
		transportation1Row.setHeight((short) 300);
		Cell transportation1Cell = transportation1Row.createCell(0);
		transportation1Cell.setCellValue("By " + supplierName);
		transportation1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row transportation2Row = sheet.createRow(rownum);
		transportation2Row.setHeight((short) 300);
		Cell transportation2Cell = transportation2Row.createCell(0);
		transportation2Cell.setCellValue("Receiver address:");
		transportation2Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row delysRow = sheet.createRow(rownum);
		delysRow.setHeight((short) 300);
		Cell delysCell = delysRow.createCell(0);
		delysCell.setCellValue(purchaserName);
		delysCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row delysAddressRow = sheet.createRow(rownum);
		delysAddressRow.setHeight((short) 300);
		Cell delysAddressCell = delysAddressRow.createCell(0);
		delysAddressCell.setCellValue("Address: " + strAddressFrom);
		delysAddressCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row publicationRow = sheet.createRow(rownum);
		publicationRow.setHeight((short) 600);
		Cell publicationCell = publicationRow.createCell(0);
		publicationCell.setCellValue(
				"All amendments to the purchase order and the supplementary agreements between both parties are valid \n only after written confirmation by both parties.");
		publicationCell.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_11"));
		rownum += 1;

		Row forTheSupplierRow = sheet.createRow(rownum);
		forTheSupplierRow.setHeight((short) 300);
		Cell forTheSupplierCell = forTheSupplierRow.createCell(0);
		forTheSupplierCell.setCellValue("FOR THE SUPPLIER");
		forTheSupplierCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));

		Cell forThePurchaserCell = forTheSupplierRow.createCell(5);
		forThePurchaserCell.setCellValue("FOR THE PURCHASER");
		forThePurchaserCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.setZoom(3, 4);
		return wb;
	}

	@SuppressWarnings("unchecked")
	public static void exportAgreementToValidationToExcel(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String billId = request.getParameter("billId");
		String containerId = request.getParameter("containerId");
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("userLogin", userLogin);
		if (UtilValidate.isEmpty(containerId)) {
			context.put("billId", billId);
		} else {
			context.put("containerId", containerId);
		}
		Map<String, Object> results = null;
		try {
			results = dispatcher.runSync("getInfomationAgreementsInBill", context);
		} catch (GenericServiceException e) {
			response.getWriter().print("GenericServiceException");
		}
		Map<String, Object> mapInfomationAgreements = (Map<String, Object>) results.get("mapInfomationAgreements");
		if (UtilValidate.isEmpty(mapInfomationAgreements)) {
			response.getWriter().print("mapInfomationAgreement null");
			response.flushBuffer();
			return;
		}
		Locale locale = (Locale) results.get("locale");
		String currencyUomId = (String) mapInfomationAgreements.get("currencyUomId");
		String thisSignCurrency = euroSign;
		if (currencyUomId.equals("USD")) {
			thisSignCurrency = "$";
		}
		String sheetName = "Sheet1";
		String fileName = "AgreementToValidation.xls";
		List<String> title = new FastList<String>();
		title.add("No.");
		title.add("Description");
		title.add("Unit");
		title.add("Quantity");
		title.add("Unit Price \n " + "(" + thisSignCurrency + ")");
		title.add("Good value \n " + "(" + thisSignCurrency + ")");
		title.add("Remark");
		Workbook wbProduct = renderAgreementsToValidationInBill(sheetName, fileName, title, mapInfomationAgreements,
				delegator, locale);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wbProduct.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + fileName);
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if (baos != null)
				baos.close();
		}
	}

	@SuppressWarnings("unchecked")
	private static Workbook renderAgreementsToValidationInBill(String sheetName, String fileName, List<String> titles,
			Map<String, Object> data, GenericDelegator delegator, Locale locale)
			throws IOException, GenericEntityException {
		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = createStyles(wb);
		Sheet sheet = wb.createSheet(sheetName);

		CellStyle csWrapText = wb.createCellStyle();
		csWrapText.setWrapText(true);

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

		sheet.setColumnWidth(0, 21 * 256);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 11 * 256);
		sheet.setColumnWidth(3, 11 * 256);
		sheet.setColumnWidth(4, 11 * 256);
		sheet.setColumnWidth(5, 15 * 256);
		sheet.setColumnWidth(6, 11 * 256);
		sheet.setColumnWidth(7, 11 * 256);

		Row imgHead = sheet.createRow(0);
		imgHead.createCell(0);
		int rownum = 0;
		// FileInputStream is = null;
		// try {
		// String imageServerPath =
		// FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("delys",
		// "image.management.logoPath"), null);
		// File file = new File(imageServerPath);
		// is = new FileInputStream(file);
		// byte[] bytesImg = IOUtils.toByteArray(is);
		// int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
		// CreationHelper helper = wb.getCreationHelper();
		// Drawing drawing = sheet.createDrawingPatriarch();
		// ClientAnchor anchor = helper.createClientAnchor();
		// anchor.setCol1(0);
		// anchor.setCol2(8);
		// anchor.setRow1(0);
		// anchor.setRow2(4);
		// Picture pict = drawing.createPicture(anchor, pictureIdx);
		// pict.getPictureData();
		// rownum = 5;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }finally{
		// if(is!=null)is.close();
		// }
		Row titleRow = sheet.createRow(rownum);
		titleRow.setHeight((short) 380);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("CONTRACT".toUpperCase());
		titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		Row agreementNameRow = sheet.createRow(rownum);
		agreementNameRow.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell agreementNameCell = agreementNameRow.createCell(0);
		agreementNameCell.setCellValue("No. ");
		agreementNameCell.setCellStyle(styles.get("cell_centered_no_border_11"));
		rownum += 1;

		Row purcharseDateRow = sheet.createRow(rownum);
		purcharseDateRow.setHeight((short) 300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell purcharseDateCell = purcharseDateRow.createCell(0);
		purcharseDateCell.setCellValue("This purchase order is made on ");
		purcharseDateCell.setCellStyle(styles.get("cell_centered_no_border_11"));
		rownum += 1;

		Row betweenRow = sheet.createRow(rownum);
		betweenRow.setHeight((short) 300);
		Cell betweenCell = betweenRow.createCell(0);
		betweenCell.setCellValue("Between");
		betweenCell.setCellStyle(styles.get("cell_italic_bold_left_no_border_11"));
		rownum += 1;

		GenericValue agreement = (GenericValue) data.get("agreement");
		Row delysCompanyRow = sheet.createRow(rownum);
		delysCompanyRow.setHeight((short) 300);
		Cell delysCompanyCell = delysCompanyRow.createCell(0);
		String partyIdFrom = (String) agreement.get("partyIdFrom");
		GenericValue purchaser = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyIdFrom), false);
		String purchaserName = "";
		if (UtilValidate.isNotEmpty(purchaser))
			purchaserName = (String) purchaser.get("groupName", locale);
		delysCompanyCell.setCellValue(purchaserName);
		delysCompanyCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row delysCompanyAddressRow = sheet.createRow(rownum);
		delysCompanyAddressRow.setHeight((short) 300);
		Cell delysCompanyAddressCell = delysCompanyAddressRow.createCell(0);
		String contactMechAddressFromId = (String) data.get("contactMechAddressFromId");
		GenericValue addressFrom = delegator.findOne("PostalAddress",
				UtilMisc.toMap("contactMechId", contactMechAddressFromId), false);
		String strAddressFrom = "";
		if (UtilValidate.isNotEmpty(addressFrom))
			strAddressFrom = (String) addressFrom.get("address1", locale);
		delysCompanyAddressCell.setCellValue("Address: " + strAddressFrom);
		delysCompanyAddressCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row delysCompanyTelAndFaxRow = sheet.createRow(rownum);
		delysCompanyTelAndFaxRow.setHeight((short) 300);
		Cell delysCompanyTelCell = delysCompanyTelAndFaxRow.createCell(0);
		String contactMechPhoneFromId = (String) data.get("contactMechPhoneFromId");
		String contactMechFaxFromId = (String) data.get("contactMechFaxFromId");
		GenericValue phoneFrom = delegator.findOne("TelecomNumber",
				UtilMisc.toMap("contactMechId", contactMechPhoneFromId), false);
		GenericValue faxFrom = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechFaxFromId),
				false);
		String phoneContactNumber = "";
		String faxContactNumber = "";
		if (UtilValidate.isNotEmpty(phoneFrom))
			phoneContactNumber = (String) phoneFrom.get("contactNumber");
		if (UtilValidate.isNotEmpty(faxFrom))
			faxContactNumber = (String) faxFrom.get("contactNumber");
		delysCompanyTelCell.setCellValue("TEL: " + phoneContactNumber);
		delysCompanyTelCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		Cell delysCompanyFaxCell = delysCompanyTelAndFaxRow.createCell(5);
		delysCompanyFaxCell.setCellValue("FAX: " + faxContactNumber);
		delysCompanyFaxCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row delysCompanyBankRow = sheet.createRow(rownum);
		delysCompanyTelAndFaxRow.setHeight((short) 300);
		Cell delysCompanyBankNameCell = delysCompanyBankRow.createCell(0);
		delysCompanyBankNameCell.setCellValue("");
		delysCompanyBankNameCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		Cell delysCompanyBankAccCell = delysCompanyBankRow.createCell(5);
		List<GenericValue> listFinAccountFroms = (List<GenericValue>) data.get("listFinAccountFroms");
		String accountCode = "";
		if (UtilValidate.isNotEmpty(listFinAccountFroms)) {
			accountCode = (String) listFinAccountFroms.get(0).get("finAccountCode");
		}
		delysCompanyBankAccCell.setCellValue("Account: " + accountCode);
		delysCompanyBankAccCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.createRow(rownum);
		rownum += 1;

		Row representedByRow = sheet.createRow(rownum);
		representedByRow.setHeight((short) 300);
		Cell representedByCell = representedByRow.createCell(0);
		String representPartyId = (String) data.get("representPartyId");
		GenericValue representParty = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", representPartyId),
				false);
		StringBuilder representPartyName = new StringBuilder();
		if (UtilValidate.isNotEmpty(representParty)) {
			if (UtilValidate.isNotEmpty(representParty.get("firstName")))
				representPartyName.append(representParty.get("firstName") + " ");
			if (UtilValidate.isNotEmpty(representParty.get("middleName")))
				representPartyName.append(representParty.get("middleName") + " ");
			if (UtilValidate.isNotEmpty(representParty.get("lastName")))
				representPartyName.append(representParty.get("lastName") + " ");
			if (UtilValidate.isNotEmpty(representParty.get("groupName")))
				representPartyName.append(representParty.get("groupName"));
			if (UtilValidate.isNotEmpty(representParty.get("partyId")))
				representPartyName.append("-" + representParty.get("partyId"));
		}
		representedByCell.setCellValue("Represented by " + representPartyName);
		representedByCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row calledDelysRow = sheet.createRow(rownum);
		calledDelysRow.setHeight((short) 300);
		Cell calledDelysCell = calledDelysRow.createCell(0);
		calledDelysCell.setCellValue(
				"Here in after called " + leftDoubleQuotationMark + "The Purchaser" + rightDoubleQuotationMark);
		calledDelysCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row andRow = sheet.createRow(rownum);
		andRow.setHeight((short) 300);
		Cell andCell = andRow.createCell(0);
		andCell.setCellValue("And");
		andCell.setCellStyle(styles.get("cell_italic_bold_left_no_border_11"));
		rownum += 1;

		Row zottCompanyRow = sheet.createRow(rownum);
		zottCompanyRow.setHeight((short) 300);
		Cell zottCompanyCell = zottCompanyRow.createCell(0);
		String partyIdTo = (String) agreement.get("partyIdTo");
		GenericValue supplier = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", partyIdTo), false);
		String supplierName = "";
		if (UtilValidate.isNotEmpty(supplier))
			supplierName = (String) supplier.get("groupName", locale);
		zottCompanyCell.setCellValue(supplierName);
		zottCompanyCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row zottCompanyAddressRow = sheet.createRow(rownum);
		zottCompanyAddressRow.setHeight((short) 300);
		Cell zottCompanyAddressCell = zottCompanyAddressRow.createCell(0);
		String contactMechAddressToId = (String) data.get("contactMechAddressToId");
		GenericValue addressTo = new GenericValue();
		addressTo = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechAddressToId), false);
		String strAddressTo = "";
		if (UtilValidate.isNotEmpty(addressTo))
			strAddressTo = (String) addressTo.get("address1");
		zottCompanyAddressCell.setCellValue("Address: " + strAddressTo);
		zottCompanyAddressCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		Row calledZottRow = sheet.createRow(rownum);
		calledZottRow.setHeight((short) 300);
		Cell calledZottCell = calledZottRow.createCell(0);
		calledZottCell.setCellValue(
				"Here in after called " + leftDoubleQuotationMark + "The Supplier" + rightDoubleQuotationMark);
		calledZottCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.createRow(rownum);
		rownum += 1;

		Row contractValueRow = sheet.createRow(rownum);
		contractValueRow.setHeight((short) 300);
		Cell contractValueCell = contractValueRow.createCell(0);
		contractValueCell.setCellValue("1. CONTRACT VALUE");
		contractValueCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row totalPurchaseRow = sheet.createRow(rownum);
		totalPurchaseRow.setHeight((short) 300);
		Cell totalPurchaseCell = totalPurchaseRow.createCell(0);
		totalPurchaseCell.setCellValue("1.1.The total purchase order price is ");
		totalPurchaseCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell totalPurchaseValueCell = totalPurchaseRow.createCell(4);

		totalPurchaseValueCell.setCellStyle(styles.get("cell_bold_right_no_border_10"));
		Cell totalPurchaseValueUnitCell = totalPurchaseRow.createCell(5);
		totalPurchaseValueUnitCell.setCellValue((String) data.get("currencyUomId"));
		totalPurchaseValueUnitCell.setCellStyle(styles.get("cell_bold_left_no_border_10"));
		rownum += 1;

		Row inWordRow = sheet.createRow(rownum);
		inWordRow.setHeight((short) 300);
		Cell inWordCell = inWordRow.createCell(0);
		inWordCell.setCellValue("In words:");
		inWordCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
		Cell nameCell = inWordRow.createCell(5);
		nameCell.setCellValue("");
		nameCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row otherNoteRow = sheet.createRow(rownum);
		otherNoteRow.setHeight((short) 300);
		Cell otherNoteCell = otherNoteRow.createCell(0);
		otherNoteCell.setCellValue("EUROS eighty five thousand, three hundered and twenty and cents fifty four only");
		otherNoteCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row breakdownAmountRow = sheet.createRow(rownum);
		breakdownAmountRow.setHeight((short) 300);
		Cell breakdownAmountCell = breakdownAmountRow.createCell(0);
		breakdownAmountCell.setCellValue("1.2 The breakdown of this amount is as follows:");
		breakdownAmountCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row headerBreakdownAmountRow = sheet.createRow(rownum);
		headerBreakdownAmountRow.setHeight((short) 500);
		for (int i = 0; i < titles.size(); i++) {
			Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
			if (titles.get(i).equals("Remark")) {
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 7));
				Cell headerBreakdownAmount2Cell = headerBreakdownAmountRow.createCell(i + 1);
				headerBreakdownAmount2Cell
						.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_right_10"));
			}
			headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			headerBreakdownAmountCell.setCellValue(titles.get(i));
		}
		rownum += 1;
		BigDecimal palet = BigDecimal.ZERO;
		float totalGoodValue = 0;
		float totalPallets = 0;
		float totalQuantity = 0;
		String uomUnit = "";
		GenericValue cfpacking = new GenericValue();
		GenericValue uom = new GenericValue();
		List<GenericValue> listProducts = (List<GenericValue>) data.get("listOrderItemTotalInBill");
		for (GenericValue x : listProducts) {
			Row productDetailRow = sheet.createRow(rownum);
			Cell productIdCell = productDetailRow.createCell(0);
			String productId = x.getString("productId");
			productIdCell.setCellValue(productId);
			productIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			String description = x.getString("description");
			Cell productDescriptionCell = productDetailRow.createCell(1);
			productDescriptionCell.setCellValue(description);
			productDescriptionCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			String quantityUomId = x.getString("purchaseUomId");
			
			String productPackingUomId = UtilProperties.getPropertyValue(IMEX_PROPERTIES, "imex.import.packing.uom.pallet");
			
			palet = ProductUtil.getConvertPackingNumber(delegator, productId, productPackingUomId, quantityUomId); 
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
			if (UtilValidate.isNotEmpty(cfpacking))
				palet = (BigDecimal) cfpacking.get("quantityConvert");
			if (UtilValidate.isNotEmpty(uom))
				uomUnit = (String) uom.get("description");

			Cell productQuantityUnitCell = productDetailRow.createCell(2);
			productQuantityUnitCell.setCellValue(uomUnit);
			productQuantityUnitCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			Cell productQuantityCell = productDetailRow.createCell(3);
			BigDecimal productQuantity = (BigDecimal) x.get("quantity");
			totalQuantity += productQuantity.floatValue();
			productQuantityCell.setCellValue(productQuantity.floatValue());
			productQuantityCell.setCellStyle(styles.get("cell_normal_right_boder_full_10"));

			Cell productPriceCell = productDetailRow.createCell(4);
			BigDecimal productPrice = (BigDecimal) x.get("unitPrice");
			if (UtilValidate.isNotEmpty(productPrice)) {
				productPriceCell.setCellValue(productPrice.toString());
			}
			productPriceCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

			Cell goodValueCell = productDetailRow.createCell(5);
			float goodValue = productQuantity.floatValue() * productPrice.floatValue();
			totalGoodValue += goodValue;
			goodValueCell.setCellValue(goodValue);
			goodValueCell.setCellStyle(styles.get("cell_normal_right_boder_full_10"));

			Cell palletCell = productDetailRow.createCell(6);
			float pallet = productQuantity.floatValue() / palet.floatValue();
			totalPallets += pallet;
			palletCell.setCellValue(pallet);
			palletCell.setCellStyle(styles.get("cell_normal_right_boder_no_right_10"));

			Cell palletsCell = productDetailRow.createCell(7);
			palletsCell.setCellValue("KAR");
			palletsCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_right_10"));

			rownum += 1;
		}
		Row totalRow = sheet.createRow(rownum);
		Cell totalCell = totalRow.createCell(1);
		totalCell.setCellValue("TOTAL");
		totalCell.setCellStyle(styles.get("cell_bold_centered_wrap_text_border_bottom_10"));

		Cell blankCell0 = totalRow.createCell(0);
		blankCell0.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_bottom_left_10"));

		Cell blankCell2 = totalRow.createCell(2);
		blankCell2.setCellStyle(styles.get("cell_bold_centered_wrap_text_border_bottom_10"));
		Cell blankCell3 = totalRow.createCell(3);
		blankCell3.setCellValue(totalQuantity);
		blankCell3.setCellStyle(styles.get("cell_bold_right_wrap_text_border_bottom_10"));
		Cell blankCell4 = totalRow.createCell(4);
		blankCell4.setCellStyle(styles.get("cell_bold_centered_wrap_text_border_bottom_10"));

		Cell totalGoodValueCell = totalRow.createCell(5);
		totalGoodValueCell.setCellValue(totalGoodValue);
		totalGoodValueCell.setCellStyle(styles.get("cell_bold_right_wrap_text_border_bottom_10"));

		Cell totalPalletCell = totalRow.createCell(6);
		totalPalletCell.setCellValue(totalPallets);
		totalPalletCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_bottom_right_10"));

		Cell palletCell = totalRow.createCell(7);
		palletCell.setCellValue("KAR");
		palletCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_bottom_right_10"));
		totalPurchaseValueCell.setCellValue(totalGoodValue);
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row ratioBetweenRow = sheet.createRow(rownum);
		ratioBetweenRow.setHeight((short) 300);
		Cell ratioBetweenCell = ratioBetweenRow.createCell(0);
		ratioBetweenCell.setCellValue("1.3     All the payment could be combine ratio between ");
		ratioBetweenCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row termsOfDeliveryRow = sheet.createRow(rownum);
		termsOfDeliveryRow.setHeight((short) 300);
		Cell termsOfDeliveryCell = termsOfDeliveryRow.createCell(0);
		termsOfDeliveryCell.setCellValue("2.     TERMS OF DELIVERY");
		termsOfDeliveryCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row dateOfShipmentRow = sheet.createRow(rownum);
		dateOfShipmentRow.setHeight((short) 300);
		Cell dateOfShipmentCell = dateOfShipmentRow.createCell(0);
		dateOfShipmentCell.setCellValue("2.1     The date of shipment: ");
		dateOfShipmentCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row dateOfShipment1Row = sheet.createRow(rownum);
		dateOfShipment1Row.setHeight((short) 300);
		Cell dateOfShipment1Cell = dateOfShipment1Row.createCell(0);
		dateOfShipment1Cell.setCellValue("     ETD:");
		dateOfShipment1Cell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell dateOfShipment1ValueCell = dateOfShipment1Row.createCell(1);
		String currentETDTerm = (String) data.get("currentETDTerm");
		GenericValue etd = new GenericValue();
		etd = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", currentETDTerm), false);
		Date currentETD = null;
		if (UtilValidate.isNotEmpty(etd))
			currentETD = (Date) etd.get("fromDate");
		if (UtilValidate.isNotEmpty(currentETD)) {
			dateOfShipment1ValueCell.setCellValue(currentETD.toString());
		}
		dateOfShipment1ValueCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row dateOfShipment2Row = sheet.createRow(rownum);
		dateOfShipment2Row.setHeight((short) 300);
		Cell dateOfShipment2Cell = dateOfShipment2Row.createCell(0);
		dateOfShipment2Cell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell dateOfShipment2ValueCell = dateOfShipment2Row.createCell(1);
		String currentPortTerm = (String) data.get("currentPortTerm");
		GenericValue eta = new GenericValue();
		GenericValue port = new GenericValue();
		eta = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", currentETDTerm), false);
		port = delegator.findOne("Facility", UtilMisc.toMap("facilityId", currentPortTerm), false);
		Date currentETA = null;
		String facilityName = "";
		if (UtilValidate.isNotEmpty(eta))
			currentETA = (Date) eta.get("fromDate");
		if (UtilValidate.isNotEmpty(port))
			facilityName = (String) port.get("facilityName");
		if (UtilValidate.isNotEmpty(currentETA)) {
			dateOfShipment2ValueCell.setCellValue(currentETA.toString());
		}
		dateOfShipment2ValueCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		dateOfShipment2Cell.setCellValue("     ETA " + facilityName + ":");
		rownum += 1;

		Row portOfDischargingRow = sheet.createRow(rownum);
		portOfDischargingRow.setHeight((short) 300);
		Cell portOfDischargingCell = portOfDischargingRow.createCell(0);
		portOfDischargingCell.setCellValue("2.2     Port of discharging: " + facilityName + ".");
		portOfDischargingCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row allowedRow = sheet.createRow(rownum);
		allowedRow.setHeight((short) 300);
		Cell allowedCell = allowedRow.createCell(0);
		allowedCell.setCellValue("2.3     Transshipment is allowed. Partial shipment is allowed.");
		allowedCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row termOfPaymentRow = sheet.createRow(rownum);
		termOfPaymentRow.setHeight((short) 300);
		Cell termOfPaymentCell = termOfPaymentRow.createCell(0);
		termOfPaymentCell.setCellValue("3.     TERM OF PAYMENT");
		termOfPaymentCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		Row termOfPayment1Row = sheet.createRow(rownum);
		termOfPayment1Row.setHeight((short) 300);
		Cell termOfPayment1Cell = termOfPayment1Row.createCell(0);
		termOfPayment1Cell.setCellValue("The transfer of money date: TTR within 60 days from date of Bill of Lading.");
		termOfPayment1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row termOfPayment2Row = sheet.createRow(rownum);
		termOfPayment2Row.setHeight((short) 600);
		Cell termOfPayment2Cell = termOfPayment2Row.createCell(0);
		termOfPayment2Cell.setCellValue(
				"In favor of the Supplier, issued by a Vietnamese bank through the advising bank and the Purchaser shall \n inform by fax to the Supplier immediately.");
		termOfPayment2Cell.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_11"));
		rownum += 1;

		List<GenericValue> listFinAccountTos = (List<GenericValue>) data.get("listFinAccountTos");
		GenericValue finAccount = new GenericValue();
		GenericValue finAccountAtrBIC = new GenericValue();
		GenericValue finAccountAtrIBAN = new GenericValue();
		for (GenericValue finAccount1 : listFinAccountTos) {
			finAccount = delegator.findOne("FinAccount",
					UtilMisc.toMap("finAccountId", finAccount1.get("finAccountId")), false);
			finAccountAtrBIC = delegator.findOne("FinAccountAttribute",
					UtilMisc.toMap("finAccountId", finAccount1.get("finAccountId"), "attrName", "BIC"), false);
			finAccountAtrIBAN = delegator.findOne("FinAccountAttribute",
					UtilMisc.toMap("finAccountId", finAccount1.get("finAccountId"), "attrName", "IBAN"), false);

			String finAccountName = "";
			String finAccountAtrBICName = "";
			String finAccountAtrIBANName = "";
			if (UtilValidate.isNotEmpty(finAccount))
				finAccountName = (String) finAccount.get("finAccountName");
			if (UtilValidate.isNotEmpty(finAccountAtrBIC))
				finAccountAtrBICName = (String) finAccountAtrBIC.get("attrValue");
			if (UtilValidate.isNotEmpty(finAccountAtrIBAN))
				finAccountAtrIBANName = (String) finAccountAtrIBAN.get("attrValue");

			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
			Row supplierRow = sheet.createRow(rownum);
			supplierRow.setHeight((short) 300);
			Cell supplierCell = supplierRow.createCell(0);
			supplierCell.setCellValue(" Supplier bank: ");
			supplierCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
			Cell supplier1Cell = supplierRow.createCell(2);
			supplier1Cell.setCellValue(finAccountName);
			supplier1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
			rownum += 1;

			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
			Row ibanCodeRow = sheet.createRow(rownum);
			ibanCodeRow.setHeight((short) 300);
			Cell ibanCodeCell = ibanCodeRow.createCell(2);
			ibanCodeCell.setCellValue("IBAN Code: " + finAccountAtrIBANName);
			ibanCodeCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
			rownum += 1;

			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
			Row bicCodeRow = sheet.createRow(rownum);
			bicCodeRow.setHeight((short) 300);
			Cell bicCodeCell = bicCodeRow.createCell(2);
			bicCodeCell.setCellValue("BIC code: " + finAccountAtrBICName);
			bicCodeCell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
			rownum += 1;
		}

		Row beneficiaryRow = sheet.createRow(rownum);
		beneficiaryRow.setHeight((short) 300);
		Cell beneficiaryCell = beneficiaryRow.createCell(0);
		beneficiaryCell.setCellValue("Beneficiary:");
		beneficiaryCell.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell beneficiary1Cell = beneficiaryRow.createCell(2);
		beneficiary1Cell.setCellValue(supplierName);
		beneficiary1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
		Row beneficiary2Row = sheet.createRow(rownum);
		beneficiary2Row.setHeight((short) 300);
		Cell beneficiary2Cell = beneficiary2Row.createCell(2);
		beneficiary2Cell.setCellValue("Address: " + strAddressTo);
		beneficiary2Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row packingRow = sheet.createRow(rownum);
		packingRow.setHeight((short) 300);
		Cell packingCell = packingRow.createCell(0);
		packingCell.setCellValue("4.     PACKING");
		packingCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row packing2Row = sheet.createRow(rownum);
		packing2Row.setHeight((short) 800);
		Cell packing2Cell = packing2Row.createCell(0);
		packing2Cell.setCellValue(
				"The product shall be delivered and packed properly for transportation according to the international \n export standard and protected to ensure safety of the goods in transportation, transit, and transshipment, \n normal handling.");
		packing2Cell.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row packing3Row = sheet.createRow(rownum);
		packing3Row.setHeight((short) 800);
		Cell packing3Cell = packing3Row.createCell(0);
		packing3Cell.setCellValue(
				"Before packing, such preventive measure as basic treatment, polyethylene film wrapping and bigger than \n five millimeters thickness outer carton with tighten belt etc... subject to mature and requirement of goods \n avoided any strike directly to product.");
		packing3Cell.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentationRow = sheet.createRow(rownum);
		documentationRow.setHeight((short) 300);
		Cell documentationCell = documentationRow.createCell(0);
		documentationCell.setCellValue("5.     DOCUMENTATION");
		documentationCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentation1Row = sheet.createRow(rownum);
		documentation1Row.setHeight((short) 300);
		Cell documentation1Cell = documentation1Row.createCell(0);
		documentation1Cell.setCellValue("According the requirement of product's specification.");
		documentation1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentation2Row = sheet.createRow(rownum);
		documentation2Row.setHeight((short) 300);
		Cell documentation2Cell = documentation2Row.createCell(0);
		documentation2Cell.setCellValue("-     Invoice");
		documentation2Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentation3Row = sheet.createRow(rownum);
		documentation3Row.setHeight((short) 300);
		Cell documentation3Cell = documentation3Row.createCell(0);
		documentation3Cell.setCellValue("-     Packing List (Delivery note)");
		documentation3Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row documentation4Row = sheet.createRow(rownum);
		documentation4Row.setHeight((short) 300);
		Cell documentation4Cell = documentation4Row.createCell(0);
		documentation4Cell.setCellValue("-     Bill of Lading");
		documentation4Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row transportationRow = sheet.createRow(rownum);
		transportationRow.setHeight((short) 300);
		Cell transportationCell = transportationRow.createCell(0);
		transportationCell.setCellValue("6.     TRANSPORTATION");
		transportationCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row transportation1Row = sheet.createRow(rownum);
		transportation1Row.setHeight((short) 300);
		Cell transportation1Cell = transportation1Row.createCell(0);
		transportation1Cell.setCellValue("By " + supplierName);
		transportation1Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row transportation2Row = sheet.createRow(rownum);
		transportation2Row.setHeight((short) 300);
		Cell transportation2Cell = transportation2Row.createCell(0);
		transportation2Cell.setCellValue("Receiver address:");
		transportation2Cell.setCellStyle(styles.get("cell_normal_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row delysRow = sheet.createRow(rownum);
		delysRow.setHeight((short) 300);
		Cell delysCell = delysRow.createCell(0);
		delysCell.setCellValue(purchaserName);
		delysCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row delysAddressRow = sheet.createRow(rownum);
		delysAddressRow.setHeight((short) 300);
		Cell delysAddressCell = delysAddressRow.createCell(0);
		delysAddressCell.setCellValue("Address: " + strAddressFrom);
		delysAddressCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Row publicationRow = sheet.createRow(rownum);
		publicationRow.setHeight((short) 600);
		Cell publicationCell = publicationRow.createCell(0);
		publicationCell.setCellValue(
				"All amendments to the purchase order and the supplementary agreements between both parties are valid \n only after written confirmation by both parties.");
		publicationCell.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_11"));
		rownum += 1;

		Row forTheSupplierRow = sheet.createRow(rownum);
		forTheSupplierRow.setHeight((short) 300);
		Cell forTheSupplierCell = forTheSupplierRow.createCell(0);
		forTheSupplierCell.setCellValue("FOR THE SUPPLIER");
		forTheSupplierCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));

		Cell forThePurchaserCell = forTheSupplierRow.createCell(5);
		forThePurchaserCell.setCellValue("FOR THE PURCHASER");
		forThePurchaserCell.setCellStyle(styles.get("cell_bold_left_no_border_11"));
		rownum += 1;

		sheet.setZoom(3, 4);
		return wb;
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

	private static CellStyle createNonBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		return style;
	}

	public static void exportDocumentTestedByContainerId(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String containerId = request.getParameter("containerId");
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("userLogin", userLogin);
		context.put("containerId", containerId);
		Map<String, Object> results = null;
		try {
			results = dispatcher.runSync("getInfomationPackingList", context);
		} catch (GenericServiceException e) {
			response.getWriter().print("GenericServiceException");
		}
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		GenericValue objCompany = null;
		try {
			objCompany = delegator.findOne("PartyFullNameDetail", false, UtilMisc.toMap("partyId", company));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne PartyFullNameDetail: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		String importer = objCompany.getString("fullName");
		List<GenericValue> ctms = PartyUtil.getPartyPostalAddressByPurpose(delegator, company, "PRIMARY_LOCATION");
		String importerAddress ="";
		if (!ctms.isEmpty()){
			importerAddress = ctms.get(0).getString("fullName");
		}
		ctms = PartyUtil.getPartyPostalAddressByPurpose(delegator, company, "PRIMARY_PHONE");
		String importerPhone ="";
		if (!ctms.isEmpty()){
			importerPhone = ctms.get(0).getString("fullName");
		}
		ctms = PartyUtil.getPartyPostalAddressByPurpose(delegator, company, "FAX_NUMBER");
		String importerFax ="";
		if (!ctms.isEmpty()){
			importerFax = ctms.get(0).getString("fullName");
		}
		
		ctms = PartyUtil.getPartyTaxParty(delegator, company);
		String taxNumber ="";
		if (!ctms.isEmpty()){
			taxNumber = ctms.get(0).getString("partyTaxId");
		}
		
		String registerName = importer;
		String registerPhone = importerPhone;
		String manufacturer = "";
		String manufactureAddress = "";
		String quantity = ((Integer) results.get("orderUnit")).toString();
		String packing = "";
		String netWeightTotal = ((Integer) results.get("netWeightTotal")).toString();
		String grossWeightTotal = ((Integer) results.get("grossWeightTotal")).toString();
		String agreementNumber = "";
		String invoiceNumber = "";
		String billingNumber = "";
		String totalPrice = "";
		String currentcy = "";
		String exporter = "";
		String exporterAddress = "";
		String exporterCountry = "";
		String customsExport = "";
		String customsImport = "";

		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = createStyles(wb);
		Sheet sheet = wb.createSheet("sheet1");

		Font normal10 = wb.createFont();
		normal10.setFontHeightInPoints((short) 10);

		Font italic10 = wb.createFont();
		italic10.setFontHeightInPoints((short) 10);
		italic10.setItalic(true);

		Font bold11 = wb.createFont();
		bold11.setFontHeightInPoints((short) 11);
		bold11.setBoldweight(Font.BOLDWEIGHT_BOLD);

		CellStyle csWrapText = wb.createCellStyle();
		csWrapText.setWrapText(true);

		sheet.setColumnWidth(0, 10 * 256);
		sheet.setColumnWidth(1, 13 * 256);
		sheet.setColumnWidth(2, 13 * 256);
		sheet.setColumnWidth(3, 13 * 256);
		sheet.setColumnWidth(4, 13 * 256);
		sheet.setColumnWidth(5, 16 * 256);
		sheet.setColumnWidth(6, 16 * 256);
		sheet.setColumnWidth(7, 16 * 256);
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
		cell_00.setCellStyle(styles.get("cell_bold_centered_no_border_11"));
		rownum += 1;

		Row row_1 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_10 = row_1.createCell(0);
		cell_10.setCellValue("ĐỘC LẬP TỰ DO HẠNH PHÚC");
		cell_10.setCellStyle(styles.get("cell_bold_center_no_border_10"));
		rownum += 1;

		Row row_2 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_20 = row_2.createCell(0);
		cell_20.setCellValue("_____________________");
		cell_20.setCellStyle(styles.get("cell_bold_center_no_border_10"));
		rownum += 1;

		Row row_3 = sheet.createRow(rownum);
		row_3.setHeight((short) 380);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_30 = row_3.createCell(0);
		cell_30.setCellValue("GIẤY ĐĂNG KÝ KIỂM TRA THỰC PHẨM NHẬP KHẨU".toUpperCase());
		cell_30.setCellStyle(styles.get("cell_bold_centered_no_border_11"));
		rownum += 1;

		Row row_4 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_40 = row_4.createCell(0);
		cell_40.setCellValue("Kính gửi: Trung tâm kỹ thuật tiêu chuẩn đo lường kỹ thuật 3");
		cell_40.setCellStyle(styles.get("cell_bold_center_no_border_10"));
		rownum += 1;

		Row row_5 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_50 = row_5.createCell(0);
		cell_50.setCellValue("Tổ chức cá nhân nhập khẩu: " + importer);
		cell_50.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_6 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_60 = row_6.createCell(0);
		cell_60.setCellValue("Địa chỉ" + importerAddress);
		cell_60.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_7 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
		row_7.createCell(0);
		cell_60.setCellValue("Điện thoại: " + importerPhone);
		cell_60.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_71 = row_7.createCell(2);
		cell_71.setCellValue("Fax/E- Mail: " + importerFax);
		cell_71.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_72 = row_7.createCell(4);
		cell_72.setCellValue("Mã số thuế: " + taxNumber);
		cell_72.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_8 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_80 = row_8.createCell(0);
		cell_80.setCellValue("Tên tổ chức cá nhân đăng ký: " + registerName);
		cell_80.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_9 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_90 = row_9.createCell(0);
		cell_90.setCellValue("Điện thoại: " + registerPhone);
		cell_90.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_10 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_100 = row_10.createCell(0);
		cell_100.setCellValue(
				"Số CMTND (cá nhân): ................     nơi cấp:............ Ngày cấp:..............");
		cell_100.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_11 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_110 = row_11.createCell(0);
		cell_110.setCellValue(
				"Đề nghị quý Trung tâm kiểm tra nhà nước an toàn thực phẩm (ATTP) lô hàng nhập khẩu sau::");
		cell_110.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_12 = sheet.createRow(rownum);
		row_12.setHeight((short) 500);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_120 = row_12.createCell(0);
		cell_120.setCellValue("1.");
		cell_120.setCellStyle(styles.get("cell_normal_centered_top10"));
		Cell cell_121 = row_12.createCell(1);
		cell_121.setCellValue(
				"Tên hàng hóa: (Chi tiết theo danh mục sản phẩm đính kèm) \nTên khoa học: ................................................................");
		cell_121.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_10"));
		rownum += 1;

		Row row_13 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_130 = row_13.createCell(0);
		cell_130.setCellValue("     Cơ sở sản xuất: " + manufacturer);
		cell_130.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_14 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_140 = row_14.createCell(0);
		cell_140.setCellValue("    Địa chỉ: " + manufactureAddress);
		cell_140.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_15 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
		Cell cell_150 = row_15.createCell(0);
		cell_150.setCellValue("2. Số lượng và loại bao bì : " + quantity + " " + packing);
		cell_150.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_151 = row_15.createCell(4);
		cell_151.setCellValue("Loại bao bì: Nhựa bao phim");
		cell_151.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_16 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
		Cell cell_160 = row_16.createCell(0);
		cell_160.setCellValue("3. Trọng lượng tịnh : " + netWeightTotal + " Kg");
		cell_160.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_161 = row_16.createCell(4);
		cell_161.setCellValue("Trọng lượng cả bì : " + grossWeightTotal + " Kg");
		cell_161.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_17 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
		Cell cell_170 = row_17.createCell(0);
		cell_170.setCellValue("4. Số hợp đồng (hoặc L/C): " + agreementNumber);
		cell_170.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_171 = row_17.createCell(4);
		cell_171.setCellValue("Hóa đơn số : " + invoiceNumber);
		cell_171.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_18 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
		Cell cell_180 = row_18.createCell(0);
		cell_180.setCellValue("    Vận đơn số : " + billingNumber);
		cell_180.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_181 = row_18.createCell(4);
		cell_181.setCellValue("Giá trị hàng hoá: " + totalPrice + " " + currentcy);
		cell_181.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_19 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_190 = row_19.createCell(0);
		cell_190.setCellValue("5. Tổ chức, cá nhân xuất khẩu: " + exporter);
		cell_190.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_20 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_200 = row_20.createCell(0);
		cell_200.setCellValue("    Địa chỉ: " + exporterAddress);
		cell_200.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_21 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_210 = row_21.createCell(0);
		cell_210.setCellValue("6. Nơi xuất khẩu: " + exporterCountry);
		cell_210.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_22 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_220 = row_22.createCell(0);
		cell_220.setCellValue("7. Cảng xuất khẩu: " + customsExport);
		cell_220.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_23 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_230 = row_23.createCell(0);
		cell_230.setCellValue("8. Cảng nhập khẩu : " + customsImport);
		cell_230.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_24 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_240 = row_24.createCell(0);
		cell_240.setCellValue("9. Phương tiện vận chuyển:  " + ballotBox + "   Tàu biển   " + ballotBox
				+ "   Máy bay   " + ballotBox + "   Khác :.................");
		cell_240.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_25 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_250 = row_25.createCell(0);
		cell_250.setCellValue("10. Mục đích sử dụng:");
		cell_250.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_26 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_260 = row_26.createCell(0);
		cell_260.setCellValue("   " + ballotBox + "   Kinh doanh   " + ballotBox + "   Sản xuất   " + ballotBox
				+ "   Khác (Mẫu thử nghiệm, trưng bày,..........");
		cell_260.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_27 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_270 = row_27.createCell(0);
		cell_270.setCellValue("11. Giấy phép kiểm tra hàng hóa nhập khẩu : ");
		cell_270.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_28 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_280 = row_28.createCell(0);
		cell_280.setCellValue("   " + ballotBox + "   Bản công bố phù hợp QCVN / QĐ ATTP    " + ballotBox
				+ "   Giấy phép giải tỏa");
		cell_280.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_29 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_290 = row_29.createCell(0);
		cell_290.setCellValue("   " + ballotBox + "   Giấy phép kiểm tra giảm/ kiểm tra hồ sơ   " + ballotBox
				+ "   Khác :...............");
		cell_290.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_30 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_300 = row_30.createCell(0);
		cell_300.setCellValue("Địa điểm kiểm tra ATTP:");
		cell_300.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_31 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_310 = row_31.createCell(0);
		cell_310.setCellValue("13. Thời gian kiểm tra ATTP dự kiến : ");
		cell_310.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_32 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_320 = row_32.createCell(0);
		cell_320.setCellValue("Chúng tôi xin cam kết: ");
		cell_320.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_33 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_330 = row_33.createCell(0);
		cell_330.setCellValue(
				"-     Bảo đảm nguyên trạng lô hàng hóa, đưa về đúng địa điểm, đúng thời gian được đăng ký.");
		cell_330.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_34 = sheet.createRow(rownum);
		row_34.setHeight((short) 500);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_340 = row_34.createCell(0);
		cell_340.setCellValue(
				"-     Chỉ đưa hàng hóa ra lưu thông/ sử dụng sau khi được quý Trung tâm cấp Thông báo lô hàng đạt yêu cầu  ATTP theo quy định.");
		cell_340.setCellStyle(styles.get("cell_normal_left_no_border_wrap_text_10"));
		rownum += 1;

		Row row_35 = sheet.createRow(rownum);
		row_35.setHeight((short) 2500);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 7));
		Cell cell_350 = row_35.createCell(0);
		Cell cell_351 = row_35.createCell(1);
		Cell cell_352 = row_35.createCell(2);
		Cell cell_353 = row_35.createCell(3);
		Cell cell_355 = row_35.createCell(5);
		Cell cell_356 = row_35.createCell(6);
		Cell cell_357 = row_35.createCell(7);
		cell_350.setCellStyle(styles.get("cell_centered_bordered_blue_top"));
		cell_351.setCellStyle(styles.get("cell_centered_bordered_blue_top"));
		cell_352.setCellStyle(styles.get("cell_centered_bordered_blue_top"));
		cell_353.setCellStyle(styles.get("cell_centered_bordered_blue_top"));
		cell_355.setCellStyle(styles.get("cell_centered_bordered_blue_top"));
		cell_356.setCellStyle(styles.get("cell_centered_bordered_blue_top"));
		cell_357.setCellStyle(styles.get("cell_centered_bordered_blue_top"));
		HSSFRichTextString rStr350 = new HSSFRichTextString(
				"ĐẠI DIỆN TỐ CHỨC, CÁ NHÂN ĐĂNG KÝ \n (Ký tên, đóng dấu, ghi rõ họ tên)  \n ........., Ngày ..... Tháng ..... Năm 20...");
		rStr350.applyFont(0, 33, bold11);
		rStr350.applyFont(33, rStr350.length(), italic10);
		cell_350.setCellValue(rStr350);
		Cell cell_354 = row_35.createCell(4);
		HSSFRichTextString rStr354 = new HSSFRichTextString(
				"TRUNG TÂM KỸ THUẬT TCĐLCL 3 \n Số đăng ký: .............. /N3..... /KT3 \n Tạm thu:............... \n ........, Ngày .....  Tháng .... Năm 20.... \n TL. GIÁM ĐỐC");
		rStr354.applyFont(0, 27, bold11);
		rStr354.applyFont(27, 119, italic10);
		rStr354.applyFont(119, rStr354.length(), bold11);
		cell_354.setCellValue(rStr354);
		cell_354.setCellStyle(styles.get("cell_centered_bordered_blue_top"));
		rownum += 1;

		Row row_36 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_360 = row_36.createCell(0);
		cell_360.setCellValue(
				"------------------------------------------------------------------------------------------");
		cell_360.setCellStyle(styles.get("cell_normal_centered_10"));
		rownum += 1;

		Row row_37 = sheet.createRow(rownum);
		row_37.createCell(0);
		FileInputStream is = null;
		try {
			// String imageServerPath =
			// FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("delys",
			// "image.management.quatest3"), null);
			// File file = new File(imageServerPath);
			// is = new FileInputStream(file);
			byte[] bytesImg = IOUtils.toByteArray(is);
			int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
			CreationHelper helper = wb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(0);
			anchor.setCol2(1);
			anchor.setRow1(38);
			anchor.setRow2(41);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.getPictureData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				is.close();
		}
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_371 = row_37.createCell(1);
		cell_371.setCellValue("TRUNG TÂM KỸ THUẬT TIÊU CHUẨN ĐO LƯỜNG CHẤT LƯỢNG 3");
		cell_371.setCellStyle(styles.get("cell_centered_blue_bold_9"));
		rownum += 1;

		Row row_38 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_380 = row_38.createCell(1);
		cell_380.setCellValue(
				"");
		cell_380.setCellStyle(styles.get("cell_left_blue_normal_8"));
		rownum += 1;

		Row row_39 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_390 = row_39.createCell(1);
		cell_390.setCellValue(
				"");
		cell_390.setCellStyle(styles.get("cell_left_blue_normal_8"));
		rownum += 1;

		Row row_40 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 7));
		Cell cell_400 = row_40.createCell(1);
		cell_400.setCellValue(
				"");
		cell_400.setCellStyle(styles.get("cell_left_blue_normal_8"));
		rownum += 1;

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String date = dateFormat.format(System.currentTimeMillis());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "GiayDangKyKiemNghiem_" + date + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if (baos != null)
				baos.close();
		}
	}

	public static void exportDocumentQuarantineByContainerId(HttpServletRequest request, HttpServletResponse response)
			throws IOException, GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String containerId = request.getParameter("containerId");
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("userLogin", userLogin);
		context.put("containerId", containerId);
		Map<String, Object> results = null;
		try {
			results = dispatcher.runSync("getInfomationPackingList", context);
		} catch (GenericServiceException e) {
			response.getWriter().print("GenericServiceException");
		}

		String placeOfManufacture = "";
		String exporter = "";
		String countryExport = "";
		String CustomsExport = "";
		
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		GenericValue objCompany = null;
		try {
			objCompany = delegator.findOne("PartyFullNameDetail", false, UtilMisc.toMap("partyId", company));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne PartyFullNameDetail: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		String importer = objCompany.getString("fullName");
		List<GenericValue> ctms = PartyUtil.getPartyPostalAddressByPurpose(delegator, company, "PRIMARY_LOCATION");
		String importerAddress ="";
		if (!ctms.isEmpty()){
			importerAddress = ctms.get(0).getString("fullName");
		}
		ctms = PartyUtil.getPartyPostalAddressByPurpose(delegator, company, "PRIMARY_PHONE");
		String importerPhone ="";
		if (!ctms.isEmpty()){
			importerPhone = ctms.get(0).getString("fullName");
		}
		ctms = PartyUtil.getPartyPostalAddressByPurpose(delegator, company, "FAX_NUMBER");
		String importerFax ="";
		if (!ctms.isEmpty()){
			importerFax = ctms.get(0).getString("fullName");
		}
		
		ctms = PartyUtil.getPartyPostalAddressByPurpose(delegator, company, "PRIMARY_EMAIL");
		String importerEmail ="";
		if (!ctms.isEmpty()){
			importerEmail = ctms.get(0).getString("fullName");
		}
		
		String transportation = "";
		String customsImport = "";
		String locationQuarantine = "";
		int orderUnit = (Integer) results.get("orderUnit");
		String uomId = "";
		int netWeightTotal = (Integer) results.get("netWeightTotal");
		int grossWeightTotal = (Integer) results.get("grossWeightTotal");
		String agreementName = "";
		String quarantineNumber = "";

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
		cell_30.setCellValue("");
		cell_30.setCellStyle(styles.get("cell_normal_right_10"));
		rownum += 1;

		Row row_4 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_40 = row_4.createCell(0);
		cell_40.setCellValue("Ngày     Tháng       năm       ");
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
		cell_61.setCellValue(" ");
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
		cell_71.setCellValue(importer);
		cell_71.setCellStyle(styles.get("cell_bold_left_10"));
		rownum += 1;

		Row row_8 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 7));
		Cell cell_80 = row_8.createCell(0);
		cell_80.setCellValue(importerAddress);
		cell_80.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		rownum += 1;

		Row row_9 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 7));
		Cell cell_90 = row_9.createCell(0);
		cell_90.setCellValue(importerPhone);
		cell_90.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_91 = row_9.createCell(2);
		cell_91.setCellValue(importerFax);
		cell_91.setCellStyle(styles.get("cell_normal_left_no_border_10"));
		Cell cell_92 = row_9.createCell(4);
		cell_92.setCellValue(importerEmail);
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
		HSSFRichTextString rStr12 = new HSSFRichTextString("Nơi sản xuất: " + placeOfManufacture);
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
		HSSFRichTextString rStr13 = new HSSFRichTextString("Số lượng:  " + orderUnit + "  " + uomId);
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
		HSSFRichTextString rStr17 = new HSSFRichTextString("Loại bao bì:");
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
		HSSFRichTextString rStr26 = new HSSFRichTextString("Mục đích sử dụng:");
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
		cell_360.setCellValue(importer.toUpperCase());
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
		cell_410.setCellValue("...........................................(**)");
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
}
