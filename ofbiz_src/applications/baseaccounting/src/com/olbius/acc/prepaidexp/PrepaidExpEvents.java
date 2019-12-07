package com.olbius.acc.prepaidexp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.olbius.services.JqxWidgetSevices;

public class PrepaidExpEvents {
	
public final static String module = PrepaidExpEvents.class.getName();  
	
	@SuppressWarnings("unchecked")
	public static String createPrepaidExpAndAlloc(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

		//Get Dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		        
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		
        boolean beganTransaction = false;
        boolean okay = true;
        String mess = "";
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			beganTransaction  = TransactionUtil.begin();
			//Get parameters
			String prepaidExpId = (String) parameters.get("prepaidExpId");
			String prepaidExpName = (String) parameters.get("prepaidExpName");
			String description = (String) parameters.get("description");
			String prepaidExpGlAccountId = (String) parameters.get("prepaidExpGlAccountId");
			BigDecimal amount = BigDecimal.valueOf(Double.parseDouble((String) parameters.get("amount")));
			Long allocPeriodNum = Long.parseLong((String) parameters.get("allocPeriodNum"));
			BigDecimal amountEachPeriod = BigDecimal.valueOf(Double.parseDouble((String) parameters.get("amountEachPeriod")));
			Timestamp acquiredDate = (Timestamp) JqxWidgetSevices.convert("java.sql.Timestamp", (String) parameters.get("acquiredDate"));
			List<Map<String, String>> listAllocs = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listAllocs"));
			
			//Create Equipment
			Map<String, Object> createPrepaidExpCtx = new HashMap<String, Object>();
			createPrepaidExpCtx.put("prepaidExpId", prepaidExpId);
			createPrepaidExpCtx.put("prepaidExpName", prepaidExpName);
			createPrepaidExpCtx.put("description", description);
			createPrepaidExpCtx.put("prepaidExpGlAccountId", prepaidExpGlAccountId);
			createPrepaidExpCtx.put("amount", amount);
			createPrepaidExpCtx.put("allocPeriodNum", allocPeriodNum);
			createPrepaidExpCtx.put("amountEachPeriod", amountEachPeriod);
			createPrepaidExpCtx.put("acquiredDate", acquiredDate);
			createPrepaidExpCtx.put("userLogin", userLogin);
			dispatcher.runSync("createPrepaidExp", createPrepaidExpCtx);
			
			//Create Equipment Alloc
			for(Map<String, String> item: listAllocs) {
				Map<String, Object> createPrepaidExpAllocCtx = new HashMap<String, Object>();
				createPrepaidExpAllocCtx.put("prepaidExpId", prepaidExpId);
				createPrepaidExpAllocCtx.put("seqId", item.get("seqId"));
				createPrepaidExpAllocCtx.put("allocPartyId", item.get("allocPartyId"));
				BigDecimal allocRate = BigDecimal.valueOf(Double.parseDouble((String) item.get("allocRate")));
				createPrepaidExpAllocCtx.put("allocRate", allocRate);
				createPrepaidExpAllocCtx.put("allocGlAccountId", item.get("allocGlAccountId"));
				createPrepaidExpCtx.put("userLogin", userLogin);
				dispatcher.runSync("createPrepaidExpAlloc", createPrepaidExpAllocCtx);
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
	
	@SuppressWarnings("unchecked")
	public static String addPEToPeriod(HttpServletRequest request, HttpServletResponse response) {
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
			List<Map<String, String>> listPEs = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listPEs"));
			String customTimePeriodId = (String)parameters.get("customTimePeriodId");
			//Create InvoiceItem
			for(Map<String, String> item : listPEs) {
				GenericValue peCustomTimePeriod = delegator.makeValue("PrepaidExpCustomTimePeriod");
				peCustomTimePeriod.put("customTimePeriodId", customTimePeriodId);
				peCustomTimePeriod.put("prepaidExpId", item.get("prepaidExpId"));
				peCustomTimePeriod.put("amount", BigDecimal.valueOf(Double.parseDouble((String)item.get("amount"))));
				peCustomTimePeriod.create();
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
