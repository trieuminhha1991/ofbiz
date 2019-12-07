package com.olbius.acc.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelUtil {
	public static CellStyle createBorderedStyle(Workbook wb) {
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
	
	public static CellStyle createNonBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		return style;
	}
	
	public static CellStyle createBorderedBlueStyle(Workbook wb) {
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
	
	public static CellStyle createBorderedThinStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}
	
	public static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		DataFormat df = wb.createDataFormat();
		String quantityFormat = "#,##0";
		String currencyFormat = "#,###0.00";
		CellStyle style;

		Font normalFormatNumberCenterBorderFullFont10 = wb.createFont();
		normalFormatNumberCenterBorderFullFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalFormatNumberCenterBorderFullFont10);
		style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setDataFormat(wb.createDataFormat().getFormat("#,##.00"));
		styles.put("cell_normal_formatnumber_centered_border_full_10", style);

		Font boldCenterNoBorderFontLeft11 = wb.createFont();
		boldCenterNoBorderFontLeft11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFontLeft11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(boldCenterNoBorderFontLeft11);
		styles.put("cell_bold_centered_no_border_left_11", style);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFontLeft11);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		style.setFillPattern(CellStyle.BIG_SPOTS);
		styles.put("cell_bold_centered_special_no_border_left_11", style);

		Font fontHeaderExcel = wb.createFont();
		fontHeaderExcel.setColor(IndexedColors.WHITE.getIndex());
		fontHeaderExcel.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontHeaderExcel.setFontHeightInPoints((short) 11);
		CellStyle _style = createNonBorderedStyle(wb);
		_style.setAlignment(CellStyle.ALIGN_CENTER);
		_style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		_style.setFont(fontHeaderExcel);
		_style.setBorderBottom(CellStyle.BORDER_THIN);
		_style.setBorderTop(CellStyle.BORDER_THIN);
		_style.setBorderRight(CellStyle.BORDER_THIN);
		_style.setBorderLeft(CellStyle.BORDER_THIN);
		_style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
		_style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put("cell_bold_centered_header_excel_no_border_left_11", _style);

		Font fontHeaderExcel10 = wb.createFont();
		//fontHeaderExcel10.setColor(IndexedColors.WHITE.getIndex());
		fontHeaderExcel10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontHeaderExcel10.setFontHeightInPoints((short) 10);
		CellStyle _style10 = createNonBorderedStyle(wb);
		_style10.setAlignment(CellStyle.ALIGN_CENTER);
		_style10.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		_style10.setFont(fontHeaderExcel10);
		_style10.setBorderBottom(CellStyle.BORDER_THIN);
		_style10.setBorderTop(CellStyle.BORDER_THIN);
		_style10.setBorderRight(CellStyle.BORDER_THIN);
		_style10.setBorderLeft(CellStyle.BORDER_THIN);
		_style10.setWrapText(true);
		styles.put("cell_bold_centered_header_excel_border_10", _style10);
		
		Font fontHeaderBlueNoBorderBottom10 = wb.createFont();
		fontHeaderBlueNoBorderBottom10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontHeaderBlueNoBorderBottom10.setFontHeightInPoints((short) 10);
		CellStyle styleBlueBorderNotBottom = createNonBorderedStyle(wb);
		styleBlueBorderNotBottom.setAlignment(CellStyle.ALIGN_CENTER);
		styleBlueBorderNotBottom.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		styleBlueBorderNotBottom.setFillPattern(IndexedColors.SKY_BLUE.getIndex());
		styleBlueBorderNotBottom.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
		styleBlueBorderNotBottom.setFont(fontHeaderBlueNoBorderBottom10);
		styleBlueBorderNotBottom.setBorderTop(CellStyle.BORDER_THIN);
		styleBlueBorderNotBottom.setBorderRight(CellStyle.BORDER_THIN);
		styleBlueBorderNotBottom.setBorderLeft(CellStyle.BORDER_THIN);
		styleBlueBorderNotBottom.setBorderBottom(CellStyle.NO_FILL);
		styleBlueBorderNotBottom.setWrapText(true);
		styles.put("cell_bold_center_blue_border_not_bottom_10", styleBlueBorderNotBottom);
		
		Font fontHeaderBlueNoBorderTop10 = wb.createFont();
		fontHeaderBlueNoBorderTop10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontHeaderBlueNoBorderTop10.setFontHeightInPoints((short) 10);
		CellStyle styleBlueBorderNotTop = createNonBorderedStyle(wb);
		styleBlueBorderNotTop.setAlignment(CellStyle.ALIGN_CENTER);
		styleBlueBorderNotTop.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		styleBlueBorderNotTop.setFillPattern(IndexedColors.SKY_BLUE.getIndex());
		styleBlueBorderNotTop.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
		styleBlueBorderNotTop.setFont(fontHeaderBlueNoBorderTop10);
		styleBlueBorderNotTop.setBorderTop(CellStyle.NO_FILL);
		styleBlueBorderNotTop.setBorderRight(CellStyle.BORDER_THIN);
		styleBlueBorderNotTop.setBorderLeft(CellStyle.BORDER_THIN);
		styleBlueBorderNotTop.setBorderBottom(CellStyle.BORDER_THIN);
		styleBlueBorderNotTop.setWrapText(true);
		styles.put("cell_bold_center_blue_border_not_top_10", styleBlueBorderNotTop);
		
		Font fontHeaderBlueBorder10= wb.createFont();
		fontHeaderBlueBorder10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontHeaderBlueBorder10.setFontHeightInPoints((short) 10);
		CellStyle styleBlueBorder = createNonBorderedStyle(wb);
		styleBlueBorder.setAlignment(CellStyle.ALIGN_CENTER);
		styleBlueBorder.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
		styleBlueBorder.setFont(fontHeaderBlueBorder10);
		styleBlueBorder.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		styleBlueBorder.setFillPattern(IndexedColors.SKY_BLUE.getIndex());
		styleBlueBorder.setBorderTop(CellStyle.BORDER_THIN);
		styleBlueBorder.setBorderRight(CellStyle.BORDER_THIN);
		styleBlueBorder.setBorderLeft(CellStyle.BORDER_THIN);
		styleBlueBorder.setBorderBottom(CellStyle.BORDER_THIN);
		styleBlueBorder.setWrapText(true);
		styles.put("cell_bold_center_blue_border_10", styleBlueBorder);
		
		Font fontNormal10 = wb.createFont();
		fontNormal10.setFontHeightInPoints((short) 10);
		_style = createNonBorderedStyle(wb);
		_style.setAlignment(CellStyle.ALIGN_CENTER);
		_style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		_style.setFont(fontNormal10);
		_style.setBorderBottom(CellStyle.BORDER_THIN);
		_style.setBorderTop(CellStyle.BORDER_THIN);
		_style.setBorderRight(CellStyle.BORDER_THIN);
		_style.setBorderLeft(CellStyle.BORDER_THIN);
		_style.setWrapText(true);
		styles.put("cell_normal_centered_border_10", _style);
		
		style = createNonBorderedStyle(wb);
		Font boldCenterNoBorderFont16 = wb.createFont();
		boldCenterNoBorderFont16.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont16.setFontHeightInPoints((short) 16);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont16);
		styles.put("cell_bold_centered_no_border_16", style);

		style = createNonBorderedStyle(wb);
		Font boldCenterNoBorderFont12 = wb.createFont();
		boldCenterNoBorderFont12.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont12.setFontHeightInPoints((short) 12);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont12);
		styles.put("cell_bold_centered_no_border_12", style);
		
		style = createNonBorderedStyle(wb);
		Font boldCenterNoBorderFont14 = wb.createFont();
		boldCenterNoBorderFont14.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont14.setFontHeightInPoints((short) 14);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont14);
		styles.put("cell_bold_centered_no_border_14", style);
		
		style = createNonBorderedStyle(wb);
		Font boldLeftNoBorderFont14 = wb.createFont();
		boldLeftNoBorderFont14.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldLeftNoBorderFont14.setFontHeightInPoints((short) 14);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(boldLeftNoBorderFont14);
		styles.put("cell_bold_left_no_border_14", style);
		
		style = createNonBorderedStyle(wb);
		Font boldCenterNoBorderFont = wb.createFont();
		boldCenterNoBorderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont.setFontHeightInPoints((short) 13);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(boldCenterNoBorderFont);
		styles.put("cell_bold_left_no_border_16", style);

		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(boldCenterNoBorderFont);
		styles.put("cell_bold_right_no_border_16", style);

		Font boldCenterNoBorderFont11 = wb.createFont();
		boldCenterNoBorderFont11.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont11.setFontHeightInPoints((short) 11);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont11);
		style.setWrapText(true);
		styles.put("cell_bold_centered_no_border_11", style);

		Font boldCenterNoBorderFont10 = wb.createFont();
		boldCenterNoBorderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont11);
		style.setWrapText(true);
		styles.put("cell_bold_centered_no_border_10", style);

		Font boldCenterNoBorderFont9 = wb.createFont();
		boldCenterNoBorderFont9.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont9.setFontHeightInPoints((short) 9);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont9);
		style.setWrapText(true);
		styles.put("cell_bold_centered_no_border_9", style);

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

		Font normalCenterNoBorderFont10 = wb.createFont();
		normalCenterNoBorderFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterNoBorderFont10);
		style.setWrapText(true);
		styles.put("cell_centered_no_border_10", style);
		
		Font normalCenterNoBorderFont9 = wb.createFont();
		normalCenterNoBorderFont9.setFontHeightInPoints((short) 9);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterNoBorderFont9);
		style.setWrapText(true);
		styles.put("cell_centered_no_border_9", style);
		
		Font normalLeftNoBorderFont10 = wb.createFont();
		normalLeftNoBorderFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftNoBorderFont10);
		style.setWrapText(true);
		styles.put("cell_left_no_border_10", style);

		Font normalCenterItalicNoBorderFont10 = wb.createFont();
		normalCenterItalicNoBorderFont10.setFontHeightInPoints((short) 10);
		normalCenterItalicNoBorderFont10.setItalic(true);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(normalCenterItalicNoBorderFont10);
		style.setWrapText(true);
		styles.put("cell_italic_normal_center_no_border_10", style);

		Font normalRightNoBorderFont10 = wb.createFont();
		normalRightNoBorderFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(normalRightNoBorderFont10);
		styles.put("cell_right_no_border_10", style);

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
		normalCenterBorderTopFont10.setFontHeightInPoints((short) 11);
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
		
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setWrapText(true);
		styles.put("cell_border_left_center", style);

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
		styles.put("cell_left_centered_border_full_10", style);

		Font normalRightBorderFullCurrencyFont10 = wb.createFont();
		normalRightBorderFullCurrencyFont10.setFontHeightInPoints((short) 10);
		CellStyle styleCurrency = createNonBorderedStyle(wb);
		styleCurrency.setAlignment(CellStyle.ALIGN_RIGHT);
		styleCurrency.setFont(normalRightBorderFullCurrencyFont10);
		styleCurrency.setWrapText(true);
		styleCurrency.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		styleCurrency.setBorderBottom(CellStyle.BORDER_THIN);
		styleCurrency.setBorderRight(CellStyle.BORDER_THIN);
		styleCurrency.setBorderTop(CellStyle.BORDER_THIN);
		styleCurrency.setBorderLeft(CellStyle.BORDER_THIN);
		styleCurrency.setDataFormat(df.getFormat(currencyFormat));
		styles.put("cell_right_centered_border_full_currency_10", styleCurrency);

		Font normalRightBorderFullQtyFont10 = wb.createFont();
		normalRightBorderFullQtyFont10.setFontHeightInPoints((short) 10);
		CellStyle styleQty = createNonBorderedStyle(wb);
		styleQty.setAlignment(CellStyle.ALIGN_RIGHT);
		styleQty.setFont(normalRightBorderFullQtyFont10);
		styleQty.setWrapText(true);
		styleQty.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		styleQty.setBorderBottom(CellStyle.BORDER_THIN);
		styleQty.setBorderRight(CellStyle.BORDER_THIN);
		styleQty.setBorderTop(CellStyle.BORDER_THIN);
		styleQty.setBorderLeft(CellStyle.BORDER_THIN);
		styleQty.setDataFormat(df.getFormat(quantityFormat));
		styles.put("cell_right_centered_border_full_quantity_10", styleQty);

		Font normalLeftBorderFullDateFormat10 = wb.createFont();
		normalLeftBorderFullDateFormat10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftBorderFullDateFormat10);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		CreationHelper createHelper = wb.getCreationHelper();
		style.setDataFormat(createHelper.createDataFormat().getFormat("DD/MM/yyyy"));
		styles.put("cell_left_border_full_date_10", style);

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
	
		Font boldLeftBorderFont10 = wb.createFont();
		boldLeftBorderFont10.setFontHeightInPoints((short) 10);
		boldLeftBorderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldLeftBorderFont10);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setWrapText(true);
		styles.put("cell_bold_left_border_10", style);

		Font boldRightNoborderFont10 = wb.createFont();
		boldRightNoborderFont10.setFontHeightInPoints((short) 10);
		boldRightNoborderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(boldRightNoborderFont10);
		styles.put("cell_bold_right_no_border_10", style);

		Font boldRightNoborderFontCurrency10 = wb.createFont();
		boldRightNoborderFontCurrency10.setFontHeightInPoints((short) 10);
		boldRightNoborderFontCurrency10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(boldRightNoborderFontCurrency10);
		style.setDataFormat(df.getFormat(currencyFormat));
		styles.put("cell_bold_right_no_border_currency_10", style);

		Font boldCenterNoborderFont10 = wb.createFont();
		boldCenterNoborderFont10.setFontHeightInPoints((short) 10);
		boldCenterNoborderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoborderFont10);
		styles.put("cell_bold_center_no_border_10", style);
		
		Font boldCenterNoborderFont12 = wb.createFont();
		boldCenterNoborderFont12.setFontHeightInPoints((short) 12);
		boldCenterNoborderFont12.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoborderFont12);
		styles.put("cell_bold_center_no_border_12", style);
		
		Font boldLeftNoborderFont12 = wb.createFont();
		boldLeftNoborderFont12.setFontHeightInPoints((short) 12);
		boldLeftNoborderFont12.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(boldLeftNoborderFont12);
		styles.put("cell_bold_left_no_border_12", style);

		Font normalCenterNoborderFont10 = wb.createFont();
		normalCenterNoborderFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(normalCenterNoborderFont10);
		styles.put("cell_normal_center_no_border_10", style);
		
		Font normalCenterNoborderFont12 = wb.createFont();
		normalCenterNoborderFont12.setFontHeightInPoints((short) 12);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(normalCenterNoborderFont12);
		styles.put("cell_normal_center_no_border_12", style);

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
		
		Font normalLeftNoBorderWraptextFont12 = wb.createFont();
		normalLeftNoBorderWraptextFont12.setFontHeightInPoints((short) 12);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftNoBorderWraptextFont12);
		style.setWrapText(true);
		styles.put("cell_normal_left_no_border_wrap_text_12", style);
		
		Font normalLeftNoBorderFont12 = wb.createFont();
		normalLeftNoBorderFont12.setFontHeightInPoints((short) 12);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftNoBorderFont12);
		styles.put("cell_normal_left_no_border_12", style);
		
		Font normalLeftNoBorderNoWrapTextFont10 = wb.createFont();
		normalLeftNoBorderNoWrapTextFont10.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(normalLeftNoBorderNoWrapTextFont10);
		styles.put("cell_normal_left_no_border_10", style);
		
		Font italicLeftNoBorderFont10 = wb.createFont();
		italicLeftNoBorderFont10.setFontHeightInPoints((short) 10);
		italicLeftNoBorderFont10.setItalic(true);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(italicLeftNoBorderFont10);
		styles.put("cell_italic_left_no_border_10", style);

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
		styles.put("cell_normal_centered_not_border_thin_11", style);

		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font);
		styles.put("cell_bold_normal_Left_8", style);
		
		return styles;
	}
	
	public static void responseWrite(HttpServletResponse response, Workbook wb, String workbookName) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			//SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + workbookName + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				baos.close();
			}
		}
	}
	
	public static void createCellOfRow(Row row, int cellIndex, CellStyle style, Integer cellType, Object value){
		Cell cell = row.createCell(cellIndex);
		if(cellType != null){
			cell.setCellType(cellType);
		}
		cell.setCellStyle(style);
		if(value != null){
			if(value instanceof Date){
				cell.setCellValue((Date)value);
			}else if(value instanceof String){
				cell.setCellValue((String)value);
			}else if(value instanceof Timestamp){
				cell.setCellValue((Timestamp)value);
			}else if(value instanceof Double){
				Double tempValue = (Double)value;
				cell.setCellValue(tempValue);
			}else if(value instanceof BigDecimal){
				Double tempValue = ((BigDecimal)value).doubleValue();
				cell.setCellValue(tempValue);
			}else if(value instanceof Integer){
				cell.setCellValue((Integer)value);
			}
		}
	}
}
