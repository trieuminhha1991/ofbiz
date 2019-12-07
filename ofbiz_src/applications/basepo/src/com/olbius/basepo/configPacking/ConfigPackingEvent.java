package com.olbius.basepo.configPacking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.ModelService;

public class ConfigPackingEvent {

	public static final String module = ConfigPackingEvent.class.getName();

	public static String getUomByProductId(HttpServletRequest request, HttpServletResponse response) {
		// Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

		boolean beganTransaction = false;
		boolean okay = true;
		String mess = "";
		// Get parameters
		Map<String, Object> parameters = UtilHttp.getParameterMap(request);

		// List results
		List<String> listResults = new ArrayList<String>();
		try {
			beganTransaction = TransactionUtil.begin();
			// Get parameters
			String productId = (String) parameters.get("productId");
			String uomId = (String) parameters.get("uomId");
			List<EntityCondition> listCond = new ArrayList<EntityCondition>();
			EntityCondition prodCond = EntityCondition.makeCondition("productId", productId);
			EntityCondition uomFromCond = EntityCondition.makeCondition("uomFromId", uomId);
			EntityCondition uomToCond = EntityCondition.makeCondition("uomToId", uomId);
			listCond.add(prodCond);
			listCond.add(uomFromCond);
			List<GenericValue> listConfigPackings = delegator.findList("ConfigPacking",
					EntityCondition.makeCondition(listCond), null, null, null, false);
			Set<String> filterSet = new HashSet<>();
			for (GenericValue item : listConfigPackings) {
				filterSet.add(item.getString("uomToId"));
			}
			listCond.clear();
			listCond.add(prodCond);
			listCond.add(uomToCond);
			listConfigPackings = delegator.findList("ConfigPacking", EntityCondition.makeCondition(listCond), null,
					null, null, false);
			for (GenericValue item : listConfigPackings) {
				filterSet.add(item.getString("uomFromId"));
			}
			listResults.addAll(filterSet);
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			mess = e.getMessage();
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			mess = e.getMessage();
		} finally {
			if (!okay) {
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, mess);
					return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
					return "error";
				}
			} else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
					return "error";
				}
			}
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		request.setAttribute("uomList", listResults);
		return "success";
	}
}
