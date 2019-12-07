package com.olbius.bi.olap.grid.export.excel.config.style;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlAlignment extends XmlElement {
	
	private String vertical;
	private String horizontal;

	public XmlAlignment(Element element, XmlElement parent) {
		super(element, parent);
		vertical = getAttributeValue("ss:Vertical");
		horizontal = getAttributeValue("ss:Horizontal");
	}

	public String getVertical() {
		return vertical;
	}

	public void setVertical(String vertical) {
		this.vertical = vertical;
	}

	public String getHorizontal() {
		return horizontal;
	}

	public void setHorizontal(String horizontal) {
		this.horizontal = horizontal;
	}

}
