package com.olbius.acc.liability;

import com.olbius.acc.invoice.entity.Invoice;
import com.olbius.acc.payment.entity.Payment;
import com.olbius.basehr.util.PartyUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CustomerLiability implements LiabilityInterface {
	public static final String module = CustomerLiability.class.getName();
	@SuppressWarnings("unchecked")
	@Override
	public List<Liability> getListLiabilities(Map<String,Object> context, DispatchContext dpct, String partyId) throws GenericServiceException {
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<Liability> listLiabilities = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	EntityListIterator listCustomer = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		//Get Customers
		Map<String, Object> getCustomersCtx = FastMap.newInstance();
		getCustomersCtx.put("userLogin", userLogin);
		getCustomersCtx.put("partyIdTo", partyId);
		
		LocalDispatcher dispatcher = dpct.getDispatcher();
//		Map<String, Object> getCustomersRs = dispatcher.runSync("getCustomerByPartyIdTo", getCustomersCtx);
		
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId,
				"roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "INTERNAL_ORGANIZATIO")));
		Boolean beganTransaction = false;
		Integer pagesize = Integer.valueOf((String) parameters.get("pagesize")[0]);
		Integer pagenum = Integer.valueOf((String) parameters.get("pagenum")[0]);
		
		try {
			beganTransaction = TransactionUtil.begin();
			listCustomer = dpct.getDelegator().find("PartyRelationShipAndDetail",
					EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND),null, UtilMisc.toSet("partyIdFrom","fullNameFrom"), listSortFields, opts);
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
			try {
				TransactionUtil.rollback(e1);
			} catch (GenericTransactionException e) {
				e.printStackTrace();
			}
		}
		
		//Get Liability
		List<GenericValue> listCustomers = FastList.newInstance();
		try {
			listCustomers = listCustomer.getPartialList(pagesize*pagenum + 1,pagesize);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			
		}
		
		Boolean isGetTotal = false;
		for(GenericValue customer : listCustomers) {
			Liability liability = new Liability();
			liability.setPartyIdFrom(customer.getString("partyIdFrom"));
			liability.setFullNameFrom(customer.getString("fullNameFrom"));
			liability.setOrganizationPartyId(partyId);
			try {
				String orgName = PartyUtil.getPartyName(dpct.getDelegator(), partyId);
				if(orgName != null) liability.setOrgName(orgName);
			} catch (GenericEntityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			Map<String, Object> getLiabilityPartyCtx = FastMap.newInstance();
			getLiabilityPartyCtx.put("userLogin", userLogin);
			getLiabilityPartyCtx.put("organizationPartyId", partyId);
			getLiabilityPartyCtx.put("partyId", customer.getString("partyIdFrom"));
	    	try {
	    		Map<String, Object> getLiabilityPartyRs = dispatcher.runSync("getLiabilityParty", getLiabilityPartyCtx);
	    		if(ServiceUtil.isSuccess(getLiabilityPartyRs)){
	    			liability.setTotalLiability((BigDecimal)getLiabilityPartyRs.get("totalLiability"));
					liability.setTotalPayable((BigDecimal)getLiabilityPartyRs.get("totalPayable"));
					liability.setTotalReceivable((BigDecimal)getLiabilityPartyRs.get("totalReceivable"));
					try {
						if(!isGetTotal){
							liability.setTotalRows(String.valueOf(listCustomer.getResultsTotalSize()));
							isGetTotal = true;
							try {
								listCustomer.close();	
								TransactionUtil.commit(beganTransaction);
							} catch (GenericTransactionException e1) {
								e1.printStackTrace();
							} catch (GenericEntityException e) {
								e.printStackTrace();
							}
						}
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					listLiabilities.add(liability);	    		
	    		}else return null;
				
			} catch (GenericServiceException e) {
				Debug.log(e.getMessage(), module);
			}			
		}
		
		return listLiabilities;
	}

	@Override
	public List<Invoice> getListInvoices(Map<String,Object> context, DispatchContext ctx) throws GenericServiceException, GenericEntityException {
		List<Invoice> listInvoices = FastList.newInstance();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String organizationPartyId = parameters.get("organizationPartyId")[0];
    	String partyId = (String) parameters.get("partyId")[0];
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
		//Get List Invoices
		context.put("organizationPartyId", organizationPartyId);
		context.put("partyId", partyId);
		
		Map<String, Object> getInvoicesRs = ctx.getDispatcher().runSync("JQGetListInvoiceForLiability", context);
		EntityListIterator listIterator = (EntityListIterator)getInvoicesRs.get("listIterator");
		List<GenericValue> listTmpInvoices = listIterator.getCompleteList();
		listIterator.close();
		for(GenericValue item : listTmpInvoices) {
			Invoice invoice = new Invoice();
			invoice.setInvoiceId(item.getString("invoiceId"));
			invoice.setInvoiceTypeId(item.getString("invoiceTypeId"));
			invoice.setCurrencyUomId(item.getString("currencyUomId"));
			invoice.setInvoiceDate(item.getTimestamp("invoiceDate"));
			invoice.setStatusId(item.getString("statusId"));
			
			Map<String, Object> getInvoiceTotalCtx = FastMap.newInstance();
			getInvoiceTotalCtx.put("invoiceId", item.getString("invoiceId"));
			getInvoiceTotalCtx.put("userLogin", userLogin);
			Map<String, Object> getInvoiceTotalRs = ctx.getDispatcher().runSync("getInvoiceTotal", getInvoiceTotalCtx);
			invoice.setTotal((BigDecimal)getInvoiceTotalRs.get("total"));
			
			Map<String, Object> getInvoiceNotAppliedCtx = FastMap.newInstance();
			getInvoiceNotAppliedCtx.put("invoiceId", item.getString("invoiceId"));
			getInvoiceNotAppliedCtx.put("userLogin", userLogin);
			Map<String, Object> getInvoiceNotAppliedRs = ctx.getDispatcher().runSync("getInvoiceNotApplied", getInvoiceTotalCtx);
			invoice.setAmountToApply((String)getInvoiceNotAppliedRs.get("amountToApply"));
			
			listInvoices.add(invoice);
		}
		return listInvoices;
	}

	@Override
	public List<Payment> getListPayments(Map<String,Object> context, DispatchContext ctx) throws GenericServiceException, GenericEntityException {
		List<Payment> listPayments = FastList.newInstance();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String organizationPartyId = parameters.get("organizationPartyId")[0];
    	String partyId = (String) parameters.get("partyId")[0];
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
		//Get List Payments
		context.put("organizationPartyId", organizationPartyId);
		context.put("partyId", partyId);
		
		Map<String, Object> getPaymentsRs = ctx.getDispatcher().runSync("JQGetListPaymentForLiability", context);
		EntityListIterator listIterator = (EntityListIterator)getPaymentsRs.get("listIterator");
		List<GenericValue> listTmpPayments = listIterator.getCompleteList();
		listIterator.close();
		for(GenericValue item : listTmpPayments) {
			Payment payment = new Payment();
			payment.setPaymentId(item.getString("paymentId"));
			payment.setPaymentTypeId(item.getString("paymentTypeId"));
			payment.setCurrencyUomId(item.getString("currencyUomId"));
			payment.setEffectiveDate(item.getTimestamp("effectiveDate"));
			GenericValue paymentType = ctx.getDelegator().findOne("StatusItem", UtilMisc.toMap("statusId", item.getString("statusId")), false);
			payment.setStatusId((String) paymentType.get("description", (Locale) context.get("locale")));
			payment.setAmount(item.getBigDecimal("amount"));
			Map<String, Object> getInvoiceNotAppliedCtx = FastMap.newInstance();
			getInvoiceNotAppliedCtx.put("paymentId", item.getString("paymentId"));
			getInvoiceNotAppliedCtx.put("userLogin", userLogin);
			Map<String, Object> getPaymentNotAppliedRs = ctx.getDispatcher().runSync("getPaymentNotApplied", getInvoiceNotAppliedCtx);
			payment.setAmountToApply((BigDecimal)getPaymentNotAppliedRs.get("amountToApply"));
			
			listPayments.add(payment);
		}
		return listPayments;
	}
}
