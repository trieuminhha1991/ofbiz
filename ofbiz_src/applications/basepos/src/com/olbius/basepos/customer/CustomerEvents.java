package com.olbius.basepos.customer;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

public class CustomerEvents {
	public static String resource_error = "BasePosErrorUiLabels";
	
	public static String createNewCustomerPOS(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		
		String customerName = request.getParameter("customerName");
		String contactAddress = request.getParameter("contactAddress");
		String phone = request.getParameter("phone");
		
		Map<String, Object> contextMap = FastMap.newInstance();
		contextMap.put("fullName", customerName);
		contextMap.put("contactAddress", contactAddress);
		contextMap.put("mobilePhone", phone);
		contextMap.put("isCustomerPOS", "Y");
		contextMap.put("shippingPhone", "PHONE_MOBILE");
		contextMap.put("primaryPhone", "PHONE_MOBILE");
		contextMap.put("userLogin", userLogin);
		
		Map<String, Object> resultMap = FastMap.newInstance();
		try {
			resultMap = dispatcher.runSync("createContactPersonal", contextMap);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (ServiceUtil.isError(resultMap)){
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateCustomer", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} else {
			String partyId = (String) resultMap.get("partyId");
			String productStoreId = ProductStoreWorker.getProductStoreId(request);
			GenericValue productStoreRole = delegator.makeValue("ProductStoreRole");
			productStoreRole.set("partyId", partyId);
			productStoreRole.set("roleTypeId", "CUSTOMER");
			productStoreRole.set("productStoreId", productStoreId);
			productStoreRole.set("fromDate", UtilDateTime.nowTimestamp());
			
			try {
				productStoreRole.create();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			request.setAttribute("partyId", partyId);
		}
		
		return "success";
	}
	
}
