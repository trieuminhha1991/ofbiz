package com.olbius.minilang.method.ifops;

import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.MiniLangValidate;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.MessageElement;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.ofbiz.security.Security;
import org.w3c.dom.Element;

import com.olbius.security.util.SecurityUtil;

public class OlbiusCheckPermission extends MethodOperation {

	private final FlexibleMapAccessor<List<String>> errorListFma;
	private final MessageElement messageElement;
	private final OlbiusPermissionInfo primaryPermissionInfo;

	public OlbiusCheckPermission(Element element, SimpleMethod simpleMethod) throws MiniLangException {
		super(element, simpleMethod);
		errorListFma = FlexibleMapAccessor
				.getInstance(MiniLangValidate.checkAttribute(element.getAttribute("error-list-name"), "error_list"));
		primaryPermissionInfo = new OlbiusPermissionInfo(element);
		messageElement = MessageElement.fromParentElement(element, simpleMethod);
	}

	@Override
	public boolean exec(MethodContext methodContext) throws MiniLangException {
		boolean hasPermission = false;
		GenericValue userLogin = methodContext.getUserLogin();
		if (userLogin != null) {
			Security security = methodContext.getSecurity();
			hasPermission = this.primaryPermissionInfo.hasPermission(methodContext, userLogin, security, simpleMethod);
		}
		if (!hasPermission && messageElement != null) {
			List<String> messages = errorListFma.get(methodContext.getEnvMap());
			if (messages == null) {
				messages = new LinkedList<String>();
				errorListFma.put(methodContext.getEnvMap(), messages);
			}
			messages.add(messageElement.getMessage(methodContext));
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<check-permission ");
		sb.append("type=\"").append(this.primaryPermissionInfo.typeFse).append("\" ");
		sb.append("app=\"").append(this.primaryPermissionInfo.appFse).append("\" ");
		sb.append("permission=\"").append(this.primaryPermissionInfo.permissionFse).append("\" ");
		if (!"error_list".equals(this.errorListFma.getOriginalName())) {
			sb.append("error-list-name=\"").append(this.errorListFma).append("\" ");
		}
		if (messageElement != null) {
			sb.append(">").append(messageElement).append("</check-permission>");
		} else {
			sb.append("/>");
		}
		return sb.toString();
	}

	/**
	 * A &lt;check-permission&gt; element factory.
	 */
	public static final class OlbiusCheckPermissionFactory implements Factory<OlbiusCheckPermission> {
		@Override
		public OlbiusCheckPermission createMethodOperation(Element element, SimpleMethod simpleMethod)
				throws MiniLangException {
			return new OlbiusCheckPermission(element, simpleMethod);
		}

		@Override
		public String getName() {
			return "olbius-check-permission";
		}
	}

	private class OlbiusPermissionInfo {
		private final FlexibleStringExpander permissionFse;
		private final FlexibleStringExpander typeFse;
		private final FlexibleStringExpander appFse;

		private OlbiusPermissionInfo(Element element) throws MiniLangException {
			this.permissionFse = FlexibleStringExpander.getInstance(element.getAttribute("permission"));
			this.typeFse = FlexibleStringExpander.getInstance(element.getAttribute("type"));
			this.appFse = FlexibleStringExpander.getInstance(element.getAttribute("app"));
		}

		private boolean hasPermission(MethodContext methodContext, GenericValue userLogin, Security security,
				SimpleMethod simpleMethod) {
			String permission = permissionFse.expandString(methodContext.getEnvMap());
			String type = typeFse.expandString(methodContext.getEnvMap());
			String app = appFse.expandString(methodContext.getEnvMap());
			if(type == null || app == null || type.isEmpty() || app.isEmpty()) {
				return false;
			}
			return SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, permission, type, app);
		}
	}

}
