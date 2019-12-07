package com.olbius.acc.setting;

import com.olbius.acc.utils.UtilServices;
import com.olbius.basehr.util.DateUtil;
import com.olbius.service.annotations.Service;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SettingServices {
	public static Map<String, Object> createAllocationCostPeriod(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String allocCostPeriodCode = (String)context.get("allocCostPeriodCode");
		try {
			List<GenericValue> allocCostPeriodCheck = delegator.findByAnd("AllocationCostPeriod", UtilMisc.toMap("allocCostPeriodCode", allocCostPeriodCode), null, false);
			if(UtilValidate.isNotEmpty(allocCostPeriodCheck)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "AllocationCostCodeIsExists", locale));
			}
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			Timestamp thruDate = (Timestamp)context.get("thruDate");
			if(fromDate.compareTo(thruDate) >= 0){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesUiLabels", "BSValidateDate", locale));
			}
			List<EntityCondition> checkConds = FastList.<EntityCondition>newInstance();
			checkConds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			checkConds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			allocCostPeriodCheck = delegator.findList("AllocationCostPeriod", EntityCondition.makeCondition(checkConds), null, null, null, false);
			if(UtilValidate.isNotEmpty(allocCostPeriodCheck)){
				GenericValue allocCostPeriodErr = allocCostPeriodCheck.get(0);
				Map<String, Object> errorMap = FastMap.<String, Object>newInstance();
				errorMap.put("fromDate", DateUtil.getDateMonthYearDesc(fromDate));
				errorMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDate));
				errorMap.put("fromDate1", DateUtil.getDateMonthYearDesc(allocCostPeriodErr.getTimestamp("fromDate")));
				errorMap.put("thruDate1", DateUtil.getDateMonthYearDesc(allocCostPeriodErr.getTimestamp("thruDate")));
				errorMap.put("allocCostPeriodCode", allocCostPeriodCode);
				errorMap.put("allocCostPeriodCode1", allocCostPeriodErr.get("allocCostPeriodCode"));
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "AllocationCostPeriodTimeError", errorMap, locale));
			}
			GenericValue newEntity = delegator.makeValue("AllocationCostPeriod");
			newEntity.setNonPKFields(context);
			String allocCostPeriodId = delegator.getNextSeqId("AllocationCostPeriod");
			newEntity.set("allocCostPeriodId", allocCostPeriodId);
			delegator.create(newEntity);
			successResult.put("allocCostPeriodId", allocCostPeriodId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createAllocationCostPeriodItem(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue allocationCostPeriodItem = delegator.makeValue("AllocationCostPeriodItem");
		allocationCostPeriodItem.setAllFields(context, false, null, null);
		delegator.setNextSubSeqId(allocationCostPeriodItem, "allocCostPeriodSeqId", 5, 1);
		try {
			delegator.create(allocationCostPeriodItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateAllocationCostPeriodItem(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue allocationCostPeriodItem = delegator.makeValue("AllocationCostPeriodItem");
		allocationCostPeriodItem.setAllFields(context, false, null, null);
		try {
			allocationCostPeriodItem.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getListInvoiceItemType(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			List<GenericValue> invoiceItemTypeList = delegator.findList("InvoiceItemTypeAndGlAccountDetail", null, null, UtilMisc.toList("description"), null, false);
			retMap.put("listReturn", invoiceItemTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createInvoiceItemType(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
		try {
			GenericValue invoiceItemType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId), false);
			if(invoiceItemType != null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceItemTypeIdIsExists", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId), locale));
			}
			invoiceItemType = delegator.makeValue("InvoiceItemType");
			invoiceItemType.setAllFields(context, false, null, null);
			delegator.create(invoiceItemType);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	public static Map<String, Object> createPaymentType(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String paymentTypeId = (String)context.get("paymentTypeId");
		try {
			GenericValue paymentType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", paymentTypeId), false);
			if(paymentType != null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentTypeIdIsExists", UtilMisc.toMap("paymentTypeId", paymentTypeId), locale));
			}
			paymentType = delegator.makeValue("PaymentType");
			paymentType.setAllFields(context, false, null, null);
			delegator.create(paymentType);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	public static Map<String, Object> updatePaymentType(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue paymentType = delegator.makeValue("PaymentType");
			paymentType.setAllFields(context, true, null, null);
			delegator.store(paymentType);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	public static Map<String, Object> createOrStorePaymentTypeGlAccountType(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue paymentTypeGlAccountType = delegator.makeValue("PaymentGlAccountTypeMap");
		paymentTypeGlAccountType.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(paymentTypeGlAccountType);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	public static Map<String, Object> createGlAccountType(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue glAccountType = delegator.makeValue("GlAccountType");
		glAccountType.setAllFields(context, true, null, null);
		try {
			delegator.create(glAccountType);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	public static Map<String, Object> createPosTerminalBank(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String posTerminalId = (String) context.get("posTerminalId");
		String partyId = (String) context.get("partyId");
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("posTerminalId", posTerminalId));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityUtil.getFilterByDateExpr());
		
		List<GenericValue> posBanks = delegator.findList("PosTerminalBank", EntityCondition.makeCondition(conds), null, null, null, false);
		if (UtilValidate.isNotEmpty(posBanks)) {
			String errorMessage = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCreatePosTerminalBankError", locale);
			return ServiceUtil.returnError(errorMessage);
		} else {
			GenericValue posTerminalBank = delegator.makeValue("PosTerminalBank");
			posTerminalBank.setAllFields(context, true, null, null);
			try {
				delegator.create(posTerminalBank);
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	public static Map<String, Object> deletePosTerminalBank(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String posTerminalId = (String) context.get("posTerminalId");
		String partyId = (String) context.get("partyId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		
		GenericValue posTerminalBank = null;
		try {
			posTerminalBank = delegator.findOne("PosTerminalBank", UtilMisc.toMap("posTerminalId", posTerminalId, "partyId", partyId, "fromDate", fromDate), false);
			if (UtilValidate.isNotEmpty(posTerminalBank)) {
				posTerminalBank.set("thruDate", UtilDateTime.nowTimestamp());
				delegator.store(posTerminalBank);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgdeletesuccess", locale));
	}
	
	public static Map<String, Object> updateGlAccountType(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue glAccountType = delegator.makeValue("GlAccountType");
		glAccountType.setAllFields(context, true, null, null);
		try {
			delegator.store(glAccountType);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	public static Map<String, Object> createOrStoreGlAccountTypeDefault(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue glAccountTypeDefault = delegator.makeValue("GlAccountTypeDefault");
		glAccountTypeDefault.setAllFields(context, true, null, null);
		try {
			delegator.createOrStore(glAccountTypeDefault);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
	}

	public static Map<String, Object> createOrUpdateEnumerationForInvoiceItemType(DispatchContext ctx, Map<String, Object> context) {
	    Delegator delegator = ctx.getDelegator();
	    Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    String defaultGlAccountId = "";
	    if(UtilValidate.isNotEmpty(context.get("defaultGlAccountId"))) defaultGlAccountId = (String) context.get("defaultGlAccountId");
	    String invoiceItemTypeId = (String) context.get("invoiceItemTypeId");
	    String description = (String) context.get("description");
	    String action = (String) context.get("action");
	    Boolean isTax = false;
        try {
            if(defaultGlAccountId.startsWith("133") || defaultGlAccountId.startsWith("333")) {
                isTax = true;
            }
                GenericValue enumInvoice = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", invoiceItemTypeId), false);
            if(("CREATE".equals(action) || "UPDATE".equals(action)) && UtilValidate.isEmpty(enumInvoice) && isTax) {
                GenericValue enumTax = delegator.makeValue("Enumeration");
                enumTax.set("enumId", invoiceItemTypeId);
                enumTax.set("enumCode", invoiceItemTypeId);
                enumTax.set("enumTypeId", "TAXABLE_INV_ITM_TY");
                enumTax.set("description", description);
                enumTax.create();
            }
            if(UtilValidate.isNotEmpty(enumInvoice) && "DELETE".equals(action)) delegator.removeValue(enumInvoice);
            if(UtilValidate.isNotEmpty(enumInvoice) && "UPDATE".equals(action) && !isTax) delegator.removeValue(enumInvoice);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Cannot processing createOrUpdateInvoiceItemType");
        }
        return successResult;
    }

    public Map<String, Object> createCustomerTimePayment(DispatchContext ctx, Map<String, ? extends Object> context) {
	    Delegator delegator = ctx.getDelegator();
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    String customerTimePaymentId = delegator.getNextSeqId("CustomerTimePayment");
        Long dueDay = (Long) context.get("dueDay");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        String partyId = (String) context.get("partyId");
        GenericValue customerTimePayment = delegator.makeValue("CustomerTimePayment");
        customerTimePayment.put("customerTimePaymentId", customerTimePaymentId);
        customerTimePayment.put("partyId", partyId);
        customerTimePayment.put("dueDay", dueDay);
        customerTimePayment.put("fromDate", fromDate);
        try {
            delegator.create(customerTimePayment);
            result.put("customerTimePaymentId", customerTimePaymentId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public Map<String, Object> updateCustomerTimePayment(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String customerTimePaymentId = (String) context.get("customerTimePaymentId");
        try {
            GenericValue customerTimePayment = delegator.findOne("CustomerTimePayment", UtilMisc.toMap("customerTimePaymentId", customerTimePaymentId), false);
            if(context.get("partyId") != null) {
                customerTimePayment.put("partyId", context.get("partyId"));
            }
            if(context.get("dueDay") != null) {
                customerTimePayment.put("dueDay", context.get("dueDay"));
            }
            if(context.get("fromDate") != null) {
                customerTimePayment.put("fromDate", context.get("fromDate"));
            }
            if(context.get("thruDate") != null) {
                customerTimePayment.put("thruDate", context.get("thruDate"));
            }
            delegator.store(customerTimePayment);
            result.put("customerTimePaymentId", customerTimePaymentId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }
}
