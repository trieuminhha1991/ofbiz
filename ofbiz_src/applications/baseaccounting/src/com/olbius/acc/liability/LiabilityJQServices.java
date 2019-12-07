package com.olbius.acc.liability;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;

public class LiabilityJQServices {
	
	public static final String module = LiabilityJQServices.class.getName();
	
    public static Map<String, Object> jqGetListCustLiabilities(DispatchContext ctx, Map<String, Object> context) {
    	
    	
    	LiabilityInterface custLiability = new CustomerLiability();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> resultMap = FastMap.newInstance();
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(ctx.getDelegator(),(String) userLogin.get("userLoginId"));
    	try {
    		List<Liability> listRs = custLiability.getListLiabilities(context, ctx, organizationPartyId);
			resultMap.put("listIterator", listRs);
			if(listRs.size() > 0){
				resultMap.put("TotalRows", String.valueOf(listRs.get(0).getTotalRows()));
			}else resultMap.put("TotalRows","0");
			
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
    	return resultMap;
	}
    
    public static Map<String, Object> getLiabilityInvoiceData(DispatchContext ctx, Map<String, Object> context) {
    	LiabilityInterface custLiability = new CustomerLiability();
    	Map<String, Object> resultMap = FastMap.newInstance();
    	try {
			resultMap.put("listIterator", custLiability.getListInvoices(context, ctx));
		} catch (GeneralException e) {
			Debug.log(e.getMessage(), module);
		}
    	return resultMap;
	}
    
    public static Map<String, Object> getLiabilityPaymentData(DispatchContext ctx, Map<String, Object> context) {
    	LiabilityInterface custLiability = new CustomerLiability();
    	Map<String, Object> resultMap = FastMap.newInstance();
    	try {
			resultMap.put("listIterator", custLiability.getListPayments(context, ctx));
		} catch (GeneralException e) {
			Debug.log(e.getMessage(), module);
		}
    	return resultMap;
	}
    
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions =  (List<EntityCondition>) (context.get("listAllConditions") != null ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<EntityCondition> listConditions = new FastList<EntityCondition>();
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");    	
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	String strOrganizationPartyId = (String) context.get("organizationPartyId");
    	String strPartyId = (String) context.get("partyId");
		List<EntityCondition> tmpAPInvoiceType = FastList.newInstance();
		tmpAPInvoiceType.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
		tmpAPInvoiceType.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId));
		tmpAPInvoiceType.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, strOrganizationPartyId));
		EntityCondition tmpApInvoice = EntityCondition.makeCondition( tmpAPInvoiceType, EntityJoinOperator.AND);
		listConditions.add(tmpApInvoice);
		List<EntityCondition> tmpArInvoiceType = FastList.newInstance();
		tmpArInvoiceType.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
		tmpArInvoiceType.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strOrganizationPartyId));
		tmpArInvoiceType.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, strPartyId));
		EntityCondition tmpArInvoice = EntityCondition.makeCondition( tmpArInvoiceType, EntityJoinOperator.AND);		
		listConditions.add(tmpArInvoice);
		EntityCondition tmpConditon = EntityCondition.makeCondition(listConditions,EntityJoinOperator.OR);
		
		listAllConditions.add(tmpConditon);
		
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }	
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPayment(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.get("listAllConditions") != null ?  context.get("listAllConditions") : FastList.newInstance());
		List<EntityCondition> listConditions = new FastList<EntityCondition>();
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");    	
    	String strOrganizationPartyId = (String) context.get("organizationPartyId");
    	String strPartyId = (String) context.get("partyId");
		List<EntityCondition> tmpArPaymentType = FastList.newInstance();
		tmpArPaymentType.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "RECEIPT"));
		tmpArPaymentType.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId));
		tmpArPaymentType.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strOrganizationPartyId));
		EntityCondition tmpArPayment = EntityCondition.makeCondition( tmpArPaymentType, EntityJoinOperator.AND);
		listConditions.add(tmpArPayment);
		List<EntityCondition> tmpApPaymentType = FastList.newInstance();
		tmpApPaymentType.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DISBURSEMENT"));
		tmpApPaymentType.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strOrganizationPartyId));
		tmpApPaymentType.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strPartyId));
		EntityCondition tmpApPayment = EntityCondition.makeCondition( tmpApPaymentType, EntityJoinOperator.AND);		
		listConditions.add(tmpApPayment);
		EntityCondition tmpConditon = EntityCondition.makeCondition(listConditions,EntityJoinOperator.OR);
		
		listAllConditions.add(tmpConditon);
		
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("PaymentAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPayment service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }		
    
    
   	@SuppressWarnings("unchecked")
       public static Map<String, Object> jqGetLiabilityDetailDistributor(DispatchContext ctx, Map<String, Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
   		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
   		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	GenericValue userLogin = (GenericValue) context.get("userLogin");
       	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("partyId"));
       	String distributorId = parameters.get("distributorId")[0];
       	String type = parameters.containsKey("type") ? parameters.get("type")[0] : null;
       	List<GenericValue> listLiability  = FastList.newInstance();
       	List<Map<String,Object>> listRs = FastList.newInstance();
       	int pagesize = Integer.parseInt(parameters.containsKey("pagesize") ? parameters.get("pagesize")[0] : null);
       	int pagenum = Integer.parseInt(parameters.containsKey("pagenum") ?parameters.get("pagenum")[0]:null);
       	if(type == null)
       		return ServiceUtil.returnError("Required type");
       	if(UtilValidate.isEmpty(distributorId)) 
       		return ServiceUtil.returnError("Required Distributor get detail Liability!");
       	else
	       	if (UtilValidate.isNotEmpty(organizationPartyId)) {
	       		try {
	           		EntityCondition tmpConditon = 
	                           EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
	                                   EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, type.concat("_INVOICE")),
	                                   EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,(type.equals("SALES") ? distributorId : organizationPartyId) ),
	                                   EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("INVOICE_IN_PROCESS", "INVOICE_READY")), 
	                                   EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, (type.equals("SALES") ? organizationPartyId : distributorId ))
	                           ), EntityJoinOperator.AND);
	           		listAllConditions.add(tmpConditon);
	           		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	           		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
	           		if(UtilValidate.isNotEmpty(listIterator))
	           		{
	           			listLiability = listIterator.getPartialList(pagesize*pagenum + 1,pagesize);
	           			if(UtilValidate.isNotEmpty(listLiability))
	           				for(GenericValue l : listLiability){
	           					Map<String,Object> Temp = FastMap.newInstance();
	           					Temp.put("invoiceId",l.getString("invoiceId"));
	           					Temp.put("invoiceTypeId",l.getString("invoiceTypeId"));
	           					Temp.put("invoiceDate",l.getString("invoiceDate"));
	           					Temp.put("statusId",l.getString("statusId"));
	           					Temp.put("description",l.getString("description"));
	           					Temp.put("partyIdFrom",l.getString("partyIdFrom"));
	           					Temp.put("partyId",l.getString("partyId"));
	           					//get Total Invoice
	           					BigDecimal total = InvoiceWorker.getInvoiceTotal(delegator, l.getString("invoiceId"));
	           					Temp.put("total",total);
	           					//get amount Not Pay
	           					BigDecimal amountToApply = InvoiceWorker.getInvoiceNotTrueApplied(delegator, l.getString("invoiceId"));
	           					Temp.put("amountToApply",amountToApply);
	           					String _fullNameFrom = PartyUtil.getPartyName(delegator, l.getString("partyIdFrom"));
	           					String _fullNameTo = PartyUtil.getPartyName(delegator, l.getString("partyId"));
	           					Temp.put("fullNameFrom",_fullNameFrom);
	           					Temp.put("fullNameTo",_fullNameTo);
	           					listRs.add(Temp);
	           				}
	           		}
	       		} catch (Exception e) {
	       			String errMsg = "Fatal error calling jqGetListCDARInvoice service: " + e.toString();
	       			Debug.logError(e, errMsg, module);
	       		}finally {
	       			try {
	       			  	successResult.put("TotalRows", String.valueOf(listIterator.getResultsTotalSize()));
						listIterator.close();
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
	       		}
	       	}
       	successResult.put("listIterator", listRs);
       	return successResult;
       }
}
