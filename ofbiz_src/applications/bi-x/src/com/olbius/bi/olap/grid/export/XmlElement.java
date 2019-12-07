package com.olbius.bi.olap.grid.export;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlElement {
	
	protected Element element;
	protected XmlElement parent;
	
	public XmlElement(Element element, XmlElement parent) {
		this.element = element;
		this.parent = parent;
	}
	
	protected void setElement(Element element) {
		this.element = element;
	}
	
	protected List<Element> getElements(String tag) {
		return getElements(element, tag);
	}
	
	protected List<Element> getElements(Element element, String tag) {
		List<Element> elements = new ArrayList<Element>();
		if(element != null) {
			NodeList nodes = element.getElementsByTagName(tag);
			for (int i = 0; i < nodes.getLength(); i++) {
				Element e = (Element) nodes.item(i);
				elements.add(e);
			}
		}
		return elements;
	}
	
	protected String getElementValue() {
		return getElementValue(element);
	}
	
	protected String getElementValue(Element element) {
		if(element != null) {
			return element.getFirstChild().getNodeValue();
		} else {
			return null;
		}
	}
	
	protected String getAttributeValue(String attribute) {
		return getAttributeValue(element, attribute);
	}
	
	protected String getAttributeValue(Element element, String attribute) {
		if(element != null) {
			return element.getAttributes().getNamedItem(attribute).getNodeValue();
		} else {
			return null;
		}
	}
	
	protected XmlElement create(Class<? extends XmlElement> type, String tag, XmlElement parent) {
		List<Element> tmp = getElements(tag);
		if(!tmp.isEmpty()) {
			try {
				return type.getConstructor(Element.class, XmlElement.class).newInstance(tmp.get(0), parent);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public XmlElement getParent() {
		return parent;
	}
}
