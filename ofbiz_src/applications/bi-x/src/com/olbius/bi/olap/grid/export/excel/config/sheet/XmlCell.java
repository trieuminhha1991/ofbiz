package com.olbius.bi.olap.grid.export.excel.config.sheet;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlCell extends XmlElement{

	public static final Map<String, String> CELL_FORMAT_DATE = new HashMap<String, String>();
	
	static {
		CELL_FORMAT_DATE.put("d", "M/d/yyyy");
		CELL_FORMAT_DATE.put("D", "dddd, MMMM dd, yyyy");
		CELL_FORMAT_DATE.put("t", "h:mm tt");
		CELL_FORMAT_DATE.put("T", "h:mm:ss tt");
		CELL_FORMAT_DATE.put("f", "dddd, MMMM dd, yyyy h:mm tt");
		CELL_FORMAT_DATE.put("F", "dddd, MMMM dd, yyyy h:mm:ss tt");
		CELL_FORMAT_DATE.put("M", "MMMM dd");
		CELL_FORMAT_DATE.put("Y", "yyyy MMMM");
		CELL_FORMAT_DATE.put("S", "yyyy\u0027-\u0027MM\u0027-\u0027dd\u0027T\u0027HH\u0027:\u0027mm\u0027:\u0027ss");
	}
	
	private String styleId;
	private String type;
	private String field;
	private String data;
	private String format;
	
	public XmlCell(Element element, XmlElement parent) {
		super(element, parent);
		styleId = getAttributeValue("ss:StyleID");
		for(Element e : getElements("Data")) {
			type = getAttributeValue(e, "ss:Type");
			field = getAttributeValue(e, "ss:Field");
			format = getAttributeValue(e, "ss:Format");
			data = getElementValue(e);
			break;
		}
	}

	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
