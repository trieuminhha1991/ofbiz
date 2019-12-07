package com.olbius.basepo.productSupplier;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.util.PartyUtil;

public class PartyGetPartyInfoEvents {

	public static final String MODULE = PartyGetPartyInfoEvents.class.getName();

	public static String getPartyName(HttpServletRequest request, HttpServletResponse response) {
		// Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		try {
			// Get parameters
			Map<String, Object> parameters = UtilHttp.getParameterMap(request);
			String partyId = (String) parameters.get("partyId");
			String partyName = PartyUtil.getPartyName(delegator, partyId);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			request.setAttribute("partyName", partyName);
		} catch (Exception e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			return "error";
		}
		return "success";
	}
}
