package com.olbius.acc.invoice;

import com.olbius.acc.invoice.entity.Invoice;
import com.olbius.acc.utils.ExcelUtil;
import com.olbius.basehr.util.PartyUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class InvoiceListExcel {
	public final static String RESOURCE = "SGCUiLabels";
	public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@SuppressWarnings("unchecked")
	public static void export(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		try {
			Map<String, String[]> params = request.getParameterMap();
			Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);
			paramsExtend.put("pagesize", new String[]{"0"});
			paramsExtend.put("sname", new String[]{"JqxGetListInvoices"});
			Map<String,Object> context = new HashMap<String,Object>();
			context.put("parameters", paramsExtend);
			context.put("userLogin", userLogin);
			context.put("timeZone", timeZone);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("jqxGridGeneralServicer", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return ;
			}
			String invoiceType = paramsExtend.get("invoiceType") != null? ((String[])paramsExtend.get("invoiceType"))[0] : null;
			List<Invoice> listData = (List<Invoice>)resultService.get("results");
			
			List<String> statusList = FastList.newInstance();
			String strFilterListFields = ((String[])paramsExtend.get("filterListFields") != null) ? ((String) paramsExtend.get("filterListFields")[0]) : ("");
			if ((strFilterListFields != null && !strFilterListFields.isEmpty())){
				String[] arrField = strFilterListFields.split("\\|OLBIUS\\|");
				for (int i = 1; i < arrField.length; i++) {
					String[] arrTmp = arrField[i].split("\\|SUIBLO\\|");
					String fieldName = arrTmp[0];
					if("newStatusId".equals(fieldName)){
						String fieldValue = arrTmp[1].toString();
						statusList.add(fieldValue);
					}
				}
			}
			
			/**=============== header =====================*/
			Map<String, Object> info = getAddressInfo(request, delegator, userLogin, locale);
			// start renderExcel
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
			
			Sheet sheet = sheetSetting(wb, "Sheet1");
			int rownum = 0;
				
			Row row = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_normal_Left_8"), null, info.get("groupName").toString().toUpperCase());
			
			rownum += 1;
			row = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_normal_Left_8"), null, (String) info.get("companyAddress"));
			
			rownum += 3;
			row = sheet.createRow(rownum);
			row.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 6));
			StringBuffer titleExcel = new StringBuffer(); 
			titleExcel.append(UtilProperties.getMessage(RESOURCE, "SGCBangKeHoaDon", locale));
			if(UtilValidate.isNotEmpty(statusList)){
				int i = 1;
				for(String statusId: statusList){
					GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
					if(status != null){
						titleExcel.append(" ");
						titleExcel.append(status.get("description"));
						if(i++ < statusList.size()){
							titleExcel.append(",");
						}
					}
				}
			}
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_centered_no_border_16"), null, titleExcel.toString().toUpperCase());
			
			/**=============== body ======================*/
			rownum += 2;
			List<String> titles = FastList.newInstance();
			titles.add(UtilProperties.getMessage("BaseSalesUiLabels", "BSSTT", locale));
			titles.add(UtilProperties.getMessage("AccountingUiLabels", "AccountingInvNr", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "SGCNgayHD", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "SGCTongTienHD", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "SGCNgayGioValidate", locale));
			if("AR".equals(invoiceType)){
				titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCustomerId", locale));
				titles.add(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCustomerName", locale));
			}else{
				titles.add(UtilProperties.getMessage("BaseLogisticsUiLabels", "SupplierId", locale));
				titles.add(UtilProperties.getMessage("BaseLogisticsUiLabels", "SupplierName", locale));
				
			}
			titles.add(UtilProperties.getMessage(RESOURCE, "SGCTinhTrangTT", locale));

			row = sheet.createRow(rownum);
			row.setHeight((short) 600);
			for (String t : titles) {
				ExcelUtil.createCellOfRow(row, titles.indexOf(t), styles.get("cell_bold_centered_header_excel_border_10"), null, t);
			}
			
			int stt = 0;
			for(Invoice tempData: listData){
				stt++;
				rownum++;           
				//String tempCurrencyUom = tempData.getCurrencyUomId();
				Row tempDataRow = sheet.createRow(rownum);
				tempDataRow.setHeight((short)400);
				ExcelUtil.createCellOfRow(tempDataRow, 0, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(stt));
				ExcelUtil.createCellOfRow(tempDataRow, 1, styles.get("cell_left_centered_border_full_10"), null, tempData.getInvoiceId());
				
				Timestamp invoiceDate = tempData.getInvoiceDate();
				ExcelUtil.createCellOfRow(tempDataRow, 2, styles.get("cell_left_border_full_date_10"), null, format.format(invoiceDate));
				
				ExcelUtil.createCellOfRow(tempDataRow, 3, styles.get("cell_right_centered_border_full_currency_10"), null, tempData.getTotal());
				
				ExcelUtil.createCellOfRow(tempDataRow, 4, styles.get("cell_left_border_full_date_10"), null, "");
				
				String partyId = "", partyName = "";
				if("AR".equals(invoiceType)){
					partyId = tempData.getPartyId();
					partyName = tempData.getFullNameTo();
				}else{
					partyId = tempData.getPartyIdFrom();
					partyName = tempData.getFullNameFrom();
				}
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				String partyCode = party.getString("partyCode");
				ExcelUtil.createCellOfRow(tempDataRow, 5, styles.get("cell_left_border_full_date_10"), null, partyCode);
				ExcelUtil.createCellOfRow(tempDataRow, 6, styles.get("cell_left_border_full_date_10"), null, partyName);
				
				String newStatusId = tempData.getNewStatusId();
				String newStatusDesc = "";
				if(newStatusId != null){
					GenericValue newStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", newStatusId), false);
					newStatusDesc = newStatus.getString("description");
				}
				ExcelUtil.createCellOfRow(tempDataRow, 7, styles.get("cell_left_border_full_date_10"), null, newStatusDesc);
			}
			/**=============== ./end ======================*/
			ExcelUtil.responseWrite(response, wb, "bang-ke-hoa-don");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, Object> getAddressInfo(HttpServletRequest request, GenericDelegator delegator,
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
		sheet.setColumnWidth(0, 10 * 200);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 22 * 256);
		sheet.setColumnWidth(3, 20 * 256);
		sheet.setColumnWidth(4, 20 * 256);
		sheet.setColumnWidth(5, 20 * 256);
		sheet.setColumnWidth(6, 40 * 256);
		sheet.setColumnWidth(7, 20 * 256);
		return sheet;
	}
}
