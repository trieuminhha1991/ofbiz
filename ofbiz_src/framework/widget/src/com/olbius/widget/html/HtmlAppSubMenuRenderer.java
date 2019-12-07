package com.olbius.widget.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HtmlAppSubMenuRenderer extends HtmlBusinessMenuRenderer{

	public HtmlAppSubMenuRenderer(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}
	
	/*public void renderMenuItem(Appendable writer, Map<String, Object> context,
			ModelMenuItem menuItem) throws IOException {
		com.olbius.widget.menu.ModelMenuItem olbiusMenuItem = (com.olbius.widget.menu.ModelMenuItem)menuItem;
		// Debug.logInfo("in renderMenuItem, menuItem:" + menuItem.getName() +
		// " context:" + context ,"");
		boolean hideThisItem = isHideIfSelected(menuItem, context);
		// if (Debug.infoOn())
		// Debug.logInfo("in HtmlMenuRendererImage, hideThisItem:" +
		// hideThisItem,"");
		if (hideThisItem)
			return;
		
		String txt = menuItem.getTitle(context);
		String widgetStyle = menuItem.getWidgetStyle();
		String style = olbiusMenuItem.getStyle();
		
		Element subMenuElement = menuItem.getSubMenuElement();
		Link link = menuItem.getLink();
		if (subMenuElement != null){
			if (menuItem.isSelected(context)) {
				context.put("subMenuOpen", "Y");
				context.put("selectedBizMenuItemTitle", txt);
				writer.append("<li class=\"open active " + style + "\"><a href=\"#\" class=\"dropdown-toggle\">");
				writer.append("<i class=\""+widgetStyle+"\"></i>");
				writer.append("<span class=\"menu-text\">");
				writer.append(txt);
				writer.append("</span>");
			} else {
				context.put("subMenuOpen", "N");
				writer.append("<li class=\"" + style + "\"><a href=\"#\" class=\"dropdown-toggle\">");
				writer.append("<i class=\""+widgetStyle+"\"></i>");
				writer.append("<span class=\"menu-text\">");
				writer.append(txt);
				writer.append("</span>");
			}
			writer.append("<b class=\"arrow icon-angle-down\"></b></a>");
		}else{
			if (menuItem.isSelected(context)) {
				context.put("selectedBizMenuItemTitle", txt);
				writer.append("<li class=\"open active " + style +">");
			} else {
				writer.append("<li class=\"" + style + "\">");
			}
			if (link != null) {
				renderLink(writer, context, link);
			} else {
				writer.append("<i class=\""+widgetStyle+"\"></i>");
				writer.append("<span class=\"menu-text\">");
				writer.append(txt);
				writer.append("</span>");
			}
		}
		
		if (subMenuElement != null){
			renderSubMenu(writer, context, subMenuElement);
		}
		writer.append("</li>");
		appendWhitespace(writer);
	}*/
}
