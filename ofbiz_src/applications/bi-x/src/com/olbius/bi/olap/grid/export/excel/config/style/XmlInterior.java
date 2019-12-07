package com.olbius.bi.olap.grid.export.excel.config.style;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlInterior extends XmlElement{

	private String color;
	private String pattern;
	
	public XmlInterior(Element element, XmlElement parent) {
		super(element, parent);
		color = getAttributeValue("ss:Color");
		pattern = getAttributeValue("ss:Pattern");
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}
