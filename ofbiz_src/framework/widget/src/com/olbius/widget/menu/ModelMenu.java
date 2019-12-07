package com.olbius.widget.menu;

import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class ModelMenu extends org.ofbiz.widget.menu.ModelMenu {
	protected String style;
	protected String iconStyle;
	protected String breakStyle;
	protected String id;
	protected String module;
	protected String moduleName;
	
	public ModelMenu(Element menuElement) {
        super(menuElement);
        
        if (this.style == null || menuElement.hasAttribute("style"))
            this.style = menuElement.getAttribute("style");
        if (this.iconStyle == null || menuElement.hasAttribute("iconStyle"))
            this.iconStyle = menuElement.getAttribute("iconStyle");
        if (this.breakStyle == null || menuElement.hasAttribute("breakStyle"))
            this.breakStyle = menuElement.getAttribute("breakStyle");
        if (this.id == null || menuElement.hasAttribute("id"))
            this.id = menuElement.getAttribute("id");
        if (this.module == null || menuElement.hasAttribute("module"))
            this.module = menuElement.getAttribute("module");
        if (this.moduleName == null || menuElement.hasAttribute("moduleName"))
            this.moduleName = menuElement.getAttribute("moduleName");
	}
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getIconStyle() {
		return iconStyle;
	}
	public void setIconStyle(String iconStyle) {
		this.iconStyle = iconStyle;
	}
	public String getBreakStyle() {
		return breakStyle;
	}
	public void setBreakStyle(String breakStyle) {
		this.breakStyle = breakStyle;
	}

}
