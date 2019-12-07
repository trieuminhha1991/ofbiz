package com.olbius.baselogistics.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

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
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

public class MicrosoftDocumentsServices {
		
	public static final String draftingPointRightWardArrow = new String("\u279B");
	public static final String resource = "BaseLogisticsUiLabels.xml";
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
	
	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		DataFormat df = wb.createDataFormat();

		CellStyle style;
		
		Font boldCenterNoBorderFont16 = wb.createFont();
		boldCenterNoBorderFont16.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont16.setFontHeightInPoints((short) 12);
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
	@SuppressWarnings({ "unchecked" })
	public static void exportInventoryOlapLogToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String categoryId = request.getParameter("categoryId");
		String checkNPP = request.getParameter("checkNPP"); 
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> categoryIdInput = null;
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		if(categoryId.equals("") || categoryId.equals("null")){
			categoryIdInput = null;
		}
		if(!categoryId.equals("") && !categoryId.equals("null")){
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if(categoryIdData.length != 0){
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}
		
		
		if(dateType == null) {
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
		context.put("checkNPP", checkNPP);
		context.put("userLogin", userLogin);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String titleFromDate = UtilProperties.getMessage(resource, "FromDate", locale);
			String titleThruDate = UtilProperties.getMessage(resource, "ThruDate", locale); 
			
			Map<String, Object> resultService =  dispatcher.runSync("jqGetListInventoryReportOlap", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
			
			//start renderExcel
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
			
			sheet.setColumnWidth(0, 21*250);
			sheet.setColumnWidth(1, 21*300);
			sheet.setColumnWidth(2, 21*300);
			sheet.setColumnWidth(3, 21*600);
			sheet.setColumnWidth(4, 21*300);
			sheet.setColumnWidth(5, 21*300);
			sheet.setColumnWidth(6, 21*300);
			sheet.setColumnWidth(7, 21*300);
			sheet.setColumnWidth(8, 21*400);
			
			/*Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);*/
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
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
			}finally{
				if(is!=null)is.close();
			}
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell titleCell = titleRow.createCell(0);
			String title = UtilProperties.getMessage(resource, "LogInventoryReport", locale);
			titleCell.setCellValue(title.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell dateFromToCell = dateRow.createCell(0);
			String dateFromTo = titleFromDate + ": " + fromDateStrTime + " - " + titleThruDate + ": " + thruDateStrTime; 
			dateFromToCell.setCellValue(dateFromTo);
			dateFromToCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			     
			String titleDate = UtilProperties.getMessage(resource, "LogDate", locale);
			String titleProductCode = UtilProperties.getMessage(resource, "ProductCode", locale);
			String titleProductName = UtilProperties.getMessage(resource, "ProductName", locale);
			String titleExpireDate = UtilProperties.getMessage(resource, "ExpireDate", locale); 
			/*String titleLifetime = UtilProperties.getMessage(resource, "Lifetime", locale); */
			String titleQuantityOnHandTotal = UtilProperties.getMessage(resource, "QuantityOnHandTotal", locale);
			String titleAvailableToPromiseTotal = UtilProperties.getMessage(resource, "AvailableToPromiseTotal", locale);
			String titleFacility = UtilProperties.getMessage(resource, "LogFacilityName", locale);
			String titleQuantityUomId = UtilProperties.getMessage(resource, "QuantityUomId", locale);
			String titleCategoryProduct = UtilProperties.getMessage(resource, "BLCategoryProduct", locale);
			
			List<String> titles = new FastList<String>();
			titles.add(titleDate);
			titles.add(titleFacility);
			titles.add(titleProductCode);
			titles.add(titleProductName);
			titles.add(titleExpireDate);
			/*titles.add(titleLifetime);*/
			titles.add(titleQuantityOnHandTotal);
			titles.add(titleAvailableToPromiseTotal);
			titles.add(titleQuantityUomId);
			titles.add(titleCategoryProduct);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 550);
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			rownum += 1;
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 500);
				
				Cell dateCell = orderDetailRow.createCell(0);
				String date = (String) map.get("date");
				dateCell.setCellValue(date);
				dateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String facilityIdOut = (String) map.get("facilityId");
				Cell facilityIdCell = orderDetailRow.createCell(1);
				facilityIdCell.setCellValue(facilityIdOut);
				facilityIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String productIdOut = (String) map.get("productCode");
				Cell productCell = orderDetailRow.createCell(2);
				productCell.setCellValue(productIdOut);
				productCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String internalName = (String) map.get("internalName");
				Cell internalNameCell = orderDetailRow.createCell(3);
				internalNameCell.setCellValue(internalName);
				internalNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String expireDate = (String) map.get("expireDate");
				Cell expireDateCell = orderDetailRow.createCell(4);
				expireDateCell.setCellValue(expireDate);
				expireDateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				/*int dateLife = (Integer) map.get("dateLife");
				String lifeTime = String.valueOf(dateLife);
				Cell dateLifeCell = orderDetailRow.createCell(5);
				dateLifeCell.setCellValue(lifeTime);
				dateLifeCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
		
				BigDecimal inventoryTotal = (BigDecimal) map.get("inventoryTotal");
				String inventoryTotalStr = inventoryTotal.toString();
				String inventoryTotalStrStr = inventoryTotalStr.split("\\.")[0];
				Cell inventoryTotalCell = orderDetailRow.createCell(5);
				inventoryTotalCell.setCellValue(inventoryTotalStrStr);
				inventoryTotalCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				BigDecimal availableToPromiseTotalBig = (BigDecimal) map.get("availableToPromiseTotal");
				String availableToPromiseTotal = availableToPromiseTotalBig.toString();
				String availableToPromiseTotalStr = availableToPromiseTotal.split("\\.")[0];
				Cell availableToPromiseTotalCell = orderDetailRow.createCell(6);
				availableToPromiseTotalCell.setCellValue(availableToPromiseTotalStr);
				availableToPromiseTotalCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String uomIdOut = (String) map.get("currencyId");
				GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomIdOut), false);
				String description = uomIdOut;
				if(uom != null){
					description = uom.getString("description");
				}
				Cell uomIdCell = orderDetailRow.createCell(7);
				uomIdCell.setCellValue(description);
				uomIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String categoryName = (String) map.get("categoryName");
				Cell categoryNameCell = orderDetailRow.createCell(8);
				categoryNameCell.setCellValue(categoryName);
				categoryNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Calendar cal = Calendar.getInstance();
			String currentDate = dateFormat.format(cal.getTime());
			response.setHeader("content-disposition", "attachment;filename=" + "Baocaotonkho_" + currentDate + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if(baos != null)baos.close();
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void exportReceiveWarehouseOlapLogToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String categoryId = request.getParameter("categoryId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> categoryIdInput = null;
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		if(categoryId.equals("") || categoryId.equals("null")){
			categoryIdInput = null;
		}
		if(!categoryId.equals("") && !categoryId.equals("null")){
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if(categoryIdData.length != 0){
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}
		
		if(dateType == null) {
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
		context.put("userLogin", userLogin);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String titleFromDate = UtilProperties.getMessage(resource, "FromDate", locale);
			String titleThruDate = UtilProperties.getMessage(resource, "ThruDate", locale);
			
			Map<String, Object> resultService =  dispatcher.runSync("jqGetListReceiveWarehouseReportOlap", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
			
			//start renderExcel
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
			
			sheet.setColumnWidth(0, 21*250);
			sheet.setColumnWidth(1, 21*300);
			sheet.setColumnWidth(2, 21*300);
			sheet.setColumnWidth(3, 21*500);
			sheet.setColumnWidth(4, 21*300);
			sheet.setColumnWidth(5, 21*300);
			sheet.setColumnWidth(6, 21*300);
			sheet.setColumnWidth(7, 21*300);
			sheet.setColumnWidth(8, 21*300);
			sheet.setColumnWidth(9, 21*300);
			/*sheet.setColumnWidth(10, 21*300);*/
			/*sheet.setColumnWidth(11, 21*300);*/
			/*sheet.setColumnWidth(12, 21*300);*/
			/*Row imgHead = sheet.createRow(0);*/
			/*Cell imgCell = imgHead.createCell(0);*/
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
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
			}finally{
				if(is!=null)is.close();
			}
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
			Cell titleCell = titleRow.createCell(0);
			String title = UtilProperties.getMessage(resource, "LogReportReceiveTitle", locale);
			titleCell.setCellValue(title.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
			Cell dateFromToCell = dateRow.createCell(0);
			String dateFromTo = titleFromDate + ": " + fromDateStrTime + " - " + titleThruDate + ": " + thruDateStrTime; 
			dateFromToCell.setCellValue(dateFromTo);
			dateFromToCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			String titleDateReceive = UtilProperties.getMessage(resource, "ReceivedDate", locale);
			String titleProductId = UtilProperties.getMessage(resource, "ProductId", locale);
			String titleProductName = UtilProperties.getMessage(resource, "ProductName", locale);
			String titleReceiveQuantity = UtilProperties.getMessage(resource, "ReceivedQuantity", locale);
			/*String titlePurchaseOrder = UtilProperties.getMessage(resource, "LogOrder", locale);*/
			/*String titleReceiveNoteId = UtilProperties.getMessage(resource, "DeliveryId", locale);*/
			String titleProductManufactureDate = UtilProperties.getMessage(resource, "ProductManufactureDate", locale);
			String titleProductExpireDate = UtilProperties.getMessage(resource, "ExpireDate", locale);
			String titleBatch = UtilProperties.getMessage(resource, "Batch", locale);
			String titleFacility = UtilProperties.getMessage(resource, "LogFacilityName", locale);
			/*String titlePurchaseChannel = UtilProperties.getMessage(resource, "LogPurchaseChannels", locale);*/
			String titleProductCatalogs = UtilProperties.getMessage(resource, "BLCategoryProduct", locale);
			String titleQuantityUomId = UtilProperties.getMessage(resource, "QuantityUomId", locale);
			/*String titleCodeReturns = UtilProperties.getMessage(resource, "LogCodeReturns", locale);
			String titleTransferId = UtilProperties.getMessage(resource, "TransferId", locale);
			String titleReasonReceive = UtilProperties.getMessage(resource, "LogReasonReceive", locale);*/
			
			titles.add(titleDateReceive);
			titles.add(titleFacility);
			titles.add(titleProductId);
			titles.add(titleProductName);
			/*titles.add(titleReasonReceive);
			titles.add("");
			titles.add("");*/
			/*titles.add(titleReceiveNoteId);*/
			titles.add(titleProductManufactureDate);
			titles.add(titleProductExpireDate);
			titles.add(titleBatch);
			titles.add(titleReceiveQuantity);
			titles.add(titleQuantityUomId);
			/*titles.add(titlePurchaseChannel);*/
			titles.add(titleProductCatalogs);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 550);
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			rownum += 1;
			/*List<String> titlesTwo = new FastList<String>();
			titlesTwo.add("");
			titlesTwo.add("");
			titlesTwo.add("");
			titlesTwo.add(titlePurchaseOrder);
			titlesTwo.add(titleCodeReturns);
			titlesTwo.add(titleTransferId);*/
			
			/*Row headerBreakdownAmountRowTwo = sheet.createRow(rownum + 1);
			headerBreakdownAmountRowTwo.setHeight((short) 500);
			for (int i = 0; i < titlesTwo.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRowTwo.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titlesTwo.get(i));
			}*/
			
			/*sheet.addMergedRegion(CellRangeAddress.valueOf("A" + String.valueOf(rownum + 1) + ":" + "A" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("B" + String.valueOf(rownum + 1) + ":" + "B" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("C" + String.valueOf(rownum + 1) + ":" + "C" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("D" + String.valueOf(rownum + 1) + ":" + "F" + String.valueOf(rownum + 1) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("G" + String.valueOf(rownum + 1) + ":" + "G" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("H" + String.valueOf(rownum + 1) + ":" + "H" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("I" + String.valueOf(rownum + 1) + ":" + "I" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("J" + String.valueOf(rownum + 1) + ":" + "J" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("K" + String.valueOf(rownum + 1) + ":" + "K" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("L" + String.valueOf(rownum + 1) + ":" + "L" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("M" + String.valueOf(rownum + 1) + ":" + "M" + String.valueOf(rownum + 2) ));*/
			/*sheet.addMergedRegion(CellRangeAddress.valueOf("N" + String.valueOf(rownum + 1) + ":" + "N" + String.valueOf(rownum + 2) ));*/
			
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 500);
					
				Cell dateCell = orderDetailRow.createCell(0);
				String date = (String) map.get("date");
				dateCell.setCellValue(date);
				dateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String facilityIdOut = (String) map.get("facilityName");
				Cell facilityIdCell = orderDetailRow.createCell(1);
				facilityIdCell.setCellValue(facilityIdOut);
				facilityIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String productIdOut = (String) map.get("productId");
				Cell productCell = orderDetailRow.createCell(2);
				productCell.setCellValue(productIdOut);
				productCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String productNameOut = (String) map.get("productName");
				Cell productNameCell = orderDetailRow.createCell(3);
				productNameCell.setCellValue(productNameOut);
				productNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				/*String orderIdOut = (String) map.get("orderId");
				Cell orderIdCell = orderDetailRow.createCell(3);
				orderIdCell.setCellValue(orderIdOut);
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String returnIdOut = (String) map.get("returnId");
				Cell returnIdCell = orderDetailRow.createCell(4);
				returnIdCell.setCellValue(returnIdOut);
				returnIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String shipmentIdOut = (String) map.get("transferId");
				Cell shipmentIdCell = orderDetailRow.createCell(5);
				shipmentIdCell.setCellValue(shipmentIdOut);
				shipmentIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
				
				/*String deliveryIdOut = (String) map.get("deliveryId");
				Cell deliveryIdCell = orderDetailRow.createCell(6);
				deliveryIdCell.setCellValue(deliveryIdOut);
				deliveryIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
				
				String datetimeManufactured = (String) map.get("datetimeManufactured");
				Cell datetimeManufacturedCell = orderDetailRow.createCell(4);
				datetimeManufacturedCell.setCellValue(datetimeManufactured);
				datetimeManufacturedCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String expireDate = (String) map.get("expireDate");
				Cell expireDateCell = orderDetailRow.createCell(5);
				expireDateCell.setCellValue(expireDate);
				expireDateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String lotId = (String) map.get("lotId");
				Cell lotIdCell = orderDetailRow.createCell(6);
				lotIdCell.setCellValue(lotId);
				lotIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				
				BigDecimal inventoryTotal = (BigDecimal) map.get("quantityOnHandTotal");
				double inventoryTotalDou = 0;
				if(UtilValidate.isNotEmpty(inventoryTotal)){
					inventoryTotalDou = inventoryTotal.doubleValue();
				}
				Cell inventoryTotalCell = orderDetailRow.createCell(7);
				inventoryTotalCell.setCellValue(inventoryTotalDou);
				inventoryTotalCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String uomIdOut = (String) map.get("uomId");
				Cell uomIdCell = orderDetailRow.createCell(8);
				GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomIdOut), false);
				String descriptionUom = uomIdOut;
				if(uom != null){
					descriptionUom = uom.getString("description");
				}
				uomIdCell.setCellValue(descriptionUom);
				uomIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String categoryName = (String) map.get("categoryName");
				Cell categoryNameCell = orderDetailRow.createCell(9);
				categoryNameCell.setCellValue(categoryName);
				categoryNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Calendar cal = Calendar.getInstance();
			String currentDate = dateFormat.format(cal.getTime());
			response.setHeader("content-disposition", "attachment;filename=" + "Baocaonhapkho_" + currentDate + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if(baos != null)baos.close();
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void exportExportWarehouseOlapLogToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String enumId = request.getParameter("enumId");
		String categoryId = request.getParameter("categoryId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> enumIdInput = null;
		List<String> categoryIdInput = null;
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		if(enumId.equals("") || enumId.equals("null")){
			enumIdInput = null;
		}
		if(!enumId.equals("") && !enumId.equals("null")){
			String[] enumIdData = enumId.split(",");
			enumIdInput = new ArrayList<>();
			if(enumIdData.length != 0){
				for (String i : enumIdData) {
					enumIdInput.add(i);
				}
			}
		}
		
		if(categoryId.equals("") || categoryId.equals("null")){
			categoryIdInput = null;
		}
		if(!categoryId.equals("") && !categoryId.equals("null")){
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if(categoryIdData.length != 0){
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}
		
		if(dateType == null) {
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
		context.put("enumId[]", enumIdInput);
		context.put("categoryId[]", categoryIdInput);
		context.put("userLogin", userLogin);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("jqGetListExporteWarehouseReportOlap", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
			
			String titleFromDate = UtilProperties.getMessage(resource, "FromDate", locale);
			String titleThruDate = UtilProperties.getMessage(resource, "ThruDate", locale); 
			
			//start renderExcel
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
			
			sheet.setColumnWidth(0, 21*250);
			sheet.setColumnWidth(1, 18*300);
			sheet.setColumnWidth(2, 15*300);
			sheet.setColumnWidth(3, 25*600);
			sheet.setColumnWidth(4, 21*300);
			sheet.setColumnWidth(5, 21*300);
			sheet.setColumnWidth(6, 21*300);
			sheet.setColumnWidth(7, 21*300);                
			sheet.setColumnWidth(8, 21*300);
			sheet.setColumnWidth(9, 21*300);
			/*sheet.setColumnWidth(10, 21*300);
			sheet.setColumnWidth(11, 21*300);
			sheet.setColumnWidth(12, 21*300);
			sheet.setColumnWidth(13, 21*300);
			sheet.setColumnWidth(14, 21*300);
			sheet.setColumnWidth(15, 21*300);
			sheet.setColumnWidth(16, 21*300);*/
			/*Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);*/
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
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
			}finally{
				if(is!=null)is.close();
			}
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
			String title = UtilProperties.getMessage(resource, "LogReportExportTitle", locale);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(title);
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
			Cell dateFromToCell = dateRow.createCell(0);
			String dateFromTo = titleFromDate + ": " + fromDateStrTime + " - " + titleThruDate + ": " + thruDateStrTime; 
			dateFromToCell.setCellValue(dateFromTo);
			dateFromToCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,9));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			String titleDateExport = UtilProperties.getMessage(resource, "ActualExportedDate", locale);
			/*String titleDateTimeRequirement = UtilProperties.getMessage(resource, "LogDateTimeRequirement", locale);*/
			String titleExportQuantity = UtilProperties.getMessage(resource, "LogExportQuantity", locale);
			/*String titleSaleOrder = UtilProperties.getMessage(resource, "SalesOrder", locale);
			String titleCodeReturns = UtilProperties.getMessage(resource, "LogCodeReturns", locale);
			String titleTransferId = UtilProperties.getMessage(resource, "TransferId", locale);
			String titleReasonShipment = UtilProperties.getMessage(resource, "LogReasonShipment", locale);*/
			String titleFacility = UtilProperties.getMessage(resource, "LogFacilityName", locale);
			/*String titleSalesChannel = UtilProperties.getMessage(resource, "DmsSalesChannel", locale);*/
			/*String titleDelivery = UtilProperties.getMessage(resource, "DeliveryId", locale);*/
			String titleProductCode = UtilProperties.getMessage(resource, "ProductCode", locale);
			String titleProductName = UtilProperties.getMessage(resource, "ProductName", locale);
			String titleProductManufactureDate = UtilProperties.getMessage(resource, "ProductManufactureDate", locale);   
			String titleProductExpireDate = UtilProperties.getMessage(resource, "ExpireDate", locale);
			String titleBatch = UtilProperties.getMessage(resource, "Batch", locale);
			String titleProductCatalogs = UtilProperties.getMessage(resource, "BLCategoryProduct", locale);
			String titleQuantityUomId = UtilProperties.getMessage(resource, "QuantityUomId", locale);
			/*String titleOrderProductsForPromotion = UtilProperties.getMessage(resource, "LogOrderProductsForPromotion", locale);*/
			
			List<String> titles = new FastList<String>();
			titles.add(titleDateExport);
			/*titles.add(titleDateTimeRequirement);
			titles.add(titleFacility);
			titles.add(titleReasonShipment);*/
			titles.add(titleFacility);
			/*titles.add(titleSalesChannel);
			titles.add(titleDelivery);*/
			titles.add(titleProductCode);
			titles.add(titleProductName);
			titles.add(titleProductManufactureDate);
			titles.add(titleProductExpireDate);
			titles.add(titleBatch);
			titles.add(titleExportQuantity);
			titles.add(titleQuantityUomId);
			/*titles.add(titleOrderProductsForPromotion);*/
			titles.add(titleProductCatalogs);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 600);
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			rownum += 1;
			/*List<String> titlesTwo = new FastList<String>();
			titlesTwo.add("");
			titlesTwo.add("");
			titlesTwo.add("");
			titlesTwo.add(titleSaleOrder);
			titlesTwo.add(titleCodeReturns);
			titlesTwo.add(titleTransferId);*/
			
			/*Row headerBreakdownAmountRowTwo = sheet.createRow(rownum + 1);
			headerBreakdownAmountRowTwo.setHeight((short) 500);
			for (int i = 0; i < titlesTwo.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRowTwo.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titlesTwo.get(i));
			}*/
			
			/*sheet.addMergedRegion(CellRangeAddress.valueOf("A" + String.valueOf(rownum + 1) + ":" + "A" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("B" + String.valueOf(rownum + 1) + ":" + "B" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("C" + String.valueOf(rownum + 1) + ":" + "C" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("D" + String.valueOf(rownum + 1) + ":" + "F" + String.valueOf(rownum + 1) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("G" + String.valueOf(rownum + 1) + ":" + "G" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("H" + String.valueOf(rownum + 1) + ":" + "H" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("I" + String.valueOf(rownum + 1) + ":" + "I" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("J" + String.valueOf(rownum + 1) + ":" + "J" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("K" + String.valueOf(rownum + 1) + ":" + "K" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("L" + String.valueOf(rownum + 1) + ":" + "L" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("M" + String.valueOf(rownum + 1) + ":" + "M" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("N" + String.valueOf(rownum + 1) + ":" + "N" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("O" + String.valueOf(rownum + 1) + ":" + "O" + String.valueOf(rownum + 2) ));
			sheet.addMergedRegion(CellRangeAddress.valueOf("P" + String.valueOf(rownum + 1) + ":" + "P" + String.valueOf(rownum + 2) ));
			rownum += 2;*/
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 500);
				
				Cell dateCell = orderDetailRow.createCell(0);
				String date = (String) map.get("date");
				dateCell.setCellValue(date);
				dateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				/*Cell deliveryDateCell = orderDetailRow.createCell(1);
				String fullDeliveryDate = (String) map.get("fullDeliveryDate");
				deliveryDateCell.setCellValue(fullDeliveryDate);
				deliveryDateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
			
				String facilityIdOut = (String) map.get("facilityName");
				Cell facilityIdCell = orderDetailRow.createCell(1);
				facilityIdCell.setCellValue(facilityIdOut);
				facilityIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				/*String orderIdOut = (String) map.get("orderId");
				Cell orderIdCell = orderDetailRow.createCell(3);
				orderIdCell.setCellValue(orderIdOut);
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
				
				/*String returnIdOut = (String) map.get("returnId");
				Cell returnIdCell = orderDetailRow.createCell(4);
				returnIdCell.setCellValue(returnIdOut);
				returnIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String shipmentIdOut = (String) map.get("transferId");
				Cell shipmentIdCell = orderDetailRow.createCell(5);
				shipmentIdCell.setCellValue(shipmentIdOut);
				shipmentIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
			
				/*String enumIdOut = (String) map.get("enumId");
				String description = enumIdOut;
				if(enumIdOut != null){
					GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumIdOut), false);
					if(enumeration != null){
						description = (String) enumeration.get("description", UtilHttp.getLocale(request));
					}else{
						GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", enumIdOut), false);
						if(productStore != null){
							description = productStore.getString("storeName");
						}
					}
				}
				Cell enumIdCell = orderDetailRow.createCell(6);
				enumIdCell.setCellValue(description);
				enumIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
			
				/*String deliveryIdOut = (String) map.get("deliveryId");
				Cell deliveryIdCell = orderDetailRow.createCell(7);
				deliveryIdCell.setCellValue(deliveryIdOut);
				deliveryIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
			
				String productIdOut = (String) map.get("productId");
				Cell productCell = orderDetailRow.createCell(2);
				productCell.setCellValue(productIdOut);
				productCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String internalNameOut = (String) map.get("internalName");
				Cell internalNameCell = orderDetailRow.createCell(3);
				internalNameCell.setCellValue(internalNameOut);
				internalNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				Cell datetimeManufacturedCell = orderDetailRow.createCell(4);
				String datetimeManufactured = (String) map.get("datetimeManufactured");
				datetimeManufacturedCell.setCellValue(datetimeManufactured);
				datetimeManufacturedCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				Cell expireDateCell = orderDetailRow.createCell(5);
				String expireDate = (String) map.get("expireDate");
				expireDateCell.setCellValue(expireDate);
				expireDateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String lotId = (String) map.get("lotId");
				Cell lotIdCell = orderDetailRow.createCell(6);
				lotIdCell.setCellValue(lotId);
				lotIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			

				Cell inventoryTotalCell = orderDetailRow.createCell(7);
				BigDecimal inventoryTotal = (BigDecimal) map.get("quantityOnHandTotal");
				double inventoryTotalDou = 0;
				if(UtilValidate.isNotEmpty(inventoryTotal)){
					inventoryTotalDou = inventoryTotal.doubleValue();
				}
				inventoryTotalCell.setCellValue(inventoryTotalDou);
				inventoryTotalCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));

				String uomIdOut = (String) map.get("uomId");
				GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomIdOut), false);
				String descriptionUom = uomIdOut;
				if(uom != null){
					descriptionUom = uom.getString("description");
				}
				Cell uomIdCell = orderDetailRow.createCell(8);
				uomIdCell.setCellValue(descriptionUom);
				uomIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				rownum += 1;
				
				String categoryName = (String) map.get("categoryName");
				Cell categoryNameCell = orderDetailRow.createCell(9);
				categoryNameCell.setCellValue(categoryName);
				categoryNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			String titleReportExportWarehouse = UtilProperties.getMessage(resource, "LogReportExportWarehouseTitleExcel", locale);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Calendar cal = Calendar.getInstance();
			String currentDate = dateFormat.format(cal.getTime());
			response.setHeader("content-disposition", "attachment;filename=" + titleReportExportWarehouse + "_" + currentDate + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if(baos != null)baos.close();
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void exportHistoryShipmentReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String statusId = request.getParameter("statusId");
		String productIdInput = null;
		String facilityIdInput = null;
		String statusIdInput = null;
		
		if(productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("null")){
			productIdInput = productId;
		}
		
		if(facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("null")){
			facilityIdInput = facilityId;
		}
		
		if(statusId.equals("null")){
			statusIdInput = null;
		}
		if(!statusId.equals("null")){
			statusIdInput = statusId;
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		if(dateType == null) {
        	dateType = "DAY";
        }
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("facilityId", facilityIdInput);
		context.put("statusId", statusIdInput);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("jqGetListHistoryShipmentReportOlap", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
			
			//start renderExcel
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
			
			sheet.setColumnWidth(0, 21*350);
			sheet.setColumnWidth(1, 25*250);
			sheet.setColumnWidth(2, 15*300);
			sheet.setColumnWidth(3, 15*300);
			sheet.setColumnWidth(4, 25*300);
			sheet.setColumnWidth(5, 18*256);
			sheet.setColumnWidth(6, 18*256);
			/*Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);*/
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
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
			}finally{
				if(is!=null)is.close();
			}
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue("Lá»ŠCH SÆ¯ CHUYá»‚N HÃ€NG".toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellValue(" ");
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("NgÃ y xuáº¥t hÃ ng");
			titles.add("MÃ£ phiáº¿u suáº¥t");
			titles.add("Ä�Æ¡n hÃ ng bÃ¡n");
			titles.add("Xuáº¥t tá»« kho");
			titles.add("NgÆ°á»�i giao hÃ ng");
			titles.add("Tá»•ng tiá»�n (VND)");
			titles.add("Tráº¡ng thÃ¡i");
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			rownum += 1;
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("date")){
						Cell dateCell = orderDetailRow.createCell(0);
						String date = (String) entry.getValue();
						dateCell.setCellValue(date);
						dateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("deliveryId")){
						String deliveryId = (String) entry.getValue();
						Cell deliveryIdCell = orderDetailRow.createCell(1);
						deliveryIdCell.setCellValue(deliveryId);
						deliveryIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("orderId")){
						String orderId = (String) entry.getValue();
						Cell orderIdCell = orderDetailRow.createCell(2);
						orderIdCell.setCellValue(orderId);
						orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("facilityId")){
						String facilityIdOut = (String) entry.getValue();
						GenericValue facilityItem = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityIdOut), false);
						String facilityName = facilityItem.getString("facilityName");
						Cell facilityIdCell = orderDetailRow.createCell(3);
						facilityIdCell.setCellValue(facilityName);
						facilityIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("partyId")){
						String partyIdOut = (String) entry.getValue();
						String fullName = "";
						Cell facilityIdCell = orderDetailRow.createCell(4);
						if(partyIdOut != null){
							GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyIdOut), false);
							if(person.getString("middleName") != null){
								fullName = person.getString("lastName") + " " + person.getString("middleName") + " " + person.getString("firstName");
							}else{
								fullName = person.getString("lastName") + " " + person.getString("firstName");
							}
							facilityIdCell.setCellValue(fullName);
						}else{
							facilityIdCell.setCellValue(fullName);
						}
						facilityIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("amount")){
						BigDecimal amountBig = (BigDecimal) entry.getValue();
						String amountStr = amountBig.toString();
						String amountStrCh = amountStr.split("\\.")[0];
						Cell amountBigCell = orderDetailRow.createCell(5);
						amountBigCell.setCellValue(amountStrCh);
						amountBigCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("statusId")){
						String statusIdOut = (String) entry.getValue();
						GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusIdOut), false);
						String description = statusItem.getString("description");
						Cell stausIdCell = orderDetailRow.createCell(6);
						stausIdCell.setCellValue(description);
						stausIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Historyshipment_" + 123 + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if(baos != null)baos.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void exportInventoryCountTheVotes(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException{
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String facilityId = request.getParameter("facilityId");
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("userLogin", userLogin);
		context.put("facilityId", facilityId);
		Map<String, Object> results = null;
		Map<String, Object> resultsLocationHasProduct = null;
		try {
			results = dispatcher.runSync("getLocationFacilityAjax", context);
			resultsLocationHasProduct = dispatcher.runSync("getListProductAvalibleAjax", context);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		List<GenericValue> listlocationFacility = (List<GenericValue>) results.get("listlocationFacility");
		Map<String, String> childAndDad = FastMap.newInstance();
		Map<String, String> mapLocationCode = FastMap.newInstance();
		for (GenericValue x : listlocationFacility) {
			String locationId = (String) x.get("locationId");
			String parentLocationId = (String) x.get("parentLocationId");
			String locationCode = (String) x.get("locationCode");
			childAndDad.put(locationId, parentLocationId);
			mapLocationCode.put(locationId, locationCode);
		}
		String pathLocation = "";
		List<GenericValue> listProductAvalible = (List<GenericValue>) resultsLocationHasProduct.get("listProductAvalible");
		Set<String> locationAvalible = FastSet.newInstance();
		Map<String, String> mapLocationAvalible = FastMap.newInstance();
		for (GenericValue x : listProductAvalible) {
			pathLocation = "";
			String locationId = (String) x.get("locationId");
			locationAvalible.add(locationId);
			pathLocation = facilityId + getParentLocation(pathLocation, childAndDad, mapLocationCode, locationId);
			mapLocationAvalible.put(locationId, pathLocation);
		}
		//start renderExcel
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
		
		sheet.setColumnWidth(0, 21*256);
		sheet.setColumnWidth(1, 25*256);
		sheet.setColumnWidth(2, 15*256);
		sheet.setColumnWidth(3, 15*256);
		sheet.setColumnWidth(4, 18*256);
		sheet.setColumnWidth(5, 18*256);
		sheet.setColumnWidth(6, 40*256);
		
		int rowLine = 0;
		
		Row imgHead = sheet.createRow(0);
		imgHead.setHeight((short) 500);
		/*Row titleRow = sheet.createRow(rowLine);*/
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 4));
		
		/*Cell imgCell = imgHead.createCell(0);*/
		FileInputStream  is = null;
		try {
			String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
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
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is!=null)is.close();
		}
		
		Cell titleCell = imgHead.createCell(2);
		titleCell.setCellValue("Phiáº¿u kiá»ƒm kho".toUpperCase());
		titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rowLine += 2;
		
		Row rowDate = sheet.createRow(rowLine);
		rowDate.setHeight((short) 300);
		Cell cellDate = rowDate.createCell(4);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String date = dateFormat.format(System.currentTimeMillis());
		cellDate.setCellValue(date);
		cellDate.setCellStyle(styles.get("cell_centered_no_border_11"));
		Cell cellDay = rowDate.createCell(3);
		cellDay.setCellValue("NgÃ y: ");
		cellDay.setCellStyle(styles.get("cell_right_no_border_11"));
		rowLine += 2;
		
		Row rowHeader = sheet.createRow(rowLine);
		rowHeader.setHeight((short) 400);
		Cell cellHeaderLocation = rowHeader.createCell(0);
		cellHeaderLocation.setCellValue("Vá»‹ trÃ­");
		cellHeaderLocation.setCellStyle(styles.get("cell_b"));
		
		Cell cellHeaderProduct = rowHeader.createCell(1);
		cellHeaderProduct.setCellValue("Sáº£n pháº©m");
		cellHeaderProduct.setCellStyle(styles.get("cell_b_centered"));
		
		Cell cellHeaderExpriceDate = rowHeader.createCell(2);
		cellHeaderExpriceDate.setCellValue("Háº¡n sá»­ dá»¥ng");
		cellHeaderExpriceDate.setCellStyle(styles.get("cell_b_centered"));
		
		Cell cellHeaderPackingUom = rowHeader.createCell(3);
		cellHeaderPackingUom.setCellValue("Ä�Æ¡n vá»‹ Ä‘Ã³ng gÃ³i");
		cellHeaderPackingUom.setCellStyle(styles.get("cell_b_centered"));
		
		Cell cellHeaderQuantity = rowHeader.createCell(4);
		cellHeaderQuantity.setCellValue("Sá»‘ lÆ°á»£ng");
		cellHeaderQuantity.setCellStyle(styles.get("cell_b_centered"));
		
		Cell cellHeaderQuantityOnHand = rowHeader.createCell(5);
		cellHeaderQuantityOnHand.setCellValue("Sá»‘ lÆ°á»£ng thá»±c táº¿");
		cellHeaderQuantityOnHand.setCellStyle(styles.get("cell_b_centered"));
		
		Cell cellHeaderResult = rowHeader.createCell(6);
		cellHeaderResult.setCellValue("Káº¿t quáº£");
		cellHeaderResult.setCellStyle(styles.get("cell_b_centered"));
		
		rowLine += 1;
		
		for (String str : locationAvalible) {
			boolean inLine = true;
			for (GenericValue gv : listProductAvalible) {
				String locationId = (String) gv.get("locationId");
				if (str.equals(locationId)) {
					Row rowProducts = sheet.createRow(rowLine);
					rowProducts.setHeight((short) (4*128));
					if (inLine) {
						Cell cellLocation = rowProducts.createCell(0);
						cellLocation.setCellValue(mapLocationAvalible.get(str));
						cellLocation.setCellStyle(styles.get("cell_border_left_top"));
						inLine = false;
					}
					String productId = (String) gv.get("productId");
					Cell cellProductId = rowProducts.createCell(1);
					cellProductId.setCellValue(productId);
					cellProductId.setCellStyle(styles.get("cell_border_centered_top"));
					
					Timestamp expireDate = (Timestamp) gv.get("expireDate");
					String strExpireDate = dateFormat.format(expireDate);
					Cell cellexpireDate = rowProducts.createCell(2);
					cellexpireDate.setCellValue(strExpireDate);
					cellexpireDate.setCellStyle(styles.get("cell_border_centered_top"));
					
					String uomId = (String) gv.get("uomId");
					GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
					if(UtilValidate.isNotEmpty(uom))uomId = (String) uom.get("description");
					Cell celluomId = rowProducts.createCell(3);
					celluomId.setCellValue(uomId);
					celluomId.setCellStyle(styles.get("cell_border_centered_top"));
					
					BigDecimal quantity = gv.getBigDecimal("quantity");
					Cell cellQuantity = rowProducts.createCell(4);
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setGroupingSeparator(',');
					symbols.setDecimalSeparator('.');
					String pattern = "#,##0";
					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
					decimalFormat.setParseBigDecimal(true);
					cellQuantity.setCellValue(decimalFormat.format(quantity));
					cellQuantity.setCellStyle(styles.get("cell_border_right_top"));
					
					Cell cellRealyQuantity = rowProducts.createCell(5);
					cellRealyQuantity.setCellStyle(styles.get("cell_border_right_top"));
					
					Cell cellNote = rowProducts.createCell(6);
					cellNote.setCellStyle(styles.get("cell_border_centered_top"));
					rowLine += 1;
				}
			}
		}
		Row rowNotes = sheet.createRow(rowLine);
		rowNotes.setHeight((short) 900);
		Cell cellNotes = rowNotes.createCell(0);
		cellNotes.setCellValue("Ghi chÃº ");
		cellNotes.setCellStyle(styles.get("cell_normal_centered_top_10"));
		
		Cell cellNotesA = rowNotes.createCell(1);
		cellNotesA.setCellValue("A: Bá»‹ máº¥t cáº¯p");
		cellNotesA.setCellStyle(styles.get("cell_normal_left_top_10"));
		
		Cell cellNotesB = rowNotes.createCell(2);
		cellNotesB.setCellValue("B: TÃ¬m tháº¥y");
		cellNotesB.setCellStyle(styles.get("cell_normal_left_top_10"));
		
		Cell cellNotesC = rowNotes.createCell(3);
		cellNotesC.setCellValue("C: Bá»‹ hÆ° há»�ng");
		cellNotesC.setCellStyle(styles.get("cell_normal_left_top_10"));
		
		Cell cellNotesD = rowNotes.createCell(4);
		cellNotesD.setCellValue("D: Máº«u(KhÃ´ng bÃ¡n)");
		cellNotesD.setCellStyle(styles.get("cell_normal_left_top_10"));
		
		Cell cellNotesE = rowNotes.createCell(5);
		cellNotesE.setCellValue("E: Sáº£n pháº©m Ä‘áº·t hÃ ng bá»‹ tháº¥t láº¡c khi váº­n chuyá»ƒn");
		cellNotesE.setCellStyle(styles.get("cell_normal_Left_wrap__top10"));
		
		Cell cellNotesF = rowNotes.createCell(6);
		cellNotesF.setCellValue("F:  Sáº£n pháº©m giao hÃ ng bá»‹ tháº¥t láº¡c khi váº­n chuyá»ƒn");
		cellNotesF.setCellStyle(styles.get("cell_normal_Left_wrap__top10"));
		rowLine += 1;
		
		Row rowEmployee = sheet.createRow(rowLine);
		Cell cellEmployee = rowEmployee.createCell(6);
		cellEmployee.setCellValue("NhÃ¢n viÃªn kiá»ƒm kho");
		cellEmployee.setCellStyle(styles.get("cell_bb"));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "PhieuKiemKho_" + date + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if(baos != null)baos.close();
		}
	}
	
	private static String getParentLocation(String pathLocation, Map<String, String> childAndDad, Map<String, String> mapLocationCode, String locationId) {
		String thisParentLocationId = childAndDad.get(locationId);
		if (UtilValidate.isEmpty(thisParentLocationId)) {
			pathLocation = " " + draftingPointRightWardArrow + mapLocationCode.get(locationId);
		}else {
			Set<String> setLocationId = childAndDad.keySet();
			for (String s : setLocationId) {
				if (thisParentLocationId.equals(s)) {
					pathLocation = getParentLocation(pathLocation, childAndDad, mapLocationCode, s) + " " + draftingPointRightWardArrow + mapLocationCode.get(locationId);
					break;
				}
			}
		}
		return pathLocation;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void exportReturnProductOlapLogToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String enumId = request.getParameter("enumId");
		String categoryId = request.getParameter("categoryId");
		String returnReasonId = request.getParameter("returnReasonId");
		String checkNPP = request.getParameter("checkNPP");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> enumIdInput = null;
		List<String> categoryIdInput = null;
		List<String> returnReasonIdInput = null;
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		if(enumId.equals("") || enumId.equals("null")){
			enumIdInput = null;
		}
		if(!enumId.equals("") && !enumId.equals("null")){
			String[] enumIdData = enumId.split(",");
			enumIdInput = new ArrayList<>();
			if(enumIdData.length != 0){
				for (String i : enumIdData) {
					enumIdInput.add(i);
				}
			}
		}
		
		if(categoryId.equals("") || categoryId.equals("null")){
			categoryIdInput = null;
		}
		if(!categoryId.equals("") && !categoryId.equals("null")){
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if(categoryIdData.length != 0){
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}
		
		if(returnReasonId.equals("") || returnReasonId.equals("null")){
			returnReasonIdInput = null;
		}
		if(!returnReasonId.equals("") && !returnReasonId.equals("null")){
			String[] returnReasonIdData = returnReasonId.split(",");
			returnReasonIdInput = new ArrayList<>();
			if(returnReasonIdData.length != 0){
				for (String i : returnReasonIdData) {
					returnReasonIdInput.add(i);
				}
			}
		}
		
		if(dateType == null) {
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
		context.put("enumId[]", enumIdInput);
		context.put("returnReasonId[]", returnReasonIdInput);
		context.put("checkNPP", checkNPP);
		context.put("userLogin", userLogin);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String titleFromDate = UtilProperties.getMessage(resource, "FromDate", locale);
			String titleThruDate = UtilProperties.getMessage(resource, "ThruDate", locale);
			
			Map<String, Object> resultService =  dispatcher.runSync("jqGetListReturnProductReportOlap", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
			
			//start renderExcel
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
			
			sheet.setColumnWidth(0, 21*300);
			sheet.setColumnWidth(1, 21*300);
			sheet.setColumnWidth(2, 21*300);
			sheet.setColumnWidth(3, 21*600);
			sheet.setColumnWidth(4, 21*300);
			sheet.setColumnWidth(5, 21*300);
			sheet.setColumnWidth(6, 21*300);
			sheet.setColumnWidth(7, 21*300);
			sheet.setColumnWidth(8, 21*400);
			sheet.setColumnWidth(9, 21*400);
			sheet.setColumnWidth(10, 21*300);
			sheet.setColumnWidth(11, 21*300);
			sheet.setColumnWidth(12, 21*300);
			sheet.setColumnWidth(13, 21*300);
			sheet.setColumnWidth(14, 21*500);
			/*Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);*/
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
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
			}finally{
				if(is!=null)is.close();
			}
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,14));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,14));
			Cell titleCell = titleRow.createCell(0);
			String title = UtilProperties.getMessage(resource, "LogStatisticalReturnProductReport", locale);
			if(checkNPP.equals("NPP_FALSE")){
				title = UtilProperties.getMessage(resource, "LogStatisticalReturnProductReport", locale);
			}else{
				title = UtilProperties.getMessage(resource, "LogStatisticalReturnProductDistributorReport", locale);
			}
			titleCell.setCellValue(title.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,14));
			Cell dateFromToCell = dateRow.createCell(0);
			String dateFromTo = titleFromDate + ": " + fromDateStrTime + " - " + titleThruDate + ": " + thruDateStrTime; 
			dateFromToCell.setCellValue(dateFromTo);
			dateFromToCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,14));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			String titleDateReturn = UtilProperties.getMessage(resource, "LogDateReturn", locale);
			String titleReturnCode = UtilProperties.getMessage(resource, "LogCodeReturns", locale);
			String titleProductCode = UtilProperties.getMessage(resource, "ProductCode", locale);
			String titleProductName = UtilProperties.getMessage(resource, "ProductName", locale);
			String titleQuantityReturn = UtilProperties.getMessage(resource, "QuantityReturned", locale);
			String titleQuantityUom = UtilProperties.getMessage(resource, "QuantityUomId", locale);
			/*String titleTotalPrice = UtilProperties.getMessage(resource, "RemainingSubTotal", locale);*/
			String titleOrderSale = UtilProperties.getMessage(resource, "LogOrdersSale", locale);
			String titleReasonReturn = UtilProperties.getMessage(resource, "LogRejectReasonReturnProduct", locale);
			String titlePersonReturn = UtilProperties.getMessage(resource, "LogProductReturns", locale);
			String titlePersonReceive = UtilProperties.getMessage(resource, "Receiver", locale);
			String titleReturnType = UtilProperties.getMessage(resource, "LogGoodReturned", locale);
			String titleFacilityName = UtilProperties.getMessage(resource, "LogFacilityName", locale);
			String titleStatus = UtilProperties.getMessage(resource, "Status", locale);
			String titlePurchaseChanel = UtilProperties.getMessage(resource, "LogPurchaseChannels", locale);
			String titleCategory = UtilProperties.getMessage(resource, "DmsProductCatalogs", locale);
			List<String> titles = new FastList<String>();
			titles.add(titleDateReturn);
			titles.add(titleReturnCode);
			titles.add(titleProductCode);
			titles.add(titleProductName);
			titles.add(titleQuantityReturn);
			titles.add(titleQuantityUom);
			/*titles.add(titleTotalPrice);*/
			titles.add(titleOrderSale);
			titles.add(titleReasonReturn);
			titles.add(titlePersonReturn);
			titles.add(titlePersonReceive);
			titles.add(titleReturnType);
			titles.add(titleFacilityName);
			titles.add(titleStatus);
			titles.add(titlePurchaseChanel);
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
				String decription  = quantityUomId;
				if(uom != null){
					decription = uom.getString("description");
				}
				Cell quantityUomIdCell = orderDetailRow.createCell(5);
				quantityUomIdCell.setCellValue(decription);
				quantityUomIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				/*int returnPrice = (Integer) map.get("returnPrice");
				Cell returnPriceCell = orderDetailRow.createCell(6);
				returnPriceCell.setCellValue(returnPrice);
				returnPriceCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));*/
			
				String orderIdOut = (String) map.get("orderId");
				Cell orderIdCell = orderDetailRow.createCell(6);
				orderIdCell.setCellValue(orderIdOut);
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String returnReasonIdOut = (String) map.get("returnReasonId");
				GenericValue returnReason = delegator.findOne("ReturnReason", UtilMisc.toMap("returnReasonId", returnReasonIdOut), false);
				if(returnReason != null){
					returnReasonIdOut = returnReason.getString("description");
				}
				Cell returnReasonIdCell = orderDetailRow.createCell(7);
				returnReasonIdCell.setCellValue(returnReasonIdOut);
				returnReasonIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String partyFromIdOut = (String) map.get("partyFromId");
				Cell partyFromIdCell = orderDetailRow.createCell(8);
				partyFromIdCell.setCellValue(partyFromIdOut);
				partyFromIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String partyToIdOut = (String) map.get("partyToId");
				Cell partyToIdCell = orderDetailRow.createCell(9);
				partyToIdCell.setCellValue(partyToIdOut);
				partyToIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String returnItemTypeId = (String) map.get("returnItemTypeId");
				GenericValue returnItemType = delegator.findOne("ReturnItemType", UtilMisc.toMap("returnItemTypeId", returnItemTypeId), false);
				if(returnItemType != null){
					returnItemTypeId = returnItemType.getString("description");
				}
				Cell returnItemTypeIdCell = orderDetailRow.createCell(10);
				returnItemTypeIdCell.setCellValue(returnItemTypeId);
				returnItemTypeIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String facilityIdOut = (String) map.get("facilityId");
				Cell facilityIdCell = orderDetailRow.createCell(11);
				facilityIdCell.setCellValue(facilityIdOut);
				facilityIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String statusIdOut = (String) map.get("statusId");
				Cell statusIdCell = orderDetailRow.createCell(12);
				statusIdCell.setCellValue(statusIdOut);
				statusIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String enumIdOut = (String) map.get("productStoreId");
				String description = enumIdOut;
				GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumIdOut), false);
				if(enumeration != null){
					description = (String) enumeration.get("description", UtilHttp.getLocale(request));
				}
				Cell enumIdCell = orderDetailRow.createCell(13);
				enumIdCell.setCellValue(description);
				enumIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String categoryName = (String) map.get("categoryName");
				Cell categoryIdCell = orderDetailRow.createCell(14);
				categoryIdCell.setCellValue(categoryName);
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
			if(baos != null)baos.close();
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void exportTransferItemOlapToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String dateType = request.getParameter("dateType");    
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		
		if(productId.equals("") || productId.equals("null")){
			productIdInput = null;
		}
		if(!productId.equals("") && !productId.equals("null")){
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if(productIdData.length != 0){
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}
		
		if(facilityId.equals("") || facilityId.equals("null")){
			facilityIdInput = null;
		}
		if(!facilityId.equals("") && !facilityId.equals("null")){
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if(facilityIdData.length != 0){
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}
		
		if(dateType == null) {
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
		context.put("userLogin", userLogin);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String titleFromDate = UtilProperties.getMessage(resource, "FromDate", locale);
			String titleThruDate = UtilProperties.getMessage(resource, "ThruDate", locale);
			
			Map<String, Object> resultService =  dispatcher.runSync("jqGetListTransferItemReportOlap", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
			
			//start renderExcel
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
			
			sheet.setColumnWidth(0, 21*250);
			sheet.setColumnWidth(1, 21*300);
			sheet.setColumnWidth(2, 21*300);
			sheet.setColumnWidth(3, 21*300);
			sheet.setColumnWidth(4, 21*300);
			sheet.setColumnWidth(5, 21*300);
			sheet.setColumnWidth(6, 21*500);
			sheet.setColumnWidth(7, 21*300);
			sheet.setColumnWidth(8, 21*300);
			sheet.setColumnWidth(9, 21*300);
			sheet.setColumnWidth(10, 21*300);
			sheet.setColumnWidth(11, 21*300);
			sheet.setColumnWidth(12, 21*300);
			sheet.setColumnWidth(13, 21*300);
			sheet.setColumnWidth(14, 21*300);
			sheet.setColumnWidth(15, 21*300);
			sheet.setColumnWidth(16, 21*300);
			
			/*Row imgHead = sheet.createRow(0);*/
			/*Cell imgCell = imgHead.createCell(0);*/
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("baselogistics", "image.management.logoPath"), null);
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
			}finally{
				if(is!=null)is.close();
			}
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,16));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,16));
			Cell titleCell = titleRow.createCell(0);
			String title = UtilProperties.getMessage(resource, "ReportTransfer", locale);
			titleCell.setCellValue(title.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,16));
			Cell dateFromToCell = dateRow.createCell(0);
			String dateFromTo = titleFromDate + ": " + fromDateStrTime + " - " + titleThruDate + ": " + thruDateStrTime; 
			dateFromToCell.setCellValue(dateFromTo);
			dateFromToCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,16));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			String titleRequiredByDate = UtilProperties.getMessage(resource, "RequiredByDate", locale);
			String titleTransferId = UtilProperties.getMessage(resource, "TransferId", locale);
			String titleTransferType = UtilProperties.getMessage(resource, "TransferType", locale);
			String titleFacilityFrom = UtilProperties.getMessage(resource, "FacilityFrom", locale);
			String titleFacilityTo = UtilProperties.getMessage(resource, "FacilityTo", locale);
			String titleProductCode = UtilProperties.getMessage(resource, "ProductCode", locale);
			String titleProductName = UtilProperties.getMessage(resource, "ProductName", locale);
			String titleProductManufactureDate = UtilProperties.getMessage(resource, "ProductManufactureDate", locale);
			String titleExpireDate = UtilProperties.getMessage(resource, "ExpireDate", locale);
			String titleRequiredNumber = UtilProperties.getMessage(resource, "RequiredNumber", locale);
			String titleShipmentStatus = UtilProperties.getMessage(resource, "LogShipmentStatus", locale);
			String titleDeliveryTransferId = UtilProperties.getMessage(resource, "DeliveryTransferId", locale);
			String titleStatusDelivery = UtilProperties.getMessage(resource, "StatusDelivery", locale);
			String titleActualExportedQuantity = UtilProperties.getMessage(resource, "ActualExportedQuantity", locale);
			String titleQuantityUomId = UtilProperties.getMessage(resource, "QuantityUomId", locale);
			String titleCreatedBy = UtilProperties.getMessage(resource, "CreatedBy", locale);
			String titleBatch = UtilProperties.getMessage(resource, "Batch", locale);
			
			titles.add(titleRequiredByDate);
			titles.add(titleTransferId);
			titles.add(titleTransferType);
			titles.add(titleFacilityFrom);
			titles.add(titleFacilityTo);
			titles.add(titleProductCode);
			titles.add(titleProductName);
			titles.add(titleProductManufactureDate);
			titles.add(titleExpireDate);
			titles.add(titleRequiredNumber);
			titles.add(titleShipmentStatus);
			titles.add(titleDeliveryTransferId);
			titles.add(titleStatusDelivery);
			titles.add(titleActualExportedQuantity);
			titles.add(titleQuantityUomId);
			titles.add(titleCreatedBy);
			titles.add(titleBatch);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 550);
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			rownum += 1;
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 500);
					
				Cell dateCell = orderDetailRow.createCell(0);
				String date = (String) map.get("date");
				dateCell.setCellValue(date);
				dateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String transferIdOut = (String) map.get("transferId");
				Cell transferIdCell = orderDetailRow.createCell(1);
				transferIdCell.setCellValue(transferIdOut);
				transferIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String transferTypeOut = (String) map.get("transferTypeId");
				GenericValue transferType = delegator.findOne("TransferType", UtilMisc.toMap("transferTypeId", transferTypeOut), false);
				if(UtilValidate.isNotEmpty(transferType)){
					transferTypeOut = (String) transferType.get("description", UtilHttp.getLocale(request));
				}
				Cell transferTypeCell = orderDetailRow.createCell(2);
				transferTypeCell.setCellValue(transferTypeOut);
				transferTypeCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String originFacilityNameOut = (String) map.get("originFacilityName");
				Cell originFacilityNameCell = orderDetailRow.createCell(3);
				originFacilityNameCell.setCellValue(originFacilityNameOut);
				originFacilityNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String destFacilityNameOut = (String) map.get("destFacilityName");
				Cell destFacilityNameCell = orderDetailRow.createCell(4);
				destFacilityNameCell.setCellValue(destFacilityNameOut);
				destFacilityNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String productCodeOut = (String) map.get("productCode");
				Cell productCodeCell = orderDetailRow.createCell(5);
				productCodeCell.setCellValue(productCodeOut);
				productCodeCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String internalName = (String) map.get("internalName");
				Cell internalNameCell = orderDetailRow.createCell(6);
				internalNameCell.setCellValue(internalName);
				internalNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String datetimeManufactured = (String) map.get("datetimeManufactured");
				Cell datetimeManufacturedCell = orderDetailRow.createCell(7);
				datetimeManufacturedCell.setCellValue(datetimeManufactured);
				datetimeManufacturedCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String expireDate = (String) map.get("expireDate");
				Cell expireDateCell = orderDetailRow.createCell(8);
				expireDateCell.setCellValue(expireDate);
				expireDateCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				
				BigDecimal quantity = (BigDecimal) map.get("quantity");
				double quantityDou = 0;
				if(UtilValidate.isNotEmpty(quantity)){
					quantityDou = quantity.doubleValue();
				}
				Cell quantityCell = orderDetailRow.createCell(9);
				quantityCell.setCellValue(quantityDou);
				quantityCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String statusId = (String) map.get("statusId");
				Cell statusIdCell = orderDetailRow.createCell(10);
				GenericValue statusItemTransfer = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				if(UtilValidate.isNotEmpty(statusItemTransfer)){
					statusId = (String) statusItemTransfer.get("description", UtilHttp.getLocale(request));
				}
				statusIdCell.setCellValue(statusId);
				statusIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			
				String deliveryId = (String) map.get("deliveryId");
				Cell deliveryIdCell = orderDetailRow.createCell(11);
				deliveryIdCell.setCellValue(deliveryId);
				deliveryIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String deliveryStatusId = (String) map.get("deliveryStatusId");
				GenericValue deliveryStatusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", deliveryStatusId), false);
				if(UtilValidate.isNotEmpty(deliveryStatusItem)){
					deliveryStatusId = (String) deliveryStatusItem.get("description", UtilHttp.getLocale(request));
				}
				Cell deliveryStatusCell = orderDetailRow.createCell(12);
				deliveryStatusCell.setCellValue(deliveryStatusId);
				deliveryStatusCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				BigDecimal actualExportedQuantity = (BigDecimal) map.get("actualExportedQuantity");
				double actualExportedQuantityDou = 0;
				if(UtilValidate.isNotEmpty(actualExportedQuantity)){
					actualExportedQuantityDou = actualExportedQuantity.doubleValue();
				}
				Cell actualExportedQuantityCell = orderDetailRow.createCell(13);
				actualExportedQuantityCell.setCellValue(actualExportedQuantityDou);
				actualExportedQuantityCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String uomId = (String) map.get("uomId");
				Cell uomIdCell = orderDetailRow.createCell(14);
				uomIdCell.setCellValue(uomId);
				uomIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String partyName = (String) map.get("partyName");
				Cell partyNameCell = orderDetailRow.createCell(15);
				partyNameCell.setCellValue(partyName);
				partyNameCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				String lotId = (String) map.get("lotId");
				Cell lotIdCell = orderDetailRow.createCell(16);
				lotIdCell.setCellValue(lotId);
				lotIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Calendar cal = Calendar.getInstance();
			String currentDate = dateFormat.format(cal.getTime());
			response.setHeader("content-disposition", "attachment;filename=" + "TransferReport_" + currentDate + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if(baos != null)baos.close();
		}
	}
}