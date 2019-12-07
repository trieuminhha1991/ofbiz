package com.olbius.widget.html;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

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
import org.ofbiz.widget.menu.ModelMenuItem.Link;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.olbius.widget.menu.ModelMenuFactory.ModelMenuEnum;

public class HtmlScreenletMenuRenderer extends org.ofbiz.widget.html.HtmlMenuRenderer {
	public HtmlScreenletMenuRenderer(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	@Override
	public void renderMenuOpen(Appendable writer, Map<String, Object> context,
			ModelMenu modelMenu) throws IOException {
		if (!userLoginIdHasChanged) {
			userLoginIdHasChanged = userLoginIdHasChanged();
		}

		this.widgetCommentsEnabled = ModelWidget
				.widgetBoundaryCommentsEnabled(context);
		renderBeginningBoundaryComment(writer, "Menu Widget", modelMenu);
	}

	@Override
	public void renderMenuClose(Appendable writer, Map<String, Object> context,
			ModelMenu modelMenu) throws IOException {
		renderEndingBoundaryComment(writer, "Menu Widget", modelMenu);

		userLoginIdHasChanged = userLoginIdHasChanged();
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		if (userLogin != null) {
			String userLoginId = userLogin.getString("userLoginId");
			setUserLoginIdAtPermGrant(userLoginId);
		} else {
			request.getSession().setAttribute("userLoginIdAtPermGrant", null);
		}
	}

	@Override
	public void renderMenuItem(Appendable writer, Map<String, Object> context,
			ModelMenuItem menuItem) throws IOException {
		boolean hideThisItem = isHideIfSelected(menuItem, context);
		if (hideThisItem)
			return;
		String widgetStyle = menuItem.getWidgetStyle();
		String titleStyle = menuItem.getTitleStyle();
		Element subMenuElement = menuItem.getSubMenuElement();
		if (subMenuElement == null){
			Link link = menuItem.getLink();
			if (link != null) {
				renderLink(writer, context, link);
			}
		}else{
			writer.append("<ul style=\""+titleStyle+"\"><i class=\""+widgetStyle+"\">");
			writer.append("<li class=\"dropdown\">");
			writer.append("<a data-toggle=\"dropdown\" class=\"dropdown-toggle\" href=\"#\" style=\"font-size:14px;\">");
			SimpleEncoder simpleEncoder = (SimpleEncoder) context.get("simpleEncoder");
    		if (simpleEncoder != null) {
    			writer.append(simpleEncoder.encode(menuItem.getTitle(context)));
    		}
    		writer.append("<b class=\"caret\"></b></a>");
			renderSubMenu(writer, context, subMenuElement);
			writer.append("</li></i></ul>");
		}
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

        if (UtilValidate.isNotEmpty(target)) {
        	String linkType = WidgetWorker.determineAutoLinkType(
					link.getLinkType(), target, link.getUrlMode(), request);
			String targetWindow = link.getTargetWindow(context);
			String uniqueItemName = menuItem.getModelMenu().getName()
					+ "_"
					+ menuItem.getName()
					+ "_LF_"
					+ UtilMisc.<String> addToBigDecimalInMap(context,
							"menuUniqueItemIndex", BigDecimal.ONE);
			if(menuItem.getModelMenu().getExtraIndex(context) != null){
				uniqueItemName += "_" + menuItem.getModelMenu().getExtraIndex(context);
			}
			if ("hidden-form".equals(linkType)) {
				writer.append("<form style=\"display:none;\" method=\"post\"");
				writer.append(" action=\"");
				// note that this passes null for the parameterList on purpose
				// so they won't be put into the URL
				WidgetWorker.buildHyperlinkUrl(writer, target,
						link.getUrlMode(), null, link.getPrefix(context),
						link.getFullPath(), link.getSecure(), link.getEncode(),
						request, response, context);
				writer.append("\"");

				if (UtilValidate.isNotEmpty(targetWindow)) {
					writer.append(" target=\"");
					writer.append(targetWindow);
					writer.append("\"");
				}

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
			String strToolTip = menuItem.getTooltip(context);
            writer.append("<a title=\"" + strToolTip + "\"");
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
			if (!"hidden-form".equals(linkType)) {
				if (UtilValidate.isNotEmpty(targetWindow)) {
					writer.append(" target=\"");
					writer.append(targetWindow);
					writer.append("\"");
				}
			}
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
            	WidgetWorker.buildHyperlinkUrl(writer, target, link.getUrlMode(), link.getParameterMap(context), link.getPrefix(context),
                        link.getFullPath(), link.getSecure(), link.getEncode(), request, response, context);
//    			if (this.request.getAttribute("extKey") != null) {
//    				target = (String) this.request.getAttribute("extKey");
//    			}
//    			writer.append(target);
    			// Icon
    			writer.append("\">");
//    			writer.append(link.getText(context));
            }
            
            writer.append("<i class=\""+menuItem.getWidgetStyle()+"\">");
    		String txt = menuItem.getTitle(context);
    		SimpleEncoder simpleEncoder = (SimpleEncoder) context.get("simpleEncoder");
    		if (simpleEncoder != null) {
    			txt = simpleEncoder.encode(txt);
    		}
    		writer.append(txt);
    		writer.append("</i>");
            writer.append("</a>");
        }
	}

	@Override
	public void renderFormatSimpleWrapperRows(Appendable writer,
			Map<String, Object> context, Object menuObj) throws IOException {
		// do nothing
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
}
