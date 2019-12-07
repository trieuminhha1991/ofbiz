package com.olbius.bi.olap.grid.export.excel.config.style;

import org.w3c.dom.Element;

import com.olbius.bi.olap.grid.export.XmlElement;

public class XmlBorder extends XmlElement {

	private BoderElement top;
	private BoderElement left;
	private BoderElement right;
	private BoderElement bottom;

	public XmlBorder(Element element, XmlElement parent) {
		super(element, parent);
		for (Element e : getElements(element, "Border")) {
			String position = getAttributeValue(e, "ss:Position");
			switch (position) {
				case "Bottom":
					bottom = new BoderElement();
					bottom.setColor(getAttributeValue(e, "ss:Color"));
					bottom.setLineStyle(getAttributeValue(e, "ss:LineStyle"));
					bottom.setWeight(getAttributeValue(e, "ss:Weight"));
					break;
				case "Left":
					left = new BoderElement();
					left.setColor(getAttributeValue(e, "ss:Color"));
					left.setLineStyle(getAttributeValue(e, "ss:LineStyle"));
					left.setWeight(getAttributeValue(e, "ss:Weight"));
					break;
				case "Right":
					right = new BoderElement();
					right.setColor(getAttributeValue(e, "ss:Color"));
					right.setLineStyle(getAttributeValue(e, "ss:LineStyle"));
					right.setWeight(getAttributeValue(e, "ss:Weight"));
					break;
				case "Top":
					top = new BoderElement();
					top.setColor(getAttributeValue(e, "ss:Color"));
					top.setLineStyle(getAttributeValue(e, "ss:LineStyle"));
					top.setWeight(getAttributeValue(e, "ss:Weight"));
					break;
				default:
					break;
			}
		}
	}

	public BoderElement getTop() {
		return top;
	}

	public BoderElement getLeft() {
		return left;
	}

	public BoderElement getRight() {
		return right;
	}

	public BoderElement getBottom() {
		return bottom;
	}

	public static class BoderElement {

		private String lineStyle;
		private String weight;
		private String color;

		public String getLineStyle() {
			return lineStyle;
		}

		public void setLineStyle(String lineStyle) {
			this.lineStyle = lineStyle;
		}

		public String getWeight() {
			return weight;
		}

		public void setWeight(String weight) {
			this.weight = weight;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

	}

}
