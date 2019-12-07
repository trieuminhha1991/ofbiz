package com.olbius.basesales.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class ExportExcelUtil {
	
	public static Cell createCell(Row row, int columnIndex, Object value, CellStyle cellStyle) {
		if (row == null) return null;
		
		Cell cell = row.createCell(columnIndex);
		if (value != null) {
			if (value instanceof String) cell.setCellValue((String) value);
			if (value instanceof Integer) cell.setCellValue((Integer) value);
			if (value instanceof Double) cell.setCellValue((Double) value);
			if (value instanceof Long) cell.setCellValue((Long) value);
		}
		if (cellStyle != null) cell.setCellStyle(cellStyle);
		return cell;
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
	
	private static void setBackgroundColor(CellStyle style, short paletteIndex) {
		style.setFillForegroundColor(paletteIndex);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	}
	
	private static CellStyle initCellStyleNormalCellContent(Workbook wb) {
		return initCellStyleNormalCellContent(wb, null, null, null);
	}
	
	private static CellStyle initCellStyleNormalCellSubtitle(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setItalic(true);
		if (textColor != null) font.setColor(textColor);
		if (fontWeight != null) font.setBoldweight(fontWeight);
		
		CellStyle style = createNonBorderedStyle(wb);
		style.setFont(font);
		if (backgroundColor != null) {
			setBackgroundColor(style, backgroundColor);
		}
		return style;
	}
	
	private static CellStyle initCellStyleNormalCellContent(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight) {
		return initCellStyleNormalCellContent(wb, textColor, backgroundColor, fontWeight, false);
	}
	
	private static CellStyle initCellStyleNormalCellContent(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight, Boolean strikeout) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		if (textColor != null) font.setColor(textColor);
		if (fontWeight != null) font.setBoldweight(fontWeight);
		if (strikeout) font.setStrikeout(strikeout);
		
		CellStyle style = createNonBorderedStyle(wb);
		style.setFont(font);
		//style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		if (backgroundColor != null) {
			setBackgroundColor(style, backgroundColor);
		}
		return style;
	}
	
	private static CellStyle initCellStyleNormalCellContentLeft(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight) {
		CellStyle style = initCellStyleNormalCellContent(wb, textColor, backgroundColor, fontWeight);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		return style;
	}
	
	private static CellStyle initCellStyleNormalCellContentCenter(Workbook wb) {
		CellStyle style = initCellStyleNormalCellContent(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		return style;
	}
	
	private static CellStyle initCellStyleNormalCellContentCurrency(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight) {
		return initCellStyleNormalCellContentCurrency(wb, textColor, backgroundColor, fontWeight, false);
	}
	
	private static CellStyle initCellStyleNormalCellContentCurrency(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight, Boolean strikeout) {
		CellStyle style = initCellStyleNormalCellContent(wb, textColor, backgroundColor, fontWeight, strikeout);
		HSSFDataFormat fmt = (HSSFDataFormat) wb.createDataFormat();
		style.setDataFormat(fmt.getFormat("#,##0.00"));
		return style;
	}
	
	private static CellStyle initCellStyleNormalCellContentNumber(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight) {
		CellStyle style = initCellStyleNormalCellContent(wb, textColor, backgroundColor, fontWeight);
		HSSFDataFormat fmt = (HSSFDataFormat) wb.createDataFormat();
		style.setDataFormat(fmt.getFormat("#,##0"));
		return style;
	}
	
	private static CellStyle initCellStyleNormalCellContentPercent(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight) {
		CellStyle style = initCellStyleNormalCellContent(wb, textColor, backgroundColor, fontWeight);
		HSSFDataFormat fmt = (HSSFDataFormat) wb.createDataFormat();
		style.setDataFormat(fmt.getFormat("0.00%"));
		return style;
	}
	
	private static CellStyle initCellStyleNormalCellContentNumberZero(Workbook wb, Short textColor, Short backgroundColor, Short fontWeight) {
		CellStyle style = initCellStyleNormalCellContent(wb, textColor, backgroundColor, fontWeight);
		HSSFDataFormat fmt = (HSSFDataFormat) wb.createDataFormat();
		style.setDataFormat(fmt.getFormat("_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)"));
		return style;
	}
	
	public static Map<String, CellStyle> createStylesNormal(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style;
		
		style = initCellStyleNormalCellContentCenter(wb);
		styles.put("cell_normal_centered_border_full_10", style);
		
		style = initCellStyleNormalCellContent(wb);
		styles.put("cell_normal_auto_border_full_10", style);
		
		style = initCellStyleNormalCellContent(wb);
		HSSFDataFormat fmt = (HSSFDataFormat) wb.createDataFormat();
		style.setDataFormat(fmt.getFormat("0.00%"));
		styles.put("cell_normal_auto_border_full_10_percent", style);
		
		style = initCellStyleNormalCellContentNumber(wb, null, null, null);
		styles.put("cell_normal_auto_border_full_10_number", style);
		
		style = initCellStyleNormalCellContentCurrency(wb, null, null, null);
		styles.put("cell_normal_auto_border_full_10_currency", style);
		
		style = initCellStyleNormalCellContentCurrency(wb, null, null, null, true);
		styles.put("cell_normal_auto_border_full_10_currency_strikeout", style);
		
		style = initCellStyleNormalCellSubtitle(wb, null, null, null);
		styles.put("cell_normal_cell_subtitle", style);
		
		style = initCellStyleNormalCellSubtitle(wb, null, null, null);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		styles.put("cell_normal_cell_subtitle_right", style);
		
		Font fontBoldCenterNoBorder16 = wb.createFont();
		fontBoldCenterNoBorder16.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontBoldCenterNoBorder16.setFontHeightInPoints((short) 16);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(fontBoldCenterNoBorder16);
		styles.put("cell_bold_centered_no_border_16", style);
		
		Font fontNormalBoldLeft8 = wb.createFont();
		fontNormalBoldLeft8.setFontHeightInPoints((short) 8);
		fontNormalBoldLeft8.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(fontNormalBoldLeft8);
		styles.put("cell_bold_normal_Left_8", style);
		
		Font fontNormalCenterBorderTop10 = wb.createFont();
		fontNormalCenterBorderTop10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(fontNormalCenterBorderTop10);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_top_10", style);
		
		Font fontNormalCenterVtopBorderTop10 = wb.createFont();
		fontNormalCenterVtopBorderTop10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(fontNormalCenterVtopBorderTop10);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_vtop_wrap_text_border_top_10", style);
		
		return styles;
	}
	
	public static Map<String, CellStyle> createStylesLevel(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style;
		
		short fontWeightBold = Font.BOLDWEIGHT_BOLD;
		// set style by level
		HSSFWorkbook hwb = new HSSFWorkbook();
		HSSFPalette palette = hwb.getCustomPalette();
		HSSFColor myColor;
		
		myColor = palette.findSimilarColor(180, 236, 144);
		short palIndexLevelne1 = myColor.getIndex();
		myColor = palette.findSimilarColor(255, 153, 204);
		short palIndexLevel1 = myColor.getIndex();
		myColor = palette.findSimilarColor(255, 204, 153);
		short palIndexLevel2 = myColor.getIndex();
		myColor = palette.findSimilarColor(230, 184, 183);
		short palIndexLevel3 = myColor.getIndex();
		myColor = palette.findSimilarColor(204, 255, 255);
		short palIndexLevel4 = myColor.getIndex();
		myColor = palette.findSimilarColor(255, 255, 153);
		short palIndexLevel5 = myColor.getIndex();
		
		// get text color
		myColor = palette.findSimilarColor(54, 136, 1);
		short palIndexTextColorLne1 = myColor.getIndex();
		myColor = palette.findSimilarColor(221, 8, 6);
		short palIndexTextColorL0 = myColor.getIndex();
		myColor = palette.findSimilarColor(0, 0, 255);
		short palIndexTextColorL2 = myColor.getIndex();
		myColor = palette.findSimilarColor(221, 8, 6);
		short palIndexTextColorL3 = myColor.getIndex();
		
		// LEVEL NEGATIVE ONE
		style = initCellStyleNormalCellContentLeft(wb, palIndexTextColorLne1, palIndexLevelne1, fontWeightBold);
		styles.put("cell_normal_left_border_full_10_levelne1", style);
		style = initCellStyleNormalCellContentNumberZero(wb, palIndexTextColorLne1, palIndexLevelne1, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_levelne1", style);
		style = initCellStyleNormalCellContentPercent(wb, palIndexTextColorLne1, palIndexLevelne1, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_percent_levelne1", style);
		
		// LEVEL ZERO 
		style = initCellStyleNormalCellContentLeft(wb, palIndexTextColorL0, null, fontWeightBold);
		styles.put("cell_normal_left_border_full_10_level0", style);
		style = initCellStyleNormalCellContentNumberZero(wb, palIndexTextColorL0, null, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_level0", style);
		style = initCellStyleNormalCellContentPercent(wb, palIndexTextColorL0, null, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_percent_level0", style);
		
		// LEVEL ONE 
		style = initCellStyleNormalCellContentLeft(wb, null, palIndexLevel1, fontWeightBold);
		styles.put("cell_normal_left_border_full_10_level1", style);
		style = initCellStyleNormalCellContentNumberZero(wb, null, palIndexLevel1, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_level1", style);
		style = initCellStyleNormalCellContentPercent(wb, null, palIndexLevel1, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_percent_level1", style);
		
		// LEVEL TWO 
		style = initCellStyleNormalCellContentLeft(wb, palIndexTextColorL2, palIndexLevel2, fontWeightBold);
		styles.put("cell_normal_left_border_full_10_level2", style);
		style = initCellStyleNormalCellContentNumberZero(wb, palIndexTextColorL2, palIndexLevel2, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_level2", style);
		style = initCellStyleNormalCellContentPercent(wb, palIndexTextColorL2, palIndexLevel2, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_percent_level2", style);
		
		// LEVEL THREE 
		style = initCellStyleNormalCellContentLeft(wb, palIndexTextColorL3, palIndexLevel3, fontWeightBold);
		styles.put("cell_normal_left_border_full_10_level3", style);
		style = initCellStyleNormalCellContentNumberZero(wb, palIndexTextColorL3, palIndexLevel3, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_level3", style);
		style = initCellStyleNormalCellContentPercent(wb, palIndexTextColorL3, palIndexLevel3, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_percent_level3", style);
		
		// LEVEL FOUR 
		style = initCellStyleNormalCellContentLeft(wb, null, palIndexLevel4, fontWeightBold);
		styles.put("cell_normal_left_border_full_10_level4", style);
		style = initCellStyleNormalCellContentNumberZero(wb, null, palIndexLevel4, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_level4", style);
		style = initCellStyleNormalCellContentPercent(wb, null, palIndexLevel4, fontWeightBold);
		styles.put("cell_normal_auto_border_full_10_percent_level4", style);
		
		// LEVEL FIVE 
		style = initCellStyleNormalCellContentLeft(wb, null, null, null);
		styles.put("cell_normal_left_border_full_10_level5", style);
		style = initCellStyleNormalCellContentNumberZero(wb, null, null, null);
		styles.put("cell_normal_auto_border_full_10_level5", style);
		style = initCellStyleNormalCellContentPercent(wb, null, palIndexLevel5, null);
		styles.put("cell_normal_auto_border_full_10_percent_level5", style);
		
		return styles;
	}
	
	/*private static CellStyle createBorderedBlueStyle(Workbook wb) {
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
	
	private static CellStyle createBorderedThinStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}*/
	
	/*public static Map<String, CellStyle> createStyles(Workbook wb) {
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

		Font normalCenterBorderTopFont10 = wb.createFont();
		normalCenterBorderTopFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		normalCenterBorderTopFont10.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(normalCenterBorderTopFont11);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_top_11", style);
		
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
	}*/
}
