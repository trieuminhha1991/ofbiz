package com.olbius.acc.invoice;

import com.olbius.acc.invoice.entity.Invoice;
import com.olbius.acc.utils.UtilServices;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ArInvoiceList implements InvoiceList {
	
	public List<String> INVOICE_TYPE = FastList.newInstance();

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getListInvoice(DispatchContext dispatcher, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		//Get local dispatcher
		List<Invoice> listInvoices = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("-invoiceDate");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	Map<String, Object> result = FastMap.newInstance();
    	
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//get All type for invoices
    	try {
			UtilServices.getAllInvoiceType(delegator, INVOICE_TYPE, "SALES_INVOICE");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	EntityCondition invoiceTypeCon = EntityCondition.makeCondition("invoiceTypeId", EntityJoinOperator.IN, INVOICE_TYPE);
    	listAllConditions.add(invoiceTypeCon);
    	if(context.containsKey("status") && UtilValidate.isNotEmpty((String)context.get("status")) ){
    		String tmp = (String) context.get("status");
    		String[] _status = null; 
    		if(tmp.indexOf(",") != -1){
    			_status = tmp.split(",");
    		}
    		if(_status != null){
    			List<String> listConds = FastList.newInstance();
    			for(String stt : _status){
    				listConds.add(stt);
    			}
    			listAllConditions.add( EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_IN,listConds));
    		}
    	}
    	//check invoices not applied payment other
    	if(context.containsKey("paymentId") && UtilValidate.isNotEmpty((String)context.get("paymentId")) ){
    		List<GenericValue> listPaymentAppl = delegator.findList("PaymentApplication",null, UtilMisc.toSet("invoiceId"), UtilMisc.toList("invoiceId"),null,false);
    			if(UtilValidate.isNotEmpty(listPaymentAppl)){
    				List<String> listInvoiceId = EntityUtil.getFieldListFromEntityList(listPaymentAppl, "invoiceId", true);
    				listAllConditions.add( EntityCondition.makeCondition("invoiceId", EntityJoinOperator.NOT_IN,listInvoiceId));
    			}
    	}
    	if (organizationPartyId!= null)
    	{
    		EntityCondition organizationPartyCon = EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, organizationPartyId);
    		listAllConditions.add(organizationPartyCon);
    	}
    	if(UtilValidate.isNotEmpty(context.get("fromDueDate"))) {
    	    if("true".equals(context.get("isNullDueDate"))) {
                EntityCondition tempCond = EntityCondition.makeCondition(
                        EntityCondition.makeCondition("dueDate", EntityOperator.BETWEEN, UtilMisc.toList(context.get("fromDueDate"), context.get("toDueDate"))),
                        EntityOperator.OR,
                        EntityCondition.makeCondition("dueDate", EntityOperator.EQUALS, null)
                );
                listAllConditions.add(tempCond);
            }
            else {
    	        listAllConditions.add(EntityCondition.makeCondition("dueDate", EntityOperator.BETWEEN, UtilMisc.toList(context.get("fromDueDate"), context.get("toDueDate"))));
            }
        }
    	//EntityCondition allConditions = EntityCondition.makeCondition(listAllConditions); 
		//EntityListIterator listAllInvoices = delegator.find("InvoicePartyDetail", allConditions, null, null, listSortFields, opts);
		
		List<GenericValue> listTmpInvs = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "InvoicePartyDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);

		for(GenericValue item : listTmpInvs) {
			Invoice inv = new Invoice();
			inv.setInvoiceId(item.getString("invoiceId"));
			inv.setInvoiceTypeId(item.getString("invoiceTypeId"));
			inv.setPartyIdFrom(item.getString("partyIdFrom"));
			inv.setFullNameFrom(item.getString("fullNameFrom").trim());
			inv.setFullNameTo(item.getString("fullNameTo").trim());
			inv.setPartyIdFrom(item.getString("partyIdFrom"));
			inv.setPartyId(item.getString("partyId"));
			inv.setInvoiceDate(item.getTimestamp("invoiceDate"));
			inv.setVerifiedDate(item.getTimestamp("verifiedDate"));
            inv.setDueDate(item.getTimestamp("dueDate"));
			inv.setDescription(item.getString("description"));
			inv.setCurrencyUomId(item.getString("currencyUomId"));
			inv.setStatusId(item.getString("statusId"));
			inv.setNewStatusId(item.getString("newStatusId"));
			inv.setPartyCode(item.getString("partyCode"));
			inv.setPartyCodeFrom(item.getString("partyCodeFrom"));
			//getTotal
			BigDecimal total = InvoiceWorker.getInvoiceTotal(delegator, item.getString("invoiceId"));
			
			BigDecimal payrollTotal = InvoiceWorker.getPayrollInvoiceTotal(delegator, item.getString("invoiceId"));
			//getAmountNotApplied
			BigDecimal amountToApply = InvoiceWorker.getInvoiceNotTrueApplied(delegator, item.getString("invoiceId"));
			inv.setTotal(total);
			inv.setPayrollAmount(payrollTotal);
			inv.setAmountToApply(amountToApply.toString());
			listInvoices.add(inv);
		}
		result.put("listIterator", listInvoices);
		//result.put("listInvoices", listInvoices);
		return result;
	}
}
