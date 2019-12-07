package com.olbius.bi.olap.grid.export.excel.config.sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlWorksheet extends XmlElement {

	private String name;
	private XmlHeader header;
	private XmlBody body;
	private Map<String, XmlColumn> columns;
	private Map<String, Integer> columnIndex;
	private int initRow = 0;
	private int initColum = 0;

	public XmlWorksheet(Element element, XmlElement parent) {
		super(element, parent);
		name = getAttributeValue("ss:Name");
		int index = 0;
		for (Element e : getElements("Table")) {
			for (Element c : getElements(e, "Column")) {
				XmlColumn column = new XmlColumn(c, this);
				if (column.getField() != null && !column.getField().isEmpty()) {
					getColumns().put(column.getField(), column);
					getColumnIndex().put(column.getField(), index++);
				}
			}
			List<Element> tmp = getElements(e, "Row");
			if (tmp.size() == 2) {
				header = new XmlHeader(tmp.get(0), this);
				body = new XmlBody(tmp.get(1), this);
			}
			break;
		}
	}

	public Map<String, XmlColumn> getColumns() {
		if (columns == null) {
			columns = new HashMap<>();
		}
		return columns;
	}

	public Map<String, Integer> getColumnIndex() {
		if (columnIndex == null) {
			columnIndex = new HashMap<>();
		}
		return columnIndex;
	}

	public XmlHeader getHeader() {
		return header;
	}

	public XmlBody getBody() {
		return body;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInitIndex(int column, int row) {
		this.initColum = column;
		this.initRow = row;
	}

	public int getInitRow() {
		return initRow;
	}

	public int getInitColum() {
		return initColum;
	}
	
}
