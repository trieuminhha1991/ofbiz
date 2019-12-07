package com.olbius.basesales.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	public static String module = MicrosoftDocumentsServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String euroSign = new String("\u20AC");
	public static final String leftDoubleQuotationMark = new String("\u201C");
	public static final String rightDoubleQuotationMark = new String("\u201D");
	public static final String draftingPointRightWardArrow = new String("\u279B");
	public static final String ballotBox = new String("\u2610");
	
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
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		styles.put("cell_normal_centered_border_full_10", style);
		
		Font normalRightBorderFullFont100 = wb.createFont();
		normalRightBorderFullFont100.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(normalRightBorderFullFont100);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		styles.put("cell_normal_right_border_full_10", style);
		
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
		
		Font normalBoldLeftFont8 = wb.createFont();
		normalBoldLeftFont8.setFontHeightInPoints((short) 8);
		normalBoldLeftFont8.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalBoldLeftFont8);
		styles.put("cell_bold_normal_Left_8", style);
		
		Font normalBoldLeftFont10 = wb.createFont();
		normalBoldLeftFont10.setFontHeightInPoints((short) 10);
		normalBoldLeftFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalBoldLeftFont10);
		styles.put("cell_bold_normal_Left_10", style);
		
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

		/*Font normalCenterBorderTopFont10 = wb.createFont();
		normalCenterBorderTopFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalCenterBorderTopFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderTopFont11);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_top_11", style);*/
		
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
		style.setFont(normalCenterFont11);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		styles.put("cell_normal_centered_not_border_thin_11", style);
		
		Font normalCenterFont12 = wb.createFont();
		normalCenterFont12.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalCenterFont12.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalCenterFont11);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		styles.put("cell_normal_left_11", style);
		
		Font boldCenterDoubleBorderTopFont10 = wb.createFont();
		boldCenterDoubleBorderTopFont10.setFontHeightInPoints((short) 10);
		boldCenterDoubleBorderTopFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterDoubleBorderTopFont10);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		styles.put("cell_bold_center_wrap_text_double_border_top_10", style);
		
		return styles;
	}
	
	//test thu
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportSynthesisReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String orderStatus = request.getParameter("orderStatus");
		List<String> orderStatusInput = null;
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusInput = null;
		}
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData = orderStatus.split(",");
			orderStatusInput = new ArrayList<>();
			if(statusData.length != 0){
				for (String i : statusData) {
					orderStatusInput.add(i);
				}
			}
		}
		
		
		Locale locale = UtilHttp.getLocale(request); 
		
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSByStore", locale);
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("orderStatus[]", orderStatusInput);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateSalesSynthesisReport", context);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 50*256);
			sheet.setColumnWidth(2, 30*256);
			for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 18*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
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
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue("BÁO CÁO TỔNG HỢP".toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellValue(mainTitle.toUpperCase());
			khoangCachCell2.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("Mã sản phẩm");
			titles.add("Tên sản phẩm");
			titles.add("Trạng thái đơn");
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			Map<String, Object> listResult = dispatcher.runSync("getListResultStore2", UtilMisc.toMap());
			List<String> listResultStore = (List<String>) listResult.get("listResultStore");
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			
			for (String s : listResultStore) {
				GenericValue product = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", s), false);
				String internalName = product.getString("storeName");
				
				Cell orderIdCell = headerBreakdownAmountRow.createCell(index + listResultStore.indexOf(s));
				orderIdCell.setCellValue(internalName);
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
			}
			rownum += 1;
			
//			sheet.createFreezePane(8, 7);
			sheet.createFreezePane(2, 7);
			
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("productId")){
						Cell orderIdCell = orderDetailRow.createCell(0);
						String storetId = (String) entry.getValue();
						orderIdCell.setCellValue(storetId);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productName")){
						Cell orderIdCell = orderDetailRow.createCell(1);
						String storeName = (String) entry.getValue();
						orderIdCell.setCellValue(storeName);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("status")){
						Cell statusCell = orderDetailRow.createCell(2);
						String sttDes = (String) entry.getValue();
						statusCell.setCellValue(sttDes);
						statusCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					for (String s : listResultStore) {
						if(entry.getKey().equals(s)){
							Cell orderIdCell = orderDetailRow.createCell(index + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							if (value.split("\\.").length > 1) {
								value = value.split("\\.")[0];
							}
							orderIdCell.setCellValue( value);
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
						}
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_tong_hop_doanh_so_ban_hang" + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportSynthesisReportBySaExToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String orderStatus = request.getParameter("orderStatus");
		String channel = request.getParameter("channel");
		List<String> orderStatusInput = null;
		List<String> orderStatusOut = null;
		String channelInput = null;
		
		if(channel.equals("") || channel.equals("null")){
			channelInput = null;
		} else {
			channelInput = channel;
		}
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusInput = null;
		}
		
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData = orderStatus.split(",");
			orderStatusInput = new ArrayList<>();
			if(statusData.length != 0){
				for (String i : statusData) {
					orderStatusInput.add(i);
				}
			}
		}
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Locale locale = UtilHttp.getLocale(request); 
		
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSSynthesisReport", locale);
		String subTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSSynthesisReportBySalesExecutive", locale);
		String fromDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSFromDate", locale);
		String thruDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSThruDate", locale);
		String orderStatus_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderStatus", locale);
		String channel_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSChannel", locale);
		String all_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", locale);
		String valueTotal = UtilProperties.getMessage("BaseSalesUiLabels", "BSValueTotal", locale);
		String volumeTotal = UtilProperties.getMessage("BaseSalesUiLabels", "BSVolumeTotal", locale);
		subTitle = "iii. " + subTitle;
		subTitle.toUpperCase();
		String staffId = UtilProperties.getMessage("BaseSalesUiLabels", "BSStaffId", locale);
		String staffName = UtilProperties.getMessage("BaseSalesUiLabels", "BSFullName", locale);
		String byVolume = UtilProperties.getMessage("BaseSalesUiLabels", "BSByVolume", locale);
		String byValue = UtilProperties.getMessage("BaseSalesUiLabels", "BSByValue", locale);
		
//		GenericValue channelId = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", channel), false);
//		String channelName = channelId.getString("description");
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusOut = null;
		}
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData_ = orderStatus.split(",");
			orderStatusOut = new ArrayList<>();
			if(statusData_.length != 0){
				for (String i : statusData_) {
					GenericValue orderStatusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", i), false);
					String orderStatusDes =(String) orderStatusId.get("description", locale);
					orderStatusOut.add(orderStatusDes);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("orderStatus[]", orderStatusInput);
		context.put("channel", channelInput);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateSalesSynthesisReportBySalesExecutive", context);
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
			
			sheet.setColumnWidth(0, 20*256);
			sheet.setColumnWidth(1, 35*256);
			sheet.setColumnWidth(2, 25*256);
			for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 18*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
//			try {
//				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
//				File file = new File(imageServerPath);
//				is = new FileInputStream(file);
//				byte[] bytesImg = IOUtils.toByteArray(is);
//				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
//				CreationHelper helper = wb.getCreationHelper();
//				Drawing drawing = sheet.createDrawingPatriarch();
//				ClientAnchor anchor = helper.createClientAnchor();
//				anchor.setCol1(0);
//				anchor.setCol2(1);
//				anchor.setRow1(0);
//				anchor.setRow2(5);
//				Picture pict = drawing.createPicture(anchor, pictureIdx);
//				pict.getPictureData();
//				rownum = 5;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}finally{
//				if(is!=null)is.close();
//			}
			
//			Row titleRow = sheet.createRow(rownum);
//			titleRow.setHeight((short) 400);
//			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
//			Cell titleCell = titleRow.createCell(0);
//			titleCell.setCellValue(mainTitle.toUpperCase());
//			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
//			rownum += 1;

			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row channelRow = sheet.createRow(rownum);
			channelRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell channelCell = channelRow.createCell(0);
			if(channel.equals("") || channel.equals("null")){
				channelCell.setCellValue(channel_ + ": " + all_);
			} else {
				GenericValue channelId_ = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", channel), false);
				String channelName_ = channelId_.getString("description");
				channelCell.setCellValue(channel_ + ": " + channelName_);
			}
			channelCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell dateCell = dateRow.createCell(0);
			dateCell.setCellValue(fromDate_ + ": " + fromDateTs + "   -   " + thruDate_ + ": " + thruDateTs);
			dateCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row statusRow = sheet.createRow(rownum);
			statusRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell statusCell_ = statusRow.createCell(0);
			if(orderStatusOut != null){
				statusCell_.setCellValue(orderStatus_ + ": " + orderStatusOut);
			} else {
				statusCell_.setCellValue(orderStatus_ + ": " + all_);
			}
			statusCell_.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellValue(subTitle.toUpperCase());
			khoangCachCell2.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row spaceRow = sheet.createRow(rownum);
			spaceRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell spaceCell = spaceRow.createCell(0);
			spaceCell.setCellValue(" ");
			spaceCell.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row titleByRow = sheet.createRow(rownum);
			titleByRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell titleByCell2 = titleByRow.createCell(0);
			titleByCell2.setCellValue(("1." + byVolume).toUpperCase());
			titleByCell2.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add(staffId);
			titles.add(staffName);
			titles.add(volumeTotal);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			
			Map<String, Object> listResult = dispatcher.runSync("getListResultStore", UtilMisc.toMap());
			List<String> listResultStore = (List<String>) listResult.get("listResultStore");
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellValue(titles.get(i));
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			}
			
			for (String s : listResultStore) {
//				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", s), false);
//				String internalName = product.getString("internalName");
				
				Cell orderIdCell = headerBreakdownAmountRow.createCell(index);
				orderIdCell.setCellValue(s + "(SL)");
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				index += 1;
//				Cell orderIdCell1 = headerBreakdownAmountRow.createCell(index + 1);
//				orderIdCell1.setCellValue(s + "(TT)");
//				orderIdCell1.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
//				index += 2;
			}
			rownum += 1;
			
			sheet.createFreezePane(3, 6);
			index = titles.size();
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 380);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("staffId")){
						Cell orderIdCell = orderDetailRow.createCell(0);
						String storetId = (String) entry.getValue();
						orderIdCell.setCellValue(storetId);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("staffName")){
						Cell orderIdCell = orderDetailRow.createCell(1);
						String storeName = (String) entry.getValue();
						orderIdCell.setCellValue(storeName);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("volumeTotal")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						DecimalFormatSymbols symbols = new DecimalFormatSymbols();
						symbols.setGroupingSeparator(',');
						symbols.setDecimalSeparator('.');
						String pattern = "#,##0";
						DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
						decimalFormat.setParseBigDecimal(true);
//						String productTotalStr = productTotal.toString();
//						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow.createCell(2);
//						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellValue(decimalFormat.format(productTotal));
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					int dummy = 0;
					int dummy2 = 1;
					String nullValue = "-";
					for (String s : listResultStore) {
						if(entry.getKey().equals(s+"q")){
							Cell orderIdCell = orderDetailRow.createCell(index + dummy + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							BigDecimal abc = (BigDecimal) entry.getValue();
							DecimalFormatSymbols symbols = new DecimalFormatSymbols();
							symbols.setGroupingSeparator(',');
							symbols.setDecimalSeparator('.');
							String pattern = "#,##0";
							DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
							decimalFormat.setParseBigDecimal(true);
							if(value.equals("0")){
								orderIdCell.setCellValue(nullValue);
							}else{
//								if (value.split("\\.").length > 1) {
//									value = value.split("\\.")[0];
//								}
//								orderIdCell.setCellValue(value);
								orderIdCell.setCellValue(decimalFormat.format(abc));
							}
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
							dummy +=  1;
						}
					}
					
//					BigDecimal quantity = gv.getBigDecimal("quantity");
//					Cell cellQuantity = rowProducts.createCell(4);
//					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//					symbols.setGroupingSeparator(',');
//					symbols.setDecimalSeparator('.');
//					String pattern = "#,##0";
//					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
//					decimalFormat.setParseBigDecimal(true);
//					cellQuantity.setCellValue(decimalFormat.format(quantity));
//					cellQuantity.setCellStyle(styles.get("cell_border_right_top"));
					
					
					
//					for (String s : listResultStore) {
//						if(entry.getKey().equals(s+"t")){
//							Cell orderIdCell = orderDetailRow.createCell(index + dummy2 + listResultStore.indexOf(s));
//							String value = ((BigDecimal) entry.getValue()).toString();
//							if(value.equals("0")){
//								orderIdCell.setCellValue(nullValue);
//							} else {
//								if (value.split("\\.").length > 1) {
//									value = value.split("\\.")[0];
//								}
//								orderIdCell.setCellValue(value);
//							}
//							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
//						}
//						dummy2 +=  1;
//					}
				}
				rownum += 1;
			}
			
			Row khoangCachRow2c = sheet.createRow(rownum);
			khoangCachRow2c.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2c = khoangCachRow2c.createCell(0);
			khoangCachCell2c.setCellValue("");
			khoangCachCell2c.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row khoangCachRow2b = sheet.createRow(rownum);
			khoangCachRow2b.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2b = khoangCachRow2b.createCell(0);
			khoangCachCell2b.setCellValue(("2." + byValue).toUpperCase());
			khoangCachCell2b.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			List<String> titles2 = new FastList<String>();
			titles2.add(staffId);
			titles2.add(staffName);
			titles2.add(valueTotal);
			Row headerBreakdownAmountRow2 = sheet.createRow(rownum);
			headerBreakdownAmountRow2.setHeight((short) 500);
			headerBreakdownAmountRow2.setRowStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			
			index = titles.size();
			
			for (int i = 0; i < titles2.size(); i++) {
				Cell headerBreakdownAmountCell2 = headerBreakdownAmountRow2.createCell(i);
				headerBreakdownAmountCell2.setCellValue(titles2.get(i));
				headerBreakdownAmountCell2.setCellStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			}
			
			for (String s : listResultStore) {
//				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", s), false);
//				String internalName = product.getString("internalName");
				
				Cell orderIdCell = headerBreakdownAmountRow2.createCell(index);
				orderIdCell.setCellValue(s + "(TT)");
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				index += 1;
//				Cell orderIdCell1 = headerBreakdownAmountRow2.createCell(index + 1);
//				orderIdCell1.setCellValue(s + "(TT)");
//				orderIdCell1.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
//				index += 2;
			}
			rownum += 1;
			
			index = titles.size();
			for (Map<String, Object> map2 : listData) {
				Row orderDetailRow2 = sheet.createRow(rownum);
				orderDetailRow2.setHeight((short) 380);
				for(Map.Entry<String,Object> entry: map2.entrySet()){
					if(entry.getKey().equals("staffId")){
						Cell orderIdCell = orderDetailRow2.createCell(0);
						String storetId = (String) entry.getValue();
						orderIdCell.setCellValue(storetId);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("staffName")){
						Cell orderIdCell = orderDetailRow2.createCell(1);
						String storeName = (String) entry.getValue();
						orderIdCell.setCellValue(storeName);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("valueTotal")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						DecimalFormatSymbols symbols = new DecimalFormatSymbols();
						symbols.setGroupingSeparator(',');
						symbols.setDecimalSeparator('.');
						String pattern = "#,##0";
						DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
						decimalFormat.setParseBigDecimal(true);
//						String productTotalStr = productTotal.toString();
//						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow2.createCell(2);
//						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellValue(decimalFormat.format(productTotal));
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					int dummy = 0;
					int dummy2 = 1;
					String nullValue = "-";
//					for (String s : listResultStore) {
//						if(entry.getKey().equals(s+"q")){
//							Cell orderIdCell = orderDetailRow2.createCell(index + dummy + listResultStore.indexOf(s));
//							String value = ((BigDecimal) entry.getValue()).toString();
//							if(value.equals("0")){
//								orderIdCell.setCellValue(nullValue);
//							}else{
//								if (value.split("\\.").length > 1) {
//									value = value.split("\\.")[0];
//								}
//								orderIdCell.setCellValue(value);
//							}
//							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
//						}
//						dummy +=  1;
//					}
					for (String s : listResultStore) {
						if(entry.getKey().equals(s+"t")){
							Cell orderIdCell = orderDetailRow2.createCell(index + dummy + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							BigDecimal abc = (BigDecimal) entry.getValue();
							DecimalFormatSymbols symbols = new DecimalFormatSymbols();
							symbols.setGroupingSeparator(',');
							symbols.setDecimalSeparator('.');
							String pattern = "#,##0";
							DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
							decimalFormat.setParseBigDecimal(true);
							if(value.equals("0")){
								orderIdCell.setCellValue(nullValue);
							} else {
//								if (value.split("\\.").length > 1) {
//									value = value.split("\\.")[0];
//								}
//								orderIdCell.setCellValue(value);
								orderIdCell.setCellValue(decimalFormat.format(abc));
							}
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
							dummy +=  1;
						}
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_TH_doanh_so_ban_hang_theo_CVBH" + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportSynthesisReportByStaffToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String orderStatus = request.getParameter("orderStatus");
		String channel = request.getParameter("channel");
		List<String> orderStatusInput = null;
		List<String> orderStatusOut = null;
		String channelInput = null;
		
		if(channel.equals("") || channel.equals("null")){
			channelInput = null;
		} else {
			channelInput = channel;
		}
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusInput = null;
		}
		
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData = orderStatus.split(",");
			orderStatusInput = new ArrayList<>();
			if(statusData.length != 0){
				for (String i : statusData) {
					orderStatusInput.add(i);
				}
			}
		}
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Locale locale = UtilHttp.getLocale(request); 
		
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSSynthesisReport", locale);
		String subTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSSynthesisReportByStaff", locale);
		String fromDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSFromDate", locale);
		String thruDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSThruDate", locale);
		String orderStatus_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderStatus", locale);
		String channel_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSChannel", locale);
		String all_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", locale);
		String valueTotal = UtilProperties.getMessage("BaseSalesUiLabels", "BSValueTotal", locale);
		String volumeTotal = UtilProperties.getMessage("BaseSalesUiLabels", "BSVolumeTotal", locale);
		subTitle = "iii. " + subTitle;
		subTitle.toUpperCase();
		String staffId = UtilProperties.getMessage("BaseSalesUiLabels", "BSStaffId", locale);
		String staffName = UtilProperties.getMessage("BaseSalesUiLabels", "BSFullName", locale);
		String byVolume = UtilProperties.getMessage("BaseSalesUiLabels", "BSByVolume", locale);
		String byValue = UtilProperties.getMessage("BaseSalesUiLabels", "BSByValue", locale);
		
//		GenericValue channelId = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", channel), false);
//		String channelName = channelId.getString("description");
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusOut = null;
		}
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData_ = orderStatus.split(",");
			orderStatusOut = new ArrayList<>();
			if(statusData_.length != 0){
				for (String i : statusData_) {
					GenericValue orderStatusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", i), false);
					String orderStatusDes =(String) orderStatusId.get("description", locale);
					orderStatusOut.add(orderStatusDes);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("orderStatus[]", orderStatusInput);
		context.put("channel", channelInput);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateSalesSynthesisReportByStaff", context);
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
			
			sheet.setColumnWidth(0, 20*256);
			sheet.setColumnWidth(1, 35*256);
			sheet.setColumnWidth(2, 25*256);
			for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 18*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
//			try {
//				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
//				File file = new File(imageServerPath);
//				is = new FileInputStream(file);
//				byte[] bytesImg = IOUtils.toByteArray(is);
//				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
//				CreationHelper helper = wb.getCreationHelper();
//				Drawing drawing = sheet.createDrawingPatriarch();
//				ClientAnchor anchor = helper.createClientAnchor();
//				anchor.setCol1(0);
//				anchor.setCol2(1);
//				anchor.setRow1(0);
//				anchor.setRow2(5);
//				Picture pict = drawing.createPicture(anchor, pictureIdx);
//				pict.getPictureData();
//				rownum = 5;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}finally{
//				if(is!=null)is.close();
//			}
			
//			Row titleRow = sheet.createRow(rownum);
//			titleRow.setHeight((short) 400);
//			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
//			Cell titleCell = titleRow.createCell(0);
//			titleCell.setCellValue(mainTitle.toUpperCase());
//			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
//			rownum += 1;

			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row channelRow = sheet.createRow(rownum);
			channelRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell channelCell = channelRow.createCell(0);
			if(channel.equals("") || channel.equals("null")){
				channelCell.setCellValue(channel_ + ": " + all_);
			} else {
				GenericValue channelId_ = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", channel), false);
				String channelName_ = channelId_.getString("description");
				channelCell.setCellValue(channel_ + ": " + channelName_);
			}
			channelCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell dateCell = dateRow.createCell(0);
			dateCell.setCellValue(fromDate_ + ": " + fromDateTs + "   -   " + thruDate_ + ": " + thruDateTs);
			dateCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row statusRow = sheet.createRow(rownum);
			statusRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell statusCell_ = statusRow.createCell(0);
			if(orderStatusOut != null){
				statusCell_.setCellValue(orderStatus_ + ": " + orderStatusOut);
			} else {
				statusCell_.setCellValue(orderStatus_ + ": " + all_);
			}
			statusCell_.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellValue(subTitle.toUpperCase());
			khoangCachCell2.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row spaceRow = sheet.createRow(rownum);
			spaceRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell spaceCell = spaceRow.createCell(0);
			spaceCell.setCellValue(" ");
			spaceCell.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row titleByRow = sheet.createRow(rownum);
			titleByRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell titleByCell2 = titleByRow.createCell(0);
			titleByCell2.setCellValue(("1." + byVolume).toUpperCase());
			titleByCell2.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add(staffId);
			titles.add(staffName);
			titles.add(volumeTotal);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			
			Map<String, Object> listResult = dispatcher.runSync("getListResultStore", UtilMisc.toMap());
			List<String> listResultStore = (List<String>) listResult.get("listResultStore");
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellValue(titles.get(i));
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			}
			
			for (String s : listResultStore) {
//				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", s), false);
//				String internalName = product.getString("internalName");
				
				Cell orderIdCell = headerBreakdownAmountRow.createCell(index);
				orderIdCell.setCellValue(s + "(SL)");
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				index += 1;
//				Cell orderIdCell1 = headerBreakdownAmountRow.createCell(index + 1);
//				orderIdCell1.setCellValue(s + "(TT)");
//				orderIdCell1.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
//				index += 2;
			}
			rownum += 1;
			
			sheet.createFreezePane(3, 6);
			index = titles.size();
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 380);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("staffId")){
						Cell orderIdCell = orderDetailRow.createCell(0);
						String storetId = (String) entry.getValue();
						orderIdCell.setCellValue(storetId);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("staffName")){
						Cell orderIdCell = orderDetailRow.createCell(1);
						String storeName = (String) entry.getValue();
						orderIdCell.setCellValue(storeName);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("volumeTotal")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						DecimalFormatSymbols symbols = new DecimalFormatSymbols();
						symbols.setGroupingSeparator(',');
						symbols.setDecimalSeparator('.');
						String pattern = "#,##0";
						DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
						decimalFormat.setParseBigDecimal(true);
//						String productTotalStr = productTotal.toString();
//						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow.createCell(2);
//						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellValue(decimalFormat.format(productTotal));
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					int dummy = 0;
					int dummy2 = 1;
					String nullValue = "-";
					for (String s : listResultStore) {
						if(entry.getKey().equals(s+"q")){
							Cell orderIdCell = orderDetailRow.createCell(index + dummy + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							BigDecimal abc = (BigDecimal) entry.getValue();
							DecimalFormatSymbols symbols = new DecimalFormatSymbols();
							symbols.setGroupingSeparator(',');
							symbols.setDecimalSeparator('.');
							String pattern = "#,##0";
							DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
							decimalFormat.setParseBigDecimal(true);
							if(value.equals("0")){
								orderIdCell.setCellValue(nullValue);
							}else{
//								if (value.split("\\.").length > 1) {
//									value = value.split("\\.")[0];
//								}
//								orderIdCell.setCellValue(value);
								orderIdCell.setCellValue(decimalFormat.format(abc));
							}
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
							dummy +=  1;
						}
					}
					
//					BigDecimal quantity = gv.getBigDecimal("quantity");
//					Cell cellQuantity = rowProducts.createCell(4);
//					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//					symbols.setGroupingSeparator(',');
//					symbols.setDecimalSeparator('.');
//					String pattern = "#,##0";
//					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
//					decimalFormat.setParseBigDecimal(true);
//					cellQuantity.setCellValue(decimalFormat.format(quantity));
//					cellQuantity.setCellStyle(styles.get("cell_border_right_top"));
					
					
					
//					for (String s : listResultStore) {
//						if(entry.getKey().equals(s+"t")){
//							Cell orderIdCell = orderDetailRow.createCell(index + dummy2 + listResultStore.indexOf(s));
//							String value = ((BigDecimal) entry.getValue()).toString();
//							if(value.equals("0")){
//								orderIdCell.setCellValue(nullValue);
//							} else {
//								if (value.split("\\.").length > 1) {
//									value = value.split("\\.")[0];
//								}
//								orderIdCell.setCellValue(value);
//							}
//							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
//						}
//						dummy2 +=  1;
//					}
				}
				rownum += 1;
			}
			
			Row khoangCachRow2c = sheet.createRow(rownum);
			khoangCachRow2c.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2c = khoangCachRow2c.createCell(0);
			khoangCachCell2c.setCellValue("");
			khoangCachCell2c.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row khoangCachRow2b = sheet.createRow(rownum);
			khoangCachRow2b.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2b = khoangCachRow2b.createCell(0);
			khoangCachCell2b.setCellValue(("2." + byValue).toUpperCase());
			khoangCachCell2b.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			List<String> titles2 = new FastList<String>();
			titles2.add(staffId);
			titles2.add(staffName);
			titles2.add(valueTotal);
			Row headerBreakdownAmountRow2 = sheet.createRow(rownum);
			headerBreakdownAmountRow2.setHeight((short) 500);
			headerBreakdownAmountRow2.setRowStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			
			index = titles.size();
			
			for (int i = 0; i < titles2.size(); i++) {
				Cell headerBreakdownAmountCell2 = headerBreakdownAmountRow2.createCell(i);
				headerBreakdownAmountCell2.setCellValue(titles2.get(i));
				headerBreakdownAmountCell2.setCellStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			}
			
			for (String s : listResultStore) {
//				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", s), false);
//				String internalName = product.getString("internalName");
				
				Cell orderIdCell = headerBreakdownAmountRow2.createCell(index);
				orderIdCell.setCellValue(s + "(TT)");
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				index += 1;
//				Cell orderIdCell1 = headerBreakdownAmountRow2.createCell(index + 1);
//				orderIdCell1.setCellValue(s + "(TT)");
//				orderIdCell1.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
//				index += 2;
			}
			rownum += 1;
			
			index = titles.size();
			for (Map<String, Object> map2 : listData) {
				Row orderDetailRow2 = sheet.createRow(rownum);
				orderDetailRow2.setHeight((short) 380);
				for(Map.Entry<String,Object> entry: map2.entrySet()){
					if(entry.getKey().equals("staffId")){
						Cell orderIdCell = orderDetailRow2.createCell(0);
						String storetId = (String) entry.getValue();
						orderIdCell.setCellValue(storetId);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("staffName")){
						Cell orderIdCell = orderDetailRow2.createCell(1);
						String storeName = (String) entry.getValue();
						orderIdCell.setCellValue(storeName);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("valueTotal")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						DecimalFormatSymbols symbols = new DecimalFormatSymbols();
						symbols.setGroupingSeparator(',');
						symbols.setDecimalSeparator('.');
						String pattern = "#,##0";
						DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
						decimalFormat.setParseBigDecimal(true);
//						String productTotalStr = productTotal.toString();
//						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow2.createCell(2);
//						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellValue(decimalFormat.format(productTotal));
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					int dummy = 0;
					int dummy2 = 1;
					String nullValue = "-";
//					for (String s : listResultStore) {
//						if(entry.getKey().equals(s+"q")){
//							Cell orderIdCell = orderDetailRow2.createCell(index + dummy + listResultStore.indexOf(s));
//							String value = ((BigDecimal) entry.getValue()).toString();
//							if(value.equals("0")){
//								orderIdCell.setCellValue(nullValue);
//							}else{
//								if (value.split("\\.").length > 1) {
//									value = value.split("\\.")[0];
//								}
//								orderIdCell.setCellValue(value);
//							}
//							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
//						}
//						dummy +=  1;
//					}
					for (String s : listResultStore) {
						if(entry.getKey().equals(s+"t")){
							Cell orderIdCell = orderDetailRow2.createCell(index + dummy + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							BigDecimal abc = (BigDecimal) entry.getValue();
							DecimalFormatSymbols symbols = new DecimalFormatSymbols();
							symbols.setGroupingSeparator(',');
							symbols.setDecimalSeparator('.');
							String pattern = "#,##0";
							DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
							decimalFormat.setParseBigDecimal(true);
							if(value.equals("0")){
								orderIdCell.setCellValue(nullValue);
							} else {
//								if (value.split("\\.").length > 1) {
//									value = value.split("\\.")[0];
//								}
//								orderIdCell.setCellValue(value);
								orderIdCell.setCellValue(decimalFormat.format(abc));
							}
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
							dummy +=  1;
						}
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_TH_doanh_so_ban_hang_theo_CVLL" + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportSynthesisReportByStaffToExcel2(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String orderStatus = request.getParameter("orderStatus");
		String channel = request.getParameter("channel");
		Locale locale = UtilHttp.getLocale(request); 
		List<String> orderStatusInput = null;
		List<String> orderStatusOut = null;
		String channelInput = null;
		String channelOut = null;
		
		if(channel.equals("") || channel.equals("null")){
			channelInput = null;
		}
		
		if(!channel.equals("") || !channel.equals("null")){
			channelInput = channel;
		}
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusInput = null;
		}
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData = orderStatus.split(",");
			orderStatusInput = new ArrayList<>();
			if(statusData.length != 0){
				for (String i : statusData) {
					orderStatusInput.add(i);
				}
			}
		}
		
		HttpSession session = request.getSession();
		
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSSynthesisReport", locale);
		String fromDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSFromDate", locale);
		String thruDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSThruDate", locale);
		String orderStatus_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderStatus", locale);
		String channel_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSChannel", locale);
		String all_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", locale);
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusOut = null;
		}
		
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData_ = orderStatus.split(",");
			orderStatusOut = new ArrayList<>();
			if(statusData_.length != 0){
				for (String i : statusData_) {
					GenericValue orderStatusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", i), false);
					String orderStatusDes = (String) orderStatusId.get("description", locale);
					orderStatusOut.add(orderStatusDes);
				}
			}
		}
		
		if(channel.equals("") || channel.equals("null")){
			channelOut = null;
		}
		
		if(!channel.equals("") && !channel.equals("null")){
			String[] statusData_ = orderStatus.split(",");
//			orderStatusOut = new ArrayList<>();
//			if(statusData_.length != 0){
//				for (String i : statusData_) {
					GenericValue channelId = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", channel), false);
					String channelDes = (String) channelId.get("description", locale);
					channelOut = channelDes;
//				}
//			}
		}
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("orderStatus[]", orderStatusInput);
		context.put("channel", channelInput);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateSalesSynthesisReportByStaff", context);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 50*256);
			sheet.setColumnWidth(2, 30*256);
			for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 25*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(is!=null)is.close();
			}
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row channelRow = sheet.createRow(rownum);
			channelRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell channelCell = channelRow.createCell(0);
			if(channel.equals("") || channel.equals("null")){
				channelCell.setCellValue(channel_ + ": " + all_);
			} else {
				channelCell.setCellValue(channel_ + ": " + channelOut);
			}
			
			channelCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell dateCell = dateRow.createCell(0);
			dateCell.setCellValue(fromDate_ + ": " + fromDateTs + "   -   " + thruDate_ + ": " + thruDateTs);
			dateCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row statusRow = sheet.createRow(rownum);
			statusRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell statusCell_ = statusRow.createCell(0);
			if(orderStatusOut != null){
				statusCell_.setCellValue(orderStatus_ + ": " + orderStatusOut);
			} else {
				statusCell_.setCellValue(orderStatus_ + ": " + all_);
			}
			statusCell_.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellValue("II. DOANH THU THEO NHÂN VIÊN");
			khoangCachCell2.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("Mã nhân viên");
			titles.add("Tên đầy đủ");
			titles.add("Tổng giá trị");
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			Map<String, Object> listResult = dispatcher.runSync("getListResultStore", UtilMisc.toMap());
			List<String> listResultStore = (List<String>) listResult.get("listResultStore");
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			
			for (String s : listResultStore) {
//				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", s), false);
//				String internalName = product.getString("internalName");
				
				Cell orderIdCell = headerBreakdownAmountRow.createCell(index);
				orderIdCell.setCellValue(s + "(SL)");
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				Cell orderIdCell1 = headerBreakdownAmountRow.createCell(index + 1);
				orderIdCell1.setCellValue(s + "(TT)");
				orderIdCell1.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				index += 2;
			}
			rownum += 1;
			
			sheet.createFreezePane(3, 12);
			index = titles.size();
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("staffId")){
						Cell orderIdCell = orderDetailRow.createCell(0);
						String storetId = (String) entry.getValue();
						orderIdCell.setCellValue(storetId);
						orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("staffName")){
						Cell orderIdCell = orderDetailRow.createCell(1);
						String storeName = (String) entry.getValue();
						orderIdCell.setCellValue(storeName);
						orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("valueTotal")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						String productTotalStr = productTotal.toString();
						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow.createCell(2);
						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					int dummy = 0;
					int dummy2 = 1;
					for (String s : listResultStore) {
						if(entry.getKey().equals(s+"q")){
							Cell orderIdCell = orderDetailRow.createCell(index + dummy + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							if (value.split("\\.").length > 1) {
								value = value.split("\\.")[0];
							}
							orderIdCell.setCellValue(value);
							orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
						}
						dummy +=  1;
					}
					for (String s : listResultStore) {
						if(entry.getKey().equals(s+"t")){
							Cell orderIdCell = orderDetailRow.createCell(index + dummy2 + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							if (value.split("\\.").length > 1) {
								value = value.split("\\.")[0];
							}
							orderIdCell.setCellValue(value);
							orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
						}
						dummy2 +=  1;
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_TH_doanh_so_ban_hang_theo_CVLL" + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportTurnoverProProStoReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productStore = request.getParameter("productStore");
		String category = request.getParameter("category");
		String orderStatus = request.getParameter("orderStatus");
		List<String> productStoreInput = null;
		List<String> categoryInput = null;
		String sortIdInput = null;
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSTurnoverReport", locale);
		String subTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSRevenuePPSReport", locale);
		String pSN = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductStore", locale);
		String cate = UtilProperties.getMessage("BaseSalesUiLabels", "BSCategoryId", locale);
		String pI = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductId", locale);
		String pN = UtilProperties.getMessage("BaseSalesUiLabels", "BSProduct", locale);
		String q = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", locale);
		String t = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", locale);
		
		if(productStore.equals("") || productStore.equals("null")){
			productStoreInput = null;
		}
		if(!productStore.equals("") && !productStore.equals("null")){
			String[] productStoreData = productStore.split(",");
			productStoreInput = new ArrayList<>();
			if(productStoreData.length != 0){
				for (String i : productStoreData) {
					productStoreInput.add(i);
				}
			}
		}
		
		if(category.equals("") || category.equals("null")){
			categoryInput = null;
		}
		if(!category.equals("") && !category.equals("null")){
			String[] categoryData = category.split(",");
			categoryInput = new ArrayList<>();
			if(categoryData.length != 0){
				for (String i : categoryData) {
					categoryInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDateOut = new Timestamp(fromDateLog);
		Timestamp thruDateOut = new Timestamp(thruDateLog);
		Date fromDateTs = UtilDateTime.getDayStart(fromDateOut);
		Date thruDateTs = UtilDateTime.getDayStart(thruDateOut);
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("sortId", sortIdInput);
		context.put("orderStatus", orderStatus);
		context.put("productStore[]", productStoreInput);
		context.put("category[]", categoryInput);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateSalesGrid", context);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 25*256);
			sheet.setColumnWidth(2, 20*256);
			sheet.setColumnWidth(3, 50*256);
			sheet.setColumnWidth(4, 18*256);
			sheet.setColumnWidth(5, 18*256);
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
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
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow3 = sheet.createRow(rownum);
			khoangCachRow3.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell3 = khoangCachRow3.createCell(0);
			khoangCachCell3.setCellValue(subTitle.toUpperCase());
			khoangCachCell3.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add(pSN);
			titles.add(cate);
			titles.add(pI);
			titles.add(pN);
			titles.add(q);
			titles.add(t);
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
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("productStoreName")){
						String storeNameOut = (String) entry.getValue();
						Cell storeNameCell = orderDetailRow.createCell(0);
						storeNameCell.setCellValue(storeNameOut);
						storeNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("category")){
						String categoryOut = (String) entry.getValue();
						Cell categoryCell = orderDetailRow.createCell(1);
						categoryCell.setCellValue(categoryOut);
						categoryCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productId")){
						String productIdOut = (String) entry.getValue();
						Cell productIdCell = orderDetailRow.createCell(2);
						productIdCell.setCellValue(productIdOut);
						productIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productName")){
						String productNameOut = (String) entry.getValue();
						Cell productNameCell = orderDetailRow.createCell(3);
						productNameCell.setCellValue(productNameOut);
						productNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("quantity1")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						String productQuantityStr = productQuantity.toString();
						String productQuantityStrOut = productQuantityStr.split("\\.")[0];
						Cell productQuantityCell = orderDetailRow.createCell(4);
						productQuantityCell.setCellValue(productQuantityStrOut);
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("total1")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						String productTotalStr = productTotal.toString();
						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow.createCell(5);
						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_doanh_so_" + fromDateTs + "_" + thruDateTs + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportTurnoverProChaReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String channel = request.getParameter("channel");
		String category = request.getParameter("category");
		String orderStatus = request.getParameter("orderStatus");
		List<String> channelInput = null;
		List<String> categoryInput = null;
		String sortIdInput = null;
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSTurnoverReport", locale);
		String subTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSRevenueProductChannel", locale);
		String cN = UtilProperties.getMessage("BaseSalesUiLabels", "BSChannel", locale);
		String cate = UtilProperties.getMessage("BaseSalesUiLabels", "BSCategoryId", locale);
		String pI = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductId", locale);
		String pN = UtilProperties.getMessage("BaseSalesUiLabels", "BSProduct", locale);
		String q = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", locale);
		String t = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", locale);
		
		if(channel.equals("") || channel.equals("null")){
			channelInput = null;
		}
		if(!channel.equals("") && !channel.equals("null")){
			String[] productStoreData = channel.split(",");
			channelInput = new ArrayList<>();
			if(productStoreData.length != 0){
				for (String i : productStoreData) {
					channelInput.add(i);
				}
			}
		}
		
		if(category.equals("") || category.equals("null")){
			categoryInput = null;
		}
		if(!category.equals("") && !category.equals("null")){
			String[] categoryData = category.split(",");
			categoryInput = new ArrayList<>();
			if(categoryData.length != 0){
				for (String i : categoryData) {
					categoryInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDateOut = new Timestamp(fromDateLog);
		Timestamp thruDateOut = new Timestamp(thruDateLog);
		Date fromDateTs = UtilDateTime.getDayStart(fromDateOut);
		Date thruDateTs = UtilDateTime.getDayStart(thruDateOut);
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("sortId", sortIdInput);
		context.put("orderStatus", orderStatus);
		context.put("storeChannel[]", channelInput);
		context.put("category[]", categoryInput);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateSalesGridByChannel", context);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 25*256);
			sheet.setColumnWidth(2, 20*256);
			sheet.setColumnWidth(3, 40*256);
			sheet.setColumnWidth(4, 18*256);
			sheet.setColumnWidth(5, 18*256);
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
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
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow3 = sheet.createRow(rownum);
			khoangCachRow3.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell3 = khoangCachRow3.createCell(0);
			khoangCachCell3.setCellValue(subTitle.toUpperCase());
			khoangCachCell3.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add(cN);
			titles.add(cate);
			titles.add(pI);
			titles.add(pN);
			titles.add(q);
			titles.add(t);
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
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("channel")){
						String storeNameOut = (String) entry.getValue();
						Cell storeNameCell = orderDetailRow.createCell(0);
						storeNameCell.setCellValue(storeNameOut);
						storeNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("category")){
						String categoryOut = (String) entry.getValue();
						Cell categoryCell = orderDetailRow.createCell(1);
						categoryCell.setCellValue(categoryOut);
						categoryCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productId")){
						String productIdOut = (String) entry.getValue();
						Cell productIdCell = orderDetailRow.createCell(2);
						productIdCell.setCellValue(productIdOut);
						productIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productName")){
						String productNameOut = (String) entry.getValue();
						Cell productNameCell = orderDetailRow.createCell(3);
						productNameCell.setCellValue(productNameOut);
						productNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("Quantity")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						String productQuantityStr = productQuantity.toString();
						String productQuantityStrOut = productQuantityStr.split("\\.")[0];
						Cell productQuantityCell = orderDetailRow.createCell(4);
						productQuantityCell.setCellValue(productQuantityStrOut);
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("Total")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						String productTotalStr = productTotal.toString();
						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow.createCell(5);
						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_doanh_so_" + fromDateTs + "_" + thruDateTs + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportOrderReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String fromDateStr2 = request.getParameter("fromDate2");
		String thruDateStr2= request.getParameter("thruDate2");
		String orderStatus = request.getParameter("orderStatus");
		List<String> orderStatusInput = null;
		String channelIn = request.getParameter("channel");
		List<String> channelInput = null;
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusInput = null;
		}
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData = orderStatus.split(",");
			orderStatusInput = new ArrayList<>();
			if(statusData.length != 0){
				for (String i : statusData) {
					orderStatusInput.add(i);
				}
			}
		}
		
		if(channelIn.equals("") || channelIn.equals("null")){
			channelInput = null;
		}
		if(!channelIn.equals("") && !channelIn.equals("null")){
			String[] channelData = channelIn.split(",");
			channelInput = new ArrayList<>();
			if(channelData.length != 0){
				for (String i : channelData) {
					channelInput.add(i);
				}
			}
		}
		
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderReport", locale);
		String channel = UtilProperties.getMessage("BaseSalesUiLabels", "BSChannel", locale);
		String salesExecutive = UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesExecutive", locale);
		String callCenter = UtilProperties.getMessage("BaseSalesUiLabels", "BSCallcenter", locale);
		String order = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderId", locale);
		String customer = UtilProperties.getMessage("BaseSalesUiLabels", "BSCustomerId", locale);
		String customerName = UtilProperties.getMessage("BaseSalesUiLabels", "BSFullName", locale);
		String address = UtilProperties.getMessage("BaseSalesUiLabels", "BSAddress", locale);
		String total = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", locale);
		String status = UtilProperties.getMessage("BaseSalesUiLabels", "BSStatus", locale);
		String orderDate = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderDate", locale);
		String deliveryDate = UtilProperties.getMessage("BaseSalesUiLabels", "BSDeliveryDate", locale);
		String completeDate = UtilProperties.getMessage("BaseSalesUiLabels", "BSCompletedOrderDate", locale);
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDateOut = new Timestamp(fromDateLog);
		Timestamp thruDateOut = new Timestamp(thruDateLog);
		Date fromDateTs = UtilDateTime.getDayStart(fromDateOut);
		Date thruDateTs = UtilDateTime.getDayStart(thruDateOut);
		
		long fromDateLog2 = Long.parseLong(fromDateStr2);
		long thruDateLog2 = Long.parseLong(thruDateStr2);
		Timestamp fromDateOut2 = new Timestamp(fromDateLog2);
		Timestamp thruDateOut2 = new Timestamp(thruDateLog2);
		Date fromDateTs2= UtilDateTime.getDayStart(fromDateOut2);
		Date thruDateTs2 = UtilDateTime.getDayStart(thruDateOut2);
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("fromDate2", fromDateTs2);
		context.put("thruDate2", thruDateTs2);
		context.put("orderStatus[]", orderStatusInput);
		context.put("channel[]", channelInput);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateOrderGrid", context);
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
			
			for(int i= 0; i <= 8; i++){
				sheet.setColumnWidth(i, 30*256);
			}
			sheet.setColumnWidth(9, 60*256);
			sheet.setColumnWidth(10, 15*256);
			sheet.setColumnWidth(11, 60*256);
			
			for(int j= 12; j <= 16; j++){
				sheet.setColumnWidth(j, 20*256);
			}
			
			sheet.setColumnWidth(17, 30*256);
			sheet.setColumnWidth(18, 25*256);
			sheet.setColumnWidth(19, 50*256);
			sheet.setColumnWidth(20, 25*256);
			sheet.setColumnWidth(21, 25*256);
			sheet.setColumnWidth(22, 25*256);
			sheet.setColumnWidth(23, 25*256);
			
			for(int k = 24; k <= 100; k++) {
				sheet.setColumnWidth(k, 23*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
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
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("Mã đơn hàng");
			titles.add("Ngày gửi đơn");
			titles.add("Kênh bán hàng	");
			titles.add("Mã NV liên lạc");
			titles.add("Tên NV liên lạc");
			titles.add("Mã NV bán hàng");
			titles.add("Tên NV bán hàng");
			titles.add("Mã khách hàng");
			titles.add("Tên khách hàng");
			titles.add("Địa chỉ");
			titles.add("Số điện thoại");
			titles.add("Địa chỉ giao hàng");
			titles.add("Số nhà, đường");
			titles.add("Phường");
			titles.add("Quận/huyện");
			titles.add("Thành phố");
			titles.add("Quốc gia");
			titles.add("TT đơn hàng");
			titles.add("Thời gian nhận hàng");
			titles.add("Khoảng thời gian nhận hàng");
			titles.add("Thời gian giao hàng");
			titles.add("TT phiếu xuất");
			titles.add("Tổng giá trị tiền hàng");
			titles.add("Thuế");
			
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 550);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));

//			Map<String, Object> listResult = dispatcher.runSync("getListResultStore", UtilMisc.toMap());
//			List<String> listResultStore = (List<String>) listResult.get("listResultStore");
			
			Map<String, Object> listResult = dispatcher.runSync("getListResultStore", UtilMisc.toMap());
			List<Map<String, String>> listResultStore = (List<Map<String, String>>) listResult.get("listResultStore");

			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			
//			for (String s : listResultStore) {
//				Cell orderIdCell = headerBreakdownAmountRow.createCell(index + listResultStore.indexOf(s));
//				orderIdCell.setCellValue(s);
//				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
//				
//			}
			int indexMerge = index;		
			for (int i = 0 ; i < listResultStore.size(); i++) {
				Map<String, String> store = listResultStore.get(i);
				Cell orderIdCell = headerBreakdownAmountRow.createCell(indexMerge + i);
				orderIdCell.setCellValue(store.get("internal_name"));
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			}
			
			rownum += 1;
			
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 500);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("orderId")){
						String storeNameOut = (String) entry.getValue();
						Cell storeNameCell = orderDetailRow.createCell(0);
						storeNameCell.setCellValue(storeNameOut);
						storeNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderDate")){
						String salesExecutiveOut = (String) entry.getValue();
						Cell salesExecutiveCell = orderDetailRow.createCell(1);
						salesExecutiveCell.setCellValue(salesExecutiveOut);
						salesExecutiveCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("channel")){
						String callCenterOut = (String) entry.getValue();
						Cell callCenterCell = orderDetailRow.createCell(2);
						callCenterCell.setCellValue(callCenterOut);
						callCenterCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("caceId")){
						String orderOut = (String) entry.getValue();
						Cell orderCell = orderDetailRow.createCell(3);
						orderCell.setCellValue(orderOut);
						orderCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("caceName")){
						String customerIdOut2 = (String) entry.getValue();
						Cell customerIdCell2 = orderDetailRow.createCell(4);
						customerIdCell2.setCellValue(customerIdOut2);
						customerIdCell2.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("saexId")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(5);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("saexName")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(6);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("cusId")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(7);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("cusName")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(8);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("cusAddress")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(9);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("cusPhone")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(10);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderAddress")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(11);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderRoad")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(12);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderWard")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(13);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderDistrict")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(14);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderState")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(15);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderCountry")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(16);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderStatusId")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(17);
						GenericValue statusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", customerNameOut3), false);
						String orderStatusResult = (String) statusId.get("description", locale);
						customerNameCell3.setCellValue(orderStatusResult);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("expectedDeliveryDate")){
						String deliveryDateOut6 = (String) entry.getValue();
						Cell deliveryDateCell6 = orderDetailRow.createCell(18);
						if(UtilValidate.isNotEmpty(deliveryDateOut6)){
							deliveryDateCell6.setCellValue(deliveryDateOut6);
						} else {
							deliveryDateCell6.setCellValue("");
						}
						deliveryDateCell6.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("expectedDeliveryRangeDate")){
						String deliveryDateOut6 = (String) entry.getValue();
						Cell deliveryDateCell6 = orderDetailRow.createCell(19);
						if(UtilValidate.isNotEmpty(deliveryDateOut6)){
							deliveryDateCell6.setCellValue(deliveryDateOut6);
						} else {
							deliveryDateCell6.setCellValue("");
						}
						deliveryDateCell6.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("actualDeliveryDate")){
						String deliveryDateOut6 = (String) entry.getValue();
						Cell deliveryDateCell6 = orderDetailRow.createCell(20);
						if(UtilValidate.isNotEmpty(deliveryDateOut6)){
							deliveryDateCell6.setCellValue(deliveryDateOut6);
						} else {
							deliveryDateCell6.setCellValue("");
						}
						deliveryDateCell6.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("deliveryStatusId")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(21);
						if(UtilValidate.isNotEmpty(customerNameOut3)){
							GenericValue statusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", customerNameOut3), false);
							String orderStatusResult = (String) statusId.get("description", locale);
							customerNameCell3.setCellValue(orderStatusResult);
						}else if(UtilValidate.isEmpty(customerNameOut3) || "".equals(customerNameOut3) || customerNameOut3.equals(null)){
							customerNameCell3.setCellValue("");
						}
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderValue")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						Cell productQuantityCell = orderDetailRow.createCell(22);
						productQuantityCell.setCellValue(productQuantity.floatValue());
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("orderTax")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						Cell productQuantityCell = orderDetailRow.createCell(23);
						productQuantityCell.setCellValue(productQuantity.floatValue());
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					for (int i= 0; i < listResultStore.size(); i++) {
						Map<String, String> store = listResultStore.get(i);
						if(entry.getKey().equals(store.get("product_id"))){
							Cell orderIdCell = orderDetailRow.createCell(index + i);
							if(entry.getValue() instanceof BigDecimal){
								BigDecimal bDValue = (BigDecimal) entry.getValue();
								orderIdCell.setCellValue(bDValue.floatValue());
							} else {
								orderIdCell.setCellValue("-");
							}
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
						}
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_don_hang_" + fromDateTs + "_" + thruDateTs + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportReturnOrderReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String fromDateStr2 = request.getParameter("fromDate2");
		String thruDateStr2= request.getParameter("thruDate2");
		String orderStatus = request.getParameter("orderStatus");
		List<String> orderStatusInput = null;
		String channelIn = request.getParameter("channel");
		List<String> channelInput = null;
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusInput = null;
		}
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData = orderStatus.split(",");
			orderStatusInput = new ArrayList<>();
			if(statusData.length != 0){
				for (String i : statusData) {
					orderStatusInput.add(i);
				}
			}
		}
		
		if(channelIn.equals("") || channelIn.equals("null")){
			channelInput = null;
		}
		if(!channelIn.equals("") && !channelIn.equals("null")){
			String[] channelData = channelIn.split(",");
			channelInput = new ArrayList<>();
			if(channelData.length != 0){
				for (String i : channelData) {
					channelInput.add(i);
				}
			}
		}
		
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderReport", locale);
		String channel = UtilProperties.getMessage("BaseSalesUiLabels", "BSChannel", locale);
		String salesExecutive = UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesExecutive", locale);
		String callCenter = UtilProperties.getMessage("BaseSalesUiLabels", "BSCallcenter", locale);
		String order = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderId", locale);
		String customer = UtilProperties.getMessage("BaseSalesUiLabels", "BSCustomerId", locale);
		String customerName = UtilProperties.getMessage("BaseSalesUiLabels", "BSFullName", locale);
		String address = UtilProperties.getMessage("BaseSalesUiLabels", "BSAddress", locale);
		String total = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", locale);
		String status = UtilProperties.getMessage("BaseSalesUiLabels", "BSStatus", locale);
		String orderDate = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderDate", locale);
		String deliveryDate = UtilProperties.getMessage("BaseSalesUiLabels", "BSDeliveryDate", locale);
		String completeDate = UtilProperties.getMessage("BaseSalesUiLabels", "BSCompletedOrderDate", locale);
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDateOut = new Timestamp(fromDateLog);
		Timestamp thruDateOut = new Timestamp(thruDateLog);
		Date fromDateTs = UtilDateTime.getDayStart(fromDateOut);
		Date thruDateTs = UtilDateTime.getDayStart(thruDateOut);
		
		long fromDateLog2 = Long.parseLong(fromDateStr2);
		long thruDateLog2 = Long.parseLong(thruDateStr2);
		Timestamp fromDateOut2 = new Timestamp(fromDateLog2);
		Timestamp thruDateOut2 = new Timestamp(thruDateLog2);
		Date fromDateTs2= UtilDateTime.getDayStart(fromDateOut2);
		Date thruDateTs2 = UtilDateTime.getDayStart(thruDateOut2);
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("fromDate2", fromDateTs2);
		context.put("thruDate2", thruDateTs2);
		context.put("orderStatus[]", orderStatusInput);
		context.put("channel[]", channelInput);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateOrderGrid", context);
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
			
			for(int i= 0; i <= 8; i++){
				sheet.setColumnWidth(i, 30*256);
			}
			sheet.setColumnWidth(9, 60*256);
			sheet.setColumnWidth(10, 15*256);
			sheet.setColumnWidth(11, 60*256);
			
			for(int j= 12; j <= 16; j++){
				sheet.setColumnWidth(j, 20*256);
			}
			
			sheet.setColumnWidth(17, 30*256);
			sheet.setColumnWidth(18, 25*256);
			sheet.setColumnWidth(19, 50*256);
			sheet.setColumnWidth(20, 25*256);
			sheet.setColumnWidth(21, 25*256);
			sheet.setColumnWidth(22, 25*256);
			sheet.setColumnWidth(23, 25*256);
			
			for(int k = 24; k <= 100; k++) {
				sheet.setColumnWidth(k, 23*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
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
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("Mã đơn hàng");
			titles.add("Ngày gửi đơn");
			titles.add("Kênh bán hàng	");
			titles.add("Mã NV liên lạc");
			titles.add("Tên NV liên lạc");
			titles.add("Mã NV bán hàng");
			titles.add("Tên NV bán hàng");
			titles.add("Mã khách hàng");
			titles.add("Tên khách hàng");
			titles.add("Địa chỉ");
			titles.add("Số điện thoại");
			titles.add("Địa chỉ giao hàng");
			titles.add("Số nhà, đường");
			titles.add("Phường");
			titles.add("Quận/huyện");
			titles.add("Thành phố");
			titles.add("Quốc gia");
			titles.add("TT đơn hàng");
			titles.add("Thời gian nhận hàng");
			titles.add("Khoảng thời gian nhận hàng");
			titles.add("Thời gian giao hàng");
			titles.add("TT phiếu xuất");
			titles.add("Tổng giá trị tiền hàng");
			titles.add("Thuế");
			
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 550);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));

//			Map<String, Object> listResult = dispatcher.runSync("getListResultStore", UtilMisc.toMap());
//			List<String> listResultStore = (List<String>) listResult.get("listResultStore");
			
			Map<String, Object> listResult = dispatcher.runSync("getListResultStore", UtilMisc.toMap());
			List<Map<String, String>> listResultStore = (List<Map<String, String>>) listResult.get("listResultStore");

			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			
//			for (String s : listResultStore) {
//				Cell orderIdCell = headerBreakdownAmountRow.createCell(index + listResultStore.indexOf(s));
//				orderIdCell.setCellValue(s);
//				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
//				
//			}
			int indexMerge = index;		
			for (int i = 0 ; i < listResultStore.size(); i++) {
				Map<String, String> store = listResultStore.get(i);
				Cell orderIdCell = headerBreakdownAmountRow.createCell(indexMerge + i);
				orderIdCell.setCellValue(store.get("internal_name"));
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
			}
			
			rownum += 1;
			
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 500);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("orderId")){
						String storeNameOut = (String) entry.getValue();
						Cell storeNameCell = orderDetailRow.createCell(0);
						storeNameCell.setCellValue(storeNameOut);
						storeNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderDate")){
						String salesExecutiveOut = (String) entry.getValue();
						Cell salesExecutiveCell = orderDetailRow.createCell(1);
						salesExecutiveCell.setCellValue(salesExecutiveOut);
						salesExecutiveCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("channel")){
						String callCenterOut = (String) entry.getValue();
						Cell callCenterCell = orderDetailRow.createCell(2);
						callCenterCell.setCellValue(callCenterOut);
						callCenterCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("caceId")){
						String orderOut = (String) entry.getValue();
						Cell orderCell = orderDetailRow.createCell(3);
						orderCell.setCellValue(orderOut);
						orderCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("caceName")){
						String customerIdOut2 = (String) entry.getValue();
						Cell customerIdCell2 = orderDetailRow.createCell(4);
						customerIdCell2.setCellValue(customerIdOut2);
						customerIdCell2.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("saexId")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(5);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("saexName")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(6);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("cusId")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(7);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("cusName")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(8);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("cusAddress")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(9);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("cusPhone")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(10);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderAddress")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(11);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderRoad")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(12);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderWard")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(13);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderDistrict")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(14);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderState")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(15);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderCountry")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(16);
						customerNameCell3.setCellValue(customerNameOut3);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderStatusId")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(17);
						GenericValue statusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", customerNameOut3), false);
						String orderStatusResult = (String) statusId.get("description", locale);
						customerNameCell3.setCellValue(orderStatusResult);
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("expectedDeliveryDate")){
						String deliveryDateOut6 = (String) entry.getValue();
						Cell deliveryDateCell6 = orderDetailRow.createCell(18);
						if(UtilValidate.isNotEmpty(deliveryDateOut6)){
							deliveryDateCell6.setCellValue(deliveryDateOut6);
						} else {
							deliveryDateCell6.setCellValue("");
						}
						deliveryDateCell6.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("expectedDeliveryRangeDate")){
						String deliveryDateOut6 = (String) entry.getValue();
						Cell deliveryDateCell6 = orderDetailRow.createCell(19);
						if(UtilValidate.isNotEmpty(deliveryDateOut6)){
							deliveryDateCell6.setCellValue(deliveryDateOut6);
						} else {
							deliveryDateCell6.setCellValue("");
						}
						deliveryDateCell6.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("actualDeliveryDate")){
						String deliveryDateOut6 = (String) entry.getValue();
						Cell deliveryDateCell6 = orderDetailRow.createCell(20);
						if(UtilValidate.isNotEmpty(deliveryDateOut6)){
							deliveryDateCell6.setCellValue(deliveryDateOut6);
						} else {
							deliveryDateCell6.setCellValue("");
						}
						deliveryDateCell6.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("deliveryStatusId")){
						String customerNameOut3= (String) entry.getValue();
						Cell customerNameCell3 = orderDetailRow.createCell(21);
						if(UtilValidate.isNotEmpty(customerNameOut3)){
							GenericValue statusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", customerNameOut3), false);
							String orderStatusResult = (String) statusId.get("description", locale);
							customerNameCell3.setCellValue(orderStatusResult);
						}else if(UtilValidate.isEmpty(customerNameOut3) || "".equals(customerNameOut3) || customerNameOut3.equals(null)){
							customerNameCell3.setCellValue("");
						}
						customerNameCell3.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("orderValue")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						Cell productQuantityCell = orderDetailRow.createCell(22);
						productQuantityCell.setCellValue(productQuantity.floatValue());
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("orderTax")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						Cell productQuantityCell = orderDetailRow.createCell(23);
						productQuantityCell.setCellValue(productQuantity.floatValue());
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					for (int i= 0; i < listResultStore.size(); i++) {
						Map<String, String> store = listResultStore.get(i);
						if(entry.getKey().equals(store.get("product_id"))){
							Cell orderIdCell = orderDetailRow.createCell(index + i);
							if(entry.getValue() instanceof BigDecimal){
								BigDecimal bDValue = (BigDecimal) entry.getValue();
								orderIdCell.setCellValue(bDValue.floatValue());
							} else {
								orderIdCell.setCellValue("-");
							}
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
						}
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_don_hang_" + fromDateTs + "_" + thruDateTs + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportCommunicaitonEmployeeToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Locale locale = UtilHttp.getLocale(request); 
		
		HttpSession session = request.getSession();
		
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSSynthesisReport", locale);
		String fromDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSFromDate", locale);
		String thruDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSThruDate", locale);
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("getCommunicationEmployeeReport", context);
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
			
			sheet.setColumnWidth(0, 30*256);
			sheet.setColumnWidth(1, 30*256);
			for (int i = 2; i < 50; i++) {
				sheet.setColumnWidth(i, 20*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(is!=null)is.close();
			}
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue("BÁO CÁO TỔNG HỢP CUỘC GỌI".toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell dateCell = dateRow.createCell(0);
			dateCell.setCellValue(fromDate_ + ": " + fromDateTs + "   -   " + thruDate_ + ": " + thruDateTs);
			dateCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("Kết quả");
			titles.add("Lý do");
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			Map<String, Object> listResult = dispatcher.runSync("getListEmployee", UtilMisc.toMap());
			List<String> listResultStore = (List<String>) listResult.get("listEmployeee");
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			
			for (String s : listResultStore) {
				GenericValue callcenterId = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", s), false);
				String fullName = callcenterId.getString("fullName");
				
				Cell orderIdCell = headerBreakdownAmountRow.createCell(index + listResultStore.indexOf(s));
				orderIdCell.setCellValue(fullName + "("+ s +")");
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
			}
			rownum += 1;
			
//			sheet.createFreezePane(8, 7);
			sheet.createFreezePane(2, 9);
			
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("result_enum_type_id")){
						Cell resultCell = orderDetailRow.createCell(0);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("result_enum_id")){
						Cell reasonCell = orderDetailRow.createCell(1);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					for (String s : listResultStore) {
						if(entry.getKey().equals(s)){
							Cell orderIdCell = orderDetailRow.createCell(index + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							if (value.split("\\.").length > 1) {
								value = value.split("\\.")[0];
							}
							orderIdCell.setCellValue( value);
							orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
						}
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_cuoc_goi" + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportCallsReportToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Locale locale = UtilHttp.getLocale(request); 
		
		HttpSession session = request.getSession();
		
//		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "SynthesisReport", locale);
		String fromDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSFromDate", locale);
		String thruDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSThruDate", locale);
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateCallsGrid", context);
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
			
			for (int i = 0; i < 50; i++) {
				sheet.setColumnWidth(i, 23*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(is!=null)is.close();
			}
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue("BÁO CÁO LỊCH SỬ CUỘC GỌI".toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell dateCell = dateRow.createCell(0);
			dateCell.setCellValue(fromDate_ + ": " + fromDateTs + "   -   " + thruDate_ + ": " + thruDateTs);
			dateCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("Mã NV liên lạc");
			titles.add("Tên NV liên lạc");
			titles.add("Mã khách hàng");
			titles.add("Tên khách hàng");
			titles.add("Địa chỉ");
			titles.add("Số nhà, đường");
			titles.add("Phường");
			titles.add("Quận/huyện");
			titles.add("Thành phố");
			titles.add("Quốc gia");
			titles.add("Số điện thoại");
			titles.add("Ngày sinh");
			titles.add("Ngày gọi");
			titles.add("Kết quả");
			titles.add("Lý do");
			titles.add("Sản phẩm thảo luận");
			
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			Map<String, Object> listResult = dispatcher.runSync("getListEmployee", UtilMisc.toMap());
			List<String> listResultStore = (List<String>) listResult.get("listEmployeee");
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			
//			for (String s : listResultStore) {
//				GenericValue callcenterId = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", s), false);
//				String fullName = callcenterId.getString("fullName");
//				
//				Cell orderIdCell = headerBreakdownAmountRow.createCell(index + listResultStore.indexOf(s));
//				orderIdCell.setCellValue(fullName + "("+ s +")");
//				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
//				
//			}
			rownum += 1;
			
//			sheet.createFreezePane(8, 7);
//			sheet.createFreezePane(2, 9);
			
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("caceId")){
						Cell resultCell = orderDetailRow.createCell(0);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("caceName")){
						Cell reasonCell = orderDetailRow.createCell(1);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusId")){
						Cell resultCell = orderDetailRow.createCell(2);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusName")){
						Cell reasonCell = orderDetailRow.createCell(3);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusAddress")){
						Cell resultCell = orderDetailRow.createCell(4);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusRoad")){
						Cell reasonCell = orderDetailRow.createCell(5);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusWard")){
						Cell resultCell = orderDetailRow.createCell(6);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusDistrict")){
						Cell reasonCell = orderDetailRow.createCell(7);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusState")){
						Cell resultCell = orderDetailRow.createCell(8);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusCountry")){
						Cell reasonCell = orderDetailRow.createCell(9);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}       
					if(entry.getKey().equals("cusPhone")){
						Cell resultCell = orderDetailRow.createCell(10);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("cusBirthday")){
						Cell reasonCell = orderDetailRow.createCell(11);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("callsDay")){
						Cell resultCell = orderDetailRow.createCell(12);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("callsResult")){
						Cell reasonCell = orderDetailRow.createCell(13);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("callsReason")){
						Cell reasonCell = orderDetailRow.createCell(14);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("productDiscussed")){
						Cell reasonCell = orderDetailRow.createCell(15);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
//					for (String s : listResultStore) {
//						if(entry.getKey().equals(s)){
//							Cell orderIdCell = orderDetailRow.createCell(index + listResultStore.indexOf(s));
//							String value = ((BigDecimal) entry.getValue()).toString();
//							if (value.split("\\.").length > 1) {
//								value = value.split("\\.")[0];
//							}
//							orderIdCell.setCellValue( value);
//							orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
//						}
//					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_cuoc_goi" + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportCustomerSatisfaction(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Locale locale = UtilHttp.getLocale(request); 
		HttpSession session = request.getSession();
		String fromDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSFromDate", locale);
		String thruDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSThruDate", locale);
		String loyaltyGroup = request.getParameter("loyaltyGroup");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		List<String> loyaltyGroupInput = null;
		
		if(loyaltyGroup.equals("") || loyaltyGroup.equals("null")){
			loyaltyGroupInput = null;
		}
		if(!loyaltyGroup.equals("") && !loyaltyGroup.equals("null")){
			String[] statusData = loyaltyGroup.split(",");
			loyaltyGroupInput = new ArrayList<>();
			if(statusData.length != 0){
				for (String i : statusData) {
					loyaltyGroupInput.add(i);
				}
			}
		}
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("loyaltyGroup[]", loyaltyGroupInput);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateCustomerSatisfaction", context);
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
			
			for (int i = 0; i < 50; i++) {
				sheet.setColumnWidth(i, 23*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(is!=null)is.close();
			}
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue("BÁO CÁO KHÁCH HÀNG THÂN THIẾT".toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell dateCell = dateRow.createCell(0);
			dateCell.setCellValue(fromDate_ + ": " + fromDateTs + "   -   " + thruDate_ + ": " + thruDateTs);
			dateCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add("Mã khách hàng");
			titles.add("Tên khách hàng");
			titles.add("Mã sản phẩm");
			titles.add("Tên sản phẩm");
			titles.add("Số lượng");
			titles.add("Thành tiền");
			titles.add("Tổng giá trị");
			
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			Map<String, Object> listResult = dispatcher.runSync("getListEmployee", UtilMisc.toMap());
			List<String> listResultStore = (List<String>) listResult.get("listEmployeee");
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
			}
			
			rownum += 1;
			
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("customerId")){
						Cell resultCell = orderDetailRow.createCell(0);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("customerName")){
						Cell reasonCell = orderDetailRow.createCell(1);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("classificationGroup")){
						Cell resultCell = orderDetailRow.createCell(2);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("productId")){
						Cell reasonCell = orderDetailRow.createCell(3);
						String reason = (String) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("productName")){
						Cell resultCell = orderDetailRow.createCell(4);
						String result = (String) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("volume")){
						Cell reasonCell = orderDetailRow.createCell(5);
						Integer reason = (Integer) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("value")){
						Cell resultCell = orderDetailRow.createCell(6);
						Integer result = (Integer) entry.getValue();
						resultCell.setCellValue(result);
						resultCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
					if(entry.getKey().equals("value_total")){
						Cell reasonCell = orderDetailRow.createCell(7);
						Integer reason = (Integer) entry.getValue();
						reasonCell.setCellValue(reason);
						reasonCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_kh_than_thiet" + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportTurnoverPPSSMToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productStore = request.getParameter("productStore");
		String category = request.getParameter("category");
		String orderStatus = request.getParameter("orderStatus");
		List<String> productStoreInput = null;
		List<String> categoryInput = null;
		String sortIdInput = null;
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSTurnoverReport", locale);
		String subTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSRevenuePPSReport", locale);
		String pSN = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductStore", locale);
		String cate = UtilProperties.getMessage("BaseSalesUiLabels", "BSCategoryId", locale);
		String pI = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductId", locale);
		String pN = UtilProperties.getMessage("BaseSalesUiLabels", "BSProduct", locale);
		String q = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", locale);
		String t = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", locale);
		String curator = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMCurator", locale);
		
		if(productStore.equals("") || productStore.equals("null")){
			productStoreInput = null;
		}
		if(!productStore.equals("") && !productStore.equals("null")){
			String[] productStoreData = productStore.split(",");
			productStoreInput = new ArrayList<>();
			if(productStoreData.length != 0){
				for (String i : productStoreData) {
					productStoreInput.add(i);
				}
			}
		}
		
		if(category.equals("") || category.equals("null")){
			categoryInput = null;
		}
		if(!category.equals("") && !category.equals("null")){
			String[] categoryData = category.split(",");
			categoryInput = new ArrayList<>();
			if(categoryData.length != 0){
				for (String i : categoryData) {
					categoryInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDateOut = new Timestamp(fromDateLog);
		Timestamp thruDateOut = new Timestamp(thruDateLog);
		Date fromDateTs = UtilDateTime.getDayStart(fromDateOut);
		Date thruDateTs = UtilDateTime.getDayStart(thruDateOut);
		String fromE = new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fromDateLog));
		String thruE = new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(thruDateLog));
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String userId = userLogin.getString("partyId");
		GenericValue user = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", userId), false);
		String userName =(String) user.get("fullName");
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("sortId", sortIdInput);
		context.put("orderStatus", orderStatus);
		context.put("productStore[]", productStoreInput);
		context.put("category[]", categoryInput);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateTurnoverSM", context);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 25*256);
			sheet.setColumnWidth(2, 20*256);
			sheet.setColumnWidth(3, 50*256);
			sheet.setColumnWidth(4, 18*256);
			sheet.setColumnWidth(5, 18*256);
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
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
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow3 = sheet.createRow(rownum);
			khoangCachRow3.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell3 = khoangCachRow3.createCell(0);
			khoangCachCell3.setCellValue(subTitle.toUpperCase());
			khoangCachCell3.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add(pSN);
			titles.add(cate);
			titles.add(pI);
			titles.add(pN);
			titles.add(q);
			titles.add(t);
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
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("productStoreName")){
						String storeNameOut = (String) entry.getValue();
						Cell storeNameCell = orderDetailRow.createCell(0);
						storeNameCell.setCellValue(storeNameOut);
						storeNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("category")){
						String categoryOut = (String) entry.getValue();
						Cell categoryCell = orderDetailRow.createCell(1);
						categoryCell.setCellValue(categoryOut);
						categoryCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productId")){
						String productIdOut = (String) entry.getValue();
						Cell productIdCell = orderDetailRow.createCell(2);
						productIdCell.setCellValue(productIdOut);
						productIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productName")){
						String productNameOut = (String) entry.getValue();
						Cell productNameCell = orderDetailRow.createCell(3);
						productNameCell.setCellValue(productNameOut);
						productNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("quantity1")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						String productQuantityStr = productQuantity.toString();
						String productQuantityStrOut = productQuantityStr.split("\\.")[0];
						Cell productQuantityCell = orderDetailRow.createCell(4);
						productQuantityCell.setCellValue(productQuantityStrOut);
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("total1")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						String productTotalStr = productTotal.toString();
						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow.createCell(5);
						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_doanh_so_" + fromE + "_" + thruE + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportSynthesisReportSMBySaExToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String orderStatus = request.getParameter("orderStatus");
		String channel = request.getParameter("channel");
		List<String> orderStatusInput = null;
		List<String> orderStatusOut = null;
		String channelInput = null;
		
		if(channel.equals("") || channel.equals("null")){
			channelInput = null;
		} else {
			channelInput = channel;
		}
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusInput = null;
		}
		
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData = orderStatus.split(",");
			orderStatusInput = new ArrayList<>();
			if(statusData.length != 0){
				for (String i : statusData) {
					orderStatusInput.add(i);
				}
			}
		}
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String userId = userLogin.getString("partyId");
		GenericValue user = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", userId), false);
		String userName =(String) user.get("fullName");
		Locale locale = UtilHttp.getLocale(request); 
		
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSSynthesisReport", locale);
		String subTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSSynthesisReportBySalesExecutive", locale);
		String fromDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSFromDate", locale);
		String thruDate_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSThruDate", locale);
		String orderStatus_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderStatus", locale);
		String channel_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSChannel", locale);
		String all_ = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", locale);
		String valueTotal = UtilProperties.getMessage("BaseSalesUiLabels", "BSValueTotal", locale);
		String volumeTotal = UtilProperties.getMessage("BaseSalesUiLabels", "BSVolumeTotal", locale);
		subTitle.toUpperCase();
		String staffId = UtilProperties.getMessage("BaseSalesUiLabels", "BSStaffId", locale);
		String staffName = UtilProperties.getMessage("BaseSalesUiLabels", "BSFullName", locale);
		String byVolume = UtilProperties.getMessage("BaseSalesUiLabels", "BSByVolume", locale);
		String byValue = UtilProperties.getMessage("BaseSalesUiLabels", "BSByValue", locale);
		String curator = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMCurator", locale);
		
		if(orderStatus.equals("") || orderStatus.equals("null")){
			orderStatusOut = null;
		}
		if(!orderStatus.equals("") && !orderStatus.equals("null")){
			String[] statusData_ = orderStatus.split(",");
			orderStatusOut = new ArrayList<>();
			if(statusData_.length != 0){
				for (String i : statusData_) {
					GenericValue orderStatusId = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", i), false);
					String orderStatusDes =(String) orderStatusId.get("description", locale);
					orderStatusOut.add(orderStatusDes);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));
		String fromE = new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fromDateLog));
		String thruE = new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(thruDateLog));
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("orderStatus[]", orderStatusInput);
		context.put("channel", channelInput);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("synthesisReportBySalesExecutiveSM", context);
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
			
			sheet.setColumnWidth(0, 20*256);
			sheet.setColumnWidth(1, 35*256);
			sheet.setColumnWidth(2, 25*256);
			for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 18*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;

			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row channelRow = sheet.createRow(rownum);
			channelRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell channelCell = channelRow.createCell(0);
			if(channel.equals("") || channel.equals("null")){
				channelCell.setCellValue(channel_ + ": " + all_);
			} else {
				GenericValue channelId_ = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", channel), false);
				String channelName_ = channelId_.getString("description");
				channelCell.setCellValue(channel_ + ": " + channelName_);
			}
			channelCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row dateRow = sheet.createRow(rownum);
			dateRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell dateCell = dateRow.createCell(0);
			dateCell.setCellValue(fromDate_ + ": " + fromDateTs + "   -   " + thruDate_ + ": " + thruDateTs);
			dateCell.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row statusRow = sheet.createRow(rownum);
			statusRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell statusCell_ = statusRow.createCell(0);
			if(orderStatusOut != null){
				statusCell_.setCellValue(orderStatus_ + ": " + orderStatusOut);
			} else {
				statusCell_.setCellValue(orderStatus_ + ": " + all_);
			}
			statusCell_.setCellStyle(styles.get("cell_italic_normal_left_no_border_10"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellValue(subTitle.toUpperCase());
			khoangCachCell2.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row spaceRow = sheet.createRow(rownum);
			spaceRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell spaceCell = spaceRow.createCell(0);
			spaceCell.setCellValue(" ");
			spaceCell.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row titleByRow = sheet.createRow(rownum);
			titleByRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell titleByCell2 = titleByRow.createCell(0);
			titleByCell2.setCellValue(("1." + byVolume).toUpperCase());
			titleByCell2.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add(staffId);
			titles.add(staffName);
			titles.add(volumeTotal);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 500);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			
			Map<String, Object> listResult = dispatcher.runSync("getListResultStore", UtilMisc.toMap());
			List<String> listResultStore = (List<String>) listResult.get("listResultStore");
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellValue(titles.get(i));
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			}
			
			for (String s : listResultStore) {
				Cell orderIdCell = headerBreakdownAmountRow.createCell(index);
				orderIdCell.setCellValue(s + "(SL)");
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				index += 1;
			}
			rownum += 1;
			
			sheet.createFreezePane(3, 6);
			index = titles.size();
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				orderDetailRow.setHeight((short) 380);
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("staffId")){
						Cell orderIdCell = orderDetailRow.createCell(0);
						String storetId = (String) entry.getValue();
						orderIdCell.setCellValue(storetId);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("staffName")){
						Cell orderIdCell = orderDetailRow.createCell(1);
						String storeName = (String) entry.getValue();
						orderIdCell.setCellValue(storeName);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("volumeTotal")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						DecimalFormatSymbols symbols = new DecimalFormatSymbols();
						symbols.setGroupingSeparator(',');
						symbols.setDecimalSeparator('.');
						String pattern = "#,##0";
						DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
						decimalFormat.setParseBigDecimal(true);
						Cell productTotalCell = orderDetailRow.createCell(2);
						productTotalCell.setCellValue(decimalFormat.format(productTotal));
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					int dummy = 0;
					int dummy2 = 1;
					String nullValue = "-";
					for (String s : listResultStore) {
						if(entry.getKey().equals(s+"q")){
							Cell orderIdCell = orderDetailRow.createCell(index + dummy + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							BigDecimal abc = (BigDecimal) entry.getValue();
							DecimalFormatSymbols symbols = new DecimalFormatSymbols();
							symbols.setGroupingSeparator(',');
							symbols.setDecimalSeparator('.');
							String pattern = "#,##0";
							DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
							decimalFormat.setParseBigDecimal(true);
							if(value.equals("0")){
								orderIdCell.setCellValue(nullValue);
							}else{
								orderIdCell.setCellValue(decimalFormat.format(abc));
							}
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
							dummy +=  1;
						}
					}
				}
				rownum += 1;
			}
			
			Row khoangCachRow2c = sheet.createRow(rownum);
			khoangCachRow2c.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2c = khoangCachRow2c.createCell(0);
			khoangCachCell2c.setCellValue("");
			khoangCachCell2c.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			Row khoangCachRow2b = sheet.createRow(rownum);
			khoangCachRow2b.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,7));
			Cell khoangCachCell2b = khoangCachRow2b.createCell(0);
			khoangCachCell2b.setCellValue(("2." + byValue).toUpperCase());
			khoangCachCell2b.setCellStyle(styles.get("cell_bold_normal_Left_10"));
			rownum += 1;
			
			List<String> titles2 = new FastList<String>();
			titles2.add(staffId);
			titles2.add(staffName);
			titles2.add(valueTotal);
			Row headerBreakdownAmountRow2 = sheet.createRow(rownum);
			headerBreakdownAmountRow2.setHeight((short) 500);
			headerBreakdownAmountRow2.setRowStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			
			index = titles.size();
			
			for (int i = 0; i < titles2.size(); i++) {
				Cell headerBreakdownAmountCell2 = headerBreakdownAmountRow2.createCell(i);
				headerBreakdownAmountCell2.setCellValue(titles2.get(i));
				headerBreakdownAmountCell2.setCellStyle(styles.get("cell_bold_center_wrap_text_double_border_top_10"));
			}
			
			for (String s : listResultStore) {
				Cell orderIdCell = headerBreakdownAmountRow2.createCell(index);
				orderIdCell.setCellValue(s + "(TT)");
				orderIdCell.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				index += 1;
			}
			rownum += 1;
			
			index = titles.size();
			for (Map<String, Object> map2 : listData) {
				Row orderDetailRow2 = sheet.createRow(rownum);
				orderDetailRow2.setHeight((short) 380);
				for(Map.Entry<String,Object> entry: map2.entrySet()){
					if(entry.getKey().equals("staffId")){
						Cell orderIdCell = orderDetailRow2.createCell(0);
						String storetId = (String) entry.getValue();
						orderIdCell.setCellValue(storetId);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("staffName")){
						Cell orderIdCell = orderDetailRow2.createCell(1);
						String storeName = (String) entry.getValue();
						orderIdCell.setCellValue(storeName);
						orderIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("valueTotal")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						DecimalFormatSymbols symbols = new DecimalFormatSymbols();
						symbols.setGroupingSeparator(',');
						symbols.setDecimalSeparator('.');
						String pattern = "#,##0";
						DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
						decimalFormat.setParseBigDecimal(true);
						Cell productTotalCell = orderDetailRow2.createCell(2);
						productTotalCell.setCellValue(decimalFormat.format(productTotal));
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					int dummy = 0;
					int dummy2 = 1;
					String nullValue = "-";
					for (String s : listResultStore) {
						if(entry.getKey().equals(s+"t")){
							Cell orderIdCell = orderDetailRow2.createCell(index + dummy + listResultStore.indexOf(s));
							String value = ((BigDecimal) entry.getValue()).toString();
							BigDecimal abc = (BigDecimal) entry.getValue();
							DecimalFormatSymbols symbols = new DecimalFormatSymbols();
							symbols.setGroupingSeparator(',');
							symbols.setDecimalSeparator('.');
							String pattern = "#,##0";
							DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
							decimalFormat.setParseBigDecimal(true);
							if(value.equals("0")){
								orderIdCell.setCellValue(nullValue);
							} else {
								orderIdCell.setCellValue(decimalFormat.format(abc));
							}
							orderIdCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
							dummy +=  1;
						}
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_TH_doanh_so_ban_hang_theo_CVBH_" + fromE + "_" + thruE + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportTurnoverProProStoReportDisToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productStore = request.getParameter("productStore");
		String category = request.getParameter("category");
		String orderStatus = request.getParameter("orderStatus");
		List<String> productStoreInput = null;
		List<String> categoryInput = null;
		String sortIdInput = null;
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSTurnoverReport", locale);
		String subTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSRevenuePPSReport", locale);
		String pSN = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductStore", locale);
		String cate = UtilProperties.getMessage("BaseSalesUiLabels", "BSCategoryId", locale);
		String pI = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductId", locale);
		String pN = UtilProperties.getMessage("BaseSalesUiLabels", "BSProduct", locale);
		String q = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", locale);
		String t = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", locale);
		
		if(productStore.equals("") || productStore.equals("null")){
			productStoreInput = null;
		}
		if(!productStore.equals("") && !productStore.equals("null")){
			String[] productStoreData = productStore.split(",");
			productStoreInput = new ArrayList<>();
			if(productStoreData.length != 0){
				for (String i : productStoreData) {
					productStoreInput.add(i);
				}
			}
		}
		
		if(category.equals("") || category.equals("null")){
			categoryInput = null;
		}
		if(!category.equals("") && !category.equals("null")){
			String[] categoryData = category.split(",");
			categoryInput = new ArrayList<>();
			if(categoryData.length != 0){
				for (String i : categoryData) {
					categoryInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDateOut = new Timestamp(fromDateLog);
		Timestamp thruDateOut = new Timestamp(thruDateLog);
		Date fromDateTs = UtilDateTime.getDayStart(fromDateOut);
		Date thruDateTs = UtilDateTime.getDayStart(thruDateOut);
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("sortId", sortIdInput);
		context.put("orderStatus", orderStatus);
		context.put("productStore[]", productStoreInput);
		context.put("category[]", categoryInput);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateTurnoverSM", context);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 25*256);
			sheet.setColumnWidth(2, 20*256);
			sheet.setColumnWidth(3, 50*256);
			sheet.setColumnWidth(4, 18*256);
			sheet.setColumnWidth(5, 18*256);
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
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
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow3 = sheet.createRow(rownum);
			khoangCachRow3.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell3 = khoangCachRow3.createCell(0);
			khoangCachCell3.setCellValue(subTitle.toUpperCase());
			khoangCachCell3.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add(pSN);
			titles.add(cate);
			titles.add(pI);
			titles.add(pN);
			titles.add(q);
			titles.add(t);
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
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("productStoreName")){
						String storeNameOut = (String) entry.getValue();
						Cell storeNameCell = orderDetailRow.createCell(0);
						storeNameCell.setCellValue(storeNameOut);
						storeNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("category")){
						String categoryOut = (String) entry.getValue();
						Cell categoryCell = orderDetailRow.createCell(1);
						categoryCell.setCellValue(categoryOut);
						categoryCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productId")){
						String productIdOut = (String) entry.getValue();
						Cell productIdCell = orderDetailRow.createCell(2);
						productIdCell.setCellValue(productIdOut);
						productIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productName")){
						String productNameOut = (String) entry.getValue();
						Cell productNameCell = orderDetailRow.createCell(3);
						productNameCell.setCellValue(productNameOut);
						productNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("quantity1")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						String productQuantityStr = productQuantity.toString();
						String productQuantityStrOut = productQuantityStr.split("\\.")[0];
						Cell productQuantityCell = orderDetailRow.createCell(4);
						productQuantityCell.setCellValue(productQuantityStrOut);
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("total1")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						String productTotalStr = productTotal.toString();
						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow.createCell(5);
						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_doanh_so_" + fromDateTs + "_" + thruDateTs + ".xls");
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportTurnoverProChaReportDisToExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String channel = request.getParameter("channel");
		String category = request.getParameter("category");
		String orderStatus = request.getParameter("orderStatus");
		List<String> channelInput = null;
		List<String> categoryInput = null;
		String sortIdInput = null;
		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSTurnoverReport", locale);
		String subTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSRevenueProductChannel", locale);
		String cN = UtilProperties.getMessage("BaseSalesUiLabels", "BSChannel", locale);
		String cate = UtilProperties.getMessage("BaseSalesUiLabels", "BSCategoryId", locale);
		String pI = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductId", locale);
		String pN = UtilProperties.getMessage("BaseSalesUiLabels", "BSProduct", locale);
		String q = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", locale);
		String t = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", locale);
		
		if(channel.equals("") || channel.equals("null")){
			channelInput = null;
		}
		if(!channel.equals("") && !channel.equals("null")){
			String[] productStoreData = channel.split(",");
			channelInput = new ArrayList<>();
			if(productStoreData.length != 0){
				for (String i : productStoreData) {
					channelInput.add(i);
				}
			}
		}
		
		if(category.equals("") || category.equals("null")){
			categoryInput = null;
		}
		if(!category.equals("") && !category.equals("null")){
			String[] categoryData = category.split(",");
			categoryInput = new ArrayList<>();
			if(categoryData.length != 0){
				for (String i : categoryData) {
					categoryInput.add(i);
				}
			}
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDateOut = new Timestamp(fromDateLog);
		Timestamp thruDateOut = new Timestamp(thruDateLog);
		Date fromDateTs = UtilDateTime.getDayStart(fromDateOut);
		Date thruDateTs = UtilDateTime.getDayStart(thruDateOut);
		
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("sortId", sortIdInput);
		context.put("orderStatus", orderStatus);
		context.put("storeChannel[]", channelInput);
		context.put("category[]", categoryInput);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("evaluateTurnoverByCD", context);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 25*256);
			sheet.setColumnWidth(2, 20*256);
			sheet.setColumnWidth(3, 40*256);
			sheet.setColumnWidth(4, 18*256);
			sheet.setColumnWidth(5, 18*256);
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
				anchor.setRow2(5);
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
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow3 = sheet.createRow(rownum);
			khoangCachRow3.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell3 = khoangCachRow3.createCell(0);
			khoangCachCell3.setCellValue(subTitle.toUpperCase());
			khoangCachCell3.setCellStyle(styles.get("cell_bold_normal_Left_8"));
			rownum += 1;
			
			List<String> titles = new FastList<String>();
			titles.add(cN);
			titles.add(cate);
			titles.add(pI);
			titles.add(pN);
			titles.add(q);
			titles.add(t);
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
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("channel")){
						String storeNameOut = (String) entry.getValue();
						Cell storeNameCell = orderDetailRow.createCell(0);
						storeNameCell.setCellValue(storeNameOut);
						storeNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("category")){
						String categoryOut = (String) entry.getValue();
						Cell categoryCell = orderDetailRow.createCell(1);
						categoryCell.setCellValue(categoryOut);
						categoryCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productId")){
						String productIdOut = (String) entry.getValue();
						Cell productIdCell = orderDetailRow.createCell(2);
						productIdCell.setCellValue(productIdOut);
						productIdCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("productName")){
						String productNameOut = (String) entry.getValue();
						Cell productNameCell = orderDetailRow.createCell(3);
						productNameCell.setCellValue(productNameOut);
						productNameCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("Quantity")){
						BigDecimal productQuantity = (BigDecimal) entry.getValue();
						String productQuantityStr = productQuantity.toString();
						String productQuantityStrOut = productQuantityStr.split("\\.")[0];
						Cell productQuantityCell = orderDetailRow.createCell(4);
						productQuantityCell.setCellValue(productQuantityStrOut);
						productQuantityCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("Total")){
						BigDecimal productTotal = (BigDecimal) entry.getValue();
						String productTotalStr = productTotal.toString();
						String productTotalStrOut = productTotalStr.split("\\.")[0];
						Cell productTotalCell = orderDetailRow.createCell(5);
						productTotalCell.setCellValue(productTotalStrOut);
						productTotalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_doanh_so_" + fromDateTs + "_" + thruDateTs + ".xls");
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
	public static void exportRouteHistoryReportToExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String timePeriod = request.getParameter("timePeriod");
		
		String mainTitle = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSRouteHistory", locale);
		String BSSalesman = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSSalesman", locale);
		String BSAgents = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSAgents", locale);
		String BSMRoute = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMRoute", locale);
		String BSMVisits = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMVisits", locale);
		String BSMOrderVolume = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMOrderVolume", locale);
		String BACCCustomerIncomeStatement = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCustomerIncomeStatement", locale);
		
		
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Timestamp fromDateOut = new Timestamp(fromDateLog);
		Timestamp thruDateOut = new Timestamp(thruDateLog);
		Date fromDateTs = UtilDateTime.getDayStart(fromDateOut);
		Date thruDateTs = UtilDateTime.getDayStart(thruDateOut);
		
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("timePeriod", timePeriod);
		context.put("PARTY_FROM", true);
		context.put("PARTY_TO", true);
		context.put("ROUTE", true);
		context.put("newCustomer", true);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> resultService =  dispatcher.runSync("routeHistory", context);
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
			
			sheet.setColumnWidth(0, 25*256);
			sheet.setColumnWidth(1, 25*256);
			sheet.setColumnWidth(2, 30*256);
			sheet.setColumnWidth(3, 25*256);
			sheet.setColumnWidth(4, 25*256);
			sheet.setColumnWidth(5, 25*256);
			
			Row imgHead = sheet.createRow(0);
			imgHead.createCell(0);
			int rownum = 0;
			FileInputStream  fis = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
				if (UtilValidate.isNotEmpty(imageServerPath)) {
					File file = new File(imageServerPath);
					fis = new FileInputStream(file);
					byte[] bytesImg = IOUtils.toByteArray(fis);
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
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(fis!=null)fis.close();
			}
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 500);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(mainTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			Row khoangCachRow2 = sheet.createRow(rownum);
			khoangCachRow2.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,4));
			Cell khoangCachCell2 = khoangCachRow2.createCell(0);
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			List<String> titles = FastList.newInstance();
			titles.add(BSSalesman);
			titles.add(BSAgents);
			titles.add(BSMRoute);
			titles.add(BSMVisits);
			titles.add(BSMOrderVolume);
			titles.add(BACCCustomerIncomeStatement);
			
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
				for(Map.Entry<String,Object> entry: map.entrySet()){
					if(entry.getKey().equals("PARTY_FROM")){
						String partyFrom = (String) entry.getValue();
						Cell partyFromCell = orderDetailRow.createCell(0);
						partyFromCell.setCellValue(partyFrom);
						partyFromCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("PARTY_TO")){
						String partyTo = (String) entry.getValue();
						Cell partyToCell = orderDetailRow.createCell(1);
						partyToCell.setCellValue(partyTo);
						partyToCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("ROUTE")){
						String route = (String) entry.getValue();
						Cell routeCell = orderDetailRow.createCell(2);
						routeCell.setCellValue(route);
						routeCell.setCellStyle(styles.get("cell_normal_left_border_full_10"));
					}
					if(entry.getKey().equals("COUNT")){
						Long count = (Long) entry.getValue();
						String countStr = count.toString();
						String countStrOut = countStr.split("\\.")[0];
						Cell countCell = orderDetailRow.createCell(3);
						countCell.setCellValue(countStrOut);
						countCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("ORDER")){
						Long order = (Long) entry.getValue();
						String orderStr = order.toString();
						String orderStrOut = orderStr.split("\\.")[0];
						Cell orderCell = orderDetailRow.createCell(4);
						orderCell.setCellValue(orderStrOut);
						orderCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
					if(entry.getKey().equals("TOTAL")){
						BigDecimal total = (BigDecimal) entry.getValue();
						String totalStr = total.toString();
						String totalStrOut = totalStr.split("\\.")[0];
						Cell totalCell = orderDetailRow.createCell(5);
						totalCell.setCellValue(totalStrOut);
						totalCell.setCellStyle(styles.get("cell_normal_right_border_full_10"));
					}
				}
				rownum += 1;
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + "Bao_cao_lich_su_tuyen_" + resultService.get("fromDate") + "_" + resultService.get("thruDate") + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}