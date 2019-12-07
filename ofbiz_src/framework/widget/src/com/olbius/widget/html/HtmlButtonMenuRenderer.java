package com.olbius.widget.html;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilCodec.SimpleEncoder;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.widget.ModelWidget;
import org.ofbiz.widget.WidgetWorker;
import org.ofbiz.widget.menu.ModelMenu;
import org.ofbiz.widget.menu.ModelMenuItem;
import org.ofbiz.widget.menu.ModelMenuItem.Image;
import org.ofbiz.widget.menu.ModelMenuItem.Link;

public class HtmlButtonMenuRenderer extends
		org.ofbiz.widget.html.HtmlMenuRenderer {
	public HtmlButtonMenuRenderer(HttpServletRequest request,
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
		org.ofbiz.widget.menu.ModelMenu  ofbizModelMenu = modelMenu;
		String iconStyle = ofbizModelMenu.getFillStyle();
		writer.append("<div class=\"").append(iconStyle).append("\"><p>");
		appendWhitespace(writer);
	}

	@Override
	public void renderMenuClose(Appendable writer, Map<String, Object> context,
			ModelMenu modelMenu) throws IOException {
		if (modelMenu.renderedMenuItemCount(context) > 0) {
			writer.append(" </p></div>");
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

		Link link = menuItem.getLink();
		if (link != null) {
			renderLink(writer, context, link);
		} else {
			String txt = menuItem.getTitle(context);
			SimpleEncoder simpleEncoder = (SimpleEncoder) context
					.get("simpleEncoder");
			if (simpleEncoder != null) {
				txt = simpleEncoder.encode(txt);
			}
			writer.append(txt);

		}
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
			writer.append("<a class=\"btn btn-primary btn-mini\" ");
			
            
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
            // Tab image if require
            // check widget-style(class css)
 			String strStyle = link.getStyle(context);
 			if(strStyle != null && !strStyle.isEmpty()){
 				writer.append("<i class='" + strStyle +"'></i>&nbsp;");
 			}
            // Text or Image
            Image img = link.getImage();
            if (img != null) {
                renderImage(writer, context, img);
                writer.append("&nbsp;" + link.getText(context));
            } else {
                writer.append(link.getText(context));
            }
            writer.append("</a>");
        }
	}

	@Override
	public void renderFormatSimpleWrapperRows(Appendable writer,
			Map<String, Object> context, Object menuObj) throws IOException {
		// do nothing
	}
}
