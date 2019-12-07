package com.olbius.bi.olap.grid.export.excel;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;

import com.olbius.bi.olap.grid.export.excel.config.sheet.XmlCell;
import com.olbius.bi.olap.grid.export.excel.config.sheet.XmlWorksheet;

public class OlbiusWorksheet {

	private OlbiusWorkbook workbook;
	private XmlWorksheet xmlWorksheet;
	private Sheet sheet;
	private int row;
	private int column;
	private String title;

	public OlbiusWorksheet(OlbiusWorkbook workbook, XmlWorksheet xmlWorksheet, String title) {
		this.workbook = workbook;
		this.xmlWorksheet = xmlWorksheet;
		this.row = xmlWorksheet.getInitRow();
		this.column = xmlWorksheet.getInitColum();
		this.title = title;
		createSheet();
		header();
	}

	public OlbiusWorkbook getWorkbook() {
		return workbook;
	}

	protected void createSheet() {
		sheet = workbook.getWorkbook().createSheet(xmlWorksheet.getName());
	}

	protected int getColumnIndex(String s) {
		if (xmlWorksheet.getColumnIndex().get(s) == null) {
			return -1;
		}
		return xmlWorksheet.getColumnIndex().get(s) + column;
	}

	protected void header() {
		Row row = sheet.createRow(this.row);
		
		sheet.addMergedRegion(new CellRangeAddress(this.row, this.row, this.column, xmlWorksheet.getColumns().size()-1));
		
		Cell cell = row.createCell(this.column);
		
		cell.setCellValue(this.title);
		
		this.row++;
		
		row = sheet.createRow(this.row++);
		
		for (String c : xmlWorksheet.getColumns().keySet()) {
			int col = getColumnIndex(c);
			if (col == -1) {
				continue;
			}
			sheet.setColumnWidth(col, Integer.parseInt(xmlWorksheet.getColumns().get(c).getWidth()) * 256 / 9);
			cell = row.createCell(col);
			cell.setCellValue(xmlWorksheet.getHeader().getCells().get(c).getData());
			cell.setCellStyle(workbook.getStyle(xmlWorksheet.getHeader().getCells().get(c).getStyleId()));
		}
	}

	public void addRow(Map<String, Object> data) {
		Row row = sheet.createRow(this.row);
		boolean flag = false;
		for (String c : data.keySet()) {
			int col = getColumnIndex(c);
			Object value = data.get(c);
			if (col == -1 || value == null) {
				continue;
			}
			flag = true;
			Cell cell = row.createCell(col);
			XmlCell cellConfig = xmlWorksheet.getBody().getCells().get(c);
			if(value instanceof BigDecimal) {
				cell.setCellValue(((BigDecimal)value).doubleValue());
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			} else if (value instanceof Date) {
				String format = XmlCell.CELL_FORMAT_DATE.get(cellConfig.getFormat());
				if (format == null) {
					format = cellConfig.getFormat();
				}
				DateFormat df = new SimpleDateFormat(format);
				cell.setCellValue(df.format(value));
				cell.setCellType(Cell.CELL_TYPE_STRING);
			} else {
				cell.setCellValue(value.toString());
				cell.setCellType(Cell.CELL_TYPE_STRING);
			}
			cell.setCellStyle(workbook.getStyle(xmlWorksheet.getBody().getCells().get(c).getStyleId()));
			
		}
		if (flag) {
			this.row++;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
