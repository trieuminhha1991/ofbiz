package com.olbius.basehr.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

public class MicrosoftDocumentServices {
	
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
	
	private static Map<String, CellStyle> createStyles(Workbook wb){
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		
		CellStyle style;
		Font boldCenterNoBorderFont16 = wb.createFont();
		boldCenterNoBorderFont16.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont16.setFontHeightInPoints((short) 16);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont16);
		styles.put("cell_bold_centered_no_border_16", style);
		
		Font normalBoldLeftFont8 = wb.createFont();
		normalBoldLeftFont8.setFontHeightInPoints((short) 8);
		normalBoldLeftFont8.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalBoldLeftFont8);
		styles.put("cell_bold_normal_Left_8", style);
		
		Font normalCenterBorderTopFont10 = wb.createFont();
		normalCenterBorderTopFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderTopFont10);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_top_10", style);
		
		Font normalLeftBorderFullFont10 = wb.createFont();
		normalLeftBorderFullFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftBorderFullFont10);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		styles.put("cell_normal_left_border_full_10", style);
		
		CreationHelper createHelper = wb.getCreationHelper();
		style = createNonBorderedStyle(wb);
		style.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		styles.put("cell_date_format", style);
		
		return styles;
	}
	
	@SuppressWarnings({ "null", "unchecked", "unused" })
	public static void exportAbsentDetailReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String employeeId = request.getParameter("employeeId");
		List<String> employeeId_input = null;
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		if(employeeId.equals("") || employeeId.equals("null")){
			employeeId_input = null;
		}else{
			String[] employeeId_data = employeeId.split(",");
			if(employeeId_data.length != 0){
				for (String s : employeeId_data) {
					employeeId_input.add(s);
				}
			}
		}
		
		Long fromDate_long = Long.parseLong(fromDateStr);
		Long thruDate_long = Long.parseLong(thruDateStr);
		
		Timestamp fromDate_input = new Timestamp(fromDate_long);
		Timestamp thruDate_input = new Timestamp(thruDate_long);
		Date fromDateTs = UtilDateTime.getDayStart(fromDate_input);
		Date thruDateTs = UtilDateTime.getDayStart(thruDate_input);
		
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String mainTitle = UtilProperties.getMessage("BaseHRUiLabels", "AbsentReport", locale);
		String subTitle = UtilProperties.getMessage("BaseHRUiLabels", "AbsentSituationDetailReport", locale);
		String employeeName = UtilProperties.getMessage("BaseHREmployeeUiLabels", "EmployeeName", locale);
		String from_date = UtilProperties.getMessage("BaseHRUiLabels", "HRCommonFromDate", locale);
		String thru_date = UtilProperties.getMessage("BaseHRUiLabels", "HRCommonThruDate", locale);
		String emplLeaveReasonType = UtilProperties.getMessage("BaseHREmployeeUiLabels", "EmplLeaveReasonType", locale);
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDate_input);
		context.put("thruDate", thruDate_input);
		context.put("employeeId[]", employeeId_input);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService = dispatcher.runSync("absentEmplDetail", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
			
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = createStyles(wb);
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
			
			List<GenericValue> listEmplReasonTypeLeave = delegator.findList("EmplLeaveReasonType", null, null, null, null, false);
			List<String> emplLeaveReasonType_list = EntityUtil.getFieldListFromEntityList(listEmplReasonTypeLeave, "description", true); 
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 25*256);
			sheet.setColumnWidth(2, 25*256);
			int colnum = 2;
			for (GenericValue g : listEmplReasonTypeLeave) {
				colnum++;
				sheet.setColumnWidth(colnum, 20*256);
			}
			
			int rownum = 0;
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,colnum));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,colnum));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,colnum));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow3 = sheet.createRow(rownum);
			khoangCachRow3.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,colnum));
			Cell khoangCachCell3 = khoangCachRow3.createCell(0);
			khoangCachCell3.setCellValue(subTitle.toUpperCase());
			khoangCachCell3.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("");
			titles.add(emplLeaveReasonType);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 550);
			for (int i = 0; i < titles.size(); i++) {
				if(i == 0){
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
					Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
					headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
					headerBreakdownAmountCell.setCellValue(titles.get(i));
				}else{
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, colnum));
					Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
					headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
					headerBreakdownAmountCell.setCellValue(titles.get(i));
				}
			}
			rownum += 1;
			
			List<String> titles_child = new FastList<String>();
			titles_child.add(employeeName);
			titles_child.add(from_date);
			titles_child.add(thru_date);
			for (String s : emplLeaveReasonType_list) {
				titles_child.add(s);
			}
			Row headerChildRow = sheet.createRow(rownum);
			headerChildRow.setHeight((short) 500);
			for (int i = 0; i < titles_child.size(); i++) {
				Cell headerChildCell = headerChildRow.createCell(i);
				headerChildCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerChildCell.setCellValue(titles_child.get(i));
			}
			rownum += 1;
			
			for (Map<String, Object> map  : listData) {
				Row absentDetailRow = sheet.createRow(rownum);
				absentDetailRow.setHeight((short) 500);
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if(entry.getKey().equals("partyName")){
						String partyName_out = (String) entry.getValue();
						Cell partyName_Cell = absentDetailRow.createCell(0);
						partyName_Cell.setCellValue(partyName_out);
						partyName_Cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					else if(entry.getKey().equals("fromDate")){
						Timestamp fromDate = (Timestamp) entry.getValue();
						Cell fromDate_Cell = absentDetailRow.createCell(1);
						fromDate_Cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						fromDate_Cell.setCellStyle(styles.get("cell_date_format"));
						fromDate_Cell.setCellValue(fromDate);
					}
					else if(entry.getKey().equals("thruDate")){
						Timestamp thruDate = (Timestamp) entry.getValue();
						Cell thruDate_Cell = absentDetailRow.createCell(2);
						thruDate_Cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						thruDate_Cell.setCellStyle(styles.get("cell_date_format"));
						thruDate_Cell.setCellValue(thruDate);
					}
					else if(entry.getKey().equals("NGHI_BU")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_BU = absentDetailRow.createCell(10);
						empl_NGHI_BU.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_BU.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_CON_OM")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_CON_OM = absentDetailRow.createCell(8);
						empl_NGHI_CON_OM.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_CON_OM.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_CUOI")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_CUOI = absentDetailRow.createCell(11);
						empl_NGHI_CUOI.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_CUOI.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_DUONG_SUC_OM")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_DUONG_SUC_OM = absentDetailRow.createCell(5);
						empl_NGHI_DUONG_SUC_OM.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_DUONG_SUC_OM.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_DUONG_SUC_TN")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_DUONG_SUC_TN = absentDetailRow.createCell(6);
						empl_NGHI_DUONG_SUC_TN.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_DUONG_SUC_TN.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_DUONG_SUC_TS")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_DUONG_SUC_TS = absentDetailRow.createCell(7);
						empl_NGHI_DUONG_SUC_TS.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_DUONG_SUC_TS.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_HN_HT")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_HN_HT = absentDetailRow.createCell(12);
						empl_NGHI_HN_HT.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_HN_HT.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_KHONG_LUONG")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_HN_HT = absentDetailRow.createCell(13);
						empl_NGHI_HN_HT.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_HN_HT.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_OM")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_OM = absentDetailRow.createCell(4);
						empl_NGHI_OM.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_OM.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_PHEP")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_PHEP = absentDetailRow.createCell(3);
						empl_NGHI_PHEP.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_PHEP.setCellValue(numb);
					}else if(entry.getKey().equals("NGHI_THAI_SAN")){
						float numb = 0;
						if(entry.getValue() != null && UtilValidate.isNotEmpty(entry.getValue())){
							numb = (float) entry.getValue();
						}
						Cell empl_NGHI_THAI_SAN = absentDetailRow.createCell(9);
						empl_NGHI_THAI_SAN.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						empl_NGHI_THAI_SAN.setCellValue(numb);
					}
				}
				rownum ++;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_nghi_phep" + fromDateTs + "_" + thruDateTs + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(baos != null)baos.close();
		}
	}
	@SuppressWarnings({ "unused", "unchecked" })
	public static void exportSicknessPregnancyReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		
		Long fromDate_long = Long.parseLong(fromDateStr);
		Long thruDate_long = Long.parseLong(thruDateStr);
		
		Timestamp fromDate = new Timestamp(fromDate_long);
		Timestamp thruDate = new Timestamp(thruDate_long);
		
		Date fromDateTs = UtilDateTime.getDayStart(fromDate);
		Date thruDateTs = UtilDateTime.getDayStart(thruDate);
		
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String mainTitle = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceReport", locale);
		String subTitle = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsBenefitSicknessPregnancyEmplTitle", locale);
		String InsuranceBenefitType = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceBenefitType", locale);
		String FullName = UtilProperties.getMessage("BaseHRUiLabels", "HRFullName", locale);
		String SocialInsuranceNbrIdentify = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "SocialInsuranceNbrIdentify", locale);
		String InsuranceSocialSalaryBenefit = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceSocialSalaryBenefit", locale);
		String InsuranceParticipatePeriod = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceParticipatePeriod", locale);
		String DayLeaveFamily = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "DayLeaveFamily", locale);
		String DayLeaveConcentrate = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "DayLeaveConcentrate", locale);
		String HRCommonAmount = UtilProperties.getMessage("BaseHRUiLabels", "HRCommonAmount", locale);
		String CommonFromDate = UtilProperties.getMessage("BaseHRUiLabels", "CommonFromDate", locale);
		String CommonThruDate = UtilProperties.getMessage("BaseHRUiLabels", "CommonThruDate", locale);
		String HRNotes = UtilProperties.getMessage("BaseHRUiLabels", "HRNotes", locale);
		String HRNumberDayLeave = UtilProperties.getMessage("BaseHREmployeeUiLabels", "HRNumberDayLeave", locale);
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("fromDate", fromDate);
		context.put("thruDate", thruDate);
		context.put("userLogin", userLogin);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			List<Map<String, Object>> listData = FastList.newInstance();
			Map<String, Object> resultService = dispatcher.runSync("insBenefitSicknessPregnancyEmpl", context);
			if(UtilValidate.isNotEmpty(resultService)){
				listData = (List<Map<String, Object>>) resultService.get("data");
			}else{
				listData = null;
			}
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = createStyles(wb);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 25*256);
			sheet.setColumnWidth(2, 25*256);
			sheet.setColumnWidth(3, 25*256);
			sheet.setColumnWidth(4, 25*256);
			sheet.setColumnWidth(5, 25*256);
			sheet.setColumnWidth(6, 25*256);
			sheet.setColumnWidth(7, 25*256);
			sheet.setColumnWidth(8, 25*256);
			sheet.setColumnWidth(9, 25*256);
			sheet.setColumnWidth(10, 25*256);
			
			int rownum = 0;
			int column = 10;
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,column));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, column));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle);
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow1 = sheet.createRow(rownum);
			khoangCachRow1.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,column));
			Cell khoangCachCell1 = khoangCachRow1.createCell(0);
			rownum += 1;
			
			Row subRow = sheet.createRow(rownum);
			subRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, column));
			Cell subCell = subRow.createCell(0);
			subCell.setCellValue(subTitle);
			subCell.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			Row groupRow = sheet.createRow(rownum);
			groupRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 6));
			Cell groupCell = groupRow.createCell(5);
			groupCell.setCellValue(HRNumberDayLeave);
			groupCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			rownum += 1;
			
			Row eleRow = sheet.createRow(rownum);
			eleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 0));
			Cell InsuranceBenefitType_cell = eleRow.createCell(0);
			InsuranceBenefitType_cell.setCellValue(InsuranceSocialSalaryBenefit);
			InsuranceBenefitType_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 1));
			Cell FullName_cell = eleRow.createCell(1);
			FullName_cell.setCellValue(FullName);
			FullName_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 2));
			Cell SocialInsuranceNbrIdentify_cell = eleRow.createCell(2);
			SocialInsuranceNbrIdentify_cell.setCellValue(SocialInsuranceNbrIdentify);
			SocialInsuranceNbrIdentify_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));

			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 3));
			Cell InsuranceSocialSalaryBenefit_cell = eleRow.createCell(3);
			InsuranceSocialSalaryBenefit_cell.setCellValue(InsuranceSocialSalaryBenefit);
			InsuranceSocialSalaryBenefit_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 4));
			Cell InsuranceParticipatePeriod_cell = eleRow.createCell(4);
			InsuranceParticipatePeriod_cell.setCellValue(InsuranceParticipatePeriod);
			InsuranceParticipatePeriod_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 5));
			Cell DayLeaveFamily_cell = eleRow.createCell(5);
			DayLeaveFamily_cell.setCellValue(DayLeaveFamily);
			DayLeaveFamily_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 6));
			Cell DayLeaveConcentrate_cell = eleRow.createCell(6);
			DayLeaveConcentrate_cell.setCellValue(DayLeaveConcentrate);
			DayLeaveConcentrate_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 7, 7));
			Cell HRCommonAmount_cell = eleRow.createCell(7);
			HRCommonAmount_cell.setCellValue(HRCommonAmount);
			HRCommonAmount_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 8, 8));
			Cell CommonFromDate_cell = eleRow.createCell(8);
			CommonFromDate_cell.setCellValue(CommonFromDate);
			CommonFromDate_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 9, 9));
			Cell CommonThruDate_cell = eleRow.createCell(9);
			CommonThruDate_cell.setCellValue(CommonThruDate);
			CommonThruDate_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 10, 10));
			Cell HRNotes_cell = eleRow.createCell(10);
			HRNotes_cell.setCellValue(HRNotes);
			HRNotes_cell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			rownum += 1;
			
			if(UtilValidate.isNotEmpty(listData)){
				for (Map<String, Object> map : listData) {
					Row eleChildRow = sheet.createRow(rownum);
					eleChildRow.setHeight((short) 550);
					for(Map.Entry<String, Object> entry : map.entrySet()){
						if(entry.getKey().equals("description")){
							String val = (String) entry.getValue();
							Cell cell = eleChildRow.createCell(0);
							cell.setCellValue(val);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}else if(entry.getKey().equals("partyName")){
							String val = (String) entry.getValue();
							Cell cell = eleChildRow.createCell(1);
							cell.setCellValue(val);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}else if(entry.getKey().equals("insSocialNbr")){
							String val = (String) entry.getValue();
							Cell cell = eleChildRow.createCell(2);
							cell.setCellValue(val);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}else if(entry.getKey().equals("insuranceSalary")){
							BigDecimal val = (BigDecimal) entry.getValue();
							float f = val.floatValue();
							Cell cell = eleChildRow.createCell(3);
							cell.setCellValue(f);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}else if(entry.getKey().equals("insParticipatePeriod")){
							String val = (String) entry.getValue();
							Cell cell = eleChildRow.createCell(4);
							cell.setCellValue(val);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}else if(entry.getKey().equals("totalDayLeave")){
							BigDecimal val = (BigDecimal) entry.getValue();
							float f = val.floatValue();
							Cell cell = eleChildRow.createCell(5);
							cell.setCellValue(f);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}else if(entry.getKey().equals("accumulatedLeave")){
							Double val = (Double) entry.getValue();
							Cell cell = eleChildRow.createCell(6);
							cell.setCellValue(val);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}else if(entry.getKey().equals("allowanceAmount")){
							BigDecimal val = (BigDecimal) entry.getValue();
							float f = val.floatValue();
							Cell cell = eleChildRow.createCell(7);
							cell.setCellValue(f);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}else if(entry.getKey().equals("fromDate")){
							Date val = (Date) entry.getValue();
							Timestamp tmp = new Timestamp(val.getTime());
							Cell cell = eleChildRow.createCell(8);
							cell.setCellValue(tmp);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
							cell.setCellStyle(styles.get("cell_date_format"));
						}else if(entry.getKey().equals("thruDate")){
							Date val = (Date) entry.getValue();
							Timestamp tmp = new Timestamp(val.getTime());
							Cell cell = eleChildRow.createCell(9);
							cell.setCellValue(tmp);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
							cell.setCellStyle(styles.get("cell_date_format"));
						}else if(entry.getKey().equals("comment")){
							String val = (String) entry.getValue();
							Cell cell = eleChildRow.createCell(10);
							cell.setCellValue(val);
							cell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
						}
					}
					rownum ++;
				}
			}
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_ds_nv_de_nghi_huong_che_do_om_dau" + fromDateTs + "_" + thruDateTs + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(baos != null)baos.close();
		}
	}
}

	
