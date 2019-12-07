package com.olbius.basehr.importExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericEntityException;

public class HSSFBuilder {
	private int rownum;
	private Locale locale;
	private Date fromDate;
	private Date thruDate;
	protected static final String resource_acc = "BaseAccountingUiLabels";

	public final Workbook build(List<SheetConfig> configs) throws ParseException {
		Workbook wb = new HSSFWorkbook();
		if (configs.size() > 0)
			this.locale = configs.iterator().next().getLocale();
		for (SheetConfig config : configs) {
			buildSheet(wb, config);
			rownum = 0;// reset variable row to 0
		}
		return wb;
	}

	public final Workbook build(List<SheetConfig> configs, Locale locale, Date fromDate, Date thruDate)
			throws ParseException {
		Workbook wb = new XSSFWorkbook();
		this.locale = locale;
		this.fromDate = (Date) ((fromDate != null) ? fromDate : new Date());
		this.thruDate = (Date) ((thruDate != null) ? thruDate : new Date());
		for (SheetConfig config : configs) {
			buildSheet(wb, config);
		}

		return wb;
	}

	protected void buildSheet(Workbook wb, SheetConfig config) throws ParseException {
		Sheet sheet = wb.createSheet(config.getSheetName());
		List<ColumnConfig> columnConfs = config.getColumnConfig();
		List<Map<String, Object>> data = config.getDataConfig();
		//sheet.setAutoFilter(new CellRangeAddress(rownum, rownum, 0, columnConfs.size()));
		Map<String, CellStyle> styles = createStyles(wb);
		//createLogo(sheet, styles);
		createTitle(sheet, config, styles, columnConfs);
		/* createPicture(wb, sheet); */
		createHeader(sheet, config, styles, columnConfs);
		createContent(sheet, data, styles, columnConfs);
	}

	protected void buildGrid(Sheet sheet, SheetConfig config, Map<String, CellStyle> styles) {
		List<ColumnConfig> columnConfs = config.getColumnConfig();
		List<Map<String, Object>> data = config.getDataConfig();
		//sheet.setAutoFilter(new CellRangeAddress(rownum, rownum, 0, columnConfs.size()));
		createHeader(sheet, config, styles, columnConfs);
		createContent(sheet, data, styles, columnConfs);
	}

	protected void createLogo(Sheet sheet, Map<String, CellStyle> styles) {
		Row companyHead = sheet.createRow(0);
		companyHead.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
		Cell companyCell = companyHead.createCell(0);

		companyCell.setCellValue("");
		companyCell.setCellStyle(styles.get("cell_bold_centered_no_border_left_11"));
		Row addressHead = sheet.createRow(1);
		addressHead.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
		Cell addressCell = addressHead.createCell(0);
		addressCell.setCellValue("");
		addressCell.setCellStyle(styles.get("cell_bold_centered_no_border_left_11"));
		rownum += 2;
	}

	protected void createTitle(Sheet sheet, SheetConfig config, Map<String, CellStyle> styles,
			List<ColumnConfig> columnConfs) throws ParseException {
		//rownum++;
		// Create title
		int totalColumnnOfTitle = 10;
		/*Row paddingTop = sheet.createRow(rownum);
		paddingTop.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle));
		Cell emptyCell = paddingTop.createCell(0);
		emptyCell.setCellValue(" ");
		emptyCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;*/

		Row titleRow = sheet.createRow(rownum);
		titleRow.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle));
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(config.getTitle());
		titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;

		/*Row paddingBottom = sheet.createRow(rownum);
		paddingBottom.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle));
		emptyCell = paddingBottom.createCell(0);
		emptyCell.setCellValue(" ");
		emptyCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
		rownum += 1;*/
		if (this.fromDate != null && this.thruDate != null) {
			Row row2 = sheet.createRow(rownum);
			row2.setHeight((short) 320);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle));
			Cell khoangCachCell2 = row2.createCell(0);
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
			khoangCachCell2.setCellValue(UtilProperties.getMessage(resource_acc, "ExcelFromDate", this.locale) + ": "
					+ ((dt.format(this.fromDate) != null) ? dt.format(this.fromDate).toString() : "") + " "
					+ UtilProperties.getMessage(resource_acc, "ExcelThruDate", locale) + " : "
					+ ((dt.format(this.thruDate) != null) ? dt.format(this.thruDate).toString() : ""));
			khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_11"));
			rownum += 1;
		}
	}

	protected void createHeader(Sheet sheet, SheetConfig config, Map<String, CellStyle> styles,
			List<ColumnConfig> columnConfs) {
		// Create header
		int colIndex = 0;
		Row header = null, temp = null;
		if (config.isGroup()) {
			temp = sheet.createRow(rownum++);
			header = sheet.createRow(rownum);
			temp.setHeight(config.getHeaderHeight());
		} else{
			header = sheet.createRow(rownum);
		}
		//sheet.setAutoFilter(new CellRangeAddress(rownum, rownum, 0, columnConfs.size()));
		header.setHeight(config.getHeaderHeight());
		for (ColumnConfig column : columnConfs) {
			sheet.setColumnWidth(colIndex, column.getWidth());
			/* sheet.autoSizeColumn(colIndex); */
			if (temp != null) {
				temp.createCell(colIndex).setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));
			}

			Cell cell = header.createCell(colIndex);
			cell.setCellValue(column.getHeader());
			cell.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));
			column.setCell(cell);
			colIndex++;
		}
		rownum += 1;
		if (config.isGroup()) {
			setGroup(sheet, config, columnConfs, config.getResource());
		}
	}

	public int getRowNum() {
		return this.rownum;
	}

	public void setRowNum(int num) {
		this.rownum = num;
	}

	@SuppressWarnings("unchecked")
	protected void setGroup(Sheet sheet, SheetConfig config, List<ColumnConfig> columnConfs, String resource) {
		Map<String, Object> groupConfigs = config.getGroupConfigs();
		int row = 0;
		if (groupConfigs != null) {
			ArrayList<Integer> indexgroups = new ArrayList<>();
			for (Map.Entry<String, Object> group : groupConfigs.entrySet()) {
				String titleParent = group.getKey();
				Set<String> values = (Set<String>) group.getValue();
				ArrayList<Integer> index = new ArrayList<>();

				for (ColumnConfig column : columnConfs) {
					for (String id : values) {
						if (column.getName().equals(id)) {
							index.add(column.getCell().getColumnIndex());
							indexgroups.add(column.getCell().getColumnIndex());
							if (row == 0)
								row = column.getCell().getRowIndex();
						}
					}
				}
				if (index.size() >= 2) {
					sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1,
							index.get(0) < index.get(1) ? index.get(0) : index.get(1),
							index.get(0) > index.get(1) ? index.get(0) : index.get(1)));
					if (index.size() == 3)
						sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1,
								index.get(0) < (index.get(2) - 1) ? index.get(0) : (index.get(2) - 1),
								index.get(1) > index.get(2) ? index.get(1) : index.get(2)));
				}
				sheet.getRow(row - 1).getCell(index.get(0)).setCellValue(
						UtilProperties.getMessage(resource != null ? resource : resource_acc, titleParent, locale));
			}

			for (ColumnConfig column : columnConfs) {
				int colIndex = column.getCell().getColumnIndex();
				if (!indexgroups.contains(colIndex))
					if (row >= 1) {
						sheet.addMergedRegion(new CellRangeAddress(row - 1, row, colIndex, colIndex));
						sheet.getRow(row - 1).getCell(colIndex).setCellValue(column.getCell().getStringCellValue());
					}
			}

		}
	}

	protected void createContent(Sheet sheet, List<Map<String, Object>> data, Map<String, CellStyle> styles,
			List<ColumnConfig> columnConfs) {
		// Create Data
		if (data == null)
			return;
		for (Map<String, Object> row : data) {
			Row rowData = sheet.createRow(rownum++);
			rowData.setHeight((short) 350);
			int colIndex = 0;
			for (ColumnConfig column : columnConfs) {
				/* sheet.autoSizeColumn(colIndex); */
				sheet.setColumnWidth(colIndex, column.getWidth());
				Cell cellData = rowData.createCell(colIndex);
				if (column.getDataType() != null) {
					switch (column.getDataType()) {
					case STRING:
						if (row.get(column.getName()) != null) {
							if (column.getFieldDescribe() != null & column.getEntityName() != null) {
								try {
									cellData.setCellValue((String) column.getValueDescribe(column.getFieldDescribe(),
											row.get(column.getName()).toString()));
								} catch (GenericEntityException e) {
									e.printStackTrace();
								}
							} else
								cellData.setCellValue(row.get(column.getName()).toString());
						} else
							cellData.setCellValue("");

						if (row.containsKey("parent")) {
							CellStyle sl = (CellStyle) styles.get("cell_bold_centered_special_no_border_left_11");
							cellData.setCellStyle(sl);
						} else
							cellData.setCellStyle(styles.get("cell_border_left_top"));
						break;
					case NUMBERIC:
						cellData.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cellData.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
						if (row.get(column.getName()) != null) {
							cellData.setCellValue(Double.parseDouble(row.get(column.getName()).toString()));
						} else
							cellData.setCellValue("0");
						break;
					default:
						break;
					}
				}

				colIndex++;
			}
		}
	}

	protected void createPicture(Workbook wb, Sheet sheet) {
		FileInputStream is = null;
		try {
			String imageServerPath = FlexibleStringExpander
					.expandString(UtilProperties.getPropertyValue("dms", "image.management.logoPath"), null);
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
			rownum += 1;
		} catch (Exception e) {
			Debug.log(e.getMessage());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	protected CellStyle createNonBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		return style;
	}

	protected CellStyle createBorderedThinStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}

	protected CellStyle createBorderedStyle(Workbook wb) {
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

	protected CellStyle createBorderedBlueStyle(Workbook wb) {
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

	protected Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		DataFormat df = wb.createDataFormat();

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
		_style.setFont(fontHeaderExcel);
		_style.setBorderBottom(CellStyle.BORDER_THIN);
		_style.setBorderTop(CellStyle.BORDER_THIN);
		_style.setBorderRight(CellStyle.BORDER_THIN);
		_style.setBorderLeft(CellStyle.BORDER_THIN);
		/*
		 * _style.setFillForegroundColor(((new
		 * HSSFWorkbook()).getCustomPalette().findSimilarColor((byte)
		 * 67,(byte)142,(byte)185)).getIndex());
		 */
		_style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
		_style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put("cell_bold_centered_header_excel_no_border_left_11", _style);

		style = createNonBorderedStyle(wb);
		Font boldCenterNoBorderFont16 = wb.createFont();
		boldCenterNoBorderFont16.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCenterNoBorderFont16.setFontHeightInPoints((short) 16);
		style = createNonBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(boldCenterNoBorderFont16);
		styles.put("cell_bold_centered_no_border_16", style);

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
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
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

		/*
		 * Font normalCenterBorderTopFont10 = wb.createFont();
		 * normalCenterBorderTopFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		 * normalCenterBorderTopFont10.setFontHeightInPoints((short) 10); style
		 * = createBorderedStyle(wb);
		 * style.setAlignment(CellStyle.ALIGN_CENTER);
		 * style.setFont(normalCenterBorderTopFont11); style.setWrapText(true);
		 * style.setBorderTop(CellStyle.BORDER_DOUBLE);
		 * styles.put("cell_normal_centered_wrap_text_border_top_11", style);
		 */

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

		return styles;
	}
}
