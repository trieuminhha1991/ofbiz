package com.olbius.bi.olap.grid.export.excel.config.sheet;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlHeader extends XmlElement{

	private Map<String, XmlCell> cells;
	
	public XmlHeader(Element element, XmlElement parent) {
		super(element, parent);
		for(Element e : getElements("Cell")) {
			XmlCell cell = new XmlCell(e, this);
			if(cell.getField() != null && !cell.getField().isEmpty()) {
				getCells().put(cell.getField(), cell);
			}
		}
	}

	public Map<String, XmlCell> getCells() {
		if(cells == null) {
			cells = new HashMap<>();
		}
		return cells;
	}
	
}
