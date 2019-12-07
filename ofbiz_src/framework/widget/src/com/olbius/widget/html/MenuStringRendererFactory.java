package com.olbius.widget.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.widget.html.HtmlMenuRenderer;
import org.ofbiz.widget.menu.MenuStringRenderer;

import com.olbius.widget.menu.ModelMenuFactory.ModelMenuEnum;

public class MenuStringRendererFactory {
	public static MenuStringRenderer create(HttpServletRequest request, HttpServletResponse response, ModelMenuEnum mmEnum){
		switch(mmEnum){
		 case ofbiz:{
			 return new HtmlMenuRenderer(request, response);
		 }
		 case olbius:{
			 return new com.olbius.widget.html.HtmlMenuRenderer(request, response);
		 }
		 case olbiustabInner:{
			 return new com.olbius.widget.html.HtmlTabInnerMenuRenderer(request, response);
		 }
		 case olbiustab:{
			 return new com.olbius.widget.html.HtmlTabMenuRenderer(request, response);
		 }
		 case olbiusbutton:{
			 return new com.olbius.widget.html.HtmlButtonMenuRenderer(request, response);
		 }
		 case olbiusInnerAdvanceMenu: {
			 return new com.olbius.widget.html.HtmlInnerAdvanceMenuRenderer(request, response);
		 }
		 case olbiusSubMenu:{
			 return new com.olbius.widget.html.HtmlSubMenuRenderer(request, response);
		 }
		 case olbiusScreenletMenu:{
			 return new com.olbius.widget.html.HtmlScreenletMenuRenderer(request, response);
		 }
		 case businessMenu:{
			 return new com.olbius.widget.html.HtmlBusinessMenuRenderer(request, response);
		 }
		 case businessSubMenu:{
			 return new com.olbius.widget.html.HtmlBusinessSubMenuRenderer(request, response);
		 }
		 case megaMenu:{
			 return new com.olbius.widget.html.HtmlMegaMenuRenderer(request, response);
		 }
		 case megaSubMenu:{
			 return new com.olbius.widget.html.HtmlMegaSubMenuRenderer(request, response);
		 }
		 case appSubMenu:{
			 return new com.olbius.widget.html.HtmlAppSubMenuRenderer(request, response);
		 }
		 default:{
			 return new HtmlMenuRenderer(request, response);
		 }
		}
	}
}
