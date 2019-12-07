package com.olbius.acc.liability;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;

import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.accounting.invoice.InvoiceWorker;

public class LiabilityGeneralUtils {
	public static final String module = LiabilityGeneralUtils.class.getName();

 	@SuppressWarnings("unchecked")
	public static Map<String, Object> getLiabilityParty(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String strOrganizationPartyId = (String) context.get("organizationPartyId");
    	String strPartyId = (String) context.get("partyId");
    	List<EntityCondition> listAllConditions = FastList.newInstance();
    	List <GenericValue> listArInvoice = null;
    	List <GenericValue> listApInvoice = null;
    	List <GenericValue> listArPayment = null;
    	List <GenericValue> listApPayment = null;    	
    	List<String> statusIds = new ArrayList<String>();
    	BigDecimal totalPayable = BigDecimal.ZERO;
    	BigDecimal totalReceivable = BigDecimal.ZERO;
    	BigDecimal totalLiability = BigDecimal.ZERO;
    	
    	statusIds.add("INVOICE_IN_PROCESS");
    	statusIds.add("INVOICE_READY");      	
    	try {    		
    	    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALES_INVOICE"),
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strOrganizationPartyId),
                            EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), 
                            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, strPartyId )
                    ), EntityJoinOperator.AND);	    	    	    
    	    
    	    listAllConditions.add(tmpConditon);
    	    
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listArInvoice = delegator.findList("InvoiceAndType", tmpConditon, null, null, null, true);    	
		} catch (Exception e) {
			String errMsg = "Fatal error calling getLiabilityParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	      
    	try {
    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"),
                            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, strOrganizationPartyId),
                            EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), 
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId )
                    ), EntityJoinOperator.AND);	
		listAllConditions = FastList.newInstance();;
    		listAllConditions.add(tmpConditon);
    		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listApInvoice = delegator.findList("InvoiceAndType", tmpConditon, null, null, null, true); 	
		} catch (Exception e) {
			String errMsg = "Fatal error calling getLiabilityParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}    	
    	
    	try {
    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "RECEIPT"),
                            EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strOrganizationPartyId),
                            EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED"), 
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId )
                    ), EntityJoinOperator.AND);	
		listAllConditions = FastList.newInstance();;
    		listAllConditions.add(tmpConditon);
    		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listArPayment = delegator.findList("PaymentAndType", tmpConditon, null, null, null, true); 	
		} catch (Exception e) {
			String errMsg = "Fatal error calling getLiabilityParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}    	
    	
    	try {
    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DISBURSEMENT"),
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strOrganizationPartyId),
                            EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_SENT"), 
                            EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strPartyId )
                    ), EntityJoinOperator.AND);	
		listAllConditions = FastList.newInstance();;
    		listAllConditions.add(tmpConditon);
    		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listApPayment = delegator.findList("PaymentAndType", tmpConditon, null, null, null, true); 	
		} catch (Exception e) {
			String errMsg = "Fatal error calling getLiabilityParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}  
    	
    	// For opening balance
    	GenericValue GlAccountBalanceParty = null;
    	try {
    		GlAccountBalanceParty = delegator.findOne("GlAccountBalanceParty", UtilMisc.toMap("organizationPartyId", strOrganizationPartyId, "partyId", strPartyId, "glAccountId", "131"), false); 	
		} catch (Exception e) {
			String errMsg = "Fatal error calling getLiabilityParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}  
    	
    	if (GlAccountBalanceParty != null && !GlAccountBalanceParty.isEmpty())
    	{
    		totalReceivable = (BigDecimal) GlAccountBalanceParty.getBigDecimal("openingDrBalance");
    		totalPayable = (BigDecimal) GlAccountBalanceParty.getBigDecimal("openingCrBalance");
    	}    	
    	
    	// For Invoice
    	for (int iInv = 0; iInv < listArInvoice.size(); iInv++)
    	{
    		GenericValue invoice = listArInvoice.get(iInv);
    		totalReceivable = totalReceivable.add(InvoiceWorker.getInvoiceNotPayment(invoice));
    	}
    	for (int iInv = 0; iInv < listApInvoice.size(); iInv++)
    	{
    		GenericValue invoice = listApInvoice.get(iInv);
    		totalPayable = totalPayable.add(InvoiceWorker.getInvoiceNotPayment(invoice));
    	} 
    	// For Payment
    	for (int iPam = 0; iPam < listApPayment.size(); iPam++)
    	{
    		GenericValue payment = listApPayment.get(iPam);
    		totalReceivable = totalReceivable.add(PaymentWorker.getPaymentNotTrueApplied(payment));
    	}    
    	
    	for (int iPam = 0; iPam < listArPayment.size(); iPam++)
    	{
    		GenericValue payment = listArPayment.get(iPam);
    		totalPayable = totalPayable.add(PaymentWorker.getPaymentNotTrueApplied(payment));
    	}        
    	
    	totalLiability = totalReceivable.subtract(totalPayable);
    	successResult.put("totalReceivable", totalReceivable);
    	successResult.put("totalPayable", totalPayable);
    	successResult.put("totalLiability", totalLiability);
    	return successResult;
    }

    public static Map<String,Object> getTotalLiabilityDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String,Object> successResult = ServiceUtil.returnSuccess();
        Delegator delegator = (Delegator) ctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<GenericValue> listGV = null;
        List<EntityCondition> listAllConditions = FastList.newInstance();
        BigDecimal totalInvoice = BigDecimal.ZERO;
        BigDecimal totalAmountToApply = BigDecimal.ZERO;

        String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("partyId"));
        String distributorId = (String)context.get("distributorId");
        String type = (String)context.get("type");

        if(UtilValidate.isEmpty(type))
            return ServiceUtil.returnError("Required type");
        if(UtilValidate.isEmpty(distributorId))
            return ServiceUtil.returnError("Required Distributor get detail Liability!");

        if (UtilValidate.isNotEmpty(organizationPartyId)) {
            try {
                listAllConditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, type.concat("_INVOICE")));
                listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,(type.equals("SALES") ? distributorId : organizationPartyId) ));
                listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("INVOICE_IN_PROCESS", "INVOICE_READY")));
                listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, (type.equals("SALES") ? organizationPartyId : distributorId )));

                listGV = delegator.findList("InvoiceAndType", EntityCondition.makeCondition(listAllConditions),null,null,null,false);
                if(UtilValidate.isNotEmpty(listGV))
                {
                    if(UtilValidate.isNotEmpty(listGV))
                        for(GenericValue l : listGV){
                            //get Total Invoice
                            BigDecimal total = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator, l.getString("invoiceId"));
                            totalInvoice =  totalInvoice.add(total);
                            //get amount Not Pay
                            BigDecimal amountToApply = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceNotTrueApplied(delegator, l.getString("invoiceId"));
                            totalAmountToApply =  totalAmountToApply.add(amountToApply);
                        }
                    successResult.put("TotalRows", listGV.size());
                    successResult.put("totalInvoice",totalInvoice);
                    successResult.put("totalAmountToApply",totalAmountToApply);
                }
            } catch (Exception e) {
                String errMsg = "Fatal error calling jqGetListCDARInvoice service: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        }
        return successResult;
    }
}