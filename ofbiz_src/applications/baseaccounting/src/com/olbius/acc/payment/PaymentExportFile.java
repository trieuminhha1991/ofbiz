package com.olbius.acc.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.payment.entity.Payment;
import com.olbius.acc.utils.ExcelUtil;
import com.olbius.basehr.util.PartyUtil;

public class PaymentExportFile {
	@SuppressWarnings("unchecked")
	public static void exportListToExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Map<String, String[]> params = request.getParameterMap();
			Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);
			paramsExtend.put("pagesize", new String[]{"0"});
			paramsExtend.put("sname", new String[]{"JqxGetListPayments"});
			Map<String,Object> context = new HashMap<String,Object>();
			context.put("parameters", paramsExtend);
			context.put("userLogin", userLogin);
			context.put("timeZone", timeZone);
			context.put("locale", locale);
			Map<String, Object> resultService;
			resultService = dispatcher.runSync("jqxGridGeneralServicer", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return;
			}
			String paymentType = paramsExtend.get("paymentType") != null? ((String[])paramsExtend.get("paymentType"))[0] : null;
			List<Payment> listData = (List<Payment>)resultService.get("results");
			
			/**=============== header =====================*/
			Map<String, Object> info = getAddressInfo(request, delegator, userLogin, locale);
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
			int totalColumns = 11;
			int startColTitle = 2;
			int totalColumnTitle = 9;
			Sheet sheet = wb.createSheet("Sheet1");
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
			sheet.setColumnWidth(0, 10 * 200);
			sheet.setColumnWidth(1, 20 * 256);
			sheet.setColumnWidth(2, 25 * 260);
			if("AR".equals(paymentType)){
				sheet.setColumnWidth(3, 15 * 256);
				sheet.setColumnWidth(4, 25 * 260);
			}else{
				sheet.setColumnWidth(3, 25 * 260);
				sheet.setColumnWidth(4, 15 * 256);
			}
			sheet.setColumnWidth(5, 32 * 260);
			sheet.setColumnWidth(6, 22 * 256);
			sheet.setColumnWidth(7, 20 * 256);
			sheet.setColumnWidth(8, 19 * 256);
			sheet.setColumnWidth(9, 22 * 256);
			sheet.setColumnWidth(10, 22 * 256);
			sheet.setColumnWidth(11, 30 * 256);
			
			int rownum = 0;
			Row row = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_normal_Left_8"), null, info.get("groupName").toString().toUpperCase());
			
			rownum += 1;
			row = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_normal_Left_8"), null, (String) info.get("companyAddress"));
			
			rownum += 3;
			row = sheet.createRow(rownum);
			row.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, startColTitle, startColTitle + totalColumnTitle));
			ExcelUtil.createCellOfRow(row, startColTitle, styles.get("cell_bold_centered_no_border_16"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentList", locale).toUpperCase());
			/**=============== ./header =====================*/
			
			/**=============== body ======================*/
			rownum += 2;
			List<String> titles = FastList.newInstance();
			titles.add(UtilProperties.getMessage("BaseSalesUiLabels", "BSSTT", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentId", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentFromParty", locale));
			if("AP".equals(paymentType)){
				titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentToParty", locale));
				titles.add(UtilProperties.getMessage("BaseSalesUiLabels", "BSOrganizationId", locale));
			}else{
				titles.add(UtilProperties.getMessage("BaseSalesUiLabels", "BSOrganizationId", locale));
				titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentToParty", locale));
			}
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentMethodType", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentTypeId", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCStatusId", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEffectiveDate", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmount", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmountToApply", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCComment", locale));
			
			row = sheet.createRow(rownum);
			row.setHeight((short) 600);
			for (String t : titles) {
				ExcelUtil.createCellOfRow(row, titles.indexOf(t), styles.get("cell_bold_centered_header_excel_border_10"), null, t);
			}
			
			if(UtilValidate.isNotEmpty(listData)){
				int stt = 0;
				for(Payment tempData: listData){
					stt++;
					rownum++;
					//String tempCurrencyUom = tempData.getCurrencyUomId();
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(stt));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.getPaymentCode());
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_centered_border_full_10"), null, tempData.getFullNameFrom());
					if("AP".equals(paymentType)){
						ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.getFullNameTo());
						ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.getPartyCodeTo());
					}else{
						ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, tempData.getPartyCodeFrom());
						ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_centered_border_full_10"), null, tempData.getFullNameTo());
					}
					String tempPaymentMethodId = tempData.getPaymentMethodId();
					GenericValue paymentMethod = delegator.findOne("PaymentMethod", UtilMisc.toMap("paymentMethodId", tempPaymentMethodId), false);
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_left_centered_border_full_10"), null, paymentMethod!= null? paymentMethod.get("description", locale) : "");
					
					String tempPaymentTypeId = tempData.getPaymentTypeId();
					GenericValue paymentTypeGv = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", tempPaymentTypeId), false);
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_left_centered_border_full_10"), null, paymentTypeGv!= null? paymentTypeGv.get("description", locale) : "");
					
					String tempStatusId = tempData.getStatusId();
					GenericValue statusItemGv = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", tempStatusId), false);
					ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_left_centered_border_full_10"), null, statusItemGv!= null? statusItemGv.getString("description") : "");
					
					Timestamp effectiveDate = tempData.getEffectiveDate();
					ExcelUtil.createCellOfRow(tempDataRow, 8, styles.get("cell_left_border_full_date_10"), null, format.format(effectiveDate));
					
					ExcelUtil.createCellOfRow(tempDataRow, 9, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.getAmount());
					ExcelUtil.createCellOfRow(tempDataRow, 10, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.getAmountToApply());
					
					ExcelUtil.createCellOfRow(tempDataRow, 11, styles.get("cell_left_centered_border_full_10"), null, tempData.getComments());
				}
			}else{
				rownum++;
				Row tempRow = sheet.createRow(rownum);
				tempRow.setHeight((short)400);
				ExcelUtil.createCellOfRow(tempRow, totalColumns, styles.get("cell_normal_centered_border_full_10"), null, null);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumns));
				ExcelUtil.createCellOfRow(tempRow, 0, styles.get("cell_normal_centered_border_full_10"), null, 
						UtilProperties.getMessage("WidgetUiLabels", "wgemptydatastring", locale));
			}
			/**=============== ./body ======================*/
			ExcelUtil.responseWrite(response, wb, "danh_sach_thanh_toan");
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Map<String, Object> getAddressInfo(HttpServletRequest request, Delegator delegator,
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
			String fullName = delegator.findOne("PostalAddressDetail", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false).getString("fullName");
			if (UtilValidate.isNotEmpty(fullName)) {
				fullName = fullName.replaceAll(", __", "");
			}
			info.put("companyAddress", fullName);
		}
		return info;
	}
	
	public static void exportListPaymentApplExcel(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String paymentId = request.getParameter("paymentId");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Locale locale = UtilHttp.getLocale(request);
		Workbook wb = new HSSFWorkbook();
		Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
		Sheet sheet = wb.createSheet("Sheet1");
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
		
		sheet.setColumnWidth(0, 8 * 250);
		sheet.setColumnWidth(1, 16 * 256);
		sheet.setColumnWidth(2, 18 * 256);
		sheet.setColumnWidth(3, 18 * 256);
		sheet.setColumnWidth(4, 18 * 256);
		sheet.setColumnWidth(5, 20 * 256);
		sheet.setColumnWidth(6, 20 * 256);
		sheet.setColumnWidth(7, 12 * 256);
		sheet.setColumnWidth(8, 21 * 256);
		sheet.setColumnWidth(9, 24 * 256);
		try {
			String organizationId = PartyUtil.getRootOrganization(delegator);
			GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
			if(payment == null){
				return;
			}
			GenericValue partyToDetail = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", payment.get("partyIdTo")), false);
			GenericValue organization = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), false);
			int rownum = 0;
			Row row = sheet.createRow(rownum);
			row.setHeight((short)350);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 3));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_10"), null, organization.getString("groupName").toUpperCase());
			Map<String, Object> resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("partyId", organizationId, "userLogin", userLogin,
																										"contactMechPurposeTypeId", "PRIMARY_LOCATION"));
			String contactMechId = (String)resultService.get("contactMechId");
			if(contactMechId != null){
				rownum++;
				row = sheet.createRow(rownum);
				row.setHeight((short)350);
				String districtGeoId = (String)resultService.get("districtGeoId");
				String stateProvinceGeoId = (String)resultService.get("stateProvinceGeoId");
				String address1 = (String)resultService.get("address1");
				GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
				String wardGeoId = postalAddress.getString("wardGeoId");
				String addressFull = address1;
				if(wardGeoId != null){
					GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", wardGeoId), false);
					addressFull += ", " + geo.getString("geoName");
				}
				if(districtGeoId != null){
					GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId), false);
					addressFull += ", " + geo.getString("geoName");
				}
				if(stateProvinceGeoId != null){
					GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
					addressFull += ", " + geo.getString("geoName");
				}
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_10"), null, addressFull);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 3));
			}
			
			rownum += 2;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 8));
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("AccountingUiLabels", "AccountingBillingAccount", locale));
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 8));
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCBatchName", locale));
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 8));
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCVoucherNumber", locale));
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 8));
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_left_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaidDate", locale));
			
			rownum += 2;
			row = sheet.createRow(rownum);
			row.setHeight((short)600);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_no_border_16"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BangKeThanhToan", locale).toUpperCase());
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 6));
			String content = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMaCungCap", locale) + ": " + partyToDetail.getString("partyCode");
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_center_no_border_10"), null, content);
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 7));
			content = UtilProperties.getMessage("BasePOUiLabels", "POSupplier", locale) + ": " + partyToDetail.getString("groupName");
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_center_no_border_10"), null, content);
			
			rownum += 2;
			row = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_right_no_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTotal", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_right_no_border_currency_10"), Cell.CELL_TYPE_NUMERIC, payment.getBigDecimal("amount").doubleValue());
			
			rownum++;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
			content = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCacHoaDonCuaDonVi", locale) + " " + organization.getString("groupName");
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_10"), null, content.toUpperCase());
			content = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCurrencyUom", locale) + ": " + payment.getString("currencyUomId");
			ExcelUtil.createCellOfRow(row, 9, styles.get("cell_normal_right_10"), null, content);
			
			/**====================== table header =============**/
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short)350);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 6));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 0, 0));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 7, 8));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 9, 9));
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCSeqId", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_center_blue_border_not_bottom_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceSupplier", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseLogisticsUiLabels", "DiscountUpper", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPayment", locale).toUpperCase());
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short)350);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 6));
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_center_blue_border_not_top_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTriGiaHoaDon", locale).toUpperCase());
			
			rownum++;
			row = sheet.createRow(rownum);
			row.setHeight((short)600);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_center_blue_border_10"), null, "");
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceId", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "VoucherForm", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "VoucherSerial", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNumber", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseSalesUiLabels", "BSDate", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTriGia", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 7, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPercent", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 8, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTotal", locale).toUpperCase());
			ExcelUtil.createCellOfRow(row, 9, styles.get("cell_bold_center_blue_border_10"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCMoney", locale).toUpperCase());
			
			List<GenericValue> paymentApplList = delegator.findByAnd("PaymentApplicationAndInvoice", UtilMisc.toMap("paymentId", paymentId), UtilMisc.toList("-invoiceDate"), false);
			
			/**====================== table body =============**/
			if(UtilValidate.isNotEmpty(paymentApplList)){
				int stt = 0;
				for(GenericValue paymentAppl: paymentApplList){
					rownum++;
					stt++;
					String invoiceId = paymentAppl.getString("invoiceId");
					List<GenericValue> voucherInvoiceList = delegator.findByAnd("VoucherInvoice", UtilMisc.toMap("invoiceId", invoiceId), null, false);
					String voucherForm = "";
					String voucherSerial = "";
					String voucherNumber = "";
					if(UtilValidate.isNotEmpty(voucherInvoiceList)){
						String voucherId = voucherInvoiceList.get(0).getString("voucherId");
						GenericValue voucher = delegator.findOne("Voucher", UtilMisc.toMap("voucherId", voucherId), false);
						voucherForm = voucher.getString("voucherForm");
						voucherSerial = voucher.getString("voucherSerial");
						voucherNumber = voucher.getString("voucherNumber");
					}
					Timestamp invoiceDate = paymentAppl.getTimestamp("invoiceDate");
					BigDecimal invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
					BigDecimal appliedAmount = paymentAppl.getBigDecimal("amountApplied");
					row = sheet.createRow(rownum);
					row.setHeight((short)400);
					ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, String.valueOf(stt));
					ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_centered_border_full_10"), null, invoiceId);
					ExcelUtil.createCellOfRow(row, 2, styles.get("cell_normal_centered_border_full_10"), null, voucherForm);
					ExcelUtil.createCellOfRow(row, 3, styles.get("cell_normal_centered_border_full_10"), null, voucherSerial);
					ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_centered_border_full_10"), null, voucherNumber);
					ExcelUtil.createCellOfRow(row, 5, styles.get("cell_normal_centered_border_full_10"), null, format.format(invoiceDate));
					ExcelUtil.createCellOfRow(row, 6, styles.get("cell_right_centered_border_full_currency_10"), null, invoiceAmount.doubleValue());
					ExcelUtil.createCellOfRow(row, 7, styles.get("cell_right_centered_border_full_currency_10"), null, String.valueOf(0));
					ExcelUtil.createCellOfRow(row, 8, styles.get("cell_right_centered_border_full_currency_10"), null, 0d);
					ExcelUtil.createCellOfRow(row, 9, styles.get("cell_right_centered_border_full_currency_10"), null, appliedAmount);
				}
			}else{
				rownum++;
				row = sheet.createRow(rownum);
				row.setHeight((short)400);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
				ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_centered_border_full_10"), null, UtilProperties.getMessage("WidgetUiLabels", "wgemptydatastring", locale));
			}
			ExcelUtil.responseWrite(response, wb, "bang_ke_thanh_toan");
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
