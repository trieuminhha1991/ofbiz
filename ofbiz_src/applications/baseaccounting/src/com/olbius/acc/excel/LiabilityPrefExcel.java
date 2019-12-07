package com.olbius.acc.excel;

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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class LiabilityPrefExcel {

	public final static String RESOURCE = "BaseAccountingUiLabels";
    private static final String LIABILITY_RECEIVE = "131";
    private static final String LIABILITY_PAYABLE = "331";
	public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	
	public static void exportLiabilityPrefExcel(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		TimeZone timezone = UtilHttp.getTimeZone(request);
		
		Map<String, String[]> params = request.getParameterMap();
		
		Map<String, String[]> paramExtend = FastMap.newInstance();
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("timeZone", timezone);
		context.put("locale", locale);
		
		paramExtend.put("partyIdFrom",params.get("partyIdFrom"));
		paramExtend.put("partyIdTo", params.get("partyIdTo"));
		paramExtend.put("fromDate", params.get("fromDate"));
		paramExtend.put("thruDate", params.get("thruDate"));
		paramExtend.put("sname", new String[]{"JQGetListProductLiabilityPref"});
		paramExtend.put("pagesize", new String[]{"0"});
		
		context.put("parameters", paramExtend);
		try {
			Map<String, Object> resultService = dispatcher.runSync("jqxGridGeneralServicer", context);
			Map<String, Object> prefInfor = FastMap.newInstance();
			context.remove("parameters");
			if(UtilValidate.isNotEmpty(params.get("partyIdFrom")[0]) && UtilValidate.isNotEmpty(params.get("partyIdTo")[0])
					&& UtilValidate.isNotEmpty(params.get("fromDate")[0]) && UtilValidate.isNotEmpty(params.get("thruDate")[0]) ){
				context.put("partyIdFrom", params.get("partyIdFrom")[0]);
				context.put("partyIdTo",params.get("partyIdTo")[0]);
				context.put("fromDate", params.get("fromDate")[0]);
				context.put("thruDate", params.get("thruDate")[0]);
				
				prefInfor = dispatcher.runSync("getLiabilityPreferenceInfo", context);
			}
			if(!ServiceUtil.isSuccess(prefInfor)||!ServiceUtil.isSuccess(resultService)){
				return ;
			}
			
			List<Map<String, Object>> listData = (List<Map<String, Object>>)resultService.get("results");
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
			
			sheet.setColumnWidth(1, 7 * 180);
			sheet.setColumnWidth(2, 15 * 800);
			sheet.setColumnWidth(3, 22 * 300);
			sheet.setColumnWidth(4, 11 * 280);
			sheet.setColumnWidth(5, 20 * 320);
			sheet.setColumnWidth(6, 20 * 320);
			
			/** ================ header ====================*/
			Map<String, Object> info = getAddressInfo(request, delegator, userLogin, locale);
			int rownum =0;
			Row row = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_normal_Left_8"), null, info.get("groupName").toString().toUpperCase());
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_normal_Left_8"), null, UtilProperties.getMessage(RESOURCE, "BACCVietNamNational", locale).toString().toUpperCase());
			
			rownum += 1;
			row = sheet.createRow(rownum);
			ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_normal_Left_8"), null, (String) info.get("companyAddress"));
			ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_normal_Left_8"), null, UtilProperties.getMessage(RESOURCE, "BACCVietNamTargetNational", locale).toString().toUpperCase());
			
			rownum +=3;
			row = sheet.createRow(rownum);
			row.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 5));
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_no_border_16"), null, UtilProperties.getMessage(RESOURCE, "BACCDebtReconciliation", locale));
			
			rownum +=2;
			row = sheet.createRow(rownum);
			row.setHeight((short) 300);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 5));
			StringBuffer timeMessage = new StringBuffer(UtilProperties.getMessage(RESOURCE, "BACCLiabilityPref", locale)+" ");
			timeMessage.append(UtilProperties.getMessage(RESOURCE, "ExcelFromDate", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("fromDate")[0])){
				Date fromDate = new Date((Long)Long.parseLong(params.get("fromDate")[0]));
				timeMessage.append(format.format(fromDate).toString()+" ");
			}
			
			timeMessage.append(UtilProperties.getMessage(RESOURCE, "ExcelThruDate", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("thruDate")[0])){
				Date thruDate = new Date((Long)Long.parseLong(params.get("thruDate")[0]));
				timeMessage.append(format.format(thruDate).toString()+" ");
			}
			ExcelUtil.createCellOfRow(row, 2, styles.get("cell_bold_centered_no_border_10"), null, timeMessage.toString());
			
			rownum +=2;
			row= sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 3));
			StringBuffer messagePartyA = new StringBuffer("1. ");
			messagePartyA.append(UtilProperties.getMessage(RESOURCE, "BACCAParty", locale));
			messagePartyA.append("("+ UtilProperties.getMessage(RESOURCE, "BACCThePurchaser", locale) +") : ");
			if(UtilValidate.isNotEmpty(params.get("partyFromName")[0])){
				messagePartyA.append(params.get("partyFromName")[0]);
			}
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_left_no_border_10"), null, messagePartyA.toString());
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 6));
			StringBuffer messagePartyB = new StringBuffer("2. ");
			messagePartyB.append(UtilProperties.getMessage(RESOURCE, "BACCBParty", locale));
			messagePartyB.append("(" + UtilProperties.getMessage(RESOURCE, "BACCTheSeller", locale)+"): ");
			if(UtilValidate.isNotEmpty(params.get("partyToName")[0])){
				messagePartyB.append(params.get("partyToName")[0]);
			}
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_left_no_border_10"), null, messagePartyB.toString());
			
			rownum +=1;
			row =sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 3));
			messagePartyA = new StringBuffer(" -");
			messagePartyA.append(UtilProperties.getMessage("CommonUiLabels", "CommonAddress1", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("address1From")[0])){
				messagePartyA.append(params.get("address1From")[0]);
			}
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyA.toString());
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 6));
			messagePartyB = new StringBuffer(" -");
			messagePartyB.append(UtilProperties.getMessage("CommonUiLabels", "CommonAddress1", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("address1To")[0])){
				messagePartyB.append(params.get("address1To")[0]);
			}
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyB.toString());
			
			rownum +=1;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));
			messagePartyA = new StringBuffer(" -");
			messagePartyA.append(UtilProperties.getMessage("CommonUiLabels", "CommonTelephoneAbbr", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("phoneNbrFrom")[0])){
				messagePartyA.append(params.get("phoneNbrFrom")[0]);
			}
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyA.toString());
			messagePartyA = new StringBuffer("Fax: ");
			if(UtilValidate.isNotEmpty(params.get("faxFrom")[0])){
				messagePartyA.append(params.get("faxFrom")[0]);
			}
			ExcelUtil.createCellOfRow(row, 3, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyA.toString());
			
			messagePartyB = new StringBuffer(" -");
			messagePartyB.append(UtilProperties.getMessage("CommonUiLabels", "CommonTelephoneAbbr", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("phoneNbrTo")[0])){
				messagePartyB.append(params.get("phoneNbrTo")[0]);
			}
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyB.toString());
			messagePartyB = new StringBuffer("Fax: ");
			if(UtilValidate.isNotEmpty(params.get("faxTo")[0])){
				messagePartyA.append(params.get("faxTo")[0]);
			}
			ExcelUtil.createCellOfRow(row, 6, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyA.toString());
			
			rownum +=1;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 3));
			messagePartyA = new StringBuffer(" -");
			messagePartyA.append(UtilProperties.getMessage(RESOURCE, "PartyRepresent", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("representativeFrom")[0])){
				messagePartyA.append(params.get("representativeFrom")[0]);
			}
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyA.toString());
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 6));
			messagePartyB = new StringBuffer(" -");
			messagePartyB.append(UtilProperties.getMessage(RESOURCE, "PartyRepresent", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("representativeTo")[0])){
				messagePartyB.append(params.get("representativeTo")[0]);
			}
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyB.toString());
			
			rownum +=1;
			row = sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 3));
			messagePartyA = new StringBuffer(" -"+ UtilProperties.getMessage("BaseHRUiLabels", "HRCommonPosition", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("positionFrom")[0])){
				messagePartyA.append(params.get("positionFrom")[0]);
			}
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyA.toString());
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 6));
			messagePartyB = new StringBuffer(" -"+ UtilProperties.getMessage("BaseHRUiLabels", "HRCommonPosition", locale)+": ");
			if(UtilValidate.isNotEmpty(params.get("positionTo")[0])){
				messagePartyB.append(params.get("positionTo")[0]);
			}
			ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_wrap_text_10"), null, messagePartyB.toString());
			
			rownum +=2;
			row= sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 6));
			StringBuffer openLia = new StringBuffer(UtilProperties.getMessage(RESOURCE, "BACCOpenLiability", locale)+": ");
			if(UtilValidate.isNotEmpty(prefInfor.get("openingBalance"))){
				openLia.append(prefInfor.get("openingBalance").toString());
			}
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_wrap_text_10"), null, openLia.toString());
			
			rownum +=1;
			row= sheet.createRow(rownum);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 6));
			ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_wrap_text_10"), null, UtilProperties.getMessage(RESOURCE, "BACCPostedAmount", locale)+" :");
			
			List<String> titles = FastList.newInstance();
			titles.add(UtilProperties.getMessage(RESOURCE, "BACCSeqId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BACCProductName", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BACCEquipQuantityUom", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BACCQuantity", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BACCUnitCost", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BACCTotal", locale));
			
			rownum +=2;
			row = sheet.createRow(rownum);
			row.setHeight((short)600);
			
			for(String t: titles){
				ExcelUtil.createCellOfRow(row, titles.indexOf(t)+1, styles.get("cell_bold_centered_header_excel_border_10"), null, t);
			}
			
			rownum++;
			if(UtilValidate.isNotEmpty(listData)){
				int i =0;
				for(Map<String, Object> item : listData){
					i++;
					Row temRow = sheet.createRow(rownum);
					temRow.setHeight((short)400);
					ExcelUtil.createCellOfRow(temRow, 1, styles.get("cell_left_centered_border_full_10"), null, String.valueOf(i));
					ExcelUtil.createCellOfRow(temRow, 2, styles.get("cell_left_centered_border_full_10"), null, item.get("productName"));
					ExcelUtil.createCellOfRow(temRow, 3, styles.get("cell_left_centered_border_full_10"), null, item.get("quantityUomDesc"));
					ExcelUtil.createCellOfRow(temRow, 4, styles.get("cell_left_centered_border_full_10"), null, item.get("quantity"));
					ExcelUtil.createCellOfRow(temRow, 5, styles.get("cell_left_centered_border_full_10"), null, item.get("amount"));
					ExcelUtil.createCellOfRow(temRow, 6, styles.get("cell_left_centered_border_full_10"), null, item.get("subTotalAmount"));
					rownum++;
				}
				rownum +=2;
				row= sheet.createRow(rownum);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 6));
				StringBuffer amountPaid = new StringBuffer(UtilProperties.getMessage(RESOURCE, "BACCAmountPaidByPartyA", locale)+": ");
				if(UtilValidate.isNotEmpty(prefInfor.get("amountPaid"))){
					amountPaid.append(prefInfor.get("amountPaid").toString());
				}
				ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_left_no_border_10"), null, amountPaid.toString());
				
				rownum +=1;
				row= sheet.createRow(rownum);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 6));
				StringBuffer amountNotPaid = new StringBuffer(UtilProperties.getMessage("BaseHRUiLabels", "HRCommonConclusion", locale)+": ");
				amountNotPaid.append(UtilProperties.getMessage(RESOURCE, "BACCTinhDenHetNgay", locale)+" ");
				if(UtilValidate.isNotEmpty(params.get("thruDate")[0])){
					Date thruDate = new Date((Long)Long.parseLong(params.get("thruDate")[0]));
					amountNotPaid.append(format.format(thruDate).toString()+" ");
				}
				amountNotPaid.append(UtilProperties.getMessage(RESOURCE, "BACCBenAThanhToanCho", locale)+" ");
				if(UtilValidate.isNotEmpty(params.get("partyToName")[0])){
					amountNotPaid.append(params.get("partyToName")[0].toString()+ " : ");
				}else{
					amountNotPaid.append("__________: ");
				}
				if(UtilValidate.isNotEmpty(prefInfor.get("amountNotPaid"))){
					amountNotPaid.append(prefInfor.get("amountNotPaid").toString()+"");
				}
				ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_left_no_border_10"), null, amountNotPaid.toString());
			}else{
				Row tempRow = sheet.createRow(rownum);
				tempRow.setHeight((short)400);
				ExcelUtil.createCellOfRow(tempRow, 0, styles.get("cell_normal_centered_border_full_10"), null, null);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
				ExcelUtil.createCellOfRow(tempRow, 0, styles.get("cell_normal_centered_border_full_10"), null,
						UtilProperties.getMessage("WidgetUiLabels", "wgemptydatastring", locale));
			}
			ExcelUtil.responseWrite(response, wb, "bang-doi-chieu-cong-no");
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			
		}
	}
    public static void exportLiabilityPrefExcelNew(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        TimeZone timezone = UtilHttp.getTimeZone(request);
        Map<String, String[]> params = request.getParameterMap();

        Map<String, Object> context = FastMap.newInstance();
        context.put("userLogin", userLogin);
        context.put("timeZone", timezone);
        context.put("locale", locale);

        try {
            String organizationId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            Map<String, Object> prefInfor = FastMap.newInstance();
            if (UtilValidate.isNotEmpty(params.get("partyIdFrom")[0]) && UtilValidate.isNotEmpty(params.get("partyIdTo")[0])
                    && UtilValidate.isNotEmpty(params.get("fromDate")[0]) && UtilValidate.isNotEmpty(params.get("thruDate")[0])) {
                context.put("partyIdFrom", params.get("partyIdFrom")[0]);
                context.put("partyIdTo",params.get("partyIdTo")[0]);
                context.put("fromDate", params.get("fromDate")[0]);
                context.put("thruDate", params.get("thruDate")[0]);
                prefInfor = dispatcher.runSync("getLiabilityPreferenceInfoNew", context);
            }
            if (!ServiceUtil.isSuccess(prefInfor)) {
                return ;
            }

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

            sheet.setColumnWidth(1, 7 * 180);
            sheet.setColumnWidth(2, 15 * 800);
            sheet.setColumnWidth(3, 22 * 300);
            sheet.setColumnWidth(4, 11 * 280);
            sheet.setColumnWidth(5, 21 * 320);
            sheet.setColumnWidth(6, 20 * 320);

            /** ================ header ====================*/
            Map<String, Object> info = getAddressInfo(request, delegator, userLogin, locale);
            int rownum = 0;
            Row row = sheet.createRow(rownum);
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_no_border_12"), null, info.get("groupName").toString().toUpperCase());
            ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_no_border_12"), null, UtilProperties.getMessage(RESOURCE, "BACCVietNamNational", locale).toString().toUpperCase());

            rownum += 1;
            row = sheet.createRow(rownum);
            ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_no_border_12"), null, UtilProperties.getMessage(RESOURCE, "BACCVietNamTargetNational", locale).toString());

            rownum += 1;
            row = sheet.createRow(rownum);
            ExcelUtil.createCellOfRow(row, 5, styles.get("cell_bold_centered_no_border_12"), null, "----------oOo----------");

            rownum += 2;
            row = sheet.createRow(rownum);
            row.setHeight((short) 400);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_centered_no_border_14"), null, UtilProperties.getMessage(RESOURCE, "BACCDebtReconciliation", locale).toString().toUpperCase());

            rownum += 1;
            row = sheet.createRow(rownum);
            row.setHeight((short) 300);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
            String timeMessage = UtilProperties.getMessage(RESOURCE, "ExcelFromDate", locale) + ": ";
            if(UtilValidate.isNotEmpty(params.get("fromDate")[0])){
                Date fromDate = new Date((Long)Long.parseLong(params.get("fromDate")[0]));
                timeMessage += (format.format(fromDate).toString() + " ");
            }

            timeMessage += UtilProperties.getMessage(RESOURCE, "ExcelThruDate", locale) + ": ";
            if(UtilValidate.isNotEmpty(params.get("thruDate")[0])){
                Date thruDate = new Date((Long)Long.parseLong(params.get("thruDate")[0]));
                timeMessage += format.format(thruDate).toString() + " ";
            }
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_center_no_border_10"), null, timeMessage.toString());

            rownum += 1;
            row = sheet.createRow(rownum);
            row.setHeight((short) 300);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
            String glAccountId = "";
            if(organizationId.equals(params.get("partyIdFrom")[0])) {
                glAccountId = LIABILITY_RECEIVE;
            } else {
                glAccountId = LIABILITY_PAYABLE;
            }
            GenericValue glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), false);
            String accountName = "";
            if (UtilValidate.isNotEmpty(glAccount)) {
                accountName = glAccount.getString("accountName");
            }
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_center_no_border_10"), null,
                    UtilProperties.getMessage(RESOURCE, "BACCGlAccountId", locale) + ": " + glAccountId + " - " + accountName);

            rownum += 1;
            row = sheet.createRow(rownum);
            row.setHeight((short) 300);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 5));
            String supplier = "";
            if(organizationId.equals(params.get("partyIdFrom")[0])) {
                supplier += UtilProperties.getMessage(RESOURCE, "BACCCustomer", locale) + ": ";
                if(UtilValidate.isNotEmpty(params.get("partyIdTo")[0])){
                    supplier += params.get("partyIdTo")[0] + " - ";
                }
                if(UtilValidate.isNotEmpty(params.get("partyFromName")[0])){
                    supplier += params.get("partyFromName")[0].toString().toUpperCase();
                }
            } else {
                supplier += UtilProperties.getMessage("BaseLogisticsUiLabels", "Supplier", locale) + ": ";
                if(UtilValidate.isNotEmpty(params.get("partyIdFrom")[0])){
                    supplier += params.get("partyIdFrom")[0] + " - ";
                }
                if(UtilValidate.isNotEmpty(params.get("partyToName")[0])){
                    supplier += params.get("partyToName")[0].toString().toUpperCase();
                }
            }
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_center_no_border_10"), null, supplier);

            rownum += 2;
            row = sheet.createRow(rownum);
            row.setHeight((short) 300);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
            Date now = new Date();
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_left_no_border_wrap_text_10"), null, UtilProperties.getMessage(RESOURCE, "BACCToday", locale) + " "
                    + format.format(now).toString() + ", " + UtilProperties.getMessage(RESOURCE, "BACCWeAre", locale) + ": ");

            rownum += 1;
            row = sheet.createRow(rownum);
            row.setHeight((short) 300);
            String representativeFrom = "+ " + UtilProperties.getMessage(RESOURCE, "BACCMrMs", locale).toString().toUpperCase() + ": ";
            if(UtilValidate.isNotEmpty(params.get("representativeFrom")[0])){
                representativeFrom += params.get("representativeFrom")[0].toString().toUpperCase();
            }
            String partyA = UtilProperties.getMessage(RESOURCE, "BACCARep", locale) + ": ";
            if(UtilValidate.isNotEmpty(params.get("partyFromName")[0])){
                partyA += params.get("partyFromName")[0].toString().toUpperCase();
            }
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_left_no_border_10"), null, representativeFrom);
            ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_left_no_border_10"), null, partyA);

            rownum += 1;
            row = sheet.createRow(rownum);
            row.setHeight((short) 300);
            String representativeTo = "+ " + UtilProperties.getMessage(RESOURCE, "BACCMrMs", locale).toString().toUpperCase() + ": ";
            if(UtilValidate.isNotEmpty(params.get("representativeTo")[0])){
                representativeTo += params.get("representativeTo")[0].toString().toUpperCase();
            }
            String partyB = UtilProperties.getMessage(RESOURCE, "BACCBRep", locale) + ": ";
            if(UtilValidate.isNotEmpty(params.get("partyToName")[0])){
                partyB += params.get("partyToName")[0].toString().toUpperCase();
            }
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_left_no_border_10"), null, representativeTo);
            ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_left_no_border_10"), null, partyB);

            rownum += 1;
            row = sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_left_no_border_10"), null, UtilProperties.getMessage(RESOURCE, "BACCLiabilityDesc", locale) + ":");

            rownum += 2;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_no_border_10"), null,
                    "I. " + UtilProperties.getMessage(RESOURCE, "BACCOpeningBalance", locale).toString().toUpperCase());

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "1. " + UtilProperties.getMessage(RESOURCE, "BACCOpeningDrBalance", locale));
            if (UtilValidate.isNotEmpty(prefInfor.get("openingDrAmount")) && ((BigDecimal) prefInfor.get("openingDrAmount")).compareTo(BigDecimal.ZERO) != 0) {
                ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_right_no_border_currency_10"), null, (BigDecimal) prefInfor.get("openingDrAmount"));
            } else {
                ExcelUtil.createCellOfRow(row, 3, styles.get("cell_right_no_border_10"), null, "-");
            }
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_10"), null, "VND");

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "2. " + UtilProperties.getMessage(RESOURCE, "BACCOpeningCrBalance", locale));
            if (UtilValidate.isNotEmpty(prefInfor.get("openingCrAmount")) && ((BigDecimal) prefInfor.get("openingCrAmount")).compareTo(BigDecimal.ZERO) != 0) {
                ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_right_no_border_currency_10"), null, (BigDecimal) prefInfor.get("openingCrAmount"));
            } else {
                ExcelUtil.createCellOfRow(row, 3, styles.get("cell_right_no_border_10"), null, "-");
            }
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_10"), null, "VND");

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_no_border_10"), null,
                    "II. " + UtilProperties.getMessage(RESOURCE, "BACCPostedDrAmount", locale).toString().toUpperCase());

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            BigDecimal returnSupplierAmount = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(prefInfor.get("returnSupplierAmount"))) {
                returnSupplierAmount = (BigDecimal) prefInfor.get("returnSupplierAmount");
            }
            if(organizationId.equals(params.get("partyIdFrom")[0])) {
                ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "- " + UtilProperties.getMessage(RESOURCE, "BACCXuatTraKH", locale));
            } else {
                ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "- " + UtilProperties.getMessage(RESOURCE, "BACCXuatTraNCC", locale));
            }
            ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_right_no_border_currency_10"), null, returnSupplierAmount);
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_10"), null, "VND");

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            BigDecimal paymentAmount = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(prefInfor.get("paymentAmount"))) {
                paymentAmount = (BigDecimal) prefInfor.get("paymentAmount");
            }
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "- " + UtilProperties.getMessage(RESOURCE, "BACCPayment", locale));
            ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_right_no_border_currency_10"), null, paymentAmount);
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_10"), null, "VND");

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_no_border_10"), null,
                    "III. " + UtilProperties.getMessage(RESOURCE, "BACCPostedCrAmount", locale).toString().toUpperCase());

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            BigDecimal goodsAmount = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(prefInfor.get("goodsAmount"))) {
                goodsAmount = (BigDecimal) prefInfor.get("goodsAmount");
            }
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "- " + UtilProperties.getMessage(RESOURCE, "BACCTienHang", locale));
            ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_right_no_border_currency_10"), null, goodsAmount);
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_10"), null, "VND");

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            BigDecimal receiveableAmount = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(prefInfor.get("receiveableAmount"))) {
                receiveableAmount = (BigDecimal) prefInfor.get("receiveableAmount");
            }
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "- " + UtilProperties.getMessage(RESOURCE, "BACCThuTienXuatTra", locale));
            ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_right_no_border_currency_10"), null, receiveableAmount);
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_10"), null, "VND");

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_bold_left_no_border_10"), null,
                    "IV. " + UtilProperties.getMessage(RESOURCE, "BACCEndingBalance", locale).toString().toUpperCase());

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "1. " + UtilProperties.getMessage(RESOURCE, "BACCOpeningDrBalance", locale));
            if (UtilValidate.isNotEmpty(prefInfor.get("endingDrAmount")) && ((BigDecimal) prefInfor.get("endingDrAmount")).compareTo(BigDecimal.ZERO) != 0) {
                ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_right_no_border_currency_10"), null, (BigDecimal) prefInfor.get("endingDrAmount"));
            } else {
                ExcelUtil.createCellOfRow(row, 3, styles.get("cell_right_no_border_10"), null, "");
            }
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_10"), null, "VND");

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_normal_left_no_border_10"), null, "2. " + UtilProperties.getMessage(RESOURCE, "BACCOpeningCrBalance", locale));
            if (UtilValidate.isNotEmpty(prefInfor.get("endingCrAmount")) && ((BigDecimal) prefInfor.get("endingCrAmount")).compareTo(BigDecimal.ZERO) != 0) {
                ExcelUtil.createCellOfRow(row, 3, styles.get("cell_bold_right_no_border_currency_10"), null, (BigDecimal) prefInfor.get("endingCrAmount"));
            } else {
                ExcelUtil.createCellOfRow(row, 3, styles.get("cell_right_no_border_10"), null, "");
            }
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_normal_left_no_border_10"), null, "VND");

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            String amountNotPaidText = "";
            BigDecimal amountNotPaid = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(prefInfor.get("amountNotPaidText"))) {
                amountNotPaidText = prefInfor.get("amountNotPaidText").toString();
            }
            if (UtilValidate.isNotEmpty(prefInfor.get("amountNotPaid"))) {
                amountNotPaid = (BigDecimal) prefInfor.get("amountNotPaid");
            }
            if (amountNotPaid.compareTo(BigDecimal.ZERO) >= 0) {
                ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_left_no_border_10"), null,
                        UtilProperties.getMessage(RESOURCE, "BACCBenAThanhToanChoBenB", locale) + " " + amountNotPaidText);
            } else {
                ExcelUtil.createCellOfRow(row, 0, styles.get("cell_normal_left_no_border_10"), null,
                        UtilProperties.getMessage(RESOURCE, "BACCBenBThanhToanChoBenA", locale) + " " + amountNotPaidText);
            }

            rownum += 2;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 5));
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_center_no_border_10"), null, UtilProperties.getMessage(RESOURCE, "BACCARep", locale));
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_center_no_border_10"), null, UtilProperties.getMessage(RESOURCE, "BACCBRep", locale));

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 5));
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_center_no_border_10"), null, params.get("positionFrom")[0].toString().toUpperCase());
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_center_no_border_10"), null, params.get("positionTo")[0].toString().toUpperCase());

            rownum += 5;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 5));
            ExcelUtil.createCellOfRow(row, 1, styles.get("cell_bold_center_no_border_10"), null, params.get("representativeFrom")[0].toString().toUpperCase());
            ExcelUtil.createCellOfRow(row, 4, styles.get("cell_bold_center_no_border_10"), null, params.get("representativeTo")[0].toString().toUpperCase());

            rownum += 1;
            row= sheet.createRow(rownum);
            row.setHeight((short) 300);
            ExcelUtil.createCellOfRow(row, 0, styles.get("cell_italic_left_no_border_10"), null, "* "
                    + UtilProperties.getMessage(RESOURCE, "BACCLiabilityNote", locale) + ". ");

            ExcelUtil.responseWrite(response, wb, "bang-doi-chieu-cong-no");
        } catch (GenericServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
}
