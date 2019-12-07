package com.olbius.widget.menu;

import org.w3c.dom.Element;

public class ModelMenuFactory {
	public static enum ModelMenuEnum{ofbiz, olbius, olbiustab, olbiustabInner, olbiusbutton, olbiusScreenletMenu, olbiusSubMenu, olbiusInnerAdvanceMenu, businessMenu, businessSubMenu, megaMenu, megaSubMenu, appSubMenu}
	public static org.ofbiz.widget.menu.ModelMenu createModelMenu(Element ele){
		String modelMenu = ele.getAttribute("modelMenu");
		ModelMenuEnum mmEnum = ModelMenuEnum.valueOf(modelMenu);
		org.ofbiz.widget.menu.ModelMenu mm;
		switch(mmEnum){
		 case ofbiz:{
			 mm = new org.ofbiz.widget.menu.ModelMenu(ele);
			 break;
		 }
		 case olbius:
		 case businessMenu:
		 case businessSubMenu:
		 case megaMenu:
		 case megaSubMenu:
		 case appSubMenu:
		 case olbiusInnerAdvanceMenu:
		 case olbiusSubMenu:{
			 mm = new com.olbius.widget.menu.ModelMenu(ele);
			 break;
		 }		
		 default:{
			 mm = new org.ofbiz.widget.menu.ModelMenu(ele);
		 }
		}
		return mm;
	}
}
