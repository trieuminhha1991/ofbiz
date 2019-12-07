package com.olbius.widget.menu.condition;

import org.ofbiz.widget.menu.ModelMenuCondition.MenuCondition;

import java.util.Map;

import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.widget.menu.ModelMenuItem;
import org.w3c.dom.Element;

import com.olbius.security.api.Application;
import com.olbius.security.util.SecurityUtil;

public class IfOlbiusHasPermission extends MenuCondition {

	protected FlexibleStringExpander permissionExdr;
	protected FlexibleStringExpander entityExdr;
	protected String type;

	public IfOlbiusHasPermission(ModelMenuItem modelMenuItem, Element conditionElement, String type) {
		super(modelMenuItem, conditionElement);
		this.type = type;
		permissionExdr = FlexibleStringExpander.getInstance(conditionElement.getAttribute("permission"));
		entityExdr = FlexibleStringExpander.getInstance(conditionElement.getAttribute("entity"));
	}

	@Override
	public boolean eval(Map<String, Object> context) {
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		if (userLogin != null) {
			String permission = permissionExdr.expandString(context);
			String app = null;

			if (Application.MENU.equals(type)) {
				app = modelMenuItem.getModelMenu().getBoundaryCommentName() + "#" + modelMenuItem.getName();
			} else if (Application.ENTITY.equals(type)) {
				app = entityExdr.expandString(context);
			}

			Security security = (Security) context.get("security");

			return SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, permission, type, app);
		}
		return false;
	}

}
