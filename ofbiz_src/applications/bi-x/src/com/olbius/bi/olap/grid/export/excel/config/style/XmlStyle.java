package com.olbius.bi.olap.grid.export.excel.config.style;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlStyle extends XmlElement {

	private String id;
	private String name;
	private XmlAlignment alignment;
	private XmlBorder border;
	private XmlFont font;
	private XmlInterior interior;

	public XmlStyle(Element element, XmlElement parent) {
		super(element, parent);
		id = getAttributeValue("ss:ID");
		name = getAttributeValue("ss:Name");
		alignment = (XmlAlignment) create(XmlAlignment.class, "Alignment", this);
		border = (XmlBorder) create(XmlBorder.class, "Borders", this);
		font = (XmlFont) create(XmlFont.class, "Font", this);
		interior = (XmlInterior) create(XmlInterior.class, "Interior", this);
	}

	public XmlAlignment getAlignment() {
		return alignment;
	}

	public XmlBorder getBorder() {
		return border;
	}

	public XmlFont getFont() {
		return font;
	}

	public XmlInterior getInterior() {
		return interior;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	

}
