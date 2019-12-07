package com.olbius.order;

import java.math.MathContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;

import com.olbius.util.SalesPartyUtil;

public class DeliveryRequirementCartEvent {
	public static String module = ShoppingCartEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";

//    private static final String NO_ERROR = "noerror";
//    private static final String NON_CRITICAL_ERROR = "noncritical";
//    private static final String ERROR = "error";

    public static final MathContext generalRounding = new MathContext(10);
	
	// Create order event - uses createOrder service for processing
    public static String createDeliveryRequirement(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        DeliveryRequirementCart cart = DeliveryRequirementCart.getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        DeliveryRequirementCartHelper checkOutHelper = new DeliveryRequirementCartHelper(delegator, dispatcher, cart);
        Map<String, Object> callResult;

        if (UtilValidate.isEmpty(userLogin)) {
            userLogin = cart.getUserLogin();
            session.setAttribute("userLogin", userLogin);
        }
        // remove this whenever creating an order so quick reorder cache will refresh/recalc
//        session.removeAttribute("_QUICK_REORDER_PRODUCTS_");

        //get the TrackingCodeOrder List
//        String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
//        String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
        String visitId = VisitHandler.getVisitId(session);
//        String webSiteId = WebSiteWorker.getWebSiteId(request);

        callResult = checkOutHelper.createOrder(userLogin, visitId);
        if (callResult != null) {
            ServiceUtil.getMessages(request, callResult, null);
            if (ServiceUtil.isError(callResult)) {
                // messages already setup with the getMessages call, just return the error response code
                return "error";
            }
            if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                // set the requirementId for use by chained events
                String deliveryReqId = cart.getDeliveryReqId();
                request.setAttribute("requirementId", deliveryReqId);
            }
        }
        
        String issuerId = request.getParameter("issuerId");
        if (UtilValidate.isNotEmpty(issuerId)) {
            request.setAttribute("issuerId", issuerId);
        }
        
        List<String> partiesList = new ArrayList<String>();
     	String header = "";
     	String state = "open";
     	String action = "";
     	String targetLink = "";
     	String ntfType = "ONE";
     	String sendToGroup = "N";
     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
     	try {
	     	List<String> listLOG = SalesPartyUtil.getLogsSpecialist(delegator);
			if (listLOG != null) {
				partiesList.addAll(listLOG);
			}
			List<String> listAcc = SalesPartyUtil.getAccoutants(delegator);
			if (listAcc != null) {
				partiesList.addAll(listAcc);
			}
			header = UtilProperties.getMessage("DelysAdminUiLabels", "DAHaveNewProposalDelivary", cart.getLocale()) + " [" + cart.getDeliveryReqId() +"]";
			action = "viewDeliveryProposal";
	 		targetLink = "requirementId=" + cart.getDeliveryReqId();
	 	} catch (Exception e) {
	 		Debug.logError(e, "Error when set value for notify", module);
	 	}
	 	try {
	 		@SuppressWarnings("unused")
			Map<String, Object> tmpResult = dispatcher.runSync("createNotification", UtilMisc.<String, Object>toMap("partiesList", partiesList, "header", header, "state", state, "action", action, "targetLink", targetLink, "dateTime", nowTimestamp, "ntfType", ntfType, "sendToGroup", sendToGroup, "userLogin", userLogin));
	 		//Error: tmpResult
	 	} catch (Exception e) {
			Debug.logError(e, "Error when create notify", module);
		}

        return cart.getDeliveryReqType().toLowerCase();
    }

}
