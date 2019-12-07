package com.olbius.basesales.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;

import javolution.util.FastMap;

public class ExcelUtil {

	public static int CELL_TYPE_NUMERIC = HSSFCell.CELL_TYPE_NUMERIC;
	public static int CELL_TYPE_STRING = HSSFCell.CELL_TYPE_STRING;

	public static int insertLogo(Workbook wb, Sheet sheet) throws IOException {
		int rownum = 0;
		sheet.createRow(rownum).createCell(rownum);
		FileInputStream is = null;
		try {
			String imageServerPath = FlexibleStringExpander
					.expandString(UtilProperties.getPropertyValue("basesales", "image.management.logoPath"), null);
			File file = new File(imageServerPath);
			if (file.exists() && !file.isDirectory()) {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				is.close();
		}
		return rownum;
	}

	public static void responseWrite(HttpServletResponse response, Workbook wb, String workbookName) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + workbookName + format.format(new Date()) + ".xls");
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

	public static void responseWrite(HttpServletResponse response, List<Map<String, Workbook>> wbs, String fileName)
			throws IOException {
		ByteArrayOutputStream baos = null;
		ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Map<String, Integer> nameUnique = FastMap.newInstance();
			for (Map<String, Workbook> x : wbs) {
				Set<String> keys = x.keySet();
				for (String key : keys) {
					baos = new ByteArrayOutputStream();
					String entryName = key;
					if (nameUnique.containsKey(key) && UtilValidate.isNotEmpty(nameUnique.get(key))) {
						entryName = entryName + "(" + nameUnique.get(key) + ")";
						nameUnique.put(key, nameUnique.get(key) + 1);
					} else {
						nameUnique.put(key, 1);
					}
					x.get(key).write(baos);
					zip.putNextEntry(new ZipEntry(entryName + ".xls"));
					zip.write(baos.toByteArray());
					zip.closeEntry();
				}
			}
			response.setHeader("content-disposition",
					"attachment;filename=" + fileName + format.format(new Date()) + ".zip");
			response.setContentType("application/zip");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (zip != null) {
				zip.close();
			}
		}
	}

	public static void setCellNumber(Cell cell, Map<String, CellStyle> styles, BigDecimal value) {
		cell.setCellType(ExcelUtil.CELL_TYPE_NUMERIC);
		if (value == null) {
			cell.setCellValue(Double.parseDouble("0"));
			cell.setCellStyle(styles.get("cell_number_zero_bordered_10"));
		} else {
			cell.setCellValue(Double.parseDouble(value.toString()));
			if (value.compareTo(BigDecimal.ZERO) > 0) {
				cell.setCellStyle(styles.get("cell_number_bordered_10"));
			} else {
				cell.setCellStyle(styles.get("cell_number_zero_bordered_10"));
			}
		}
	}

	public static CellStyle createNonBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		return style;
	}

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

	private static void setBackgroundColor(CellStyle style, short paletteIndex) {
		style.setFillForegroundColor(paletteIndex);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	}

	private static CellStyle initCellStyleNormalCellContent(Workbook wb) {
		return initCellStyleNormalCellContent(wb, null, null, null);
	}

	private static CellStyle initCellStyleNormalCellSubtitle(Workbook wb, Short textColor, Short backgroundColor,
			Short fontWeight) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setItalic(true);
		if (textColor != null)
			font.setColor(textColor);
		if (fontWeight != null)
			font.setBoldweight(fontWeight);

		CellStyle style = createNonBorderedStyle(wb);
		style.setFont(font);
		if (backgroundColor != null) {
			setBackgroundColor(style, backgroundColor);
		}
		return style;
	}

	private static CellStyle initCellStyleNormalCellContent(Workbook wb, Short textColor, Short backgroundColor,
			Short fontWeight) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		if (textColor != null)
			font.setColor(textColor);
		if (fontWeight != null)
			font.setBoldweight(fontWeight);

		CellStyle style = createNonBorderedStyle(wb);
		style.setFont(font);
		// style.setWrapText(true);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		if (backgroundColor != null) {
			setBackgroundColor(style, backgroundColor);
		}
		return style;
	}

	private static CellStyle initCellStyleNormalCellContentCenter(Workbook wb) {
		CellStyle style = initCellStyleNormalCellContent(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		return style;
	}

	private static CellStyle initCellStyleNormalCellContentCurrency(Workbook wb, Short textColor, Short backgroundColor,
			Short fontWeight) {
		CellStyle style = initCellStyleNormalCellContent(wb, textColor, backgroundColor, fontWeight);
		HSSFDataFormat fmt = (HSSFDataFormat) wb.createDataFormat();
		style.setDataFormat(fmt.getFormat("#,##0.000"));
		return style;
	}

	private static CellStyle initCellStyleNormalCellContentNumber(Workbook wb, Short textColor, Short backgroundColor,
			Short fontWeight) {
		CellStyle style = initCellStyleNormalCellContent(wb, textColor, backgroundColor, fontWeight);
		HSSFDataFormat fmt = (HSSFDataFormat) wb.createDataFormat();
		style.setDataFormat(fmt.getFormat("#,##0"));
		return style;
	}

	public static Map<String, CellStyle> createStylesNormal(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style;
		Font font;

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

		style = initCellStyleNormalCellSubtitle(wb, null, null, null);
		styles.put("cell_normal_cell_subtitle", style);

		style = initCellStyleNormalCellSubtitle(wb, null, null, null);
		style.setWrapText(true);
		styles.put("cell_wrap_text_cell_subtitle", style);

		style = initCellStyleNormalCellSubtitle(wb, null, null, null);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		styles.put("cell_normal_cell_subtitle_right", style);

		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 16);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);
		styles.put("cell_bold_centered_no_border_16", style);
		
		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);
		styles.put("cell_bold_centered_no_border_10", style);		

		font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font);
		styles.put("cell_bold_normal_Left_8", style);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_wrap_text_border_top_10", style);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setFont(font);
		style.setWrapText(true);
		style.setBorderTop(CellStyle.BORDER_DOUBLE);
		styles.put("cell_normal_centered_vtop_wrap_text_border_top_10", style);

		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(font);
		style.setWrapText(true);
		styles.put("cell_bold_centered_wrap_text_bordered_10", style);

		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(font);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styles.put("cell_yellow_bold_centered_wrap_text_bordered_10", style);

		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font);
		style.setWrapText(true);
		styles.put("cell_left_wrap_text_bordered_10", style);

		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font);
		style.setWrapText(true);
		DataFormat df = wb.createDataFormat();
		String currencyFormat = "#,###0.00";
		style.setDataFormat(df.getFormat(currencyFormat));
		styles.put("cell_right_wrap_text_bordered_10", style);

		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);
		style.setWrapText(true);
		styles.put("cell_center_wrap_text_bordered_10", style);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		style = createBorderedStyle(wb);
		style.setFont(font);
		styles.put("cell_normal_bordered_10", style);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font);
		styles.put("cell_right_10", style);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setFont(font);
		styles.put("cell_normal_10", style);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		style = createNonBorderedStyle(wb);
		style.setFont(font);
		style.setWrapText(true);
		styles.put("cell_wrap_text_10", style);
		
		font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font);
		style.setWrapText(true);
		style.setDataFormat(wb.createDataFormat().getFormat("#,##.00"));
		styles.put("cell_number_bordered_10", style);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font);
		style.setWrapText(true);
		styles.put("cell_number_zero_bordered_10", style);		

		return styles;
	}

}