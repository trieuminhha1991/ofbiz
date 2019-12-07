package com.olbius.common.export;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

public class ExportExcelStyle {
	public static final short FONT_SIZE_HEADER = 16;
	public static final short FONT_SIZE_SUBTITLE = 10;
	public static final short FONT_SIZE_CONTENT = 10;
	public static final String STYLE_HEADER_NAME = "cell_header_name";
	public static final String STYLE_SUBTITLE_LABEL = "cell_subtitle_label";
	public static final String STYLE_SUBTITLE_CONTENT = "cell_subtitle_content";
	public static final String STYLE_COLUMN_LABEL = "cell_column_label";
	public static final String STYLE_CELL_CONTENT = "cell_column_content";
	public static final String STYLE_CELL_CONTENT_CENTER = "cell_column_content_center";
	public static final String STYLE_CELL_PERCENT = "cell_column_percent";
	public static final String STYLE_CELL_NUMBER = "cell_column_number";
	public static final String STYLE_CELL_DECIMAL = "cell_column_decimal";
	public static final String STYLE_CELL_NUMBER_ZERO = "cell_column_number_zero";
	public static final String STYLE_CELL_DECIMAL_ZERO = "cell_column_decimal_zero";
	public static final String STYLE_CELL_CURRENCY = "cell_column_currency";
	public static final String STYLE_CELL_DATETIME = "cell_column_datetime";
	public static final String STYLE_CELL_DATE = "cell_column_date";
	public static final String STYLE_CELL_TIME = "cell_column_time";
	
	public static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		
		styles.put(STYLE_HEADER_NAME, createStyleHeaderName(wb)); // cell_bold_centered_no_border_16
		styles.put(STYLE_SUBTITLE_LABEL, createStyleSubtitleLabel(wb)); // cell_normal_cell_subtitle_right
		styles.put(STYLE_SUBTITLE_CONTENT, createStyleSubtitleContent(wb)); // cell_normal_cell_subtitle
		styles.put(STYLE_COLUMN_LABEL, createStyleColumnLabel(wb)); // cell_normal_centered_wrap_text_border_top_10
		styles.put(STYLE_CELL_CONTENT, createStyleColumnContent(wb)); // cell_normal_auto_border_full_10
		styles.put(STYLE_CELL_CONTENT_CENTER, createStyleColumnContent(wb, CellStyle.ALIGN_CENTER));
		
		// special cell data
		styles.put(STYLE_CELL_PERCENT, createStyleColumn(wb, "0.00%")); // cell_normal_auto_border_full_10_percent
		styles.put(STYLE_CELL_NUMBER, createStyleColumn(wb, "#,##0")); // cell_normal_auto_border_full_10_number
		styles.put(STYLE_CELL_DECIMAL, createStyleColumn(wb, "#,##0.##"));
		styles.put(STYLE_CELL_NUMBER_ZERO, createStyleColumn(wb, "_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)")); // cell_normal_auto_border_full_10_levelne1
		styles.put(STYLE_CELL_DECIMAL_ZERO, createStyleColumn(wb, "_(* #,##0.##_);_(* (#,##0.##);_(* \"-\"??_);_(@_)"));
		styles.put(STYLE_CELL_CURRENCY, createStyleColumn(wb, "#,##0.00")); // cell_normal_auto_border_full_10_currency
		styles.put(STYLE_CELL_DATETIME, createStyleColumn(wb, "dd/mm/yyyy HH:mm:ss"));
		styles.put(STYLE_CELL_DATE, createStyleColumn(wb, "dd/mm/yyyy"));
		styles.put(STYLE_CELL_TIME, createStyleColumn(wb, "HH:mm:ss"));
		
		return styles;
	}
	
	public static CellStyle initStyle(Workbook wb, short fontSize, Boolean isStatic, Short textColor, Short backgroundColor, Short fontWeight, Short textAlign) {
		Font font = wb.createFont();
		CellStyle style = wb.createCellStyle();
		
		font.setFontHeightInPoints(fontSize);
		if (isStatic != null) font.setItalic(isStatic);
		if (textColor != null) font.setColor(textColor);
		if (fontWeight != null) font.setBoldweight(fontWeight);
		
		style.setFont(font);
		if (textAlign != null) style.setAlignment(textAlign);
		if (backgroundColor != null) {
			style.setFillForegroundColor(backgroundColor);
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		}
		return style;
	}
	
	private static CellStyle createStyleHeaderName(Workbook wb) {
		Font font = wb.createFont();
		CellStyle style = wb.createCellStyle();
		// set font
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints(FONT_SIZE_HEADER);
		// set style
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);
		return style;
	}
	
	private static CellStyle createStyleSubtitleLabel(Workbook wb) {
		return initStyle(wb, FONT_SIZE_SUBTITLE, true, null, null, null, CellStyle.ALIGN_RIGHT);
	}
	
	private static CellStyle createStyleSubtitleContent(Workbook wb) {
		return initStyle(wb, FONT_SIZE_SUBTITLE, true, null, null, null, null);
	}
	
	private static void setBorder(CellStyle style) {
		short colorBlack = IndexedColors.BLACK.getIndex();
		short borderThin = CellStyle.BORDER_THIN;
		style.setBorderRight(borderThin);
		style.setRightBorderColor(colorBlack);
		style.setBorderBottom(borderThin);
		style.setBottomBorderColor(colorBlack);
		style.setBorderLeft(borderThin);
		style.setLeftBorderColor(colorBlack);
		style.setBorderTop(borderThin);
		style.setTopBorderColor(colorBlack);
	}
	
	private static CellStyle createStyleColumnLabel(Workbook wb) {
		CellStyle style = initStyle(wb, FONT_SIZE_CONTENT, null, null, null, Font.BOLDWEIGHT_BOLD, CellStyle.ALIGN_CENTER);
		// set border
		setBorder(style);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		// allow wrap text
		style.setWrapText(true);
		return style;
	}

	private static CellStyle createStyleColumnContent(Workbook wb) {
		CellStyle style = initStyle(wb, FONT_SIZE_CONTENT, null, null, null, null, null);
		setBorder(style);
		return style;
	}
	
	private static CellStyle createStyleColumnContent(Workbook wb, short textAlign) {
		CellStyle style = initStyle(wb, FONT_SIZE_CONTENT, null, null, null, null, textAlign);
		setBorder(style);
		return style;
	}
	
	public static CellStyle createStyleColumn(Workbook wb, String format) {
		CellStyle style = initStyle(wb, FONT_SIZE_CONTENT, null, null, null, null, null);
		// set border
		setBorder(style);
		XSSFDataFormat fmt = (XSSFDataFormat) wb.createDataFormat();
		style.setDataFormat(fmt.getFormat(format));
		return style;
	}
	
	public static CellStyle createStyleColumnPercent(Workbook wb) {
		return createStyleColumn(wb, "0.00%");
	}
	
	public static CellStyle createStyleColumnNumber(Workbook wb, Boolean isDisplayZeroIfNull) {
		if (isDisplayZeroIfNull) {
			return createStyleColumn(wb, "_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)");
		} else {
			return createStyleColumn(wb, "#,##0");
		}
	}
	
	public static CellStyle createStyleColumnCurrency(Workbook wb) {
		return createStyleColumn(wb, "#,##0.00");
	}
}
