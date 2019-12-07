package com.olbius.widget.html;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilCodec.SimpleEncoder;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.widget.ModelWidget;
import org.ofbiz.widget.WidgetWorker;
import org.ofbiz.widget.menu.MenuFactory;
import org.ofbiz.widget.menu.ModelMenu;
import org.ofbiz.widget.menu.ModelMenuItem;
import org.ofbiz.widget.menu.ModelMenuItem.Image;
import org.ofbiz.widget.menu.ModelMenuItem.Link;

import com.olbius.widget.menu.ModelMenuFactory.ModelMenuEnum;

public class HtmlBusinessSubMenuRenderer extends org.ofbiz.widget.html.HtmlMenuRenderer {
	public HtmlBusinessSubMenuRenderer(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	@Override
	public void renderMenuOpen(Appendable writer, Map<String, Object> context,
			ModelMenu modelMenu) throws IOException {
		if (!userLoginIdHasChanged) {
			userLoginIdHasChanged = userLoginIdHasChanged();
		}

		// Debug.logInfo("in HtmlMenuRenderer, userLoginIdHasChanged:" +
		// userLoginIdHasChanged,"");
		this.widgetCommentsEnabled = ModelWidget
				.widgetBoundaryCommentsEnabled(context);
		renderBeginningBoundaryComment(writer, "Menu Widget", modelMenu);
		if ("Y".equals(context.get("subMenuOpen"))){
			writer.append("<ul class=\"submenu\" style=\"display: block;\">");
		}else{
			writer.append("<ul class=\"submenu\" style=\"display: none;\">");
		}
		appendWhitespace(writer);
	}

	@Override
	public void renderMenuClose(Appendable writer, Map<String, Object> context,
			ModelMenu modelMenu) throws IOException {
		if (modelMenu.renderedMenuItemCount(context) > 0) {
			writer.append(" </ul>");
			appendWhitespace(writer);
		}
		renderEndingBoundaryComment(writer, "Menu Widget", modelMenu);

		userLoginIdHasChanged = userLoginIdHasChanged();
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		if (userLogin != null) {
			String userLoginId = userLogin.getString("userLoginId");
			// request.getSession().setAttribute("userLoginIdAtPermGrant",
			// userLoginId);
			setUserLoginIdAtPermGrant(userLoginId);
			// Debug.logInfo("in HtmlMenuRenderer, userLoginId(Close):" +
			// userLoginId + " userLoginIdAtPermGrant:" +
			// request.getSession().getAttribute("userLoginIdAtPermGrant"),"");
		} else {
			request.getSession().setAttribute("userLoginIdAtPermGrant", null);
		}
	}

	@Override
	public void renderMenuItem(Appendable writer, Map<String, Object> context,
			ModelMenuItem menuItem) throws IOException {
		// Debug.logInfo("in renderMenuItem, menuItem:" + menuItem.getName() +
		// " context:" + context ,"");
		boolean hideThisItem = isHideIfSelected(menuItem, context);
		// if (Debug.infoOn())
		// Debug.logInfo("in HtmlMenuRendererImage, hideThisItem:" +
		// hideThisItem,"");
		if (hideThisItem)
			return;
		
		String txt = menuItem.getTitle(context);
		
		Element subMenuElement = menuItem.getSubMenuElement();
		Link link = menuItem.getLink();
		if (subMenuElement != null){
			if (menuItem.isSelected(context)) {
				context.put("selectedBizSubMenuItemTitle", txt);
				writer.append("<li class=\"open\"><a href=\"#\" class=\"dropdown-toggle\"><i class=\"icon-double-angle-right\"></i>");
				if (link != null) {
					renderLink(writer, context, link);
				} else {
					writer.append(txt);
				}
			} else {
				writer.append("<li><a href=\"#\" class=\"dropdown-toggle\"><i class=\"icon-double-angle-right\"></i>");
				if (link != null) {
					renderLink(writer, context, link);
				} else {
					writer.append(txt);
				}
			}
			writer.append("<b class=\"arrow icon-angle-down\"></b></a>");
		}else{
			if (menuItem.isSelected(context)) {
				context.put("selectedBizSubMenuItemTitle", txt);
				writer.append("<li class=\"active\">");
			} else {
				writer.append("<li>");
			}
			if (link != null) {
				renderLink(writer, context, link);
			} else {
				writer.append(txt);
			}
		}
		
		if (subMenuElement != null){
			renderSubMenu(writer, context, subMenuElement);
		}
		writer.append("</li>");
		appendWhitespace(writer);
	}

	@Override
	public void renderLink(Appendable writer, Map<String, Object> context,
			ModelMenuItem.Link link) throws IOException {
		String target = link.getTarget(context);
		ModelMenuItem menuItem = link.getLinkMenuItem();
		if (menuItem.getDisabled() || isDisableIfEmpty(menuItem, context)) {
			target = null;
		}
		String linkType = WidgetWorker.determineAutoLinkType(
				link.getLinkType(), target, link.getUrlMode(), request);
		String uniqueItemName = menuItem.getModelMenu().getName()
				+ "_"
				+ menuItem.getName()
				+ "_LF_"
				+ UtilMisc.<String> addToBigDecimalInMap(context,
						"menuUniqueItemIndex", BigDecimal.ONE);
		if (UtilValidate.isNotEmpty(target)) {
			if ("hidden-form".equals(linkType)) {
				writer.append("<form style=\"display:none;\" method=\"post\"");
				writer.append(" action=\"");
				// note that this passes null for the parameterList on purpose
				// so they won't be put into the URL
				writer.append(target);
				writer.append("\"");

//				if (UtilValidate.isNotEmpty(target)) {
//					writer.append(" target=\"");
//					writer.append(target);
//					writer.append("\"");
//				}

				writer.append(" name=\"");
				writer.append(uniqueItemName);
				writer.append("\">");

				SimpleEncoder simpleEncoder = (SimpleEncoder) context
						.get("simpleEncoder");
				for (Map.Entry<String, String> parameter : link
						.getParameterMap(context).entrySet()) {
					writer.append("<input name=\"");
					writer.append(parameter.getKey());
					writer.append("\" value=\"");
					if (simpleEncoder != null) {
						writer.append(simpleEncoder.encode(parameter.getValue()));
					} else {
						writer.append(parameter.getValue());
					}
					writer.append("\" type=\"hidden\"/>");
				}

				writer.append("</form>");
			}
			writer.append("<a ");
			// check widget-style(class css)
			String strStyle = link.getStyle(context);
			if(strStyle != null && !strStyle.isEmpty()){
				writer.append("class='" + strStyle +"' ");
			}
            // Assign Id for a tag
 			String id = link.getId(context);
 			if (UtilValidate.isNotEmpty(id)) {
 				writer.append(" id=\"");
 				writer.append(id);
 				writer.append("\"");
 			}
 			// Assign Name for a tag
			String name = link.getName(context);
			if (UtilValidate.isNotEmpty(name)) {
				writer.append(" name=\"");
				writer.append(name);
				writer.append("\"");
			}
			// Assign target for a tag
//			if (!"hidden-form".equals(linkType)) {
//				if (UtilValidate.isNotEmpty(target)) {
//					writer.append(" target=\"");
//					writer.append(target);
//					writer.append("\"");
//				}
//			}
			// begin assign link
			writer.append(" href=\"");
			
			String confirmationMsg = link.getConfirmation(context);
            if ("hidden-form".equals(linkType)) {
                if (UtilValidate.isNotEmpty(confirmationMsg)) {
                    writer.append("javascript:confirmActionFormLink('");
                    writer.append(confirmationMsg);
                    writer.append("', '");
                    writer.append(uniqueItemName);
                    writer.append("')\"");
                    writer.append(">");
                } else {
                    writer.append("javascript:document.");
                    writer.append(uniqueItemName);
                    writer.append(".submit()\"");
                    writer.append(">");
                    
                }
            } else {
            	if (UtilValidate.isNotEmpty(confirmationMsg)) {
                    writer.append("javascript:confirmActionLink('");
                    writer.append(confirmationMsg);
                    writer.append("', '");
                    WidgetWorker.buildHyperlinkUrl(writer, target, link.getUrlMode(), link.getParameterMap(context), link.getPrefix(context),
                            link.getFullPath(), link.getSecure(), link.getEncode(), request, response, context);
                    writer.append("')");
                } else {
                WidgetWorker.buildHyperlinkUrl(writer, target, link.getUrlMode(), link.getParameterMap(context), link.getPrefix(context),
                        link.getFullPath(), link.getSecure(), link.getEncode(), request, response, context);
                }
    			writer.append("\">");
            }
            // Text or Image
            Image img = link.getImage();
            if (img != null) {
                renderImage(writer, context, img);
                writer.append("&nbsp;" + link.getText(context));
            } else {
            	// Icon
    			writer.append("<i class=\"icon-double-angle-right\"></i>");
    			writer.append(link.getText(context));
            }
            writer.append("</a>");
		}

	}
	
	public void renderSubMenu(Appendable writer, Map<String, Object> context,Element subMenuElement){
		ModelMenu subMenu;
		if (subMenuElement != null) {
            String subMenuLocation = subMenuElement.getAttribute("location");
            String subMenuName = subMenuElement.getAttribute("name");
            try {
                subMenu = MenuFactory.getMenuFromLocation(subMenuLocation, subMenuName);
                if (subMenu != null){
                	subMenu.renderMenuString(writer, context, MenuStringRendererFactory.create(request, response, ModelMenuEnum.valueOf(subMenu.getModelMenuName())));
                }
            } catch (IOException e) {
                String errMsg = "Error getting subMenu [" + subMenuName + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (SAXException e2) {
                String errMsg = "Error getting subMenu in menu [" + subMenuName + "]: " + e2.toString();
                Debug.logError(e2, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (ParserConfigurationException e3) {
                String errMsg = "Error getting subMenu in menu [" + subMenuName + "]: " + e3.toString();
                Debug.logError(e3, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
	}

	@Override
	public void renderFormatSimpleWrapperRows(Appendable writer,
			Map<String, Object> context, Object menuObj) throws IOException {
		// do nothing
	}
}
