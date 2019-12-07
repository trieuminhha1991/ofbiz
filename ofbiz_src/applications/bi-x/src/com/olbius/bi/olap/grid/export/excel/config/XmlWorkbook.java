package com.olbius.bi.olap.grid.export.excel.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.olbius.bi.olap.grid.export.XmlElement;
import com.olbius.bi.olap.grid.export.excel.config.sheet.XmlWorksheet;
import com.olbius.bi.olap.grid.export.excel.config.style.XmlStyle;

public class XmlWorkbook extends XmlElement {

	private Map<String, XmlStyle> styles;
	private List<XmlWorksheet> worksheets;
	
	public XmlWorkbook(Element element, XmlElement parent) {
		super(element, parent);
		init();
	}
	
	public XmlWorkbook(String xml) throws ParserConfigurationException, SAXException, IOException {
		super(null, null);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));
		Document doc = db.parse(is);
		setElement((Element) doc.getElementsByTagName("Workbook").item(0));
		init();
	}
	
	private void init() {
		if (element != null) {
			for (Element e : getElements("Styles")) {
				for (Element e2 : getElements(e, "Style")) {
					XmlStyle style = new XmlStyle(e2, this);
					if(style.getId() != null && !style.getId().isEmpty()) {
						getStyles().put(style.getId(), style);
					}
				}
			}
			for (Element e : getElements("Worksheet")) {
				getWorksheets().add(new XmlWorksheet(e, this));
			}
		}
	}

	public Map<String, XmlStyle> getStyles() {
		if (styles == null) {
			styles = new HashMap<>();
		}
		return styles;
	}

	public XmlStyle getStyle(String id) {
		return getStyles().get(id);
	}

	public List<XmlWorksheet> getWorksheets() {
		if(worksheets == null) {
			worksheets = new ArrayList<>();
		}
		return worksheets;
	}
	
}
