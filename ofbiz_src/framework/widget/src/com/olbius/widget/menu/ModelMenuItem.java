package com.olbius.widget.menu;

import org.ofbiz.widget.menu.ModelMenu;
import org.w3c.dom.Element;

public class ModelMenuItem extends org.ofbiz.widget.menu.ModelMenuItem{
	
	protected String style;
	
	public ModelMenuItem(Element fieldElement, ModelMenu modelMenu) {
		super(fieldElement, modelMenu);
		 if (this.style == null || fieldElement.hasAttribute("style"))
	            this.style = fieldElement.getAttribute("style");
	}
	
	public ModelMenuItem(Element fieldElement, org.ofbiz.widget.menu.ModelMenuItem modelMenuItem) {
		super(fieldElement, modelMenuItem);
		if (this.style == null || fieldElement.hasAttribute("style"))
            this.style = fieldElement.getAttribute("style");
	}
	
	public ModelMenuItem(org.ofbiz.widget.menu.ModelMenu modelMenu) {
		super(modelMenu);
	}
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
}
