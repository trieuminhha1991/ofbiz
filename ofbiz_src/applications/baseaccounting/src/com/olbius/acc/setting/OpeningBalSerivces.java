package com.olbius.acc.setting;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.UtilServices;
import com.olbius.acc.utils.accounts.AccountUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

public class OpeningBalSerivces {

	public static final String module = OpeningBalSerivces.class.getName();
	public static final String resourceAcc = "BaseAccountingUiLabels";
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListGlAccountBal(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	List<Map<String, Object>> listBal = new ArrayList<Map<String,Object>>();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("glAccountId");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	if (organizationPartyId!= null)
    	{
    		EntityCondition organizationPartyCon = EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationPartyId);
    		listAllConditions.add(organizationPartyCon);
    	}       	
    	try {
    		listIterator = delegator.find("GlAccountBalanceView", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		List<GenericValue> listGenBal = listIterator.getCompleteList();
    		for(GenericValue item: listGenBal) {
    			Map<String, Object> bal = new HashMap<String, Object>();
    			bal.put("glAccountId", item.get("glAccountId"));
    			bal.put("organizationPartyId", organizationPartyId);
    			bal.put("accountName", item.get("accountName"));
    			bal.put("openingCrBalance", item.get("openingCrBalance"));
    			bal.put("openingDrBalance", item.get("openingDrBalance"));
    			bal.put("accountType", AccountUtils.getAccountType(item.getString("glAccountId"), delegator));
    			listBal.add(bal);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListGlAccountBal service: " + e.toString();
			Debug.logError(e, errMsg, module);
		} finally{
			try {
				listIterator.close();
			} catch (GenericEntityException e) {
				ErrorUtils.processException(e, module);
			}
		}
    	
    	successResult.put("listIterator", listBal);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListGlAccount(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	if(organizationPartyId != null){
    		mapCondition.put("organizationPartyId",organizationPartyId );
    	}
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		listIterator = delegator.find("GlOrganizationClassAndParent", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListGlAccount service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> deleteGlAccountBal(DispatchContext ctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = ctx.getDelegator();
		
		//Get parameters
		String glAccountId = (String)context.get("glAccountId");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		
		try {
			GenericValue glAccountBal = delegator.findOne("GlAccountBalance", UtilMisc.toMap("glAccountId", glAccountId, "organizationPartyId", organizationPartyId, "customTimePeriodId", customTimePeriodId), false);
			glAccountBal.remove();
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateGlAccountBal(DispatchContext ctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = ctx.getDelegator();
		
		//Get parameters
		String glAccountId = (String)context.get("glAccountId");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		BigDecimal openingDrBalance = (BigDecimal)context.get("openingDrBalance");
		BigDecimal openingCrBalance = (BigDecimal)context.get("openingCrBalance");
		
		try {
			GenericValue glAccountBal = delegator.findOne("GlAccountBalance", UtilMisc.toMap("glAccountId", glAccountId, "organizationPartyId", organizationPartyId), false);
			glAccountBal.put("openingDrBalance", openingDrBalance);
			glAccountBal.put("openingCrBalance", openingCrBalance);
			glAccountBal.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	/**GlAccount Balance for Party**/
 	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListGlAccountBalParty(DispatchContext ctx, Map<String, Object> context) {
 		Delegator delegator = ctx.getDelegator();
    	List<Map<String, Object>> listBal = new ArrayList<Map<String,Object>>();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("glAccountId");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Integer pagenum = Integer.valueOf(parameters.get("pagenum")[0]);
    	Integer pagesize = Integer.valueOf(parameters.get("pagesize")[0]);
    	String organizationPartyId = parameters.containsKey("organizationPartyId") ?  (String) parameters.get("organizationPartyId")[0] : null;
    	if (organizationPartyId!= null)
    	{
    		EntityCondition organizationPartyCon = EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationPartyId);
    		listAllConditions.add(organizationPartyCon);
    	}       	
    	try {
    		listIterator = delegator.find("GlAccountBalancePartyView", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		if(pagenum + pagesize > listIterator.getResultsTotalSize()){
    			pagesize = listIterator.getResultsTotalSize() - pagenum;
    		}
    		List<GenericValue> listGenBal = listIterator.getPartialList(pagenum*pagesize + 1, pagesize);
    		for(GenericValue item: listGenBal) {
    			Map<String, Object> bal = new HashMap<String, Object>();
    			bal.put("glAccountId", item.get("glAccountId"));
    			bal.put("organizationPartyId", organizationPartyId);
    			bal.put("accountName", item.get("accountName"));
    			bal.put("openingCrBalance", item.get("openingCrBalance"));
    			bal.put("openingDrBalance", item.get("openingDrBalance"));
    			bal.put("partyId", item.getString("partyId"));
    			bal.put("fullName", item.getString("fullName"));
    			bal.put("partyCode", item.getString("partyCode"));
    			bal.put("accountType", UtilServices.getAccountType(item.getString("glAccountId"), delegator));
    			listBal.add(bal);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListGlAccountBalParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		} finally{
		}
    	
    	successResult.put("listIterator", listBal);
    	try {
			successResult.put("TotalRows", String.valueOf(listIterator.getResultsTotalSize()));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
			listIterator.close();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return successResult;
    }
 	
 	@SuppressWarnings("unchecked")
	public static String createGlAccountBalParty(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
        boolean beganTransaction = false;
        boolean okay = true;
        String mess = "";
        //Get parameters
        String orgId = (String) request.getParameter("orgId");
        String listValue = (String) request.getParameter("listValue");
      
        try {
        	if(listValue == null || listValue.equals("null") || listValue.equals("undefined")) return "error";
			beganTransaction  = TransactionUtil.begin();
			  JSONArray _array = JSONArray.fromObject(listValue);
			if(UtilValidate.isNotEmpty(_array)){
				for(int i =0 ;i  < _array.size();i++){
					JSONObject obj = (JSONObject) _array.get(i);
					Iterator irt = obj.keys();
					while(irt.hasNext()){
						String key = (String) irt.next();
						String[] temp = key.indexOf("-") != -1 ? key.split("-") : null;
						if(temp != null && temp.length >= 2){
							String glAccountId = temp[0];
							String partyId = temp[1];
							Map<String,Object> _dataD_C = (Map<String,Object>) obj.get(key);
							BigDecimal openingCrBalance = !UtilValidate.isEmpty(_dataD_C.get("openingCrBalance"))? new BigDecimal((Integer)_dataD_C.get("openingCrBalance")) : BigDecimal.ZERO;
							BigDecimal openingDrBalance = !UtilValidate.isEmpty(_dataD_C.get("openingDrBalance"))? new BigDecimal((Integer)_dataD_C.get("openingDrBalance")) : BigDecimal.ZERO;
							GenericValue checkExists = delegator.findOne("GlAccountBalanceParty", UtilMisc.toMap("glAccountId", glAccountId,"partyId",partyId,"organizationPartyId",orgId),false);
							if(checkTotalBalanceAccount(delegator, orgId, glAccountId, (openingDrBalance.compareTo(BigDecimal.ZERO) > 0 ? openingDrBalance : openingCrBalance ))){
								if(checkExists != null){
									BigDecimal valueCr = BigDecimal.ZERO;
									BigDecimal valueDr = BigDecimal.ZERO;
									valueCr = checkExists.getBigDecimal("openingCrBalance");
									valueCr = valueCr.add(openingCrBalance);
									checkExists.set("openingCrBalance", valueCr);
									valueDr = checkExists.getBigDecimal("openingDrBalance");
									valueDr = valueDr.add(openingDrBalance);
									checkExists.set("openingDrBalance", valueDr);
									checkExists.store();
								}else{											
									GenericValue balance = delegator.makeValue("GlAccountBalanceParty");
									balance.set("glAccountId", glAccountId);
									balance.set("partyId", partyId);
									balance.set("organizationPartyId", orgId);
									balance.set("openingCrBalance", openingCrBalance);
									balance.set("openingDrBalance", openingDrBalance);
									balance.create();
								}
							}else{
								request.setAttribute(ModelService.RESPONSE_MESSAGE, UtilProperties.getMessage(resourceAcc, "BACCTotalBalanceNotValid", UtilHttp.getLocale(request))  + " " + glAccountId);
								return "error";
							}
						}
						
					}
				}
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
 	
 	
 	public static Map<String, Object> updateGlAccountBalParty(DispatchContext ctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = ctx.getDelegator();
		
		//Get parameters
		String glAccountId = (String)context.get("glAccountId");
		String partyId = (String) context.get("partyId");
    	String organizationPartyId = (String)context.get("organizationPartyId");	
		BigDecimal openingDrBalance = (BigDecimal)context.get("openingDrBalance");
		BigDecimal openingCrBalance = (BigDecimal)context.get("openingCrBalance");
		
		try {
			GenericValue glAccountBal = delegator.findOne("GlAccountBalanceParty", UtilMisc.toMap("glAccountId", glAccountId, "organizationPartyId", organizationPartyId,"partyId",partyId), false);
			glAccountBal.put("openingDrBalance", openingDrBalance);
			glAccountBal.put("openingCrBalance", openingCrBalance);
			if(checkTotalBalanceAccount(delegator, organizationPartyId, glAccountId, (openingDrBalance.compareTo(BigDecimal.ZERO) > 0 ? openingDrBalance : openingCrBalance ))){
				glAccountBal.store();
			}else return ServiceUtil.returnError(UtilProperties.getMessage(resourceAcc, "BACCTotalBalanceNotValid", (Locale) context.get("locale")));
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
 	
 	public static Map<String, Object> removeGlAccountBalParty(DispatchContext ctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = ctx.getDelegator();
		
		//Get parameters
		String glAccountId = (String)context.get("glAccountId");
		String partyId = (String) context.get("partyId");
    	String organizationPartyId = (String)context.get("organizationPartyId");	
		
		try {
			GenericValue glAccountBal = delegator.findOne("GlAccountBalanceParty", UtilMisc.toMap("glAccountId", glAccountId, "organizationPartyId", organizationPartyId,"partyId",partyId), false);
			glAccountBal.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
 	
 	
 	private static Boolean checkTotalBalanceAccount(Delegator delegator, String organizationPartyId, String glAccountId, BigDecimal amount){
 		try {
 			//init total balance of account gl with party
			BigDecimal _total = BigDecimal.ZERO;
			List<GenericValue> _listGlAccount = delegator.findByAnd("GlAccountBalanceParty", UtilMisc.toMap("organizationPartyId", organizationPartyId,"glAccountId",glAccountId),null,false);
			String _accType =  UtilServices.getAccountType(glAccountId, delegator);
			//init total balance debit,credit of account in Entity GlAccountBalance
			GenericValue originAccount = delegator.findOne("GlAccountBalance", UtilMisc.toMap("glAccountId", glAccountId,"organizationPartyId",organizationPartyId), false);
			if(UtilValidate.isNotEmpty(_listGlAccount)){
				
				for(GenericValue gl : _listGlAccount){
					if(_accType.equals("DEBIT")){
						_total = _total.add(gl.getBigDecimal("openingDrBalance"));
					}else if(_accType.equals("CREDIT")){
						_total = _total.add(gl.getBigDecimal("openingCrBalance"));
					}
				}
			}
			//compare total balance of glACcount with new amount set balance account for party
 			if(originAccount != null){
 				BigDecimal originalBalance = BigDecimal.ZERO;
 				if(_accType.equals("DEBIT")){
 					originalBalance = originAccount.getBigDecimal("openingDrBalance");
				}else if(_accType.equals("CREDIT")){
					originalBalance = originAccount.getBigDecimal("openingCrBalance");
				}
 				if(originalBalance != null){
 					if((originalBalance.subtract(_total)).compareTo(amount) >= 0){
 						return true;
 					}else return false;
 				}
 			}
		} catch (Exception e) {
			Debug.logError("Problems when set balance account for party cause " + e.getMessage(), module);
		}
 		return false;
 	}
 	
 	public static Map<String, Object> getListGlAccountBalance(DispatchContext dctx, Map<String, Object> context){
 		Delegator delegator = dctx.getDelegator();
 		GenericValue userLogin = (GenericValue)context.get("userLogin");
 		Map<String, Object> successResult = FastMap.<String, Object>newInstance();
 		try {
 			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
 			List<EntityCondition> conds = FastList.<EntityCondition>newInstance();
			conds.add(EntityCondition.makeCondition("organizationPartyId", orgId));
			conds.add(EntityCondition.makeCondition("parentGlAccountId", EntityJoinOperator.NOT_EQUAL, null));
			List<GenericValue> glAccountOrgList = delegator.findList("GlAccountOrgDetail", EntityCondition.makeCondition(conds),
					UtilMisc.toSet("glAccountId", "parentGlAccountId", "accountCode", "accountName", "organizationPartyId"), UtilMisc.toList("accountCode"), null, false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			Map<String, Map<String, BigDecimal>> cache = FastMap.newInstance();
			for(GenericValue glAccountOrg: glAccountOrgList){
				Map<String, Object> tempMap = glAccountOrg.getAllFields();
				GenericValue glAccountBalance = delegator.findOne("GlAccountBalance",
						UtilMisc.toMap("glAccountId", glAccountOrg.get("glAccountId"), "organizationPartyId", glAccountOrg.get("organizationPartyId")), false);
				Map<String, BigDecimal> tempOpeningGlBalance = UtilServices.getOpeningGlAccountBalance(delegator, glAccountOrg.getString("glAccountId"), glAccountBalance, orgId, cache);
				tempMap.put("openingDrBalance", tempOpeningGlBalance.get("openingDrBalance"));
				tempMap.put("openingCrBalance", tempOpeningGlBalance.get("openingCrBalance"));
				listReturn.add(tempMap);
			}
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
 		return successResult;
 	}
 	
 	public static Map<String, Object> getGlAccountOrganization(DispatchContext dctx, Map<String, Object> context){
 		Delegator delegator = dctx.getDelegator();
 		GenericValue userLogin = (GenericValue)context.get("userLogin");
 		Map<String, Object> successResult = FastMap.<String, Object>newInstance();
 		try {
 			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            List<EntityCondition> conds = FastList.<EntityCondition>newInstance();
            conds.add(EntityCondition.makeCondition("organizationPartyId", orgId));
            conds.add(EntityCondition.makeCondition("parentGlAccountId", EntityJoinOperator.NOT_EQUAL, null));
            List<GenericValue> glAccountList = delegator.findList("GlAccountOrgDetail", EntityCondition.makeCondition(conds), null, null, null, false);
			List<String> parentGlAccountList = EntityUtil.getFieldListFromEntityList(glAccountList, "parentGlAccountId", true);
			conds.clear();
			conds.add(EntityCondition.makeCondition("organizationPartyId", orgId));
			conds.add(EntityCondition.makeCondition("glAccountId", EntityJoinOperator.NOT_IN, parentGlAccountList));
			List<GenericValue> glAccountOrgList = delegator.findList("GlAccountOrgDetail", EntityCondition.makeCondition(conds),
					UtilMisc.toSet("glAccountId","glAccountTypeId", "parentGlAccountId", "accountCode", "accountName", "organizationPartyId"), UtilMisc.toList("accountCode"), null, false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			for(GenericValue glAccountOrg: glAccountOrgList){
				Map<String, Object> tempMap = glAccountOrg.getAllFields();
				GenericValue glAccountBalance = delegator.findOne("GlAccountBalance",
						UtilMisc.toMap("glAccountId", glAccountOrg.get("glAccountId"), "organizationPartyId", glAccountOrg.get("organizationPartyId")), false);
				if(glAccountBalance != null){
					tempMap.put("openingDrBalance", glAccountBalance.get("openingDrBalance"));
					tempMap.put("openingCrBalance", glAccountBalance.get("openingCrBalance"));
				}
				String accountType = AccountUtils.getAccountType(glAccountOrg.getString("glAccountId"), delegator);
				tempMap.put("accountType", accountType);
				listReturn.add(tempMap);
			}
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
 		return successResult;
 	}
 	
 	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateListGlAccountBalance(DispatchContext dctx, Map<String, Object> context){
 		Delegator delegator = dctx.getDelegator();
 		List<GenericValue> listGlAccountBalance = (List<GenericValue>)context.get("listGlAccountBalance");
 		try {
 			for(GenericValue glAccountBalance: listGlAccountBalance){
 				//Debug.log(module + "::updateListGlAccountBalance, gl_acc " + glAccountBalance.get("glAccountId")+
 				//		", organizationPartyId = " + glAccountBalance.get("organizationPartyId")+
 				//		", openingDrBalance = " + glAccountBalance.get("openingDrBalance")+
 				//		", openingCrBalance = " + glAccountBalance.get("openingCrBalance")
 				//		);
				delegator.createOrStore(glAccountBalance);
 			}
 		} catch (GenericEntityException e) {
 			e.printStackTrace();
 			return ServiceUtil.returnError(e.getLocalizedMessage());
 		}
 		return ServiceUtil.returnSuccess();
 	}
}
