package com.olbius.bi.olap.grid.export.excel.config.sheet;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlColumn extends XmlElement{

	private String width;
	private String field;
	
	public XmlColumn(Element element, XmlElement parent) {
		super(element, parent);
		width = getAttributeValue("ss:Width");
		field = getAttributeValue("ss:Field");
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

}
