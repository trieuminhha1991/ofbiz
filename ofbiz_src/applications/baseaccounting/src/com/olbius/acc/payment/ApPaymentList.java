package com.olbius.acc.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.acc.payment.entity.Payment;
import com.olbius.acc.utils.UtilServices;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;

public class ApPaymentList implements PaymentList{
	
	public List<String> PAYMENT_TYPE = FastList.newInstance();
	public String[] listParent = {"DISBURSEMENT","TAX_PAYMENT"};
	
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public Map<String, Object> getListPayments(DispatchContext dispatcher, Map<String, Object> context) throws Exception {
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		//Get local dispatcher
		LocalDispatcher localDis = dispatcher.getDispatcher();
		List<Payment> listPayments = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("-effectiveDate");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//get All Payment Type
    	for(String parentType : listParent){
    		UtilServices.getAllPaymentType(delegator,PAYMENT_TYPE,parentType);
    	}
    	EntityCondition paymemtTypeCon = EntityCondition.makeCondition("paymentTypeId", EntityJoinOperator.IN, PAYMENT_TYPE);
    	listAllConditions.add(paymemtTypeCon);
    	listAllConditions.add(EntityCondition.makeCondition("paymentCode", EntityJoinOperator.NOT_EQUAL, null));
    	if (organizationPartyId!= null)
    	{
    		EntityCondition organizationPartyCon = EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, organizationPartyId);
    		listAllConditions.add(organizationPartyCon);
    	}      	
    	EntityCondition allConditions = EntityCondition.makeCondition(listAllConditions); 
    	
		List<GenericValue> listTmpPays = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "PaymentPartyDetail", allConditions, null, null, listSortFields, opts);
		for(GenericValue item : listTmpPays) {
			Payment pay = new Payment();
			pay.setPaymentId(item.getString("paymentId"));
			pay.setPaymentTypeId(item.getString("paymentTypeId"));
			pay.setPartyIdFrom(item.getString("partyIdFrom"));
			pay.setPartyIdTo(item.getString("partyIdTo"));
			pay.setStatusId(item.getString("statusId"));
			pay.setAmount(item.getBigDecimal("amount"));
			pay.setComments(item.getString("comments"));
			pay.setCurrencyUomId(item.getString("currencyUomId"));
			pay.setEffectiveDate(item.getTimestamp("effectiveDate"));
			pay.setFullNameFrom(item.getString("fullNameFrom"));
			pay.setFullNameTo(item.getString("fullNameTo"));
			pay.setPaymentCode(item.getString("paymentCode"));
			pay.setPaymentMethodId(item.getString("paymentMethodId"));
			pay.setPartyCodeFrom(item.getString("partyCodeFrom"));
			pay.setPartyCodeTo(item.getString("partyCodeTo"));
			//getAmountNotApplied
			BigDecimal amountToApply = PaymentWorker.getPaymentNotTrueApplied(item);
			pay.setAmountToApply(amountToApply);
			listPayments.add(pay);
		}

		result.put("listIterator", listPayments);	
		return result;
	}
	
}