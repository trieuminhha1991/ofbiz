package com.olbius.widget.menu;

import org.ofbiz.widget.menu.ModelMenu;
import org.ofbiz.widget.menu.ModelMenuItem;
import org.w3c.dom.Element;

import com.olbius.widget.menu.ModelMenuFactory.ModelMenuEnum;

public class ModelMenuItemFactory {
	
	public static org.ofbiz.widget.menu.ModelMenuItem createModelMenuItem(Element ele, ModelMenu modelMenu){
		String modelMenuStr = modelMenu.getModelMenuName();
		ModelMenuEnum mmEnum = ModelMenuEnum.valueOf(modelMenuStr);
		org.ofbiz.widget.menu.ModelMenuItem mm;
		switch(mmEnum){
		 case ofbiz:{
			 mm = new org.ofbiz.widget.menu.ModelMenuItem(ele, modelMenu);
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
			 mm = new com.olbius.widget.menu.ModelMenuItem(ele, modelMenu);
			 break;
		 }		
		 default:{
			 mm = new org.ofbiz.widget.menu.ModelMenuItem(ele, modelMenu);
			 break;
		 }
		}
		return mm;
	}
	
	public static org.ofbiz.widget.menu.ModelMenuItem createModelMenuItem(Element ele, ModelMenuItem modelMenuItem){
		String modelMenuStr = modelMenuItem.getModelMenu().getModelMenuName();
		ModelMenuEnum mmEnum = ModelMenuEnum.valueOf(modelMenuStr);
		org.ofbiz.widget.menu.ModelMenuItem mm;
		switch(mmEnum){
		 case ofbiz:{
			 mm = new org.ofbiz.widget.menu.ModelMenuItem(ele, modelMenuItem);
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
			 mm = new com.olbius.widget.menu.ModelMenuItem(ele, modelMenuItem);
			 break;
		 }		
		 default:{
			 mm = new org.ofbiz.widget.menu.ModelMenuItem(ele, modelMenuItem);
			 break;
		 }
		}
		return mm;
	}
	
	
	public static org.ofbiz.widget.menu.ModelMenuItem createModelMenuItem(ModelMenu modelMenu){
		String modelMenuStr = modelMenu.getModelMenuName();
		ModelMenuEnum mmEnum = ModelMenuEnum.valueOf(modelMenuStr);
		org.ofbiz.widget.menu.ModelMenuItem mm;
		switch(mmEnum){
		 case ofbiz:{
			 mm = new org.ofbiz.widget.menu.ModelMenuItem(modelMenu);
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
			 mm = new com.olbius.widget.menu.ModelMenuItem(modelMenu);
			 break;
		 }		
		 default:{
			 mm = new org.ofbiz.widget.menu.ModelMenuItem(modelMenu);
			 break;
		 }
		}
		return mm;
	}
}
