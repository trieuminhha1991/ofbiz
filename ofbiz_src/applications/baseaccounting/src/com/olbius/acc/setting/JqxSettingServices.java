package com.olbius.acc.setting;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.text.ParseException;
import java.util.*;

public class JqxSettingServices {
	
	public static final String module = JqxSettingServices.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListGLAccountChart(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListGLAccountChart service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }	 
	/*******************************************jqx services for organization
	 * @throws GenericEntityException *************************************************************************/
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListChartOfAccountOrigination(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	if(organizationPartyId != null){
    		listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
    	}
    	
    	List<GenericValue> glAccounts = delegator.findList("GlAccount", EntityCondition.makeCondition("parentGlAccountId", EntityOperator.NOT_EQUAL, null),
    			null, null, null, false);
    	if (UtilValidate.isNotEmpty(glAccounts)) {
    		List<String> parentGlAccountIds = EntityUtil.getFieldListFromEntityList(glAccounts, "parentGlAccountId", true);
    		if (UtilValidate.isNotEmpty(parentGlAccountIds)) {
    			listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.NOT_IN, parentGlAccountIds));
    		}
    	}
    	
    	try {
    		listIterator = delegator.find("GlOrganizationClassAndParent", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListChartOfAccount service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListChartOfAccountOriginationTrans(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        if(organizationPartyId != null){
            listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
        }

        try {
            listIterator = delegator.find("GlOrganizationClassAndParent", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListChartOfAccount service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPosTerminalBank(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		try {
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-thruDate");
				listSortFields.add("-fromDate");
			}
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PosTerminalBankDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPosTerminalBank service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListFinAccountTypeGlAccount(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	mapCondition.put("organizationPartyId", organizationPartyId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listIterator = delegator.find("FinAccountTypeGlAccount", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListFinAccountTypeGlAccount service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	 
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getGLAccountTypeDedault(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	mapCondition.put("organizationPartyId", organizationPartyId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listIterator = delegator.find("GlAccountTypeDefaultAndDetail", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getGLAccountTypeDedault service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListGlAccountTypeJQ(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("description");
			}
			tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			listIterator = delegator.find("GlAccountTypeAndGlAccount", tmpConditon, null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListGlAccountType service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListGlAccountTypeDefaultJQ(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			List<GenericValue> listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "GlAccountTypeDefaultDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			if (UtilValidate.isNotEmpty(listIterator)) {
				for (GenericValue item : listIterator) {
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(item);
					map.put("description", item.get("description", locale));
					listReturn.add(map);
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListGlAccountTypeDefaultJQ service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listReturn);
		return successResult;
	}
	 
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getListInvoicesItemTypesGlAccount(DispatchContext dpct, Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		Map<String,Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String invItemTypePrefix = (String) (parameters.get("invItemTypePrefix") != null ? parameters.get("invItemTypePrefix")[0] : "INV");
			invItemTypePrefix += "_%";
			listAllConditions.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityJoinOperator.LIKE,invItemTypePrefix));
			EntityListIterator listIterator = delegator.find("InvoicesItemTypesGlAccountDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			Debug.logError(e,"An error occured while getListInvoicesItemTypesGlAccount", module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> getPaymentMethodTypeNotDedault(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	EntityCondition tmpConditon = EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.NOT_EQUAL,organizationPartyId);
    	
    	Locale locale = (Locale) context.get("locale");
    	List<Map<String,Object>> listRs = FastList.newInstance();
    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		int end = start + pagesize;
    		
    		List<GenericValue> listPaymentMTOfOrganization = delegator.findList("PaymentMethodTypeGlAccountDetail", EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
    		List<String> listPaymentMethodUsed = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(listPaymentMTOfOrganization)){
    			for(GenericValue var  : listPaymentMTOfOrganization){
    				listPaymentMethodUsed.add(var.getString("paymentMethodTypeId"));
    			}
    		}
    		listIterator = delegator.find("PaymentMethodType", EntityCondition.makeCondition("paymentMethodTypeId", EntityJoinOperator.NOT_IN, UtilValidate.isNotEmpty(listPaymentMethodUsed) ? listPaymentMethodUsed : UtilMisc.toList("")), null, null, listSortFields, opts);
    		
    		if(pagesize > listIterator.getResultsTotalSize()){
    			pagesize = 	listIterator.getResultsTotalSize();
    		}
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				mapGv.put("paymentMethodTypeId", gv.getString("paymentMethodTypeId"));
    				GenericValue glType = delegator.findOne("PaymentMethodType", false, UtilMisc.toMap("paymentMethodTypeId", gv.getString("paymentMethodTypeId")));
    				mapGv.put("description",(UtilValidate.isNotEmpty(glType) ? glType.get("description",locale) : ""));
    				listRs.add(mapGv);
    			}
    		}
    		listRs = EntityMiscUtil.filterMap(listRs, listAllConditions);
    		listRs = EntityMiscUtil.sortList(listRs, listSortFields);
    		resultSuccess.put("listIterator", listRs);
    		if(!listAllConditions.isEmpty()){
    			resultSuccess.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			resultSuccess.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getGLAccountTypeNotDedault service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return resultSuccess;		    	
    }
	 
	@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> getListGLAccountOACsData(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	listSortFields.add("+glAccountId");
    	try {
    		String getAlGlAccount = parameters.containsKey("getAlGlAccount") ? (String) parameters.get("getAlGlAccount")[0] : null;
    		if(UtilValidate.isNotEmpty(getAlGlAccount) || getAlGlAccount != null){
    			if(getAlGlAccount.equals("getAll")){
        			listIterator = delegator.find("GlacountAndParent", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    			}
    		}else{
    			if(organizationPartyId != null) listAllConditions.add(EntityCondition.makeCondition("organizationPartyId",organizationPartyId));
        		listIterator = delegator.find("GlAccountOrganizationAndClass", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListGLAccountOACsData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	 
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getGLAccountTypeNotDedault(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	
    	Locale locale = (Locale) context.get("locale");
    	List<Map<String,Object>> listRs = FastList.newInstance();
    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		
    		List<GenericValue> listPaymentOfOrganization = delegator.findList("GlAccountTypeDefaultAndDetail", EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
    		List<String> listPaymentUsed = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(listPaymentOfOrganization)){
    			for(GenericValue var  : listPaymentOfOrganization){
    				listPaymentUsed.add(var.getString("glAccountTypeId"));
    			}
    		}
    		listIterator = delegator.find("GlAccountType", EntityCondition.makeCondition("glAccountTypeId", EntityJoinOperator.NOT_IN, UtilValidate.isNotEmpty(listPaymentUsed) ? listPaymentUsed : UtilMisc.toList("") ), null, null, listSortFields, opts);
    		if(pagesize > listIterator.getResultsTotalSize()){
    			pagesize = 	listIterator.getResultsTotalSize();
    		}
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				mapGv.put("glAccountTypeId", gv.getString("glAccountTypeId"));
    				GenericValue glType = delegator.findOne("GlAccountType", false, UtilMisc.toMap("glAccountTypeId", gv.getString("glAccountTypeId")));
    				mapGv.put("description",(UtilValidate.isNotEmpty(glType) ? glType.get("description",locale) : ""));
    				listRs.add(mapGv);
    			}
    		}
    		listRs = EntityMiscUtil.filterMap(listRs, listAllConditions);
    		listRs = EntityMiscUtil.sortList(listRs, listSortFields);
    		resultSuccess.put("listIterator", listRs);
    		if(!listAllConditions.isEmpty()){
    			resultSuccess.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			resultSuccess.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getGLAccountTypeNotDedault service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return resultSuccess;	    	
    }
	 
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getFinAccountTypeNotGlAccount(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	
    	Locale locale = (Locale) context.get("locale");
    	List<Map<String,Object>> listRs = FastList.newInstance();
    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		
    		List<GenericValue> listPaymentOfOrganization = delegator.findList("FinAccountTypeGlAccount", EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
    		List<String> listPaymentUsed = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(listPaymentOfOrganization)){
    			for(GenericValue var  : listPaymentOfOrganization){
    				listPaymentUsed.add(var.getString("finAccountTypeId"));
    			}
    		}
    		listIterator = delegator.find("FinAccountType", EntityCondition.makeCondition("finAccountTypeId", EntityJoinOperator.NOT_IN,(UtilValidate.isNotEmpty(listPaymentUsed) ? listPaymentUsed : UtilMisc.toList(""))), null, null, listSortFields, opts);
    		if(pagesize > listIterator.getResultsTotalSize()){
    			pagesize = 	listIterator.getResultsTotalSize();
    		}
    		
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				mapGv.put("finAccountTypeId", gv.getString("finAccountTypeId"));
    				GenericValue glType = delegator.findOne("FinAccountType", false, UtilMisc.toMap("finAccountTypeId", gv.getString("finAccountTypeId")));
    				mapGv.put("description",(UtilValidate.isNotEmpty(glType) ? glType.get("description",locale) : ""));
    				listRs.add(mapGv);
    			}
    		}
    		listRs = EntityMiscUtil.filterMap(listRs, listAllConditions);
    		listRs = EntityMiscUtil.sortList(listRs, listSortFields);
    		resultSuccess.put("listIterator", listRs);
    		if(!listAllConditions.isEmpty()){
    			resultSuccess.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			resultSuccess.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getFinAccountTypeNotGlAccount service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return resultSuccess;
    }
	 
	@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> getPaymentTypeGlAccountTypeNotDedault(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	EntityCondition tmpConditon = EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.NOT_EQUAL,organizationPartyId);
    	
    	Locale locale = (Locale) context.get("locale");
    	List<Map<String,Object>> listRs = FastList.newInstance();
    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		int end = start + pagesize;
    		
    		List<GenericValue> listPaymentOfOrganization = delegator.findList("PaymentGlAccountTypeMap", EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
    		List<String> listPaymentUsed = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(listPaymentOfOrganization)){
    			for(GenericValue var  : listPaymentOfOrganization){
    				listPaymentUsed.add(var.getString("paymentTypeId"));
    			}
    		}
    		listIterator = delegator.find("PaymentType", EntityCondition.makeCondition("paymentTypeId", EntityJoinOperator.NOT_IN, UtilValidate.isNotEmpty(listPaymentUsed) ? listPaymentUsed : UtilMisc.toList("")), null, null, listSortFields, opts);
    		
    		if(pagesize > listIterator.getResultsTotalSize()){
    			pagesize = 	listIterator.getResultsTotalSize();
    		}
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				mapGv.put("paymentTypeId", gv.getString("paymentTypeId"));
    				GenericValue glType = delegator.findOne("PaymentType", false, UtilMisc.toMap("paymentTypeId", gv.getString("paymentTypeId")));
    				mapGv.put("description",(UtilValidate.isNotEmpty(glType) ? glType.get("description",locale) : ""));
    				listRs.add(mapGv);
    			}
    		}
    		listRs = EntityMiscUtil.filterMap(listRs, listAllConditions);
    		listRs = EntityMiscUtil.sortList(listRs, listSortFields);
    		resultSuccess.put("listIterator", listRs);
    		if(!listAllConditions.isEmpty()){
    			resultSuccess.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			resultSuccess.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getPaymentMethodTypeGlAccountTypeNotDedault service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return resultSuccess;		    	
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListPaymentTypeJQ(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
		int start = pagenum * pagesize;
		int end = start + pagesize;
		try {
			String organizationId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("organizationPartyId", null),
								EntityJoinOperator.OR,
								EntityCondition.makeCondition("organizationPartyId", organizationId)));
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("paymentTypeId");
			}
			List<GenericValue> listPaymentType = delegator.findList("PaymentTypeAndGlAccountType", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			int totalRow = listPaymentType.size();
			if(end > totalRow){
				end = totalRow;
			}
			listPaymentType = listPaymentType.subList(start, end);
			for(GenericValue paymentType: listPaymentType){
				Map<String, Object> tempMap = paymentType.getAllFields();
				tempMap.put("description", paymentType.get("description", locale));
				//tempMap.put("parentTypeDesc", paymentType.get("parentTypeDesc"));
				listReturn.add(tempMap);
			}
			successResult.put("listIterator", listReturn);
			successResult.put("TotalRows", String.valueOf(totalRow));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	 
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getVarianceReasonGlAccounts(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
        //GenericValue userLogin = (GenericValue) context.get("userLogin");
    	//String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));    	
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		listIterator = delegator.find("VarianceReasonGlAccountDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetVarianceReasonGlAccounts service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> getVarianceReasonNotGlAccounts(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
    	try {
    		Locale locale = (Locale) context.get("locale");
	    	List<Map<String,Object>> listRs = FastList.newInstance();
    		List<GenericValue> listVarianceOfOrganization = delegator.findList("VarianceReasonGlAccount", EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
    		List<String> listVarianceUsed = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(listVarianceOfOrganization)){
    			for(GenericValue var  : listVarianceOfOrganization){
    				listVarianceUsed.add(var.getString("varianceReasonId"));
    			}
    		}
    		listIterator = delegator.find("VarianceReason", EntityCondition.makeCondition("varianceReasonId", EntityJoinOperator.NOT_IN, UtilValidate.isNotEmpty(listVarianceUsed) ? listVarianceUsed : UtilMisc.toList("")), null, null, listSortFields, opts);
    		
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		int end = start + pagesize;
    		
    		if(pagesize > listIterator.getResultsTotalSize()){
    			pagesize = 	listIterator.getResultsTotalSize();
    		}
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				mapGv.put("varianceReasonId", gv.getString("varianceReasonId"));
    				GenericValue glType = delegator.findOne("VarianceReason", false, UtilMisc.toMap("varianceReasonId", gv.getString("varianceReasonId")));
    				mapGv.put("description",(UtilValidate.isNotEmpty(glType) ? glType.get("description",locale) : ""));
    				listRs.add(mapGv);
    			}
    		}
    		listRs = EntityMiscUtil.filterMap(listRs, listAllConditions);
    		listRs = EntityMiscUtil.sortList(listRs, listSortFields);
    		resultSuccess.put("listIterator", listRs);
    		if(!listAllConditions.isEmpty()){
    			resultSuccess.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			resultSuccess.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    		
    		
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getVarianceReasonNotGlAccounts service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return resultSuccess;		    	
    }
	  
	@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqgetListCreditCardTypeNotGlAccount(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	
    	Locale locale = (Locale) context.get("locale");
    	List<Map<String,Object>> listRs = FastList.newInstance();
    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
    	try {
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		int end = start + pagesize;
    		
    		List<GenericValue> listPaymentOfOrganization = delegator.findList("CreditCardTypeGlAccount", EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
    		List<String> listPaymentUsed = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(listPaymentOfOrganization)){
    			for(GenericValue var  : listPaymentOfOrganization){
    				listPaymentUsed.add(var.getString("cardType"));
    			}
    		}
    		
    		List<EntityCondition> listConds = FastList.newInstance();
    		listConds.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CREDIT_CARD_TYPE"));
    		listConds.add(EntityCondition.makeCondition("enumId", EntityJoinOperator.NOT_IN, UtilValidate.isNotEmpty(listPaymentUsed) ? listPaymentUsed : UtilMisc.toList("")));
    		EntityCondition cardTypeCond = EntityCondition.makeCondition(listConds, EntityOperator.AND);
    		
    		listIterator = delegator.find("Enumeration",cardTypeCond, null, null, listSortFields, opts);
    		
    		if(pagesize > listIterator.getResultsTotalSize()){
    			pagesize = 	listIterator.getResultsTotalSize();
    		}
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				mapGv.put("cardType", gv.getString("enumId"));
    				GenericValue glType = delegator.findOne("Enumeration", false, UtilMisc.toMap("enumId", gv.getString("enumId")));
    				mapGv.put("description",(UtilValidate.isNotEmpty(glType) ? glType.get("description",locale) : ""));
    				listRs.add(mapGv);
    			}
    		}
    		listRs = EntityMiscUtil.filterMap(listRs, listAllConditions);
    		listRs = EntityMiscUtil.sortList(listRs, listSortFields);
    		resultSuccess.put("listIterator", listRs);
    		if(!listAllConditions.isEmpty()){
    			resultSuccess.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			resultSuccess.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqgetListCreditCardTypeNotGlAccount service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return resultSuccess;		    
    }
	  
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListCreditCardTypeGlAccount(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	mapCondition.put("organizationPartyId", organizationPartyId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		
    		DynamicViewEntity view = new DynamicViewEntity();
    		view.setEntityName("CreditCardAndEnumeration");
    		view.addMemberEntity("CC", "CreditCardTypeGlAccount");
    		view.addMemberEntity("EN", "Enumeration");
    		view.addMemberEntity("GL", "GlAccount");
    		view.addAlias("CC", "cardType");
    		view.addAlias("CC", "organizationPartyId");
    		view.addAlias("CC", "glAccountId");
    		view.addAlias("EN", "enumCode");
    		view.addAlias("EN", "enumId");
    		view.addAlias("GL", "glAccountId");
    		view.addAlias("GL", "accountName");
    		view.addAlias("GL", "accountCode");
    		view.addViewLink("EN","CC",Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("enumId", "cardType")));
    		view.addViewLink("CC","GL",Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("glAccountId", "glAccountId")));
    		try {
    			view.makeModelViewEntity(delegator);
			} catch (Exception e) {
				Debug.log("Fatal Error when create View Entity in service jqListCreditCardTypeGlAccount",module,e);
				return ServiceUtil.returnError("Fatal Error when create View Entity in service jqListCreditCardTypeGlAccount"  +e.toString());
			}
    			if(UtilValidate.isNotEmpty(listAllConditions)){
        			for(int i  = 0; i < listAllConditions.size();i++){
        				String listCondStr = listAllConditions.get(i).toString().trim();
        				String fieldCond = listCondStr.split(" ")[0];
        				int index = listCondStr.indexOf("%");
        				if(index != -1){
        					String tmpValue = listCondStr.substring(index + 1, listCondStr.length() - 1);
        					if(fieldCond.equals("cardType")){
        	    				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("enumCode", EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)), EntityJoinOperator.OR));
        	    				listAllConditions.remove(i);
        	    				break;
        					}else if(fieldCond.equals("glAccountId")){
        						listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("accountName", EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)), EntityJoinOperator.OR));
        	    				listAllConditions.remove(i);
        	    				break;
        					}else continue;
        				}
        			}
        		}	
        		
    		listIterator = delegator.findListIteratorByCondition(view, EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListCreditCardTypeGlAccount service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	  
	@SuppressWarnings("unchecked")
 	public static Map<String, Object> jqListTaxAuthorityGLAccounts(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	mapCondition.put("organizationPartyId", organizationPartyId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		if(UtilValidate.isNotEmpty(listAllConditions)){
    			for(int i  = 0; i < listAllConditions.size();i++){
    				String listCondStr = listAllConditions.get(i).toString().trim();
    				String fieldCond = listCondStr.split(" ")[0];
    				int index = listCondStr.indexOf("%");
    				if(index != -1){
    					String tmpValue = listCondStr.substring(index + 1, listCondStr.length() - 1);
    					if(fieldCond.equals("glAccountId")){
    	    				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("accountCode", EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)), EntityJoinOperator.OR));
    	    				listAllConditions.remove(i);
    	    				break;
    					}else continue;
    				}
    			}
    		}	
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listIterator = delegator.find("TaxAuthorityGlAccountAndGeo", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListTaxAuthorityGLAcounts service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
		
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListFixedAssetTypeGlAccount(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	mapCondition.put("organizationPartyId", organizationPartyId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listIterator = delegator.find("FixedAssetTypeGlAccount", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListFixedAssetTypeGlAccount service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
		
		
    /** Method to get the period type.  These are identified in GroupPeriodType with enumTypeId "FISCAL_ACCOUNT". */
    public static List<String> getPeriodTypeIds(Delegator delegator) throws GenericEntityException {
        List<String> typeIds = FastList.newInstance();
        List<GenericValue> groupPeriodTypes = delegator.findByAnd("PeriodType", UtilMisc.toMap("groupPeriodTypeId", "FISCAL_ACCOUNT"), null, true);
        for (GenericValue groupPeriodType : groupPeriodTypes) {
            typeIds.add(groupPeriodType.getString("periodTypeId"));
        }
        return typeIds;
    }	

		
    /** Method to get the period type.  These are identified in GroupPeriodType with enumTypeId "FISCAL_ACCOUNT". */
    public static List<String> getPartiesAcctgPreferenceIds(Delegator delegator) throws GenericEntityException {
        List<String> partyIds = FastList.newInstance();
        List<GenericValue> partyAcctgPreferenceList = delegator.findByAnd("PartyAcctgPreference", null, null, true);
        for (GenericValue partyAcctgPreference : partyAcctgPreferenceList) {
        	partyIds.add(partyAcctgPreference.getString("partyId"));
        }
        return partyIds;
    }	
		
    @SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqListCustomTimePeriod(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityCondition periodTypeList = EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, getPeriodTypeIds(delegator));
    	listAllConditions.add(periodTypeList);   	
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listIterator = delegator.find("CustomTimePeriodsWithOrgFullName", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListCustomTimePeriod service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListOpenTimePeriod(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	mapCondition.put("organizationPartyId", organizationPartyId);
    	mapCondition.put("isClosed", "N");
    	mapCondition.put("groupPeriodTypeId", "FISCAL_ACCOUNT");
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listIterator = delegator.find("CustomTimePeriodAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListOpenTimePeriod service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
			 
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListClosedTimePeriod(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("organizationPartyId", organizationPartyId);
    	mapCondition.put("isClosed", "Y");
    	mapCondition.put("groupPeriodTypeId", "FISCAL_ACCOUNT");
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listIterator = delegator.find("CustomTimePeriodAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListClosedTimePeriod service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> listCostAccMapDepartment(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			EntityListIterator listIterator = delegator.find("CostAccMapDepartmentDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListClosedTimePeriod service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAllocationCostPeriodJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("-fromDate");
		}
		try {
			EntityListIterator listIterator = delegator.find("AllocationCostPeriod", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListInvoiceType(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        try {
            listIterator = delegator.find("InvoiceType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListInvoiceType service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings({ "unchecked" })
    public static Map<String, Object> jqGetPOListTaxProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        //Locale locale = (Locale) context.get("locale");
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");

        EntityListIterator productVirtualInterator = null;
        try {
            listAllConditions.add(EntityCondition.makeCondition("productTypeId", "PRD_TAX_INV_MANL"));
            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("-createdDate");
            }

            Set<String> listSelectFields = FastSet.newInstance();
            listSelectFields.add("productId");
            listSelectFields.add("productCode");
            listSelectFields.add("primaryProductCategoryId");
            listSelectFields.add("productName");
            listSelectFields.add("quantityUomId");
            listSelectFields.add("longDescription");
            listSelectFields.add("isVirtual");
            listSelectFields.add("categoryName");
            listSelectFields.add("salesDiscontinuationDate");
            listSelectFields.add("purchaseDiscontinuationDate");
            // internalName, brandName, productWeight, weightUomId
            productVirtualInterator = delegator.find("ProductAndCategoryPrimary", EntityCondition.makeCondition(listAllConditions), null, listSelectFields, listSortFields, opts);
            List<GenericValue> listProductVirtual = SalesUtil.processIterator(productVirtualInterator, parameters, successResult);
            List<Map<String, Object>> listProducts = FastList.newInstance();
            if (UtilValidate.isNotEmpty(listProductVirtual)) {
                for (GenericValue x : listProductVirtual) {
                    Map<String, Object> mapProduct = x.getAllFields();
                    List<GenericValue> listProductAssoc = ProductWorker.getChildrenAssocProduct(x.getString("productId"), delegator, null);
                    if (UtilValidate.isNotEmpty(listProductAssoc)) {
                        List<Map<String, Object>> listProductVariant = FastList.newInstance();
                        for (GenericValue z : listProductAssoc) {
                            GenericValue productVariant = EntityUtil.getFirst(delegator.findList("ProductAndCategoryPrimary", EntityCondition.makeCondition("productId", z.getString("productIdTo")), listSelectFields, null, null, false));
                            if (productVariant != null) {
                                Map<String, Object> mapProductVariant = FastMap.newInstance();
                                mapProductVariant.put("productId", productVariant.getString("productId"));
                                mapProductVariant.put("productCode", productVariant.getString("productCode"));
                                mapProductVariant.put("primaryProductCategoryId", productVariant.getString("primaryProductCategoryId"));
                                mapProductVariant.put("productName", productVariant.getString("productName"));
                                mapProductVariant.put("quantityUomId", productVariant.getString("quantityUomId"));
                                mapProductVariant.put("longDescription", productVariant.getString("longDescription"));
                                mapProductVariant.put("isVirtual", productVariant.getString("isVirtual"));
                                mapProductVariant.put("categoryName", productVariant.getString("categoryName"));
                                mapProductVariant.put("taxCatalogs", com.olbius.basesales.product.ProductServices.getTaxCatalogs(delegator, productVariant.getString("productId")));

                                listProductVariant.add(mapProductVariant);
                            }
                        }
                        mapProduct.put("rowDetail", listProductVariant);
                        mapProduct.put("numChild", listProductVariant.size());
                    }
                    mapProduct.put("taxCatalogs", com.olbius.basesales.product.ProductServices.getTaxCatalogs(delegator, x.getString("productId")));
                    listProducts.add(mapProduct);
                }
            }
            successResult.put("listIterator", listProducts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetPOListTaxProducts service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListInvoiceItemType(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        try {
            listIterator = delegator.find("InvoiceItemType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListInvoiceItemType service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqListGlAccountTypePaymentType(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        //Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        Map<String, String> mapCondition = new HashMap<String, String>();
        //GenericValue userLogin = (GenericValue) context.get("userLogin");
        //String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        listAllConditions.add(tmpConditon);
        try {
            tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
            listIterator = delegator.find("PaymentGlAccountTypeMap", tmpConditon, null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqListGlAccountTypePaymentType service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCustomerTimePayment(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        try {
            listIterator = delegator.find("CustomerTimePaymentInfoView", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling CustomerTimePaymentInfoView service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }
}
