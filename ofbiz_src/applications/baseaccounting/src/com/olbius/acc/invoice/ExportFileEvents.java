package com.olbius.acc.invoice;

import com.olbius.acc.utils.ExcelUtil;
import javolution.util.FastList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExportFileEvents {
	@SuppressWarnings("unchecked")
	public static void exportListVoucherInvoiceExcel(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, String[]> params = request.getParameterMap();
		Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		paramsExtend.put("pagesize", new String[]{"0"});
		paramsExtend.put("sname", new String[]{"JQGetListVoucher"});
		Map<String,Object> context = new HashMap<String,Object>();
		context.put("parameters", paramsExtend);
		context.put("userLogin", userLogin);
		context.put("timeZone", timeZone);
		context.put("locale", locale);
		try {
			Map<String, Object> resultService = dispatcher.runSync("jqxGridGeneralServicer", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return;
			}
			List<GenericValue> listData = (List<GenericValue>)resultService.get("results");
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
			Sheet sheet = wb.createSheet("Sheet1");
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setAutobreaks(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);
			
			sheet.setColumnWidth(0, 7 * 250);
			sheet.setColumnWidth(1, 11 * 320);
			sheet.setColumnWidth(2, 12 * 350);
			sheet.setColumnWidth(3, 12 * 350);
			sheet.setColumnWidth(4, 12 * 320);
			sheet.setColumnWidth(5, 12 * 320);
			sheet.setColumnWidth(6, 15 * 350);
			sheet.setColumnWidth(7, 16 * 350);
			sheet.setColumnWidth(8, 15 * 300);
			sheet.setColumnWidth(9, 14 * 300);
			sheet.setColumnWidth(10, 15 * 300);
			
			int rownum = 1;
			int totalColumns = 10;
			
			Row row = sheet.createRow(rownum);
			row.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 5));
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_no_border_16"), null, UtilProperties.getMessage("BaseAccountingUiLabels", "ListVouchers", locale));
			
			rownum += 2;
			List<String> titles = FastList.newInstance();
			titles.add(UtilProperties.getMessage("BaseSalesUiLabels", "BSSTT", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "VoucherForm", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "VoucherSerial", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "VoucherNumber", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCIssueDate", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "PublicationReceivingDate", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceId", locale));
			titles.add(UtilProperties.getMessage("CommonUiLabels", "CommonStatus", locale));
			titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "AmountNotIncludeTax", locale));
			titles.add(UtilProperties.getMessage("CommonUiLabels", "CommonTax", locale));
			titles.add(UtilProperties.getMessage("CommonUiLabels", "CommonTotal", locale));
			
			row = sheet.createRow(rownum);
			row.setHeight((short) 600);
			for (int i = 0; i < titles.size(); i++) {
				String title = titles.get(i);
				ExcelUtil.createCellOfRow(row, i, styles.get("cell_bold_centered_header_excel_border_10"), null, title);
			}
			int stt = 0;
			if(UtilValidate.isNotEmpty(listData)){
				for(GenericValue data: listData){
					stt++;
					rownum++;
					//String currencyUomId = data.getString("currencyUomId");
					Row tempDataRow = sheet.createRow(rownum);
					tempDataRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_normal_centered_border_full_10"), null, String.valueOf(stt));
					ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, data.get("voucherForm"));
					ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_centered_border_full_10"), null, data.get("voucherSerial"));
					ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_left_centered_border_full_10"), null, data.get("voucherNumber"));
					Timestamp issuedDate = data.getTimestamp("issuedDate");
					Timestamp voucherCreatedDate = data.getTimestamp("voucherCreatedDate");
					ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_normal_centered_border_full_10"), null, format.format(issuedDate));
					ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_normal_centered_border_full_10"), null, format.format(voucherCreatedDate));
					ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_left_centered_border_full_10"), null, data.get("invoiceId"));
					String statusId = data.getString("newStatusId");
					if(statusId != null){
						GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
						ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_left_centered_border_full_10"), null, statusItem.get("description", locale));
					}
					BigDecimal amount = data.getBigDecimal("amount");
					BigDecimal taxAmount = data.getBigDecimal("taxAmount");
					BigDecimal totalAmount = data.getBigDecimal("totalAmount");
					ExcelUtil.createCellOfRow(tempDataRow, 8, styles.get("cell_right_centered_border_full_currency_10"), null, amount);
					ExcelUtil.createCellOfRow(tempDataRow, 9, styles.get("cell_right_centered_border_full_currency_10"), null, taxAmount);
					ExcelUtil.createCellOfRow(tempDataRow, 10, styles.get("cell_right_centered_border_full_currency_10"), null, totalAmount);
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
			ExcelUtil.responseWrite(response, wb, "danh_sach_chung_tu");
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
