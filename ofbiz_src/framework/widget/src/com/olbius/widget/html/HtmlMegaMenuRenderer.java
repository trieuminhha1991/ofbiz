package com.olbius.widget.html;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.widget.ModelWidget;
import org.ofbiz.widget.menu.ModelMenu;

public class HtmlMegaMenuRenderer extends HtmlMenuRenderer{

	public HtmlMegaMenuRenderer(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}
	
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
		org.ofbiz.widget.menu.ModelMenu  ofbizModelMenu = (org.ofbiz.widget.menu.ModelMenu) modelMenu;
		String iconStyle = ofbizModelMenu.getFillStyle();
		writer.append("<div class=\"").append(iconStyle).append("\"><p>");
		appendWhitespace(writer);
	}
}
