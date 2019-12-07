package com.olbius.bi.olap.grid.export.excel;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.grid.export.OlapExport;
import com.olbius.bi.olap.grid.export.excel.config.XmlWorkbook;
import com.olbius.bi.olap.grid.export.excel.config.sheet.XmlWorksheet;
import com.olbius.bi.olap.grid.export.excel.config.style.XmlAlignment;
import com.olbius.bi.olap.grid.export.excel.config.style.XmlBorder;
import com.olbius.bi.olap.grid.export.excel.config.style.XmlFont;
import com.olbius.bi.olap.grid.export.excel.config.style.XmlInterior;

public class OlbiusWorkbook implements OlapExport {

	private XmlWorkbook xmlWorkbook;
	private HSSFWorkbook workbook;
	private Map<String, CellStyle> styles;
	private List<OlbiusWorksheet> worksheets;
	private String title;
	
	public OlbiusWorkbook(XmlWorkbook xmlWorkbook, String title) {
		this.xmlWorkbook = xmlWorkbook;
		this.workbook = new HSSFWorkbook();
		this.title = title;
		createStyle();
		createSheet();
	}

	public Map<String, CellStyle> getStyles() {
		if (styles == null) {
			styles = new HashMap<>();
		}
		return styles;
	}
	
	public CellStyle getStyle(String name) {
		return getStyles().get(name);
	}

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public List<OlbiusWorksheet> getWorksheets() {
		if (worksheets == null) {
			worksheets = new ArrayList<>();
		}
		return worksheets;
	}

	protected void createSheet() {
		for (XmlWorksheet ws : xmlWorkbook.getWorksheets()) {
			getWorksheets().add(new OlbiusWorksheet(this, ws, this.title));
		}
	}
	
	protected void createStyle() {
		for (String s : xmlWorkbook.getStyles().keySet()) {
			CellStyle style = workbook.createCellStyle();
			getStyles().put(s, style);
			style.setFont(createFont(xmlWorkbook.getStyle(s).getFont()));
			setAlignment(style, xmlWorkbook.getStyle(s).getAlignment());
			setInterior(style, xmlWorkbook.getStyle(s).getInterior());
			setBoder(style, xmlWorkbook.getStyle(s).getBorder());
		}
	}

	protected Font createFont(XmlFont xmlFont) {
		Font font = workbook.createFont();
		font.setColor(getColor(xmlFont.getColor()));
		return font;
	}

	protected void setAlignment(CellStyle cellStyle, XmlAlignment xmlAlignment) {
		if (xmlAlignment.getVertical() != null && !xmlAlignment.getVertical().isEmpty()) {
			switch (xmlAlignment.getVertical()) {
				case "Justify":
					cellStyle.setVerticalAlignment(CellStyle.VERTICAL_JUSTIFY);
					break;
				case "Center":
					cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
					break;
				case "Bottom":
					cellStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
					break;
				case "Top":
					cellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
					break;
				default:
					break;
			}
		}

		if (xmlAlignment.getHorizontal() != null && !xmlAlignment.getHorizontal().isEmpty()) {
			switch (xmlAlignment.getHorizontal()) {
				case "Justify":
					cellStyle.setAlignment(CellStyle.ALIGN_JUSTIFY);
					break;
				case "Center":
					cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
					break;
				case "General":
					cellStyle.setAlignment(CellStyle.ALIGN_GENERAL);
					break;
				case "Left":
					cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
					break;
				case "Right":
					cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
					break;
				case "Fill":
					cellStyle.setAlignment(CellStyle.ALIGN_FILL);
					break;
				default:
					break;
			}
		}
	}
	
	protected void setInterior(CellStyle cellStyle, XmlInterior xmlInterior) {
		switch (xmlInterior.getPattern()) {
			case "Solid":
				cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cellStyle.setFillForegroundColor(getColor(xmlInterior.getColor()));
				break;
			default:
				break;
		}
	}
	
	protected void setBoder(CellStyle cellStyle, XmlBorder xmlBorder) {
		if(xmlBorder.getBottom() != null) {
			cellStyle.setBottomBorderColor(getColor(xmlBorder.getBottom().getColor()));
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		}
		if(xmlBorder.getTop() != null) {
			cellStyle.setTopBorderColor(getColor(xmlBorder.getTop().getColor()));
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		}
		if(xmlBorder.getLeft() != null) {
			cellStyle.setLeftBorderColor(getColor(xmlBorder.getLeft().getColor()));
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		}
		if(xmlBorder.getRight() != null) {
			cellStyle.setRightBorderColor(getColor(xmlBorder.getRight().getColor()));
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		}
	}

	public short getColor(String color) {
		Color tmp = Color.decode(color);
		HSSFPalette palette = workbook.getCustomPalette();
		HSSFColor hssfColor = palette.findSimilarColor(tmp.getRed(), tmp.getGreen(), tmp.getGreen());
		return hssfColor.getIndex();
	}

	@Override
	public void addData(Map<String, Object> map) {
		if(!getWorksheets().isEmpty()) {
			getWorksheets().get(0).addRow(map);
		}
	}

	@Override
	public OutputStream getOutputStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			this.workbook.write(out);
			return out;
		} catch (IOException e) {
			Debug.logError(e, OlbiusWorkbook.class.getName());
			return null;
		}
	}

}
