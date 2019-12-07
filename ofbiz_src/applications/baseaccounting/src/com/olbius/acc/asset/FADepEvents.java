package com.olbius.acc.asset;

import com.olbius.services.JqxWidgetSevices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FADepEvents {
	
	public final static String module = FADepEvents.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String addFAToPeriod(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
        boolean beganTransaction = false;
        boolean okay = true;
        String mess = "";
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			beganTransaction  = TransactionUtil.begin();
			//Get parameters
			List<Map<String, String>> listFixedAssets = (List<Map<String,String>>) JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listFixedAssets"));
			String customTimePeriodId = (String)parameters.get("customTimePeriodId");
			//Create InvoiceItem
			for(Map<String, String> item : listFixedAssets) {
				GenericValue faPeriod = delegator.makeValue("FixedAssetCustomTimePeriod");
				faPeriod.put("customTimePeriodId", customTimePeriodId);
				faPeriod.put("fixedAssetId", item.get("fixedAssetId"));
				faPeriod.put("amount", new BigDecimal(item.get("depreciation")));
				faPeriod.create();
			}
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			mess = e.getMessage();
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			mess = e.getMessage();
		}finally {
			if(!okay) {
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
			}else {
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
        return "success";
	}
}
