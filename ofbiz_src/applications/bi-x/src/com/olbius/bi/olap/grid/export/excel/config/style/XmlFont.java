package com.olbius.bi.olap.grid.export.excel.config.style;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlFont extends XmlElement {
	
	private String color;

	public XmlFont(Element element, XmlElement parent) {
		super(element, parent);
		color = getAttributeValue("ss:Color");
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
