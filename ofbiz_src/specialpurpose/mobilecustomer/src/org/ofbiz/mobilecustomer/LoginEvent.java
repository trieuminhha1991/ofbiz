package org.ofbiz.mobilecustomer;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.webapp.control.LoginWorker;

public class LoginEvent {
	public static final String resource_error = "MCLoginErrorUiLabels";

	public static String loginMobile(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		try {
			String username = request.getParameter("USERNAME");
			List<GenericValue> checkUserLogin = delegator.findList("TemporaryParty",
					EntityCondition.makeCondition("userLoginId", username), null, null, null, false);
			for (GenericValue checkUser : checkUserLogin) {
				if ("PARTY_CREATED".equals(checkUser.getString("statusId"))) {
					request.setAttribute("_ERROR_MESSAGE_",
							UtilProperties.getMessage(resource_error, "AccoutWaitForApprove", locale));
					return "error";
				}
			}
			String result = LoginWorker.login(request, response);
			if (result.equals("error")) {
				return "error";
			}
			if (result.equals("requirePasswordChange")) {
				return "requirePasswordChange";
			}
		} catch (Exception ex) {
			request.setAttribute("_ERROR_MESSAGE_",
					UtilProperties.getMessage(resource_error, "ErrorOccurredDuringLogin", locale));
			return "error";
		}
		return "success";
	}
}
