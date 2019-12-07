package com.olbius.minilang.method.conditional;

import java.util.Collections;
import java.util.List;

import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.artifact.ArtifactInfoContext;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.ofbiz.minilang.method.conditional.Conditional;
import org.ofbiz.minilang.method.conditional.ConditionalFactory;
import org.ofbiz.security.Security;
import org.w3c.dom.Element;

import com.olbius.security.util.SecurityUtil;

public class OlbiusHasPermissionCondition extends MethodOperation implements Conditional {

	private final FlexibleStringExpander permissionFse;
	private final FlexibleStringExpander typeFse;
	private final FlexibleStringExpander appFse;
	private final List<MethodOperation> elseSubOps;
	private final List<MethodOperation> subOps;

	protected OlbiusHasPermissionCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
		super(element, simpleMethod);
		this.permissionFse = FlexibleStringExpander.getInstance(element.getAttribute("permission"));
		this.typeFse = FlexibleStringExpander.getInstance(element.getAttribute("type"));
		this.appFse = FlexibleStringExpander.getInstance(element.getAttribute("app"));
		Element childElement = UtilXml.firstChildElement(element);
		if (childElement != null && !"else".equals(childElement.getTagName())) {
			this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
		} else {
			this.subOps = null;
		}
		Element elseElement = UtilXml.firstChildElement(element, "else");
		if (elseElement != null) {
			this.elseSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(elseElement, simpleMethod));
		} else {
			this.elseSubOps = null;
		}
	}

	@Override
	public boolean checkCondition(MethodContext methodContext) throws MiniLangException {
		GenericValue userLogin = methodContext.getUserLogin();
		if (userLogin != null) {
			Security security = methodContext.getSecurity();
			String permission = permissionFse.expandString(methodContext.getEnvMap());
			String type = typeFse.expandString(methodContext.getEnvMap());
			String app = appFse.expandString(methodContext.getEnvMap());
			if(type == null || app == null || type.isEmpty() || app.isEmpty()) {
				return false;
			}
			return SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, permission,
					type, app);
		}
		return false;
	}

	@Override
	public boolean exec(MethodContext methodContext) throws MiniLangException {
		if (checkCondition(methodContext)) {
			if (this.subOps != null) {
				return SimpleMethod.runSubOps(subOps, methodContext);
			}
		} else {
			if (elseSubOps != null) {
				return SimpleMethod.runSubOps(elseSubOps, methodContext);
			}
		}
		return true;
	}

	@Override
	public void gatherArtifactInfo(ArtifactInfoContext aic) {
		if (this.subOps != null) {
			for (MethodOperation method : this.subOps) {
				method.gatherArtifactInfo(aic);
			}
		}
		if (this.elseSubOps != null) {
			for (MethodOperation method : this.elseSubOps) {
				method.gatherArtifactInfo(aic);
			}
		}
	}

	@Override
	public void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext) {
		messageBuffer.append("olbius-has-permission[");
		messageBuffer.append(this.permissionFse);
		messageBuffer.append("]");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<if-olbius-has-permission ");
		if (!this.appFse.isEmpty()) {
			sb.append("app=\"").append(this.appFse).append("\" ");
		}
		if (!this.typeFse.isEmpty()) {
			sb.append("type=\"").append(this.typeFse).append("\" ");
		}
		if (!this.permissionFse.isEmpty()) {
			sb.append("permission=\"").append(this.permissionFse).append("\" ");
		}
		sb.append("/>");
		return sb.toString();
	}

	public static final class OlbiusHasPermissionConditionFactory extends ConditionalFactory<OlbiusHasPermissionCondition>
			implements Factory<OlbiusHasPermissionCondition> {
		@Override
		public OlbiusHasPermissionCondition createCondition(Element element, SimpleMethod simpleMethod)
				throws MiniLangException {
			return new OlbiusHasPermissionCondition(element, simpleMethod);
		}

		@Override
		public OlbiusHasPermissionCondition createMethodOperation(Element element, SimpleMethod simpleMethod)
				throws MiniLangException {
			return new OlbiusHasPermissionCondition(element, simpleMethod);
		}

		@Override
		public String getName() {
			return "if-olbius-has-permission";
		}
	}

}
