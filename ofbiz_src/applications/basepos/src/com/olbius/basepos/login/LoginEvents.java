package com.olbius.basepos.login;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.webapp.control.LoginWorker;
import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.transaction.WebPosTransaction;

import javolution.util.FastList;

public class LoginEvents {

	public final static String module = LoginEvents.class.getName();

	public static String checkLogin(HttpServletRequest request, HttpServletResponse response) {
		String responseString = org.ofbiz.securityext.login.LoginEvents.storeCheckLogin(request, response);
		if ("error".equals(responseString)) {
			return responseString;
		}

		HttpSession session = request.getSession(true);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		if (webPosSession != null) {
			try {
				String posTerminalId = webPosSession.getId();
				GenericValue posTerminal = delegator.findOne("PosTerminal",
						UtilMisc.toMap("posTerminalId", posTerminalId), false);
				String facilityId = posTerminal.getString("facilityId");
				String partyId = userLogin.getString("partyId");
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				conditions.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> facilityParties = delegator.findList("FacilityParty",
						EntityCondition.makeCondition(conditions, EntityOperator.AND),
						UtilMisc.toSet("roleTypeId", "facilityId", "partyId", "fromDate", "thruDate"),
						UtilMisc.toList("-fromDate"), null, false);
				if (UtilValidate.isNotEmpty(facilityParties)) {

					if (UtilValidate.isNotEmpty(facilityId)) {
						WebPosTransaction webposTransaction = webPosSession.getCurrentTransaction();
						if (UtilValidate.isNotEmpty(webposTransaction)) {
							if (!webposTransaction.isOpen() && !LoginWorker.isAjax(request)) {
								request.getSession().setAttribute("_PREVIOUS_REQUEST_", "showOpenTerminal");
								return "showOpenTerminal";
							}
						}
					} else {
						responseString = "error";
					}
				} else {
					responseString = "error";
				}
			} catch (GenericEntityException e) {
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				responseString = "error";
			}
		} else {
			responseString = "error";
		}

		if ("error".equals(responseString)) {
			session.removeAttribute("userLogin");
			session.removeAttribute("autoUserLogin");
		}

		return responseString;
	}
}
