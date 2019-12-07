package com.olbius.acc.setting;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.PartyUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class OpeningBalEvents {
	
	public final static String module = OpeningBalEvents.class.getName();
	
	/*public static String createGlAccountBal(HttpServletRequest request, HttpServletResponse response) {
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
			List<Map<String, String>> glAccountList = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("glAccountList"));
			//Create InvoiceItem
			for(Map<String, String> item : glAccountList) {
				GenericValue glAccBal = delegator.makeValue("GlAccountBalance");
				glAccBal.put("glAccountId", item.get("glAccountId"));
				glAccBal.put("organizationPartyId", item.get("organizationPartyId"));
				if(!UtilValidate.isEmpty(item.get("openingCrBalance"))) {
					glAccBal.put("openingCrBalance", new BigDecimal(item.get("openingCrBalance")));
				}else {
					glAccBal.put("openingCrBalance", BigDecimal.ZERO);
				}
				if(!UtilValidate.isEmpty(item.get("openingDrBalance"))) {
					glAccBal.put("openingDrBalance", new BigDecimal(item.get("openingDrBalance")));
				}else {
					glAccBal.put("openingDrBalance", BigDecimal.ZERO);
				}
				glAccBal.create();
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
	}*/
	
	public static String createGlAccountBal(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		String glAccountBalanceParam = request.getParameter("glAccountBalance");
		//Debug.log(module + "::createGlAccountBal, JSON param = " + glAccountBalanceParam);
		
		JSONArray glAccountBalanceJsonArr = JSONArray.fromObject(glAccountBalanceParam);
		List<GenericValue> listGlAccountBalance = FastList.newInstance();
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			for(int i = 0; i < glAccountBalanceJsonArr.size(); i++){
				JSONObject glAccountBalanceJson = glAccountBalanceJsonArr.getJSONObject(i);
				//Debug.log(module + "::createGlAccountBal, orgId = " + orgId + ", gl_acc = " + glAccountBalanceJson.get("glAccountId")
				//		+ ", D = " + glAccountBalanceJson.getString("openingDrBalance")
				//		+ ", C = " + glAccountBalanceJson.getString("openingCrBalance"));
				
				GenericValue glAccountBalance = delegator.makeValue("GlAccountBalance");
				glAccountBalance.put("glAccountId", glAccountBalanceJson.get("glAccountId"));
				glAccountBalance.put("organizationPartyId", orgId);
				//glAccountBalance.put("openingDrBalance", glAccountBalanceJson.has("openingDrBalance")? new BigDecimal(glAccountBalanceJson.getString("openingDrBalance")) : null);
				//glAccountBalance.put("openingCrBalance", glAccountBalanceJson.has("openingCrBalance")? new BigDecimal(glAccountBalanceJson.getString("openingCrBalance")) : null);
				glAccountBalance.put("openingDrBalance", glAccountBalanceJson.get("openingDrBalance") != null && !glAccountBalanceJson.getString("openingDrBalance").equals("null")? new BigDecimal(glAccountBalanceJson.getString("openingDrBalance")) : null);
				glAccountBalance.put("openingCrBalance", glAccountBalanceJson.get("openingCrBalance") != null && !glAccountBalanceJson.getString("openingCrBalance").equals("null")? new BigDecimal(glAccountBalanceJson.getString("openingCrBalance")) : null);
				
				listGlAccountBalance.add(glAccountBalance);
			}
			Map<String, Object> context = FastMap.newInstance();
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			context.put("listGlAccountBalance", listGlAccountBalance);
			Map<String, Object> resultService = dispatcher.runSync("updateListGlAccountBalance", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
